<?php
	fast_require('SystemProperty', get_library_directory().'/system/system_property.php');
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');	

	class MigworldController
	{
		public function new_migworld(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'home';
			$view->model_data = $model_data;
			return $view;
		}

		public function redirect_discover(&$model_data)
		{
			if (get_view() == 'midlet' || get_view() == 'wap') {
				global $migworld_url;
				header('Location: '.$migworld_url.'?platform='.get_view());
				die();
			}
		}

		public function home(&$model_data)
		{
			if (is_midlet_view())
			{
				$notification_url = SystemProperty::get_instance()->get_string(SystemProperty::NotificationsURL, $GLOBALS['migcore_migbo_server_root']."/%1/migalert");
				$notification_url = str_replace("%1", "midlet", $notification_url);
				$model_data["notification_url"] = $notification_url;

				$ice = new IceDAO();
				$model_data["is_sysprop_chatsync_enabled"] = $ice->is_sysprop_chatsync_enabled($model_data['session_user'], $model_data['session_user_id']);
			}
		}
	}
?>