<?php
fast_require('DAO', get_dao_directory() . '/dao.php');
fast_require('XCache', get_framework_common_directory() . '/xcache.php');

class CountryDAO extends DAO
{
	private static $countries = null;

	/**
	* SOAP call to get list of countries as stored in server
	* @return array List of countries
	*/
	public static function get_countries_from_server()
	{
		$countries = array();
		try
		{
			$countries_from_db = soap_call_ejb('getCountries', array());
			if (empty($countries_from_db))
			{
				throw new Exception('CountryDAO::get_countries() empty response');
			}
			foreach($countries_from_db as $country)
			{
				$countries[$country['id']] = $country;
			}
		}
		catch(Exception $e)
		{
			error_log('get_countries: ' . $e->getMessage()); //print $e->getMessage();
			throw $e;
		}
		return $countries;
	}

	/**
	 * Shortcut for get_country_from_ip(null)
	 * @return array $countryData
	 */
	public static function get_country_from_user_ip()
	{
		return self::get_country_from_ip();
	}

	/**
	 * For a certain IP detect the country it belongs to and return the relavant country data
	 * @param long $remote_ip IP Address
	 * @return array $countryData
	 * @throws Exception
	 */
	public static function get_country_from_ip($remote_ip=null)
	{
		if (empty($remote_ip))
		{
			if (function_exists('getRemoteIPAddress'))
			{
				$remote_ip = getRemoteIPAddress();
			}
			else
			{
				throw new Exception('Unable to determine country code for user.');
			}
		}
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

	/**
	 * Get Countries
	 * @return array an array of items.
	 */
	public static function get_countries()
	{
		if (! is_array(self::$countries))
		{
			try
			{
				$countries = XCache::getInstance()->get(
					XCache::KEYSPACE_COUNTRIES_HASH
					, array('CountryDAO', 'get_countries_from_server')
					, 86400
				);
			}
			catch (Exception $ex)
			{
				$countries = array();
			}
			self::$countries = $countries;
			return $countries;
		}
		return self::$countries;
	}

	/**
	* Get the country data
	* @param	string	$countryID		The country ID
	* @return	countryData in array
	*/
	public static function get_country_data($countryID)
	{
		$countries = self::get_countries();
		if (isset($countries[$countryID]))
		{
			return $countries[$countryID];
		}
		return false;
	}

	/**
	* Get the value for a country
	* @param	string	$countryID		The country ID
	* @return	string	$countryIDD		The IDD of the country
	*/
	private static function get_country_attr($countryID, $attr_name=null)
	{
		if (empty($attr_name)) return false;
		$country = self::get_country_data($countryID);
		if ($country)
		{
			if (isset($country[$attr_name]))
			{
				return $country[$attr_name];
			}
		}
		return false;
	}

	/**
	* Get the name for a country
	* @param	string	$countryID		The country ID
	* @return	string	$countryName	The full name of the country
	*/
	public static function get_country_name($countryID)
	{
		return self::get_country_attr($countryID, 'name');
	}

	/**
	* Get the idd for a country
	* @param	string	$countryID		The country ID
	* @return	string	$countryIDD		The IDD of the country
	*/
	public static function get_country_idd($countryID)
	{
		return self::get_country_attr($countryID, 'iddCode');
	}

	/**
	*Obtain the currency for a particular country
	*/
	public static function get_currency($countryID)
	{
		return self::get_country_attr($countryID, 'currency');
	}

	/**
	*Obtain the supported currency for CC payment given the local currency
	*/
	public static function get_currency_cc($countryID)
	{
		return self::get_country_attr($countryID, 'creditCardCurrency');
	}

	/**
	*Obtain the supported currency for LBD payment given the local currency
	*/
	public static function get_currency_lbd($countryID)
	{
		return self::get_country_attr($countryID, 'bankTransferCurrency');
	}

	/**
	*Obtain the supported currency for WU payment given the local currency
	*/
	public static function get_currency_wu($countryID)
	{
		return self::get_country_attr($countryID, 'westernUnionCurrency');
	}

	/**
	*Get if credit card is enabled
	*/
	public static function is_credit_card_payment_enabled($countryID)
	{
		return self::get_country_attr($countryID, 'allowCreditCard') > 0;
	}
}
?>
