<?php
	fast_require("GprsCountrySetting", get_domain_directory() . "/settings/gprs_country_setting.php");
	fast_require("GprsNetworkSetting", get_domain_directory() . "/settings/gprs_network_setting.php");

	class ApnSetting
	{
		public $country_settings = array();

		public function __construct()
		{
			$gprs_apn_filename = get_resources_directory() . "/gprs_apn.csv";
			$fp = fopen($gprs_apn_filename, "r");
			while (!feof($fp))
			{
		        $buffer = fgets($fp);
		        $this->create_country_setting($buffer);
			}
			fclose($fp);
		}

		/**
		*
		* Get the carriers for a country
		*
		**/
		public function get_network_names_for_country($country_iso_code)
		{
			foreach($this->country_settings as $country_setting)
			{
				if( strtolower($country_iso_code) == strtolower($country_setting->country) )
				{
					return $country_setting->get_network_names();
				}
			}
		}

		/**
		*
		* Get the carrier setting
		*
		**/
		public function get_carrier_settings($country_iso_code, $carrier)
		{
			foreach($this->country_settings as $country_setting)
			{
				if( strtolower($country_iso_code) == strtolower($country_setting->country) )
				{
					return $country_setting->get_network_detail($carrier);
				}
			}
		}

		/**
		*
		* Create the country setting object
		*
		**/
		protected function create_country_setting($data)
		{
			$buffer = trim($data);
			$data = split(",", $buffer);

			$country = $data[0];
			$network = $data[1];
			$apn = $data[2];
			$username = $data[3];
			$password = $data[4];
			$extra = $data[5];

			$network_setting = new GprsNetworkSetting($network, array
																	(
																	"apn"=>$apn,
																	"username"=>$username,
																	"password"=>$password,
																	"extra" => $this->process_extra_data($extra)
																	)
													);

			if( array_key_exists($country, $this->country_settings) )
			{
				$country_setting = $this->country_settings[$country];
			}
			else
			{
				$country_setting = new GprsCountrySetting($country);
			}
			$country_setting->networks[$network_setting->name] = $network_setting;
			$this->country_settings[$country] = $country_setting;
		}

		/**
		*
		* Parse the extra data and return an array of that data
		*
		**/
		protected function process_extra_data($data)
		{
			$return_array = array();
			$strings = split(" ", $data);
			foreach($strings as $string)
			{
				$values = split(":", $string);
				$return_array[strtolower($values[0])][] = $values[1];
			}

			return $return_array;
		}

	}
?>