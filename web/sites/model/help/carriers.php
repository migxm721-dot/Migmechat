<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");
	fast_require("ApnSetting", get_library_directory() . "/apn/apn_setting.php");
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');

	class CarriersModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$apn_setting = new ApnSetting();

			$country_id = get_value("country", "integer");

			$country_data = CountryDAO::get_country_data($country_id);
			$network_names = $apn_setting->get_network_names_for_country(get_value_from_array("isoCountryCode", $country_data));

			$data = array();
			$data["country"] = get_value_from_array("name", $country_data);
			$data["country_id"] = get_value_from_array("id", $country_data, "integer", 0);
			$data["country_iso_code"] = get_value_from_array("isoCountryCode", $country_data);
			$data["country_settings"] = $apn_setting->country_settings;
			$data["network_names"] = $network_names;
			return $data;
		}
	}
?>