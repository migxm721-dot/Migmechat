<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
//include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

ice_check_session();
$userData = ice_get_userdata();

$pf = $_GET['pf'];

//emitHeader();
emitTitle("My Account");

//Get latest account balance
try{
	$balance = soap_call_ejb('getAccountBalance', array($userData->username));
}catch(Exception $e){}
?>
		<center><small>Current Balance: <?=$balance['currency.code']?> <?= number_format($balance['balance'], 2)?></small></center>
		<ul>
			<li><small><a href="recharge.php">Recharge</a></small></li>
			<li><small><a href="history_index.php">Account History</a></small></li>
		<?php
			if($userData->type > 1){
				print '<li><small><a href="../merchant_v2/merchant_center.php?pf=MYACCOUNT">Merchant Center</a></small></li>';
			} else {
				print '<li><small><a href="../merch/merchant.php?pf=MYACCOUNT">Become a Merchant</a></small></li>';
			}
		?>
			<li><small><a href="transfer_credit.php?pf=MYACCOUNT">Transfer Credit</a></small></li>
			<li><small><a href="change_password.php">Change Password</a></small></li>
		</ul>
		<?php
		if($pf == 'HOME'){
		?>
		<small><a href="../member2/t.php?cmd=home">Back</a></small><br/>
		<?php
		}
		?>
		<small><a href="contact.php">Contact Customer Service</a></small><br/>
		<small><a href="../member2/help.php?pf=MYACCOUNT">Help</a></small><br/>
		<br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
  	</body>
</html>
