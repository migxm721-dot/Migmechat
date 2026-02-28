<?php
	fast_require("SmsDAO", get_dao_directory() . "/sms_dao.php");
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");

	class SendSMSModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$remove_chars = array('+', '-', ' ', '(', ')', '[' ,']');
			$body = get_attribute_value("body");
			$body = utf8_encode($body);
			$to_number = str_replace($remove_chars, '', get_value("to_number"));
			$submit_type = get_value('submit_type');

			try
			{
				if (strcasecmp($submit_type,'send') == 0)
				{
					$this->make_soap_call('sendSMS', array($model_data['session_user'], $model_data['session_user_detail']->mobilePhone_original, $to_number, $body, getRemoteIPAddress(), getSessionID(), getMobileDevice(), getUserAgent()) );
					$model_data['sms_success'] = true;

					//Clear memcache list of source and destination number for that user.
					$memcache = Memcached::get_instance();
					$memcache->remove_item(SmsDAO::$MEM_HEADER_SMS_DESTINATION_USERNAME.$model_data['session_user']);
				}
				elseif (strcasecmp($submit_type,'cost') == 0)
				{
					$result = $this->make_soap_call('getSMSCost', array($model_data['session_user'], $to_number));
					if (isset($result->data))
					{
						$model_data['sms_cost'] = $result->data;
					}
					else
					{
						$model_data['sms_error'] = $result->message;
					}
				}
			}
			catch(Exception $e)
			{
				$model_data['sms_error'] = $e->getMessage();
			}

			return $model_data;
		}
	}
?>