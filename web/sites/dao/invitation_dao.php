<?php
fast_require('Redis', get_framework_common_directory() . '/redis.php');
fast_require('DAO', get_dao_directory() . '/dao.php');
fast_require('Logger', get_framework_common_directory() . '/logger.php');

class InvitationDAO extends DAO
{

    function friend_get_invitations_count($username)
    {
        $query = "SELECT COUNT(*) FROM pendingcontact WHERE username=?";

        $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
        $stmt->bind_param("s", $username);
        $stmt->execute();

        $stmt->bind_result($count);
        $stmt->fetch();
        $stmt->close();
        $this->closeSlaveConnection();

        settype($count, "integer");

        return $count;
    }

	/**
	 * Get unique invite_hash key
	 *
	 * @param string &$invite_hash overwrite $invite_hash if a unique invite_hash is found
	 * @param Predis_Client $redis_instance
	 * @return string full invite_hash_key including keyspace
	 */
    public function get_invite_hash_key(&$invite_hash, Predis_Client $redis_instance)
	{
		//for Emails, 32 chars
		if(strlen($invite_hash) == 32)
			return Redis::KEYSPACE_EXTERNAL_INVITE . $invite_hash;

		//for SMS, 8 chars
		$_keys = $redis_instance->keys(Redis::KEYSPACE_EXTERNAL_INVITE . $invite_hash . '*');

		//filter non-SMS
		if( count($_keys) > 1 )
		{
			$_values = $redis_instance->mget($_keys);
			foreach($_keys as $i => $_key)
			{
				$_values[$i] = json_decode($_values[$i], true);
				if($_values[$i]['type'] != 'sms')
					unset($_keys[$i], $_values[$i]);
			}
			$_keys = array_values($_keys);
		}

		switch(count($_keys))
		{
			case 1:
				$invite_hash = str_replace(Redis::KEYSPACE_EXTERNAL_INVITE, '', $_keys[0]);
				return $_keys[0];
				break;
			case 0:
				// INVITATION HASH NOT FOUND
				Logger::getLogger('external.invites.errors')->warn(sprintf(
					'Invitation hash not found: %s'
					, $invite_hash
				));
				break;
			default:
				// INVITATION HASH COLLISION FOUND
				Logger::getLogger('external.invites.errors')->error(sprintf(
					'Hash collision found: %s (%d collisions)'
					, $invite_hash
					, count($_keys)
				));
				break;
		}

		return null;
	}

	/**
	 * Get invite hash data from redis
	 *
	 * @param string $invite_hash overwrite from get_invite_hash_key
	 * @return array|null $invite_data
	 * @example array(
	 *			'inviter_id' => $user_id,
	 *			'username'   => $session_user,
	 *			'inviter_name' => $name,
	 *			'invitee_id' => $invitee_id,
	 *			'app_id'     => $app_id,
	 *			'game_name'  => $game_name,
	 *			'type'       => $type
	 *			)
	 * @uses setcookit(invite_hash)
	 * @uses set_value(invite_hash)
	 */
	public function get_invite_hash_data(&$invite_hash)
	{
		// connect to the redis games instance
		try
		{
			$redis_instance = Redis::get_slave_instance_for_games();
		}
		catch (Exception $re)
		{
			return array('error' => 'Unable to get redis instance.');
		}

		//get hash key
		try
		{
			$invite_hash_key = $this->get_invite_hash_key($invite_hash, $redis_instance);
			setcookie('invite_hash', $invite_hash, 0, '/', '', '', true);
			set_value(get_field_name('invite_hash'), $invite_hash);
		}
		catch (Exception $re)
		{
			$redis_instance->disconnect();
			return array('error' => 'Unable to retrieve invite key.');
		}

		if( empty($invite_hash_key) )
		{
			$redis_instance->disconnect();
			return null;
		}

		//get invite data
		try
		{
			$invite_data = json_decode($redis_instance->get($invite_hash_key), true);
		}
		catch (Exception $re)
		{
			$redis_instance->disconnect();
			return array('error' => 'Unable to retrieve invite data.');
		}

		$redis_instance->disconnect();

		if( empty($invite_data) )
		{
			// INVITATION HASH NOT FOUND
			Logger::getLogger('external.invites.errors')->warn(sprintf(
				'Invitation hash not found: %s'
				, $invite_hash
			));
		}

		if($invite_data['type'] == 'sms')
			$invite_data['invitee_id'] = preg_replace('/[^\d]/','',$invite_data['invitee_id']);

		return $invite_data;
	}

