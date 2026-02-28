<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");

ice_check_session();
$userDetails = ice_get_userdata();

	$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}
?>
<html>
	<head><title>Premium SMS</title></head>
	<body>
		<small><b>How do I add credits with a Premium SMS?</b></small><br/>
		<p><small>This is only available to to Banglalink mobile customers in Bangladesh.</small></p>
		<p><small>Open an SMS text message box, and write 'content' as a new message. Then send to short code 7575.  After you wil receive an SMS with a recharge code. The code will add Taka 15 to your account.</small></p>
		<p><small>To add the credit value, login to mig33 and find the 'My Accounts/Recharge/Add credits/' area, and click on 'Redeem a Voucher.' Enter the code when prompted. Your mig33 account will be added with Taka 15.</small></p>
		<br/>
		<small>Visit www.mig33.com on the Web for more information.</small><br/><br/>
		<?php
			if($pf == 'RO'){
		?>
		<small><a href="recharge.php">Return to Recharge Options</a></small><br/>
		<?php
			}else if($pf == 'BC'){
		?>
		<small><a href="buy_credits.php">Return to Buying Credits</a></small><br/>
		<?php
			}
		?>
		<br/>
		<?php
			if($userDetails->type > 1){
		?>
		<small><a href="merchant_center.php">Merchant Center</a></small><br/>
		<?php
			} else {
		?>
		<small><a href="recharge.php">Recharge Options</a></small><br/>
		<?php
			}
		?>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
	</body>
</html>