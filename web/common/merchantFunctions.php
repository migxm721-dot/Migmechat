<?php
	//require_once("common-inc.php");
	//require_once("pageletFunctions.php");

	function getMerchantCustomers($userpagingobject)
	{
		try
		{
			return soap_call_ejb('getMerchantCustomers', array($userpagingobject->username, $userpagingobject->pageNumber, $userpagingobject->numberOfEntries));
		}
		catch(Exception $e)
		{
		}
	}

	function getReferralCountForAMonth($username, $month, $year)
	{
		try
		{
			settype($month, "integer");
			settype($year, "integer");
			$referral_count = soap_call_ejb('getUserReferralCountByMonth', array($username, $month, $year));
			return $referral_count;
		}
		catch(Exception $e)
		{
		}
	}

	function getUserReferralForAMonth($userpagingobject, $month, $year)
	{
		try
		{
			settype($month, "integer");
			settype($year, "integer");
			$referrals = soap_call_ejb('getUserReferralByMonth',
								array($userpagingobject->username,
										$month, $year,
										$userpagingobject->pageNumber,
										$userpagingobject->numberOfEntries));
			return new UserReferralResult($referrals);
		}
		catch(Exception $e)
		{
		}
	}

	function getUserReferrals($userpagingobject)
	{
		try
		{
			settype($month, "integer");
			settype($year, "integer");
			$referrals = soap_call_ejb('getUserReferral',
								array($userpagingobject->username,
										$userpagingobject->pageNumber,
										$userpagingobject->numberOfEntries));
			return new UserReferralResult($referrals);
		}
		catch(Exception $e)
		{
		}
	}

	/**
	* Gets the user transaction summary for the month
	*
	* @param username of the user
	* @param month of the year the summary is required for
	* @param year the summary is required for
	*
	**/
	function getTransactionSummaryForAMonth($username, $month, $year, $type="")
	{
		try
		{
			settype($month, "integer");
			settype($year, "integer");
			$transactionSummary = soap_call_ejb('getTransactionSummaryByMonth', array($username, $month, $year, $type));
			return new TransactionSummary($transactionSummary);
		}
		catch(Exception $e)
		{
		}
	}

	/**
	* Get the transactions for a user
	*
	* @param userpageingobject containing the details for username, page, and count per page, see pageletFunctions.php
	*
	**/
	function getTransactions($userpagingobject, $type="")
	{
		try
		{
			$transactions = soap_call_ejb('getTransactions',
								array($userpagingobject->username,
										$userpagingobject->pageNumber,
										$userpagingobject->numberOfEntries, $type) );
			$transactionResults = new TransactionResults($transactions['page'], $transactions['hasMore'], $transactions['totalResults']);

			$results = $transactions['accountEntries'];
			foreach($results as $entries)
			{
				$transactionResults->add_transaction($entries);
			}
			return $transactionResults;
		}
		catch(Exception $e)
		{
		}
	}

	function getTransactionForCustomer($username, $userpagingobject)
	{
		try
		{
			$transactions = soap_call_ejb('getMerchantCustomerTransactions',
								array($username, $userpagingobject->username,
										$userpagingobject->pageNumber,
										$userpagingobject->numberOfEntries) );
			$transactionResults = new TransactionResults($transactions['page'], $transactions['hasMore'], $transactions['totalresults']);

			$results = $transactions['account_entries'];
			if( is_array($results) )
			foreach($results as $entries)
			{
				$transactionResults->add_transaction($entries);
			}
			return $transactionResults;
		}
		catch(Exception $e)
		{
		}
	}

	function getTransactionSummaryForCustomer($username, $customername, $type="")
	{
		try
		{
			settype($month, "integer");
			settype($year, "integer");
			$transactionSummary = soap_call_ejb('getTransactionSummaryForCustomer', array($username, $customername, $type));
			return new TransactionSummary($transactionSummary);
		}
		catch(Exception $e)
		{
		}
	}

	/**
	* Get the transactions for a user since a data
	*
	* @param userpageingobject containing the details for username, page, and count per page, see pageletFunctions.php
	* @param date of the request to retrieve from
	*
	*/
	function getTransactionSinceDate($userpagingobject, $date, $type="")
	{
		try
		{
			settype($date, "integer");
			$transactions = soap_call_ejb('getTransactionsSinceDate',
								array($userpagingobject->username,
										$date,
										$userpagingobject->pageNumber,
										$userpagingobject->numberOfEntries, $type) );
			$transactionResults = new TransactionResults($transactions['page'], $transactions['hasMore'], $transactions['totalResults']);

			$results = $transactions['accountEntries'];
			foreach($results as $entries)
			{
				$transactionResults->add_transaction($entries);
			}
			return $transactionResults;
		}
		catch(Exception $e)
		{
		}
	}

	/**
	* Class holding the transaction results
	**/
	class TransactionResults
	{
		public $pagenumber = 0;
		public $hasmore = false;
		public $totalresults = 0;
		public $transactions = array();

		/**
		* Constructor
		**/
		public function __construct($pagenumber, $hasmore, $totalresults)
		{
			$this->pagenumber = $pagenumber;
			$this->hasmore = $hasmore;
			$this->totalresults = $totalresults;
		}

		/**
		* Add a transaction to the result set
		**/
		public function add_transaction($transaction)
		{
			$transaction = new Transaction($transaction);
			$this->transactions[] = $transaction;
		}
	}

	/**
	* Transaction class
	**/
	class Transaction
	{
		public $datecreated;
		public $type;
		public $description;
		public $currency;
		public $exchangerate;
		public $amount;
		public $destination_username;

		public function __construct($transaction)
		{
			$this->datecreated = $transaction['dateCreated'];
			$this->type = $transaction['type'];
			$this->description = $transaction['description'];
			$this->currency = $transaction['currency'];
			$this->exchangerate = $transaction['exchangeRate'];
			$this->amount = $transaction['amount'];
			if( isset($transaction['destinationUsername']) )
				$this->destination_username = $transaction['destinationUsername'];
		}
	}

	/**
	* Transaction Summary Class
	**/
	class TransactionSummary
	{
		public $totalSale;
		public $numberOfSales;
		public $totalCredit;
		public $numberOfCredits;

		public function __construct($transactionSummary)
		{
			$this->totalSale = $transactionSummary['totalSales'];
			$this->totalSale = abs($this->totalSale);
			$this->numberOfSales = $transactionSummary['numberOfSales'];
			$this->totalCredit = $transactionSummary['totalCredits'];
			$this->numberOfCredits = $transactionSummary['numberOfCredits'];
		}
	}

	class UserReferralResult
	{
		public $pagenumber;
		public $totalresults;
		public $totalpages;
		public $referrals = array();

		public function __construct($referrals)
		{
			$this->pagenumber = $referrals['pagenumber'];
			$this->totalresults = $referrals['totalresults'];
			$this->totalpages = $referrals['totalpages'];
			$ref = $referrals['invitations'];
			foreach($ref as $r)
			{
				$this->add_referral($r);
			}
		}

		public function add_referral($referral)
		{
			$this->referrals[] = new UserReferral($referral);
		}
	}

	/**
	*
	* User referral
	*
	**/
	class UserReferral
	{
		public $mobilephone;
		public $username;
		public $datecreated;

		public function __construct($referral)
		{
			$this->mobilephone = $referral['mobilephone'];
			$this->username = $referral['username'];
			$this->datecreated = $referral['datecreated'];
		}

		/**
		*
		* Check if user has accepted referral
		*
		**/
		public function referral_accepted()
		{
			return (!empty($this->username));
		}
	}
?>