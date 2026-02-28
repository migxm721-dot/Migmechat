<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("Photo", get_domain_directory() . "/photo.php");

	class PhotosDAO extends DAO
	{
		static $known_existing_photos = array();

		public function get_photo_id_from_file_id($file_id)
		{
			$photo_id = 0;
			$query = "SELECT id FROM scrapbook WHERE fileid = ? AND status = 1 LIMIT 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $file_id);
			$stmt->execute();

			$stmt->bind_result($photo_id);
			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return $photo_id;
		}

		public function is_photo_owned_by_user($image_id, $username)
		{
			$query = "
				SELECT ID
				FROM scrapbook
				WHERE ID = ?
				AND Username = ?
			";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("is", $image_id, $username);
			$stmt->execute();
			$stmt->bind_result($fetched_id);
			$res = $stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			return (bool) $res;
		}

		public function get_single_photo($session_user, $username, $image_id)
		{
			// INACTIVE(0), PRIVATE(1), PUBLIC(2), CONTACTS_ONLY(3), REPORTED(4);
			$allow_status = array();

			if(empty($username))
				$username = $session_user;

			// We use view_wall privacy settings because it is the same.
			if(!$this->user_can_view_wall($session_user, $username))
				throw new Exception(sprintf(_('You are not allowed to view %s\'s photos'), $username));

			if($session_user == $username)
			{
				$allow_status = array(1,2,3,4);
			}
			elseif($this->users_are_friends($session_user, $username))
			{
				$allow_status = array(2,3,4);
			}
			else
			{
				$allow_status = array(2,4);
			}

			$query = "SELECT scrapbook.id, scrapbook.username, scrapbook.fileid, scrapbook.datecreated AS scrapbookdatecreated, scrapbook.receivedfrom, scrapbook.status, scrapbook.description, file.size, file.datecreated AS filedatecreated, file.mimetype, file.width, file.height, file.length, file.uploadedby FROM scrapbook INNER JOIN file ON scrapbook.fileid = file.id WHERE scrapbook.username = ? AND scrapbook.status IN (".implode(',', $allow_status).") AND scrapbook.id = ? LIMIT 1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $username, $image_id);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$photo = new Photo($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			$older_photo_exist = false;
			$newer_photo_exist = false;
			if($offset > 0)
			{
				$older_photo_exist = true;
			}
			if(isset($photo))
			{
				$newer_photo_exist = true;
			}

			return array("photo" => $photo, "offset" => $offset, "older_photo_exist" => $older_photo_exist, "newer_photo_exist" => $newer_photo_exist);
		}

		public function get_single_photo_with_offset($session_user, $username, $item_id, $offset)
		{
			// INACTIVE(0), PRIVATE(1), PUBLIC(2), CONTACTS_ONLY(3), REPORTED(4);
			$allow_status = array();

			if(empty($username))
				$username = $session_user;

			// We use view_wall privacy settings because it is the same.
			if(!$this->user_can_view_wall($session_user, $username))
				throw new Exception(sprintf(_('You are not allowed to view %s\'s photos'), $username));

			if($session_user == $username)
			{
				$allow_status = array(1,2,3,4);
			}
			elseif($this->users_are_friends($session_user, $username))
			{
				$allow_status = array(2,3,4);
			}
			else
			{
				$allow_status = array(2,4);
			}

			$min = 0;
			if( $offset == 0 ){
				$limit = '';
			} else {
				$limit = ' LIMIT ?,3';
				$min = $offset - 1;
			}
			$query = "SELECT scrapbook.id, scrapbook.username, scrapbook.fileid, scrapbook.datecreated AS scrapbookdatecreated, scrapbook.receivedfrom, scrapbook.status, scrapbook.description, file.size, file.datecreated AS filedatecreated, file.mimetype, file.width, file.height, file.length, file.uploadedby FROM scrapbook INNER JOIN file ON scrapbook.fileid = file.id WHERE scrapbook.username = ? AND scrapbook.status IN (".implode(',', $allow_status).") ORDER BY scrapbook.id DESC ". $limit;

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			if( $offset == 0 ){
				$stmt->bind_param("s", $username);
			} else {
				$stmt->bind_param("si", $username, $min );
			}
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$photos  = array();
			while( $stmt->fetch() )
			{
				$photos[] = new Photo($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			$i = 0;
			$older_photo_exist = false;
			$newer_photo_exist = false;
			$total = count($photos) - 1;

			foreach($photos as $key=>$val){

				if( $val->item_id == $item_id ){
					$photo = $val;
					$offset = $min + $i;
					if($total > $i){
						$newer_photo_exist = true;
						$next_idx = $i + 1;
						$prev_idx = $i - 1;

						if( !empty( $photos[$next_idx] ) )
							$next_idit = $photos[$next_idx]->item_id;

						if( !empty( $photos[$prev_idx] ) )
							$prev_idit = $photos[$prev_idx]->item_id;
					}
				}
				$i++;
			}


			if($offset > 0)
			{
				$older_photo_exist = true;
			}

			return array("photo" => $photo, "offset" => $offset, "older_photo_exist" => $older_photo_exist, "newer_photo_exist" => $newer_photo_exist, "next_idit" => $next_idit, "prev_idit" => $prev_idit);
		}

		public function get_photo($session_user, $username, $offset)
		{
			// INACTIVE(0), PRIVATE(1), PUBLIC(2), CONTACTS_ONLY(3), REPORTED(4);
			$allow_status = array();

			if(empty($username))
				$username = $session_user;

			// We use view_wall privacy settings because it is the same.
			if(!$this->user_can_view_wall($session_user, $username))
				throw new Exception(sprintf(_('You are not allowed to view %s\'s photos'), $username));

			if($session_user == $username)
			{
				$allow_status = array(1,2,3,4);
			}
			elseif($this->users_are_friends($session_user, $username))
			{
				$allow_status = array(2,3,4);
			}
			else
			{
				$allow_status = array(2,4);
			}

			$query = "SELECT scrapbook.id, scrapbook.username, scrapbook.fileid, scrapbook.datecreated AS scrapbookdatecreated, scrapbook.receivedfrom, scrapbook.status, scrapbook.description, file.size, file.datecreated AS filedatecreated, file.mimetype, file.width, file.height, file.length, file.uploadedby FROM scrapbook INNER JOIN file ON scrapbook.fileid = file.id WHERE scrapbook.username = ? AND scrapbook.status IN (".implode(',', $allow_status).") ORDER BY scrapbook.id DESC LIMIT ?, 2";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $username, $offset);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$photos = array();
			while( $stmt->fetch() )
			{
				$photos[] = new Photo($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			$older_photo_exist = false;
			$newer_photo_exist = false;
			if($offset > 0)
			{
				$older_photo_exist = true;
			}
			if(isset($photos[1]))
			{
				$newer_photo_exist = true;
			}
			return array("photos" => $photos, "offset" => $offset, "older_photo_exist" => $older_photo_exist, "newer_photo_exist" => $newer_photo_exist);
		}

		public function get_photos($session_user, $username, $offset, $number_of_entries = 10)
		{
			$allow_status = array();

			// We use view_wall privacy settings because it is the same.
			if(!$this->user_can_view_wall($session_user, $username))
				throw new Exception(sprintf(_('You are not allowed to view %s\'s photos'), $username));

			if($session_user == $username)
			{
				$allow_status = array(1,2,3,4);
			}
			elseif($this->users_are_friends($session_user, $username))
			{
				$allow_status = array(2,3,4);
			}
			else
			{
				$allow_status = array(2,4);
			}

			$query = 'SELECT
							SQL_CALC_FOUND_ROWS
							scrapbook.id,
							scrapbook.username,
							scrapbook.fileid,
							scrapbook.datecreated AS scrapbookdatecreated,
							scrapbook.receivedfrom,
							scrapbook.status,
							scrapbook.description,
							file.size,
							file.datecreated AS filedatecreated,
							file.mimetype,
							file.width,
							file.height,
							file.length,
							file.uploadedby
						FROM
							scrapbook
						INNER JOIN
							file
						ON
							scrapbook.fileid = file.id
						WHERE
							scrapbook.username = "%s"
							AND scrapbook.status IN (%s)
						ORDER BY
							scrapbook.id DESC
						LIMIT %s, %s;
						SELECT FOUND_ROWS()';

    		$query = sprintf($query,
    					$this->getSlaveConnection()->escape_string($username),
    					implode(',', $allow_status),
    					intval($offset),
    					intval($number_of_entries)
    				);
        	$photos = array();
		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		            $photos[] = new Photo($row);
		        }

		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }

			$this->closeSlaveConnection();

        	return array('photos' => $photos, 'total_count' => $total_count);
		}

		### Due to time constrain, we have no time to covert the rest of the platform
		public function get_photos_old($session_user, $username, $max_photos_to_return, $offset = 0)
		{
			$allow_status = array();
			$limit = $max_photos_to_return + 1;

			if(empty($username))
				$username = $session_user;

			// We use view_wall privacy settings because it is the same.
			if(!$this->user_can_view_wall($session_user, $username))
				throw new Exception(sprintf(_('You are not allowed to view %s\'s photos'), $username));

			if($session_user == $username)
			{
				$allow_status = array(1,2,3,4);
			}
			elseif($this->users_are_friends($session_user, $username))
			{
				$allow_status = array(2,3,4);
			}
			else
			{
				$allow_status = array(2,4);
			}

			$query = "SELECT scrapbook.id, scrapbook.username, scrapbook.fileid, scrapbook.datecreated AS scrapbookdatecreated, scrapbook.receivedfrom, scrapbook.status, scrapbook.description, file.size, file.datecreated AS filedatecreated, file.mimetype, file.width, file.height, file.length, file.uploadedby FROM scrapbook INNER JOIN file ON scrapbook.fileid = file.id WHERE scrapbook.username = ? AND scrapbook.status IN (".implode(',', $allow_status).") ORDER BY scrapbook.id DESC LIMIT ?, ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("sii", $username, $offset, $limit);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$photos = array();
			while( $stmt->fetch() )
			{
				$photos[] = new Photo($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			$older_photos_exist = false;

			if (sizeof($photos) > $max_photos_to_return) {
				$older_photos_exist = true;
				array_pop($photos);
			}

			$first_id = 0;
			$last_id = 0;
			if(count($photos) > 0) {
				$first_id = reset($photos)->item_id;
				$last_id = end($photos)->item_id;
			}
			return array("photos" => $photos, "last_photo_id" => $last_id, "first_photo_id" => $first_id, "older_photos_exist" => $older_photos_exist, 'offset' => $offset);
		}

		public function like_photo($session_username, $scrapbook_id, $like)
		{
			$should_allow_likes = SystemProperty::get_instance()->get_boolean(SystemProperty::Photo_PhotoLikeEnabled, true);
			if (!$should_allow_likes)
				return;

			// Ensure that scrapbook id is not 0
			if(intval($scrapbook_id) <= 0)
				return;

			// Ensure that like is either -1 (dislike) or 1 (like)
			if ($like != 1 && $like != -1)
				return;

			if (!$this->photo_exists($scrapbook_id)) //FRAME-245
				return;

			// Ensure that user has permission to like the scrapbook. Default to 10
			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be migLevel %s or higher to like a photo'), $reputation_level_permission['required_level']));

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO scrapbooklike (scrapbookid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $scrapbook_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			// Update the scrapbooklikesummary table only if there is change
			if ($affected_rows > 0) {
				$query = "SELECT CAST(SUM(type = 1) AS UNSIGNED INTEGER) AS numlikes, CAST(ABS(SUM(type = -1)) AS UNSIGNED INTEGER) AS numdislikes FROM scrapbooklike WHERE scrapbookid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $scrapbook_id);
				$stmt->execute();
				$stmt->bind_result($numlikes, $numdislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($numlikes) ? $numlikes : 0;
				$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

				$query = "INSERT INTO scrapbooklikesummary (scrapbookid, numlikes, numdislikes) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE numlikes = ?, numdislikes = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iiiii", $scrapbook_id, $new_num_likes, $new_num_dislikes, $new_num_likes, $new_num_dislikes);
				$stmt->execute();
				$stmt->close();
			}
			$this->closeMasterConnection();
			// Return the new number of likes and dislikes
			return $this->get_photo_likes($scrapbook_id, true);
		}

		public function dislike_photo($session_username, $scrapbook_id)
		{
			return $this->like_photo($session_username, $scrapbook_id, -1);
		}

		public function get_photo_likes($scrapbook_id, $from_master = false)
		{
			// Return the new number of likes and dislikes
			$query = "SELECT numlikes, numdislikes FROM scrapbooklikesummary WHERE scrapbookid = ?";
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}
			$stmt->bind_param("i", $scrapbook_id);
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

		public function get_photos_likes($scrapbook_ids)
		{
			if(is_array($scrapbook_ids) && !empty($scrapbook_ids)) {
				$scrapbook_ids = array_map('intval', $scrapbook_ids);
				$scrapbook_ids_sql = implode(',', $scrapbook_ids);
				// Return the new number of likes and dislikes
				$query = "SELECT scrapbookid, numlikes, numdislikes FROM scrapbooklikesummary WHERE scrapbookid IN($scrapbook_ids_sql)";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$scrapbook_likes = array();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while( $stmt->fetch())
				{
					$scrapbook_likes[$row['scrapbookid']] = array('numlikes' => intval($row['numlikes']), 'numdislikes' => intval($row['numdislikes']));
				}
				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();
				return $scrapbook_likes;
			}
		}

		/**
		 * Check status of photo (cross reference of scrapbook & file table)
		 * @param int $scrapbook_id Photo ID
		 * @return boolean Does photo exist?
		 */
		public function photo_exists($scrapbook_id)
		{
			if (
				   ! is_numeric($scrapbook_id)
				|| $scrapbook_id < 0
			)
			{
				return false;
			}
			$mem = self::$known_existing_photos;
			if (!isset($mem[$scrapbook_id]))
			{
				$numphotos = 0;
				$query = "SELECT CAST(count(scrapbook.id) AS UNSIGNED INTEGER) AS numphotos FROM scrapbook INNER JOIN file ON scrapbook.fileid = file.id WHERE scrapbook.id = ?";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $scrapbook_id);
				$stmt->execute();
				$stmt->bind_result($numphotos);
				$stmt->fetch();
				$stmt->close();
				$this->closeSlaveConnection();
				$mem[$scrapbook_id] = ($numphotos > 0);
			}
			return (bool)$mem[$scrapbook_id];
		}
}
?>