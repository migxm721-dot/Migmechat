<?php
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("LeaderboardDomain", get_domain_directory() . "/leaderboard.php");
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("ReputationDAO", get_dao_directory() . "/reputation_dao.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");

	class LeaderboardDAO extends DAO
	{
		const VALUE_SEPARATOR = ':';


		// increases the weekly and daily board scores by the given score_delta
		public function increase_board_score_for($board, $username, $userid, $score_delta)
		{
			// we do not write if leaderboards are disabled
			if (!LeaderboardDomain::WRITES_ENABLED)
			{
				return false;
			}

			// increase daily and weekly by quantity
			// if anything fails in this call, we do not really care :/
			try
			{
				$redis_instance = Redis::get_master_instance_for_leaderboards();
				$redis_instance
					->pipeline()
					->zincrby($board . Redis::KEYSPACE_LB_DAILY,  $score_delta, $username.self::VALUE_SEPARATOR.$userid)
					->zincrby($board . Redis::KEYSPACE_LB_WEEKLY, $score_delta, $username.self::VALUE_SEPARATOR.$userid)
					->execute();

				return true;
			}
			catch(Exception $e)
			{
				// do nothing
			}
		}

		// set the score for a value in the given all time board
		public function set_all_time_board_score_for($board, $username, $userid, $score)
		{
			// we do not write if leaderboards are disabled
			if (!LeaderboardDomain::WRITES_ALL_TIME_ENABLED)
			{
				return false;
			}

			// increase all time board by the full amount
			// if anything fails in this call, we do not really care :/
			try
			{
				$redis_instance = Redis::get_master_instance_for_leaderboards();
				$redis_instance->zadd($board . Redis::KEYSPACE_LB_ALL_TIME, $score, $username.self::VALUE_SEPARATOR.$userid);
			}
			catch(Exception $e)
			{
				// do nothing
			}
		}

		// returns an array of 3 elements:
		// 1) leaderboard data asan array or user data in descending order
		//    each element of the array is a hash with the folowing properties
		//    userid, username, value, rank
		// 2) the current user data (he may not be in the top 10)
		// 3) a boolean value to indicate whether the user is in the top 10
		public function get_leaderboard($board, $time, $username, $userid, $size=10)
		{
			// nothing to do if leaderboards are disabled
			if (!LeaderboardDomain::UI_ENABLED)
			{
				return null;
			}

			$leaderboard = array();
			$user_data = null;
			$is_user_in_top = false;
			$size = max($size, 1); // $size cannot be less than 1

			try
			{
				$redis_instance = Redis::get_slave_instance_for_leaderboards();
				$res = $redis_instance->zrevrange($board . $time, 0, $size - 1, 'WITHSCORES');

				$rank = 0;
				foreach($res as &$leader)
				{
					list($cur_username, $cur_userid) = explode(self::VALUE_SEPARATOR, $leader[0]);
					$score = (float)$leader[1];
					$user_obj = array(
						'userid' => $cur_userid,
						'username' => $cur_username,
						'value' => ($board == Redis::KEYSPACE_LB_MIG_LEVEL ? $this->score_to_level($score) : $score),
						'rank'  => ++$rank
					);
					$leaderboard[] = $user_obj;
					if ($cur_username == $username)
					{
						$user_data = $user_obj;
						$is_user_in_top = true;
					}
				}


				// if curent user is not in the top, then we need to fetch his position and score
				if (!$is_user_in_top)
				{
					$redis_value = $username.self::VALUE_SEPARATOR.$userid;
					list($score, $rank) = $redis_instance->pipeline()
							->zscore($board . $time, $redis_value)
							->zrevrank($board . $time, $redis_value)
							->execute();

					if (is_null($score) || is_null($rank))
					{
						throw new Exception("Leaderboard: unable to get user data from redis");
					}

					$score = (float)$score;

					$user_data = array(
						'userid' => $userid,
						'username' => $username,
						'value'  => ($board == Redis::KEYSPACE_LB_MIG_LEVEL ? $this->score_to_level($score) : $score),
						'rank'   => intval($rank) + 1 // redis zsets are zero-based
					);
				}
			}
			catch(Exception $e)
			{
				// return empty array if exception
			}

			return array($leaderboard, $user_data, $is_user_in_top);
		}



		public function get_friends_leaderboard($board, $time, $username, $userid, $size=10)
		{
			// nothing to do if leaderboards are disabled
			if (!LeaderboardDomain::UI_ENABLED)
			{
				return null;
			}


			$memcache = Memcached::get_instance();


			// check if current leaderboard is stored in memcached
			$MEM_KEY_LB = Memcached::$KEYSPACE_LEADERBOARD . $userid . '/' . $board . '/' . $time;

			$leaderboard_data = $memcache->get($MEM_KEY_LB);

			if ($leaderboard_data)
			{
				// cache hit, yeah!
				return $leaderboard_data;
			}




			// cache miss, we must compute the list of friends first
			// and then query the score for each in the global leaderboard sorted set
			// then re-sort in PHP

			// prepare dummy data in case we cannot fetch leaderboards
			$leaderboard = array();
			$user_data = null;
			$is_user_in_top = false;

			try
			{
				// first we get the friends list
				// this is memcached because a user typically visits several boards in a row
				// useful to store its relevnt leaderboard list of friends
				$MEM_KEY_LIST_OF_FRIENDS = Memcached::$KEYSPACE_LEADERBOARD . $userid . '/_friends_';
				$friends = $memcache->get($MEM_KEY_LIST_OF_FRIENDS);
				if (!$friends)
				{
					// cache miss on friends list compute now
					$friends = array( $username.self::VALUE_SEPARATOR.$userid ); // we always include current user in contact search

					// gather list of friends from mysql (all broadcastlist contacts who have logged in the past 14 days)
					$query = "
						SELECT concat(uid.username, '".self::VALUE_SEPARATOR."', uid.id) as contact
						FROM broadcastlist b, userid uid, user u
						WHERE
							b.username = ?
							AND b.broadcastUsername=uid.username
							AND b.broadcastUsername=u.username
							AND u.lastLoginDate >= date_sub(now(), interval 14 day);
					";

					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					$stmt->bind_param('s', $username);
					$stmt->execute();
					$stmt->bind_result($friend);

					// ok, now we populate the contacts array and queue the redis commands in the pipe in one pass
					while ($stmt->fetch())
					{
						$friends[] = $friend;
					}

					$stmt->free_result();
					$stmt->close();
					$this->closeSlaveConnection();

					// done, store that in memcache
					$memcache->add_or_update(
							$MEM_KEY_LIST_OF_FRIENDS,
							$friends,
							LeaderboardDomain::$EXPIRY_LIST_OF_FRIENDS
					);
				}

				// ok, now we have a list of friends
				// we'll prepares the redis pipe to find all their scores
				// Note: there will always be at least one command in the pipe, which is getting
				// the score for the current user
				$redis_instance = Redis::get_slave_instance_for_leaderboards();
				$redis_pipe = $redis_instance->pipeline();

				foreach($friends as $friend)
				{
					$redis_pipe->zscore($board . $time, $friend);
				}

				$scores = $redis_pipe->execute();

				// cleanup redis pipe and instance (it's not needed after this point)
				unset($redis_pipe);
				$redis_instance->disconnect();

				// verify that we have a score for all redis contacts
				if (count($friends) != count($scores))
				{
					throw new Exception("Unable to compute friend's leaderboard");
				}

				// now we trim the zeros/nulls out, sort the result, and we'll keep the top $size
				$leaderboard_tmp = array_combine($friends, $scores);
				arsort($leaderboard_tmp, SORT_NUMERIC);

				// at this point, we must go through the array
				// we need to keep, the top $size match, as well as the user_data
				// TODO: optimize, we can probably reduce the size of iteration
				$rank = 0;
				foreach($leaderboard_tmp as $friend => $score)
				{
					$rank++;
					list($cur_username, $cur_userid) = explode(self::VALUE_SEPARATOR, $friend);
					if ($rank > $size && $cur_username != $username) continue;

					$score = is_null($score) ? 0 : (float)$score;
					$user_obj = array(
						'userid' => $cur_userid,
						'username' => $cur_username,
						'value' => ($board == Redis::KEYSPACE_LB_MIG_LEVEL ? $this->score_to_level($score) : $score),
						'rank'  => $rank
					);
					if ($rank <= $size && $score > 0)
					{
						$leaderboard[] = $user_obj;
					}
					if ($cur_username == $username)
					{
						$user_obj['num_friends'] = count($friends) - 1; // we exclude the user himself
						$user_data = $user_obj;
						$is_user_in_top = ($rank <= $size);
					}

					if ($rank >= $size && !is_null($user_data))
					{
						break;
					}
				}

				// all good, cache into memcache
				$memcache->add_or_update(
						$MEM_KEY_LB,
						array($leaderboard, $user_data, $is_user_in_top),
						empty($leaderboard) ?
								LeaderboardDomain::$EXPIRY_FRIENDS_EMPTY_LB
								: LeaderboardDomain::$EXPIRY_FRIENDS_LB
				);
			}
			catch(Exception $e)
			{
				// hmm, what do we do here?
				// nothing so far, we'll just return an empty leaderboard...
			}

			return array($leaderboard, $user_data, $is_user_in_top);
		}


		public function score_to_level($score=0)
		{
			static $scoretolevel = null;

			if (is_null($scoretolevel))
			{
				$dao = new ReputationDAO();
				$scoretolevel = $dao->get_score_to_miglevel();
			}

			// TODO: since the chart is sorted, we could also do a quick binary search,
			// TODO: but I'm not caring about that for now, since we only have 70 levels...

			// backward loop is more efficient than forward for now :)
			for ($i=count($scoretolevel); $i-->0;)
			{
				if ($score >= $scoretolevel[$i]['score'])
				{
					return $scoretolevel[$i]['level'];
				}
			}

			// should never happen :/
			return 1;
		}
	}
?>