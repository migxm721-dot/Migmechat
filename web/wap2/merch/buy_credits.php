<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$countryID = '';
$ppid = '';

emitHeader();
emitTitle("Buying Credits");

?>
<br/>
		<small>Select an option to buy discount credits.</small><br/>
		<ul>
		<?php
			try{
				if($ppid == ''){
					//Check if user's country of origin can support Bank transfer
					$countryID = $_SESSION['user']['countryID'];
					settype($countryID, "int");

					$ppid = soap_call_ejb('getBankTransferProductID', array($countryID));
				}

				if($ppid != '0'){
		?>
			<small><b>Get a 30% discount rate, when you buy mig33 credits from US$100.</b> </small><br/>
			<small>For example, buy $500 worth of mig33 credits, and pay only $350 (30% discount) </small><br/>
			<br/>
			<small><b>Bonus $100 level. </b> </small><br/>
			<small>If you're making smaller purchases, once you reach a total of US$100 mig33 credits purchased in one month, you get a bonus discount!
			<br/>We will give you the 30% discount for all prior purchases.  </small><br/><br/>
			<small>Eg. In one month, you purchase US$20 mig33 credits and do not qualify for discount.
			<br/>You pay US$20. Then you buy US$90 worth of credits. <br/>
			The total of your purchases for the month is now worth US$110 (US$20 + US$90).  </small><br/><br/>
			<small>So with the 30% discount, you pay US$63 (for the US$90 value). An adjustment is made for all prior purchases
			(add US$6. This is 30% of US$20 mig33 credits you already purchased) </small><br/>	<br/>

			<small><b>mig33,your partner in growing your business  </b> </small><br/>
			<small>Greater volume discounts may apply as you grow.  We're your partner in growing your mig33 merchant business and we're here to help.
			<br/><br/>Please email <a href="mailto:merchant@mig33.com">merchant@mig33.com</a> to apply for greater discounts for greater volume purchases  </small><br/><br/>
		<?php
				}
			}catch(Exception $e){
				//echo $e->getMessage();
			}
		?>
		<br/>
			<li><small><a href="recharge_wu.php?pf=BC">Western Union</a> (pay with cash)</small></li>
		<?php
			//Only if the user is a merchant do we need to check if discount applies
			try{
				$discountTiers = soap_call_ejb('getDiscountTiers', array(0, $_SESSION['user']['username']));
				for ($i = 0; $i < sizeof($discountTiers); $i++){
					//ignore all inactive tiers
					if($discountTiers[$i]['canBeApplied']){

						if($discountTiers[$i]['type'] == 'FIRST_TIME_ONLY'){
							$sp_tier = $discountTiers[$i];
							break;
						}
					}
				}
			}catch(Exception $te){}

			if(!empty($sp_tier)){
		?>
			<li><small><a href="https://<?= $_SERVER['HTTP_HOST'] ?>/merch/recharge_cc.php?pb=<?=$sp_tier['percentageDiscount']?>">Credit and Debit Cards</a> (Starter Discount only)</small></li>
		<?php
			}
		?>
			<li><small><a href="recharge_tt.php?pf=BC">Telegraphic Transfer</a></small></li>
		</ul>
		<small><a href="merchant_center.php">Back</a></small><br/>
		<small><a href="bc_help.php">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>