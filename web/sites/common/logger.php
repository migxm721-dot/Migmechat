<?php
	class Logger
	{
		const INFO  = 1;
		const WARN  = 2;
		const ERROR = 3;

		public static $log_type_strings = array();
		private static $loggers = array();

		private $logger_name;
		private $log_file_base;
		public $throw_on_error = false;

		/**
		 * @param string $logger_name
		 * @return Logger
		 */
		public static function getLogger($logger_name)
		{
			if (!isset(self::$loggers[$logger_name]))
			{
				self::$loggers[$logger_name] = new Logger($logger_name);
			}

			return self::$loggers[$logger_name];
		}

		private function __construct($logger_name)
		{
			global $apache_dir;

			$this->logger_name = $logger_name;

			// set up a generic file_name convention for date-based logging
			$this->log_file_base = $apache_dir . '/logs/'. self::get_clean_file_name($logger_name);
		}

		public function log($message, $severity=self::INFO)
		{
			// logic for file name by date
			$filename = $this->log_file_base . '.log.' . date('Ymd');

			$fp = @fopen($filename, 'a');
			if (!$fp)
			{
				if ($this->throw_on_error)
				{
					throw new Exception("Unable to log to $filename");
				}
				else
				{
					error_log("Unable to log to $filename");
					return false;
				}
			}

			fprintf($fp, "%s - %s - %s: %s\n"
				, getRemoteIPAddress()
				, date('c')
				, self::$log_type_strings[$severity]
				, $message
			);

			fclose($fp);
		}

		public function info($message)
		{
			return $this->log($message, self::INFO);
		}

		public function warn($message)
		{
			return $this->log($message, self::WARN);
		}

		public function error($message)
		{
			return $this->log($message, self::ERROR);
		}


		private static function get_clean_file_name($name)
		{
			// make the file filesystem friendly by replacing a few items
			$name = trim($name);
			$name = preg_replace('/\s+/', '_', $name);
			$name = preg_replace('/[^a-z0-9._-]/i', '', $name);

			return strtolower($name);
		}
	}

	Logger::$log_type_strings[Logger::INFO] = 'INFO';
	Logger::$log_type_strings[Logger::WARN] = 'WARN';
	Logger::$log_type_strings[Logger::ERROR] = 'ERROR';
?>