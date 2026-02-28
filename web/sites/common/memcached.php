<?php
	/**
	*	Memcached
	* 	Desc: Singleton class
	*
	*   Usage:
	*     To SET a variable:
	*        Memcached::get_instance()->add_or_update("key", "value", [expiry time in seconds]);
	*
	*     To GET a variable:
	*        Memcached::get_instance()->get("key");
    *
	*/

	require_once(get_framework_common_directory() . "/utilities.php");
	require_once(get_common_config_location());

	class Memcached extends Memcache
	{
		/**
		 * @var array Memcached
		 * @access private
		 */
		static private $memcached = NULL;

		public static $KEY_SEPERATOR = '/';
		public static $KEYSPACE_USERID = 'F/UID/';
		public static $KEYSPACE_USERNAME = 'F/UNAME/';
		public static $KEYSPACE_BAN_USERNAME = 'LB/';
		public static $KEYSPACE_DISTRIBUTED_LOCK = 'LOCK/';
		public static $KEYSPACE_GROUPS_COUNT = 'GRPC/';
		public static $KEYSPACE_GROUP_MEMBER = 'GRP/MEMBER';
		public static $KEYSPACE_GROUP_FORUM_TOPICS = 'GRP/FT';
		public static $KEYSPACE_GROUP_PHOTOS = 'GRP/PH';
		public static $KEYSPACE_GROUP_LIKES = 'GRP/LIKES';
		public static $KEYSPACE_GROUPS_FEATURED = 'GRP/FEAT/';
		public static $KEYSPACE_GROUPS_OFFICIAL = 'GRP/OFCL/';
		public static $KEYSPACE_GROUPS_ACTIVE_USER = 'GRP/ACTIVE/';
		public static $KEYSPACE_GROUPS_ACTIVE_USER_COUNT = 'GRP/ACTIVE/COUNT';
		public static $KEYSPACE_GROUPS_PUBLIC = 'GRP/PUBLIC/';
		public static $KEYSPACE_GROUP_FEEDS = 'GRP/FEEDS/';
		public static $KEYSPACE_THIRD_PARTY_API_AUTHORIZATION = 'TPA/AUTHORIZATION/';
        public static $KEYSPACE_THIRD_PARTY_API_ACCESS = 'TPA/ACCESS/';
        public static $KEYSPACE_THIRD_PARTY_API_DEBIT = 'TPA/ACCESS/DEBIT';
        public static $KEYSPACE_MERCHANT_TAG = 'MTT/'; // + username
		public static $KEYSPACE_CHATROOMS_COUNT = 'RCC/';
		public static $KEYSPACE_FAILED_LOGIN_ATTEMPTS = "FLA/"; // +username
		public static $KEYSPACE_LEADERBOARD = "LDB/";
		public static $KEYSPACE_GROUP_FRIENDS = 'GRP/FRIENDS';
		public static $KEYSPACE_GROUP_MODERATORS = 'GRP/MODERATORS/';
		public static $KEYSPACE_SECURITY_QUESTION = 'SEC_QN';
		public static $KEYSPACE_FORGOT_PASSWORD_ATTEMPTS = 'FPA';
		public static $KEYSPACE_SLIM_SESSION = 'slim_sess_';
		public static $KEYSPACE_SSO_SESSION = 'LG_DTA/';
		public static $KEYSPACE_USSD_PARTNER = 'USSD_PARTNER/';
		public static $KEYSPACE_TWITTER_REQUEST_TOKEN = 'TWITTER/REQUESTTOKEN/';
		public static $KEYSPACE_VIRTUAL_GIFTS_RECEIVED = 'VGS/RECEIVED/';

		public static $KEYSPACE_THIRD_PARTY_APPLICATIONS = 'TPA/ALL_APS';
		public static $KEYSPACE_THIRD_PARTY_APPLICATION_GROUPS = 'TPA/GROUPS';
		public static $KEYSPACE_THIRD_PARTY_APPLICATION_BY_CID = 'TPA/APP/CID/'; // + CLIENT_ID
		public static $KEYSPACE_THIRD_PARTY_APPLICATION_BY_ACK = 'TPA/APP/ACK/'; // + APPLICATION_CONSUMER_KEY
		public static $KEYSPACE_THIRD_PARTY_APPLICATION_BY_ID  = 'TPA/APP/ID/';   // + APPLICATION_ID
		public static $KEYSPACE_MERCHANT_DASHBOARD_CUSTOMERS = 'MERCH/DASH/CUST';

		public static $KEYSPACE_GROUP_POPULAR_IN_COUNTRY = 'GRP/POPLR_CTRY'; // + COUNTRY_ID

		public static $KEYSPACE_ACCOUNT_BALANCE = "BAL/";
		public static $KEYSPACE_MERCHANTS_SUGGESTION_IN_COUNTRY = 'MERCH_SUGGEST/CTRY'; // + COUNTRY_ID

		public static $KEYSPACE_3RD_PRTY_VENDOR_IPS = 'PYMNT_SSPD_IP/'; // + IP_ADDRESS
		public static $KEYSPACE_3RD_PRTY_VENDOR_ENABLED = 'PYMNT_ENBLD';
		public static $KEYSPACE_SESSION_ACTIVE_VIEW = 'ACTV_VW'; // + SHA1 of sessionID


		public static $CACHEDURATION_BAN_USERNAME = 3600;
		public static $CACHEDURATION_GROUP_MEMBER = 1200;
		public static $CACHEDURATION_GROUP_FORUM_TOPICS = 1200;
		public static $CACHEDURATION_GROUP_PHOTOS = 1200;
		public static $CACHEDURATION_GROUP_LIKES = 1200;
		public static $CACHEDURATION_GROUP_FEATURED = 1200;
		public static $CACHEDURATION_GROUP_OFFICIAL = 1200;
		public static $CACHEDURATION_GROUP_FRIENDS = 86400;
		public static $CACHEDURATION_GROUP_MODERATORS = 600;
		public static $CACHEDURATION_GROUP_ACTIVE_USER = 1200;
		public static $CACHEDURATION_GROUP_ACTIVE_USER_COUNT = 1200;
		public static $CACHEDURATION_GROUP_PUBLIC = 1200;
		public static $CACHEDURATION_GROUP_FEEDS = 3600;
		public static $CACHEDURATION_THIRD_PARTY_API_AUTHORIZATION = 1800;
		public static $CACHEDURATION_THIRD_PARTY_API_ACCESS = 604800;
		public static $CACHEDURATION_THIRD_PARTY_API_DEBIT = 86400;
        public static $CACHEDURATION_FAILED_LOGIN_ATTEMPTS = 604800;
    	public static $CACHEDURATION_SYSTEM_PROPERTY = 300;
    	public static $CACHEDURATION_SECURITY_QUESTION = 600;
    	public static $CACHEDURATION_FORGOT_PASSWORD_ATTEMPTS = 34560;
    	public static $CACHEDURATION_ACCOUNT_BALANCE = 604800;
    	public static $CACHEDURATION_USSD_PARTNER = 600;
    	public static $CACHEDURATION_GROUP_POPULAR_IN_COUNTRY = 86400;
		public static $CACHEDURATION_3RD_PRTY_VENDOR_IPS = 18000;
		public static $CACHEDURATION_3RD_PRTY_VENDOR_ENABLED = 60; // we cache this for a minute only - this is cached only for optimization
		public static $CACHEDURATION_SESSION_ACTIVE_VIEW = 600;

        /** Currently in Fusion. Ask koko for more definitions if needed.
		ACCOUNT_BALANCE
			("BAL", 7, TimeUnit.DAYS),
		ALERT_MESSAGE
			("AM", 5, TimeUnit.MINUTES),
		BLOCK_LIST
			("BL", 7, TimeUnit.DAYS),
		CHATROOM
			("CR", 7, TimeUnit.DAYS),
		CHATROOM_MODERATORS
			("CRM", 7, TimeUnit.DAYS),
		CHATROOM_BANNED_USERS
			("CRBU", 7, TimeUnit.DAYS),
		CONTACT_GROUP
			("CG", 7, TimeUnit.DAYS),
		CONTACT_LIST_VERSION
			("CLV", 7, TimeUnit.DAYS),
		CURRENCY
			("CUR", 15, TimeUnit.MINUTES),
		DID_NUMBER
			("DID", 5, TimeUnit.MINUTES),
		EMOTICON_PACKS_OWNED
			("EPO", 7, TimeUnit.DAYS),
		FAILED_LOGIN_ATTEMPTS
			("FLA", 7, TimeUnit.DAYS),
		GROUP
			("GRP", 7, TimeUnit.DAYS),
		GROUP_ANNOUNCEMENT
			("GA", 7, TimeUnit.DAYS),
		GROUP_INVITATION
			("GI", 30, TimeUnit.DAYS),
		LOGIN_BAN
			("LB", 1, TimeUnit.HOURS),
		NUM_VIRTUAL_GIFTS_RECEIVED
			("NVGR", 7, TimeUnit.DAYS),
		RECENT_CHATROOM_COUNT
			("RCC", 20, TimeUnit.MINUTES),
		REPUTATION_LEVEL
			("RL", 1, TimeUnit.HOURS),
		SCRAPBOOK
			("SB", 7, TimeUnit.DAYS),
		SYSTEM_PROPERTY
			("SYS", 5, TimeUnit.MINUTES),
		USER_ID
			("UID", 7, TimeUnit.DAYS),
		USER_PROFILE
			("UP", 7, TimeUnit.DAYS),
		USER_PROFILE_STATUS
			("UPS", 7, TimeUnit.DAYS),
		USER_SETTING
			("US", 7, TimeUnit.DAYS);
		**/

		/**
		 * __construct function.
		 *
		 * @access private
		 * @return void
		 */
		private function __construct()
		{
		}

		/**
		 * __clone function.
		 *
		 * @access private
		 * @return void
		 */
		private function __clone()
		{
		}

		/**
		 * Get Memcache Full Key.
		 *
		 * @access public
		 * @static
		 * @param string $keyspace
		 * @param string $name
		 * @return string
		 */
		public static function get_memcache_full_key($keyspace, $name)
		{
			return $keyspace. Memcached::$KEY_SEPERATOR . $name;
		}

		/**
		 * get_instance function.
		 *
		 * @access public
		 * @static
		 * @param string $instance_name (default: 'common')
		 * @return Memcached
		 */
		public static function get_instance($instance_name='common')
		{
			global $memcache_servers;

			if (is_null(self::$memcached))
			{
				self::$memcached = array();
			}

			if (!isset(self::$memcached[$instance_name]))
			{
				if (!isset($memcache_servers[$instance_name]))
				{
					return null;
				}

				self::$memcached[$instance_name] = new Memcached();

				// Add all the memcache servers to the pool
				foreach($memcache_servers[$instance_name] as $server => $port)
				{
					self::$memcached[$instance_name]->addServer($server, $port, false);
				}
			}

			return self::$memcached[$instance_name];
		}

		/**
		 * Add an value to memcache if it doesn't exist otherwise it updates the item (Default expiry is 72hrs)
		 *
		 * @access public
		 * @param string $key
		 * @param mixed $data
		 * @param int $expire (default: 259200)
		 * @return bool
		 */
		public function add_or_update($key, $data, $expire = 259200)
		{
			if ($this->replace($key, $data, false, $expire))
				return true;

			return $this->set($key, $data, false, $expire);
		}

		/**
		 * Increment a integer counter associated to a key
		 *
		 * @access public
		 * @param string $key
		 * @param int $value (default: 1)
		 * @return void
		 */
		public function increment_key_counter($key, $value=1)
		{
			$this->increment($key, $value);
		}

		/**
		 * Decrement a integer counter associated to a key
		 *
		 * @access public
		 * @param string $key
		 * @param int $value (default: 1)
		 * @return void
		 */
		public function decrement_key_counter($key, $value=1)
		{
			$this->decrement($key, $value);
		}


		/**
		 * Check if an item exists in memcache
		 *
		 * @access public
		 * @param string $key
		 * @return bool
		 */
		public function item_exists($key)
		{
			if( $this->get($key) == FALSE )
				return false;
			else
				return true;
		}

		/**
		 * Removes an item from memcache
		 *
		 * @access public
		 * @param string $key
		 * @return bool
		 */
		public function remove_item($key)
		{
			return $this->delete($key, 0);
		}

		/**
		 * Obtains a distributed lock. The lock will expire after 30s (by default). This is a safety mechanism in case the process that created the lock dies.
		 *
		 * @access public
		 * @param int $lock_id
		 * @param int $timeout (default: 30)
		 * @return void
		 */
		public function get_distributed_lock($lock_id, $timeout = 30)
		{
			while (!$this->add(self::$KEYSPACE_DISTRIBUTED_LOCK . $lock_id, 1, false, $timeout))
			{
				usleep(100000); // Sleep for 100 milliseconds
			}
		}

		/**
		 * Release a distributed lock
		 *
		 * @access public
		 * @param int $lock_id
		 * @return bool
		 */
		public function release_distributed_lock($lock_id)
		{
			return $this->delete(self::$KEYSPACE_DISTRIBUTED_LOCK . $lock_id);
		}

		/**
		 * Add an item to the server (Default expiry is 72hrs)
		 *
		 * @access public
		 * @param string $key
		 * @param mixed $var
		 * @param bool $flag (default: false)
		 * @param int $expire (default: 259200)
		 * @return bool
		 */
		public function add($key, $var, $flag = false, $expire = 259200)
		{
			return parent::add($this->prepare_key($key), $var, $flag, $expire);
		}

		/**
		 * Replace value of the existing item (Default expiry is 72hrs)
		 *
		 * @access public
		 * @param string $key
		 * @param mixed $var
		 * @param bool $flag (default: false)
		 * @param int $expire (default: 259200)
		 * @return bool
		 */
		public function replace($key, $var, $flag = false, $expire = 259200)
		{
			return parent::replace($this->prepare_key($key), $var, $flag, $expire);
		}

		/**
		 * Store data at the server (Default expiry is 72hrs)
		 *
		 * @access public
		 * @param string $key
		 * @param mixed $var
		 * @param bool $flag (default: false)
		 * @param int $expire (default: 259200)
		 * @return bool
		 */
		public function set($key, $var, $flag = false, $expire = 259200)
		{
			return parent::set($this->prepare_key($key), $var, $flag, $expire);
		}

		/**
		 * Retrieve item from the server
		 *
		 * @access public
		 * @param string $key
		 * @param bool $flags (default: false)
		 * @return string
		 */
		public function get($key, $flags = false)
		{
			return parent::get($this->prepare_key($key), $flags);
		}


		/**
		 * Delete item from the server
		 *
		 * @access public
		 * @param string $key
		 * @return bool
		 */
		public function delete($key)
		{
			return parent::delete($this->prepare_key($key));
		}

		/**
		 * Increment item's value
		 *
		 * @access public
		 * @param string $key
		 * @param int $value (default: 1)
		 * @return int
		 */
		public function increment($key, $value = 1)
		{
			return parent::increment($this->prepare_key($key), $value);
		}

		/**
		 * Decrement item's value
		 *
		 * @access public
		 * @param string $key
		 * @param int $value (default: 1)
		 * @return int
		 */
		public function decrement($key, $value = 1)
		{
			return parent::decrement($this->prepare_key($key), $value);
		}

		/**
		 * Prepare key before passing it to Memcached
		 *
		 * @access private
		 * @param string $key
		 * @return string
		 */
		private function prepare_key($key)
		{
			// Replace \n with %0A, \r with %0D and Space with %20
			return str_replace(array("\n", "\r", ' '), array('%0A', '%0D', '%20'), $key);
		}
	}
?>