	/**
	 * Delete invite hash data from Redis
	 *
	 * @param string $invite_hash
	 * @return null|array error if any
	 */
	public function delete_invite_hash_data($invite_hash)
	{
		//delete cookie
		setcookie('invite_hash', '', time()-60*60*24*30, '/', '', '', true);
		set_value(get_field_name('invite_hash'), null);

		// connect to the redis games instance
		try
		{
			$redis_instance = Redis::get_master_instance_for_games();
		}
		catch (Exception $re)
		{
			return array('error' => 'Unable to get redis instance.');
		}

		//get hash key
		try
		{
			$invite_hash_key = $this->get_invite_hash_key($invite_hash, $redis_instance);
		}
		catch (Exception $re)
		{
			return array('error' => 'Unable to retrieve invite key.');
		}

		//delete hash key
		try
		{
			if(!empty($invite_hash_key))
				$redis_instance->del($invite_hash_key);
		}
		catch (Exception $re)
		{
			return array('error' => 'Unable to delete invite data');
		}

		$redis_instance->disconnect();
	}

	public function update_userreferral($invite_data, $register_user)
	{
		/**
			register_user
				balance: 0.0
				password: ten20304050
				emailAlertSent: 0
				allowBuzz: 1
				registrationIPAddress: 10.3.3.66
				messageSetting: EVERYONE
				type: MIG33
				username: chernjie21
				currency: SGD
				fundedBalance: 0.0
				emailActivated: 0
				registrationDevice: Wap
				countryID: 199
				failedActivationAttempts: 0
				mobileVerified: 0
				failedLoginAttempts: 0
				emailAlert: 0
				dateRegistered: 1307599213
				anonymousCallSetting: ENABLED
				chatRoomBans: 0
				verificationCode: 29818
				status: ACTIVE
				language: ENG
				mobilePhone: 6584500141
				userID: 669
				chatRoomAdmin: 0
		/**/
		if($invite_data['type'] == 'sms'
		&& $invite_data['invitee_id'] == $register_user['mobilePhone'] )
		{
			// this is currently handled by fusion, so no work is required
			return null;
		}

		if($invite_data['type'] == 'sms') // registered number != invited number
		{
			Logger::getLogger('external.invites.errors')->info(sprintf(
				'New user (%s) registered with number (%s) instead of invited number (%s)'
				, $register_user['username']
				, $register_user['mobilePhone']
				, $invite_data['invitee_id']
			));
			return null; //do not give credit if user register with a different number;
		}

		$query = 'SELECT * FROM userreferral WHERE username=? AND mobilephone=?';
		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param('ss', $invite_data['username'], $register_user['mobilePhone']);
		$stmt->execute();
		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
		$rows = array(); while($stmt->fetch()) $rows[] = $row;
		$stmt->close();
		$this->closeSlaveConnection();

		if(count($rows))
		{
			Logger::getLogger('external.invites.errors')->info(sprintf(
				'User referral entry exist for %s (%s) on %s %s: %s. %s: %s'
				, $register_user['username']
				, $register_user['mobilePhone']
				, $rows['DateCreated']//TODO: test required
				, $rows['Paid']?'Paid':'Unpaid'//TODO: test required
				, $rows['Amount']//TODO: test required
				, $invite_data['type']
				, $invite_data['invitee_id']
			));
			return $rows;
		}
		else
		{
			return $rows;//soap_call_ejb($function, $parameters)
			/**
			$inviter_username = $invite_data['username'];
			$referrername = $invite_data['inviter_name'];
			$datecreated = gmdate('Y-m-d H:i:s', $register_user['dateRegistered']);
			$mobilephone = $register_user['mobilePhone'];
			$amount = 0.00;//TODO: referralCredit
			$paid = 0;

			$query = 'INSERT INTO userreferral
						(username, referrername, datecreated, mobilephone, amount, paid)
						VALUES (?, ?, ?, ?, ?, ?)
						ON DUPLICATE KEY
						UPDATE referrername = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param(
				'ssssdi'
				, $inviter_username
				, $referrername
				, $datecreated
				, $mobilephone
				, $amount
				, $paid
				, $referrername
			);
			$stmt->execute();

			if ($stmt->affected_rows > 1)
			{
				$this->getMasterConnection()->rollback();
			}
			else
			{
				$this->getMasterConnection()->commit();
			}

			$stmt->close();
			$this->closeMasterConnection();
			/**/
		}
	}

	/** =======================================================================
	 * JIRA-1311 Invitations system update
	 */

	/**
	 * Response type for FusionRest::KEYSPACE_SENT_INVITATION
	 * @see com.projectgoth.fusion.invitation.InvitationUtils.SendInvitationResultEnum
	 */
	const SUCCESS_SEND_INVITATION_TO_EXTERNAL_USER = 1;
	const SEND_FOLLOWING_ME_REQUEST_TO_EXISTING_USER = 2;
	const NOTHING_HAPPENS_TO_HALFWAY_REGISTERED_USER = 3;
	const FAILED_TO_SEND_INVITATION_TO_DESTINATION = -1;
	const INVALID_DESTINATION = -2;
	public static $response_message = array(
		  self::SUCCESS_SEND_INVITATION_TO_EXTERNAL_USER => 'Invitations successfully sent.'
		, self::SEND_FOLLOWING_ME_REQUEST_TO_EXISTING_USER => 'User has already registered, follow request sent.'
		, self::NOTHING_HAPPENS_TO_HALFWAY_REGISTERED_USER => 'User has already registered, pending verification.'
		, self::FAILED_TO_SEND_INVITATION_TO_DESTINATION => 'Failed to send invitation.'
		, self::INVALID_DESTINATION => 'Invalid email, please try again.'
	);

