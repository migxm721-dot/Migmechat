<?php
	//fast_require("Cache", get_library_directory() . "/cache/cache.php");
	fast_require("Image", get_library_directory() . "/image/image.php");

	class AvatarController
	{
		public function level_check(&$model_data)
		{
		}

		public function home(&$model_data)
		{
			$user_avatar_created = get_value_from_array("user_avatar_created", $model_data, "boolean", false);
			if( !$user_avatar_created )
			{
				$view = new ControllerMethodReturn();
				$view->method = "create";
				return $view;
			}

			$categories = $model_data["categories"];
			$method = "equippable";

			if(empty($categories))
			{
				$view = new ControllerMethodReturn();
				$view->method = $method;
				return $view;
			}
		}

		public function create($model_data)
		{

		}

		public function avatar_rate(&$model_data) {
			$from = get_attribute_value("from", "string");
			$view = new ControllerMethodReturn();
			$view->model_data = $model_data;
			if($from == 'home')
			{
				$view->method = "home";
			}
			elseif($from == 'view')
			{
				$view->method = "view";
			}
			elseif($from == 'comment')
			{
				$view->method = "comment";
			}
			return $view;
		}
	}
?>