<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$ppid = '';
$amount = '';
$yourname = '';
$error = '';
$step = 1;
$bankTransferData = '';
$totalWithDiscount = '';
$discount = '';

$pr = '';
$ah = '';
$bn = '';
$ban = '';
$url = '';

$yourname_error = '';
$amount_error = '';

$pf = $_GET['pf']; //Page from, tracks where user is coming from. BC = Buyin credits, RO = Recharge options
if($pf == ''){
	$pf = $_POST['pf'];
}

//Get the supported currency for local bank deposit type
$currency_lbd = get_currency_lbd($_SESSION['user']['countryID']);

//Sets the ppid, it should be from a GET or POST
if($_GET){
	if(isset($_GET['ppid'])){
		$ppid = $_GET['ppid'];
	}

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
	if(isset($_GET['ah'])){
		$ah = $_GET['ah'];
	}
	if(isset($_GET['bn'])){
		$bn = $_GET['bn'];
	}
	if(isset($_GET['ban'])){
		$ban = $_GET['ban'];
	}
	if(isset($_GET['pr'])){
		$pr = $_GET['pr'];

		//We know here must be a PR so we set the url again
		$url = ereg_replace(' ', '%20','ppid='.$ppid.'&amp;pf='.$pf.'&amp;pr='.$pr.'&amp;ah='.$ah.'&amp;bn='.$bn.'&amp;ban='.$ban.'&amp;amount='.$amount.'&amp;yourname='.$yourname);
	}

}

if($_POST){
	if($_POST['ppid']){
		$ppid = $_POST['ppid'];
	}

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

	if(isset($_POST['yourname'])){
		$yourname = $_POST['yourname'];
	}

	//Check if all fields have been filled up
	if($amount == ''){
		$amount_error = 'Enter amount.';
		$error = 'true';
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
					settype($ppid, "int");
					settype($countryID, "int");
					settype($amount, "double");
					$bankTransferData = soap_call_ejb('bankTransfer', array($_SESSION['user']['username'],$ppid,$countryID,$yourname, '', $amount, $currency_lbd));

					if(isset($bankTransferData['paymentReference'])){
						$pr = $bankTransferData['paymentReference'];
					}
					if(isset($bankTransferData['accountHolder'])){
						$ah = $bankTransferData['accountHolder'];
					}
					if(isset($bankTransferData['bankName'])){
						$bn = $bankTransferData['bankName'];
					}
					if(isset($bankTransferData['bankAccountNumber'])){
						$ban = $bankTransferData['bankAccountNumber'];
					}
				}catch(Exception $e){
					$step = 2;
					$error_message = $e->getMessage();
				}
			}

			$url = ereg_replace(' ', '%20','ppid='.$ppid.'&amp;pf='.$pf.'&amp;pr='.$pr.'&amp;ah='.$ah.'&amp;bn='.$bn.'&amp;ban='.$ban.'&amp;amount='.$amount.'&amp;yourname='.$yourname);
		}
	}
}

emitHeader();

