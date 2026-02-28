<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");
	fast_require("GroupPoll", get_domain_directory() . "/group/group_poll.php");
	fast_require("GroupPollOption", get_domain_directory() . "/group/group_poll_option.php");

	class GroupPollDAO extends DAO
	{
		const WEIGHT_POLL_NEW = 5;
		const WEIGHT_POLL_VOTE = 1;

		### Poll
		public function get_poll($group_id, $poll_id)
		{
			$query = "SELECT grouppoll.*, userid.username AS username, DATEDIFF(grouppoll.dateexpiry, grouppoll.datecreated) AS daysexpiry FROM grouppoll LEFT JOIN userid ON grouppoll.userid = userid.id WHERE grouppoll.id = ? AND grouppoll.groupid = ?  AND grouppoll.status != 0";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ii", $poll_id, $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$poll = new GroupPoll($row);
			}
			$stmt->close();

			if($poll)
			{
				//$query = "SELECT * FROM grouppolloption WHERE grouppollid = ? ORDER BY numvotes DESC";

				//Removing the order by; requested by the product team
				$query = "SELECT * FROM grouppolloption WHERE grouppollid = ?";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $poll_id);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$poll_options = array();

				while($stmt->fetch())
				{
					$poll_options[] = new GroupPollOption($row);
				}
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return array('poll' => $poll, 'poll_options' => $poll_options);
		}

		public function get_polls($group_id, $offset, $number_of_entries)
		{
			$album_id = 0;
			$query = "SELECT grouppoll.*, userid.username AS username, DATEDIFF(grouppoll.dateexpiry, grouppoll.datecreated) AS daysexpiry FROM grouppoll LEFT JOIN userid ON grouppoll.userid = userid.id WHERE grouppoll.groupid = ? AND grouppoll.status != 0 ORDER BY grouppoll.datecreated DESC LIMIT ?, ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("iii", $group_id, $offset, $number_of_entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$polls = array();

			while($stmt->fetch())
			{
				$polls[] = new GroupPoll($row);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $polls;
		}

		public function create_poll($group_id, $session_userid, $name, $description, $date_expiry, $poll_options)
		{
			$poll_id = 0;
			$name = htmlentities(strip_tags($name));
			$description = htmlentities(strip_tags($description));
			if($date_expiry == 0)
			{
				$query = "INSERT INTO grouppoll (groupid, userid, name, description, datecreated) VALUES (?, ?, ?, ?, NOW())";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iiss", $group_id, $session_userid, $name, $description);
			}
			else
			{
				$query = "INSERT INTO grouppoll (groupid, userid, name, description, datecreated, dateexpiry) VALUES (?, ?, ?, ?, NOW(), ADDDATE(NOW(), ?))";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("iissi", $group_id, $session_userid, $name, $description, $date_expiry);
			}
			$stmt->execute();
			$poll_id = $stmt->insert_id;
			$stmt->close();

			if($poll_id > 0 && $poll_options){
				foreach($poll_options as $poll_option)
				{
					$poll_option = htmlentities(strip_tags($poll_option));
					$query = "INSERT INTO grouppolloption (grouppollid, name) VALUES (?, ?)";
					$stmt = $this->getMasterConnection()->get_prepared_statement($query);
					$stmt->bind_param("is", $poll_id, $poll_option);
					$stmt->execute();
				}
			}
			$stmt->close();
			$this->closeMasterConnection();

			//increment score
			$group_dao = new GroupDAO();
			$group_dao->increment_score($group_id, self::WEIGHT_POLL_NEW);

			return $poll_id;
		}

		public function update_poll($group_id, $poll_id, $name, $description, $date_expiry, $poll_options, $poll_new_options)
		{
			$name = htmlentities(strip_tags($name));
			$description = htmlentities(strip_tags($description));
			if($date_expiry == 0)
			{
				$query = "UPDATE grouppoll SET name = ?, description = ?, dateexpiry = '0000-00-00 00:00:00' WHERE id = ? AND groupid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("ssii", $name, $description, $poll_id, $group_id);
			}
			else
			{
				$query = "UPDATE grouppoll SET name = ?, description = ?, dateexpiry = ADDDATE(datecreated, ?) WHERE id = ? AND groupid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("ssiii", $name, $description, $date_expiry, $poll_id, $group_id);
			}
			$stmt->execute();
			$stmt->close();
			if(!empty($poll_options))
			{
				foreach($poll_options as $poll_option_key => $poll_option_value)
				{
					$poll_option_value = htmlentities(strip_tags($poll_option_value));
					$query = "UPDATE grouppolloption SET name = ? WHERE ID = ? AND grouppollid = ?";
					$stmt = $this->getMasterConnection()->get_prepared_statement($query);
					$stmt->bind_param("sii", $poll_option_value, $poll_option_key, $poll_id);
					$stmt->execute();

				}
			}

			if(!empty($poll_new_options))
			{
				foreach($poll_new_options as $poll_new_option)
				{
					if(!empty($poll_new_option))
					{
						$query = "INSERT INTO grouppolloption (grouppollid, name) VALUES (?, ?)";
						$stmt = $this->getMasterConnection()->get_prepared_statement($query);
						$stmt->bind_param("is", $poll_id, $poll_new_option);
						$stmt->execute();
					}
				}
			}

			$stmt->close();
			$this->closeMasterConnection();
		}

		public function delete_poll($group_id, $poll_id)
		{
			$query = "UPDATE grouppoll SET status = 0 WHERE id = ? AND groupid = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $poll_id, $group_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();

			if ($affected_rows > 0)
			{
				$query = "UPDATE grouppolloption SET status = 0 WHERE grouppollid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $poll_id);
				$stmt->execute();
			}
			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			//increment score
			$group_dao = new GroupDAO();
			$group_dao->decrement_score($group_id, (-1)*self::WEIGHT_POLL_NEW);

			return true;
		}

		public function close_poll($group_id, $poll_id)
		{
			$query = "UPDATE grouppoll SET status = 2 WHERE id = ? AND groupid = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $poll_id, $group_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function vote_poll($poll_option_id, $poll_id, $group_id, $session_userid)
		{
			$query = "INSERT INTO grouppollvote (grouppolloptionid, grouppollid, groupid, userid, datecreated) VALUES (?, ?, ?, ?, NOW())";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $poll_option_id, $poll_id, $group_id, $session_userid);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();
			if($affected_rows == 1)
			{
				$increment_votes = $this->increment_votes_count($poll_id, $poll_option_id);
				if($increment_votes)
				{
					$increment_totalvotes = $this->increment_totalvotes_count($group_id, $poll_id);

					if($increment_totalvotes)
					{
						//increment score
						$group_dao = new GroupDAO();
						$group_dao->increment_score($group_id, self::WEIGHT_POLL_VOTE);

						return true;
					}
				}
			}
			return false;
		}

		public function check_voted($group_id, $poll_id, $user_id)
		{
			$voted = false;
			$query = "SELECT userid FROM grouppollvote WHERE grouppollid = ? AND groupid = ? AND userid = ? LIMIT 1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("iii", $poll_id, $group_id, $user_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$voted = true;
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $voted;
		}

		public function get_poll_count($group_id)
		{
			$num_polls = 0;
			$query = "SELECT COUNT(id) AS numpolls FROM grouppoll WHERE groupid = ? AND status != 0";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("i", $group_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			if($stmt->fetch())
			{
				$num_polls = $row['numpolls'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $num_polls;
		}

		public function increment_totalvotes_count($group_id, $poll_id)
		{
			$query = "UPDATE grouppoll SET totalvotes = totalvotes + 1 WHERE id = ? AND groupid = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $poll_id, $group_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();
			if ($affected_rows != 1)
				return false;

			return true;
		}

		public function increment_votes_count($poll_id, $poll_option_id)
		{
			$query = "UPDATE grouppolloption SET numvotes = numvotes + 1 WHERE id = ? AND grouppollid = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("ii", $poll_option_id, $poll_id);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			$this->closeMasterConnection();
			if ($affected_rows != 1)
				return false;

			return true;
		}
	}
?>