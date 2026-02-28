<?php
	fast_require('GroupDAO', get_dao_directory() . '/group_dao.php');
	fast_require('GroupForumDAO', get_dao_directory() . '/group_forum_dao.php');
	fast_require("CallResult", get_framework_common_directory() . "/call_result.php");

	class GroupController
	{
		public function create(&$model_data)
		{
			return new ControllerMethodReturn('create', $model_data);
		}

		public function create_submit(&$model_data)
		{
			$dao = new GroupDAO();
			$session_user = get_value_from_array('session_user', $model_data);
			$roomname = get_value('rm');
			$room_id = get_value('rmid', 'integer', 0);
			try
			{
				switch(strtoupper(get_attribute_value('type')))
				{
				case 'PUBLIC':
					$type = 0;
					break;
				case 'BY_APPROVAL':
					$type = 3;
					break;
				default:
					$type = 1;
					break;
				}
				$group_id = $dao->create_group($session_user, get_value('name'), get_attribute_value('description'),
							$type,
							get_value('email'), get_attribute_value('official', 'integer', 0));

				// Chatroom
				if(!empty($roomname) && $room_id > 0 )
				{
					$dao->link_chatroom_to_group($session_user, $group_id, $room_id);
				}

				// Forum
				$group_forum_dao = new GroupForumDAO();
				$forum = $group_forum_dao->create_forum($session_user, $group_id);

			}
			catch(Exception $e)
			{
				$error = $e->getMessage();
			}

			$view = new ControllerMethodReturn();
			if( !empty($error) )
			{
				$view->method = 'create';
				$view->model_data = array('error'=>$error, 'name'=>get_value('name'),
									'description'=>get_attribute_value('description'),
									'email'=>get_value('email'), 'type'=>get_attribute_value('type'),
									'has_user_chatrooms'=>$model_data['has_user_chatrooms']);
			}
			else
			{
				$view->method = 'choose_avatar';
				$view->model_data = array('group_id'=>$group_id, 'has_user_chatrooms'=>$model_data['has_user_chatrooms'], 'forum'=>$forum);
			}
			return $view;
		}

		public function show_create_group_submit(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'create_group_create';
			$model_data['group_name'] = get_attribute_value('group_name');
			$model_data['room_name'] = get_attribute_value('room_name');
			$model_data['type'] = get_attribute_value('type');
			$model_data['email'] = get_attribute_value('email');
			$model_data['description'] = get_attribute_value('description');
			$model_data['type'] = get_attribute_value('type');
			$model_data['from'] = get_attribute_value('from');
			$view->model_data = $data;
			return $view;
		}

		public function show_leave(&$model_data)
		{
			$group = $model_data['group'];
			$session_user = $model_data['session_user'];

			if(!$model_data["session_user_creator"]){
				$call_return = make_soap_call('leaveGroup', array($session_user, $group->id));
			} else {
				$call_return = new CallResult();
				$call_return->set_error();
				$call_return->message = 'Owner can\'t leave the group';
			}

			$data = array();
			$data['call_return'] = $call_return;
			$data['session_user_member'] = false;

			$view = new ControllerMethodReturn();
			$view->method = 'leave_complete';
			$view->model_data = $data;
			return $view;
		}

		public function show_create_group_create(&$model_data)
		{
			$group_id = get_value_from_array('group_id', $model_data, 'integer', 0);
			$error = get_value_from_array('error', $model_data);

			$view = new ControllerMethodReturn();

			if( !empty($error) )
			{
				if (is_ajax_view())
				{
					$view->method = 'create';
				}
				else
				{
					$view->method = 'create_group';
				}
				$view->model_data = array('error'=>$error,
										'name'=>get_attribute_value('name'),
										'description'=>get_attribute_value('description'),
										'email'=>get_attribute_value('email'),
										'type'=>get_attribute_value('type'),
										'room_name'=>get_attribute_value('room_name'));
			}
			else
			{
				$view->method = 'choose_avatar';
				$model_data['group_id'] = $group_id;
				$model_data['from'] = 'create';
				$view->model_data = $model_data;
				/**
				$view->model_data = array('group_id'=>$group_id,
										'from'=>'create',
										'room_name'=>get_attribute_value('room_name'));
										**/
			}
			return $view;
		}

		public function choose_avatar_submit(&$model_data)
		{
			$group_id = get_attribute_value('group_id', 'integer', 0);
			$is_create = (get_value('create', 'integer', 0)==1);
			$has_user_chatrooms = $model_data['has_user_chatrooms'];

			if (is_midlet_view() || is_mre_view() || is_wap_view() || is_touch_view() || is_ios_view() || is_blackberry_view())
			{
				//Check where this is coming from
				$from = get_attribute_value('from');

				if(!empty($from))
				{
					$is_create = true;
				}
			}

			$view = new ControllerMethodReturn();


			if( $is_create )
			{
				if( !empty($error) )
				{
					$view->method = 'choose_avatar';
					$view->model_data = array('group_id'=>$group_id, 'has_user_chatrooms'=>$model_data['has_user_chatrooms'], 'from'=>'create');
				}
				else
				{
					if( !$has_user_chatrooms )
					{
						$view->method = 'invite_contact';
					}
					else
					{
						$view->method = 'link_chatrooms';
					}
					$view->model_data = array('group_id'=>$group_id, 'has_user_chatrooms'=>$model_data['has_user_chatrooms'], 'from'=>'create');
				}

				return $view;
			}
			else if (is_midlet_view() || is_mre_view() || is_wap_view() || is_touch_view() || is_ios_view() || is_blackberry_view())
			{
				$view->method = 'choose_avatar';
				$model_data['successes'][] = 'You have successfully set a new group avatar.';
				$view->model_data = $model_data;
				return $view;
			}
		}

		public function show_invite_contacts(&$model_data)
		{
			$group = $model_data['group'];
			$session_user = $model_data['session_user'];
			$create = get_value('create', 'integer', 0)==1;
			$from = get_attribute_value('from', 'string', '');

			$count = 0;

			$error_msg = '';

			$invited_someone = false;
			$invited_mig33user = "";

			foreach($_POST as $ind=>$value)
			{
				$ind = str_replace('+','.',$ind);
				if( $ind != 'c' &&
					$ind != 'v' &&
					$ind != 'a' &&
					$ind != 'cid' &&
					$ind != 'p' &&
					$ind != 'rm' &&
					$ind != 'rmid' &&
					$ind != 'create' &&
					$ind != 'nc')
				{
					$invited_someone = true;
					if($ind == 'invite_name'):
						$invited_mig33user = trim($value);
						if(strlen($invited_mig33user) == 0): //invited an empty username
							$invited_someone = false;
						else:
							$returnMsg = make_soap_call('inviteUserToGroup', array($session_user, $invited_mig33user, $group->id));
						endif;
					else: //inviting contacts
						$returnMsg = make_soap_call('inviteUserToGroup', array($session_user, $ind, $group->id));
					endif;

					if(isset($returnMsg)):
						if(strtolower($returnMsg->data) == 'true'):
							$count += 1;
						else:
							$error_msg = $error_msg . '<br>' . $returnMsg->message;
						endif;
					endif;
				}
			}

			if(!$invited_someone):
				$error_msg = 'You have not selected anyone to invite';
			endif;

			$model_data['contacts_invited'] = $count;

			if(strlen($error_msg) > 0):
				$model_data['error'] = $error_msg;
			endif;

			if($count > 0):
				if(strlen($invited_mig33user)>0):
					$model_data['success'] = 'Thanks, '.$invited_mig33user.' has been invited to the '.$group->name.' group.';
				else:
					$success_msg = "";
					if($count==1):
						$success_msg = 'Thanks, '.$count.' friend has been invited to the '.$group->name.' group.';
					else:
						$success_msg = 'Thanks, '.$count.' friends have been invited to the '.$group->name.' group.';
					endif;

					$model_data['success'] = $success_msg;
				endif;
			endif;

			if( $create )
			{
				$view = new ControllerMethodReturn();
				$view->method = 'create_completed';

				if( isset($model_data['has_user_chatrooms']) )
					$has_chatrooms = $model_data['has_user_chatrooms'];
				else
					$has_chatrooms = get_value('nc', 'integer', 0) == 1;
				$view->model_data = array('group' => $group, 'has_user_chatrooms'=>$has_chatrooms, 'success'=>$model_data['success'], 'error'=>$model_data['error']);
				return $view;
			}
			else
			{
				//Go straight back to group home page
			}
		}

		public function setting_check(&$model_data)
		{
			$data = array();
			$data['type'] = get_value_from_array('type', $model_data);
			$data['email'] = get_value_from_array('email', $model_data);
			$data['description'] = get_value_from_array('description', $model_data);
			$data['group_id'] = get_value_from_array('group_id', $model_data);
			$data['type'] = $type = get_value_from_array('type', $model_data);

			$num_join_requests = get_value_from_array('num_join_requests', $model_data, 'integer', 0);
			$cur_type = $model_data['group']->type;


			// conditions
			if ($cur_type == 'BY_APPROVAL' && $type == 'PUBLIC' && $num_join_requests>0)
			{
				$data['cond'] = 'setting_approval_public';
				$method = 'setting_pending';
			}
			else if ($cur_type == 'BY_APPROVAL' && $type == 'PRIVATE' && $num_join_requests>0)
			{
				$data['cond'] = 'setting_approval_private';
				$method = 'setting_pending';
			}
			else
			{
				$data['cond'] = '';
				$method = 'setting_update';
			}

			$view = new ControllerMethodReturn();
			$view->method = $method;
			$view->model_data = $data;

			return $view;
		}

		public function comment_photo_post_submit(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'view_photo';
			$tmp_body = get_attribute_value('body');
			$model_data['body'] = '';
			$model_data['username'] = get_attribute_value('username');
			$view->model_data = $model_data;
			return $view;
		}

		public function comment_photo_action(&$model_data)
		{
			$post_id = get_attribute_value('post_id');
			$post_id_filter = get_attribute_value('page');
			$data['post_id'] = $post_id;
			$data['page'] = $post_id_filter;
			if(!empty($data['page']))
			{
				$data['post_id'] = $data['page'];
				set_value(get_field_name('post_id'), $data['page']);
			}
			else
			{
				$data['post_id'] = '';
				set_value(get_field_name('post_id'), '');
			}
			$view = new ControllerMethodReturn();
			$view->method = 'view_photo';
			$view->model_data = $model_data;
			return $view;
		}

		public function show_ignore(&$model_data)
		{
			$group = $model_data['group'];
			$session_user = $model_data['session_user'];
			$session_user_member = $model_data['session_user_member'];

			if( $session_user_member )
			{
				$model_data['error'] = 'You are a member of this group and can not ignore.';
			}
			else
				make_soap_call('declineGroupInvitation', array($session_user, $group->id));
		}

		/**
		* CHATROOMS
		**/
		public function link_chatrooms_submit(&$model_data)
		{
			$is_create = (get_value('create', 'integer', 0)==1);
			$group_id = get_attribute_value(get_field_name('group_id'), 'integer', 0);

			if( $is_create )
			{
				$view = new ControllerMethodReturn();
				$view->method = 'invite_contact';
				$view->model_data = array('cid'=>$group_id, 'has_user_chatrooms'=>$model_data['has_user_chatrooms']);
				return $view;
			}
		}

		public function show_unlink_chatroom(&$model_data)
		{
			$error = get_value_from_array('error', $model_data);

			$view = new ControllerMethodReturn();
			$view->controller = 'chatroom';

			$from = get_attribute_value('from', 'string');
			if($from == 'user_owned_chatrooms')
			{
				$view->method = 'user_owned_chatrooms';
			}
			else
			{
				$view->method = 'user_chatrooms';
			}

			if(empty($error)) //Success
			{
				$model_data['success'] = 'You have successfully unlinked a chatroom.';
			}
			else
			{
				$model_data['error'] = $error;
			}

			$view->model_data = $model_data;
			return $view;

		}

		public function like_forum_post(&$model_data)
		{
			if ($model_data['from'] == 'comments'):
				$method = 'view_forum_post_comments';
			elseif($model_data['from'] == 'view_forum'):
				$method = 'view_forum';
			else:
				$method = $model_data['from'];
			endif;

			$view = new ControllerMethodReturn();
			$view->method = $method;
			$view->model_data = $model_data;
			return $view;
		}

		public function dislike_forum_post(&$model_data)
		{
			if ($model_data['from'] == 'comments')
			{
				$method = 'view_forum_post_comments';
			}
			else
			{
				$method = 'view_forum_post';
			}

			$view = new ControllerMethodReturn();
			$view->method = $method;
			$view->model_data = $model_data;
			return $view;
		}

		public function redirect_after_create(&$model_data)
		{
			$from = get_attribute_value('from', 'string', '');
			$group_id = get_attribute_value('group_id', 'integer', 0);

			$data = array('from'=>$from, get_field_name('group_id')=>$group_id);

			if(is_wap_view() && $from == 'create_group')
			{
				$data['successes'][] = 'Group created successfully';

				$view = new ControllerMethodReturn();
				$view->method = 'home';
				$view->model_data = $data;
				return $view;
			}
		}

		public function redirect_after_delete(&$model_data)
		{
			$view = new ControllerMethodReturn();

			$data = array();
			$data['successes'] = $model_data['successes'];
			$data['errors'] = $model_data['errors'];

			if($model_data['from'] == 'comments')
			{
				$view->method = 'view_forum_post_comments';
				if(! is_ajax_view()):
					$data['number_of_entries'] = 5;
				endif;
				$data['sort_order'] = 'recent';
			}
			else
			{
				$view->method = 'view_forum_post';
				if(! is_ajax_view()):
					$data['number_of_entries'] = 5;
				endif;
			}

			$view->model_data = $data;

			return $view;
		}

		public function redirect_after_comment(&$model_data)
		{
			$view = new ControllerMethodReturn();

			$data = array();
			$data['successes'] = $model_data['successes'];
			$data['errors'] = $model_data['errors'];

			if($model_data['from'] == 'comments')
			{
				$view->method = 'view_forum_post_comments';
				if(! is_ajax_view()):
					$data['number_of_entries'] = 5;
				endif;
				$data['sort_order'] = 'recent';
			}
			else
			{
				$view->method = 'view_forum_post';
				if(! is_ajax_view()):
					$data['number_of_entries'] = 5;
				endif;
			}

			$view->model_data = $data;

			return $view;
		}

		public function redirect_after_request_join(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'home';
			$view->model_data = $data;

			return $view;
		}

		public function group_like(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'home');
			$view = new ControllerMethodReturn();
			$view->model_data = $model_data;
			if($from == 'my_groups')
			{
				$view->method = 'list_my_groups';
			}
			else if($from == 'joined_groups')
			{
				$view->method = 'list_joined_groups';
			}
			else if($from == 'home')
			{
				$view->method = 'home';
			}
			return $view;

		}

		public function group_wall_post_submit(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'wall_posts');
			$view = new ControllerMethodReturn();
			if($from == 'wall_posts')
				$view->method = 'group_wall_posts';
			elseif($from == 'home')
				$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}

		public function group_wall_remove_post(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'wall_posts');
			$view = new ControllerMethodReturn();
			if($from == 'wall_posts')
				$view->method = 'group_wall_posts';
			elseif($from == 'wall_post_comments')
				$view->method = 'home';
			elseif($from == 'home')
				$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}

		public function group_wall_like_post(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'wall_posts');
			$view = new ControllerMethodReturn();
			if($from == 'wall_posts')
				$view->method = 'group_wall_posts';
			elseif($from == 'wall_post_comments')
				$view->method = 'group_wall_post_comments';
			elseif($from == 'home')
				$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}

		public function group_wall_post_comment_submit(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'wall_post_comments');
			$view = new ControllerMethodReturn();
			if($from == 'wall_posts')
				$view->method = 'group_wall_posts';
			elseif($from == 'wall_post_comments')
				$view->method = 'group_wall_post_comments';
			elseif($from == 'home')
				$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}
	}
?>