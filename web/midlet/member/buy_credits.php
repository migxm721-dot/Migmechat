<?php
include_once("../../common/common-inc.php");
include_once("../../common/common-config.php");

ice_check_session();
$userDetails = ice_get_userdata();
$countryID = '';
$ppid = '';
$from = "";
if(isset($_GET['from']) )
	$from = $_GET['from'];
?>

<html>
	<head>
		<title>Buying Credits</title>
	</head>
	<body>
		<p>Select an option to buy discount credits.</p>
		<?php
			try{
				if($ppid == ''){
					//Check if user's country of origin can support Bank transfer
					$countryID = $userDetails->countryID;
					settype($countryID, "int");

					$ppid = soap_call_ejb('getBankTransferProductID', array($countryID));
				}

				if($ppid != '0'){
		?>
		<p>&gt;<a href="<?=$server_root?>/midlet/member/recharge_bt.php?ppid=<?=$ppid?>&pf=BC">Local Bank Deposit</a> (send money to our bank in your country)</p>
		<?php
				}
			}catch(Exception $e){
				//echo $e->getMessage();
			}
		?>
		<p>&gt;<a href="<?=$server_root?>/midlet/member/recharge_wu.php?pf=BC">Western Union</a> (pay with cash)</p>
		<p>&gt;<a href="<?=$server_root?>/midlet/member/recharge_tt.php?pf=BC">Telegraphic Transfer</a></p>
		<p>&gt;<a href="<?=$server_root?>/midlet/member/recharge_mb.php?pf=BC">Moneybookers</a></p>
		<p>&gt;<a href="<?=$server_root?>/midlet/member/recharge_cc.php?pf=BC">Credit and Debit Cards</a></p>
		<br>
<?php
		if($from == "store")
		{
?>
			<p><a href="<?=$server_root?>/midlet/member/store.php">Back to Store</a></p>
<?php
		}
		else
		{
?>
			<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Back</a></p>
<?php
		}
?>

		<p><a href="<?=$server_root?>/midlet/member/center_help.php">Help</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
	</body>
</html>