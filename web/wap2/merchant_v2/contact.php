<?php
$commonIncPath = "../../common/common-inc.php";
include_once("../wap_includes/wap_functions.php");
include("includes.php");
putenv("pagelet=true");

session_start();

$pf = $_GET['pf'];
if(!empty($_POST['pf'])){
	$pf = $_POST['pf'];
}

//If from session set known variables
if($_SESSION['user']['username']){
	$username = $_SESSION['user']['username'];
}
if($_SESSION['user']['emailAddress']){
	$email = $_SESSION['user']['emailAddress'];
}
if($_SESSION['user']['mobilePhone']){
	$mobile = $_SESSION['user']['mobilePhone'];
}

if($_POST) {
	if(!empty($_POST['username'])){
		$username = trim($_POST['username']);
	}

	if(!empty($_POST['email'])){
		$email = trim($_POST['email']);
		if(!checkEmail($email)){
			$error = 'true';
			$email_error = 'Incorrect email address format.';
		}
	} else {
		$email = '';
		$error = 'true';
		$email_error = 'Enter email address.';
	}

	if(!empty($_POST['mobile'])){
		$mobile = trim($_POST['mobile']);
	} else {
		$username = '';
		$error = 'true';
		$mobile_error = 'Enter mobile number.';
	}

	if(!empty($_POST['mobilemodel'])){
		$mobilemodel = trim($_POST['mobilemodel']);
	} else {
		$mobilemodel = '';
		$error = 'true';
		$mobilemodel_error = 'Enter phone make / model.';
	}

	if(!empty($_POST['topic'])){
		$topic = trim($_POST['topic']);
	} else {
		$error = 'true';
		$topic_error = 'Choose a subject.';
	}

	if(!empty($_POST['message'])){
		$message = trim($_POST['message']);
	} else {
		$error = 'true';
		$message_error = 'Enter your message.';
	}

	$country = trim($_POST['country']);
	$mobile_provider = trim($_POST['mobilenetwork']);

	if (empty($error)) {
		//Email is valid, prepare and send message
		$message = "
		Message from wapsite :
		username	--> $username
		country		--> $country
		email 	 	--> $email
		mobile	 	--> $mobile
		subject 	--> $topic
		::: message :::
		$message

		Mobile Make/Model 		--> $mobilemodel
		Mobile Network Provider	--> $mobile_provider
		";

		mail($cs_email, 'Email from Wapsite', $message,'From:' .$email. "\r\n" .'Reply-To: ' .$email);
		$success_message = 'Thank you for your message, we aim to reply to all messages within 48hrs.';
	}
}

// output HHTML
emitHeader();
emitTitleWithBody("Contact Us", "contactus");

