<?php
	class NewUserController
	{
		public function setup_im_post(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$im = get_value("im", "integer", 0);

			$id = get_value("id");
			$password = get_attribute_value("password");

			$result = make_soap_call("addIMDetail", array($session_user, $im, $id, $password));
			$model_data["call_result"] = $result;
			$model_data["im"] = $im;
		}

		public function choose_avatar(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = "choose_avatar_done";
			$view->model_data = $model_data;
			return $view;
		}
	}
?>