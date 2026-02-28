<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

emitHeader();
emitTitle("Create User Help");

$success = $_GET['success'];

if(isset($_GET['username'])){
	$username = $_GET['username'];
}

if(isset($_GET['mobile'])){
	$mobile = $_GET['mobile'];
}

//Construct URL back link
$url = 'username='.$username.'&amp;mobile='.$mobile;

//Strip blank space
$url = ereg_replace(' ', '%20', $url);

?>
		<small>this is create_help.php Need content.</small><br/>
		<br/>
		<small><a href="merchant_createuser.php?<?=$url?>&amp;success=<?=$success?>">Back to Create User</a></small><br/>
		<br/>
	</body>
</html>