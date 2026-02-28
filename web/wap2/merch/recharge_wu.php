<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();




$amount = '';
$yourname = '';
$error = '';
$step = 1;
$westernunionData = '';
$totalWithDiscount = '';
$discount = '';

$pr = '';
$cn = '';
$cc = '';
$ctry = '';

$yourname_error = '';
$amount_error = '';

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

//Get the supported currency for local bank deposit type
$currency_wu = get_currency_wu($_SESSION['user']['countryID']);

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

	if(isset($_POST['amount'])){
		$amount = $_POST['amount'];
		if(!is_numeric($amount)){
			$amount_error = 'Enter a number for amount.';
			$error = 'true';
		}
	}

	if($amount == ''){
		$amount_error = 'Enter amount.';
		$error = 'true';
	}

	if(isset($_POST['yourname'])){
		$yourname = $_POST['yourname'];
	}

	if($yourname == ''){
		$yourname_error = 'Enter your name.';
		$error = 'true';
	}

	//if no error, continue
	if($error == ''){
		if($step == 2){
			$step = 3;
		}

		if($step == 3){
			if($pr == ''){
				try{
					$countryID = $_SESSION['user']['countryID'];
					settype($countryID, "int");
					settype($amount, "double");

					$westernunionData = soap_call_ejb('westernUnionPayment', array($_SESSION['user']['username'], '' /* wuid */, $countryID, $yourname, $amount, $currency_wu));

					if(isset($westernunionData['paymentReference'])){
						$pr = $westernunionData['paymentReference'];
					}
					if(isset($westernunionData['paymentDiscount'])){
						$totalWithDiscount = $westernunionData['paymentDiscount'];
					}
					if(isset($westernunionData['companyName'])){
						$cn = $westernunionData['companyName'];
					}
					if(isset($westernunionData['companyCode'])){
						$cc = $westernunionData['companyCode'];
					}
					if(isset($westernunionData['countryCode'])){
						$ctry = $westernunionData['countryCode'];
					}

					$url = ereg_replace(' ', '%20','pf='.$pf.'&amp;pr='.$pr.'&amp;cn='.$cn.'&amp;cc='.$cc.'&amp;ctry='.$ctry.'&amp;amount='.$amount.'&amp;yourname='.$yourname);
				}catch(Exception $e){
					$step = 2;
					$error_message = $e->getMessage();
				}
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
				<li><small>Your account is credited after you make a payment by selecting the link below.</small></li>
				<li><small>Go to a Western Union agent. Take your photo ID.</small></li>
				<li><small>Fill out the Blue Form, called Quick Pay or Payment Services form.</small></li>
				<li><small>Give the money to the agent. They will give you a receipt. Please keep this receipt as proof of payment as it may be required by use to track your payment progress..</small></li>
				<li><small>We will send you an SMS when we have recharged your account.</small></li>
			</ol>
			<small><center><a href="recharge_wu.php?step=2&amp;pf=<?=$pf?>">Start Western Union Payment</a></center></small><br/><br/>
			<small>Visit www.mig33.com on the Web for example forms.</small><br/><br/>
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
			<small>Get payment details.</small><br/><br/>
		<?php
				if($error_message != ''){
		?>
			<small><b><font style="color:red">*There is an error while requesting for Western Union details. <?=$error_message?></font></b></small><br/>
		<?php
				}

				//Show any input errors
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
		?>
			<br/>
			<form method="post" action="recharge_wu.php">
                <input type="hidden" name="step" value="<?=$step?>" />
			<input type="hidden" name="pf" value="<?=$pf?>" />
			<small><b>Your Name:</b></small><br/>
			<input type="text" name="yourname" value="<?=$yourname?>" alt="Your Name" /><br/><br/>
			<small><b>Amount (<?=$currency_wu?>):</b></small><br/>
			<input type="text" name="amount" value="<?=$amount?>" alt="Amount" /><br/><br/>
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
								print ' (Approx. '.$userDetails['currency'].'$'.round_twodec($approx_inlocal).') or more.</small><br/>* additional discounts available at higher amounts. Contact mig33 to find out more.<br/><br/>';
							} else {
								print ' or more.<br/>* additional discounts available at higher amounts. Contact mig33 to find out more.</small><br/><br/>';
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
	emitTitle("Western Union Payment Details");

			settype($amount, 'double');

			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(4, $_SESSION['user']['username'], $amount));
			}catch(Exception $o){}

			if(!isset($applicableDiscountTier['discountAmount'])){
		?>
			<small>You will get approx* <?=$currency_wu?>$<?=$amount?> in credits. If you wanted a discount, go '<a href="recharge_wu.php?step=2&amp;yourname=<?=$yourname?>&amp;pf=<?=$pf?>">Back</a>' to enter a higher amount.</small><br/><br/>

		<?php
			} else {
		?>
			<small>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</small><br/><br/>
		<?php
			}
		?>
			<small>***Write these down***</small><br/><br/>
			<small>Account Number:</small><br/>
			<small><b><?=$pr?></b></small><br/><br/>
			<small>Company Name:</small><br/>
			<small><b><?=$cn?></b></small><br/><br/>
			<small>Company Code:</small><br/>
			<small><b><?=$cc?></b></small><br/><br/>
			<small>Country:</small><br/>
			<small><b><?=$ctry?></b></small><br/><br/>
			<small>Your Name:</small><br/>
			<small><b><?=$yourname?></b></small><br/><br/>
			<small>Deposit Amount:</small><br/>
			<small><b><?=$currency_wu?>$<?=$amount?></b></small><br/><br/>

			<small>Go to a Western Union agent. Use the Blue Quick Pay or Payment Services form.</small><br/><br/>
			<small>Pay the Western Union fee, usually USD$15.</small><br/><br/>
			<small>Keep your receipt. We will send you an SMS when we have recharged your account.</small><br/><br/>
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