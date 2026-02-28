<?php
#mig33 common includes file
#PURPOSE : 	this file will be used to contain all common code used by the mig33
#web project.

//Include Server Configuration Variables
require_once("common-config.php");
require_once("language.php");

// Alternative to above, this does not use mysql_real_escape_string (which we currently have turned off in dev at least).
// From the web site: http://talks.php.net/show/php-best-practices/26
// Recommended to be secure from a buffer overflow attack...

if (get_magic_quotes_gpc()) {
        $in = array(&$_GET, &$_POST, &$_COOKIE);
        while (list($k,$v) = each($in)) {
                foreach ($v as $key => $val) {
                        if (!is_array($val)) {
                                $in[$k][$key] = stripslashes($val);
                                continue;
                        }
                        $in[] =& $in[$k][$key];
                }
        }
        unset($in);
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

function round_twodec($value){
	if ($value != round($value))
		return number_format($value, 2);
	else
		return round($value);
}

// format currency numbers to never be more than 4 decimals total including decimal point
function format_currency($value){
	if (round($value) > 99)
		return round($value);
	if (round($value) > 9)
		return number_format($value, 2);
	else
		return number_format($value, 3);
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
		header('Location: ' . $actualPath . '/member/login.php');
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
		header('Location: ' . $actualPath . '/merch/index.php');
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
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_country_name($countryID);
}


/**
* Get the idd for a country
* @param	string	$countryID		The country ID
* @return	string	$countryIDD		The IDD of the country
*/
function get_country_idd($countryID)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_country_idd($countryID);
}

/**
* Get the country data
* @param	string	$countryID		The country ID
* @return	countryData in array
*/
function get_country_data($countryID)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_country_data($countryID);
}

/**
*Obtain the currency for a particular country
*/
function get_currency($countryID)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_currency($countryID);
}

/**
*Obtain the supported currency for CC payment given the local currency
*/
function get_currency_cc($countryID)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_currency_cc($countryID);
}

/**
*Obtain the supported currency for LBD payment given the local currency
*/
function get_currency_lbd($countryID)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_currency_lbd($countryID);
}

/**
*Obtain the supported currency for WU payment given the local currency
*/
function get_currency_wu($countryID)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_currency_wu($countryID);
}

/**
* Print out a combo box of countries
* @param	string	$countryIDSelected		The country ID to be selected in the combo box
* @param	string	$cselectName			The name of the select form element
*/
function countries_construct_combo($countryIDSelected, $all, $view='web')
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
			{
				if($view == 'wap')
				{
					print ' selected="selected"';
				}
				else
				{
					print ' selected';
				}
			}
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
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	return CountryDAO::get_countries();
}

