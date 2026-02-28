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
$url = ereg_replace(' ', '%20','pf='.$pf.'&pr='.$pr.'&cn='.$cn.'&cc='.$cc.'&ctry='.$ctry.'&amount='.$amount.'&yourname='.$yourname.'&step='.$step);




?>

<html>
  <head>
    <title>Western Union FAQ</title>
  </head>
  <body bgcolor="white">
  	<p><b>What does the form require?</b></p>
  	<p>Use the GREEN QuickCash Form. You enter the informatuon provided when you make a request <a href="<?=$server_root?>/midlet/member/recharge_wu.php">here</a>. Ask your Western Union agent for help or email contact@mig33.com.</p>
  	<br>
  	<p><b>What is a Photo ID?</b></p>
  	<p>An unexpired driver's license, passport or country identity card.</p>
  	<br>
  	<p><b>Are there fees?</b></p>
  	<p>Agent fee is usually USD$15. mig33 does not charge fees.</p>
  	<br>
  	<p><b>What happens if my account is not credited?</b></p>
  	<p>Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</p>
  	<br>
  	<p><b>Where can I find a Western Union Agent?</b></p>
  	<p>Email your country to contact@mig33.com. We will help you.</p>
  	<br>
 	<p><a href="<?=$server_root?>/midlet/member/recharge_wu.php?<?=$url?>">Back to Western Union</a> &gt;&gt;</p>
 	<br>
  </body>
</html>