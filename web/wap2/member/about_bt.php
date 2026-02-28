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
$pf = $_GET['pf'];
ice_check_session();



$step = 1;
$amount = '';
$lastname = '';
$ppid = '';
if(isset($_GET['step'])){
	$step = $_GET['step'];
}
if(isset($_GET['amount'])){
	$amount = $_GET['amount'];
}
if(isset($_GET['lastname'])){
	$lastname = $_GET['lastname'];
}
if(isset($_GET['ppid'])){
	$ppid = $_GET['ppid'];
}

if(isset($_GET['pr'])){
	$pr = $_GET['pr'];
}
if(isset($_GET['ah'])){
	$ah = $_GET['ah'];
}
if(isset($_GET['bn'])){
	$bn = $_GET['bn'];
}
if(isset($_GET['city'])){
	$city = $_GET['city'];
}
if(isset($_GET['ban'])){
	$ban = $_GET['ban'];
}
if(isset($_GET['sc'])){
	$sc = $_GET['sc'];
}

$url = ereg_replace(' ', '%20','step='.$step.'&pr='.$pr.'&ah='.$ah.'&bn='.$bn.'&city='.$city.'&ban='.$ban.'&sc='.$sc.'&amount='.$amount.'&lastname='.$lastname);

emitHeader();
emitTitle("Bank Transfer Help");

//Bank Transfer FAQ main page
?>
	<ol>
		<li><small><a href="bt_faq_whatis.php?<?=$url?>">What's a Bank Transfer?</a></small></li>
		<li><small><a href="bt_faq_useinfo.php?<?=$url?>">How does a Bank Transfer work?</a></small></li>
		<li><small><a href="bt_faq_noacct.php?<?=$url?>">If I don&#39;t have a bank account, can I still make a bank transfer?</a></small></li>
		<li><small><a href="bt_faq_why.php?<?=$url?>">Why's the Payment Reference number critical?</a></small></li>
		<li><small><a href="bt_faq_nocredit.php?<?=$url?>">I submitted a Bank Transfer, so why hasn&#39;t my mig33 account been credited?</a></small></li>
	</ol>
	<?php
	if($step == 1){
		print '<small><a href="recharge_bt.php?step=1&ppid='.$ppid.'">Back to Bank Transfer Overview</a></small><br/>';
	} else {
		print '<small><a href="recharge_bt.php?step='.$step.'&ppid='.$ppid.'&amount='.$amount.'&lastname='.$lastname.'">Back to Bank Transfer</a></small><br/>';
	}
	?>
	<br/>
	<small><a href="index.php">My Account Home</a></small><br/>
	<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
	<small><a href="logout.php">Logout</a></small><br/>
	<br/>
  </body>
</html>

