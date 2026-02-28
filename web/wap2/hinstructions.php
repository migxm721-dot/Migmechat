<?php
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");

global $server_root;

$pf = $_GET['pf'];
if(!empty($_POST['pf'])){
	$pf = $_POST['pf'];
}

//See where the request it is coming from
$rego_success = $_GET['rego_success'];
if($_POST['rego_success']){
	$rego_success = $_POST['rego_success'];
}

if($_POST['manual']){
	$manual = $_POST['manual'];
} else {
	$manual = false;
}

if($_POST && !empty($_POST['mobile_vendor'])){
	$mobile_vendor = $_POST['mobile_vendor'];
}

if($_POST && !empty($_POST['mobile_phonemodel'])){
	$mobile_phonemodel = $_POST['mobile_phonemodel'];
}

$useragent = $_SERVER['HTTP_USER_AGENT'];
$useragent = strtolower($useragent);


emitHeader("Connection Errors and Instructions");
?>
	<div id="content">
		<div class="section">
<?php
	if(empty($mobile_vendor)){
		//Detect vendor from user agent
		detect_vendor($useragent);
	}

	if(empty($mobile_vendor)){
		//Show vendor list
?>
		<small><p><b>Please select your vendor:</b></p></small>
		<form action="hinstructions.php" method="POST">
		<input type="hidden" name="rego_success" value="<?=$rego_success?>" />
		<p>
			<small>Vendor:</small> <?=vendors_construct_combo("mobile_vendor"); ?><br/>
			<input type="submit" name="submit" value="Submit"/>
		</p>
		</form>
<?php
	} else {
  		if(empty($mobile_phonemodel)){
			if($mobile_vendor == 'Other'){
				$mobile_phonemodel = 'Other';
			} else {
				if(!$manual){
					//Detect models
					detect_phonemodel($mobile_vendor, $useragent);
				}
			}
		}

  		if(empty($mobile_phonemodel)){
			//Show phone model list
?>
		<small><p><b>Please select the model for your <?=$mobile_vendor?>:</b></p></small>
		<form action="hinstructions.php" method="POST">
		<input type="hidden" name="rego_success" value="<?=$rego_success?>" />
		<p>
			<input type="hidden" name="mobile_vendor" value="<?=$mobile_vendor?>" />
			<input type="hidden" name="manual" value="<?=$manual?>" />
			<small>Vendor: <?=$mobile_vendor?></small><br/>
			<small>Model: </small><?=handsets_construct_combo("mobile_phonemodel", $mobile_vendor); ?><br/>
			<input type="submit" name="submit" value="Submit"/>
		</p>
		</form>
<?php
		} else {
			//Retrieve / Detect MIDP layer
			if(empty($mobile_midp)){
				if($mobile_vendor != 'Other' && $mobile_phonemodel != 'Other'){
					retrieve_midp($mobile_vendor, $mobile_phonemodel);
				} else {
					detect_midp($useragent);
				}
			}

			if(empty($mobile_midp)){
				$mobile_midp = 'Not_Sure';
			}

			//Time to get the full details of the given parameters
			if($mobile_phonemodel == 'Other'){
				retrieve_defaulthandsetdetails($mobile_vendor, $mobile_midp);
			} else {
				retrieve_handsetdetails($mobile_vendor, $mobile_phonemodel);
			}

			if(!empty($mobile_instructiontext)){
				print '<small><b>Connection and Installation Notes</b></small><br/><br/>';
				$mobile_instructiontext = str_replace('[vendor]', $mobile_vendor, $mobile_instructiontext);
				$mobile_instructiontext = str_replace('[model]', $mobile_phonemodel, $mobile_instructiontext);
				print '<small>'.$mobile_instructiontext.'</small><br/><br/>';
			} else {
				print '<small>There are no additional information to be shown for the device that you selected. Please email to contact@mig33.com for further help.</small><br/><br/>';
			}
		}
  	}
?>

		<small><a href="member2/help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="contact.php?pf=HELP">Contact us</a></small><br/>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=login&v=wap">Home</a></small><br/>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>