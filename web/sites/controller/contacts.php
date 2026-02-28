<?php
	class ContactsController
	{
		public function add_contact_redirect(&$model_data)
		{
			//Ensure the proper inputs are there
			$view = new ControllerMethodReturn();
			$view->method = 'add_number_contact';
			$view->model_data = $model_data;
			return $view;
		}

		public function add_contact_submit(&$model_data) {
			$type = get_attribute_value('type', 'string', '');
			$view = new ControllerMethodReturn();
			if($type == 'mobilenumber') {
				$view->method = 'add_number_contact';
			} else {
				$view->method = 'add_mig33_user';
			}
			$view->model_data = $model_data;
			return $view;
		}

		public function add_contact_group_redirect(&$model_data) {
			$view = new ControllerMethodReturn();
			$view->method = 'add_contact_group';
			$view->model_data = $model_data;
			return $view;
		}
	}
?>