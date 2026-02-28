<?php
function showPurchases($username, $countryID, $clientType, $br) {
	if (empty($_GET['id']))
		showAllPurchases($username, $countryID, $clientType, $br);
	else
		showSpecificPurchase($username, $countryID, $clientType, $br);
}

function showAllPurchases($username, $countryID, $clientType, $br) {
	if (empty($_GET['page']))
		$page = 1;
	else
		$page = $_GET['page'];
	settype($page, 'int');

	global $server_root;

	try {
		$purchases = soap_call_ejb('getPurchasedContent', array($username, $page-1, 10));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	$numItems = $purchases[0]['numEntries'];
	if ($numItems >= 1) {
		$numPages = $purchases[0]['numPages'];

		print '<p><b>Items you have purchased:</b></p>';
		print '<p>';
		for ($i = 1; $i < sizeof($purchases); $i++) {
			if ($purchases[$i]['type'] == '0') {  // If it's an emoticon pack
				print $purchases[$i]['dateCreated'] . ': ' . $purchases[$i]['name'] . ' emoticon pack' . $br;
				//print $purchases[$i]['name'] . ' emoticon pack (' . $purchases[$i]['dateCreated'] . ')' . $br;
			}
			else if ($purchases[$i]['type'] == '1') {  // If it's a ringtone
				print $purchases[$i]['dateCreated'] . ': <a href="'.$server_root.'/midlet/member/store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $page . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a> ringtone' . $br;
				//print '<a href="store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $purchases[$i]['page'] . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a> ringtone (' . $purchases[$i]['dateCreated'] . ')' . $br;
			}
			else if ($purchases[$i]['type'] == '2') {  // If it's a wallpaper
				print $purchases[$i]['dateCreated'] . ': <a href="'.$server_root.'/midlet/member/store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $page . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a> wallpaper' . $br;
				//print '<a href="store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $purchases[$i]['page'] . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a> wallpaper (' . $purchases[$i]['dateCreated'] . ')' . $br;
			}
			else if ($purchases[$i]['type'] == '3') {  // If it's a video
				print $purchases[$i]['dateCreated'] . ': <a href="'.$server_root.'/midlet/member/store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $page . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a> video' . $br;
			}
			else if ($purchases[$i]['type'] == '4') {  // If it's an application
				print $purchases[$i]['dateCreated'] . ': <a href="'.$server_root.'/midlet/member/store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $page . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a> game' . $br;
			}
			else {
				print $purchases[$i]['dateCreated'] . ': <a href="'.$server_root.'/midlet/member/store.php?loc=purchased&type=' . $purchases[$i]['type'] . '&page=' . $page . '&id=' . $purchases[$i]['id'] . '">' . $purchases[$i]['name'] . '</a>' . $br;
			}
		}
		print '</p>';

		if ($numPages > 1) {
			print $br . '<p>';
			if ($page > 1) {
				print '<a href="'.$server_root.'/midlet/member/store.php?loc=purchased&page=' . ($page - 1) . '">&lt; Previous Page</a>';
			}
			print ' Page ' . $page . ' of ' . $numPages . ' ';
			if ($page < $numPages) {
				print '<a href="'.$server_root.'/midlet/member/store.php?loc=purchased&page=' . ($page + 1) . '">Next Page &gt;</a>';
			}
			print '</p>' . $br;
		}
	}
	else {
		print '<p>You have not purchased anything yet!</p>';
	}

	print '<p><a href="'.$server_root.'/midlet/member/store.php">Back to store front</a></p>';
}

function showSpecificPurchase($username, $countryID, $clientType, $br) {
	// Load the content
	$contentId = $_GET['id'];
	settype($contentId, 'int');


	global $server_root;
	try {
		$content = soap_call_ejb('getMobileContentItem', array($username, $contentId, false));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	if ($content['id'] == '') {
		print '<p>Sorry, the following error occurred: Item not found</p>';
		return;
	}

	if ($content['type'] == 'RINGTONE')
		print '<p><b>' . $content['name'] . '</b>' . $br . 'by ' . $content['artist'] . '</p>' . $br;
	else
		print '<p><b>' . $content['name'] . '</b></p>' . $br;

	if ($content['numdownloads'] != '') {
		print '<p>You have downloaded this item ' . $content['numdownloads'];
		if ($content['numdownloads'] == 1)
			print ' time.';
		else
			print ' times.';
		print '</p>' . $br;
	}

	if ($content['downloadurl'] != '') {
		$itemType = 'item';
		if ($content['type'] == 'RINGTONE')
			$itemType = 'ringtone';
		else if ($content['type'] == 'WALLPAPER')
			$itemType = 'wallpaper';
		else if ($content['type'] == 'GAME')
			$itemType = 'game';

		if ($content['hourssincepurchase'] <= 24) {
			print '<p>Use the link below to download your ' . $itemType . ':</p>';'
			//. Your $itemType can only be downloadedwill be available for 24 hours after your purchase';
			print '<p><tag type="4" href="' . $content['downloadurl'] . '">Download ' . $itemType . '</a></p>';
			print '<p>(Your phone may close mig33 when you download the ' . $itemType . '. It may also prompt for network connection, please select Yes.)</p>';
		}
		else {
			print '<p>You purchased this ' . $itemType . ' more than 24 hours ago, so it can no longer be downloaded.</p>';
		}
	}
	else {
		print '<p>Your download is being prepared. A link to download will be available soon.</p>';
	}

	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=purchased&page=' . $_GET['page'] . '">Back</a></p>';
}
?>