<?php
require_once('../../common/common-inc.php');
includeLanguagePack();
ice_check_session();
$userDetails = ice_get_userdata();
$headers = apache_request_headers();
?>

<html>
		<head>
			<title><?=MONEYBOOKERS_TITLE?></title>
		</head>
		<body>
		<p><b> <?=MONEYBOOKERS_STEP?></b></p>
		<br>
		<p><b>1.</b> <?=MONEYBOOKERS_DEVICE_STEP_1?></p>
		<p><b>2.</b> <?=MONEYBOOKERS_DEVICE_STEP_2?></p>
		<p><b>3.</b> <?=MONEYBOOKERS_DEVICE_STEP_3?></p>
		<p><b>4.</b> <?=MONEYBOOKERS_DEVICE_STEP_4?></p>
		<p><b>5.</b> <?=MONEYBOOKERS_DEVICE_STEP_5?></p>
		<p><b>6.</b> <?=MONEYBOOKERS_DEVICE_STEP_6?></p>
		<p><b>7.</b> <?=MONEYBOOKERS_DEVICE_STEP_7?></p>
		<br>
		<p>We will send you an SMS when we have recharged your account (1-3 days).</p>
		<br>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Back</a></p>
		<br>
		<br>
		<?php
			if($userDetails->type > 1){
		?>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Center</a> &gt;&gt;</p>
		<?php
			} else {
		?>
		<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Recharge Options</a> &gt;&gt;</p>
		<?php
			}
		?>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a> &gt;&gt;</p>
		<br>
		</body>
</html>
