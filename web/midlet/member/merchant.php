<?php

header('Location: /sites/index.php?c=merchant&a=dashboard&v=midlet');
exit;
session_start();
include_once("../../common/common-inc.php");
global $server_root;
ice_check_session();
$userDetails = ice_get_userdata();

$step = 1;
$fn = '';
$ln = '';
$name = '';
$email = '';
$error = '';
$name_error = '';
$email_error = '';
$success_message = '';
$promo = 'PAGELET';

//Detect the users country for idd code pre population
try
{
	$countryData = get_country_from_ip(getRemoteIPAddress());
}
catch(Exception $e){}


if($_GET){
	if(isset($_GET['step'])){
		$step = $_GET['step'];
	}
	if(isset($_GET['fn'])){
		$fn = $_GET['fn'];
	}
	if(isset($_GET['ln'])){
		$ln = $_GET['ln'];
	}
	if(isset($_GET['email'])){
		$email = $_GET['email'];
	}
}

if($_POST){
	if(isset($_POST['step'])){
		$step = $_POST['step'];
	}
	if(isset($_POST['name'])){
		$name = $_POST['name'];
	}
	if(isset($_POST['email'])){
		$email = $_POST['email'];
	}

	if(empty($name)){
		$error_message = 'Enter your name.';
		$error = 'true';
	}


	// split the name string into first and last name
	if($name) {
		$nameString = $name;

		$pos = strpos($nameString, " ");

		$fn = substr($nameString, 0, $pos);
		$ln = substr($nameString, $pos);
	}
	if(empty($email)){
		$error_message = 'Enter email address.';
		$error = 'true';
	}else if(!checkEmail($email)){
		$error_message = 'Incorrect email address format.';
		$error = 'true';
	}

		$phone = '16505045555';  // removed from form at this time, may add back later

		if(!empty($_POST['phone'])){
			$phone = $_POST['phone'];
		} else {
			//$error_message = 'Enter a contact phone number.';
			//$error = 'true';
		}

	// Extra added fields for new survey questions on merchant registration form
	// validate the first survey question checkbox set, at least one item must be checked

		if(!empty($_POST['profit'])){
			$profit = 'true';
		}
		if(!empty($_POST['myself'])){
			$myself = 'true';
		}
		if(!empty($_POST['givetofriends'])){
			$givetofriends = 'true';
		}
		if(!empty($_POST['admin'])){
			$admin = 'true';
		}
		if(empty($_POST['other2'])){
			$other2 = 'false';
		}
		else {
			$other2 = 'true';
			if(!empty($_POST['other_description2'])){
				$other_description2 = $_POST['other_description2'];
			}
			else {
				$error_message = 'Enter an other interest text field.';
				$error = 'true';
			}
		}

		if(!($profit || $myself || $givetofriends || $admin || $other2)) {
			$error_message = 'Fill in one of the second survey question checkboxes';
		$error = 'true';
	}

	if($step == 3){

		if($error == ''){

			try{
				$merchantData['sessionId'] = getSessionID();
				$merchantData['username'] = $userDetails->username;

				$merchantData['emailAddress'] = $email;
				$merchantData['firstName'] = $fn;
				$merchantData['lastName'] = $ln;
				$merchantData['additionalInfo'] = '';

				try{

					if ($countryData['id'] > 0)
					$merchantData['countryIdDetected'] = $countryData['id'];
					else
						$merchantData['countryIdDetected'] = '231';  // Default to USA
				}catch(Exception $e){

				}

				$merchantData['registrationIpAddress'] = getRemoteIPAddress();

				//print 'mig33community=' . $mig33community; die;

				soap_call_ejb('registerMerchant', soap_prepare_call($merchantData));

				//Add merchant to Salesforce
				$merchantData['promo'] = $promo;
				$merchantData['phone'] = $phone;
				$merchantData['newuser'] = 'false';
				$merchantData['profit'] = $profit;
				$merchantData['myself'] = $myself;
				$merchantData['givetofriends'] = $givetofriends;
				$merchantData['admin'] = $admin;
				$merchantData['other2'] = $other2;
				$merchantData['other_description2'] = $other_description2;

			//Add merchant to Salesforce
				addMerchantToSalesforce($merchantData['username'],
																$merchantData['countryIdDetected'],
																$merchantData['firstName'],
																$merchantData['lastName'],
																'[not provided]',
																$merchantData['emailAddress'],
									$merchantData['promo'],
									$merchantData['phone'],
									$merchantData['additionalInfo'],
									$merchantData['newuser'],
									$merchantData['myself'],
									$merchantData['profit'],
									$merchantData['givetofriends'],
									$merchantData['other2'],
									$merchantData['admin'],
									$merchantData['other_description2']);

				//Send Merchant Registration email
				sendMerchantEmail($merchantData['username'], $merchantData['emailAddress'], $merchantData['firstName']);

				$success_message = 'You have successfully registered as a merchant.';
			}catch(Exception $e){
				$step = 2;
				$error_message = $e->getMessage();
				$error = 'true';
			}
		} else {
			$step = 2;
		}
	}
}

