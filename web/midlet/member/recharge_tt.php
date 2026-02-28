<?php
require_once('../../common/common-inc.php');

includeLanguagePack();
ice_check_session();
$userDetails = ice_get_userdata();

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

$goto = $_GET['goto']; //details / notify / history
$yourname ='';
$yourname_error = '';
$amount = '';
$amount_error = '';
$receipt = '';
$receipt_error = '';
$error = '';
$error_message = '';
$notify_success = '';

$moneyTransferData = '';

if ($_POST){
	if (!isset($_POST['yourname'])){
		$yourname_error = 'Enter your name.';
		$error = 'true';
	} else {
		$yourname = $_POST['yourname'];
	}

	if (!isset($_POST['amount'])){
		$amount_error = 'Enter an amount.';
		$error = 'true';
	} else {
		$amount = $_POST['amount'];
		if (!is_numeric($_POST['amount'])){
			$amount_error = 'Enter a number for amount.';
			$error = 'true';
		}
	}

	if (!isset($_POST['receipt'])){
		$receipt_error = 'Enter TT receipt number.';
		$error = 'true';
	} else {
		$receipt = $_POST['receipt'];
	}

	if($error == ''){
		//Contstruct the MoneyTransfer data object
		$moneyTransferData['username'] = $userDetails->username;
		$moneyTransferData['receiptNumber'] = $receipt;
		$moneyTransferData['fullName'] = $yourname;
		$moneyTransferData['amount'] = $amount;
		$moneyTransferData['type'] = 'TELEGRAPHIC_TRANSFER';

		try{
			$status = soap_call_ejb('sendTTNotification', soap_prepare_call($moneyTransferData));
		}catch(Exception $e){
			$error_message = $e->getMessage();
		}

		if ($status != 'TRUE'){
			$error_message = $status;
		} else {
			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(3, $userDetails->username, $amount));
			}catch(Exception $o){}

			if(isset($applicableDiscountTier['discountAmount'])){
				$notify_success = 'You got a '.$applicableDiscountTier['percentageDiscount'].'% discount ('.$applicableDiscountTier['currency'].'$'.round_twodec($applicableDiscountTier['discountAmount'] + $amount).'). New credits will be added to your account after we confirm the receipt of your funds. This may take 3-5 working days.';
			} else {
				$notify_success = 'Money Transfer Notification Received. New credits will be added to your account after we confirm the receipt of your funds. This may take 3-5 working days.';
			}
		}
	}
}

