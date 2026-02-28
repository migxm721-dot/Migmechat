<?php
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");

	class LevelModel extends Model
	{
		public function get_data($model_data)
		{
			$group = $model_data["group"];

			if( !$group->is_official() )
			{
				$dao = new UserDAO();
				$creator_level = $dao->get_user_level($group->created_by);
				$chatroom_size = get_value_from_array("chatroom_size", $creator_level, "integer", 30);
				$group_count = get_value_from_array("create_group", $creator_level, "integer", 0);
				$group_size = get_value_from_array("group_size", $creator_level, "integer", 0);
				$storage_size = get_value_from_array("group_storage_size", $creator_level, "integer", 0);
				$num_group_chat_rooms = get_value_from_array("num_group_chat_rooms", $creator_level, "integer", 0);

				return array(
					"max_chatroom_size"=> $chatroom_size,
					"max_group_count" => $group_count,
					"max_member_size" => $group_size,
					"max_storage_size" => $storage_size,
					"max_linked_chatroom" => $num_group_chat_rooms
				);
			}
		}
	}
?>