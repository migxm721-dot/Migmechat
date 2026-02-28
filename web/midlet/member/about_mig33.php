<?php
include_once("../../common/common-inc.php");
include_once("../../common/common-config.php");
ice_check_session();
$userDetails = ice_get_userdata();

$page = $_GET['page'];
$ppid = $_GET['ppid'];
$org = $_GET['org'];

//Check if user is a merchant to begin with
if($userDetails->type == 1){
	//Not a merchant
?>
<html>
	<head>
		<title>Merchant Center</title>
	</head>
	<body>
		<p>This area is restricted to merchants only.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a></p>
		<br>
	</body>
</html>
<?php
}else {
?>
<html>
	<head>
		<title>Merchant Center</title>
	</head>
	<body>
		<br><br>
		<p>mig33 is the first global mobile community, integrating the most popular internet applications
		together for anyone with a mobile phone.</p><br>

		<p>Founded in December 2005, mig33 has quickly spread around the world, growing to millions of
		users in over 200 countries.</p><br>

		<p>mig33 is currently headquartered in Burlingame, California, USA
		and is funded by private investors and venture capital firms.</p><br><br>

		<p>For more tools and information, visit www.mig33.com on the Web and sign in to the Merchant Center.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Back</a></p>
		<p><a href="<?=$server_root?>/midlet/member/center_help.php">Help</a></p>
		<br>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
	</body>
</html>
<?php
}
?>

