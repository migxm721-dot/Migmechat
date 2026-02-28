<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class JoinModel extends SoapModel
	{
		const WEIGHT_GROUP_MEMBER_NEW = 3;
		public function get_data($model_data)
		{
			$max_member_size = get_value_from_array("max_member_size", $model_data, "integer", 10);
			$group = $model_data["group"];
			$max_size_reached = false;
			/*
			if( $group->is_official() == false )
			{
				if( ($max_member_size < ($group->member_count+1)) )
				{
					$max_size_reached = true;
				}
			}
			*/
			$session_user = get_value_from_array("session_user", $model_data);
			$group_id = get_attribute_value("group_id", "integer", 0);
			$location_id = get_value_from_array("location_id", $model_data, "integer", 0);
			$ip = getRemoteIPAddress();

			$group_dao = new GroupDAO();
			$is_member = $group_dao->is_group_member($group_id, $session_user);


			$data = array();
			$data['successes'] = $model_data['successes'];
			$data['errors'] = $model_data['errors'];

			if($is_member)
			{
				$data['ok'] = false;
				$data["error"] = _('You are already a member of this group.');
			}
			else if(!$is_member && !$max_size_reached)
			{
				$call_return = make_soap_call("joinGroup", array($session_user, $group_id, $location_id, $ip, getSessionID(), getMobileDevice(), getUserAgent(),
																	false, true, true,
																	false, false, false));
				$data['ok'] = ($call_return->is_error()==false);
				$data['call_return'] = $call_return;
				$data['message'] = $call_return->message;

				if($data['ok'])
				{
					$data['successes'][] = _('You have successfully joined the group.');

					// increment score
					$group_dao->increment_score($group_id, self::WEIGHT_GROUP_MEMBER_NEW);

				}
			}
			elseif($max_member_size)
			{
				$data['ok'] = false;
				$data["error"] = sprintf(_('This group has reached the maxium size of %s members.'), $max_member_size);
			}
			else
			{
				$data["error"] = _('You are already part of the group.');
				$data['errors'][] = _('You are already part of the group.');
			}


			return $data;
		}
	}
?>