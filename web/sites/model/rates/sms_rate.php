<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");

	class SmsRateModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$session_user_detail = $model_data['session_user_detail'];

			$costs = $this->make_soap_call("getLocalSMSCost", array($session_user, $session_user_detail->countryID));

			$data = array();
			$data['local_sms_cost'] = $costs->data;
			return $data;
		}
	}
?>