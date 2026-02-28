<?php
include_once("../../common/common-inc.php");

// Check Session and Load UserData
if(!$userData){
	ice_check_session();
	$userData = ice_get_userdata();
}
?>
<html>
	<head>
		<title>LookOut</title>
	</head>
	<body bgcolor="white">
<?php
$contact = '';
if (isset($_GET['contact']))
	$contact = $_GET['contact'];

if ($_GET['delete'] == '1') {
	try {
		soap_call_ejb('removeLookout', array($userData->username, $contact));
	}
	catch(Exception $e) {
		$error = $e->getMessage();
	}

	if(empty($error)) {
		print '<p style="color:green">LookOut deleted</p>';
	}
	else {
		print '<p style="color:red">' . $error . '</p>';
	}
}

try {
	$lookouts = soap_call_ejb('getLookouts', array($userData->username));
}
catch (Exception $e) {
?>
<html>
	<head><title>LookOut</title></head>
  	<body bgcolor="white">
		<p>Sorry, an error occurred. Please close this screen and try again.</p>
		<p>Error: <?= $e->getMessage() ?></p>
	</body>
</html>
<?php
	die;
}

if (is_array($lookouts) && count($lookouts) >= 1) {
	print '<p>Your current LookOuts:</p>';
	foreach ($lookouts as $lookout) {
		print ' ' . $lookout . ' <a href="'.$server_root.'/midlet/member/lookout_edit.php?delete=1&contact=' . urlencode($lookout) . '">Delete</a><br>';
	}
}
else {
	print '<p>You do not have any LookOuts</p>';
}
?>
	</body>
</html>
