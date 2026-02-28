<?php
include_once("../../common/common-inc.php");

ice_check_session();
$username = ice_get_username();
$userDetails = ice_get_userdata();

//Function to get a nicer description of your account activity
function getBetterDescription($original){
	//Convert type to a user friendly value
	if ($original == 'ACTIVATION_CREDIT')
		return 'Activation';
	else if ($original == 'BONUS_CREDIT')
		return 'Bonus';
	else if ($original == 'CALL_CHARGE')
		return 'Call';
	else if ($original ==  'CREDIT_CARD')
		return 'Credit Card';
	else if ($original == 'CREDIT_CARD_CHARGEBACK')
		return 'Charge back';
	else if ($original == 'CREDIT_CARD_REFUND')
		return 'Refund';
	else if ($original == 'MANUAL')
		return 'Manual';
	else if ($original == 'PREMIUM_SMS_FEE')
		return 'Fee';
	else if ($original == 'PREMIUM_SMS_RECHARGE')
		return 'Recharge';
	else if ($original == 'VOUCHER_RECHARGE')
		return 'Voucher Recharge';
	else if ($original == 'VOUCHERS_CREATED')
		return 'Vouchers Created';
	else if ($original == 'VOUCHERS_CANCELLED')
		return 'Vouchers Cancelled';
	else if ($original == 'CURRENCY_CONVERSION')
		return 'Currency Conversion';
	else if ($original == 'PRODUCT_PURCHASE')
		return 'Product';
	else if ($original == 'REFERRAL_CREDIT')
		return 'Referral';
	else if ($original == 'REFUND')
		return 'Refund';
	else if ($original == 'SMS_CHARGE')
		return 'SMS';
	else if ($original == 'SYSTEM_SMS_CHARGE')
		return 'SMS';
	else if ($original == 'SUBSCRIPTION')
		return 'Subscription';
	else if ($original == 'TELEGRAPHIC_TRANSFER')
		return 'TT';
	else if ($original == 'USER_TO_USER_TRANSFER')
		return 'Transfer';
	else if ($original == 'BANK_TRANSFER')
		return 'Bank Transfer';
	else if ($original == 'BANK_TRANSFER_REVERSAL')
		return 'Bank Transfer Reversal';
	else if ($original == 'CHATROOM_KICK_CHARGE')
		return 'Chatroom Kick';
	else if ($original == 'CREDIT_EXPIRED')
		return 'Credit Expired';
	else if ($original == 'WESTERN_UNION')
		return 'Western Union';
	else if ($original == 'WESTERN_UNION_REVERSAL')
		return 'Western Union Reversal';
	else if ($original == 'EMOTICON_PURCHASE')
		return 'Emoticons';
	else if ($original == 'CONTENT_ITEM_PURCHASE')
		return 'Mobile Content';
	else if ($original == 'CONTENT_ITEM_REFUND')
		return 'Mobile Content Refund';
	else if ($original == "VIRTUAL_GIFT_PURCHASE")
		return "Virtual Gift";
	else
		return '';
}

//Settings for
$rpage = 0;
$rentries = 5;

if ($_GET['page'])
{
	$rpage = $_GET['page'];
	settype($rpage, 'int');
}
if ($_GET['entries'])
{
	$rentries = $_GET['entries'];
	settype($rentries, 'int');
}

