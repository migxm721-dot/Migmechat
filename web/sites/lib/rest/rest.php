<?php
	class Rest
	{
		public static $response_codes = array(
							             100 => 'Continue',
							             101 => 'Switching Protocols',
							             200 => 'OK',
							             201 => 'Created',
							             202 => 'Accepted',
							             203 => 'Non-Authoritative Information',
							             204 => 'No Content',
							             205 => 'Reset Content',
							             206 => 'Partial Content',
							             300 => 'Multiple Choices',
							             301 => 'Moved Permanently',
							             302 => 'Found',
							             303 => 'See Other',
							             304 => 'Not Modified',
							             305 => 'Use Proxy',
							             306 => '(Unused)',
							             307 => 'Temporary Redirect',
							             400 => 'Bad Request',
							             401 => 'Unauthorized',
							             402 => 'Payment Required',
							             403 => 'Forbidden',
							             404 => 'Not Found',
							             405 => 'Method Not Allowed',
							             406 => 'Not Acceptable',
							             407 => 'Proxy Authentication Required',
							             408 => 'Request Timeout',
							             409 => 'Conflict',
							             410 => 'Gone',
							             411 => 'Length Required',
							             412 => 'Precondition Failed',
							             413 => 'Request Entity Too Large',
							             414 => 'Request-URI Too Long',
							             415 => 'Unsupported Media Type',
							             416 => 'Requested Range Not Satisfiable',
							             417 => 'Expectation Failed',
							             500 => 'Internal Server Error',
							             501 => 'Not Implemented',
							             502 => 'Bad Gateway',
							             503 => 'Service Unavailable',
							             504 => 'Gateway Timeout',
								         505 => 'HTTP Version Not Supported'
										);


		/**
		*
		* Check if the request method is allowed
		*
		**/
		public static function check_request_method($method_array)
		{
			$request_method = strtolower($_SERVER["REQUEST_METHOD"]);

			foreach($method_array as $method)
			{
				if(strtolower($method) == $request_method) return true;
			}
			return false;
		}

		/**
		*
		* Get the status code string
		*
		**/
		public static function get_status_string($status_code)
		{
			return (isset(self::$response_codes[$status_code]) ? self::$response_codes[$status_code] : "");
		}

		/**
		*
		* Get the body of the request
		*
		**/
		public static function get_payload(){

			$payload = null;

			$fp = fopen('php://input', 'r');

			while( !feof($fp) )
				$payload = fgets($fp);

			fclose( $fp );

			return $payload;

		}

		/**
		*
		* Get the header of the request
		* returns an array of the headers
		**/
		public static function get_headers(){
			return apache_request_headers();
		}

		public static function get_server_vars(){
			return $_SERVER;
		}

	}
?>
