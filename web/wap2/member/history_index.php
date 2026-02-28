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
emitTitle("Account History");

?>
		<ul>
			<li><small><a href="history.php?cp=sms">SMS History</a></small></li>
			<li><small><a href="history.php?cp=call">Call History</a></small></li>
			<li><small><a href="history.php?cp=acct">Account History</a></small></li>
			<li><small><a href="history.php?cp=tt">TT Notifications</a></small></li>
		</ul>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>