<?php
//include_once("../../common/common-inc.php");
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
//$prog = $_SESSION['prog'];
//include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

ice_check_session();
$userDetails = ice_get_userdata();
$ppid = '';

$pf = $_GET['pf'];

try{
	//Check if user's country of origin can support Bank transfer
	$countryID = $userDetails->countryID;
	settype($countryID, "int");

	//Check on local bank deposit
	$ppid = soap_call_ejb('getBankTransferProductID', array($countryID));

}catch (Exception $ex){}

emitHeader();
emitTitle("Recharge");
?>

		<?php
		// Display two merchants which the user may be able to purchase credits from
		try {
  			$merchantUsernames = soap_call_ejb('getMerchantsUserMayPurchaseFrom', array($userDetails->username));
  			if (!empty($merchantUsernames[0])) {
  				print '<small>You may be able to buy credits from:</small><br/>';
  				print '<ul><li><small><a href="../member2/view_profile_wap.php?username=' . $merchantUsernames[0] . '">' . $merchantUsernames[0] . '</a>';
  				if (!empty($merchantUsernames[1]))
	  				print ' (friend of <a href="../member2/view_profile_wap.php?username=' . $merchantUsernames[1] . '">' . $merchantUsernames[1] . '</a>)';
	  			print '</small></li>';
	  			if (!empty($merchantUsernames[2])) {
	  				print '<li><small><a href="../member2/view_profile_wap.php?username=' . $merchantUsernames[2] . '">' . $merchantUsernames[2] . '</a>';
	  				if (!empty($merchantUsernames[3]))
		  				print ' (friend of <a href="../member2/view_profile_wap.php?username=' . $merchantUsernames[3] . '">' . $merchantUsernames[3] . '</a>)';
		  			print '</small></li>';
		  		}
		  		print '</ul><br/>';
		  	}
  		} catch(Exception $e) {}
  		?>

  		<small>Select how to add credits to your account.</small><br/>
  		<ul>
			<?php
			// Display "Find a Local Merchant" link if there are any merchant locations in the user's country
			try {
	  			$countryHasMerchantLocations = soap_call_ejb('countryHasMerchantLocations', array($countryID));
	  		} catch(Exception $e) {}

			if ($countryHasMerchantLocations)
				print '<li><small><a href="find_merchant.php">Find a Local Store / Merchant</a></small></li>';

	  		// Only display the Bank Deposit link if the user's country has the facility, AND the user is not a non-merchant Indonesian
	  		if( $countryID == 20 )
	  		{
	  		?>
			<li><small><a href="recharge_psms.php?ppid=<?=$ppid?>&amp;pf=RO">Premium SMS (Bangladesh)</a></small></li>
			<?php
	  		}
			if($ppid != '0' && !($userDetails->type == 1 && $countryID == 107)){
			?>
			<li><small><a href="recharge_bt.php?ppid=<?=$ppid?>&amp;pf=RO">Local Bank Deposit</a></small></li>
			<?php
			}
			?>
			<li><small><a href="recharge_wu.php?pf=RO">Western Union</a></small></li>
			<li><small><a href="recharge_voucher.php?pf=RO">Redeem a Voucher</a></small></li>
			<li><small><a href="https://<?= $_SERVER['HTTP_HOST'] ?>/member/recharge_cc.php?pf=RO">Credit and Debit Cards</a></small></li>
			<?php if($userDetails->type > 1) { ?>
				<li><small><a href="recharge_tt.php?pf=RO">Telegraphic Transfer</a></small></li>
			<?php } ?>
			<li><small><a href="recharge_mb.php?pf=RO">Moneybookers</a></small></li>
		</ul>
		<?php
		if($pf == 'HOME'){
		?>
		<small><a href="../member2/t.php?cmd=home">Back</a></small><br/>
		<?php
		} else {
		?>
		<small><a href="index.php">Back</a></small><br/>
		<?php
		}
		?>

		<small><a href="recharge_help.php">Help</a></small><br/>
		<br/>
  		<small><a href="index.php">My Account Home</a></small><br/>
  		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
  		<br/>
	</body>
</html>
