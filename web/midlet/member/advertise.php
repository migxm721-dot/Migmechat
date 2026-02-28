<?php
require_once('../../common/common-inc.php');
require_once("../../common/common-config.php");

ice_check_session();
$userDetails = ice_get_userdata();

$discountTiers = '';
$sp_tier = '';
$discount_tier = '';

$contactName = '';
$contactName_error = '';
$contactNumber = '';
$contactNumber_error = '';
$contactBy = '';
$contactBy_error = '';
$email = '';
$email_error = '';
$location = '';
$location_error = '';
$location1 = '';
$location2= '';
$error = '';
$error_message = '';
$step = 1;
if($_GET['step']){
	$step=$_GET['step'];
}

//data that can prepopulated if needed (i.e. POST data overwrites it later if available)
try{
	$merchantData = soap_call_ejb('loadMerchant', array($userDetails->username));
}catch(Exception $e){}

//Prepop initial data if available
$contactName = $merchantData['firstName'];
$contactNumber = $userDetails->mobilePhone;
$email = $merchantData['emailAddress'];

//Get the supported currency for local bank deposit type
$currency_lbd = get_currency_lbd($userDetails->countryID);

if($_GET){
	if($_GET['contactName']){
		$contactName = $_GET['contactName'];
	}

	if($_GET['contactNumber']){
		$contactNumber = $_GET['contactNumber'];
	}

	if($_GET['contactBy']){
		$contactBy = $_GET['contactBy'];
	}

	if($_GET['email']){
		$email = $_GET['email'];
	}

	if($_GET['location']){
		$location = $_GET['location'];
	}

	if($_GET['location1']){
		$location1 = $_GET['location1'];
	}

	if($_GET['location2']){
		$location2 = $_GET['location2'];
	}

	if(isset($_GET['min_reload'])){
		$min_reload = $_GET['min_reload'];
	}
}


if($_POST){
	$min_reload = $_POST['min_reload'];

	//Check for contact / business name data
	if($_POST['contactName']){
		$contactName = $_POST['contactName'];
		//Check name length limit
		if(strlen($contactName) < 3 || strlen($contactName) > 10){
			$contactName_error = 'Contact name must be between 3 to 10 characters.';
			$error = 'true';
		}
	} else {
		$contactName_error = 'Enter contact name.';
		$error = 'true';
	}

	//Check for contact number data
	if($_POST['contactNumber']){
		$contactNumber = $_POST['contactNumber'];
		//Check number length limit, has to be at least 5
		if(strlen($contactNumber) < 5){
			$contactNumber_error = 'Contact number is too short.';
			$error = 'true';
		}else if(!is_numeric($contactNumber)){
			$contactNumber_error = 'Contact number must be numeric.';
			$error = 'true';
		}
	} else {
		$contactNumber_error = 'Enter contact number.';
		$error = 'true';
	}

	//Check for contact by data
	if($_POST['contactBy']){
		$contactBy = $_POST['contactBy'];
	} else {
		$contactBy_error = 'Select contact method.';
		$error = 'true';
	}

	//Check for email data, optional data
	if($_POST['email']){
		$email = $_POST['email'];
		if(checkEmail($email) == 0){
			$email_error = 'Email address format is incorrect.';
			$error = 'true';
		}
	} else {
		$email_error = 'Enter email address.';
		$error = 'true';
	}

	//Check for location data
	if($_POST['location']){
		$location = $_POST['location'];
		if(strlen($location) < 3 || strlen($location) > 15){
			$location_error = 'Your nearest city / location must be between 3 to 15 characters.';
			$error = 'true';
		}
	} else {
		$location_error = 'Enter your nearest city / location.';
		$error = 'true';
	}

	//Check for location 1 data. optional data
	if($_POST['location1']){
		$location1 = $_POST['location1'];
		if(strlen($location1) < 3 || strlen($location1) > 15){
			$location1_error = 'Your nearest city / location 1 must be between 3 to 15 characters.';
			$error = 'true';
		}
	}

	//Check for location 2 data. optional data
	if($_POST['location2']){
		$location2 = $_POST['location2'];
		if(strlen($location2) < 3 || strlen($location2) > 15){
			$location2_error = 'Your nearest city / location 2 must be between 3 to 15 characters.';
			$error = 'true';
		}
	}

	if($_POST['step']){
		$step = $_POST['step'];
	}

	if($error == ''){
		if($step < 3){
			$step = 2;
		} else {
			$username = $userDetails->username;

			//Send email to merchant regarding this advertisement request
			$message = "Username: $username
			Contact or business name: $contactName
			Contact number: $contactNumber
			Contact By: $contactBy
			Email address: $email
			Nearest city / location: $location
			Nearest city / location 1: $location1
			Nearest city / location 2: $location2";

			mail('merchant@mig33.com', 'Advertisement with mig33 - WAP', $message);
			$step = 3;
		}
	}
}

