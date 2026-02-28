<?php
	fast_require('DAO', get_dao_directory() . '/dao.php');
	fast_require('MrechantDAO', get_dao_directory() . '/merchant_dao.php');
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');
	fast_require('Memcached', get_framework_common_directory() . '/memcached.php');
	fast_require('XCache', get_framework_common_directory() . '/xcache.php');
	fast_require('NewuserChatroom', get_domain_directory() . '/newuser/newuser_chatroom.php');
	fast_require('Bot', get_domain_directory() . '/chatroom/bot.php');

	class ChatroomDAO extends IceDAO
	{


		public static $NEWUSER_CHATROOMS_PREFIX = array(
			// Global
			  -1	=> array('name' => 'HappyNewbie', 'size' => 3)
			// Indonesia
			, 107	=> array('name' => 'KenalanYuk', 'size' => 5)
			// Syria
			, 212	=> array('name' => 'AssalamArab', 'size' => 3)
			// Saudi Arabia
			, 194	=> array('name' => 'AssalamArab', 'size' => 3)
			// Egypt
			, 71	=> array('name' => 'AssalamArab', 'size' => 3)
			// India
			, 106	=> array('name' => 'SwagatHai', 'size' => 2)
			// Pakistan
			, 171	=> array('name' => 'SwagatHai', 'size' => 2)
			// Bangladesh
			, 20	=> array('name' => 'Shagotom', 'size' => 2)
			// Nepal
			, 157	=> array('name' => 'Swagatam', 'size' => 2)
		);

		public static $MAX_FREE_CHATROOMS_GAMES = 10;
		public static $FREE_CHATROOMS_GAMES_PREFIX = array('Low Card', 'Dice', 'Danger', 'Heads or Tails', 'migCricket', 'Trivia');
		public static $CHATROOM_RETURN_COUNT = 6;
		public static $MEMCACHE_EXPIRY = 3600;

		private function get_num_participants($chatroom)
		{
			try
			{
				return intval($this->registry->get_object_prx()
					->findChatRoomObject($chatroom)
					->getNumParticipants());
			}
			catch(Exception $ex)
			{
				return 0;
			}
		}

		public function get_newuser_chatrooms($user_country_id = 0)
		{
			$count = 0;

			$chatrooms = array();

			for($i = 1; $i <= self::$NEWUSER_CHATROOMS_PREFIX[-1]['size']; $i++)
			{
				$chatrooms[] = self::$NEWUSER_CHATROOMS_PREFIX[-1]['name'].'_'.$i;
			}

			if($user_country_id > 0)
				{
				if(isset(self::$NEWUSER_CHATROOMS_PREFIX[$user_country_id]))
				{
					for($i = 1; $i <= self::$NEWUSER_CHATROOMS_PREFIX[$user_country_id]['size']; $i++)
					{
						$chatrooms[] = self::$NEWUSER_CHATROOMS_PREFIX[$user_country_id]['name'].'_'.$i;
					}
				}
			}

			$memcache = Memcached::get_instance();
			$newuser_chatrooms = $memcache->get('NewUser/Chatrooms');

			if(empty($newuser_chatrooms))
			{
				$newuser_chatrooms = array();

				$query = 'SELECT id, name, description, maximumsize FROM chatroom WHERE name IN (\''.implode('\',\'', $chatrooms).'\') AND status = 1';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while($stmt->fetch())
				{
					$chatroom = new NewuserChatroom($row);
					$newuser_chatrooms[] = $chatroom;
				}

				$stmt->close();
				$this->closeSlaveConnection();
				$memcache->add_or_update('NewUser/Chatrooms', $newuser_chatrooms, self::$MEMCACHE_EXPIRY);
			}

			$newuser_chatrooms_data = array();
			if(is_array($newuser_chatrooms) && sizeof($newuser_chatrooms) > 0)
			{
				foreach($newuser_chatrooms as $newuser_chatroom)
					{
						$num_participants = $this->get_num_participants($newuser_chatroom->name);

						if($num_participants < $newuser_chatroom->maximum_size)
						{
							$newuser_chatroom->set_num_participants($num_participants);
							$newuser_chatrooms_data[] = $newuser_chatroom;
					}
				}
			}
			return array('newuser_chatrooms' => $newuser_chatrooms_data);
		}

		public function get_free_chatrooms_games()
		{
			$count = 0;

			$chatrooms = array();

			foreach (self::$FREE_CHATROOMS_GAMES_PREFIX as $chatroom)
			{
				for($i = 1; $i <= self::$MAX_FREE_CHATROOMS_GAMES; $i++)
				{
					$chatrooms[] = $chatroom . ' ' . $i;
				}
			}

			$memcache = Memcached::get_instance();
			$newuser_chatroomsgames = $memcache->get('NewUser/ChatroomGames');
			if(empty($newuser_chatroomsgames))
			{
				$query = 'SELECT id, name, description, maximumsize FROM chatroom WHERE name IN (\''.implode('\',\'', $chatrooms).'\') AND status = 1';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while($stmt->fetch())
				{
					$chatroom = new NewuserChatroom($row);

					foreach(self::$FREE_CHATROOMS_GAMES_PREFIX as $chatroom_prefix)
					{
						if(preg_match('/' . $chatroom_prefix . '/', $chatroom->name))
						{
							$newuser_chatroomsgames[str_replace(' ', '_', $chatroom_prefix)][] = $chatroom;
						}
					}
				}
				$stmt->close();
				$this->closeSlaveConnection();
				$memcache->add_or_update('NewUser/ChatroomGames', $newuser_chatroomsgames, self::$MEMCACHE_EXPIRY);
			}

			$newuser_chatroomsgames_data = array();
			if(is_array($newuser_chatroomsgames) && sizeof($newuser_chatroomsgames) > 0)
			{
				foreach($newuser_chatroomsgames as $newuser_crgames_category)
				{
					$games_count = 0;

					foreach($newuser_crgames_category as $newuser_chatroomgames)
					{
						$num_participants = $this->get_num_participants($newuser_chatroomgames->name);

						if($num_participants < $newuser_chatroomgames->maximum_size)
						{
							$newuser_chatroomgames->set_num_participants($num_participants);
							$newuser_chatroomsgames_data[] = $newuser_chatroomgames;
							$count++;
							$games_count++;
						}

						if($games_count >= 2) break;
					}

					if($count >= self::$CHATROOM_RETURN_COUNT)
						break;
				}
			}

			return array('free_chatrooms_games' => $newuser_chatroomsgames_data);
		}

        public function get_tagged_users($room_name, &$merchant_detail)
        {
            $grouped = array(
                'tagged_by_me' => array(),
                'tagged_by_others' => array(),
                'tagged_by_none' => array()
            );

			$users = $this->get_chatroom_users($room_name, $merchant_detail->username);

            // if only the merchant is in the chatroom, just return
            if (1 == count($users) && $users[0] == $merchant_detail->username)
            {
                return $groupped;
            }

            /*
             * In case we want to access the DB directly,
             * the code belows gets the merchant tags for the chatroom users
             *
             * Note that the user categorization loop would have to be udpated because
             * memcached keys are pre-pended by the key namespace Memcached::$KEYSPACE_MERCHANT_TAG
            */
            /*
            $query = sprintf('
                    SELECT userid.username as username, merchanttag.MerchantUserID as MerchantUserID
                    FROM userid, merchanttag
                    WHERE merchanttag.UserID=userid.id
                    AND userid.username in ('%s')
                ',
                implode('\',\'', array_map('addslashes', $users))
            );

            $tagged_users = array();
            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

            while($stmt->fetch())
            {
                $tagged_users[$row['username']] = $row['MerchantUserID'];
            }
            $stmt->close();
            $this->closeSlaveConnection();
            /**/

       	    $merchant_dao = new MerchantDAO();
			
			// Now categorize chatroom members into 3 groups
            foreach($users as $username)
            {	
            	// the merchant who tagged the user
				$merchant_tag = $merchant_dao->get_merchant_tag_from_username($username);
				
				if ($username == $merchant_detail->username)
                {
                    continue; // filter out current merchant
                }

                if (!isset($merchant_tag))
                {
                   $grouped['tagged_by_none'][] = $username;
                }
                else if ($merchant_tag == $merchant_detail->username)
                {
                    $grouped['tagged_by_me'][] = $username;
                }
                else
                {
                    $grouped['tagged_by_others'][] = $username;
                }
            }

            // okay, users are groupped, sort each group alphabetically
            asort($grouped['tagged_by_me']);
            asort($grouped['tagged_by_others']);
            asort($grouped['tagged_by_none']);

            return $grouped;
        }

        function get_users_tagged_by_merchant($room_name, &$merchant_detail)
        {
            $grouped = $this->get_tagged_users($room_name, $merchant_detail);
            return $grouped['tagged_by_me'];
        }

        function get_users_tagged_by_merchants_other_than($room_name, &$merchant_detail)
        {
            $grouped = $this->get_tagged_users($room_name, $merchant_detail);
            return $grouped['tagged_by_others'];
        }

        function get_users_tagged_by_none($room_name, &$merchant_detail)
        {
            $grouped = $this->get_tagged_users($room_name, $merchant_detail);
            return $grouped['tagged_by_none'];
        }

        function get_total_chatrooms_count()
        {
			$memcache = Memcached::get_instance();
			return $memcache->get(Memcached::$KEYSPACE_CHATROOMS_COUNT);
        }

 		function get_chatroom_game_bot($id)
        {
        	// Get Bots From XCache
			$bots = XCache::getInstance()->get
				(
					XCache::KEYSPACE_CHATROOM_GAME_BOTS
					, array(&$this, '_get_chatroom_game_bots_from_source')
					, 86400
				);

			$bot = null;
			if(in_array($id, array_keys($bots)))
			{
				// Get Bot
				$bot = new Bot($bots[$id]);

				// Get Chatrooms From XCache
				$chatrooms = XCache::getInstance()->get
				(
					XCache::KEYSPACE_CHATROOM_GAME_ROOMS . strtoupper($bot->displayname)
					, array(&$this, '_get_chatroom_game_rooms_from_source')
					, 864000
					, array('callback_args' => array($bot->game))
				);

				// Covert Chatroom Array To Object
				array_walk($chatrooms, array(&$this, '_convert_chatroom_array_to_object'));

				// Find Chatroom Object In ICE
				$chatrooms_data = array();
				for($i = 1; $i <= 10; $i++)
				{
					$bot_name = $bot->game.' '.$i;
					$num_participants = $this->get_num_participants($bot_name);

					// Update Object With Number Of Participants
					if(isset($chatrooms[$bot_name]))
					{
						$chatrooms[$bot_name]->set_num_participants($num_participants);
						$chatrooms_data[] = $chatrooms[$bot_name];
					}
				}
			}

        	return array('bot' => $bot, 'chatrooms' => $chatrooms_data);
		}

        function get_chatroom_game_bots($offset, $number_of_entries = 10)
        {
        	// Get Bots From XCache
			$bots = XCache::getInstance()->get
				(
					XCache::KEYSPACE_CHATROOM_GAME_BOTS
					, array(&$this, '_get_chatroom_game_bots_from_source')
					, 86400
				);

			// Remove Sort Order 0 Games From The List
			$bots_filtered = array_filter($bots, array(&$this, '_filter_hidden_chatroom_game_bots'));

			// Paginate: Show Only What Is Required
			$bots_sliced = array_slice($bots_filtered, $offset, $number_of_entries);

			// Covert Bot Array To Object
			array_walk($bots_sliced, array(&$this, '_convert_bot_array_to_object'));

        	return array('bots' => $bots_sliced, 'total_count' => sizeof($bots_filtered));
		}

		public function _get_chatroom_game_rooms_from_source($bot_name)
        {
			// Loop Through 10 Rooms For Each Bot
			$chatroom_names = array();
			for($i = 1; $i <= 10; $i++)
			{
				$chatroom_names[] = $bot_name.' '.$i;
			}

			try
			{
				$chatrooms = array();
				$query = 'SELECT id, name, description, maximumsize FROM chatroom WHERE name IN (\''.implode('\',\'', $chatroom_names).'\') AND status = 1';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				while($stmt->fetch())
				{
				   $chatrooms[$row['name']] = array('id' => $row['id'], 'name' => $row['name'], 'description' => $row['description'], 'maximumsize' => $row['maximumsize']);
				}
				$stmt->close();
				$this->closeSlaveConnection();

				return $chatrooms;
			}
			catch (Exception $e)
			{
				error_log("unable to fetch Games Chat Room from database");
			}

			return null;
        }

        /**
		* get populaer chatrooms using fusion ice.
		* @throws com_projectgoth_fusion_slice_ObjectNotFoundException
		* @throws Ice_LocalException
		* @throws IceDAOException
		* @return array ice array object
		*/
        public function get_popular_chatrooms()
		{
			$popular_chatrooms = array();
			try
			{
				$popular_chatrooms = $this->get_connection_prx()->getPopularChatRooms();
			}
			catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
			{
				error_log($ex->getMessage());
			}
			catch(Ice_LocalException $ex)
			{
				error_log($ex->getMessage());
			}
			catch (IceDAOException $ex)
			{
				error_log($ex->getMessage());
			}
			return $popular_chatrooms;
		}

        public function _get_chatroom_game_bots_from_source()
        {
			try
			{
				$bots = array();
				$query = 'SELECT
							  b.id
							, b.game
							, b.displayname
							, b.description
							, b.commandname
							, b.type
							, b.leaderboards
							, b.emoticonkeylist
							, b.sortorder
							, b.status
							, g.id AS group_id
							, g.name AS group_name
							, g.picture AS group_picture
						FROM
							bot b
						LEFT JOIN
							groups g
						ON
							g.id = b.groupid
						WHERE
							b.status = 1
						ORDER BY
							b.sortorder ASC';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				while($stmt->fetch())
				{
				   $bots[$row['id']] = array
				   (
					   	  'id' => $row['id']
					   	, 'game' => $row['game']
					   	, 'displayname' => $row['displayname']
					   	, 'description' => $row['description']
					   	, 'commandname' => $row['commandname']
					   	, 'type' => $row['type']
					   	, 'leaderboards' => $row['leaderboards']
					   	, 'emoticonkeylist' => $row['emoticonkeylist']
					   	, 'sortorder' => $row['sortorder']
					   	, 'group_id' => $row['group_id']
					   	, 'group_name' => $row['group_name']
					   	, 'group_picture' => $row['group_picture']
					   	, 'status' => $row['status']
				   	);
				}
				$stmt->close();
				$this->closeSlaveConnection();

				return $bots;
			}
			catch (Exception $e)
			{
				error_log("unable to fetch Bots from database");
			}

			return null;
        }

        public function _filter_hidden_chatroom_game_bots($data)
        {
        	return ($data['sortorder'] != 0);
        }

        public function _convert_bot_array_to_object(&$value, $key)
        {
        	$value = new Bot($value);
        }

		public function _convert_chatroom_array_to_object(&$value, $key)
        {
        	$value = new NewuserChatroom($value);
        }

		function get_user_owned_chatrooms($username, $offset=0, $num_entries=10)
        {
			$query = "select SQL_CALC_FOUND_ROWS c.*, (select group_concat(keyword) from chatroomkeyword ck, keyword k where ck.keywordid = k.id and ck.chatroomid = c.id) as keywords from chatroom c where c.userowned = 1 and c.creator = '".$username."' and c.status = 1 limit ".$offset.", ".$num_entries."; SELECT FOUND_ROWS()";
			if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
			{
				while ($row = $results->fetch_array(MYSQLI_ASSOC)){
					$chatrooms[] = $row;
					}
				$this->getSlaveConnection()->next_result();
				list($total_results)=$this->getSlaveConnection()->store_result()->fetch_row();
			}
			$this->closeSlaveConnection();
			$user_chatrooms_data=array();

			if(!empty($chatrooms))
			{
				foreach ($chatrooms as $chatroom)
				{
						$num_participants = $this->get_num_participants($chatroom['Name']);
						$chatroom=array_merge($chatroom,array("size" => $num_participants));

						$user_chatrooms_data[] = $chatroom;
				}
			}

			$total_pages=ceil($total_results/$num_entries);

			$user_owned_chatrooms_data = array("totalresults" => $total_results, "totalpages" => $total_pages, "chatrooms" => $user_chatrooms_data);

			return $user_owned_chatrooms_data;
        }

        function get_user_moderator_chatrooms($username,$offset=0,$num_entries=10)
        {
			$memcache = Memcached::get_instance();
			$chatrooms = $memcache->get($username."/mod_chatrooms");

			if(empty($chatrooms))
			{
				$username = $this->getSlaveConnection()->escape_string($username);
				$query ="select A.* from ( select c.*, (select group_concat(keyword) from chatroomkeyword ck, keyword k
						where ck.keywordid = k.id and ck.chatroomid = c.id) as keywords from chatroom c, chatroommoderator cm
						where cm.chatroomid=c.id and cm.username = '%s' and c.status = 1
						union
						select c.*, (select group_concat(keyword) from chatroomkeyword ck, keyword k
						where ck.keywordid = k.id and ck.chatroomid = c.id) as keywords from chatroom c,groupmember gm
						where gm.groupid=c.groupid and gm.username='%s' and gm.type=3 and gm.status=1) A ";
				$query = sprintf($query, $username ,$username);
				$chatrooms=array();
				if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
				{

			       while ($row = $results->fetch_array(MYSQLI_ASSOC))
			 			$chatrooms[] = $row;

			    }


				$this->closeSlaveConnection();
				$memcache->add_or_update($username."/mod_chatrooms", $chatrooms, self::$MEMCACHE_EXPIRY);
			}
			$total_results=count($chatrooms);

			(count($chatrooms) < $num_entries ) ? $limit = count($chatrooms) : $limit = $num_entries ;

			$cur_chatrooms=array();
			for($i=$offset;$i < $limit;$i++)
				$cur_chatrooms[]=$chatrooms[$i];

			$user_chatrooms_data=array();

			if(!empty($cur_chatrooms))
			{
				foreach ($cur_chatrooms as $chatroom)
				{
						$num_participants = $this->get_num_participants($chatroom['Name']);
						$chatroom=array_merge($chatroom,array("size" => $num_participants));

						$user_chatrooms_data[] = $chatroom;
				}
			}

			$total_pages=ceil($total_results/$num_entries);

			$user_mod_chatrooms_data = array("totalresults" => $total_results, "totalpages" => $total_pages, "chatrooms" => $user_chatrooms_data);

			return $user_mod_chatrooms_data;
        }

        function get_chatroom_info($chatroom_name)
        {
        	$query="select * from chatroom where name= ?";
        	$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
        	$stmt->bind_param('s', $chatroom_name);
			$stmt->execute();
			$stmt->bind_result($chatroom);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			return $chatroom;


        }

		/**
		 * Join chatroom
		 * @param string $roomname
		 * @return ChatroomDAO $this
		 */
		function join_chatroom($roomname)
		{
			try
			{
				$sessionPrx = $this->get_connection_prx()->getSessionObject();
				$chatroom = $this->registry->get_object_prx()
					->findChatRoomObject($roomname)
					->addParticipant(
					  $this->get_user_prx()
					, $sessionPrx
					, SessionUtilities::get_session_id()
					, getRemoteIPAddress()
					, getMobileDevice()
					, getUserAgent()
				);
				$sessionPrx->chatroomJoined($chatroom, $roomname);
			}
			catch(Exception $ex)
			{
			}
			return $this;
		}

		/**
		 * Convert into user owned chatroom
		 * @param string $chatroom_name
		 * @return ChatroomDAO $this
		 */
		function convert_into_user_owned_chatroom($chatroom_name)
		{
			try
			{
				$chatroom = $this->registry->get_object_prx()
					->findChatRoomObject($chatroom_name)
					->convertIntoUserOwnedChatRoom();
			}
			catch (Exception $e)
			{
			}
			return $this;
		}

		/**
		 * Convert into group chatroom
		 * @param string $chatroom_name
		 * @param integer $group_id
		 * @param string $group_name
		 * @return ChatroomDAO $this
		 */
		function convert_into_group_chatroom($chatroom_name, $group_id, $group_name = '')
		{
			try
			{
				$chatroom = $this->registry->get_object_prx()
					->findChatRoomObject($chatroom_name)
					->convertIntoGroupChatRoom($group_id, $group_name);
			}
			catch (Exception $e)
			{
			}
			return $this;
		}
	}
?>
