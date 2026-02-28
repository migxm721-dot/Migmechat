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

$step = 1;
$amount = '';
$lastname = '';
$pf = $_GET['pf'];

if(isset($_GET['step'])){
	$step = $_GET['step'];
}

if(isset($_GET['amount'])){
	$amount = $_GET['amount'];
}

if(isset($_GET['yourname'])){
	$yourname = $_GET['yourname'];
}

if(isset($_GET['cn'])){
	$cn = $_GET['cn'];
}
if(isset($_GET['cc'])){
	$cc = $_GET['cc'];
}
if(isset($_GET['ctry'])){
	$ctry = $_GET['ctry'];
}
if(isset($_GET['pr'])){
	$pr = $_GET['pr'];
}

$url = ereg_replace(' ', '%20','pf='.$pf.'&amp;pr='.$pr.'&amp;cn='.$cn.'&amp;cc='.$cc.'&amp;ctry='.$ctry.'&amp;amount='.$amount.'&amp;yourname='.$yourname.'&amp;step='.$step);


emitHeader();
emitTitle("Western Union FAQ");
?>
		<small><b>What does the form require?</b></small><br/>
		<small>Use the GREEN QuickCash form. You enter the informatuon provided when you make a request <a href="recharge_wu.php">here</a>. Ask your Western Union agent for help or email contact@mig33.com.</small><br/><br/>

		<small><b>What is a Photo ID?</b></small><br/>
		<small>An unexpired driver's license, passport or country identity card.</small><br/><br/>

		<small><b>Are there fees?</b></small><br/>
		<small>Agent fee is usually USD$15. mig33 does not charge fees.</small><br/><br/>

		<small><b>What happens if my account is not credited?</b></small><br/>
		<small>Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</small><br/><br/>

		<small><b>Where can I find a Western Union Agent?</b></small><br/>
		<small>Email your country to contact@mig33.com. We will help you.</small><br/><br/>

		<small><a href="recharge_wu.php?<?=$url?>">Back to Western Union</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
	</body>
</html>