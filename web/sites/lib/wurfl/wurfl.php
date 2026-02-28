<?php
	define("WURFL_DIR",  get_library_directory() . '/wurfl/library/');
	define("CONFIG_DIR", get_library_directory() . "/wurfl/config/");

	require_once(WURFL_DIR . "WURFLManagerProvider.php");

	class Wurfl
	{
		/**
		 * @var Wurfl
		 */
		static private $wurfl = NULL;
		static private $wurflManager;

		private function __construct()
		{
		}

		private function __clone()
		{
		}

		/**
		 * @return Wurfl
		 */
		static function get_instance()
		{
			if (self::$wurfl == NULL)
			{
				self::$wurfl = new Wurfl();

				$wurflConfigFile = CONFIG_DIR . 'wurfl-config.xml';
				$wurflConfig = new WURFL_Configuration_XmlConfig($wurflConfigFile);

				$wurflManagerFactory = new WURFL_WURFLManagerFactory($wurflConfig);
				self::$wurflManager = $wurflManagerFactory->create();
			}

			return self::$wurfl;
		}

		public function get_device($http_req)
		{
			return self::$wurflManager->getDeviceForHttpRequest($http_req);
		}

		public function get_wurfl_manager()
		{
			return self::$wurflManager;
		}


	}
?>