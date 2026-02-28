<?php
	class EmailController extends Controller
	{
		public function settings_submit(&$model_data)
		{
			$view = new ControllerMethodReturn();
			if (is_touch_view() || is_blackberry_view() || is_ios_view())
			{
				$view->controller = 'settings';
				$view->method = 'services';
			}
			else
			{
				$view->method = 'settings';
			}
			$view->model_data = $model_data;
			return $view;
		}
	}
?>