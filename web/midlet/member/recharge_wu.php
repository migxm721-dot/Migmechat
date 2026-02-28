<?php
require_once('../../common/common-inc.php');
ice_check_session();
$userDetails = ice_get_userdata();

$amount = '';
$paidto = '';
$username = $userDetails->username;
$error = '';
$step = 1;
$westernunionData = '';
$totalWithDiscount = '';
$discount = '';

$pr = '';
$cn = '';
$cc = '';
$ctry = '';

$username_error = '';
$amount_error = '';
$paidto_error = '';

$email = '';
$email_error = '';
$country = '';
$country_error = '';
$answer = '';

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

//Get the supported currency for local bank deposit type
$currency_wu = get_currency_wu($userDetails->countryID);

try{
	$country = get_country($userDetails->countryID);
}catch(Exception $e){
	echo $e.':Unknown';
}

if($_GET){
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
	if(isset($_GET['cn'])){
		$cn = $_GET['cn'];
	}
	if(isset($_GET['cc'])){
		$cc = $_GET['cc'];
	}
	if(isset($_GET['ctry'])){
		$ctry = $_GET['ctry'];
	}
	if(isset($_GET['pr'])){
		$pr = $_GET['pr'];

		$url = ereg_replace(' ', '%20','pf='.$pf.'&pr='.$pr.'&cn='.$cn.'&cc='.$cc.'&ctry='.$ctry.'&amount='.$amount.'&yourname='.$yourname);
	}
}

if($_POST){
	if(isset($_POST['step'])){
		$step = $_POST['step'];
	}

	if($_POST['amount']){
		$amount = $_POST['amount'];
		if(!is_numeric($amount)){
			$amount_error = 'Please enter a numeric value for amount.';
			$error = 'true';
		}
	} else {
		$amount_error = 'Please enter an amount.';
		$error = 'true';
	}

	if($_POST['paidto'])
	{
		$paidto = $_POST['paidto'];
	}
	else
	{
		$paidto_error = "Please select the person that the transfer was made to.";
		$error = 'true';
	}

	if($_POST['username']){
		$username = $_POST['username'];
	} else {
		$username_error = 'Please enter your migme user name.';
		$error = 'true';
	}

	if($_POST['firstname']){
		$firstname = $_POST['firstname'];
	} else {
		$firstname_error = 'Please enter your first name.';
		$error = 'true';
		}

	if($_POST['lastname']){
		$lastname = $_POST['lastname'];
	} else {
		$lastname_error = 'Please enter your last name.';
		$error = 'true';
	}

	if($_POST['mtcn_number']){
		$mtcn_number = $_POST['mtcn_number'];
		$mtcn_number = str_replace("-","",$mtcn_number);// strip any dashes from formatting of number
		if(!is_numeric($mtcn_number)){
			$mtcn_number_error = 'MTCN number is incorrect.';
			$error = 'true';
		}
		$len = strlen($mtcn_number);
		if($len != 10) {
			$mtcn_number_error = 'MTCN number is incorrect.';
			$error = 'true';
		}
	} else {
		$mtcn_number_error = 'MTCN number is incorrect.';
		$error = 'true';
	}

	if($_POST['city']){
		$city = $_POST['city'];
	} else {
		$city_error = 'Please enter the city name.';
		$error = 'true';
					}

	if($_POST['country']){
		$country = $_POST['country'];
	} else {
		$country_error = 'Please enter the country name.';
		$error = 'true';
					}

	if($_POST['email']){
		$email = $_POST['email'];
	} else {
		$email_error = 'Please enter the email address.';
		$error = 'true';
					}

	if($_POST['answer']){
		$answer = $_POST['answer'];
	} else {
		//$answer_error = 'Please enter the secret question answer.';    // NOT REQUIRED FIELD
		//$error = 'true';
					}

	//if no error, continue
	if($error == ''){
		if($step == 2){
			$step = 3;
					}

		if($step == 3){
			// calculate local currency for email to give USD amount, and then round it to two decimal places
			$amountUSD = $amount / get_exchangeRate($currency_wu) * get_exchangeRate("USD");
			$amountUSD = round_twodec($amountUSD);

/*
			print $username;
			print $firstname;
			print $lastname;
			print $mtcn_number;
			print $city;
			print $amountUSD;
			print $currency_wu;
			print $amount;
			print $country;
			print $email;
			print $answer;
			die;
*/
			try {
				sendWesternUnionEmail($username, $firstname, $lastname, $mtcn_number, $city, $country, $email, $amountUSD, $currency_wu, $amount, $answer, $paidto);
				$success_message = 'Thank you for submitting a Western Union notification to migme.';
				}
			catch(Exception $e){
				$error_message = $e->getMessage();
			}
		}
	}
}

