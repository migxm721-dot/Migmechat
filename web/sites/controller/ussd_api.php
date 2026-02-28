<?php
	fast_require("Rest", get_library_directory()."/rest/rest.php");
	fast_require("RestJsonResponse", get_library_directory()."/rest/rest_json_response.php");
	fast_require("RestResult", get_library_directory()."/rest/rest_result.php");
	fast_require("RestXmlResponse", get_library_directory()."/rest/rest_xml_response.php");
	fast_require("Logger", get_framework_common_directory()."/logger.php");
	fast_require("APIException", get_domain_directory()."/api/api_exception.php");
	fast_require("SystemProperty", get_library_directory."/system/system_property");
	fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');
	fast_require('Logger', get_framework_common_directory(). '/logger.php');

	// TODO: create a third party wrapper for numedia schema

	class USSDApiController
	{
		private function _log_source()
		{
			$logger = Logger::getLogger("ussd_api");
			$message = $_SERVER['REQUEST_METHOD'].' '.$_SERVER['SCRIPT_URI'].'?'.$_SERVER['QUERY_STRING'].' - PAYLOAD: '.Rest::get_payload();
			$logger->log($message);
		}

		public function send_error($model_data, $args)
		{
			$data = array();
			$error = new APIException( array_key_exists('error', $model_data) ? $model_data['error'] : 0);
			$data['result_code'] = $error->err_no;
			$data['message'] = $model_data['message'];

			return $this->_send_response($data, $error);
		}

		public function register($model_data, $args)
		{
			$data = array();
			$error = new APIException( array_key_exists('error', $model_data) ? $model_data['error'] : 0, $model_data['error_message']);

			$data['result_code'] = $error->err_no;
			if ($error->has_no_error() && !isset($model_data['partner_user']))
			{
				$error = new APIException(APIException::USER_NOT_CREATED);
			}
			elseif(isset($model_data['partner_user']) && $model_data['partner_user'] instanceof USSDPartnerUser)
			{
				$partner_user = $model_data['partner_user'];
				$data = $partner_user->to_array();
			}

			return $this->_send_response($data, $error);

		}

		public function user($model_data, $args)
		{
			$data = array();
			$error = new APIException( array_key_exists('error', $model_data) ? $model_data['error'] : 0);

			if($error->has_no_error() && (! isset($model_data['user']) || empty($model_data['user'])))
			{
				$error = new APIException(APIException::USER_DOES_NOT_EXIST);
			}
			else
			{
				$data['user'] = $model_data['user']->to_array();
			}

			return $this->_send_response($data, $error);
		}

		public function credit_transaction($model_data, $args)
		{
			$data = array();
			$error = new APIException( array_key_exists('error', $model_data) ? $model_data['error'] : 0, $model_data['error_message']);
			$system_property = SystemProperty::get_instance();

			$data['result_code'] = $error->err_no;
			if ($error->has_no_error())
			{
				$data['trans_id'] = $model_data['trans_id'];
			}
			return $this->_send_response($data, $error);
		}

		public function balance($model_data, $args)
		{
			$data = array();
			$error = new APIException( array_key_exists('error', $model_data) ? $model_data['error'] : 0, $model_data['error_message']);
			$system_property = SystemProperty::get_instance();

			$data['result_code'] = $error->err_no;
			if ($error->has_no_error())
			{
				$data['balance'] = $model_data['balance'];
				$data['currency'] = $model_data['currency'];
			}
			return $this->_send_response($data, $error);
		}

		public function ping($model_data, $args)
		{
			$data = array();
			$error_array = array(   'error' => 0
								 ,  'error_message' => '');

			try{
				$result = FusionRest::get_instance()->get(FusionRest::KEYSPACE_PING);
				$data = $result;
			} catch (Mig33apiHttpException $e) {
				$error_array['error_message'] = $e->getMessage();
				$error_array['error'] = APIException::GENERIC_ERROR;
			} catch (Mig33apiException $e) {
				$error_array['error_message'] = $e->getMessage();
				$error_array['error'] = APIException::GENERIC_ERROR;
			}

			$error = new APIException( $error_array['error'], $error_array['error_message']);
			$this->_send_response($data, $error);
		}

		protected function _send_response($data, APIException $error)
		{
			$this->_log_source();
			$rest_result = new RestResult($data, $error->response_code, $error->err_no, $error->err);
			$rest_result->set_logger(Logger::getLogger("ussd_api"));
			if(0 == strcasecmp(get_api_output(), 'xml'))
			{
				$response = new RestXmlResponse($rest_result);
			}
			else
			{
				$response = new RestJsonResponse($rest_result);
			}
			return $response->send_response();
		}

	}
?>