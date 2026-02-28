<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
require_once('../../common/rechargeFunctions.php');
putenv("pagelet=true");

session_start();
check_session_merchant();

$error = '';
$error_message = '';
$nameoncard_error = '';
$cardnumber_error = '';
$verification_error = '';
$success_message = '';

$pb = $_GET['pb'];
if($_POST['pb']){
	$pb = $_POST['pb'];
}

$currency_cc = get_currency_cc($_SESSION['user']['currency']);

if ($_POST){
	if(!empty($_POST['cardNumber'])){
		$cardnumber = $_POST['cardNumber'];
	} else {
		$cardnumber_error = 'Enter card number.';
		$error = 'true';
	}

	if (!empty($_POST['cardHolder'])){
		$nameoncard = $_POST['cardHolder'];
	} else {
		$nameoncard_error = 'Enter name on card.';
		$error = 'true';
	}

	if (!empty($_POST['pin'])){
		$verification = $_POST['pin'];
	} else {
		$verification_error = 'Enter verification code.';
		$error = 'true';
	}

	//Check expiry date
	$currentYear = date('y');
	$currentMonth = date('m');
	if($_POST['expiryYear'] < $currentYear){
		$error = 'true';
		$expiry_error = 'Credit Card has expired.';
	}else if($currentYear == $_POST['expiryYear']){
		if($_POST['expiryMonth'] < $currentMonth){
			$error = 'true';
			$expiry_error = 'Credit Card has expired.';
		}
	}

	if($error == ''){
		$remote_ip = getRemoteIPAddress();

		//Construct the payment data object
		$paymentData['username'] = $_SESSION['user']['username'];
		$paymentData['cardNumber'] = ''.$cardnumber;
		$paymentData['cardExpiryDate'] = $_POST['expiryMonth'] . $_POST['expiryYear'];
		$paymentData['cardHolder'] = $nameoncard;
		$paymentData['cardVerificationNumber'] = ''.$verification;
		$paymentData['amount'] = $_POST['amount'];
		$paymentData['cardType'] = $_POST['cardType'];
		$paymentData['currency'] = $currency_cc;
		$paymentData['source'] = 'WAP';
		$paymentData['ipAddress'] = getRemoteIPAddress();


		try{
			$returnPaymentData = soap_call_ejb('creditCardPayment', array(array_keys($paymentData), array_values($paymentData), getRemoteIPAddress(), getSessionID(), getMobileDevice(), getUserAgent()));

			//Check the status of payment
			if ($returnPaymentData['status'] == 'APPROVED' ||
				$returnPaymentData['status'] == 'AWAITING_APPROVAL'){
				$is_approved = ($returnPaymentData['status'] == 'APPROVED');
				$success_message = "You have successfully recharged through credit card.";
			} else {
				$error_message = 'There is a problem initiating a credit card payment. '.$returnPaymentData['responseCode'];
			}
		}catch(Exception $e){
			$error_message = $e->getMessage();
		}
	}
}

$rechargeDiscount = new RechargeDiscounts("cc", $_SESSION['user']['username']);
$discount = $rechargeDiscount->discounts[0];


