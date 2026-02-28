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
emitTitle("Change Password Help");

?>
		<small>this is password_help.php Need content.</small><br/>
		<br/>
		<small><a href="change_password.php">Back to Changing Password</a></small><br/>
		<br/>
	</body>
</html>