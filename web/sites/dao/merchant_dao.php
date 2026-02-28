<?php
	require_once(get_framework_common_directory() . "/utilities.php");
	require_once(get_framework_common_directory() . "/database.php");
	fast_require('Constants', get_framework_common_directory() . '/constants.php');
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");

	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	fast_require('UserDAO', get_dao_directory() . '/user_dao.php');

	fast_require("AccountTransaction", get_domain_directory() . '/account/account_transaction.php');
	fast_require("AuthenticationToken", get_domain_directory() . "/merchant/authentication_token.php");
	fast_require("Customer", get_domain_directory() . '/merchant/customer.php');
	fast_require("Merchant", get_domain_directory() . '/merchant/merchant.php');
	fast_require('MerchantTag', get_domain_directory() . '/merchant/merchant_tag.php');
	fast_require('MerchantLanguage', get_domain_directory() . '/merchant/merchant_lang.php');
	
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");
	fast_require('FusionRest', get_library_directory() . '/fusion/fusion_rest.php');

	class MerchantDAO extends DAO
	{
		public function insert_merchant_mentor($username, $mentor){

			if( !strlen(trim($username) ) )
				return false;

			$query = "INSERT INTO merchantdetails(id, mentor, logincount) SELECT id, ? AS mentor, 0 AS logincount FROM userid WHERE username = ? ON DUPLICATE KEY UPDATE mentor = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("sss", $mentor, $username, $mentor);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows == 1;

		}

		public function insert_merchant_referrer($username, $referer){

			if( !strlen(trim($username) ) )
				return false;

			$query = "INSERT INTO merchantdetails(id, referrer, logincount) SELECT id, ? AS referrer, 0 AS logincount FROM userid WHERE username = ? ON DUPLICATE KEY UPDATE referrer = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("sss", $referer, $username, $referer);
			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows == 1;

		}

		/*
		 * query derived from:
		 * accountBean.java: function getAccountEntriesByDate
		 * webBean.java: function getUserReferral
		 */
		public function get_account_transactions($username, $entries = 10, $page = 1, $type = 0, $month='', $year = '', $trail_type = ''){

			if( !strlen(trim($username)) )
				return 0;

			$hasDate = false;

			if( $month != '' && $year != '' ){
				$startDate = date('Y-m-d', mktime(0,0,0,$month,1,$year));
				$endDate = date('Y-m-d', mktime(0,0,0,$month+1,1,$year));
				$hasDate = true;
			}

			switch($type){
				case Constants::get_value('TRANSFERS'):
					$query = $this->get_transfer_transactions_query($username, $entries, $page, $hasDate);
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					if($entries == 0){
						if( $hasDate )
							$stmt->bind_param('sss',$username, $startDate, $endDate);
						else
							$stmt->bind_param('s',$username);
						$stmt->execute();
					}
					else{
						if( $hasDate )
							$stmt->bind_param('sssii',$username, $startDate, $endDate, intval(($page - 1) * $entries), $entries);
						else
							$stmt->bind_param('sii',$username, intval(($page - 1) * $entries), $entries);
						$stmt->execute();
					}
					break;

				case Constants::get_value('VOUCHERS'):
					$query = $this->get_voucher_transactions_query($username, $entries, $page, $hasDate);
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					if($entries == 0){
						if( $hasDate )
							$stmt->bind_param('sss',$username, $startDate, $endDate);
						else
							$stmt->bind_param('s',$username);
						$stmt->execute();
					}
					else{
						if( $hasDate )
							$stmt->bind_param('sssii',$username, $startDate, $endDate, intval(($page - 1) * $entries), $entries);
						else
							$stmt->bind_param('sii',$username, intval(($page - 1) * $entries), $entries);
						$stmt->execute();
					}
					break;

				case Constants::get_value('CREDITS'):
					$query = $this->get_credit_transactions_query($username, $entries, $page, $hasDate);
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					if($entries == 0){
						if( $hasDate )
							$stmt->bind_param('sss',$username, $startDate, $endDate);
						else
							$stmt->bind_param('s',$username);
						$stmt->execute();
					}
					else{
						if( $hasDate )
							$stmt->bind_param('sssii',$username, $startDate, $endDate, intval(($page - 1) * $entries), $entries);
						else
							$stmt->bind_param('sii',$username, intval(($page - 1) * $entries), $entries);
						$stmt->execute();
					}
					break;

				case Constants::get_value('INVITES'):
					$query = $this->get_invite_transactions_query($username, $entries, $page, $hasDate);
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					if($entries == 0){
						if( $hasDate )
							$stmt->bind_param('sss', $username, $startDate, $endDate);
						else
							$stmt->bind_param('s', $username);
						$stmt->execute();
					}
					else{
						if( $hasDate )
							$stmt->bind_param('sssii', $username, $startDate, $endDate, intval(($page - 1) * $entries), $entries);
						else
							$stmt->bind_param('sii', $username, intval(($page - 1) * $entries), $entries);
						$stmt->execute();
					}
					break;

				case Constants::get_value('TRAILS'):
					$query = $this->get_trails_query($username, $entries, $page, $hasDate, $trail_type);
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					if($entries == 0){
						if( $hasDate )
							$stmt->bind_param('sss', $username, $startDate, $endDate);
						else
							$stmt->bind_param('s', $username);
						$stmt->execute();
					}
					else{
						if( $hasDate )
							$stmt->bind_param('sssii', $username, $startDate, $endDate, intval(($page - 1) * $entries), $entries);
						else
							$stmt->bind_param('sii', $username, intval(($page - 1) * $entries), $entries);
						$stmt->execute();
					}
					break;

				default:
					$query = $this->get_all_account_transactions_query($username, $entries, $page, $hasDate);
					$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
					if($entries == 0){
						if( $hasDate )
							$stmt->bind_param('sssisss',$username, $startDate, $endDate, Constants::get_value('ACCOUNT_ENTRY_INVITE'), $username, $startDate, $endDate);
						else
							$stmt->bind_param('sis',$username, Constants::get_value('ACCOUNT_ENTRY_INVITE'), $username);
					}
					else{
						if( $hasDate )
							$stmt->bind_param('sssisssii',$username, $startDate, $endDate, Constants::get_value('ACCOUNT_ENTRY_INVITE'), $username, $startDate, $endDate, intval(($page - 1) * $entries), $entries);
						else
							$stmt->bind_param('sisii',$username, Constants::get_value('ACCOUNT_ENTRY_INVITE'), $username, intval(($page - 1) * $entries), $entries);
					}
					$stmt->execute();
			}

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() ){
				$data['transactions'][] = new AccountTransaction($row);
			}

			$query = "SELECT FOUND_ROWS() as recordcount";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$stmt->bind_result($transactions_total_entries);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			$data['numpages'] = $entries != 0 ? ceil($transactions_total_entries/$entries) : 1;
			$data['page'] = $page;
			return $data;
		}

		protected function get_all_account_transactions_query($username, $entries, $page, $hasDate){

			$query = "	SELECT 	SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(dateCreated) AS dateCreated,
								description,
								amount,
								currency,
								type AS transactionType,
								CASE
									WHEN type = " . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRANSFERS')) . " THEN 'Transfer'
									WHEN type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_VOUCHERS')) . ") THEN 'Vouchers'
									WHEN type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITS')) . ") THEN 'Credits'
									WHEN type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRAILS')) . ") THEN 'Trails'
								END AS `type`,
								exchangeRate,
								if(type = 14, substring_index(description, ' ', -1), null) as destusername,
								accountentry.id
						FROM accountentry
						WHERE username = ?
						AND amount != 0
						AND type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_ALL')) . ") ";

			if( $hasDate){
				$query .= "	AND datecreated >= ?
							AND datecreated < ? ";
			}

			$query .="	UNION ALL

						SELECT 	UNIX_TIMESTAMP(userreferral.dateCreated) AS dateCreated,
								CASE
									WHEN user.username IS NULL THEN CONCAT('Sent to +', userreferral.mobilephone)
									WHEN user.username IS NOT NULL THEN CONCAT('Sent to ', user.username)
								END AS description,
								userreferral.amount,
								'AUD' AS currency,
								? AS transactionType,
								'Invite' AS `type`,
								'' AS exchangeRate,
								'' AS destusername,
								userreferral.id
						FROM userreferral
						LEFT JOIN user ON user.mobilephone = userreferral.mobilephone
						WHERE userreferral.username = ? ";

			if( $hasDate){
				$query .= "	AND userreferral.datecreated >= ?
							AND userreferral.datecreated < ? ";
			}

			$query .= "ORDER BY dateCreated DESC ";
			if($entries != 0)
				$query .= 'LIMIT ?,?';

			return $query;

		}

		protected function get_transfer_transactions_query($username, $entries, $page, $hasDate){

			$query = "	SELECT 	SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(dateCreated) AS dateCreated,
								description,
								amount,
								currency,
								type AS transactionType,
								'Transfer' AS `type`,
								exchangeRate,
								if(type = 14, substring_index(description, ' ', -1), null) as destusername,
								accountentry.id
						FROM accountentry
						WHERE username = ?
						AND amount != 0
						AND type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRANSFERS')) . ") ";

			if( $hasDate ){
				$query .= "	AND datecreated >= ?
							AND datecreated < ? ";
			}

			$query .= "	ORDER BY accountentry.id DESC ";
			if($entries != 0)
				$query .= 'LIMIT ?,?';

			return $query;

		}

		protected function get_credit_transactions_query($username, $entries, $page, $hasDate){

			$query = "	SELECT 	SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(dateCreated) AS dateCreated,
								description,
								amount,
								currency,
								type AS transactionType,
								'Credits' AS `type`,
								exchangeRate,
								'' as destusername,
								id
						FROM accountentry
						WHERE username = ?
						AND amount != 0
						AND type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITS')) . ")";

			if( $hasDate ){
				$query .= "	AND datecreated >= ?
							AND datecreated < ? ";
			}

			$query .= "	ORDER BY id DESC ";
			if($entries != 0)
				$query .= 'LIMIT ?,?';

			return $query;

		}

		protected function get_voucher_transactions_query($username, $entries, $page, $hasDate){

			$query = "	SELECT 	SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(dateCreated) AS dateCreated,
								description,
								amount,
								currency,
								type AS transactionType,
								'Vouchers' AS `type`,
								exchangeRate,
								'' as destusername,
								id
						FROM accountentry
						WHERE username = ?
						AND amount != 0
						AND type IN (" . implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_VOUCHERS')) . ")";

			if( $hasDate ){
				$query .= "	AND datecreated >= ?
							AND datecreated < ? ";
			}

			$query .= "	ORDER BY id DESC ";
			if($entries != 0)
				$query .= 'LIMIT ?,?';

			return $query;

		}

		protected function get_invite_transactions_query($username, $entries, $page, $hasDate){

			$query = "	SELECT SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(userreferral.dateCreated) AS dateCreated,
										CASE
											WHEN user.username IS NULL THEN CONCAT('Sent to +', userreferral.mobilephone)
											WHEN user.username IS NOT NULL THEN CONCAT('Sent to ', user.username)
										END AS description,
										userreferral.amount,
										'AUD' AS currency,
										'Invite' AS `type`,
										" . Constants::get_value('ACCOUNT_ENTRY_INVITE') . " AS transactionType,
										'' AS exchangeRate ,
								userreferral.id
								FROM userreferral
								LEFT JOIN user ON user.mobilephone = userreferral.mobilephone
								WHERE userreferral.username = ? ";

			if( $hasDate ){
				$query .= "	AND userreferral.datecreated >= ?
							AND userreferral.datecreated < ? ";
			}

			$query .= "	ORDER BY userreferral.id DESC ";
			if($entries != 0)
				$query .= 'LIMIT ?,?';

			return $query;

		}

		protected function get_trails_query($username, $entries, $page, $hasDate, $trail_type){

			$query = "	SELECT 	SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(dateCreated) AS dateCreated,
								description,
								amount,
								currency,
								type AS transactionType,
								'Trails' AS `type`,
								exchangeRate,
								'' as destusername,
								id
						FROM accountentry
						WHERE username = ?
						AND amount != 0
						AND type IN (" ;
			$query .= 	!empty($trail_type) ? strval($trail_type) : implode(',', Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRAILS')) ;
			$query .=  ")";
			if( $hasDate ){
				$query .= "	AND datecreated >= ?
							AND datecreated < ? ";
			}

			$query .= "	ORDER BY id DESC ";
			if($entries != 0)
				$query .= 'LIMIT ?,?';

			return $query;

		}

		public function get_account_transactions_summary($username, $month='', $year=''){


			$transactionSummary = array();

			if( $month != '' && $year != '' ){
				$startDate = date('Y-m-d', mktime(0,0,0,$month,1,$year));
				$endDate = date('Y-m-d', mktime(0,0,0,$month+1,1,$year));
			}

			// sales
			$query = "	SELECT 	COUNT(*) AS numberOfSales,
								-sum(	CASE WHEN user.currency = accountentry.currency THEN amount
										ELSE amount/exchangerate END) AS totalSales
						FROM accountentry, user
						WHERE accountentry.username = ?
						AND user.username = accountentry.username
						AND accountentry.type IN (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_SALES')) . ")
						AND amount < 0 ";

			if( $month != '' && $year != ''){
				$query .= "	AND accountentry.datecreated >= ?
							AND accountentry.datecreated < ? ";
			}

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			if( $month != '' && $year != '' )
				$stmt->bind_param("sss", $username, $startDate, $endDate);
			else
				$stmt->bind_param("s", $username);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$stmt->fetch();
			$transactionSummary['numberOfSales'] = get_value_from_array("numberOfSales", $data, "integer", 0);
			$transactionSummary['totalSales'] = get_value_from_array("totalSales", $data, "double", 0);
			$stmt->close();

			// credits
			$query = "	SELECT COUNT(*) AS numberOfCredits, sum(amount) AS totalCredits
						FROM accountentry
						WHERE username = ?
						AND type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITS')) . ")
						AND amount > 0 ";

			if( $month != '' && $year != ''){
				$query .= "	AND accountentry.datecreated >= ?
							AND accountentry.datecreated < ? ";
			}
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			if( $month != '' && $year != '' )
				$stmt->bind_param("sss", $username, $startDate, $endDate);
			else
				$stmt->bind_param("s", $username);

			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			$stmt->fetch();

			$transactionSummary['numberOfCredits'] = get_value_from_array("numberOfCredits", $data, "integer", 0);
			$transactionSummary['totalCredits'] = get_value_from_array("totalCredits", $data, "double", 0);
			$stmt->close();

			// transactions
			$query = "	SELECT (accountEntryCount + countInvites) transactionsCount
						FROM (	SELECT 	COUNT(*) AS accountEntryCount
								FROM accountentry
								WHERE username= ?
								AND amount != 0
								AND type IN (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_ALL')) . ")
								GROUP BY username) qAccountEntries,
							 (  SELECT COUNT(*) AS countInvites
								FROM userreferral
								WHERE username = ? ) qInvites";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("ss", $username, $username);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$stmt->fetch();
			$transactionSummary['numberOfTransactions'] = get_value_from_array("transactionsCount", $data, "integer", 0);

			$stmt->close();
			$this->closeSlaveConnection();

			return $transactionSummary;

		}

		public function get_month_year_transactions($username, $type=0, $format='%c %Y'){

			$query = "	SELECT DISTINCT DATE_FORMAT(mon_year, ?) AS mon_year_str FROM (";

			if( !$type || $type != Constants::get_value('INVITES') ){

				$query .= " SELECT datecreated AS mon_year
							FROM accountentry
							WHERE username=?
							AND amount != 0 ";

				switch($type){
					case Constants::get_value('TRANSFERS'):
						$query .= "AND type IN (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRANSFERS')) . ")";
						break;
					case Constants::get_value('VOUCHERS'):
						$query .= "AND type IN (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_VOUCHERS')) . ")";
						break;
					case Constants::get_value('CREDITS'):
						$query .= "AND type IN (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITS')) . ")";
						break;
					default:
						$query .= "AND type IN (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_ALL')) . ")";
						break;
				}

			}

			if( !$type )
				$query .= " UNION";

			if( !$type || $type == Constants::get_value('INVITES') )
				$query .= " SELECT userreferral.datecreated AS mon_year
							FROM userreferral
							WHERE userreferral.username = ? ";

			$query .= ") qTrans ORDER BY mon_year DESC";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			if( !$type )
				$stmt->bind_param("sss", $format, $username, $username);
			else
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

		/**
		 * @name search_customer
		 * @desc search for customer of merchant, query derived from webBean.java: method getMerchantCustomers()
		 * @param string $username username of merchant
		 * @param string $searchKey
		 */
		public function search_customer($username, $searchKey, $exactMatch=false, $entries=20, $page=1, $orderBy='user.username DESC'){

			$customers = array();

			if (SystemProperty::get_instance()->get_boolean(SystemProperty::Merchant_customer_search_disabled, true))
			{
				return $customers;
			}

			$query = "	SELECT SQL_CALC_FOUND_ROWS user.username, UNIX_TIMESTAMP(max(datecreated)) as datecreated, UNIX_TIMESTAMP(dateregistered) AS dateregistered, displaypicture, userid.id
						FROM user,
						(	SELECT username
							FROM user
							WHERE merchantcreated = ?

							UNION

							SELECT DISTINCT LCASE(a1.username) username
							FROM accountentry a1, accountentry a2
							WHERE a1.type = " . Constants::get_value('USER_TO_USER_TRANSFER') . "
							AND a1.reference = CAST(a2.id AS CHAR)
							AND a2.type = " . Constants::get_value('USER_TO_USER_TRANSFER') . "
							AND a2.username = ?
							AND a2.amount < 0

							UNION

							SELECT DISTINCT LCASE(accountentry.username) username
							FROM accountentry
							JOIN voucher ON voucher.voucherbatchid = accountentry.reference
							AND voucher.status = 3
							WHERE username = ?
							AND type = " . Constants::get_value('VOUCHERS_CREATED') . "
						) customers,
						userid,
						accountentry
						WHERE user.username = customers.username
						AND user.username = userid.username
						and accountentry.username = user.username
						and accountentry.type = " . Constants::get_value('USER_TO_USER_TRANSFER') . "
						and amount > 0 ";

			if( $searchKey ) {
				if( $exactMatch )
					$query .= " AND LOWER(user.username) = LOWER(?) ";
				else
					$query .= " AND user.username REGEXP ? ";
			}

			$query .= " AND user.username <> ? ";
			$query .= " group by user.username ";
			$query .= " ORDER BY $orderBy ";
			$query .= " LIMIT ?,?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			if( $searchKey )
				$stmt->bind_param("sssssii", $username, $username, $username, $searchKey, $username, intval(($entries * $page) - $entries), $entries);
			else
				$stmt->bind_param("ssssii", $username, $username, $username, $username, intval(($entries * $page) - $entries), $entries);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() ){
				$customers[] = new Customer($row);
			}

			$query = "SELECT FOUND_ROWS() as recordcount";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$stmt->bind_result($customer_total_entries);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			$data['customers'] = $customers;
			$data['numpages'] = (int)ceil($customer_total_entries/$entries);
			$data['page'] = $page;

			return $data;

		}

		public function get_customer_transaction_summary($username, $customer){

			$query = "  SELECT -SUM(ROUND(CASE WHEN qTransactions.currency = user.currency THEN amount
										  ELSE (amount/exchangerate) END, 2)) AS totalSales,
							   COUNT(*) AS totalTransactions
						FROM
							(
								SELECT a1.* FROM accountentry a1, accountentry a2
								WHERE a1.type = ?
								AND a1.reference = CAST(a2.id AS CHAR)
								AND a2.username = ?
								AND a2.amount > 0
								AND a1.username = ?
								AND a2.type = ?
							) qTransactions,
							user
						WHERE user.username = qTransactions.username
						AND user.username = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("issis", Constants::get_value('USER_TO_USER_TRANSFER'), $customer, $username, Constants::get_value('USER_TO_USER_TRANSFER'), $username);
			$stmt->execute();
			$stmt->bind_result($totalSales, $totalTransactions);
			$stmt->fetch();

			return array(
				'totalsales' => $totalSales,
				'totaltransactions' => $totalTransactions
			);

		}

		public function get_customer_transactions($username, $customer, $page=1, $entries=25){

			$query = "  SELECT  SQL_CALC_FOUND_ROWS UNIX_TIMESTAMP(qTransactions.datecreated) AS dateCreated,
								qTransactions.type,
								description,
								user.currency,
								exchangerate AS exchangeRate,
								CASE WHEN qTransactions.currency = user.currency THEN amount
								ELSE (amount/exchangerate) END AS amount,
								fundedamount,
								tax,
								costofgoodssold,
								costoftrial,
								destusername

						FROM(
								SELECT  a1.datecreated,
										a1.type AS transactionType,
										'Transfer' AS type,
										a1.description,
										a1.currency,
										a1.exchangerate AS exchangeRate,
										a1.amount,
										a1.fundedamount,
										a1.tax,
										a1.costofgoodssold,
										a1.costoftrial,
										a1.username,
										a2.username AS destusername
								FROM accountentry a1, accountentry a2
								WHERE a1.type = ?
								AND a1.reference = CAST(a2.id AS CHAR)
								AND a2.username = ?
								AND a2.amount != 0
								AND a1.username = ?
								AND a2.type = ?

						) qTransactions,
						user
						WHERE qTransactions.username = user.username
						ORDER BY datecreated DESC
						LIMIT ?,?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("issiii", Constants::get_value('USER_TO_USER_TRANSFER'), $customer, $username, Constants::get_value('USER_TO_USER_TRANSFER'), intval(($page * $entries) - $entries), $entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$account_transactions = array();

			while( $stmt->fetch() ){
				$account_transactions[] = new AccountTransaction($row);
			}

			$query = "SELECT FOUND_ROWS() as recordcount";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$stmt->bind_result($transactions_total_entries);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			$data = array();
			$data['transactions'] = $account_transactions;
			$data['numpages'] = ceil($transactions_total_entries/$entries);
			$data['page'] = $page;

			return $data;

		}

		public function get_sub_merchants($username, $searchKey='', $page=1, $entries=25){

			$query = "	SELECT 	SQL_CALC_FOUND_ROWS user.username,
								UNIX_TIMESTAMP(user.dateregistered) AS dateregistered,
								user.displaypicture,
								userid.id,
								user.mobilephone,
								merchantdetails.mentor
						FROM user, merchantdetails, userid
						WHERE user.username = userid.username
						AND merchantdetails.id = userid.id
						AND merchantdetails.mentor = ? ";

			if( trim($searchKey) )
				$query .= " AND user.username REGEXP ? ";

			$query .= " ORDER BY username ASC
						LIMIT ?,?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			if( trim($searchKey) )
				$stmt->bind_param("ssii", $username, $searchKey, intval(($page * $entries) - $entries), $entries);
			else
				$stmt->bind_param("sii", $username, intval(($page * $entries) - $entries), $entries);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$sub_merchants = array();

			while( $stmt->fetch() ){
				$sub_merchants[] = new Merchant($row);
			}

			$query = "SELECT FOUND_ROWS() as recordcount";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();
			$stmt->bind_result($submerchant_total_count);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			foreach( $sub_merchants as $merchant ){
				// get merchant latest transaction
				$merchant->last_transfer = $this->get_merchant_latest_transfer($merchant->username);
			}

			$data = array();
			$data['submerchants'] = $sub_merchants;
			$data['numpages'] = ceil($submerchant_total_count/$entries);
			$data['page'] = $page;

			return $data;

		}

		public function get_merchant($username){

			#get merchant details
			$query = "	SELECT  user.username,
							  UNIX_TIMESTAMP(user.dateregistered) AS dateregistered,
							  user.displaypicture,
							  userid.id,
							  mentor,
							  user.mobilephone,
							  username_color_type
						FROM user ,merchantdetails, userid
						WHERE user.username = userid.username
						AND merchantdetails.id = userid.id
						AND userid.username=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() ){
				$details = $row;
			}

			$stmt->close();

			if ($details) {
				# get last transfer
				$query = "SELECT UNIX_TIMESTAMP(datecreated) FROM accountentry WHERE username = ? AND type=? AND amount < 0 ORDER BY id DESC LIMIT 1";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("si", $username, Constants::get_value('USER_TO_USER_TRANSFER'));
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				while( $stmt->fetch() ){
					$details = array_merge($details, $row);
				}

				$stmt->close();
			}

			$this->closeSlaveConnection();

			if( $details )
				return new Merchant($details);
			else
				return null;

		}

		public function get_mentor($username){

			#get mentor details
			$query = "  SELECT  user.username,
								UNIX_TIMESTAMP(user.dateregistered) AS dateregistered,
								user.displaypicture,
								userid.id,
								mobilephone
						FROM user, merchantdetails, userid,
							 userid AS sub_merchant_userid, merchantdetails AS sub_merchant_details
						WHERE user.username = userid.username
						AND merchantdetails.id = userid.id
						AND userid.username = sub_merchant_details.mentor
						AND sub_merchant_details.id = sub_merchant_userid.id
						AND sub_merchant_userid.username = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $username);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			while( $stmt->fetch() ){
				$details = $row;
			}

			$stmt->close();

			if ($details) {
				# get last transfer
				$query = "SELECT UNIX_TIMESTAMP(datecreated) AS lasttransfer FROM accountentry WHERE username = ? AND type=? AND amount < 0 ORDER BY id DESC LIMIT 1";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param("si", $details['username'], Constants::get_value('USER_TO_USER_TRANSFER'));
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

				while( $stmt->fetch() ){
					 $details = array_merge($details, $row);
				}

				$stmt->close();
			}

			$this->closeSlaveConnection();


			if( $details )
				return new Merchant($details);
			else
				return null;

		}

		public function get_merchant_latest_transfer($username){

			# get last transfer
			$query = "SELECT UNIX_TIMESTAMP(datecreated) AS lasttransfer FROM accountentry WHERE type=? AND username = ? AND amount < 0 ORDER BY id DESC LIMIT 1";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("is", Constants::get_value('USER_TO_USER_TRANSFER'), $username);
			$stmt->execute();
			$stmt->bind_result($lasttransfer);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			return $lasttransfer ? (int)$lasttransfer : null;

		}

		/**
		 *
		 * Merchant PIN
		 *
		 */

		/**
		 *
		 * Get the authentication token
		 *
		 * @throws Exception
		 * @param  $token
		 * @return void
		 */
		public function get_authentication_token($token)
		{
			if( $this->check_authentication_token_exists($token) == false )
				throw new Exception("Merchant does not have an authentication token");

			$query = "SELECT * FROM merchantpin WHERE AuthToken=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $token);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$authentication_token = null;

			if( $stmt->fetch() == true )
				$authentication_token = new AuthenticationToken($row);

			$stmt->close();
			$this->closeSlaveConnection();

			return $authentication_token;
		}


		/**
		 * Store the pin and authentication for the merchant
		 *
		 * @param  $pin
		 * @param  $authentication
		 * @return void
		 */
		public function store_authentication_token($user_id, $authentication, $secret_question, $secret_answer, $email, $remove_old_tokens=true)
		{
			if( $this->authentication_token_exists($user_id) )
			{
				if($remove_old_tokens)
					$this->remove_unauthenticated_tokens($user_id);
				else
					throw new Exception("Authentication Token Exists.");
			}

			$query = "INSERT INTO merchantpin (UserID, AuthToken, DateCreated, SecretQuestion, SecretAnswer, Email)
							VALUES (?, ?, NOW(), ?, ?, ?)";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("issss", $user_id, $authentication, $secret_question, $secret_answer, $email);

			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows == 1;
		}

		/**
		 *
		 * Set the authentication to active by token
		 *
		 * @param  $token
		 * @return void
		 */
		public function set_authentication_active_by_token($token)
		{
			$auth = $this->get_authentication_token($token);

			$this->remove_authenticated_tokens($auth->user_id);

			$this->set_authentication_active($auth->id);
		}

		/**
		 * Set the authentication active by Id
		 * @param  $id
		 * @return void
		 */
		public function set_authentication_active($id)
		{
			$query = "UPDATE merchantpin SET status=1 WHERE id=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $id);

			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();
		}

		/**
		 *
		 * Check if the auth token exists
		 *
		 * @param  $token
		 * @return bool
		 */
		public function check_authentication_token_exists($token)
		{
			$query = " 	SELECT COUNT(*) FROM merchantpin WHERE AuthToken=? and status=0";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $token);
			$stmt->execute();
			$stmt->bind_result($count);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			return $count>0;
		}

		/**
		 *
		 * Check if authentication token exists
		 *
		 * @param  $user_id
		 * @return bool
		 */
		public function authentication_token_exists($user_id)
		{
			$query = " 	SELECT COUNT(*) FROM merchantpin WHERE UserID=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);
			$stmt->execute();
			$count = 0;
			$stmt->bind_result($count);
			$stmt->fetch();

			$stmt->close();
			$this->closeSlaveConnection();

			return $count>0;
		}

		/**
		 *
		 * Remove existing authentication tokens
		 *
		 * @param  $user_id
		 * @return bool
		 */
		public function remove_authentication_tokens($user_id)
		{
			$query = "DELETE FROM merchantpin WHERE UserID=?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);

			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows == 1;
		}


		/**
		 *
		 * Remove the tokens that aren't authenticated
		 *
		 * @param  $user_id
		 * @return bool
		 */
		public function remove_unauthenticated_tokens($user_id)
		{
			$query = "DELETE FROM merchantpin WHERE userid=? AND status=0";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);

			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows == 1;
		}

		/**
		 *
		 * remove the authenticated tokens
		 *
		 * @param  $user_id
		 * @return void
		 */
		public function remove_authenticated_tokens($user_id)
		{
			$query = "DELETE FROM merchantpin WHERE userid=? AND status=1";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("i", $user_id);

			$stmt->execute();

			$affected_rows = $stmt->affected_rows;

			$stmt->close();
			$this->closeMasterConnection();

			return $affected_rows == 1;
		}


		/**
		 *
		 * Verify secret
		 *
		 * @param $user_id, $secret_qn, $secret_ans
		 * @return $email
		 *
		 */
		public function verify_secret($user_id, $secret_ans)
		{
			$query = "SELECT Email FROM merchantpin WHERE UserID=? AND SecretAnswer=? AND Status=1";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $user_id, $secret_ans);
			$stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$email = '';
			if($stmt->fetch())
			{
				$email = $data['Email'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $email;
		}

		/**
		 *
		 * Get Secret Question
		 *
		 * @param $user_id
		 * @return $secret_qn
		 *
		 */
		public function get_secret_question($user_id)
		{
			$query = "SELECT SecretQuestion FROM merchantpin WHERE UserID=? AND Status=1";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('i', $user_id);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$secret_qn = '';
			if($stmt->fetch())
			{
				$secret_qn = $data['SecretQuestion'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $secret_qn;
		}

		public function get_merchants_in_country($country_id, $limit=20)
		{
			$memcache = Memcached::get_instance();
			$merchants = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_MERCHANTS_SUGGESTION_IN_COUNTRY, $country_id));
			
			if (empty($merchants)) 
			{
			if(SystemProperty::get_instance()->get_boolean(SystemProperty::AccountTransaction_SuggestMerchantFromSpecificLocationEnabled, false))
				{
					$query = "SELECT  m.username as merchant 
						  	  FROM location l1
						  	   , merchantlocation m 
						  	  WHERE l1.id=m.locationid 
						  	  AND m.status=1 
						  	  AND l1.CountryID = ? 
						  	  LIMIT ?";
				}
				else 
				{
					$query = "SELECT username merchant
						  FROM user
						  WHERE mobileverified = 1
						  AND status = 1
						  AND type = 3
						  AND countryid = ?
						  LIMIT ?";
				}
				
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param('ii', $country_id, $limit);
				$stmt->execute();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
				$merchants=array();
				while($stmt->fetch())
				{
					$merchants[]=$data['merchant'];
				}
				$stmt->close();
				$this->closeSlaveConnection();
				
				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_MERCHANTS_SUGGESTION_IN_COUNTRY, $country_id), $merchants, SystemProperty::get_instance()->get_integer(SystemProperty::AccountTransaction_SuggestedMerchantsFromCountryCacheExpiry, 1200));
			}
			
			return $merchants;
		}

		public function get_merchants_in_country_fusion($country_id, $page=1, $num_records=5) {
			try{
				$all_merchants = FusionRest::get_instance()->get(
					sprintf(
						FusionRest::KEYSPACE_ACCOUNT_MERCHANT_COUNTRY
						, $country_id
					) . '?' . http_build_query(array( 'offset' => 0
											,'limit' => 1000))
				);

				$merchants_count = count($all_merchants);
				$offset = ($page -1) * $num_records;
				for ($i=$offset; $i<$offset+$num_records; $i++) {
					if (empty($all_merchants[$i])) continue;
					$merchants[] = $all_merchants[$i];
				}

				return array($merchants, $merchants_count);
			} 
			catch(Exception $ex)
			{	
				return array();
			}
		}

		public function get_suggested_merchants_fusion($username, $page=1, $num_records=5) {
			try{
				$all_merchants = FusionRest::get_instance()->get(
					sprintf(
						FusionRest::KEYSPACE_ACCOUNT_USER_MERCHANT
						, $username
					) . '?' . http_build_query(array( 'offset' => 0
											,'limit' => 1000))
				);

				$merchants_count = count($all_merchants);
				$offset = ($page -1) * $num_records;
				for ($i=$offset; $i<$offset+$num_records; $i++) {
					if (empty($all_merchants[$i])) continue;
					$merchants[] = $all_merchants[$i];
				}

				return array($merchants, $merchants_count);
			} 
			catch(Exception $ex)
			{	
				return array();
			}
		}

		// for load's sake, we should feed this method the countryid of the user
		public function get_user_merchant_friends_in_country($username, $country_id = null)
		{
			if (null == $country_id)
			{
				return array();
			}

			$query = "SELECT DISTINCT u.username friend
					  FROM location l, merchantlocation ml, user u, broadcastlist b
					  WHERE ml.locationid = l.id
					  AND ml.username = u.username
					  AND ml.status = 1
					  AND l.countryID = u.countryID
					  AND u.type = 3
					  AND u.countryid = ?
					  AND u.status = 1
					  AND b.broadcastusername = ?
					  AND ml.username = b.username";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('is', $country_id, $username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			$friends=array();
			while($stmt->fetch())
			{
				$friends[]=$data['friend'];
			}
			$stmt->close();
			$this->closeSlaveConnection();
			return $friends;

		}

		public function get_merchant_tag_from_username($username)
		{
			try{

				$tag = FusionRest::get_instance()->get( sprintf( FusionRest::KEYSPACE_MERCHANT_TAG
																 ,$username
														));
				$tag_details = array(  'id' => $tag['id']
								  ,'merchantuserid' => $tag['merchantUserId']
								  ,'merchantusername' => $tag['merchantUserName']
								  ,'username' => $tag['username']
								  ,'datecreated' => $tag['dateCreated']
								  ,'lastsalesdate' => $tag['lastSalesDate']
								  ,'amount' => $tag['amount']
								  ,'currency' => $tag['currency']
								  ,'status' => $tag['status']
								  ,'userid' => $tag['userID']
								  ,'expiry' => $tag['expiry'] );
				if(!is_null($tag))
				{
					return new MerchantTag($tag_details);
				}

			}
			catch(Exception $e)
			{
				error_log("Unable to retrieve merchant tag for ["+$username+"]:: "+$e);
			}
		}


		public function get_min_non_top_merchant_tag_details($country_id, $currency)
			{
		try{

				return FusionRest::get_instance()->get( sprintf( FusionRest::KEYSPACE_MIN_NON_TOP_TAG
																 ,$country_id
																 ,$currency
														));
			}
			catch(Exception $e)
			{
				error_log("Unable to retrieve minimum merchant tag amount for countryid [".$country_id."] ".$e);
			}
			}

		public function get_min_top_merchant_tag_details($currency)
		{
		try{

				return FusionRest::get_instance()->get( sprintf( FusionRest::KEYSPACE_MIN_TOP_TAG
																 ,$currency
														));
			}
			catch(Exception $e)
			{
				error_log("Unable to retrieve minimum merchant tag amount for countryid [".$country_id."] ".$e);
			}
		}

		public function get_user_mentor($user_id, $is_username = False)
		{
			if(!$is_username){
				$query = "select mentor from merchantdetails where id= ?";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param('i', $user_id);
			} else {
				$query = "select md.mentor
					from merchantdetails md 
					join userid ui 
						on md.id = ui.id
					where ui.username = ?";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param('s', $user_id);
			}
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			$mentor=array();
			while($stmt->fetch())
			{
				$mentor[]=$data['mentor'];
			}
			$stmt->close();
			$this->closeSlaveConnection();
			return $mentor;
		}

		public function get_merchant_in_location_name($location)
		{
			$suggested_merchants = array();
			try{
				$suggested_merchants = FusionRest::get_instance()->get(
					sprintf(
						FusionRest::KEYSPACE_ACCOUNT_MERCHANT_COUNTRY_SEARCH
					) . '?' . http_build_query(array( 'offset' => 0
											,'name' => $location
											,'limit' => 1000))
				);
			} 
			catch(Exception $ex) { }
			
			if ($suggested_merchants) {
				return $suggested_merchants;
			}

			// if location search using fusion not gettin result, try to search using query (because query can search up to the city)
			$query = "select ml.name as name,ml.username as username,ml.address as address,ml.phonenumber as phonenumber from merchantlocation ml, location l where ml.locationid=l.id and lower(l.name)= lower(?)";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('s', $location);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			while($stmt->fetch())
			{
				$merchant = array("name"=>$data['name'],"username"=>$data['username'],"address"=>$data['address'],"phonenumber"=>$data['phonenumber']);
				try{
					$user = FusionRest::get_instance()->get(
						sprintf(
							FusionRest::KEYSPACE_USER_PROFILE_BY_USERNAME
							, $merchant['username']
						)
					);
					$merchant['userData'] = $user;
				} 
				catch(Exception $ex) {}
				$suggested_merchants[] = $merchant;
			}
			$stmt->close();
			$this->closeSlaveConnection();

			return $suggested_merchants;
		}

		public function get_expiring_tags($user_id, $days)
		{
		$expiring_tags = array();

		try{

				$tmp_expiring_tags = FusionRest::get_instance()->get( sprintf( FusionRest::KEYSPACE_MERCHANT_EXPIRING_TAGS
											  								   ,$user_id
																	  		   ,$days)
														);
				if(!is_null($tmp_expiring_tags))
				{
					foreach($tmp_expiring_tags['tags'] as $data)
					{
						if ($data['expiry']/1000 > time())
						{
							$expiring_tags[] = array(
											  "username" 		=> $data['userName']
											, "userid" 			=> $data['userID']
											, "datecreated" 	=> date('Y-m-d', $data['dateCreated']/1000)
											, "lastsalesdate" 	=> date('Y-m-d', $data['lastSalesDate']/1000)
											, "expirydate" 		=> $data['expiry']
											, "status" 			=> $data['status']
											, "type" 			=> $data['userType']
											, "displaypicture"	=> $data['displayPicture']
							);
						}
					}

				}

			}
			catch(Exception $e)
			{
				error_log("Unable to retrieve merchant tag stats:: "+$e);
			}

			return $expiring_tags;
		}

		public function get_merchant_performance($merchant, $user_id)
		{
			$merchant_performance_key = Redis::KEYSPACE_ENTITY_USER .
									$user_id .
									Redis::KEYSPACE_SEPARATOR .
									Redis::KEYSPACE_MERCHANT_PERFORMANCE;
			try
			{
				$redis_instance = Redis::get_master_instance_for_user_id($user_id);
				$merchant_performance = @json_decode($redis_instance->get($merchant_performance_key), true);
			}
			catch (Exception $re)
			{}

			if (!empty($merchant_performance))
			{
				return $merchant_performance;
			}

			$time = time();
			$year = intval(date('Y',$time));
			$month = intval(date('n',$time));

			$start_date = date('Y-m-d', mktime(0,0,0,1,1,$year));

			if ($month == 1)
			{
				$start_date = date('Y-m-d', mktime(0,0,0,$month-1,1,$year));
			}

			$end_date = date('Y-m-d', mktime(0,0,0,$month+1,1,$year));


			$current_month = $month;
			$previous_month = date('m',mktime(0,0,0,$month-1,1,$year));

			$query = "select
						case month(datecreated)
							when " . $current_month . " then 'cur'
							when " . $previous_month . " then 'pre'
							else month(datecreated)
						end as period,
						case
							when type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITPURCHASE')) . ")
								then 'credit_purchased'
							when type in ( " . Constants::get_value('USER_TO_USER_TRANSFER') . ") and amount < 0
								then 'unique_transfers'
							when type in ( " . Constants::get_value('USER_TO_USER_TRANSFER') . ") and amount > 0
								then 'credit_received'
							when type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITSPEND')) . ")
								then 'credit_spent'
							when type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRAILS')) . ")
								then 'trails_earned'
							else 'other'
						end as tx_type,
						case
							when type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITPURCHASE')) . ")
								then round(abs(sum(amount)),2)
							when type = " . Constants::get_value('USER_TO_USER_TRANSFER') . "  and amount < 0
								then truncate(count(distinct(substring_index(description,' ',-1))),0)
							when type in ( " . Constants::get_value('USER_TO_USER_TRANSFER') . ") and amount > 0
								then round(abs(sum(amount)),2)
							when type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_CREDITSPEND')) . ")
								then round(abs(sum(amount)),2)
							when type in (" . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_TRAILS')) . ")
								then round(abs(sum(amount)),2)
							else NULL
						end as tx_value
					from accountentry
					where username = ? and datecreated >= ? and datecreated < ?  group by period,tx_type
					UNION
					select
						case month(datecreated)
							when " . $current_month . " then 'cur'
							when " . $previous_month . " then 'pre'
							else month(datecreated)
						end as period,
						'total_sales' as tx_type,
						round(abs(sum(amount)),2) as tx_value
					from accountentry
					where type in ( " . implode(',',Constants::get_value('ACCOUNT_TRANSACTION_TYPE_SALES')) . ")
					and amount < 0
					and username = ?
					and datecreated >= ? and datecreated < ?  group by period,tx_type
					order by period,tx_type";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ssssss', $merchant,$start_date,$end_date,$merchant,$start_date,$end_date);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			$return_data = array();
			while($stmt->fetch())
			{
				$return_data[$data['period']][$data['tx_type']] = $data['tx_value'];
			}

			$stmt->close();
			$this->closeSlaveConnection();


			if ($redis_instance)
			{
				try
				{
					$redis_instance->setex($merchant_performance_key, 1800, json_encode($return_data));
					$redis_instance->disconnect();
				}
				catch (Exception $re)
				{}
			}

			return $return_data;
		}

		public function get_total_received_from_mentor_network($username)
		{
			$query = "SELECT z.Country, z.username, z.type, z.Purchased_From, sum(z.Total_Amt_AUD) total
				FROM
				(	
					SELECT t.Country, t.username, t.type, t.mentor, t.Purchased_From, md1.mentor mentor1, t.Total_Amt_AUD
					FROM
					(
						SELECT 
						b.name Country, ae.username, c.type, md.mentor, 
						trim(reverse(substring(reverse(ae.description),1,locate(' ',reverse(ae.description))))) Purchased_From,
						sum(ae.Amount/ae.ExchangeRate) Total_Amt_AUD
						FROM accountentry ae 
						JOIN user c ON (ae.username = c.username)
						JOIN userid uid ON (c.username = uid.username)
						JOIN country b ON (c.countryid = b.id) 
						JOIN merchantdetails md ON (uid.id = md.id)
						WHERE 
							not(lcase(md.mentor) in ('mentor', 'direct payment', 'directpayment', 'none', 'pilot10', ''))
							and not(lcase(md.mentor) like 'pilot10%') 
							and ae.type = 14
							and ae.amount > 0
							and ae.username = ?
							and c.type = 3
						GROUP BY 
							b.name, ae.username, c.type, md.mentor, 
							trim(reverse(substring(reverse(ae.description),1,locate(' ',reverse(ae.description)))))
					) t
					JOIN user u1 ON (t.Purchased_From = u1.username) 
					JOIN userid uid1 ON (u1.username = uid1.username)
					JOIN merchantdetails md1 ON (uid1.id = md1.id)
					WHERE (t.mentor = md1.mentor OR t.mentor = t.Purchased_From)
					GROUP BY t.Country, t.username, t.type, t.mentor, t.Purchased_From, md1.mentor, t.Total_Amt_AUD
					UNION ALL
					SELECT 
						b.name Country, ae.Username, c.type, md.mentor, 'mig33' Purchased_From, '' mentor1, 
						SUM(ae.Amount/ae.exchangerate) Total_Amt_AUD
					FROM accountentry ae 
					JOIN user c ON (ae.username = c.username)
					JOIN userid uid ON (c.username = uid.username)
					JOIN country b ON (c.countryid = b.id) 
					JOIN merchantdetails md ON (uid.id = md.id)
					WHERE ae.type IN (1,15) AND c.type = 3 AND ae.Amount > 0 
					AND ae.username = ?
					AND lcase(md.mentor) in ('mentor', 'direct payment', 'directpayment')	 
					GROUP BY 
					b.name, ae.Username, c.type, md.mentor
				) z 
				GROUP BY z.Country, z.username, z.type, z.Purchased_From, z.Total_Amt_AUD";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('ss', $username, $username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);
			$return_data = array();
			while($stmt->fetch())
			{
				$return_data[$data['puchased_from']] = $data['total'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $return_data;		
		}

		public function get_dashboard_old_customers($merchant_name, $older_than, $elements = 5)
		{

		if (SystemProperty::get_instance()->get_boolean(SystemProperty::Merchant_customer_search_disabled, true))
			{
				return array();
			}

			$memcache = Memcached::get_instance();
			$customers = $memcache->get(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_MERCHANT_DASHBOARD_CUSTOMERS, $merchant_name));

			if (!empty($customers))
			{
				return $customers;
			}

			$query = "SELECT user.username as username,  displaypicture, userid.id as userid, customers.datecreated as datecreated
						FROM user,
						(
							SELECT DISTINCT LCASE(a1.username) username ,max(a2.datecreated) as datecreated
							FROM accountentry a1, accountentry a2
							WHERE a1.type = " . Constants::get_value('USER_TO_USER_TRANSFER') . "
							AND a1.reference = CAST(a2.id AS CHAR)
							AND a2.type = " . Constants::get_value('USER_TO_USER_TRANSFER') . "
							AND a2.username = ?
							AND a2.amount < 0
							and a2.datecreated < now()
							group by username

							UNION

							SELECT DISTINCT LCASE(a.username) username ,max(a.datecreated) as datecreated
							FROM accountentry a,
							voucher v,
							voucherbatch vb
							where v.id = a.reference
							and v.voucherbatchid = vb.id
							and vb.username = ?
							AND v.status = 3
							AND a.type = " . Constants::get_value('VOUCHER_RECHARGE') . "
							and a.datecreated < now()
							and a.username =  SUBSTRING_INDEX(v.notes,' ',-1)
							and a.username != ?
							group by username
						) customers,
						userid
						WHERE user.username = customers.username
						AND user.username = userid.username
						and customers.datecreated < ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ssss", $merchant_name, $merchant_name, $merchant_name, $older_than);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$customers = array();
			while( $stmt->fetch() ){
				$customers[] = array(
						  'username' => $row['username']
						, 'displaypicture' => $row['displaypicture']
						, 'userid' => $row['userid']
						, 'datecreated' => $row['datecreated']
				);
			}

			$return_array = array();
			if ($customers)
			{
				shuffle($customers);
				$return_array = count($customers) > $elements
								? array_slice($customers, 0, $elements)
								: $customers;
				$memcache->add_or_update(Memcached::get_memcache_full_key(Memcached::$KEYSPACE_MERCHANT_DASHBOARD_CUSTOMERS, $merchant_name)
							, $return_array
							, 60);
			}

			$stmt->close();
			$this->closeSlaveConnection();


			return $return_array;
		}

		

		public function get_top_merchant_achievements_country($countryid)
		{
			try
			{
				$r_leader_slave = Redis::get_slave_instance_for_leaderboards();
				$data = $r_leader_slave->get(Redis::KEYSPACE_TOP_MERCHANT_ACHIEVEMENT.Redis::KEYSPACE_SEPARATOR.$countryid);

			}
			catch (Exception $re)
			{
				return false;
			}
			try
			{
				$r_leader_slave->disconnect();
			}
			catch (Exception $re)
			{}
			return json_decode($data, true);

		}



		public function get_top_merchant_achievements_all()
		{
			try
			{
				$r_leader_slave = Redis::get_slave_instance_for_leaderboards();
				$data = $r_leader_slave->get(Redis::KEYSPACE_TOP_MERCHANT_ACHIEVEMENT_ALL);

			}
			catch (Exception $re)
			{
				return false;
			}
			try
			{
				$r_leader_slave->disconnect();
			}
			catch (Exception $re)
			{}
			return json_decode($data, true);
		}

		public function get_merchant_popular_groups_in_country($username, $countryid, $user_id, $limit = 5)
		{
			$popular_groups_key = Redis::KEYSPACE_ENTITY_USER .
									$user_id .
									Redis::KEYSPACE_SEPARATOR .
									Redis::KEYSPACE_MERCHANT_POPULAR_GROUPS;
			try
			{
				$redis_instance = Redis::get_master_instance_for_user_id($user_id);
				$popular_groups = @json_decode($redis_instance->get($popular_groups_key), true);
			}
			catch (Exception $re)
			{}


			if (!empty($popular_groups))
			{
				return $popular_groups;
			}

			$query = "select g.name as name, g.id as id, max(gwp.datecreated) as lastpostdate, count(1) as numposts FROM
						groups g,
						user u,
						groupwallpost gwp
						where g.createdby=u.username
						and gwp.groupid = g.id
						and u.countryid = ?
						and g.status=1
						and g.ID not in (select groupid from groupmember where username = ?)
						group by g.name
						order by numposts desc ,lastpostdate desc limit ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("isi", $countryid, $username, $limit);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$popular_groups = array();
			while( $stmt->fetch())
			{
				$popular_groups[] = array(
						  'name' => html_entity_decode($row['name'], ENT_QUOTES)
						, 'id' => $row['id']
						, 'lastpostdate' => $row['lastpostdate']
						, 'numposts' => $row['numposts']
				);
			}

			if ($redis_instance)
			{
				try
				{
					$redis_instance->setex($popular_groups_key, 21600, json_encode($popular_groups)); //expiry 6 hours
					$redis_instance->disconnect();
				}
				catch (Exception $re)
				{}
			}
			$stmt->close();
			$this->closeSlaveConnection();

			return $popular_groups;
		}

		public function get_merchant_popular_chatrooms_in_country($countryid, $limit = 5)
		{
			$popular_chatrooms_key = Redis::KEYSPACE_MERCHANT_POPULAR_CHATROOMS .
										Redis::KEYSPACE_SEPARATOR .
										$countryid;
			try
			{
				$r_leader_master = Redis::get_master_instance_for_leaderboards();
				$popular_chatrooms = @json_decode($r_leader_master->get($popular_chatrooms_key), true);

			}
			catch (Exception $re)
			{}
			if (!empty($popular_chatrooms))
			{
				return $popular_chatrooms;
			}

			$query = "select name FROM
						chatroom
						where primarycountryid = ?
						order by DateLastAccessed desc limit ?";
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);

			$stmt->bind_param("ii", $countryid, $limit);

			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$popular_chatrooms = array();
			while( $stmt->fetch())
			{
				$popular_chatrooms[] = $row['name'];
			}
			if ($r_leader_master)
			{
				try
				{
					$r_leader_master->setex($popular_chatrooms_key, 60, json_encode($popular_chatrooms)); //expiry 1 min
					$r_leader_master->disconnect();
				}
				catch (Exception $re)
				{}
			}
			$stmt->close();
			$this->closeSlaveConnection();

			return $popular_chatrooms;
		}

		public function fix_merchanttag_accountentry(MerchantTag $merchant_tag)
		{
			$user_dao = new UserDAO();
			$merchant = $user_dao->get_user_detail_from_id($merchant_tag->merchant_user_id);
			$user = $user_dao->get_user_detail_from_id($merchant_tag->user_id);
			$system_property = SystemProperty::get_instance();

			$query = "	SELECT currency, ABS(amount), id
						FROM accountentry
						WHERE id >= ?
						AND type = ?
						AND datecreated >= ?
						AND username = ?
						AND substring_index(description, ' ', -1) = ?
						AND amount < 0
						ORDER BY id DESC
						LIMIT 1";

			$buffer_time = time() - $system_property->get_integer(SystemProperty::MerchantTagValidPeriod, 43200);
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('iissi'
						  , $system_property->get_integer(SystemProperty::MinimumCreditTransferAccountentryID, 716668286)
							  , Constants::get_value('USER_TO_USER_TRANSFER')
							  , $buffer_time
							  , $merchant->username
							  , $user->username);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$accountentry_id = null;
			while($stmt->fetch())
			{
				$merchant_tag->amount = get_value_from_array('amount', $row, 'float', 0.00);
				$merchant_tag->currency = get_value_from_array('currency', $row);
				$accountentry_id = get_value_from_array('id', $row);
			}

			$stmt->close();

			if ($accountentry_id)
			{
				$query = "UPDATE merchanttag SET accountentryid = ? WHERE id = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param('ii', $accountentry_id, $merchant_tag->id);
				$stmt->execute();
				$stmt->close();
				$this->closeMasterConnection();
			}

			$this->closeSlaveConnection();
			return $merchant_tag;
		}

		public function get_top_active_tags($merchant_userid, $limit=5)
		{
			$top_tag_users_key = Redis::KEYSPACE_ENTITY_USER.$merchant_userid.Redis::KEYSPACE_SEPARATOR.Redis::KEYSPACE_TOP_TAG_ACTIVE_USERS_CURRENT;

			try
			{
				$redis_instance = Redis::get_master_instance_for_user_id($merchant_userid);
				$top_tag_users = @json_decode($redis_instance->get($top_tag_users_key), true);
			}
			catch (Exception $re)
			{}

			if (!empty($top_tag_users))
			{
				return $top_tag_users;
			}

			return $this->get_top_merchant_tag_stats($merchant_userid, Constants::get_value('MERCHANT_TAG_ACTIVE'), $limit);
		}

		public function get_top_inactive_tags($merchant_userid, $limit=5)
		{
			$top_tag_users_key = Redis::KEYSPACE_ENTITY_USER.$merchant_userid.Redis::KEYSPACE_SEPARATOR.Redis::KEYSPACE_TOP_TAG_INACTIVE_USERS;

			try
			{
				$redis_instance = Redis::get_master_instance_for_user_id($merchant_userid);
				$top_tag_users = @json_decode($redis_instance->get($top_tag_users_key), true);
			}
			catch (Exception $re)
			{}

			if (!empty($top_tag_users))
			{
				return $top_tag_users;
			}

			return $this->get_top_merchant_tag_stats($merchant_userid, Constants::get_value('MERCHANT_TAG_INACTIVE'), $limit);
		}

		private function get_top_merchant_tag_stats($merchant_userid, $status, $limit=5)
		{
			$query = "SELECT u.username
							 , u.displaypicture
							 , mts.currMonthActivity activity
					  FROM merchanttag mt
						   , userid ui
						   , user u
						   , merchanttaguseractivitystat mts
					  WHERE u.username = ui.username
					  AND mt.userID = ui.id
					  AND mt.status = 1
					  AND mts.userID = mt.userID
					  AND mt.merchantuserid = ? ";

			if ($status == Constants::get_value('MERCHANT_TAG_ACTIVE'))
			{
				$query .= "AND mts.currMonthActivity/? > ? ";
				$key = Redis::KEYSPACE_ENTITY_USER.$merchant_userid.Redis::KEYSPACE_SEPARATOR.Redis::KEYSPACE_TOP_TAG_ACTIVE_USERS_CURRENT;
			}
			else
			{
				$query .= "AND mts.currMonthActivity/? <= ? ";
				$key = Redis::KEYSPACE_ENTITY_USER.$merchant_userid.Redis::KEYSPACE_SEPARATOR.Redis::KEYSPACE_TOP_TAG_INACTIVE_USERS;
			}

			$query .= "GROUP BY mt.userID
					   ORDER BY mts.currMonthActivity ASC
					   LIMIT ?";
			$system_property = SystemProperty::get_instance();

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param('iiii'
							  , $merchant_userid
							  , date('j')
						  , $system_property->get_integer(SystemProperty::ActiveMerchantTagActivityCount, 23)
							  , $limit);

			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$top_tag_users = array();

			while( $stmt->fetch())
			{
				$top_tag_users[$row['username']] = array(
							  "username" => $row['username']
							, "displaypicture" => $row['displaypicture']
							, "activity" => $row['activity']
				);
			}

			// save result in redis
			$expiry = $system_property->get_integer(SystemProperty::MerchantTagTopActiveExpiry, 3600);

			try
			{
				$redis_instance = Redis::get_master_instance_for_user_id($merchant_userid);
				$redis_instance->setex($key, $expiry, json_encode($top_tag_users));
				$redis_instance->disconnect();
			}
			catch (Exception $re)
			{}

			$stmt->close();
			$this->closeSlaveConnection();


			return $top_tag_users;
		}

		public function get_merchant_tag_stats($merchant_userid, $page=1, $num_records=20)
			{
			$tag_stats = array(  "tags" => array()
								,"total_tags" => 0);

			try{

				$tags = FusionRest::get_instance()->get( sprintf( FusionRest::KEYSPACE_MERCHANT_TAGS
																 ,$merchant_userid
																 ,http_build_query(array( 'page' => $page
																						 ,'numRecords' => $num_records))
														));
				$tmp_tags = array();

				if(!is_null($tags))
				{
					// note, java timestamp is in milliseconds so we have to divide the timestamps by 1000
					foreach($tags['tags'] as $tag)
					{
						$tmp_tags[$tag['userName']] = array(	"username" => $tag['userName']
															, "status" => $tag['status']
															, "datecreated" => $tag['dateCreated']
															, "lastsalesdate" => $tag['lastSalesDate']
															, "expiry" => $tag['expiry']
															, "gender" => $tag['gender']
															, "aboutme" => $tag['aboutMe']
															, "labels" => $tag['labels']
															, "displaypicture" => $tag['displayPicture']
															, "country" => $tag['country']
															, "miglevel" => $tag['migLevel']
				);
			}
					$tag_stats["tags"] = $tmp_tags;
					$tag_stats["total_tags"] = $tags['totalTags'];
				}

			}
			catch(Exception $e)
			{
				error_log("Unable to retrieve merchant tag stats:: "+$e);
			}

			return $tag_stats;

		}

		public function get_mail_template_from_server($lang_code)
		{
			global $merchant_mail_content_url;
			$url = sprintf($merchant_mail_content_url, $lang_code);
			try {
				$mail_template = @file_get_contents($url);
			} catch(Exception $e)
			{
				error_log("Tried to get mail contentand failed for language - ", $lang_code);
				$mail_template='';
			}
			if(strlen($mail_template) === 0)
			{
				//Create a empty array with is_language_defined property as false and save it in the xcache
				return array('is_lang_defined'=>false);
			} else {
				$content = preg_split('/[\r\n]+/', $mail_template, 2);
				return array(
					  'is_lang_defined' => true
					, 'subject'			=> $content[0]
					, 'body'			=> $content[1]
				);
			}
		}

		private function get_merchant_mail_template($lang)
		{
			$lang_code = $lang;
			if(array_key_exists($lang, MerchantLanguage::$MERCHANT_PREF_LANGS))
			{
				$lang_code = MerchantLanguage::$MERCHANT_PREF_LANGS[$lang];
			}

			$mail_template_from_cache = XCache::getInstance()->get(
				XCache::KEYSPACE_MERCHANT_EMAIL_WELCOME . $lang_code
				, array(&$this, 'get_mail_template_from_server')
				, 60 * 60 * 6 //
				, array(
					  'fetch_without_lock' 	=> true
					, 'set_without_lock' 	=> true
					, 'callback_args' 		=> array($lang_code)
				)
			);

			if(!is_null($mail_template_from_cache) && $mail_template_from_cache["is_lang_defined"])
			{
				return $mail_template_from_cache;
			} else {
				if($lang === MerchantLanguage::$MERCHANT_MAIL_DEFAULT_LANG)
				{
					throw new Exception("Unable to fetch the default mail template for " + $lang);
				}
				return $this->get_merchant_mail_template(MerchantLanguage::$MERCHANT_MAIL_DEFAULT_LANG);
			}
		}


		public function send_merchant_email($username, $user_email, $first_name, $lang)
		{
			try {
				$message_template = $this->get_merchant_mail_template($lang);
				$subject = $message_template['subject'];
				$mail_body = $message_template['body'];
				soap_call_ejb('sendEmailFromNoReply', array($user_email, $subject, $mail_body));
				//mail($user_email, $subject, $mail_body, 'From:' .'merchant@mig33.com'. "\r\n" .'Reply-To: ' . 'merchant.mig33.com');
			} catch(Exception $e)
			{
				error_log("Exception when sending merchant welcome mail - " . $lang);
			}
		}

		public function send_campaign_email($user_email)
		{
			try
			{
				$subject = "Merchant Certified Program";
				$mail_body = "Dear Valued User,\n\n";
				$mail_body .= "We congratulate you for signing up as a merchant and this is your chance to grab the opportunity to become our Top merchant!\n\n";
				$mail_body .= "Our friendly migme Merchant Team will get in touch with you shortly. We look forward to meet you and show you how to be a successful merchant with migme.\n\n";
				$mail_body .= "Best regards,\nThe migme Merchant Team\n";
				soap_call_ejb('sendEmailFromNoReply', array($user_email, $subject, $mail_body));
			}
			catch(Exception $e)
			{
				error_log("Exception when sending campaign email - " . $e->getMessage());
			}
		}

		//cache per object
		private $merchant_quest_data = null;
		private function load_quest_data() 
		{
			$json_path = $GLOBALS['sharedStoragePath'].'merchant/merchant_points.json';
			try {
				if(!file_exists($json_path)) throw new Exception('JSON dump not found');
				$mq_data = json_decode(file_get_contents($json_path), true);
			} catch (Exception $e){
				//guarentee a 'merchants' key
				$mq_data = Array('merchants' => array());
			}
			$this->merchant_quest_data = $mq_data;

		}

		public function get_merchant_quest_data()
		{
			if($this->merchant_quest_data === null)
			{
				$this->load_quest_data();
			}
			return $this->merchant_quest_data;
		}
		
		public function get_merchant_quest($username)
		{
			$merchant_quest_data = $this->get_merchant_quest_data();

			$mq_data = array();
			$mq_data['merchant'] = isset($merchant_quest_data['merchants'][$username]) ? $merchant_quest_data['merchants'][$username] : array();
			$mq_data['merchant']['username'] = $username;

			return $mq_data;
		}

		private $ranked_merchants = null;

		private function point_sort()
		{
			$aPoint = $a['total_points'];
			$bPoint = $b['total_points'];

			if($aPoint == $bPoint) return 0;
			return ($aPoint < $bPoint) ? 1 : -1;
		}

		private function get_ranked_merchants()
		{
			if($this->ranked_merchants == null)
			{
				$quest_data = $this->get_merchant_quest_data();
				$merchants = $quest_data['merchants'];

				uasort($merchants, array($this,'point_sort'));
				$this->ranked_merchants = $merchants;
			}

			return $this->ranked_merchants;
		}

		private function get_ranking_data($username, $ranked_merchants, $limit)
		{
			$ranking = 0;
			$count = 0;
			$limit_reached = false;
			$ranking_found = false;
			$merchants = array();
			foreach($ranked_merchants as $name => $info)
			{
				if($limit_reached && $ranking_found) break;
				if(!$ranking_found)
				{
					$ranking++;
					if($name == $username) $ranking_found = true;
				}

				if(!$limit_reached)
				{
					$count++;
					if($count == $limit) $limit_reached = true;
					$merchants[$name] = $info;
				}
			}

			return array("ranking" => $ranking, "merchants" => $merchants);
		}

		private function get_merchant_ranking_by_country($username, $countryid, $limit)
		{
			$ranked_merchants = $this->get_ranked_merchants();

			$filtered_merchants = array();
			
			foreach($ranked_merchants as $name => $merch)
			{
				if($merch['countryid'] == $countryid)
				{
					$filtered_merchants[$name] = $merch;
				}
			}
			return $this->get_ranking_data($username, $filtered_merchants, $limit);

		}

		public function get_top_merchant_by_user_country($username, $limit = 10)
		{
			$dao = new UserDAO();
			$user = $dao->get_user_detail($username);
			$country = CountryDAO::get_country_data($user->countryID);
			
			$ranked_merchants = $this->get_merchant_ranking_by_country($username, $country['id'], $limit);

			$top_merchant['ranking'] = $ranked_merchants['ranking'];
			$top_merchant['merchants'] = $ranked_merchants['merchants'];
			$top_merchant['country'] = $country;

			return $top_merchant;
		}

		private function get_merchant_ranking($username, $limit)
		{
			$quest_data = $this->get_merchant_quest_data();
			$ranked_merchants = $this->get_ranked_merchants();
			return $this->get_ranking_data($username, $ranked_merchants, $limit);
		}

		public function get_top_merchant($username, $limit = 10)
		{
			$ranked_merchants = $this->get_merchant_ranking($username, $limit);

			$dao = new UserDAO();
			$merchants = array();
			foreach($ranked_merchants['merchants'] as $merchant_name => $info)
			{
				$merchants[] = array( 
					'merchant' 	=> $merchant_name,
					'level' 	=> $dao->get_user_level($merchant_name),
					'profile'	=> $dao->get_user_profile($username, $merchant_name),
					'country'	=> CountryDAO::get_country_data($info['countryid'])
				);
			}

			$top_merchant['ranking'] = $ranked_merchants['ranking'];
			$top_merchant['merchants'] = $merchants;

			return $top_merchant;
		}
	}

?>
