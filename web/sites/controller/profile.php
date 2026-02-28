<?php
	require_once(get_framework_common_directory() . '/pagelet_utilities.php');
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");


	class ProfileController
	{
		public function show_gift_received(&$model_data) {
			$view = new ControllerMethodReturn();
			$view->method = 'gifts_received';
			return $view;
		}

		public function edit_profile(&$model_data)
		{
			
			return new ControllerMethodReturn('settings/account_profile');
			
		}

		public function update_profile(&$model_data) {
			$view = new ControllerMethodReturn();
			if(is_touch_view() || is_ajax_view() || is_blackberry_view() || is_ios_view())
			{
				if($model_data['update_successful'])
				{
					$view->method = 'about_me';
				}
				else
				{
					$view->method = 'edit';
				}
				$view->model_data = $model_data;
			}
			else
			{
				$view->method = 'edit';
				$view->model_data = $model_data;
			}
			return $view;
		}

		public function paint(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}

		public function clean(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}

		public function home_router(&$model_data)
		{
			$action = 'home_migcore';

			if (
				((is_midlet_view() || is_mre_view() || is_wap_view())
				&& (! SystemProperty::get_instance()->get_integer(SystemProperty::Migbo_MigboDisabled, false)))
			)
				$action = 'home_migbo';

			if (
				ClientInfo::is_j2me_client() 
				&& ! UserCapability::is_user_allowed($model_data['session_user_id'], UserCapability::GATING_MIGBO_ACCESS)
			)
				$action = 'home_migcore';

			return new ControllerMethodReturn($action, $model_data);
		}

		public function home_migbo(&$model_data)
		{
			exit($model_data['html']);
		}
	}
?>