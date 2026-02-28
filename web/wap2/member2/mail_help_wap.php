<?php
include_once("common-inc-kk.php");
include_once("./emit.php");
include_once("./check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(getProgFile($prog));

//Check async messages
checkServerSessionStatus();

$bid = $_GET['bid'];
$pageNum = $_GET['pagenum'];

emitHeader();
emitTitle("Mail Help");
print checkForMsgs('','');
?>
		<small>You can now receive mail on mig33. Setup your mail account with your username: username@mig33.com and you can start sending and receiving mail from your mig33 buddies.  You can also choose to receive SMS notification* when you receive a mail.</small><br/><br/>
		<small><b>Inbox</b></small><br/>
		<small>You can view a list of all the mail that you received. Subject and Sender is shown here.</small><br/><br/>
		<small><b>Sent Item</b></small><br/>
		<small>View a history of all the mail that you sent.</small><br/><br/>
		<small><b>Deleted Item</b></small><br/>
		<small>View a history of all the mail that you deleted.</small><br/><br/>
		<small>* SMS notification costs AUD$0.10 per SMS.</small><br/><br/>
	<?php
		if(!$bid){
			print '<small><a href="mail_main_wap.php">Back to Mail Main</a></small><br/>';
		}else if($bid == 2){
			print '<small><a href="mail_wap.php?bid=2&amp;pagenum='.$pageNum.'">Back to Sent Items</a></small><br/>';
		}else if($bid == 3){
			print '<small><a href="mail_wap.php?bid=3&amp;pagenum='.$pageNum.'">Back to Deleted Items</a></small><br/>';
		} else {
			print '<small><a href="mail_wap.php?bid=1&amp;pagenum='.$pageNum.'">Back to Inbox</a></small><br/>';
		}
	?>
	</body>
</html>