emitHeader();
emitTitle("Credit and Debit Cards (Visa, Amex, Mastercard, JCB)");
			if ($success_message != '')
			{
				//Check if theres any discount tiers being applied
				$amount = $_POST['amount'];
				settype($amount, 'double');

				if(isset($returnPaymentData['discountAmount']))
				{
					$totalDiscount	= $returnPaymentData['discountAmount'] + $amount;
					$amountentered_local = ($amount / get_exchangeRate($currency_cc)) * get_exchangeRate($_SESSION['user']['currency']);
					$discount_local = ($totalDiscount / get_exchangeRate($currency_cc)) * get_exchangeRate($_SESSION['user']['currency']);

					if($currency_cc != $_SESSION['user']['currency'])
					{
			?>
				<small><b>Congratulations:</b> You entered <?=$currency_cc?>$<?=$amount?> (Approx. <?=$_SESSION['user']['currency']?>$<?=round_twodec($amountentered_local)?>) and have received a <?=round_twodec($returnPaymentData['percentageDiscount'])?>% discount (Approx. <?=$_SESSION['user']['currency']?>$<?=round_twodec($discount_local)?> in credits).</small><br/><br/>
			<?php
					}
					else
					{
			?>
				<small><b>Congratulations:</b> You entered <?=$currency_cc?>$<?=$amount?> and have received a <?=round_twodec($returnPaymentData['percentageDiscount'])?>% discount (Approx. <?=$_SESSION['user']['currency']?>$<?=round_twodec($discount_local)?> in credits).</small><br/><br/>
			<?php
					}
				}
			?>
			<?php
				if($is_approved)
				{
			?>
				<small>We have recharged your account through credit card. You are ready to start selling.</small><br/><br/>
				<small><a href="buy_credits.php">Back to Buy Credits</a></small><br/>
			<?php
				}
				else
				{
			?>
				<small>Thank you for submitting your credit card details.</small>
				<small>We are processing your request and we'll credit your account in the next 24 hours.</small>
				<small>Please email contact@mig33.com if you have any questions.</small><br/><br/>
			<?php
				}

			} else {
				if($error_message != ''){
					print '<small><b><font style="color:red">*'.$error_message.'</font></b></small><br/>';
				}

				if($cardnumber_error != ''){
					print '<small><b><font style="color:red">*'.$cardnumber_error.'</font></b></small><br/>';
				}

				if($nameoncard_error != ''){
					print '<small><b><font style="color:red">*'.$nameoncard_error.'</font></b></small><br/>';
				}

				if($verification_error != ''){
					print '<small><b><font style="color:red">*'.$verification_error.'</font></b></small><br/>';
				}

				if($expiry_error != ''){
					print '<small><b><font style="color:red">*'.$expiry_error.'</font></b></small><br/>';
				}

				if($error != ''){
					print '<br/>';
				}
		?>
		<form method="post" action="recharge_cc.php">
		<small>Card Type:</small><br/>
		<?php
				$ct = 1;
				if(!empty($cardType)){
					$ct = $cardType;
				}
		?>
		<select name="cardType">
			<option value="VISA" <?php if($ct == 'VISA'){ echo 'selected="selected"';} ?>>Visa</option>
			<option value="MASTERCARD" <?php if($ct == 'MASTERCARD'){ echo 'selected="selected"';} ?>>Mastercard</option>
			<option value="AMEX" <?php if($ct == 'AMEX'){ echo 'selected="selected"';} ?>>AMEX</option>
			<option value="JCB" <?php if($ct == 'JCB'){ echo 'selected="selected"';} ?>>JCB</option>
		</select><br/><br/>
		<small>Name on Card:</small><br/>
		<input type="text" name="cardHolder" value="<?=$nameoncard?>" alt="Card Holder" /><br/><br/>
		<small>Card Number:</small><br/>
		<input type="text" name="cardNumber" value="<?=$cardnumber?>" alt="Card Number" /><br/><br/>
		<small>Verification Code*:</small><br/>
		<input name="pin" type="text" value="<?=$verification?>" alt="Verification Code" /><br/><br/>
		<small>Expiry Date:</small><br/>
		<?php
			//Used for prepopulation of expiry date
			if($_POST['expiryMonth'])
			{
				$em = $_POST['expiryMonth'];
			}

			if($_POST['expiryYear'])
			{
				$ey = $_POST['expiryYear'];
			}
		?>
		<select name="expiryMonth">
			<option value="01" <?php if($em == '01'){ echo 'selected="selected"';} ?>>01</option>
			<option value="02" <?php if($em == '02'){ echo 'selected="selected"';} ?>>02</option>
			<option value="03" <?php if($em == '03'){ echo 'selected="selected"';} ?>>03</option>
			<option value="04" <?php if($em == '04'){ echo 'selected="selected"';} ?>>04</option>
			<option value="05" <?php if($em == '05'){ echo 'selected="selected"';} ?>>05</option>
			<option value="06" <?php if($em == '06'){ echo 'selected="selected"';} ?>>06</option>
			<option value="07" <?php if($em == '07'){ echo 'selected="selected"';} ?>>07</option>
			<option value="08" <?php if($em == '08'){ echo 'selected="selected"';} ?>>08</option>
			<option value="09" <?php if($em == '09'){ echo 'selected="selected"';} ?>>09</option>
			<option value="10" <?php if($em == '10'){ echo 'selected="selected"';} ?>>10</option>
			<option value="11" <?php if($em == '11'){ echo 'selected="selected"';} ?>>11</option>
			<option value="12" <?php if($em == '12'){ echo 'selected="selected"';} ?>>12</option>
		</select>
		<select name="expiryYear">
			<option value="07" <?php if($ey == '07'){ echo 'selected="selected"';} ?>>2007</option>
			<option value="08" <?php if($ey == '08'){ echo 'selected="selected"';} ?>>2008</option>
			<option value="09" <?php if($ey == '09'){ echo 'selected="selected"';} ?>>2009</option>
			<option value="10" <?php if($ey == '10'){ echo 'selected="selected"';} ?>>2010</option>
			<option value="11" <?php if($ey == '11'){ echo 'selected="selected"';} ?>>2011</option>
			<option value="12" <?php if($ey == '12'){ echo 'selected="selected"';} ?>>2012</option>
			<option value="13" <?php if($ey == '13'){ echo 'selected="selected"';} ?>>2013</option>
			<option value="14" <?php if($ey == '14'){ echo 'selected="selected"';} ?>>2014</option>
			<option value="15" <?php if($ey == '15'){ echo 'selected="selected"';} ?>>2015</option>
			<option value="16" <?php if($ey == '16'){ echo 'selected';} ?>>2016</option>
			<option value="17" <?php if($ey == '17'){ echo 'selected';} ?>>2017</option>
			<option value="18" <?php if($ey == '18'){ echo 'selected';} ?>>2018</option>
			<option value="19" <?php if($ey == '19'){ echo 'selected';} ?>>2019</option>
			<option value="20" <?php if($ey == '20'){ echo 'selected';} ?>>2020</option>
			<option value="21" <?php if($ey == '21'){ echo 'selected';} ?>>2021</option>
			<option value="22" <?php if($ey == '22'){ echo 'selected';} ?>>2022</option>
			<option value="23" <?php if($ey == '23'){ echo 'selected';} ?>>2023</option>
			<option value="24" <?php if($ey == '24'){ echo 'selected';} ?>>2024</option>
			<option value="25" <?php if($ey == '25'){ echo 'selected';} ?>>2025</option>
			<option value="26" <?php if($ey == '26'){ echo 'selected';} ?>>2026</option>
			<option value="27" <?php if($ey == '27'){ echo 'selected';} ?>>2027</option>
			<option value="28" <?php if($ey == '28'){ echo 'selected';} ?>>2028</option>
			<option value="29" <?php if($ey == '29'){ echo 'selected';} ?>>2029</option>
			<option value="30" <?php if($ey == '30'){ echo 'selected';} ?>>2030</option>
		</select><br/><br/>
		<small>Recharge value:</small><br/>
		<select name="amount">
		<?php
			try
			{
				$value_array = soap_call_ejb('getCreditCardPaymentAmounts',
									array($_SESSION['user']['username'], true, $currency_cc));
			}
			catch(Exception $e)
			{}

			//Loops thru the possible value to create the combo box
			foreach ($value_array as $value)
			{
		?>
				<option value="<?=$value?>" <?php if($value == $_POST['amount']){print 'selected="selected"';} ?>><?=$currency_cc?>$<?=$value?> <?php
						if($currency_cc != $_SESSION['user']['currency']){
							//Show approx values
				?>
				(Approx. <?=$_SESSION['user']['currency']?>$<?=number_format((($value / get_exchangeRate($currency_cc)) * get_exchangeRate($_SESSION['user']['currency'])),2)?>)
				<?php
						}
				?></option>
			<?php
			}
			?>
		</select><br/><br/>
		<input type="submit" value="Submit" /><br/><br/>
		<small><?=$discount->getDiscountString()?></small><br/>
		<small>*additional discounts available at higher amounts. Contact mig33 to find out more.</small><br/>
		<small>*3-digit code typically on the back of the card and  top-right of the signature strip. For AMEX a 4-digit code on the front towards the right.</small><br/>
		</form>
		<small><a href="buy_credits.php">Back</a></small><br/>
		<?php
			}
		?>

		<small><a href="cc_help.php">Help</a></small><br/>
		<br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>