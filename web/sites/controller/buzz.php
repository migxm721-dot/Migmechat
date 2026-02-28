<?php
	class BuzzController extends Controller
	{
		public function settings(&$model_data)
		{
			if (is_touch_view() || is_blackberry_view() || is_ios_view())
			{
				$view = new ControllerMethodReturn();
				$view->controller = 'settings';
				$view->method = 'services';
				$view->model_data = $model_data;
				return $view;
			}
		}
	}
?>