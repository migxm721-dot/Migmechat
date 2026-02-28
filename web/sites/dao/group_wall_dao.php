<?php
	fast_require('DAO', get_dao_directory() . '/dao.php');
	fast_require('GroupDAO', get_dao_directory() . '/group_dao.php');
	fast_require('GroupWallPost', get_domain_directory() . '/group/group_wall_post.php');
	fast_require('GroupWallPostComment', get_domain_directory() . '/group/group_wall_post_comment.php');

	class GroupWallPostDAO extends DAO
	{

		const WEIGHT_WALL_POST = 5;
		const WEIGHT_WALL_POST_COMMENT = 3;
		const WEIGHT_WALL_POST_LIKE = 1;

		/**
		*
		*	Get the total wall posts in a group
		*
		**/
		public function get_total_group_wall_posts($group_id)
		{
			$query = 'SELECT COUNT(*) AS total FROM groupwallpost WHERE groupid = ? AND status = 1';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$total=0;

			if($stmt->fetch())
			{
				$total = $data['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $total;
		}

		public function get_group_wall($session_username, $group_id, $offset, $number_of_entries = 10, $older_than_id = 0)
		{
			if($older_than_id > 0)
			{
				$query = 'SELECT
								SQL_CALC_FOUND_ROWS
								groupwallpost.*,
								userid.username authorusername
							FROM
								groupwallpost, userid
							WHERE
								groupwallpost.groupid = %s
								AND groupwallpost.id < %s
								AND groupwallpost.authoruserid = userid.id
								AND groupwallpost.status = 1
							ORDER BY
								groupwallpost.id DESC
							LIMIT %s, %s;
							SELECT FOUND_ROWS()';

	    		$query = sprintf($query,
	    					intval($group_id),
	    					intval($older_than_id),
	    					intval($offset),
	    					intval($number_of_entries)
	    				);
			}
			else
			{
				$query = 'SELECT
								SQL_CALC_FOUND_ROWS
								groupwallpost.*,
								userid.username authorusername
							FROM
								groupwallpost, userid
							WHERE
								groupwallpost.groupid = %s
								AND groupwallpost.authoruserid = userid.id
								AND groupwallpost.status = 1
							ORDER BY
								groupwallpost.id DESC
							LIMIT %s, %s;
							SELECT FOUND_ROWS()';

	    		$query = sprintf($query,
	    					intval($group_id),
	    					intval($offset),
	    					intval($number_of_entries)
	    				);
			}
        	$posts = array();
		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		            $posts[] = new GroupWallPost($row);
		        }

		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }

			$this->closeSlaveConnection();

        	return array('posts' => $posts, 'total_count' => $total_count);
		}

		/*
		 * Get the wall posts of all the groups the user is member of
		 */
		public function get_group_wall_for_user_groups($session_username, $offset, $number_of_entries = 10)
		{
			$query = "SELECT
							SQL_CALC_FOUND_ROWS
							groupwallpost.*,
							groups.name groupname,
							userid.username authorusername
						FROM
							groupwallpost, userid, groupmember, groups
						WHERE
							groupmember.username = '%s'
							AND groupwallpost.groupid = groupmember.GroupID
							AND groupwallpost.groupid = groups.ID
							AND groupwallpost.authoruserid = userid.id
							AND groupwallpost.status = 1
						ORDER BY
							groupwallpost.datecreated DESC
						LIMIT %s, %s;
						SELECT FOUND_ROWS()";

    		$query = sprintf($query,
    					$this->getSlaveConnection()->escape_string($session_username),
    					intval($offset),
    					intval($number_of_entries)
    				);

        	$posts = array();
		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		            $posts[] = $row;
		        }

		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }
			foreach($posts as &$post)
			{
				$post = new GroupWallPost($post);
			}

			$this->closeSlaveConnection();

        	return array('posts' => $posts, 'total_count' => $total_count);
		}

		/**
		*
		*	Get group type
		*
		**/
		public function get_group_type($group_id)
		{
			$query = 'SELECT type FROM groups WHERE id = ?';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$type = $data['type'];
			}
			else
			{
				$type = -1;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $type;
		}

		/**
		*
		*	Get group wall for previewing.
		*
		**/
		public function get_group_wall_preview($group_id, $max_posts_to_return)
		{
			//Ensure group is public
			if($this->get_group_type($group_id) == 1 || $this->get_group_type($group_id) == 2)
			{
				throw new Exception(_('Group is not public or by approval.'));
			}

			$query = 'SELECT
							groupwallpost.*,
							userid.username authorusername
						FROM
							groupwallpost, userid
						WHERE
							groupwallpost.groupid = ?
							AND groupwallpost.authoruserid = userid.id
							AND groupwallpost.status = 1
						ORDER BY
							groupwallpost.id DESC
						LIMIT ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $max_posts_to_return);
			$stmt->execute();
			$stmt->store_result();
			$posts = array();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() )
			{
				$posts[] = new GroupWallPost($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array('posts'=>$posts);
		}

		public function get_group_wall_post($session_username, $group_id, $group_wall_post_id)
		{
			$query = 'SELECT
							groupwallpost.*,
							userid.username authorusername
						FROM
							groupwallpost, userid
					   WHERE
							groupwallpost.authoruserid = userid.id
							AND groupwallpost.groupid = ?
							AND groupwallpost.id = ?
							AND groupwallpost.status = 1';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $group_wall_post_id);
			$stmt->execute();
			$stmt->store_result();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$post = new GroupWallPost($data);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			if (!$this->user_is_member_of_group($session_username, get_value_from_array('groupid', $data)))
				throw new Exception(_('You must be a member of the group to view its posts'));

			return $post;
		}

		public function get_group_wall_post_comments($session_username, $group_id, $group_wall_post_id, $offset, $number_of_entries = 10, $older_than_id = 0, $desc_order = true)
		{
			$order = 'DESC';
			if(!$desc_order)
				$order = 'ASC';

			if($older_than_id > 0)
			{
				$query = 'SELECT
								SQL_CALC_FOUND_ROWS
								groupwallpostcomment.*,
								userid.username authorusername
							FROM
								groupwallpostcomment, userid
							WHERE
								groupwallpostcomment.groupwallpostid = %s
								AND groupwallpostcomment.id < %s
								AND groupwallpostcomment.userid = userid.id
								AND groupwallpostcomment.status = 1
							ORDER BY
								groupwallpostcomment.id %s
							LIMIT %s, %s;
							SELECT FOUND_ROWS()';

	    		$query = sprintf($query,
	    					intval($group_wall_post_id),
	    					intval($older_than_id),
	    					$order,
	    					intval($offset),
	    					intval($number_of_entries)
	    				);
			}
			else
			{
				$query = 'SELECT
								SQL_CALC_FOUND_ROWS
								groupwallpostcomment.*,
								userid.username authorusername
							FROM
								groupwallpostcomment, userid
							WHERE
								groupwallpostcomment.groupwallpostid = %s
								AND groupwallpostcomment.userid = userid.id
								AND groupwallpostcomment.status = 1
							ORDER BY
								groupwallpostcomment.id %s
							LIMIT %s, %s;
							SELECT FOUND_ROWS()';

	    		$query = sprintf($query,
	    					intval($group_wall_post_id),
	    					$order,
	    					intval($offset),
	    					intval($number_of_entries)
	    				);
			}
        	$comments = array();
		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		            $comments[] = new GroupWallPostComment($row);
		        }

		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }

			$this->closeSlaveConnection();

        	return array('comments' => $comments, 'total_count' => $total_count);
		}

		public function create_group_wall_post($session_username, $group_id, $body)
		{
			if (!$this->user_is_admin_of_group($session_username, $group_id))
				throw new Exception(_('You must be an admin of the group to create a post'));

			$body = htmlentities(strip_tags($body));

			$query = 'INSERT INTO groupwallpost (groupid, authoruserid, datecreated, body, type) SELECT ?, userid.id, NOW(), ?, 1 FROM userid WHERE userid.username = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('iss', $group_id, $body, $session_username);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return;
			}

			$new_post_id = $stmt->insert_id;  // Get the ID of the newly inserted row
			$stmt->close();

			// Select the newly inserted row to create a GroupWallPost domain object to return
			$query = 'SELECT
							groupwallpost.*,
							userid.username authorusername
						FROM
							groupwallpost, userid
						WHERE
							groupwallpost.id = ?
							AND groupwallpost.groupid = ?
							AND groupwallpost.authoruserid = userid.id
						LIMIT 1';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);

			$stmt->bind_param('ii', $new_post_id, $group_id);
			$stmt->execute();
			$stmt->store_result();
			$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			if($stmt->fetch())
			{
				$post = new GroupWallPost($row);
			}
			$stmt->close();
			$this->closeMasterConnection();

			//increment score
			$group_dao = new GroupDAO();
			$group_dao->increment_score($group_id, self::WEIGHT_WALL_POST);

			return $post;
		}

		public function remove_group_wall_post($session_username, $group_id, $group_wall_post_id)
		{
			$query = 'UPDATE
						groupwallpost, groupmember
					SET
						groupwallpost.status = 0
					WHERE
						groupwallpost.id = ?
						AND groupwallpost.groupid = ?
						AND groupwallpost.groupid = groupmember.groupid
						AND groupmember.status = 1
						AND groupmember.type = 2
						AND groupmember.username = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('iis', $group_wall_post_id, $group_id, $session_username);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			//decrement group score
			$group_dao = new GroupDAO();
			$group_dao->decrement_score($group_id, (-1)*self::WEIGHT_WALL_POST);

			return true;
		}

		public function comment_on_group_wall_post($session_username, $group_id, $group_wall_post_id, $comment)
		{
			if (!$this->user_is_member_of_group($session_username, $group_id))
				throw new Exception(_('You must be a member of the group to comment on its posts'));

			$comment = htmlentities(strip_tags($comment));

			// Increment the NumComments field in groupwallpost (done first to avoid a MySQL transaction deadlock)
			$query = 'UPDATE groupwallpost SET numcomments = numcomments + 1 WHERE id = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_wall_post_id);
			$stmt->execute();

			$query = 'INSERT INTO groupwallpostcomment (groupwallpostid, userid, datecreated, comment) SELECT ?, userid.id, NOW(), ? FROM userid WHERE userid.username = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('iss', $group_wall_post_id, $comment, $session_username);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return;
			}

			$new_comment_id = $stmt->insert_id;  // Get the ID of the newly inserted row
			$stmt->close();

			// Select the newly inserted row to create a GroupWallPostComment domain object to return
			$query = 'SELECT
							groupwallpostcomment.*,
							userid.username authorusername
						FROM
							groupwallpostcomment, userid
						WHERE
							groupwallpostcomment.id = ?
							AND groupwallpostcomment.userid = userid.id
						LIMIT 1';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);

			$stmt->bind_param('i', $new_comment_id);
			$stmt->execute();
			$stmt->store_result();
			$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			if($stmt->fetch())
			{
				$new_comment = new GroupWallPostComment($row);
			}

			$stmt->close();
			$this->closeMasterConnection();

			//increment group score
			$group_dao = new GroupDAO();
			$group_dao->increment_score($group_id, self::WEIGHT_WALL_POST_COMMENT);

			return $new_comment;
		}

		public function remove_group_wall_post_comment($session_username, $group_id, $group_wall_post_id, $group_wall_post_comment_id)
		{
			if (!$this->user_is_admin_of_group($session_username, $group_id))
				throw new Exception(_('You must be an admin of the group to remove a comment'));

			// Decrement the NumComments field in groupwallpost (done first to avoid a MySQL transaction deadlock)
			$query = 'UPDATE
							groupwallpostcomment, groupwallpost
						SET
							groupwallpost.numcomments = groupwallpost.numcomments - 1
						WHERE
							groupwallpost.id = groupwallpostcomment.groupwallpostid
							AND groupwallpost.groupid = ?
							AND groupwallpostcomment.id = ?';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('ii', $group_id, $group_wall_post_comment_id);
			$stmt->execute();

			$query = 'UPDATE groupwallpostcomment SET status = 0 WHERE id = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_wall_post_comment_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();

			if($affected_rows):
				$group_dao = new GroupDAO();
				$group_dao->decrement_score($group_id, (-1)*self::WEIGHT_WALL_POST_COMMENT);
				return true;
			else:
				return false;
			endif;
		}

		public function like_group_wall_post($session_username, $group_id, $group_wall_post_id, $like = 1)
		{
			if ($like != 1)
				return;

			if (!$this->user_is_member_of_group($session_username, $group_id))
				throw new Exception(_('You must be a member of the group'));

			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			// Get an exclusive lock on the groupwallpost table (as we'll be updating it below)
			$query = 'SELECT * FROM groupwallpost WHERE id = ? FOR UPDATE';
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $group_wall_post_id);
			$stmt->execute();
			$stmt->close();

			$query = 'INSERT INTO groupwallpostlike (groupwallpostid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?';

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('iiii', $group_wall_post_id, $session_userid, $like, $like);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();

			$num_likes_updated = false;

			// Update the groupwallpost row if a (dis)like was added (i.e. the user didn't just (dis)like after they have already done so)
			if ($affected_rows > 0) {
				// Get the new number of likes and dislikes
				$query = 'SELECT CAST(SUM(CASE type WHEN 1 THEN 1 ELSE 0 END) AS UNSIGNED INTEGER) AS numlikes, CAST(SUM(CASE type WHEN -1 THEN 1 ELSE 0 END) AS UNSIGNED INTEGER) AS numdislikes FROM groupwallpostlike WHERE groupwallpostid=?';
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param('i', $group_wall_post_id);
				$stmt->execute();
				$stmt->bind_result($numlikes, $numdislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($numlikes) ? $numlikes : 0;
				$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

				$query = 'UPDATE groupwallpost SET numlikes = ?, numdislikes = ? WHERE id = ?';
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param('iii', $new_num_likes, $new_num_dislikes, $group_wall_post_id);
				$stmt->execute();
				$stmt->close();

				$num_likes_updated = true;

				//incrementing score of the group
				$group_dao = new GroupDAO();
				$group_dao->increment_score($group_id, self::WEIGHT_WALL_POST_LIKE);
			}

			// Now get the number of likes and dislikes if we didn't just calculate it
			if (!$num_likes_updated)
			{
				$query = 'SELECT numlikes, numdislikes FROM groupwallpost WHERE id = ?';
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param('i', $group_wall_post_id);
				$stmt->execute();

				$stmt->bind_result($new_num_likes, $new_num_dislikes);
				$stmt->fetch();
				$stmt->close();
			}

			$this->closeMasterConnection();

			// Return the new number of likes and dislikes
			return array(
				'num_likes' => $new_num_likes,
				'num_dislikes' => $new_num_dislikes,
			);
		}

		public function dislike_group_wall_post($session_username, $group_wall_post_id)
		{
			return $this->like_group_wall_post($session_username, $group_wall_post_id, -1);
		}
	}
?>