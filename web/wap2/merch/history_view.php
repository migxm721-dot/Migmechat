<?php
//include_once("../../common/common-inc.php");
include_once("../member2/common-inc-kk.php");

session_start();
check_session_merchant();

$title = $_GET['title'];
$return = $_GET['return'];
$page = $_GET['page'];
$id = $_GET['id'];
$type = 'tt';

//Make sure $id is int
settype($id, 'int');

emitHeader();
emitTitle("History View");

try
{
	$entry = soap_call_ejb('getHistoryEntry', array($id, $type));

	print '<small><b>Date</b><br/>' . date('r', $entry['dateCreated'])  . '</small><br/>';
	print '<small><b>Receipt Number</b><br/>' . $entry['receiptNumber'] . '</small><br/>';
	print '<small><b>Full Name</b><br/>' . $entry['fullName'] . '</small><br/>';
	print '<small><b>Amount</b><br/>' . $entry['amount'] . '</small><br/>';

}
catch(Exception $e){$error = 'Error ' . $e->getMessage();}
?>

	<small><a href="history.php?page=<?=$page?>">Back</a></small><br/>
	<br/>
	<small><a href="merchant_center.php">Merchant Home</a></small><br/>
	<small><a href="logout.php">Logout</a></small><br/>
	<br/>
  </body>
</html>