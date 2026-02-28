<?php

fast_require('Rest', get_library_directory() . '/rest/rest.php');

/**
 * Rest Result class - to be consumed by Rest Response
 * @author chernjie
 * @since tags/REL_7.78
 *
 */
class RestResult
{
	const CACHE_NO_CACHE      = 0;
	const CACHE_OMIT_CACHE    = -1;

	private $http_status_code = 200;
	private $http_status_msg  = '';
	private $http_headers     = null;
	private $app_status_code  = 0;
	private $app_status_msg   = '';
	private $cache_in_seconds = 0;
	private $data             = null;
	private $debug            = null;
	private $logger           = null;

	public function __construct($data = null, $http_status_code = 200, $app_status_code = 0, $app_status_msg = '', $http_status_msg = '')
	{
		$this->set_data($data);
		$this->set_http_status($http_status_code, $http_status_msg);
		$this->set_app_status($app_status_code, $app_status_msg);
		$this->set_cache(self::CACHE_NO_CACHE);

		$this->http_headers = array();

		return $this;
	}

	public function set_data($data)
	{
		$this->data = $data;
		return $this;
	}

	public function get_data()
	{
		return $this->data;
	}

	public function set_cache($cache_time)
	{
		$cache_time = (int) $cache_time;

		if (self::CACHE_NO_CACHE === $cache_time || self::CACHE_OMIT_CACHE === $cache_time || 0 < $cache_time)
		{
			$this->cache_in_seconds = $cache_time;
			return $this;
		}

		throw new Exception("Invalid cache time provided");
	}

	public function get_cache()
	{
		return $this->cache_in_seconds;
	}

	public function add_http_header($header, $value)
	{
		$this->http_headers[$header] = $value;
		return $this;
	}

	public function has_http_header($header)
	{
		return isset($this->http_headers[$header]);
	}

	public function get_http_header($header)
	{
		return $this->http_headers[$header];
	}

	public function get_http_headers()
	{
		return $this->http_headers;
	}

	public function set_http_status($http_status_code = 200, $http_status_msg = '')
	{
		if (!isset(Rest::$response_codes[$http_status_code]))
		{
			throw new Exception('Invalid http response code');
		}

		$this->http_status_code = $http_status_code;
		$this->http_status_msg = $http_status_msg;
		return $this;
	}

	public function get_http_status_code()
	{
		return $this->http_status_code;
	}

	public function get_http_status_msg()
	{
		return $this->http_status_msg;
	}

	public function set_app_status($app_status_code = 0, $app_status_msg = '')
	{
		$this->app_status_code = $app_status_code;
		$this->app_status_msg = $app_status_msg;
		return $this;
	}

	public function get_app_status_code()
	{
		return $this->app_status_code;
	}

	public function get_app_status_msg()
	{
		return $this->app_status_msg;
	}

	public function set_debug($debug)
	{
		$this->debug = $debug;
		return $this;
	}

	public function get_debug()
	{
		return $this->debug;
	}

	public function set_logger($logger)
	{
		$this->logger = $logger;
		return $this;
	}

	public function get_logger()
	{
		return $this->logger;
	}

	public function is_empty()
	{
		return ! $this->get_data()
			&& ! $this->get_app_status_code()
			&& ! $this->get_app_status_msg()
			&& ! $this->get_debug();
	}
}

?>