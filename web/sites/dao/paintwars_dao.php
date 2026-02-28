<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");
	fast_require("Image", get_library_directory() . "/image/image.php");
	fast_require("Identicon", get_library_directory() . "/identicon/identicon.php");
	fast_require('MogilefsDAO', get_dao_directory() . '/mogilefs_dao.php');

	class PaintwarsDAO extends DAO
	{
		const PAINTWARS_HASH_PREFIX = 'PW_';
		const DEFAULT_MIG_LEVEL = 1;
		const DEFAULT_PAINTWARS_FREE_PAINTS = 3;
		const DEFAULT_PAINTWARS_FREE_CLEANS = 2;

		public function generate_user_identicon_hash($username, $current_identicon_index)
		{
			return md5($username.'_pw_'.$current_identicon_index);
		}

		/*
		 * Retreives and returns the User's current Indenticon Index from Redis (as integer)
		 */
		public function get_user_identicon_index($username)
		{
			$user_dao = new UserDAO();
			$user_id = $user_dao->get_user_id($username);

			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			if (!isset($redis_instance))
			{
				return;
 			}

 			$identicon_index = $redis_instance->hget(Redis::KEYSPACE_USER . $user_id, Redis::FIELD_USER_IDENTICON_INDEX);
			$redis_instance->disconnect();

			return intval($identicon_index);
		}

		/*
		 * Retreives and returns the User's current Identicon Hash from Redis
		 */
		public function get_user_identicon_hash($username)
		{
			$user_dao = new UserDAO();
			$user_id = $user_dao->get_user_id($username);

			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			if (!isset($redis_instance))
			{
				return;
 			}

 			$user_hash = $redis_instance->hget(Redis::KEYSPACE_USER . $user_id, Redis::FIELD_USER_IDENTICON_HASH);
			$redis_instance->disconnect();

			return $user_hash;
		}

		public function set_user_identicon_hash($username, $hash)
		{
			$user_dao = new UserDAO();
			$user_id = $user_dao->get_user_id($username);

			$redis_instance = Redis::get_master_instance_for_user_id($user_id);
			if (!isset($redis_instance))
			{
				return;
 			}

 			$redis_instance->hset(Redis::KEYSPACE_USER . $user_id, Redis::FIELD_USER_IDENTICON_HASH, $hash);
			$redis_instance->disconnect();
		}

		public function build_identicon_and_update_hash($username)
		{
			$filename = tempnam(sys_get_temp_dir(), uniqid());

			$current_identicon_index = $this->get_user_identicon_index($username);
			$user_hash = $this->generate_user_identicon_hash($username, $current_identicon_index);

			//$file_url = get_library_directory() . '/sites/lib/identicon/identicon.php?size=70&hash=' . $user_hash;
            $resource = Identicon::get_instance()->generate_identicon($user_hash, 70);

            //save image in a temp location
            //Image::get_instance()->save_image($resource, $filename);
            imagepng($resource, $filename);

            //push to mogile fs
            MogilefsDAO::get_instance()->save_file($filename, self::PAINTWARS_HASH_PREFIX . $user_hash);

            //remove the temporary resource and file
            Image::get_instance()->destroy_image($resource);
            unlink($filename);

            //update hash on redis
            $this->set_user_identicon_hash($username, self::PAINTWARS_HASH_PREFIX . $user_hash);

            //return the new hash
            return self::PAINTWARS_HASH_PREFIX . $user_hash;
        }

		public function delete_existing_identicon($username)
		{
			$user_hash = $this->get_user_identicon_index($username);

			//delete from mogilefs
			if( !empty($user_hash) )
				MogilefsDAO::get_instance()->delete_file($user_hash);
        }


		//returns the required mig level for the fashion show game
		public function get_reqd_mig_level()
		{
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance))
			{
				throw new Exception ("Redis slave games instance not found");
			}

			$mig_level = $redis_instance->hget(Redis::KEYSPACE_PAINTWARS, Redis::FIELD_PAINTWARS_MIGLEVEL);
			if (!isset($mig_level))
			{
				$mig_level = self::DEFAULT_MIG_LEVEL;
			}

			$redis_instance->disconnect();
			return intval($mig_level);
		}

		public function get_total_free_paints_per_day()
		{
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance))
			{
				throw new Exception ("Redis slave games instance not found");
			}

			$val = $redis_instance->hget(Redis::KEYSPACE_PAINTWARS, Redis::FIELD_PAINTWARS_FREE_PAINTS);
			if (!isset($val))
			{
				$val = self::DEFAULT_PAINTWARS_FREE_PAINTS;
			}

			$redis_instance->disconnect();
			return intval($val);
		}

		public function get_total_free_cleans_per_day()
		{
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance))
			{
				throw new Exception ("Redis slave games instance not found");
			}

			$val = $redis_instance->hget(Redis::KEYSPACE_PAINTWARS, Redis::FIELD_PAINTWARS_FREE_CLEANS);
			if (!isset($val))
			{
				$val = self::DEFAULT_PAINTWARS_FREE_CLEANS;
			}

			$redis_instance->disconnect();
			return intval($val);
		}

		public function get_current_week_winners()
		{
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance))
			{
				throw new Exception ("Redis slave games instance not found");
			}

			$winners = $redis_instance->smembers(Redis::KEYSPACE_PAINTWARS . Redis::KEYSPACE_SEPARATOR . Redis::KEYSPACE_PW_CURRENT_WINNERS);

			$redis_instance->disconnect();
			return $winners;
		}

		public function get_prev_week_winners()
		{
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance))
			{
				throw new Exception ("Redis slave games instance not found");
			}

			$winners = $redis_instance->smembers(Redis::KEYSPACE_PAINTWARS . Redis::KEYSPACE_SEPARATOR . Redis::KEYSPACE_PW_PREV_WINNERS);

			$redis_instance->disconnect();
			return $winners;
		}

	}

?>