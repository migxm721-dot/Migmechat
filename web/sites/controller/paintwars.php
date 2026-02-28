<?php
	require_once(get_framework_common_directory() . '/pagelet_utilities.php');

	class PaintwarsController
	{
		protected $response;

		public function buy_identicon(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'home');

			$view = new ControllerMethodReturn();
			if($from == 'home')
				$view->method = 'home';
			elseif($from == 'store')
				$view->method = 'store';
			else
				$view->method = 'home';

			$view->model_data = $model_data;
			return $view;
		}

		public function buy_item(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'store';
			$view->model_data = $model_data;
			return $view;
		}

		public function use_item(&$model_data)
		{
			$view = new ControllerMethodReturn();
			$view->method = 'store';
			$view->model_data = $model_data;
			return $view;
		}

	}
?>