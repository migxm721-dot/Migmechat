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
emitTitle("Create User Help");

?>
		<small>this is create_help.php Need content.</small><br/>
		<br/>
		<small><a href="merchant_createuser.php?sm=<?=$_GET['sm']?>&amp;mobile=<?=$_GET['mobile']?>&amp;username=<?=$_GET['username']?>&amp;success=<?=$_GET['success']?>">Back to Create User</a></small><br/>
		<br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
  	</body>
</html>