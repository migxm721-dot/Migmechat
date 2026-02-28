<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
includeLanguagePack();
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];

//Check async messages
checkServerSessionStatus();

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
	if (!empty($_POST['yourname'])){
		$yourname = $_POST['yourname'];
	}

	if($yourname == ''){
		$yourname_error = 'Enter your name.';
		$error = 'true';
	}

	if (!empty($_POST['amount'])){
		$amount = $_POST['amount'];
		if (!is_numeric($amount)){
			$amount_error = 'Enter a number for amount.';
			$error = 'true';
		}
	}

	if($amount == '' && $amount_error == ''){
		$amount_error = 'Enter amount.';
		$error = 'true';
	}

	if (isset($_POST['receipt'])){
		$receipt = $_POST['receipt'];
	}

	if($receipt == ''){
		$receipt_error = 'Enter TT receipt number.';
		$error = 'true';
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

emitHeader();
if($goto == ''){
	emitTitle("Telegraphic Transfer");
?>
			<small><b><?=TT_DEVICE_QUESTION?></b></small><br/>
			<ol>
				<li><small><?=TT_DEVICE_STEP_1?></small></li>
				<li><small><?=TT_DEVICE_STEP_2?></small></li>
				<li><small><?=TT_DEVICE_STEP_3?></small></li>
				<li><small><?=TT_DEVICE_STEP_4?></small></li>
				<li><small><?=TT_DEVICE_STEP_5?></small></li>
			</ol>
			<small><a href="recharge_tt.php?goto=details&amp;pf=<?=$pf?>">Get mig33's Account Details</a></small><br/>
			<small><a href="recharge_tt.php?goto=notify&amp;pf=<?=$pf?>">Submit a TT Notification</a></small><br/>
			<small><a href="history.php?cp=tt&amp;pf=<?=$pf?>">Previous TT Notifications</a></small><br/>
			<br/>
			<small>Visit www.mig33.com on the Web for more information.</small><br/><br/>
			<?php
				if($pf == 'RO'){
			?>
			<small><a href="recharge.php">Return to Recharge Options</a></small><br/>
			<?php
				}else if($pf == 'BC'){
			?>
			<small><a href="buy_credits.php">Return to Buying Credits</a></small><br/>
			<?php
				}
			?>
			<small><a href="tt_help.php?pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<?php
				if($userDetails->type > 1){
			?>
			<small><a href="merchant_center.php">Merchant Center</a></small><br/>
			<?php
				} else {
			?>
			<small><a href="recharge.php">Recharge Options</a></small><br/>
			<?php
				}
			?>
			<small><a href="index.php">My Account Home</a></small><br/>
			<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
<?php
}else if($goto == 'details'){
	emitTitle("TT Account Details");
?>
			<small>Fill in the details below on the form.</small><br/><br/>
			<small><?=TT_BENEFICIARY_TITLE?></small><br/>
			<small><b><?=TT_BENEFICIARY_DETAIL?></b></small><br/><br/>
			<small><?=TT_BANK_TITLE?></small><br/>
			<small><b><?=TT_BANK_DETAIL?></b></small><br/><br/>
			<small><?=TT_BANK_ACCOUNT_NUMBER_TITLE?></small><br/>
			<small><b><?=TT_BANK_ACCOUNT_NUMBER_DETAIL?></b></small><br/><br/>
			<small><?=TT_BANK_SWIFTCODE_TITLE?></small><br/>
			<small><b><?=TT_BANK_SWIFTCODE_DETAIL?></b></small><br/><br/>
			<small>Special Instructions:</small><br/>
			<small><b><?=TT_SPECIAL_INSTRUCTION_DETAIL?></b></small><br/><br/>
			<small>If your bank requires more information, <a href="recharge_tt.php?goto=moredetails&amp;pf=<?=$pf?>">click here</a></small><br/>
			<small><a href="recharge_tt.php?pf=<?=$pf?>">Back</a></small><br/>
			<small><a href="tt_help.php?goto=details&amp;pf=<?=$pf?>">Help</a></small><br/>
			<br/>

			<?php
				if($userDetails->type > 1){
			?>
			<small><a href="merchant_center.php">Merchant Center</a></small><br/>
			<?php
				} else {
			?>
			<small><a href="recharge_index.php">Recharge Options</a></small><br/>
			<?php
				}
			?>
			<small><a href="index.php">My Account Home</a></small><br/>
			<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
		</body>
	</html>
<?php
}
else if( $goto == "moredetails")
{
	emitTitle("TT Account Details");
?>
	<small><?=TT_BENEFICIARY_TITLE?></small><br/>
	<small><b><?=TT_BENEFICIARY_DETAIL?></b></small><br/><br/>
	<small><?=TT_COMPANY_ADDRESS_TITLE?></small><br/>
	<small><b><?=TT_COMPANY_ADDRESS_DETAIL?></b></small><br/><br/>
	<small><?=TT_BANK_TITLE?></small><br/>
	<small><b><?=TT_BANK_DETAIL?></b></small><br/><br/>
	<small><?=TT_BANK_ADDRESS_TITLE?></small><br/>
	<small><b><?=TT_BANK_ADDRESS_DETAIL?></b></small><br/><br/>
	<small><?=TT_ROUTING_AND_TRANSIT_NUMBER_TITLE?></small><br/>
	<small><b><?=TT_ROUTING_AND_TRANSIT_NUMBER_DETAIL?></b></small><br/>
	<small><?=TT_BANK_ACCOUNT_NUMBER_TITLE?></small><br/>
	<small><b><?=TT_BANK_ACCOUNT_NUMBER_DETAIL?></b></small><br/><br/>
	<small><?=TT_BANK_SWIFTCODE_TITLE?></small><br/>
	<small><b><?=TT_BANK_SWIFTCODE_DETAIL?></b></small><br/><br/>
	<small>Special Instructions:</small><br/>
	<small><b><?=TT_SPECIAL_INSTRUCTION_DETAIL?></b></small><br/><br/>
	<small><a href="recharge_tt.php?pf=<?=$pf?>">Back</a></small><br/>
	<small><a href="tt_help.php?goto=details&amp;pf=<?=$pf?>">Help</a></small><br/>
	<br/>

	<?php
		if($userDetails->type > 1){
	?>
	<small><a href="merchant_center.php">Merchant Center</a></small><br/>
	<?php
		} else {
	?>
	<small><a href="recharge_index.php">Recharge Options</a></small><br/>
	<?php
		}
	?>
	<small><a href="index.php">My Account Home</a></small><br/>
	<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
	<small><a href="logout.php">Logout</a></small><br/>
	</body>
</html>

<?php
}
else if($goto == 'notify'){
	emitTitle("Submit TT Notification");
			if($notify_success != ''){
				settype($amount, 'double');

				try{
					$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(2, $userDetails->username, $amount));
				}catch(Exception $o){}

				if(!isset($applicableDiscountTier['discountAmount'])){
			?>
				<small>You will get approx* <?=$userDetails->currency?>$<?=$amount?> in credits.</small><br/><br/>
			<?php
				} else {
			?>
				<small>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</small><br/><br/>
			<?php
				}
			?>
				<small>Thank you for submitting a TT notification.</small><br/><br/>
				<small>*The amount credited into your account will vary depending on currency exchange.</small><br/><br/>
			<?php
			} else {
				if($error_message != ''){
			?>
			<small><b><font style="color:red">*There is a problem submitting a TT notification. Please try again. <?=$error_message?></font></b></small><br/>
			<?php
				}

				if($yourname_error != ''){
			?>
			<small><b><font style="color:red">*<?=$yourname_error?></font></b></small><br/>
			<?php
				}

				if($amount_error != ''){
			?>
			<small><b><font style="color:red">*<?=$amount_error?></font></b></small><br/>
			<?php
				}

				if($receipt_error != ''){
			?>
			<small><b><font style="color:red">*<?=$receipt_error?></font></b></small><br/>
			<?php
				}
			?>
			<form method="post" action="recharge_tt.php?goto=notify">
			<input type="hidden" name="pf" value="<?=$pf?>" />
			<small>Your Name:</small><br/>
			<input type="text" name="yourname" value="<?=$_POST['yourname']?>" alt="Your Name" /><br/><br/>

			<small>Amount (<?=$userDetails->currency?>):</small><br/>
			<input type="text" name="amount" value="<?=$_POST['amount']?>" alt="Amount" /><br/><br/>

			<small>TT Receipt Number:</small><br/>
			<input name="receipt" type="text" value="<?=$_POST['receipt']?>" alt="Receipt Number" /><br/><br/>
			<input type="submit" name="Submit" value="Submit" /><br/>
			</form>
			<?php
			}
			?>
			<small><a href="recharge_tt.php?pf=<?=$pf?>">Back</a></small><br/>
			<small><a href="tt_help.php?goto=notify&amp;pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<?php
				if($userDetails->type > 1){
			?>
			<small><a href="merchant_center.php">Merchant Center</a></small><br/>
			<?php
				} else {
			?>
			<small><a href="recharge_index.php">Recharge Options</a></small><br/>
			<?php
				}
			?>
			<small><a href="index.php">My Account Home</a></small><br/>
			<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
		</body>
	</html>
<?php
}
?>
