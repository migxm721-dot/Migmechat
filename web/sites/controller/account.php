<?php
	class AccountController
	{
		public function form_submit(&$model_data)
		{
			if( $model_data['success'] == false)
			{
				$view = new ControllerMethodReturn();
				$view->method = "bank_deposit_form";
				$view->model_data = $model_data;
				return $view;
			}
		}
	}
?>