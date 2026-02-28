<?php
include_once("common-inc-kk.php");
include_once("./emit.php");
include_once("./check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once("t.php");

//Check async messages
checkServerSessionStatus();

session_start();
global  $cid, $prog;

$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];

function backUrl() {

        global $cid, $prog;
        return("$prog?cmd=home");
}


//Check Session and Load UserData
if(!$userData){
	ice_check_session();
	$userData = ice_get_userdata();
}


//Decide whether to open Inbox, sent box or deleted items box. Default to Inbox
//box id (bid): 1=Inbox, 2=Sent box, 3=Deleted box
$bid = 1;
if($_GET['bid']){
	$bid = $_GET['bid'];
}else if($_POST && $_POST['bid']){
	$bid = $_POST['bid'];
}

//Global values for the imap server
global $imap_server;
global $imap_port;

try{

	if(!$mailbox){
		//Login to imap server
		if($bid == 2){
			$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $userData->username, $userData->password, CL_EXPUNGE);
		}else if($bid == 3){
			$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $userData->username, $userData->password, CL_EXPUNGE);
		} else {
			$bid = 1;
			$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $userData->username, $userData->password, CL_EXPUNGE);
		}
	}

	//Sort the mailbox by date
	$headers = @imap_sort($mailbox, 1, 1);

	emitHeader();

	if($bid == 2){
		emitTitle("Sent");
	}else if($bid == 3){
		emitTitle("Deleted");
	} else {
		emitTitle("My Inbox");
	}
	print checkForMsgs('','');
	?>
	<small>Inbox</small><br/>
	<small><a href="mail_wap.php?bid=1">Check Mail</a></small><br/>
	<hr/>
	<?php
	//If theres a success message (mainly from composing an email), show it
	if($success){
		print '<small><font style="color:green;">'.$success.'</font></small><br/><br/>';
	}

	if ($headers){
		//Paging area. Get the main numbers needed
		$totalEntries = count($headers);
		$entriesPerPage = 10;
		$totalPages = ceil($totalEntries / $entriesPerPage);
		$pageNum = 1; 	//Default to page 1
		if(!empty($_GET['pagenum'])){
			//Get the pagenum, make sure it is within the boundary
			$pageNum = $_GET['pagenum'];
			if($pageNum > $totalPages){
				$pageNum = $totalPages;
			}else if($pageNum < 1){
				$pageNum = 1;
			}
		}

		//Get the start index of the array to show
		$startIndex = ($pageNum-1) * $entriesPerPage;

		//Get the stop index of the array to show
		if($startIndex + $entriesPerPage > $totalEntries){
			$stopIndex = $totalEntries;
		} else {
			$stopIndex = $startIndex + $entriesPerPage;
		}

		//Get that part of the array for showing
		$headers = array_slice($headers, $startIndex, $stopIndex);

		//Loop through to Display Messages
		for($i = 0; $i < sizeof($headers); $i++){
			//Get all the main contents (i.e. FROM, Subject, Date)
			$mailHeader = @imap_headerinfo($mailbox, $headers[$i]);
			$from = $mailHeader->fromaddress;
			$to = $mailHeader->toaddress;
			$subject = strip_tags($mailHeader->subject);
			$date = $mailHeader->date;

			//Truncate values for display
			$to = truncateString($to,20);
			$from = truncateString($from,20);
			$subject = truncateString($subject,15);

			//Make sure that TO and FROM is not empty, or else put "unknown"
			if(empty($to)){$to = 'Unknown';}
			if(empty($from)){$from = 'Unknown';}

			if (strlen($subject) == 0){
				//Assume message content is on Part 1
				$message = imap_fetchbody($mailbox, trim($mailHeader->Msgno), '1', FT_PEEK);
				$subject = truncateString($message, 15);
			}


			//$from = str_replace("@", "&#64;", $from);
			$from = str_replace("<", "&lt;", $from);
			$from = str_replace(">", "&gt;", $from);
			//If unread then start bold * italicized tag
			if ( ($mailHeader->Unseen == 'U') || ($mailHeader->Recent == 'N') ){
				if($bid == 2){
					//print '<small><em>To: '.$to.'</em></small><br />';
					print '<small><b><i>To: '.$to.'</i></b></small><br />';
				} else {
					print '<small><b><i>'. $from .'</i></b></small><br />';
				}
				print '<table border="0" width="100%"><tr><td width="7" valign="top">&nbsp;</td><td width="90%" valign="top" align="left"><b><i>*&nbsp;<a href="mail_view_wap.php?msg='.trim($mailHeader->Msgno).'&amp;bid='.$bid.'&amp;pagenum='.$pageNum.'">'.$subject.'</a>&gt;&gt;</i></b> '.date('d M', strtotime($date)).'</td></tr></table>';
			} else {
				if($bid == 2){
					print '<small>To: '.$to.'</small><br />';
				} else {
					print '<small>'.$from.'</small><br />';
				}
				print '<table border="0" width="100%"><tr><td width="7" valign="top">&nbsp;</td><td width="90%" valign="top" align="left"><a href="mail_view_wap.php?msg='.trim($mailHeader->Msgno).'&amp;bid='.$bid.'&amp;pagenum='.$pageNum.'">'.$subject.'</a>&gt;&gt; '.date('d M', strtotime($date)).'</td></tr></table>';
			}
		}

		//Start of the paging links
		print '<center>';
		//First Page
		if ($pageNum > 1){
			print '<small><a href="mail_wap.php?pagenum=1&amp;bid='.$bid.'">&lt;&lt;</a>&nbsp;</small>';

			//The page before
			print '<small><a href="mail_wap.php?pagenum='.($pageNum-1).'&amp;bid='.$bid.'">&lt;&lt;</a>&nbsp;</small>';
		}

		//The current page out of how many in total
		print '<small>'.($pageNum).'/'.($totalPages).'&nbsp;</small>';

		//the page after
		if ($pageNum < ($totalPages)){
			print '<small><a href="mail_wap.php?pagenum='.($pageNum+1).'&amp;bid='.$bid.'">&gt;</a>&nbsp;</small>';

			//Last Page
			print '<small><a href="mail_wap.php?pagenum='.($totalPages).'&amp;bid='.$bid.'">&gt;&gt;</a></small>';
		}
		print '</center>';
	} else {
		//The specified box is empty
		if($bid == 2){
			print '<small>Your Sent folder is empty.</small><br/><br/>';
		}else if($bid == 3){
			print '<small>Your Deleted folder is empty.</small><br/><br/>';
		} else {
			print '<small>Your Inbox is empty.</small><br/><br/>';
		}
	}

	//Show the appropriate links for different mail boxes
	if($bid == 2){
		//Sent Items
		if($headers){
			print '<small><a href="mail_deleteall_wap.php?bid='.$bid.'">Delete All</a></small><br/>';
		}
		print '<small><a href="mail_compose_wap.php?bid='.$bid.'">Create New</a></small><br/>';
		print '<small><a href="mail_wap.php?bid=1">Inbox</a>&nbsp;</p>';
		print '<small><a href="mail_wap.php?bid=3">Deleted</a>&nbsp;</p>';
		print '<small><a href="mail_settings_wap.php?bid='.$bid.'">Mail Settings</a></small><br/>';
	}else if($bid == 3){
		//Deleted Items
		if($headers){
			print '<small><a href="mail_deleteall_wap.php?bid='.$bid.'">Delete All</a></small><br/>';
		}
		print '<small><a href="mail_compose_wap.php?bid='.$bid.'">Create New</a></small><br/>';
		print '<small><a href="mail_wap.php?bid=1">Inbox</a></small><br/>';
		print '<small><a href="mail_wap.php?bid=2">Sent</a></small><br/>';
		print '<small><a href="mail_settings_wap.php?bid='.$bid.'">Mail Settings</a></small><br/>';
	} else {
		//Inbox
		if($headers){
			print '<small><a href="mail_deleteall_wap.php?bid='.$bid.'">Delete All</a></small><br/>';
		}
		print '<small><a href="mail_compose_wap.php?bid='.$bid.'">Create New</a></small><br/>';
		print '<small><a href="mail_wap.php?bid=2">Sent</a></small><br/>';
		print '<small><a href="mail_wap.php?bid=3">Deleted</a></small><br/>';
		print '<small><a href="mail_settings_wap.php?bid='.$bid.'">Mail Settings</a></small><br/>';
	}
	$bu = backUrl();
	print "<small><a href=\"$prog?cmd=home\">Home</a></small><br/>";
?>
</body>
</html>
<?php
}catch(Exception $e){
	$error = 'There has been an error while performing that action. Error:'.$e->getMessage();

	emitHeader();

	if($bid == 2){
		emitTitle("Sent");
	}else if($bid == 3){
		emitTitle("Deleted");
	} else {
		emitTitle("Inbox");
	}

	print checkForMsgs('','');

	print '<small><font style="color:red">'.$error.'</font></small><br/><br/>';

	if($bid == 2){
		print '<small><a href="mail_wap.php?bid=2&amp;pagenum='.$pageNum.'">Sent</a></small><br/>';
	}else if($bid == 3){
		print '<small><a href="mail_wap.php?bid=3&amp;pagenum='.$pageNum.'">Deleted</a></small><br/>';
	} else {
		print '<small><a href="mail_wap.php?bid=1&amp;pagenum='.$pageNum.'">Inbox</a></small><br/>';
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
