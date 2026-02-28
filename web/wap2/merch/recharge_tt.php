<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

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
		$moneyTransferData['username'] = $_SESSION['user']['username'];
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
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(3, $_SESSION['user']['username'], $amount));
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
			<small><b>How do i do a Telegraphic Transfer (TT)?</b></small><br/>
			<ol>
				<li><small>Get mig33's account details by selecting the link below.</small></li>
				<li><small>Go to your bank and fill out a TT form. You must enter your mobile number and country into the 'Reference/Notes' section.</small></li>
				<li><small>Give the money to the bank staff. They will give you a receipt with a TT receipt number on it. Please keep this receipt as proof of payment as it may be required by use to track your payment progress.</small></li>
				<li><small>Tell mig33 that you have sent payment by submitting a TT Notification.</small></li>
				<li><small>We will send you an SMS when we have recharged your account (3-5 days).</small></li>
			</ol>
			<small><a href="recharge_tt.php?goto=details&amp;pf=<?=$pf?>">Get mig33's Account Details</a></small><br/>
			<small><a href="recharge_tt.php?goto=notify&amp;pf=<?=$pf?>">Submit a TT Notification</a></small><br/>
			<small><a href="history.php?cp=tt&amp;pf=<?=$pf?>">Previous TT Notifications</a></small><br/>
			<br/>
			<small>Visit www.mig33.com on the Web for more information.</small><br/><br/>
			<small><a href="buy_credits.php">Back</a></small><br/>
			<small><a href="tt_help.php?pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
<?php
}else if($goto == 'details'){
	emitTitle("TT Account Details");
?>
			<small>Fill in the details below on the form.</small><br/><br/>
			<small>Name of Beneficiary:</small><br/>
			<small><b>Project Goth Pty Ltd</b></small><br/><br/>
			<small>Name of Bank:</small><br/>
			<small><b>Silicon Valley Bank</b> (if sending from outside USA) / <b>Sil Vly BK SJ</b> (if sending from inside USA)</small><br/><br/>
			<small>Bank Account Number:</small><br/>
			<small><b>3300659157</b></small><br/>
			<small>Bank Swift Code:</small><br/>
			<small><b>SVBKUS6S</b></small><br/><br/>
			<small>Special Instructions:</small><br/>
			<small><b>Enter your mig33 username and registered mobile number along with the country code (e.g. +61412345000 mig33user)</b></small><br/><br/>
			<small>If your bank requires more information, <a href="recharge_tt.php?goto=moredetails&amp;pf=<?=$pf?>">click here</a></small><br/>
			<small><a href="recharge_tt.php?pf=<?=$pf?>">Back to Telegraphic Transfer</a></small><br/>
			<small><a href="tt_help.php?goto=details&amp;pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
<?php
}
else if( $goto == "moredetails")
{
	emitTitle("TT Account Details");
?>
	<small>Name of Beneficiary:</small><br/>
	<small><b>Project Goth Pty Ltd</b></small><br/><br/>
	<small>Company Address:</small><br/>
	<small><b>270 East Lane, Suite 2, Burlingame, CA 94010</b></small><br/><br/>
	<small>Name of Bank:</small><br/>
	<small><b>Silicon Valley Bank</b> (if sending from outside USA) / <b>Sil Vly BK SJ</b> (if sending from inside USA)</small><br/><br/>
	<small>Address of Bank:</small><br/>
	<small><b>3003 Tasman Drive, Santa Clara, CA 95054, USA</b></small><br/><br/>
	<small>Routing &amp; Transit Number:</small><br/>
	<small><b>121140399</b></small><br/>
	<small>Bank Account Number:</small><br/>
	<small><b>3300659157</b></small><br/>
	<small>Bank Swift Code:</small><br/>
	<small><b>SVBKUS6S</b></small><br/><br/>
	<small>Special Instructions:</small><br/>
	<small><b>Enter your mig33 username and registered mobile number along with the country code (e.g. +61412345000 mig33user)</b></small><br/><br/>
	<small><a href="recharge_tt.php?pf=<?=$pf?>">Back</a></small><br/>
	<small><a href="tt_help.php?goto=details&amp;pf=<?=$pf?>">Help</a></small><br/>
	<br/>
	<small><a href="merchant_center.php">Merchant Home</a></small><br/>
	<small><a href="logout.php">Logout</a></small><br/>
	<br/>
	</body>
</html>

<?php
}
else if($goto == 'notify')
{
	emitTitle("Submit TT Notification");
		if($notify_success != ''){
			settype($amount, 'double');

			//Get applicable discount tier for the particular user, payment type and amount
			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(2, $_SESSION['user']['username'], $amount));
			}catch(Exception $o){}

			if(!isset($applicableDiscountTier['discountAmount'])){
		?>
			<small>You will get approx* <?=$_SESSION['user']['currency']?>$<?=$amount?> in credits.</small><br/><br/>
		<?php
			} else {
		?>
			<small>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</small><br/><br/>
		<?php
			}
		?>
			<small>Thank you for submitting a TT notification.</small><br/><br/>
			<small>*The amount credited into your account will vary depending on currency exchange.</small><br/><br/>
			<small><a href="recharge_tt.php?pf=<?=$pf?>">Back to Telegraphic Transfer</a></small><br/>
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
			<br/>
			<form method="post" action="recharge_tt.php?goto=notify">
			<input type="hidden" name="pf" value="<?=$pf?>" />
			<small>Your Name:</small><br/>
			<input type="text" name="yourname" value="<?=$_POST['yourname']?>" alt="Your Name" /><br/><br/>
			<small>Amount (<?=$_SESSION['user']['currency']?>):</small><br/>
			<input type="text" name="amount" value="<?=$_POST['amount']?>" alt="Amount" /><br/><br/>
			<small>TT Receipt Number:</small><br/>
			<input name="receipt" type="text" value="<?=$_POST['receipt']?>" alt="Receipt Number" /><br/><br/>
			<input type="submit" name="Submit" value="Submit" /><br/>
			</form>
			<small><a href="recharge_tt.php?pf=<?=$pf?>">Back</a></small><br/>
		<?php
		}
		?>
			<small><a href="tt_help.php?goto=notify&amp;pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
<?php
}
?>