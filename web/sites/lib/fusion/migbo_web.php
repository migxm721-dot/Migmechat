<?php
fast_require('Mig33api', PACKAGESPATH . 'mig33/libraries/Mig33api.php');
fast_require('APIException', get_domain_directory() . '/api/api_exception.php');

/**
 * migbo Data Service
 *
 * @author chernjie
 * @link https://trac.projectgoth.com/Mig33/wiki/projects/migBo/tech/api/migBo/DataService
 * @global string $migbo_datasvc_api_url
 */
class MigboWeb extends Mig33api
{
	const OK = 'ok';

	const USER_PROFILE = '/u/%s';
	protected $_response_type = 'RAW'; // raw for raw output

	protected function execute($ch_array)
	{
		if (SystemProperty::get_instance()->get_boolean(SystemProperty::Migbo_MigboDisabled, false))
			throw new Mig33apiException(
				  _('Mini blog data service is temporarily disabled')
				, APIException::SERVICE_DISABLED
			);

		return parent::execute($ch_array);
	}

	public function __construct()
	{
		$this->api_url = (empty($GLOBALS['migbo_web_api']['url']) ? 'http://localhost/b/' : $GLOBALS['migbo_web_api']['url']) . get_view();
		$this->api_enabled = (empty($GLOBALS['migbo_web_api']['url']) ? $GLOBALS['migbo_datasvc_api']['enabled'] : $GLOBALS['migbo_web_api']['enabled']);
		$this->api_timeout['connection'] = 3;
		$this->api_timeout['request'] = 30;
		$this->api_cache['enabled'] = false;
		if ($this->api_cache['enabled'])
		{
			$this->api_cache['expiry'] = 300;
			$this->api_cache['driver'] = XCache::getInstance();
		}
		$api_log = isset($GLOBALS['api_log']) ? $GLOBALS['api_log'] : array();
		foreach ($this->api_log as $key => $default)
			if (! is_array($api_log) || array_key_exists($key, $api_log))
				$this->api_log[$key] = $api_log[$key];
		return $this;
	}

	/**
	 * @var MigboWeb
	 */
	private static $instance;

	/**
	 * @return MigboWeb
	 */
	public static function get_instance()
	{
		if (! isset(self::$instance))
		{
			$c = __CLASS__;
			self::$instance = new $c;
		}

		return self::$instance;
	}

	/**
	 * Check if API exists
	 *
	 * @param string $path API path to be tested with values
	 * @return string rest path format with %s or %d, or false if doesn't exist
	 * @example
	 * api_exists('/sso/check/asdf/asdf') // return '/sso/check/%s/%s'
	 * api_exists('/sso/fake/rest/path') // return false
	 */
	public static function api_exists($path)
	{
		return Mig33api::api_exists_helper($path, __CLASS__);
	}

	protected function merge_curlopt($method_curlopt, $ch_array)
	{
		return parent::merge_curlopt($method_curlopt
			, $this->passthru_incoming_headers($ch_array)
		);
	}

	private function passthru_incoming_headers($ch_array)
	{
		// Merge cookies
		$cookies = empty($ch_array[CURLOPT_COOKIE])
			? array()
			: $ch_array[CURLOPT_COOKIE];
		$ch_array[CURLOPT_COOKIE] = $cookies + $_COOKIE;

		// Populate headers
		$headers = apache_request_headers();
		$headers['Host'] = $_SERVER['SERVER_NAME'];
		unset($headers['Cookie']);
		foreach($headers as $key => $item)
		{
			$ch_array[CURLOPT_HTTPHEADER][] = sprintf('%s: %s', $key, $item);
		}

		return $ch_array;
	}
}