/**
* Print out a combo box of currencies
* @param	string	$countryIDSelected		The currency code to be selected in the combo box
* @param	string	$cselectName			The name of the select form element
*/
function currencies_construct_combo($currencyCodeSelected, $selectName, $printSelectTag)
{
	try
	{
		$currencies = get_currencies();

		//Construct Output
		if($printSelectTag) print '<select name="'.$selectName.'">';
		foreach ($currencies as $currency)
		{
			print '<option value="' . $currency['code']. '"';
			if ($currency['code'] == $currencyCodeSelected)
				print ' selected';
			print '>' . $currency['code'] . ' ' . $currency['name'] . '</option>';

		}
		if($printSelectTag) print '</select>';


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
		if ( (file_exists($filename)) && filesize($filename) > 100 && ( ( time() - filectime($filename) ) < $cache_objects_timeout) )
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
		global $cache_objects_timeout, $cache_handsets_timeout;
		global $apache_dir;
		$filename = $apache_dir.'/cache/objects/handsetvendorprefixes.ser';

		//Try to deserialize the list if the cache file exists
		if ( (file_exists($filename)) && filesize($filename) > 10 && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
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
		if ( (file_exists($filename)) && filesize($filename) > 10 && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
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

		//Construct Output
		print '<select name="'.$selectName.'">';

		//Default
		print '<option value="">- Choose -</option>';

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

	global $cache_objects_timeout, $cache_handsets_timeout;

	//Try to deserialize
	if ( (file_exists($filename)) && filesize($filename) > 10 && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
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
	if ( (file_exists($filename)) && filesize($filename) > 10 && ( ( time() - filectime($filename) ) < $cache_handsets_timeout) )
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
		//print_r($handsets);

		//Construct Output
		print '<select name="'.$selectName.'">';

		//Default
		print '<option value="">- Choose -</option>';

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
   $isValid = 1;
   $atIndex = strrpos($email, "@");
   if (is_bool($atIndex) && !$atIndex)
   {
      $isValid = 0;
   }
   else
   {
      $domain = substr($email, $atIndex+1);
      $local = substr($email, 0, $atIndex);
      $localLen = strlen($local);
      $domainLen = strlen($domain);
      if ($localLen < 1 || $localLen > 64)
      {
         // local part length exceeded
         $isValid = 0;
      }
      else if ($domainLen < 1 || $domainLen > 255)
      {
         // domain part length exceeded
         $isValid = 0;
      }
      else if ($local[0] == '.' || $local[$localLen-1] == '.')
      {
         // local part starts or ends with '.'
         $isValid = 0;
      }
      else if ($domainLen > 0 && ($domain[$domainLen-1] == '.') || ($domain[0] == '.') )
      {
         // domain part start or ends with '.'
         $isValid = 0;
      }
      else if (preg_match('/\\.\\./', $local))
      {
         // local part has two consecutive dots
         $isValid = 0;
      }
      else if (!preg_match('/^[A-Za-z0-9\\-\\.]+$/', $domain))
      {
         // character not valid in domain part
         $isValid = 0;
      }
      else if (preg_match('/\\.\\./', $domain))
      {
         // domain part has two consecutive dots
         $isValid = 0;
      }
      else if (!preg_match('/\\./', $domain))
      {
         // domain part has no dot
         $isValid = 0;
      }
      else if (!preg_match('/^(\\\\.|[A-Za-z0-9!#%&`_=\\/$\'*+?^{}|~.-])+$/', str_replace("\\\\","",$local)))
      {
         // character not valid in local part unless
         // local part is quoted
         if (!preg_match('/^"(\\\\"|[^"])+"$/', str_replace("\\\\","",$local)))
         {
            $isValid = 0;
         }
      }
   }

   return $isValid;
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
//	$mail_reserved_aliases = "contact;affiliate;marketing;corporate;finance";
	global $mail_reserved_aliases;

	$reserved_aliases = split(';', $mail_reserved_aliases);
	foreach ($reserved_aliases as $alias)
		if (strtolower($username) == $alias)
			return false;

	return true;
}

/**
*Detect if device is a windows mobile device.
*@param String useragent
*/
function detect_wm_device($useragent)
{
	global $mobile_wm;
	global $mobile_wm_os_version;	//Indicates wm's os version, i.e. WM 5 or WM 6
	global $mobile_wm_type;			//indicates ppc or smartphone

	if(empty($useragent))
	{
		return;
	}
	else
	{
		strtolower($useragent);
	}

	if(strstr($useragent, 'windows ce'))
	{
		$mobile_wm = 1;

		//Detect if it is WM version
		if(strstr($useragent, 'msie 4.') || strstr($useragent, 'msie 5.'))
		{
			//Detected that it is WM 5
			$mobile_wm_os_version = '5';

			//Detect what type it is
			if(strstr($useragent, 'ppc'))
			{
				//Detected it is a WM 5: PPC
				$mobile_wm_type = 'ppc';

			}
			else if(strstr($useragent, 'smartphone'))
			{
				//Detected it is a WM 5: Smartphone
				$mobile_wm_type = 'smartphone';
			}
			else
			{
				//END: We know it is a WM 5 device only. So show only WM 5 version.
			}

		}
		else if(strstr($useragent, 'msie 6.'))
		{
			//Detected that it is WM 6
			$mobile_wm_os_version = '6';


			//Detect what type it is
			if(strstr($useragent, 'ppc'))
			{
				//Detected it is a WM 6: PPC
				$mobile_wm_type = 'professional';

			}
			else if(strstr($useragent, 'smartphone'))
			{
				//Detected it is a WM 6: Smartphone
				$mobile_wm_type = 'standard';

			}
			else
			{
				//END: We know it is a WM 6 device only. So show only WM 6 versions.
			}

		}
		else
		{
			//END: We know it is a WM device but no idea what version it is. So show all the WM versions available.
		}
	}
	else
	{
		$mobile_wm = 0;
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
*Detect vendor, model and MIDP in one go
*@param String useragent
*/
function detect_phonedetails($useragent)
{
	global $mobile_phonemodel;
	global $mobile_midp;
	global $mobile_vendor;

	//Useragent is mandatory
  	if(empty($useragent))
  	{
  		return;
  	}

	//Lowercase of the user agent
  	$useragent = strtolower($useragent);

  	//Detect Vendor
  	detect_vendor($useragent);

  	//Check if we detected a vendor. If not check if it is a WM device
  	if(!empty($mobile_vendor))
  	{
  		//Detected a vendor.
  		//Attempt to detect a model, given a vendor
  		detect_phonemodel($mobile_vendor, $useragent);

  		//Check if model has been detected.
  		if(!empty($mobile_phonemodel))
  		{
  			//Detected model and vendor. We also have MIDP information from DB for that model.
  			//Do a check to see if it is a WM device so we can show extra link for all WM versions
  			detect_wm_device($useragent);
  		}
  		else
  		{
  			//Could not detect the model of the phone.
  			//Last thing to detect is MIDP layer
  			detect_midp($useragent);

  			if(empty($mobile_midp))
  			{
  				//No MIDP information, check if it is a WM device
  				detect_wm_device($useragent);
  			}
  		}
  	}
  	else
  	{
  		//Not able to detect a vendor by going through vendor prefixes in DB.
  		//Detect if it is a WM device
  		detect_wm_device($useragent);
  	}

  	return array("model"=> $mobile_phonemodel, "midp" => $mobile_midp, "vendor" => $mobile_vendor);
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
				//Special check if it is Siemens (need more checking) since its prefix is SIE (similar to MSIE for win mobiles)
				if($vendors_pref['vendor'] == 'Siemens' && strstr($useragent, 'msie'))
				{
					//User agent has got MSIE in it and current iteration is on siemens phone
					//Do nothing. Assume no match
				}
				else
				{
					$mobile_vendor = $vendors_pref['vendor'];
					break;
				}
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
	global $mobile_application_icon_size;

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
			$mobile_application_icon_size = $device['applicationIconSize'];
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

	if (! empty($devices) && is_array($devices))
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
function get_country_from_ip($remote_ip=null)
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
	if (empty($remote_ip))
	{
		return CountryDAO::get_country_from_user_ip();
	}
	return CountryDAO::get_country_from_ip($remote_ip);
}

/**
* Log a user in
* @param	string	$username	Username
* @param	string	$password	Password
*/
function login($username, $password, $captcha_ok, $merchantLogin=false)
{
	session_start();

	//Check that username and password entered are not blank
	if ((strlen($username) == 0) || (strlen($password) == 0))
	{
		sleep(3);  // Sleep to prevent brute-force attacks
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
			sleep(3);  // Sleep to prevent brute-force attacks
			return 0;
		}

	}

	//If merchant login, check the user is a merchant
	if ($merchantLogin)
	{
		if ($userData['type'] == 'MIG33')
		{
			//If they are not a merchant return error
			sleep(3);  // Sleep to prevent brute-force attacks
			return 4;
			die();
		}
	}

	//If more than 5 failed logins captcha must be displayed. Unless captcha has been verified
	if (!$captcha_ok)
	{
		if ($userData['failedLoginAttempts'] > 5)
		{
			sleep(3);  // Sleep to prevent brute-force attacks
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
			sleep(3);  // Sleep to prevent brute-force attacks
			return 2;
		}
	}
	else
	{
		//Password is incorrect. Increment the number of failed login attempts
		//consoleOut($userData['failedLoginAttempts']);

		//Update the number of failed logins
		try{
			soap_call_ejb('loginFailed',array( $userData['username']));
		}
		catch(Exception $e)
		{
			//Nothing We Can Do Here
		}

		sleep(3);  // Sleep to prevent brute-force attacks
		return 0;
	}
}


/**
* Log a user out of the website
* NOT USED ANYMORE...

function logout() {

	//End the users session
	session_start();
	unset($_SESSION['user']);

	//Redirect to Front Page
	//Somehow reinitialize frame
	header("Location: ../index.php");
}
*/

function areSoapResponsesEqual($nusoapResponse, $nativesoapResponse)
{
    return $nusoapResponse == $nativesoapResponse;
}


function make_nusoap($arr)
{
	foreach($arr as $k => $v)
	{
        // for fusion return types, 'objects' are vectors and should contain arrays
        // php native soap has a bug that if the vector contains a single element
        // the element itself is returned instead of an array with the element at index 0
        // the code below is used to correct that
		if (is_object($v) && isset($v->item))
		{
			if (!is_array($v->item) || !isset($v->item[0]))
			{
                // caters for single element arrays
                $arr[$k] = make_nusoap(array($v->item));
			}
			else
			{
                $arr[$k] = make_nusoap($v->item);
			}
		}
		else if (is_array($v))
		{
			$arr[$k] = make_nusoap($v);
		}
	}
	return $arr;
}

/**
* Call a remote fusion EJB via SOAP
*
* @param	string	$function	The EJB function to call
* @param	array	$parameters	An array representing the parameter list for the call
* @return	mixed	$response	The output of the EJB function
*/
function soap_call_ejb($function, $parameters)
{
    global $soap_ejb_service_url;
	global $soap_ejb_service_name;

    $USE_NATIVE_SOAP = TRUE;

    if ($USE_NATIVE_SOAP)
    {
    	try{
	        $client = new soapclient(null, array(
	            'location' => $soap_ejb_service_url,
	            'uri' => $soap_ejb_service_name,
	            'features' => SOAP_SINGLE_ELEMENT_ARRAYS | SOAP_USE_XSI_ARRAY_TYPE,
	            'trace' => false,
	            'connection_timeout' => 5
	            // 'trace' => true,
	        ));
	        $response = $client->__soapCall($function, $parameters);
	        $response = make_nusoap(array($response));
	        $response = $response[0];

	        // we will compare responses for the following actions
	        if (isset($_GET['compare_soap']))
	        {
	            require_once('nusoap.php');
	            $nusoapclient = new nusoapclient($soap_ejb_service_url);
	            $response2 = $nusoapclient->call($function, $parameters , $soap_ejb_service_name);
	            if (!areSoapResponsesEqual($response2, $response))
	            {
	                error_log("SOAP_RESPONSE_DIFFERENCE: when calling $function");
	            }
	        }
    	}catch(Exception $e){
    		$msg = $e->getMessage();
    		$pos = strrpos($msg,'EJBException:');
    		if($pos){
    			$response = substr($msg,$pos,strlen($msg)-$pos+1);
    		}
    		//$response = $e->getMessage();
    	}

    }
    else
    {
        // ===================================
        // original nusoap code below
        // ===================================
        require_once('nusoap.php');

        $nusoapclient = new nusoapclient($soap_ejb_service_url);

        $response = $nusoapclient->call($function, $parameters , $soap_ejb_service_name);
    }

	// Handle exeption for when the SOAP Web Service is Down
    // Note: this throws an exception when the return value is null, even is null was a valid response :(
	if(!isset($response))
	{
		throw new Exception('No Response Received from SOAP Service');
	}

	// Handle exceptions for Vectors / Hashtables return values
	if(is_array($response))
	{
		if (isset($response['EJBException']))
		{
			throw new Exception($response['EJBException']);
		}
		else //this is to handle two level array response
		{
			if (isset($response[0]) && is_array($response[0]))
			{
				$new_response = $response[0];
				if (isset($new_response['EJBException']))
				{
					throw new Exception($new_response['EJBException']);
				}
			}
		}
	}

	// Handle exceptions for String Return Values
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

function get_ice_dao()
{
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');

	return new IceDAO();
}

/**
* Checks with the Fusion registry whether a connection exists for the session id sent during an XHTML paglet request.
* Automatically dies if no valid session exists.
*/
function ice_check_session()
{
	try
	{
		get_ice_dao()->check_session();
	}
	catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
	{
		die();
	}
	catch(Ice_LocalException $ex)
	{
		die();
	}
	catch (IceDAOException $ex)
	{
		die();
	}
}

/**
* Get the username from the Fusion registry. Must be called after a call to ice_check_session
*/
function ice_get_username()
{
	try
	{
		return get_ice_dao()->get_username(0);
	}
	catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
	{
		die();
	}
	catch(Ice_LocalException $ex)
	{
		die();
	}
	catch (IceDAOException $ex)
	{
		die();
	}
}

/**
* Get userdata for a particular username from the Fusion registry.
* Must be called after a call to ice_check_session
*/
function ice_get_userdata()
{
	try
	{
		return get_ice_dao()->get_userdata();
	}
	catch(com_projectgoth_fusion_slice_ObjectNotFoundException $ex)
	{
		die();
	}
	catch(Ice_LocalException $ex)
	{
		die();
	}
	catch (IceDAOException $ex)
	{
		die();
	}
}

/**
* Update the users unread message count with the Fusion registry. This will update the count in the midlet
*/
function ice_email_notification($numUnread)
{
	get_ice_dao()->email_notification($numUnread);
}

function ice_get_publishing_privacy_setting($username)
{
	try
	{
		return get_ice_dao()->get_publishing_privacy_setting($username);
	}
	catch(Exception $ex)
	{
		die();
	}
}

function ice_get_receiving_privacy_setting($username)
{
	try
	{
		return get_ice_dao()->get_receiving_privacy_setting($username);
	}
	catch(Exception $ex)
	{
		die();
	}
}

function ice_set_publishing_privacy_setting($username, $settings)
{
	try
	{
		return get_ice_dao()->set_publishing_privacy_setting($username, $settings);
	}
	catch(Exception $ex)
	{
		die();
	}
}

function ice_set_receiving_privacy_setting($username, $settings)
{
	try
	{
		return get_ice_dao()->set_receiving_privacy_setting($username, $settings);
	}
	catch(Exception $ex)
	{
		die();
	}
}

function ice_get_chatroom_users($chatroom, $username)
{
	return get_ice_dao()->get_chatroom_users($chatroom, $username);
}

function ice_get_groupchat_users($groupchatid, $username)
{
	return get_ice_dao()->get_groupchat_users($groupchatid, $username);
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
	$email_map = array('contact@support.mig33.com' => 'support@mig33.zendesk.com');

	$from = $header_info->from[0];
	$email_box = $from->mailbox;
	$email_host = $from->host;
	$to = $email_box . '@' . $email_host;
	if(in_array($to, array_keys($email_map)))
		return $email_map[$to];
	else
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

/**
* Replaces all new lines characters with html <br> tag.
*/
function nl2brNoSlash($string)
{
	$string = str_replace(array("\r\n", "\r", "\n"), "<br>", $string);
	return $string;
}

function br2nl($string){
	$string = eregi_replace('<br[[:space:]]*/?'.'[[:space:]]*>',chr(13).chr(10),$string);
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

function sendMerchantEmail($username, $user_email, $first_name)
{
$message = "Welcome!
You now have exclusive access to the mig33 Merchant Center, where you'll find
custom tools and support to help you build your business selling mig33
credits.

There are many benefits to being a mig33 merchant and making money.
Some of these include free marketing, mig33 sending buyers to you,
and being recognized as a successful merchant within the mig33
community.

Simply go to the website www.mig33.com and click on the 'Merchant' link.
Then login to the Merchant Center on the left hand side. You're
now ready to start.

NEW: And now it's even easier to start a trial purchase. For your first time,
all you need is at least US$5 and you can buy at a 30% discount when paying
with local banking deposit, Telegraphic Transfer or Western Union.

To chat further with our merchant team, please email merchant@mig33.com.
You can also provide a phone number or IM along with your preferred
day/time and we'll contact you to discuss more.

Good luck and we look forward to helping you.

Yours sincerely,
The mig33 merchant team

Coming soon  - Merchant Web Forum and phone support for our merchants!";


mail($user_email, 'Welcome to the mig33 Merchant program', $message,'From:' .'merchant@mig33.com'. "\r\n" .'Reply-To: ' . 'merchant.mig33.com');
}

/**
* Adds the detail of a merchant into Salesforce.com as a lead
*/
function addMerchantToSalesforce($username, $countryID, $firstName, $lastName, $company, $email, $promo, $phone, $additionalInfo, $newuser, $myself, $profit, $givetofriends, $other2, $admin, $other_description2) {
	/*
	require_once("salesforce/SforcePartnerClient.php");

	global $salesforce_username;
	global $salesforce_password;
	global $apache_dir;

	$connection = new SforcePartnerClient();
	$connection->createConnection($_SERVER['DOCUMENT_ROOT']."/common/salesforce/partner.wsdl.xml");
	$connection->login($salesforce_username, $salesforce_password);

// SForce requires we explicitely set all booleans to true or false...

	if(!$profit || $profit != 'true') $profit = 'false';
	if(!$myself || $myself != 'true') $myself = 'false';
	if(!$givetofriends || $givetofriends != 'true') $givetofriends = 'false';
	if(!$admin || $admin != 'true') $admin = 'false';
	if(!$other2 || $other2 != 'true') $other2 = 'false';
	if(!$newuser || $newuser != 'true') $newuser = 'false';

	$sObject = new SObject;
	$sObject->type = 'Lead';
	$sObject->fields = array('firstname' => $firstName,
							'lastname' => $lastName,
							'company' => $company,
							'email' => $email,
							'promo__c' => $promo,
							'Phone' => $phone,
							'mig33_username__c' => $username,
							'comments__c' => $additionalInfo,
							'affiliate_country__c' => get_country($countryID),
							'friend__c' => 'false',
							'advertising__c' => 'false',
							'mig33website__c' => 'false',
							'mig33inproduct__c' => 'false',
							'mig33community__c' => 'false',
							'other__c' => 'false',
							'other_description__c' => '',
							'profit__c' => $profit,
							'myself__c' => $myself,
							'givetofriends__c' => $givetofriends,
							'admin__c' => $admin,
							'other2__c' => $other2,
							'other_description2__c' => $other_description2,
							'newuser__c' => $newuser);

	return $connection->upsert('mig33_username__c', array($sObject));
	*/
	return true;
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

function getSessionID() {
    return getGatewaySessionID();
}

function getGatewaySessionID() {
	require_once $_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php';
	fast_require('SessionUtilities', get_framework_common_directory() . '/session_utilities.php');
	return SessionUtilities::get_session_id();
}

function getMobileDevice()
{
	return get_ice_dao()->get_mobile_device();
}

function getUserAgent() {
	//Get the request headers
	$headers = apache_request_headers();

	//Try to get user agent for midlet
	if (isset($headers['ua']))
		return $headers['ua'];

	//If user agent not in header then client is AJAX. Load user agent from $_SERVER
	return $_SERVER['HTTP_USER_AGENT'];
}

function sendWesternUnionEmail($username, $firstname, $lastname, $mtcn_number, $city, $country, $email, $amountUSD, $currency_wu, $amount, $answer, $paidto="")
{
$message = "Hi!

WU Notification:

The following Western Union payment has been logged by a user using the Green Form.

This is a person to person money transfer in the name of Jeffrey Tien-Huang Lim for the company migme.

The following information was entered by the user:

Username: ".$username. "

First Name: ".$firstname. "

Last Name: ".$lastname. "

10 Digit MTCN Number: ".$mtcn_number. "

City: ".$city. "

Country: ".$country. "

Email Address: ".$email. "

Amount Sent in USD: ".$amountUSD. "

Users Local Currency: ".$currency_wu. "

Amount Sent: ".$amount. "

Paid To:".$paidto."

Answer to secret question: ".$answer;

mail('mig33merch@gmail.com', 'WU Notification', $message,'From:' .'merchant@mig33.com'. "\r\n" .'Reply-To: ' . 'merchant.mig33.com');

}

function sendReportAbuseEmail($source, $type, $reporter, $offender, $subject, $subjectId, $reason)
{
	global $report_abuse_email;
    global $report_admin_abuse_email;
	global $mogileFSImagePath;
	global $imap_domain;

	$reporterEmail = $reporter.'@'.$imap_domain;
	$offenderEmail = $offender.'@'.$imap_domain;

	$message = "Reporter: ".$reporter."\n".
			   "Reporter email: ".$reporterEmail."\n".
			   "Date: ".gmdate('m/d/y H:i')."\n".
			   "Offender: ".$offender."\n".
			   "Offender email: ".$offenderEmail."\n";

	// FRAME-109: TODO: the url below must come from config or system property table
	$MIS_V2_BASE_URL = "http://mis.projectgoth.com:8989";

	if ($type == 1) {
        $to = isAdmin($offender) ? $report_admin_abuse_email : $report_abuse_email;
		$title = 'mig33 photo abuse report from '.$source;
		$message = $message."Photo: ".$mogileFSImagePath."/".$subjectId."\n";
		$message = $message."MIS: $MIS_V2_BASE_URL/photo/scrapbook/".$offender."\n";
	} else if ($type == 2) {
        $to = isAdmin($offender) ? $report_admin_abuse_email : $report_abuse_email;
		$title = 'mig33 profile abuse report from '.$source;
		$message = $message."Profile: $MIS_V2_BASE_URL/mig33_user/".$offender."\n";
	} else if ($type == 3) {
        $to = isAdmin($offender) ? $report_admin_abuse_email : $report_abuse_email;
		$title = 'mig33 chatroom abuse report from '.$source;
		$message = $message."Chatroom: ".$subjectId."\n";
		$message = $message."Chat log: ".constructChatLogSearchURL(3, $subjectId)."\n";
	} else if ($type == 4) {
        $to = isAdmin($offender) ? $report_admin_abuse_email : $report_abuse_email;
		$title = 'mig33 group chat abuse report from '.$source;
		$message = $message."Group chat: ".$subjectId."\n";
		$message = $message."Chat log: ".constructChatLogSearchURL(2, $reporter)."\n";
	} else if ($type == 5) {
        $to = $report_abuse_email;
		$title = 'mig33 group abuse report from '.$source;
		$message = $message."Group Name: ".$subject."\n";
		$message = $message."Group Id: ".$subjectId."\n";
	} else {
		return 'Unsupported report abuse type '.$type;
	}

	$message = $message."Reason: ".$reason."\n";

	mail($to, $title, $message, "From:".$reporterEmail."\r\n"."Reply-To: ".$reporterEmail);
}

function isAdmin($username) {
    try {
		$userData = soap_call_ejb('loadUserDetails', array($username));
        return $userData->chatRoomAdmin == 1;
	} catch(Exception $e) {
		return false;
	}
}

function constructChatLogSearchURL($destinationType, $destination) {
	$currentTime = gmdate('Y-m-d\TH:i:s\Z');

	$url = 'https://chat.projectgoth.com:8443/illusion/shards';

	$destinationTypeCriteria = 'type:'.$destinationType;
	$destinationCriteria = 'dest:"'.strtolower($destination).'"';
	$dateCriteria = 'date:['.$currentTime.'-5MINUTE TO '.$currentTime.']';

	$searchQuery = urlencode($destinationTypeCriteria.' AND '.$destinationCriteria.' AND '.$dateCriteria);

	return $url.'?start=0&rows=1000&q='.$searchQuery;
}

### Google Analaytics Code (Defaults to wap)
function googleAnalyticsGetImageUrl($where = View::WAP, $ga_id = '') {
	global $server_root, $ga_account;

	$img_ga_account = str_replace('UA', 'MO', $ga_account);
	$THIS_GA_ACCOUNT = $ga_id;

	if(empty($THIS_GA_ACCOUNT))
	{
	 	switch ($where)
	 	{
	 	 	case 'devlab':
	 	 		$THIS_GA_ACCOUNT = "MO-15340342-1"; // devlab Google Analytics Account
	 	 		break;
	 	 	default:
				$THIS_GA_ACCOUNT = $img_ga_account;
	 	 		break;
		}
	}

	if (DEBUG_MODE)
	{
		$THIS_GA_ACCOUNT = "MO-15340342-1";
	}

	$GA_PIXEL = "$server_root/sites/lib/ga.php";
	$url = "";
	$url .= $GA_PIXEL . "?";
	$url .= "utmac=" . $THIS_GA_ACCOUNT;
	$url .= "&utmn=" . rand(0, 0x7fffffff);
	$referer = empty($_SERVER["HTTP_REFERER"])?'-':$_SERVER["HTTP_REFERER"];
	$query = $_SERVER["QUERY_STRING"];
	$path = $_SERVER["REQUEST_URI"];

	$url .= "&utmr=" . urlencode($referer);
	if (!empty($path)) {
		$url .= "&utmp=" . urlencode($path);
	}
	$url .= "&guid=ON";

	if($where == 'midlet') {
		return $url;
	}
	return str_replace("&", "&amp;", $url);
}

/*
netmask('192.168.6.255', 8)  will return 192.0.0.0
netmask('192.168.6.255', 16) will return 192.168.0.0
netmask('192.168.6.255', 24) will return 192.168.6.0
*/
function netmask($ip, $cidr) {
    $bitmask = $cidr == 0 ? 0 : 0xffffffff << (32 - $cidr);
    return long2ip(ip2long($ip) & $bitmask);
}

#-------------------------- FUNCTIONS END --------------------------#
?>
