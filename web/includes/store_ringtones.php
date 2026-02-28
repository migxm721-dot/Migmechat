<?php
require_once("../../common/common-config.php");

function showRingtoneStore($username, $countryID, $authenticated, $clientType, $br) {
	if (empty($_GET['contentid']))
		showRingtones($username, $countryID, $clientType, $br);
	elseif (empty($_GET['buy']))
		showRingtone($username, $countryID, $clientType, $br);
	elseif (empty($_GET['confirm']))
		showRingtoneBuyConfirmation($username, $countryID, $authenticated, $clientType, $br);
	elseif (empty($_GET['tryagain']))
		buyRingtone($username, $countryID, $clientType, $br);
	else
		waitForRingtoneDownloadURL($username, $countryID, $clientType, $br);
}

function showRingtones($username, $countryID, $clientType, $br) {
	// The root ContentCategory.ID value in our DB for Ringtones
	$RINGTONE_PARENT_CATEGORY_ID = 1;
	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	global $server_root;

	if (empty($_GET['categoryid']))
		$categoryId = $RINGTONE_PARENT_CATEGORY_ID;
	else
		$categoryId = $_GET['categoryid'];
	settype($categoryId, 'int');

	if (empty($_GET['categorypage']))
		$categoryPage = 1;
	else
		$categoryPage = $_GET['categorypage'];
	settype($categoryPage, 'int');

	if (empty($_GET['contentpage']))
		$contentPage = 1;
	else
		$contentPage = $_GET['contentpage'];
	settype($contentPage, 'int');

	// Display any categories
	try {
		$categories = soap_call_ejb('getMobileContentCategories', array($username, $categoryId, $group_id, $categoryPage, 12));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	$show_header = true;
	$numCategories = $categories[0]['numEntries'];
	if ($numCategories >= 1) {
		$show_header = false;
		$numCategoryPages = $categories[0]['numPages'];

		print '<p><b>Ringtone Categories</b></p>';
		print '<p>';
		for ($i = 1; $i < sizeof($categories); $i++) {
			print '<a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $categories[$i]['id'] . '&gcid='.$group_id.'">' . $categories[$i]['name'] . '</a>' . $br;
		}
		print '</p>';

		if ($numCategoryPages > 1) {
			print $br . '<p>';
			if ($categoryPage > 1) {
				print '<a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&categorypage=' . ($categoryPage - 1) . '&gcid='.$group_id.'">&lt; Previous Page</a>';
			}
			print ' Page ' . $categoryPage . ' of ' . $numCategoryPages . ' ';
			if ($categoryPage < $numCategoryPages) {
				print '<a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&categorypage=' . ($categoryPage + 1) . '&gcid='.$group_id.'">Next Page &gt;</a>';
			}
			print '</p>' . $br;
		}
	}

	// Display any ringtones in this category
	try {
		$content = soap_call_ejb('getMobileContent', array($username, $categoryId, $group_id, $contentPage-1, 12));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	$has_header = false;
	$numContent = $content[0]['numEntries'];
	if ($numContent >= 1) {
		$numContentPages = $content[0]['numPages'];

		if( $show_header )
		{
			$has_header = true;
			print '<html><head>';
		}
		if ($numContentPages > 1 && canPrefetch()) {
			if ($contentPage > 1) {
				print '<prefetch url="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentpage=' . ($contentPage - 1) . '&gcid='.$group_id.'">';
			}
			if ($contentPage < $numContentPages) {
				print '<prefetch url="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentpage=' . ($contentPage + 1) . '&gcid='.$group_id.'">';
			}
		}
		print '</head><body>';

		print '<p><b>Ringtones</b></p>';
		print '<p>';
		for ($i = 1; $i < sizeof($content); $i++) {
			print '<a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $categoryId . '&contentid=' . $content[$i]['id'] . '&categorypage=' . $categoryPage . '&contentpage=' . $contentPage . '&gcid='.$group_id.'">';
			if ($content[$i]['artist'] != '')
				print $content[$i]['artist'] . ' - ';
			print $content[$i]['name'] . '</a>';
			if ($i < sizeof($content) - 1) {
				print $br;
			}
		}
		print '</p>';

		if ($numContentPages > 1) {
			print $br . '<p>';
			if ($contentPage > 1) {
				print '<a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentpage=' . ($contentPage - 1) . '&gcid='.$group_id.'">&lt; Previous</a>';
			}
			print ' Page ' . $contentPage . ' of ' . $numContentPages . ' ';
			if ($contentPage < $numContentPages) {
				print '<a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentpage=' . ($contentPage + 1) . '&gcid='.$group_id.'">Next &gt;</a>';
			}
			print '</p>' . $br;
		}

		//print '<p><a href="store.php?loc=ring">Back to ringtone categories</a></p>';
	}

	print '<br>';
	print '<br>';
	if( $group_id == 0 )
		print '<p><a href="'.$server_root.'/midlet/member/store.php">Back to store front</a></p>';
	else
		print '<p><a href="'.$server_root.'/sites/index.php?c=group&v=midlet&a=store&cid='.$group_id.'">Back to group store front</a></p>';
	if( $has_header )
		print '</body></html>';
}

function showRingtone($username, $countryID, $clientType, $br) {
	// Load the content
	$contentId = $_GET['contentid'];
	settype($contentId, 'int');
	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	global $server_root;

	try {
		$content = soap_call_ejb('getMobileContentItem', array($username, $contentId, true));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	if ($content['id'] == '') {
		print '<p>Sorry, the following error occurred: Item not found</p>';
		return;
	}

	print '<p><b>' . $content['name'] . '</b>';
	if ($content['artist'] != '')
		print $br . 'by ' . $content['artist'];
	print '</p>' . $br;

	// Display a link to play a preview if the midlet is >= version 4
	$headers = apache_request_headers();
	$midletVersion = $headers['ver'];
	if (is_numeric($midletVersion)) {
		if ($midletVersion >= 4.00 && strpos($content['preview'], "http://") === 0) {
			print '<p><a href="' . $content['preview'] . '">Preview ringtone</a> (preview may not work on some phones)</p>' . $br;
		}
	}

	//if ($content['purchased'] == 1) {
	//	print '<p>You own this Ringtone!</p>';
	//}
	//else {
		if ($content['price'] == 0)
			print '<p>Price: Free!</p>';
		else
			print '<p>Price: ' . number_format($content['price'], 2) . ' ' . $content['currency'] . '</p>';

		// Get the user's account balance
		try {
			$balance = soap_call_ejb('getAccountBalance', array($username));
		} catch(Exception $e) {
			print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
			return;
		}
		print '<p>Your balance: ' . number_format($balance['balance'], 2) . ' ' . $balance['currency.code'] . '</p>';

		if ($content['currency'] == $balance['currency.code'] && $content['price'] > $balance['balance']) {
			print '<p>You do not have enough credit to purchase this item.<br><a href="'.$server_root.'/midlet/member/recharge_index.php">Add credit to your account</a></p>';
		}
		else {
			print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentid=' . $contentId . '&buy=1&gcid='.$group_id.'">Buy</a></p>';
		}
	//}

	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&categorypage=' . $_GET['categorypage'] . '&contentpage=' . $_GET['contentpage'] . '&gcid='.$group_id.'">Back</a></p>';
}

function showRingtoneBuyConfirmation($username, $countryID, $authenticated, $clientType, $br) {
	$contentId = $_GET['contentid'];
	settype($contentId, 'int');
	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	// Make sure the user has authenticated their account
	if ($authenticated == '0') {
		print '<p>You will need to authenticate your mig33 account before you can purchase mobile content</p>';
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentid=' . $contentId . '">Back</a></p>';
		return;
	}

	// Get the user's account balance
	try {
		$balance = soap_call_ejb('getAccountBalance', array($username));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	// Load the content
	try {
		$content = soap_call_ejb('getMobileContentItem', array($username, $contentId, true));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	if ($content['id'] == '') {
		print '<p>Sorry, the following error occurred: Item not found</p>';
		return;
	}

	print '<p><b>Confirm Purchase of ' . $content['name'];
	if ($content['artist'] != '')
		print ' by ' . $content['artist'];
	print '</b></p>' . $br;
	print '<p>' . number_format($content['price'], 2) . ' ' . $content['currency'] . ' will be deducted from your account.</p>';
	print '<p>Your account balance is ' . number_format($balance['balance'], 2) . ' ' . $balance['currency.code'] . '.</p>';
	print '<p>Are you sure you want to buy the <b>' . $content['name'] . '</b> ringtone?</p>' . $br;
	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentname=' . urlencode($content['name']) . '&contentid=' . $contentId . '&buy=1&confirm=1&gcid='.$group_id.'">Yes, buy the Ringtone</a></p>';
	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&categoryid=' . $_GET['categoryid'] . '&contentid=' . $contentId . '&contentpage=' . $_GET['contentpage'] . '&gcid='.$group_id.'">No, go back</a></p>';
}

function buyRingtone($username, $countryID, $clientType, $br) {
	// Load the content
	$contentId = $_GET['contentid'];
	settype($contentId, 'int');

	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	global $server_root;
	try {
		$content = soap_call_ejb('getMobileContentItem', array($username, $contentId, true));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	if ($content['id'] == '') {
		print '<p>Sorry, the following error occurred: Item not found</p>';
		return;
	}

	try {
		$ret = soap_call_ejb('buyMobileContentItem', array($username, $contentId, getRemoteIPAddress(), getSessionID(), getMobileDevice(), getUserAgent()));
	} catch(Exception $e) {
		$error = $e->getMessage();
	}

	if ($ret != '' && !(substr($ret,0,4) == 'http') && empty($error))
		$error = $ret;

	if (!empty($error)) {
		print '<p>Sorry, your purchase could not be completed due to the following reason:' . $br;
		print $e->getMessage() . '</p>';
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&contentname=' . urlencode($_GET['contentname']) . '&categoryid=' . $_GET['categoryid'] . '&contentid=' . $contentId . '&gcid='.$group_id.'">Back</a></p>';
		return;
	}
	else if (substr($ret,0,4) == 'http') {
		showRingtoneDownloadLink($ret);
	}
	else {
		waitForRingtoneDownloadURL($username, $countryId, $clientTYpe, $br);
	}
}

function waitForRingtoneDownloadURL($username, $countryID, $clientType, $br) {
	$contentId = $_GET['contentid'];
	settype($contentId, 'int');
	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	global $server_root;

	// We now wait for iLoop to have asynchronously sent us the download URL. We wait for up to 10s
	$timeWaiting = 0;

	do {
		sleep(2); // Sleep for 2 seconds
		$timeWaiting = $timeWaiting + 2;

		// See if the URL has been received
		try {
			$downloadURL = soap_call_ejb('getMobileContentDownloadURL', array($username, $contentId));
		} catch(Exception $e) {
			// Don't do anything here, as the code will retry, and the user can choose to retry too
			//print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
			//return;
		}

	} while ($downloadURL == '' && $timeWaiting < 10);

	if ($downloadURL != '') {
		showRingtoneDownloadLink($downloadURL);
	}
	else {
		print '<p>Your newly purchased ringtone is being prepared and is not yet ready for download.</p>';
		print '<p>Please <a href="'.$server_root.'/midlet/member/store.php?loc=ring&contentname=' . urlencode($_GET['contentname']) . '&buy=1&confirm=1&categoryid=' . $_GET['categoryid'] . '&contentid=' . $contentId . '&tryagain=1&gcid='.$group_id.'">click here to see if your download is ready</a>.</p>';
		//print '<p>When your download is ready we will automatically send you an SMS containing the download link.</p>' . $br;
		//print '<p>If you are unable to download your new ringtone please contact us.</p>';
	}
}

function showRingtoneDownloadLink($downloadURL) {
	print '<p>Congratulations! You have just purchased the ringtone <b>' . $_GET['contentname'] . '</b>.</p>';

	global $server_root;
	$linkShown = false;
	$headers = apache_request_headers();
	$midletVersion = $headers['ver'];

	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	// If this is a midlet version >= 3.05
	if (is_numeric($midletVersion)) {
		if ($midletVersion >= 3.05) {
			print '<p>Click the link below to download the ringtone to your phone:</p>';
			if( $midletVersion >= 4.00 )
			{
				printf('<p><a href="mig33:invokeNativeBrowser(%s)">Download Ringtone</a></p>', $downloadURL );
			}
			else
			{
				print '<p><tag type="4" href="' . $downloadURL . '">Download Ringtone</tag></p>';
			}
			print '<p>(Your phone may close mig33 when you download the ringtone. It may also prompt for network connection, please select Yes.)</p>' . $br;
			//print '<p>We have also sent you an SMS containing the download link.</p>';
			//print '<p>If the download link does not work for you, you may <a href="">request the download link in an SMS</a>.</p>';
			//print '<p>If you are unable to download your new ringtone please contact us.</p>' . $br;
			print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&gcid='.$group_id.'">Browse more ringtones</a></p>';


			if( $group_id == 0 )
				print '<p><a href="'.$server_root.'/midlet/member/store.php">Back to store front</a></p>';
			else
				print '<p><a href="'.$server_root.'/sites/index.php?c=group&v=midlet&a=store&cid='.$group_id.'">Back to group store front</a></p>';
			$linkShown = true;
		}
	}

	if (!$linkShown) {
		//print '<p><a href="">Click here to receive the download link in an SMS</a>.</p>';
		//print '<p>Write down the link below and enter it into your mobile phone\'s browser:</p>' . $br;
		//print '<p>' . htmlspecialchars($downloadURL) . '</p>' . $br;
		print '<p>Your download is ready, however your version of mig33 does not support downloads. Please upgrade to the latest version of mig33, and then go to the mig33 store to view your purchase and download the ringtone.</p>' . $br;
		//print '<p>We have sent you an SMS containing the download link. You will need to open the link in your mobile phone\'s browser.</p>' . $br;
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=ring&gcid='.$group_id.'">Browse more ringtones</a></p>';

		if( $group_id == 0 )
			print '<p><a href="'.$server_root.'/midlet/member/store.php">Back to store front</a></p>';
		else
			print '<p><a href="'.$server_root.'/sites/index.php?c=group&v=midlet&a=store&cid='.$group_id.'">Back to group store front</a></p>';
	}
}
?>