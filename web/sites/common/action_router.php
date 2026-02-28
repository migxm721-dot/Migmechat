<?php
	fast_require("Controller", get_framework_common_directory()."/controller.php");
	fast_require("PageCache", get_framework_common_directory()."/page_cache.php");

	/**
	*
	* Class to route the page to the right controller
	*
	**/
	class ActionRouter
	{
		/**
		*
		* Actions
		*
		**/
		private $actions = array();

		/**
		*
		* Constructor
		*
		**/
		protected function __construct()
		{
			set_exception_handler(array($this, "exception_handler"));
		}

		/**
		* Get a singleton instance of this class
		* @return ActionRouter
		**/
		public static function get_instance()
	    {
	        static $instance;

			if (!isset($instance))
			{
				$c = __CLASS__;
				$instance = new $c;
			}

			return $instance;
		}

		/**
		*
		* Redirect to another controller
		*
		**/
		public function redirect($controller, $action, $data=null)
		{
			if (get_action() == $action && get_controller() == $controller)
				throw new Exception(sprintf('Operation not allowed, controller (%s) can not redirect to itself', $controller . '/' . $action));

			set_controller($controller);
			set_action($action);

			$this->execute($data, true);
		}

		/**
		*
		* Stop execution
		*
		**/
		public function stop()
		{
			Controller::get_instance()->stop();
		}

		/**
		*
		* Execute the action
		*
		**/
		public function execute($data=null, $redirected=false)
		{
			$controller = get_controller();
            $controller = sanitize($controller);

            $action = get_action();
            $action = sanitize($action);

            $view = get_view();
            $view = sanitize($view);

			// FRAME-48, at the initial routing, we will log POST requests
			// which do NOT have c/a/v in query string
			if (!$redirected && is_post() && (!isset($_GET['c']) || !isset($_GET['a'])))
			{
				fast_require("Logger", get_framework_common_directory()."/logger.php");

				// log query parameters, up to 500 length
				$params = array_merge($_GET, $_POST);
				unset($params['c']);
				unset($params['a']);
				unset($params['v']);

				Logger::getLogger("www.post.access")->info(
					sprintf(
						"\"POST c=%s&a=%s&v=%s\" \"%s\" \"%s\""
						, $controller
						, $action
						, $view
						, $_SERVER["HTTP_USER_AGENT"]
						, substr(http_build_query($params), 0, 250)
					)
				);
			}

            set_controller($controller);
            set_action($action);
            set_view($view);

            $filename = get_controller_directory() . "/" . $controller . ".yaml";

			if( !file_exists($filename) )
				throw new Exception("ActionRouter: Controller=".$controller." not found.");

			Controller::get_instance()->execute($controller, $action, $data);
		}

		/**
		*
		* Exception Handler for the framework
		*
		**/
		public function exception_handler($exception)
		{
			include_once(get_error_directory() . "/error.php");
			$this->stop();
		}
	}
?>