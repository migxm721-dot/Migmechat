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
emitTitle("Recharge Help");

?>
		<small>You can use your mig33 credits for many things!</small><br/>
		<ol>
			<li><small>Cheap international calls</small></li>
			<li><small>Premium emoticons</small></li>
			<li><small>International SMS</small></li>
			<li><small>Buzz, Lookout, Email notifications</small></li>
			<li><small>Mobile content, games, ringtones and more coming soon!</small></li>
		</ol>

		<small>
		Which method should you choose?<br/>
		Whatever is easiest for you. No credit card?  Go to your local bank or Western Union to make a payment.  Or purchase vouchers from a merchant advertised in your country.<br/><br/>

		How soon will I get my credits?<br/>
		Instantly with credit card and vouchers and within 5 working days for Bank Transfer or Western Union.  Credits can be used as soon as they are credited in your account.<br/><br/>

		Where can I get more help?<br/>
		Please email our customer service at contact@mig33.com and one of our staff will get back to you as soon as possible.<br/><br/>
		</small>

 		<br/>
		<small><a href="recharge.php">Back to Recharge Options</a></small><br/>
		<br/>
   </body>
</html>
