<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$step = 1;
$amount = '';
$lastname = '';
$ppid = '';
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
if(isset($_GET['ppid'])){
	$ppid = $_GET['ppid'];
}

if(isset($_GET['pr'])){
	$pr = $_GET['pr'];
}
if(isset($_GET['ah'])){
	$ah = $_GET['ah'];
}
if(isset($_GET['bn'])){
	$bn = $_GET['bn'];
}
if(isset($_GET['ban'])){
	$ban = $_GET['ban'];
}

$url = ereg_replace(' ', '%20','step='.$step.'&amp;pr='.$pr.'&amp;ah='.$ah.'&amp;bn='.$bn.'&amp;ban='.$ban.'&amp;amount='.$amount.'&amp;yourname='.$yourname.'&amp;ppid='.$ppid.'&amp;pf='.$pf);

emitHeader();
emitTitle("Local Bank Deposit FAQ");
?>
		<small><b>What does the form look like?</b></small><br/>
		<small>You enter the information provided when you make a request <a href="recharge_bt.php?ppid=<?=$ppid?>">here</a>. Ask the bank staff for help or email contact@mig33.com.</small><br/><br/>

		<small><b>Are there fees?</b></small><br/>
		<small>It's usually free. Some banks may charge a small fee. Please ask your bank.</small><br/><br/>

		<small><b>What happens if my account is not credited?</b></small><br/>
		<small>Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</small><br/><br/>

		<small><b>I have my own bank account.</b></small><br/>
		<small>Tell your bank to transfer funds to the mig33 bank account. Just give them all the same details that we provide you.</small><br/><br/>

		<small><b>Do i need my own bank account?</b></small><br/>
		<small>No. You can go to any branch of mig33's bank and they will let you deposit cash.</small><br/><br/>

		<small><a href="recharge_bt.php?<?=$url?>">Back to Local Bank Deposit</a></small><br/><br/>

		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
	</body>
</html>