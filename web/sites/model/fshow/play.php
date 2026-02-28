<?php
	fast_require("Model", get_framework_common_directory() . "/model.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("FshowDAO", get_dao_directory() . "/fshow_dao.php");
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");
	fast_require("ReputationDAO", get_dao_directory() . "/reputation_dao.php");
	fast_require("UserReputationLevel", get_domain_directory() . "/user/user_reputation_level.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");

	fast_require("UserItem", get_domain_directory() . "/fshow/user_item.php");

	class PlayModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user_id = get_value_from_array("session_user_id", $model_data);

			$return_data['random_users'] = $this->get_user_profiles($session_user_id);
			return $return_data;
		}

		public function get_user_profiles($voted_by_userid)
		{
			/*
			 * (1) Get the next moving start index
			 * (2) Get the candidates
			 * Case: If the user is in the list of selected candidates, then do (1) and (2) again
			 * (3) Set the current moving index
			 */

			//DAOs
			$fshow_dao = new FshowDAO();
			$user_dao = new UserDAO();
			$avatar_dao = new AvatarDAO();

			$delta = 3; //get three avatars

			//Redis Instance
			$redis_instance = Redis::get_slave_instance_for_games();
			if (!isset($redis_instance)){
				throw new Exception ("Redis slave games instance not found");
			}

			//get total number of candidates
			$total_num_of_candidates = $fshow_dao->get_number_of_avatar_candidates($redis_instance);

			//current index
			$current_index = $user_dao->get_user_current_avatar_candidate_index($voted_by_userid);
			if(!isset($current_index)){
				$current_index = -1; //assigning a placeholder value, so that a new random value can be fetched
			}

			//get the subsequent index to query from
			$current_index = $fshow_dao->get_next_index($voted_by_userid, $current_index, $total_num_of_candidates, $delta);

			//get the user names & IDs from redis
			$candidates = $fshow_dao->get_avatar_candidates($redis_instance,$current_index,$delta);

			//case: if the current user is in the candidate list (low probability: 1 case every list traversal)
			if($this->is_user_in_list($candidates, $voted_by_userid))
			{
				//get the subsequent index to query from (next set of data)
				$current_index = $fshow_dao->get_next_index($voted_by_userid, $current_index, $total_num_of_candidates, $delta);
				$candidates = $fshow_dao->get_avatar_candidates($redis_instance,$current_index,$delta);
			}
			$redis_instance->disconnect();

			//updating the index
			$fshow_dao->set_next_index($voted_by_userid, $current_index);

			//populate the return list
			foreach($candidates as $candidate){
				list($uname, $uid) = split(':', $candidate);

				//getting the avatar
				$uuid = $avatar_dao->get_user_avatar_body_uuid_by_user_id($uid, true);
				$body_key = get_value_from_array("body_key", $uuid);
				$head_key = get_value_from_array("head_key", $uuid);

				//getting the migLevel
				$migLevel = $user_dao->get_user_level($uname);
				$userRepLev = new UserReputationLevel($migLevel);

				//return list
				$users_list[] = new UserItem($uid,$uname,$body_key,$head_key,$userRepLev);
			}

			return $users_list;
		}

		public function is_user_in_list($arr,$uid){
			if($arr){
				foreach($arr as $usr){
					list($candidate_user_name,$candidate_user_id) = split(':',$usr);
					if($uid == $candidate_user_id){ return true; }
				}
			}
			return false;
		}


	}
?>