//DETECT WHAT PAGE TO SHOW
	$currentPage = "details";
	$pageTitle = "";
	if($_GET["cp"] == "sms"){
		$currentPage = "sms";
		$pageTitle = "SMS History";
	}else if($_GET["cp"] == "call"){
		$currentPage = "call";
		$pageTitle = "Call History";
	}else if($_GET["cp"] == "acct"){
		$currentPage = "acct";
		$pageTitle = "Account History";
	}else if($_GET["cp"] == "tt"){
		$currentPage = "tt";
		$pageTitle = "TT History";
	}

	// GET THE ACCOUNT BALANCE
	try {
		$balance_data = soap_call_ejb('getAccountBalance', array($username));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	$balance = number_format($balance_data['balance'], 2);
	$currency = $balance_data['currency.code'];

?>
<html>
  <head>
    <title><?=$pageTitle?></title>
  </head>
  <body bgcolor="white">
	<p>Account Balance: <?=$balance?> <?=$currency?></p>
	<?php

	/////////////////////////////////////////
	//ACCOUNT
	/////////////////////////////////////////
	if($currentPage == "acct")
	{
		$aedList = soap_call_ejb('getAccountEntries', array($username, $rpage, $rentries));
		$page = $aedList[0]['page'];
		$hasMore = $aedList[0]['hasMore'];

		if (sizeof($aedList) > 1)
		{
			//print ''.sizeof($aedList).'';
			for ($i = 1; $i < sizeof($aedList); $i++)
			{
				//Construct Individual View URL parameters
				$view_url  = 'history_view.php?title=Transaction%20Details&return='.$currentPage.'&page='.$page.'&type='.$currentPage.'&id='.$aedList[$i]['id'];
			  	print '<p><b>'.$aedList[$i]['dateCreated'].'</b></p>';
			  	print '<p><a href="'.$server_root.'/midlet/member/'.$view_url.'">'.getBetterDescription($aedList[$i]['type']).'</a>: ' . number_format(abs($aedList[$i]['amount']), 2) . ' ' . $aedList[$i]['currency'] . '</p><br>';
			}
		}
		else
		{
			print '<p>You have no account entries to view.</p><br>';
		}
	/////////////////////////////////////////
	//SMS
	/////////////////////////////////////////
	}
	else if($currentPage == "sms")
	{
		$aedList = soap_call_ejb('getSMSHistory', array($username, $rpage, $rentries));
		$page = $aedList[0]['page'];
		$num_pages = $aedList[0]['numPages'];
		$num_entries = $aedList[0]['numEntries'];

		if (sizeof($aedList) > 1)
		{
			for ($i = 1; $i < sizeof($aedList); $i++)
			{
				//Construct Individual View URL parameters
				$view_url  = 'history_view.php?title=SMS%20Details&return='.$currentPage.'&page='.$page.'&type='.$currentPage.'&id='.$aedList[$i]['id'];
				print '<p><b>'.$aedList[$i]['dateCreated'].'</b></p>';
				print '<p>Sent To <a href="'.$server_root.'/midlet/member/'.$view_url.'">'.$aedList[$i]['destination'].'</a></p><br>';
			}
		}
		else
		{
			print '<p>You have no sms entries to view.</p><br>';
		}

	/////////////////////////////////////////
	//Call History
	/////////////////////////////////////////
	}
	else if($currentPage == "call")
	{
		$aedList = soap_call_ejb('getCallHistory', array($username, $rpage, $rentries));
		$page = $aedList[0]['page'];
		$num_pages = $aedList[0]['numPages'];
		$num_entries = $aedList[0]['numEntries'];

		if (sizeof($aedList) > 1)
		{
			for ($i = 1; $i < sizeof($aedList); $i++)
			{
			     //Construct Individual View URL parameters
				$view_url  = 'history_view.php?title=Call%20Details&return='.$currentPage.'&page='.$page.'&type='.$currentPage.'&type='.$currentPage. '&id=' . $aedList[$i]['id'];
				print '<p><b>'.$aedList[$i]['dateCreated'].'</b></p>';
				print '<p>Called to <a href="'.$server_root.'/midlet/member/'.$view_url.'">'.$aedList[$i]['destination'].'</a></p><br>';
			}
		}
		else
		{
			print '<p>You have no call entries to view.</p><br>';
		}

	/////////////////////////////////////////
	//TT Notifications
	/////////////////////////////////////////
	}
	else if($currentPage == "tt")
	{
		$aedList = soap_call_ejb('getMoneyTransferEntries', array($username, $rpage, $rentries));
		$page = $aedList[0]['page'];
		$num_pages = $aedList[0]['numPages'];
		$num_entries = $aedList[0]['numEntries'];

		if (sizeof($aedList) > 1)
		{
			for ($i = 1; $i < sizeof($aedList); $i++)
			{
			    //Construct Individual View URL parameters
				$view_url  = 'history_view.php?title=TT%20Details&return=' . $currentPage . '&page=' . $page . '&type=' . $currentPage . '&type=' . $currentPage . '&id=' . $aedList[$i]['id'];
				print '<p><b>'.$aedList[$i]['dateCreated'].'</b></p>';
				print '<p>Reciept no.: <a href="'.$server_root.'/midlet/member/'.$view_url.'">'.$aedList[$i]['receiptNumber'].'</a></p><br>';
			}
		}
		else
		{
			print '<p>You have no TT entries to view.</p><br>';
		}
	}

	//Navigation
	if (sizeof($aedList) > 1)
	{
		print '<p><center>';
		//The page before
		if ($page > 0){
			print '<a href="'.$server_root.'/midlet/member/history.php?cp='.$currentPage.'&page='.($page-1).'">Previous</a>&nbsp;';
		}

		//The current page out of how many in total
		print ''.($page+1).'&nbsp;';

		//the page after
		if ($hasMore || $page < ($num_pages-1)){
			print '<a href="'.$server_root.'/midlet/member/history.php?cp='.$currentPage.'&page='.($page+1).'">Next</a>&nbsp;';
		}

		print '</center></p><br>';

	}

?>

	<p><a href="<?=$server_root?>/midlet/member/history_index.php">Back</a> &gt;&gt;</p>
	<p><a href="<?=$server_root?>/my/profile/?v=midlet">My Profile</a> &gt;&gt;</p>
	<p><a href="<?=$server_root?>/midlet/member/profile_search_main.php">Search Profiles</a> &gt;&gt;</p>
	<p><a href="<?=$server_root?>/my/photos/?v=midlet">View My Photos</a>&gt;&gt;</p>
	<?php
	if($userDetails->type > 1 && $pf != ''){
	?>
	<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?pf=<?=$pf?>">Back to TT Recharge</a>&gt;&gt;</p>
	<?php
	}
	?>
  </body>
</html>

