<?php

	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");

	class PageCache
	{
		protected $memcache = null;
		protected $started = false;
		/**
		*
		* private constructor for singleton
		*
		**/
		protected function __construct()
		{
			$this->memcache = Memcached::get_instance();
		}

		/**
		*
		* private clone for singleton
		*
		**/
		protected function __clone()
		{
		}

		/**
		* Get a singleton instance
		* @return PageCache
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
		* Check if the cache has started
		*
		**/
		public function get_started()
		{
			return $this->started;
		}

		/**
		*
		* Start the caching for the page
		*
		**/
		public function start()
		{
			if( $this->started == false )
				$this->started = ob_start();
		}

		/**
		*
		* End the cache and flush the contents
		*
		**/
		public function stop()
		{
			if( $this->started == true )
			{
				ob_end_flush();
				$this->started = false;
			}
		}

		/**
		*
		* Get current cached contents
		*
		**/
		public function get_contents()
		{
			return ob_get_contents();
		}

		/**
		*
		* Save the page
		*
		**/
		public function save_page($key)
		{
			$this->memcache->add_or_update($key, $this->get_contents(), 30);
		}

		/**
		*
		* Stream the page
		*
		**/
		public function stream_page($key)
		{
			$page = $this->memcache->get($key);

			//FB::log("PageCache [KEY]: " . $key);
			//FB::log("PageCache [PAGE]: " . $page);

			if( is_bool($page) && $page == FALSE ) return false;
			if( empty($page) ) return false;

			/**
			* Print the page data
			**/
			print $page;

			return true;
		}
	}
?>