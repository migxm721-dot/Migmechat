<?php
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("DAO", get_dao_directory() . "/dao.php");

	class ReputationDAO extends DAO
	{
		public function get_score_to_miglevel()
		{
			$memcached = Memcached::get_instance();
			$data = $memcached->get('Reputation/Score2Miglevel/');

			if ($data == false)
			{
				$query = "SELECT rstl.score as score, rstl.level as level
						  FROM reputationscoretolevel rstl
						  ORDER BY rstl.score ASC";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$ctr = 0;
				$data = array();
				while($stmt->fetch())
				{
					$data[$ctr++] = array('score' => $row['score'],
										  'level' => $row['level']);
				}

				$stmt->close();
				$this->closeSlaveConnection();

				// Set the reputation table for 3 hours
				$memcached->add_or_update('Reputation/Score2Miglevel/', $data, 3 * 60 * 60);
			}

			return $data;
		}
	}
?>