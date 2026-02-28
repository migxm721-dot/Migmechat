<?php
	fast_require('RestResult', get_library_directory() . "/rest/rest_result.php");
	fast_require("Logger", get_framework_common_directory()."/logger.php");
	
	class CrAuthController
	{
		public function get_app_details($data)
		{
			return $this->send_result($data);
		}
		
		public function get_login_challenge($data)
		{
			return $this->send_result($data);
		}
		
		public function authenticate($data)
		{
			return $this->send_result($data);
		}
		
		private function send_result($data)
		{
			if (isset($data['error']))
			{
				return new RestResult(array()
					, 200
					, $data['error']['code']
					, $data['error']['message']
					, $data['error']['message']
				);
			}
			else 
			{
				$rest_result = new RestResult($data['result']);
				$rest_result->set_logger(Logger::getLogger('cr_auth_access'));
				return $rest_result;
			}
		}
	}//class CrAuthController

?>