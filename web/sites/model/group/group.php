<?php
	fast_require("Group", get_domain_directory() . "/group/group.php");
	fast_require("GroupMember", get_domain_directory() . "/group/group_member.php");
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");
	fast_require("ThirdPartyApplicationDAO", get_dao_directory() . "/third_party_application_dao.php");
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");

	class GroupModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$session_user_detail = $model_data["session_user_detail"];
			$group_id = get_attribute_value("group_id", "integer", get_value_from_array("group_id", $model_data, "integer", 0));

			if ($group_id == 0) return array();

			$dao = new GroupDAO();
			$user_dao = new UserDAO();

			$group = $dao->get_group($group_id);
			$group->member_count = $dao->get_group_active_member_count($group_id);

			$creator_data = $user_dao->get_user_level($group->created_by);

			$group->creator_miglevel = get_value_from_array("level", $creator_data, "integer", 1);
			$group_member = $dao->get_group_member($group_id, $session_user);
			$member_status = $group_member->status;

			$data = array();

			set_value(get_field_name("group_id"), $group_id);

			$data["group"] = $group;
			$data['session_user_country_id'] = $session_user_detail->countryID;
			$data["member"] = $group_member;
			$data["session_user_member"] = ($member_status==1);
			$data["session_user_admin"] = $group_member->is_administrator();
			$data["session_user_moderator"] = $group_member->is_moderator();
			$data['session_user_creator'] = strtolower($group->created_by) == strtolower($session_user);

			// get group game
			$tpa_dao = new ThirdPartyApplicationDAO;
			$game_view = (get_view() == View::MTK_MRE) ? View::MIDLET : get_view();
			$tpa_details = $tpa_dao->get_group_game_link($group_id, $game_view);
			if ($tpa_details)
			{
				$data['client_id'] = $tpa_details['name'];
			}

			if($group_member->is_administrator())
			{
				$data['num_join_requests'] = $dao->get_total_join_requests($group_id);
				$data["group"]->pending_invitation = $data['num_join_requests'];
			}

			$data['max_rss'] = 5;

			return $data;
		}
	}
?>