<?php
require_once("../../common/common-config.php");

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

$url = ereg_replace(' ', '%20','step='.$step.'&pr='.$pr.'&ah='.$ah.'&bn='.$bn.'&ban='.$ban.'&amount='.$amount.'&yourname='.$yourname.'&ppid='.$ppid.'&pf='.$pf);


?>

<html>
  <head>
    <title>Local Bank Deposit FAQ</title>
  </head>
  <body bgcolor="white">
  	<p><b>What does the form look like?</b></p>
  	<p>You enter the information provided when you make a request <a href="<?=$server_root?>/midlet/member/recharge_bt.php?ppid=<?=$ppid?>">here</a>. Ask the bank staff for help or email contact@mig33.com.</p>
  	<br>
  	<p><b>Are there fees?</b></p>
  	<p>It's usually free. Some banks may charge a small fee. Please ask your bank.</p>
  	<br>
  	<p><b>What happens if my account is not credited?</b></p>
  	<p>Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</p>
  	<br>
  	<p><b>I have my own bank account.</b></p>
  	<p>Tell your bank to transfer funds to the mig33 bank account. Just give them all the same details that we provide you.</p>
  	<br>
  	<p><b>Do i need my own bank account?</b></p>
  	<p>No. You can go to any branch of mig33's bank and they will let you deposit cash.</p>
  	<br>
 	<p><a href="<?=$server_root?>/midlet/member/recharge_bt.php?<?=$url?>">Back to Local Bank Deposit</a> &gt;&gt;</p>
  </body>
</html>