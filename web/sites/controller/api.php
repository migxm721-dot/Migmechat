<?php
	fast_require("Rest", get_library_directory() . "/rest/rest.php");
	fast_require("RestJsonResponse", get_library_directory() . "/rest/rest_json_response.php");
	fast_require("VirtualGiftDAO", get_dao_directory() . "/virtual_gift_dao.php");
	fast_require("NewRelicInstrumentation", get_library_directory() . "/instrumentation/new_relic_events.php");

	// Note, those returning with $this->response->send() can be removed. We are converting to new RestResult()
	class ApiController
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

		protected function convert_location_to_url( $location )
		{
			global $server_root;
			$location = str_replace("\\", "/", $location);
			$pos = strpos($location, "emoticons");
			$base = substr($location, $pos);
			$imageUrl = "/images/".$base;
			return $imageUrl;
		}

		public function get_emoticon($data)
		{
			$post_data = file_get_contents("php://input");
			$e = json_decode($post_data);

			// default return value
			$emots = array();

			// only contact jboss if necessary
			if(!empty($e) && is_array($e->hotkeys) && count($e->hotkeys) > 0)
			{
				$keys = implode(' ', $e->hotkeys);
				$result = make_soap_call("getEmoticonDetailsFromHotkeys", array($keys));
				if (is_array($result->data))
				{
					foreach($result->data as $data)
					{
						$emots[] = array
						(
							"hk" => get_value_from_array("hotkey", $data),
							"a" => get_value_from_array("alias", $data),
							"url" => $this->convert_location_to_url(get_value_from_array("location", $data)),
							"t" => get_value_from_array("type", $data)
						);
					}
				}
			}

			$this->response->send(200, array("emoticons"=>$emots));
		}

		public function get_user_permissions($data)
		{
			$this->response->send(200, array(
								"allow_like" => $data["allow_like"],
								"allow_like_level" => get_value_from_array("allow_like_level", $data, "integer", 0),
								"allow_add_to_photo_wall" => $data["allow_add_to_photo_wall"],
								"avatar_created" => $data["avatar_created"],
								"allow_use_display_picture" => get_value_from_array("allow_use_display_picture", $data, "boolean", true)
							));
		}

		public function user_like($data)
		{
			$this->response->send(200, array("likes"=>get_value_from_array("likes", $data, "integer", 0)));
		}

		public function get_user_gifts($data)
		{
			return new RestResult(array(
				  'total'	=> $data['number_of_gifts']
				, 'gifts'	=> $data['gifts_received']
			));
		}

		public function get_user_games($data)
		{
			global $mogileFSImagePath;
			// Prevent exposure of Oauth key & secret.
			foreach($data['list_of_games'] as &$game)
			{
				if ( $game instanceof ThirdPartyApplication )
				{
					unset($game->oauth_consumer_key);
					unset($game->secret);
					unset($game->ip_whitelist);
					unset($game->notes);
					unset($game->created_by);
					unset($game->date_created);
					unset($game->date_last_modified);
					unset($game->last_modified_by);
					unset($game->users);
					unset($game->auth_callback_url);
					unset($game->status);
					$views = array();
					foreach($game->views as $key => $view)
					{
						$views[$key] = array(
							  'view' => $key
							, 'url'  => get_framework_url('opensocial', 'start_app', $key, array('appid'=>$game->name))
						);
					}
					$game->views = $views;
					$game->group_picture_url = $game->get_group_picture_url();
				}
			}
			return new RestResult(array(
				  'total'	=> $data['num_of_games']
				, 'games'	=> $data['list_of_games']
			));
		}

		public function profile_compat($data)
		{
			return new RestResult(array
				(
				    'presence' => $data['presence']
				  , 'balance'  => $data['balance']
				)
			);
		}

		public function get_profile($data)
		{
			$username = get_value("username");
			$session_user = get_value_from_array("session_user", $data);
			$user_profile = $data["user_profile"];

			$result = make_soap_call("isContactFriend", array($session_user, $username));

			$is_friend = $username==$session_user?true:$result->data;

			$viewable = false;
			if( ($username == $session_user) || ($is_friend && $user_profile->is_friend_only_profile()) || $user_profile->is_public_profile())
				$viewable = true;

			$this->response->send(200, array("is_friend"=>$is_friend, "viewable"=>$viewable,
								"is_private" => $user_profile->is_private_profile(),
								"avatar_created"=>get_value_from_array("user_avatar_created", $data, "boolean", false)
								));
		}

		public function get_friends($data)
		{
			global $mogileFSImagePath;

			$f = $data["friends"];

			$friends = array();
			for( $i=0; $i<sizeof($f); $i++ )
			{
				$fr = $f[$i];
				$friends[] = array(
								"id"=> $fr['userid'],
								"u" => $fr['username'],
								"c" => $fr['country'],
								"limg" => $fr['level_image']
							);
			}

			$page = get_value_from_array("page", $data, "integer", 1);
			$offset = $page * count($friends);

			$this->response->send(200, array(
								"p" => $page,
								"f" => $friends,
								"t" => (($data['older_entries_exist']) ? $offset + 1 : $offset) + (($page == 1) ? 0 : 1)
							));
		}

		public function browse_profile($data)
		{
			$users = $data["profiles"];
			$number_of_pages = get_value_from_array("number_of_pages", $data, "integer", 0);
			$total_results = get_value_from_array("total_results", $data, "integer", 0);
			$page = get_value_from_array("page", $data, "integer", 0);

			$p = array();

			foreach( $users as $user )
			{
				$p[] = array(
							"fid" => get_value_from_array("display_id", $user),
							"u" => get_value_from_array("username", $user),
							"c" => get_value_from_array("country", $user)
						);
			}

			$this->response->send(200, array(
								"t" => $total_results,
								"np"=>$number_of_pages,
								"p"=>$page,
								"u" => $p
								));
		}

		public function get_photos($data)
		{
			$p = array();
			$i = 0;
			$photos = $data["photos"];
			if( is_array($photos) )
			foreach($photos as $photo)
			{
				$pl = $data["likes"][$photo->item_id];
				$p[] = array(
							"fid"	=>	$photo->image_id,
							"id"	=>	$photo->item_id,
							"st"	=>	$photo->status_name,
							"c"		=>	$photo->date_created,
							"w"		=>	$photo->width,
							"h"		=>	$photo->height,
							"u"		=>	$photo->uploaded_by,
							"dsc"	=>	htmlspecialchars($photo->description),
							"numlikes"		=> get_value_from_array('numlikes', $pl, 'integer', 0),
							"numdislikes"	=> get_value_from_array('numdislikes', $pl, 'integer', 0),
							"index"	=>	(get_value_from_array('offset', $data, 'integer', 0) + $i)
						);
				$i++;
			}

			$older_photos_exist = get_value_from_array('older_photos_exist', $data, 'boolean', false);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 6);
			$page = get_attribute_value('page', 'integer', 1);
			$page_number = ($page+$number_of_entries)/$number_of_entries;
			if($page_number <= 0)
				$page_number = 1;
			$offset = $page_number * $number_of_entries;

			$this->response->send(200, array(
								"t" => ($older_photos_exist ? ($offset + 1) : ($offset - 1)),
								"p" => $page,
								"d" => $p
								));
		}

		public function get_my_photos($data)
		{
			$p = array();
			$i = 0;
			$photos = $data["photos"];
			foreach($photos as $photo)
			{
				$pl = $data["likes"][$photo->item_id];
				$p[] = array(
							"fid"	=>	$photo->image_id,
							"id"	=>	$photo->item_id,
							"st"	=>	$photo->status_name,
							"c"		=>	$photo->date_created,
							"w"		=>	$photo->width,
							"h"		=>	$photo->height,
							"u"		=>	$photo->uploaded_by,
							"dsc"	=>	htmlspecialchars($photo->description),
							"numlikes"		=> get_value_from_array('numlikes', $pl, 'integer', 0),
							"numdislikes"	=> get_value_from_array('numdislikes', $pl, 'integer', 0),
							"index"	=>	(get_value_from_array('offset', $data, 'integer', 0) + $i)
						);
				$i++;
			}

			$older_photos_exist = get_value_from_array('older_photos_exist', $data, 'boolean', false);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 6);
			$page = get_attribute_value('page', 'integer', 1);
			$page_number = ($page+$number_of_entries)/$number_of_entries;
			if($page_number <= 0)
				$page_number = 1;
			$offset = $page_number * $number_of_entries;

			$this->response->send(200, array(
								"t" => ($older_photos_exist ? ($offset + 1) : ($offset - 1)),
								"p" => $page,
								"d" => $p
								));
		}

		public function get_photo_wall($data)
		{
			$photos = $data["photos"];

			$record = get_attribute_value("page", "integer", 0);
			$page = intval($record/8 + 1);
			$p = array();

			foreach($photos as $photo)
			{
				$p[] = array(
							"fid" => $photo->image_id,
							"id" => $photo->item_id,
							"c" => date("H:i d M Y",$photo->date_created),
							"w" => $photo->width,
							"h" => $photo->height,
							"u" => $photo->received_from,
							"d" => htmlspecialchars($photo->description)
						);
			}

			if( count($p) == 8 )
				$total = $page*8+8;
			else
				$total = $record + count($p);
			$this->response->send(200, array(
								"t" => $total,
								"np"=>$page+1,
								"p"=>$page,
								"d" => $p
								));
		}

		public function get_my_photos_header($data)
		{
			$photos = $data["photos"];

			$photo_data = $photos[0];

			$this->response->send(200, array(
								"t" => get_value_from_array("numEntries", $photo_data, "integer", 0),
								"ts" => get_value_from_array("totalSize", $photo_data, "integer", 0)
								));
		}

		public function publish_to_wall($data)
		{
			$session_user = get_value_from_array("session_user", $data);
			$image_id = get_attribute_value("image_id");
			$description = get_attribute_value("description");

			$result = make_soap_call("saveExistingFileToScrapbooks", array($session_user, array("wall200712041"), $image_id, $description));
			$this->response->send(200, array("st"=>"ok"));
		}

		public function report_wall_photo($data)
		{
			$this->response->send(200, array("st"=>"ok"));
		}

		public function gift_like($data)
		{
			$dao = new VirtualGiftDAO();
			$session_user = get_value_from_array("session_user", $data);
			$id = get_value("vgrid", "integer", 0);
			$type = get_value("like", "integer", 0);
			$like = $dao->like_virtual_gift($session_user, $id, $type);
			$this->response->send(200, $like);
		}

		public function photo_like($data)
		{
			$this->response->send(200, $data);
		}

		public function storeitem_rate($data) {
			$this->response->send(200, $data);
		}

        public function block_list($data)
        {
            $blocked_users = $data['blocked_users'];

            $users = array();
            if(!empty($blocked_users) && is_array($blocked_users))
            {
                foreach($blocked_users as $user)
                    $users[] = array('u'=>$user);
            }

            $this->response->send(200, array(
								"t" => get_value_from_array("total_results", $data, "integer", 8),
								"np" => get_value_from_array("number_of_pages", $data, "integer", 0),
								"p" => get_value_from_array("page", $data, "integer", 0),
								"m" => $users
							));
        }

        public function unblock_user($data)
        {
            $this->response->send(200, array("ok"=>true));
        }

		public function get_user_chatrooms($data)
		{
			return new RestResult(array(
				  'own' => array('total' => $data['own_total_results'], 'chatrooms' => $data['own_chatrooms'])
				, 'mod' => array('total' => $data['mod_total_results'], 'chatrooms' => $data['mod_chatrooms'])
			));
		}

		public function push_events_to_instrumentation()
		{
			$data_from_client = get_value('logs');
			if(!empty($data_from_client))
			{
				$log_arr = json_decode($data_from_client, true);
				SessionUtilities::push_user_logs($log_arr);
			}

			$this->response->send(200, array("ok"=>true));
		}
	}
?>