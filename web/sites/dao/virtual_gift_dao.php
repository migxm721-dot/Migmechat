<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("VirtualGiftSummary", get_domain_directory() . "/virtual_gift/virtual_gift_summary.php");
	fast_require("VirtualGiftReceived", get_domain_directory() . "/virtual_gift/virtual_gift_received.php");

	class VirtualGiftDAO extends DAO
	{
		public function get_virtual_gift_summary($session_user, $username)
		{
			$query = "select v1.id as id, v1.sender as sender, v2.virtualgiftid as giftid,
						v2.datecreated as datecreated, v2.giftcount as giftcount, v2.location16x16gif as location, v2.name as name
						from virtualgiftreceived v1,
						(select virtualgift.name, virtualgift.location16x16gif, virtualgiftid,
						count(*) as giftcount, max(datecreated) datecreated  from virtualgiftreceived, virtualgift
						where  virtualgift.id = virtualgiftreceived.virtualgiftid and username=?
						and virtualgiftreceived.removed = 0 group by virtualgiftid) v2 where v1.datecreated = v2.datecreated";

			if( $session_user != $username )
				$query .= " and private = 0 ";

			$query .= " order by v1.datecreated desc";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);

			$stmt->execute();

			$summaries = array();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() )
			{
				$virtual_gift_summary = new VirtualGiftSummary($row);
				$summaries[] = $virtual_gift_summary;
			}

			return $summaries;
		}

		public function get_virtual_gifts_received($session_user, $username, $page, $number_entries)
		{
			if ($number_entries > 20) $number_entries = 20;
			if ($number_entries < 1) $number_entries = 1;

			$key = strtoupper(
				Memcached::$KEYSPACE_VIRTUAL_GIFTS_RECEIVED . implode('/'
					, array($username
						, $username == $session_user ? 1 : 0
						, $page
						, $number_entries
					)
				)
			);
			$memcache = Memcached::get_instance();
			$result = $memcache->get($key);
			if (! empty($result)) return $result;

			$query = "select
						vgr.id as id,
						vgr.virtualgiftid as giftid,
						vgr.datecreated as datecreated,
						vgr.sender as sender,
						vgr.message as message,
						vgr.removed as removed,
						vgr.private as private,
						si.name as name,
						si.id as storeitemid,
						si.catalogimage as location
						from
						virtualgiftreceived vgr,
						storeitem si
						where
						vgr.username = ? and
						si.type = 1 and
						si.referenceid = vgr.virtualgiftid";

			if( $session_user != $username )
				$query .= " and vgr.private = 0 ";

			$query .= " order by vgr.id desc limit 100 ";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);

			$stmt->execute();

			$gifts = array();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$gifts_received = array();
			$gifts_ids = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$g = new VirtualGiftReceived($row);
				$gifts_received[] = $g;
				$gifts_ids[] = $row['id'];
				$count += 1;
			}
			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			$gifts_likes = $this->get_virtual_gifts_likes($gifts_ids);

			$gifts_comment_count = $this->get_virtualgiftreceived_comments_count($gifts_ids);

			$data = array("total_pages"=> $total_pages, "total_results"=>$num_rows, "gifts_received"=>$gifts_received, "gifts_likes" => $gifts_likes, "gifts_comment_count" => $gifts_comment_count);
			$memcache->add_or_update($key, $data, 300);
			return $data;
		}

		public function get_virtual_gift_received($session_user, $username, $virtualgiftreceived_id)
		{
			$query = "SELECT
								vgr.id AS id,
								vgr.virtualgiftid AS giftid,
								vgr.datecreated AS datecreated,
								vgr.sender AS sender,
								vgr.message AS message,
								vgr.removed AS removed,
								vgr.private AS private,
								si.id AS storeitemid,
								si.name AS name,
								si.catalogimage AS location
							FROM
								virtualgiftreceived vgr,
								storeitem si
							WHERE
								vgr.username = ? AND
								si.type = 1 AND
								si.referenceid = vgr.virtualgiftid AND
								vgr.id = ?";

			if( $session_user != $username )
				$query .= " AND vgr.private = 0";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $username, $virtualgiftreceived_id);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->fetch();
			$stmt->store_result();
			$gift = new VirtualGiftReceived($row);
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();
			return $gift;
		}


		public function like_virtual_gift($session_username, $virtualgiftreceived_id, $like)
		{
			// Ensure that like is either -1 (dislike) or 1 (like)
			if ($like != 1 && $like != -1)
				return;
			// Ensure that user has permission to like the virtual gift. Default to 10
			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be Level %s or higher to like a virtual gift'), $reputation_level_permission['required_level']));

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO virtualgiftreceivedlike (virtualgiftreceivedid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $virtualgiftreceived_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			// Update the virtualgiftreceivedlikesummary table only if there is change
			if ($affected_rows > 0)
			{
				$query = 'SELECT CAST(SUM(type = 1) AS UNSIGNED INTEGER) AS numlikes, CAST(ABS(SUM(type = -1)) AS UNSIGNED INTEGER) AS numdislikes FROM virtualgiftreceivedlike WHERE virtualgiftreceivedid = ?';
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $virtualgiftreceived_id);
				$stmt->execute();
				$stmt->bind_result($numlikes, $numdislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($numlikes) ? $numlikes : 0;
				$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

				$query = "INSERT INTO virtualgiftreceivedlikesummary (virtualgiftreceivedid, numlikes, numdislikes) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE numlikes = ?, numdislikes = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iiiii", $virtualgiftreceived_id, $new_num_likes, $new_num_dislikes, $new_num_likes, $new_num_dislikes);
				$stmt->execute();
				$stmt->close();
			}

			$this->closeMasterConnection();
			// Return the new number of likes and dislikes
			return $this->get_virtual_gift_likes($virtualgiftreceived_id, true);
		}

		public function dislike_virtual_gift($session_username, $virtualgiftreceived_id)
		{
			return $this->like_virtual_gift($session_username, $virtualgiftreceived_id, -1);
		}

		public function get_virtual_gift_likes($virtualgiftreceived_id, $from_master = false)
		{
			// Return the new number of likes and dislikes
			$query = "SELECT numlikes, numdislikes FROM virtualgiftreceivedlikesummary WHERE virtualgiftreceivedid = ?";
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}
			$stmt->bind_param("i", $virtualgiftreceived_id);
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

		public function get_virtual_gifts_likes($virtualgiftreceived_ids)
		{
			if(is_array($virtualgiftreceived_ids) && !empty($virtualgiftreceived_ids)) {
				$virtualgiftreceived_ids = array_map('intval', $virtualgiftreceived_ids);
				$virtualgiftreceived_ids_sql = implode(',', $virtualgiftreceived_ids);
				// Return the new number of likes and dislikes
				$query = "SELECT virtualgiftreceivedid, numlikes, numdislikes FROM virtualgiftreceivedlikesummary WHERE virtualgiftreceivedid IN($virtualgiftreceived_ids_sql)";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$virtualgiftreceivedid_likes = array();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while( $stmt->fetch())
				{
					$virtualgiftreceivedid_likes[$row['virtualgiftreceivedid']] = array('numlikes' => intval($row['numlikes']), 'numdislikes' => intval($row['numdislikes']));
				}
				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();
				return $virtualgiftreceivedid_likes;
			}
		}

		public function get_virtualgiftreceived_comment_count($virtualgiftreceived_id) {
			// Return the new ratings
			$query = "SELECT CAST(COUNT(*) AS UNSIGNED INTEGER) AS total FROM virtualgiftreceivedcomment WHERE virtualgiftreceivedid = ? AND status = 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $virtualgiftreceived_id);
			$stmt->execute();
			$stmt->bind_result($comment_count);
			$stmt->fetch();
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();
			return $comment_count;
		}

		public function get_virtualgiftreceived_comments_count($virtualgiftreceived_ids)
		{
			if(is_array($virtualgiftreceived_ids) && !empty($virtualgiftreceived_ids)) {
				$virtualgiftreceived_ids = array_map('intval', $virtualgiftreceived_ids);
				$virtualgiftreceived_ids_sql = implode(',', $virtualgiftreceived_ids);
				// Return the new number of likes and dislikes
				$query = "SELECT virtualgiftreceivedid, COUNT(*) AS comment_count FROM virtualgiftreceivedcomment WHERE status = 1 AND virtualgiftreceivedid IN($virtualgiftreceived_ids_sql) GROUP BY virtualgiftreceivedid";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$virtualgiftreceivedid_commentcount = array();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while( $stmt->fetch())
				{
					$virtualgiftreceivedid_commentcount[$row['virtualgiftreceivedid']] = array('comment_count' => intval($row['comment_count']));
				}
				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();
				return $virtualgiftreceivedid_commentcount;
			}
		}

		public function get_virtualgiftreceived_comments($session_username, $virtualgiftreceived_username, $virtualgiftreceived_id, $max_posts_to_return, $older_than_id=0, $newer_than_id=0)
		{
			if (!$this->user_can_view_wall($session_username, $virtualgiftreceived_username))
				throw new Exception(sprintf(_('You must be friends with %s to view their virtual gift comments'), $virtualgiftreceived_username));

			$params = array();

			$query = "SELECT
							virtualgiftreceivedcomment.*,
							user.username authorusername,
							user.displaypicture authordisplaypicture
						FROM
							virtualgiftreceivedcomment, user, userid userid_author
						WHERE
							virtualgiftreceivedcomment.userid = userid_author.id
							AND userid_author.username = user.username
							AND virtualgiftreceivedcomment.virtualgiftreceivedid = ?
							AND virtualgiftreceivedcomment.status = 1";
			$params[] = array('i'=>$virtualgiftreceived_id);

			if( $older_than_id != 0 )
			{
				$query = sprintf("%s AND virtualgiftreceivedcomment.id < ?", $query);
				$params[] = array('i'=>$older_than_id);
			}
			else if( $newer_than_id != 0 )
			{
				$query = sprintf("%s AND virtualgiftreceivedcomment.id > ?", $query);
				$params[] = array('i'=>$newer_than_id);
			}

			// We try to get $max_posts_to_return + 1 posts back, so we will know if older comments exist
			$query = sprintf("%s ORDER BY virtualgiftreceivedcomment.id DESC LIMIT ?", $query);
			$params[] = array('i'=>$max_posts_to_return + 1);

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$this->auto_bind_params($stmt, $params);
			$stmt->execute();

			$posts = array();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() )
			{
				$posts[] = new VirtualGiftReceivedComment($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			$older_posts_exist = false;
			if (sizeof($posts) > $max_posts_to_return) {
				$older_posts_exist = true;
				array_pop($posts);  // Remove the extra post (at the end of the array) we don't want to return
			}

			$first_id = 0;
			$last_id = 0;
			if(count($posts) > 0) {
				$first_id = reset($posts)->id;
				$last_id = end($posts)->id;
			}
			return array("posts"=>$posts, "last_post_id"=>$last_id, "first_post_id"=>$first_id, "older_posts_exist"=>$older_posts_exist);
		}

		public function get_virtualgiftreceived_comment($session_username, $virtualgiftreceived_comment_id)
		{
			$query = "SELECT
								virtualgiftreceivedcomment.*,
								user.username authorusername,
								user.displaypicture authordisplaypicture,
								userid_owner.username ownerusername
							FROM
								virtualgiftreceivedcomment, user, userid userid_author, userid userid_owner
							WHERE
								virtualgiftreceivedcomment.virtualgiftreceiveduserid = userid_owner.id
								AND virtualgiftreceivedcomment.userid = userid_author.id
								AND userid_author.username = user.username
								AND virtualgiftreceivedcomment.id = ?
								AND virtualgiftreceivedcomment.status = 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $virtualgiftreceived_comment_id);
			$stmt->execute();
			$stmt->bind_result($data);
			$stmt->fetch();
			$this->closeSlaveConnection();

			if (!isset($data))
				return null;

			$post = new VirtualGiftReceivedComment($data);
			$ownerusername = get_value_from_array("ownerusername", $data);

			if (!$this->user_can_view_wall($session_username, $ownerusername))
				throw new Exception(sprintf(_('You must be friends with %s to view their virtual gift comments'), $ownerusername));

			return $post;
		}

		public function create_virtualgiftreceived_comment($session_username, $virtualgiftreceived_username, $virtualgiftreceived_id, $comment)
		{
			if (!$this->users_are_friends($session_username, $virtualgiftreceived_username))
				throw new Exception(sprintf(_('You must be friends with %s to post comment'), $virtualgiftreceived_username));

			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be Level %s or higher to post a comment on virtual gift'), $reputation_level_permission['required_level']));

			$comment = htmlentities(strip_tags($comment));

			$query = "INSERT INTO virtualgiftreceivedcomment (virtualgiftreceivedid, virtualgiftreceiveduserid, userid, datecreated, comment)
						SELECT ?, virtualgiftreceiveduserid.id, authoruserid.id, now(), ?
						FROM userid virtualgiftreceiveduserid, userid authoruserid
						WHERE virtualgiftreceiveduserid.username = ? AND authoruserid.username = ?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("isss", $virtualgiftreceived_id, $comment, $virtualgiftreceived_username, $session_username);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return;
			}

			// Get newly inserted ID
			$new_virtualgiftreceived_comment_id = $stmt->insert_id;
			$stmt->close();

			// Query the database again to get the latest comment
			$query = "SELECT
							virtualgiftreceivedcomment.*,
							user.username authorusername,
							user.displaypicture authordisplaypicture
						FROM
							virtualgiftreceivedcomment, user, userid
						WHERE
							virtualgiftreceivedcomment.id = ?
							AND virtualgiftreceivedcomment.userid = userid.id
							AND userid.username = user.username
						LIMIT 1";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $new_virtualgiftreceived_comment_id);
			$stmt->execute();
			$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->fetch();
			$stmt->close();
			$this->closeMasterConnection();

			return new VirtualGiftReceivedComment($row);
		}

		public function remove_virtualgiftreceived_comment($session_username, $virtualgiftreceived_comment_username, $virtualgiftreceived_comment_id)
		{
			$query = "UPDATE virtualgiftreceivedcomment, userid SET virtualgiftreceivedcomment.status = 0 WHERE virtualgiftreceivedcomment.id = ? AND ((virtualgiftreceivedcomment.virtualgiftreceiveduserid = userid AND userid.username = ?) OR (virtualgiftreceivedcomment.userid = userid AND userid.username = ?))";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iss", $virtualgiftreceived_comment_id, $session_username, $virtualgiftreceived_comment_username);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();
			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function like_virtualgiftreceived_comment($session_username, $virtualgiftreceivedcomment_id, $like)
		{
			// Make sure the session user is allowed to (dis)like
			if (!$this->user_can_view_virtualgiftreceived_comment($session_username, $virtualgiftreceivedcomment_id))
				throw new Exception(sprintf(_('You must be friends with %s to like their virtual gift comments'), $username));
			// Ensure that like is either -1 (dislike) or 1 (like)
			if ($like != 1 && $like != -1)
				return;
			// Ensure that user has permission to like. Default to 10
			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be Level %s or higher to like a virtual gift comment'), $reputation_level_permission['required_level']));

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO virtualgiftreceivedcommentlike (virtualgiftreceivedcommentid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $virtualgiftreceivedcomment_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			if ($affected_rows > 0)
			{
				$query = 'SELECT CAST(SUM(type = 1) AS UNSIGNED INTEGER) AS numlikes, CAST(ABS(SUM(type = -1)) AS UNSIGNED INTEGER) AS numdislikes FROM virtualgiftreceivedcommentlike WHERE virtualgiftreceivedcommentid = ?';
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $virtualgiftreceivedcomment_id);
				$stmt->execute();
				$stmt->bind_result($numlikes, $numdislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($numlikes) ? $numlikes : 0;
				$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

				$query = "UPDATE virtualgiftreceivedcomment SET numlikes = ?, numdislikes = ? WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iii", $new_num_likes, $new_num_dislikes, $virtualgiftreceivedcomment_id);
				$stmt->execute();
				$stmt->close();
			}
			$this->closeMasterConnection();
			// Return the new number of likes and dislikes
			return $this->get_virtualgiftreceived_comment_likes($virtualgiftreceivedcomment_id, true);
		}

		public function dislike_virtualgiftreceived_comment($session_username, $virtualgiftreceivedcomment_id)
		{
			return $this->like_virtualgiftreceived_comment($session_username, $virtualgiftreceivedcomment_id, -1);
		}

		public function get_virtualgiftreceived_comment_likes($virtualgiftreceivedcomment_id, $from_master = false)
		{
			// Return the new number of likes and dislikes
			$query = "SELECT numlikes, numdislikes FROM virtualgiftreceivedcomment WHERE id = ?";
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}
			$stmt->bind_param("i", $virtualgiftreceivedcomment_id);
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

		protected function user_can_view_virtualgiftreceived_comment($username_viewing, $virtualgiftreceivedcomment_id)
		{
			$query = "SELECT userid.username FROM virtualgiftreceivedcomment, userid WHERE virtualgiftreceivedcomment.virtualgiftreceiveduserid = userid.id AND virtualgiftreceivedcomment.id = ? AND virtualgiftreceivedcomment.status = 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $virtualgiftreceivedcomment_id);
			$stmt->execute();
			$stmt->bind_result($comment_owner);
			$stmt->fetch();
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();
			if (!isset($comment_owner))
				return false;

			return $this->user_can_view_wall($username_viewing, $comment_owner);
		}
	}
?>