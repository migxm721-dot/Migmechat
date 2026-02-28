<?php
require_once(get_framework_common_directory() . "/database.php");
fast_require("DAO", get_dao_directory() . "/dao.php");
fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
fast_require("UserDetail", get_domain_directory() . "/user/user_detail.php");
fast_require("UserProfile", get_domain_directory() . "/user/user_profile.php");
fast_require("EmoticonPack", get_domain_directory() . "/store/emoticon_pack.php");
fast_require("ContactDetail", get_domain_directory() . "/contact/contact_detail.php");
fast_require("Redis", get_framework_common_directory() . "/redis.php");
fast_require('CaptchaDomain', get_domain_directory() . '/captcha/captcha.php');
fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");
fast_require("OpensocialDAO", get_dao_directory() . "/opensocial_dao.php");
fast_require('MerchantDAO', get_dao_directory() . '/merchant_dao.php');
fast_require("ThirdPartyApplicationDAO", get_dao_directory() . "/third_party_application_dao.php");
fast_require('Application', get_domain_directory() . '/third_party_application/application.php');
fast_require('UserCapability', get_domain_directory() . '/capability/user_capability.php');

class UserDAO extends DAO
{
	public static $MEM_HEADER_CONTACT_LIST = 'user_contacts_';
	public static $MEM_HEADER_CONTACT_LIST_USERNAME = 'user_contacts_username_';
	public static $MEM_HEADER_CONTACT_LIST_USERNAME_AND_NUMBER = 'user_contacts_with_number_';

	public static $MEM_EXPIRY = 1200; //20 mins

	public function get_user_id($username)
	{
		return $this->get_userid($username);

	}

	public function mig33_user_exists($username)
	{
		return $this->get_user_id(trim($username)) > 0;
	}

	/**
	 * function get_user_by_mbile_phone
	 *
	 **/
	public function get_user_by_mobile_phone($mobile_phone)
	{

		$username = null;

		$query = 'SELECT username FROM user WHERE mobilephone=?';

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param('s', $mobile_phone);
		$stmt->execute();

		$stmt->bind_result($user);
		while ($stmt->fetch()) {
			$username = $user;
		}
		$stmt->close();
		$this->closeSlaveConnection();

		return $username;

	}

	//
	// this function  disables a user permanently by setting its status to 0
	//
	public function disable_user_permanently($username, $reason)
	{
		if (empty($username) || empty($reason)) return false;

		$query = "update user set status=0, notes=CONCAT(IFNULL(notes,''),?) where username=? and status=1";

		$stmt = $this->getMasterConnection()->get_prepared_statement($query);
		$stmt->bind_param('ss', $reason, $username);
		$stmt->execute();
		$affected_rows = $stmt->affected_rows;
		$stmt->close();
		$this->closeMasterConnection();

		if ($affected_rows) {
			return true;
		} else {
			return false;
		}
	}

	protected static $user_details = array(
		'username' => array()
	, 'user_id' => array()
	, 'mobile_phone' => array()
	, 'email_address' => array()
	);

	/**
	 * @param string|integer $identifier
	 * @param string $method
	 * @return UserDetail
	 */
	public function get_user_detail($identifier, $method = 'username')
	{
		if (array_key_exists($identifier, self::$user_details[$method])) {
			parent::$memorization_cache['userdetail']['hit']++;
			return self::$user_details[$method][$identifier];
		}
		parent::$memorization_cache['userdetail']['miss']++;

		$query = 'select u.*, uid.id as uid from user u inner join userid uid on u.username = uid.username';

		switch ($method) {
			case 'user_id':
				$query .= ' where uid.id = ?';
				$param_type = 'i';
				break;

			case 'mobile_phone':
				$query .= ' where u.mobilephone = ?';
				$param_type = 's';
				break;

			case 'email_address':
				return null;
				break;

			case 'username':
			default:
				$query .= ' where u.username = ?';
				$param_type = 's';
				break;
		}

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param($param_type, $identifier);
		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

		$userData = null;
		if ($stmt->fetch()) {
			$userData = new UserDetail($row);
		}

		$stmt->close();
		$this->closeSlaveConnection();

		self::$user_details[$method][$identifier] = $userData;
		return self::$user_details[$method][$identifier];
	}

	public function is_valid_alias($userid, $alias)
	{
		return ($this->get_username($userid) == $alias || strlen($alias) >= 6)
			&& strlen($alias) <= 20
			&& preg_match('/^[a-zA-Z](\.?[\w-])+$/', $alias);
	}

	public function is_valid_alias_via_rest($userid, $alias)
	{
		fast_require('AccountAlias', get_domain_directory() . '/settings/account_alias.php');
		$account_alias = new AccountAlias($userid);
		try {
			$data = $account_alias->check($alias);
			return $data['is_valid_alias'];
		}
		catch (Exception $e) {
			return false;
		}
	}

	public function get_user_alias($userid)
	{
		fast_require('AccountAlias', get_domain_directory() . '/settings/account_alias.php');
		$alias = new AccountAlias($userid);
		return $alias->alias;
	}

	public function set_user_alias($userid, $alias)
	{
		fast_require('AccountAlias', get_domain_directory() . '/settings/account_alias.php');
		$setalias = new AccountAlias($userid);
		$setalias->set_user_setting('alias', $alias);
		return $setalias->save_settings();
	}

	/**
	 * @param $user_id
	 * @return UserDetail
	 */
	public function get_user_detail_from_id($user_id)
	{
		return $this->get_user_detail($user_id, 'user_id');
	}

	/**
	 * @param $mobile_phone
	 * @return UserDetail
	 */
	public function get_user_detail_from_mobilephone($mobile_phone)
	{
		return $this->get_user_detail($mobile_phone, 'mobile_phone');
	}

	/**
	 * @param $email_address
	 * @return UserDetail
	 */
	public function get_user_detail_from_emailaddress($email_address)
	{
		return $this->get_user_detail($email_address, 'email_address');
	}

