<?php
	fast_require('ChatroomDAO', get_dao_directory() . '/chatroom_dao.php');

	class ChatroomController
	{
		public function user_owned_chatrooms(&$model_data)
		{
			if(value_exists(get_field_name("join")) )
			{
				$chatroom_dao = new ChatroomDAO();
				$chatroom_dao->join_chatroom(get_attribute_value('room_name'));
			}
		}

		public function user_moderated_chatrooms(&$model_data)
		{
			if(value_exists(get_field_name("join")) )
			{
				$chatroom_dao = new ChatroomDAO();
				$chatroom_dao->join_chatroom(get_attribute_value('room_name'));
			}
		}
		public function change_owner_send_email(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$username = get_value("name");
			$roomname = get_value_from_array("roomname", $model_data);
			$result = make_soap_call("sendChangeRoomOwnerEmail", array($session_user, $roomname, $username));


			$model_data["username"] = $username;

                        //Set success/error messages
                        if(!empty($result->message))
                        {
                                $model_data['message'] = $result->message;
                        }
                        else if(!empty($result->data))
                        {
                                if($result->data == 'TRUE')
                                        $model_data['message'] = sprintf(_('An email has been sent to %s about change of ownership for the Chat Room %s'), $username, $roomname);
                                else
                                        $model_data['message'] = $result->data;
                        }
		}

		public function change_owner_confirm(&$model_data)
		{
			$username = get_value("name");

			$model_data["name"] = $username;
		}

		public function ban_user(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$roomname = get_value_from_array("roomname", $model_data);
			$username = get_value("name");
			$from = get_attribute_value("from");

			$result = make_soap_call("banUserFromRoom", array($session_user, $roomname, $username));

			$data = array();
			$data["call_result"] = $result;

			$view = new ControllerMethodReturn();
			if(is_touch_view() || is_blackberry_view() || is_ios_view())
				$view->method = 'ban_user_by_member';
			else
				$view->method = ($from=="member")?"ban_user_by_member":"ban_user_by_id";
			$view->model_data = $data;
			return $view;
		}

		public function add_moderator($model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$moderator_name = get_value("name");
			$roomname = get_value_from_array("roomname", $model_data);
			$from = get_attribute_value("from");

			$result = make_soap_call("addRoomModerator", array($session_user, $roomname, $moderator_name));

			$data = array();
			$data["call_result"] = $result;

			if($from=="contact")
			{
				$view = new ControllerMethodReturn();
				$view->method = "add_contact_moderators";
				$view->model_data = $data;
				return $view;
			}
			else
			{
				$view = new ControllerMethodReturn();
				$view->method = "add_moderator_id";
				$view->model_data = $data;
				return $view;
			}
		}

		public function create_submit(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$roomname = get_value_from_array("roomname", $model_data);
			$language = get_value("language");
			$description = get_attribute_value("description");
			$keywords = get_value("keywords");
			$allow_kicking = value_exists("allow_kicking");

			$result = make_soap_call("createChatroom", array($session_user, $roomname, $language, $description, $keywords, $allow_kicking));

			if($result->is_error() || $result->data != "TRUE" )
			{
				$view = new ControllerMethodReturn();
				$view->method = "create";
				$view->model_data["call_error"] = $result->message;
				$view->model_data["roomname"] = get_value_from_array("roomname", $model_data);
				$view->model_data["description"] = $description;
				$view->model_data["lang_code"] = $language;
				$view->model_data["allow_kicking"] = $allow_kicking;
				$view->model_data["keywords"] = $keywords;
				return $view;
			}

			$model_data["roomname"] = $roomname;
		}

		public function join(&$model_data)
		{
			$do = get_value("do");

			$chatroom_dao = new ChatroomDAO();
			$chatroom_dao->join_chatroom(get_value_from_array('roomname', $model_data));

			if( !empty($do) )
			{
				$view = new ControllerMethodReturn();
				$view->method = $do;
				$view->model_data = array();
				$view->model_data[get_field_name("roomname")] = $room_name;
				return $view;
			}
		}
	}
?>
