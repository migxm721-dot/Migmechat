<?php
include_once("../../common/common-inc.php");
include_once("../../common/pageletFunctions.php");
include_once("../../common/profileFunctions.php");

global $server_root;

ice_check_session();
$sessUser = ice_get_username();

header("TTL: 3600");

//All variables used. Set to a value if theres $_GET data for it
$username = '';
$error = '';
$loadError = '';
$pageNum=1;

if(isset($_GET['username'])){
	$username = strip_tags($_GET['username']);
}
if(isset($_GET['pagenum'])){
	$pageNum = $_GET['pagenum'];
	settype($pageNum, "integer");
}

$isContact = false;

//Attempt to load the profile, either a requested profile or your own profile
$userDTO = getProfileData($sessUser, $username, $userProfile, $userDetails, $isContact, $error);

//A variable that indicates if the user is browsing his own profile
if($username == $sessUser || $username == ''){
	$viewOwn = true;
}

//Set username to whichever that the profile belongs to
if($username == ''){
	$username = $sessUser;
}
?>

<html>
  <head>
    <title>View Friends</title>
    <?php
    	if( isMidletVersion4() )
	    	showCommonStyle();
    ?>
  </head>
  <body bgcolor="white">

<?php
	if(empty($error)){
 		getShortProfile($userDTO, $viewOwn, false );
 	}
// Determine Privacy Level
if($userDTO->status == 'PRIVATE' && !$viewOwn) {
	echo 'This profile has been marked as private';
} elseif(($userDTO->status == 'CONTACTS_ONLY' || $userDTO->status == 'PRIVATE') && !$userDTO->contactDetail['isContact']) {
	echo 'You must be friends first before you can view this profile';
} else {
 	$pagingObject = new UserPagingObject($username, $pageNum, 5);
 	$content = getFriends($sessUser, $pagingObject, 30);
 	$page = $content['pageNumber'];
	$num_pages = $content['totalPages'];
	if( $num_pages > 0 )
		showNavigation($server_root . "/midlet/member/view_friends.php?username=".$username, $page, $num_pages, false);
}
?>
	<br>