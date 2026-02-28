<?php
include_once("../../common/common-inc.php");
include_once("../../common/costs.php");

//Check Session and Load UserData
ice_check_session();
$userData = ice_get_userdata();

//Check if user is an authenticated user
//Then check if has activated their email account,
//Check if user still has identical username/passworrd combo (not allowed in imap server)
//If yes, go to email home paglet
//If no, check if username is compatible
if($userData->mobileVerified == "0"){
	?>
		<html>
  			<head>
   				<title>Email</title>
  			</head>
  			<body bgcolor="white">
  				<p>&nbsp;</p>
  				<p>A mobile number is required to use this feature, you can get full access to all mig33 features by adding your mobile number to your account via "Settings > Mobile Number", and activate it with the authentication code sent to you via SMS.</p>
  				<br>
  				<p><a href="mail_help.php">Help &gt;&gt;</a></p>
  			</body>
  		</html>
	<?php
}else if(strtolower($userData->password) == strtolower($userData->username)){
	?>
		<html>
  			<head>
   				<title>Email</title>
  			</head>
  			<body bgcolor="white">
  				<p>&nbsp;</p>
  				<p>Your username and password is the same. Please change your password.</p>
  				<br>
  				<p><a href="mail_help.php">Help &gt;&gt;</a></p>
  			</body>
  		</html>
	<?php
}else if($userData->emailActivated == "1"){
	global $imap_server;
	global $imap_port;

	try{
		//Check and Create folders if doesnt exist
		$mailbox = @imap_open('{' . $imap_server . ':' . $imap_port . '/imap}INBOX', $userData->username, $userData->password, CL_EXPUNGE);
		if($mailbox){
			$list = imap_list($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}', "*");
			if (is_array($list)) {
				$gotInbox = false;
				$gotSentBox = false;
				$gotTrash = false;
				foreach ($list as $val) {
					$boxname = imap_utf7_decode($val);
					if(strpos($boxname,'INBOX')){
						$gotInbox = true;
					}else if(strpos($boxname, 'Trash')){
						$gotTrash = true;
					}else if(strpos($boxname, 'Sent Items')){
						$gotSentBox = true;
					}
				}

				if(!$gotInbox){
					try{
						@imap_createmailbox($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}INBOX');
					}catch(Exception $e1){
						$error = false;
					}
				}

				if(!$gotTrash){
					try{
						@imap_createmailbox($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}Trash');
					}catch(Exception $e2){
						$error = false;
					}
				}

				if(!$gotSentBox){
					try{
						@imap_createmailbox($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}Sent Items');
					}catch(Exception $e3){
						$error = false;
					}
				}
			}
		}

		//If user has selected to receive SMS Alerts then update flag in user table
		$notify = $_GET['notify'];
		if($notify == 'true')
		{
			//Set notify flag to true for user
			try{
				soap_call_ejb('setEmailAlert',array($userData->username, true));
			}catch(Exception $e)
			{
				$error = $e->getMessage();
			}
		}
		else
		{
			//If we are hitting this page normally (not during registration), clear the emailAlertSent flag so the user can recieve further SMS alerts
			try{
				soap_call_ejb('setEmailAlertSent',array($userData->username, false));
			}catch(Exception $e)
			{
				$error = $e->getMessage();
			}
		}
	}catch(Exception $ex2){
		$error = $ex2->getMessage();
	}

	if(empty($error)){
		try{
			//Sort the mailbox by date
			$headers = @imap_sort($mailbox, 1, 1);

			//Get the number of unread emails
			$mailboxinfo = @imap_mailboxmsginfo($mailbox);
			$numNewEmails = $mailboxinfo->Unread;

			//Show Email main paglet
			?>
				<html>
					<head>
						<title>My Email Home</title>
					</head>
					<body bgcolor="white">
						<p>&nbsp;</p>
						<p><a href="mail.php">Inbox
						<?php
							//show number of unread messages if there are
							if($numNewEmails > 0){
								print '<b>('.$numNewEmails.' unread)</b>';
							}
						?>
						</a></p>
						<p><a href="mail_compose.php">Create New</a></p>
						<p><a href="mail.php?bid=3">Deleted Items</a></p>
						<p><a href="mail.php?bid=2">Sent Items</a></p>
						<p><a href="mail_settings.php">Settings</a></p>
						<p><a href="mail_help.php">Help</a></p>
					</body>
				</html>
			<?php

			//We know imap connection is connected or else it would have thrown the exception
			@imap_close($mailbox);

			//Stop process
			die();
		}catch(Exception $e){
			$error = $e->getMessage();
		}
	}

	//Show the error page if it ever gets down here
	?>
		<html>
			<head>
				<title>mig33 Email</title>
			</head>
			<body bgcolor="white">
				<p>There appears to be a problem setting up your mig33 email. Error: <?=$error?></p>
				<p><a href="mail_main.php?notify=<?=$notify?>">Try again</a></p>
			</body>
		</html>
	<?php

	//Clean up imap connection if needed (likely for cases where theres an error but not due to imap_open)
	if($mailbox){
		try{
			@imap_close($mailbox);
		}catch(Exception $eh){}
	}
} else {
	if(checkEmailCompatibleUsername($userData->username) && checkEmailReservedAlias($userData->username)){
		//Username is compatible, Activate user's email
		try{
			@mail_create_account($userData->username, $userData->password);
		}catch(Exception $e){
			$error = $e->getMessage();
		}

		if(empty($error)){
			//show welcome screen and ask notify question
			?>
				<html>
					<head>
						<title>mig33 Email</title>
					</head>
					<body bgcolor="white">
						<p>Welcome to mig33 email. Your mig33 mail is <b><?=$userData['username']?>@mig33.com</b>.</p>
						<?php
							$cost = getEmailAlertCost($userData->username);
							printf("<p>If you would like us to notify you via SMS when someone has sent you an email, please sign up below. Each notification SMS is %0.02f %s each.</p>", $cost['price'], $cost['currency']);
						?>
						<p><center><a href="mail_main.php?notify=true">Yes, Sign Up</a></center></p>
						<p><center><a href="mail_main.php?notify=false">No, but thanks</a></center></p>
					</body>
				</html>

			<?php
		} else {
			?>
				<html>
					<head>
						<title>mig33 Email</title>
					</head>
					<body bgcolor="white">
						<p>There appears to be a problem while activating your email. Error: <?=$error?></p>
						<p><center><a href="mail_main.php">Try again</a></center></p>
					</body>
				</html>

			<?php
		}
	} else {
		//Username is incompatible, show a message to contact customer support
		?>
			<html>
  				<head>
   					<title>mig33 Email</title>
  				</head>
  				<body bgcolor="white">
  					<p>Your username is email incompatible. We hope to bring you this feature soon.</p>
  					<p><a href="community_main.php">mig33 Community &gt;&gt;</a></p>
  				</body>
  			</html>
		<?php
	}
}
?>

