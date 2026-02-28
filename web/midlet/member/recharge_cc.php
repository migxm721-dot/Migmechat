<?php
require_once('../../common/common-inc.php');
require_once('../../common/rechargeFunctions.php');
ice_check_session();
$userDetails = ice_get_userdata();
$error = '';
$error_message = '';
$nameoncard_error = '';
$cardnumber_error = '';
$verification_error = '';
$success_message = '';

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

$currency_cc = get_currency_cc($userDetails->countryID);

if ($_POST){
	if(isset($_POST['cardNumber'])){
		$cardnumber = $_POST['cardNumber'];
	} else {
		$cardnumber_error = 'Enter card number.';
		$error = 'true';
	}

	if (isset($_POST['cardHolder'])){
		$nameoncard = $_POST['cardHolder'];
	} else {
		$nameoncard_error = 'Enter name on card.';
		$error = 'true';
	}

	if (isset($_POST['pin'])){
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
		//Construct the payment data object
		$paymentData['username'] = $userDetails->username;
		$paymentData['cardNumber'] = ''.$cardnumber;
		$paymentData['cardExpiryDate'] = $_POST['expiryMonth'] . $_POST['expiryYear'];
		$paymentData['cardHolder'] = $nameoncard;
		$paymentData['cardVerificationNumber'] = ''.$verification;
		$paymentData['amount'] = $_POST['amount'];
		$paymentData['cardType'] = $_POST['cardType'];
		$paymentData['currency'] = $currency_cc;
		$paymentData['source'] = 'MIDLET';
		$paymentData['ipAddress'] = getRemoteIPAddress();

		try{
			$returnPaymentData = soap_call_ejb('creditCardPayment', array(array_keys($paymentData), array_values($paymentData), getRemoteIPAddress(), getSessionID(), getMobileDevice(), getUserAgent()));

			//Check the status of payment
			if ($returnPaymentData['status'] == 'APPROVED' ||
			 	 $returnPaymentData['status'] == 'AWAITING_APPROVAL'){
				$is_approved = ($returnPaymentData['status']=='APPROVED');
				$success_message = "You have successfully recharged through credit card.";
			} else {
				$error_message = 'There is a problem initiating a credit card payment. '.$returnPaymentData['responseCode'];
			}
		}catch(Exception $e){
			$error_message = $e->getMessage();
		}
	}
}

$rechargeDiscount = new RechargeDiscounts("cc", $userDetails->username);
$discount = $rechargeDiscount->discounts[0];

