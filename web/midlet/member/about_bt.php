<?php
include_once("../../common/common-config.php");

$step = 1;
$amount = '';
$lastname = '';
$ppid = '';
if(isset($_GET['ppid'])){
	$ppid = $_GET['ppid'];
}
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

$url = ereg_replace(' ', '%20','step='.$step.'&ppid='.$ppid.'&pr='.$pr.'&ah='.$ah.'&bn='.$bn.'&city='.$city.'&ban='.$ban.'&sc='.$sc.'&amount='.$amount.'&lastname='.$lastname);

//Bank Transfer FAQ main page
?>
<html>
  <head>
    <title>Bank Transfer Help</title>
  </head>
  <body bgcolor="white">
	<p><b>Bank Transfer FAQs:</b></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/bt_faq_whatis.php?<?=$url?>">What is a Bank Transfer?</a></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/bt_faq_useinfo.php?<?=$url?>">How does a Bank Transfer work?</a></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/bt_faq_noacct.php?<?=$url?>">If I don't have a bank account, can I still make a bank transfer?</a></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/bt_faq_why.php?<?=$url?>">Why is the Payment Reference number critical?</a></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/bt_faq_nocredit.php?<?=$url?>">I submitted a Bank Transfer, so why has not my mig33 account been credited?</a></p>
	<br>
	<?php
	if($step == 1){
		print '<p><a href="'.$server_root.'/midlet/member/recharge_bt.php?step=1&ppid='.$ppid.'">Back to Bank Transfer Overview</a> &gt;&gt;</p>';
	}else if($step == 2){
		print '<p><a href="'.$server_root.'/midlet/member/recharge_bt.php?step=2&ppid='.$ppid.'&amount='.$amount.'&lastname='.$lastname.'">Back to Bank Transfer Form</a> &gt;&gt;</p>';
	}else if($step == 3){
		print '<p><a href="'.$server_root.'/midlet/member/recharge_bt.php?step=3&ppid='.$ppid.'&amount='.$amount.'&lastname='.$lastname.'">Back to Verify Bank Transfer</a> &gt;&gt;</p>';
	}else if($step == 4){
		print '<p><a href="'.$server_root.'/midlet/member/recharge_bt.php?'.$url.'">Back to Bank Transfer</a> &gt;&gt;</p>';
	}
	?>
	</body>
</html>
