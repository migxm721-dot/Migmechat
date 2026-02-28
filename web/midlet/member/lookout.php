<?php
include_once("../../common/common-inc.php");
include_once("../../common/costs.php");

// Check Session and Load UserData
if(!$userData){
	ice_check_session();
	$userData = ice_get_userdata();
}

$sessUser = ice_get_username();

$contactId = '';
if (isset($_GET['cid']))
	$contactId = $_GET['cid'];
else if (isset($_POST['cid']))
	$contactId = $_POST['cid'];
else
	die;
settype($contactId, 'int');

if ($userData->mobileVerified == "0") {
?>
	<html>
		<head>
   			<title>LookOut</title>
  		</head>
  		<body bgcolor="white">
  			<p>&nbsp;</p>
			<p>A mobile number is required to use this feature, you can get full access to all mig33 features by adding your mobile number to your account via "<a href="mig33:newBrowser(<?=$server_root?>/sites/midlet/settings/account_contact)">Settings > Mobile Number</a>", and activate it with the authentication code sent to you via SMS.</p>
  		</body>
  	</html>
<?php
	die;
}

// If POST then setup the LookOut
if (isset($_POST['contact_name'])) {
	try {
		if( $sessUser != $_POST['contact_name'] )
		{
			soap_call_ejb('createLookout', array($userData->username, $_POST['contact_name']));
		}
		else
		{
			$error = "Unable to create a LookOut on yourself.";
		}
	}
	catch(Exception $e) {
		$error = $e->getMessage();
	}

	if (!$error) {
?>
<html>
	<head>
		<title>LookOut</title>
	</head>
	<body bgcolor="white">
		<p>You have created a LookOut for your contact <?= $_POST['contact_name'] ?> and
		will now receive an SMS whenever <?= $_POST['contact_name'] ?> is online.</p>
		<p><a href="<?=$server_root?>/midlet/member/lookout_edit.php">Edit my LookOuts</a></p>
	</body>
</html>
<?php
		die;
	}
}

// Grab details of the contact from the DB
try {
	$contactData = soap_call_ejb('getContact', array($contactId));
}
catch(Exception $e){
	$error = $e->getMessage();
?>
<html>
	<head><title>LookOut</title></head>
  	<body bgcolor="white">
		<p>Sorry, an error occurred. Please close this screen and try again.</p>
		<p>Error: <?= $error ?></p>
	</body>
</html>
<?php
	die;
}

// Make sure the contact is a mig33 contact
if ($contactData['fusionUsername'] == "") {
?>
<html>
	<head><title>LookOut</title></head>
  	<body bgcolor="white">
		<p>Sorry, you can only create a LookOut for mig33 contacts.</p>
	</body>
</html>
<?php
	die;
}
else if( $sessUser == $contactData['fusionUsername'] )
{
?>
<html>
	<head><title>LookOut</title></head>
  	<body bgcolor="white">
		<p>Sorry, you cannot create a LookOut for yourself.</p>
	</body>
</html>

<?php
die;
}

?>
<html>
	<head>
    	<title>LookOut</title>
  	</head>
  	<body bgcolor="white">
  		<?php
  			if ($error) {
  				print '<p style="color:red">' . $error . '</p>';
  			}

			// See if a lookout already exists
			$lookoutExists = 0;
			try {
				$lookoutExists = soap_call_ejb('lookoutExists', array($userData->username, $contactData['fusionUsername']));
			}
			catch(Exception $e){
				$error = $e->getMessage();
			}

			if ($lookoutExists == 1) {
		?>
			<p>You currently have a LookOut setup for your friend <?= $contactData['fusionUsername'] ?>.</p><br>
			<p>Whenever <?= $contactData['fusionUsername'] ?> comes online you will receive an SMS.</p><br>
			<p><a href="<?=$server_root?>/midlet/member/lookout_edit.php?delete=1&contact=<?= $contactData['fusionUsername'] ?>">Stop looking out for <?= $contactData['fusionUsername'] ?></a></p><br>
		<?php
			}
			else {
				try {
					soap_call_ejb('isLookoutPossible', array($userData->username, $contactData['fusionUsername']));
				}
				catch(Exception $e) {
					$isPossibleError = $e->getMessage();
				}

				if (!$isPossibleError) {
  		?>
		<p><b>Create a LookOut for your contact <?= $contactData['fusionUsername'] ?>!</b></p>
		<p>When you create a LookOut you will receive an SMS whenever <?= $contactData['fusionUsername'] ?> is online.</p>
		<?php
			$cost = getLookoutCost($sessUser);
			printf("<p>Each SMS costs %s %0.02f</p>", $cost["currency"], $cost["price"]);
		?>

		<form method="POST" action="<?=$server_root?>/midlet/member/lookout.php">
			<p><input type="submit" value="Create Lookout"></p>
			<input type="hidden" name="cid" value="<?= $contactId ?>">
			<input type="hidden" name="contact_name" value="<?= $contactData['fusionUsername'] ?>">
		</form>

		<?php
				}
				else {
					print '<p>LookOut is not allowed with this user.</p><br>';
				}
			}
		?>
		<p><a href="<?=$server_root?>/midlet/member/lookout_edit.php">Edit my LookOuts</a></p>
	</body>
</html>