	public function get_user_profile_privacy_status($username)
	{
		$query = "select status from userprofile where username=?";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param("s", $username);
		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

		$stmt->fetch();
		$stmt->close();
		$this->closeSlaveConnection();

		$status = get_value_from_array("status", $data, "integer", 3);
		switch ($status) {
			case 1:
				return "PUBLIC";
			case 2:
				return "CONTACTS_ONLY";
			default:
				return "PRIVATE";
		}
	}

	public function get_contact_detail($session_user, $username)
	{
		$query = "select * from contact where username=? and fusionUsername=?";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param("ss", $session_user, $username);
		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

		$stmt->fetch();
		$stmt->close();
		$this->closeSlaveConnection();

		$contact_detail = new ContactDetail($data, true);

		$contact_detail->privacy = $this->get_user_profile_privacy_status($username);
		return $contact_detail;
	}

	public function get_contact_detail_from_id($contact_id)
	{
		$query = "select * from contact where id=?";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param("i", $contact_id);
		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

		$stmt->fetch();
		$stmt->close();
		$this->closeSlaveConnection();

		$contact_detail = new ContactDetail($data, true);

		$contact_detail->privacy = $this->get_user_profile_privacy_status($contact_detail->username);
		return $contact_detail;
	}

	public function get_user_profile($session_user, $username)
	{
		/*
		$query = "select * from userprofile where username=?";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param("s", $username);
		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

		$stmt->fetch();
		$stmt->close();
		$this->closeSlaveConnection();

		return new UserProfile($data, false);
		*/
		try {
			$user_profile = soap_call_ejb('loadUserProfile', array($session_user, $username));
			return new UserProfile($user_profile, true);
		}
		catch (Exception $e) {
			return null;
		}
	}

	public function get_user_profile_migbo($session_user_id)
	{
		try {
			fast_require('MigboDatasvc', get_library_directory() . '/fusion/migbo_datasvc.php');
			$user_profile = MigboDatasvc::get_instance()->get(
				sprintf(MigboDatasvc::GET_USER, $session_user_id)
				);
			return new UserProfile($user_profile, false);
		}
		catch (Exception $e) {
			return null;
		}
	}

	public function get_user_level($username)
	{
		$key = 'LEVEL/' . $username;
		$memcache = Memcached::get_instance();
		$result = $memcache->get($key);

		if (empty($result) || $result == FALSE) {
			$query = "SELECT * FROM reputationscoretolevel WHERE level = (
							SELECT IF(MAX(level) IS NULL, 1, MAX(level)) FROM reputationscoretolevel WHERE score <= (
							SELECT score FROM userid u, score s
							WHERE u.id = s.userid AND u.username=?))";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();
			$result = array("level" => get_value_from_array("Level", $data, "integer", 0),
				"name" => get_value_from_array("Name", $data),
				"image" => get_value_from_array("Image", $data),
				"create_chat_room" => get_value_from_array("CreateChatRoom", $data, "integer", 0),
				"chat_room_size" => get_value_from_array("ChatRoomSize", $data, "integer", 0),
				"create_group" => get_value_from_array("CreateGroup", $data, "integer", 0),
				"group_size" => get_value_from_array("GroupSize", $data, "integer", 0),
				"num_group_moderators" => get_value_from_array("NumGroupModerators", $data, "integer", 0),
				"group_storage_size" => get_value_from_array("GroupStorageSize", $data, "integer", 0),
				"num_group_chat_rooms" => get_value_from_array("NumGroupChatRooms", $data, "integer", 0),
				"publish_photo" => get_value_from_array("PublishPhoto", $data, "integer", 0),
				"post_comment_like_user_wall" => get_value_from_array("PostCommentLikeUserWall", $data, "integer", 0),
				"add_to_photo_wall" => get_value_from_array("AddToPhotoWall", $data, "integer", 0),
				"enter_pot" => get_value_from_array("EnterPot", $data, "integer", 0),
				"use_display_picture" => get_value_from_array("UseDisplayPicture", $data, "integer", 0)
			);

			$memcache->add_or_update($key, $result, 3600);
		}

