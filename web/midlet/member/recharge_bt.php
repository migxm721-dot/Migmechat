<?php
require_once('../../common/common-inc.php');
includeLanguagePack();
ice_check_session();
$userDetails = ice_get_userdata();
$ppid = '';

$amount = '';
$yourname = '';
$error = '';
$step = 1;
$bankTransferData = '';
$totalWithDiscount = '';
$discount = '';

$pr = '';
$ah = '';
$bn = '';
$ban = '';
$url = '';

$yourname_error = '';
$amount_error = '';

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

//Get the supported currency for local bank deposit type
$currency_lbd = get_currency_lbd($userDetails->countryID);

//Sets the ppid, it should be from a GET or POST
if($_GET){
	if(isset($_GET['ppid'])){
		$ppid = $_GET['ppid'];
	}

	if(isset($_GET['step'])){
		$step = $_GET['step'];
	}

	if(isset($_GET['amount'])){
		$amount = $_GET['amount'];
	}

	if(isset($_GET['yourname'])){
		$yourname = $_GET['yourname'];
	}

	//Next bit is to set variables if there is for the last page
	if(isset($_GET['ah'])){
		$ah = $_GET['ah'];
	}
	if(isset($_GET['bn'])){
		$bn = $_GET['bn'];
	}
	if(isset($_GET['ban'])){
		$ban = $_GET['ban'];
	}
	if(isset($_GET['pr'])){
		$pr = $_GET['pr'];

		//We know here must be a PR so we set the url again
		$url = ereg_replace(' ', '%20','ppid='.$ppid.'&pf='.$pf.'&pr='.$pr.'&ah='.$ah.'&bn='.$bn.'&ban='.$ban.'&amount='.$amount.'&yourname='.$yourname);
	}

}

if($_POST){
	if($_POST['ppid']){
		$ppid = $_POST['ppid'];
	}

	if(isset($_POST['step'])){
		$step = $_POST['step'];
	}

	if(isset($_POST['amount'])){
		$amount = $_POST['amount'];
		if(!is_numeric($amount)){
			$amount_error = 'Enter a number for amount.';
			$error = 'true';
		}

		//TODO: Check if it is higher than minimum value for this payment type
	}

	if(isset($_POST['yourname'])){
		$yourname = $_POST['yourname'];
	}

	//Check if all fields have been filled up
	if($amount == ''){
		$amount_error = 'Enter amount.';
		$error = 'true';
	}

	if($yourname == ''){
		$yourname_error = 'Enter your name.';
		$error = 'true';
	}

	//if no error, continue
	if($error == ''){
		if($step == 2){
			$step = 3;
		}

		if($step == 3){
			if($pr == ''){
				try{
					$countryID = $userDetails->countryID;
					settype($ppid, "int");
					settype($countryID, "int");
					settype($amount, "double");
					$bankTransferData = soap_call_ejb('bankTransfer', array($userDetails->username,$ppid,$countryID,$yourname, '', $amount, $currency_lbd));

					if(isset($bankTransferData['paymentReference'])){
						$pr = $bankTransferData['paymentReference'];
					}
					if(isset($bankTransferData['accountHolder'])){
						$ah = $bankTransferData['accountHolder'];
					}
					if(isset($bankTransferData['bankName'])){
						$bn = $bankTransferData['bankName'];
					}
					if(isset($bankTransferData['bankAccountNumber'])){
						$ban = $bankTransferData['bankAccountNumber'];
					}
				}catch(Exception $e){
					$step = 2;
					$error_message = $e->getMessage();
				}
			}

			$url = ereg_replace(' ', '%20','ppid='.$ppid.'&pf='.$pf.'&pr='.$pr.'&ah='.$ah.'&bn='.$bn.'&ban='.$ban.'&amount='.$amount.'&yourname='.$yourname);
		}
	}
}

