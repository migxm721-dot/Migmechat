<?php
function showEmoticonStore($username, $countryID, $clientType, $br) {
	if (!empty($_GET['help']))
		showEmoticonHelp($username, $countryID, $clientType, $br);
	elseif (empty($_GET['packid']))
		showEmoticonPacks($username, $countryID, $clientType, $br);
	elseif (empty($_GET['buy']))
		showEmoticonPack($username, $countryID, $clientType, $br);
	elseif (empty($_GET['confirm']))
		showBuyConfirmation($username, $countryID, $clientType, $br);
	else
		buyEmoticonPack($username, $countryID, $clientType, $br);
}

function is_super_emoticon()
{
	$is_super = false;
	if(!empty($_GET['se']))
	{
		$super = $_GET['se'];
		$sp = settype($super, "integer");
		$is_super = $sp==1;
	}
	return $is_super;
}

function get_super_emoticon_attribute()
{
	if( is_super_emoticon() )
		return "se=1";
	else
		return "se=0";
}

function get_emoticon_title()
{
	if( is_super_emoticon() )
		return "super emoticons";
	else
		return "emoticons";
}

function showEmoticonHelp($username, $countryID, $clientType, $br) {

	global $server_root;
	print '<p><b>' . ucwords(get_emoticon_title()) . '</b></p>';

	if( is_super_emoticon() )
	{
		print '<p>Super Emoticons are special emoticons that you can purchase and install for use in private and group chat. Super Emoticons only work with migme version 4.1 or higher.</p>'.$br;
		print '<p>Once you have purchased and installed Super Emoticons you will be able to use them in chat. Other users will be able to see and experience Super Emoticons even if they have not purchased the pack (if they are using migme version 4.1 or higher).</p>'.$br;
		print '<p>Super emoticons may increase the amount of downloaded data.</p>'.$br;
		print '<p>Super emoticons are purchased on a subscription basis.</p>'.$br;
	}
	else
	{
		print '<p>'. ucfirst(get_emoticon_title()) . ' are additional emoticons you can purchase and install for use in private and group chat. Note that some versions of migme (currently the \'lite\' and web versions) do not display '. get_emoticon_title() . ', including ' . get_emoticon_title() . ' you have purchased. Users of these versions will see text codes instead.</p>';
		print '<p>Once you have purchased and installed ' . get_emoticon_title() . ' you will be able to use them in chat. Other users will be able to see  ' . get_emoticon_title() . ' even if they have not purchased the pack.</p>';
		print '<p>Purchasing additional ' . get_emoticon_title() . ' will increase the amount of downloaded images when using certain parts of migme, such as the Insert Emoticons menu item.</p>';
		print '<p>Once ' . get_emoticon_title() . ' have been purchased they cannot be refunded.</p>';
	}
	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}
	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&gcid='.$group_id.'&' . get_super_emoticon_attribute() . '">'. ucwords(get_emoticon_title()) . '</a></p>';
}

function showPack($emoPack, $br, $owned) {
	if( !$owned && ($emoPack['purchased'] == 1) ) return;
	global $server_root;
	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}
	print '<p><img src="http://' . $_SERVER['HTTP_HOST'] . '/img/emoticon_packs/' . $emoPack['id'] . '/thumb.gif" width="50" height="40" vspace="2" hspace="3" style="float:left">';
	print '<a href="'.$server_root.'/midlet/member/store.php?loc=emot&owned=' . $_GET['owned'] . '&packid=' . $emoPack['id'] . '&gcid='.$group_id.'&' . get_super_emoticon_attribute() . '">' . $emoPack['name'] . '</a>';
	if( $group_id != 0 && $emoPack["groupviponly"] == true )
	{
		print ' (VIP Only)';
	}
	print $br;
	print $emoPack['numemoticons'] . ' emoticons' . $br;
	if (!$owned) {
		if ($emoPack['purchased'] == 1) {
			print 'Purchased!' . $br;
		}
		else {
			if( is_super_emoticon() == false )
			{
				if ($emoPack['price'] == 0)
					print 'Price: Free!' . $br;
				else
					print 'Price: ' . number_format($emoPack['price'], 2) . ' ' . $emoPack['currency'] . $br;
			}
		}
	}
	print '</p>';
}

