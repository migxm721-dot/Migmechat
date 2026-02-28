<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$page = $_GET['page'];
$ppid = $_GET['ppid'];
$org = $_GET['org'];

emitHeader();

//Check if user is a merchant to begin with
if($_SESSION['user']['type'] == 1){
	//Not a merchant
	emitTitle("Merchant Center");
?>
		<small>This area is restricted to merchants only.</small><br/><br/>
		<small><a href="logout.php">Back to mig33 Home</a></small><br/>
	</body>
</html>
<?php
}else {
	emitTitle("Merchant Center");
?>
		<small>Welcome <?=$_SESSION['user']['username']?>. Here you will find all the tools you need to easily buy and sell mig33 credits.</small><br/><br/>
<?php
			//Retrieve all discount tiers for this user
			try{
				$discountTiers = soap_call_ejb('getDiscountTiers', array(0, $_SESSION['user']['username']));

				for ($i = 0; $i < sizeof($discountTiers); $i++){
					//ignore all inactive tiers
					if($discountTiers[$i]['canBeApplied']){
						if($discountTiers[$i]['type'] == 'FIRST_TIME_ONLY'){
							$sp_tier = $discountTiers[$i];
						} else {
		?>
			<small><b>Getting started. </b><br/>
			Making money with mig33 is all about buying and selling mig33 credits. With mig33 credits, users can do more with mig33. </small><br/>
			<small>There are two ways to begin.</small><br/><br/>

			<small><b>Option 1: Test how it works.  </b><br/>
			All you need is your mobile phone and friends. Firstly build up your credits, and practice making a few sales.
			Then make your first purchase of credits from as low as US$5, with our Starter Discount rate.</small> <br/>	<br/>

			<small><b>Option 2: First purchase, from US$5   </b><br/>
			If you know how it works, make your first purchase now. Usually you will need at least US$70 to make this worthwhile.</small>  <br/>
			<br/>
			<small><b>STARTER DISCOUNT:</b>However, for your first time only, you can buy from US$5 and receive the 30% Starter Discount rate.
			Payment is easy using local banking, Telegraphic Transfer or Western Union. </small>  <br/>
		<?php
							break;
						}
					}
				}
			}catch(Exception $te){}
		?>

		<ul>
			<li><small><a href="buy_credits.php">Buying Credits</a></small></li>
			<li><small><a href="sell_credits.php">Selling Credits</a></small></li>
		</ul>
		<br/>
		<?php
			//show starter pack if there is one
			if($sp_tier){
		?>
		<small><b>New:</b> Try the mig33 merchant program for as little as US$5 with a one-time Starter Discount of 30%.</small><br/><br/>
		<?php
			}

		?>
		<small>For more information, email <a href="mailto:merchant@mig33.com">merchant@mig33.com</a> or <a href="../contact.php">Contact Us</a> now.</small><br/><br/>

		<small><a href="center_help.php">Help</a></small><br/>
		<br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
?>

