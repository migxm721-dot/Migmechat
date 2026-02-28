<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
    fast_require("UserDAO", get_dao_directory() . "/user_dao.php");

    fast_require("AvatarItem", get_domain_directory() . "/avatar/avatar_item.php");
	fast_require("AvatarItemCategory", get_domain_directory() . "/avatar/avatar_item_category.php");
	fast_require("AvatarBody", get_domain_directory() . "/avatar/avatar_body.php");
	fast_require("AvatarComment", get_domain_directory() . "/avatar/avatar_comment.php");
    fast_require("AvatarSet", get_domain_directory() . "/avatar/avatar_set.php");
    fast_require("AvatarCurrentItem", get_domain_directory() . "/avatar/avatar_current_item.php");

	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");

	fast_require("Redis", get_framework_common_directory() . "/redis.php");
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");


	class AvatarDAO extends DAO
	{
		protected static $NO_AVATAR = -1;
		/**
		*
		* Get the avatar item categories
		*
		**/
		public function get_categories($parent_category_id=0)
		{
			$query = "SELECT * FROM avataritemcategory WHERE avataritemcategoryid = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("i", $parent_category_id);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$items = array();
			while( $stmt->fetch() )
			{
				$items[] = new AvatarItemCategory($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $items;
		}

        /**
         *
         * Get the avatar categories
         *
         * @return array
         */
		public function get_all_categories()
		{
			$query = "SELECT * FROM avataritemcategory WHERE avataritemcategoryid";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$items = array();
			while( $stmt->fetch() )
			{
				$items[] = new AvatarItemCategory($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $items;
		}

        /**
         *
         * Get all the active sets in the system
         *
         * @return array
         */
        public function get_active_avatar_sets()
        {
            $query = "SELECT * FROM avatarset where status=1";
            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);

            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

            $sets = array();
            while( $stmt->fetch() )
            {
                $sets[] = new AvatarSet($row);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $sets;
        }

        /**
         *
         * Get the set items
         *
         * @param  $set_id
         * @return array
         */
        public function get_set_items($set_id)
        {
            $query = "SELECT avataritem.* FROM avatarsetitem, avataritem
                      WHERE avataritem.id = avatarsetitem.avataritemid AND
                      avatarsetitem.avatarsetid = ?";

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $set_id);

            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

            $items = array();
            while( $stmt->fetch() )
            {
                $items[] = new AvatarItem($row);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $items;
        }

		/**
		*
		* Check if the user has an avatar body associated
		*
		**/
		public function user_has_avatar($user_id)
		{
			$memcache = Memcached::get_instance();
			$has_avatar = $memcache->get("Avatar/HasAvatar/Id/" . $user_id);

			if( $has_avatar == false )
			{
				$query = "SELECT COUNT(*) FROM avataruserbody WHERE userid=?";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $user_id);
				$stmt->execute();

				$stmt->bind_result($bodies);
				$stmt->fetch();
				$stmt->close();
				$this->closeSlaveConnection();

				$has_avatar = ($bodies>=1) ? 1 : -1;

				$memcache->add_or_update("Avatar/HasAvatar/Id/". $user_id, $has_avatar);
			}

			return $has_avatar == 1;
		}

		/**
		*
		* Check if the user has an avatar body associated
		*
		**/
		public function user_has_avatar_from_username($username)
		{
			return $this->user_has_avatar($this->get_userid($username));
		}

		/**
		*
		* use the avatar body
		*
		**/
		public function use_body($user_id, $body_id)
		{
			// check that user_id is valid
			// getting the username for a userid is the quickest way to check for userid validity
			if ($user_id < 1 || false === $this->get_username($user_id))
			{
				throw new Exception(_("Invalid user id ($user_id)"));
			}

            if( $this->body_exists($body_id) == false )
                throw new Exception(_('Avatar body can not be owned.'));

			$body_owned = $this->body_is_owned($user_id, $body_id);

			$this->getMasterConnection()->autocommit(FALSE);

			$query = "UPDATE avataruserbody SET used=0 WHERE userid=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);
			$stmt->execute();
			$stmt->close();

			$query = "UPDATE avataruseritem SET used=0 WHERE userid=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);
			$stmt->execute();
			$stmt->close();

			if( $body_owned )
			{
				$query = "UPDATE avataruserbody SET used=1 WHERE userid=? AND avatarbodyid=?";
			}
			else
			{
				$query = "INSERT INTO avataruserbody (userid, avatarbodyid, used) VALUES (?, ?, 1)";

				$memcache = Memcached::get_instance();
				$memcache->delete("Avatar/HasAvatar/Id/" . $user_id);
			}

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $user_id, $body_id);
			$stmt->execute();

			if ($stmt->affected_rows != 1)
			{
				$this->getMasterConnection()->rollback();
				$stmt->close();
				$this->closeMasterConnection();
				return false;
			}
			$this->getMasterConnection()->commit();
			$this->closeMasterConnection();
			return true;
		}

		/**
		* Get the user avatar body
		**/
		public function get_user_avatar_body($username, $get_used_only=false, $from_master=false)
		{
			$query = "SELECT avatarbody.*, avataruserbody.used as Used
						FROM avataruserbody LEFT JOIN avatarbody on avatarbody.id = avataruserbody.avatarbodyid
					   WHERE avataruserbody.userid = ? AND avataruserbody.used = ? LIMIT 1;";

			// Get prepared statement
			if($from_master)
			{
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			}
			else
			{
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}

			$is_used = ($get_used_only?1:0);
			$user_id = $this->get_userid($username);

			$stmt->bind_param("ii", $user_id, $is_used);
			$stmt->execute();

			if($from_master)
			{
				$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			}
			else
			{
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			}


			if($stmt->fetch())
			{
				$body = new AvatarBody($row);
			}

			$stmt->close();

			// Close connection
			if($from_master)
			{
				$this->closeMasterConnection();
			}
			else
			{
				$this->closeSlaveConnection();
			}


			return $body;
		}

        /**
         *
         * Get the UUID for the avatar for the user
         *
         * @param  $user_id
         * @param bool $from_master
         * @return UUID
         */
		public function get_user_avatar_body_uuid_by_user_id($user_id, $from_master=false)
		{
			$memcache = Memcached::get_instance();
			$body_id = $memcache->get("Avatar/Body/Id/" . $user_id);
			$head_id = $memcache->get("Avatar/Head/Id/" . $user_id);

			if( $body_id == false || $head_id == false )
			{
				$query = "SELECT avataruserbody.bodyuuid as bodyuuid, avataruserbody.headuuid as headuuid
						  FROM avataruserbody WHERE avataruserbody.userid=? AND avataruserbody.used=? LIMIT 1;";

				if($from_master)
				{
					$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				}
				else
				{
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				}

				$is_used = 1;
				$stmt->bind_param("ii", $user_id, $is_used);
				$stmt->execute();

				if($from_master)
				{
					$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);
				}
				else
				{
					$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
				}

				$stmt->fetch();
				$stmt->close();

				// Close connection
				if($from_master)
				{
					$this->closeMasterConnection();
				}
				else
				{
					$this->closeSlaveConnection();
				}

				$body_id = get_value_from_array("bodyuuid", $data);
				$head_id = get_value_from_array("headuuid", $data);

				if( empty($body_id) ) $body_id = AvatarDAO::$NO_AVATAR;
				if( empty($head_id) ) $head_id = AvatarDAO::$NO_AVATAR;

				$memcache->add_or_update("Avatar/Body/Id/". $user_id, $body_id);
				$memcache->add_or_update("Avatar/Head/Id/". $user_id, $head_id);
			}

			if($body_id == AvatarDAO::$NO_AVATAR ) $body_id = "";
			if($head_id == AvatarDAO::$NO_AVATAR ) $head_id = "";

			return array("body_key"=>$body_id, "head_key"=>$head_id);
		}

		public function get_user_avatar_body_uuid($username, $from_master=false)
		{
			return $this->get_user_avatar_body_uuid_by_user_id($this->get_userid($username), $from_master);
		}

		public function set_user_avatar_body_uuid($username, $body_id, $body_key, $head_key)
		{
			$user_id = $this->get_userid($username);

			$query = "UPDATE avataruserbody SET bodyuuid=?, headuuid = ? WHERE userid=? AND avatarbodyid=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ssii", $body_key, $head_key, $user_id, $body_id);

			$stmt->execute();
			$this->closeMasterConnection();

			$memcache = Memcached::get_instance();
			$memcache->add_or_update("Avatar/Body/Id/". $user_id, $body_id);
			$memcache->add_or_update("Avatar/Head/Id/". $user_id, $head_id);
		}

		/**
		* Get the user avatar items
		**/
		public function get_user_avatar_items($username, $get_used_only=false, $from_master=false)
		{
			$query = "SELECT avataritem.*, avataruseritem.Used as Used,
							avataritemcategory.id as CategoryID, avataritemcategory.name as CategoryName
						FROM avataruseritem
						LEFT JOIN avataritem ON avataritem.id = avataruseritem.avataritemid
						LEFT JOIN avataritemcategory on avataritem.categoryid = avataritemcategory.id
						WHERE avataruseritem.userid = ? and avataruseritem.Used = ?
						ORDER BY avataritem.zorder ASC";

			$user_id = $this->get_userid($username);
			$get_used = $get_used_only?1:0;
            if( $from_master )
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			else
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ii", $user_id, $get_used);
			$stmt->execute();
			if($from_master)
				$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			else
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$items = array();
			while( $stmt->fetch() )
			{
				$items[] = new AvatarItem($row);
			}

			$stmt->close();
			if( $from_master )
				$this->closeMasterConnection();
			else
				$this->closeSlaveConnection();

			return $items;
		}

        /**
         *
         * Add set to the user
         *
         * @throws Exception
         * @param  $user_id
         * @param  $item_id
         * @param bool $used
         * @return bool
         */
        public function add_avatar_set_to_user($user_id, $set_id)
		{
			// check that user_id is valid
			// getting the username for a userid is the quickest way to check for userid validity
			if ($user_id < 1 || false === $this->get_username($user_id))
			{
				throw new Exception("Invalid user id ($user_id)");
			}

			// TODO: we should verify that $set_id is valid as well...


			$this->getMasterConnection()->autocommit(FALSE);

			if($this->set_is_owned($user_id, $set_id, true, false) )
			{
				$this->closeMasterConnection();

				throw new Exception(_('You already own this set'));
			}

			$query = "INSERT INTO avataruserset (userid, avatarsetid) VALUES (?,?)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $user_id, $set_id);
			$stmt->execute();

			$res = ($stmt->affected_rows >= 1);
			$stmt->close();

			if ($res)
			{
				$this->getMasterConnection()->commit();
			}
			else
			{
				$this->getMasterConnection()->rollback();
			}

			$this->closeMasterConnection();
			return $res;
		}

		/**
		* Add an item to the user
		**/
		public function add_avatar_item_to_user($user_id, $item_id, $used=false, $throw=true)
		{
			// check that user_id is valid
			// getting the username for a userid is the quickest way to check for userid validity
			if ($user_id < 1 || false === $this->get_username($user_id))
			{
				throw new Exception("Invalid user id ($user_id)");
			}

			// TODO: we should verify that $item_id is valid as well...


			$this->getMasterConnection()->autocommit(FALSE);

			if($this->item_is_owned($user_id, $item_id, true, false) )
            {
				$this->closeMasterConnection();

                if( $throw )
				    throw new Exception(_('Sorry this item is unique and you already own one'));
                else
                    return false;
            }

			$query = "INSERT INTO avataruseritem (userid, avataritemid, used) VALUES (?,?,0)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $user_id, $item_id);
			$stmt->execute();

			$res = ($stmt->affected_rows >= 1);
			$stmt->close();

			if ($res)
			{
				$this->getMasterConnection()->commit();
			}
			else
			{
				$this->getMasterConnection()->rollback();
			}

			$this->closeMasterConnection();

			return $res;
		}

        /**
         *
         * Attach the set to the user
         *
         * @throws Exception
         * @param  $user_id
         * @param  $set_id
         * @return void
         */
        public function equip_set_for_user($user_id, $set_id)
        {
            $requires_ownership = $this->set_requires_ownership($set_id);
            if( !$this->set_is_owned($user_id, $set_id) )
            {
                if($requires_ownership)
                    throw new Exception(_('You do not own this set'));
                else
                    $this->add_avatar_set_to_user($user_id, $set_id);
            }
        }

		/**
		* Equip an item for the user
		**/
		public function equip_item_for_user($user_id, $item_id, $type=0, $remove_same_type=false)
		{
			$requires_ownership = $this->item_requires_ownership($item_id);
			if(!$this->item_is_owned($user_id, $item_id))
			{
				if($requires_ownership)
					throw new Exception(_('You do not own this item'));
				else
					$this->add_avatar_item_to_user($user_id, $item_id);
			}

			if( $this->item_is_equipped($user_id, $item_id) )
				throw new Exception(_('This item is already being used.'));

			$this->getMasterConnection()->autocommit(FALSE);

			// un equip any items of that type
			if( $remove_same_type && $type>0 )
			{
				$query = "UPDATE avataruseritem, avataritem
							SET avataruseritem.used=0
							WHERE avataruseritem.avataritemid=avataritem.id AND avataritem.type=?
							AND avataruseritem.userid=?";

				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("ii", $type, $user_id);
				$stmt->execute();

				$stmt->close();
			}

			$query = "UPDATE avataruseritem
						SET avataruseritem.used=1
						WHERE avataruseritem.avataritemid=? AND
							avataruseritem.userid=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $item_id, $user_id);
			$stmt->execute();

			if ($stmt->affected_rows < 1) {
				$this->getMasterConnection()->rollback();
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Unable to equip item to the user'));
			}

			$this->getMasterConnection()->commit();
			$this->closeMasterConnection();
			return true;
		}

		/**
		* Unequip an item for a user
		**/
		public function unequip_item_for_user($user_id, $item_id)
		{
			if(!$this->item_is_owned($user_id, $item_id))
				throw new Exception(_('You do not own this item'));

			if(!$this->item_is_equipped($user_id, $item_id) )
				throw new Exception(_('Item is not being used'));

			$this->getMasterConnection()->autocommit(FALSE);
			$query = "UPDATE avataruseritem
						SET avataruseritem.used=0
						WHERE avataruseritem.avataritemid=? AND
							avataruseritem.userid=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $item_id, $user_id);
			$stmt->execute();

			if ($stmt->affected_rows < 1) {
				$this->getMasterConnection()->rollback();
				$stmt->close();
				$this->closeMasterConnection();
				throw new Exception(_('Unable to un-equip item from the user'));
			}

			$this->getMasterConnection()->commit();
			$this->closeMasterConnection();
			return true;
		}

		/**
		* Check if an item can be equipped
		**/
		public function can_equip_item($item_id, $body_id)
		{
			$query = "SELECT COUNT(*)
						FROM avataritem
						LEFT JOIN avatarbodyitem ON avatarbodyitem.avataritemid = avataritem.id
						WHERE avataritem.id = ? AND avatarbodyitem.avatarbodyid=?;";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $item_id, $body_id);
			$stmt->execute();

			$stmt->bind_result($count);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			if($count > 0 )
			{
				return true;
			}

			$query = "SELECT COUNT(*)
						FROM avataritem
						LEFT JOIN avatarbodyitem ON avatarbodyitem.avataritemid = avataritem.id
						WHERE avataritem.id=? AND ISNULL(avatarbodyitem.avatarbodyid)";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $item_id);
			$stmt->execute();

			$stmt->bind_result($count);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			if($count > 0 )
			{
				return true;
			}

			return false;
		}

		/**
		*
		* Get a single avatar item category
		*
		**/
		public function get_category($category_id)
		{
			$query = "SELECT * FROM avataritemcategory WHERE id=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $category_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$category = new AvatarItemCategory($data);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $category;
		}

		/**
		*
		* Get the avatar bodies according to gender or blank for all bodies
		*
		**/
		public function get_avatar_bodies($gender="", $page=1, $number_of_entries = 10)
		{
			$query = "SELECT * FROM avatarbody";
			if( !empty($gender) )
			{
				$query .= " WHERE gender=?";
			}

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			if( !empty($gender) )
			{
				$stmt->bind_param("s", $gender);
			}
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_of_entries);
			$count = 1;

			$bodies = array();
			while( $stmt->fetch() && $count <= $number_of_entries)
			{
				$bodies[] = new AvatarItem($row);
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_of_entries);

			$stmt->free_result();

			$this->closeSlaveConnection();

			return array("bodies"=>$bodies, "total_pages"=>$total_pages, "total_results"=>$num_rows);

		}

		/**
		* Get an avatar item
		**/
		public function get_avatar_item($item_id)
		{
			$query = "SELECT avataritem.*, avataritemcategory.id as CategoryID, avataritemcategory.name as CategoryName
						FROM avataritem LEFT JOIN avataritemcategory ON avataritem.categoryid = avataritemcategory.id
					   WHERE avataritem.id = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $item_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$item = new AvatarItem($data);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $item;
		}

		/**
		*
		* Get the avatar items for a user
		*
		**/
		public function get_avatar_items($user_id, $body_id, $category_id, $page=1, $number_of_entries = 10)
		{
			$query =
				"SELECT * FROM
				(SELECT avataritem.*, avataritemcategory.name as CategoryName
				FROM avataritem, avataruseritem, avataritemcategory
				WHERE avataritemcategory.id = avataritem.categoryid
					AND avataruseritem.avataritemid = avataritem.id
					AND avataritem.usedonbody = 0
					AND avataritem.categoryid = ?
					AND avataruseritem.userid = ?
				UNION
				SELECT avataritem.*, avataritemcategory.name as CategoryName
				FROM avataritem, avataruseritem, avataritemcategory, avatarbodyitem
				WHERE avataritemcategory.id = avataritem.categoryid
					AND avataritem.id = avatarbodyitem.avataritemid
					AND avataruseritem.avataritemid = avataritem.id
					AND avatarbodyitem.avatarbodyid = ?
					AND avataritem.categoryid = ?
					AND avataruseritem.userid = ?
				UNION
				SELECT avataritem.*, avataritemcategory.name as CategoryName
				FROM avataritem, avataritemcategory
				WHERE avataritemcategory.id = avataritem.categoryid
					AND avataritem.usedonbody = 0
					AND avataritem.categoryid = ?
					AND avataritem.ownershiprequired = 0
					AND avataritem.status = 1
				UNION
				SELECT avataritem.*, avataritemcategory.name as CategoryName
				FROM avataritem, avataritemcategory, avatarbodyitem
				WHERE avataritemcategory.id = avataritem.categoryid
					AND avatarbodyitem.avataritemid = avataritem.id
					AND avatarbodyitem.avatarbodyid = ?
					AND avatarbodyitem.avataritemid = avataritem.id
					AND avataritem.categoryid = ?
					AND avataritem.ownershiprequired = 0
					AND avataritem.status = 1) items
				ORDER BY items.id DESC";


			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiiiiiii", $category_id, $user_id,
							$body_id, $category_id, $user_id, $category_id, $body_id, $category_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$items = array();

			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_of_entries);
			$count = 1;

			while( $stmt->fetch() && $count <= $number_of_entries)
			{
				$item = new AvatarItem($row);
				$items[] = $item;
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_of_entries);

			$stmt->free_result();

			$this->closeSlaveConnection();

			$ret_items = array();
			foreach($items as $item)
			{
				if($user_id!=0)
				{
					$item->used = $this->item_is_equipped($user_id, $item->id);
				}
				$ret_items[] = $item;
			}


			return array("items"=>$ret_items, "total_pages"=>$total_pages, "total_results"=>$num_rows);
		}

        /**
         *
         * Check if a set is owned
         *
         * @param  $user_id
         * @param  $item_id
         * @param bool $from_master
         * @return bool
         */
        public function set_is_owned($user_id, $set_id, $from_master=false, $close_connection=true)
		{
			$query = "SELECT COUNT(*) FROM avataruserset WHERE userid=? AND avatarsetid = ?";

			if( $from_master )
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			else
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ii", $user_id, $set_id);
			$stmt->execute();

			$stmt->bind_result($equip);
			$stmt->fetch();
			$stmt->close();

			if ($close_connection)
			{
				if( $from_master )
					$this->closeMasterConnection();
				else
					$this->closeSlaveConnection();
			}

			return $equip >= 1;
		}

		/**
		*
		* Check if the item is owned
		*
		**/
		public function item_is_owned($user_id, $item_id, $from_master=false, $close_connection=true)
		{
			$query = "SELECT COUNT(*) FROM avataruseritem WHERE userid=? AND avataritemid = ?";

			if( $from_master )
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			else
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ii", $user_id, $item_id);
			$stmt->execute();

			$stmt->bind_result($equip);
			$stmt->fetch();
			$stmt->close();

			// we might not want to close connection if this method is called as part of a transaction
			if ($close_connection)
			{
				if( $from_master )
					$this->closeMasterConnection();
				else
					$this->closeSlaveConnection();
			}

			return $equip >= 1;
		}

		/**
		*
		* Check if the item requires ownership
		*
		**/
		public function item_requires_ownership($item_id)
		{
			$query = "SELECT COUNT(*) FROM avataritem WHERE ownershiprequired=1 AND id=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $item_id);
			$stmt->execute();

			$stmt->bind_result($owner);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $owner == 1;
		}

        /**
         *
         * Check if the set requires ownership
         *
         * @param  $item_id
         * @return bool
         */
        public function set_requires_ownership($set_id)
		{
			$query = "SELECT COUNT(*) FROM avatarset WHERE ownershiprequired=1 AND id=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $set_id);
			$stmt->execute();

			$stmt->bind_result($owner);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $owner == 1;
		}

		/**
		* Check if the item is equipped
		**/
		protected function item_is_equipped($user_id, $item_id)
		{
			$query = "SELECT COUNT(*) FROM avataruseritem WHERE userid=? AND avataritemid = ? AND used=1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $user_id, $item_id);
			$stmt->execute();

			$stmt->bind_result($equip);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $equip >= 1;
		}

        /**
         * Check if the body exists
         * @param  $body_id
         * @return boolean
         */
        protected function body_exists($body_id)
        {
            $query = "SELECT COUNT(*) FROM avatarbody WHERE id=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $body_id);
			$stmt->execute();

			$stmt->bind_result($body);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $body > 0;
        }

		/**
		* Check if the user already has the body
		**/
		protected function body_is_owned($user_id, $body_id)
		{
			$query = "SELECT COUNT(*) FROM avataruserbody WHERE userid=? AND avatarbodyid = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $user_id, $body_id);
			$stmt->execute();

			$stmt->bind_result($body);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $body == 1;
		}

		/**
		* Check if the user can equip the item
		**/
		public function item_is_wearable($user_id, $item_id)
		{
			$query = "SELECT avatarbodyid FROM avataruserbody WHERE userid=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);
			$stmt->execute();

			$stmt->bind_result($body_id);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $this->can_equip_item($item_id, $body_id);
		}

		/**
		* Rate avatar
		**/
		public function rate_avatar($session_username, $avataruser_id, $rating)
		{
			// Ensure that ratings are between 1 and 5
			if ($rating < 0 || $rating > 5)
				return;
			// Ensure that user has permission to like. Default to 10
			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be Level %s or higher to like an avatar'), $reputation_level_permission['required_level']));

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO avatarrating (avataruserid, userid, datecreated, rating) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE rating = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $avataruser_id, $session_userid, $rating, $rating);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			// Update the avatarrating table only if there is change
			if ($affected_rows > 0) {
				$query2 = "SELECT CAST(SUM(rating) AS UNSIGNED INTEGER) AS total, CAST(COUNT(*) AS UNSIGNED INTEGER) AS numratings FROM avatarrating WHERE avataruserid = ? ";
				$stmt2 = $this->getMasterConnection()->get_prepared_statement($query2);
				$stmt2->bind_param("i", $avataruser_id);
				$stmt2->execute();
				$stmt2->bind_result($total, $numratings);
				$stmt2->fetch();
				$stmt2->close();

				$new_total = is_numeric($total) ? $total : 0;
				$new_numratings = is_numeric($numratings) ? $numratings : 0;
				$new_average = number_format($new_total/$new_numratings, 5);

				$query = "INSERT INTO avatarratingsummary (avataruserid, average, total, numratings) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE average = ?, total = ?, numratings = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("idiidii", $avataruser_id, $new_average, $new_total, $new_numratings, $new_average, $new_total, $new_numratings);
				$stmt->execute();
				$stmt->close();
			}
			$this->closeMasterConnection();
			// Return the new ratings
			return $this->get_avatar_ratings($avataruser_id, true);
		}

	/**
		* Get avatar ratings
		**/
		public function get_avatar_ratings($avataruser_id, $from_master = false)
		{
			// Return the new ratings
			$query = "SELECT average, total, numratings FROM avatarratingsummary WHERE avataruserid = ?";
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}
			$stmt->bind_param("i", $avataruser_id);
			$stmt->execute();
			$stmt->bind_result($average, $total, $num_ratings);
			$stmt->fetch();
			$stmt->free_result();
			$stmt->close();
			if($from_master) {
				$this->closeMasterConnection();
			} else {
				$this->closeSlaveConnection();
			}
			return array(
				'average' => $average,
				'total' => intval($total),
				'numratings' => intval($num_ratings)
			);
		}

		/**
		* Get avatar comment count
		**/
		public function get_avatar_comment_count($avataruser_id) {
			$posts_count = 0;
			$query = "SELECT COUNT(*) FROM avatarcomment WHERE avataruserid = ? AND status = 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $avataruser_id);
			$stmt->execute();
			$stmt->bind_result($posts_count);
			$stmt->fetch();
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();
			return $posts_count;
		}

		/**
		 * Get avatar comments (Many)
		 * @param string $session_username
		 * @param string $username
		 * @param int $offset
		 * @param int $number_of_entries
		 * @param int $older_than_id
		 * @return array params: posts, total_count
		 * @throws Exception You must be friends with $username to view their avatar comments
		 */
		public function get_avatar_wall($session_username, $username, $offset, $number_of_entries = 10, $older_than_id = 0)
		{
			if (!$this->user_can_view_wall($session_username, $username))
				throw new Exception(sprintf(_('You must be friends with %s to view their avatar comments'), $username));

			$system_property = SystemProperty::get_instance();
            $do_use_cache = $system_property->get_boolean(SystemProperty::UserAvatarCommentsCacheEnabled, true);
            $feature_enabled = $system_property->get_boolean(SystemProperty::UserAvatarCommentsRetrievalEnabled, true);

			if (!$feature_enabled)
			{
				return array('posts'=>array(), 'total_count'=>0);
			}

			$posts = array();
			$execute_multi_query = true;
			$update_cache = false;

			if($older_than_id > 0)
			{
				$query = 'SELECT
	        					SQL_CALC_FOUND_ROWS
								avatarcomment.*,
								userid_author.username authorusername
							FROM
								avatarcomment, userid userid_author, userid userid_owner
							WHERE
								avatarcomment.avataruserid = userid_owner.id
								AND userid_owner.username = "%s"
								AND avatarcomment.id < %s
								AND avatarcomment.userid = userid_author.id
								AND avatarcomment.status = 1
							ORDER BY
								avatarcomment.id DESC
							LIMIT %s, %s;
							SELECT FOUND_ROWS()';

	    		$query = sprintf($query,
	    					$this->getSlaveConnection()->escape_string($username),
	    					intval($older_than_id),
	    					intval($offset),
	    					intval($number_of_entries)
	    				);
			}
			else
			{
				$is_cache_miss = false;

				if($do_use_cache && $offset == 0)
				{
					//first page:
					//1. fetch from cache
					//2. cache-miss: fetch from db and populate cache
					$user_dao = new UserDAO();
					$comments = $this->fetch_comments_from_cache($user_dao->get_user_id($username));
					$comment_ids = $comments['comments'];
					if(isset($comment_ids) && count($comment_ids) > 0)
					{
						//cache hit
						$total_count = $comments['total_count'];
						$query = 'SELECT
									avatarcomment.*,
									userid_author.username authorusername
								FROM
									avatarcomment, userid userid_author, userid userid_owner
								WHERE
									avatarcomment.id in (%s)
									AND avatarcomment.avataruserid = userid_owner.id
									AND avatarcomment.userid = userid_author.id
								ORDER BY
									avatarcomment.id DESC';

						$query = sprintf($query, implode(",",$comment_ids));
						$execute_multi_query = false;
					}
					else
					{
						$is_cache_miss = true;
					}
				}

				if( !$do_use_cache || $is_cache_miss || $offset > 0)
				{
					$query = 'SELECT
		        					SQL_CALC_FOUND_ROWS
									avatarcomment.*,
									userid_author.username authorusername
								FROM
									avatarcomment, userid userid_author, userid userid_owner
								WHERE
									avatarcomment.avataruserid = userid_owner.id
									AND userid_owner.username = "%s"
									AND avatarcomment.userid = userid_author.id
									AND avatarcomment.status = 1
								ORDER BY
									avatarcomment.id DESC
								LIMIT %s, %s;
								SELECT FOUND_ROWS()';

		    		$query = sprintf($query,
		    					$this->getSlaveConnection()->escape_string($username),
		    					intval($offset),
		    					intval($number_of_entries)
		    				);

		    		$update_cache = $offset == 0;
				}
			}

			if($execute_multi_query)
			{
				if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
			    {
			        while ($row = $results->fetch_array(MYSQLI_ASSOC))
			        {
			            $posts[] = new AvatarComment($row);

			        }

			        $this->getSlaveConnection()->next_result();
			        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
			    }

				$this->closeSlaveConnection();
			}
			else
			{
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while( $stmt->fetch() )
				{
					$posts[] = new AvatarComment($row);
				}

				$stmt->close();
				$this->closeSlaveConnection();
			}

		 	//update cache if required
	        if ($update_cache && count($posts) > 0)
	        {
	        	$user_id = $posts[0]->avatar_userid;
	        	if(isset($user_id))
	        	{
	        		$this->update_avatar_comment_cache($user_id, $posts);
	        		$this->set_total_comments_count_in_cache($user_id, $total_count);
	        	}
	        }

        	return array('posts' => $posts, 'total_count' => $total_count);
		}

		/**
		* Get avatar comment (One)
		**/
		public function get_avatar_wall_post($session_username, $username, $avatar_comment_id)
		{
			$query = 'SELECT
						avatarcomment.*,
						userid_author.username authorusername,
						userid_owner.username ownerusername
					FROM
						avatarcomment, userid userid_author, userid userid_owner
					WHERE
						avatarcomment.avataruserid = userid_owner.id
						AND avatarcomment.userid = userid_author.id
						AND avatarcomment.id = ?
						AND avatarcomment.status = 1';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $avatar_comment_id);
			$stmt->execute();
			$stmt->store_result();
			$stmt->bind_result($data);
			$stmt->fetch();
			$this->closeSlaveConnection();

			if (!isset($data))
				return null;

			$post = new AvatarComment($data);

			return $post;
		}

		/**
		* Create avatar comment
		**/
		public function create_avatar_post($session_username, $username, $body)
		{
			if (!$this->users_are_friends($session_username, $username))
				throw new Exception(sprintf(_('You must be friends with %s to post comment'), $username));

			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be Level %s or higher to post a comment on avatar'), $reputation_level_permission['required_level']));

			$body = htmlentities(strip_tags($body));

			$query = 'INSERT INTO avatarcomment (avataruserid, userid, datecreated, comment) SELECT avataruserid.id, authoruserid.id, now(), ? FROM userid avataruserid, userid authoruserid WHERE avataruserid.username = ? AND authoruserid.username = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('sss', $body, $username, $session_username);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return;
			}

			// Get newly inserted ID
			$new_avatar_comment_id = $stmt->insert_id;
			$stmt->close();

			// Query the database again to get the latest comment
			$query = 'SELECT
							avatarcomment.*,
							userid.username authorusername
						FROM
							avatarcomment, userid
						WHERE
							avatarcomment.id = ?
							AND avatarcomment.userid = userid.id
						LIMIT 1';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $new_avatar_comment_id);
			$stmt->execute();
			$stmt->store_result();
			$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$post = new AvatarComment($row);
			}

			$this->insert_comment_to_cache($post->avatar_userid, $new_avatar_comment_id);
			$stmt->close();
			$this->closeMasterConnection();

			return $post;
		}

		/**
		* Remove avatar comment
		**/
		public function remove_avatar_post($session_username, $username, $avatar_wall_post_id)
		{
			//the query updates only when avatar wall post is that of session_user
			$query = 'UPDATE avatarcomment, userid SET avatarcomment.status = 0 WHERE avatarcomment.id = ? AND avatarcomment.avataruserid = userid.id AND userid.username = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $avatar_wall_post_id, $session_username);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();
			if ($affected_rows != 1)
				return false;

			//delete from cache (redis)
			$user_dao = new UserDAO();
			$this->delete_comment_from_cache($user_dao->get_user_id($username), $avatar_wall_post_id);

			return true;
		}

		/**
		* Like Avatar Comment
		**/
		public function like_avatar_wall_post($session_username, $username, $avatar_wall_post_id, $like = 1)
		{
			if ($like != 1)
				return;

			// Make sure the session user is allowed to (dis)like
			if (!$this->user_can_view_wall($session_username, $username))
				throw new Exception(sprintf(_('You must be friends with %s to like their avatar comments'), $username));

			// Ensure that user has permission to like. Default to 10
			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be Level %s or higher to like an avatar comment'), $reputation_level_permission['required_level']));

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO avatarcommentlike (avatarcommentid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $avatar_wall_post_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();

			if ($affected_rows > 0)
			{
				$query = "SELECT CAST(SUM(type = 1) AS UNSIGNED INTEGER) AS numlikes, CAST(ABS(SUM(type = -1)) AS UNSIGNED INTEGER) AS numdislikes FROM avatarcommentlike WHERE avatarcommentid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $avatar_wall_post_id);
				$stmt->execute();
				$stmt->bind_result($new_num_likes, $new_num_dislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($new_num_likes) ? $new_num_likes : 0;
				$new_num_dislikes = is_numeric($new_num_dislikes) ? $new_num_dislikes : 0;

				$query = "UPDATE avatarcomment SET numlikes = ?, numdislikes = ? WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iii", $new_num_likes, $new_num_dislikes, $avatar_wall_post_id);
				$stmt->execute();
				$stmt->close();
			}

			$this->closeMasterConnection();
			// Return the new number of likes and dislikes
			return $this->get_avatar_wall_post_likes($avatar_wall_post_id, true);
		}

		/**
		* Get Avatar Comment Likes/Dislikes
		**/
		public function get_avatar_wall_post_likes($avatar_wall_post_id, $from_master = false)
		{
			// Return the new number of likes and dislikes
			$query = 'SELECT numlikes, numdislikes FROM avatarcomment WHERE id = ?';
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}
			$stmt->bind_param('i', $avatar_wall_post_id);
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
			return array(
				'numlikes' => intval($num_likes),
				'numdislikes' => intval($num_dislikes)
			);
		}

		/**
		* Get Current User Avatar Items From Store
		**/
		function get_user_current_avatar_items($username)
		{
			$query = "SELECT
						avataritem.id AS AvatarItemID,
						avataritem.name AS AvatarItemName,
						avataritemcategory.name AS CategoryName,
						storeitem.id AS StoreItemID,
						avataritem.categoryid AS StoreItemCategoryID,
						storeitem.catalogimage AS StoreItemImage,
						storeitem.currency AS StoreItemCurrency,
						storeitem.price AS StoreItemPrice
					FROM
						avataruseritem
					LEFT JOIN
						avataritem ON avataritem.id = avataruseritem.avataritemid
					LEFT JOIN
						avataritemcategory ON avataritem.categoryid = avataritemcategory.id
					LEFT JOIN
						storeitem ON avataritem.id = storeitem.referenceid
					WHERE
						avataruseritem.userid = ?
						AND avataruseritem.Used = 1
						AND storeitem.type = 2";

			$user_id = $this->get_userid($username);
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param('i', $user_id);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$items = array();
			while( $stmt->fetch() )
			{
				$items[] = new AvatarCurrentItem($row);
			}

			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();

			return $items;
		}

		function set_total_comments_count_in_cache($user_id, $total_count)
		{
			$redis_instance = Redis::get_master_instance_for_user_id($user_id);
			$user_hash_key = Redis::KEYSPACE_USER . $user_id;

			if (!isset($redis_instance))
			{
				throw new Exception ("master redis instance for ".$user_id." not found");
			}

			$redis_instance->hset($user_hash_key, Redis::FIELD_NUM_OF_AVATAR_CMTS, $total_count);
			$redis_instance->disconnect();
		}

		function update_avatar_comment_cache($user_id, $posts)
		{
			$redis_instance = Redis::get_master_instance_for_user_id($user_id);
			$key = Redis::KEYSPACE_ENTITY_USER . $user_id . Redis::KEYSPACE_SEPARATOR . Redis::KEYSPACE_USER_AVATAR_CMT;

			if (!isset($redis_instance))
			{
				throw new Exception ("master redis instance for ".$user_id." not found");
			}

			//delete existing key (if exists)
			$redis_instance->del($key);

			//add the IDs to cache
			foreach($posts as $post)
			{
				$redis_instance->zadd($key, $post->id, $post->id);
			}

			$redis_instance->disconnect();
		}

		function insert_comment_to_cache($user_id, $comment_id)
		{
			$redis_instance = Redis::get_master_instance_for_user_id($user_id);
			$key = Redis::KEYSPACE_ENTITY_USER . $user_id . Redis::KEYSPACE_SEPARATOR . Redis::KEYSPACE_USER_AVATAR_CMT;
			$user_hash_key = Redis::KEYSPACE_USER . $user_id;

			if (!isset($redis_instance)){
				throw new Exception ("master redis instance for ".$user_id." not found");
			}

			//add new comment id
			$redis_instance->zadd($key, $comment_id, $comment_id);

			//if cache size exceeds 10, trim it
			//pre-condition: There will be a maximum of only 11 IDs
			if ( $redis_instance->zcard($key) > 10 )
			{
				//delete the one with the lowest score (oldest comment)
				$id_to_delete = $redis_instance->zrange($key, 0, 0);
				if(isset($id_to_delete)){
					$redis_instance->zrem($key, $id_to_delete[0]);
				}
			}

			//increment the total count
			$redis_instance->hincrby($user_hash_key, Redis::FIELD_NUM_OF_AVATAR_CMTS, 1);

			//disconnect
			$redis_instance->disconnect();
		}

		function delete_comment_from_cache($user_id, $comment_id)
		{
			$redis_instance = Redis::get_master_instance_for_user_id($user_id);
			$key = Redis::KEYSPACE_ENTITY_USER . $user_id . Redis::KEYSPACE_SEPARATOR . Redis::KEYSPACE_USER_AVATAR_CMT;
			$user_hash_key = Redis::KEYSPACE_USER . $user_id;

			if (!isset($redis_instance)){
				throw new Exception ("master redis instance for ".$user_id." not found");
			}

			//delete the post from cache
			$del_result = $redis_instance->zrem($key, $comment_id);

			//decrement the num_of_records
			if($del_result){
				$redis_instance->hincrby($user_hash_key, Redis::FIELD_NUM_OF_AVATAR_CMTS, -1);
			}

			$redis_instance->disconnect();

			return $del_result;
		}

		function fetch_comments_from_cache($user_id)
		{
			$redis_instance = Redis::get_slave_instance_for_user_id($user_id);
			$key = Redis::KEYSPACE_ENTITY_USER . $user_id . Redis::KEYSPACE_SEPARATOR . Redis::KEYSPACE_USER_AVATAR_CMT;
			$user_hash_key = Redis::KEYSPACE_USER . $user_id;

			$comment_ids = array();
			$total_num_posts_in_db = 0;
			if (isset($redis_instance))
			{
				$total_num_posts_in_db = intval($redis_instance->hget($user_hash_key, Redis::FIELD_NUM_OF_AVATAR_CMTS));
				$comment_ids = $redis_instance->zrevrange($key, 0, -1);
				$redis_instance->disconnect();
			}
			return array('comments' => $comment_ids, 'total_count' => $total_num_posts_in_db);
		}
	}
?>