function showEmoticonPacks($username, $countryID, $clientType, $br) {
	$owned = false;
	if ($_GET['owned'] == 1)
		$owned = true;

	global $server_root;

	print '<html><head>';
	if( !$owned && canPrefetch())
	{
		print '<prefetch url="'.$server_root.'/midlet/member/store.php?loc=emot&owned=1">';
	}
	print '</head>';
	print '<body>';


	if ($owned) {
		print '<p><b>Emoticon Packs You Own</b></p>' . $br;
	}
	else {
		$group_id = 0;
		if( isset($_GET["gcid"]) )
		{
			$group_id = $_GET["gcid"];
			settype($group_id, "integer");
		}
		print '<p><b>'. ucfirst(get_emoticon_title()) . '</b></p>';
		print '<p>Buy new '. get_emoticon_title() . ' you can use in chat! <a href="'.$server_root.'/midlet/member/store.php?loc=emot&help=1&gcid='.$group_id.'&'.get_super_emoticon_attribute().'">Find out more</a></p>' . $br;
	}

	// Load all emoticion packs
	try {
		$group_id = 0;
		if( isset($_GET["gcid"]) )
		{
			$group_id = $_GET["gcid"];
			settype($group_id, "integer");
		}
		$type = is_super_emoticon()?3:2;

		$emoPacks = soap_call_ejb('getEmoticonPacks', array($username, $owned, $group_id, $type));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		print '</body></html>';
		return;
	}

	// Show v4 pack to v4 clients first
	$headers = apache_request_headers();
	$midletVersion = $headers['ver'];

	if (is_numeric($midletVersion) && $midletVersion >= 4.00)
	{

		foreach( $emoPacks as $emoPack )
		{
			$id = $emoPack['id'];
			settype($id, "integer");
			if( $id == 2 )
			{
				showPack($emoPack, $br, $owned);
			}
		}
	}

	if( is_array($emoPacks))
	{
		for ($i = 0; $i < sizeof($emoPacks); $i++) {
			// Don't show the Mig Games 2008 pack (this will be done in the back end, rather than here, when the back end is ready)
			// Disable the EID emoticon pack
			// Disable the Deepavali emoticon pack
			// Don't show the v4 pack (we would have shown it above)
			// Don't show the "bruneisneakpeek" pack
			if ($emoPacks[$i]['id'] != 6
					&& $emoPacks[$i]['id'] != 8
					&& $emoPacks[$i]['id'] != 11
					&& $emoPacks[$i]['id'] != 13
					&& $emoPacks[$i]['id'] != 15
					&& $emoPacks[$i]['id'] != 16
					&& $emoPacks[$i]['id'] != 17
					&& $emoPacks[$i]['id'] != 22
					&& $emoPacks[$i]['id'] != 15
					&& $emoPacks[$i]['id'] != 2)
			{
				if( $emoPacks[$i]['id'] == 15 && $countryID != 34 ) continue;
				showPack($emoPacks[$i], $br, $owned);
			}
		}
	}
	else
	{
		print '<p>You haven\'t purchased any '. get_emoticon_title() . '. Click <a href="'.$server_root.'/midlet/member/store.php?loc=emot&'. get_super_emoticon_attribute() . '">here</a> to buy some!</p>';
	}

	if (!$owned) {
		// Get the user's account balance
		try {
			$balance = soap_call_ejb('getAccountBalance', array($username));
		} catch(Exception $e) {
			print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
			return;
		}
		print '<p>Available balance: ' . number_format($balance['balance'], 2) . ' ' . $balance['currency.code'] . '</p>' . $br;
		$group_id = 0;
		if( isset($_GET["gcid"]) )
		{
			$group_id = $_GET["gcid"];
			settype($group_id, "integer");
		}
		if( $group_id == 0 )
			print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&owned=1&'.get_super_emoticon_attribute().'">Emoticon packs you already own</a></p>';
		if( is_super_emoticon())
		{
			print '<p><a href="'.$server_root.'/sites/index.php?c=subscription&v=midlet&a=home&'. get_super_emoticon_attribute() . '&f=super_emoticon">Subscriptions</a></p>'.$br;
		}
		else
		{
			print $br;
		}
	}
	else
	{
		print $br;
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&'. get_super_emoticon_attribute() . '">Purchase '.get_emoticon_title().'</a></p>'.$br;
		if( is_super_emoticon() )
		{
			print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot">Purchase emoticons</a></p>';
			print '<p><a href="'.$server_root.'/sites/index.php?c=subscription&v=midlet&a=home&'. get_super_emoticon_attribute() . '&f=super_emoticon">Subscriptions</a></p>'.$br;
		}
	}

	print '</body></html>';

	/*
	if ($owned) {
		print '<p><a href="store.php?loc=emot">Back</a></p>';
	}
	else {
		print '<p><a href="store.php?loc=emot&owned=1">View emoticon packs you own</a></p>';
		print '<p><a href="store.php">Back</a></p>';
	}
	*/
}

