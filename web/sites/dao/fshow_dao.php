<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");

	class FshowDAO extends DAO
	{
		const DEFAULT_MIG_LEVEL = 1;
		const DEFAULT_ACTIVE_DAYS = 14;
		const DEFAULT_ITEM_COUNT = 2;

		//local helper function: returns the reference to the redis instance
		private function get_redis_instance_candidates($redis_instance)
		{
			//if the parameter has a valid instance, just returns that instance

			$new_instance_created = false;
			if(!isset($redis_instance)){
				$redis_instance = Redis::get_slave_instance_for_games();
				$new_instance_created = true;
				if (!isset($redis_instance)){
					throw new Exception ("Redis slave games instance not found");
				}
			}

			return array("redis_instance"=>$redis_instance,"new_instance_created"=>$new_instance_created);
		}

		//local helper function: closes the connection if a new local redis instance was created
		private function disconnect_redis_instance_candidates($redis_instance, $is_new){
			if($is_new){
				$redis_instance->disconnect();
			}
		}

		public function get_avatar_candidates($redis_instance, $start_index, $delta)
		{
			$resultArray = $this->get_redis_instance_candidates($redis_instance);
			$redis_instance = $resultArray["redis_instance"];
			$new_instance_created = $resultArray["new_instance_created"];

			$candidates = $redis_instance->zrange(Redis::KEYSPACE_AVATAR_CANDIDATES, $start_index, ($start_index+$delta-1));

			$this->disconnect_redis_instance_candidates($redis_instance, $new_instance_created);
			return $candidates;
		}

		public function get_number_of_avatar_candidates($redis_instance)
		{
			$resultArray = $this->get_redis_instance_candidates($redis_instance);
			$redis_instance = $resultArray["redis_instance"];
			$new_instance_created = $resultArray["new_instance_created"];

			$num_of_candidates = $redis_instance->zcard(Redis::KEYSPACE_AVATAR_CANDIDATES);

			$this->disconnect_redis_instance_candidates($redis_instance, $new_instance_created);
			return $num_of_candidates;
		}


		//get the next index from where we can fetch the set of users from the candidate list
		public function get_next_index($user_id, $current_index, $list_length, $delta)
		{
			//case: only 1 item
			//case: delta is the same as length
			if($list_length == 1 || $delta == $list_length){
				return 0;
			}

			//case: if delta is zero
			if($delta == 0){
				return $current_index;
			}

			//case: handling error cases
			if($list_length < 1 || $delta < 0 || $delta > $list_length){
				throw new Exception("Error while retrieving avatars");
			}

			//if the current index passed in is just a placeholder (first time access)
			if($current_index < 0)
			{
				//if the user doesn't have an index in redis (ie., first time), then get a random value between 0 and end of list given the delta
				$current_index = mt_rand(0,$list_length-$delta-1);
			}
			else //the parameter current index has a positive value
			{
				//pre-condition: length > 1 and delta > 1

				//this handles the index out of bound as well -> the index moves to the beginning
				$current_index = ($current_index + $delta)%($list_length-($delta-1));
			}

			return $current_index;
		}

		//Update the current index where the user is
		public function set_next_index($user_id, $newIndex){
			$user_dao = new UserDAO();
			$user_dao->set_user_current_avatar_candidate_index($user_id,$newIndex);
		}

		public function vote_avatar($voted_by_userid, $voted_for_userid, $vote_for_user_name, $body_id)
		{
			$is_invalid_body_id = false;
			$vote_added = false;

			$user_dao = new UserDAO();
			$avatar_dao = new AvatarDAO();

			$existing_bid = $avatar_dao->get_user_avatar_body_uuid_by_user_id($voted_for_userid);

			if($existing_bid["body_key"] != $body_id){
				$is_invalid_body_id = true;
				return array("is_invalid_body_id"=>$is_invalid_body_id,"success"=>$vote_added);
			}
			else{

				//get the redis instance
				$redis_instance = Redis::get_master_instance_for_user_id($voted_for_userid);

				//1. add the voted_by_username:avatar_body_id to the voted_by_username:date key
				$this_date = getdate();

				$key = Redis::KEYSPACE_AVATAR_VOTES.':'.$voted_for_userid;
				$val = $voted_by_userid .':'. $body_id.':'.$this_date['mday'];

				$found = $redis_instance->zscore($key,$val);

				if(!$found) //if the vote is not present
				{
					//add vote only if the combination is not found -> this is to ensure one vote per combination per user per day
					Redis::add_to_sorted_set_with_timestamp_and_trim_by_age($redis_instance, $key, $val, 604800, true); // 7 days and make volatile
					$vote_added = true;
				}

				$redis_instance->disconnect();

				try
				{
					if ($vote_added)
					{
						//2. Increment the total num of Votes for the User key
						$new_num_votes = $user_dao->increment_avatar_vote_count($voted_for_userid);

						//3. call the leaderboard to update the ranking (upon successful casting of vote)
						fast_require("LeaderboardDAO", get_dao_directory() . "/leaderboard_dao.php");
						$lb_dao = new LeaderboardDAO();

						// call below increase the count for the weekly and daily boards
						$lb_dao->increase_board_score_for(Redis::KEYSPACE_LB_AVATAR_VOTES, $vote_for_user_name, $voted_for_userid, 1);

						// call below updates the total num_votes for all time for that user
						$lb_dao->set_all_time_board_score_for(Redis::KEYSPACE_LB_AVATAR_VOTES, $vote_for_user_name, $voted_for_userid, $new_num_votes);
					}

				} catch (Exception $e)
				{
					error_log("vote_avatar(): ". $e->getMessage() . "; Params: $voted_for_userid, $voted_by_userid, $body_id");
					if (isset($redis_instance)) $redis_instance->disconnect();
				}

				return array("is_invalid_body_id"=>$is_invalid_body_id,"success"=>$vote_added);

			}

			//4. <ToDo> send notification

		}

		//returns the required mig level for the fashion show game
		public function get_reqd_mig_level(){
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance)){
				throw new Exception ("Redis slave games instance not found");
			}

			$mig_level = $redis_instance->hget(Redis::KEYSPACE_FASHIONSHOW, Redis::FIELD_FASHIONSHOW_MIGLEVEL);
			if(!isset($mig_level)){
				$mig_level = self::DEFAULT_MIG_LEVEL;
			}

			$redis_instance->disconnect();
			return $mig_level;
		}
	}
?>
