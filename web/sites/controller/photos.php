<?php
	require_once(get_framework_common_directory() . "/web_utilities.php");

	class PhotosController
	{
		public function show_received_confirmed(&$model_data)
		{
			$do = get_value("do");
			$photo_file_id = get_value("nid");
			$sender = get_value("sender");
			$session_user = get_value_from_array("session_user", $model_data);

			$model_data["accepted"] = ($do=="accept");

			if( $do == "accept" )
			{
				$call_return = make_soap_call("saveExistingFileToScrapbooks", array($sender, array($session_user), $photo_file_id, ""));
				$model_data["call_return"] = $call_return;
			}
			else
			{
			}
		}

		public function photo_like(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->model_data = $model_data;
			$view->method = "home";
			return $view;
		}
	}
?>