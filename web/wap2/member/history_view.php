<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

ice_check_session();

//Get Parameters
//$keys = array_keys($_GET);
//$values = array_values($_GET);

$title = $_GET['title'];
$return = $_GET['return'];
$page = $_GET['page'];
$id = $_GET['id'];
$type = $_GET['type'];

//Make sure $id is int
settype($id, 'int');


emitHeader();
emitTitle($title);
		//Get the details, different for each type
		if ($type == "acct")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<small><b>Date</b></small><br/><small>' . date('r', $entry['dateCreated'])  . '</small><br/>';
				print '<small><b>Amount</b></small><br/><small>' . $entry['amount'] . ' ' . $entry['currency'] . '</small><br/>';
				print '<small><b>Description</b></small><br/><small>' . $entry['description'] . '</small><br/>';

			}
			catch(Exception $e){$error = 'Error ' . $e->getMessage();}
		}
		else if ($type == "sms")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<small><b>Date</b></small><br/><small>' . date('r', $entry['dateCreated'])  . '</small><br/>';
				print '<small><b>Destination</b></small><br/><small>' . $entry['destination'] . '</small><br/>';
				print '<small><b>Message</b></small><br/><small>' . $entry['messageText'] . '</small><br/>';

			}
			catch(Exception $e){$error = 'Error ' . $e->getMessage();}
		}
		else if ($type == "call")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<small><b>Date</b></small><br/><small>' . date('r', $entry['dateCreated'])  . '</small><br/>';
				print '<small><b>Return Call To</b></small><br/><small>' . $entry['source'] . '</small><br/>';
				print '<small><b>Call Destination</b></small><br/><small>' . $entry['destination'] . '</small><br/>';
				print '<small><b>Duration (s)</b></small><br/><small>' . $entry['billedDuration'] . '</small><br/>';
				print '<small><b>Cost</b></small><br/><small>' . abs($entry['amount']) . ' ' . $entry['currency'].'</small><br/>';

			}
			catch(Exception $e){$error = 'Error ' . $e->getMessage();}
		}
		else if ($type == "tt")
		{
			try
			{
				$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

				print '<small><b>Date</b></small><br/><small>' . date('r', $entry['dateCreated'])  . '</small><br/>';
				print '<small><b>Receipt Number</b></small><br/><small>' . $entry['receiptNumber'] . '</small><br/>';
				print '<small><b>Full Name</b></small><br/><small>' . $entry['fullName'] . '</small><br/>';
				print '<small><b>Amount</b></small><br/><small>' . $entry['amount'] . '</small><br/>';

			}
			catch(Exception $e){$error = 'Error ' . $e->getMessage();}
		}

	?>

	<small><a href="history.php?cp=<?php print $return ?>&amp;page=<?php print $page ?>">Back</a></small><br/>

  </body>
</html>