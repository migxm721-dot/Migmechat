<?php
require_once($_SERVER['DOCUMENT_ROOT']."/common/common-config.php");

// Function to display the list of states/provinces where migme credit resellers
// are located in the given country.
// $userCountryID: The ID of the country in which the resellers we'll display are in
// $clientType: Where this is being displayed. May be "AJAX", "WAP" or "Midlet"
// $loggedIn: Whether this page is being displayed to public users or members logged in

function showMerchantLocator($userCountryID, $clientType, $loggedIn) {
	global $server_root;

	switch(strtolower($clientType))
	{
		case "ajax":
			$url_pref = $server_root . "/members";
			break;
		case "wap":
			$url_pref = $server_root . "/wap2/member";
			break;
		default:
			$url_pref = $server_root . "/midlet/member";
	}

	$br = '<br/>';
	if ($clientType == 'Midlet')
		$br = '<br>';

	if (isset($_GET['countryid']))
		$countryID = $_GET['countryid'];
	else
		$countryID = $userCountryID;

	if (isset($_GET['pickcountry']))
		showCountrySelection($clientType, $loggedIn, $br);
	else if (isset($_GET['merchantid']))
		showMerchant($clientType, $loggedIn, $_GET['merchantid'], $br);
	else if ($countryID == 0)
		showCountrySelection($clientType, $loggedIn, $br);
	else
		showLocationsAndMerchants($countryID, $clientType, $loggedIn, $br);
}

function showCountrySelection($clientType, $loggedIn, $br) {
	global $server_root;

	switch(strtolower($clientType))
	{
		case "ajax":
			$url_pref = $server_root . "/members";
			break;
		case "wap":
			$url_pref = $server_root . "/wap2/member";
			break;
		default:
			$url_pref = $server_root . "/midlet/member";
	}

	try {
		$countries = soap_call_ejb('getCountriesWithMerchants', array());
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return;
	}

	if (empty($countries[0]['id'])) {
		print 'Sorry, no countries with merchants were found';
		return;
	}

	print '<p>Select a country (only countries with migme merchants are displayed):</p>';

	foreach ($countries as $country) {
		print '<a href="'.$url_pref.'/find_merchant.php?countryid=' . $country['id'] . '">' . $country['name'] . '</a>' . $br;
	}
}

function showLocationsAndMerchants($countryID, $clientType, $loggedIn, $br) {
	global $server_root;

	switch(strtolower($clientType))
	{
		case "ajax":
			$url_pref = $server_root . "/members";
			break;
		case "wap":
			$url_pref = $server_root . "/wap2/member";
			break;
		default:
			$url_pref = $server_root . "/midlet/member";
	}
	if (isset($_GET['level']))
		$level = $_GET['level'];
	else
		$level = 0;

	if (isset($_GET['locationid']))
		$locationID = $_GET['locationid'];
	else
		$locationID = 0;

	settype($countryID, "int");
	settype($locationID, "int");

	try {
		if ($locationID == 0)
			$locations = soap_call_ejb('getLocationsWithMerchantsInCountry', array($countryID));
		else
			$locations = soap_call_ejb('getLocationsWithMerchantsInParentLocation', array($locationID));
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return;
	}

	$locationsDisplayed = true;
	if (empty($locations[0]['id']))
		$locationsDisplayed = false;

	if ($locationID == 0) {
		if ($countryID == 204)
			print '<p>Other ';
		else
			print '<p>Locate ';
		print 'stores / merchants in <b>' . get_country($countryID) . '</b> (<a href="'.$url_pref.'/find_merchant.php?pickcountry=1">change country</a>):</p>';
	}

	if ($clientType == "Midlet")
		print $br;

	print '<p>';

	// Get the path to the current location
	if ($locationID != 0) {
		try {
			$path = soap_call_ejb('getLocationPath', array($locationID));
		} catch(Exception $e) {
			print 'Sorry, an error occurred. ' . $e->getMessage();
			return;
		}

		if ($locationID != 0)
			print get_country($countryID) . ' &gt; ';

		for ($i = 0; $i < sizeof($path); $i++)
			print $path[$i]['name'] . ' &gt; ';

		print $br;
	}

	if (!empty($locations[0]['id'])) {
		for ($i = 0; $i < sizeof($locations); $i++)
			print '<a href="'.$url_pref.'/find_merchant.php?countryid=' . $countryID . getAnd($clientType) . 'locationid=' . $locations[$i]['id'] . '">' . $locations[$i]['name'] . '</a> (' . $locations[$i]['num'] . ')' . $br;
	}
	print '</p>';

	$merchantsShown = showMerchantsInLocation($countryID, $locationID, $path[sizeof($path) - 1]['name'], $clientType, $loggedIn, $br);

	if ($merchantsShown == false && $locationsDisplayed == false) {
		if ($locationID == 0) {
			print '<p>Sorry, there are no merchants in ' . get_country($countryID) . '.</p>';
			print '<p><a href="'.$url_pref.'/find_merchant.php?pickcountry=1">Try another country</a></p>';
		}
		else {
			print '<p>Sorry, there are no merchants in ' . $path[sizeof($path) - 1]['name'] . '.</p>';
			showBackLink($clientType, $countryID, $locationID, $path);
		}
	}
	else {
		showBackLink($clientType, $countryID, $locationID, $path);
	}
}

