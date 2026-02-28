<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

ice_check_session();
$userDetails = ice_get_userdata();

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

//Settings for
$rpage = 0;
$rentries = 6;

if ($_GET['page']){
	$rpage = $_GET['page'];
	settype($rpage, 'int');
}if ($_GET['entries']){
	$rentries = $_GET['entries'];
	settype($rentries, 'int');
}

emitHeader();

//DETECT WHAT PAGE TO SHOW
$currentPage = "details";
if($_GET["cp"] == "sms")
	$currentPage = "sms";
else if($_GET["cp"] == "call")
	$currentPage = "call";
else if($_GET["cp"] == "acct")
	$currentPage = "acct";
else if($_GET["cp"] == "tt")
	$currentPage = "tt";

/////////////////////////////////////////
//ACCOUNT
/////////////////////////////////////////
if($currentPage == "acct"){
	emitTitle("Account History");
	$aedList = soap_call_ejb('getAccountEntries', array($userDetails->username, $rpage, $rentries));

	$page = $aedList[0]['page'];
	$hasMore = $aedList[0]['hasMore'];

	if (sizeof($aedList) > 1){
?>
			<table align="center" columns="3">
				<tr>
					<td><b><small>Date</small></b></td>
					<td><b><small>Type</small></b></td>
					<td><b><small>Amt</small></b></td>
				</tr>
<?php
		for ($i = 1; $i < sizeof($aedList); $i++){
			//Construct Individual View URL parameters
			$view_url  = 'history_view.php?pf='.$pf.'&amp;title=Transaction Details&amp;return='.$currentPage.'&amp;page='.$page.'&amp;type='.$currentPage.'&amp;id='.$aedList[$i]['id'];
?>
				<tr>
					<td><small><?=$aedList[$i]['dateCreated']?></small></td>
					<td><small><a href="<?=$view_url?>"><?=get_short_acc_description($aedList[$i]['type'])?></a></small></td>
					<td><small><?=abs($aedList[$i]['amount'])?>&nbsp;<?=$aedList[$i]['currency']?></small></td>
				</tr>
<?php
		}
?>
			</table>
<?php
	} else {
		print '<small>You have no account entries to view</small><br/><br/>';
	}
/////////////////////////////////////////
//SMS
/////////////////////////////////////////
}else if($currentPage == "sms"){
	emitTitle("SMS History");
	$aedList = soap_call_ejb('getSMSHistory', array($userDetails->username, $rpage, $rentries));

	$page = $aedList[0]['page'];
	$num_pages = $aedList[0]['numPages'];
	$num_entries = $aedList[0]['numEntries'];

	if (sizeof($aedList) > 1){
?>
			<table align="center" columns="2">
				<tr>
					<td><b><small>Date</small></b></td>
					<td><b><small>Destination</small></b></td>
				</tr>
<?php
		for ($i = 1; $i < sizeof($aedList); $i++){
			//Construct Individual View URL parameters
			$view_url  = 'history_view.php?pf='.$pf.'&amp;title=SMS Details&amp;return='.$currentPage.'&amp;page='.$page.'&amp;type='.$currentPage.'&amp;id='.$aedList[$i]['id'];
?>
				<tr>
					<td><small><?=$aedList[$i]['dateCreated']?></small></td>
					<td><small><a href="<?=$view_url?>"><?=$aedList[$i]['destination']?></a></small></td>
				</tr>
<?php
		}
?>
			</table>
<?php
	} else {
		print '<small>You have no sms entries to view</small><br/><br/>';
	}
/////////////////////////////////////////
//Call History
/////////////////////////////////////////
}else if($currentPage == "call"){
	emitTitle("Call History");
	$aedList = soap_call_ejb('getCallHistory', array($userDetails->username, $rpage, $rentries));

	$page = $aedList[0]['page'];
	$num_pages = $aedList[0]['numPages'];
	$num_entries = $aedList[0]['numEntries'];

	if (sizeof($aedList) > 1){
?>
			<table align="center" columns="2">
				<tr>
					<td><b><small>Date</small></b></td>
					<td><b><small>Destination</small></b></td>
				</tr>
<?php
		for ($i = 1; $i < sizeof($aedList); $i++){
			//Construct Individual View URL parameters
			$view_url = 'history_view.php?pf='.$pf.'&amp;title=Call Details&amp;return='.$currentPage.'&amp;page='.$page.'&amp;type='.$currentPage.'&amp;type='.$currentPage.'&amp;id='.$aedList[$i]['id'];
?>
				<tr>
					<td><small><?=$aedList[$i]['dateCreated']?></small></td>
					<td><small><a href="<?=$view_url?>"><?=$aedList[$i]['destination']?></a></small></td>
				</tr>
<?php
		}
?>
			</table>
<?php
	} else {
		print '<small>You have no call entries to view</small><br/><br/>';
	}
/////////////////////////////////////////
//TT Notifications
/////////////////////////////////////////
}else if($currentPage == "tt"){
	emitTitle("TT History");
	$aedList = soap_call_ejb('getMoneyTransferEntries', array($userDetails->username, $rpage, $rentries));

	$page = $aedList[0]['page'];
	$num_pages = $aedList[0]['numPages'];
	$num_entries = $aedList[0]['numEntries'];

	if (sizeof($aedList) > 1){
?>
			<table align="center" columns="2">
				<tr>
					<td><b><small>Date</small></b></td>
					<td><b><small>Receipt Number</small></b></td>
				</tr>
<?php
		for ($i = 1; $i < sizeof($aedList); $i++){
			//Construct Individual View URL parameters
			$view_url = 'history_view.php?pf='.$pf.'&amp;title=TT Details&amp;return='.$currentPage.'&amp;page='.$page.'&amp;type='.$currentPage.'&amp;type='.$currentPage.'&amp;id=' . $aedList[$i]['id'];
?>
				<tr>
					<td><small><?=$aedList[$i]['dateCreated']?></small></td>
					<td><small><a href="<?=$view_url?>"><?=$aedList[$i]['receiptNumber']?></a></small></td>
				</tr>
<?php
		}
?>
			</table>
<?php
	} else {
		print '<small>You have no TT entries to view</small><br/><br/>';
	}
}

if (sizeof($aedList) > 1){
	print '<center>';
   	//The page before
   	if ($page > 0){
		print '<small><a href="history.php?pf='.$pf.'&amp;cp='.$currentPage.'&amp;page='.($page-1).'">Previous</a>&nbsp;</small>';
   	}

   	//The current page out of how many in total
  	print '<small>'.($page+1).'&nbsp;</small>';

   	//the page after
   	if ($hasMore || $page < ($num_pages-1)){
		print '<small><a href="history.php?pf='.$pf.'&amp;cp='.$currentPage.'&amp;page='.($page+1).'">Next</a>&nbsp;</small>';
   	}

   	print '</center>';
}

if($pf == 'RO' || $pf == 'BC'){
	//Assume its from TT notification
	print '<small><a href="recharge_tt.php?pf='.$pf.'">Back</a></small><br/><br/>';
} else {
	//Back to Account History
	print '<small><a href="history_index.php">Back</a></small><br/><br/>';
}
?>
	  <small><a href="index.php">My Account</a></small><br/>
	  <small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
	  <small><a href="logout.php">Logout</a></small><br/>
	  <br/>
	</body>
</html>
