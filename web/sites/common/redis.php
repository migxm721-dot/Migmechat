<?php
	/**
	*	Redis
	*
	*	This class is used to interact with Redis instances at mig33.
	*
	*   It uses the Predis client library: http://github.com/nrk/predis
	*
	*   We "shard" data across Redis instancces to provide scalability. This means we run multiple instances
	*   of Redis (across many servers) and we distribute data across these instances (or shards).
	*
	*   We are using directory-based partitioning in order to shard the data. One instance of Redis is a "shard
	*   directory". Its job is to keep track of which data is on which shard. Before getting or setting data, we
	*   query the shard directory to find out which shard we should be talking to.
	*
	*   The shard directory also contains the locations of each shard (i.e. host:port).
	*
	*   We scale the shard directory by using Redis replication (as there will be many more reads than writes).
	*
	*   The directory lookup is handled by calling the appropriate get_[master|slave]_instance_for_X() function,
	*   where X is a type of key such as user_id, group_id, chat_room_id, etc.
	*
	*   We make sure that all data pertaining to a particular user/group/etc is always stored on the same shard.
	*      e.g., a user's profile footprints, no. profile views, contact list version, etc will be stored on
	*      the same shard.
	*   This is achieved by always using the user's UserID to obtain the shard to use for this information.
	*
	*   Usage example:
	*     To SET a variable:
	*        Redis::get_master_instance_for_user_id($user_id)->set(Redis::KEYSPACE_USER_DETAILS . $user_id, "value");
	*
	*     To GET a variable:
	*        Redis::get_slave_instance_for_user_id($user_id)->get(Redis::KEYSPACE_USER_DETAILS . $user_id);
	*
	*   IMPORTANT NOTE: Redis is a permanent store! Once you put something in Redis it will use up
	*                   disk and memory on the server forever*! If you want temporary storage, use
	*                   Memcache instead.
	*                      *unless an expiration is specified.
	**/

	fast_require("Predis", get_framework_common_directory() . "/Predis.php");
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("XCache", get_framework_common_directory() . "/xcache.php");


	class RedisException extends Exception {}

	class RedisShardSettings
	{
		public $shard_masters     = array();
		public $shard_slaves      = array();
		public $shard_weights     = array();
		public $sum_shard_weights = 0;
	}

	class Redis
	{
		const MAX_GET_SETTINGS_TRIES = 2;

    	const MASTER = ':M';
    	const SLAVE  = ':S';

		static private $shard_directory_master = NULL;
		static private $shard_directory_slave = NULL;

		static private $leaderboards_master = NULL;
		static private $leaderboards_slave = NULL;

		static private $games_master = NULL;
		static private $games_slave = NULL;

		static private $groups_master = NULL;
		static private $groups_slave = NULL;

		private static $shard_settings = NULL;
		private static $num_get_settings_try = 0;
		private static $cache_keys_to_shard_ids = array();

		private static $redis_parameters_query = "?connection_timeout=2&read_write_timeout=3";

		const KEYSPACE_SEPARATOR         = ':';

		// Keyspaces used in the shard directory instance:
		const KEYSPACE_SHARD_DIRECTORY_REDIS_SHARD_MASTERS = 'R:MASTERS';   // "R:MASTERS" => [A hash of ShardID=>host:port]
		const KEYSPACE_SHARD_DIRECTORY_REDIS_SHARD_SLAVES  = 'R:SLAVES';    // "R:WEIGHTS" => [A hash of ShardID=>host:port]
		const KEYSPACE_SHARD_DIRECTORY_REDIS_SHARD_WEIGHTS = 'R:WEIGHTS';   // "R:WEIGHTS" => [A list of each shard's weight]
		const KEYSPACE_SHARD_DIRECTORY_REDIS_USER_ID       = 'R:U:';		// "R:U:<UserID>"
		const KEYSPACE_SHARD_DIRECTORY_REDIS_GROUP_ID      = 'R:G:';		// "R:G:<GroupID>"
		const KEYSPACE_SHARD_DIRECTORY_REDIS_CHAT_ROOM_ID  = 'R:C:';		// "R:C:<ChatRoomID>"
		const KEYSPACE_SHARD_DIRECTORY_REDIS_TEMP_USER_ID  = 'R:TU:';		// "R:TU:<UserID>"
		//const KEYSPACE_SHARD_DIRECTORY_REDIS_AVATAR_CANDIDATES  = 'R:AVATARCANDIDATES:';		// "R:AVATARCANDIDATES" => [A list of usernames]

		const KEYSPACE_DISTRIBUTED_LOCK = 'D_LOCK:';

		// initial implementation of new keyspaces
		// Entity KeySpaces
		const KEYSPACE_ENTITY_USER     = 'U:';
		const KEYSPACE_ENTITY_GROUP    = 'G:';
		const KEYSPACE_LIKES           = 'LK';
		const KEYSPACE_LIKES_USER      = ':LK:UL';
		const KEYSPACE_LIKES_WALL_POST = ':LK:WP';
		const KEYSPACE_USER_WALL_POST  = 'UWP';
		const KEYSPACE_USER_AVATAR_CMT  = 'UAC';
		// for external invitee
		const KEYSPACE_ENTITY_TEMP_USER = 'TU:'; // keyspace for external invite receiver

		// Keyspaces used for storing data in the individual shards:
		const KEYSPACE_USER                   = 'User:';                // "User:<UserID>"  A hash that holds user details (like contact list version, avatar, etc)
		const KEYSPACE_USER_ACTIVITY          = 'UserActivity:';        // "UserActivity:<UserID>"
		const KEYSPACE_USER_LIKES             = 'UserLikes:';           // "UserLikes:<UserID>" A set of ids of users sent likes for the user identified by UserID
		const KEYSPACE_USER_PROFILES_VIEWED   = 'UserProfilesViewed:';  // "UserProfilesViewed:<UserID>"  Holds a list of the users this user has viewed
		const KEYSPACE_USER_PROFILE_VIEWED_BY = 'UserProfileViewedBy:'; // "UserProfileViewedBy:<UserID>"  Holds a list of users that have viewed this user's profile
		const KEYSPACE_USER_SETTINGS          = 'UserSettings:';        // "UserSettings:<UserID>"  A hash holding the user's settings/preferences
		const KEYSPACE_GROUP                  = 'Group:';               // "Group:<GroupID>"  Holds group details (like name, tags, etc)
		const KEYSPACE_GROUP_ACTIVITY         = 'GroupActivity:';       // "GroupActivity:<GroupID>"
		const KEYSPACE_GROUP_MODERATORS       = ':Moderators';          //
		const KEYSPACE_GROUP_MODERATORS_COUNT = ':ModeratorsCount';      //
		const KEYSPACE_CHAT_ROOM              = 'ChatRoom:';            // "ChatRoom:<ChatRoomID>"  Holds chat room details (like name, tags, etc)
		const KEYSPACE_GROUP_SCORE            = 'Score';
		const KEYSPACE_SETTINGS               = ':Config';

		// Field names used in Redis hashes
		// (Note: these should be placed in the appropriate DAO class, not here. I've just left them here
		// for reference until they're all moved)

		// KEYSPACE_USER
		const FIELD_USER_AVATAR_VOTES 					= 'AvatarVotes';
		const FIELD_USER_AVATAR_CANDIDATE_CURRENT_INDEX	= 'AvatarCandidateCurrentIndex';
		const FIELD_FASHIONSHOW_MIGLEVEL 				= 'ReqdLevel';
		const FIELD_FASHIONSHOW_ACTIVE_DAYS				= 'ReqdActiveDays';
		const FIELD_FASHIONSHOW_ITEMS 					= 'ReqdAvtrItems';
		const FIELD_NUM_OF_WALL_POSTS 					= 'NumOfWallPosts';
		const FIELD_NUM_OF_AVATAR_CMTS 					= 'NumOfAvatarComments';
		const FIELD_USER_IDENTICON_HASH 				= 'IdenticonHash';
		const FIELD_USER_IDENTICON_INDEX 				= 'IdenticonIndex';
		const FIELD_PAINTWARS_MIGLEVEL 					= 'ReqdMigLevel';
		const FIELD_PAINTWARS_FREE_PAINTS				= 'FreePaintsPerDay';
		const FIELD_PAINTWARS_FREE_CLEANS				= 'FreeCleansPerDay';
		const FIELD_USER_CAMPAIGN_MERCHANT_LOGIN_COUNT	= 'CampaignMerchantLoginCount';
		//const FIELD_USER_AVATAR = 'Avatar';
		//const FIELD_USER_CONTACT_LIST_VERSION = 'ContactListVersion';
		//const FIELD_USER_NUM_NEW_FOOTPRINTS = 'NumNewFootprints';

		// KEYSPACE_GROUP
		//const FIELD_GROUP_TAGS = 'Tags';
		const FIELD_GROUP_LAST_ACTIVITY					= 'LastActivity';

		// KEYSPACE_CHAT_ROOT
		//const FIELD_CHAT_ROOM_TAGS = 'Tags';

		// Keyspaces Captcha
		const KEYSPACE_CAPTCHA				= 'Captcha:';

		// KEYSPACES LEADERBOARD
		const KEYSPACE_LB_USER_LIKES       = 'LB:UserLikes:';
		const KEYSPACE_LB_MIG_LEVEL        = 'LB:MigLevel:';
		const KEYSPACE_LB_REFERRER         = 'LB:Referrer:';
		const KEYSPACE_LB_GIFT_SENT        = 'LB:GiftSent:';
		const KEYSPACE_LB_GIFT_RECEIVED    = 'LB:GiftReceived:';
		const KEYSPACE_LB_AVATAR_VOTES	   = 'LB:AvatarVotes:';
		const KEYSPACE_LB_PAINTWARS_POINTS = 'LB:PaintPoints:';

		const KEYSPACE_LB_MOST_WINS		   = 'LB:MostWins:';
		const KEYSPACE_LB_GAMES_PLAYED	   = 'LB:GamesPlayed:';

		const KEYSPACE_GAME_LOWCARD			= 'LowCard:';
		const KEYSPACE_GAME_DICE			= 'Dice:';
		const KEYSPACE_GAME_FOOTBALL		= 'Football:';
		const KEYSPACE_GAME_GUESS			= 'Guess:';
		const KEYSPACE_GAME_DANGER			= 'Danger:';
		const KEYSPACE_GAME_MIGCRICKET		= 'Cricket:';

		const KEYSPACE_LB_DAILY            = 'Daily';
		const KEYSPACE_LB_PREVIOUS_DAILY   = 'PreviousDaily';
		const KEYSPACE_LB_WEEKLY           = 'Weekly';
		const KEYSPACE_LB_PREVIOUS_WEEKLY  = 'PreviousWeekly';
		const KEYSPACE_LB_ALL_TIME         = 'AllTime';

		// KEYSPACES_AVATAR
		const KEYSPACE_AVATAR_CANDIDATES   = 'AvatarCandidates';
		const KEYSPACE_AVATAR_VOTES   	   = 'AvatarVotes';
		const KEYSPACE_FASHIONSHOW		   = 'FashionShow';
		const KEYSPACE_PAINTWARS		   = 'PaintWars';
		const KEYSPACE_PW_CURRENT_WINNERS  = 'CurrentWeekWinners';
		const KEYSPACE_PW_PREV_WINNERS     = 'PreviousWeekWinners';


		//External invites
		const KEYSPACE_EXT_INVITE_SENT_SMS   = 'EInvSent:Sms'; //external invite flood control for sender
		const KEYSPACE_EXT_INVITE_SENT_EMAIL = 'EInvSent:Email'; //external invite flood control for sender
		const KEYSPACE_EXT_INVITE_RECEIVED   = 'EInvReceived'; //external invite flood control for receiver
		const KEYSPACE_EXTERNAL_INVITE       = 'EInv:'; // external invite hash, data EInv:<hash>

		//merchant performance
		const KEYSPACE_MERCHANT_PERFORMANCE = 'MerchantPerformance';
		// top merchant achivements for prev month(merchant centre dashboard) --leaderboard
		const KEYSPACE_TOP_MERCHANT_ACHIEVEMENT_ALL = 'TopMerchAchivement:All';
		const KEYSPACE_TOP_MERCHANT_ACHIEVEMENT = 'TopMerchAchivement'; // TopMerchAchivement:<countryid>

		// get popular groups, chatrooms for merchant for dashboard
		const KEYSPACE_MERCHANT_POPULAR_GROUPS = 'MerchPopGroups'; //-directory
		const KEYSPACE_MERCHANT_POPULAR_CHATROOMS = 'MerchPopChatrooms'; //-leaderboard

		// top tagged and untagged users - merchant dashboard -- directory
		const KEYSPACE_TOP_TAG_ACTIVE_USERS_CURRENT = 'TopTagActiveUsersCur';
		const KEYSPACE_TOP_TAG_ACTIVE_USERS_PREVIOUS = 'TopTagActiveUsersPre';
		const KEYSPACE_TOP_TAG_INACTIVE_USERS = 'TopTagInactiveUsers';
		const KEYSPACE_MERCHANT_TAG_ACTIVITY_STATS = 'MerchantTagActivityStats';

		// KEYSPACES DEVELOPER CENTER
		const KEYSPACE_DEVSITE_ROLLBACK_TOKEN = 'DevSite:RollbackToken:';

		// KEYSPACE CR AUTH TEMP SESSION. temporary session id for challenge response user authentication
		const KEYSPACE_CR_AUTH_TEMP_SESSIONID = 'TS:CRAuthSid:';

		/**
		 * Returns a Predis instance connected to the master Redis instance assigned to store data for the given user
		 * @throws RedisException
		 * @throws Exception
		 * @return Predis_Client|string
		 **/
		public static function get_master_instance_for_user_id($user_id, $options=array())
		{
			if (empty($user_id))
				throw new RedisException('User ID not specified');
			return self::get_instance(self::KEYSPACE_SHARD_DIRECTORY_REDIS_USER_ID . $user_id, self::MASTER, $options);
		}

		/**
		 * Returns a Predis instance connected to a Redis slave instance assigned to store data for the given user
		 * @throws RedisException
		 * @throws Exception
		 * @return Predis_Client|string
		 **/
		public static function get_slave_instance_for_user_id($user_id, $options=array())
		{
			if (empty($user_id))
				throw new RedisException('User ID not specified');
			return self::get_instance(self::KEYSPACE_SHARD_DIRECTORY_REDIS_USER_ID . $user_id, self::SLAVE, $options);
		}

		/**
		 * Returns a Predis instance connected to the master Redis instance assigned to store data for the given group
		 * @throws RedisException
		 * @throws Exception
		 * @return Predis_Client|string
		 **/
		public static function get_master_instance_for_group_id($group_id, $options=array())
		{
			if (empty($group_id))
				throw new RedisException('Group ID not specified');
			return self::get_instance(self::KEYSPACE_SHARD_DIRECTORY_REDIS_GROUP_ID . $group_id, self::MASTER, $options);
		}

		/**
		 * Returns a Predis instance connected to a Redis slave instance assigned to store data for the given group
		 * @throws RedisException
		 * @throws Exception
		 * @return Predis_Client|string
		 **/
		public static function get_slave_instance_for_group_id($group_id, $options=array())
		{
			if (empty($group_id))
				throw new RedisException('Group ID not specified');
			return self::get_instance(self::KEYSPACE_SHARD_DIRECTORY_REDIS_GROUP_ID . $group_id, self::SLAVE, $options);
		}

		/**
		 * Returns a Predis instance connected to the master Redis instance assigned to store data for the given chat room
		 * @throws RedisException
		 * @throws Exception
		 * @return Predis_Client|string
		 **/
		public static function get_master_instance_for_chat_room_id($chat_room_id, $options=array())
		{
			if (empty($chat_room_id))
				throw new RedisException('Chat Room ID not specified');
			return self::get_instance(self::KEYSPACE_SHARD_DIRECTORY_REDIS_CHAT_ROOM_ID . $chat_room_id, self::MASTER, $options);
		}

		/**
		 * Returns a Predis instance connected to a Redis slave instance assigned to store data for the given chat room
		 * @throws RedisException
		 * @throws Exception
		 * @return Predis_Client|string
		 **/
		public static function get_slave_instance_for_chat_room_id($chat_room_id, $options=array())
		{
			if (empty($chat_room_id))
				throw new RedisException('Chat Room ID not specified');
			return self::get_instance(self::KEYSPACE_SHARD_DIRECTORY_REDIS_CHAT_ROOM_ID . $chat_room_id, self::SLAVE, $options);
		}

		/**
		 * Returns a Predis instance connected to the master Leaderboards Redis instance
		 * @throws Predis_ClientException
		 * @return Predis_Client|string
		 **/
		public static function get_master_instance_for_leaderboards($options=array())
		{
			global $redis_leaderboards_master_server;

			if(isset($options['as_uri']) && $options['as_uri'])
			{
				return 'redis://'.$redis_leaderboards_master_server;
			}

			if (is_null(self::$leaderboards_master))
			{
				self::$leaderboards_master = new Predis_Client('redis://'.$redis_leaderboards_master_server.self::$redis_parameters_query);
			}

			return self::$leaderboards_master;
		}


		/**
		 * Returns a Predis instance connected to the slave Leaderboards Redis instance
		 * @throws Predis_ClientException
		 * @return Predis_Client|string
		 **/
		public static function get_slave_instance_for_leaderboards($options=array())
		{
			global $redis_leaderboards_slave_server;

			if(isset($options['as_uri']) && $options['as_uri'])
			{
				return 'redis://'.$redis_leaderboards_slave_server;
			}

			if (is_null(self::$leaderboards_slave))
			{
				self::$leaderboards_slave = new Predis_Client('redis://'.$redis_leaderboards_slave_server.self::$redis_parameters_query);
			}

			return self::$leaderboards_slave;
		}


		/**
		 * Returns a Predis instance connected to the master Games Redis instance
		 * @throws Predis_ClientException
		 * @return Predis_Client|string
		 **/
		public static function get_master_instance_for_games($options=array())
		{
			global $redis_games_master_server;

			if(isset($options['as_uri']) && $options['as_uri'])
			{
				return 'redis://'.$redis_games_master_server;
			}

			if (is_null(self::$games_master))
			{
				self::$games_master = new Predis_Client('redis://'.$redis_games_master_server);
			}

			return self::$games_master;
		}

		/**
		 * Returns a Predis instance connected to the slave Games Redis instance
		 * @throws Predis_ClientException
		 * @return Predis_Client|string
		 **/
		public static function get_slave_instance_for_games($options=array())
		{
			global $redis_games_slave_server;

			if(isset($options['as_uri']) && $options['as_uri'])
			{
				return 'redis://'.$redis_games_slave_server;
			}

			if (is_null(self::$games_slave))
			{
				self::$games_slave = new Predis_Client('redis://'.$redis_games_slave_server);
			}

			return self::$games_slave;
		}

		private static function get_directory_slave_instance()
		{
			global $redis_shard_directory_slave_server;

			if (self::$shard_directory_slave == NULL)
				self::$shard_directory_slave = new Predis_Client('redis://' . $redis_shard_directory_slave_server);

			return self::$shard_directory_slave;
		}

		private static function get_directory_master_instance()
		{
			global $redis_shard_directory_master_server;

			if (self::$shard_directory_master == NULL)
				self::$shard_directory_master = new Predis_Client('redis://' . $redis_shard_directory_master_server);

			return self::$shard_directory_master;
		}

		private static function get_shard_settings()
		{
			if (self::$num_get_settings_try > self::MAX_GET_SETTINGS_TRIES)
			{
				throw new RedisException("Unable to get redis cluster shard settings");
			}

			if (is_null(self::$shard_settings))
			{
				try
				{
					$settings = XCache::getInstance()->get
					(
						XCache::KEYSPACE_REDIS_SHARD_SETTINGS
						, array(__CLASS__, 'get_shard_settings_from_source')
						, 600
						, array(
							'fetch_without_lock' => true
							, 'set_without_lock' => true
						)
					);

					if (
							   empty($settings)
							|| empty($settings['shard_masters'])
							|| empty($settings['shard_slaves'])
							|| empty($settings['shard_weights'])
					)
					{
						// Something's wrong! XCache data is potentially invalid or corrupt, delete the key now
						XCache::getInstance()->delete(XCache::KEYSPACE_REDIS_SHARD_SETTINGS);

						$msg = "Unable to retrieve valid redis cluster settings from XCache!";
						error_log($msg);

						// we make no attempt at recovery for now, throw an exception and be done
						throw new RedisException($msg);
					}
				}
				catch(RedisException $re)
				{
					if (self::$num_get_settings_try++ < self::MAX_GET_SETTINGS_TRIES)
					{
						return self::get_shard_settings();
					}

					throw $re;
				}

				// although the data is stored in cache as plain arrays
				// we format a valid instance of the class RedisShardSettings
				// it's easier to handle in good IDEs :p
				self::$shard_settings = new RedisShardSettings();
				self::$shard_settings->shard_masters     = $settings['shard_masters'];
				self::$shard_settings->shard_slaves      = $settings['shard_slaves'];
				self::$shard_settings->shard_weights     = $settings['shard_weights'];
				self::$shard_settings->sum_shard_weights = $settings['sum_shard_weights'];
			}

			return self::$shard_settings;
		}

		/**
		 * This function returns the shard settings from the authoritative source
		 * Function MUST be public because it is used a callback in get_shard_settings above
		 **/
		public static function get_shard_settings_from_source()
		{
			// Make sure we have a connection to the shard directory slave
			$directory_slave = self::get_directory_slave_instance();

			$redis_result = $directory_slave->pipeline()
					->hgetall(self::KEYSPACE_SHARD_DIRECTORY_REDIS_SHARD_MASTERS)
					->hgetall(self::KEYSPACE_SHARD_DIRECTORY_REDIS_SHARD_SLAVES)
					->lrange(self::KEYSPACE_SHARD_DIRECTORY_REDIS_SHARD_WEIGHTS, 0, -1)
					->execute();

			$masters         = $redis_result[0];
			$slaves          = $redis_result[1];
			$weightAsStrings = $redis_result[2];

			// !!!HACK CHECK!!! we know there must ALWAYS be more than one shard
			$count_masters = count($masters);
			$count_slaves  = count($slaves);
			$count_weights = count($weightAsStrings);

			if (
				   empty($masters)
				|| empty($slaves)
				|| empty($weightAsStrings)
				|| $count_masters <= 1
				|| $count_slaves <= 1
				|| $count_weights <= 1
				|| $count_masters != $count_slaves
				|| $count_masters != $count_weights
			)
			{
				$msg = "Retrieved invalid cluster settings from redis directory";
				error_log("ERROR: $msg: ".print_r($redis_result, true));
				throw new RedisException($msg);
			}


			// All tests pass, we now set the weight types correctly and pre-compute the sum of the weights
			// Note: we store in xcache the data as simple arrays (NOT an instance of a classe)
			$settings = array
			(
				  'shard_masters' => $masters
				, 'shard_slaves'  => $slaves
				, 'shard_weights' => array_map('intval', $weightAsStrings)
			);
			$settings['sum_shard_weights'] = array_sum($settings['shard_weights']);

			return $settings;
		}

		/**
		 * This function returns a Predis instance connected to a Redis instance.
		 * The $masterOrSlave parameter must be self::MASTER or self::SLAVE
		 * @return Predis_Client|string
		 **/
		private static function get_instance($shard_directory_key, $masterOrSlave, $options=array())
		{
			// check options
			$as_uri = isset($options['as_uri']) && $options['as_uri'];
			$allow_cache = !(isset($options['no_cache']) && $options['no_cache']);

			// Make sure we have a connection to the shard directory slave
			self::get_directory_slave_instance();
			self::get_shard_settings();

			// TODO: Investigate optimizing the current process of getting the shard ID, followed by getting the shard host:port.
			//       Q. Can this be done in a single Redis call, rather than two?
			//       A. Not yet, but possibly in a future version of Redis
			//            - see http://groups.google.com/group/redis-db/browse_thread/thread/d7ec7df97d89ec89#

			// First, we need the shard id
			$shard_id = null;
			if ($allow_cache && isset(self::$cache_keys_to_shard_ids[$shard_directory_key]))
			{
				$shard_id = self::$cache_keys_to_shard_ids[$shard_directory_key];
			}
			else
			{
				$shard_id = self::$shard_directory_slave->get($shard_directory_key);

				if (is_null($shard_id) || empty($shard_id))
				{
					// No shard has been assigned for the given $shard_directory_key yet.
					// If the caller wanted a master connection, we will assign a shard now.
					// If they wanted a slave connection we'll just return NULL
					if ($masterOrSlave != self::MASTER)
						return NULL;

					$shard_id = self::get_new_shard_id($shard_directory_key, $allow_cache);
				}

				if ($allow_cache)
				{
					// add the newly found shard_id to local cache
					self::$cache_keys_to_shard_ids[$shard_directory_key] = $shard_id;
				}
			}


			// assignment by reference is faster!
			if ($masterOrSlave == self::MASTER)
			{
				$servers =& self::$shard_settings->shard_masters;
			}
			else
			{
				$servers =& self::$shard_settings->shard_slaves;
			}

			if (!isset($servers[$shard_id]))
			{
				$msg = "Unable to find address of redis shard $shard_id";
				error_log("ERROR: $msg - Key: $shard_directory_key - MasterOrSlave: $masterOrSlave");
				throw new Exception($msg);
			}

			$shard_uri = 'redis://' . $servers[$shard_id];

			return $as_uri ? $shard_uri : new Predis_Client($shard_uri.self::$redis_parameters_query);
		}

		/**
		 * This function is used to obtain a new shard ID onto which new data will be stored.
		 *
		 * We select a random shard ID based on the "weight" parameter of each shard.
		 *   e.g. if we have four shards:
		 *          - Shard 1 with weight 3
		 *          - Shard 2 with weight 4
		 *          - Shard 3 with weight 5
		 *          - Shard 4 with weight 0
		 *        We will select shard 1 25% of the time, shard 2 33.3% of the time, shard 3 41.6% of the time,
		 *        and never select shard 4.
         *
         *   See the algorithm used here: http://20bits.com/articles/random-weighted-elements-in-php/
         *
		 * We use a distributed lock here to ensure that any concurrent requests for a shard ID for a given $id,
		 * from anywhere in the system, will not return different shard IDs.
		 **/
		private static function get_new_shard_id($shard_directory_key, $allow_weight_cache=true)
		{
			$lock_id = 'get_new_shard:' . $shard_directory_key;

			Memcached::get_instance()->get_distributed_lock($lock_id);

			// Make sure we have a connection to the shard directory master
			self::get_directory_master_instance();

			// Double-check the shard directory now (in case another process grabbed the lock first and assigned a shard ID)
			$shard_id = self::$shard_directory_master->get($shard_directory_key);
			if (!empty($shard_id)) {
				Memcached::get_instance()->release_distributed_lock($lock_id);
				return $shard_id;
			}

			// We will now select a new shard
			$r = mt_rand(1, self::$shard_settings->sum_shard_weights);
			$offset = 0;
			foreach (self::$shard_settings->shard_weights as $k => $weight) {
				$offset += $weight;
				if ($r <= $offset) {
					// We have selected a shard
					$shard_id = $k + 1;
					self::$shard_directory_master->set($shard_directory_key, $shard_id);
					Memcached::get_instance()->release_distributed_lock($lock_id);
					return $shard_id;
				}
			}

			// If we got here, something is seriously wrong
			Memcached::get_instance()->release_distributed_lock($lock_id);
			error_log("redis.php: get_new_shard_id('$shard_directory_key') could not locate a shard");
			throw new RedisException('Unable to assign new shard ID');
		}

		/**
		 * This function will
		 *   1. Append an element to the tail (right) of the List at $key, and
		 *   2. Trim old items off the list (if necessary) so it is at most $max_elements long
		 **/
		public static function append_to_list_and_trim($redis_instance, $key, $value, $max_elements)
		{
			$redis_instance->pipeline()
				->rpush($key, $value)
				->ltrim($key, $max_elements)
				->execute();
		}

		/**
		 * This function will
		 *   1. Add an element to a Sorted Set, using the current time as the score, and
		 *   2. Trim the Sorted Set to be at most $max_elements elements long.
		 * We use a Unix timestamp (no. seconds since Unix epoch) for the score.
		 */
		public static function add_to_sorted_set_with_timestamp_and_trim_by_size($redis_instance, $key, $value, $max_elements)
		{
            $redis_instance->pipeline()
				->zadd($key, time(), $value)
				->zremrangebyrank($key, 0, -$max_elements - 1)
				->execute();
		}

		/**
		 * This function will
		 *   1. Add an element to a Sorted Set, using the current time as the score, and
		 *   2. Trim the Sorted Set, removing all elements that are more than $max_age_seconds old.
		 * We use a Unix timestamp (no. seconds since Unix epoch) for the score.
		 */
		public static function add_to_sorted_set_with_timestamp_and_trim_by_age($redis_instance, $key, $value, $max_age_seconds, $set_expiry=false)
		{
			$now = time();

			$pipe = $redis_instance->pipeline()
				->zremrangebyscore($key, '-inf', $now - $max_age_seconds)
				->zadd($key, $now, $value);

			if ($set_expiry)
			{
				$pipe->expire($key, $max_age_seconds);
			}

			return $pipe->execute();
        }

        /**
         * Acquire a redis-based distributed lock
         * (All distributed locks will be on the redis directory master)
         */
        public static function get_distributed_lock($lock_id, $lock_ttl=30)
        {
            // Make sure we have a connection to the shard directory master
            self::get_directory_master_instance();

            $lock_key = self::KEYSPACE_DISTRIBUTED_LOCK . $lock_id;

            while (!self::$shard_directory_master->setnx($lock_key, 1))
            {
                    usleep(50000); // sleep for 50ms
            }

            // we have the lock, make it expire (in case application crashes before releasing)
            if ($lock_ttl > 0)
            {
                    self::$shard_directory_master->expire($lock_key, $lock_ttl);
            }
        }

        /**
         * Release a redis-based distributed lock
         */
        public static function release_distributed_lock($lock_id)
        {
            // Make sure we have a connection to the shard directory master
            self::get_directory_master_instance();

            self::$shard_directory_master->del(self::KEYSPACE_DISTRIBUTED_LOCK . $lock_id);
        }
	}
?>
