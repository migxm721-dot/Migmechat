<?php
include_once("../../common/common-inc.php");

ice_check_session();
$sessUser = ice_get_username();

//Get the page to serve up on
$page = $_GET['page'];
if(empty($page)){
	$page = $_POST['page'];
}

//Get the username getting reported on
$username = $_GET['username'];
if(empty($username)){
	$username = $_POST['username'];
}


if ($_POST)
{
	//**If 'Where it happened is' 'chat room' or 'other' display the 'what happened' text box
	//If 'Where it happened' is 'Profile Image' or 'Profile Content' then submit the page
	if ( ($_POST['where'] == 'Profile Image') || ($_POST['where'] == 'Profile Content') )
	{
		//Check for blanks
	 	if ((strlen($_POST['offender']) < 1) || (strlen($_POST['where']) < 1))
	 	{
	 		$error = 'Please provide the offending username and where it happened!';
	 	}

	 	//If email was entered, validate it
	 	if (strlen($_POST['email']) > 0)
	 	{
	 		if (!checkEmail($_POST['email']))
	 			$error = 'Please enter a valid email address!';
	 	}

	 	//Gather information on user
	 	$offender = $_POST['offender'];
	 	$email = $_POST['email'];
	 	$where = $_POST['where'];

		if(empty($error))
		{
			//If here then form is validated. Send off the email.
			$message = "
			mig33 Report from midlet:
			username			--> $sessUser
			email 	 			--> $email
			offender username	--> $offender
			where				--> $where
			::: message :::
			none
			";

			try{
				mail($cs_email, 'mig33 Report from midlet', $message,'From:' .$email. "\r\n" .'Reply-To: ' .$email);

				//Mark for final page
				$final = true;
			}catch(Exception $e){
				$error = $e->getMessage();
			}
		}
	}
	else if ( ($_POST['where'] == 'Chat Room') || ($_POST['where'] == 'Other') )
	{
		//Check for blanks
	 	if ((strlen($_POST['offender']) < 1) || (strlen($_POST['where']) < 1))
	 	{
	 		$error = 'Please provide the offending username and where it happened!';
	 	}

		//If email was entered, validate it
	 	if (strlen($_POST['email']) > 0)
	 	{
	 		if (!checkEmail($_POST['email']))
	 			$error = 'Please enter a valid email address!';
	 	}

	 	if($page == 'step2'){
	 		if (strlen($_POST['message']) < 1){
		 		$error = 'Please enter a message';
		 	}
	 	}

		//If theres errors, show it
		if(empty($error)){
			if($page == 'step1'){
				$page = 'step2';
			} else {
				//Gather information on user
				$offender = $_POST['offender'];
				$msg = $_POST['message'];
				$email = $_POST['email'];
				$where = $_POST['where'];

				$message = "
				mig33 Report from midlet:
				username			--> $sessUser
				email 	 			--> $email
				offender username	--> $offender
				where				--> $where
				::: message :::
				$msg
				";

				try{
					mail($cs_email, 'mig33 Report from midlet', $message,'From:' .$email. "\r\n" .'Reply-To: ' .$email);

					//Mark for final page
					$final = true;
				}catch(Exception $e){
					$error = $e->getMessage();
				}
			}
		}
	}
}

if ($final){
?>
	<html>
		<head>
			<title>Report User</title>
	  	</head>
	  	<body bgcolor="white">
			<p>Thank you for reporting this user. We will take this information into consideration.</p>
			<p><a href="<?=$server_root?>/midlet/member/community_main.php">Back to Community</a></p>
	  	</body>
	</html>
<?php } else { ?>
	<html>
	<?php if ($page == 'step1') { ?>
 		<head>
			<title>Report User</title>
  		</head>
  		<body bgcolor="white">
  			<form action="<?=$server_root?>/midlet/member/report_user.php" method="POST">
  			<input type="hidden" name="page" value="step1">
			<?php
			if($error){
				print '<p style="color:red">'.$error.'</p><br>';
			}
			?>
				<p><b>*Your Email:</b></p>
				<p><input type="text" name="email" size="7" alt="Your Email" value="<?=$_POST['email']?>"></p>
				<p>*Note: Please enter your email correctly so that we can reply to you in case we need more information. Your details are kept private.</p>
				<p><b>Offender's Username:</b></p>
	 			<p><input type="text" name="offender" size="5" alt="Offender's Username" value="<?php if ($username) { print $username; } else {print $_POST['offender'];} ?>"></p>
				<p><b>Where it happened:</b></p>
				<select name="where" size="7">
					<option value="Chat Room" <?php if ($_POST['where'] == 'Chat Room') {print 'selected';} ?>>Chat Room</option>
					<option value="Profile Image" <?php if ($_POST['where'] == 'Profile Image') {print 'selected';} ?>>Profile Image</option>
					<option value="Profile Content" <?php if ($_POST['where'] == 'Profile Content') {print 'selected';} ?>>Profile Content</option>
					<option value="Other" <?php if ($_POST['where'] == 'Other') {print 'selected';} ?>>Other</option>
				</select>
				<br>
				<p><input type="submit" name="submit" id="submit" value="Submit"></p>
			</form>
  		</body>
  <?php }else if($page == 'step2'){ ?>
  		<head>
			<title>Report User</title>
  		</head>
  		<body bgcolor="white">
  			<form action="report_user.php" method="POST">
  				<input type="hidden" name="page" value="step2">
  				<input type="hidden" name="email" value="<?=$_POST['email']?>">
  				<input type="hidden" name="offender" value="<?=$_POST['offender']?>">
  				<input type="hidden" name="where" value="<?=$_POST['where']?>">
  				<p><b>Tell us what happened:</b></p>
				<p><input type="hidden" name="page" value="2"></p>
				<textarea name="message" cols="10" rows="5" alt=""><?=$_POST['message']?></textarea>
	  			<p><input type="submit" name="submit" id="submit" value="Submit"></p>
			</form>
  		</body>
  <?php } else { ?>
		<head>
    		<title>Report User</title>
  		</head>
  		<body bgcolor="white">
			<p>You have selected this link as a user has abused mig33 services, used offensive content or other offenses.</p>
			<p>We thank you for taking the time to report this. All reports will be kept anonymous.</p>
			<p>Please select 'continue' to proceed with the report.</p>
			<p><a href="<?=$server_root?>/midlet/member/report_user.php?page=step1&username=<?=$_GET['username']?>">Continue</a></p>
  		</body>
  <?php } ?>
	</html>
<?php } ?>