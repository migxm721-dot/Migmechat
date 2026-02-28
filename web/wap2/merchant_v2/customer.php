<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();

//Page from, tracks where user is coming from
$pf = $_GET['pf'];

// check if coming from friends list
$isFriend = $_GET['isFriend'];

// get merchant
$userDetails = ice_get_userdata();

// get customer details
$customerName = $_GET['u'];
$customerProfile;
$customerDetails;
$contact;
$error = "";
$customerDTO = getProfileData($userDetails->username, $customerName, $customerProfile, $customerDetails, $contact, $error);

// get pagination param
$page = isset($_GET['p']) ? $_GET['p'] : 1;

// get transaction summary
$transactionSummary = getTransactionSummaryForCustomer($userDetails->username, $customerName) ;

// get customer transactions
$itemsPerPage = 10;
$transactionResults = getTransactionForCustomer($userDetails->username, new UserPagingObject($customerName, (int)$page, $itemsPerPage));

// process transaction rows
if ($transactionResults->totalresults > 0) {
	$startCount = ($page == 1 ? 1 : ($page-1)*$itemsPerPage+1);
	$transactionList = array();

	foreach($transactionResults->transactions as $transaction) {
		$rowClass = ($startCount % 2 == 0 ? "even" : "odd");
		$elapsedTime = getElapsedTime($transaction->datecreated);
		$tType = $transaction->type;
		if ($tType == 'VOUCHERS_CREATED') {
			$iconType = "voucher";
			$altText = "VOUCHER";
			$desc = 'Redeemed '.$transaction->currency.'$'.round_twodec(abs($transaction->amount)).' voucher';
		} else {
			$iconType = "transfer";
			$altText = "TRANSFER";
			if ($transaction->amount > 0) {
				$desc = $customerName.' transferred '.$transaction->currency.'$'.round_twodec(abs($transaction->amount)).' to you';
			} else {
				$desc = 'You transferred '.$transaction->currency.'$'.round_twodec(abs($transaction->amount)).' to '.$customerName;
			}
		}
		$desc .= (isPagelet() ? ' , '.$elapsedTime : '<span class="sec"> '.$elapsedTime.'</span>');

		$transactionList[] = array (
				"count" => $startCount,
				"rowClass" => $rowClass,
				"iconType" => $iconType,
				"altText" => $altText,
				"desc" => $desc
				);
		$startCount++;
	}
}

// set up pagination links
$showPagination = true;
$baseLink = getMCWapPath().'customer.php?pf='.$pf.paramSeparator().'u='.$customerName;
$pPrevLink = $baseLink.paramSeparator().'p='.($page-1);
$pNextLink = $baseLink.paramSeparator().'p='.($page+1);

// set up links
$backLink = getMCWapPath()."customers.php";
if ($pf == 'MC')
	$backLink = getMCWapPath()."merchant_center.php";
else if ($pf == 'TL' )
	$backLink = getMCWapPath()."transactions.php";
else if ($pf == 'IL')
	$backLink = getMCWapPath()."invitations.php?m=".$_GET['pfm'].paramSeparator()."y=".$_GET['pfy'].paramSeparator()."pf=".$_GET['pff'];
else if ($isFriend == 'true')
	$backLink = getMCWapPath()."friends.php";

$helpLink = "";

$pageTitle = "Customer Profile";

// output HTML
if (isPagelet()) {
	include("view/customer_pagelet.php");
	flushOutputBuffer();
} else {
	include("view/customer_wap.php");
}
?>
