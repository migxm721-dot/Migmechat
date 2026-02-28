<?php
require_once('../../common/common-inc.php');
ice_check_session();

$title = $_GET['title'];
$return = $_GET['return'];
$page = $_GET['page'];
$id = $_GET['id'];
$type = $_GET['type'];

//Make sure $id is int
settype($id, 'int');

?>
<html>
  <head>
    <title><?php print $title ?></title>
  </head>
  <body bgcolor="white">
	<?php
		//Get the details, different for each type
		if ($type == "acct")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<p><b>Date</b></p>';
				print '<p>'.date('r', $entry['dateCreated']).'</p><br>';
				print '<p><b>Amount</b></p>';
				print '<p>'.number_format(abs($entry['amount']), 2).' '.$entry['currency'].'</p><br>';
				print '<p><b>Description</b></p>';
				print '<p>'.$entry['description'].'</p><br>';
			}catch(Exception $e){
				print '<p>Error: '.($e->getMessage()).'</p><br>';
			}
		}
		else if ($type == "sms")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));
				print '<p><b>Date</b></p>';
				print '<p>'.date('r', $entry['dateCreated']).'</p><br>';
				print '<p><b>Destination</b></p>';
				print '<p>'.$entry['destination'].'</p><br>';
				print '<p><b>Message</b></p>';
				print '<p>'.$entry['messageText'].'</p><br>';

			}catch(Exception $e){
				print '<p>Error: '.($e->getMessage()).'</p><br>';
			}
		}
		else if ($type == "call")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<p><b>Date</b></p>';
				print '<p>'.date('r', $entry['dateCreated']).'</p><br>';
				print '<p><b>Return Call To</b></p>';
				print '<p>'.$entry['source'].'</p><br>';
				print '<p><b>Call Destination</b></p>';
				print '<p>'.$entry['destination'].'</p><br>';
				print '<p><b>Duration (s)</b></p>';
				print '<p>'.$entry['billedDuration'].'</p><br>';
				print '<p><b>Cost</b></p>';
				print '<p>'.abs($entry['amount']).' '.$entry['currency'].'</p><br>';

			}catch(Exception $e){
				print '<p>Error: '.($e->getMessage()).'</p><br>';
			}
		}
		else if ($type == "tt")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<p><b>Date</b></p>';
				print '<p>'.date('r', $entry['dateCreated']).'</p><br>';
				print '<p><b>Receipt Number</b></p>';
				print '<p>'.$entry['receiptNumber'].'</p><br>';
				print '<p><b>Full Name</b></p>';
				print '<p>'.$entry['fullName'].'</p><br>';
				print '<p><b>Amount</b></p>';
				print '<p>'.$entry['amount'].'</p><br>';

			}catch(Exception $e){
				print '<p>Error: '.($e->getMessage()).'</p><br>';
			}
		}

	?>

	<p><a href="<?=$server_root?>/midlet/member/history.php?cp=<?php print $return ?>&page=<?php print $page ?>">Back to Account History</a> &gt;&gt;</p>
  </body>
</html>