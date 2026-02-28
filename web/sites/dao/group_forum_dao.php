<?php
	fast_require("GroupForum", get_domain_directory() . "/group/group_forum.php");
	fast_require("GroupForumPost", get_domain_directory() . "/group/group_forum_post.php");
	fast_require("GroupForumPostComment", get_domain_directory() . "/group/group_forum_post_comment.php");

	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class GroupForumDAO extends DAO
	{
		const WEIGHT_FORUM_POST = 5;
		const WEIGHT_FORUM_POST_COMMENT = 3;
		const WEIGHT_FORUM_POST_LIKE = 1;

		public function get_public_group_forums($page, $number_entries, $limit=50)
		{
			$params = array();

			$query = "SELECT gf.*, g.name AS GroupName
							FROM groups g, groupforum gf
						   WHERE g.id=gf.groupid
							 AND g.status=1
							 AND g.type=0
						ORDER BY gf.lastupdated DESC,
						         gf.numcomments DESC,
						         gf.numposts DESC
						   LIMIT ?";
			$params[] = array('i'=>$limit);

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$this->auto_bind_params($stmt, $params);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$groupforums = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$groupforums[] = new GroupForum($row);
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "groupforums"=>$groupforums);
		}

		/**
		*
		*	List out groups owned by user and still a member of group
		*
		**/
		public function get_owned_forums($session_user, $page, $number_entries)
		{
			$query = "SELECT gf.*, g.name AS GroupName
						FROM groups g, groupmember m, groupforum gf
						WHERE g.id=gf.groupid AND g.id=m.groupid AND m.username=? AND m.status=1 AND g.status=1 AND g.createdby = ?
						ORDER BY gf.lastupdated DESC";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ss", $session_user, $session_user);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$groupforums = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$groupforums[] = new GroupForum($row);
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "groupforums"=>$groupforums);
		}

		/**
		*
		*	List out groups where user is a member
		*
		**/
		public function get_group_forums($session_user, $page, $number_entries)
		{
			$query = "SELECT gf.*, g.name AS GroupName
						FROM groups g, groupmember m, groupforum gf
						WHERE g.id=gf.groupid AND g.id=m.groupid AND m.username=? AND m.status=1 AND g.status=1
						ORDER BY gf.lastupdated DESC";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $session_user);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$groupforums = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$groupforums[] = new GroupForum($row);
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "groupforums"=>$groupforums);
		}

		public function like_group_forum_post($session_user, $session_userid, $group_id, $post_id, $like=1)
		{
			if ($like != 1 && $like != -1)
				return;

			// Make sure the session user is allowed to (dis)like
			$group_dao = new GroupDAO();
			$group_member = $group_dao->get_group_member($group_id, $session_user);

			if (isset($group_member))
			{
				// Get an exclusive lock on the userwallpost table (as we'll be updating it below)
				$query = "SELECT * FROM groupforumpost WHERE id=? FOR UPDATE";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $post_id);
				$stmt->execute();
				$stmt->close();

				$query = "INSERT INTO groupforumpostlike (groupforumpostid, userid, datecreated, type)
							VALUES (?, ?, NOW(), ?)
							ON DUPLICATE KEY UPDATE type=?";

				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iiii", $post_id, $session_userid, $like, $like);
				$stmt->execute();

				$affected_rows = $stmt->affected_rows;
				$stmt->close();

				$num_likes_updated = false;

				// Update the userwallpost row if a (dis)like was added (i.e. the user didn't just (dis)like after they have already done so)
				if ($affected_rows > 0) {
					// Get the new number of likes and dislikes
					$query = "SELECT CAST(SUM(CASE type WHEN 1 THEN 1 ELSE 0 END) AS UNSIGNED INTEGER) AS numlikes,
								CAST(SUM(CASE type WHEN -1 THEN 1 ELSE 0 END) AS UNSIGNED INTEGER) AS numdislikes
								FROM groupforumpostlike WHERE groupforumpostid=?";
					$stmt = $this->getMasterConnection()->get_prepared_statement($query);
					$stmt->bind_param("i", $post_id);
					$stmt->execute();
					$stmt->bind_result($numlikes, $numdislikes);
					$stmt->fetch();
					$stmt->close();

					$new_num_likes = is_numeric($numlikes)? $numlikes : 0;
					$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

					$query = "UPDATE groupforumpost SET numlikes=?, numdislikes=? WHERE id=?";
					$stmt = $this->getMasterConnection()->get_prepared_statement($query);
					$stmt->bind_param("iii", $new_num_likes, $new_num_dislikes, $post_id);
					$stmt->execute();
					$stmt->close();

					$num_likes_updated = true;

					//incrementing score of the group
					$group_dao = new GroupDAO();
					$group_dao->increment_score($group_id, self::WEIGHT_FORUM_POST_LIKE);
				}

				// Now get the number of likes and dislikes if we didn't just calculate it
				if (!$num_likes_updated)
				{
					$query = "SELECT numlikes, numdislikes FROM groupforumpost WHERE id=?";
					$stmt = $this->getMasterConnection()->get_prepared_statement($query);
					$stmt->bind_param("i", $post_id);
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
			else
			{
				throw new Exception(_('You have to be a member of the Group'));
			}
		}

		public function dislike_group_forum_post($session_user, $session_userid, $group_id, $post_id)
		{
			$this->like_group_forum_post($session_user, $session_userid, $group_id, $post_id, -1);
		}

		public function get_group_forum($group_forum_id)
		{
			$query = "SELECT * FROM groupforum gf WHERE gf.id = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_forum_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$forum = new GroupForum($data);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $forum;
		}

		public function get_group_forum_by_groupid($group_id)
		{
			$query = "SELECT * FROM groupforum gf WHERE gf.groupid=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$forum = new GroupForum($data);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $forum;
		}

		public function create_forum($session_username, $group_id)
		{
			// check for admin rights
			$group_dao = new GroupDAO();
			$group_member = $group_dao->get_group_member($group_id, $session_username, TRUE);

			if(isset($group_member))
			{
				if($group_member->is_administrator() && $group_member->is_active())
				{
					if($this->get_group_forum_by_groupid($group_id) == null)
					{
						$this->getMasterConnection()->autocommit(FALSE);

						// create a forum
						$query = "INSERT INTO groupforum
									(groupid, datecreated)
									VALUES
									(?, NOW())";

						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("i", $group_id);
						$stmt->execute();

						if ($stmt->affected_rows != 1) {
							$this->getMasterConnection()->rollback();
							$stmt->close();
							$this->closeMasterConnection();
							return;
						}

						$new_forum_id = $stmt->insert_id;  // Get the ID of the newly inserted row

						$stmt->close();
						$this->getMasterConnection()->commit();

						$forum = $this->get_group_forum($new_forum_id);

						if(!isset($forum))
							return null;

						return $forum;
					}
					else
					{
						throw new Exception(_('There can only be one forum in a group.'));
					}
				}
				else
				{
					throw new Exception(_('You have to be an admin of the Group to create forum.'));
				}
			}
			else
			{
				throw new Exception(_('You have to be a member of the Group'));
			}
		}

		public function create_forum_post($session_user, $session_userid, $group_id, $forum_id, $title, $body)
		{
			// check for admin rights
			$group_dao = new GroupDAO();
			$group_member = $group_dao->get_group_member($group_id, $session_user);

			if(isset($group_member))
			{
				if($group_member->is_active())
				{
					$forum = $this->get_group_forum_by_groupid($group_id);

					if($forum != null)
					{
						// update total posts
						$query = "UPDATE groups SET numforumposts = numforumposts + 1 WHERE id=?";
						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("i", $group_id);
						$stmt->execute();

						// update total posts
						$query = "UPDATE groupforum SET numposts = numposts + 1, lastupdated = NOW() WHERE id=?";
						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("i", $forum_id);
						$stmt->execute();

						$title = htmlentities(strip_tags($title));
						$body = htmlentities(strip_tags($body));
						// create a forum
						$query = "INSERT INTO groupforumpost
									(forumid, datecreated, title, body, authorid, lastupdated)
									VALUES
									(?, NOW(), ?, ?, ?, NOW())";

						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("issi", $forum_id, $title, $body, $session_userid);
						$stmt->execute();

						if ($stmt->affected_rows != 1) {
							$stmt->close();
							$this->closeMasterConnection();
							return;
						}

						$new_forum_post_id = $stmt->insert_id;  // Get the ID of the newly inserted row
						$stmt->close();

						$post = $this->get_group_forum_post($session_user, $group_id, $new_forum_post_id);

						if(!isset($post))
							return null;

						//increment score
						$group_dao = new GroupDAO();
						$group_dao->increment_score($group_id, self::WEIGHT_FORUM_POST);

						return $post;
					}
					else
					{
						throw new Exception(_('Can\'t create forum post without a forum.'));
					}
				}
				else
				{
					throw new Exception(_('You have to be an member of the Group to create forum post.'));
				}
			}
			else
			{
				throw new Exception(_('You have to be a member of the Group'));
			}
		}

		public function create_forum_post_comment($session_user, $session_userid, $group_id, $post_id, $comment)
		{
			// check for admin rights
			$group_dao = new GroupDAO();
			$group_member = $group_dao->get_group_member($group_id, $session_user);

			if(isset($group_member))
			{
				if($group_member->is_active())
				{
					$forum = $this->get_group_forum_by_groupid($group_id);

					if($forum != null)
					{
						// update total posts
						$query = "UPDATE groupforum SET numcomments = numcomments + 1 WHERE id=?";
						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("i", $forum->id);
						$stmt->execute();

						// update total comments
						$query = "UPDATE groupforumpost SET numcomments = numcomments + 1, lastupdated=NOW() WHERE id=?";
						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("i", $post_id);
						$stmt->execute();

						// create a forum
						$comment = htmlentities(strip_tags($comment));
						$query = "INSERT INTO groupforumpostcomment
									(postid, userid, datecreated, comment)
									VALUES
									(?, ?, NOW(), ?)";

						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("iis", $post_id, $session_userid, $comment);
						$stmt->execute();

						if ($stmt->affected_rows != 1) {
							$stmt->close();
							$this->closeMasterConnection();
							return;
						}

						//$new_forum_post_comment_id = $stmt->insert_id;  // Get the ID of the newly inserted row
						$stmt->close();

						//increment score
						$group_dao = new GroupDAO();
						$group_dao->increment_score($group_id, self::WEIGHT_FORUM_POST_COMMENT);

						//$post = $this->get_group_forum_post($session_user, $group_id, $new_forum_post_comment_id);

						/*if(!isset($post))
							return null;

						return $post;*/
					}
					else
					{
						throw new Exception(_('Can\'t create forum post comment without a forum.'));
					}
				}
				else
				{
					throw new Exception(_('You have to be an member of the Group to create forum post comment.'));
				}
			}
			else
			{
				throw new Exception(_('You have to be a member of the Group'));
			}
		}

		public function user_can_view_forum($username, $group_id)
		{
			// if no valid group id is passed in, we just reject request
			if (empty($group_id) || !ctype_digit("$group_id"))
			{
				return false;
			}

			$group_dao = new GroupDAO();
			$group = $group_dao->get_group($group_id);

			// no group exists for that id
			if (is_null($group))
			{
				error_log("GroupForumDAO: invalid group id given to user_can_view_forum(): [$group_id]");
				return false;
			}

			// everyone can see public groups
			if($group->is_public())
			{
			    return true;
			}

			// if no valid username was passed, non-public groups are not viewable
			if (empty($username))
			{
				return false;
			}

			// only if group is private and user is not a member, then user can't view...
			if($group->is_private() && !$group_dao->get_group_member($group_id, $username)->is_active())
			{
				return false;
			}
			else
			{
				return true;
			}
		}

		public function get_group_forum_post($session_username, $group_id, $post_id)
		{
			if (!$this->user_can_view_forum($session_username, $group_id))
				throw new Exception(_('You must be part of the group to view the forum posts.'));

			$query = "SELECT
						gfp.id AS ID,
						gfp.title AS Title,
						gfp.body AS Body,
						gfp.numcomments AS NumComments,
						gfp.numlikes AS NumLikes,
						gfp.numdislikes AS NumDislikes,
						gfp.fileid AS FileID,
						gfp.forumid AS ForumID,
						gfp.lastupdated AS LastUpdated,
						gfp.datecreated AS DateCreated,
						gfp.status AS Status,
						gfp.islocked AS IsLocked,
						gfp.issticked AS IsSticked,
						uid.id AS AuthorUserID,
						uid.username AS AuthorUsername
					  FROM
					  	groupforumpost gfp,
					  	userid uid
					  WHERE
					  	gfp.id = ? AND
					  	gfp.authorid = uid.id AND
					  	gfp.status = 1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $post_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$stmt->fetch();
			$stmt->close();
			$this->closeSlaveConnection();

			if (!isset($data))
				return null;

			return new GroupForumPost($data);
		}

		public function get_total_group_forum_posts($forum_id)
		{
			$forum = $this->get_group_forum($forum_id);
			return $forum->num_posts;
		}

		public function get_active_group_forum_posts($session_username, $group_id, $group_forum_id, $current_page, $number_of_entries, $order='latest', $advance_pages=1)
		{
			if (!$this->user_can_view_forum($session_username, $group_id))
				throw new Exception('You must be part of the group to view the forum posts.');

			$query = "SELECT
						gfp.id AS ID,
						gfp.authorid AS AuthorUserID,
						gfp.title AS Title,
						gfp.body AS Body,
						gfp.numcomments AS NumComments,
						gfp.numlikes AS NumLikes,
						gfp.numdislikes AS NumDislikes,
						gfp.fileid AS FileID,
						gfp.datecreated AS DateCreated,
						gfp.lastupdated AS LastUpdated,
						gfp.status AS Status,
						gfp.islocked AS IsLocked,
						uid.id AS AuthorUserID,
						uid.username AS AuthorUsername
					  FROM
						groupforumpost gfp,
						userid uid
					  WHERE
						gfp.forumid = ? AND
						gfp.authorid = uid.id AND
						gfp.status = 1 AND
						gfp.numcomments >= 1 AND
						gfp.lastupdated >= DATE_SUB(NOW(), INTERVAL 1 WEEK)
					  ORDER BY gfp.lastupdated %s, gfp.numcomments DESC LIMIT ?, ?";

			// Offset
			// @current_page starts from 1
			$offset = ($current_page - 1) * $number_of_entries;

			// Total entries
			$total_entries = $number_of_entries + ((($advance_pages-1 <=0) ? 0 : $advance_pages-1) * $number_of_entries) + 1;

			// Order
			$order = ($order == 'oldest') ? 'ASC' : 'DESC';
			$query = sprintf($query, $order);

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_forum_id, $offset, $total_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$ctr=0;
			$posts = array();
			while( $stmt->fetch() )
			{
				if ($ctr < $number_of_entries)
					$posts[] = new GroupForumPost($row);
				$ctr++;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			// Calculate advance_pages
			$total_pages = ceil($ctr / $number_of_entries);
			$advance_pages = ($total_pages > 0) ? $total_pages-1 : $total_pages;

			return array("forum_posts"=>$posts, "advance_pages"=>$advance_pages, "current_page"=>$current_page, "number_of_entries"=>$number_of_entries);
		}

		public function search_group_forum_posts($session_username, $group_id, $group_forum_id, $search, $current_page, $number_of_entries, $order='latest',$advance_pages=1)
		{
			if (!$this->user_can_view_forum($session_username, $group_id))
				throw new Exception(_('You must be part of the group to view the forum posts.'));

			$query = "SELECT
						gfp.id AS ID,
						gfp.title AS Title,
						gfp.body AS Body,
						gfp.numcomments AS NumComments,
						gfp.numlikes AS NumLikes,
						gfp.numdislikes AS NumDislikes,
						gfp.fileid AS FileID,
						gfp.datecreated AS DateCreated,
						gfp.lastupdated AS LastUpdated,
						gfp.status AS Status,
						gfp.islocked AS IsLocked,
						gfp.issticked AS IsSticked,
						uid.id AS AuthorUserID,
						uid.username AS AuthorUsername
					  FROM
						groupforumpost gfp,
						userid uid
					  WHERE
						gfp.forumid = ? AND
						gfp.authorid = uid.id AND
						gfp.status = 1 AND
						gfp.title LIKE ?
					  ORDER BY %s LIMIT ?, ?";

			// Offset
			// @current_page starts from 1
			$offset = ($current_page - 1) * $number_of_entries;

			// Total entries
			$total_entries = $number_of_entries + ((($advance_pages-1 <=0) ? 0 : $advance_pages-1) * $number_of_entries) + 1;

			// Order
			switch($order)
			{
				case 'oldest':
					$sort_order = 'gfp.lastupdated ASC'; break;

				case 'liked':
					$sort_order = 'gfp.numlikes DESC'; break;

				case 'discussed':
					$sort_order = 'gfp.numcomments DESC'; break;

				default:
					$sort_order = 'gfp.lastupdated DESC'; break;
			}

			// We try to get $max_posts_to_return + 1 posts back, so we will know if older posts exist
			$query = sprintf($query, $sort_order);
			$search = '%'.$search.'%';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("isii", $group_forum_id, $search, $offset, $total_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$ctr=0;
			$posts = array();
			while( $stmt->fetch() )
			{
				if ($ctr < $number_of_entries)
					$posts[] = new GroupForumPost($row);
				$ctr++;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			// Calculate advance_pages
			$total_pages = ceil($ctr / $number_of_entries);
			$advance_pages = ($total_pages > 0) ? $total_pages-1 : $total_pages;

			return array("forum_posts"=>$posts, "current_page"=>$current_page, "advance_pages"=>$advance_pages, "number_of_entries"=>$number_of_entries);
		}

		public function get_group_forum_posts($session_username, $group_id, $group_forum_id, $current_page, $number_of_entries, $order='recent', $is_sticky=true)
		{
			if (!$this->user_can_view_forum($session_username, $group_id))
				throw new Exception(_('You must be part of the group to view the forum posts.'));

			$total_posts = $this->get_total_group_forum_posts($group_forum_id);
			$total_pages = ceil($total_posts / $number_of_entries);

			$query = "SELECT
								gfp.id AS ID,
								gfp.authorid AS AuthorUserID,
								gfp.title AS Title,
								gfp.body AS Body,
								gfp.numcomments AS NumComments,
								gfp.numlikes AS NumLikes,
								gfp.numdislikes AS NumDislikes,
								gfp.fileid AS FileID,
								gfp.datecreated AS DateCreated,
								gfp.lastupdated AS LastUpdated,
								gfp.status AS Status,
								gfp.islocked AS IsLocked,
								gfp.issticked AS IsSticked,
								uid.id AS AuthorUserID,
								uid.username AS AuthorUsername
							  FROM
							  	groupforumpost gfp,
							  	userid uid
							  WHERE
							  	gfp.forumid = ? AND
							  	gfp.authorid = uid.id AND
							  	gfp.status = 1
							  ORDER BY %s %s LIMIT ?, ?";

			// Offset
			// @current_page starts from 1
			$offset = ($current_page - 1) * $number_of_entries;

			// Order
			switch($order)
			{
				case 'oldest':
					$sort_order = 'gfp.lastupdated ASC'; break;

				case 'liked':
					$sort_order = 'gfp.numlikes DESC'; break;

				case 'discussed':
					$sort_order = 'gfp.numcomments DESC'; break;

				default:
					$sort_order = 'gfp.lastupdated DESC'; break;

			}

			// Sticky
			if($is_sticky==true)
			{
				$sticky = "gfp.issticked DESC,";
			}
			else
			{
				$sticky = "";
			}

			// We try to get $max_posts_to_return + 1 posts back, so we will know if older posts exist
			$query = sprintf($query, $sticky, $sort_order);

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_forum_id, $offset, $number_of_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$posts = array();
			while( $stmt->fetch() )
			{
				$posts[] = new GroupForumPost($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array("forum_posts"=>$posts, "current_page"=>$current_page, "total_pages"=>$total_pages);
		}

		/**
		*
		*	Function to get posts from a specified forum for previewing. Forum is public
		*
		**/
		public function get_group_forum_posts_preview($group_id, $group_forum_id, $number_of_entries)
		{
			if (!$this->user_can_view_forum('', $group_id))
				throw new Exception(_('You must be part of the group to view the forum posts.'));

			$query = "SELECT
								gfp.id AS ID,
								gfp.authorid AS AuthorUserID,
								gfp.title AS Title,
								gfp.body AS Body,
								gfp.numcomments AS NumComments,
								gfp.numlikes AS NumLikes,
								gfp.numdislikes AS NumDislikes,
								gfp.fileid AS FileID,
								gfp.datecreated AS DateCreated,
								gfp.lastupdated AS LastUpdated,
								gfp.status AS Status,
								gfp.islocked AS IsLocked,
								gfp.issticked AS IsSticked,
								uid.id AS AuthorUserID,
								uid.username AS AuthorUsername
							  FROM
							  	groupforumpost gfp,
							  	userid uid
							  WHERE
							  	gfp.forumid = ? AND
							  	gfp.authorid = uid.id AND
							  	gfp.status = 1
							  ORDER BY gfp.issticked DESC, gfp.numcomments DESC LIMIT ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $group_forum_id, $number_of_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$posts = array();
			while( $stmt->fetch() )
			{
				$posts[] = new GroupForumPost($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array("forum_posts"=>$posts);
		}

		public function get_group_forum_post_comments($session_user, $group_id, $group_forum_post_id, $current_page, $number_of_entries, $order='', $advance_pages=1)
		{
			if (!$this->user_can_view_forum($session_user, $group_id))
				throw new Exception(_('You must be part of the group to view the forum comments.'));

			$query = "SELECT
							gfpc.id AS ID,
							gfpc.postid AS PostID,
							gfpc.comment AS Comment,
							gfpc.datecreated AS DateCreated,
							gfpc.status AS Status,
							uid.id AS UserID,
							uid.username AS Username
						FROM
							groupforumpostcomment gfpc, userid uid
					   WHERE
							gfpc.postid = ? AND
							gfpc.userid = uid.id AND
							gfpc.status = 1
					   ORDER BY %s LIMIT ?, ?";

			// Offset
			// @current_page starts from 1
			$offset = ($current_page - 1) * $number_of_entries;

			// Total entries
			$total_entries = $number_of_entries + ((($advance_pages-1 <=0) ? 0 : $advance_pages-1) * $number_of_entries) + 1;

			// Order
			switch($order)
			{
				case 'oldest':
					$sort_order = 'gfpc.datecreated ASC'; break;

				default:
					$sort_order = 'gfpc.datecreated DESC'; break;
			}

			// We try to get $max_posts_to_return + 1 posts back, so we will know if older posts exist
			$query = sprintf($query, $sort_order);

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_forum_post_id, $offset, $total_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$ctr=0;
			$comments = array();
			while( $stmt->fetch() )
			{
				if ($ctr < $number_of_entries)
					$comments[] = new GroupForumPostComment($row);
				$ctr++;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			// Calculate advance_pages
			$total_pages = ceil($ctr / $number_of_entries);
			$advance_pages = ($total_pages > 0) ? $total_pages-1 : $total_pages;

			return array("comments"=>$comments, "current_page"=>$current_page, "advance_pages"=>$advance_pages, "number_of_entries"=>$number_of_entries);
		}

		private function is_user_admin($username, $group_id)
		{
			$group_dao = new GroupDAO();
			return $group_dao->get_group_member($group_id, $username)->is_administrator();
		}

		public function lock_unlock_post($session_user, $group_id, $post_id)
		{
			// Check if the user is a legit admin
			if (!$this->is_user_admin($session_user, $group_id))
				throw new Exception(_('Only owners/administrators are allow to lock/unlock post'));

			// Lock/Unlock post
			$query = "UPDATE groupforumpost SET islocked=(CASE islocked WHEN 1 THEN 0 ELSE 1 END) WHERE id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $post_id);
			$stmt->execute();

			$stmt->close();
			$this->closeMasterConnection();
		}

		public function stick_unstick_post($session_user, $group_id, $post_id)
		{
			// Check if the user is a legit admin
			if (!$this->is_user_admin($session_user, $group_id))
				throw new Exception(_('Only owners/administrators are allow to stick/unstick post'));

			// Stick/Unstick post
			$query = "UPDATE groupforumpost SET issticked=(CASE issticked WHEN 1 THEN 0 ELSE 1 END) WHERE id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $post_id);
			$stmt->execute();

			$stmt->close();
			$this->closeMasterConnection();
		}

		public function delete_forum_post($session_user, $group_id, $post_id)
		{
			// Check if the user is a legit admin
			if (!$this->is_user_admin($session_user, $group_id))
				throw new Exception(_('Only owners/administrators are allow to delete post'));

			// Delete post
			$query = "UPDATE groupforumpost SET status=0 WHERE id=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $post_id);
			$stmt->execute();
			$stmt->close();

			// Decrease counter
			$query = "UPDATE groupforum SET numposts=numposts-1 WHERE groupid=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();
			$stmt->close();

			$query = "UPDATE groups SET numforumposts=numforumposts-1 WHERE id=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();
			$stmt->close();
			$this->closeMasterConnection();

			//increment score
			$group_dao = new GroupDAO();
			$group_dao->decrement_score($group_id, (-1)*self::WEIGHT_FORUM_POST);
		}

		public function delete_forum_post_comment($session_user, $group_id, $post_id, $comment_id)
		{
			// Check if the user is a legit admin
			if (!$this->is_user_admin($session_user, $group_id))
				throw new Exception(_('Only owners/administrators are allow to delete post'));

			// Delete post
			$query = "UPDATE groupforumpostcomment SET status=0 WHERE id=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $comment_id);
			$stmt->execute();
			$stmt->close();

			// Decrease counter
			$query = "UPDATE groupforumpost SET numcomments=numcomments-1 WHERE id=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $post_id);
			$stmt->execute();
			$stmt->close();

			$query = "UPDATE groupforum SET numcomments=numcomments-1 WHERE groupid=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();
			$stmt->close();
			$this->closeMasterConnection();

			//increment score
			$group_dao = new GroupDAO();
			$group_dao->decrement_score($group_id, (-1)*self::WEIGHT_FORUM_POST_COMMENT);
		}

		public function get_user_groups_hot_forums($session_username, $number_of_entries)
		{

			$query = 'SELECT gf.*, g.name AS GroupName FROM groups g, groupmember m, groupforum gf  WHERE g.id=gf.groupid AND g.id=m.groupid AND m.username= ? AND m.status=1 AND g.status=1 ORDER BY gf.numcomments DESC LIMIT ?';

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('si', $session_username, $number_of_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$forums = array();
			while( $stmt->fetch() )
			{
				$forums[] = new GroupForum($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array('forums' => $forums);
		}
	}
?>