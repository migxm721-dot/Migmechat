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
emitTitle("buying Credits Help");

?>
		<small>this is bc_help.php Need content.</small><br/>
		<br/>
		<small><a href="buy_credits.php?pf=<?=$pf?>">Back to Buying Credits</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
  	</body>
</html>