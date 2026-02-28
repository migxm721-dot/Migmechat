<?php
fast_require('DAO', get_dao_directory() . '/dao.php');
fast_require('SessionUtilities', get_framework_common_directory() . '/session_utilities.php');
fast_require('IceDomain', get_domain_directory() . '/common/ice_domain.php');

class IceDAOException extends Exception {}


class IceDAO extends DAO
{
	const CHAT_TYPE_PRIVATE = 1;
	const CHAT_TYPE_GROUP = 2;
	const CHAT_LIST_VERSION = 0;
	const NULL_FUSION_BYTE = -128;

	public static $ICE;            //Ice_Communicator IcePHP_Communicator

	private static $MIN_TIMESTAMP_VALUE;

	private static $connectionPrx; //Ice_ObjectPrx
	private static $userPrx;       //Ice_ObjectPrx
	private static $proxies = array();


	protected $registry;
	protected $eventstore;
	protected $authenticationservice;
	protected $registryadmin;
	protected $usernotificationservice;

	public function __construct()
	{		

		parent::__construct();
		self::loadProfile();

		// Make it compatible with Java
		self::$MIN_TIMESTAMP_VALUE = pow(-2, 63); 

		$this->registry                = new IceDomain('registry');
		$this->eventstore              = new IceDomain('eventstore');
		$this->authenticationservice   = new IceDomain('authenticationservice');
		$this->registryadmin           = new IceDomain('registryadmin');
		$this->usernotificationservice = new IceDomain('usernotificationservice');
	}

	/**
	 * Convert to PHP Int a Java Byte.
	 * 
	 * @param  int $int The number
	 * @return int
	 */
	private static function int_to_java_byte($int)
	{
		return (($int+128) % 256) - 128;
	}

	/**
	 * Check if ChatSync is enabled on Sysprop
	 * @return boolean [description]
	 */
	public function is_sysprop_chatsync_enabled()
	{
		try
		{
			return SystemProperty::get_instance()->get_boolean(SystemProperty::ChatSync_WebEnabled, false) 
					&& SystemProperty::get_instance()->get_boolean(SystemProperty::ChatSync_Enabled, false);			
		}
		catch(Exception $e)
		{
			return false;
		}
	}

	/**
	 * Check if ChatSync is enabled
	 * @param  string  $username [description]
	 * @param  int  $user_id  [description]
	 * @return boolean           Whether it's enabled or not
	 */
	public function is_user_chatsync_enabled($username, $user_id)
	{
		try 
		{
			return $this->registry->get_object_prx()
						->getMessageSwitchboard()
						->isUserChatSyncEnabled($this->get_connection_prx(), $username, $user_id);
		} 
		catch(Ice_OperationNotExistException $e) 
		{
			return false;
		} 
		catch(Ice_UnknownException $e) 
		{
			return false;
		}
		catch(Exception $e) 
		{
			return false;
		}
	}

	/**
	 * Get the list of conversations. Get the participants usernames and display them in a list.
	 * 
	 * @param  int $user_id Just the current user's ID
	 * @param  int $limit Messages limit
	 * @param  int $version Version number
	 * @param  int $chat_type Chat type
	 * @return $chats The chats object from ICE
	 */
	public function get_chats($user_id, $limit=5, $version=self::CHAT_LIST_VERSION, $chat_type=self::CHAT_TYPE_PRIVATE)
	{
		try 
		{
			return $this->registry->get_object_prx()->getMessageSwitchboard()->getChats(
				$user_id
				, $version
				, $limit
				, self::int_to_java_byte($chat_type)
			);
		} 
		catch(Ice_OperationNotExistException $e) 
		{
			error_log('Ice_OperationNotExistException: ' . $e->getMessage());
		} 
		catch(Ice_UnknownException $e) 
		{
			error_log('Ice_UnknownException: ' . $e->getMessage());
		}
		catch(Exception $e) 
		{
			error_log('Exception: ' . $e->getMessage());
		}

		return array();
	}

	/**
	 * Wrapper method of get_chats for group. Get the list of Group conversations. Get the participants usernames and display them in a list.
	 * 
	 * @param  int $user_id Just the current user's ID
	 * @param  int $limit Messages limit
	 * @param  int $version Version number
	 * @return $chats The chats object from ICE
	 */
	public function get_group_chats($user_id, $limit=5, $version=self::CHAT_LIST_VERSION)
	{
		return $this->get_chats($user_id, $limit, $version, self::CHAT_TYPE_GROUP);
	}	

