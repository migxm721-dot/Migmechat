<?php
	require_once(get_common_config_location());
	require_once(get_common_inc_location());
	require_once(get_framework_common_directory() . "/query_string_field.php");

	fast_require('View', get_framework_common_directory() . '/view.php');
	fast_require('ClientInfo' , PACKAGESPATH . 'mig33/libraries/ClientInfo.php');
	fast_require('Console' , PACKAGESPATH . 'mig33/libraries/Console.php');
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");
	fast_require("Language", get_library_directory() . "/language/language.php");
	Language::get_instance()->initialise();

	/**
	*
	* Perform a first require_once
	*
	**/
	function fast_require($classname, $filename)
	{
		class_exists($classname) || require_once($filename);
	}

	/**
	*
	* Get the file location
	*
	**/
	function get_file_location($filename)
	{
		return $_SERVER["DOCUMENT_ROOT"] . '/' . $filename;
	}

	/**
	*
	* Get the file location for common-inc.php
	*
	**/
	function get_common_inc_location()
	{
		return get_file_location("common/common-inc.php");
	}

	/**
	*
	* Get the file location for common-config.php
	*
	**/
	function get_common_config_location()
	{
		return get_file_location("common/common-config.php");
	}

	/**
	*
	* Get the base directory for the mvc framework
	*
	**/
	function get_framework_base_directory()
	{
		return $_SERVER["DOCUMENT_ROOT"] . '/sites';
	}

	/**
	*
	* Get the error directory
	*
	**/
	function get_error_directory()
	{
		return get_framework_base_directory() . '/error';
	}

	/**
	*
	* Get the DAO directory
	*
	**/
	function get_dao_directory()
	{
		return get_framework_base_directory() . '/dao';
	}

	/**
	*
	* Get the library directory
	*
	**/
	function get_library_directory()
	{
		return get_framework_base_directory() . '/lib';
	}

	/**
	*
	* Get the validator directory
	*
	**/
	function get_validator_directory()
	{
		return get_framework_base_directory() . '/validation';
	}

	/**
	*
	* Get the resources directory
	*
	**/
	function get_resources_directory()
	{
		return get_framework_base_directory() . '/resources';
	}

	/**
	*
	* Get the controller directory
	*
	**/
	function get_controller_directory()
	{
		return get_framework_base_directory() . '/controller';
	}

	/**
	*
	* Get the view directory
	*
	**/
	function get_view_directory()
	{
		return get_framework_base_directory() . '/view';
	}

	/**
	*
	* Get the view directory
	*
	**/
	function get_decorator_directory()
	{
		return get_framework_base_directory() . '/decorator';
	}

	/**
	*
	* Get the model directory
	*
	**/
	function get_model_directory()
	{
		return get_framework_base_directory() . '/model';
	}

	/**
	*
	* Get the module directory
	*
	**/
	function get_module_directory()
	{
		return get_framework_base_directory() . '/module';
	}

	/**
	*
	* Get the domain base directory
	*
	**/
	function get_domain_directory()
	{
		return get_framework_base_directory() . '/domain';
	}

	/**
	*
	* Get the framework common directory
	*
	**/
	function get_framework_common_directory()
	{
		return get_framework_base_directory() . '/common';
	}

	/*
	*
	* Get http header
	*
	*/
	function get_http_header()
	{
		return apache_request_headers();
	}

	/**
	*
	* Check if the request is a post
	*
	**/
	function is_post()
	{
		$method = $_SERVER["REQUEST_METHOD"];
		return (strtolower($method) == "post");
	}

	/**
	*
	* Get the controller
	*
	**/
	function get_controller()
	{
		return get_value('c', 'string', '');
	}

	/**
	*
	* Set the controller
	*
	**/
	function set_controller($controller_value)
	{
		set_value('c', $controller_value);
	}

	/**
	*
	* get the view for the action
	*
	**/
	function get_view()
	{
		return get_value('v', 'string', '');
	}

	/**
	 * Set the view
	 * @return string
	 */
	function set_view($view)
	{
		set_value('v', $view);
	}

	function is_ajax_view()
	{
		return (strtolower(get_view())==View::MIG33_AJAX);
	}

	function is_wap_view()
	{
		return (strtolower(get_view())==View::WAP);
	}

	function is_midlet_view()
	{
		return (strtolower(get_view())==View::MIDLET);
	}

	function is_mre_view()
	{
		return (strtolower(get_view())==View::MTK_MRE);
	}

	function is_touch_view()
	{
		return (strtolower(get_view())==View::TOUCH);
	}

	function is_blackberry_view()
	{
		return (strtolower(get_view())==View::BLACKBERRY);
	}

	function is_ios_view()
	{
		return (strtolower(get_view())==View::IOS);
	}

	function is_corporate_view()
	{
		return (strtolower(get_view())==View::MIG33_CORPORATE);
	}

	function is_json_view()
	{
		return (strtolower(get_view())==View::JSON);
	}

	/**
	 * Get the method for this action
	 * @return string default to 'default'
	 */
	function get_action()
	{
		return get_value('a', 'string', 'default');
	}

	/**
	 * Set the method for an action
	 * @param string $action_value
	 * @return void
	 */
	function set_action($action_value)
	{
		set_value('a', $action_value);
	}

	/**
	*
	* get the type for this action
	*
	**/
	function get_type()
	{
		return get_value('t', 'string', '');
	}

	/**
	 * Get the current controller and view url
	 * @deprecated use get_action_url($action, $attributes)
	 * @param string $action
	 * @param array|string $attributes
	 * @return string
	 */
	function get_controller_view_url($action, $attributes = array())
	{
		return get_action_url($action, $attributes);
	}

	function get_action_url($action, $attributes = array())
	{
		return get_framework_url(get_controller(), $action, get_view(), $attributes);
	}

	function get_controller_action_url($controller, $action, $attributes = array())
	{
		return get_framework_url($controller, $action, get_view(), $attributes);
	}

	/**
	 * Get framework URL
	 * @param string $controller
	 * @param string $action
	 * @param string $view
	 * @param array|string $attributes
	 * @return string /sites/index.php?c=...&a=...&v=...&attr1=...&attr2=...
	 */
	function get_framework_url($controller, $action, $view, $attributes = array())
	{
		global $server_root;

		$controller = empty($controller) ? get_controller() : $controller;
		$action = empty($action) ? get_action() : $action;
		$view = empty($view) ? get_view() : $view;

		if(is_string($attributes))
		{
			fast_require("Logger", get_framework_common_directory()."/logger.php");
			Logger::getLogger("get_framework_url")->info(
				  $view . '|'
				. $controller . '|'
				. $action . ' '
				. get_view() . '/'
				. get_controller() . '/'
				. get_action() . ' '
				. $attributes
			);

			parse_str($attributes, $attributes);
		}

		if (isset($attributes['c']))
		{
				if (!empty($attributes['c'])) $controller = $attributes['c'];
				unset($attributes['c']);
		}

		if (isset($attributes['a']))
		{
				if (!empty($attributes['a'])) $action = $attributes['a'];
				unset($attributes['a']);
		}

		if (isset($attributes['v']))
		{
				if (!empty($attributes['v'])) $view = $attributes['v'];
				unset($attributes['v']);
		}

		if ($controller == 'profile'
			&& $action == 'home'
			&& in_array(View::get_override_view($view), array(View::MIDLET, VIEW::WAP))
			&& ! SystemProperty::get_instance()->get_boolean(SystemProperty::Migbo_MigboDisabled, false))
		{
			try
			{
				return get_migbo_profile_url($controller, $action, $view, $attributes);
			}
			catch (Exception $ex)
			{
				// do nothing and return regular url format
			}
		}
		else if ($controller == 'group'
			&& $action == 'home'
			&& is_ajax_view()
			&& (array_key_exists('group_id', $attributes) || array_key_exists('cid', $attributes)))
		{
			$group_id = array_key_exists('group_id', $attributes) ? $attributes['group_id'] : $attributes['cid'];
			return '/group/' . $group_id;
		}

		$attrs = set_attributes($attributes);
		$prefix = DEBUG_MODE && (
				   1 == strpos($_SERVER['REQUEST_URI'], 'debug/')
				|| 1 == strpos($_SERVER['REQUEST_URI'], 'sites/tracer.php')
			)
			? 'debug' : 'sites';

		return implode('/', array($server_root, $prefix, $view, $controller, $action)) . (empty($attrs) ? '?' : '?'.$attrs);
	}

	/**
	 * @param string $method
	 * @return array ($controller, $method, $view)
	 */
	function get_cav_from_string($method)
	{
		$cav = explode('/', $method);
		switch(count($cav))
		{
			case 1:
				return array(get_controller(), empty($method) ? get_action() : $method, get_view());
				break;
			case 2:
				return array($cav[0], $cav[1], get_view());
				break;
			case 3:
			default:
				return array($cav[1], $cav[2], $cav[0]);
				break;
		}
	}

	/**
	 * @param string $controller
	 * @param string $action
	 * @param string $view
	 * @param array $attributes
	 * @throws Exception
	 * @return string
	 */
	function get_migbo_profile_url($controller, $action, $view, $attributes)
	{
		if (! empty($attributes['username']))
		{
			$username = $attributes['username'];
			unset($attributes['username']);
		}
		else if (class_exists('UserModel') && ! empty(SessionUtilities::$session_user))
		{
			// requires UserModel to be loaded
			$username = SessionUtilities::$session_user;
		}
		else
		{
			// this is extremely edge case but we can't rule out the possibility
			fast_require("Logger", get_framework_common_directory()."/logger.php");
			Logger::getLogger("get_migbo_profile_url")->info(
				$_SERVER['REQUEST_URI'] . ' ' . http_build_query($attributes)
			);
			throw new Exception('username and session_user not found, return common profile/home');
		}

		$migbo_server_root = View::get_override_view($view) == View::MIDLET ? $GLOBALS['migcore_migbo_server_root'] : $GLOBALS['migbo_server_root'];
		$view = empty($view) ? get_view().'/' : $view.'/';
		return $migbo_server_root . '/' . $view . 'u/' . $username . '?' . http_build_query($attributes);
	}

	/**
	 * Get URL of avatar on mogileFS based on username
	 *
	 * @param string $username
	 * @param array $attributes default to array( 'w'=>48, 'h'=>48, 'c'=>1, 'a'=>1 )
	 * @return string avatar url
	 */
	function get_avatar_url($username, $attributes = array())
	{
		return get_image_url('a/'.$username, $attributes);
	}

	/**
	 * Get URL of profile picture on mogileFS based on username
	 *
	 * @param string $username
	 * @param array $attributes default to array( 'w'=>48, 'h'=>48, 'c'=>1, 'a'=>1 )
	 * @return string profile picture url
	 */
	function get_profile_picture_url($username, $attributes = array())
	{
		return get_image_url('u/'.$username, $attributes);
	}

	/**
	 * Get URL of images on mogileFS
	 *
	 * @param string $imagename
	 * @param array $attributes default to array( 'w'=>48, 'h'=>48, 'c'=>1, 'a'=>1 )
	 * @return string avatar url
	 */
	function get_image_url($imagename, $attributes = array())
	{
		global $mogileFSImagePath;
		/**
		 * if param is not set, use default param
		 */
		$default = array( 'w'=>48, 'h'=>48, 'c'=>1, 'a'=>1 );
		foreach($default as $key=>$defaultValue)
			if( !isset($attributes[$key]) )
				$attributes[$key] = $defaultValue;

		return $mogileFSImagePath.'/'.$imagename.'?'.http_build_query($attributes);
	}

	/**
	 * Get login URL
	 *
	 * @param string $url should be absolute
	 * @param boolean $is_logout
	 * @param boolean $use_redirect_url
	 * @return string login url with hash
	 */
	function get_login_url($url = '', $is_logout = false, $use_redirect_url = true)
	{
		global $server_root, $login_server_root, $return_url_key;

		$current_url = empty($url) ? $server_root . $_SERVER['REQUEST_URI'] : $url;

		if ($use_redirect_url) {
			return implode('/', array(
				  $login_server_root
				, get_view()
				, $is_logout ? 'logout' : 'login'
				, md5($return_url_key . urlencode($current_url))
			)) . '?' . http_build_query(array(
				'return_url' => $current_url
			));
		} else {
			return implode('/', array(
				  $login_server_root
				, get_view()
				, $is_logout ? 'logout' : 'login'
			));
		}
	}

	/**
	 * Get logout URL
	 *
	 * @return string logout url with hash
	 */
	function get_logout_url($url = '', $use_redirect_url = true)
	{
		return get_login_url($url, true, $use_redirect_url);
	}

	/**
	 * Get the value from either $_GET or $_POST or empty string if not available
	 *
	 * @param string $valuename
	 * @param string $type
	 * @param string|int $default
	 * @return string|int $value
	 */
	function get_value($valuename, $type="string", $default="")
	{
		$value = $default;
		if( isset($_GET[$valuename]) && $_GET[$valuename] !== '' )
			$value = $_GET[$valuename];
		else if( isset($_POST[$valuename]) && $_POST[$valuename] !== '' )
			$value = $_POST[$valuename];
		settype($value, $type);
		return $value;
	}

	/**
	*
	* Set the value to valuename to either $_GET or $_POST
	*
	**/
	function set_value($valuename, $value)
	{
		if(value_exists($valuename))
		{
			if( isset($_POST[$valuename]) )
				$_POST[$valuename] = $value;
			else
				$_GET[$valuename] = $value;
		}
		else
		{
			if( $_POST )
			{
				$_POST[$valuename] = $value;
			}
			else
			{
				$_GET[$valuename] = $value;
			}
		}
	}

	/**
	*
	* Get the value from an array or return default
	*
	**/
	function get_value_from_array($valuename, $array, $type="string", $default="")
	{
		if (! is_array($array) && ! ($array instanceof ArrayObject)) return $default;
		$value = $default;
		if( isset($array[$valuename]) )
			$value = $array[$valuename];
		settype($value, $type);
		return $value;
	}

	/**
	*
	* Get the value from an array if the value is empty
	*
	**/
	function get_value_if_empty($value, $valuename, $array, $type="string", $default="")
	{
		if( !empty($value) )return $value;
		$value = $default;
		if( isset($array[$valuename]) )
			$value = $array[$valuename];
		return $value;
	}

	/**
	*
	* Check if the value exists
	*
	**/
	function value_exists($value)
	{
		if( isset($_POST[$value]) || isset($_GET[$value]) ) return true;
		return false;
	}

	/**
	*
	* Format Bytes Into TB/GB/MB/KB/Bytes
	*
	**/
	function format_filesize($rawSize) {
		if($rawSize > 1099511627776) {
			return number_format($rawSize/1099511627776, 1).' TB';

		} elseif($rawSize > 1073741824) {
			return number_format($rawSize/1073741824, 1).' GB';

		} elseif($rawSize > 1048576) {
			return number_format($rawSize/1048576, 1).' MB';

		} elseif($rawSize > 1024) {
			return number_format($rawSize/1024, 1).' KB';

		} elseif($rawSize >= 0) {
			return number_format($rawSize, 0).' bytes';

		} else {
			return 'unknown';
		}
	}

	/**
	 * Remove all but alphanumeric values
	 * @param  $value
	 * @return mixed
	 */
	function sanitize($value)
	{
		return preg_replace('/[^a-zA-Z0-9_\s]/', '', $value);
	}

	/**
	*
	* Parse Display Picture
	*
	**/
	function parse_display_picture($display_picture)
	{
		if(!preg_match("/^[A-Za-z0-9._-]+$/", $display_picture))
		{
			return '';
		}
		else
		{
			return $display_picture;
		}
	}

	/*
	 * filter string to avoid xss
	 */
	function clean_value($string)
	{
		return htmlspecialchars( strip_tags( trim( $string ) ), ENT_QUOTES, 'UTF-8' );
	}

	/*
	 * Escape JavaScript
	 */
	 function escape_js($text)
	 {
		$safe_text = preg_replace('/&#(x)?0*(?(1)27|39);?/i', "'", stripslashes($text));
		$safe_text = str_replace("\r", '', $safe_text);
		$safe_text = str_replace("\n", '\\n', addslashes($safe_text));
		return $safe_text;
	 }

	/*
	 * Ceiling With Precision
	 */
	function ceiling($value, $precision = 0)
	{
		return ceil($value * pow(10, $precision)) / pow(10, $precision);
	}

	function redirect($url)
	{
		if (is_wap_view())
		{
			exit('<html><head><title>' . _('Redirecting...')
				. '</title><meta http-equiv="refresh" content="0;url='
				. $url
				. '"></head><body><p style="text-align: center;">'
				. _('Redirecting...')
				. '</p><p style="text-align: center;"><a href="'.$url.'">'
				. _('or click here')
				. '</a></p></body></html>'
			);
		}
		else
		{
			header('Location: ' . $url); exit();
		}
	}

	/*
	 * default output is JSON
	 */
	function get_api_output()
	{
		return get_value('format', 'string', 'json');
	}

	/**
	 * Request Headers
	 *
	 * In Apache, you can simply call apache_request_headers(), however for
	 * people running other webservers the function is undefined.
	 *
	 * @return array
	 */
	function request_headers($reset = false)
	{
		static $request_headers = array();
		if ($reset) $request_headers = array();
		if (count($request_headers)) return $request_headers;

		// Look at Apache go!
		if (function_exists('apache_request_headers'))
		{
			$request_headers = apache_request_headers();
		}
		else
		{
			$headers['Content-Type'] = (isset($_SERVER['CONTENT_TYPE'])) ? $_SERVER['CONTENT_TYPE'] : @getenv('CONTENT_TYPE');

			foreach ($_SERVER as $key => $val)
			{
				if (strncmp($key, 'HTTP_', 5) === 0)
				{
					$headers[substr($key, 5)] = array_key_exists($key, $_SERVER) ? $_SERVER[$key] : FALSE;
				}
			}

			// take SOME_HEADER and turn it into Some-Header
			foreach ($headers as $key => $val)
			{
				$key = str_replace('_', ' ', strtolower($key));
				$key = str_replace(' ', '-', ucwords($key));

				$request_headers[$key] = $val;
			}
		}

		return $request_headers;
	}

	function is_https_request()
	{
		return array_key_exists('HTTPS', $_SERVER) && !empty($_SERVER['HTTPS']);
	}

	/**
	 * Created to redirect old framework pages to CAV framework
	 *
	 * @param string $controller
	 * @param string $action
	 * @param string $view
	 * @param array $data
	 */
	function execute_cav_framework($controller, $action, $view, $data = array())
	{
		set_view($view);
		set_controller($controller);
		set_action($action);
		foreach ($data as $key => $value) set_value($key, $value);

		fast_require('ActionRouter', get_framework_common_directory() . '/action_router.php');
		ActionRouter::get_instance()->execute();
	}

	/**
	 * Store the last used view used by user in session
	 * to be used in conjunction with get_last_active_view()
	 */
	function store_active_view($sessionID) {
		$memcached = Memcached::get_instance();
		$key = Memcached::get_memcache_full_key(Memcached::$KEYSPACE_SESSION_ACTIVE_VIEW, sha1($sessionID));
		$memcached->add_or_update($key, get_view(), Memcached::$CACHEDURATION_SESSION_ACTIVE_VIEW);
	}

	function get_last_active_view($sessionID) {
		$memcached = Memcached::get_instance();
		$key = Memcached::get_memcache_full_key(Memcached::$KEYSPACE_SESSION_ACTIVE_VIEW, sha1($sessionID));
		return $memcached->get($key);
	}

	/**
	 * Coupling with local migbo-web repository, to be used with extract()
	 * @example extract(require_migbo_variables())
	 * @return array $i18n, $variable, $build_number
	 */
	function require_migbo_variables()
	{
		global $apache_dir;
		if (! defined('APPPATH'))
			// migcore needs APPPATH to be defined
			define('APPPATH', $apache_dir . '/migbo/application');

		// Get Variables & i18n Text
		require_once(APPPATH . '/config/variables.php');
		$i18n = $config['i18n_js'];
		unset($config['i18n_js']);
		$variable = $config;
		unset($config);

		// Get Build Number
		require_once(APPPATH . '/config/build.php');
		$build_number = $config['build_number'];
		unset($config);

		return array(
			  'i18n' => $i18n
			, 'variable' => $variable
			, 'build_number' => $build_number
		);
	}

	/**
	 * helper functions for google analytics measurement protocol Krizon
	 */
	function get_cid() {
		// Handle the parsing of the _ga cookie or setting it to a uniqute identifier
		if (isset($_COOKIE['_ga'])) {
			list($version,$domainDepth, $cid1, $cid2) = preg_split('[\.]', $_COOKIE["_ga"],4);
			$contents = array('version' => $version, 'domainDepth' => $domainDepth, 'cid' => $cid1.'.'.$cid2);
			$cid = $contents['cid'];
		} else {
			$cid = $this->gaGenUUID();
		}
		return $cid;
	}

	function ga_track_event($category, $action, $description) {
		require_once (PACKAGESPATH . 'mig33/libraries/composer/vendor/autoload.php');
		$config = array(
			'ssl' => true // Enable/Disable SSL, default false
		);
		$client = Krizon\Google\Analytics\MeasurementProtocol\MeasurementProtocolClient::factory($config);

		$channel = '';
		switch($this->CI->get_reg_method())
		{
			case Token_model::RegTypeEmail1:
				$channel = 'email';
				break;
			case Token_model::RegTypeEmail2:
				$channel = 'email';
				break;
			case Token_model::RegTypeFacebook:
				$channel = 'facebook';
				break;
			default:
				$channel = 'web';
		}

		$client->event(array(
			'v'   => '1',
			'tid' => $this->CI->config->item('ga_account'), // Tracking Id 
			'cid' => $this->get_cid(), // Customer Id
			't'   => 'event', // Hit type
			'ec'  => $category, // Event category
			'ea'  => $action, // Event action
			'el'  => $channel.' - '.$description, // Event label
		));
	}

	// Generate UUID v4 function - needed to generate a CID when one isn't available
	function gaGenUUID() {
	  return sprintf( '%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
		// 32 bits for "time_low"
		mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ),

		// 16 bits for "time_mid"
		mt_rand( 0, 0xffff ),

		// 16 bits for "time_hi_and_version",
		// four most significant bits holds version number 4
		mt_rand( 0, 0x0fff ) | 0x4000,

		// 16 bits, 8 bits for "clk_seq_hi_res",
		// 8 bits for "clk_seq_low",
		// two most significant bits holds zero and one for variant DCE1.1
		mt_rand( 0, 0x3fff ) | 0x8000,

		// 48 bits for "node"
		mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff )
	  );
	}
