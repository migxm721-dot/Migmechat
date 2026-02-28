<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$pf = $_GET['pf']; //Page from, tracks where user is coming from.
if($pf == ''){
	$pf = $_POST['pf'];
}

$success = $_GET['success'];
$transferto = $_GET['transferto'];
$amount = $_GET['amount'];

emitHeader();
emitTitle("Transfer Credit Help");
?>
		<small>Transfer credit from your account to your buddy's mig33 account. Select the amount, username and transfer the credit.</small><br/>
		<br/>
		<small><a href="transfer_credit.php?pf=<?=$pf?>&amp;transferto=<?=$transferto?>&amp;amount=<?=$amount?>&amp;success=<?=$success?>">Back to Transfer Credits</a></small><br/>
		<br/>
  	</body>
</html>