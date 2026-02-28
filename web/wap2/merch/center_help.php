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
emitTitle("Merchant Center Help");
?>

		<ul>
			<li><small><a href="merch_whatis_help.php">What is the merchant program? </a></small></li>
			<li><small><a href="merch_purch_help.php">How do I purchase credits?</a></small></li>
			<li><small><a href="merch_sell_help.php">How do I sell credits?</a></small></li>
			<li><small><a href="merch_profit_help.php">How do I make a profit?</a></small></li>
			<li><small><a href="merch_cust_help.php">Where do I find my customers? </a></small></li>
			<li><small><a href="merch_more_help.php">Where can I go for more help?</a></small></li>
		</ul>

		<br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
  	</body>
</html>