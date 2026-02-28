<?php
include_once("common-inc-kk.php");
include_once("./emit.php");
include_once("./check.php");
include_once("../../common/costs.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(getProgFile($prog));

//Check async messages
checkServerSessionStatus();

//Check Session and Load UserData
ice_check_session();
$userData = ice_get_userdata();

//Check if user is an authenticated user
//Then check if has activated their email account,
//Check if user still has identical username/passworrd combo (not allowed in imap server)
//If yes, go to email home paglet
//If no, check if username is compatible
emitHeader();

if($userData->mobileVerified == "0"){
	emitTitle("Email");
	print checkForMsgs('','');
	?>
			<small>A mobile number is required to use this feature, you can get full access to all mig33 features by adding your mobile number to your account via "Settings > Mobile Number", and activate it with the authentication code sent to you via SMS.</small><br/><br/>
			<small><a href="mail_help_wap.php">Help</a></small><br/>
		</body>
	</html>
	<?php
}else if(strtolower($userData->password) == strtolower($userData->username)){
	print checkForMsgs('','');
	?>
			<small>Your username and password is the same. Please change your password.</small><br/><br/>
			<small><a href="mail_help_wap.php">Help</a></small><br/>
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
			emitTitle("My Email Home");
			print checkForMsgs('','');
			?>
					<small><a href="mail_wap.php">Inbox
					<?php
						//show number of unread messages if there are
						if($numNewEmails > 0){
							print '<b>('.$numNewEmails.' unread)</b>';
						}
					?>
					</a></small><br/>
					<small><a href="mail_compose_wap.php">Create New</a></small><br/>
					<small><a href="mail_wap.php?bid=3">Deleted Items</a></small><br/>
					<small><a href="mail_wap.php?bid=2">Sent Items</a></small><br/>
					<small><a href="mail_settings_wap.php">Settings</a></small><br/>
					<small><a href="mail_help_wap.php">Help</a></small><br/>
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
	emitTitle("mig33 Email");
	print checkForMsgs('','');
	?>
			<small>There appears to be a problem setting up your mig33 email. Error: <?=$error?></small><br/><br/>
			<small><a href="mail_main_wap.php?notify=<?=$notify?>">Try again</a></small><br/>
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
			emitTitle("mig33 Email");
			print checkForMsgs('','');
			?>
					<small>Welcome to mig33 email. Your mig33 mail is <b><?=$userData['username']?>@mig33.com</b>.</small><br/><br/>
					<?php
							$cost = getEmailAlertCost($userData->username);
							printf("<small>If you would like us to notify you via SMS when someone has sent you an email, please sign up below. Each notification SMS is %0.02f %s each.</small>", $cost['price'], $cost['currency']);
					?>
					<br/><br/>
					<small><a href="mail_main_wap.php?notify=true">Yes, Sign Up</a></small><br/><br/>
					<small><a href="mail_main_wap.php?notify=false">No, but thanks</a></small><br/>
				</body>
			</html>
			<?php
		} else {
			emitTitle("mig33 Email");
			print checkForMsgs('','');
			?>
					<small>There appears to be a problem while activating your email. Error: <?=$error?></small><br/><br/>
					<small><a href="mail_main_wap.php">Try again</a></small><br/>
				</body>
			</html>
			<?php
		}
	} else {
		//Username is incompatible, show a message to contact customer support
		emitTitle("mig33 Email");
		print checkForMsgs('','');
		?>
				<small>Your username is email incompatible. We hope to bring you this feature soon.</small><br/><br/>
			</body>
		</html>
		<?php
	}
}
?>