	/**
	 * Channel Type
	 * @see com.projectgoth.fusion.invitation.InvitationData.ChannelType
	 */
	const ChannelType_EMAIL = 1;
	const ChannelType_SMS = 2;
	const ChannelType_FACEBOOK = 3;

	/**
	 * Activity Type
	 * @see com.projectgoth.fusion.invitation.InvitationData.ActivityType
	 */
	const ActivityType_JOIN_MIG33 = 1;
	const ActivityType_BE_MY_FRIEND = 2;
	const ActivityType_PLAY_A_GAME = 3;

	/**
	 * @param array $emails
	 * @throws Exception should immediately skip all other tasks
	 * @return array(map of email to SendInvitationResult)
	 */
	public function send_email_invite($emails)
	{
		$return = $destinations = array();

		// removes duplicates
		$emails = array_unique($emails);

		// Validate email destinations
		fast_require('CommonValidator', get_library_directory() . '/validator/common.php');
		$common_validator = new CommonValidator();
		foreach ($emails as $email)
		{
			$email = trim($email);
			if (empty($email)) continue;
			if ($common_validator->check_email($email))
			{
				$destinations[] = $email;
			}
			else
			{
				// we return the list of invalid emails destinations if they exists
				$return[$email] = self::INVALID_DESTINATION;
			}
		}

		// destinations empty or no valid destinations
		if (empty($destinations)) return $return;

		// Backend datasvc call
		if (SystemProperty::get_instance()->get_boolean(SystemProperty::Invitation_ReferralInvitationEnabled, FALSE))
		{
			$return += $this->send_email_invite_via_fusion_rest($destinations);
		}
		else
		{
			$result = $this->send_email_invite_via_migbo_datasvc($destinations);
			foreach ($destinations as $email)
			{
				$return[$email] = empty($result)
					? self::FAILED_TO_SEND_INVITATION_TO_DESTINATION
					: self::SUCCESS_SEND_INVITATION_TO_EXTERNAL_USER;
			}
		}

		return $return;
	}

	/**
	 * @param array $destinations
	 * @throws Exception
	 * @return array map of emails to InvitationDAO::$response_message keys
	 */
	private function send_email_invite_via_fusion_rest($destinations)
	{
		fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');
		return FusionRest::get_instance()->json_post(
			  sprintf(FusionRest::KEYSPACE_SENT_INVITATION, SessionUtilities::$session_user_id)
			, array(
				'data' => array(
					  'channel' => self::ChannelType_EMAIL
					, 'type' => self::ActivityType_JOIN_MIG33
					, 'destinations' => $destinations
				)
			)
		);
	}

	/**
	 * @param string $facebookRequestId
	 * @param array $destinations
	 * @throws Exception
	 * @return array map of FBIDs to InvitationDAO::$response_message keys
	 */
	public function log_facebook_invite_via_fusion_rest($facebookRequestId, $destinations)
	{
		fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');
		return FusionRest::get_instance()->json_post(
			  sprintf(FusionRest::KEYSPACE_SENT_INVITATION, SessionUtilities::$session_user_id)
			, array(
				'data' => array(
					  'channel'            => self::ChannelType_FACEBOOK
					, 'type'               => self::ActivityType_JOIN_MIG33
					, 'destinations'       => $destinations
					, 'invitationMetadata' => array(
							'facebookRequestId' => $facebookRequestId
						)
				)
			)
		);
	}

	/**
	 * @deprecated controlled by SystemProperty::Invitation_ReferralInvitationEnabled
	 * @param array $destinations
	 * @return boolean
	 */
	private function send_email_invite_via_migbo_datasvc($destinations)
	{
		fast_require('MigboDatasvc', get_library_directory() . '/fusion/migbo_datasvc.php');
		$query_params = MigboDatasvc::get_instance()->get_session_params() + array('method' => '@email');

		try
		{
			$result = MigboDatasvc::get_instance()->json_post(
				  sprintf(MigboDataSvc::REFERRAL_INVITE, SessionUtilities::$session_user_id) . '?' . http_build_query($query_params)
				, array('destinations' => $destinations)
			);
			return $result == MigboDatasvc::OK;
		}
		catch (Exception $ex)
		{
			return false;
		}
	}
}
