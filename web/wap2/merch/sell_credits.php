<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");
session_start();
check_session_merchant();

emitHeader();
emitTitle("Selling Credits");
?>
		<small>Tools to advertise, sell and transfer credits to your customers.</small><br/>
		<ul>
			<li><small><a href="merchant_createuser.php">Create User</a> (get your customer started now)</small></li>
			<li><small><a href="invite_customers.php">Invite Customers</a> (send a free SMS. Earn bonus credit)</small></li>
			<li><small><a href="transfer_credit.php?pf=SC">Transfer Credits</a> (send credits to another mig33 account)</small></li>
			<li><small><a href="advertise.php?pf=SC">Free Advertising</a> (request advertising to users near you)</small></li>
		</ul>
		<small>View <a href="popular_rates.php?pf=SC">call rates</a> and <a href="sales_kit.php?page=mktg">sales tips</a>.</small><br/><br/>
		<small>You can create your own prepaid cards making mig33 vouchers. Visit www.mig33.com on the Web and sign in to the Merchant Center for more selling tools.</small><br/><br/>

		<small><a href="merchant_center.php">Back</a></small><br/>
		<small><a href="sc_help.php">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
		<br/>
	</body>
</html>