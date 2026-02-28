<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();
$userDetails = ice_get_userdata();

$pf = $_GET['pf'];
$list_for = $_GET['lf'];
$customer_base_link = getMCWapPath()."customer.php?pf=".$pf.paramSeparator()."u=";
if ($list_for == "TC")
	$customer_base_link = getMCWapPath()."transfer_credit.php?pf=".$pf.paramSeparator()."transferto=";

// get pagination param
$page = isset($_GET['p']) ? $_GET['p'] : 1;
$itemsPerPage = 20;

// get customers
$customerResults = getMerchantCustomers(new UserPagingObject($userDetails->username, (int)$page, $itemsPerPage));

// pagination links
$pPrevLink = getMCWapPath().'customers.php?pf='.$pf.paramSeparator().'p='.($page-1).paramSeparator().'lf='.$list_for;
$pNextLink = getMCWapPath().'customers.php?pf='.$pf.paramSeparator().'p='.($page+1).paramSeparator().'lf='.$list_for;

// footer links
$backLink = getMCWapPath()."merchant_center.php";
$footerType = "";
if ($list_for == "TC") {
	$backLink = getMCWapPath()."transfer_credit.php?pf=".$pf;
	$footerType = "cancel";
}
$helpLink = "";

// output HTML
if (isPagelet()) {
	include("view/customers_pagelet.php");
	flushOutputBuffer();
} else {
	include("view/customers_wap.php");
}