if($step == 1){
	//Show Western Union overview
	?>
	<html>
		<head>
			<title>Western Union</title>
		</head>
		<body>
		<p><b>How do I send payment using Western Union?</b></p>
		<br>

		<p><b>1.</b>migme's Western Union details:</p><br>

					Recipient: <b>Jeffrey Tien-Huang Lim</b><br>
					City: <b>Singapore</b><br>
					Country: <b>Singapore</b><br>	<br>

		<p><b>2.</b>Go to your nearest Western Union agent and fill out the GREEN form. Don't forget to bring Picture ID. You will need to write the information above on the GREEN form.</p>
		<p><b>3.</b>Give the money to the agent and they will print you a receipt with a MTCN number. Keep it!</p>
		<p><b>4.</b>Tell migme that you have sent payment by submitting a Western Union Notification.</p>
		<p><b>5.</b>We will process your payment immediately and send you an SMS when we have credited your migme account.</p>

		<br>
		<p><a href="<?=$server_root?>/midlet/member/recharge_wu.php?step=2&pf=<?=$pf?>">Submit a Western Union Notification</a></p>
		<br>
		<p>Visit www.mig.me on the Web for example forms.</p>
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
		<p><a href="<?=$server_root?>/midlet/member/wu_help.php?pf=<?=$pf?>">Help</a></p>
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
			<title>Western Union</title>
		</head>
		<body>
			<p>&nbsp;</p>
			<p>Get payment details.</p>

			<p><b>Step 1: Go to Western Union</b></p><br/>
			<p><b>Step 2: Submit Payment Receipt Below</b></p><br/>
		<?php
				if($error_message != ''){
		?>
			<p style="color:red">*There is an error while requesting for Western Union details. <?=$error_message?></p>
		<?php
				}

				//Show any input errors
				if($username_error != ''){
					print '<p style="color:red">*'.$username_error.'</p>';
				}

				if($firstname_error != ''){
					print '<p style="color:red">*'.$firstname_error.'</p>';
				}
				if($lastname_error != ''){
					print '<p style="color:red">*'.$lastname_error.'</p>';
				}

				if($city_error != ''){
					print '<p style="color:red">*'.$city_error.'</p>';
				}

				if($country_error != ''){
					print '<p style="color:red">*'.$country_error.'</p>';
				}

				if($email_error != ''){
					print '<p style="color:red">*'.$email_error.'</p>';
				}

				if($mtcn_number_error != ''){
					print '<p style="color:red">*'.$mtcn_number_error.'</p>';
				}

				if($amount_error != ''){
					print '<p style="color:red">*'.$amount_error.'</p>';
				}

				if($paidto_error != ''){
					print '<p style="color:red">*'.$paidto_error.'</p>';
				}

				// Serve the form, possibly with errors. If errors are detected, echo error=true in the query string of the tracking image below:
				$url = $_SERVER['SCRIPT_NAME'];    // BELOW, must use FULL URL PATH for pagelets!
				//print '<img width="1" height="1" src="http://www.mig33.com/tr.php?turl='.$url.'&amp;terror='.$error.'">';

		?>
			<br>
			<form method="post" action="<?=$server_root?>/midlet/member/recharge_wu.php">
			<input type="hidden" name="step" value="<?=$step?>">
			<input type="hidden" name="pf" value="<?=$pf?>">

			<p><b>migme username:</b></p>
			<p><input type="text" name="username" value="<?=$username?>" size="7" alt="migme User Name" /></p>
			<p><b>First Name:</b></p>
			<p><input type="text" name="firstname" value="<?=$firstname?>" size="7" alt="First Name" /></p>
			<p><b>Last Name:</b></p>
			<p><input type="text" name="lastname" value="<?=$lastname?>" size="7" alt="Last Name" /></p>
			<p><b>MTCN Number:</b></p>
			<p><input type="text" name="mtcn_number" value="<?=$mtcn_number?>" size="7" alt="MTCN Number" /></p>
			<p><b>City:</b></p>
			<p><input type="text" name="city" value="<?=$city?>" size="7" alt="City" /></p>
			<p><b>Country:</b></p>
			<p><input type="text" name="country" value="<?=$country?>" size="7" alt="Country" /></p>
			<p><b>Email Address:</b></p>
			<p><input type="text" name="email" value="<?=$email?>" size="7" alt="Email" /></p>
			<p><b>Amount Sent in (<?=$currency_wu?>):</b></p>
			<p><input type="text" name="amount" value="<?=$amount?>" size="7" alt="Amount" /></p>
			<p><b>Paid To:</b></p>
			<p>Jeffrey Tien-Huang Lim</p>
			<input type="hidden" name="paidto" value="JeffreyTienHuangLim">
			<p><b>Answer to secret question:</b></p>
			<p><input type="text" name="answer" value="<?=$answer?>" size="7" alt="Answer" /></p>


			<p><input type="submit" name="Submit" value="Submit"></p>
			</form>

		<?php
			//Retrieve all discount tiers for this user
			try{
				$discountTiers = soap_call_ejb('getDiscountTiers', array(4, $userDetails->username));

				for ($i = 0; $i < sizeof($discountTiers); $i++){
					//ignore all inactive tiers
					if($discountTiers[$i]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == 30){
		?>
			<p>Get a <?=round_twodec($discountTiers[$i]['percentageDiscount'])?>% discount for <?=$discountTiers[$i]['currency']?>$<?=round_twodec($discountTiers[$i]['displayMin'])?><?php
						if($currency_wu != $userDetails->currency){
							$approx_inlocal = ($discountTiers[$i]['displayMin'] / get_exchangeRate($currency_wu)) * get_exchangeRate($userDetails->currency);
							print ' (Approx. '.$userDetails->currency.'$'.round_twodec($approx_inlocal).') or more.<br>* additional discounts available at higher amounts. Contact migme to find out more.</p>';
						} else {
							print ' or more.<br>* additional discounts available at higher amounts. Contact migme to find out more.</p>';
						}

						if($discountTiers[$i]['type'] != 'FIRST_TIME_ONLY'){
							break;
						}
					}
				}
			}catch(Exception $te){}
		?>
			<br>
			<p><a href="<?=$server_root?>/midlet/member/recharge_wu.php?step=1&pf=<?=$pf?>">Back</a></p>
			<p><a href="<?=$server_root?>/midlet/member/wu_help.php?step=2&pf=<?=$pf?>">Help</a></p>
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
}else if($step == 3){
	?>
	<html>
		<head>
			<title>Western Union Payment Details</title>
		</head>
		<body>
		<?php

			// tracking image, will denote success from failure and errors conditions of the form page (below) being served
			print '<img width="1" height="1" src="http://www.mig.me/tr.php?turl='.$url.'&amp;tstate=success">';

			settype($amount, 'double');

			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(4, $userDetails->username, $amount));
			}catch(Exception $o){}

			if(!isset($applicableDiscountTier['discountAmount'])){
		?>
			<p>You will get approx* $<?=$amountUSD?> USD in credits.
		<?php
				if($userDetails->type > 1){
		?>
				If you wanted a discount, go <a href="<?=$server_root?>/midlet/member/recharge_wu.php?step=2&yourname=<?=$yourname?>&pf=<?=$pf?>">Back</a> to enter a higher amount.</p>
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
			}
		?>
			<br>


			<p>*The amount credited into your account will vary depending on currency exchange.</p>
			<br>
		<?php
			if($pf = 'RO'){
		?>
			<p><a href="<?=$server_root?>/midlet/member/recharge_index.php">Back to Recharge Options</a></p>
		<?php
			}else if($pf = 'BC'){
		?>
			<p><a href="<?=$server_root?>/midlet/member/buy_credits.php">Back to Buying Credits</a></p>
		<?php
			}
		?>
			<p><a href="<?=$server_root?>/midlet/member/wu_help.php?<?=$url?>&step=3">Help</a></p>
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
