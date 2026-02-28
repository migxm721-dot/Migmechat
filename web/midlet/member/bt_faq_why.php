<?php
require_once("../../common/common-config.php");

$step = 1;
$amount = '';
$lastname = '';
$ppid = '';
if(isset($_GET['step'])){
	$step = $_GET['step'];
}
if(isset($_GET['amount'])){
	$amount = $_GET['amount'];
}
if(isset($_GET['lastname'])){
	$lastname = $_GET['lastname'];
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
if(isset($_GET['city'])){
	$city = $_GET['city'];
}
if(isset($_GET['ban'])){
	$ban = $_GET['ban'];
}
if(isset($_GET['sc'])){
	$sc = $_GET['sc'];
}

$url = ereg_replace(' ', '%20','step='.$step.'&pr='.$pr.'&ah='.$ah.'&bn='.$bn.'&city='.$city.'&ban='.$ban.'&sc='.$sc.'&amount='.$amount.'&lastname='.$lastname);


?>

<html>
  <head>
    <title>Bank Transfer</title>
  </head>
  <body bgcolor="white">
  	<p><b>Why is the payment reference number critical?</b></p>
	<p>The 12-digit Payment Reference number (the one that shows up in red when we send it) is critical as it allows our payment processing partner (GlobalCollect) to match your payment to your order. Once successfully matched, GlobalCollect will notify us, which can take up to a week, and we'll then credit your account. Without that number, there's no way to link up the entire process and move the money around.</p>
 <p>If you omit the payment reference number, or enter an incorrect one, it will delay the process or prevent GlobalCollect from successfully matching your payment.
</p>
	<p><a href="<?=$server_root?>/midlet/member/recharge_bt.php?<?=$url?>">Back to Bank Transfer Details</a> &gt;&gt;</p>
  </body>
</html>