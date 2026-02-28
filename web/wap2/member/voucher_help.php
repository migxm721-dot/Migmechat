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

emitHeader();
emitTitle("Voucher Help");
?>
		<small>You can recharge your mig33 account using mig33 vouchers.  You can purchase vouchers from a merchant in your city or from our prepaid card distributors.  For a merchant in your city or a prepaid card distributor, email customer service at contact@mig33.com.</small><br/>
		<br/>
		<small><a href="recharge_voucher.php">Back to Voucher</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
	</body>
</html>