function showEmoticonPack($username, $countryID, $clientType, $br) {
	// Load the emoticion pack
	$packId = $_GET['packid'];
	settype($packId, 'int');

	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	if ($packId == 22) {
		print '<p>Sorry, the pack is not available</p>';
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&'.get_super_emoticon_attribute().'">Buy '.get_emoticon_title().'</a></p>';
		return;
	}

	global $server_root;

	try {
		$emoPack = soap_call_ejb('getEmoticonPack', array($username, $packId));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	//print '<p><b>Premium Emoticons</b></p>' . $br;
	print '<p><b>' . $emoPack['name'] . '</b></p>';

	print '<p><img src="http://' . $_SERVER['HTTP_HOST'] . '/img/emoticon_packs/' . $emoPack['id'] . '/preview.gif" width="100" height="80" vspace="2" hspace="3"></p>';
	if (strlen($emoPack['description']) > 0)
		print '<p>' . $emoPack['description'] . '</p>' . $br;
	if( is_super_emoticon())
	{
		$emoticons = soap_call_ejb('getEmoticonsInPack', array($packId));
		$normal = 0;
		$animated = 0;
		$vibrating = 0;
		$sound = 0;

		foreach( $emoticons as $emoticon )
		{
			switch($emoticon["type"])
			{
				case 1:
					$normal+=1;
					break;
				case 2:
					$vibrating += 1;
					break;
				case 3:
					$animated += 1;
					break;
				case 4:
					$sound += 1;
					break;
			}
		}
		$text = "";
		if( $animated > 0 )
		{
			$text = $text . " " . $animated . " animated";
		}

		if( $vibrating > 0 )
		{
			if(strlen($text)>0) $text = $text . ", ";
			$text = $text . $vibrating . " vibrating";
		}

		if( $sound > 0 )
		{
			if(strlen($text)>0) $text = $text . ", ";
			$text = $text . $sound . " sound";
		}
		if( strlen($text) > 0 )
			print '<p>'. $text . '</p>'.$br;
	}
	else
	{
		print '<p>' . $emoPack['numemoticons'] . ' emoticons</p>';
	}
	if ($emoPack['purchased'] == 1) {
		print '<p>You own this pack!</p>';
	}
	else {
		if ($emoPack['price'] == 0)
			print '<p>Price: Free!</p>';
		else
		{
			if( is_super_emoticon() )
			{
				print '<p>Price: ' . number_format($emoPack['price'], 2) . ' ' . $emoPack['currency'] . ' per '. $emoPack['durationdays'] . ' days [<a href="'.$server_root.'/midlet/member/store.php?loc=emot&help=1&'.get_super_emoticon_attribute().'">?</a>]</p>';
			}
			else
				print '<p>Price: ' . number_format($emoPack['price'], 2) . ' ' . $emoPack['currency'] . '</p>';
		}

		// Get the user's account balance
		try {
			$balance = soap_call_ejb('getAccountBalance', array($username));
		} catch(Exception $e) {
			print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
			return;
		}
		print '<p>Available balance: ' . number_format($balance['balance'], 2) . ' ' . $balance['currency.code'] . '</p>';

		if ($emoPack['currency'] == $balance['currency.code'] && $emoPack['price'] > $balance['balance']) {
			print '<p>You do not have enough credit to purchase this item.<br><a href="'.$server_root.'/midlet/member/recharge_index.php">Add credit to your account</a></p>';
		}
		else {
			$buy = is_super_emoticon()?"Subscribe":"Buy";
			print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&packid=' . $packId . '&buy=1&gcid='.$group_id.'&'.get_super_emoticon_attribute().'">'.$buy.'</a></p>';
		}
	}

	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&owned=' . $_GET['owned'] . '&gcid='.$group_id.'&'.get_super_emoticon_attribute().'">Back</a></p>';
	print $br;
}

function showBuyConfirmation($username, $countryID, $clientType, $br) {
	// Get the user's account balance
	global $server_root;

	$group_id = 0;
	if( isset($_GET["gcid"]) )
	{
		$group_id = $_GET["gcid"];
		settype($group_id, "integer");
	}

	try {
		$balance = soap_call_ejb('getAccountBalance', array($username));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	// Load the emoticion pack
	$packId = $_GET['packid'];
	settype($packId, 'int');

	try {
		$emoPack = soap_call_ejb('getEmoticonPack', array($username, $packId));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	// If the user is using a midlet with version < 3.05, display a message asking them to upgrade.
	// If we can't determine the midlet version (say the header was stripped by a WAP gateway) then
	// we warn the user that they need at least version 3.05
	$versionChecked = false;
	$headers = apache_request_headers();
	$midletVersion = $headers['ver'];
	if (is_numeric($midletVersion)) {
		if ($midletVersion < 3.05) {
			print '<p>Sorry, '.get_emoticon_title().' are not available in this version of migme.</p>';
			print '<p>Please download the latest version of migme by going to http://wap'.$session_cookie_domain.' in your phone\'s browser.</p>';
			print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&packid=' . $packId . '&'.get_super_emoticon_attribute().'">Back</a></p>';
			return;
		}
		$versionChecked = true;
	}

	print '<p><b>Confirm Purchase</b></p>' . $br;
	if( is_super_emoticon() )
	{
		print '<p>' . number_format($emoPack['price'], 2) . ' ' . $emoPack['currency'] . ' will be deducted from your account every '. $emoPack['durationdays'] .' days. [<a href="'.$server_root.'/midlet/member/store.php?loc=emot&help=1&'.get_super_emoticon_attribute().'">?</a>]</p>';
	}
	else
		print '<p>' . number_format($emoPack['price'], 2) . ' ' . $emoPack['currency'] . ' will be deducted from your account.</p>';
	print '<p>Your account balance is ' . number_format($balance['balance'], 2) . ' ' . $balance['currency.code'] . '</p>' . $br;
	$buy = is_super_emoticon()?"subscribe to":"buy";
	print '<p>Are you sure you want to '.$buy.' the <b>' . $emoPack['name'] . '</b> emoticon pack?</p>' . $br;
	$text = "Yes, buy the pack";
	if( is_super_emoticon() )
	{
		$text = "Yes, subscribe to the pack";
	}
	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&packid=' . $packId . '&buy=1&confirm=1&gcid='.$group_id.'&'.get_super_emoticon_attribute() . '">'.$text.'</a></p>';
	print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&packid=' . $packId . '&gcid='.$group_id.'&'.get_super_emoticon_attribute() . '">No, go back</a></p>'.$br;

	if (!$versionChecked) {
		print '<p>Please note that you must be running at least version 3.05 of migme Beta in order to use '.get_emoticon_title().'.</p>';
		print '<p>You can check which version of migme you have by going to \'About migme\' in the \'Settings\' menu.</p>';
	}
}

function buyEmoticonPack($username, $countryID, $clientType, $br) {
	// Load the emoticion pack
	$packId = $_GET['packid'];
	settype($packId, 'int');

	global $server_root;

	try {
		$emoPack = soap_call_ejb('getEmoticonPack', array($username, $packId));
	} catch(Exception $e) {
		print '<p>Sorry, the following error occurred:' . $br . $e->getMessage() . '</p>';
		return;
	}

	try {
		$ret = soap_call_ejb('buyEmoticonPack', array($username, $packId, getRemoteIPAddress(), getSessionID(), getMobileDevice(), getUserAgent()));
	} catch(Exception $e) {
		$error = $e->getMessage();
	}

	if ($ret != 'TRUE' && empty($error))
		$error = $ret;

	if (!empty($error)) {
		$group_id = 0;
		if( isset($_GET["gcid"]) )
		{
			$group_id = $_GET["gcid"];
			settype($group_id, "integer");
		}
		print '<p>Sorry, your purchase could not be completed due to the following reason:' . $br;
		print $e->getMessage() . '</p>';
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&packid=' . $packId . '&gcid='.$group_id.'&'.get_super_emoticon_attribute().'">Back</a></p>';
		return;
	}
	else {
		$group_id = 0;
		if( isset($_GET["gcid"]) )
		{
			$group_id = $_GET["gcid"];
			settype($group_id, "integer");
		}
		$purchased = "purchased";
		if( is_super_emoticon() )
			$purchased = "subscribed to";
		print '<p>Congratulations! You have just '.$purchased.' the <b>' . $emoPack['name'] . '</b> emoticon pack.</p>' . $br;
		print '<p>To use your new '.get_emoticon_title().', choose Insert Emoticon from the menu when you are in a chatroom or chatting to another migme user.</p>' . $br;
		print '<p>Please note that some versions of migme do not support the display of '.get_emoticon_title().'. For more information please see <a href="'.$server_root.'/midlet/member/store.php?loc=emot&help=1&gcid='.$group_id.'&'.get_super_emoticon_attribute().'">Help</a>' . $br;
		print '<p><a href="'.$server_root.'/midlet/member/store.php?loc=emot&gcid='.$group_id.'&'.get_super_emoticon_attribute().'">Browse more '.get_emoticon_title().'</a></p>';
		//print '<p><a href="store.php">migme Store</a></p>';
	}
}
?>