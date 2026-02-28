<?php
#mig33 common includes file
#PURPOSE : 	this file will be used to contain all common code used by the mig33
#web project.

//Include Server Configuration Variables
require_once("../../common/common-config.php");
require_once("../../common/language.php");

function round_twodec($value){
	if ($value != round($value))
			return number_format($value, 2);
	else
			return round($value);
}


#FUNCTION formatDate
#PURPOSE:	Formats a date in the format, "dd/mm/yyyy" or "dd/mm/yy" to the format campatible with the
#			mySQL date time format, "yyyy-mm-dd hh.mm.ss"
#INPUTS:	str $date
#            str $time (hh.mm.ss)
#OUTPUTS:	date $date
function formatDate($date,$time){
	list ($S_day, $S_month, $S_year) = split ('[/.-]', $date);
	$date = $S_year."-".$S_month."-".$S_day." ".$time;
	return $date;
}

/**
* Adds the detail of a merchant into Salesforce.com as a lead
*/
function addMerchantToSalesforce($username, $countryID, $firstName, $lastName, $company, $email, $additionalInfo) {

	require_once("../../common/salesforce/SforcePartnerClient.php");

	global $salesforce_username;
	global $salesforce_password;
	global $apache_dir;

	$connection = new SforcePartnerClient();
	$connection->createConnection($_SERVER['DOCUMENT_ROOT']."/common/salesforce/partner.wsdl.xml");
	$connection->login($salesforce_username, $salesforce_password);

	$sObject = new SObject;
	$sObject->type = 'Lead';
	$sObject->fields = array('firstname' => $firstName,
														'lastname' => $lastName,
														'company' => $company,
														'email' => $email,
			    	     						'mig33_username__c' => $username,
				     								'comments__c' => $additionalInfo,
				     								'affiliate_country__c' => get_country(countryID));

	return $connection->upsert('mig33_username__c', array($sObject));
}

function sendMerchantEmail($username, $user_email, $first_name)
{
$message = "Welcome!
You�re now a member of the mig33 merchant program.

You now have exclusive access to our Merchant Center, where you'll find custom tools and support to help you build your business selling mig33 credits.

There are many benefits to being a merchant, and making money.
Some of these include free marketing, mig33 driving buyers to you, easy purchase and being recognized as a successful merchant within the mig33 community.

Simply go to the website www.mig33.com and click on the 'Merchant' link. Then simply login to the Merchant Center on the left hand side. You�re now ready to start.

To chat further with our merchant team, please email merchant@mig33.com. You can also provide a phone number or IM along with your preferred day/time and we�ll contact you to discuss more.

Good luck and we look forward to helping you.

Yours sincerely,
The mig33 merchant team";

mail($user_email, 'Welcome to the mig33 Merchant program', $message,'From:' .'merchant@mig33.com'. "\r\n" .'Reply-To: ' . 'merchant.mig33.com');
}

//Strip Out Javascript, Style Tags and Comments but allow HTML tags
function removeScriptFrom($string)
{
	$allowTags = '<strong><b><i><u><strike><p><li><ul><ol><br>';
	$returnString = strip_tags($string, $allowTags);
	return $returnString;
}

/**
* Check that a user is logged in. If they are, return true. Otherwise, die
*/
function check_session() {

	session_start();
	if (isset($_SESSION['user']))
	{
		return 1;
	}
	else
	{
		//Redirect to login page
		//header('Location: ' . $actualPath . '/member/login.php');
		header('location: ' . $_SESSION['loginLoc']);
		die();
	}
}

/**
* Check that a user is logged in and that they are a merchant. If they are, return true. Otherwise, die
*/
function check_session_merchant() {

	session_start();
	if ( (isset($_SESSION['user'])) &&  (!$_SESSION['user']['type'] != 'MIG_33') )
	{
		return 1;
	}
	else
	{
		//Redirect to merchant login page
		header('Location: ' . $actualPath . '/wap2/merch/login.php');
		die();
	}
}

