<?php
	fast_require("Model", get_framework_common_directory() . "/model.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("FshowDAO", get_dao_directory() . "/fshow_dao.php");
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");
	fast_require("ReputationDAO", get_dao_directory() . "/reputation_dao.php");
	fast_require("UserReputationLevel", get_domain_directory() . "/user/user_reputation_level.php");

	fast_require("UserItem", get_domain_directory() . "/fshow/user_item.php");

	class VotersModel extends Model
	{
		public function get_data($model_data)
		{
			// Is There Next Page?
			$has_more = false;

			$user_dao = new UserDAO();
			$fshow_dao = new FshowDAO();

			$session_user = get_value_from_array('session_user', $data);
			$session_user_id = get_value_from_array("session_user_id", $model_data);

			$uid = get_attribute_value("uid", "string", $session_user);
			$q_uname = get_attribute_value("uname", "string", $session_user);
			$q_bid = get_attribute_value("bid", "string", $session_user);

			if(strlen($uid)==0){
				$uid = $session_user_id;
			}

			$number_of_entries = get_attribute_value("number_of_entries", "integer", 20);
			$page = get_attribute_value("page", "integer", 1);
			$start_index = ($page-1)*$number_of_entries;
			$end_index = ($start_index + $number_of_entries);

			$list =  $user_dao->get_voters_list($uid, $start_index, $end_index);

			$new_list = array();

			if($list)
			{
				foreach($list as $list_item)
				{
					$user_id = "";
					$bid = "";

					list($user_id,$bid,$day) = split(':',$list_item[0]);
					$days = floor((time() - $list_item[1])/(3600*24)); //# of days

					$user_detail = $user_dao->get_user_detail_from_id($user_id);
					$uname = $user_detail->username;

					$newStr = $user_id.':'.$bid.':'.$uname;

					$new_list[] = array(
						'username' => $uname,
						'user_id' => $user_id,
						'body_id' => $bid,
						'day' => $days
					);
				}
			}

			if(sizeof($new_list) > $number_of_entries)
			{
				$has_more = true;
				array_pop($new_list);
			}

			$return_data['this_user_id'] = $uid;
			$return_data['this_user_name'] = $q_uname;
			$return_data['this_user_bid'] = $q_bid;

			$return_data['has_more'] = $has_more;
			$return_data['voters_list'] = $new_list;
			return $return_data;
		}
	}
?>