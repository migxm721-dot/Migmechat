<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

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

emitHeader();
emitTitle("Free Advertising");
if($userDetails->type <= 1){
	//Not a merchant
?>
		<small>You have to be a merchant for free advertising.</small><br/>
		<br/>
		<small><a href="sell_credits.php">Back to Selling Tools</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
<?php
} else {
	if($step == 1){
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
?>
		<small>Submit a request to show users a message when they login to mig33.</small><br/>
<?php
		//Show if theres any input errors
		if($error_message != ''){
?>
		<small><b><font style="color:red">*There is an error while submitting your advertisement message. <?=$error_message?></font></b></small><br/>
<?php
		}

		if($contactName_error != ''){
?>
		<small><b><font style="color:red">*<?=$contactName_error?></font></b></small><br/>
<?php
		}

		if($contactNumber_error != ''){
?>
		<small><b><font style="color:red">*<?=$contactNumber_error?></font></b></small><br/>
<?php
		}

		if($contactBy_error != ''){
?>
		<small><b><font style="color:red">*<?=$contactBy_error?></font></b></small><br/>
<?php
		}

		if($email_error != ''){
?>
		<small><b><font style="color:red">*<?=$email_error?></font></b></small><br/>
<?php
		}

		if($location_error != ''){
?>
		<small><b><font style="color:red">*<?=$location_error?></font></b></small><br/>
<?php
		}

		if($location1_error != ''){
?>
		<small><b><font style="color:red">*<?=$location1_error?></font></b></small><br/>
<?php
		}

		if($location2_error != ''){
?>
		<small><b><font style="color:red">*<?=$location2_error?></font></b></small><br/>
<?php
		}
?>
		<br/>
		<form method="post" action="advertise.php">
		<input type="hidden" name="step" value="<?=$step?>" />
		<input type="hidden" name="min_reload" value="<?=$min_reload?>" />
		<small><b>Contact Name*:</b></small><br/>
		<input type="text" name="contactName" value="<?=$contactName?>" alt="Contact Name" /><br/>
		<br/>
		<small><b>Number*:</b></small><br/>
		<input type="text" name="contactNumber" value="<?=$contactNumber?>" alt="Number" /><br/>
		<br/>
		<small><b>Contact By*:</b></small><br/>
		<select name="contactBy">
			<option value="" <?php if($contactBy == ''){ echo 'selected="selected"';} ?>>- Select -</option>
			<option value="callonly" <?php if($contactBy == 'callonly'){ echo 'selected="selected"';} ?>>Phone call</option>
			<option value="smsonly" <?php if($contactBy == 'smsonly'){ echo 'selected="selected"';} ?>>SMS</option>
			<option value="any" <?php if($contactBy == 'any'){ echo 'selected="selected"';} ?>>Either</option>
		</select><br/>
		<br/>
		<small><b>Email Address*:</b></small><br/>
		<input type="text" name="email" value="<?=$email?>" alt="Email Address" /><br/>
		<br/>
		<small><b>Nearest City/Location*:</b></small><br/>
		<input type="text" name="location" value="<?=$location?>" alt="Nearest City/Location" /><br/>
		<br/>
		<small><b>Nearest City/Location 1:</b></small><br/>
		<input type="text" name="location1" value="<?=$location1?>" alt="Nearest City/Location 1" /><br/>
		<br/>
		<small><b>Nearest City/Location 2:</b></small><br/>
		<input type="text" name="location2" value="<?=$location2?>" alt="Nearest City/Location 2" /><br/>
		<br/>
		<input type="submit" name="Submit" value="Preview" /><br/>
		</form>
		<small>* are required &amp; you must purchase at least <?=$userDetails->currency?>$<?=$min_reload?> before mig33 can consider advertising you as a merchant in <?=get_country($userDetails->countryID)?>.</small><br/>
		<br/>
		<small><a href="sell_credits.php">Back</a></small><br/>
		<small><a href="center_help.php?<?=$url?>">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
<?php
	}else if($step == 2){
?>
		<small>Submit if you are happy with your message or go back to re-enter.</small><br/>
		<br/>
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
					$full_loc = $location.', '.$location1.' &amp; '.$location2;
				} else {
					$full_loc = $location.' &amp; '.$location1;
				}
			} else {
				$full_loc = $location;
			}
		?>
		<hr/>
		<small>Buy mig33 credits from your local merchant in <?=$full_loc?>. <?=$cb?> <?=$contactName?> on <?=$contactNumber?><?php if($email != ''){ print ' or email '.$email;} else { print '.';} ?></small>
		<hr/>
		<br/>
		<form method="post" action="advertise.php">
		<input type="hidden" name="step" value="3" />
		<input type="hidden" name="min_reload" value="<?=$min_reload?>" />
		<input type="hidden" name="contactName" value="<?=$contactName?>" />
		<input type="hidden" name="contactNumber" value="<?=$contactNumber?>" />
		<input type="hidden" name="contactBy" value="<?=$contactBy?>" />
		<input type="hidden" name="email" value="<?=$email?>" />
		<input type="hidden" name="location" value="<?=$location?>" />
		<input type="hidden" name="location1" value="<?=$location1?>" />
		<input type="hidden" name="location2" value="<?=$location2?>" />
		<input type="submit" name="Submit" value="Submit" /><br/>
		</form>
		<small><a href="advertise.php?<?=$url?>">Back</a></small><br/>
		<small><a href="center_help.php?<?=$url?>&amp;step=<?=$step?>">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
<?php
	}else if($step == 3){
?>
		<small>Congratulations your message has been submitted for consideration.</small><br/>
		<br/>
		<small>After you make a discount credit purchase from mig33 of at least <?=$userDetails->currency?>$<?=$min_reload?>, we can consider providing free advertising and helping you as a merchant in your country. <a href="buy_credits.php">Make a credit puchase</a>.</small><br/>
		<br/>
		<small><a href="sell_credits.php">Back To Selling Tools</a></small><br/>
		<small><a href="center_help.php?<?=$url?>&amp;step=<?=$step?>">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
<?php
	}
}
?>
	</body>
</html>