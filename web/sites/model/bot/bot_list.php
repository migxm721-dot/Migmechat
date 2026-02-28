<?php
	fast_require('ChatRoomDAO', get_dao_directory() . '/chatroom_dao.php');
	fast_require('GroupDAO', get_dao_directory().'/group_dao.php');
	fast_require("ThirdPartyApplicationDAO", get_dao_directory() . "/third_party_application_dao.php");
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");
	fast_require('UserCapability', get_domain_directory().'/capability/user_capability.php');
	fast_require('SystemProperty', get_library_directory().'/system/system_property.php');
	fast_require('WordPress', get_library_directory() . '/wordpress/wordpress.php');
	fast_require('WordPressDomain', get_domain_directory() . '/wordpress.php');
	fast_require("View", get_framework_common_directory()."/view.php");

	class BotListModel extends Model
	{
		public function get_data($model_data)
		{
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 10);
			$page = get_attribute_value('page', 'integer', 1);
			$offset = ($page - 1 ) * $number_of_entries;

			$data = array();
			$tpa_dao = new ThirdPartyApplicationDAO();
			$dao = new ChatRoomDAO();

			$session_user_id = get_value_from_array('session_user_id', $model_data, 'integer', 0);
			$session_user = get_value_from_array('session_user', $model_data);

			if($session_user_id && UserCapability::list_own_group_games_only($session_user_id))
			{
				$group_dao = new GroupDAO();
				// TODO: refactor
				$user_groups = $group_dao->get_groups($model_data['session_user'], 1, 50);
				$user_group_ids = array();
				foreach($user_groups['groups'] as $group)
				{
				    $user_group_ids[] = $group->id;
				}
				$game_view = (get_view() == View::MTK_MRE) ? View::MIDLET : get_view();
				$data['apps'] = $tpa_dao->get_applications_by_group_ids_and_view($user_group_ids, $game_view);
			}
			else
			{
				$system_property = SystemProperty::get_instance();

				// only show third party applications in first page
				if($system_property->get_integer(SystemProperty::ShowAllThirdPartyApplications, 1) && $page == 1)
				{
					$game_view = (get_view() == View::MTK_MRE) ? View::MIDLET : get_view();
					$data['apps'] = $tpa_dao->get_applications_by_view($game_view);
				}
				else
					$data['apps'] = array();
			}


			// GAMES-424 mig33 sample appss should not be visible in the games list pages on production
			if ( (substr($_SERVER['HTTP_HOST'], -9) == "mig33.com") || (substr($_SERVER['HTTP_HOST'], -9) == substr($GLOBALS['session_cookie_domain'], 1)) )
			{
				$data['apps'] = $this->remove_mig33_applications($data['apps']);
			}

			$data['game_bookmarks'] = $this->get_game_bookmarks($session_user);

			//featured games
			$data['featured_games'] = $this->get_featured_games($data['apps']);

			$bots = $dao->get_chatroom_game_bots($offset, $number_of_entries);
			$total_count = $bots['total_count'];
			$data['bots'] = $bots['bots'];
			$data['page'] = $data['current_page'] = $page;
			$data['total_pages'] = ceil($total_count/$number_of_entries);
			$data['numposts'] = $total_count;
			$data['posts_per_page'] = $number_of_entries;

			// get games which link to home page
			$system_property = SystemProperty::get_instance();
			$home_linked_games = $system_property->get_string(SystemProperty::LinkTPAHomeInList.ucfirst(get_view()), '');
			$data['home_linked_games'] = array();
			if (!empty ($home_linked_games))
			foreach( explode(':', $home_linked_games) as $link )
			{
				list($app_name, $controller_name, $controller_action) = explode(',', $link);
				$data['home_linked_games'][$app_name] = array
				(
					  'controller' 	=> $controller_name
					, 'action' 		=> $controller_action
				);
			}

			return $data;
		}


		function parse_wp_data($content, $model_data = '')
		{
			if (is_midlet_view() || is_mre_view())
			{
				fast_require('MobileCommonTranslator', get_library_directory() . '/translator/translator/mobilecommon_translator.php');
				fast_require('MobileNewTranslator', get_library_directory() . '/translator/translator/mobilenew_translator.php');
				fast_require('MobileDataTranslator', get_library_directory() . '/translator/translator/mobiledata_translator.php');

				$translator = new MobileCommonTranslator();
				$content = $translator->parse($content);

				$translator = new MobileNewTranslator();
				$content = $translator->parse($content);

				$translator = new MobileDataTranslator();
				$content = $translator->parse($content, $model_data);

			}
			if (is_ajax_view() || is_corporate_view())
			{
				fast_require('WebTranslator', get_library_directory() . '/translator/translator/web_translator.php');
				fast_require('DataTranslator', get_library_directory() . '/translator/translator/data_translator.php');

				$translator = new WebTranslator();
				$content = $translator->parse($content);

				$translator = new DataTranslator();
				$content = $translator->parse($content, $model_data);
			}
			if (is_wap_view())
			{
				fast_require("WapTranslator", get_library_directory() . "/translator/translator/wap_translator.php");
				fast_require("WapDataTranslator", get_library_directory() . "/translator/translator/wapdata_translator.php");

				$translator = new WapTranslator();
				$content = $translator->parse($content);

				$translator = new WapDataTranslator();
				$content = $translator->parse($content, $model_data);
			}

			return $content;
		}


		private function remove_mig33_applications($apps)
		{
			$filtered_apps = array();
			foreach ($apps as $app)
			{
				if (!$app->is_mig33_app())
				{
					$filtered_apps[] = $app;
				}
			}
			return $filtered_apps;
		}

		private function get_featured_games($apps)
		{
			$content = WordPress::get_instance()->get_page_content(WordPressDomain::$FEATURED_GAMES_PAGE);
			$content = $this->parse_wp_data($content);
			$content = html_entity_decode($content);
			
			if(sizeof($apps) > 0)
 			{
 				$counter = 0;
	            foreach($apps as $app_data)
	            {
					$launch_url = get_controller_action_url('opensocial', 'start_app', array('appid' => $app_data->name));
					if (is_touch_view())
					{
						$launch_url = open_activities_url($app_data->display_name, $launch_url, $app_data->name);
					}

					$content = str_replace
					(
						  '<game>'.$app_data->display_name.'</game>'
						, '<a href="'.$launch_url.'">'.$app_data->display_name.'</a>'
						, $content
						, $count
					);
					if($count > 0)
            			$counter++;
	            }
	            if($counter > 0)
            		return $content;
            }
            
            return '';
		}

		private function get_game_bookmarks($username)
		{
			$user_dao = new UserDAO();
			$list_of_games = $user_dao->get_unique_games_played($username);
			if (count($list_of_games) > 3)
			{
				$list_of_games = array_slice($list_of_games, 0, 3);
			}
			return array(
				  'list_of_games' => $list_of_games
			);
		}
	}
?>