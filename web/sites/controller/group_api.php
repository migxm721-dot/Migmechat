<?php
	fast_require("Rest", get_library_directory() . "/rest/rest.php");
	fast_require("RestJsonResponse", get_library_directory() . "/rest/rest_json_response.php");
	fast_require("GroupMember", get_domain_directory() . "/group/group_member.php");
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class GroupApiController
	{
		protected $response;

		public function __construct()
		{
			$this->response = new RestJsonResponse();
		}

		protected function send_on_error($call_result, $response_code = 500, $data = array())
		{
			if( $call_result->is_error() )
			{
				$this->response->send(500, $data);
			}
		}

		public function invalid_admin($model_data)
		{
			$this->response->send(500, array("error"=>get_value_from_array("error", $model_data)));
		}

		public function invalid_member($model_data)
		{
			$this->response->send(500, array("error"=>get_value_from_array("error", $model_data)));
		}

		public function unlink_chatroom($model_data)
		{
			$error = $model_data["error"];
			$ok = empty($error)?$model_data["ok"]:false;

			$this->response->send(200, array("ok"=>$ok, "error"=>$error));
		}

		public function check_group_vip()
		{
			$mobile_number = get_value("mn");
			$group_id = get_attribute_value("group_id", "integer", 0);

			$result = make_soap_call("loadUserDetailsFromMobilePhone", array($mobile_number));
			$this->send_on_error($result, 200, array("status"=>"invalid"));

			if( empty($result->data) )
			{
				$this->response->send(200, array("status"=>"invalid"));
			}

			$username = $result->data["username"];

			$this->send_on_error($result);

			$dao = new GroupDAO();
			$group_member = $dao->get_group_member($group_id, $username);;

			$this->response->send(200, array("status"=>($group_member->is_vip?"valid":"invalid")));
		}

		public function group($data)
		{
			$group = $data['group'];
			$member = $data['member'];
			$max_num_group_moderators = $data['max_num_group_moderators'];
			$num_group_moderators = $data['num_group_moderators'];

			$is_member = false;
			$is_vip = false;
			if( !empty($member) && $member->id > 0 && $member->is_active())
			{
				$is_member = true;
				$is_vip = $member->is_vip;
			}

			$this->response->send(200, array(
											'name'=>$group->name,
											'is_member'=>$is_member,
											'is_vip' => $is_vip,
											'is_administrator' => $member->is_administrator(),
											'created_by' => strtolower($group->created_by),
											'type' => $group->get_type(),
											'is_creator' => (strtolower($member->username)==strtolower($group->created_by)),
											'max_num_group_moderators' => $max_num_group_moderators,
											'num_group_moderators' => $num_group_moderators
											)

							);
		}

		public function get_group_create_info($data)
		{
			$this->response->send(200, array("has_user_chatrooms"=>$data["has_user_chatrooms"]));
		}

		public function join_group($data)
		{
			$ok = $data["ok"];
			$message = $data["message"];

			if( !empty($data["error"]) )
			{
				$ok = false;
				$message = $data["error"];
			}

			$this->response->send(200, array("ok"=>$ok, "message" => $message));
		}

		public function apply_join_group($data)
		{
			$ok = $data["ok"];
			$message = $data["message"];

			if( !empty($data["error"]) )
			{
				$ok = false;
				$message = $data["error"];
			}

			$this->response->send(200, array("ok"=>$ok, "message" => $message));
		}

		public function leave_group($data)
		{
			$this->response->send(200, array("ok"=>$data["ok"]));
		}

		public function group_members($data)
		{
			$members = array();

			$mdata = $data["members"];

			foreach( $mdata as $member )
			{
				$members[] = array(
								"u" => $member->username,
								"fid" => empty($member->display_picture)?"null":$member->display_picture,
								"admin" => $member->is_administrator()
				);
			}

			$this->response->send(200, array(
								"t" => get_value_from_array("members_total_results", $data, "integer", 8),
								"np" => get_value_from_array("members_total_pages", $data, "integer", 0),
								"p" => get_value_from_array("members_page", $data, "integer", 0),
								"m" => $members
							));
		}

		public function ignore_group_invite($data)
		{
			$group_id = get_attribute_value("group_id", "integer", 0);
			$session_user = get_value_from_array("session_user", $data);
			$result = make_soap_call("declineGroupInvitation", array($session_user, $group_id));

			$this->response->send(200, array("ok"=>($result->is_error==false)));
		}

		public function group_give_admin_rights($data)
		{
			$this->response->send(200, array("ok"=>$data["ok"]));
		}

		public function group_remove_admin_rights($data)
		{
			$this->response->send(200, array("ok"=>$data["ok"]));
		}

		public function group_block_user($data)
		{
			$this->response->send(200, array("ok"=>$data["ok"]));
		}

		public function group_unblock_user($data)
		{
			$this->response->send(200, array("ok"=>$data["ok"]));
		}

		public function group_like($data)
		{
			$this->response->send(200, $data);
		}

		public function get_user_wall($data)
		{
			$posts = $data["posts"];
			$p = array();

			foreach($posts as $post)
			{
				$p[] = $post->to_array();
			}
			$this->response->send(200, array(
				"lid" => get_value_from_array("last_post_id", $data, "integer", 0),
				"fiid" => get_value_from_array("first_post_id", $data, "integer", 0),
				"ope" => isset($data["older_posts_exist"])?$data["older_posts_exist"]:false,
				"posts" => $p
			));
		}

		public function user_wall_create_post($data)
		{
			$post = $data["post"];
			$this->response->send(200, array(
				"posts" => array($post->to_array())
			));
		}

		public function user_wall_remove_post($data)
		{
			$this->response->send(200, array(
				"ok" => $data['ok']
			));
		}

		public function user_wall_like_post($data)
		{
			$this->response->send(200, array(
				"likes" => get_value_from_array("num_likes", $data, "integer", 0),
				"dislikes" => get_value_from_array("num_dislikes", $data, "integer", 0)
			));
		}

		public function user_wall_dislike_post($data)
		{
			$this->response->send(200, array(
				"likes" => get_value_from_array("num_likes", $data, "integer", 0),
				"dislikes" => get_value_from_array("num_dislikes", $data, "integer", 0)
			));
		}

		public function get_photos($data)
		{
			$photos = $data["photos"];
			$num_pages = get_value_from_array("photos_number_of_pages", $data, "integer", 0);
			$num_entries = get_value_from_array("photos_number_of_entries", $data, "integer", 0);
			$total_entries = get_value_from_array("photos_total_entries", $data, "integer", 0);
			$page = get_value_from_array("photos_page", $data, "integer", 1);

			$p = array();
			foreach($photos as $photo)
			{
				$p[] = array(
							"fid" => $photo->file_id,
							"id" => $photo->id,
							"st" => $photo->status,
							"sz" => $photo->file_size,
							"c" => $photo->date_created,
							"w" => $photo->file_width,
							"h" => $photo->file_height,
							"u" => $photo->file_uploadedby,
							"dsc" => htmlspecialchars($photo->description),
							"numlikes" => $photo->num_likes,
							"numdislikes" => $photo->num_dislikes,
							"numcomments" => $photo->num_comments
						);
			}
			$this->response->send(200, array(
								"t" => $total_entries,
								"np"=>$num_pages,
								"p"=>$page,
								"d" => $p
								));
		}

		public function like_photo($data)
		{
			$this->response->send(200, $data);
		}

		public function group_wall_posts($data)
		{
			$posts = $data['posts'];
			$first_post_id = $posts[0]->id;
			$last_post_id = $posts[sizeof($posts)-1]->id;
			$older_posts_exist = ($data['total_pages'] > $data['current_page']) ? true : false;

			$p = array();
			if(sizeof($posts) > 0)
			{
				foreach($posts as $post)
				{
					$p[] = $post->to_array();
				}
			}
			$this->response->send(200, array(
				'lid' => $last_post_id,
				'fiid' => $first_post_id,
				'ope' => $older_posts_exist,
				'posts' => $p
			));
		}

		public function group_wall_post($data)
		{
			$post = $data['post']->to_array();

			$this->response->send(200, array(
				'lid' => $data['post']->id,
				'error' => get_value_from_array('error', $data),
				'posts' => $post
			));
		}

		public function group_wall_create_post($data)
		{
			$post = $data['new_post'];
			$this->response->send(200, array(
				'posts' => array($post->to_array()),
				'i18n'  => array('Comments' => _('Comments'), 'Likes' => _('Likes'), 'Delete' => _('Delete'))
			));
		}

		public function group_wall_create_comment($data)
		{
			$comment = $data['new_comment'];
			$this->response->send(200, array(
				'c' => array($comment->to_array())
			));
		}

		public function group_wall_remove_post($data)
		{
			$this->response->send(200, array(
				'ok' => $data['ok']
			));
		}

		public function group_wall_remove_comment($data)
		{
			$this->response->send(200, array(
				'ok' => $data['ok']
			));
		}

		public function group_wall_like_post($data)
		{
			$this->response->send(200, array(
				'likes' => get_value_from_array('likes', $data, 'integer', 0),
				'dislikes' => 0
			));
		}

		public function group_wall_post_comments($data)
		{
			$c = array();
			$comments = $data['comments'];
			$last_comment_id = intval($comments[0]->id);
			$older_comments_exist = ($data['total_pages'] > $data['current_page']) ? true : false;

			if(sizeof($comments) > 0)
			{
				foreach($comments as $comment)
				{
					$c[] = $comment->to_array();
				}
			}
			$this->response->send(200, array(
				'oce' => $older_comments_exist,
				'rcnt' => $data['numposts'],
				'lcid' => $last_comment_id,
				'c' => array_reverse($c)
			));
		}

		/**
		 * Get Group
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_group(&$model_data)
		{
			$data = array();
			$data['group'] = $model_data['group'];
			foreach(array(
				  'session_user_country_id'
				, 'session_user_member'
				, 'session_user_admin'
				, 'session_user_moderator'
				, 'session_user_creator'
				, 'request_exists'
				, 'moderators'
			) as $name)
				$data['group']->$name = $model_data[$name];
			$rest_result = new RestResult($data);
			return $rest_result->set_debug($model_data);
		}

		/**
		 * To be used with extract, should be moved to a helper class
		 * @param string $sort_by
		 * @param string $sort_order
		 * @return multitype:string
		 */
		protected function get_sort_param($sort_by = 'datecreated', $sort_order = 'desc')
		{
			$sort_by = get_attribute_value('sort_by', 'string', 'datecreated');
			! in_array($sort_by, array('name', 'datecreated', 'nummembers','numforumposts'))
				&& $sort_by = 'datecreated';

			$sort_order = get_attribute_value('sort_order', 'string', 'desc');
			! in_array($sort_order, array('asc', 'desc'))
				&& $sort_order = 'desc';

			return array('sort_by' => $sort_by, 'sort_order' => $sort_order);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_official_groups(&$model_data)
		{
			$data = array();
			$group_dao = new GroupDAO();
			extract($this->get_sort_param()); // $sort_by, $sort_order
			$data['groups'] = $group_dao->get_official_groups($sort_by, $sort_order);
			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_featured_groups(&$model_data)
		{
			$data = array();
			$group_dao = new GroupDAO();
			extract($this->get_sort_param()); // $sort_by, $sort_order
			$data['groups'] = $group_dao->get_featured_groups($sort_by, $sort_order);
			return new RestResult($groups);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_groups_by_user(&$model_data)
		{
			$group_dao = new GroupDAO();
			extract($this->get_sort_param()); // $sort_by, $sort_order
			$groups = $group_dao->get_groups($group_dao->get_username(get_attribute_value('user_id'))
				, get_attribute_value('page', 'integer', 1)
				, get_attribute_value('number_of_entries', 'integer', 10)
				, $sort_by
				, $sort_order
			);
			foreach ($groups['groups'] as &$group)
			{
				//Check memcache for group likes
				$group->likes = $group_dao->get_group_likes($group->id);
			}
			return new RestResult($groups);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_group_members(&$model_data)
		{
			$group_dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');

			$group_members = $group_dao->get_group_members($group_id
				, get_attribute_value('offset', 'integer', 0)
				, get_attribute_value('number_of_entries', 'integer', 10)
			);
			return new RestResult($group_members);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_group_moderators(&$model_data)
		{
			$group_dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');

			$group_moderators = $group_dao->get_moderators($group_id);
			$max_num_group_moderators = $group_dao->get_max_moderators($group_id);
			$num_group_moderators = $group_dao->get_moderators_count($group_id);

			$data = array();
			$data['moderators'] = $group_moderators;
			$data['total']		= $num_group_moderators;
			$data['max']		= $max_num_group_moderators;

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_group_pending_invites(&$model_data)
		{
			$group_dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');

			$pending_invites = $group_dao->get_join_requests($group_id);
			$num_pending_invites = $group_dao->get_total_join_requests($group_id);

			$data = array();
			$data['pending_invites'] = $pending_invites;
			$data['total']			 = $num_pending_invites;

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_join(&$model_data)
		{
			$data = array();
			if(!empty($model_data['successes']))
				$data['success']	= $model_data['successes'][0];
			if(!empty($model_data['error']))
				$data['error']		= $model_data['error'];

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_leave(&$model_data)
		{
			$data = array();
			$group_id = get_attribute_value('group_id', 'integer');

			if(!$model_data['session_user_creator'])
			{
				$call_return = make_soap_call('leaveGroup', array(SessionUtilities::$session_user, $group_id));

				if($call_return->is_error())
					$data['error'] = $call_return->message;
				else
					$data['success'] = _('You have left the group');
			}
			else
			{
				$data['error'] = 'Owner can\'t leave the group';
			}

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_invite_request(&$model_data)
		{
			$data = array();
			$group_dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');
			$result = $group_dao->create_join_request($group_id, SessionUtilities::$session_user_id);

			if($result)
			{
				$data['success'] = _('Your request to join this group has been sent');
			}
			else
			{
				$data['error']	= _('Error requesting to join this group');
			}

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_invite_accept(&$model_data)
		{
			$data = array();
			if(!empty($model_data['successes']))
				$data['success']	= $model_data['successes'][0];
			if(!empty($model_data['errors']))
				$data['error']		= $model_data['errors'][0];

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_invite_reject(&$model_data)
		{
			$data = array();
			if(!empty($model_data['successes']))
				$data['success']	= $model_data['successes'][0];
			if(!empty($model_data['errors']))
				$data['error']		= $model_data['errors'][0];

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_member_promote(&$model_data)
		{
			$data = array();
			$dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');
			$username = get_attribute_value('username');

			$result = $dao->give_group_member_admin_rights(SessionUtilities::$session_user, $group_id, $username);

			if($result == 'ok')
			{
				$data['success'] = sprintf(_('%s is now a moderator of your group'), $username);
			}
			else
			{
				$data['error']	= sprintf(_('Error setting %s to be a moderator of your group'), $username);
			}

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_member_demote(&$model_data)
		{
			$data = array();
			$dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');
			$username = get_attribute_value('username');

			$result = 	$dao->remove_group_member_admin_rights(SessionUtilities::$session_user, $group_id, $username);

			if($result == 'ok')
			{
				$data['success'] = sprintf(_('%s is no longer a moderator of your group'), $username);
			}
			else
			{
				$data['error']	= sprintf(_('Error removing %s as a moderator from your group'), $username);
			}

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function post_group_member_block(&$model_data)
		{
			$data = array();
			$dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');
			$username = get_attribute_value('username');

			$result = $dao->ban_group_member(SessionUtilities::$session_user, $group_id, $username);

			if($result == 'ok')
			{
				$data['success'] = sprintf(_('Block %s successfully'), $username);
			}
			else
			{
				$data['error']	= sprintf(_('Error blocking %s'), $username);
			}

			return new RestResult($data);
		}

		/**
		 * @param array $model_data
		 * @return RestResult
		 */
		public function get_group_chatrooms(&$model_data)
		{
			fast_require('Chatroom', get_domain_directory() . '/chatroom/chatroom.php');
			$group_dao = new GroupDAO();
			$group_id = get_attribute_value('group_id', 'integer');
			$page = get_attribute_value('page', 'integer', 1);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 33);
			$category_id = get_attribute_value('category_id', 'integer', 0);

			$result = make_soap_call('getGroupChatrooms', array($group_id, $category_id, $page, $number_of_entries));

			$data = array();
			$group_chatrooms = $result->data['group_chatrooms'];
			$page = get_value_from_array('page', $result->data, 'integer', 0);
			$total_results = get_value_from_array('totalresults', $result->data, 'integer', 0);
			$total_pages = get_value_from_array('totalpages', $result->data, 'integer', 0);

			$chatrooms = array();
			if (is_array($group_chatrooms) && sizeof($group_chatrooms) > 0)
			{
				foreach ($group_chatrooms as $chatroom)
				{
					$room = new Chatroom();
					$room->id = get_value_from_array('id', $chatroom, 'integer', 0);
					$room->name = get_value_from_array('name', $chatroom, 'string', '');
					$room->max_users = get_value_from_array('maximumSize', $chatroom, 'integer', 25);
					$room->type = get_value_from_array('type', $chatroom, 'string', 'CHATROOM');
					$room->description = get_value_from_array('description', $chatroom, 'string', '');
					$room->total_users = get_value_from_array('size', $chatroom, 'integer', 0);
					$room->category_id = $category_id;
					$room->user_owned = get_value_from_array('creator', $chatroom, 'string', '');
					$chatrooms[] = $room;
				}
			}

			$data['chatrooms'] = $chatrooms;
			$data['chatroom_page'] = $page;
			$data['chatroom_total_results'] = $total_results;
			$data['chatroom_total_pages'] = $total_pages;
			$data['chatroom_category_id'] = $category_id;
			return new RestResult($data);
		}
	}