/**
* Check that a user is logged in. If they are, return true. Otherwise, die
*/
function is_logged_in() {

	if (isset($_SESSION['user']))
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

/**
* Truncate a string to specified length and append '...' if necessary
*/
function truncateString($string, $length) {
	if(strlen($string) > $length){
		$string = substr($string,0,$length).'...';
	}

	return $string;
}

/**
* Get the name for a country
* @param	string	$countryID		The country ID
* @return	string	$countryName	The full name of the country
*/
function get_country($countryID)
{
	try
	{
		$countries = get_countries();
		foreach ($countries as $country)
		{
			if ($country['id'] == $countryID)
				return $country['name'];
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Get the country data
* @param	string	$countryID		The country ID
* @return	countryData in array
*/
function get_country_data($countryID)
{
	try
	{
		$countries = get_countries();
		foreach ($countries as $country)
		{
			if ($country['id'] == $countryID)
				return $country;
		}
	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Print out a combo box of countries
* @param	string	$countryIDSelected		The country ID to be selected in the combo box
* @param	string	$cselectName			The name of the select form element
*/
function countries_construct_combo($countryIDSelected, $all)
{
	try
	{
		$countries = get_countries();

		//Construct Output
		//print '<select name="'.$selectName.'"';
		if ($all)
			print '<option value="-1">All</option>';
		foreach ($countries as $country)
		{
			print '<option value="' . $country['id']. '"';
			if ($country['id'] == $countryIDSelected)
				print ' selected';
			print '>' . $country['name'] . '</option>';

		}
		//print '</select>';


	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

function countries_construct_combo_wnames($countryNameSelected)
{
	try
	{
		$countries = get_countries();

		//Construct Output
		foreach ($countries as $country)
		{
			print '<option value="' . $country['name']. '"';
			if ($country['name'] == $countryNameSelected)
				print ' selected';
			print '>' . $country['name'] . '</option>';

		}
		//print '</select>';


	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Get the list of countries. Attempts to use a cached version unless it is more then X hours old
* @return	mixed	$countries	The list of countries
*/
function get_countries()
{
	try
	{
		global $cache_objects_timeout;
		global $apache_dir;
		$filename = $apache_dir.'/cache/objects/countries.ser';

		//Try to deserialize the countries list if the cache file exists
		if ( (file_exists($filename)) && ( ( time() - filectime($filename) ) < $cache_objects_timeout) )
		{
			$handle = fopen($filename, 'r');
			//print time() - filectime($filename) . '<br/>';
			$countries_serialized = fread($handle, filesize($filename));
			fclose($handle);
			$countries = unserialize($countries_serialized);
			return $countries;
		}
		else
		//Call the EJB to get the countries list and serialize it to the cache file
		{
			$countries = soap_call_ejb('getCountries', array());
			$countries_serialized = serialize($countries);
			$handle = fopen($filename, 'w');
			fwrite($handle,$countries_serialized);
			fclose($handle);
			return $countries;
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Get the idd for a country
* @param	string	$countryID		The country ID
* @return	string	$countryIDD		The IDD of the country
*/
function get_country_idd($countryID)
{
	try
	{
		$countries = get_countries();
		foreach ($countries as $country)
		{
			if ($country['id'] == $countryID)
				return $country['iddCode'];
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
*Obtain the supported currency for CC payment given the local currency
*/
function get_currency_cc($countryId)
{
	try
	{
		$countries = get_countries();
		foreach ($countries as $country)
		{
			if ($country['id'] == $countryId){
				return $country['creditCardCurrency'];
			}
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
*Obtain the supported currency for LBD payment given the local currency
*/
function get_currency_lbd($countryId)
{
	try
	{
		$countries = get_countries();
		foreach ($countries as $country)
		{
			if ($country['id'] == $countryId){
				return $country['bankTransferCurrency'];
			}
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
*Obtain the supported currency for WU payment given the local currency
*/
function get_currency_wu($countryId)
{
	try
	{
		$countries = get_countries();
		foreach ($countries as $country)
		{
			if ($country['id'] == $countryId){
				return $country['westernUnionCurrency'];
			}
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}


/**
* Print out a combo box of currencies
* @param	string	$countryIDSelected		The currency code to be selected in the combo box
* @param	string	$cselectName			The name of the select form element
*/
function currencies_construct_combo($currencyCodeSelected, $selectName)
{
	try
	{
		$currencies = get_currencies();

		//Construct Output
		print '<select name="'.$selectName.'">';
		foreach ($currencies as $currency)
		{
			print '<option value="' . $currency['code']. '"';
			if ($currency['code'] == $currencyCodeSelected)
				print ' selected';
			print '>' . $currency['code'] . ' ' . $currency['name'] . '</option>';

		}
		print '</select>';


	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Get the exchange rate for a particular currency code
* @param	string	$currencyCode		The currency code
* @return	mixed	$exchangeRate		The exchange rate
*/
function get_exchangeRate($currencyCode)
{
	try
	{
		$currencies = get_currencies();
		foreach ($currencies as $currency)
		{
			if ($currency['code'] == $currencyCode)
				return $currency['exchangeRate'];
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}



/**
* Get the list of currencies. Attempts to use a cached version unless it is more then default seconds old
* @return	mixed	$currencies	The list of currencies
*/
function get_currencies()
{
	try
	{
		global $cache_objects_timeout;
		global $apache_dir;
		$filename = $apache_dir.'/cache/objects/currencies.ser';

		//Try to deserialize the countries list if the cache file exists
		if ( (file_exists($filename)) && ( ( time() - filectime($filename) ) < $cache_objects_timeout) )
		{
			$handle = fopen($filename, 'r');
			//print time() - filectime($filename) . '<br/>';
			$currencies_serialized = fread($handle, filesize($filename));
			fclose($handle);
			$currencies = unserialize($currencies_serialized);
			return $currencies;
		}
		else
		//Call the EJB to get the countries list and serialize it to the cache file
		{
			$currencies = soap_call_ejb('getCurrencies', array());
			$currencies_serialized = serialize($currencies);
			$handle = fopen($filename, 'w');
			fwrite($handle,$currencies_serialized);
			fclose($handle);
			return $currencies;
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Get the cached list of handset to vendor prefixes. Attempts to use a cached version unless it is more then default seconds old
* @return	mixed	$currencies	The Array List of Prefixes
*/
function get_handsetvendorprefixes()
{
	try
	{
		global $cache_objects_timeout;
		global $apache_dir;
		$filename = $apache_dir.'/cache/objects/handsetvendorprefixes.ser';

		//Try to deserialize the list if the cache file exists
		if ( (file_exists($filename)) && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
		{
			$handle = fopen($filename, 'r');
			//print time() - filectime($filename) . '<br/>';
			$prefixes_serialized = fread($handle, filesize($filename));
			fclose($handle);
			$prefixes = unserialize($prefixes_serialized);
			return $prefixes;
		}
		else
		//Call the EJB to get the list and serialize it to the cache file
		{
			$prefixes = soap_call_ejb('getHandsetVendorPrefixes', array());
			$prefixes_serialized = serialize($prefixes);
			$handle = fopen($filename, 'w');
			fwrite($handle,$prefixes_serialized);
			fclose($handle);
			return $prefixes;
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

/**
* Get the cached list of handset vendors. Attempts to use a cached version unless it is more then default seconds old
* @return	mixed	$prefixes The list of handset vendors
*/
function get_handsetvendors()
{
	try
	{
		global $cache_handsets_timeout;
		global $apache_dir;
		$filename = $apache_dir.'/cache/objects/handsetvendors.ser';

		//Try to deserialize the list if the cache file exists
		if ( (file_exists($filename)) && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
		{
			$handle = fopen($filename, 'r');
			//print time() - filectime($filename) . '<br/>';
			$prefixes_serialized = fread($handle, filesize($filename));
			fclose($handle);
			$prefixes = unserialize($prefixes_serialized);
			return $prefixes;
		}
		else
		//Call the EJB to get the list and serialize it to the cache file
		{
			$prefixes = soap_call_ejb('getHandsetVendors', array());
			$prefixes_serialized = serialize($prefixes);
			$handle = fopen($filename, 'w');
			fwrite($handle,$prefixes_serialized);
			fclose($handle);
			return $prefixes;
		}

	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}


/**
* Print out a combo box of handset vendors
* @param	string	$vendorSelected		The vendor to be pre selected
* @param	string	$selectName			The name of the select form element
*/
function vendors_construct_combo($selectName, $vendorSelected = null)
{
	try
	{
		$vendors = get_handsetvendors();
		//print_r($vendors);

		//Construct Output
		print '<select name="'.$selectName.'">';

		foreach ($vendors as $vendor)
		{
			print '<option value="' . $vendor['vendor']. '"';
			if ($vendor['vendor'] == $vendorSelected)
				print ' selected';
			print '>' . $vendor['vendor'] . '</option>';

		}
		print '<option value="Other">Other</option>';
		print '</select>';


	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}

}

/**
* Get the cached list of handset details for a specific vendor. Attempts to use a cached version unless it is more then default seconds old
* @return	mixed	$handsets	The Array List of Handsets
*/
function get_handsetdetails($vendor)
{
	//Determine which rategrid we need (The rategrid filename is the countryID
	global $apache_dir;
	$filename = $apache_dir.'/cache/objects/handsets_' . $vendor . '.ser';

	global $cache_objects_timeout;

	//Try to deserialize
	if ( (file_exists($filename)) && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
	{
		$handle = fopen($filename, 'r');
		$handsets_serialized = fread($handle, filesize($filename));
		fclose($handle);
		$handsets = unserialize($handsets_serialized);
	}
	else
	//Call the EJB to get the list and serialize it to the cache file
	{
		try
		{
			$handsets = soap_call_ejb('getHandsetDetails', array($vendor));
		}catch(Exception $e)
		{
			$error = $e->getMessage();
			print $error;
		}

		$handsets_serialized = serialize($handsets);
		$handle = fopen($filename, 'w');
		fwrite($handle,$handsets_serialized);
		fclose($handle);
		//print_r($handsets);
		//printf($handsets);
	}
	return $handsets;
}

/**
* Get the cached list of handset details for all default handsets. Attempts to use a cached version unless it is more then default seconds old
* @return	mixed	$handsets	The Array List of Handsets
*/
function get_default_handsetdetails()
{
	global $apache_dir;
	$filename = $apache_dir.'/cache/objects/default_handsets.ser';

	global $cache_objects_timeout;

	//Try to deserialize
	if ( (file_exists($filename)) && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
	{
		$handle = fopen($filename, 'r');
		$handsets_serialized = fread($handle, filesize($filename));
		fclose($handle);
		$handsets = unserialize($handsets_serialized);
	}
	else
	//Call the EJB to get the list and serialize it to the cache file
	{
		try
		{
			$handsets = soap_call_ejb('getDefaultHandsetDetails', array());
		}catch(Exception $e)
		{
			$error = $e->getMessage();
			print $error;
		}
		$handsets_serialized = serialize($handsets);
		$handle = fopen($filename, 'w');
		fwrite($handle,$handsets_serialized);
		fclose($handle);
	}
	return $handsets;
}

/**
* Print out a combo box of handsets for a particular vendor
* @param	string	$vendorSelected		The vendor to be pre selected
* @param	string	$selectName			The name of the select form element
*/
function handsets_construct_combo($selectName, $vendor, $handsetSelected = null)
{
	try
	{
		$handsets = get_handsetdetails($vendor);
		//print_r($vendors);

		//Construct Output
		print '<select name="'.$selectName.'">';

		foreach ($handsets as $handset)
		{
			//print_r($handset);
			print '<option value="' . $handset['phoneModel']. '"';
			if ($handset['phoneModel'] == $handsetSelected)
				print ' selected';
			print '>' . $handset['phoneModel'] . '</option>';

		}
		print '<option value="Other">Other</option>';
		print '</select>';


	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}

}

/**
* Validate an email address
* @param	string	$email	The email address to validate
* @return	boolean	Whether email address is valid
*/
function checkEmail($email)
{
	if (!eregi("^[_\.0-9a-z-]+@([0-9a-z][0-9a-z-]+\.)+[a-z]{2,3}$", $email))
		return 0;
	else
		return 1;
}

/**
* Check if a username is email compatible
* @param	string	$username	The username to validate
* @return	boolean	Whether the username is email compatible
*/
function checkEmailCompatibleUsername($username)
{
	if (preg_match('/^[a-z][a-z0-9_-]*(\.[a-z0-9_-]+)*$/i', $username))
		return 1;
	else
		return 0;
}

/**
* Check if a username is not a reserved alias for mail
* @param	string	$username	The username to validate
* @return	boolean	Whether the username is valid
*/
function checkEmailReservedAlias($username)
{
	/*
	global $mail_reserved_aliases;
	$usernameOK = true;

	$reserved_aliases = split(';', $mail_reserved_aliases);
	for ($i = 0; $i < sizeof($reserved_aliases); $i++)
	{
		if (strtolower($username) == $reserved_aliases[$i])
			$usernameOK = false;
	}
	return $usernameOK;
	*/
	if ($username == "contact")
		return false;
	else if ($username == "affiliate")
		return false;
	else if ($username == "marketing")
		return false;
	else if ($username == "corporate")
		return false;
	else if ($username == "finance")
		return false;
	else
		return true;
}

/*
*Detecting vendor information from user agent
*@param String useragent
*/
function detect_vendor($useragent){
  	global $mobile_vendor;

  	//Return if empty
  	if(empty($useragent)){
  		return;
  	}

  	//Lowercase of the user agent
  	$useragent = strtolower($useragent);

	//Retrieve from cache, the available vendor prefixes
	$vendors = get_handsetvendorprefixes();

	if(is_array($vendors)){
		//Loop thru vendor prefixes
		foreach ($vendors as $vendors_pref)
		{
			if(strstr($useragent, strtolower($vendors_pref['prefix']))){
				$mobile_vendor = $vendors_pref['vendor'];
				break;
			}
		}
	}
}

/*
*Detecting phone model from the user agent
*@param String useragent
*@param String vendor
*/
function detect_phonemodel($vendor, $useragent){
	global $mobile_phonemodel;
	global $mobile_midp;
	//Return if vendor is specified
	if(empty($vendor)){
		return;
	}

	//Get possible mobile handset models from a specific vendor
	$devices = get_handsetdetails($vendor);

	if(is_array($devices))
	{
		foreach ($devices as $device)
		{
			if(strstr($useragent, strtolower($device['phoneModel']))){
				$mobile_phonemodel = $device['phoneModel'];
				$mobile_midp = $device['midp'];
				break;
			}
		}

	}
}

/*
*Retrieve the midp version from a vendor and model combo
*
*/
function retrieve_midp($vendor, $model){
	global $mobile_midp;
	//Return if vendor is specified
	if(empty($vendor) || empty($model)){
		return;
	}

	//Get possible mobile handset models from a specific vendor
	$devices = get_handsetdetails($vendor);

	foreach ($devices as $device)
	{
		if($model == $device['phoneModel']){
			$mobile_midp = $device['midp'];
			break;
		}
	}
}

/*
*Retrieve al the information from a vendor and model combo
*
*/
function retrieve_handsetdetails($vendor, $model){
	global $mobile_vendor;
	global $mobile_phonemodel;
	global $mobile_instructionid;
	global $mobile_instructiontext;
	global $mobile_midletversion;
	global $mobile_midletaccepttype;
	global $mobile_cldc;
	global $mobile_midp;
	global $mobile_camerasupport;
	global $mobile_filesystemsupport;
	global $mobile_jpegsupport;
	global $mobile_gifsupport;
	global $mobile_pngsupport;
	global $mobile_screenwidth;
	global $mobile_screenheight;
	global $mobile_comments;
	global $mobile_signedmidletsupport;

	//Return if vendor is specified
	if(empty($vendor) || empty($model)){
		return;
	}

	//Get possible mobile handset models from a specific vendor
	$devices = get_handsetdetails($vendor);

	foreach ($devices as $device)
	{
		if($model == $device['phoneModel']){
			$mobile_phonemodel = $device['phoneModel'];
			$mobile_instructionid = $device['instructionId'];
			$mobile_instructiontext = $device['instructionText'];
			$mobile_midletversion = $device['midletVersion'];
			$mobile_midletaccepttype = $device['midletAcceptType'];
			$mobile_cldc = $device['cldc'];
			$mobile_midp = $device['midp'];
			$mobile_camerasupport = $device['cameraSupport'];
			$mobile_filesystemsupport = $device['fileSystemSupport'];
			$mobile_jpegsupport = $device['jpegSupport'];
			$mobile_gifsupport = $device['gifSupport'];
			$mobile_pngsupport = $device['pngSupport'];
			$mobile_screenwidth = $device['screenWidth'];
			$mobile_screenheight = $device['screenHeight'];
			$mobile_comments = $device['comments'];
			$mobile_signedmidletsupport = $device['signedMidletSupport'];
			break;
		}
	}
}

/*
*Retrieve al the information from a vendor and model combo (default cases)
*
*/
function retrieve_defaulthandsetdetails($vendor, $midp){
	global $mobile_vendor;
	global $mobile_phonemodel;
	global $mobile_instructionid;
	global $mobile_instructiontext;
	global $mobile_midletversion;
	global $mobile_midletaccepttype;
	global $mobile_cldc;
	global $mobile_midp;
	global $mobile_camerasupport;
	global $mobile_filesystemsupport;
	global $mobile_jpegsupport;
	global $mobile_gifsupport;
	global $mobile_pngsupport;
	global $mobile_screenwidth;
	global $mobile_screenheight;
	global $mobile_comments;
	global $mobile_signedmidletsupport;

	//Get possible mobile handset models from a specific vendor
	$devices = get_default_handsetdetails();

	foreach ($devices as $device)
	{
		if($vendor == $device['vendor'] && $midp == $device['midp']){
			$mobile_instructionid = $device['instructionId'];
			$mobile_instructiontext = $device['instructionText'];
			$mobile_midletversion = $device['midletVersion'];
			$mobile_midletaccepttype = $device['midletAcceptType'];
			$mobile_cldc = $device['cldc'];
			$mobile_midp = $device['midp'];
			$mobile_camerasupport = $device['cameraSupport'];
			$mobile_filesystemsupport = $device['fileSystemSupport'];
			$mobile_jpegsupport = $device['jpegSupport'];
			$mobile_gifsupport = $device['gifSupport'];
			$mobile_pngsupport = $device['pngSupport'];
			$mobile_screenwidth = $device['screenWidth'];
			$mobile_screenheight = $device['screenHeight'];
			$mobile_comments = $device['comments'];
			$mobile_signedmidletsupport = $device['signedMidletSupport'];
			break;
		}
	}
}

/*
*Detect midp version from user agent
*@param String useragent
*/
function detect_midp($useragent){
	global $mobile_midp;

	if(strstr($useragent, 'midp-1.0')){
		$mobile_midp = '1.0';
	}else if(strstr($useragent, 'midp-2.0')){
		$mobile_midp = '2.0';
	}
}

/**
* Detect a phone type for the WAP site.
* Attempts to set global variables $mobile_vendor, $mobile_model and $mobile_midp
*/
function phone_detect()
{
	global $mobile_vendor;
	global $mobile_model;
	global $mobile_midp;
	//Get the sessionID
	//$headers = apache_request_headers();
	//$user_agent = $headers['UserAgent'];
	$user_agent = $_SERVER['HTTP_USER_AGENT'];
	$user_agent = strtolower($user_agent);
	//print $user_agent;

	//Default is Unknown
	//$mobile_vendor = '';
	//$mobile_model  = '';
	//$mobile_midp = '';

	//Detect MIDP Version
	if (strstr($user_agent, 'midp-2.0'))
		$mobile_midp = '2.0';
	else if (strstr($user_agent, 'midp-1.0'))
		$mobile_midp = '1.0';


	if ($user_agent)
	{
		//samsung-sgh-d600/1.0 profile/midp-2.0 configuration/cldc-1.1 up.browser/6.2.3.3.c.1.101 (gui) mmp/2.0
		//nokian70-1/3.0546.2.3 series60/2.8 profile/midp-2.0 configuration/cldc-1.1
		//Determine the phone vendor
		if (strstr($user_agent, 'nokia'))
			$mobile_vendor = 'Nokia';
		if (strstr($user_agent, 'sonyericsson'))
			$mobile_vendor = 'Sony Ericsson';
		if (strstr($user_agent, 'motorola') || strstr($user_agent, 'mot-'))
			$mobile_vendor = "Motorola";
		if (strstr($user_agent, 'BlackBerry') || strstr($user_agent, 'blackberry'))
			$mobile_vendor = 'Blackberry';
		if (strstr($user_agent, 'MSIE') || strstr($user_agent, 'msie'))
			$mobile_vendor = 'msie';
		if (strstr($user_agent, 'samsung'))
			$mobile_vendor = 'Samsung';
		if (strstr($user_agent, 'lg'))
			$mobile_vendor = 'LG';
		if (strstr($user_agent, 'o2'))
			$mobile_vendor = 'O2';

		//Attempt to determine the phone model for Nokia Phones
		if ($mobile_vendor == 'Nokia')
		{
			$startIndex = (strpos($user_agent, 'nokia') + 5);
			//char ch = '\u002F'; In Java Slash was detected as this character code
			$endIndex = strpos($user_agent, '/');
			if ($endIndex < 6 || $endIndex > 9)
				$endIndex = strpos($user_agent, '-');
			$length = $endIndex - $startIndex;
			$mobile_model = substr($user_agent, $startIndex, $length);
			$mobile_model = ucfirst($mobile_model);
		}
		//Attempt to determine the phone model for Sony Ericsson Phones
		else if ($mobile_vendor == 'Sony Ericsson')
		{
			$startIndex = (strpos($user_agent, 'sonyericsson') + 12);
			//char ch = '\u002F'; In Java Slash was detected as this character code
			$endIndex = strpos($user_agent, '/');
			$length  = $endIndex - $startIndex;
			$mobile_model = substr($user_agent, $startIndex, $length);
			$mobile_model = ucfirst($mobile_model);
		}
		//Attempt to determine the phone model for Samsung Phones
		else if ($mobile_vendor == 'Samsung')
		{
			$startIndex = (strpos($user_agent, 'samsung') + 12);
			//char ch = '\u002F'; In Java Slash was detected as this character code
			$endIndex = strpos($user_agent, '/');
			$length  = $endIndex - $startIndex;
			$mobile_model = substr($user_agent, $startIndex, $length);
			$mobile_model = ucfirst($mobile_model);
		}
		//Attempt to determine the phone model for LG Phones
		else if ($mobile_vendor == 'LG')
		{
			$startIndex = (strpos($user_agent, 'lg') + 3);
			//char ch = '\u002F'; In Java Slash was detected as this character code
			$endIndex = strpos($user_agent, ' ');
			$length  = $endIndex - $startIndex;
			$mobile_model = substr($user_agent, $startIndex, $length);
			$mobile_model = ucfirst($mobile_model);
		}
		//Attempt to determine the phone model for Sony Ericsson Phones
		else if ($mobile_vendor == 'blackberry')
		{
			$startIndex = (strpos($user_agent, 'BlackBerry') + 11);
			//char ch = '\u002F'; In Java Slash was detected as this character code
			$endIndex = strpos($user_agent, '/');
			$length  = $endIndex - $startIndex;
			$mobile_model = substr($user_agent, $startIndex, $length);
			$mobile_model = ucfirst($mobile_model);
		}
		//Detect if it's the o2 atom
		else if ($mobile_vendor == 'O2')
		{
			if (strstr($user_agent, 'atom'))
				$mobile_model = 'Atom';
		}
	}


	//Just set test values for now
	//$mobile_vendor = 'Nokia';
	//$mobile_model = '6255';
	//print 'You are using a ' . $mobile_vendor . ' ' . $mobile_model;


}

//
/**
* For a certain IP detect the country it belongs to and return the relavant country data
*/
function get_country_from_ip($remote_ip)
{
	try
	{
		$ipNumber = sprintf("%u", ip2long($remote_ip));
		if ($ipNumber > 0)
		{
			settype($ipNumber, 'String');
			$countryData = soap_call_ejb('getCountryFromIPNumber', array($ipNumber));
			return $countryData;
		}
		else
		{
			throw new Exception('Unable to determine country code for IP ' . $remote_ip);
		}
	}
	catch(Exception $e)
	{
		throw new Exception($e->getMessage());
	}
}

//
/**
* Get a nicer description of your account activity for the WAP site.
*/
function get_short_acc_description($original)
{
       //Convert type to a user friendly value
       if ($original == 'ACTIVATION_CREDIT')
               return 'Activation';
       else if ($original == 'BONUS_CREDIT')
               return 'Bonus';
       else if ($original == 'CALL_CHARGE')
               return 'Call';
       else if ($original ==  'CREDIT_CARD')
               return 'Credit Card';
       else if ($original == 'CREDIT_CARD_CHARGEBACK')
               return 'Charge back';
       else if ($original == 'CREDIT_CARD_REFUND')
               return 'Refund';
       else if ($original == 'MANUAL')
               return 'Manual';
       else if ($original == 'PREMIUM_SMS_FEE')
               return 'Fee';
       else if ($original == 'PREMIUM_SMS_RECHARGE')
               return 'Recharge';
       else if ($original == 'VOUCHER_RECHARGE')
               return 'Voucher Recharge';
       else if ($original == 'VOUCHERS_CREATED')
               return 'Vouchers Created';
       else if ($original == 'VOUCHERS_CANCELLED')
               return 'Vouchers Cancelled';
       else if ($original == 'CURRENCY_CONVERSION')
               return 'Currency Conversion';
       else if ($original == 'PRODUCT_PURCHASE')
               return 'Product';
       else if ($original == 'REFERRAL_CREDIT')
               return 'Referral';
       else if ($original == 'REFUND')
               return 'Refund';
       else if ($original == 'SMS_CHARGE')
               return 'SMS';
       else if ($original == 'SYSTEM_SMS_CHARGE')
               return 'SMS';
       else if ($original == 'SUBSCRIPTION')
               return 'Subscription';
       else if ($original == 'TELEGRAPHIC_TRANSFER')
               return 'TT';
       else if ($original == 'USER_TO_USER_TRANSFER')
               return 'Transfer';
       else
               return '';
}


/**
* Log a user in
* @param	string	$username	Username
* @param	string	$password	Password
*/
function login($username, $password, $captcha_ok)
{
	session_start();

	//Check that username and password entered are not blank
	if ((strlen($username) == 0) || (strlen($password) == 0))
	{
		return 0;
		die();
	}

	//Load the userdata
	if ((strlen($username) > 0) && (strlen($password) > 0))
	{
		try
		{
			$userData = soap_call_ejb('loadUserDetails', array($username));
		}
		catch(Exception $e)
		{
			//This exception is successful as we do not want the user to exist
			return 0;
		}

	}

	//If more than 5 failed logins captcha must be displayed. Unless captcha has been verified
	if (!$captcha_ok)
	{
		if ($userData['failedLoginAttempts'] > 5)
		{
			return 3;
		}
	}

	//Check username and password
	if (strtolower($password) == strtolower($userData['password']))
	{
		if($userData['status'] == 'ACTIVE')
		{
			//Login Successful: Start a Session, assigning the users data
			$_SESSION['user'] = $userData;

			//Call EJB to indicate successful login
			try{
				soap_call_ejb('loginSucceeded',array( $userData['username']));
			}
			catch(Exception $e)
			{
				//Nothing We Can Do Here
			}

			return 1;
		}
		else
		{
			//Account is disabled
			return 2;
		}
	}
	else
	{
		//Password is incorrect. Increment the number of failed login attempts
		consoleOut($userData['failedLoginAttempts']);

		//Update the number of failed logins
		try{
			soap_call_ejb('loginFailed',array( $userData['username']));
		}
		catch(Exception $e)
		{
			//Nothing We Can Do Here
		}

		return 0;
	}
}


/**
* Log a user out of the website (also end Fusion Session)
* NOT USED ANYMORE...
*/
function logout() {

	//End the users session
	session_start();
	unset($_SESSION['user']);

	//Redirect to Front Page
	//Somehow reinitialize frame
	header("Location: ../index.php");
}


/**
* Call a remote fusion EJB via SOAP
*
* @param	string	$function	The EJB function to call
* @param	array	$parameters	An array representing the parameter list for the call
* @return	mixed	$response	The output of the EJB function
*/
function soap_call_ejb($function, $parameters){

	require_once('nusoap.php');
	global $soap_ejb_service_url;
	global $soap_ejb_service_name;

	$nusoapclient = new nusoapclient($soap_ejb_service_url);

	$response = $nusoapclient->call($function, $parameters , $soap_ejb_service_name);

	//print_r($response);
	//die();

	//Handle exeption for when the SOAP Web Service is Down
	if(!isset($response))
	{
		throw new Exception('No Response Received from SOAP Service');
	}
	//Handle exceptions for Vectors / Hashtables return values

	if(is_array($response))
	{
		if (isset($response['EJBException']))
		{
			throw new Exception($response['EJBException']);
		}
	}
	//Handle exceptions for String Return Values
	if (is_string($response))
	{
		if (stripos($response, 'Exception') == 3)
		{
			throw new Exception(str_replace('EJBException:', '', $response));
		}
	}

	return $response;
}

/**
* Prepare a SOAP EJB Call that works on a Data Object that was originally sent from Java
*
* @param	$hashTableData	The hashtable Data object that was derived from a previous EJB Call
* @return	$response	An array
*/
function soap_prepare_call($hashTableData)
{
	$keys = array_keys($hashTableData);
	$values = array_values($hashTableData);
	return array($keys, $values);
}

/**
* Checks with the Fusion registry whether a connection exists for the session id sent during an XHTML paglet request.
* Automatically dies if no valid session exists.
*/
function ice_check_session() {

	//Get the request headers
	$headers = apache_request_headers();

	//Try to get session ID for midlet
	if (isset($headers['sid']))
		$sessionID = $headers['sid'];
	//If session ID not in header then client is AJAX. Load session ID from cookie.
	else if (isset($_COOKIE['sid'])) {
		$sessionID = $_COOKIE['sid'];
	} else {
		header('location: ' . $_COOKIE['loginLoc']);
		//echo "die";
		die;
	}

	//Get midlet version
	if (isset($headers['mig33']))
		$mig33 = $headers['mig33'];
	else
		$mig33 = "";

	global $ICE;
	global $ice_registry_connection;
	global $ice_registry_slice;
	global $connectionPrx;

	Ice_loadProfile();

	try
	{
		$obj = $ICE->stringToProxy($ice_registry_connection);
		$registry = $obj->ice_checkedCast($ice_registry_slice);

	    try
	    {
	    	$connectionPrx = $registry->findConnectionObject($sessionID);
	    }
	    catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
		{
			//echo "object not found";
			header("location: " . $_COOKIE['loginLoc']);
			die();
		}
	}
	catch(Ice_LocalException $ex)
	{
  	header("location: " . $_COOKIE['loginLoc']);
	  die();
	}
}

/**
* Get the username from the Fusion registry. Must be called after a call to ice_check_session
*/
function ice_get_username() {

	global $ICE;
	global $ice_registry_connection;
	global $ice_registry_slice;
	global $connectionPrx;

	try
	{
	    try
	    {
	    	$username = $connectionPrx->getUsername();
	    	return $username;
	    }
	    catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
		{
			die();
		}
	}
	catch(Ice_LocalException $ex)
	{
	   die();
	}
}

/**
* Get userdata for a particular username from the Fusion registry. Must be called after a call to ice_check_session
*/
function ice_get_userdata() {

	global $ICE;
	global $ice_registry_connection;
	global $ice_registry_slice;
	global $connectionPrx;

	try
	{
	    try
	    {
	    	$userPrx = $connectionPrx->getUserObject();
	    	$userData = $userPrx->getUserData();
	    	return $userData;
	    }
	    catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
		{
			die();
		}
	}
	catch(Ice_LocalException $ex)
	{
	   die();
	}
}

/**
* Update the users unread message count with the Fusion registry. This will update the count in the midlet
*/
function ice_email_notification($numUnread) {

	global $ICE;
	global $ice_registry_connection;
	global $ice_registry_slice;
	global $connectionPrx;

	try
	{
	    try
	    {
	    	$userPrx = $connectionPrx->getUserObject();
	    	$userData = $userPrx->emailNotification($numUnread);
	    }
	    catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
		{
			throw new Exception($ex->getMessage());
		}
	}
	catch(Ice_LocalException $ex)
	{
	   throw new Exception($ex->getMessage());
	}


}


/**
* Write a message out to the Java console. (Useful for debugging)
*/
function consoleOut($message)
{
	try
	{
		soap_call_ejb('consoleOut', array($message));
	}
	catch(Exception $e)
	{
		$error = $e->getMessage();
	}
}

/**
* Create an IMAP mail account with the supplied username and password.
*/
function mail_create_account($username, $password)
{
	//throw new Exception("Email is currently disabled due to routine maintenance. Please try again soon");

	global $imap_admin;
	global $imap_admin_port;
	global $imap_admin_username;
	global $imap_admin_password;
	global $imap_server;
	global $imap_port;
	global $imap_domain;

    // Login to the mailbox (which will automatically create the SurgeMail account) and then create
    // the Sent Items and Trash folders
	$mailbox = @imap_open('{' . $imap_server . ':' . $imap_port . '/imap}', $username, $password, CL_EXPUNGE);

	if($mailbox){
		$list = imap_list($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}', "*");
		if (is_array($list)) {
			$gotInbox = false;
			$gotSentBox = false;
			$gotTrash = false;
			foreach ($list as $val) {
				$boxname = imap_utf7_decode($val);
				if(strpos($boxname,'INBOX')){
					$gotInbox = true;
				}else if(strpos($boxname, 'Trash')){
					$gotTrash = true;
				}else if(strpos($boxname, 'Sent Items')){
					$gotSentBox = true;
				}
			}

			if(!$gotInbox){
				if (!@imap_createmailbox($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}INBOX')) {
					imap_close($mailbox);
					throw new Exception("Unable to create mail folders. " . imap_last_error());
				}
			}

			if(!$gotTrash){
				if (!@imap_createmailbox($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}Trash')) {
					imap_close($mailbox);
					throw new Exception("Unable to create mail folders. " . imap_last_error());
				}
			}

			if(!$gotSentBox){
				if (!@imap_createmailbox($mailbox, '{' . $imap_server . ':' . $imap_port . '/imap}Sent Items')) {
					imap_close($mailbox);
					throw new Exception("Unable to create mail folders. " . imap_last_error());
				}
			}
		}
	}

	imap_close($mailbox);
}

/**
* Takes in IMAP headerinfo and returns the reply email adress
*/
function mail_return_address($header_info)
{
	$from = $header_info->from[0];
	$email_box = $from->mailbox;
	$email_host = $from->host;
	$to = $email_box . '@' . $email_host;
	return $to;
}

/**
* Return the username for a mig33 email address
*/
function username_address($address)
{
	if (stripos($address, '@') > 0)
		return substr($address, 0, stripos($address, '@'));
	else
		return $address;
}

/**
* Open a mailbox connection with the IMAP Mailserver. If an account with the supplied details does not exist, one will be created.
*/
function mail_openbox($username, $password, $boxname)
{
	global $imap_server;
	global $imap_port;


	$mailbox = @imap_open('{' . $imap_server . ':' . $imap_port . '/imap}' . $boxname, $username, $password, CL_EXPUNGE);

	if (!$mailbox)
		throw new Exception("Could not connect to mail server");

 	return $mailbox;
}

/**
* Replaces all new lines characters with html <br> tag.
*/
function nl2br2($string)
{
	$string = str_replace(array("\r\n", "\r", "\n"), "<br/>", $string);
	return $string;
}

function build_group_combo()
{
	$groupIds = $_REQUEST['groupid'];
	$groupNames = $_REQUEST['groupname'];

	//Construct Output
	print '<select name="groupName" size="">';
	for ($i = 0; $i < sizeof($groupIds); $i++)
	{
		print '<option value="' . $groupIds[$i]. '"';

		print '>' . $groupNames[$i] . '</option>';

	}
	print '</select>';
}

/* Return the number of years since a particular date (useful for finding someone's age from a birth date) */
function yearsSince($birthdate){
	$year = date("Y", $birthdate);
	$month = date("m", $birthdate);
	$day = date("d", $birthdate);

	$year_diff  = date("Y") - $year;
    $month_diff = date("m") - $month;
    $day_diff   = date("d") - $day;
    if ($month_diff < 0) $year_diff--;
    elseif (($month_diff==0) && ($day_diff < 0)) $year_diff--;
    return $year_diff;
}

/**
 * Returns the remote IP address of the user.
 *
 * Normally we would use "$_SERVER['REMOTE_ADDR']".
 * However, the cookie-based load balancer places the remote IP at the end of
 * the "X-Forwarded-For" HTTP header, so we have to get it from there.
 *
 * The "X-Forwarded-For" may contain multiple addresses including any load
 * balancer IPs, separated by ", ".
 * e.g.:
 * X-Forwarded-For: 196.207.40.236, 196.207.40.212, 10.3.1.132, 10.3.2.145
 * If this is the case, we want to use the last address on the line that is
 * not the load balancer.
 *
 * @return string
 */
function getRemoteIPAddress()
{
	$remoteIP = isset($_SERVER['REMOTE_ADDR']) ? $_SERVER['REMOTE_ADDR'] : '';

	if (empty($_SERVER['HTTP_X_FORWARDED_FOR']))
	{
		return $remoteIP;
	}
	// pick the last IP which does not start with 10.XXX.XXX.XXX
	else if (preg_match('/(\d{1,3}(\.\d{1,3}){3})(\s*,\s*10(\.\d{1,3}){3})*$/i', $_SERVER['HTTP_X_FORWARDED_FOR'], $addresses))
	{
		return $addresses[1];
	}
	else
	{
		// X-Forwarded-For found but the above regex fails
		// use error_log instead of Logger
		error_log('getRemoteIPAddress, no IP address found, HTTP_X_FORWARDED_FOR: ' . $_SERVER['HTTP_X_FORWARDED_FOR']);
		return $remoteIP;
	}
}


### Google Analaytics Code
function googleAnalyticsGetImageUrl() {
	global $server_root, $ga_account;
	$GA_PIXEL = "$server_root/sites/lib/ga.php";
	$url = "";
	$url .= $GA_PIXEL . "?";
	$url .= 'utmac=' . str_replace('UA', 'MO', $ga_account);
	$url .= "&utmn=" . rand(0, 0x7fffffff);
	$referer = $_SERVER["HTTP_REFERER"];
	$query = $_SERVER["QUERY_STRING"];
	$path = $_SERVER["REQUEST_URI"];
	if (empty($referer)) {
		$referer = "-";
	}
	$url .= "&utmr=" . urlencode($referer);
	if (!empty($path)) {
		$url .= "&utmp=" . urlencode($path);
	}
	$url .= "&guid=ON";
	return str_replace("&", "&amp;", $url);
}
#-------------------------- FUNCTIONS END --------------------------#
?>
