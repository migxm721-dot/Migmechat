<?php
	fast_require("SessionUtilities", get_framework_common_directory() . "/session_utilities.php");
	fast_require('UserDAO', get_dao_directory() . "/user_dao.php");
	fast_require('VasDAO', get_dao_directory() . "/vas_dao.php");
	fast_require('Language', get_library_directory().'/language/Language.php');
	fast_require('RestResult', get_library_directory().'/rest/rest_result.php');
	fast_require('UserCapability', get_domain_directory() . '/capability/user_capability.php');

	class VasController
	{
		public function login(&$model_data)
        {
			if (SessionUtilities::is_logged_in())
			{
				return new ControllerMethodReturn('agreements');
			}
			else
			{
				//var_dump($data, $error); foreach($error->errors as $error) echo $error; exit();
				fast_require('Logger', get_framework_common_directory() . '/logger.php');
				Logger::getLogger('infinite redirect')->info(json_encode($data));

				//Reset cookie to prevent infinite redirection loop
				setcookie(SessionUtilities::ENCRYPTED_SESSION_COOKIE_KEY, null, 1, '/', $GLOBALS['session_cookie_domain']);
				setcookie(SessionUtilities::SESSION_COOKIE_KEY, null, 1, '/', $GLOBALS['session_cookie_domain']);
				redirect($GLOBALS['login_server_root']);
			}
        }

        public function vasworld_action_redirect(&$model_data)
        {
        	$from = get_attribute_value('from', 'string', '');

			$view = new ControllerMethodReturn();
			$view->method = $from == 'vasworld_status' ? 'vasworld_status' : 'view_vasworld';
			$view->model_data = $model_data;
			return $view;
        }

		public function get_vas_main_menu()
		{
			// 1. check that there is a version parameter passed in
			$version_known = get_value('version', 'string', '');
			if ('' === $version_known || !ctype_digit($version_known))
			{
				$err = 'version parameter is missing or invalid';
				return new RestResult(null, 400, 1, $err, $err);
			}


			// 2. extract the mig identifier from the user agent string
			$user_agent = "";
			if (
				   preg_match('#mig33/(android|blackberry|mre)/[a-z0-9._-]+#i', $_SERVER['HTTP_USER_AGENT'], $matches)
				|| preg_match('#J2MEv[a-z0-9._-]+#i', $_SERVER['HTTP_USER_AGENT'], $matches)
			)
			{
				$user_agent = $matches[0];
			}
			else
			{
				$err = 'invalid client';
				return new RestResult(null, 403, 2, $err, $err);
			}


			// 3. locate build version details from DB
			$dao = new VasDAO();
			$build = $dao->get_partner_build($user_agent);

			if (null === $build)
			{
				// there is no custom menu for this build
				$msg = 'no vas menu for build '.$user_agent;
				return new RestResult(null, 404, 0, '', $msg);
			}


			// 4. if version has not changed, return a 304 - Not modified
			if ($build['MainMenuVersion'] == $version_known)
			{
				$res = new RestResult(null, 304);
				$res->set_cache(43200); // 12 hours;
				return $res;
			}


			// 5. version differs, we must fetch the menu and format the response
			$menu = $dao->get_menu($user_agent, 0); // *cough* magic number type 0 is main menu

			if (null === $menu)
			{
				$err = "menu could not be found for $user_agent";
				return new RestResult(null, 404, 3, $err, $err);
			}

			// 6. perform translation of the menu labels (based on language pased in
			foreach($menu as &$entry)
			{
				$entry['label'] = _($entry['label']);
			}

			// 7. prepare the response
			$response = array
			(
				  'v'           => $build['MainMenuVersion']
				, 'n_cols'      => 3
				, 'body_bg_url' => isset($build['Options']['MainMenuBackground']) ? $build['Options']['MainMenuBackground'] : null
				, 'css_url'     => null
				, 'ctas'        => $menu
				, 'lang'        => Language::get_request_language()
			);

			// 8. and sends it as json! (possibly as jsonp)
			$res = new RestResult($response);
			$res->set_cache(43200); // 12 hours;
			return $res;
		}
	}
?>
