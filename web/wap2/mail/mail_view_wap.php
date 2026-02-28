<?php
include_once("../../common/common-inc.php");

//Check Session and Load UserData
ice_check_session();
$userData = ice_get_userdata();

//Decide whether to open Inbox, sent box or deleted items box. Default to Inbox
//box id (bid): 1=Inbox, 2=Sent box, 3=Deleted box
$bid = 1;
if($_GET['bid']){
	$bid = $_GET['bid'];
}else if($_POST && $_POST['bid']){
	$bid = $_POST['bid'];
}

$pageNum = 1; 	//Default to page 1
if(!empty($_GET['pagenum'])){
	//Get the pagenum, make sure it is within the boundary
	$pageNum = $_GET['pagenum'];
}

//Global values for the imap server
global $imap_server;
global $imap_port;

if($_POST){
	//msg id from POST
	$msg = $_POST['msg'];

	try{
		//Deal with actions specified
		if($_POST['action'] == 'reply'){
			//Login to imap server
			if($bid == 2){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
			}else if($bid == 3){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
			} else {
				$bid = 1;
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
			}

			//Pre-set the TO field and Subject field
			$header_info = @imap_headerinfo($mailbox, $msg);
			$to = mail_return_address($header_info);
			$subject = $header_info->subject;

			//Check if 'RE:' is already there
			if(strpos($subject, 'RE:') === false){
				$subject = 'RE:'.$subject;
			}

			//Discard POST data so it doesn't mess around the included page's data
			$_POST = null;

			include('mail_compose.php');
		}else if($_POST['action'] == 'forward'){
			//Login to imap server
			if($bid == 2){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
			}else if($bid == 3){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
			} else {
				$bid = 1;
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
			}

			//Pre-set the TO field and Subject field
			$header_info = @imap_headerinfo($mailbox, $msg);
			$subject = $header_info->subject;

			//Check if 'FW:' is already there
			if(strpos($subject, 'FW:') === false){
				$subject = 'FW:'.$subject;
			}

			//Get the message structure
			$struct = @imap_fetchstructure($mailbox, $msg);
			$parts = $struct->parts;
			$part_number = '1';
			$i = 1;

			//If the message is multipart then loop through the parts. We want to use the HTML part preferably. Otherwise, PLAIN.
			//If neither can be found, we assume the content is part '1'.
			if ($parts){
				foreach ($parts as $part){
					if ($part-subtype == 'HTML'){
						$msg_type = 'HTML';
						$part_number = $i;
					}else if ($part-subtype == 'PLAIN'){
						$msg_type = 'PLAIN';
						$part_number = $i;
					}
					$i++;
				}
			}
			settype($part_number, 'String');

			//Fetch message body
			$message = @imap_fetchbody($mailbox, $msg, $part_number);

			//Discard POST data so it doesn't mess around the included page's data
			$_POST = null;

			include('mail_compose.php');
		}else if($_POST['action'] == 'delete'){
			//Login to imap server
			if($bid == 2){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
			}else if($bid == 3){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
			} else {
				$bid = 1;
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
			}

			//Move to Deleted item box
			@imap_mail_move($mailbox, $msg, 'Trash');

			//Mark the specified email for deletion
			@imap_delete($mailbox, $msg);

			//Clear that mail in current mailbox
			@imap_expunge($mailbox);

			?>
				<html>
					<head>
						<title>Mail Deleted</title>
					</head>
					<body bgcolor="white">
						<p>Your mail has been moved to the Deleted Items folder successfully!. </p>
						<br>
						<?php
							if($bid == 2){
								print '<p><a href="mail.php?bid=2&pagenum='.$pageNum.'">Back to Sent Items</a></p>';
							}else if($bid == 3){
								print '<p><a href="mail.php?bid=3&pagenum='.$pageNum.'">Back to Deleted Items</a></p>';
							} else {
								print '<p><a href="mail.php?bid=1&pagenum='.$pageNum.'">Back to Inbox</a></p>';
							}
						?>
					</body>
				</html>
			<?php
		}else if($_POST['action'] == 'block'){
			//Block User
			$blockUsername = username_address($_POST['from']);

			soap_call_ejb('blockContact', array($userData->userID, $userData->username, $blockUsername));

			//Login to imap server
			if($bid == 2){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
			}else if($bid == 3){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
			} else {
				$bid = 1;
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
			}

			//Move to Deleted item box
			@imap_mail_move($mailbox, $msg, 'Trash');

			//Mark the specified email for deletion
			@imap_delete($mailbox, $msg);

			//Clear that mail in current mailbox
			@imap_expunge($mailbox);

			//Set success message
			$success = "User '".$blockUsername."' has been blocked. You will not receive messages from this user";

			//Discard POST data so it doesn't mess around the included page's data
			$_POST = null;
			$mailbox = null;

			include('mail.php');
		}else if($_POST['action'] == 'details'){
			//Discard POST data so it doesn't mess around the included page's data
			$_POST = null;

			include('mail_msgdetails.php');
		}else if($_POST['action'] == 'undelete'){
			//Login to imap server
			if($bid == 2){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
			}else if($bid == 3){
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
			} else {
				$bid = 1;
				$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
			}

			//Decide which folder it would go back to with the undelete
			$header_info = @imap_headerinfo($mailbox, $msg);
			$from = $header_info->fromaddress;
			if(username_address($from) == ($userData->username)){
				//This was a sent item
				@imap_mail_move($mailbox, $msg, 'Sent Items');
				$moveTo	= 'Sent Items';
			} else {
				@imap_mail_move($mailbox, $msg, 'Inbox');
				$moveTo = 'Inbox';
			}

			//Mark the specified email for deletion
			@imap_delete($mailbox, $msg);

			//Clear that mail in current mailbox
			@imap_expunge($mailbox);

			?>
				<html>
					<head>
						<title>Mail Undeleted</title>
					</head>
					<body bgcolor="white">
						<?php
							if($moveTo == 'Inbox'){
								print '<p>Mail has been undeleted and moved back into the Inbox.</p><br>';
								print '<p><a href="mail.php?bid=1&pagenum='.$pageNum.'">Back to Inbox</a></p>';
							} else {
								print '<p>Mail has been undeleted and moved back into Sent Items.</p><br>';
								print '<p><a href="mail.php?bid=2&pagenum='.$pageNum.'">Back to Sent Items</a></p>';
							}
						?>
					</body>
				</html>
			<?php

		}
	}catch(Exception $e){
		$error = 'There has been an error while performing that action. Error:'.$e->getMessage();

		?>
			<html>
				<head>
					<?php
						if($bid == 2){
							print '<title>Sent Items - Read</title>';
						}else if($bid == 3){
							print '<title>Deleted Items - Read</title>';
						} else {
							print '<title>Inbox - Read</title>';
						}
					?>
				</head>
				<body bgcolor="white">
					<?php
						print '<p style="color:red">'.$error.'</p>';

						if($bid == 2){
							print '<p><a href="mail.php?bid=2&pagenum='.$pageNum.'">Back to Sent Items</a></p>';
						}else if($bid == 3){
							print '<p><a href="mail.php?bid=3&pagenum='.$pageNum.'">Back to Deleted Items</a></p>';
						} else {
							print '<p><a href="mail.php?bid=1&pagenum='.$pageNum.'">Back to Inbox</a></p>';
						}
					?>
				</body>
			</html>
		<?php
	}

	//Close imap connection
	if($mailbox){
		try{
			@imap_close($mailbox);
		}catch(Exception $ew){}
	}

	//Stop display
	die();
}


