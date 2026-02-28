<?php
session_start();
include_once("../../common/common-inc.php");
ice_check_session();
fast_require('UserDetail', get_domain_directory() . '/user/user_detail.php');
$userDetails = new UserDetail(ice_get_userdata());

//Get latest account balance
try{
	$balance = soap_call_ejb('getAccountBalance', array($userDetails->username));
	$mobilePhone = soap_call_ejb('getPreviousMobileNumber', array($userDetails->username));
}catch(Exception $e){}
?>

<html>
  <head>
    <title>My Account</title>
  </head>
  <body bgcolor="white">
	<p>Current Balance (<?=$balance['currency.code']?>):<br>
	$<?=number_format($balance['balance'],2)?></p>
	<br>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/recharge_index.php">Recharge</a></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/history_index.php">Account History</a></p>
	<?php
		//Check if the user is already a merchant, show merchant center if so, or else link to merchant registeration
		/* if($userDetails->type > 1){           // ?pagenum='.($totalPages).'&bid='.$bid.'"

			try
			{
				$countryData = get_country_from_ip(getRemoteIPAddress());
			}
			catch(Exception $e){}

			$cname = str_replace(' ', '', $countryData['name']);
			$uname = $userDetails->username;

			print '<p>&gt;<a href="'.$server_root.'/wap2/merchant_v2/merchant_center.php?output=pagelet&tusername='.$uname.'&tcountry='.$cname.'">Merchant Center</a></p>';
		} else {
			print '<p>&gt;<a href="'.$server_root.'/midlet/member/merchant.php">Become a merchant</a></p>';
		} */
	?>
	<p>&gt;<a href="<?=$server_root?>/sites/index.php?c=merchant&a=dashboard&v=midlet"><?= $userDetails->type > 1 ? 'Merchant Center':'Become a Merchant' ?></a></p>
	<p>&gt;<a href="<?=$server_root?>/sites/index.php?c=account&v=midlet&a=transfer_credit">Transfer Credits</a></p>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/change_password.php">Change Password</a></p>
	<?php
		if( $userDetails->is_mobile_verified() == 1 )
		{
	?>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/view_phone_options.php?type=1">Change Mobile Phone Number</a></p>
	<?php
		}
	?>
	<?php
		if( !empty($mobilePhone) )
		{
	?>
	<p>&gt;<a href="<?=$server_root?>/midlet/member/view_phone_options.php?type=2">Cancel Previous Mobile Phone Number Change</a></p>
	<?php
		}
	?>
	<p>&gt;<a href="<?=$server_root?>/sites/index.php?c=subscription&v=midlet&a=home">Subscriptions</a></p>
	<?php if( in_array($userDetails->type, array(2,3)) ): ?>
		<p>&gt;<a href="<?=$server_root?>/sites/index.php?c=merchant&v=midlet&a=create_pin">Create a secure PIN</a></p>
		<a href="<?=$server_root?>/sites/index.php?c=merchant&v=midlet&a=recover_pin">Recover Secure PIN</a>
	<?php endif ?>
<?php
/*
	$headers = apache_request_headers();
	$midletVersion = $headers['ver'];
	if (is_numeric($midletVersion)) {
		if ($midletVersion >= 3.05) {
			print '<p>&gt;<a href="store.php">mig33 Store</a></p>';
		}
	}
*/
?>
	<br>
  </body>
</html>
