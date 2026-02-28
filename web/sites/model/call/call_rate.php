<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");

	class CallRateModel extends Model
	{
		public function get_data($model_data)
		{

			$sourceCountryId = $model_data['sourceCountryId'];
			$destinationCountryId = $model_data['destinationCountryId'];
			$currency = $model_data['currency'];

			try {
				settype($sourceCountryId, 'int');
				settype($destinationCountryId, 'int');
				$callRates = soap_call_ejb('getCallRates', array($sourceCountryId, $destinationCountryId, true, $currency));
				$model_data['landline_call_rates'] = $callRates;

				$callRates = soap_call_ejb('getCallRates', array($sourceCountryId, $destinationCountryId, false, $currency));
				$model_data['mobile_call_rates'] = $callRates;
			} catch(Exception $e) {
			   	$model_data['errors'] = array('rate_error'=>$e->getMessage());
			}
			return $model_data;
		}
	}
?>