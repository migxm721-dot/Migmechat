<?php
include_once("../../common/common-inc.php");
include_once("../../common/pageletFunctions.php");
include_once("../../common/menu.php");
include_once("../../common/profileFunctions.php");

//Get session
ice_check_session();
$sessUser = ice_get_username();

//Get HTTP GET data
$username = $_GET['username'];
$pageNum = 1;
if($_GET['pagenum'] != ''){
	$pageNum = $_GET['pagenum'];
}
settype($pageNum, 'int');

//Set max number entries to be shown
$numEntries = 10;

//Attempt to load the profile, either a requested profile or your own profile
$userDTO = getProfileData($sessUser, $username, $userProfile, $userDetails, $contact, $error);

//A variable that indicates if the user is browsing his own profile
if($username == $sessUser || $username == ''){
	$viewOwn = true;
}

//Set username to whichever that the profile belongs to
if($username == ''){
	$username = $sessUser;
}

//Get midlet version of the mobile device currently browsing this page.
getMidletVersion();

?>
<html>
	<head>
    	<title>
<?php
		//Different titles depending on who is viewing whos profile
		if($viewOwn)
		{
			echo 'My Updates';
		}
		else
		{
			echo 'Updates';
		}
?>
    	</title>
  	</head>
<?php
	//From version 4.00 onwards, kbrowser will be able to support pagelet generated menus
	//Generate menu for Profile page (Also make sure there are no errors from loading anything from SOAP).
	if (isMidletVersion4())
	{
		$menu = new Menu("LEFT", truncateString($username, $menuContextualTitleMaxLength).' - Updates', "Menu");

		//Menu for user's own profile page
		if($viewOwn)
		{
			$menu->addMenuItem("Set Status Message", "mig33:updateStatus()");
			$menu->addMenuItem(sprintf("Privacy Settings (%s)", $userDTO->getStatus()), $server_root."/midlet/member/privacy_settings.php");
		}
		//Menu when viewing someone else's profile page
		else
		{
			$menu->addMenuItem("Invite to be Friends", sprintf("mig33:inviteFriend(%s)", $username) );
		}

		$menu->show();
		showCommonStyle();
	}
?>
	<body bgcolor="white">
<?php
	/*******************Show short profile*************************/
  	getShortProfile($userDTO, $viewOwn, false);

// Determine Privacy Level
if($userDTO->status == 'PRIVATE' && !$viewOwn) {
	echo 'This profile has been marked as private';
} elseif(($userDTO->status == 'CONTACTS_ONLY' || $userDTO->status == 'PRIVATE') && !$userDTO->contactDetail['isContact']) {
	echo 'You must be friends first before you can view this profile';
} else {

	/******************Show Updates*************************/
	$pagingObject = new UserPagingObject($username, $pageNum, 5);
	$return_data = getUpdates( $pagingObject );
	$numPages = $return_data['total_pages'];

	showNavigation( $server_root."/midlet/member/view_updates.php?username=".$username, $pageNum, $numPages, false );
	if( !isMidletVersion4() )
	{
		printf('<p><a href="%s/midlet/member/privacy_settings.php">Privacy Settings (%s)</a></p>', $server_root, $userDTO->getStatus());
	}
}
?>
	</body>
</html>