if($step == 1){
?>
<html>
  <head>
    <title>Become a Merchant</title>
  </head>
  <body bgcolor="white">
  	<br>
	<p><b>Buy discounted credits from mig33</b></p>
	<p>Sell to other users for a profit. Credits are used for cheap international phone calls, SMS, and many other mig33 features.</p>
	<br>
	<p><b>Credits are easy to sell</b></p>
	<p>mig33 calling rates are cheaper than calling cards. Anyone with a mobile phone can be your customer! Merchants receive free promotion to mig33 users.</p>
	<br>
	<p><b>How do i start?</b></p>
	<?php
		//Retrieve all discount tiers for this user
		try{
			$discountTiers = soap_call_ejb('getDiscountTiers', array(0, $userDetails->username));
			//$discountTiers = soap_call_ejb('getDiscountTiers', array(0, $username));

			if(sizeof($discountTiers) > 0){print '<p>';}

			for ($i = 0; $i < sizeof($discountTiers); $i++){
				//ignore all inactive tiers
				if($discountTiers[$i]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == 30){
					// If the next tier is the same discount % but with a lower min and can still be applied, don't show this one
					if (i < sizeof($discountTiers)-1 && $discountTiers[$i+1]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == $discountTiers[$i+1]['percentageDiscount'] && $discountTiers[$i]['actualMin'] > $discountTiers[$i+1]['actualMin'])
						continue;

					if($discountTiers[$i]['type'] == 'FIRST_TIME_ONLY'){
	?>
	For as little as $5 US get a 30% Starter Discount (one time).

	<?php
					} else {
	?>
	For <?=$discountTiers[$i]['currency']?>$<?=round_twodec($discountTiers[$i]['displayMin'])?> or more, get a <?=round_twodec($discountTiers[$i]['percentageDiscount'])?>% discount.
	<?php
					}
				}
			}

			if(sizeof($discountTiers) > 0){print '</p>';}
		}catch(Exception $te){}
	?>

	<p><a href="<?=$server_root?>/midlet/member/merchant.php?step=2">Register to become a Merchant</a></p>
	<p><a href="<?=$server_root?>/midlet/member/testimonial.php">Read what other Merchants think</a></p>
	<br>
	<p>For more information, visit www.mig33.com on the Web and select the Merchants link.</p>
	<br>
	<p><a href="<?=$server_root?>/midlet/member/popular_rates.php?pf=BM">View mig33 Call Rates</a></p>
	<br>
	<p><a href="<?=$server_root?>/midlet/member/my_account.php">Back</a></p>
	<p><a href="<?=$server_root?>/midlet/member/merchant_help.php?step=1">Help</a></p>
	<br>
 	<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a></p>
 	<br>
  </body>
</html>
<?php
}else if($step == 2){
?>
<html>
	<head>
		<title>Merchant Registration</title>
	</head>
	<body bgcolor="white">
		<?php
			if($userDetails->type > 1){
			//if($type > 1){
		?>
		<p>We have detected that you are already a merchant.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant.php">Back</a></p>
		<p><a href="<?=$server_root?>/midlet/member/merchant_help.php?step=2">Help</a></p>
		<?php
			} else {
				if($error_message != ''){
					print '<p style="color:red">'.$error_message.'</p>';
				}

		?>
		<form method="post" action="<?=$server_root?>/midlet/member/merchant.php">
		<input type="hidden" name="step" value="3">

		<p>Name:</p>
		<p><input type="text" name="name" size="7" value="<?=$name?>"></p>

		<p>Email Address:</p>
		<p><input type="text" name="email" size="7" value="<?=$email?>"></p>

		<p><b>What is your interest in the mig33 Merchant Program?</b></p>
		<p><input type="checkbox" name="profit" value="true" <?php if($profit == 'true') { ?> checked="yes" <?php } ?>/>Purchase credits to sell to others for profit</p>

		<p><input type="checkbox" name="myself" value="true" <?php if($myself == 'true') { ?> checked="yes" <?php } ?>/>Purchase credits for myself</p>

		<p><input type="checkbox" name="givetofriends" value="true" <?php if($givetofriends == 'true') { ?> checked="yes" <?php } ?>/>Purchase credits to give to friends and family</p>

		<p><input type="checkbox" name="admin" value="true" <?php if($admin == 'true') { ?> checked="yes" <?php } ?>/>Become a mig33 chat room admin</p>

		<p><input type="checkbox" name="other2" value="true" <?php if($other2 == 'true') { ?> checked="yes" <?php } ?>/>Other (please describe):</p>
		<input type="text" name="other_description2" value="<?=$other_description2?>" />
		<br>
		<input type="hidden" name="promo" value="<?=$promo?>" />
		<p><input type="submit" name="Submit" value="Submit"></p>
		</form>

		<p><a href="<?=$server_root?>/midlet/member/merchant.php">Back</a></p>
		<p><a href="<?=$server_root?>/midlet/member/merchant_help.php?step=2">Help</a></p>
		<?php
			}
		?>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a></p>
		<br>
	</body>
</html>

<?php
}else if($step == 3){
?>
<html>
	<head>
		<title>Merchant Registration</title>
	</head>
	<body bgcolor="white">
		<p><b>Welcome aboard!</b></p>
		<p>Congratulations! You are now a mig33 merchant.</p>
		<br>
<?php
	$countryData = get_country_from_ip(getRemoteIPAddress());
	$cname = str_replace(' ', '', $countryData['name']);
?>
		<p>Go to the <a href="<?=$server_root?>/midlet/member/merchant_center.php?tusername=FIRST_<?= $userDetails->username ?>&tcountry=<?= $cname ?>">Merchant Center</a> to buy discounted credits, get helpful sales info and tools, and more.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_help.php?step=3">Help</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a></p>
		<br>
	</body>
</html>
<?php
}
?>