	/**
	 * Get and push the messages to the J2ME Client
	 * 
	 * @param  string $username    	Logged-in user's username
	 * @param  string $participant 	Chat participant's username
	 * @param  string $chat_type 	Chat type
	 * @throws ObjectNotFoundException
	 * @throws Exception
	 * 
	 */
	public function get_and_push_messages($username, $participant, $chat_type=self::CHAT_TYPE_PRIVATE, $limit=5)
	{
		if (!$participant || !in_array($chat_type, array(IceDAO::CHAT_TYPE_PRIVATE, IceDAO::CHAT_TYPE_GROUP)))
			return null;
		
		try 
		{
			$this->registry->get_object_prx()->getMessageSwitchboard()->getAndPushMessages(
				$username
				, self::int_to_java_byte($chat_type)
				, $participant
				, self::$MIN_TIMESTAMP_VALUE
				, self::$MIN_TIMESTAMP_VALUE
				, self::int_to_java_byte($limit)
				, $this->get_connection_prx()
			);
		}
		catch(ObjectNotFoundException $e)
		{	
			error_log('ObjectNotFoundException: ' . $e->getMessage());
		} 
		catch(Exception $e) 
		{
			error_log('Exception' . $e->getMessage());
		}
	}

	public static function loadProfile()
	{
		if (! (self::$ICE instanceof Ice_Communicator))
		{
			try
			{
				if (function_exists('Ice_loadProfile'))
				{
					Ice_loadProfile();
					self::$ICE = $GLOBALS['ICE'];
				}
				else if (function_exists('Ice_initialize'))
				{
					require_once 'Ice.php';
					require_once get_library_directory() . '/fusion/Fusion.php';
					self::$ICE = Ice_initialize();
				}
				else
				{
					throw new IceDAOException('Unable to initialize ICE.');
				}
			}
			catch (Ice_ProfileAlreadyLoadedException $e)
			{
			}
			catch (IceDAOException $e)
			{
				error_log($e->getMessage());
			}
		}
		return self::$ICE;
	}

	/**
	 * Get proxy
	 * @param IceDomain $config
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @return Ice_ObjectPrx
	 */
	public static function get_object_prx(IceDomain $config)
	{
		if (empty(self::$proxies[$config->proxy])
			|| ! (self::$proxies[$config->proxy] instanceof Ice_ObjectPrx))
		{
			self::$proxies[$config->proxy] = self::$ICE
				->stringToProxy($config->connection)
				->ice_checkedCast($config->slice);
		}
		return self::$proxies[$config->proxy];
	}

	/**
	 * Get connectionPrx
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return Ice_ObjectPrx self::$connectionPrx
	 */
	protected function get_connection_prx()
	{
		$sessionID = SessionUtilities::get_session_id();

		if (empty($sessionID))
			throw new IceDAOException('No session ID found');

		if (! (self::$connectionPrx instanceof Ice_ObjectPrx))
		{
			self::$connectionPrx = $this->registry->get_object_prx()
				->findConnectionObject($sessionID);

			if (! (self::$connectionPrx instanceof Ice_ObjectPrx))
				throw new IceDAOException('connectionPrx is not an instance of Ice_ObjectPrx');
		}
		return self::$connectionPrx;
	}

	/**
	 * Get userPrx
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return Ice_ObjectPrx self::$userPrx
	 */
	protected function get_user_prx()
	{
		if (! (self::$userPrx instanceof Ice_ObjectPrx))
		{
			self::$userPrx = $this->get_connection_prx()->getUserObject();

			if (! (self::$userPrx instanceof Ice_ObjectPrx))
				throw new IceDAOException('userPrx is not an instance of Ice_ObjectPrx');
		}
		return self::$userPrx;
	}

	/**
	 * Checks with the Fusion registry whether a connection exists for the session id sent during an XHTML paglet request.
	 * Throws an exception if no valid session exists.
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return IceDAO $this
	 */
	public function check_session()
	{
		$this->get_connection_prx();
		/* 2011-04-06: Timothee Groleau: Disabling session origin check until we have a better solution */
		/*
		$sessionIP = $this->get_connection_prx()->getRemoteIPAddress();
		$remoteAddress = getRemoteIPAddress();
		$username = $this->get_username();
		if ( netmask($sessionIP,24) != netmask($remoteAddress,24) && strpos($remoteAddress,"10.3.1.") !== 0) {
			error_log ("Invalid Session Request. Session for user: '". $username ."' created at: ". $sessionIP ." Requested from: ".$remoteAddress);
			// possible session hijacking - we stop this here.
			header ("HTTP/1.0 503 Service Unavailable");
			die();
		}
		/**/
		return $this;
	}

	/**
	 * Get the username from the Fusion registry.
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return string
	 */
	public function get_username($userid)
	{
		return $this->get_connection_prx()->getUsername();
	}

	/**
	 * Get userdata for a particular username from the Fusion registry.
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return com_projectgoth_fusion_slice_UserDataIce
	 */
	public function get_userdata()
	{
// 		var_dump($this->get_user_prx()->getUserData());
		return $this->get_user_prx()->getUserData();
	}

	/**
	 * Disconnect flooder
	 *
	 * @param string $reason
	 * @return IceDAO $this
	 */
	public function disconnect_flooder($reason)
	{
		try
		{
			$this->get_user_prx()->disconnectFlooder($reason);
		}
		catch (Exception $ex)
		{
		}
		return $this;
	}

	/**
	 * Get stats
	 * @throws Exception
	 * @return com_projectgoth_fusion_slice_RegistryStats
	 */
	function get_stats()
	{
		return $this->registryadmin->get_object_prx()->getStats();
	}

