<?php
include_once("../common/common-inc.php");
include_once("member2/emit.php");
include_once("member2/check.php");
include_once("wap_includes/wap_functions.php");
putenv("pagelet=true");

session_start();


$pf = $_GET['pf'];
if(!empty($_POST['pf'])){
	$pf = $_POST['pf'];
}

//If from session set known variables
if($_SESSION['user']['username'])
{
	$username = $_SESSION['user']['username'];
}
if($_SESSION['user']['emailAddress'])
{
	$email = $_SESSION['user']['emailAddress'];
}
if($_SESSION['user']['mobilePhone'])
{
	$mobile = $_SESSION['user']['mobilePhone'];
}

if($_POST)
{
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

	if (empty($error))
	{
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

emitHeader('Welcome to mig33');

		if(!empty($success_message)){
?>
		<small><b><?=$success_message?></b></small><br/><br/>
<?php
		} else {
?>
		<small>Contact customer service by completing the form below:</small><br/><br/>
<?php
			if(!empty($error_message)){
?>
		<small><b><font style="color:red;">*<?=$error_message?></font></b></small><br/>
<?php
			}

			if(!empty($mobile_error)){
?>
		<small><b><font style="color:red;">*<?=$mobile_error?></font></b></small><br/>
<?php
			}

			if(!empty($email_error)){
?>
		<small><b><font style="color:red;">*<?=$email_error?></font></b></small><br/>
<?php
			}

			if(!empty($mobilemodel_error)){
?>
		<small><b><font style="color:red;">*<?=$mobilemodel_error?></font></b></small><br/>
<?php
			}

			if(!empty($topic_error)){
?>
		<small><b><font style="color:red;">*<?=$topic_error?></font></b></small><br/>
<?php
			}

			if(!empty($message_error)){
?>
		<small><b><font style="color:red;">*<?=$message_error?></font></b></small><br/>
<?php
			}

			if(!empty($error)){
?>
		<br/>
<?php
			}
?>

		<form name="contact" action="contact.php" method="post" >
		<input type="hidden" name="pf" value="<?=$pf?>" />

		<small><b>mig33 Username</b></small><br/>
		(if applicable)<br/>
		<input type="text" name="username" value="<?=$username?>" /><br/><br/>

		<small><b>Email Address</b>*</small><br/>
		<input type="text" name="email" value="<?=$email?>" /><br/><br/>

		<small><b>Mobile Number</b>*</small><br/>
		<input type="text" name="mobile" value="<?=$mobile?>"/><br/><br/>

		<small><b>Phone Make / Model</b>*</small><br/>
		<input type="text" name="mobilemodel" value="<?=$mobilemodel?>" /><br/><br/>

		<small><b>Country</b></small><br/>
		<input type="text" name="country" value="<?=$country?>" /><br/><br/>

		<small><b>Mobile Network</b></small><br/>
		<input type="text" name="mobilenetwork" value="<?=$mobile_provider?>" /><br/><br/>

		<small><b>Subject</b>*</small><br/>
		<select name="topic">
			<option value="">- select subject -</option>
			<option <?php if ($topic === "Download/Connection Issue"){echo " selected=\"selected\"";} ?> value="Download/Connection Issue">Download/Connection Issue</option>
			<option <?php if ($topic === "Authentication/Username/Password Request"){echo " selected=\"selected\"";} ?> value="Authentication/Username/Password Request" >Authentication/Username/Password Request</option>
			<option <?php if ($topic === "SMS Issue"){echo " selected=\"selected\"";} ?> value="SMS Issue" >SMS Issue</option>
			<option <?php if ($topic === "Call Issue"){echo " selected=\"selected\"";} ?> value="Call Issue" >Call Issue</option>
			<option <?php if ($topic === "mig33 Feedback"){echo " selected=\"selected\"";} ?> value="mig33 feedback" >mig33 feedback</option>
			<option <?php if ($topic === "Merchant/Marketing/Corporate"){echo " selected=\"selected\"";} ?> value="Merchant/Marketing/Corporate" >Merchant/Marketing/Corporate</option>
			<option <?php if ($topic === "General/Other"){echo " selected=\"selected\"";} ?> value="General/Other" >General/Other</option>
		</select><br/><br/>

		<small><b>Your Message</b>*</small><br/>
		<textarea name="message" cols="25" rows="5"><?=$message?></textarea><br/>
		<input type="submit" name="Submit"/><br/><br/>

		</form>
		<small>*Required Field</small><br/><br/>

<?php
		}

		if($pf == 'JOIN'){
?>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap">Back</a></small><br/>
<?php
		}else if($pf == 'HELP'){
?>
		<small><a href="member2/help.php?pf=<?=$pf?>">Back</a></small><br/>
<?php
		}else if($pf == 'DOWNLOAD'){
?>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Back</a></small><br/>
<?php
		}else if($pf == 'JOIN_HELP'){
?>
		<small><a href="member2/help.php?pf=JOIN">Back</a></small><br/>
<?php
		}else if($pf == 'DOWNLOAD_HELP'){
?>
		<small><a href="member2/help.php?pf=DOWNLOAD">Back</a></small><br/>
<?php
		}else if($pf == 'MYACCOUNT_HELP'){
?>
		<small><a href="member2/help.php?pf=MYACCOUNT">Back</a></small><br/>
<?php
		}
?>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=login&v=wap">Home</a></small><br/>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>

