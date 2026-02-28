<?php
	fast_require('RestResult', get_library_directory() . '/rest/rest_result.php');
	class SettingsController
	{
		public function show_search_block_list(&$model_data)
		{
		}

		public function show_block_list(&$model_data)
		{
		}

		public function chat_privacy(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);

			$result = make_soap_call("getMessageSetting", array($session_user));
			$model_data["setting"] = $result->data;
		}

		public function chat_privacy_post(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$setting = get_value("chat_privacy", "integer", 1);

			$call_result = make_soap_call("updateMessageSetting", array($session_user, $setting));
			$model_data["call_result"] = $call_result;

			$result = make_soap_call("getMessageSetting", array($session_user));
			$model_data["setting"] = $result->data;
		}

		public function account_profile(&$model_data)
		{
			if (! get_value('submit')) return null;
//			if(isset($model_data['error']) || isset($model_data['errors'])) return null;

			try
			{
				if(!$model_data['account_profile']->save_settings())
				{
					throw new Exception(_('Oops, the update has failed. Please try again.'));
				}

				SessionUtilities::set_session_in_cache($model_data['session_user']);
				$model_data['successes'][] = _('Congrats! The update was successful.');

				if (get_action() == 'account_email_submit')
				{
					$model_data['successes'] = array();
					$model_data['successes'][] = empty($model_data['account_profile']->externalEmail)
						? _('Your email has successfully been deleted.')
						: sprintf(
							_('Thank you! Please verify your email address by clicking on the link we\'ve sent to your inbox. <a href="%s">Click here to have us send it again.</a>')
							, get_action_url('account_email_resend')
						);
					return new ControllerMethodReturn('account_contact', $model_data);
				}
				if (!empty($model_data['return_url']))
					redirect ($model_data['return_url']);
			}
			catch (Exception $e)
			{
				$model_data['errors'][] = $e->getMessage();
			}
		}

		public function account_mobile_submit(&$model_data)
		{
			if (! empty($model_data['success']))
			{
				if (is_ajax_view()) set_view(View::MIG33_CORPORATE);
				$redirect = new ControllerMethodReturn('auth_inapp', $model_data);
				$redirect->controller = 'registration';
				return $redirect;
			}
		}

		public function account_email_resend(&$model_data)
		{
			try
			{
				$model_data['response'] = FusionRest::get_instance()->get(
					sprintf(
						FusionRest::KEYSPACE_SETTINGS_EMAIL_RESEND
						, $model_data['session_user_id']
					)
				);
				if (empty($model_data['response']) || FusionRest::OK != $model_data['response'])
					throw new Exception(sprintf(_('An error has occurred, <a href="%s">please try again</a>!'), $_SERVER['REQUEST_URI']));

				$model_data['successes'][] = sprintf(
					_('Thank you! Please verify your email address by clicking on the link we\'ve sent to your inbox. <a href="%s">Click here to have us send it again.</a>')
					, get_action_url('account_email_resend')
				);
			}
			catch (Exception $e)
			{
				$model_data['errors'][] = $e->getMessage();
			}
		}

		public function account_email_resend_noauth(&$model_data)
		{
			try
			{
				fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');
				$model_data['response'] = FusionRest::get_instance()->get(
					sprintf(
						FusionRest::KEYSPACE_SETTINGS_EMAIL_RESEND
						, $model_data['session_user_id']
					)
				);
				if (empty($model_data['response']) || FusionRest::OK != $model_data['response'])
					throw new Exception(sprintf(_('An error has occurred, <a href="%s">please try again</a>!'), $_SERVER['REQUEST_URI']));

				$model_data['successes'][] = _('Thank you! Please verify your email address by clicking on the link we\'ve sent to your inbox.');
			}
			catch (Exception $e)
			{
				$model_data['errors'][] = $e->getMessage();
			}
		}

		public function account_email_verify(&$model_data)
		{
			// Redirect to settings contact page
			// If email verification is successful
			// and if session_user is the same as email verified
			if (   ! empty($model_data['successes'])
				&& ! empty($model_data['session_user'])
				&& ! empty($model_data['username'])
				&& $model_data['session_user'] == $model_data['username']
			)
			{
				return new ControllerMethodReturn('account_contact', $model_data);
			}
		}

		public function account_personalized_url_route(&$model_data)
		{
			$view = new ControllerMethodReturn();

			if (isset($_POST['personalized_url_check']))
			{
				$rest_response = $this->account_personalized_url_check($model_data);
				$model_data['validity_check'] = array(
													  'status_code' => $rest_response->get_app_status_code()
													, 'status_msg' => $rest_response->get_app_status_msg()
													, 'response_data' =>  $rest_response->get_data()
													  );
				$view->method = "account_personalized_url";
			}

			elseif (isset($_POST['submit']))
			{
				$view->method = "account_personalized_url_submit";
			}

			$view->model_data = $model_data;
			return $view;
		}

		public function account_personalized_url(&$model_data)
		{
			fast_require('UserDAO', get_dao_directory() . '/user_dao.php');
			$username = get_attribute_value('username');
			$dao = new UserDAO();
			try
			{
				if (!$dao->is_valid_alias($model_data['session_user_id'], $username))
				{
					throw new Exception(_('Invalid alias'));
				}
				$dao->set_user_alias($model_data['session_user_id'], $username);
			}
			catch (Exception $e)
			{
				return new ControllerMethodReturn('account_personalized_url', array('error'=>$e->getMessage()));
			}
			$model_data['session_user_alias'] = $username;
//			$model_data['username_is_valid_alias'] = $dao->is_valid_alias($model_data['session_user_id'], $model_data['session_user']);
			$model_data['successes'] = array(_('Congratulations! You have created your personalized URL!'));
		}

		public function account_personalized_url_check(&$model_data)
		{
			fast_require('UserDAO', get_dao_directory() . '/user_dao.php');
			fast_require('RegistrationDAO', get_dao_directory() . '/registration_dao.php');
			$username = get_attribute_value('username', 'string', '');
			$dao = new UserDAO();
			$registration_dao = new RegistrationDAO();
			if (empty($username))
			{
				return new RestResult(array(), 200, 1, _('Please provide an alias'));
			}
			else if (!$dao->is_valid_alias($model_data['session_user_id'], $username))
			{
				return new RestResult(array(), 200, 2, _('Your address should start with a letter and be 6-20 characters long. You may include letters, numbers, dashes (-), underscores (_) and dots (.).'));
			}
			else if ($model_data['session_user'] != $username && !$dao->is_valid_alias_via_rest($model_data['session_user_id'], $username))
			{
				$data = array('suggested_usernames' => $registration_dao->suggest_usernames($username, 3));
				return new RestResult($data, 200, 3, _('Sorry, that address is not available. Please try another, or select one from below:'));
			}
			else
			{
				return new RestResult(array('is_valid_alias'=>true));
			}
		}

		public function account_communication(&$model_data)
		{
			if(!get_value('submit') || empty($model_data['account_communication'])) return null;
			if(isset($model_data['error']) || isset($model_data['errors'])) return null;

			try
			{
				if(!$model_data['account_communication']->save_settings())
				{
					throw new Exception(_('Oops, the update has failed. Please try again.'));
				}

				$model_data['successes'][] = _('Congrats! The update was successful.');
			}
			catch (Exception $e)
			{
				$model_data['errors'][] = $e->getMessage();
			}
		}

		public function account_picture(&$model_data)
		{
			if(!get_value('submit') || empty($model_data['account_picture'])) return null;
			if(isset($model_data['error']) || isset($model_data['errors'])) return null;

			try
			{
				if(!$model_data['account_picture']->save_settings())
				{
					throw new Exception(_('Oops, the update has failed. Please try again.'));
				}

//				$model_data['successes'][] = _('Congrats! The update was successful.');
				return new RestResult(array(
					'displayPictureURL'=>$model_data['displayPictureURL']
					, 'message'=>_('Congrats! The update was successful.')
				));
			}
			catch (Exception $e)
			{
//				$model_data['errors'][] = $e->getMessage();
				return new RestResult(null, 200, 1, $e->getMessage());
			}
		}

		public function updates_posts(&$model_data)
		{
			if(!get_value('submit') || empty($model_data['updates_posts'])) return null;
			if(isset($model_data['error']) || isset($model_data['errors'])) return null;

			try
			{
				if(!$model_data['updates_posts']->save_settings())
				{
					throw new Exception(_('Oops, the update has failed. Please try again.'));
				}

				$model_data['successes'][] = _('Congrats! The update was successful.');
			}
			catch (Exception $e)
			{
				$model_data['errors'][] = $e->getMessage();
			}
		}

		public function account_thirdpartysites_auth_redirect(&$model_data)
		{
			if(isset($model_data['error']) || isset($model_data['errors'])) return null;
			if(isset($model_data['url']))
				redirect($model_data['url']);
			else
				$model_data['errors'][] = _("Unable to construct the redirect URL. Please try again later.");
		}

		public function nue_update_avatar_submit(&$model_data)
		{
			if (empty($model_data['errors']))
			{
				if (is_midlet_view() || is_mre_view())
				{
					redirect ($GLOBALS['migcore_migbo_server_root'].'/'.get_view().'/nue/skip/profile_picture');
				}
				elseif(is_wap_view())
				{
					redirect ($GLOBALS['migbo_server_root'].'/wap/nue/skip/profile_picture');
				}
				else
				{
					redirect ($GLOBALS['migbo_server_root'].'/nue/skip/profile_picture');
				}
			}
		}

		public function new_settings(&$model_data)
		{
			$methods = array(
				  'change_mobile'          => 'account_mobile'
				, 'change_mobile_submit'   => 'account_mobile_submit'
				, 'change_password'        => 'account_password'
				, 'change_password_submit' => 'account_password_submit'
			);
			if (! is_ajax_view() && ! is_touch_view() && ! is_ios_view() && ! is_blackberry_view() && array_key_exists(get_action(), $methods))
			{
				return new ControllerMethodReturn($methods[get_action()], $model_data);
			}
		}

		public function security_question_submit(&$model_data)
		{
			if (get_attribute_value('from') == 'contact')
			{
				if (get_value('new_mobile'))
				{
					return new ControllerMethodReturn('account_mobile_submit', $model_data);
				}
				else if (get_value('externalEmail'))
				{
					return new ControllerMethodReturn('account_email_submit', $model_data);
				}
				else
				{
					return new ControllerMethodReturn('account_contact', $model_data);
				}
			}
		}

		public function redirect_connect_apps(&$model_data)
		{
			return new ControllerMethodReturn('connect_apps', $model_data);
		}
	}
?>