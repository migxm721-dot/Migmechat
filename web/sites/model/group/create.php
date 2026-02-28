<?php
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");
	fast_require("UserLevel", get_file_location("/common/user_level.php"));

	class CreateModel extends Model
	{
		public function get_data($model_data)
		{
			$dao = new GroupDAO();
			$session_user = get_value_from_array("session_user", $model_data);
			$roomname = get_value("rm");
			$room_id = get_value("rmid", "integer", 0);
			if(UserLevel::can_access($session_user, 1))
			{
				$official = get_attribute_value("official", "integer", 0);
			}
			else
			{
				$official = 0;
			}

			$group_type = get_attribute_value("type");

			switch(strtoupper($group_type))
			{
				case 'PUBLIC':
					$type = 0;
					break;
				case 'BY_APPROVAL':
					$type = 3;
					break;
				case 'PRIVATE':
					$type = 1;
					break;
			}

			$group_id = $dao->create_group($session_user, get_value("name"),
												get_attribute_value("description"),
												$type,
												get_value("email"), $official);
			$data = array("group_id"=>$group_id );
			if(!empty($roomname) && $room_id > 0 )
			{
				$dao->link_chatroom_to_group($session_user, $group_id, $room_id);
			}
			return $data;
		}
	}
?>