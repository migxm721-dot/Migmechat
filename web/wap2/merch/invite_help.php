<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

emitHeader();
emitTitle("Invite Customer Help");

$success = $_GET['success'];

if(isset($_GET['name'])){
	$name = $_GET['name'];
}

if(isset($_GET['mobile'])){
	$mobile = $_GET['mobile'];
}

//Construct URL back link
$url = 'name='.$name.'&amp;mobile='.$mobile;

//Strip blank space
$url = ereg_replace(' ', '%20', $url);

?>
		<small>this is invite_help.php Need content.</small><br/>
		<br/>
		<small><a href="invite_customers.php?<?=$url?>&amp;success=<?=$success?>">Back to Invite Customers</a></small><br/>
		<br/>
	</body>
</html>