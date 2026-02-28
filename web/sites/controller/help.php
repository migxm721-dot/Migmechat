<?php
	class HelpController
	{
		// redirect
		public function __construct() {
			global $corporate_url;
			switch (get_view()) {
				case 'wap':
					$help_view = 'wap';
					break;
				case 'midlet':
					$help_view = 'j2me';
					break;
				case 'touch':
					$help_view = 'android';
					break;
				default:
					$help_view = 'web';
					break;
			}
			
			header('Location: '.$corporate_url.'/faq/'.$help_view);
			die();
		}

		public function show_apn_carrier_detail(&$model_data)
		{
			global $mobile_midp;

			$vendor = get_value("vendor");
			$model = get_value("model");

			$model_data["title"] = "GPRS APN Setting";
			$model_data["vendor"] = $vendor;
			$model_data["model"] = $model;

			if($vendor != 'Other' && $model != 'Other')
			{
				retrieve_midp($vendor, $model);
			}
			else
			{
				$useragent = $_SERVER['HTTP_USER_AGENT'];
				$useragent = strtolower($useragent);
				detect_midp($useragent);
			}

			$model_data["midp"] = $mobile_midp;
		}

		public function show_phone_select_model(&$model_data)
		{
			$model_data["title"] = _('select phone') . " " . _('Model');
		}

		public function show_phone_select_vendor(&$model_data)
		{
			$model_data["title"] = _('select phone') . " " . _('Vendor');
		}

		public function show_apn(&$model_data)
		{
			$vendor = get_value_from_array("vendor", $model_data);
			$model = get_value_from_array("model", $model_data);

			if( empty($vendor) )
			{
				$view = new ControllerMethodReturn();
				$view->method = "phone_select_vendor";
				return $view;
			}
			else if( empty($model) )
			{
				$view = new ControllerMethodReturn();
				$view->method = "phone_select_model";
				return $view;
			}
			$model_data["title"] = "Connection Errors and Instructions";
		}

		public function show_apn_select_carrier(&$model_data)
		{
			$network_names = $model_data["network_names"];
			if( empty($network_names) )
			{
				$view = new ControllerMethodReturn();
				$view->method = "apn_no_carrier";
				$data = array();
				$data["country"] = get_value_from_array("country", $model_data);
				$data["title"] = "APN Settings";
				$view->model_data = $data;
				return $view;
			}
		}
	}
?>