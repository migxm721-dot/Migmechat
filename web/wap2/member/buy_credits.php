<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];

//Check async messages
checkServerSessionStatus();

ice_check_session();
$userDetails = ice_get_userdata();
$countryID = '';
$ppid = '';

emitHeader();
emitTitle("Buying Credits");
?>

		<small>Select an option to buy discount credits.</small><br/>
		<ul>
		<?php
			try{
				if($ppid == ''){
					//Check if user's country of origin can support Bank transfer
					$countryID = $userDetails->countryID;
					settype($countryID, "int");

					$ppid = soap_call_ejb('getBankTransferProductID', array($countryID));
				}

				if($ppid != '0'){
		?>
			<li><small><a href="recharge_bt.php?ppid=<?=$ppid?>&amp;pf=BC">Local Bank Deposit</a> (send money to our bank in your country)</small></li>
		<?php
				}
			}catch(Exception $e){
				//echo $e->getMessage();
			}
		?>
			<li><small><a href="recharge_wu.php?pf=BC">Western Union</a> (pay with cash)</small></li>
			<li><small><a href="https://<?= $_SERVER['HTTP_HOST'] ?>/member/recharge_cc.php?pf=BC">Credit and Debit Cards</a></small></li>
			<li><small><a href="recharge_tt.php?pf=BC">Telegraphic Transfer</a></small></li>
			<li><small><a href="recharge_mb.php?pf=BC">Moneybookers</a></small></li>
		</ul>
		<small><a href="getting_started.php">Back</a></small><br/>
		<small><a href="center_help.php">Help</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<?php
		if($_SESSION['pf'] == 'MYACCOUNT'){
		?>
		<small><a href="index.php">My Account Home</a></small><br/>
		<?php
		}
		?>
		<br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
	</body>
</html>
