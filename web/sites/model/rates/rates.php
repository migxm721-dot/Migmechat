<?php
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	class RatesModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user_detail = isset($model_data['session_user_detail'])
				? $model_data['session_user_detail']
				: null;

			if (empty($session_user_detail))
			{
				$country_data = CountryDAO::get_country_from_ip();
				return array(
					"country_data" => $country_data['name']
					, "country_id" => $country_data['id']
				);
			}
			else
			{
				return array(
					"country_data" => $session_user_detail->get_country_name()
					, "country_id" => $session_user_detail->countryID
				);
			}
		}
	}
?>