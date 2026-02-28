<?php
	fast_require("Model", get_framework_common_directory()."/model.php");
	fast_require("Validator", get_framework_common_directory()."/validator.php");
	fast_require("View", get_framework_common_directory()."/view.php");
	fast_require("ControllerDefinition", get_framework_common_directory()."/controller_definition.php");
	fast_require("ControllerMethodReturn", get_framework_common_directory()."/controller_method_return.php");

	require_once(get_framework_common_directory() . "/modules.php");
	require_once(get_framework_common_directory() . "/web_utilities.php");
	require_once(get_framework_common_directory() . "/pagelet_utilities.php");
	require_once(get_framework_common_directory() . "/touch_utilities.php");

    require_once(get_library_directory() . "/instrumentation/instrumentation.php");

	//require_once('FirePHPCore/fb.php');

	/**
	*
	* Controller singleton class
	*
	**/
	class Controller
	{
		protected $debug = DEBUG_MODE;
		private $template_dir = "template";
		/**
		*
		* Protected constructor for singleton pattern
		*
		**/
		protected function __construct()
		{
			if( value_exists("debug") )
			{
				error_reporting(get_value("debug") == '1' ? (E_ALL ^ E_NOTICE) : E_ALL);
				ini_set("display_errors", 1);
			}
		}

		/**
		*
		* Protected clone for singleton pattern
		*
		**/
		protected function __clone()
		{
		}

		/**
		*
		* We add Instrumentation::stop() in destruct so that instrumentation data surely gets written to disk.
                * This function MUST BE public - so that PHP Garbage Collector can call it during shutdown
		*
		**/
		function __destruct()
		{
			Instrumentation::stop();
		}

		/**
		* Get a singleton instance
		* @return Controller
		**/
		public static function get_instance()
		{
			static $instance;

			if (!isset($instance))
			{
				$c = __CLASS__;
				$instance = new $c;
			}

			Instrumentation::start();
			return $instance;
		}

		/**
		*
		* Get the page title
		*
		**/
		protected function get_page_title($config)
		{
			if( isset($config["page_title"]) )
				return $config["page_title"];
			return null;
		}

		/**
		*
		* Get the body class
		*
		**/
		protected function get_body_class($config)
		{
			if( isset($config["body_class"]) )
				return $config["body_class"];
			return null;
		}

		/**
		*
		* Get the header template
		*
		**/
		protected function get_header($config)
		{
			if( isset($config["header"]) )
				return $config["header"];
			return null;
		}

		/**
		*
		* Get the view directory
		*
		**/
		protected function get_view_directory($view='')
		{
			if(empty($view))
			{
				$view = get_view();
			}
			return get_framework_base_directory() . "/view/" . get_controller() . "/" . $view;
		}

		/**
		 * Redirects via 302
		 * Replace %view% with current view
		 * Replace %.*% with GLOBALS variable if found
		 *
		 * @param string $redirect
		 * @example /%view%/home
		 * @example %migbo_server_root%/%view%/home
		 */
		protected function redirect($redirect)
		{
			$redirect = str_replace('%view%', get_view(), $redirect);
			if (preg_match_all('/%([^%]+)%/', $redirect, $matches, PREG_SET_ORDER))
			{
				foreach ($matches as $match)
					array_key_exists($match[1], $GLOBALS)
					&& $redirect = str_replace($match[0], $GLOBALS[$match[1]], $redirect);
			}
			header('Location: ' . $redirect);
			exit();
		}

		/**
		*
		* Execute the validators
		*
		**/
		protected function validate($validator, $data=null)
		{
			if (empty($validator)) return null;

			if(strtolower($validator['name']) == "none" ) return null;

			// check whitelist/blacklist
			if (isset($validator['whitelist']))
			{
				if (!in_array(get_view(), $validator['whitelist'])) return null;
			}
			else if (isset($validator['blacklist']))
			{
				if (in_array(get_view(), $validator['blacklist'])) return null;
			}

			$file = get_validator_directory() . "/" . $validator['base'] . "/" . $validator['name'] . "_validator.php";

			if(!file_exists($file))
			{
				throw new Exception($validator['base'] . '.' . $validator['name'] . ' validator not found.');
			}

			require_once($file);

			$v_obj = new $validator['class']();
			$error = new ValidatorError();
			$view = $v_obj->validate($data, $error, $validator['args']);

			if( $error->is_error )
			{
				ActionRouter::get_instance()->redirect($view->get_controller(), $view->get_action(), $view->get_data());
			}


			return null;
		}

		/**
		*
		* Get the model data
		*
		**/
		protected function get_model_data($model, $start_model=null)
		{
			if ($start_model==null) $start_model = array();


			// check whitelist/blacklist
			if (isset($model['whitelist']))
			{
				if (!in_array(get_view(), $model['whitelist'])) return $start_model;
			}
			else if (isset($model['blacklist']))
			{
				if (in_array(get_view(), $model['blacklist'])) return $start_model;
			}

			$this->execute_model($model, $start_model);


			return $start_model;
		}

		/**
		*
		* Execute the model and retrieve the data
		*
		**/
		protected function execute_model($model, &$model_array)
		{
			$filename = get_model_directory() . "/" . $model['base'] . "/" . $model['name'] . ".php";

			if (!file_exists($filename))
			{
				throw new Exception("model class file doesn't exist: {$model['base']}/{$model['name']}");
			}

			fast_require($model['class'], $filename);

			try
			{
				$m = new $model['class']();
				$data = $m->get_data($model_array, $model['args']);
			}
			catch(ControllerMethodReturn $return)
			{
				ActionRouter::get_instance()->redirect($return->get_controller(), $return->get_action(), $return->get_data());
			}
			catch(Exception $e)
			{
				error_log("model exception {$model['base']}/{$model['name']}: ".$e->getMessage());
				$data = array("error" => $e->getMessage());
			}

			if( !is_null($data) && is_array($data) )
			{
				$tmp = array_merge($model_array, $data);
				$model_array = $tmp;
			}
		}

		/**
		*
		* Execute function
		*
		**/
		protected function execute_function($function, &$data)
		{
			$return = null;

			if(is_null($function)) return $return;

			$filename = get_controller_directory() . "/" . get_controller() . ".php";

			if( !file_exists($filename) )
				throw new Exception("unable to load controller: " . get_controller());

			fast_require($function['class'], $filename);

			$c = new $function['class']();

			if( method_exists($c, $function['method']) )
			{
				$return = $c->$function['method']($data, $function['args']);

				if( $return instanceof ControllerMethodReturn )
				{
					// Note: we don't need to return because ActionRouter takes over the whole processing flow and exits
					// in effect, the current method run will never return
					ActionRouter::get_instance()->redirect($return->get_controller(), $return->get_action(), $return->get_data());
				}
			}

			return $return;
		}

		/**
		*
		* Get cache key
		*
		**/
		protected function get_page_cache_key($config, $data)
		{
			$data["url"] = urlencode($_SERVER["REQUEST_URI"]);
			return str_replace('%URL%', $data["url"], $config);
		}

		/**
		*
		**/
		protected function get_template_file($template,$active_view) {
			$template_file = $this->get_view_directory($active_view) ."/". $this->template_dir ."/". $template . "_template.php";

			if( !file_exists($template_file) )
			{
				if($this->template_dir != "template") {
					$template_file = $this->get_view_directory($active_view) ."/template/". $template . "_template.php";
					if( !file_exists($template_file) )
					{
						throw new Exception("View not found. [".$template_file."]");
					}
				} else{
					throw new Exception("View not found. [".$template_file."]");
				}
			}

			return $template_file;
		}

		/**
		*
		* Render the view
		*
		**/
		protected function render($template, $decorator, $data=null, $active_view=null)
		{
			if( is_null($template) ) return;

			if( empty($active_view) )
			{
				$active_view = get_view();
			}

			// $server_root is accessed directly in views and MUST be defined as global in this method
			// see JIRA-856 for what happens when it is removed :/
			global $server_root, $mogileFSImagePath;

			$template_file = $this->get_template_file($template,$active_view);

			// this block creates local variable for this function which are meant to
			// be accessible by the view and decorator
			if( !is_null($data) )
			{
				foreach($data as $key=>$value)
				{
					$$key = $value;
				}
			}

			if(!is_null($decorator) && "none" != $decorator["name"])
			{
				if(!is_null($this->get_header($decorator)))
				{
					$header_template = $this->get_view_directory($active_view) . "/template/" . $this->get_header($decorator) . ".php";
					if( !file_exists($header_template) )
						throw new Exception("Header Template not found. [".$this->get_header($decorator)."]");
				}

				$body_class = $this->get_body_class($decorator);
				$page_title = $this->get_page_title($decorator);
				$body_template = $template_file;

				// compute decorator location
				$tokens = explode('.', $decorator["name"]);
				$filename = (count($tokens) > 1 ? get_framework_base_directory() : $this->get_view_directory($active_view))
					. "/decorator/"
					. end($tokens)
					. ".php";

				require($filename);
			}
			else
			{
				require($template_file);
			}
		}

		/**
		*
		* Encode the data
		*
		**/
		protected function encode($encoding, RestResult $rest_result)
		{
			switch( $encoding )
			{
				case 'xml':
					fast_require('RestXmlResponse', get_library_directory() . '/rest/rest_xml_response.php');
					$response = new RestXmlResponse($rest_result);
					break;
				case 'json':
				default:
					fast_require('RestJsonResponse', get_library_directory() . '/rest/rest_json_response.php');
					$response = new RestJsonResponse($rest_result);
					break;
			}

			$response->send_response();
		}

		protected function execute_components($components, &$data)
		{
			foreach ($components as $component)
			{
				if($component['type'] == 'Model')
				{
					$data = $this->get_model_data($component, $data);

				}

				if ($component['type'] == 'Validator')
				{
					$this->validate($component, $data);
				}
			}

			return $data;
		}

		/**
		*
		* Stop execution
		*
		**/
		public function stop()
		{
			fast_require('Logger', get_framework_common_directory(). '/logger.php');
			if (SystemProperty::get_instance()->get_boolean(SystemProperty::EnableMemorizationCacheLogging, false))
			foreach (DAO::$memorization_cache as $type => $cache)
			{
				if ($cache['hit'] || $cache['miss'])
				Logger::getLogger('memorization_cache')->info(implode(' '
					, array($type, $cache['miss'], $cache['hit'], $_SERVER['REQUEST_URI'])
				));
			}

			PageCache::get_instance()->stop();
			Instrumentation::stop();
			exit();
		}

		/**
		*
		* Execute the controller
		*
		**/
		public function execute($controller, $action, $data=null)
		{
			/**
			* Get the action definition
			**/

			$definition = new ControllerDefinition($controller, $action, !$this->debug);

			/**
			 * Redirect via header
			 */
			$redirect = $definition->get_redirect();
			if (! empty($redirect)) $this->redirect($redirect);

			/**
			*
			* Initialise the page cache
			*
			**/

			$cache = $definition->get_cache();
			$use_page_cache = ( !empty($cache) && !is_post() );
			$page_cache_key = null;

			if( $use_page_cache )
			{
				/**
				* Get the cache key for the page
				**/
				$page_cache_key = $this->get_page_cache_key($cache, $init_model_data);

				if( empty($page_cache_key) )
				{
					throw new Exception("Invalid page cache key");
				}

				fast_require("PageCache", get_framework_common_directory() . "/page_cache.php");
				PageCache::get_instance()->start();
			}

			/**
			* check for caching
			**/
			if( $use_page_cache )
			{
				/**
				* Attempt stream cached file
				**/
				if( PageCache::get_instance()->stream_page($page_cache_key) )
				{
					$this->stop();
				}
			}


			/**
			* Execute models and validators
			**/
			$model_data = $this->execute_components($definition->get_components(), $data);

			if ($definition->get_encoding())
			{
				fast_require('RestResult', get_library_directory() . '/rest/rest_result.php');
				try
				{
					if (is_null($definition->get_function()))
						throw new Exception('Function must be defined if encoding is expected.');

					/**
					* Execute function
					**/
					$rest_result = $this->execute_function($definition->get_function(), $model_data);
					if (!($rest_result instanceof RestResult))
						throw new Exception('Result must be an instance of RestResult.');
				}
				catch(Exception $e)
				{
					$rest_result = new RestResult(null, 503, -1, $e->getMessage(), $e->getMessage());
				}

				/**
				* Encode the data
				**/
				$this->encode(
					$definition->get_encoding(),
					$rest_result
				);
			}
			else
			{
				/**
				* Execute function
				**/
				$this->execute_function($definition->get_function(), $model_data);

				/**
				* Render the view
				**/

				list($decorator, $active_view) = $this->get_active_view_and_decorator($definition->get_view(), $definition);
				$this->render(
					$definition->get_view(),
					$decorator,
					$model_data,
					$active_view
				);
			}


			if( $use_page_cache )
			{
				PageCache::get_instance()->save_page($page_cache_key);
			}


			$this->stop();
		}

		protected function get_active_view_and_decorator($template, ControllerDefinition $controller_definition)
		{
			$active_view = get_view();
			$version_suffix = $this->get_version_suffix();
			$this->template_dir = "template".$version_suffix;

			$view_precedence = array_unique(array(
				  $active_view
				, View::get_override_view($active_view)
			));

			foreach ($view_precedence as $selected_view)
			{
				$decorator = $controller_definition->get_decorator($selected_view, $this->get_view_directory($selected_view), $version_suffix);
				// if $decorator is defined, break out of loop
				if (! empty($decorator)) break;
				// if there is no decorator defined at all, $decorator will be null
			}

			foreach ($view_precedence as $selected_view)
			{
				if (file_exists($this->get_view_directory($selected_view) ."/". $this->template_dir ."/". $template . "_template.php")
					|| file_exists($this->get_view_directory($selected_view) ."/template/". $template . "_template.php")
					)
				{
					return array($decorator, $selected_view);
				}
			}

			return array($decorator, $active_view);
		}

		private function get_version_suffix() {
			
			$client_version = floor(ClientInfo::get_version_number());

			// special handling for v4 account/payment
			if( $client_version == 4 && (get_controller() == 'account' || get_controller() == 'payment' ) ) {
				return "_v5";
			}

			if($client_version > 0) 
			{
				return "_v".$client_version;
			}
			else
			{
				return "";
			}
		}
	}