if (isPagelet()) {
	if(!empty($success_message)){
		echo '<p><b>'.$success_message.'</b></p>';
	} else {
		echo '<p>Contact customer service by completing the form below:</p><br>';
	}

	// show any input errors
	echo wrapIfError($error_message);
	echo wrapIfError($mobile_error);
	echo wrapIfError($email_error);
	echo wrapIfError($mobilemodel_error);
	echo wrapIfError($topic_error);
	echo wrapIfError($message_error);

	?>
    <form name="contact" action="<?=getMCWapPath()?>contact.php" method="post" >
		<input type="hidden" name="pf" value="<?=$pf?>" />

		<label>mig33 Username (if applicable)</label><br>
		<input type="text" name="username" value="<?=$username?>" /><br>

		<label>Email Address*</label><br>
		<input type="text" name="email" value="<?=htmlspecialchars($email, ENT_QUOTES)?>" /><br>

		<label>Mobile Number*</label><br>
		<input type="text" name="mobile" value="<?=$mobile?>"/><br>

		<label>Phone Make / Model*</label><br>
		<input type="text" name="mobilemodel" value="<?=$mobilemodel?>" /><br>

		<label>Country</label><br>
		<input type="text" name="country" value="<?=$country?>" /><br>

		<label>Mobile Network</label><br>
		<input type="text" name="mobilenetwork" value="<?=$mobile_provider?>" /><br>

		<label>Subject*</label><br>
		<select name="topic">
			<option value="">- select subject -</option>
			<option <?php if ($topic === "Download/Connection Issue"){echo " selected=\"selected\"";} ?> value="Download/Connection Issue">Download/Connection Issue</option>
			<option <?php if ($topic === "Authentication/Username/Password Request"){echo " selected=\"selected\"";} ?> value="Authentication/Username/Password Request" >Authentication/Username/Password Request</option>
			<option <?php if ($topic === "SMS Issue"){echo " selected=\"selected\"";} ?> value="SMS Issue" >SMS Issue</option>
			<option <?php if ($topic === "Call Issue"){echo " selected=\"selected\"";} ?> value="Call Issue" >Call Issue</option>
			<option <?php if ($topic === "mig33 Feedback"){echo " selected=\"selected\"";} ?> value="mig33 feedback" >mig33 feedback</option>
			<option <?php if ($topic === "Merchant/Marketing/Corporate"){echo " selected=\"selected\"";} ?> value="Merchant/Marketing/Corporate" >Merchant/Marketing/Corporate</option>
			<option <?php if ($topic === "General/Other"){echo " selected=\"selected\"";} ?> value="General/Other" >General/Other</option>
		</select><br>

		<label>Your Message*</label><br>
		<textarea name="message" cols="10" rows="5"><?=$message?></textarea><br>
		<input type="submit" name="Submit" value="Send" class="btn"/>
    </form>
	<p>*Required Field</p>
    <br>
<?php
if($pf == 'MC') $backLink = getMCWapPath()."merchant_center.php";
if($pf == 'LOGIN') $backLink = getMCWapPath()."merchant.php";
if($pf == 'JOIN') $backLink = get_server_root().'/sites/index.php?c=registration&a=register&v=wap';
if($pf == 'HELP') $backLink = getWapPath()."/member2/help.php?pf=<?=$pf?>";
if($pf == 'DOWNLOAD') $backLink = getWapPath()."/download.php";
if($pf == 'JOIN_HELP') $backLink = getWapPath()."/member2/help.php?pf=JOIN";
if($pf == 'DOWNLOAD_HELP') $backLink = getWapPath()."/member2/help.php?pf=DOWNLOAD";
if($pf == 'MYACCOUNT_HELP') $backLink = getWapPath()."/member2/help.php?pf=MYACCOUNT";

} else {
// WAP output
?>
<div id="content">
<?php
	if(!empty($success_message)){
		echo '<p><strong>'.$success_message.'</strong></p>';
	} else {
		echo '<p>Contact customer service by completing the form below:</p>';
	}

	// show any input errors
	echo wrapIfError($error_message);
	echo wrapIfError($mobile_error);
	echo wrapIfError($email_error);
	echo wrapIfError($mobilemodel_error);
	echo wrapIfError($topic_error);
	echo wrapIfError($message_error);

	?>
    <form name="contact" action="contact.php" method="post" >
		<input type="hidden" name="pf" value="<?=$pf?>" />

		<label>mig33 Username <span class="sec">(if applicable)</span></label>
		<input type="text" name="username" value="<?=$username?>" />

		<label>Email Address*</label>
		<input type="text" name="email" value="<?=htmlspecialchars($email, ENT_QUOTES)?>" />

		<label>Mobile Number*</label>
		<input type="text" name="mobile" value="<?=$mobile?>"/>

		<label>Phone Make / Model*</label>
		<input type="text" name="mobilemodel" value="<?=$mobilemodel?>" />

		<label>Country</label>
		<input type="text" name="country" value="<?=$country?>" />

		<label>Mobile Network</label>
		<input type="text" name="mobilenetwork" value="<?=$mobile_provider?>" />

		<label>Subject*</label>
		<select name="topic">
			<option value="">- select subject -</option>
			<option <?php if ($topic === "Download/Connection Issue"){echo " selected=\"selected\"";} ?> value="Download/Connection Issue">Download/Connection Issue</option>
			<option <?php if ($topic === "Authentication/Username/Password Request"){echo " selected=\"selected\"";} ?> value="Authentication/Username/Password Request" >Authentication/Username/Password Request</option>
			<option <?php if ($topic === "SMS Issue"){echo " selected=\"selected\"";} ?> value="SMS Issue" >SMS Issue</option>
			<option <?php if ($topic === "Call Issue"){echo " selected=\"selected\"";} ?> value="Call Issue" >Call Issue</option>
			<option <?php if ($topic === "mig33 Feedback"){echo " selected=\"selected\"";} ?> value="mig33 feedback" >mig33 feedback</option>
			<option <?php if ($topic === "Merchant/Marketing/Corporate"){echo " selected=\"selected\"";} ?> value="Merchant/Marketing/Corporate" >Merchant/Marketing/Corporate</option>
			<option <?php if ($topic === "General/Other"){echo " selected=\"selected\"";} ?> value="General/Other" >General/Other</option>
		</select><br/>

		<label>Your Message*</label>
		<textarea name="message" cols="25" rows="5"><?=$message?></textarea><br/>
		<input type="submit" name="Submit" value="Send" class="btn"/>

    </form>
	<p class="sec">*Required Field</p>
</div>
<?php
if($pf == 'MC') $backLink = "merchant_center.php";
if($pf == 'LOGIN') $backLink = "merchant.php";
if($pf == 'JOIN') $backLink = get_server_root().'/sites/index.php?c=registration&a=register&v=wap';
if($pf == 'HELP') $backLink = "../member2/help.php?pf=<?=$pf?>";
if($pf == 'DOWNLOAD') $backLink = "../download.php";
if($pf == 'JOIN_HELP') $backLink = "../member2/help.php?pf=JOIN";
if($pf == 'DOWNLOAD_HELP') $backLink = "../member2/help.php?pf=DOWNLOAD";
if($pf == 'MYACCOUNT_HELP') $backLink = "../member2/help.php?pf=MYACCOUNT";
}

$helpLink = "";
emitFooter($backLink, $helpLink, "", "");
include_once("../gs_inc.php");
?>
</body>
</html>
<?php flushOutputBuffer(); ?>
