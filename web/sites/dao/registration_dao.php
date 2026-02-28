<?php
	require_once(get_framework_common_directory() . "/database.php");
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');
	class CheckNameException extends Exception{}

	class RegistrationDAO extends DAO
	{
		protected $fusion_endpoint_available = true;
		protected $known_unavailables = array();
		protected $known_availables = array();

		public function is_username_available($username='')
		{
			if (empty($username)) return 0;
			$username = strtolower(strip_tags($username));
			$return = 0;
			try
			{
				$result = $this->is_username_available_in_memory($username);
				$return = ($result == true) ? 1 : 0;
			}
			catch (CheckNameException $ex)
			{
				if ($this->fusion_endpoint_available == false)
				{
					return $this->is_username_available_db_query($username);
				}
				try
				{
					$result = $this->is_username_available_on_server($username);
					if ($result == true)
					{
						$this->known_availables[$username] = $username;
					}
					else
					{
						$this->known_unavailables[$username] = $username;
					}
					$return = ($result == true) ? 1 : 0;
				}
				catch (Exception $ex)
				{
					$this->fusion_endpoint_available = false;
					return $this->is_username_available_db_query($username);
				}
			}
			return $return;
		}

		public function usernames_available(array $usernames = array())
		{
			if ($this->fusion_endpoint_available == false)
			{
				return $this->usernames_available_db_query($usernames);
			}
			$available_names = array();
			if (empty($usernames)) return $available_names;
			$usernames = array_map("strtolower", $usernames);
			$usernames = array_map("strip_tags", $usernames);
			$result = $this->usernames_available_in_memory($usernames);
			$available_names = $result['available'];
			$usernames = $result['remainder'];
			if (empty($usernames)) return $available_names;

			$first_name = $usernames[0];
			try
			{
				$result = $this->is_username_available_on_server($first_name);
				if ($result == true)
				{
					$available_names[] = $first_name;
					$this->known_availables[$first_name] = $first_name;
				}
				else
				{
					$this->known_unavailables[$first_name] = $first_name;
				}
				array_shift($usernames);
				if (!empty($usernames))
				{
					foreach($usernames as $username)
					{
						if ($this->is_username_available($username) == 1)
						{
							$available_names[] = $username;
						}
					}
				}
				return $available_names;
			}
			catch (Exception $ex)
			{
				$this->fusion_endpoint_available = false;
				return $this->usernames_available_db_query($usernames);
			}
		}

		private function is_username_available_in_memory($username)
		{
			if (isset($this->known_availables[$username])) return true;
			if (isset($this->known_unavailables[$username])) return false;
			throw new CheckNameException("Not available in memory");
		}

		private function is_username_available_on_server($username)
		{
			$result = FusionRest::get_instance()->get(sprintf(FusionRest::KEYSPACE_REG_TOKEN_USERNAME_CHECK, urlencode($username)));
			if (!isset($result['value']))
			{
				throw new Mig33apiHttpException("Server endpoint unavailable.");
			}
			return (bool) $result['value'];
		}

		private function is_username_available_db_query($username)
		{
			$username = strtolower(strip_tags($username));
			
			$query = 'SELECT COUNT(*) FROM user WHERE username = ?';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();
			$stmt->bind_result($exists);
			$stmt->fetch();
			$stmt->close();
			if ($exists == 0)
			{
				//it's faster to check one query at a time, then to lock up the whole table
				//SELECT COUNT(*) FROM user INNER JOIN useralias ON user.username = useralias.username WHERE user.username = ? OR useralias.alias = ?
				$query = 'SELECT COUNT(*) FROM useralias WHERE alias = ?';
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("s", $username);
				$stmt->execute();
				$stmt->bind_result($exists);
				$stmt->fetch();
				$stmt->close();
			}
			$this->closeSlaveConnection();
			return ($exists == 1 ? 0 : 1);
		}

		protected function usernames_available_in_memory($usernames)
		{
			$return = array(
				  'available' => array()
				, 'remainder' => $usernames
			);
			$return['remainder'] = array_diff($return['remainder'], $this->known_unavailables);
			if (empty($return['remainder']))
			{
				return $return;
			}
			else
			{
				$return['available'] = array_intersect($return['remainder'], $this->known_availables);
				if (!empty($return['available']))
				{
					$return['remainder'] = array_diff($return['remainder'], $return['available']);
				}
			}
			return $return;
		}

		private function usernames_available_db_query($usernames)
		{
			$usernames = array_map("strtolower", $usernames);
			$usernames = array_map("strip_tags", $usernames);
			$usernames_escaped = $usernames;
			foreach($usernames_escaped as &$username)
			{
				$username = $this->getSlaveConnection()->escape_string($username);
			}
			$query = sprintf("SELECT username FROM user WHERE username IN('%s')", implode("','", $usernames_escaped));
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$usernames_taken = array();
			while ($stmt->fetch())
			{
				$usernames_taken[] = get_value_from_array("username", $row, "string", "");
			}
			$query = sprintf("SELECT alias FROM useralias WHERE alias IN('%s')", implode("','", $usernames_escaped));
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$useralias_taken = array();
			while ($stmt->fetch())
			{
				$useralias_taken[] = get_value_from_array("alias", $row, "string", "");
			}
			$this->closeSlaveConnection();
			return array_diff($usernames, $usernames_taken, $useralias_taken);
		}

		public function suggest_usernames($username, $num_usernames)
		{
			$usernames = $this->generate_usernames($username);
			$valid_usernames = array();
			foreach($usernames as $username)
			{
				if ($this->is_username_available($username) == 1)
				{
					$valid_usernames[] = $username;
				}
				if (count($valid_usernames) == $num_usernames)
				{
					break;
				}
			}
			return array_slice($usernames, 0, $num_usernames);
		}

		private function generate_usernames($username, $length_to_pad=2, $char_to_append=array('', '-', '.', '_'))
		{
			$suggested_usernames_array = array();

			for($j = 0; $j < sizeof($char_to_append); $j++)
			{
				// Pad Numbers
				for($i = 0; $i < 10; $i++)
				{
					$suggested_username = $username.$char_to_append[$j].mt_rand(pow(10, ($length_to_pad - 1)), (pow(10, $length_to_pad) - 1));
					if(!in_array($suggested_username, $suggested_usernames_array)) {
						$suggested_usernames_array[] = $suggested_username;
					}
				}
			}

			return $suggested_usernames_array;
		}

		public function generate_mobile_phone()
		{
			$query = "SELECT CASE WHEN MAX(mobilephone) IS NULL THEN 11000000000
							 ELSE MAX(CAST(mobilephone AS UNSIGNED)) + 1 END mobilephone
					  FROM user
					  WHERE mobilephone REGEXP '^11[0-9]{9}$'";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $data['mobilephone'];

		}

		public function create_staging_user($user)
		{
			$sp_call = "CALL spCreateUser(?, ?, ?, ?, ?, ?, ?, ?)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($sp_call);
			$stmt->bind_param('sssisssi'
				, $user['username']
				, $user['password']
				, $user['mobilePhone']
				, $user['type']
				, $user['registrationIPAddress']
				, $user['registrationDevice']
				, $user['userAgent']
				, $user['countryID']
			);
			$stmt->execute();
			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);
			$stmt->fetch();
			$stmt->close();
			$this->closeMasterConnection();

			return $data;
		}
	}
?>