		return $result;
	}

	public function get_user_level_and_reputation_level_permission($username, $reputation_level_permission_action)
	{
		$user_level = $this->get_user_level($username);
		$user_level = $user_level["level"];

		$key = 'PERMISSION/' . $reputation_level_permission_action;
		$memcache = Memcached::get_instance();
		$required_level = $memcache->get($key);

		if (empty($required_level)) {
			$query = sprintf("SELECT MIN(IF(%s = 1, level, null)) basemiglevel FROM reputationscoretolevel", $this->getSlaveConnection()->escape_string($reputation_level_permission_action));

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			$required_level = get_value_from_array("basemiglevel", $data, "integer", 0);

			$memcache->add_or_update($key, $required_level, 600);
		}

		return array("user_level" => $user_level, "required_level" => $required_level);
	}

	public function like_user($session_username, $liked_username, $like = 1)
	{
		if ($like != 1 && $like != -1)
			return;

		// User must have a sufficient Level to like another user
		$reputation_level_permission = $this->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
		if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
			throw new Exception(sprintf(_('You must be Level %s or higher to like another user'), $reputation_level_permission['required_level']));

		$session_userid = $this->get_userid($session_username);
		if (!isset($session_userid))
			return;

		$liked_userid = $this->get_userid($liked_username);
		if (!isset($liked_userid))
			return;


		// JIRA-1005 Disable receiving of likes for non-authenticated users
		//
		// Note: 'sending of likes' is governed by model/common/user_permissions.php and therefore is not validated here
		//
		//	$system_property = SystemProperty::get_instance();
		//		$receive_likes_disabled_for_unauth = $system_property->get_boolean(SystemProperty::UserLikesReceiveDisabledForUnauthenticatedUsers, false);

		if (!UserCapability::has_authenticated_access_by_username($liked_username, "RECEIVE_USER_LIKE")) //if (false == $this->is_user_authenticated($liked_username) && true == $receive_likes_disabled_for_unauth)
		{
			throw new Exception($liked_username . " is not able to receive likes since the account is not yet authenticated.");
		}

		if ($session_userid != $liked_userid) {
			$new_num_likes = 0;
			try {
				$redis_instance = Redis::get_master_instance_for_user_id($liked_userid);
				if (!isset($redis_instance)) {
					throw new Exception ("master redis instance for user $liked_userid not found");
				}

				$key = Redis::KEYSPACE_ENTITY_USER . $liked_userid . Redis::KEYSPACE_LIKES_USER;
				$like_added = $redis_instance->sadd($key, $session_userid);
				$new_num_likes = $redis_instance->scard($key);
				$redis_instance->disconnect();

				// if the like was counted, we must increase the leaderboard
				if ($like_added) {
					// real-time leaderboard update for userlikes :/
					fast_require("LeaderboardDAO", get_dao_directory() . "/leaderboard_dao.php");
					$lb_dao = new LeaderboardDAO();
					// call below increase the count for the weekly and daily boards
					$lb_dao->increase_board_score_for(Redis::KEYSPACE_LB_USER_LIKES, $liked_username, $liked_userid, 1);
					// call below updates the total num_likes for all time for that user
					$lb_dao->set_all_time_board_score_for(Redis::KEYSPACE_LB_USER_LIKES, $liked_username, $liked_userid, $new_num_likes);
				}

			}
			catch (Exception $e) {
				error_log("like_user(): " . $e->getMessage() . "; Params: $liked_user_id, $session_userid");
				if (isset($redis_instance)) $redis_instance->disconnect();
			}

			// Note, Tim 29/11/2010: the code used to return NULL on exception,
			// I'm changing to return 0, to be a non-breaking error :/

			return array(
				'num_likes' => $new_num_likes,
				'num_dislikes' => 0 // this is here for legacy reasons, if unused, we can delete
			);
		}
	}

	public function get_user_likes($username)
	{
		$res = array("num_likes" => 0, "num_dislikes" => 0);

		try {
			$userid = $this->get_userid($username);
			if (!isset($userid) || empty($userid))
				return $res;

			$redis_instance = Redis::get_slave_instance_for_user_id($userid);
			if (!isset($redis_instance)) {
				// this probably mean we are hitting a new user,
				// which has not been set in the redis directory yet
				// in which case, we just return the default zero
				return $res;
			}
			$res['num_likes'] = $redis_instance->scard(Redis::KEYSPACE_ENTITY_USER . $userid . Redis::KEYSPACE_LIKES_USER);
			$redis_instance->disconnect();

		}
		catch (Exception $e) {
			error_log("get_user_likes(): " . $e->getMessage() . "; Params: $username");
			if (isset($redis_instance)) $redis_instance->disconnect();
		}

		return $res;
	}

	public function get_user_account_balance($username)
	{
		$result = make_soap_call("getAccountBalance", array($username));
		$data = $result->data;

		return array('currency' => get_value_from_array("currency.code", $data, "string", "US$"),
			'balance' => get_value_from_array("balance", $data, "float", 0));
	}

	/**
	 *
	 *    Get the contact list of the session user, returning only the username
	 *    Cache in memcache for 20 minutes. Use at your own risk if you need the most up to date contact list
	 *
	 **/
	public function get_contact_list($username)
	{
		$memcache = Memcached::get_instance();
		$user_contacts = $memcache->get(UserDao::$MEM_HEADER_CONTACT_LIST . $username);

		if (empty($user_contacts)) {
			$query = "SELECT t.dn DisplayName, t.mp MobilePhone, t.fu FusionUsername, uid.id ID
							FROM
							userid uid
							RIGHT JOIN
							(SELECT c.displayname as dn, c.mobilephone as mp, c.fusionusername AS fu, c.username as u
							FROM contact c
							WHERE
							c.username = ? AND
							(c.fusionusername IS NOT NULL OR c.mobilephone IS NOT NULL)) t
							ON uid.username=t.fu ORDER BY t.dn ASC";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			//$stmt->bind_result($name);

			$user_contacts = array();

			while ($stmt->fetch()) {
				$user_contacts[] = new ContactDetail($row, true);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			//Put into cache
			$memcache->add_or_update(UserDao::$MEM_HEADER_CONTACT_LIST . $username, $user_contacts, UserDao::$MEM_EXPIRY);

			return $user_contacts;
		}

		return $user_contacts;
	}

	/**
	 *
	 *    Remove contact list cache
	 *
	 **/
	public function reset_contact_list_cache($username)
	{
		$memcache = Memcached::get_instance();
		$memcache->remove_item(UserDao::$MEM_HEADER_CONTACT_LIST . $username);
	}

	/**
	 *
	 *
	 **/
	public function get_contact_list_mig33_users_only($username)
	{
		$memcache = Memcached::get_instance();
		$user_contacts = $memcache->get(UserDao::$MEM_HEADER_CONTACT_LIST_USERNAME . $username);

		if (empty($user_contacts)) {
			$query = "SELECT fusionusername as username FROM contact WHERE username = ? AND fusionusername IS NOT NULL ORDER by fusionusername ASC";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->bind_result($name);

			$user_contacts = array();

			while ($stmt->fetch()) {
				$user_contacts[] = $name;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			//Put into cache
			$memcache->add_or_update(UserDao::$MEM_HEADER_CONTACT_LIST_USERNAME . $username, $user_contacts, UserDao::$MEM_EXPIRY);

			return $user_contacts;
		}

		return $user_contacts;
	}


	/**
	 *
	 *    Get the contact list of the session user, returning only the username and phone number
	 *    Cache in memcache for 20 minutes. Use at your own risk if you need the most up to date contact list. This is not meddling with object cache
	 *
	 **/
	public function get_contact_list_with_phone_number($username)
	{
		$memcache = Memcached::get_instance();
		$contacts = $memcache->get(UserDao::$MEM_HEADER_CONTACT_LIST_USERNAME_AND_NUMBER . $username);

		if (empty($contacts)) {
			$query = "SELECT DisplayName, FusionUsername, MobilePhone FROM contact WHERE username = ? AND mobilephone IS NOT NULL ORDER BY DisplayName";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$contacts = array();

			while ($stmt->fetch()) {
				$contacts[] = new ContactDetail($row, true);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			//Put into cache
			$memcache->add_or_update(UserDao::$MEM_HEADER_CONTACT_LIST_USERNAME_AND_NUMBER . $username, $contacts, UserDao::$MEM_EXPIRY);

			return $contacts;
		}

		return $contacts;
	}

	public function get_user_referral_count($username, $month = 0, $year = 0)
	{

		$monthYear = ($month > 0 && $year > 0) ? $month . ' ' . $year : '';

		$query = '  SELECT COUNT(id) AS referrals
                        FROM userreferral
                        WHERE username = ? ';

		if ($monthYear)
			$query .= "AND DATE_FORMAT(datecreated, '%c %Y') = ? ";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		if ($monthYear)
			$stmt->bind_param("ss", $username, $monthYear);
		else
			$stmt->bind_param("s", $username);
		$stmt->execute();
		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
		$stmt->fetch();

		$stmt->close();
		$this->closeSlaveConnection();

		return get_value_from_array("referrals", $data, "integer", 0);

	}

	public function get_emoticon_packs($username, $current_page, $number_entries)
	{
		$query = "SELECT epo.emoticonpackid AS id,
        					 si.name AS name,
        					 epo.username AS username,
        					 epo.status AS status,
        					 si.previewimage AS preview_image
        				FROM emoticonpackowner epo,
        					 storeitem si
        			   WHERE epo.username = ?
        			     AND epo.emoticonpackid = si.referenceid
        			     AND si.type = 3";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param('s', $username);
		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

		$stmt->store_result();
		$stmt->data_seek(($current_page - 1) * $number_entries);
		$count = 1;

		$emoticon_packs = array();
		while ($stmt->fetch() && $count <= $number_entries) {
			$emoticon_packs[] = new EmoticonPack($row);
			$count += 1;
		}

		$num_rows = $stmt->num_rows();
		$total_pages = ceil($num_rows / $number_entries);

		$stmt->close();
		$this->closeSlaveConnection();

		return array('emoticon_packs' => $emoticon_packs, 'total_pages' => $total_pages);
	}

	public function get_all_friends($username, $get_only_usernames = false)
	{
		$friends = array();
		$friend = null;

		$query = "
				SELECT
					uid.id as id
					, b.broadcastusername AS username
				FROM
					broadcastlist b
					, userid uid
				WHERE
					b.username = ?
					AND b.broadcastusername = uid.username
			";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param('s', $username);
		$stmt->execute();
		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $friend);

		while ($stmt->fetch()) {
			if (!$get_only_usernames) {
				$friends[] = array
				(
					'id' => $friend['id']
				, 'username' => $friend['username']
				);
			} else {
				$friends[] = $friend['username'];
			}
		}

		$stmt->close();
		$this->closeSlaveConnection();

		return $friends;
	}

	/**
	 *  retrieves list of userid of the friends of a user
	 *  this query uses the broadcastlist table in fusion
	 *
	 * @username - the user whose friends we want to retrieve
	 * @last_seen - starts from 0, integer. all userids returned is guaranteed to be larger than this
	 * @number_of_entries - the page size default is 10
	 **/
	//public function get_friends_id_list_from_username($username, $last_seen, $number_of_entries=10)
	public function get_friends_id_list_from_username($username)
	{
		$key = 'FRIENDS/' . $username;
		$memcache = Memcached::get_instance();

		$system_property = SystemProperty::get_instance();
		$do_use_cache = $system_property->get_boolean(SystemProperty::FriendsListCacheEnabled, false);

		if ($do_use_cache) {
			$result = $memcache->get($key);
		}

		if (empty($result) || $result == FALSE) {

			$friend_ids = array();
			$friend_usernames = array();

			// fetch all, cache all, filter within app not at mysql
			$query = "SELECT b.broadcastusername as username FROM broadcastlist b WHERE b.username = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param('s', $username);

			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $friend);


			while ($stmt->fetch()) {
				$friend_usernames [] = $friend['username'];
			}

			// closing this now because get_user_id() may open another conn
			//
			$stmt->close();
			$this->closeSlaveConnection();

			foreach ($friend_usernames as $fname) {
				$id = $this->get_user_id($fname);
				$friend_ids[$id] = $fname;
				//debug: print "u [$fname] id[$id] <br/>";
			}

			if ($do_use_cache) {
				$memcache->add_or_update($key, json_encode($friend_ids), 1200); // cache for 20 minutes
			}
		} else {
			$friend_ids = json_decode($result, True);
		}
		/*
					// sanitize the input
					if ($last_seen < 0 ) { $last_seen = 0; }

					$friends_ids_page = array();
					$counter = 0;
					foreach ($friends_ids as $id => $name)
					{
						if ($id <= $last_seen) continue;

						$friends_ids_page [$id] = $name;
						$counter = $counter+1;

						// we take 1 more so we know if this is the last page or not
						if ($counter == $number_of_entries+1) break;
					}

					$last_page = true;
					if(count($friend_ids_page) > $number_of_entries)
					{
						$last_page  = false;
						array_pop($friends_ids_page);
					}
					return array('ids'=>$friends_ids_page, 'number_of_entries'=>$number_of_entries, 'page'=>$page, 'last_page'=>$last_page);
		*/

		return $friend_ids;
	}

	public function get_friends($username, $page, $number_of_entries = 10, $search = '')
	{
		$friend = null;

		$query = "
        		SELECT
        			u.username AS username,
        			c.name AS country
        		FROM
        			user u, country c, broadcastlist b
        		WHERE
        			u.username = b.broadcastusername
        			AND b.username = ?
        			AND u.countryid = c.id
        	";

		if (!empty($search)) {
			$query .= " AND b.broadcastusername LIKE ?";
			$extended_search = $search . '%';
		}

		$query .= " LIMIT ?, ?";

		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$offset = (($page - 1) * $number_of_entries);
		$limit = $number_of_entries + 1;

		if (!empty($search)) {
			$stmt->bind_param('ssii', $username, $extended_search, $offset, $limit);
		} else {
			$stmt->bind_param('sii', $username, $offset, $limit);
		}

		$stmt->execute();

		$this->getSlaveConnection()->stmt_bind_assoc($stmt, $friend);

		$contacts = array();
		while ($stmt->fetch()) {
			$contacts[] = array
			(
				'username' => $friend['username']
			, 'country' => $friend['country']
			);
		}

		$older_entries_exist = false;
		$number_of_contacts = count($contacts);
		if ($number_of_contacts > $number_of_entries) {
			$older_entries_exist = true;
			array_pop($contacts);
		}

		$stmt->close();
		$this->closeSlaveConnection();

		return array('contacts' => $contacts, 'number_of_entries' => $number_of_entries, 'page' => $page, 'older_entries_exist' => $older_entries_exist, 'num_of_contacts' => $number_of_contacts);
	}

	public function get_friends_list($username, $offset, $number_of_entries = 10, $search = '')
	{
		$query = 'SELECT
							SQL_CALC_FOUND_ROWS
							broadcastlist.broadcastusername AS username
						FROM
							broadcastlist
						WHERE
							broadcastlist.username = "%s"';

		if (!empty($search))
			$query .= ' AND broadcastlist.broadcastusername LIKE ("%s%%")';

		$query .= ' LIMIT %s, %s; SELECT FOUND_ROWS()';

		// We Are Using sprintf Instead of Prepared Statement Because We Need To Execute 2 Queries In One Connection
		if (!empty($search)) {
			$query = sprintf($query,
				$this->getSlaveConnection()->escape_string($username),
				$this->getSlaveConnection()->escape_string($search),
				intval($offset),
				intval($number_of_entries)
			);
		} else {
			$query = sprintf($query,
				$this->getSlaveConnection()->escape_string($username),
				intval($offset),
				intval($number_of_entries)
			);
		}

		$friends = array();
		$friends_w_details = array();
		if ($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result()) {
			$this->getSlaveConnection()->next_result();
			$next_result = $this->getSlaveConnection()->store_result();

			while ($row = $results->fetch_array(MYSQLI_ASSOC)) {
				$user_details = array();
				$user_details['username'] = $row['username'];
				$user_details['mig_level'] = $this->get_user_level($row['username']);
				$friends[$row['username']] = $user_details;

				$friends_w_details[$row['username']] = $row;
			}
			list($total_count) = $next_result->fetch_row();
		}

		$this->closeSlaveConnection();

		return array(
			'friends' => $friends,
			'friends_w_details' => $friends_w_details,
			'total_count' => $total_count
		);
	}

	public function get_friends_list_with_status_message($username, $offset, $number_of_entries = 10, $search = '')
	{
		$query = 'SELECT
							SQL_CALC_FOUND_ROWS
							broadcastlist.broadcastUsername AS username,
							user.StatusMessage,
							user.StatusTimeStamp
						FROM
							broadcastlist
						INNER JOIN
							user
						ON
							user.Username = broadcastlist.broadcastUsername
						WHERE
							broadcastlist.username="%s"';

		if (!empty($search))
			$query .= ' AND broadcastlist.broadcastusername LIKE ("%s%%")';

		$query .= ' LIMIT %s, %s; SELECT FOUND_ROWS()';

		// We Are Using sprintf Instead of Prepared Statement Because We Need To Execute 2 Queries In One Connection
		if (!empty($search)) {
			$query = sprintf($query,
				$this->getSlaveConnection()->escape_string($username),
				$this->getSlaveConnection()->escape_string($search),
				intval($offset),
				intval($number_of_entries)
			);
		} else {
			$query = sprintf($query,
				$this->getSlaveConnection()->escape_string($username),
				intval($offset),
				intval($number_of_entries)
			);
		}

		$friends = array();
		$friends_w_details = array();
		if ($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result()) {
			$this->getSlaveConnection()->next_result();
			$next_result = $this->getSlaveConnection()->store_result();

			while ($row = $results->fetch_array(MYSQLI_ASSOC)) {
				$user_details = array();
				$user_details['username'] = $row['username'];
				$user_details['mig_level'] = $this->get_user_level($row['username']);
				$friends[$row['username']] = $user_details;

				$friends_w_details[$row['username']] = $row;
			}
			list($total_count) = $next_result->fetch_row();
		}

		$this->closeSlaveConnection();

		return array(
			'friends' => $friends,
			'friends_w_details' => $friends_w_details,
			'total_count' => $total_count
		);
	}

	public function get_mutual_friends_list($session_username, $username, $offset, $number_of_entries = 10, $search = '')
	{
		$query = 'SELECT
        				broadcastusername,
        				COUNT(broadcastusername) AS num
        			FROM
        				broadcastlist
        			WHERE
        				username IN ("%s", "%s")';

		if (!empty($search))
			$query .= ' AND broadcastlist.broadcastusername LIKE ("%s%%")';

		$query .= ' GROUP BY
        				broadcastusername
        			HAVING
        				num > 1
        			LIMIT
        				%s, %s;
        			SELECT FOUND_ROWS()';

		// We Are Using sprintf Instead of Prepared Statement Because We Need To Execute 2 Queries In One Connection
		if (!empty($search)) {
			$query = sprintf($query,
				$this->getSlaveConnection()->escape_string($session_username),
				$this->getSlaveConnection()->escape_string($username),
				$this->getSlaveConnection()->escape_string($search),
				intval($offset),
				intval($number_of_entries)
			);
		} else {
			$query = sprintf($query,
				$this->getSlaveConnection()->escape_string($session_username),
				$this->getSlaveConnection()->escape_string($username),
				intval($offset),
				intval($number_of_entries)
			);
		}

		$friends = array();
		$friends_minimal = array();
		if ($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result()) {
			$this->getSlaveConnection()->next_result();
			$next_result = $this->getSlaveConnection()->store_result();
			$i = 0;
			while ($row = $results->fetch_array(MYSQLI_ASSOC)) {
				$uname = $row['broadcastusername'];
				$ulevel = $this->get_user_level($uname);

				$user_details = array();
				$user_details['username'] = $uname;
				$user_details['mig_level'] = $ulevel;
				$friends[$uname] = $user_details;

				$friends_minimal[$i]['username'] = $uname;
				$friends_minimal[$i]['userid'] = $this->get_user_id($uname);
				$friends_minimal[$i]['level'] = $ulevel['level'];
				$friends_minimal[$i]['level_image'] = $ulevel['image'];
				$i++;
			}

			list($total_count) = $next_result->fetch_row();
		}

		$this->closeSlaveConnection();
		return array('friends' => $friends, 'total_count' => $total_count, 'friends_minimal' => $friends_minimal);
	}

	/**
	 * This function saves a profile "footprint" (i.e. a profile view) in Redis.
	 *
	 * Each footprint is stored as JSON.
	 *   e.g. {"UserID":123456,"Username":"dave","Country":"Australia"}
	 *
	 * We store each profile view in two places:
	 *   1. The viewing user's list of "Users I've viewed" (Redis::KEYSPACE_USER_PROFILES_VIEWED), and
	 *   2. The viewed user's list of "Users who viewed me" (Redis::KEYSPACE_USER_PROFILE_VIEWED_BY)
	 *
	 * Before appending the footprint to each list we trim the list, removing all elements that are more than $max_age_seconds old.
	 **/
	public function record_profile_view($user_id_viewing, $username_viewing, $country_viewing, $user_id_viewed, $username_viewed, $country_viewed, $max_age_seconds)
	{
		try {
			// Store the profile view for the viewed user
			$redis_instance = Redis::get_master_instance_for_user_id($user_id_viewed);
			$key = Redis::KEYSPACE_USER_PROFILE_VIEWED_BY . $user_id_viewed;
			$json_value = json_encode(array('UserID' => $user_id_viewing, 'Username' => $username_viewing, 'Country' => $country_viewing));
			Redis::add_to_sorted_set_with_timestamp_and_trim_by_age($redis_instance, $key, $json_value, $max_age_seconds, true);
			$redis_instance->disconnect();

			// Store the profile view for the viewing user
			$redis_instance = Redis::get_master_instance_for_user_id($user_id_viewing);
			$key = Redis::KEYSPACE_USER_PROFILES_VIEWED . $user_id_viewing;
			$json_value = json_encode(array('UserID' => $user_id_viewed, 'Username' => $username_viewed, 'Country' => $country_viewed));
			Redis::add_to_sorted_set_with_timestamp_and_trim_by_age($redis_instance, $key, $json_value, $max_age_seconds, true);
			$redis_instance->disconnect();
		}
		catch (Exception $e) {
			error_log("record_profile_view(): " . $e->getMessage() . "; Params: $user_id_viewing, $username_viewing, $country_viewing, $user_id_viewed, $username_viewed, $country_viewed, $max_age_seconds");
		}
	}

	/**
	 * This function returns profile views the user has received (i.e., the footprints left behind by other users).
	 * Elements between $start_index and $end_index are returned. The first element's index is 0.
	 * Returns an array with elements like { 0 => '{"UserID":123456,"Username":"dave","Country":"Australia"}', 1 => 1283402115 }
	 * where 1283402115 is the timestamp of the footprint.
	 **/
	public function get_profile_views($user_id, $start_index, $end_index)
	{
		try {
			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			if (!isset($redis_instance))
				return NULL;
			$views = $redis_instance->zrevrange(Redis::KEYSPACE_USER_PROFILE_VIEWED_BY . $user_id, $start_index, $end_index, 'withscores');
			$redis_instance->disconnect();
			return $views;
		}
		catch (Exception $e) {
			error_log("get_profile_views(): " . $e->getMessage() . "; Params: $user_id, $start_index, $end_index");
			return NULL;
		}
	}

	/**
	 * This function returns a list of profiles the user has viewed (i.e., the footprints they have left).
	 * Elements between $start_index and $end_index are returned. The first element's index is 0.
	 * Returns an array with elements like { 0 => '{"UserID":123456,"Username":"dave","Country":"Australia"}', 1 => 1283402115 }
	 * where 1283402115 is the timestamp of the footprint.
	 **/
	public function get_profiles_viewed($user_id, $start_index, $end_index)
	{
		try {
			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			if (!isset($redis_instance))
				return NULL;
			$viewed = $redis_instance->zrevrange(Redis::KEYSPACE_USER_PROFILES_VIEWED . $user_id, $start_index, $end_index, 'withscores');
			$redis_instance->disconnect();
			return $viewed;
		}
		catch (Exception $e) {
			error_log("get_profiles_viewed(): " . $e->getMessage() . "; Params: $user_id, $start_index, $end_index");
			return NULL;
		}
	}

	/**
	 * This function returns the number of profile views the user has received (i.e., the number of footprints left behind by other users).
	 * Only views less than $max_age_seconds are counted.
	 **/
	public function get_num_profile_views($user_id, $max_age_seconds)
	{
		try {
			if (empty($user_id) || intval($user_id) == 0)
				return 0;

			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			if (!isset($redis_instance))
				return 0;
			$num_views = $redis_instance->zcount(Redis::KEYSPACE_USER_PROFILE_VIEWED_BY . $user_id, time() - $max_age_seconds, '+inf');
			$redis_instance->disconnect();
			return $num_views;
		}
		catch (Exception $e) {
			error_log("get_num_profile_views(): " . $e->getMessage() . "; Params: $user_id, $max_age_seconds");
			return 0;
		}
	}

	/**
	 * This function returns the number of profile views the user has viewed.
	 * Only views less than $max_age_seconds are counted.
	 **/
	public function get_num_profiles_viewed($user_id, $max_age_seconds)
	{
		try {
			if (empty($user_id) || intval($user_id) == 0)
				return 0;

			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			if (!isset($redis_instance))
				return 0;
			$num_views = $redis_instance->zcount(Redis::KEYSPACE_USER_PROFILES_VIEWED . $user_id, time() - $max_age_seconds, '+inf');
			$redis_instance->disconnect();
			return $num_views;
		}
		catch (Exception $e) {
			error_log("get_num_profiles_viewed(): " . $e->getMessage() . "; Params: $user_id, $max_age_seconds");
			return 0;
		}
	}

	/**
	 * This function removes a recorded profile view ("footprint")
	 **/
	public function remove_profile_view($user_id_viewing, $username_viewing, $country_viewing, $user_id_viewed, $username_viewed, $country_viewed)
	{
		try {
			// Remove the profile view for the viewed user
			$redis_instance = Redis::get_master_instance_for_user_id($user_id_viewed);
			$key = Redis::KEYSPACE_USER_PROFILE_VIEWED_BY . $user_id_viewed;
			$json_value = json_encode(array('UserID' => $user_id_viewing, 'Username' => $username_viewing, 'Country' => $country_viewing));
			$redis_instance->zrem($key, $json_value);
			$redis_instance->disconnect();

			// Remove the profile view for the viewing user
			$redis_instance = Redis::get_master_instance_for_user_id($user_id_viewing);
			$key = Redis::KEYSPACE_USER_PROFILES_VIEWED . $user_id_viewing;
			$json_value = json_encode(array('UserID' => $user_id_viewed, 'Username' => $username_viewed, 'Country' => $country_viewed));
			$redis_instance->zrem($key, $json_value);
			$redis_instance->disconnect();
		}
		catch (Exception $e) {
			error_log("remove_profile_view(): " . $e->getMessage() . "; Params: $user_id_viewing, $username_viewing, $country_viewing, $user_id_viewed, $username_viewed, $country_viewed");
		}
	}

	public function set_captcha_required($user_id)
	{
		$redis_instance = Redis::get_master_instance_for_user_id($user_id);
		$redis_instance->pipeline()
			->hset(Redis::KEYSPACE_CAPTCHA . $user_id, 'FailCount', 0)
			->hset(Redis::KEYSPACE_CAPTCHA . $user_id, 'Time', time())
			->execute();
	}

	public function is_captcha_required($user_id)
	{
		$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
		if (is_null($redis_instance))
			return false;

		$is_captcha_required = $redis_instance->exists(Redis::KEYSPACE_CAPTCHA . $user_id);
		return $is_captcha_required;
	}

	public function get_failed_captcha_counter($user_id)
	{
		$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
		if (is_null($redis_instance))
			return 0;

		$captcha_counter = $redis_instance->hget(Redis::KEYSPACE_CAPTCHA . $user_id, 'FailCount');
		return $captcha_counter;
	}

	public function increment_failed_captcha_counter($user_id)
	{
		$redis_instance = Redis::get_master_instance_for_user_id($user_id);
		return $redis_instance->hincrby(Redis::KEYSPACE_CAPTCHA . $user_id, 'FailCount', 1);
	}

	public function remove_captcha_required($user_id)
	{
		$redis_instance = Redis::get_master_instance_for_user_id($user_id);
		$delete_counter = $redis_instance->del(Redis::KEYSPACE_CAPTCHA . $user_id);
		return $delete_counter;
	}

	public function set_user_captcha_odds($user_id, $odds)
	{
		$redis_instance = Redis::get_master_instance_for_user_id($user_id);
		$redis_instance->hset(
			Redis::KEYSPACE_USER . $user_id
			, 'CaptchaOdds'
			, max($odds, CaptchaDomain::CAPTCHA_ODDS_MIN_ODDS)
		);
	}

	public function remove_user_captcha_odds($user_id)
	{
		$redis_instance = Redis::get_master_instance_for_user_id($user_id);
		$redis_instance->hdel(Redis::KEYSPACE_USER . $user_id, 'CaptchaOdds');
	}

	public function get_user_captcha_odds($user_id)
	{
		$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
		if (is_null($redis_instance))
			return CaptchaDomain::CAPTCHA_ODDS_ACCOUNT_NOT_VERIFIED;

		$user_captcha_odds = $redis_instance->hget(Redis::KEYSPACE_USER . $user_id, 'CaptchaOdds');
		return $user_captcha_odds;
	}

	public function get_avatar_vote_count($user_id)
	{
		$num_votes = 0;
		$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
		if (is_null($redis_instance))
			return 0;

		$key = Redis::KEYSPACE_USER . $user_id;
		$num_votes = $redis_instance->hget($key, Redis::FIELD_USER_AVATAR_VOTES);
		if (!isset($num_votes)) {
			$num_votes = 0;
		}

		$redis_instance->disconnect();

		return $num_votes;
	}

	public function increment_avatar_vote_count($voted_for_userid)
	{
		$new_num_votes = 0;

		$redis_instance = Redis::get_master_instance_for_user_id($voted_for_userid);
		$key = Redis::KEYSPACE_USER . $voted_for_userid;
		if (!isset($redis_instance)) {
			throw new Exception ("master redis instance for $voted_for_userid not found");
		}

		$new_num_votes = $redis_instance->hincrby($key, Redis::FIELD_USER_AVATAR_VOTES, 1);
		$redis_instance->disconnect();

		return $new_num_votes;
	}

	public function get_user_current_avatar_candidate_index($user_id)
	{
		$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
		if (is_null($redis_instance))
			return null;


		$key = Redis::KEYSPACE_USER . $user_id;
		$i = $redis_instance->hget($key, Redis::FIELD_USER_AVATAR_CANDIDATE_CURRENT_INDEX);
		$redis_instance->disconnect();

		return $i;
	}

	public function set_user_current_avatar_candidate_index($user_id, $num)
	{
		$redis_instance = Redis::get_master_instance_for_user_id($user_id);
		if (!isset($redis_instance)) {
			throw new Exception ("master redis instance for $user_id not found");
		}

		$key = Redis::KEYSPACE_USER . $user_id;
		$i = $redis_instance->hset($key, Redis::FIELD_USER_AVATAR_CANDIDATE_CURRENT_INDEX, $num);
		$redis_instance->disconnect();

		return $i;
	}

	public function get_voters_list($uid, $start_index, $end_index)
	{
		try {
			$redis_instance = Redis::get_slave_instance_for_user_id($uid);
			if (!isset($redis_instance))
				return NULL;

			$this_date = getdate();
			$day = $this_date['mday'];

			$key = Redis::KEYSPACE_AVATAR_VOTES . ':' . $uid;
			$now = time();
			$seven_days_earlier = time() - 604800; //7 days

			//ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]
			//$list = $redis_instance->zrevrangebyscore($key, $now, $seven_days_earlier, $start_index, ($end_index-$start_index+1));

			$list = $redis_instance->zrevrange($key, $start_index, $end_index, 'WITHSCORES');

			return $list;
		}
		catch (Exception $e) {
			error_log("get_voters_list(): " . $e->getMessage() . "; Params: $uid, $start_index, $end_index");
			return NULL;
		}
	}

	private function cmp_games_played($a, $b)
	{
		return $b->last_played - $a->last_played;
	}

	public function get_unique_games_played($username)
	{
		$chatroom_games_list = $this->get_unique_chatroom_games_played($username);
		$third_party_games_list = $this->get_unique_third_party_games_played($username);

		// merge both arrays
		$result = array_merge($third_party_games_list, $chatroom_games_list);

		// sort by last played timestamp
		usort($result, array($this, "cmp_games_played"));
		return $result;
	}


	private function get_unique_third_party_games_played($username)
	{
		$dao = new OpensocialDAO();
		$tpdao = new ThirdPartyApplicationDAO();

		$userid = $this->get_userid($username);

		// get all third party applications
		$appnames = $dao->get_user_applications($userid);

		$apps = array();

		//get the application data based on the names
		$appids = $tpdao->get_application_ids_from_names($appnames);

		if ($appids) {
			$apps = $tpdao->get_applications($appids);
		}

		$recently_played_apps = array();
		foreach ($apps as $key => $app) {
			try {
				$app->last_played = $dao->user_last_played($userid, $app->name);
				$recently_played_apps[$key] = $app;
			}
			catch (Exception $ex) {
				// Do nothing: Game has not been played before (prob has been removed).
			}
		}
		return $recently_played_apps;
	}

	private function get_unique_chatroom_games_played($username)
	{
		$redis_instance = Redis::get_slave_instance_for_leaderboards();
		$boards = array(
			Redis::KEYSPACE_GAME_LOWCARD,
			Redis::KEYSPACE_GAME_DICE,
			Redis::KEYSPACE_GAME_FOOTBALL,
			Redis::KEYSPACE_GAME_GUESS,
			Redis::KEYSPACE_GAME_DANGER,
			Redis::KEYSPACE_GAME_MIGCRICKET);
		$value_separator = ':';

		//TODO: need a better soap call function to get "all" the games without the need for a limit on the number of entries
		$content = make_soap_call('getBotList', array(1, 10000));
		$bots = $content->data['bots'];

		$list_of_games = array();

		foreach ($boards as $board):
			$played_score = $redis_instance->zscore(Redis::KEYSPACE_LB_GAMES_PLAYED . $board . Redis::KEYSPACE_LB_WEEKLY, $username . $value_separator . $this->get_user_id($username));
			$won_score = $redis_instance->zscore(Redis::KEYSPACE_LB_MOST_WINS . $board . Redis::KEYSPACE_LB_WEEKLY, $username . $value_separator . $this->get_user_id($username));

			if (isset($played_score)):
				$bot_details = $this->get_bot_details(str_replace(':', '', $board), $bots);
				if ($bot_details):
					$bot_app = new Application();
					$bot_app->name = $bot_details['name'];
					$bot_app->display_name = $bot_details['name'];
					$bot_app->id = $bot_details['id'];
					$bot_app->last_played = time() - 7 * 86400;
					$bot_app->type = 'CHATROOM';
					/*
				 $bot_details['games_played'] = $played_score;
				 $bot_details['games_won'] = (isset($won_score)) ? $won_score : 0;
				$list_of_games[] = $bot_details;
				*/
					$list_of_games[] = $bot_app;
				endif;
			endif;
		endforeach;
		return $list_of_games;
	}

	public function get_bot_details($name, $bots)
	{
		foreach ($bots as $bot):
			if (strtolower(str_replace(' ', '', $bot['displayName'])) == strtolower($name)):
				return array('name' => $bot['displayName'], 'id' => $bot['id']);
			endif;
		endforeach;
	}


	/**
	 * Returns true if user specified by username has attempted to verify account in the past X hours
	 **/
	public function user_attempted_verification($username, $hours)
	{
		if (empty($username)) {
			return false;
		}

		$query = 'SELECT if (count(*)<>0,1,0) FROM systemsms WHERE username=? and type=1 and subtype=1 and datecreated > DATE_SUB(now(), INTERVAL ? HOUR)';
		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param('si', $username, $hours);
		$stmt->execute();

		$stmt->bind_result($attempted_verification);
		$stmt->fetch();
		$this->closeSlaveConnection();

		if (empty($attempted_verification)) {
			error_log("user_attempted_verification() - $username does not exist");
			$attempted_verification = 0;
		}

		return (1 == $attempted_verification);

	}

	public function is_user_authenticated($username)
	{
		if (empty($username)) {
			return false;
		}

		$query = 'SELECT mobileverified FROM user WHERE username=?';
		$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
		$stmt->bind_param('s', $username);
		$stmt->execute();

		$stmt->bind_result($mobileverified);
		$stmt->fetch();
		$this->closeSlaveConnection();

		if (empty($mobileverified)) {
			$mobileverified = 0;
		}

		return (1 == $mobileverified);
	}

	public function get_mutual_games_played($username1, $username2)
	{
		$redis_instance = Redis::get_slave_instance_for_leaderboards();
		$boards = array(
			Redis::KEYSPACE_GAME_LOWCARD,
			Redis::KEYSPACE_GAME_DICE,
			Redis::KEYSPACE_GAME_FOOTBALL,
			Redis::KEYSPACE_GAME_GUESS,
			Redis::KEYSPACE_GAME_DANGER,
			Redis::KEYSPACE_GAME_MIGCRICKET);
		$value_separator = ':';

		//TODO: need a better soap call function to get "all" the games without the need for a limit on the number of entries
		$content = make_soap_call('getBotList', array(1, 10000));
		$bots = $content->data['bots'];

		$user1 = $username1 . $value_separator . $this->get_user_id($username1);
		$user2 = $username2 . $value_separator . $this->get_user_id($username2);

		foreach ($boards as $board):
			$key = Redis::KEYSPACE_LB_GAMES_PLAYED . $board . Redis::KEYSPACE_LB_WEEKLY;

			$score1 = $redis_instance->zscore($key, $user1);
			$score2 = $redis_instance->zscore($key, $user2);

			if (isset($score1) && isset($score2)):
				$bot_details = $this->get_bot_details(str_replace(':', '', $board), $bots);
				if ($bot_details):
					$list_of_games[] = $bot_details;
				endif;
			endif;
		endforeach;

		return $list_of_games;
	}

}
