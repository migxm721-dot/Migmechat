<?php
	class SMSController
	{
		public function send_sms_redirect(&$model_data)
		{
			if(!empty($model_data['sms_error']))
			{
				$view = new ControllerMethodReturn();
				$view->method = "home";
				$view->model_data = $model_data;
				return $view;
			}
		}

	}
?>