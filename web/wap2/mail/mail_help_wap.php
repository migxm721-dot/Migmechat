<?php
include_once("../../common/common-inc.php");
$bid = $_GET['bid'];
$pageNum = $_GET['pagenum'];

?>

<html>
	<head>
		<title>Mail Help</title>
	</head>
	<body bgcolor="white">
		<p>You can now receive mail on mig33. Setup your mail account with your username: username@mig33.com and you can start sending and receiving mail from your mig33 buddies.  You can also choose to receive SMS notification* when you receive a mail.</p><br>
		<p><u>Inbox</u></p>
		<p>You can view a list of all the mail that you received.  Subject and Sender is shown here.</p>
		<br>
		<p><u>Sent Item</u></p>
		<p>View a history of all the mail that you sent.</p>
		<br>
		<p><u>Deleted Item</u></p>
		<p>View a history of all the mail that you deleted.</p>
		<br>
		<p>* SMS notification costs AUD$0.10 per SMS.</p>
		<br>

		<?php
			if(!$bid){
				print '<p><a href="mail_main.php">Back to Mail Main</a></p>';
			}else if($bid == 2){
				print '<p><a href="mail.php?bid=2&pagenum='.$pageNum.'">Back to Sent Items</a></p>';
			}else if($bid == 3){
				print '<p><a href="mail.php?bid=3&pagenum='.$pageNum.'">Back to Deleted Items</a></p>';
			} else {
				print '<p><a href="mail.php?bid=1&pagenum='.$pageNum.'">Back to Inbox</a></p>';
			}
		?>
	</body>
</html>
