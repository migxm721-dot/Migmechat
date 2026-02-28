<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
include_once("../../includes/find_merchant.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];

//Check async messages
checkServerSessionStatus();

ice_check_session();
$userDetails = ice_get_userdata();
$ppid = '';

emitHeader();
emitTitle("Find a Local Store / Merchant");

showMerchantLocator($userDetails->countryID, "WAP", true);
?>
		<br/>
		<small><a href="recharge_help.php">Help</a></small><br/>
  		<small><a href="index.php">My Account Home</a></small><br/>
  		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
  		<br/>
	</body>
</html>