try{
	//Login to imap server
	if($bid == 2){
		$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
	}else if($bid == 3){
		$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
	} else {
		$bid = 1;
		$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
	}

	//Get the structure
	$struct = @imap_fetchstructure($mailbox, $_GET['msg']);
	$parts = $struct->parts;
	$part_number = '1';
	$i = 1;

	//If the message is multipart then loop through the parts. We want to use the HTML part preferably. Otherwise, PLAIN.
	//If neither can be found, we assume the content is part '1'.
	if ($parts){
		foreach ($parts as $part) {
			if ($part-subtype == 'HTML'){
				$msg_type = 'HTML';
				$part_number = $i;
			}else if ($part-subtype == 'PLAIN'){
				$msg_type = 'PLAIN';
				$part_number = $i;
			}
			$i++;
		}
	}
	settype($part_number, 'String');

	//Prepare TO and SUBJECT values for reply link
	$header_info = @imap_headerinfo($mailbox, $_GET['msg']);
	$to = truncateString(mail_return_address($header_info),12);
	$subject = truncateString($header_info->subject, 15);

	//If Unread then update midlet with number of unread
	if ( ($header_info->Unseen == 'U') || ($header_info->Recent == 'N') ){
		//Get the number of unread emails
		$mailboxinfo = @imap_mailboxmsginfo($mailbox);
		$numNewEmails = $mailboxinfo->Unread;
		//Minus 1 as we have not marked as read	ONLY if its an Inbox item
		if($bid == 1){
			try {
				ice_email_notification($numNewEmails - 1);
			}catch (Exception $ex){}
		}
	}

	//Fetch message body
	$message_body = @imap_fetchbody($mailbox, $_GET['msg'], $part_number);

	if ($msg_type == 'HTML'){
		$message_body = strip_tags($message_body);
	} else {
		$message_body = nl2br2($message_body);
	}

	?>
	<html>
		<head>
			<?php
				if($bid == 2){
					print '<title>Sent Items - Read</title>';
				}else if($bid == 3){
					print '<title>Deleted Items - Read</title>';
				} else {
					print '<title>Inbox - Read</title>';
				}
			?>
		</head>
		<body bgcolor="white">
			<?php
				//Show Error if there is one
				if($error){
					print '<p style="color:red">'.$error.'</p>';
				}
			?>
			<p><b>From: </b><?= $to?></p>
			<p><b>Subj: </b><?= $subject?></p>
			<p>
				<form action="mail_view.php" method="POST">
					<input type="hidden" name="msg" value="<?=$_GET['msg']?>">
					<input type="hidden" name="bid" value="<?=$bid?>">
					<input type="hidden" name="from" value="<?=mail_return_address($header_info)?>">
					<select name="action" size="10">
						<option value="reply" <?php if($bid==1){ print 'selected'; } ?>>Reply</option>
						<option value="forward" <?php if($bid==2){ print 'selected'; } ?>>Forward</option>
						<?php
							if($bid != 3){
								print '<option value="delete">Delete Mail</option>';
							}

							if($bid == 1){
								print '<option value="block">Block User</option>';
							}
						?>
						<option value="details">Mail Details</option>
						<?php
							if($bid == 3){
								print '<option value="undelete" selected>Undelete Mail</option>';
							}
						?>
					</select>
					<input type="submit" name="submit" id="submit" value="Submit">
				</form>
			</p>
			<p><b>Message:</b></p>
			<p><small><?= $message_body?></small></p>
			<?php
				if($bid == 2){
					print '<p><a href="mail.php?bid=2&pagenum='.$pageNum.'">Back to Sent Items</a></p>';
				}else if($bid == 3){
					print '<p><a href="mail.php?bid=3&pagenum='.$pageNum.'">Back to Deleted Items</a></p>';
				} else {
					print '<p><a href="mail.php?bid=1&pagenum='.$pageNum.'">Back to Inbox</a></p>';
				}
			?>
		</body>
	</html>
<?php
}catch(Exception $e){
	$error = 'There has been an error while performing that action. Error:'.$e->getMessage();

	?>
	<html>
		<head>
			<?php
				if($bid == 2){
					print '<title>Sent Items - Read</title>';
				}else if($bid == 3){
					print '<title>Deleted Items - Read</title>';
				} else {
					print '<title>Inbox - Read</title>';
				}
			?>
		</head>
		<body bgcolor="white">
			<?php
				print '<p style="color:red">'.$error.'</p>';

				if($bid == 2){
					print '<p><a href="mail.php?bid=2&pagenum='.$pageNum.'">Back to Sent Items</a></p>';
				}else if($bid == 3){
					print '<p><a href="mail.php?bid=3&pagenum='.$pageNum.'">Back to Deleted Items</a></p>';
				} else {
					print '<p><a href="mail.php?bid=1&pagenum='.$pageNum.'">Back to Inbox</a></p>';
				}
			?>
		</body>
	</html>
	<?php
}

//Close imap connection
if($mailbox){
	try{
		@imap_close($mailbox);
	}catch(Exception $ew){}
}

?>