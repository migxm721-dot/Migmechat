<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("GroupPhoto", get_domain_directory() . "/group/group_photo.php");
	fast_require("GroupPhotoComment", get_domain_directory() . "/group/group_photo_comment.php");

	class GroupPhotoDAO extends DAO
	{
		### File
		public function create_file($id, $mimetype, $size, $width, $height, $uploadedby)
		{
			$file_id = 0;
			$query = "INSERT INTO file (id, datecreated, mimetype, size, width, height, uploadedby) VALUES (?, NOW(), ?, ?, ?, ?, ?)";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ssiiis", $id, $mimetype, $size, $width, $height, $uploadedby);
			$stmt->execute();
			$file_id = $stmt->insert_id;
			$stmt->close();
			$this->closeMasterConnection();
			return $file_id;
		}

		### Photo
		public function create_photo($group_id, $session_userid, $file_id, $group_name, $description, $file_size)
		{
			$description = htmlentities(strip_tags($description));
			$photo_id = 0;
			$album_id = $this->get_photo_album($group_id);
			if(!$album_id)
			{
				$album_id = $this->create_photo_album($group_id, $session_userid, $group_name);
			}
			$query = "INSERT INTO groupphoto (groupphotoalbumid, groupid, userid, fileid, datecreated, description) VALUES (?, ?, ?, ?, NOW(), ?)";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiiss", $album_id, $group_id, $session_userid, $file_id, $description);
			$stmt->execute();
			$photo_id = $stmt->insert_id;
			$stmt->close();
			$this->closeMasterConnection();
			if($photo_id)
			{
				$this->increment_photo_count($group_id, $album_id, $file_size);
			}
			return $photo_id;
		}


		public function update_photo($group_id, $photo_id, $description)
		{
			$description = htmlentities(strip_tags($description));
			$query = "UPDATE groupphoto SET description = ? WHERE id = ? AND groupid = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("sii", $description, $photo_id, $group_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function delete_photo($group_id, $photo_id, $file_size)
		{
			$query = "UPDATE groupphoto SET status = 0 WHERE id = ? AND groupid = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $photo_id, $group_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			$album_id = $this->get_photo_album($group_id);
			$this->increment_photo_count($group_id, $album_id, $file_size, true);
			return true;
		}

		public function get_photo($group_id, $photo_id)
		{
			$album_id = 0;
			$query = "SELECT groupphoto.*, file.size AS FileSize, file.width As FileWidth, file.height AS FileHeight, file.uploadedby AS FileUploadedBy, userid.username AS username FROM groupphoto LEFT JOIN file ON groupphoto.fileid = file.id LEFT JOIN userid ON groupphoto.userid = userid.id WHERE groupphoto.id = ? AND groupphoto.groupid = ? AND groupphoto.status = 1 LIMIT 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ii", $photo_id, $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$photo = new GroupPhoto($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $photo;
		}

		public function get_photos($group_id, $offset, $number_of_entries)
		{
			$album_id = 0;
			$query = "SELECT groupphoto.*, file.size AS FileSize, file.width As FileWidth, file.height AS FileHeight, file.uploadedby AS FileUploadedBy FROM groupphoto LEFT JOIN file ON groupphoto.fileid = file.id WHERE groupphoto.groupid = ? AND groupphoto.status = 1 ORDER BY groupphoto.datecreated DESC LIMIT ?, ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_id, $offset, $number_of_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$photos = array();

			while($stmt->fetch())
			{
				$photos[] = new GroupPhoto($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $photos;
		}

		public function get_photo_count($group_id)
		{
			$num_photos = 0;
			$query = "SELECT numphotos FROM groupphotoalbum WHERE groupid = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$num_photos = $row['numphotos'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $num_photos;
		}


		public function get_photo_usage($group_id)
		{
			$photos_usage = 0;
			//$query = "SELECT SUM(size) AS photos_usage FROM groupphoto LEFT JOIN file ON groupphoto.fileid = file.id WHERE groupphoto.groupid = ?";
			$query = "SELECT size AS photos_usage FROM groupphotoalbum WHERE groupid = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$photos_usage = $row['photos_usage'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $photos_usage;
		}

		public function get_file_size($file_id)
		{
			$file_size = 0;
			$query = "SELECT size FROM file WHERE id = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $file_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$file_size = $row['size'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $file_size;
		}

		public function get_file_size_from_photo($photo_id)
		{
			$file_size = 0;
			$query = "SELECT file.size AS size FROM groupphoto LEFT JOIN file ON groupphoto.fileid = file.id  WHERE groupphoto.id = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $photo_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$file_size = $row['size'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $file_size;
		}

		public function get_photos_file_ids($group_id)
		{
			$album_id = 0;
			$query = "SELECT fileid FROM groupphoto WHERE groupid = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$file_ids = array();

			while($stmt->fetch())
			{
				$file_ids[] = $row['fileid'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $file_ids;
		}

		### Photo Album
		public function create_photo_album($group_id, $session_userid, $group_name)
		{
			$query = "INSERT INTO groupphotoalbum (groupid, userid, name, datecreated) VALUES (?, ?, ?, NOW())";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iis", $group_id, $session_userid, $group_name);
			$stmt->execute();
			$album_id = $stmt->insert_id;
			$stmt->close();
			$this->closeMasterConnection();
			return $album_id;
		}

		public function increment_photo_count($group_id, $album_id, $file_size, $decrease = false)
		{
			if($decrease)
			{
				$query = "UPDATE groupphotoalbum SET numphotos = numphotos - 1, size = size - ? WHERE id = ?";
				$query2 = "UPDATE groups SET numphotos = numphotos - 1 WHERE id = ?";
			}
			else
			{
				$query = "UPDATE groupphotoalbum SET numphotos = numphotos + 1, size = size + ? WHERE id = ?";
				$query2 = "UPDATE groups SET numphotos = numphotos + 1 WHERE id = ?";
			}
			// Update GroupPhotoAlbum NumPhotos Field
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $file_size, $album_id);
			$stmt->execute();
			$stmt->close();
			// Update Groups NumPhotos Field
			$stmt = $this->getMasterConnection()->get_prepared_statement($query2);
			$stmt->bind_param("i", $group_id);
			$stmt->execute();
			$stmt->close();
			$this->closeMasterConnection();
		}

		public function get_photo_album($group_id)
		{
			$album_id = 0;
			$query = "SELECT id FROM groupphotoalbum WHERE groupid = ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			if($stmt->fetch())
			{
				$album_id = $data['id'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $album_id;
		}

		### Like/Dislike Photo
		public function like_photo($session_username, $photo_id, $like)
		{
			// Ensure that photo id is not 0
			if(intval($photo_id) <= 0)
				return;

			// Ensure that like is either -1 (dislike) or 1 (like)
			if ($like != 1 && $like != -1)
				return;

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO groupphotolike (groupphotoid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $photo_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();

			// Update the groupphoto row if a (dis)like was added (i.e. the user didn't just (dis)like after they have already done so)
			$num_likes_updated = false;
			if ($affected_rows > 0) {
				// Get the new number of likes and dislikes
				$query = "SELECT CAST(SUM(type = 1) AS UNSIGNED INTEGER) AS numlikes, CAST(ABS(SUM(type = -1)) AS UNSIGNED INTEGER) AS numdislikes FROM groupphotolike WHERE groupphotoid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $photo_id);
				$stmt->execute();
				$stmt->bind_result($numlikes, $numdislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($numlikes) ? $numlikes : 0;
				$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

				$query = "UPDATE groupphoto SET numlikes = ?, numdislikes = ? WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iii", $new_num_likes, $new_num_dislikes, $photo_id);
				$stmt->execute();
				$stmt->close();

				$num_likes_updated = true;
			}

			// Now get the number of likes and dislikes if we didn't just calculate it
			if (!$num_likes_updated)
			{
				$query = "SELECT numlikes, numdislikes FROM groupphoto WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $photo_id);
				$stmt->execute();

				$stmt->bind_result($new_num_likes, $new_num_dislikes);
				$stmt->fetch();
				$stmt->close();
			}

			$this->closeMasterConnection();

			// Return the new number of likes and dislikes
			return array(
				'numlikes' => $new_num_likes,
				'numdislikes' => $new_num_dislikes,
			);
		}

		public function dislike_photo($session_username, $photo_id)
		{
			return $this->like_photo($session_username, $photo_id, -1);
		}

		public function get_photo_comments($photo_id, $offset, $number_of_entries)
		{

			$query = 'SELECT
							SQL_CALC_FOUND_ROWS
							groupphotocomment.*,
							user.username authorusername,
							user.displaypicture authordisplaypicture
						FROM
							groupphotocomment, user, userid userid_author
						WHERE
							groupphotocomment.userid = userid_author.id
							AND userid_author.username = user.username
							AND groupphotocomment.groupphotoid = %d
							AND groupphotocomment.status = 1
						ORDER BY
							groupphotocomment.id DESC
						LIMIT %s, %s;
						SELECT FOUND_ROWS();';

	 		$query = sprintf($query,
        					intval($photo_id),
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
				$post = new GroupPhotoComment($post);
			}
			$this->closeSlaveConnection();


			return array('posts' => $posts, 'total_count' => $total_count);
	 	}

		public function get_photo_comment($photo_comment_id)
		{
			$query = "SELECT
								groupphotocomment.*,
								user.username authorusername,
								user.displaypicture authordisplaypicture,
							FROM
								groupphotocomment, user, userid userid_author
							WHERE
								groupphotocomment.userid = userid_author.id
								AND userid_author.username = user.username
								AND groupphotocomment.id = ?
								AND groupphotocomment.status = 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $photo_comment_id);
			$stmt->execute();
			$stmt->bind_result($data);
			$stmt->fetch();
			$this->closeSlaveConnection();

			if (!isset($data))
				return null;

			$post = new GroupPhotoComment($data);

			return $post;
		}

		public function create_photo_comment($session_username, $photo_id, $comment)
		{
			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$comment = htmlentities(strip_tags($comment));

			$query = "INSERT INTO groupphotocomment (groupphotoid, userid, datecreated, comment) VALUES(?, ?, NOW(), ?)";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iis", $photo_id, $session_userid, $comment);
			$stmt->execute();

			if ($stmt->affected_rows != 1) {
				$stmt->close();
				$this->closeMasterConnection();
				return;
			}

			// Increment Photo Comment Count
			$this->increment_photo_comment_count($photo_id);

			// Get newly inserted ID
			$new_photo_comment_id = $stmt->insert_id;
			$stmt->close();

			// Query the database again to get the latest comment
			$query = "SELECT
							groupphotocomment.*,
							user.username authorusername,
							user.displaypicture authordisplaypicture
						FROM
							groupphotocomment, user, userid
						WHERE
							groupphotocomment.id = ?
							AND groupphotocomment.userid = userid.id
							AND userid.username = user.username
						LIMIT 1";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $new_photo_comment_id);
			$stmt->execute();
			$this->getMasterConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->fetch();
			$stmt->close();
			$this->closeMasterConnection();

			return new GroupPhotoComment($row);
		}

		public function remove_photo_comment($session_username, $group_id, $photo_id, $photo_comment_id)
		{
			if(!$this->user_is_admin_of_group($session_username, $group_id))
				return false;

			$query = "UPDATE groupphotocomment SET status = 0 WHERE id = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $photo_comment_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();
			if ($affected_rows != 1)
				return false;
			$this->increment_photo_comment_count($photo_id, true);
			return true;
		}

		public function increment_photo_comment_count($photo_id, $decrease = false)
		{
			if($decrease)
				$query = "UPDATE groupphoto SET numcomments = numcomments - 1 WHERE id = ?";
			else
				$query = "UPDATE groupphoto SET numcomments = numcomments + 1 WHERE id = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $photo_id);
			$stmt->execute();
			$stmt->close();
			$this->closeMasterConnection();
		}

		public function like_photo_comment($session_username, $photo_comment_id, $like)
		{
			// Ensure that like is either -1 (dislike) or 1 (like)
			if ($like != 1 && $like != -1)
				return;

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO groupphotocommentlike (groupphotocommentid, userid, datecreated, type) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE type = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $photo_comment_id, $session_userid, $like, $like);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$num_likes_updated = false;
			if ($affected_rows > 0)
			{
				$query = "SELECT CAST(SUM(type = 1) AS UNSIGNED INTEGER) AS numlikes, CAST(ABS(SUM(type = -1)) AS UNSIGNED INTEGER) AS numdislikes FROM groupphotocommentlike WHERE groupphotocommentid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $photo_comment_id);
				$stmt->execute();
				$stmt->bind_result($numlikes, $numdislikes);
				$stmt->fetch();
				$stmt->close();

				$new_num_likes = is_numeric($numlikes) ? $numlikes : 0;
				$new_num_dislikes = is_numeric($numdislikes) ? $numdislikes : 0;

				$query = "UPDATE groupphotocomment SET numlikes = ?, numdislikes = ? WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);

				$stmt->bind_param("iii", $new_num_likes, $new_num_dislikes, $photo_comment_id);
				$stmt->execute();
				$stmt->close();
				$num_likes_updated = true;
			}

			// Now get the number of likes and dislikes if we didn't just calculate it
			if (!$num_likes_updated)
			{
				$query = "SELECT numlikes, numdislikes FROM groupphotocomment WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $photo_comment_id);
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

		public function dislike_photo_comment($session_username, $photo_comment_id)
		{
			return $this->like_photo_comment($session_username, $photo_comment_id, -1);
		}
	}
?>