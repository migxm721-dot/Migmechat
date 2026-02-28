<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
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
	if(isset($_GET['contactName'])){
		$contactName = $_GET['contactName'];
	}

	if(isset($_GET['contactNumber'])){
		$contactNumber = $_GET['contactNumber'];
	}

	if(isset($_GET['contactBy'])){
		$contactBy = $_GET['contactBy'];
	}

	if(isset($_GET['email'])){
		$email = $_GET['email'];
	}

	if(isset($_GET['location'])){
		$location = $_GET['location'];
	}

	if(isset($_GET['location1'])){
		$location1 = $_GET['location1'];
	}

	if(isset($_GET['location2'])){
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
$url = 'contactName='.$contactName.'&amp;contactNumber='.$contactNumber.'&amp;contactBy='.$contactBy.'&amp;email='.$email.'&amp;location='.$location.'&amp;location1='.$location1.'&amp;location2='.$location2.'&amp;min_reload='.$min_reload;

//Strip blank space
$url = ereg_replace(' ', '%20', $url);

// set up step specific data in preparation for view
$footerType = "";
if($userDetails->type <= 1){
	$backLink = getMCWapPath()."sell_credits.php";
	$helpLink = "";
} else {
	// user is a merchant, so set up step specific data
	if ($step == 1) {
		$backLink = getMCWapPath()."sell_credits.php";
		$helpLink = getMCWapPath()."help_mc.php?".$url;
		$footerType = "cancel"	;

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

	} else if($step == 2){
		$backLink = getMCWapPath()."advertise.php?".$url;
		$helpLink = getMCWapPath()."help_mc.php?".$url.paramSeparator()."step=".$step;

		// A more friendly text for "contact by"
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
				$full_loc = $location.', '.$location1.' &amp; '.$location2;
			} else {
				$full_loc = $location.' &amp; '.$location1;
			}
		} else {
			$full_loc = $location;
		}

	} else if($step == 3) {
		$backLink = getMCWapPath()."sell_credits.php";
		$helpLink = getMCWapPath()."help_mc.php?".$url.paramSeparator()."step=".$step;
	}
}

// output HTML
if (isPagelet()) {
	include("view/advertise_pagelet.php");
	flushOutputBuffer();
} else {
	include("view/advertise_wap.php");
}