	/**
	 * Get all IM credentials
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return array com_projectgoth_fusion_slice_Credential
	 */
	function get_other_im_credentials()
	{
		return $this->get_user_prx()->getOtherIMCredentials();
	}

	/**
	 * Get IM Credentials by $im_type
	 * @param integer $im_type
	 * @return com_projectgoth_fusion_slice_Credential
	 * @return null on error
	 */
	function get_im_credentials($im_type)
	{
		try
		{
			$credentials = $this->get_other_im_credentials();
		}
		catch(Exception $ex)
		{
			return null;
		}
		foreach($credentials as $credential)
		{
			$passwordType = $credential->passwordType;
			settype($passwordType, 'integer');
			if($passwordType == $im_type)
				return $credential;
		}
		return null;
	}

	/**
	 * Get all IM contacts
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return array
	 */
	function get_other_im_contacts($username)
	{
		return $this->registry->get_object_prx()
			->findUserObject($username)
			->getOtherIMContacts();
	}

	/**
	 * Get overall presence
	 * @param string $requestor username
	 * @param string $target username
	 * @return number e.g. 1 online, 3 busy, 4 away, 99 offline
	 */
	function get_overall_presence($requestor, $target)
	{
		try
		{
			return $this->registry->get_object_prx()
				->findUserObject($target)
				->getOverallFusionPresence($requestor == $target ? null : $requestor);
		}
		catch(Exception $ex)
		{
			// return OFFLINE
			return 99;
		}
	}

	/**
	 * Get current chatrooms of specified users
	 * @param string $username
	 * @return array of chatrooms
	 */
	function get_current_chatrooms($username)
	{
		try
		{
			return $this->registry->get_object_prx()
				->findUserObject($username)
				->getCurrentChatrooms();
		}
		catch(Exception $ex)
		{
			return array();
		}
	}

	/**
	 * Get Chatroom users
	 * @param string $chatroom
	 * @param string $username
	 * @return array of usernames
	 */
	function get_chatroom_users($chatroom, $username)
	{
		try
		{
			return $this->registry->get_object_prx()
				->findChatRoomObject($chatroom)
				->getParticipants($username);
		}
		catch(Exception $ex)
		{
			return array();
		}
	}

	/**
	 * Get groupchat users
	 * @param integer $groupchatid
	 * @param string $username
	 * @return array of usernames
	 */
	function get_groupchat_users($groupchatid, $username)
	{
		try
		{
			return $this->registry->get_object_prx()
				->findGroupChatObject($groupchatid)
				->getParticipants($username);
		}
		catch(Exception $ex)
		{
			return array();
		}
	}

	/**
	 * Update the users unread message count with the Fusion registry.
	 * This will update the count in midlet
	 * @param integer $numUnread
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return IceDAO $this
	 */
	function email_notification($numUnread)
	{
		$this->get_user_prx()->emailNotification($numUnread);
		return $this;
	}

	/**
	 * Capture metrics for edit profile
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return IceDAO $this
	 */
	function edit_profile_metric()
	{
		$this->get_connection_prx()->getSessionObject()->profileEdited();
		return $this;
	}

	/**
	 * Update theme metric
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return IceDAO $this
	 */
	function update_theme_metric()
	{
		$this->get_connection_prx()->getSessionObject()->themeUpdated();
		return $this;
	}

	/**
	 * Get publishing privacy setting
	 * @param string $username
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return
	 */
	function get_publishing_privacy_setting($username)
	{
		return $this->eventstore->get_object_prx()
			->getPublishingPrivacyMask($username);
	}

	/**
	 * Get receiving privacy setting
	 * @param string $username
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return
	 */
	function get_receiving_privacy_setting($username)
	{
		return $this->eventstore->get_object_prx()
			->getReceivingPrivacyMask($username);
	}

	/**
	 * Set publishing privacy setting
	 * @param string $username
	 * @param stdClass $settings
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return
	 */
	function set_publishing_privacy_setting($username, $settings)
	{
		return $this->eventstore->get_object_prx()
			->setPublishingPrivacyMask($username, $settings);
	}

	/**
	 * Set receiving privacy setting
	 * @param string $username
	 * @param stdClass $settings
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return
	 */
	function set_receiving_privacy_setting($username, $settings)
	{
		return $this->eventstore->get_object_prx()
			->setReceivingPrivacyMask($username, $settings);
	}

	/**
	 * Get online contacts count
	 * @return integer
	 */
	function get_online_contacts_count()
	{
		try
		{
			return $this->get_user_prx()->getOnlineContactsCount();
		}
		catch (Exception $e)
		{
			return 0;
		}
	}

	/**
	 * Get mobile device
	 * @return string
	 */
	function get_mobile_device()
	{
		try
		{
			if (is_wap_view()) return 'WAP';

			$mobileDevice = $this->get_connection_prx()->getMobileDevice();
			return empty($mobileDevice) ? '' : $mobileDevice;
		}
		catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
		{
			return '';
		}
		catch(Ice_LocalException $ex)
		{
			return '';
		}
		catch(IceDAOException $ex)
		{
			return '';
		}
	}
}
?>