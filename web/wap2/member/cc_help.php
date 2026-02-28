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
emitHeader();
emitTitle("Credit and Debit Card Help");

?>
		<small>You can recharge your mig33 account via Credit Card. We currently accept only Visa and Mastercard.</small><br/><br/>
		<small>The maximum amount that you can purchase each time is AUD$50. When you recharge, you will see the amount in your local currency, based on an estimated exchange rate.</small><br/><br/>
		<small><a href="recharge_cc.php?pf=<?=$pf?>">Back to Credit and Debit Cards</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>