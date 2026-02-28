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
   			<title>Buzz Settings</title>
  		</head>
  		<body bgcolor="white">
  			<p>&nbsp;</p>
<?php
if (isset($_POST['update'])) {
	// Store whether the user wants to receive Buzz messages
	try {
		$userDetails = soap_call_ejb('loadUserDetails', array($userData->username));
		if ($_POST['allow_buzz'] == 'on') {
			$userDetails['allowBuzz'] = '1';
		}
		else {
			$userDetails['allowBuzz'] = '0';
		}
		soap_call_ejb('updateUserDetails',soap_prepare_call($userDetails));
		$userData->allowBuzz = $userDetails['allowBuzz'];
	} catch(Exception $e) {
		$error = $e->getMessage();
	}

	if(empty($error)) {
		print '<p style="color:green">Your setting has been saved</p>';
	}
	else {
		print '<p style="color:red">' . $error . '</p>';
	}
}
?>
			<form method="POST" action="<?=$server_root?>/midlet/member/buzz_edit.php">
				<p><input type="checkbox" name="allow_buzz"<?php if ($userData->allowBuzz == 1) { print ' checked'; } ?>>Allow other users to Buzz me</input></p>
				<p><input type="submit" value="Submit"></p>
				<input type="hidden" name="update" value="1">
			</form>
  		</body>
  	</html>
