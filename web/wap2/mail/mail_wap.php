<?php
include_once("../common-inc-kk.php");
include_once("../emit.php");

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

	?>
	<html>
		<head>
			<?php
				if($bid == 2){
					print '<title>Sent Items</title>';
				}else if($bid == 3){
					print '<title>Deleted Items</title>';
				} else {
					print '<title>My Inbox</title>';
				}
			?>
		</head>
		<body bgcolor="white">
		<?php

		//If theres a success message (mainly from composing an email), show it
		if($success){
			print '<p style="color:green">'.$success.'</p>';
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


				//If unread then start bold tag
				if ( ($mailHeader->Unseen == 'U') || ($mailHeader->Recent == 'N') ){
					if($bid == 2){
						print '<p><small><b>To: '.$to.'</b></small></p>';
					} else {
						print '<p><small><b>'.$from.'</b></small></p>';
					}
					print '<p><small><b>&nbsp;<a href="mail_view.php?msg='.trim($mailHeader->Msgno).'&bid='.$bid.'&pagenum='.$pageNum.'">'.$subject.'</a>&gt;&gt; '.date('d M', strtotime($date)).'</b></small></p>';
				} else {
					if($bid == 2){
						print '<p><small>To: '.$to.'</small></p>';
					} else {
						print '<p><small>'.$from.'</small></p>';
					}
					print '<p><small>&nbsp;<a href="mail_view.php?msg='.trim($mailHeader->Msgno).'&bid='.$bid.'&pagenum='.$pageNum.'">'.$subject.'</a>&gt;&gt; '.date('d M', strtotime($date)).'</small></p>';
				}
			}

			//Start of the paging links
			print '<br><p><center>';
			//First Page
			if ($pageNum > 1){
				print '<a href="mail.php?pagenum=1&bid='.$bid.'">&lt;&lt;</a>&nbsp;';

				//The page before
				print '<a href="mail.php?pagenum='.($pageNum-1).'&bid='.$bid.'">&lt;</a>&nbsp;';
			}

			//The current page out of how many in total
			print ''.($pageNum).'/'.($totalPages).'&nbsp;';

			//the page after
			if ($pageNum < ($totalPages)){
				print '<a href="mail.php?pagenum='.($pageNum+1).'&bid='.$bid.'">&gt;</a>&nbsp;';

				//Last Page
				print '<a href="mail.php?pagenum='.($totalPages).'&bid='.$bid.'">&gt;&gt;</a>';
			}
			print '</center></p><br>';
		} else {
			//The specified box is empty
			if($bid == 2){
				print '<p><small>Your Sent Items box is empty.</small></p><br>';
			}else if($bid == 3){
				print '<p><small>Your Deleted Items box is empty.</small></p><br>';
			} else {
				print '<p><small>Your Inbox is empty.</small></p><br>';
			}
		}

		//Show the appropriate links for different mail boxes
		if($bid == 2){
			//Sent Items
			if($headers){
				print '<p><a href="mail_deleteall.php?bid='.$bid.'">Delete All &gt;&gt;</a>&nbsp;</p>';
			}
			print '<p><a href="mail_compose.php?bid='.$bid.'">Create New &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail.php?bid=1">Inbox &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail.php?bid=3">Deleted Items &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail_settings.php?bid='.$bid.'">Settings &gt;&gt;</a>&nbsp;</p>';
		}else if($bid == 3){
			//Deleted Items
			if($headers){
				print '<p><a href="mail_deleteall.php?bid='.$bid.'">Delete All &gt;&gt;</a>&nbsp;</p>';
			}
			print '<p><a href="mail_compose.php?bid='.$bid.'">Create New &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail.php?bid=1">Inbox &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail.php?bid=2">Sent Items &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail_settings.php?bid='.$bid.'">Settings &gt;&gt;</a>&nbsp;</p>';
		} else {
			//Inbox
			print '<p><a href="mail.php?bid=1">Refresh Inbox</a></p>';
			if($headers){
				print '<p><a href="mail_deleteall.php?bid='.$bid.'">Delete All &gt;&gt;</a>&nbsp;</p>';
			}
			print '<p><a href="mail_compose.php?bid='.$bid.'">Create New &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail.php?bid=2">Sent Items &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail.php?bid=3">Deleted Items &gt;&gt;</a>&nbsp;</p>';
			print '<p><a href="mail_settings.php?bid='.$bid.'">Settings &gt;&gt;</a>&nbsp;</p>';
		}
		print '<p><a href="mail_help.php?bid='.$bid.'&pagenum='.$pageNum.'">Help &gt;&gt;</a>&nbsp;</p>';
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
					print '<title>Sent Items</title>';
				}else if($bid == 3){
					print '<title>Deleted Items</title>';
				} else {
					print '<title>My Inbox</title>';
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