if($step == 1){
	//Show Bank Deposit overview

	emitTitle("Local Bank Deposit");
	?>
			<small><b>How do i do a Local Bank Deposit?</b></small><br/>
			<ol>
				<li><small>Your account is credited after you make a bank deposit by selecting the link below.</small></li>
				<li><small>Go to a bank where mig33 has an account. We will tell you.</small></li>
				<li><small>Fill out a Bank Deposit form with the details.</small></li>
				<li><small>Give the money to the bank staff. They will give you a receipt. Please keep this receipt as proof of payment as it may be required by use to track your payment progress.</small></li>
				<li><small>We will send you an SMS when we have recharged your account (3-5 days).</small></li>
			</ol>
			<small><center><a href="recharge_bt.php?step=2&amp;ppid=<?=$ppid?>&amp;pf=<?=$pf?>">Start Local Bank Deposit</a></center></small><br/><br/>
			<small>Visit www.mig33.com on the Web for example forms.</small><br/><br/>
			<small><a href="buy_credits.php">Back</a></small><br/>
			<small><a href="bt_help.php?ppid=<?=$ppid?>&amp;pf=<?=$pf?>">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
	<?php
}else if($step == 2){
	emitTitle("Local Bank Deposit");
	?>
			<small>Get bank details.</small><br/>
		<?php
			if(!isset($ppid)){
		?>
			<small><font style="color:red">There was some difficulties loading the page. Please try again later.</font></small><br/>
			<small><a href="buy_credits.php">Back to Buying Credits</a></small><br/><br/>
		<?php
			} else {
				//Show if theres any input errors
				if($error_message != ''){
		?>
			<small><b><font style="color:red">*There is an error while requesting for Bank deposit details. <?=$error_message?></font></b></small><br/>
		<?php
				}

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
			<form method="post" action="recharge_bt.php">
			<input type="hidden" name="ppid" value="<?=$ppid?>" />
			<input type="hidden" name="step" value="<?=$step?>" />
			<input type="hidden" name="pf" value="<?=$pf?>" />
			<small><b>Your Name:</b></small><br/>
			<input type="text" name="yourname" value="<?=$yourname?>" alt="Your Name" /><br/>
			<br/>
			<small><b>Amount (<?=$currency_lbd?>):</b></small><br/>
			<input type="text" name="amount" value="<?=$amount?>" alt="Amount" /><br/>
			<br/>
			<input type="submit" name="Submit" value="Submit" /><br/>
			</form>
		<?php
				//Retrieve all discount tiers for this user
				try{
					$discountTiers = soap_call_ejb('getDiscountTiers', array(3, $_SESSION['user']['username']));

					for ($i = 0; $i < sizeof($discountTiers); $i++){
						//ignore all inactive tiers
						if($discountTiers[$i]['canBeApplied'] && $discountTiers[$i]['percentageDiscount'] == 30){
		?>
			<small>Get a <?=round_twodec($discountTiers[$i]['percentageDiscount'])?>% discount for <?=$discountTiers[$i]['currency']?>$<?=round_twodec($discountTiers[$i]['displayMin'])?><?php
							if($currency_lbd != $_SESSION['user']['currency']){
								$approx_inlocal = ($discountTiers[$i]['displayMin'] / get_exchangeRate($currency_lbd)) * get_exchangeRate($_SESSION['user']['currency']);
								print ' (Approx. '.$_SESSION['user']['currency'].'$'.round_twodec($approx_inlocal).') or more.<br/>* additional discounts available at higher amounts. Contact mig33 to find out more.</small><br/>';
							} else {
								print 'or more.<br/>* additional discounts available at higher amounts. Contact mig33 to find out more.</small><br/>';
							}

							if($discountTiers[$i]['type'] != 'FIRST_TIME_ONLY'){
								break;
							}
						}
					}

					if(sizeof($discountTiers) > 0){
						print '<br/>';
					}
				}catch(Exception $te){}
			}
		?>
			<small><a href="recharge_bt.php?ppid=<?=$ppid?>&amp;step=1&amp;pf=<?=$pf?>">Back</a></small><br/>
			<small><a href="bt_help.php?ppid=<?=$ppid?>&amp;pf=<?=$pf?>&amp;step=2">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
	<?php
}else if($step == 3){
	emitTitle("Local Bank Deposit Details");

			settype($amount, 'double');

			try{
				$applicableDiscountTier = soap_call_ejb('getApplicableDiscountTier', array(3, $_SESSION['user']['username'], $amount));
			}catch(Exception $o){}

			if(!isset($applicableDiscountTier['discountAmount'])){
		?>
			<small>You will get approx* <?=$currency_lbd?>$<?=$amount?> in credits.<?php
				if($_SESSION['user']['type'] > 1){
					print 'If you wanted a discount, go "<a href="recharge_bt.php?ppid='.$ppid.'&amp;step=2&amp;yourname='.$yourname.'&amp;pf='.$pf.'">Back</a>" to enter a higher amount.</small><br/><br/>';
				} else {
					print '</small><br/><br/>';
				}
			} else {
		?>
			<small>You got a <?=round_twodec($applicableDiscountTier['percentageDiscount'])?>% discount (approx* <?=$applicableDiscountTier['currency']?>$<?=round_twodec($applicableDiscountTier['discountAmount'] + $amount)?> in credits).</small><br/><br/>
		<?php
			}
		?>
			<small>***Write these down***</small><br/>
			<small>Reference Number:</small><br/>
			<small><b><?=$pr?></b></small><br/><br/>
			<small>Account Holder:</small><br/>
			<small><b><?=$ah?></b></small><br/><br/>
			<small>Bank:</small><br/>
			<small><b><?=$bn?></b></small><br/><br/>
			<small>Account Number:</small><br/>
			<small><b><?=$ban?></b></small><br/><br/>
			<small>Your Name:</small><br/>
			<small><b><?=$yourname?></b></small><br/><br/>
			<small>Deposit Amount:</small><br/>
			<small><b><?=$currency_lbd?>$<?=$amount?></b></small><br/><br/>

			<small>Go to any <?=$bn?> branch or ask your bank to transfer funds.</small><br/><br/>
			<small>In order for your mig33 account to be credited, write your Reference Number on the Bank Deposit Form.</small><br/><br/>
			<small>Keep your receipt. We will send you an SMS when we have recharged your account.</small><br/><br/>
			<small>*The amount credited into your account will vary depending on currency exchange.</small><br/><br/>

			<small><a href="buy_credits.php">Back to Buying Credits</a></small><br/>
			<small><a href="bt_help.php?<?=$url?>&amp;step=3">Help</a></small><br/>
			<br/>
			<small><a href="merchant_center.php">Merchant Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>
	<?php
}
	?>