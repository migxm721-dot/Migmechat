<?php
	fast_require('Model', get_framework_common_directory() . '/model.php');
	fast_require('UserDAO', get_dao_directory() . '/user_dao.php');
	fast_require('Constants', get_framework_common_directory() . '/constants.php');
	fast_require('SessionUtilities', get_framework_common_directory() . '/session_utilities.php');
	fast_require('UserDetailLazy', get_domain_directory() . '/user/user_detail_lazy.php');
	fast_require('UserReputationLevelLazy', get_domain_directory() . '/user/user_reputation_level_lazy.php');

	class UserModel extends Model
	{
		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args return_here_upon_login allow_no_session encode_payload
		 */
		public function get_data($model_data, $model_args = array())
		{
			try
			{
				$data = SessionUtilities::get_session_details();
				$data['migLevel'] = new UserReputationLevelLazy($data['session_user']);
				return $data;
			}
			catch (Exception $exception)
			{
				//Reset cookie to prevent infinite redirection loop
				setcookie(SessionUtilities::ENCRYPTED_SESSION_COOKIE_KEY, null, 1, '/', $GLOBALS['session_cookie_domain']);
				setcookie(SessionUtilities::SESSION_COOKIE_KEY, null, 1, '/', $GLOBALS['session_cookie_domain']);

				if (is_array($model_args) && in_array('return_here_upon_login', $model_args))
					redirect(get_login_url());

				if (is_array($model_args) && in_array('allow_no_session', $model_args))
					return null;

				if (is_array($model_args) && in_array('encode_payload', $model_args))
					$this->encode_payload();

				if(get_view() == 'wap' || get_view() == 'touch') {
					redirect(get_login_url());
				}

				$this->redirect_to_login_then_home();
			}
		}

		private function encode_payload()
		{
			$definition = new ControllerDefinition(get_controller(), get_action());
			$encoding = $definition->get_encoding();

			fast_require('RestResult', get_library_directory() . '/rest/rest_result.php');
			$rest_result = new RestResult(null, 401, 401, _('Invalid session'));
			switch( $encoding )
			{
				case 'xml':
					fast_require('RestXmlResponse', get_library_directory() . '/rest/rest_xml_response.php');
					$response = new RestXmlResponse($rest_result);
					break;
				case 'json':
				default:
					fast_require('RestJsonResponse', get_library_directory() . '/rest/rest_json_response.php');
					$response = new RestJsonResponse($rest_result);
					break;
			}

			$response->send_response();
			exit();
		}

		private function redirect_to_login_then_home()
		{
			if (DEBUG_MODE) redirect(get_login_url());

			$home_list = array(
				  '/ajax/' => array('c'=>'web_portal', 'a'=>'home')
				, '/midlet/' => array('c'=>'migworld', 'a'=>'home')
				, '/mre/' => array('c'=>'migworld', 'a'=>'home')
				, '/wap/' => array('c'=>'migworld', 'a'=>'home')
				, '/touch/' => array('c'=>'migworld', 'a'=>'get_touchpage')
				, '/blackberry/' => array('c'=>'migworld', 'a'=>'get_touchpage')
				, '/ios/' => array('c'=>'migworld', 'a'=>'get_touchpage')
				, '/corporate#vas/' => array('c'=>'vas', 'a'=>'agreements')
				, '/corporate#merchant/' => array('c'=>'merchant', 'a'=>'dashboard')
				, '/corporate#buzz/' => array('c'=>'merchant', 'a'=>'dashboard')
				, '/corporate#lookout/' => array('c'=>'merchant', 'a'=>'dashboard')
			);

			foreach($home_list as $pattern => $ca)
			{
				if (preg_match($pattern, get_view() . '#' . get_controller()))
				{
					redirect(get_login_url(get_controller_action_url($ca['c'], $ca['a'])));
				}
			}

			global $migbo_server_root;
			redirect(get_login_url($migbo_server_root));
		}
	}
?>