?>
<html>
	<head>
    	<title>Credit and Debit Cards (Visa, Amex, Mastercard, JCB)</title>
  	</head>
  	<body>
		<?php
			if ($success_message != '') {
				//Check if theres any discount tiers being applied
				$amount = $_POST['amount'];
				settype($amount, 'double');

				if(isset($returnPaymentData['discountAmount']))
				{
					$totalDiscount	= $returnPaymentData['discountAmount'] + $amount;
					$amountentered_local = ($amount / get_exchangeRate($currency_cc)) * get_exchangeRate($userDetails->currency);
					$discount_local = ($totalDiscount / get_exchangeRate($currency_cc)) * get_exchangeRate($userDetails->currency);

					if($currency_cc != $userDetails->currency)
					{
			?>
				<p><b>Congratulations:</b> You entered <?=$currency_cc?>$<?=$amount?> (Approx. <?=$userDetails->currency?>$<?=round_twodec($amountentered_local)?>) and have received a <?=round_twodec($returnPaymentData['percentageDiscount'])?>% discount (Approx. <?=$userDetails->currency?>$<?=round_twodec($discount_local)?> in credits).</p>
			<?php
					}
					else
					{
			?>
				<p><b>Congratulations:</b> You entered <?=$currency_cc?>$<?=$amount?> and have received a <?=round_twodec($returnPaymentData['percentageDiscount'])?>% discount (Approx. <?=$userDetails->currency?>$<?=round_twodec($discount_local)?> in credits).</p>
			<?php
					}
				}
			?>
			<?php
				if($is_approved)
				{
			?>
				<p>We have recharged your account through credit card. You are ready to start selling.</p>
			<?php
				}
				else
				{
			?>
				<p>Thank you for submitting your credit card details.</p>
				<p>We are processing your request and we'll credit your account in the next 24 hours.</p>
				<p>Please email contact@mig33.com if you have any questions.</p>
			<?php
				}

				if($pf == 'RO'){
			?>
			<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Back to Recharge Options</a></p>
			<?php
				}else if($pf == 'BC'){
			?>
			<p><a href="<?=$server_root?>/midlet/member/buy_credits.php">Back to Buying Credits</a></p>
			<?php
				}
			} else {
				if($error_message != ''){
					print '<p style="color:red">*'.$error_message.'</p><br>';
				}

				if($cardnumber_error != ''){
					print '<p style="color:red">*'.$cardnumber_error.'</p>';
				}

				if($nameoncard_error != ''){
					print '<p style="color:red">*'.$nameoncard_error.'</p>';
				}

				if($verification_error != ''){
					print '<p style="color:red">*'.$verification_error.'</p>';
				}

				if($expiry_error != ''){
					print '<p style="color:red">*'.$expiry_error.'</p>';
				}

		?>
		<form method="post" action="<?=$server_root?>/midlet/member/recharge_cc.php">
		<input type="hidden" name="pf" value="<?=$pf?>">
		<p>Card Type:</p>
		<?php
			$ct = $_POST['cardType'];
		?>
		<p>
			<select name="cardType" size="7">
				<option value="VISA" <?php if($ct != '' && $ct == 'VISA'){ echo 'selected';} ?>>Visa</option>
				<option value="MASTERCARD" <?php if($ct != '' && $ct == 'MASTERCARD'){ echo 'selected';} ?>>Mastercard</option>
				<option value="AMEX" <?php if($ct != '' && $ct == 'AMEX'){ echo 'selected';} ?>>AMEX</option>
				<option value="JCB" <?php if($ct != '' && $ct == 'JCB'){ echo 'selected';} ?>>JCB</option>
			</select>
		</p>

		<p>Name on Card:</p>
		<p><input type="text" name="cardHolder" value="<?=$nameoncard?>" size="7" alt="Card Holder"></p>
		<p>Card Number:</p>
		<p><input type="text" name="cardNumber" value="<?=$cardnumber?>" size="7" alt="Card Number"></p>
		<p>Verification Code*: </p>
		<p><input name="pin" type="text" value="<?=$verification?>" alt="Verification Code"></p>
		<p>Expiry Date: </p>
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
		<p>
			<select name="expiryMonth">
				<option value="01" <?php if($em == '01'){ echo 'selected';} ?>>01</option>
				<option value="02" <?php if($em == '02'){ echo 'selected';} ?>>02</option>
				<option value="03" <?php if($em == '03'){ echo 'selected';} ?>>03</option>
				<option value="04" <?php if($em == '04'){ echo 'selected';} ?>>04</option>
				<option value="05" <?php if($em == '05'){ echo 'selected';} ?>>05</option>
				<option value="06" <?php if($em == '06'){ echo 'selected';} ?>>06</option>
				<option value="07" <?php if($em == '07'){ echo 'selected';} ?>>07</option>
				<option value="08" <?php if($em == '08'){ echo 'selected';} ?>>08</option>
				<option value="09" <?php if($em == '09'){ echo 'selected';} ?>>09</option>
				<option value="10" <?php if($em == '10'){ echo 'selected';} ?>>10</option>
				<option value="11" <?php if($em == '11'){ echo 'selected';} ?>>11</option>
				<option value="12" <?php if($em == '12'){ echo 'selected';} ?>>12</option>
			</select>
		</p>
		<p>
			<select name="expiryYear">
				<option value="09" <?php if($ey == '09'){ echo 'selected';} ?>>2009</option>
				<option value="10" <?php if($ey == '10'){ echo 'selected';} ?>>2010</option>
				<option value="11" <?php if($ey == '11'){ echo 'selected';} ?>>2011</option>
				<option value="12" <?php if($ey == '12'){ echo 'selected';} ?>>2012</option>
				<option value="13" <?php if($ey == '13'){ echo 'selected';} ?>>2013</option>
				<option value="14" <?php if($ey == '14'){ echo 'selected';} ?>>2014</option>
				<option value="15" <?php if($ey == '15'){ echo 'selected';} ?>>2015</option>
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
			</select>
		</p>

		<p>Recharge value: </p>
		<p>
		<select name="amount">
		<?php
			try
			{
				$value_array = soap_call_ejb('getCreditCardPaymentAmounts',
										array($userDetails->username, ($userDetails->type>1?true:false), $currency_cc));
			}
			catch(Exception $e)
			{}

			//Loops thru the possible value to create the combo box
			foreach ($value_array as $value)
			{
		?>
				<option value="<?=$value?>" <?php if($value == $_POST['amount']){print 'selected';} ?>><?=$currency_cc?>$<?=$value?> <?php
						if($currency_cc != $userDetails->currency){
							//Show approx values
				?>(~<?=$userDetails->currency?>$<?=number_format((($value / get_exchangeRate($currency_cc)) * get_exchangeRate($userDetails->currency)),2)?>)<?php
						}
				?></option><?php
			}
			?>
		</select>
		</p>
		<p><input type="submit" value="Submit"></p>
		<p><?=$discount->getDiscountString()?></p>
		<p>*additional discounts available at higher amounts. Contact mig33 to find out more.</p>
		<p>*3-digit code typically on the back of the card and  top-right of the signature strip. For AMEX a 4-digit code on the front towards the right.</p>
		</form>
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
			}
		?>
		<p><a href="<?=$server_root?>/midlet/member/cc_help.php?pf=<?=$pf?>">Help</a></p>
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