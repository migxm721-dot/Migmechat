<?php
include_once("../member2/common-inc-kk.php");
//include_once("../../common/common-inc.php");
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

$step = 1;
$fn = '';
$fn_error = '';
$ln = '';
$ln_error = '';
$email = '';
$email_error = '';
$error = '';
$error_message = '';



if($_GET){
	if(isset($_GET['step'])){
		$step = $_GET['step'];
	}
}

if($_POST){
	if(!empty($_POST['step'])){
		$step = $_POST['step'];
	}

	if(!empty($_POST['fn'])){
		$fn = $_POST['fn'];
	} else {
		$fn_error = 'Enter first name.';
		$error = 'true';
	}

	if(!empty($_POST['ln'])){
		$ln = $_POST['ln'];
	} else {
		$ln_error = 'Enter last name.';
		$error = 'true';
	}

	if(!empty($_POST['email'])){
		$email = $_POST['email'];
		if(!checkEmail($email)){
			$email_error = 'Email address format is incorrect.';
			$error = 'true';
		}
	} else {
		$email_error = 'Enter email address.';
		$error = 'true';
	}


	if($step == 3){
		if($error == ''){
			try{
				$merchantData['password'] = $userDetails->password;
				$merchantData['username'] = $userDetails->username;
				$merchantData['emailAddress'] = $email;
				$merchantData['firstName'] = $fn;
				$merchantData['lastName'] = $ln;
				$merchantData['additionalInfo'] = '';

				try{
					$countryData = get_country_from_ip(getRemoteIPAddress());
					$merchantData['countryIdDetected'] = $countryData['id'];
				}catch(Exception $e){
					$countryData = null;
				}

				$merchantData['registrationIpAddress'] = getRemoteIPAddress();

				soap_call_ejb('registerMerchant', soap_prepare_call($merchantData));

				//Add merchant to Salesforce
				addMerchantToSalesforce($merchantData['username'],
																$merchantData['countryIdDetected'],
																$merchantData['firstName'],
																$merchantData['lastName'],
																'[not provided]',
																$merchantData['emailAddress'],
																$merchantData['additionalInfo']);

				//Send Merchant Registration email
				sendMerchantEmail($merchantData['username'], $merchantData['emailAddress'], $merchantData['firstName']);

				$success_message = 'You have successfully registered as a merchant.';
				$_SESSION['user']['type'] = 'MIG33_AFFILIATE';
			}catch(Exception $e){
				$step = 2;
				$error_message = $e->getMessage();
			}
		} else {
			$step = 2;
		}
	}
}

emitHeader();


if($step == 1){
emitTitle("Become a Merchant");
?>
		<small><b>Buy discount credits from mig33</b></small><br/>
		<small>Sell to other users for a profit. Credits are used for cheap international phone calls, SMS, and many others mig33 features.</small><br/><br/>

		<small><b>Credits are easy to sell</b></small><br/>
		<small>mig33 calling rates are cheaper than calling cards. Anyone with a mobile phone can be your customer! Merchants receive free promotion to mig33 users.</small><br/><br/>

		<small><b>How do i start?</b></small><br/>

		<small>For as little as USD$5 you get a 10% discount.</small><br/>
		<br/>
		<small><a href="merchant.php?step=2">Register to become a Merchant</a></small><br/>
		<small><a href="testimonial.php">Read what other Merchants think</a></small><br/>
		<br/>
		<small>For more information, visit www.mig33.com on the Web and select the Merchant link.</small><br/>
		<br/>
		<small><a href="popular_rates.php?pf=BM">View mig33 Call Rates</a></small><br/>
		<br/>
		<small><a href="index.php">Back</a></small><br/>
		<small><a href="merchant_help.php?step=1">Help</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}else if($step == 2){
emitTitle("Merchant Registration");
		if(!empty($error_message)){
			print '<small><b><font style="color:red">*'.$error_message.'</font></b></small><br/>';
		}
		if(!empty($fn_error)){
			print '<small><b><font style="color:red">*'.$fn_error.'</font></b></small><br/>';
		}
		if(!empty($ln_error)){
			print '<small><b><font style="color:red">*'.$ln_error.'</font></b></small><br/>';
		}
		if(!empty($email_error)){
			print '<small><b><font style="color:red">*'.$email_error.'</font></b></small><br/>';
		}
		?>
		<form method="post" action="merchant.php">
		<input type="hidden" name="step" value="3" />
		<small><b>First Name:</b></small><br/>
		<input type="text" name="fn" value="<?=$fn?>" /><br/><br/>

		<small><b>Last Name:</b></small><br/>
		<input type="text" name="ln" value="<?=$ln?>" /><br/><br/>

		<small><b>Email address:</b></small><br/>
		<input type="text" name="email" value="<?=$email?>" /><br/>
		<input type="submit" name="Submit" value="Submit" /><br/>
		</form>
		<br/>

		<small><a href="merchant.php">Back</a></small><br/>
		<small><a href="merchant_help.php?step=2">Help</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}else if($step == 3){
emitTitle("Merchant Registration");
?>
		<small><b>Welcome aboard!</b></small><br/>
		<small>Congratulations! You are now a mig33 merchant.</small><br/>
		<br/>
		<small>Go to the <a href="merchant_center.php">Merchant Center</a> to buy discounted credits, get helpful sales info and tools, and more.</small><br/>
		<br/>
		<small><a href="merchant_help.php?step=3">Help</a></small><br/>
		<br/>
		<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
?>