function showBackLink($clientType, $countryID, $locationID, $path) {

	if ($locationID == 0) return;
	if ($clientType == "Web") return;

	global $server_root;

	switch(strtolower($clientType))
	{
		case "ajax":
			$url_pref = $server_root . "/members";
			break;
		case "wap":
			$url_pref = $server_root . "/wap2/member";
			break;
		default:
			$url_pref = $server_root . "/midlet/member";
	}

	// Don't show the back link for v4+ clients (as they already have a Back soft key)
	$headers = apache_request_headers();
	if (is_numeric($headers['ver']) && $headers['ver'] >= 4.00)
		return;

	$backToLocationID = 0;
	if (sizeof($path) > 1)
		$backToLocationID = $path[sizeof($path) - 2]['id'];

	print '<p><a href="'.$url_pref.'/find_merchant.php?countryid=' . $countryID . getAnd($clientType) . 'locationid=' . $backToLocationID . '">Back</a></p>';
}

function showMerchantsInLocation($countryID, $locationID, $locationName, $clientType, $loggedIn, $br) {
	if ($locationID == 0) return;

	global $server_root;

	switch(strtolower($clientType))
	{
		case "ajax":
			$url_pref = $server_root . "/members";
			break;
		case "wap":
			$url_pref = $server_root . "/wap2/member";
			break;
		default:
			$url_pref = $server_root . "/midlet/member";
	}

	try {
		$merchants = soap_call_ejb('getMerchantsInLocation', array($locationID));
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return false;
	}

	if (empty($merchants[0]['id']))
		return false;

	print '<p>Merchants in ' . $locationName . ':' . $br;

	foreach ($merchants as $merchant)
		print '<a href="'.$url_pref.'/find_merchant.php?countryid=' . $countryID . getAnd($clientType) . 'locationid=' . $locationID . getAnd($clientType) . 'merchantid=' . $merchant['id'] . '">' . htmlspecialchars($merchant['name']) . '</a>' . $br;

	print '</p>';

	return true;
}

function showMerchant($clientType, $loggedIn, $merchantID, $br) {
	global $server_root;

	switch(strtolower($clientType))
	{
		case "ajax":
			$url_pref = $server_root . "/members";
			break;
		case "wap":
			$url_pref = $server_root . "/wap2/member";
			break;
		default:
			$url_pref = $server_root . "/midlet/member";
	}
	try {
		settype($merchantID, "int");
		$merchant = soap_call_ejb('getMerchantLocation', array($merchantID));
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return false;
	}

	if (empty($merchant['id'])) {
		return false;
	}

	print '<p><b>' . htmlspecialchars($merchant['name']) . '</b></p>';
	if ($clientType == "Midlet") print $br;

	if (!empty($merchant['username'])) {
		if ($clientType == "Midlet")
			print '<p>migme username: <a href="'.$url_pref.'/view_profile.php?username=' . urlencode($merchant['username']) . '">' . htmlspecialchars($merchant['username']) . '</a></p>';
		else if ($clientType == "WAP") {
			print '<p>migme username: ' . htmlspecialchars($merchant['username']) . '</p>';
		}
		else if ($clientType == "Web") {
			print '<p>migme username: <a href="javascript:window.parent.showUserProfile(\'' . $merchant['username'] . '\')">' . htmlspecialchars($merchant['username']) . '</a></p>';
		}
	}
	if ($clientType == "Midlet") print $br;

	if (!empty($merchant['address']))
		print '<p>Address: ' . $br . htmlspecialchars($merchant['address']) . '</p>';
	if ($clientType == "Midlet") print $br;

	if (!empty($merchant['phonenumber']))
		print '<p>Phone: ' . htmlspecialchars($merchant['phonenumber']) . '</p>';
	if ($clientType == "Midlet") print $br;

	if (!empty($merchant['emailaddress']))
		print '<p>Email: ' . htmlspecialchars($merchant['emailaddress']) . '</p>';
	if ($clientType == "Midlet") print $br;

	if (!empty($merchant['notes']))
		print '<p>Notes: ' . $br . htmlspecialchars($merchant['notes']) . '</p>';
	if ($clientType == "Midlet") print $br;

	if ($clientType != "Web")
		print '<p><a href="'.$url_pref.'/find_merchant.php?countryid=' . $_GET['countryid'] . getAnd($clientType) . 'locationid=' . $_GET['locationid'] . '">Back</a></p>';
}

function getAnd($clientType) {
	if ($clientType == "WAP")
		return "&amp;";
	else
		return "&";
}
?>