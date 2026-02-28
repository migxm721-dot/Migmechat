<?php
include_once("store_emoticons.php");
include_once("store_ringtones.php");
include_once("store_wallpaper.php");
include_once("store_apps.php");
include_once("store_purchases.php");
include_once("store_virtualgifts.php");
include_once("../../common/pageletFunctions.php");

// Function to display the migme store.
// $username: The username of the user viewing the store
// $countryID: The ID of the country the user is in
// $clientType: Where this is being displayed. May be "AJAX", "WAP" or "Midlet"
function showStore($username, $countryID, $authenticated, $clientType){
	settype($countryID, "int");

	$br = '<br/>';
	if ($clientType == 'Midlet'){
		$br = '<br>';
	}

	if (isset($_GET['loc']))
		$location = $_GET['loc'];
	else if (isset($_POST['loc']))
		$location = $_POST['loc'];

	// We are no longer hiding ringtones and wallpapers from some users.
	// The comments and code below have been left for posterity...

//	// We want to hide the ringtones and wallpapers from users unless they are:
//	// 1. In a certain country, or
//	// 2. In a list of allowed usernames

//	// 137 = Maldives; 231 = USA
//	if ($countryID != 137/* && $countryID != 231*/) {
//		// If the username is not in the list below, hide ringtones and wallpapers from the user:
//		if (stripos('crazygrape elmocookie coauthor sf_buck lisa kien koko jazzbo pinchy dave natcat sjuang dgarstang scoday ninjachic47 martinjwells jkinsf mreid80289 thenephilim surfbo jones_cai hiphopgw gw12345 e2test caixyz pc-gan sspidey yellowpink joemac izzaldin putra1974 aishue babycute27 lutiek', $username) === false) {
//			$location = 'emot';
//		}
//	}

	if ($location == 'emot') {
		showEmoticonStore($username, $countryID, $clientType, $br);
	}
	elseif ($location=='vg') {
		showVirtualGiftStore($_GET['username'], $br);
	}
	//elseif ($location == 'bgrnd')
	//	showBackgrounds($username, $countryID, $clientType, $br);
	elseif ($location == 'ring')
		showRingtoneStore($username, $countryID, $authenticated, $clientType, $br);
	elseif ($location == 'wall')
		showWallpaperStore($username, $countryID, $authenticated, $clientType, $br);
	elseif ($location == 'app')
		showAppStore($username, $countryID, $authenticated, $clientType, $br);
	else if ($location == 'help') {
		showHelp($username, $countryID, $clientType, $br);
	}
	else if ($location == 'purchased') {
		showPurchases($username, $countryID, $clientType, $br);
	}
	else if($location == 'kicking')
	{
		include_once("templates/store_kicking.php");
	}
	else if($location == 'buzz')
	{
		include_once("templates/store_buzz.php");
	}
	else if($location == 'lookout')
	{
		include_once("templates/store_lookout.php");
	}
	else if($location == 'email')
	{
		include_once("templates/store_email.php");
	}
	else if($location == 'sms')
	{
		include_once("templates/store_sms.php");
	}
	else {
		showStoreFront($username, $countryID, $clientType, $br);
	}
}

function showStoreFront($username, $countryID, $clientType, $br) {
	try {
		$balance_data = soap_call_ejb('getAccountBalance', array($username));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	$balance = number_format($balance_data['balance'], 2);
	$currency = $balance_data['currency.code'];
	include_once("templates/store_front.php");
/*
	print '<p>Welcome to the migme store!</p>';

	// Get the user's account balance
	try {
		$balance = soap_call_ejb('getAccountBalance', array($username));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}
	print '<p>Your balance: ' . number_format($balance['balance'], 2) . ' ' . $balance['currency.code'] . '</p>';

	//print '<p>Browse our store below, or <a href="?loc=help">find out more</a>.</p>';
	//print '<p><a href="store.php?loc=emot">Browse emoticons</a> now, or <a href="store.php?loc=help">find out more</a>.</p>';

	print '<p><b>For migme:</b>' . $br;
	print '<a href="store.php?loc=vg&ploc=9">Gifts</a>' . $br;
	if( isMidletVersion4() )
	{
		print '<a href="view_theme.php">Themes</a>'.$br;
	}
	print '<a href="store.php?loc=emot">New Emoticons</a>' . $br;
	//print '<a href="store.php?loc=bgrnd">Backgrounds</a>' . $br;
	print '</p>';

	print '<p><b>For your phone:</b>' . $br;
	print '<a href="store.php?loc=ring">Ringtones</a>' . $br;
	print '<a href="store.php?loc=wall">Wallpapers</a>' . $br;

	//showExternalLink('http://m.mig33.getjar.com/export_wap/software/universal/all/Games?lvt=1229053927&ref=0', 'Try fun games');
	print $br;

	// Only show the "Games" link if the username is in the list below
	if (stripos('crazygrape elmocookie coauthor sf_buck lisa kien koko jazzbo dave natcat sjuang dgarstang scoday ninjachic47 martinjwells jkinsf mreid80289 thenephilim surfbo jones_cai hiphopgw gw12345 e2test caixyz pc-gan sspidey yellowpink joemac izzaldin putra1974 aishue babycute27 lutiek', $username) !== false) {
		print '<a href="store.php?loc=app">Games</a>' . $br;
	}

	print '</p>';

	print '<p><a href="store.php?loc=purchased">View items you have purchased</a>' . $br;
	print '</p>';

	print '<p><a href="my_account.php?username=' . $username . '">My Account</a>&gt;&gt;' . $br;
	print '<a href="view_profile.php">My Profile</a>&gt;&gt;' . $br;
	print '<a href="view_photos.php?username=' . $username . '">My Photos</a>&gt;&gt;</p>';

	print $br;
*/
}

function showHelp($username, $countryID, $clientType, $br) {
	global $server_root;
	print '<p>Nothin\' here yet</p>';
	print '<p><a href="'.$server_root.'/midlet/member/store.php">Back</a></p>';
}
?>