if($step == 1){
	//Show Bank Deposit overview
	?>
	<html>
		<head>
			<title><?=BANKTRANSFER_TITLE?></title>
		</head>
		<body>
		<p><b><?=BANKTRANSFER_STEP?></b></p>
		<br>
		<p><b>1.</b> <?=BANKTRANSFER_DEVICE_STEP_1?></p>
		<p><b>2.</b> <?=BANKTRANSFER_DEVICE_STEP_2?></p>
		<p><b>3.</b> <?=BANKTRANSFER_DEVICE_STEP_3?></p>
		<p><b>4.</b> <?=BANKTRANSFER_DEVICE_STEP_4?></p>
		<p><b>5.</b> <?=BANKTRANSFER_DEVICE_STEP_5?></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/recharge_bt.php?step=2&ppid=<?=$ppid?>&pf=<?=$pf?>">Start Local Bank Deposit</a></p>
		<br>
		<p>Visit www.mig33.com on the Web for example forms.</p>
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
		<p><a href="<?=$server_root?>/midlet/member/bt_help.php?ppid=<?=$ppid?>&pf=<?=$pf?>">Help</a></p>
		<br>
		<?php
			if($userDetails->type > 1){
		?>
		<p><a href="<?=$server_root?>/midlet/member/merchant.php">Merchant Center</a> &gt;&gt;</p>
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
}else if($step == 2){
	?>
	<html>
		<head>
			<title>Local Bank Deposit</title>
		</head>
		<body>
			<p>Get bank details.</p>
		<?php
			if(!isset($ppid)){
				print '<p style="color:red">There was some difficulties loading the page. Please try again later.</p>';
				if($pf == 'RO'){
					print '<p><a href="'.$server_root.'/midlet/member/recharge_index.php">Back to Recharge Options</a></p><br>';
				}else if($pf == 'BC'){
					print '<p><a href="'.$server_root.'/midlet/member/buy_credits.php">Back to Buying Credits</a></p><br>';
				}
				print '<p><a href="'.$server_root.'/midlet/member/my_account.php">My Account Home</a></p><br>';
			} else {
				if($error_message != ''){
		?>
			<p style="color:red">*There is an error while requesting for Bank deposit details. <?=$error_message?></p>
		<?php
				}

				//Show if theres any input errors
				if($yourname_error != ''){
					print '<p style="color:red">*'.$yourname_error.'</p>';
				}

				if($amount_error != ''){
					print '<p style="color:red">*'.$amount_error.'</p>';
				}
		?>

			<br>
			<form method="post" action="<?=$server_root?>/midlet/member/recharge_bt.php">
			<input type="hidden" name="ppid" value="<?=$ppid?>">
			<input type="hidden" name="step" value="<?=$step?>">
			<input type="hidden" name="pf" value="<?=$pf?>">
			<p><b>Your Name:</b></p>
			<p><input type="text" name="yourname" value="<?=$yourname?>" size="7" alt="Your Name"></p>
			<p><b>Amount (<?=$currency_lbd?>):</b></p>
			<p><input type="text" name="amount" value="<?=$amount?>" size="7" alt="Amount"></p>
			<p><input type="submit" name="Submit" value="Submit"></p>
			</form>
		<?php
			//Retrieve all discount tiers for this user
			try{
				$discountTiers = soap_call_ejb('getDiscountTiers', array(3, $userDetails->username));

				for ($i = 0; $i < sizeof($discountTiers); $i++){
					//ignore all inactive tiers
					if($discountTiers[$i]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == 30){
						// If the next tier is the same discount % but with a lower min and can still be applied, don't show this one
						if (i < sizeof($discountTiers)-1 && $discountTiers[$i+1]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == $discountTiers[$i+1]['percentageDiscount'] && $discountTiers[$i]['actualMin'] > $discountTiers[$i+1]['actualMin'])
							continue;
		?>
			<p>Get a <?=round_twodec($discountTiers[$i]['percentageDiscount'])?>% discount for <?=$discountTiers[$i]['currency']?>$<?=round_twodec($discountTiers[$i]['displayMin'])?><?php
						if($currency_lbd != $userDetails->currency){
							$approx_inlocal = ($discountTiers[$i]['displayMin'] / get_exchangeRate($currency_lbd)) * get_exchangeRate($userDetails->currency);
							print ' (Approx. '.$userDetails->currency.'$'.round_twodec($approx_inlocal).') or more.<br>* additional discounts available at higher amounts. Contact mig33 to find out more.</p>';
						} else {
							print ' or more.<br>* additional discounts available at higher amounts. Contact mig33 to find out more.</p>';
						}
					}
				}
			}catch(Exception $te){}
		?>
			<br>
			<p><a href="<?=$server_root?>/midlet/member/recharge_bt.php?ppid=<?=$ppid?>&step=1&pf=<?=$pf?>">Back</a></p>
			<p><a href="<?=$server_root?>/midlet/member/bt_help.php?ppid=<?=$ppid?>&pf=<?=$pf?>&step=2">Help</a></p>
			<br>
		<?php
			if($userDetails->type > 1){
		?>
			<p><a href="<?=$server_root?>/midlet/member/merchant.php">Merchant Center</a> &gt;&gt;</p>
		<?php
			} else {
		?>
			<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Recharge Options</a> &gt;&gt;</p>
		<?php
			}
		}
		?>
			<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a> &gt;&gt;</p>
			<br>
		</body>
	</html>
	<?php
}else if($step == 3){
	?>
	<html>
		<head>
			<title>Local Bank Deposit Details</title>
		</head>
		<body>
		<?php
			settype($amount, 'double');

			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(3, $userDetails->username, $amount));
			}catch(Exception $o){}
			/*
			if(!isset($applicableDiscountTier['discountAmount'])){
		?>
			<p>You will get approx* <?=$currency_lbd?>$<?=$amount?> in credits.
		<?php
				if($userDetails->type > 1){
		?>
				If you wanted a discount, go <a href="<?=$server_root?>/midlet/member/recharge_bt.php?ppid=<?=$ppid?>&step=2&yourname=<?=$yourname?>&pf=<?=$pf?>">Back</a> to enter a higher amount.</p>
		<?php
				} else {
				print '</p>';
				}
		?>

		<?php
			} else {
		?>

			<p>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</p>
		<?php
			} */
		?>

			<p>***Write these down***</p>
			<p>Reference Number:</p>
			<p><b><?=$pr?></b></p>
			<p>Account Holder:</p>
			<p><b><?=$ah?></b></p>
			<p>Bank:</p>
			<p><b><?=$bn?></b></p>
			<p>Account Number:</p>
			<p><b><?=$ban?></b></p>
			<p>Your Name:</p>
			<p><b><?=$yourname?></b></p>
			<p>Deposit Amount:</p>
			<p><b><?=$currency_lbd?>$<?=$amount?></b></p>
			<br>
			<p>Go to any <?=$bn?> branch or ask your bank to transfer funds.</p>
			<br>
			<p>In order for your mig33 account to be credited, write your Reference Number on the Bank Deposit Form.</p>
			<br>
			<p>Keep your receipt. We will send you an SMS when we have recharged your account.</p>
			<br>
			<p>*The amount credited into your account will vary depending on currency exchange.</p>
			<br>
		<?php
			if($pf == 'RO'){
		?>
			<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Back to Recharge Options</a></p>
		<?php
			}else if($pf == 'BC'){
		?>
			<p><a href="<?=$server_root?>/midlet/member/buy_credits.php">Back to Buying Credits</a></p>
		<?php
			}
		?>
			<p><a href="<?=$server_root?>/midlet/member/bt_help.php?<?=$url?>&step=3">Help</a></p>
			<br>
		<?php
			if($userDetails->type > 1){
		?>
			<p><a href="<?=$server_root?>/midlet/member/merchant.php">Merchant Center</a> &gt;&gt;</p>
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