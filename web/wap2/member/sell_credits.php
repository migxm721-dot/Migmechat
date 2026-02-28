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

emitHeader();
emitTitle("Selling Credits");
?>
		<br/><small>mig33 makes selling easy.  We have several tools here to help you with selling credits.
		These tools include setting up your customers and selling your credits to them.</small><br/><br/>

		<small><b>BUILDING YOUR CUSTOMER BASE</b></small><br/>

		<ul>
			<li><small><a href="merch_create_user.php">Create User</a> (get your customer started now)</small></li>
			<li><small><a href="invite_customer_intro.php">Invite Customers</a> (send a free SMS. Earn bonus credit)</small></li>
		</ul>

		<small><b>SELLING YOUR CREDITS TO YOUR CUSTOMERS</b></small><br/>

		<ul>
			<li><small><a href="transfer_credit_intro.php">Transfer Credits</a> (send credits to another mig33 account)</small></li>
			<li><small><a href="free_advertising_intro.php">Free Advertising</a> (request advertising to users near you)</small></li>
		</ul>

		<small>View <a href="popular_rates.php?pf=SC">call rates</a> and <a href="sales_kit.php?page=mktg">sales tips</a>.</small><br/><br/>

		<small><a href="getting_started.php">Back</a></small><br/>
		<small><a href="center_help.php">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
		<br/>
	</body>
</html>