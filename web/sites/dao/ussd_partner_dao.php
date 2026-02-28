<?php

	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require('DAO', get_dao_directory().'/dao.php');
	fast_require('USSDPartner', get_domain_directory().'/ussd/ussd_partner.php');
	fast_require('USSDPartnerUser', get_domain_directory().'/ussd/ussd_partner_user.php');


	class USSDPartnerDAO extends DAO
	{

		public function get_partner_by_app_key($app_key)
		{
			$memcache = Memcached::get_instance();
			$key = Memcached::$KEYSPACE_USSD_PARTNER."/".$app_key;

			$data = $memcache->get($key);
			if(empty($data))
			{
				$query = 'SELECT *
					  FROM ussdpartner
					  WHERE appkey = ?';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param('s', $app_key);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

				if(! $stmt->fetch())
					return null;
				$memcache->add_or_update($key, json_encode($data), Memcached::$CACHEDURATION_USSD_PARTNER);
			}
			else
			{
				$data = json_decode($data, true);
			}

            return new USSDPartner($data);
		}

		public function get_partner_user($partner_id, $msisdn)
		{
			$query = 'SELECT ui.id, ui.username, mobilephone, status, IFNULL(ue.emailaddress, "") emailaddress
					  FROM   ussdpartner up
					       , ussdpartneruser upu
					  	   , user u
					  	   , userid ui LEFT JOIN useremailaddress ue ON ui.id = ue.userid
					  WHERE up.id = upu.ussdpartnerid
					  AND upu.userid = ui.id
					  AND u.username = ui.username
					  AND up.id = ?
					  AND mobilephone = ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ss', $partner_id, $msisdn);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if(! $stmt->fetch())
				return null;

			return new USSDPartnerUser($data);
		}

		public function check_user_collisions($mobilephone, $email)
		{
			$query = '
            (
                SELECT ui.id, ui.username, mobilephone, status, NULL
                FROM user u, userid ui
                WHERE u.username = ui.username
                AND u.mobilephone = ?
            )
            UNION
            (
                SELECT ui.id, ui.username, mobilephone, status, ue.emailaddress
                FROM user u, userid ui, useremailaddress ue
                WHERE
                    u.username = ui.username
                AND ui.id = ue.userid
                AND ue.emailaddress = ?
             )';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ss', $mobilephone, $email);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$collisions = array();

			while($stmt->fetch())
			{
				if (0 === strcasecmp($email, $data['emailaddress']) && 0 === strcasecmp($mobilephone, $data['mobilephone']))
				{
					$collisions['both'] = new USSDPartnerUser($data);
				}
				else if(0 === strcasecmp($email, $data['emailaddress']))
				{
					$collisions['emailaddress'] = new USSDPartnerUser($data);
				}
				else
				{
					$collisions['mobilephone']  = new USSDPartnerUser($data);
				}

			}

			$stmt->close();

			return $collisions;

		}

		public function hijack_mobile_phone($new_owner, $mobile_phone)
		{

			// get user details
			$query = 'SELECT username FROM user WHERE mobilephone = ?';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $mobile_phone);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if(! $stmt->fetch())
			{
				$stmt->close();
				$this->closeSlaveConnection();
				return 0;
			}

			$old_owner = $data['username'];
			$notes = sprintf("Account disabled and mobile number hijacked through USSD API by %s on %s", $new_owner, date("Y-m-d H:i:s"));

			// update user table
			$query = 'UPDATE user SET status = 0, mobileverified = 0, mobilephone = NULL, notes = CONCAT_WS(";", notes, ?) WHERE username = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param(   'ss'
			                   , $notes
			                   , $old_owner);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return 0;
			}

			// insert into ussdpartnermobiletransfer
			$query = 'INSERT INTO ussdpartnermobiletransfer(originalowner, newowner, originalnumber, datecreated)
					  VALUES(?,?,?,NOW())';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('sss', $old_owner, $new_owner, $mobile_phone);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return 0;
			}

			$hijack_id = $stmt->insert_id;
			$stmt->close();
			$this->closeMasterConnection();

			return $hijack_id;
		}

		public function hijack_mobile_phone_rollback($username, $mobile_phone, $status, $mobile_verified)
		{
			// update user table
			$notes = sprintf("USSD hijacking rolled back on %s", date("Y-m-d H:i:s"));
			$query = 'UPDATE user SET mobilephone = ?, status = ?, mobileverified = ?, notes = CONCAT_WS(";", notes, ?)  WHERE username = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param(   'siiss'
			                   , $mobile_phone
			                   , $status
			                   , $mobile_verified
			                   , $notes
			                   , $username);
			$stmt->execute();

			// remove entry in ussdpartnermobiletransfer
			$query = 'DELETE FROM ussdpartnermobiletransfer WHERE originalnumber = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $mobile_phone);
			$stmt->execute();

			$stmt->close();
			$this->closeMasterConnection();

		}

	}

?>
