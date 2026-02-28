<?php
	require_once(get_framework_common_directory() . "/database.php");
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("SmsRecord", get_domain_directory() . "/sms/sms_record.php");

	class SmsDAO extends DAO
	{
		public static $MEM_HEADER_SMS_DESTINATION_USERNAME = 'sms_destination_user_';
        public static $MEM_HEADER_SMS_HISTORY = 'sms_history_user_';
		public static $MEM_EXPIRY = 1200; //20 mins

		public function get_user_sms_destination_records($session_username)
		{
			$memcache = Memcached::get_instance();
			$destinations = $memcache->get(SmsDao::$MEM_HEADER_SMS_DESTINATION_USERNAME.$username);

			if(empty($destinations))
			{
				$query = 'SELECT DISTINCT
							msgdest.destination AS Destination
							FROM
							message msg,
							messagedestination msgdest
							WHERE
							msg.username = ? AND
							msg.id = msgdest.messageid AND
							DATE_SUB(CURDATE(),INTERVAL 90 DAY) <= msg.datecreated
							LIMIT 20';
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("s", $session_username);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$destinations = array();

				while( $stmt->fetch() )
				{
					$destinations[] = new SmsRecord($row);
				}

				$stmt->close();
				$this->closeSlaveConnection();

				//Put into cache
				$memcache->add_or_update(SmsDao::$MEM_HEADER_SMS_DESTINATION_USERNAME.$username, $destinations, SmsDao::$MEM_EXPIRY);

				return $destinations;
			}

			return $destinations;
		}

        public function get_sms_history($username, $page = 1, $entries = 25, $month='', $year=''){

            $memcache = Memcached::get_instance();
            $sms_history = array();
			/*$sms_entries_in_page = $memcache->get(SmsDao::$MEM_HEADER_SMS_HISTORY.$username.'_p_'.$page);
            $sms_total_entries = $memcache->get(SmsDao::$MEM_HEADER_SMS_HISTORY.$username.'_smsCount');

			if(empty($sms_entries_in_page))
			{*/
				$query = '  SELECT  SQL_CALC_FOUND_ROWS message.id,
				                    UNIX_TIMESTAMP(message.dateCreated) AS dateCreated,
				                    message.messageText,
				                    messagedestination.Destination,
				                    messagedestination.status

				            FROM message,
				                 messagedestination

				            WHERE message.id = messagedestination.messageid
				            AND message.type = 2
				            AND username = ? ';

                if( $month != '' && $year != '')
				    $query .= "AND DATE_FORMAT(message.datecreated, '%c %Y') = ? ";

                $query .= ' ORDER BY datecreated DESC';
                if( $entries != 0 )
					$query .= ' LIMIT ?,?';

                $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				if($entries == 0){
					if( $month != '' && $year != '')
          	        	$stmt->bind_param("ss", $username, strval($month . ' ' . $year));
           	     	else
                    	$stmt->bind_param("s", $username);
				}
				else{
					if( $month != '' && $year != '')
          	        	$stmt->bind_param("ssii", $username, strval($month . ' ' . $year), intval(($page - 1) * $entries), $entries);
           	     	else
                    	$stmt->bind_param("sii", $username, intval(($page - 1) * $entries), $entries);
				}


                $stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				$sms_entries_in_page = array();

				while( $stmt->fetch() )
				{
					$sms_entries_in_page[] = new SmsRecord($row);

				}

				$query = "SELECT FOUND_ROWS() as recordcount";
                $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
                $stmt->execute();
                $stmt->bind_result($sms_total_entries);
                $stmt->fetch();

                $stmt->close();
                $this->closeSlaveConnection();
/*
				//Put into cache
				$memcache->add_or_update(SmsDao::$MEM_HEADER_SMS_HISTORY.$username.'_p_'.$page, $sms_entries_in_page, SmsDao::$MEM_EXPIRY);
                $memcache->add_or_update(SmsDao::$MEM_HEADER_SMS_HISTORY.$username.'_smsCount', $sms_total_entries, SmsDao::$MEM_EXPIRY);

			}*/

            $sms_history['sms'] = $sms_entries_in_page;
            $sms_history['totalCount'] = $sms_total_entries;
            $sms_history['numpages'] = $entries != 0 ? ceil($sms_total_entries/$entries):1;

            return $sms_history;

        }

        public function get_month_year_sms($username, $format='%c %Y'){

            $query = '  SELECT DISTINCT DATE_FORMAT(message.datecreated, ?) AS mon_year_str

                        FROM message,
                             messagedestination

                        WHERE message.id = messagedestination.messageid
                        AND message.type = 2
                        AND username = ?
                        ORDER BY message.datecreated DESC';

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("ss", $format, $username);
            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

            $mon_year = array();

            while( $stmt->fetch() ){
                $mon_year[] = get_value_from_array('mon_year_str', $row);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $mon_year;

        }

	}
?>