if($goto == ''){
?>
	<html>
		<head>
			<title><?=TT_DEVICE_TITLE?></title>
		</head>
		<body>
		<p><b><?=TT_DEVICE_QUESTION?></b></p>
		<br>
		<p><b>1.</b> <?=TT_DEVICE_STEP_1?></p>
		<p><b>2.</b> <?=TT_DEVICE_STEP_2?></p>
		<p><b>3.</b> <?=TT_DEVICE_STEP_3?></p>
		<p><b>4.</b> <?=TT_DEVICE_STEP_4?></p>
		<p><b>5.</b> <?=TT_DEVICE_STEP_5?></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?goto=details&pf=<?=$pf?>">Get mig33's Account Details</a></p>
		<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?goto=notify&pf=<?=$pf?>">Submit a TT Notification</a></p>
		<p><a href="<?=$server_root?>/midlet/member/history.php?cp=tt&pf=<?=$pf?>">Previous TT Notifications</a></p>
		<br>
		<p>Visit www.mig33.com on the Web for more information.</p>
		<br>
		<?php
			if($pf == 'RO'){
		?>
		<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Back</a></p>
		<?php
			}else if($pf == 'BC'){
		?>
		<p><a href="<?=$server_root?>/midlet/member/buy_credits.php">Back</a></p>
		<?php
			}
		?>
		<p><a href="<?=$server_root?>/midlet/member/tt_help.php">Help</a></p>
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
<?php
}else if($goto == 'details'){
?>
	<html>
		<head>
			<title><?=TT_DEVICE_ACCOUNT_TITLE?></title>
		</head>
		<body>
		<p>Fill in the details below on the form.</p>
		<br>
		<p><?=TT_BENEFICIARY_TITLE?></p>
		<p><b><?=TT_BENEFICIARY_DETAIL?></b></p>
		<p><?=TT_BANK_TITLE?></p>
		<p><b><?=TT_BANK_DETAIL?></b></p>
		<p><?=TT_BANK_ACCOUNT_NUMBER_TITLE?></p>
		<p><b><?=TT_BANK_ACCOUNT_NUMBER_DETAIL?></b></p>
		<p><?=TT_BANK_SWIFTCODE_TITLE?></p>
		<p><b><?=TT_BANK_SWIFTCODE_DETAIL?></b></p>
		<p>Special Instructions:</p>
		<p><b><?=TT_SPECIAL_INSTRUCTION_DETAIL?></b></p>
		<br>
		<p>If your bank requires more information, <a href="<?=$server_root?>/midlet/member/recharge_tt.php?goto=moredetails&amp;pf=<?=$pf?>">click here</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?pf=<?=$pf?>">Back to Telegraphic Transfer</a></p>
		<p><a href="<?=$server_root?>/midlet/member/tt_help.php?goto=details">Help</a></p>
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
<?php
}
else if($goto == "moredetails" )
{
?>
	<html>
		<head>
			<title><?=TT_DEVICE_ACCOUNT_TITLE?></title>
		</head>
		<body>
			<p><?=TT_BENEFICIARY_TITLE?></p>
			<p><b><?=TT_BENEFICIARY_DETAIL?></b></p>
			<p><?=TT_COMPANY_ADDRESS_TITLE?></p>
			<p><b><?=TT_COMPANY_ADDRESS_DETAIL?></b></p>
			<p><?=TT_BANK_TITLE?></p>
			<p><b><?=TT_BANK_DETAIL?></b></p>
			<p><?=TT_BANK_ADDRESS_TITLE?></p>
			<p><b><?=TT_BANK_ADDRESS_DETAIL?></b></p>
			<p><?=TT_ROUTING_AND_TRANSIT_NUMBER_TITLE?></p>
			<p><b><?=TT_ROUTING_AND_TRANSIT_NUMBER_DETAIL?></b></p>
			<p><?=TT_BANK_ACCOUNT_NUMBER_TITLE?></p>
			<p><b><?=TT_BANK_ACCOUNT_NUMBER_DETAIL?></b></p>
			<p><?=TT_BANK_SWIFTCODE_TITLE?></p>
			<p><b><?=TT_BANK_SWIFTCODE_DETAIL?></b></p>
			<p>Special Instructions:</p>
			<p><b><?=TT_SPECIAL_INSTRUCTION_DETAIL?></b></p>
			<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?pf=<?=$pf?>">Back to Telegraphic Transfer</a></p>
			<p><a href="<?=$server_root?>/midlet/member/tt_help.php?goto=details">Help</a></p>
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
<?php
}
else if($goto == 'notify'){
?>
	<html>
		<head>
			<title>Submit TT Notification</title>
		</head>
		<body>
		<?php
			if($notify_success != ''){
				settype($amount, 'double');

				try{
					$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(2, $userDetails->username, $amount));
				}catch(Exception $o){}

				if(!isset($applicableDiscountTier['discountAmount'])){
			?>
				<p>You will get approx* <?=$userDetails->currency?>$<?=$amount?> in credits.</p>
			<?php
				} else {
			?>
				<p>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</p>
			<?php
				}
			?>
				<p>Thank you for submitting a TT notification.</p>
				<p>*The amount credited into your account will vary depending on currency exchange.</p>
				<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?pf=<?=$pf?>">Back to Telegraphic Transfer</a></p>
			<?php
			} else {
				if($error_message != ''){
		?>
			<p style="color:red">There is a problem submitting a TT notification. Please try again. <?=$error_message?></p>
		<?php
				} else {
					//Show input errors if available
					if($yourname_error != ''){
						print '<p style="color:red">*'.$yourname_error.'</p>';
					}

					if($amount_error != ''){
						print '<p style="color:red">*'.$amount_error.'</p>';
					}

					if($receipt_error != ''){
						print '<p style="color:red">*'.$receipt_error.'</p>';
					}
		?>
		<form method="post" action="<?=$server_root?>/midlet/member/recharge_tt.php?goto=notify">
		<input type="hidden" name="pf" value="<?=$pf?>">
		<p>Your Name:</p>
		<p><input type="text" name="yourname" value="<?=$_POST['yourname']?>" size="7" alt="Your Name"></p>
		<p>Amount (<?=$userDetails->currency?>):</p>
		<p><input type="text" name="amount" value="<?=$_POST['amount']?>" alt="Amount"></p>
		<p>TT Receipt Number:</p>
		<p><input name="receipt" type="text" value="<?=$_POST['receipt']?>" size="7" alt="Receipt Number"></p>
		<p><input type="submit" name="Submit" value="Submit"></p>
		</form>
		<?php
				}
		?>
		<p><a href="<?=$server_root?>/midlet/member/recharge_tt.php?pf=<?=$pf?>">Back</a></p>
		<?php
			}
		?>
		<br>

		<p><a href="<?=$server_root?>/midlet/member/tt_help.php?goto=notify">Help</a></p>
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
<?php
}
?>