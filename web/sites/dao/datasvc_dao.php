<?php
fast_require('Mig33api', PACKAGESPATH . 'mig33/libraries/Mig33api.php');
fast_require('Mig33Datasvc_model', PACKAGESPATH . 'restserver/models/mig33datasvc_model.php');
fast_require('Rest_request', PACKAGESPATH . 'restserver/libraries/rest_request.php');
fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');
fast_require('MigcoreRestService', get_library_directory() . '/fusion/migcore_rest_service.php');

class DatasvcDAO extends Mig33Datasvc_model
{
	const FusionRest = 'FusionRest';
	const MigcoreRestService  = 'MigcoreRestService';
	private static $whitelist = array(
		  FusionRest::KEYSPACE_PING                     => array('get', 'post', 'put', 'delete')
		, FusionRest::KEYSPACE_SESSION_CHECK            => array('get', 'post', 'put', 'delete')
//		, FusionRest::KEYSPACE_USER_PROFILE             => array('get')
//		, FusionRest::KEYSPACE_SETTINGS_ACCOUNT_PROFILE => array('get')
	);

	public function __construct()
	{
		$this->api_url = $GLOBALS['fusion_rest_api']['url'];
		$this->api_enabled = $GLOBALS['fusion_rest_api']['enabled'];
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
		$this->DEBUG = DEBUG_MODE;
		$this->rest_result = new RestResult();
		return $this->set_return_assoc_array(false);
	}

	/**
	 * @var DatasvcDAO
	 */
	private static $instance;

	/**
	 * @return DatasvcDAO
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
	 * @param Rest_request $request
	 * @return boolean
	 */
	protected function api_exists_with_method(Rest_request $request)
	{
		// make sure that Rest_request has been memorized
		$request = $this->determine_data_provider($request);
		switch ($request->provider)
		{
			case DatasvcDAO::FusionRest:
				return isset(self::$whitelist[$request->unprep])
					&& in_array($method, self::$whitelist[$request->unprep]);
				break;
			case DatasvcDAO::MigcoreRestService:
				return ! empty($request->callback);
				break;
			default:
				return false;
		}
	}

	protected function get_content_type_and_body($data = null, $content_type = null)
	{
		return parent::get_content_type_and_body($data, isset($_SERVER['CONTENT_TYPE'])
			? $_SERVER['CONTENT_TYPE']
			: $content_type
		);
	}

	private function get_session_params()
	{
		$_get_args = $_GET;
		$_get_args['sessionId']        = SessionUtilities::get_session_id();
		// requires UserModel to be loaded
		$_get_args['requestingUserid'] = class_exists('UserModel') && SessionUtilities::$session_user_id
			? SessionUtilities::$session_user_id : null;
		$_get_args['view']             = SessionUtilities::get_sso_view();
		foreach(array('c', 'a', 'v') as $i) unset($_get_args[$i]);
		return $_get_args;
	}

	private function populate_session_params($action)
	{
		$session_parms = $this->get_session_params();
		if (empty($session_parms)) return $action;

		if(strpos($action, '?') === false)
			return $action . '?' . http_build_query($session_parms);
		else
			return $action . '&' . http_build_query($session_parms);
	}

	/**
	 * determine data provider and unprep the rest_path
	 * @param string $method get/post/put/delete
	 * @return Rest_request
	 */
	public function determine_data_provider(Rest_request $request)
	{
		if (array_key_exists($request->rest_path, self::$rest_requests))
			return self::$rest_requests[$request->rest_path];

		if ($unprep = FusionRest::api_exists($request->rest_path))
		{
			$request->populate(array(
				  'unprep'    => $unprep
				, 'provider'  => DatasvcDAO::FusionRest
			));
		}
		else if ($unprep = MigcoreRestService::api_exists($request->rest_path))
		{
			$request->populate(array(
				  'unprep'    => $unprep
				, 'provider'  => DatasvcDAO::MigcoreRestService
				, 'callback'  => MigcoreRestService::get_callback($request->method, $unprep)
			));
		}
		return self::$rest_requests[$request->rest_path] = $request;
	}

	/**
	 * @param Rest_request $request
	 * @return RestResult
	 */
	public function request_from_fusion_rest(Rest_request $request)
	{
		$request->rest_path = $this->populate_session_params($request->rest_path);
		return $this->request($request);
	}

	/**
	 * @param Rest_request $request
	 * @param array $data
	 * @return ControllerMethodReturn
	 */
	public function back_to_controller(Rest_request $request, $data = array())
	{
		// substr($rest_path, 0, strpos($rest_path, '?') === false ? strlen($rest_path) : strpos($rest_path, '?'))
		if (! $this->api_exists_with_method($request))
			return $this->rest_result->set_http_status(404)
				->set_app_status(404, sprintf(_('Invalid DAO API: %s'), $request->rest_path));

		MigcoreRestService::populate_get_param_from_api_path($request->rest_path);
		$data['rest_request'] = $request;
		return new ControllerMethodReturn($request->callback, $data);
	}
}
