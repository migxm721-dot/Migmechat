<?php
	class IMController
	{
		/**
		* Set data
		*
		*/
		public function setup_im(&$model_data)
		{
			$im = get_value("im", "integer", 2);
			$model_data["im"] = $im;
		}

		/**
		* Set data
		*
		*/
		public function home(&$model_data)
		{
			$from = get_attribute_value("from");
			$model_data["from"] = $from;
		}

		public function setup_im_post(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$im = get_value("im", "integer", 0);
			$id = get_value("id");
			$password = get_attribute_value("password");

			$result = make_soap_call("addIMDetail", array($session_user, $im, $id, $password));
			$model_data["im"] = $im;
			$model_data["from"] = get_attribute_value("from");

			//Check if there is an error
			if($result->is_error())
			{
				//There is an error
				$view = new ControllerMethodReturn();
				$view->method = "setup_im";
				$model_data['im_error'] = $result->message;
				$view->model_data = $model_data;
				$model_data['id'] = $id;
				$model_data['password'] = $password;
				return $view;
			}
			else
			{
				$model_data["call_result"] = $result;
			}
		}

		public function setup_im_remove(&$model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$im = get_value("im", "integer", 0);
			$id = '';
			$password = '';

			$result = make_soap_call("addIMDetail", array($session_user, $im, $id, $password));
			$model_data["im"] = $im;
			$model_data["from"] = get_attribute_value("from");

			$model_data["call_result"] = $result;
			$model_data['setup_im'] = 'remove';
		}
	}
?>