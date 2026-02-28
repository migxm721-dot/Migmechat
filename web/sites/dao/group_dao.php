<?php
	require_once(get_framework_common_directory() . "/database.php");
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("GroupMember", get_domain_directory() . "/group/group_member.php");
	fast_require("GroupRSS", get_domain_directory() . "/group/group_rss.php");
	fast_require("Group", get_domain_directory() . "/group/group.php");
	fast_require("GroupJoinRequest", get_domain_directory() . "/group/group_join_request.php");
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");
	fast_require("UserLevel", get_file_location("/common/user_level.php"));
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");

	class GroupDAO extends DAO
	{
		public static $MAX_FEEDS = 5;

		const WEIGHT_RSS_NEW = 1;
		const WEIGHT_GROUP_LIKE = 1;
		const WEIGHT_CHATROOM_LINK = 5;
		const WEIGHT_MEMBER_NEW = 3;
		const WEIGHT_GROUP_LINK = 3;


        /**
         * Change the group name
         * @param  $group_id
         * @param  $name
         * @return void
         */
        public function change_group_name($group_id, $name)
        {
            if(!$this->is_group_name_available($name))
				throw new Exception(sprintf(_('Group name "%s" has already been taken'), $name));

            $query = 'UPDATE groups set name=? where id=?';
            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param('si', $name, $group_id);
            $stmt->execute();

            $affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			return true;
        }

		/**
		*
		*	Get total groups currently in system
		*
		**/
		public function get_total_groups_count()
		{
			$memcache = Memcached::get_instance();
			return $memcache->get(Memcached::$KEYSPACE_GROUPS_COUNT);
			/*
			$query = "SELECT COUNT(*) as total FROM groups WHERE status=1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$total = 0;
			if($stmt->fetch())
			{
				$total = $data['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $total;
			*/
		}

		/**
		*
		*	Get an individual group
		*
		**/
		public function get_group($group_id)
		{
			$query = "SELECT groups.*, service.status SupportsVIPs FROM groups
				LEFT OUTER JOIN service ON (groups.vipserviceid=service.id AND service.status=1)
				WHERE groups.id=? AND groups.status=1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$group = new Group($data);
				$this->set_group_object_with_last_activity($group);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			//Check memcache for group likes
			if(!empty($group))
			{
				$group->likes = $this->get_group_likes($group_id);
			}
			return $group;
		}

		public function get_group_name($group_id)
		{
			$query = 'SELECT name FROM groups WHERE id = ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$stmt->bind_result($group_name);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $group_name;
		}

		public function get_group_avatar($group_id)
		{
			$query = 'SELECT picture FROM groups WHERE id = ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$stmt->bind_result($group_picture);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $group_picture;
		}

		public function get_group_owner($group_id)
		{
			$query = 'SELECT createdby FROM groups WHERE id = ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$stmt->bind_result($owner);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $owner;
		}

		public function get_group_active_member_count($group_id)
		{
			$memcache = Memcached::get_instance();
			$group_active_member_count = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_ACTIVE_USER_COUNT, $group_id));

			if(!empty($group_active_member_count))
			{
				return $group_active_member_count;
			}
			else
			{
				$query = 'SELECT count(*) FROM groupmember WHERE groupid = ? and status=1';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param('i', $group_id);
				$stmt->execute();

				$stmt->bind_result($group_active_member_count);
				$stmt->fetch();
				$stmt->close();
				$this->closeSlaveConnection();

				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_ACTIVE_USER_COUNT, $group_id), $group_active_member_count, Memcached::$CACHEDURATION_GROUP_ACTIVE_USER_COUNT);

				return $group_active_member_count;
			}
		}

		public function give_group_member_admin_rights($session_username, $group_id, $username)
		{
			// #1 Ensure user is higher than Level 10
			$userDAO = new UserDAO();
			$reputation_level = $userDAO->get_user_level($username);
			$user_level = get_value_from_array('level', $reputation_level, 'integer', 0);
			if($user_level < 10)
				throw new Exception(sprintf(_('%s\'s Level must be at least %s to become a group moderator'), $username, 10));

			// #2 Only the creator of the group can make a group member a moderator
			if (!$this->user_is_owner_of_group($session_username, $group_id))
				throw new Exception(_('You must be an owner of the group to promote a user to be a moderator'));

			// #3 Check the number of moderators allowed based on group owner's reputation level
			$max_num_group_moderators = $this->get_max_moderators($group_id);
			$num_group_moderators = $this->get_moderators_count($group_id);
			if ($num_group_moderators >= $max_num_group_moderators)
				throw new Exception(sprintf(_('You cannot have more than %s moderators in this group'), $num_group_moderators));

			$query = 'UPDATE groupmember, groups SET groupmember.type = 3 WHERE groupmember.groupid = ? AND groupmember.status = 1 AND groupmember.username = ? AND groups.id = ? AND groups.createdby = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);

			$stmt->bind_param('isis', $group_id, $username, $group_id, $session_username);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;
			try
			{
				FusionRest::get_instance()->post(
						sprintf(
								FusionRest::KEYSPACE_GROUP_ADMIN_RIGHTS
								, $group_id
								, $username
						));
			}
			catch (Exception $e)
			{
				error_log("group_dao: unable to add group moderator from fusion rest");
			}

			return true;
		}

		public function remove_group_member_admin_rights($session_username, $group_id, $username)
		{
			if($session_username == $username){
				throw new Exception(_('You can\'t demote yourself'));
			}

			// #1 Only the creator of the group can make a group moderator back to a group member
			if (!$this->user_is_owner_of_group($session_username, $group_id))
				throw new Exception(_('You must be an owner of the group to demote a user'));

			$query = 'UPDATE groupmember, groups SET groupmember.type = 1 WHERE groupmember.groupid = ? AND groupmember.status = 1 AND groupmember.username = ? AND groups.id = ? AND groups.createdby = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);

			$stmt->bind_param('isis', $group_id, $username, $group_id, $session_username);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;
			try
			{
				FusionRest::get_instance()->delete(
						sprintf(
								FusionRest::KEYSPACE_GROUP_ADMIN_RIGHTS
								, $group_id
								, $username
						));
			}
			catch (Exception $e)
			{
				error_log("group_dao: unable to remove group moderator from fusion rest");
			}

			return true;
		}

		public function get_moderators_count($group_id)
		{
			if (!SystemProperty::get_instance()->get_boolean(SystemProperty::ViewGroupModeratorsEnabled, true))
			{
				return 0;
			}

			fast_require("ThirdPartyApiDAO", get_dao_directory() . "/third_party_api_dao.php");
			$tpa_dao = new ThirdPartyApiDAO();
			$group_ids_to_cache = $tpa_dao->get_linked_groups();

			$moderators_count = false;

			if (in_array($group_id, $group_ids_to_cache))
			{
				$redis = Redis::get_slave_instance_for_group_id($group_id);
				$moderator_count_key = Redis::KEYSPACE_ENTITY_GROUP.$group_id.Redis::KEYSPACE_GROUP_MODERATORS_COUNT;

				if (!is_null($redis))
				{
					$raw_data = $redis->get($moderator_count_key);
					if (!is_null($raw_data))
					{
						$moderators_count = (int) $raw_data;
					}

					$redis->disconnect();
				}
			}

			if ($moderators_count === false)
			{
				$query = 'SELECT COUNT(*) FROM groupmember WHERE groupid = ? AND status = 1 AND type = 3';
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

				$stmt->bind_param('i', $group_id);
				$stmt->execute();

				$stmt->bind_result($moderators_count);
				$stmt->fetch();

				$stmt->close();
				$this->closeSlaveConnection();

				if (in_array($group_id, $group_ids_to_cache))
				{
					try
					{
						$redis = Redis::get_master_instance_for_group_id($group_id);
						$redis->setex($moderator_count_key, 600, $moderators_count); // cache for 10mns
						$redis->disconnect();
					}
					catch(Exception $e)
					{
						error_log("group_dao: unable to cache moderator list for group $group_id: ".$e->getMessage());
					}
				}
			}

			return $moderators_count;
		}

		public function get_moderators($group_id)
		{
			if (!SystemProperty::get_instance()->get_boolean(SystemProperty::ViewGroupModeratorsEnabled, true))
			{
				return array();
			}

			fast_require("ThirdPartyApiDAO", get_dao_directory() . "/third_party_api_dao.php");
			$tpa_dao = new ThirdPartyApiDAO();
			$group_ids_to_cache = $tpa_dao->get_linked_groups();

			$moderators = false;

			if (in_array($group_id, $group_ids_to_cache))
			{
				$redis = Redis::get_slave_instance_for_group_id($group_id);
				$moderator_key = Redis::KEYSPACE_ENTITY_GROUP.$group_id.Redis::KEYSPACE_GROUP_MODERATORS;

				if (!is_null($redis))
				{
					$raw_data = $redis->get($moderator_key);
					if (!is_null($raw_data))
					{
						$moderators = unserialize($raw_data);
					}

					$redis->disconnect();
				}
			}

			if(empty($moderators))
			{
				$query = "SELECT m.*, sub.id VIPSubscriptionID
						FROM groupmember m
						INNER JOIN groups g ON m.groupid=g.id
						LEFT OUTER JOIN service ON (g.vipserviceid=service.id and service.status=1)
						LEFT OUTER JOIN subscription sub ON (sub.serviceid=service.id and sub.status=1 AND sub.username=m.username)
						WHERE m.groupid=? AND m.status=1 and m.type=3";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $group_id);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$moderators = array();

				while($stmt->fetch())
				{
					$moderators[] = new GroupMember($row);
				}

				$stmt->close();
				$this->closeSlaveConnection();

				if (in_array($group_id, $group_ids_to_cache))
				{
					try
					{
						$redis = Redis::get_master_instance_for_group_id($group_id);
						$redis->setex($moderator_key, 600, serialize($moderators)); // cache for 10mns
						$redis->disconnect();
					}
					catch(Exception $e)
					{
						error_log("group_dao: unable to cache moderator list for group $group_id: ".$e->getMessage());
					}
				}
			}

			return $moderators;

		}

		public function get_max_moderators($group_id)
		{
			$group_owner = $this->get_group_owner($group_id);
			$userDAO = new UserDAO();
			$reputation_level = $userDAO->get_user_level($group_owner);
			$max_num_group_moderators = get_value_from_array('num_group_moderators', $reputation_level, 'integer', 0);

			return $max_num_group_moderators;
		}

		public function decrement_member_count($group_id)
		{
			$query = "UPDATE groups SET nummembers=nummembers-1 WHERE id=? AND nummembers>=1";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function increment_member_count($group_id)
		{
			$query = "UPDATE groups SET nummembers=nummembers+1 WHERE id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function ban_group_member($session_username, $group_id, $username)
		{
			if($session_username == $username){
				throw new Exception(_('You can\'t ban youself'));
			}

			$group_owner = $this->get_group_owner($group_id);

			if (!$this->user_is_admin_of_group($session_username, $group_id)){
				throw new Exception(_('You must be an admin of the group to ban members'));
			}

			if($group_owner == $username){
				throw new Exception(_('The owner of the group can\'t be banned'));
			}

			if( $this->user_is_admin_of_group($username, $group_id) && $session_username!=$group_owner ){
				throw new Exception(_('Only group owners can ban moderators'));
			}

			$query = 'UPDATE groupmember SET status = 2 WHERE groupid = ? AND username = ? AND status = 1';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $group_id, $username);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			// Decrement Group Member Count
			$this->decrement_member_count($group_id);

			// Send Banned User An Email
			try
			{
				$group_name = $this->get_group_name($group_id);
				$email_address = $username.'@'.$GLOBALS['imap_domain'];
				$subject = 'You Have Been Banned From Group "'.$group_name.'"';
				$message = "Hi $username,\n\nYou have been banned from Group \"$group_name\" by $session_username.";
				soap_call_ejb('sendEmailFromNoReply', array($email_address, $subject, $message));
			}
			catch(Exception $e)
			{
			}

			return true;
		}

		public function unban_group_member($session_username, $group_id, $username)
		{
			if (!$this->user_is_admin_of_group($session_username, $group_id))
				throw new Exception(_('You must be an admin of the group to unban members'));

			$query = 'UPDATE groupmember SET status = 1 WHERE groupid = ? AND username = ? AND status = 2';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $group_id, $username);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			// Increment Group Member Count
			$this->increment_member_count($group_id);

			return true;
		}

		public function get_banned_group_members($group_id, $max_members_to_return, $older_than_id=0)
		{
			$query = sprintf("SELECT SQL_CALC_FOUND_ROWS * FROM groupmember WHERE groupid = %s AND status = 2 ", intval($group_id));

			if ($older_than_id != 0)
				$query = sprintf("%s AND id < %d ", $query, intval($older_than_id));

			$num_members_to_return = $max_members_to_return + 1;

			$query = $query . sprintf("ORDER BY id DESC LIMIT %s; SELECT FOUND_ROWS()", intval($num_members_to_return));

			$members = array();
		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		            $members[] = new GroupMember($row);
		        }

		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }

			$members = array_reverse($members);  // We want the members in ascending chronological order

			$more_members_exist = false;
			if (sizeof($members) > $max_members_to_return)
			{
				$more_members_exist = true;
				array_shift($members);  // Remove the extra members we don't want to return
			}

			$last_id = 0;
			if (count($members) > 0)
				$last_id = $members[0]->id;
			return array("num_members"=>sizeof($members), "more_members_exist"=>$more_members_exist, "members"=>$members, "last_member_id"=>$last_id, "total_blocked_members" => $total_count);
		}

		/**
		*
		* Group RSS
		*
		**/

		/**
		* Get the group rss
		**/
		public function get_group_rss($group_id, $master=false)
		{
			$query = "SELECT * from grouprss where groupid = ?";

			if( $master )
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			else
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			if($master)
				$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			else
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$rss = array();
			while( $stmt->fetch() )
			{
				$rss[] = new GroupRSS($row);
			}

			$stmt->close();
			if($master)
				$this->closeMasterConnection();
			else
				$this->closeSlaveConnection();

			return $rss;
		}

		/**
		*
		* Get the feed detail
		*
		**/
		public function get_rss_detail($feed_id)
		{
			$query = "SELECT * from grouprss where id = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $feed_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() )
			{
				$rss = new GroupRSS($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $rss;
		}

		/**
		* Add group rss
		**/
		public function add_group_rss($group_id, $url)
		{
			$count = $this->get_group_feed_count($group_id);
			$url = strip_tags($url);
			// check if the user has exceeded the maximum group limits for feeds
			if( $count >= GroupDAO::$MAX_FEEDS )
				throw new Exception(_('You have reached your limit of feeds for this group.'));

			$query = "INSERT INTO grouprss (groupid, url) VALUES (?, ?)";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("is", $group_id, $url);
			$stmt->execute();
			$rss_id = $stmt->insert_id;
			$stmt->close();

			//increment score
			$this->increment_score($group_id, self::WEIGHT_RSS_NEW);

			return $rss_id;
		}

		/**
		* Update the feed url
		**/
		public function update_group_rss($feed_id, $url, $name)
		{
			$url = strip_tags($url);
			$name = htmlentities(strip_tags($name));
			$query = "UPDATE grouprss SET url=?, name=? WHERE id=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ssi", $url, $name, $feed_id);
			$stmt->execute();
			$stmt->close();
		}

		/**
		* Get the group feed count
		**/
		public function get_group_feed_count($group_id)
		{
			$query = "SELECT COUNT(*) FROM grouprss WHERE groupid = ?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$stmt->bind_result($feeds);
			$stmt->fetch();
			$stmt->close();
			$this->closeMasterConnection();

			return $feeds;
		}

		/**
		* Delete the feed
		**/
		public function delete_feed($feed_id)
		{
			$query = "DELETE FROM grouprss WHERE id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $feed_id);
			$stmt->execute();

			$stmt->fetch();
			$stmt->close();
			$this->closeMasterConnection();

			//decrement score
			$this->decrement_score($group_id, (-1)*self::WEIGHT_RSS_NEW);
		}

		/**
		* Update feed name
		**/
		public function update_feed_name($feed_id, $name)
		{
			$name = htmlentities(strip_tags($name));

			$query = "UPDATE grouprss SET name = ? WHERE id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $name, $feed_id);
			$stmt->execute();

			$stmt->fetch();
			$stmt->close();
			$this->closeMasterConnection();
		}
		/***** Group RSS *****/

		public function create_group($session_username, $name, $description, $type, $email, $official = 0)
		{
			if ($type !=0 && $type != 1 && $type !=3)
				throw new Exception(_('Invalid type specified'));

			if ($official == 1 && UserLevel::can_access($session_username, 1) == false)
				throw new Exception(_('You can not create an official group'));

			$groupcategoryid = 4;
			if ($official == 1)
				$groupcategoryid = 5;

			$this->check_user_can_create_group($session_username);

			$name = htmlentities(strip_tags($name));
			$description = htmlentities(strip_tags($description));
			$email = htmlentities(strip_tags($email));

			if ($email != '' && !checkEmail($email))
				throw new Exception(sprintf(_('%s is not a valid email address'), $email));

			if(!$this->is_group_name_available($name))
				throw new Exception(sprintf(_('Group name "%s" has already been taken'), $name));

			$this->getMasterConnection()->autocommit(FALSE);

			// Create the group
			$query = "INSERT INTO groups (type, name, description, about, datecreated, groupcategoryid, createdby, allownonmemberstojoinrooms, emailaddress, nummembers, status, featured, official)
						VALUES (?, ?, ?, '', NOW(), ?, ?, 0, ?, 1, 1, ?, ?)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ississii", $type, $name, $description, $groupcategoryid, $session_username, $email, $official, $official);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$this->getMasterConnection()->rollback();
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Unable to create group'));
			}

			$new_group_id = $stmt->insert_id;  // Get the ID of the newly inserted row
			$stmt->close();

			// Increment Total Group Counter In Memcache
			$memcache = Memcached::get_instance();
			$memcache->increment_key_counter(Memcached::$KEYSPACE_GROUPS_COUNT);

			// Make the creator a member of the group, and an admin
			$query = "INSERT INTO groupmember (username, groupid, datecreated, type, smsnotification, emailnotification, eventnotification,
						smsgroupeventnotification, emailthreadupdatenotification, eventthreadupdatenotification, status)
						VALUES (?, ?, NOW(), 2, 0, 1, 1, 0, 0, 0, 1)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $session_username, $new_group_id);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$this->getMasterConnection()->rollback();
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Unable to create group'));
			}

			// Create a group chat room
			$query = "INSERT INTO chatroom (name, type, creator, groupid, maximumsize, userowned, allowkicking, allowuserkeywords, allowbots, language, datecreated, status)
					  VALUES (?, 1, ?, ?, ?, 1, 0, 0, 1, 'ENG', now(), 1)";

			$userDAO = new UserDAO();
			$reputation_level = $userDAO->get_user_level($session_username);
			$maximum_size = get_value_from_array("chat_room_size", $reputation_level, "integer", 25);

			$room_name = "Lobby " . $new_group_id;

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ssii", $room_name, $session_username, $new_group_id, $maximum_size);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$this->getMasterConnection()->rollback();
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Unable to create group chat room'));
			}

			// Create a stadium if this is an official group
			if ($official == 1) {
				$query = "INSERT INTO chatroom (name, type, creator, groupid, maximumsize, userowned, allowkicking, allowuserkeywords, allowbots, language, datecreated, status)
					  	  VALUES (?, 2, ?, ?, ?, 0, 0, 0, 0, 'ENG', now(), 1)";

				$room_name = "Stadium Lobby " . $new_group_id;
				$maximum_size = 5000;

				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("ssii", $room_name, $session_username, $new_group_id, $maximum_size);
				$stmt->execute();

				if ($stmt->affected_rows != 1) {
					$this->getMasterConnection()->rollback();
					$stmt->close();
					$this->closeMasterConnection();
					throw new Exception(_('Unable to create group stadium'));
				}
			}

			$this->getMasterConnection()->commit();
			$this->closeMasterConnection();

			return $new_group_id;
		}

		public function link_chatroom_to_group($session_username, $group_id, $chatroom_id)
		{
			// #1 User must be an admin of a group
			if(!$this->user_is_admin_of_group($session_username, $group_id))
				throw new Exception(_('You must be an owner or a moderator of the group to link a chatroom.'));

			// #2 Ensure the chat room belongs to the user, doesn't belong to a group already, and is active
			$query = 'SELECT name FROM chatroom WHERE id = ? AND creator = ? AND groupid IS NULL AND userowned = 1 AND status = 1';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $chatroom_id, $session_username);
			$stmt->execute();

			$stmt->bind_result($chatroom_name);

			if (!$stmt->fetch()) {
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Invalid chat room. The chat room must be owned by you, and not already part of a group'));
			}

			$stmt->close();

			// #3 Check the number of chat rooms allowed based on group owner's reputation level
			$max_num_group_chatrooms = $this->get_max_chatrooms($group_id);
			$num_group_chatrooms = $this->get_chatrooms_count($group_id);
			if ($num_group_chatrooms >= $max_num_group_chatrooms)
				throw new Exception(sprintf(_('You cannot link more than %s chatrooms to this group'), $max_num_group_chatrooms-1));//substract 1 to exclude group lobby chatroom SNSER-58

			// Link the chat room to the group
			$query = 'UPDATE chatroom, groups SET chatroom.groupid = groups.id WHERE chatroom.id = ? AND groups.id = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $chatroom_id, $group_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			// Change the chatroom to be a group chatroom in the objectcache
			fast_require('ChatroomDAO', get_dao_directory() . '/chatroom_dao.php');
			$chatroom_dao = new ChatroomDAO();
			$chatroom_dao->convert_into_group_chatroom($chatroom_name, $group_id);

			//increment score
			$this->increment_score($group_id, self::WEIGHT_CHATROOM_LINK);

			return true;
		}

		 public function user_is_owner_of_linked_chatroom($username, $group_id, $chatroom_id)
        {
        	$query = 'SELECT id FROM chatroom WHERE id = ? AND creator = ? AND GroupID = ?';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('sii', $chatroom_id, $username, $group_id);
			$stmt->execute();

			$stmt->bind_result($id);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			if(empty($id))
				return false;
			else
				return true;
        }

		public function unlink_chatroom_from_group($session_username, $group_id, $chatroom_id)
		{
			$admin_moderator = $this->user_is_admin_of_group($session_username, $group_id);
			$is_chatroom_owner =  $this->user_is_owner_of_linked_chatroom($session_username, $group_id, $chatroom_id);

			if(!$admin_moderator && !$is_chatroom_owner)
				throw new Exception(_('You must be an owner or a moderator of the group or the owner of the chatroom to unlink a chatroom.'));

			// Get chat room
			$query = 'SELECT creator, newowner, name FROM chatroom WHERE id = ? AND groupid = ? AND userowned = 1 AND status = 1';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);

			$stmt->bind_param('ii', $chatroom_id, $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$chatroom_name = get_value_from_array('name', $data);
				$chatroom_creator = get_value_from_array('creator', $data);
				$chatroom_newowner = get_value_from_array('newowner', $data);
			}
			else
			{
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Invalid chat room. The chat room must be owned by you, and belongs to the group'));
			}

			if($admin_moderator == 3){
				if(($session_username != $chatroom_creator) && ($session_username != $chatroom_newowner)){
					throw new Exception(_('Invalid chat room. The chat room must be owned by you'));
				}
			}

			$stmt->close();

			// Unlink the chat room from the group
			$query = 'UPDATE chatroom, groups SET chatroom.groupid = NULL WHERE chatroom.groupid = groups.id AND chatroom.id = ? AND groups.id = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);

			$stmt->bind_param('ii', $chatroom_id, $group_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			// Change the chatroom back to a user-owned chatroom in the objectcache
			fast_require('ChatroomDAO', get_dao_directory() . '/chatroom_dao.php');
			$chatroom_dao = new ChatroomDAO();
			$chatroom_dao->convert_into_user_owned_chatroom($chatroom_name);

			//increment score
			$this->decrement_score($group_id, (-1)*self::WEIGHT_CHATROOM_LINK);

			return true;
		}

		public function update_group_settings($session_username, $group_id, $description, $type, $email)
		{
			if ($type !=0 && $type != 1 && $type !=3)
				throw new Exception(_('Invalid type specified'));

			$description = strip_tags($description);
			$email = strip_tags($email);

			if ($email != '' && !checkEmail($email))
				throw new Exception(sprintf(_('%s is not a valid email address'), $email));

			$query = "UPDATE groups SET description=?, type=?, emailaddress=? WHERE id=? AND createdby=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("sisis", $description, $type, $email, $group_id, $session_username);
			$stmt->execute();
			$stmt->close();
			$this->closeMasterConnection();
		}

		public function update_group_avatar($session_username, $group_id, $file_id)
		{
			$query = "UPDATE groups SET picture=? WHERE id=? AND createdby=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("sis", $file_id, $group_id, $session_username);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function check_if_group_exists($name)
		{
			$query = "SELECT COUNT(*) as count FROM groups WHERE name=?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $name);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$count = $data['count'];
			}
			$stmt->close();
			$this->closeSlaveConnection();

			return ($count>0) ? true : false;
		}

		public function check_user_can_create_group($session_username)
		{
			$userDAO = new UserDAO();
			$reputation_level = $userDAO->get_user_level($session_username);
			$max_groups_can_own = get_value_from_array('create_group', $reputation_level, 'integer', 0);

			// If 0 means user Level is less than 20
			if($max_groups_can_own == 0)
				throw new Exception(sprintf(_('You need at least Level %s to create a group'), 20));

			$query = 'SELECT COUNT(*) FROM groups WHERE createdby = ? AND status = 1';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $session_username);
			$stmt->execute();

			$stmt->bind_result($num_groups);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			// User cannot create more than X groups
			if ($num_groups >= $max_groups_can_own)
				throw new Exception(sprintf(_('You may not own more than %s groups'), $max_groups_can_own));
		}

		public function get_max_groups_can_own($session_username){
			$userDAO = new UserDAO();
			$reputation_level = $userDAO->get_user_level($session_username);
			$max_groups_can_own = get_value_from_array('create_group', $reputation_level, 'integer', 0);

			$returnArray['can_create'] = true;
			if($max_groups_can_own == 0): 	$returnArray['can_create'] = false;  endif;

			$query = 'SELECT COUNT(*) FROM groups WHERE createdby = ? AND status = 1';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $session_username);
			$stmt->execute();

			$stmt->bind_result($num_groups);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			// User cannot create more than X groups
			if ($num_groups >= $max_groups_can_own):
				$returnArray['can_create'] = false;
				$returnArray['groups_left'] = 0;
			else:
				$returnArray['can_create'] = true;
				$returnArray['groups_left'] = $max_groups_can_own-$num_groups;
			endif;

			return $returnArray;
		}

		public function get_chatrooms_that_can_be_linked($session_username)
		{
			$query = "SELECT id, name FROM chatroom WHERE creator=? AND groupid IS NULL AND userowned=1 AND status=1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $session_username);
			$stmt->execute();

			$chatroom_ids = array();
			$chatroom_names = array();

			$stmt->bind_result($id, $name);

			while ($stmt->fetch())
			{
				$chatroom_ids[] = $id;
				$chatroom_names[] = $name;
			}


			$stmt->close();
			$this->closeSlaveConnection();

			return array("chatroom_ids"=>$chatroom_ids, "chatroom_names"=>$chatroom_names);
		}

		public function get_linked_chatrooms_created_by_user($session_user, $group_id)
		{
			$query = "SELECT id, name FROM chatroom WHERE (creator = ? OR newowner = ?) AND groupid = ? AND userowned = 1 AND status = 1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ssi', $session_user, $session_user, $group_id);
			$stmt->execute();

			$chatroom_ids = array();
			$chatroom_names = array();

			$stmt->bind_result($id, $name);

			while ($stmt->fetch())
			{
				$chatroom_ids[] = $id;
				$chatroom_names[] = $name;
			}


			$stmt->close();
			$this->closeSlaveConnection();

			return array("chatroom_ids"=>$chatroom_ids, "chatroom_names"=>$chatroom_names);
		}

		public function get_all_linked_chatrooms($group_id)
		{
			$query = "SELECT id, name FROM chatroom WHERE groupid = ? AND userowned = 1 AND status = 1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$chatroom_ids = array();
			$chatroom_names = array();

			$stmt->bind_result($id, $name);

			while ($stmt->fetch())
			{
				$chatroom_ids[] = $id;
				$chatroom_names[] = $name;
			}


			$stmt->close();
			$this->closeSlaveConnection();

			return array("chatroom_ids"=>$chatroom_ids, "chatroom_names"=>$chatroom_names);
		}

		public function get_chatrooms_count($group_id)
		{
			$query = 'SELECT count(*) FROM chatroom WHERE groupid = ? AND userowned = 1 AND status = 1';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$stmt->bind_result($num_group_chatrooms);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			return $num_group_chatrooms;
		}

		public function get_max_chatrooms($group_id)
		{
			$group_owner = $this->get_group_owner($group_id);
			$userDAO = new UserDAO();
			$reputation_level = $userDAO->get_user_level($group_owner);
			$max_num_group_chatrooms = get_value_from_array('num_group_chat_rooms', $reputation_level, 'integer', 0);

			return $max_num_group_chatrooms;
		}

		public function get_num_chatrooms_that_can_be_linked($session_username)
		{
			$query = "SELECT COUNT(*) FROM chatroom WHERE creator=? AND groupid IS NULL AND userowned=1 AND status=1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $session_username);
			$stmt->execute();

			$stmt->bind_result($num_chatrooms);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();
			return $num_chatrooms;
		}

		public function search_groups($search_string, $max_groups_to_return, $older_than_id=0, $sort_by = 'datecreated', $sort_order = 'desc')
		{
            $query = "SELECT SQL_CALC_FOUND_ROWS * FROM groups ";
			$criteria = "";

			if ($search_string != "")
				$criteria = sprintf("%s name like '%s%%' ", $criteria, $this->getSlaveConnection()->escape_string($search_string));

			if ($older_than_id != 0)
			{
				if ($criteria != "")
					$criteria = $criteria . "AND ";

				$criteria = sprintf("%s id < %s ", $criteria, intval($older_than_id));
			}

			if ($criteria != "")
				$criteria = "WHERE status = 1 AND (type = 0 OR type = 3) AND " . $criteria;

			$sort = sprintf("ORDER BY %s %s ", $this->getSlaveConnection()->escape_string($sort_by), $this->getSlaveConnection()->escape_string($sort_order));

			$num_groups_to_return = $max_groups_to_return + 1;

			$limit = sprintf("LIMIT %s; SELECT FOUND_ROWS()", intval($num_groups_to_return));

			$query = $query . $criteria . $sort . $limit;

			$groups = array();
		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		        	$group = new Group($row);
		        	$this->set_group_object_with_last_activity($group);
		            $groups[] = $group;
		        }

		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }

			$this->closeSlaveConnection();

			//$groups = array_reverse($groups);  // We want the groups in ascending chronological order

			$more_groups_exist = false;
			if (sizeof($groups) > $max_groups_to_return)
			{
				$more_groups_exist = true;
				//array_shift($groups);  // Remove the extra groups we don't want to return
				array_pop($groups);
			}

			$last_id = 0;
			if (count($groups) > 0)
			{
				//$last_id = $groups[0]->id;
				$last_id = $groups[sizeof($groups)-1]->id;
			}

			return array("num_groups"=>sizeof($groups), "more_groups_exist"=>$more_groups_exist, "groups"=>$groups, "last_group_id"=>$last_id, "total_groups" => $total_count);
		}

		public function get_pending_group_invitations($session_username, $page, $number_entries)
		{
			$query = "SELECT groups.* FROM groups, groupinvitation
					 	WHERE groups.id = groupinvitation.groupid
					 	AND groupinvitation.username = ?
					 	AND groupinvitation.status = 0
					 	AND groups.status = 1
						ORDER BY groupinvitation.id DESC";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $session_username);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;
			$group_ids = array();
			$groups = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$groups[] = $group;

				$group_ids[] = $row['ID'];
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "groups"=>$groups, "group_ids" => $group_ids);
		}

		/**
		*
		*	Get a list of Featured groups
		*	Will only cache results for landing page, anything else will come straight from db
		*
		**/
		public function get_featured_groups($sort_by = 'datecreated', $sort_order = 'desc')
		{

			$memcache = Memcached::get_instance();
			$featured_groups = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_FEATURED, $sort_by.$sort_order));

			if(!empty($featured_groups))
			{
				return $featured_groups;
			}
			else
			{
				$query = "SELECT * FROM groups	 WHERE status = 1 AND featured = 1 AND type = 0 ORDER BY ".$sort_by." ".$sort_order.", groups.id DESC LIMIT 100";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$groups = array();
				while( $stmt->fetch())
				{
					$group = new Group($row);
					$this->set_group_object_with_last_activity($group);
					$groups[] = $group;
				}

				//Cache result if this is for landing page
				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_FEATURED, $sort_by.$sort_order), $groups, Memcached::$CACHEDURATION_GROUP_FEATURED);

				return $groups;
			}
		}

		/**
		*
		*	Get a list of Featured groups
		*	Will only cache results for landing page, anything else will come straight from db
		*
		**/
		public function get_official_groups($sort_by = 'datecreated', $sort_order = 'desc')
		{

			$memcache = Memcached::get_instance();
			$groups = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_OFFICIAL, $sort_by.$sort_order));

			if(! empty($groups))
			{
				return $groups;
			}
			else
			{
				$query = "SELECT * FROM groups " .
						 "WHERE status = 1 " .
						 "AND official = 1 " .
						 "AND type = 0 " .
						 "ORDER BY ".$sort_by." ".$sort_order.", groups.id DESC LIMIT 100";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$groups = array();
				while( $stmt->fetch())
				{
					$group = new Group($row);
					$this->set_group_object_with_last_activity($group);
					$groups[] = $group;
				}

				foreach ($groups as &$group)
				{
					//Check memcache for group likes
					$group->likes = $this->get_group_likes($group->id);
				}

				//Cache result if this is for landing page
				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_OFFICIAL, $sort_by.$sort_order), $groups, Memcached::$CACHEDURATION_GROUP_OFFICIAL);

				return $groups;
			}
		}

		/**
		*
		*	Function to get top active groups
		*
		**/
		public function get_top_active_groups()
		{
			$query = "SELECT groups.* FROM groupmember INNER JOIN groups ON (groupmember.groupid = groups.id) WHERE groupmember.username = ? AND groupmember.status= 1 AND groups.status = 1 AND groups.featured = 0 AND groups.official = 0 AND (groups.type = 0 OR groups.type = 3) ORDER BY groups.numforumposts DESC LIMIT 2";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $session_username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$groups = array();
			while( $stmt->fetch())
			{
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$groups[] = $group;

			}
			return $groups;
		}


		/**
		*
		*	Function to retrieve user's active user created groups with more forum posts
		*
		**/
		public function get_user_active_user_groups($session_username = '')
		{
			$query = "SELECT groups.* FROM groupmember INNER JOIN groups ON (groupmember.groupid = groups.id) WHERE groupmember.username = ? AND groupmember.status= 1 AND groups.status = 1 AND groups.featured = 0 AND groups.official = 0 AND (groups.type = 0 OR groups.type = 3) ORDER BY groups.numforumposts DESC LIMIT 2";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $session_username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$groups = array();
			while( $stmt->fetch())
			{
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$groups[] = $group;
			}
			return $groups;
		}

		/**
		*
		*	Function to retrieve active user created groups with more forum posts
		*	Retrieve and cache 100 active user groups and cache for an hour
		*
		**/
		public function get_active_user_groups()
		{

			$memcache = Memcached::get_instance();
			$active_user_groups = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_ACTIVE_USER, $group_id));

			if(!empty($active_user_groups))
			{
				return $active_user_groups;
			}
			else
			{
				$query = "SELECT * FROM groups	 WHERE status = 1 AND featured = 0 AND official = 0 AND (type = 0 OR type = 3) ORDER BY numforumposts DESC LIMIT 100";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$groups = array();
				while( $stmt->fetch())
				{
					$group = new Group($row);
					$this->set_group_object_with_last_activity($group);
					$groups[] = $group;
				}

				//Cache result if this is for landing page
				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_ACTIVE_USER, $group_id), $groups, Memcached::$CACHEDURATION_GROUP_ACTIVE_USER);

				return $groups;
			}
		}


		/**
		*
		*	Function to retrieve public groups with more forum posts
		*	Retrieve and cache 100 public groups and cache for an hour
		*
		**/
		public function get_public_groups()
		{

			$memcache = Memcached::get_instance();
			$public_groups = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_PUBLIC, ''));

			if(!empty($public_groups))
			{
				return $public_groups;
			}
			else
			{
				$query = "SELECT * FROM groups WHERE status = 1 AND (type = 0 OR type = 3) ORDER by featured DESC, numforumposts DESC LIMIT 100";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$groups = array();
				while( $stmt->fetch())
				{
					$group = new Group($row);
					$this->set_group_object_with_last_activity($group);
					$groups[] = $group;
				}

				//Cache result if this is for landing page
				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUPS_PUBLIC, ''), $groups, Memcached::$CACHEDURATION_GROUP_PUBLIC);

				return $groups;
			}
		}


                /**
                * Get Group ID given a Group Name given the group si ACTIVE (status=1).
                *
                **/
		public function get_group_id_from_group_name($group_name)
		{
			$query = "SELECT id
						FROM groups
					   WHERE name=? and status=1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $group_name);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$group_id = 0;
			if($stmt->fetch())
			{
				$group_id = $row['id'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $group_id;
		}


		/**
		*
		*	Get the total groups joined by a user
		*
		**/
		public function get_total_joined_groups($username)
		{
			$query = "SELECT COUNT(*) AS total FROM groupmember WHERE groupmember.username = ? AND groupmember.status=1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$total = $data['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $total;
		}

		/**
		*
		*	Get the total groups moderated by a user
		*
		**/
		public function get_total_moderated_groups($username)
		{
			$query = "SELECT COUNT(*) AS total FROM groupmember WHERE groupmember.username = ? AND groupmember.status=1 AND groupmember.type=3";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$total = $data['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $total;
		}

		/**
		*
		*	Get the groups for a specific user
		*
		**/
		public function get_groups($session_username, $page, $number_entries,$sort_by = 'datecreated', $sort_order = 'desc')
		{
			$total_joined_groups = $this->get_total_joined_groups($session_username);
			$total_pages = ceil($total_joined_groups / $number_entries);

			// Offset
			// @current_page starts from 1
			$offset = ($page - 1) * $number_entries;

			$query = "SELECT groups.* FROM groupmember INNER JOIN groups ON (groupmember.groupid = groups.id) WHERE groupmember.username = ? AND groupmember.status=1 ORDER BY groups.".$sort_by." ".$sort_order.",groupmember.datecreated DESC,  groups.id DESC LIMIT ?, ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("sii", $session_username, $offset, $number_entries);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$groups = array();
			while( $stmt->fetch())
			{
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$groups[] = $group;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array("total_pages"=> $total_pages, "total_results"=>$total_joined_groups, "groups"=>$groups);
		}

		/**
		*
		*	Get the moderated groups for a specific user
		*
		**/
		public function get_groups_moderated_by_user($session_username, $page, $number_entries,$sort_by = 'datecreated', $sort_order = 'desc')
		{
			$total_moderated_groups = $this->get_total_moderated_groups($session_username);
			$total_pages = ceil($total_moderated_groups / $number_entries);

			// Offset
			// @current_page starts from 1
			$offset = ($page - 1) * $number_entries;

			$query = "SELECT groups.* FROM groupmember INNER JOIN groups ON (groupmember.groupid = groups.id) WHERE groupmember.username = ? AND groupmember.type=3 ORDER BY groups.".$sort_by." ".$sort_order.",groupmember.datecreated DESC,  groups.id DESC LIMIT ?, ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("sii", $session_username, $offset, $number_entries);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$groups = array();
			while( $stmt->fetch())
			{
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$groups[] = $group;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array("moderated_groups_total_pages"=> $total_pages
				, "moderated_groups_total_results"=>$total_moderated_groups
				, "moderated_groups"=>$groups
			);
		}

		public function get_mutual_groups($username1, $username2)
		{
			$query = "SELECT
						g.*
						FROM groupmember gm, groups g
						WHERE g.ID = gm.GroupID and
						gm.username in (?,?)
						GROUP BY g.ID
						HAVING count(g.ID) > 1;";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ss", $username1, $username2);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$mutual_groups = array();
			while( $stmt->fetch() ):
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$mutual_groups[] = $group;
			endwhile;

			$stmt->close();
			$this->closeSlaveConnection();

			return $mutual_groups;
		}

		//List out groups owned by user and still a member of group
		public function get_owned_groups($session_username, $page, $number_entries, $sort_by = 'datecreated', $sort_order = 'desc')
		{
			$query = "SELECT g.*, gc.name categoryname
						FROM groups g, groupcategory gc, groupmember m
						WHERE g.groupcategoryid=gc.id AND g.id=m.groupid AND m.username=? AND m.status=1 AND g.status=1 AND g.createdby = ?
						ORDER BY g.".$sort_by." ".$sort_order;

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ss", $session_username, $session_username);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$groups = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$groups[] = $group;

				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();
			$stmt->close();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "groups"=>$groups);
		}

		public function does_user_own_groups($username, $other_than_group_id)
		{
			// a user can only link a group if he himself owns any group

			$query = "
				SELECT count(*)
				FROM groups g
				WHERE
					CreatedBy=?
					AND status=1
					AND ID<>?
			";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $username, $other_than_group_id);
			$stmt->execute();
			$stmt->bind_result($count);

			$result = false;

			if ($stmt->fetch())
			{
				$result = ($count > 0);
			}

			$stmt->free_result();
			$stmt->close();

			return $result;
		}


		public function get_groups_suggestion_for_linkings($username, $group_id_to_link)
		{
			// get the list of groups a user owns which are not already linked to group_id_to_link

			$query = "
				SELECT g.ID, g.Name, SUM(gl.LinkedGroupID IS NOT NULL AND gl.LinkedGroupID=?) as is_linked
				FROM groups g LEFT OUTER JOIN grouplinks gl
					ON g.ID=gl.GroupID
				WHERE
					g.CreatedBy=?
					AND g.status=1
					AND g.ID<>?
				GROUP BY g.ID
				HAVING is_linked=0
				ORDER BY g.Name ASC
			";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("isi", $group_id_to_link, $username, $group_id_to_link);
			$stmt->execute();
			$stmt->bind_result($gid, $gname);

			$suggestions = array();

			while($stmt->fetch())
			{
				$suggestions[] = array
				(
					"id"   => $gid,
					"name" => $gname
				);
			}
			$stmt->free_result();
			$stmt->close();

			return $suggestions;
		}

		public function get_group_member($group_id, $username, $from_master=false)
		{
			$query = "SELECT m.*, u.DisplayPicture, sub.id VIPSubscriptionID
						FROM groupmember m INNER JOIN user u ON m.username=u.username
						INNER JOIN groups g ON m.groupid=g.id
						LEFT OUTER JOIN service ON (g.vipserviceid=service.id and service.status=1)
						LEFT OUTER JOIN subscription sub ON (sub.serviceid=service.id and sub.status=1 and sub.username=u.username)
						WHERE m.username=? AND m.groupid=? AND m.status=1";

			if ($from_master == false)
			{
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("si", $username, $group_id);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

				$stmt->fetch();
				$stmt->close();
				$this->closeSlaveConnection();
			}
			else
			{
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("si", $username, $group_id);
				$stmt->execute();

				$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

				$stmt->fetch();
				$stmt->close();
				$this->closeMasterConnection();
			}

			return new GroupMember($data);
		}



		public function is_group_member($group_id, $username)
		{
			$query = "SELECT COUNT(*) as is_member
						FROM groupmember m, groups g
						WHERE g.id=m.groupid AND m.username=? AND m.groupid=? AND m.status=1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $username, $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $data['is_member'];
		}

		public function get_group_members($group_id, $offset, $number_of_entries)
		{
			$query = "SELECT m.*, u.DisplayPicture, sub.id VIPSubscriptionID
						FROM groupmember m inner join user u ON m.username=u.username
						INNER JOIN groups g ON m.groupid=g.id
						LEFT OUTER JOIN service ON (g.vipserviceid=service.id and service.status=1)
						LEFT OUTER JOIN subscription sub ON (sub.serviceid=service.id and sub.status=1 AND sub.username=u.username)
						WHERE m.groupid=? AND m.status=1 LIMIT ?, ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_id, $offset, $number_of_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$members = array();

			while($stmt->fetch())
			{
				$members[] = new GroupMember($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $members;
		}

		public function get_group_banned_members($group_id, $page, $number_entries)
		{
			$query = "SELECT m.*, u.DisplayPicture, sub.id VIPSubscriptionID
						FROM groupmember m inner join user u ON m.username=u.username
						INNER JOIN groups g ON m.groupid=g.id
						LEFT OUTER JOIN service ON (g.vipserviceid=service.id and service.status=1)
						LEFT OUTER JOIN subscription sub ON (sub.serviceid=service.id and sub.status=1 AND sub.username=u.username)
						WHERE m.groupid=? AND m.status=2 limit 200";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$members = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$members[] = new GroupMember($row);
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "group_members"=>$members);
		}

		public function like_group($session_username, $group_id, $like)
		{
			// Ensure that like is either -1 (dislike) or 1 (like)
			if ($like != 1 && $like != -1)
				return;

			$user_is_member_of_group = $this->user_is_member_of_group($session_username, $group_id);
			if(!$user_is_member_of_group)
				return;

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO grouplike (groupid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $group_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			// Update the grouplikesummary table only if there is change
			if ($affected_rows > 0) {
				$query = sprintf("SELECT SUM(type = 1) numlikes, ABS(SUM(type = -1)) numdislikes FROM grouplike WHERE groupid = %s", mysql_escape_string($group_id));
				$data = $this->execute_one_row($this->getMasterConnection(), $query);
				$new_num_likes = get_value_from_array("numlikes", $data, "integer", 0);
				$new_num_dislikes = get_value_from_array("numdislikes", $data, "integer", 0);

				$query = "INSERT INTO grouplikesummary (groupid, numlikes, numdislikes) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE numlikes = ?, numdislikes = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iiiii", $group_id, $new_num_likes, $new_num_dislikes, $new_num_likes, $new_num_dislikes);
				$stmt->execute();
				$stmt->close();

				//increment score
				$this->increment_score($group_id, self::WEIGHT_GROUP_LIKE);

			}
			$this->closeMasterConnection();

			// Return the new number of likes and dislikes
			return $this->get_group_likes($group_id, true);
		}

		/**
		*
		*	Get the number of likes for a group.
		*
		**/
		public function get_group_likes($group_id, $from_master = false)
		{
			//Data required from master will never hit memcache
			if(!$from_master)
			{
				//Check if memcache has it
				$memcache = Memcached::get_instance();

				$group_likes = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUP_LIKES, $group_id));
				if(is_string($group_likes))
				{
					$group_likes = (int)$group_likes;
				}

				if(is_bool($group_likes) && $group_likes == FALSE)
				{
					//Not in memcache, get from slave db
				}
				else
				{
					//Already in memcache, jsut return result
					return intval($group_likes);
				}
			}

			// Return the new number of likes and dislikes
			$query = "SELECT numlikes, numdislikes FROM grouplikesummary WHERE groupid = ?";
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}

			$stmt->bind_param("i", $group_id);
			$stmt->execute();
			$stmt->bind_result($num_likes, $num_dislikes);
			$stmt->fetch();
			$stmt->free_result();
			$stmt->close();
			if($from_master) {
				$this->closeMasterConnection();
			} else {
				$this->closeSlaveConnection();
			}

			//Add/update to memcache
			if(empty($memcache))
				$memcache = Memcached::get_instance();
			$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUP_LIKES, $group_id), (int)($num_likes), Memcached::$CACHEDURATION_GROUP_LIKES);

			return intval($num_likes);
		}

		public function get_groups_likes($group_ids)
		{
			if(is_array($group_ids) && !empty($group_ids)) {
				$group_ids = array_map('intval', $group_ids);
				$group_ids_sql = implode(',', $group_ids);
				// Return the new number of likes and dislikes
				$query = "SELECT groupid, numlikes, numdislikes FROM grouplikesummary WHERE groupid IN($group_ids_sql)";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$group_likes = array();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while( $stmt->fetch())
				{
					$group_likes[$row['groupid']] = array('numlikes' => intval($row['numlikes']), 'numdislikes' => intval($row['numdislikes']));
				}
				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();
				return $group_likes;
			}
		}

		// Join Request
		public function get_join_request($request_id)
		{
			$query = "SELECT gjr.id as Id,
							 gjr.groupid as GroupId,
			  				 gjr.datecreated as DateCreated,
			  				 gjr.requesterid as RequesterID,
			  				 uid.username as RequesterUsername,
			  				 gjr.status as Status
						FROM groupjoinrequest gjr, userid uid
					   WHERE gjr.id=?
					     AND gjr.status=0
					     AND gjr.requesterid=uid.id";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $request_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$request = "";
			if($stmt->fetch())
			{
				$request = new GroupJoinRequest($row);
			}

			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();

			return $request;
		}


		public function get_join_requests($group_id)
		{
			$query = "SELECT gjr.id as Id,
							 gjr.groupid as GroupId,
			  				 gjr.datecreated as DateCreated,
			  				 gjr.requesterid as RequesterID,
			  				 uid.username as RequesterUsername,
			  				 gjr.status as Status
						FROM groupjoinrequest gjr, userid uid
					   WHERE gjr.groupid=?
					     AND gjr.status=0
					     AND gjr.requesterid=uid.id
					ORDER BY gjr.datecreated ASC";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$requests = array();
			while($stmt->fetch())
			{
				$requests[] = new GroupJoinRequest($row);
			}

			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();

			return $requests;
		}

		public function get_total_join_requests($group_id)
		{
			$query = "SELECT COUNT(*) as total
						FROM groupjoinrequest
					   WHERE groupid=? AND status=0";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$num_request = 0;
			if($stmt->fetch())
			{
				$num_requests = $row['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $num_requests;
		}

		public function is_request_exists($group_id, $user_id)
		{
			$query = "SELECT COUNT(*) as total FROM groupjoinrequest WHERE groupid=? AND requesterid=? AND status=0";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $user_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$total = $row['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return ($total == 1);
		}

		public function create_join_request($group_id, $user_id)
		{
			if($this->is_request_exists($group_id, $user_id))
			{
				throw new Exception(_('Request exists'));
			}

			$query = "INSERT INTO groupjoinrequest
						(groupid, datecreated, requesterid, status)
						VALUES (?, NOW(), ?, 0)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $user_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows;
		}

		public function accept_request($group_id, $request_id)
		{
			$query = "UPDATE groupjoinrequest SET status=1 WHERE groupid=? AND id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $request_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if($affected_rows == 0)
				throw new Exception(_('Invalid request'));

			return $affected_rows;
		}

		public function reject_request($group_id, $request_id)
		{
			$query = "UPDATE groupjoinrequest SET status=2 WHERE groupid=? AND id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $request_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if($affected_rows == 0)
				throw new Exception(_('Invalid request'));

			return $affected_rows;
		}

		public function accept_all_requests($group_id)
		{
			$query = "UPDATE groupjoinrequest SET status=1 WHERE groupid=? AND status=0";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows;
		}

		public function reject_all_requests($group_id)
		{
			$query = "UPDATE groupjoinrequest SET status=2 WHERE groupid=? AND status=0";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows;
		}

		public function is_group_name_available($group_name)
		{
			$query = "SELECT COUNT(*) FROM groups WHERE name = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", strip_tags(trim($group_name)));
			$stmt->execute();
			$stmt->bind_result($exists);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();
			return ($exists == 1 ? 0 : 1);
		}

		public function search_group_members($group_id, $username, $offset, $number_of_entries)
		{
			$members_count = 0;
			$search_query = sprintf('SELECT COUNT(*) FROM groupmember m inner join user u ON m.username=u.username INNER JOIN groups g ON m.groupid=g.id LEFT OUTER JOIN service ON (g.vipserviceid=service.id and service.status=1) LEFT OUTER JOIN subscription sub ON (sub.serviceid=service.id and sub.status=1 AND sub.username=u.username) WHERE m.groupid=? AND m.username LIKE(\'%s%%\') AND m.status=1', mysql_escape_string($username));
			$stmt = $this->getSlaveConnection()->get_prepared_statement($search_query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();
			$stmt->bind_result($members_count);
			$stmt->fetch();
			$stmt->close();

			$members = array();
			$query = sprintf('SELECT m.*, u.DisplayPicture, sub.id VIPSubscriptionID FROM groupmember m inner join user u ON m.username=u.username INNER JOIN groups g ON m.groupid=g.id LEFT OUTER JOIN service ON (g.vipserviceid=service.id and service.status=1) LEFT OUTER JOIN subscription sub ON (sub.serviceid=service.id and sub.status=1 AND sub.username=u.username) WHERE m.groupid=? AND m.username LIKE(\'%s%%\') AND m.status=1 LIMIT ?, ?', mysql_escape_string($username));
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_id, $offset, $number_of_entries);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			while($stmt->fetch())
			{
				$members[] = new GroupMember($row);
			}
			$stmt->close();
			$this->closeSlaveConnection();

			return array('members' => $members, 'members_count' => $members_count);
		}

		public function link_group($group_id, $linked_group_id)
		{
			$success = false;
			$query = "INSERT INTO grouplinks VALUES (?, ?)";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $group_id, $linked_group_id);
			$stmt->execute();
			if ($stmt->affected_rows == 1) {
				$success = true;

				//increment score
				$this->increment_score($group_id, self::WEIGHT_GROUP_LINK);
			}

			$stmt->close();
			return $success;
		}

		public function unlink_group($group_id, $linked_group_id)
		{
			$success = false;
			$query = "DELETE FROM grouplinks WHERE groupid = ? AND linkedgroupid = ?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $group_id, $linked_group_id);
			$stmt->execute();

			$stmt->fetch();
			if ($stmt->affected_rows == 1) {
				$success = true;

				//decrement score
				$this->decrement_score($group_id, (-1)*self::WEIGHT_GROUP_LINK);
			}
			$stmt->close();
			$this->closeMasterConnection();

			return $success;
		}

		public function get_linked_groups($group_id, $sort_by = 'datecreated', $sort_order = 'desc'){

			$query = "SELECT
						g.*
						FROM grouplinks gl, groups g
						WHERE gl.LinkedGroupID = g.ID and
						gl.GroupID = ? ORDER BY ".$sort_by." ".$sort_order;

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$linked_groups = array();
			while( $stmt->fetch() ):
				$group = new Group($row);
				$this->set_group_object_with_last_activity($group);
				$linked_groups[] = $group;
			endwhile;

			$stmt->close();
			$this->closeSlaveConnection();

			return $linked_groups;

		}

		public function increment_score($group_id, $delta = 1) {
			$this->update_score($group_id, $delta);
		}

		public function decrement_score($group_id, $delta = -1)	{
			$this->update_score($group_id, $delta);
		}

		private function update_score($group_id, $delta)
		{
			//we are using the leaderboards instance for groups score
			$redis_instance = Redis::get_master_instance_for_leaderboards();
			if (!isset($redis_instance)){
				throw new Exception ("decrement_score: directory master redis instance not found");
			}

			$key = Redis::KEYSPACE_ENTITY_GROUP . Redis::KEYSPACE_GROUP_SCORE;
			$new_score = $redis_instance->zincrby($key, $delta, $group_id);
			$redis_instance->disconnect();

			//updating last activity timestamp
			$this->update_group_last_activity($group_id, time());
		}

		public function get_active_groups($num_of_groups = 15)
		{
			//we are using the leaderboards instance for groups score
			$redis_instance = Redis::get_slave_instance_for_leaderboards();

			if (!isset($redis_instance)){
				throw new Exception ("get_active_groups: directory slave redis instance not found");
			}

			$key = Redis::KEYSPACE_ENTITY_GROUP . Redis::KEYSPACE_GROUP_SCORE;
			$num_of_active_groups = $redis_instance->zcard($key);
			$active_groups = array();
			if($num_of_active_groups > 0){
				$active_groups = $redis_instance->zrevrange($key, 0, $num_of_groups);
			}

			$redis_instance->disconnect();
			return $active_groups;
		}

		public function get_number_of_active_groups()
		{
			//we are using the leaderboards instance for groups score
			$redis_instance = Redis::get_slave_instance_for_leaderboards();

			if (!isset($redis_instance)){
				throw new Exception ("get_active_groups: directory slave redis instance not found");
			}

			$key = Redis::KEYSPACE_ENTITY_GROUP . Redis::KEYSPACE_GROUP_SCORE;
			$num_of_active_groups = $redis_instance->zcard($key);

			$redis_instance->disconnect();
			return $num_of_active_groups;
		}

		public function get_friends_groups($username, $offset, $number_of_entries, $sort_by = 'datecreated', $sort_order = 'desc')
		{
			//remove memcaching for pagination and sorting
			//$memcache = Memcached::get_instance();
			//$groups_container = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUP_FRIENDS, $username));

			if(empty($groups_container))
			{
				$query = 'SELECT
	 					SQL_CALC_FOUND_ROWS
	 					g.*
        			FROM
        				groupmember gm,
        				groups g,
        				broadcastlist bl
        			WHERE
        				gm.GroupID = g.ID AND
        				gm.Username = bl.broadcastusername AND
        				bl.username = "%s" AND
        				g.status = 1
        			GROUP BY g.id
        			ORDER BY
        				g.'.$sort_by.' '.$sort_order.',
						gm.DateCreated DESC';

		 		$query .= ' LIMIT %s, %s; SELECT FOUND_ROWS()';

		 		$query = sprintf($query,
	        					$this->getSlaveConnection()->escape_string($username),
	        					intval($offset),
	        					intval($number_of_entries)
	        				);

		 		//$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

		 		$groups = array();
		 		$groups_container = array();
				if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
				{
			        while ($row = $results->fetch_array(MYSQLI_ASSOC))
			        {
			            $group = new Group($row);
						$this->set_group_object_with_last_activity($group);
						$groups[] = $group;
					}

			        $this->getSlaveConnection()->next_result();
			        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();

			        $groups_container['groups'] = $groups;
			        $groups_container['total_count'] = $total_count;

			        //Cache result
					//$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUP_FRIENDS, $username), $groups_container, Memcached::$CACHEDURATION_GROUP_FRIENDS);
				}

			    $this->closeSlaveConnection();
			}

			return array('groups' => $groups_container['groups'], 'total_count' => $groups_container['total_count']);
	 	}

	 	/*
	 	 * Returns the black listed users of a group
	 	 */
		public function get_group_black_list($group_id, $page, $number_of_entries)
		{
			$offset = ($page - 1) * $number_entries;
			if($offset < 0): $offset = 0; endif;

			$query = 'SELECT
 					SQL_CALC_FOUND_ROWS
 					Username
        		FROM
        			groupblacklist bl
        		WHERE
        			bl.GroupID = %s';

	 		$query .= ' LIMIT %s, %s; SELECT FOUND_ROWS()';

	 		$query = sprintf($query,
        					intval($group_id),
        					intval($offset),
        					intval($number_of_entries)
        				);

	 		$blacklisted_users = array();
			if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result()):
		        while ($row = $results->fetch_array(MYSQLI_ASSOC)):
		            $blacklisted_users[] = $row['Username'];
		        endwhile;

		        $this->getSlaveConnection()->next_result();
		        $total_count = $this->getSlaveConnection()->store_result()->fetch_row();
		    endif;

		    $this->closeSlaveConnection();

			return array('blacklisted_users' => $blacklisted_users, 'total_count' => $total_count);
	 	}

		public function get_groups_by_id($group_id_array,$num_entries = 10, $sort_by = 'datecreated', $sort_order = 'desc')
		{
				if (empty($group_id_array)) return array();

				$group_ids=implode(",",$group_id_array);

				$query = "SELECT * FROM groups WHERE id in (".$group_ids.") AND status = 1 AND (type = 0 OR type = 3) ORDER BY ".$sort_by." ".$sort_order." LIMIT ".$num_entries;

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$groups = array();
				while( $stmt->fetch())
				{
					$g = new Group($row);
					$this->set_group_object_with_last_activity($g);
					$groups[] = $g;
				}

				return $groups;

		}

		public function update_group_last_activity($group_id, $timestamp)
		{
			if( !(isset($group_id) && isset($timestamp)) ){
				throw new Exception ("Invalid parameters has been passed");
			}

			$redis_instance = Redis::get_master_instance_for_group_id($group_id);
			if (!isset($redis_instance)){
				//throw new Exception ("update_group_last_activity: directory master redis instance for groups not found");
				return false;
			}

			$key = Redis::KEYSPACE_ENTITY_GROUP . $group_id;
			$new_timestamp = $redis_instance->hset($key, Redis::FIELD_GROUP_LAST_ACTIVITY, $timestamp);
			$redis_instance->disconnect();

			return true;
		}

		public function get_group_last_activity($group_id)
		{
			if( !isset($group_id) ){
				throw new Exception ("Invalid Group ID has been passed");
			}

			$redis_instance = Redis::get_slave_instance_for_group_id($group_id);
			if (!isset($redis_instance)){
				//throw new Exception ("get_group_last_activity: directory slave redis instance for groups not found");
				return 0;
			}

			$key = Redis::KEYSPACE_ENTITY_GROUP . $group_id;
			$timestamp = $redis_instance->hget($key, Redis::FIELD_GROUP_LAST_ACTIVITY);
			$redis_instance->disconnect();

			return intval($timestamp);
		}

		//local helper function: set last activity to the passed in group object parameter
		private function set_group_object_with_last_activity(&$group)
		{
			$last_activity = $this->get_group_last_activity($group->id);
			if($last_activity > 0){
				$group->set_last_activity($last_activity);
			}
		}

		public function get_popular_groups_in_country($country_id)
        {
        	$memcache = Memcached::get_instance();
        	$memcache_key = Memcached::get_memcache_full_key(Memcached::$KEYSPACE_GROUP_POPULAR_IN_COUNTRY, $country_id);

			$popular_groups = $memcache->get($memcache_key);

			if(! empty($popular_groups))
			{
				return $popular_groups;
			}

        	// get the popular groups, nummembers in groups may not be in sync with the actual number of members
        	// this is a slow query with average run time of 5.x seconds so we need to cache this for at least a day
			$query = "SELECT g.id, g.name, nummembers " .
        			 "FROM groups g, user u " .
        			 "WHERE g.createdby = u.username " .
        			 "AND u.countryid = ? " .
        			 "ORDER BY nummembers " .
        			 "LIMIT 100";

        	$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

            $stmt->bind_param("i", $country_id);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$popular_groups = array();
			while( $stmt->fetch())
			{
				$popular_groups[$row['id']] = array(   'name' => html_entity_decode($row['name'], ENT_QUOTES)
													 , 'nummembers' => $row['nummembers']);
			}

			// we cache
			$memcache->add_or_update($memcache_key, $popular_groups, Memcached::$CACHEDURATION_GROUP_POPULAR_IN_COUNTRY);

			// we return the number of records required
			return array_slice($popular_groups, 0, $limit);

		}

	}
?>
