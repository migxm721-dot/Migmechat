<?php
include_once("../../common/common-inc.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$amount = '';
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
$currency_wu = get_currency_wu($_SESSION['user']['countryID']);

$countryID = $_SESSION['user']['countryID'];

$username = $_SESSION['user']['username'];

try{
	$country = get_country($countryID);
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

		$url = ereg_replace(' ', '%20','pf='.$pf.'&amp;pr='.$pr.'&amp;cn='.$cn.'&amp;cc='.$cc.'&amp;ctry='.$ctry.'&amp;amount='.$amount.'&amp;yourname='.$yourname);
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
		$email_error = 'Please enter your email address.';   // REQUIRED FIELD
		$error = 'true';
	}

	if($_POST['answer']){
		$answer = $_POST['answer'];
	} else {
		//$answer_error = 'Please enter the city name.';   // NOT A REQUIRED FIELD
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

			try{
				sendWesternUnionEmail($username, $firstname, $lastname, $mtcn_number, $city, $country, $email, $amountUSD, $currency_wu, $amount, $answer);
				$success_message = 'Thank you for submitting a Western Union notification to migme.';
					}
			catch(Exception $e){
					$error_message = $e->getMessage();
				}
			}
		}
	}

emitHeader();
if($step == 1){
	//Show Western Union overview
	emitTitle("Western Union");
	?>
			<small><b>How do I send payment using Western Union?</b></small><br/>

			<ol>
				<li><small><b>migme's Western Union details:</b><br/>
					Recipient: <b>Jeffrey Tien-Huang Lim</b><br/>
					City: <b>Singapore</b><br/>
					Country: <b>Singapore</b><br/>
				</small></li>
				<li><small>Go to your nearest Western Union agent and fill out the GREEN form. Don't forget to bring Picture ID. You will need to write the information above on the GREEN form. </small></li>
				<li><small>Give the money to the agent and they will print you a receipt with a MTCN number. Keep it!</small></li>
				<li><small>Tell migme that you have sent payment by submitting a Western Union Notification.</small></li>
				<li><small>We will process your payment immediately and send you an SMS when we have credited your migme account.</small></li>
			</ol>
			<small><center><a href="recharge_wu.php?step=2&amp;pf=<?=$pf?>">Submit a Western Union Notification</a></center></small><br/><br/>
			<small>Visit www.mig.me on the Web for example forms.</small><br/><br/>
			<?php
				if($pf == 'RO'){
			?>
			<small><a href="recharge.php">Back</a></small><br/>
			<?php
				}else if($pf == 'BC'){
			?>
			<small><a href="buy_credits.php">Back</a></small><br/>
			<?php
				}
			?>
			<small><a href="wu_help.php?pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
	<?php
}else if($step == 2){
	emitTitle("Western Union");
	?>
			<br/>
			<small><b>Submit WU Notification</b></small><br/><br/>
			<small><b>Step 1: Go to Western Union</b></small><br/>
			<small><b>Step 2: Submit Payment Receipt Below</b></small><br/>

		<?php
				if($error_message != ''){
		?>
			<small><b><font style="color:red">*There is an error while requesting for Western Union details. <?=$error_message?></font></b></small><br/>
		<?php
				}

				//Show any input errors

				if($username_error != ''){
		?>
			<small><b><font style="color:red">*<?=$username_error?></font></b></small><br/>
		<?php
				}

				if($firstname_error != ''){
		?>
			<small><b><font style="color:red">*<?=$firstname_error?></font></b></small><br/>
		<?php
				}

				if($lastname_error != ''){
		?>
			<small><b><font style="color:red">*<?=$lastname_error?></font></b></small><br/>
		<?php
				}

				if($city_error != ''){
		?>
			<small><b><font style="color:red">*<?=$city_error?></font></b></small><br/>
		<?php
				}

				if($country_error != ''){
		?>
			<small><b><font style="color:red">*<?=$country_error?></font></b></small><br/>
		<?php
				}

				if($email_error != ''){
		?>
			<small><b><font style="color:red">*<?=$email_error?></font></b></small><br/>
		<?php
				}

				if($mtcn_number_error != ''){
		?>
			<small><b><font style="color:red">*<?=$mtcn_number_error?></font></b></small><br/>
		<?php
				}

				if($amount_error != ''){
		?>
			<small><b><font style="color:red">*<?=$amount_error?></font></b></small><br/>
		<?php
				}

				if($paidto_error != ''){
		?>
			<small><b><font style="color:red">*<?=$paidto_error?></font></b></small><br/>
		<?php
				}

			// Serve the form, possibly with errors. If errors are detected, echo error=true in the query string of the tracking image below:
			$url = $_SERVER['SCRIPT_NAME'];
			print "<img src='/tr.php?turl=".$url."&amp;terror=".$error."'></img>";

		?>
			<br/>
			<form method="post" action="recharge_wu.php">
			<input type="hidden" name="step" value="<?=$step?>" />
			<input type="hidden" name="pf" value="<?=$pf?>" />
			<small><b>migme username:</b></small><br/>
			<input type="text" name="username" value="<?=$username?>" alt="migme User Name" /><br/>
			<small><b>First Name:</b></small><br/>
			<input type="text" name="firstname" value="<?=$firstname?>" alt="First Name" /><br/>
			<small><b>Last Name:</b></small><br/>
			<input type="text" name="lastname" value="<?=$lastname?>" alt="Last Name" /><br/>
			<small><b>MTCN Number:</b></small><br/>
			<input type="text" name="mtcn_number" value="<?=$mtcn_number?>" alt="MTCN Number" /><br/>
			<small><b>City:</b></small><br/>
			<input type="text" name="city" value="<?=$city?>" alt="City" /><br/>
			<small><b>Country:</b></small><br/>
			<input type="text" name="country" value="<?=$country?>" alt="Country" /><br/>
			<small><b>Email Address:</b></small><br/>
			<input type="text" name="email" value="<?=$email?>" alt="Email" /><br/>
			<small><b>Amount Sent in (<?=$currency_wu?>):</b></small><br/>
			<input type="text" name="amount" value="<?=$amount?>" alt="Amount" /><br/><br/>
			<small><b>Paid To:</b></small><br/>
			<small>Jeffrey Tien-Huang Lim</small><br/>
			<input type="hidden" name="paidto" value="JeffreyTienHuangLim">
			<small><b>Answer to secret question:</b></small><br/>
			<input type="text" name="answer" value="<?=$answer?>" alt="Answer" /><br/>
			<input type="submit" name="Submit" value="Submit" /><br/>
			</form>

		<?php
			//Retrieve all discount tiers for this user
			try{
					$discountTiers = soap_call_ejb('getDiscountTiers', array(4, $_SESSION['user']['username']));

				for ($i = 0; $i < sizeof($discountTiers); $i++){
					//ignore all inactive tiers
					if($discountTiers[$i]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == 30){
		?>
			<small>Get a <?=round_twodec($discountTiers[$i]['percentageDiscount'])?>% discount for <?=$discountTiers[$i]['currency']?>$<?=round_twodec($discountTiers[$i]['displayMin'])?><?php
							if($currency_wu != $userDetails['currency']){
								$approx_inlocal = ($discountTiers[$i]['displayMin'] / get_exchangeRate($currency_wu)) * get_exchangeRate($userDetails['currency']);
								print ' (Approx. '.$userDetails['currency'].'$'.round_twodec($approx_inlocal).') or more.</small><br/>* additional discounts available at higher amounts. Contact migme to find out more.<br/><br/>';
						} else {
								print ' or more.<br/>* additional discounts available at higher amounts. Contact migme to find out more.</small><br/><br/>';
						}

						if($discountTiers[$i]['type'] != 'FIRST_TIME_ONLY'){
							break;
						}
					}
				}
			}catch(Exception $te){}
		?>
			<small><a href="recharge_wu.php?step=1&amp;pf=<?=$pf?>">Back</a></small><br/>
			<small><a href="wu_help.php?step=2&amp;pf=<?=$pf?>">Help</a></small><br/>

			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
	<?php
}else if($step == 3){
	emitTitle("Western Union Notification Details");

			// tracking image, will denote success from failure and errors conditions of the form page (below) being served
			print "<img src='/tr.php?turl=".$url."&amp;tstate=success'></img>";

			settype($amount, 'double');

			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(4, $_SESSION['user']['username'], $amount));
			}catch(Exception $o){}

			if(!isset($applicableDiscountTier['discountAmount'])){
		?>
			<small>You will get approx* $<?=amountUSD?> USD in credits. If you wanted a discount, go '<a href="recharge_wu.php?step=2&amp;yourname=<?=$yourname?>&amp;pf=<?=$pf?>">Back</a>' to enter a higher amount.</small><br/><br/>

		<?php
			} else {
		?>
			<small>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</small><br/><br/>
		<?php
			}
		?>



			<small>*The amount credited into your account will vary depending on currency exchange.</small><br/><br/>

			<small><a href="buy_credits.php">Back to Buying Credits</a></small><br/>
			<small><a href="wu_help.php?<?=$url?>&amp;step=3">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
	<?php
}
	?>
