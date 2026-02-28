<?php
include_once("../../common/common-inc.php");

//Check Session and Load UserData
if(!$userData){
	ice_check_session();
	$userData = ice_get_userdata();
}

if ($userData->mobileVerified == "0") {
?>
	<html>
		<head>
   			<title>Email</title>
  		</head>
  		<body bgcolor="white">
  			<p>&nbsp;</p>
			<p>A mobile number is required to use this feature, you can get full access to all mig33 features by adding your mobile number to your account via "<a href="mig33:newBrowser(<?=$server_root?>/sites/midlet/settings/account_contact)">Settings > Mobile Number</a>", and activate it with the authentication code sent to you via SMS.</p>
  			<br>
  			<p><a href="<?=$server_root?>/midlet/member/mail_help.php">Help &gt;&gt;</a></p>
  		</body>
  	</html>
<?php
	die;
}

//Fields for Prepopulation/Submission
if ($to == ""){
	$to = $_GET['to'];
	if($to == ""){
		$to = $_POST['to'];
	}
}
$to = strip_tags($to);

$cid = 0;
if( !empty($_GET['cid']) )
{
	$cid = $_GET['cid'];
}
else if (!empty($_POST['cid']) )
{
	$cid = $_POST['cid'];
}
settype($cid, "integer");

if ($subject == ""){
	$subject = $_GET['subject'];
	if($subject == ""){
		$subject = $_POST['subject'];
	}
}
$subject = strip_tags($subject);

if ($message == ""){
	$message = $_GET['message'];
	if($message == ""){
		$message = $_POST['message'];
	}
}
$message = strip_tags($message);

//If POST then send the email
if ($_POST)
{
	//Do some checking to see if the required fields are populated
	if(empty($to)){
		$error = 'Please specify the TO field.';
	}else if(empty($message)){
		$error = 'Please specify the MESSAGE field.';
	} else {
		//Attempt to send a mail (it would be a TRY and CATCH statement here soon) TODO
		try
		{
			soap_call_ejb('sendEmail', array($userData->username, $userData->password, trim($to), trim($subject), trim($message) ));
		}
		catch(Exception $e)
		{
			$error = $e->getMessage();
		}

		//If mail sent successfully
		if (!$error){
			//Display the inbox with a success message
			?>
				<html>
					<head>
						<title>Compose Mail</title>
					</head>
					<body bgcolor="white">
						<p style="color:green">Email has been successfully sent!</p>
						<br>
						<p><a href="<?=$server_root?>/midlet/member/mail.php">Inbox &gt;&gt;</a>&nbsp;</p>
						<p><a href="<?=$server_root?>/midlet/member/mail_compose.php">Create New &gt;&gt;</a>&nbsp;</p>
						<p><a href="<?=$server_root?>/midlet/member/mail.php?bid=2">Sent Items &gt;&gt;</a>&nbsp;</p>
						<p><a href="<?=$server_root?>/midlet/member/mail.php?bid=3">Deleted Items &gt;&gt;</a>&nbsp;</p>
						<p><a href="<?=$server_root?>/midlet/member/mail_settings.php">Settings &gt;&gt;</a>&nbsp;</p>
					</body>
				</html>

			<?php

			//don't show anymore pages below
			die();
		}
	}
}

?>
<html>
	<head>
    	<title>Compose Mail</title>
  	</head>
  	<body bgcolor="white">
  		<?php
  			if($error){
  				print '<p style="color:red">'.$error.'</p>';
  			}
  		?>

		<form method="POST" action="<?=$server_root?>/midlet/member/mail_compose.php">
		<input type="hidden" name="msg" value="<?=$_GET['msg']?>">
		<input type="hidden" name="bid" value="<?=$bid?>">
			<p><b>To</b></p>
			<p><input type="text" name="to" value="<?=$to?>" size="10" alt="To">
			<small><a href="<?=$server_root?>/midlet/member/mail_pickcontacts.php?to=<?=$to?>&subject=<?=urlencode($subject)?>&message=<?=urlencode($message)?>">Pick Contacts &gt;&gt;</a></small></p>
			<br>
			<p><b>Subject</b></p>
			<p><input type="text" name="subject" value="<?=$subject?>" size="10" alt="Subject"></p>
			<p><b>Message</b></p>
			<p>
				<textarea name="message" cols="10" rows="5" alt="Message"><?=$message?></textarea>
			</p>
			<p><input type="submit" value="Send Mail"></p>
		</form>
		<br>

		<?php
			if($cid!=0)
			{
				print '<p><a href="'.$server_root.'/sites/index.php?c=group&v=midlet&a=home&cid='.$cid.'">Return to group</a></p>';
			}
			else
			{
				if($bid == 2){
					print '<p><a href="'.$server_root.'/midlet/member/mail.php?bid=2">Return to Sent Items</a></p>';
				}else if($bid == 3){
					print '<p><a href="'.$server_root.'/midlet/member/mail.php?bid=3">Return to Deleted Items</a></p>';
				} else {
					print '<p><a href="'.$server_root.'/midlet/member/mail.php">Return to Inbox</a></p>';
				}
			}
		?>
	</body>
</html>
