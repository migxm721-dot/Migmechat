<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
    fast_require('Constants', get_framework_common_directory(). '/constants.php');
    fast_require('Voucher', get_domain_directory() . '/voucher/voucher.php');
    fast_require('VoucherBatch', get_domain_directory() . '/voucher/voucher_batch.php');

	class VoucherDAO extends DAO
	{
        public static $MEM_HEADER_VOUCHER_BATCHES = 'voucher_batch_user_';
        public static $MEM_EXPIRY = 1200; //20 mins

		public function is_voucher_owner($username, $voucher_number)
		{
			if (empty($username) || empty($voucher_number)) return false;
			$query = 'SELECT COUNT(*) AS isOwner
					 FROM voucher, voucherbatch
					 WHERE voucherbatch.id = voucher.voucherbatchid
					 AND voucher.number=?
					 AND voucherbatch.username=?';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $voucher_number, $username);
			$stmt->execute();
			$stmt->bind_result($isOwner);
			$stmt->fetch();
			$stmt->close();
			return is_numeric($isOwner) ? (bool)$isOwner : false;
		}

		public function is_voucher_batch_owner($username, $voucher_batch_id)
		{
			if (empty($username)||empty($voucher_batch_id)) return false;
			$query = 'SELECT COUNT(*) AS isOwner
					 FROM voucherbatch
					 WHERE id=?
					 AND username=?';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $voucher_batch_id, $username);
			$stmt->execute();
			$stmt->bind_result($isOwner);
			$stmt->fetch();
			$stmt->close();
			return is_numeric($isOwner) ? (bool)$isOwner : false;
		}

        public function find_voucher($owner, $voucher_id){

            $voucher = null;

            $query = '  SELECT  voucher.id,
                                voucher.voucherbatchid,
                                voucher.number,
                                voucher.status,
                                voucher.lastupdated,
                                voucher.notes

                        FROM voucherbatch,
                             voucher

                        WHERE voucherbatch.username = ?
                        AND voucher.voucherbatchid = voucherbatch.id
                        AND voucher.number = ?';

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("ss", $owner, $voucher_id);
            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

            while( $stmt->fetch() )
            {
                $voucher = new Voucher($row);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $voucher;

        }

        public function get_voucher_batches($owner, $page=1, $entries = 25){

            $memcache = Memcached::get_instance();
            $voucher_batches = array();
			// $voucher_batches_in_page = $memcache->get(VoucherDAO::$MEM_HEADER_VOUCHER_BATCHES.$owner.'_p_'.$page);
            // $voucher_batch_total_entries = $memcache->get(VoucherDAO::$MEM_HEADER_VOUCHER_BATCHES.$owner.'_voucherBatchCount');

			// if(empty($voucher_batches_in_page)){

                $voucher_batches = array();
                $voucher_batches_in_page = array();
                $voucher_batch_total_entries = 0;

                $query = "SELECT SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(vb.datecreated) AS datecreated, vb.id, vb.currency, vb.amount, vb.numvoucher, vb.notes, UNIX_TIMESTAMP(vb.expirydate),
                                 (SELECT COUNT(*) FROM voucher WHERE voucher.voucherbatchid = vb.id AND voucher.status = " . Constants::get_value('VOUCHER_ACTIVE') . ") AS active,
                                 (SELECT COUNT(*) FROM voucher where voucher.voucherbatchid = vb.id and voucher.status = " . Constants::get_value('VOUCHER_CANCELLED') . ") as cancelled,
                                 (SELECT COUNT(*) FROM voucher where voucher.voucherbatchid = vb.id and voucher.status = " . Constants::get_value('VOUCHER_REDEEMED') . ") as redeemed,
                                 (SELECT COUNT(*) FROM voucher where voucher.voucherbatchid = vb.id and voucher.status = " . Constants::get_value('VOUCHER_EXPIRED') . ") as expired,
                                 (SELECT COUNT(*) FROM voucher where voucher.voucherbatchid = vb.id and voucher.status = " . Constants::get_value('VOUCHER_INACTIVE') . ") as inactive
                          FROM voucherbatch vb
                          WHERE vb.username = ?
                          ORDER BY datecreated DESC
                          LIMIT ?,?";

                $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
                $stmt->bind_param("sii", $owner, intval(($page - 1) * $entries), $entries);
                $stmt->execute();
                $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

                $voucher_batches_in_page = array();

                while( $stmt->fetch() )
                {
                    $voucher_batches_in_page[] = new VoucherBatch($row);
                }

                $query = "SELECT FOUND_ROWS() as recordcount";
                $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
                $stmt->execute();
                $stmt->bind_result($voucher_batch_total_entries);
                $stmt->fetch();

                $stmt->close();
                $this->closeSlaveConnection();

                $voucher_batches['voucher_batches'] = $voucher_batches_in_page;
                $voucher_batches['totalCount'] = $voucher_batch_total_entries;
                $voucher_batches['numpages'] = ceil($voucher_batch_total_entries/$entries);

            /*     //Put into cache
				$memcache->add_or_update(VoucherDAO::$MEM_HEADER_VOUCHER_BATCHES.$owner.'_p_'.$page, $voucher_batches_in_page, VoucherDAO::$MEM_EXPIRY);
                $memcache->add_or_update(VoucherDAO::$MEM_HEADER_VOUCHER_BATCHES.$owner.'_voucherBatchCount', $voucher_batch_total_entries, VoucherDAO::$MEM_EXPIRY);

			} */

            return $voucher_batches;

        }

        public function retrieve_vouchers($owner, $batch_id=0, $type=-1){

            $query = "  SELECT voucher.id, voucher.voucherbatchid, voucher.number, UNIX_TIMESTAMP(voucher.lastupdated) AS lastupdated, voucher.status, voucher.notes
			            FROM voucherbatch, voucher
			            WHERE voucherbatch.username = ?
			            AND voucher.voucherbatchid =  voucherbatch.id ";

			if ( $batch_id > 0 )
				$query .= " AND voucher.voucherbatchid = ? ";

			if ( array_key_exists($type, Constants::get_value('VOUCHER_STATUS_CODES')) )
				$query .= " AND voucher.status = ? ";

			$query .= " ORDER BY voucher.lastupdated DESC";

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);

            if( array_key_exists($type, Constants::get_value('VOUCHER_STATUS_CODES')) && $batch_id > 0 )
                $stmt->bind_param("sii", $owner, $batch_id, $type);
            elseif( array_key_exists($type, Constants::get_value('VOUCHER_STATUS_CODES')) )
                $stmt->bind_param("si", $owner, $type);
            elseif( $batch_id > 0 )
                $stmt->bind_param("si", $owner, $batch_id);
            else
                $stmt->bind_param("s", $owner);

            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

            $vouchers = array();

            while( $stmt->fetch() )
            {
                $vouchers[] = new Voucher($row);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $vouchers;

        }

        public function insert_voucher_batch_record($username, VoucherBatch $voucher_batch){

            // save voucher batch
            $query = "  INSERT INTO voucherbatch (username, datecreated, currency, amount, numvoucher, notes)
                    VALUES (?,now(),?,?,?,?)";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ssiis", $username, $voucher_batch->currency, $voucher_batch->amount, $voucher_batch->num_vouchers, $voucher_batch->notes);
            $stmt->execute();





        }

	}
?>