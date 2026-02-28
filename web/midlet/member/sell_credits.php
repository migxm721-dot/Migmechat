<?php
include_once("../../common/common-inc.php");
ice_check_session();
$userDetails = ice_get_userdata();

//TODO:Check if user is a merchant
?>

<html>
	<head>
		<title>Selling Credits</title>
	</head>
	<body>
		<p>These tools include setting up your customers and selling your credits to them.</small></p><br>
		<br>
		<p><b>BUILDING YOUR CUSTOMER BASE</b></p>
		<br>

		<ul>
			<li><p><a href="<?=$server_root?>/midlet/member/merch_create_user.php">Create User</a></p></li>
			<li><p><a href="<?=$server_root?>/midlet/member/invite_customer_intro.php">Invite Customers</a></p></li>
		</ul>
		<br><br>
		<p><b>SELLING YOUR CREDITS TO YOUR CUSTOMERS</b></p>
		<br>

		<ul>
			<li><p><a href="<?=$server_root?>/midlet/member/transfer_credit_intro.php">Transfer Credits</a></p></li>
			<li><p><a href="<?=$server_root?>/midlet/member/free_advertising_intro.php">Free Advertising</a></p></li>
		</ul>
		<br><br>
		<p>View <a href="<?=$server_root?>/midlet/member/popular_rates.php?pf=SC">call rates</a> and <a href="<?=$server_root?>/midlet/member/sales_kit.php?page=mktg">sales tips</a>.</p>
		<br><br>

		<p><a href="<?=$server_root?>/midlet/member/getting_started.php">Back</a></p>
		<p><a href="<?=$server_root?>/midlet/member/center_help.php">Help</a></p>
		<br>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Center</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
	</body>
</html>