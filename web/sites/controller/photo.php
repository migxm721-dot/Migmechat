<?php
	class PhotoController extends Controller
	{
		public function photo_like(&$model_data)
		{
			$from = get_attribute_value('from', 'string', 'view_photo');

			$view = new ControllerMethodReturn();
			$view->model_data = $model_data;
			if($from == 'home')
				$view->method = 'home';
			else
				$view->method = "view_photo";
			return $view;
		}
	}
?>