<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

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
emitTitle("History");

/////////////////////////////////////////
//TT Notifications
/////////////////////////////////////////

$aedList = soap_call_ejb('getMoneyTransferEntries', array($_SESSION['user']['username'], $rpage, $rentries));

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
		$view_url = 'history_view.php?title=TT Details&amp;return='.$currentPage.'&amp;page='.$page.'&amp;type='.$currentPage.'&amp;id=' . $aedList[$i]['id'];
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


if (sizeof($aedList) > 1){
	print '<small><center>';
   	//First Page
   	if ($page > 0){
		print '<a href="history.php?page=0">&lt;&lt;</a>&nbsp;';
   	}

   	//The page before
   	if ($page > 0){
		print '<a href="history.php?page='.($page-1).'">&lt;</a>&nbsp;';
   	}

   	//The current page out of how many in total
  	print ''.($page+1).'/'.($num_pages).'&nbsp;';

   	//the page after
   	if ($page < ($num_pages-1)){
		print '<a href="history.php?page='.($page+1).'">&gt;</a>&nbsp;';
   	}

   	//Last Page
   	if ($page < ($num_pages-1)){
		print '<a href="history.php?page='.($num_pages-1).'">&gt;&gt;</a>';
   	}

   	print '</center></small><br/><br/>';
}

print '<small><a href="recharge_tt.php">Back</a></small><br/><br/>';

?>
	  	<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
