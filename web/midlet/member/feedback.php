<?php
include_once("../../common/common-inc.php");

ice_check_session();
$username = ice_get_username();

$success = false;
#get all posted values
$subject = ($_POST['subject']);
$mail = ($_POST['mail']);
$message = ($_POST['message']);

//Check if theres a POST, if so, go through the process of verifying all the input items and send the feedback back to contact@mig33.com
if($_POST){
	if(!empty($mail)){
		if(!checkEmail($mail)){
			$error = 'Please enter a valid email address.';
		}
	}

	if(empty($message)){
		$error = 'Please enter your message.';
	}

	if(empty($error)){
		if($subject == '1'){
			$subjectText = 'General/Other';
		}else if($subject == '2'){
			$subjectText = 'Download/Connection Issue';
		}else if($subject == '3'){
			$subjectText = 'Activation/Username/Password Request';
		}else if($subject == '4'){
			$subjectText = 'SMS Issue';
		}else if($subject == '5'){
			$subjectText = 'Call Issue';
		}else if($subject == '6'){
			$subjectText = 'mig33 beta Feedback';
		}else if($subject == '7'){
			$subjectText = 'Affiliate/Marketing/Corporate';
		}

		$message = "
		mig33 feedback from midlet:
		username			--> $username
		email 	 			--> $email
		subject				--> $subjectText
		::: message :::
		$message
		";

		mail($cs_email, 'mig33 Feedback from midlet', $message,'From:' .$email. "\r\n" .'Reply-To: ' .$email);

		$success = true;
	}
}
else
{
	$mail = $username . "@mig33.com";
}

?>
<html>
  <head>
    <title>Feedback / Help</title>
  </head>
  <body bgcolor="white">
  	<?php
		if($error)
			print '<p style="color:red">'.$error.'</p>';
  	?>
	<p>
		<?php if(!$success){ ?>
 		<form action="<?=$server_root?>/midlet/member/feedback.php" method="POST">
        	<br>
			<b>Username: </b><?=$username?>
			<br>
			<b>Email Address:</b><br>
			<input type="text" name="mail" value="<?=$mail?>" size="10" alt="Email Address">
			<br>
         	<b>Subject:</b><br>
			<select name="type" size="10">
				<option value="1" <?php if ($subject == '1' || $subject == '') { echo "Selected";} ?>>General/Other</option>
				<option value="2" <?php if ($subject == '2') { echo "Selected";} ?>>Download/Connection Issue</option>
				<option value="3" <?php if ($subject == '3') { echo "Selected";} ?>>Activation/User/Pass Request</option>
				<option value="4" <?php if ($subject == '4') { echo "Selected";} ?>>SMS Issue</option>
				<option value="5" <?php if ($subject == '5') { echo "Selected";} ?>>Call Issue</option>
				<option value="6" <?php if ($subject == '6') { echo "Selected";} ?>>mig33 Beta</option>
				<option value="7" <?php if ($subject == '7') { echo "Selected";} ?>>Affiliate/Marketing/Corporate</option>
			</select>
			<br>
         	<b>Message:</b><br>
			<textarea name="message" cols="10" rows="5" alt="Message">
			<?=$message?>
			</textarea>
			<br>
 			<input type="submit" name="submit" id="submit" value="Submit">
 		</form>
 		<?php
 			} else {
 				echo 'Thank you for providing your feedback to mig33. We appreciate your support!';
 			}
 		?>
 	</p>
	<p><a href="<?=$server_root?>/midlet/member/community_main.php">Back to Community</a>&gt;&gt;</p>
  </body>
</html>