//Construct URL back link
$url = 'contactName='.$contactName.'&contactNumber='.$contactNumber.'&contactBy='.$contactBy.'&email='.$email.'&location='.$location.'&location1='.$location1.'&location2='.$location2.'&min_reload='.$min_reload;

//Strip blank space
$url = ereg_replace(' ', '%20', $url);


if($userDetails->type <= 1){
	//Not a merchant
?>
<html>
	<head>
		<title>Free Advertising</title>
	</head>
	<body>
		<p>You have to be a merchant for free advertising.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a> &gt;&gt;</p>
		<br>
<?php
} else {
	if($step == 1){
?>
<html>
	<head>
		<title>Free Advertising</title>
	</head>
	<body>
		<p>Submit a request to show users a message when they login to mig33.</p>
<?php
		//Retrieve all discount tiers for this user and get the minimum. Take the min from LBD
		$min_reload = '';
		$tmp_value = '';
		try{
			$discountTiers = soap_call_ejb('getDiscountTiers', array(3, $userDetails->username));

			for ($i = 0; $i < sizeof($discountTiers); $i++){
				//ignore all inactive tiers
				if($discountTiers[$i]['canBeApplied'] && $discountTiers[$i]['type'] != 'FIRST_TIME_ONLY'){
					if($currency_lbd != $userDetails->currency){
						$tmp_value = ($discountTiers[$i]['displayMin'] / get_exchangeRate($currency_lbd)) * get_exchangeRate($userDetails->currency);
					} else {
						$tmp_value = round_twodec($discountTiers[$i]['displayMin']);
					}

					//Compare to get the lowest
					if($min_reload == '' || $min_reload > $tmp_value){
						$min_reload = $tmp_value;
					}
				}
			}
		}catch(Exception $te){}

		//Show if theres any input errors
		if($error_message != ''){
?>
		<p style="color:red">*There is an error while submitting your advertisement message. <?=$error_message?></p>
<?php
		}

		if($contactName_error != ''){
?>
		<p style="color:red">*<?=$contactName_error?></p>
<?php
		}

		if($contactNumber_error != ''){
?>
		<p style="color:red">*<?=$contactNumber_error?></p>
<?php
		}

		if($contactBy_error != ''){
?>
		<p style="color:red">*<?=$contactBy_error?></p>
<?php
		}

		if($email_error != ''){
?>
		<p style="color:red">*<?=$email_error?></p>
<?php
		}

		if($location_error != ''){
?>
		<p style="color:red">*<?=$location_error?></p>
<?php
		}

		if($location1_error != ''){
?>
		<p style="color:red">*<?=$location1_error?></p>
<?php
		}

		if($location2_error != ''){
?>
		<p style="color:red">*<?=$location2_error?></p>
<?php
		}
?>
		<br>
		<form method="post" action="<?=$server_root?>/midlet/member/advertise.php">
		<input type="hidden" name="step" value="<?=$step?>" />
		<input type="hidden" name="min_reload" value="<?=$min_reload?>" />
		<p><b>Contact Name*:</b></p>
		<p><input type="text" name="contactName" value="<?=$contactName?>" alt="Contact Name" size="7"/></p>

		<p><b>Number*:</b></p>
		<p><input type="text" name="contactNumber" value="<?=$contactNumber?>" alt="Number" size="7"/></p>

		<p><b>Contact By*:</b></p>
		<p>
		<select name="contactBy">
			<option value="" <?php if($contactBy == ''){ echo 'selected';} ?>>- Select -</option>
			<option value="callonly" <?php if($contactBy == 'callonly'){ echo 'selected';} ?>>Phone call</option>
			<option value="smsonly" <?php if($contactBy == 'smsonly'){ echo 'selected';} ?>>SMS</option>
			<option value="any" <?php if($contactBy == 'any'){ echo 'selected';} ?>>Either</option>
		</select>
		</p>

		<p><b>Email Address*:</b></p>
		<p><input type="text" name="email" value="<?=$email?>" alt="Email Address" size="7" /></p>

		<p><b>Nearest City/Location*:</b></p>
		<p><input type="text" name="location" value="<?=$location?>" alt="Nearest City/Location" size="7"/></p>

		<p><b>Nearest City/Location 1:</b></p>
		<p><input type="text" name="location1" value="<?=$location1?>" alt="Nearest City/Location 1" size="7"/></p>

		<p><b>Nearest City/Location 2:</b></p>
		<p><input type="text" name="location2" value="<?=$location2?>" alt="Nearest City/Location 2" size="7"/></p>

		<p><input type="submit" name="Submit" value="Preview" /></p>
		</form>
		<p>* are required & you must purchase at least <?=$userDetails->currency?>$<?=$min_reload?> before mig33 can consider advertising you as a merchant in <?=get_country($userDetails->countryID)?>.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sell_credits.php">Back</a></p>
		<p><a href="<?=$server_root?>/midlet/member/advertise_help.php?<?=$url?>">Help</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a></p>
		<br>
<?php
	}else if($step == 2){
?>
<html>
	<head>
		<title>Free Advertising</title>
	</head>
	<body>
		<p>Submit if you are happy with your message or go back to re-enter.</p>

		<?php
			//A more friendly text for "contact by"
			if($contactBy == 'callonly'){
				$cb = 'Call';
			}else if($contactBy == 'smsonly'){
				$cb = 'SMS';
			}else if($contactBy == 'any'){
				$cb = 'Call or Text';
			}

			//Construct text for location/locations
			if($location1 != ''){
				if($location2 != ''){
					$full_loc = $location.', '.$location1.' & '.$location2;
				} else {
					$full_loc = $location.' & '.$location1;
				}
			} else {
				$full_loc = $location;
			}
		?>
		<br>
		<p>Buy mig33 credits from your local merchant in <?=$full_loc?>. <?=$cb?> <?=$contactName?> on <?=$contactNumber?><?php if($email != ''){ print ' or email '.$email;} else { print '.';} ?>
		</p>
		<form method="post" action="<?=$server_root?>/midlet/member/advertise.php">
		<input type="hidden" name="step" value="3" />
		<input type="hidden" name="min_reload" value="<?=$min_reload?>" />
		<input type="hidden" name="contactName" value="<?=$contactName?>" />
		<input type="hidden" name="contactNumber" value="<?=$contactNumber?>" />
		<input type="hidden" name="contactBy" value="<?=$contactBy?>" />
		<input type="hidden" name="email" value="<?=$email?>" />
		<input type="hidden" name="location" value="<?=$location?>" />
		<input type="hidden" name="location1" value="<?=$location1?>" />
		<input type="hidden" name="location2" value="<?=$location2?>" />
		<p><input type="submit" name="Submit" value="Submit" /></p>
		</form>
		<p><a href="<?=$server_root?>/midlet/member/advertise.php?<?=$url?>">Back</a></p>
		<p><a href="<?=$server_root?>/midlet/member/advertise_help.php?<?=$url?>&step=<?=$step?>">Help</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a></p>
		<br>
<?php
	}else if($step == 3){
?>
<html>
	<head>
		<title>Free Advertising</title>
	</head>
	<body>
		<p>Congratulations your message has been submitted for consideration.</p>
		<br>
		<p>After you make a discount credit purchase from mig33 of at least <?=$userDetails->currency?>$<?=$min_reload?>, we can consider providing free advertising and helping you as a merchant in your country. <a href="<?=$server_root?>/midlet/member/buy_credits.php">Make a credit puchase</a>.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sell_credits.php">Back To Selling Tools</a></p>
		<p><a href="<?=$server_root?>/midlet/member/advertise_help.php?<?=$url?>&step=<?=$step?>">Help</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
<?php
	}
}
?>
	</body>
</html>