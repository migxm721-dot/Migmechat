<?php
	abstract class DatabaseConnection
	{
		protected $host = "";
		protected $user = "";
		protected $password = "";
		protected $database;
		protected $port = 3306;
		/**
		 * @var mysqli
		 */
		protected $connection = null;

		public function __construct($host, $dbname, $user, $password, $port=3306)
		{
			$this->host = $host;
			$this->user = $user;
			$this->password = $password;
			$this->database = $dbname;
			$this->port = $port;

			$this->connect();
		}

		public function __destruct()
		{
			$this->close_connection();
		}

		/**
		 * @return mysqli
		 */
		public function get_connection()
		{
			return $this->connection;
		}

		protected abstract function connect();

		public abstract function close_connection();

		public abstract function execute($query);

		public abstract function execute_one_row($query);

		public abstract function get_prepared_statement($query);

		public abstract function begin();

		public abstract function commit();

		public abstract function rollback();

		public abstract function last_errorno();

		public abstract function last_error();

		public abstract function last_error_both();
	}

	class MysqliConnection extends DatabaseConnection
	{
		protected function connect()
		{
			$timeToWait = 10;  // Seconds to wait for a database connection, if the database has reached its connection limit

			while ($timeToWait > 0) {
				$this->connection = mysqli_connect(
					            $this->host,
					            $this->user,
					            $this->password,
					            $this->database,
					            $this->port);

				$this->connection->set_charset("utf8"); // Set charset to UTF=8

				if (mysqli_connect_errno() == 0)
				{
					return;
				}
				else if (mysqli_connect_errno() == 1203)  // 1203 == ER_TOO_MANY_USER_CONNECTIONS (mysqld_error.h)
				{
					sleep(1);
					$timeToWait--;
					continue;
				}
				else
				{
					throw new Exception("Database connection failed: %s", mysqli_connect_error());
				}
			}

			throw new Exception("Database connection timed out");
		}

		public function close_connection()
		{
			if( $this->connection )
			{
				mysqli_close($this->connection);
				$this->connection = null;
			}
		}

		/**
		 * @param string $query
		 * @return mysqli_result
		 */
		public function execute($query)
		{
			return mysqli_query($this->connection, $query);
		}

		/**
		 * @param string $query
		 * @return array
		 */
		public function execute_one_row($query)
		{
			$result = $this->execute($query);
			$row = mysqli_fetch_assoc($result);

			mysqli_free_result($result);
			return $row;
		}

		/**
		 * Get a prepared statement
		 * @param string $query
		 * @return mysqli_stmt
		 */
		public function get_prepared_statement($query)
		{
			$stmt = $this->connection->prepare($query);

			if (false === $stmt)
			{
				error_log('ERROR: Unable to get prepared statement: ['.$this->last_error_both().'] for query ['.$query.']');
			}

			return $stmt; // we still return, even in case of errors, to maintain client code untouched
		}

		public function begin()
		{
			$this->connection->autocommit(false);
		}

		public function commit()
		{
			$this->connection->commit();
		}

		public function rollback()
		{
			$this->connection->rollback();
		}

		public function autocommit($mode = true)
		{
			$this->connection->autocommit($mode);
		}

		public function stmt_bind_assoc(&$stmt, &$out)
		{
			$data = mysqli_stmt_result_metadata($stmt);
			$fields = array();
			$out = array();

			$fields[0] = $stmt;
			$count = 1;

			while($field = mysqli_fetch_field($data))
			{
				$fields[$count] = &$out[$field->name];
				$count++;
			}
			call_user_func_array('mysqli_stmt_bind_result', $fields);
		}

		/**
		 * @param string $string
		 * @return string
		 */
		public function escape_string($string)
		{
			return mysqli_real_escape_string($this->connection, $string);
		}

		/**
		 * @param string $query
		 * @return boolean
		 */
		public function multi_query($query)
		{
			return mysqli_multi_query($this->connection, $query);
		}

		/**
		 * @return mysqli_result
		 */
		public function store_result()
		{
			return mysqli_store_result($this->connection);
		}
		/**
		 * @return boolean
		 */
		public function next_result()
		{
			return mysqli_next_result($this->connection);
		}

		/**
		 * @return integer
		 */
		public function last_errorno()
		{
			return mysqli_errno($this->connection);
		}

		/**
		 * @return string
		 */
		public function last_error()
		{
			return mysqli_error($this->connection);
		}

		/**
		 * @return string
		 */
		public function last_error_both()
		{
			return sprintf("%d: %s", $this->last_errorno(), $this->last_error());
		}
	}
	/*
	class MysqlPDOConnection extends DatabaseConnection
	{
		protected function connect()
		{
			try
			{
				$this->connection = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->database, $this->user, $this->password);
			}
			catch(PDOException $e)
			{
			}
		}

		public function close_connection()
		{
			$this->connection = null;
		}

		public function begin()
		{
			$this->connection->beginTransaction();
		}

		public function commit()
		{
			$this->connection->commit();
		}

		public function rollback()
		{
			$this->connection->rollback();
		}

		public function execute($query)
		{
			return $this->connection->exec($query);
		}

		public function get_prepared_statement($query)
		{
			return $this->connection->prepare($query);
		}

		public function bind_param($statement, $position, $value, $type="string")
		{
			switch(strtolower($type))
			{
				case "int":
					$statement->bindParam($position, $value, PDO::PARAM_INT);
					break;
				default:
					$statement->bindParam($position, $value, PDO::PARAM_STR);
					break;
			}
		}
	}
	*/
	class DatabaseManager
	{
		/**
		 * @var array MysqliConnection
		 */
		private $database_connections = array();

		/**
		 * @var DatabaseManager
		 */
		private static $instance;

		private function __construct()
		{
		}

		public function __destruct()
		{
			/*
			foreach($this->database_connections as $ind=>$key)
			{
				if(isset($key))
				{
					$key->close_connection();
				}
			}
			*/
		}

		/**
		 * Return the DatabaseManager singleton
		 * @static
		 * @return DatabaseManager
		 */
		public static function get_instance()
	    {
    	    if (!self::$instance)
        	{
            	self::$instance = new DatabaseManager();
	        }

    	    return self::$instance;
	    }

		/**
		 * Register new DB connection
		 * @param string $name
		 * @param string $host
		 * @param string $user
		 * @param string $password
		 * @param string $database_name
		 * @param int $port
		 * @return MysqliConnection
		 */
	    public function register_database($name, $host, $user, $password, $database_name, $port=3306)
	    {
			if( !array_key_exists($name, $this->database_connections) || is_null($this->database_connections[$name]))
			{
				$connection = new MysqliConnection($host, $database_name, $user, $password, $port);
				$this->database_connections[$name] = $connection;
			}

			return $this->database_connections[$name];
	    }

	    /**
	     * @param string $name
	     * @return void
	     */
	    public function deregister_database($name)
	    {
	    	if( array_key_exists($name, $this->database_connections) )
	    	{
	    		$connection = $this->database_connections[$name];
	    		$connection->close_connection();
	    		$this->database_connections[$name] = null;
	    		unset($this->database_connections[$name]);
	    	}
	    }

		/**
		 * Get connection of type $name
		 * @param string $name
		 * @return MysqliConnection
		 * @throws Exception Unable to find database connection
		 */
	    public function get_connection($name)
	    {
	    	if( !array_key_exists($name, $this->database_connections) )
	    	{
	    		throw new Exception("Unable to find database connection ".$name);
	    	}

	    	return $this->database_connections[$name];
	    }
	}
?>