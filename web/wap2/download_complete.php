<?= '<?xml version="1.0"?>' ?>
<?php
include_once("../common/common-inc.php");

$mobile_vendor = $_GET['mobile_vendor'];
$mobile_phonemodel = $_GET['mobile_phonemodel'];
include_once("wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('Download Success');
?>
	<div id="content">
		<div class="section">

<?php
	//Check for blank parameters
	if(empty($mobile_vendor) || empty($mobile_phonemodel)){
?>
		<p>There is a problem while loading this page. Try Refreshing the page.</p><br/>
<?php
	} else {
		retrieve_handsetdetails($mobile_vendor, $mobile_phonemodel);
?>
		<p>Congratulations download is complete. To access mig33 it may be saved under games, downloads, applications or extras in your menu.</p><br/>
		<p>You will be prompted to enter your authentication code we sent you to the phone number you registered.</p><br/>
		<hr/>
<?php
		if(!empty($mobile_instructiontext)){
			print '<p><b>Installation Notes</b></p><br/>';
			$mobile_instructiontext = str_replace('[vendor]', $mobile_vendor, $mobile_instructiontext);
			$mobile_instructiontext = str_replace('[model]', $mobile_phonemodel, $mobile_instructiontext);
			print '<p>'.$mobile_instructiontext.'</p><br/>';
		}
	}
?>
		<p>mig33 can help you. Email contact@mig33.com .</p><br/>
		<a href="member2/t.php">Home</a><br/>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>









