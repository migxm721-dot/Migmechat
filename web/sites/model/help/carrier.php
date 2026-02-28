<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");
	fast_require("ApnSetting", get_library_directory() . "/apn/apn_setting.php");
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');

	class CarrierModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$country_id = get_value("country", "integer");
			$carrier = get_value("carrier");

			$country_data = CountryDAO::get_country_data($country_id);

			$apn_setting = new ApnSetting();
			$carrier_detail = $apn_setting->get_carrier_settings(get_value_from_array("isoCountryCode", $country_data), $carrier);

			$data = array();
			$data["carrier_detail"] = $carrier_detail;
			$data["country_id"] = $country_id;
			return $data;
		}
	}
?>