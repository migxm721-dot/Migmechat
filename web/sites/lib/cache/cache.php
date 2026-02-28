<?php
	class Cache
	{
		/**
		 * @var Cache
		 */
		private static $instance;

		private function __construct()
		{
		}

		private function __clone()
		{
		}

		private function get_file_time_difference($filename)
		{
			if( !file_exists($filename) )
			{
				return 0;
			}

			return abs(time()-filemtime($filename));
		}

		/**
		*
		* Get the singelton instance
		* @return Cache
		**/
		public static function get_instance()
		{
			if(!self::$instance)
			{
				self::$instance = new Cache();
			}
			return self::$instance;
		}

		/**
		*
		* Delete a cached file
		*
		**/
		public function delete_cached_file($filename)
		{
			if( file_exists($filename) )
				unlink($filename);
		}

		/**
		*
		* check if a file is cached
		*
		**/
		public function is_file_cached($filename, $cache_time_expire)
		{
			$diff = $this->get_file_time_difference($filename);
			if( $diff > $cache_time_expire || $diff == 0)
			{
				$this->delete_cached_file($filename);
				return false;
			}
			else
				return true;
		}

		public function is_file_older($filename1, $filename2)
		{
			return filemtime($filename1) < filemtime($filename2);
		}

		/**
		*
		* Create the directory structure if it doesn't exist
		*
		**/
		public function create_directory($directory)
		{
			$dirs = explode("/", $directory);
			$current_dir = "/";
			foreach( $dirs as $dir )
			{
				if( !empty($dir) )
				{
					$current_dir = $current_dir . $dir . "/";
					if(!file_exists($current_dir))
					{
						mkdir($current_dir);
					}
				}
			}
		}
	}
?>