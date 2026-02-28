<?php
	fast_require("RegistrationDAO", get_dao_directory() . "/registration_dao.php");

	class StoreController
	{
		public function home()
		{
			return new ControllerMethodReturn('home');
		}

		public function show_with_category()
		{
			return new ControllerMethodReturn('show_with_category');
		}

		public function check_for_available_data(&$model_data)
		{
			$storeitems = $model_data['storeitems'];
			$item_type = get_attribute_value("type", "integer", 0);
			$has_subcategory = get_value_from_array("has_subcategory", $model_data, "boolean", true);
			$category_id = get_value_from_array("category", $model_data);

			if(empty($storeitems) || sizeof($storeitems) == 0)
			{
				if($item_type != 0)
				{
					if($has_subcategory)
					{
						//Get all item of specific type
						$view = new ControllerMethodReturn();
						if(is_wap_view())
						{
							$view->method = 'all_items_wap';
						}
						else
						{
							$view->method = 'all_items';
						}

						$data = array();
						$data["has_subcategory"] = $has_subcategory;
						$data["type"] = $item_type;
						$data["category_id"] = $model_data["category_id"];
						$data["parents"] = $model_data["parents"];
						$data["categories"] = $model_data["categories"];
						$data['balance'] = $model_data["balance"];

						//$view->model_data = $model_data;
						$view->model_data = $data;
						return $view;
					}
				}
				else
				{
					//Get some featured items
					$view = new ControllerMethodReturn();
					$view->method = 'featured';
					$view->model_data = $model_data;
					return $view;
				}
			}
		}

		public function show_categories(&$model_data)
		{
			$total_categories_results = get_value_from_array("total_categories_results", $model_data, "integer", 0);

			//Check if there are any more sub categories in it. If none, grab items under category
			if($total_categories_results > 0)
			{
				$view = new ControllerMethodReturn();
				$view->method = 'categories_result';
				$view->model_data = $model_data;
				return $view;
			}
			else
			{
				//Get items under that category
				$view = new ControllerMethodReturn();
				$view->method = 'list_items_in_category';
				$view->model_data = $model_data;
				return $view;
			}
		}

		public function check_add_message_data(&$model_data)
		{
			$username_attribute = get_attribute_value("username", "string");
			$username_post = $model_data["username"];
			$dao = new RegistrationDAO();
			$usernames_error = $dao->usernames_available(array($username_post, $username_post));
			if((empty($username_attribute) && empty($username_post)) || !empty($usernames_error))
			{
				$view = new ControllerMethodReturn();
				$view->method="view_item";
				if(!empty($usernames_error))
				{
					$model_data['error'] = 'Invalid username.';
				}
				else
				{
					$model_data['error'] = 'You need to select a friend or specify a username.';
				}
				$view->model_data = $model_data;
				return $view;
			}
		}

		public function check_buy_item(&$model_data)
		{
			$storeitem = $model_data['storeitem'];
			$message = get_value("message", "string");
			$success = false;

			// If the item belongs to a group the user must be a member of the group
			if ($storeitem->is_limited_to_group() && !$storeitem->is_group_member($session_user))
			{
				$error = "You must be a member of the $storeitem->group_name group to buy this item.";
			}
			else
			{
				// $dao = new StoreDAO();
				// $result = $dao->buyVirtualGift($model_data);
				$result = $storeitem->buy($model_data);

				$error = $result->message;
				//Check for success/failure. redirect to the right place
				if($storeitem->is_virtualgift() || $storeitem->is_avatar())
				{
					if($result->status == 1)
					{
						$success = true;
					}
				}
				else if($storeitem->is_emoticonpack() || $storeitem->is_superemoticonpack() || $storeitem->is_stickerpack())
				{

					if($result->status == 1)
					{
						$success = true;
					}
				}
			}

			if(!$success)
			{
				//failure
				$model_data['error'] = $error;
				$model_data['message'] = $message;

				//find out what type of items
				if($storeitem->can_have_personal_message())
				{
					//Back to add message area
					$view = new ControllerMethodReturn();
					$view->method="add_message";
					$view->model_data = $model_data;
					return $view;
				}
				else
				{
					$view = new ControllerMethodReturn();
					$view->method="view_item";
					$view->model_data = $model_data;
					return $view;
				}
			}
			else
			{
				//success
				$data = array();
				$data['storeitem'] = $model_data['storeitem'];
				$data['username'] = get_attribute_value("username", "string", '');
				$data['message'] = get_value("message", "string");
				$data['success'] = true;
				$data['storeitem']->num_sold++;
				$data['new_item_bought'] = true;
				$view = new ControllerMethodReturn();

				if($storeitem->item_to_subscribe())
				{
					$view->method="subscribe_item_success";
				}
				else
				{
					$view->method="buy_item_success";
				}

				$view->model_data = $data;
				return $view;
			}
		}

		public function check_push_item(&$model_data)
		{
			$storeitem = $model_data['storeitem'];
			$result = $storeitem->push($model_data);

			//Check for success/failure. redirect to the right place
			if(!empty($result['success']))
			{
				$success = true;
			}

			if(!$success)
			{
				//failure
				$model_data['error'] = $result->message;

				//find out what type of items
				$view = new ControllerMethodReturn();
				$view->method="view_item";
				$view->model_data = $model_data;
				return $view;
			}
			else
			{
				//success
				$data['storeitem'] = $model_data['storeitem'];
				$data['username'] = get_attribute_value("username", "string", '');
				$data['success'] = true;

				$view = new ControllerMethodReturn();
				$view->method="push_item_success";
				$view->model_data = $data;
				return $view;
			}
		}

		public function pick_friend($model_data)
		{
			$users = $model_data['users'];
			if( empty($users) )
			{
				$view = new ControllerMethodReturn();
				$view->method="enter_user";
				return $view;
			}
		}

		private function has_username($model_data)
		{
			$username = $model_data['username'];
			return !empty($username);
		}

		public function storeitem_rate(&$model_data) {
			$view = new ControllerMethodReturn();
			$view->model_data = $model_data;
			$view->method = "view_item";
			return $view;
		}
		/* End Virtual Gifts */

		public function list_rest(&$model_data)
		{
			$items = get_value('popular')
				? $model_data['popularitems']
				: (isset($model_data['new_items'])
					? $model_data['new_items']
					: $model_data['storeitems']
				);

			$data = array(
				'type' => 'store',
				'items' => array()
			);

			foreach($items as $storeitem)
			{
				$data['items'][] = array(
					  'id' => $storeitem->id
					, 'price' => ceil(($storeitem->price)*100)/100
					, 'image' => $storeitem->get_catalog_image_url_touch()
					, 'name' => $storeitem->name
					, 'currency' => $storeitem->currency
					, 'url' => get_framework_url(get_controller()
						, 'view_item'
						, 'touch'
						, array('item_id' => $storeitem->id, 'username' => get_attribute_value('username'))
					)
					, 'price_string' => $storeitem->price > 0
						? $storeitem->currency . ' ' . ceil(($storeitem->price)*100)/100
						: _('(Free)')
				);
			}

			return new RestResult($data);
		}
	}
?>