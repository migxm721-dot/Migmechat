<?php
	require_once(get_common_config_location());
	require_once(get_framework_common_directory()."/database.php");

	class DAO
	{
		/**
		 * @var array MysqliConnection
		 */
		protected $connections;

		public function __construct()
		{
			$this->connections = array();
		}

		public function __destruct()
		{
			foreach ($this->connections as $name => $conn)
				$this->closeConnection($name);
		}

		/**
		 * Gets the Master DB connection
		 * @return MysqliConnection
		 */
		protected function getMasterConnection()
		{
			return $this->getConnection("master");
		}

		/**
		 * Gets one of the Slave DB connection
		 * @return MysqliConnection
		 */
		protected function getSlaveConnection()
		{
			return $this->getConnection("slave");
		}

		/**
		 * Returns the DB connection
		 * @global array $db_settings
		 * @param string $name
		 * @return MysqliConnection
		 */
		protected function getConnection($name)
		{
			global $db_settings;

			if (!isset($this->connections[$name]) || $this->connections[$name] == null)
			{
				$setting = $db_settings[$name];

				DatabaseManager::get_instance()->register_database($name, $setting["host"], $setting["username"], $setting["password"], $setting["database"]);
				$this->connections[$name] = DatabaseManager::get_instance()->get_connection($name);
			}

			return $this->connections[$name];
		}

		protected function closeMasterConnection()
		{
			$this->closeConnection("master");
		}

		protected function closeSlaveConnection()
		{
			$this->closeConnection("slave");
		}

		protected function closeConnection($name)
		{
			DatabaseManager::get_instance()->deregister_database($name);
			unset($this->connections[$name]);
		}

		/**
		 * Returns a Prepared Statement
		 * @param MysqliConnection $connection
		 * @param string $query
		 * @return mysqli_stmt
		 */
		protected function get_prepared_statement($connection, $query)
		{
			return $connection->get_prepared_statement($query);
		}

		protected function execute($connection, $query)
		{
			return $connection->execute($query);
		}

		protected function execute_one_row($connection, $query)
		{
			return $connection->execute_one_row($query);
		}

		public static $memorization_cache = array(
			  'userid' => array(
				  'hit'  => 0
				, 'miss' => 0
			)
			, 'username' => array(
				  'hit'  => 0
				, 'miss' => 0
			)
			, 'userdetail' => array(
				  'hit'  => 0
				, 'miss' => 0
			)
		);
		protected static $userids = array();
		/**
		 * Below are some common database lookups used by subclasses
		 * @param string $username
		 * @return int
		 */
		public function get_userid($username)
		{
			if (isset(self::$userids[$username]))
			{
				self::$memorization_cache['userid']['hit']++;
				return self::$userids[$username];
			}
			self::$memorization_cache['userid']['miss']++;

			$key = Memcached::$KEYSPACE_USERID.$username;
			$memcache = Memcached::get_instance();
			$id = $memcache->get($key);

			if (empty($id))
			{
				$query = "SELECT id FROM userid WHERE username=? LIMIT 1";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("s", $username);
				$stmt->execute();

				$stmt->bind_result($id);
				$stmt->fetch();
				$stmt->close();
				$this->closeSlaveConnection();

				$memcache->add_or_update($key, $id);
			}

			self::$userids[$username] = (int) $id;
			return self::$userids[$username];
		}

		protected static $usernames = array();
		// This method is used while expecting a valid user
		// attempts to retrieve non-valid users will return false
		public function get_username($userid)
		{
			if (isset(self::$usernames[$userid]))
			{
				self::$memorization_cache['username']['hit']++;
				return self::$usernames[$userid];
			}
			self::$memorization_cache['username']['miss']++;

			$key = Memcached::$KEYSPACE_USERNAME.$userid;

			$memcache = Memcached::get_instance();
			$uname = $memcache->get($key);

			if (false === $uname || is_null($uname))
			{
				$query = "SELECT username FROM userid WHERE id=? LIMIT 1";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("s", $userid);
				$stmt->execute();

				$stmt->bind_result($uname);
				$res = $stmt->fetch();

				$stmt->close();
				$this->closeSlaveConnection();

				if ( $res )
				{
					$memcache->add_or_update($key, $uname);
				}
				elseif( is_null($res) )
				{
					// invalid user id
					// we cache that as well but for a much shorter time (2 hours)
					// to prevent short repeated DB hits for the same invalid user id
					$uname = 0;
					$memcache->add_or_update($key, $uname, 7200); // 2 hours
				}
				else
				{
					// DB error, throw exception
					throw new Exception("Unable to retrieve username for userid ($userid)");
				}
			}

			self::$usernames[$userid] = (0 === $uname ? false : $uname);
			return self::$usernames[$userid];
		}

		public function users_are_friends($username1, $username2)
		{
			if ($username1 === $username2)
				return true;

			$query = "SELECT COUNT(*) FROM broadcastlist WHERE username=? AND broadcastusername=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ss", $username1, $username2);
			$stmt->execute();

			$stmt->bind_result($friends);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $friends == 1;
		}

		protected function user_is_member_of_group($username, $group_id)
		{
			$query = "SELECT COUNT(*) FROM groupmember WHERE username=? AND groupid=? AND status=1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $username, $group_id);
			$stmt->execute();

			$stmt->bind_result($member);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $member == 1;
		}

		protected function user_is_admin_of_group($username, $group_id)
		{
			// 2 = Owner (Automatically)
			// 3 = Moderator
			$query = 'SELECT type FROM groupmember WHERE username = ? AND groupid = ? AND status = 1 AND type IN(2,3)';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('si', $username, $group_id);
			$stmt->execute();

			$stmt->bind_result($admin);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			if(empty($admin))
				return false;
			else
				return $admin;
		}

		protected function user_is_owner_of_group($username, $group_id)
		{
			$query = 'SELECT COUNT(*) FROM groups WHERE createdby = ? AND id = ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('si', $username, $group_id);
			$stmt->execute();

			$stmt->bind_result($owner);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $owner == 1;
		}

		public function get_exchange_rate($to_currency, $from_currency = null)
		{
			$key = 'ExchangeRate/'.$to_currency;

			if ($from_currency != null)
				$key .= "/".$from_currency;

			$memcache = Memcached::get_instance();
			$exchange_rate = $memcache->get($key);

			if (empty($exchange_rate))
			{
				$query = "SELECT exchangerate FROM currency WHERE code = ?";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("s", $to_currency);
				$stmt->execute();
				$stmt->bind_result($exchange_rate);
				$stmt->fetch();

				if ($from_currency != null)
				{
					$stmt->bind_param("s", $from_currency);
					$stmt->execute();
					$stmt->bind_result($from_exchange_rate);
					$stmt->fetch();

					$exchange_rate /= $from_exchange_rate;
				}

				$stmt->close();
				$this->closeSlaveConnection();

				$memcache->add_or_update($key, $exchange_rate, 600);
			}

			return $exchange_rate;
		}

		protected function user_can_view_wall($username_viewing, $username_being_viewed)
		{
			if ($username_viewing === $username_being_viewed)
				return true;

			// Check the privacy setting of the profile first.
			// Public (1) -> Anyone can view the wall
			// Friends Only (2) -> Only $username_being_viewed's friends can view the wall
			// Private (3) -> No one can view the wall
			$query = "SELECT status FROM userprofile WHERE username=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username_being_viewed);
			$stmt->execute();

			$stmt->bind_result($profile_status);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			if ($profile_status == 1)
				return true;
			else if ($profile_status == 3)
				return false;

			// Profile is Friends Only
			return $this->users_are_friends($username_viewing, $username_being_viewed);
		}

		protected function auto_bind_params($stmt, $params_arr)
		{
			$params = array('');
			foreach($params_arr as &$param_arr)
			{
				foreach($param_arr as $type => $param)
				{
					$params[0] .= $type;
					$params[] = &$param_arr[$type];
				}
			}
			call_user_func_array(array($stmt, "bind_param"), $params);
		}
	}
