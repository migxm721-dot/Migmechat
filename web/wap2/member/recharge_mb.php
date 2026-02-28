<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
includeLanguagePack();
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
?>

<html>
<?php
emitTitle("Moneybookers");
?>

			<small><b><?=MONEYBOOKERS_STEP?></b></small><br/>
			<ol>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_1?></small></li>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_2?></small></li>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_3?></small></li>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_4?></small></li>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_5?></small></li>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_6?></small></li>
				<li><small><?=MONEYBOOKERS_DEVICE_STEP_7?></small></li>
			</ol>
			<br/>
			<?php
				if($pf == 'RO'){
			?>
			<small><a href="recharge.php">Return to Recharge Options</a></small><br/>
			<?php
				}else if($pf == 'BC'){
			?>
			<small><a href="buy_credits.php">Return to Buying Credits</a></small><br/>
			<?php
				}
			?>
			<br/>
			<?php
				if($userDetails->type > 1){
			?>
			<small><a href="merchant_center.php">Merchant Center</a></small><br/>
			<?php
				} else {
			?>
			<small><a href="recharge.php">Recharge Options</a></small><br/>
			<?php
				}
			?>
			<small><a href="index.php">My Account Home</a></small><br/>
			<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
			<small><a href="logout.php">Logout</a></small><br/>
			<br/>
		</body>
	</html>

