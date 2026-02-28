<?php
require_once "PHPUnit/Extensions/Database/TestCase.php";
fast_require('CountryDAO', get_dao_directory() . '/country_dao.php');
fast_require('Memcached', get_framework_common_directory() . '/memcached.php');
/**
 * @group dao
 */
class CountryDAOTest extends PHPUnit_Extensions_Database_TestCase
{
	/**
     * @return PHPUnit_Extensions_Database_DB_IDatabaseConnection
     */
    public function getConnection()
    {
		$pdo = new PDO($GLOBALS['L_DB_DSN'], $GLOBALS['L_DB_USER'], $GLOBALS['L_DB_PWD']);
		$pdo->exec("set foreign_key_checks = 0;");
		return $this->createDefaultDBConnection($pdo, $GLOBALS['L_DB_NAME']);
    }

	/**
     * @return PHPUnit_Extensions_Database_DataSet_IDataSet
     */
    public function getDataSet()
    {
		global $test_base_dir;
		return $this->createMySQLXMLDataSet($test_base_dir.'/fixtures/country.xml');
    }

	public function setUp()
	{
		parent::setUp();
		Memcached::get_instance()->flush();
		XCache::getInstance()->delete(XCache::KEYSPACE_COUNTRIES_HASH);
	}

	public function tearDown()
	{
		$this->getConnection()->getConnection()->exec("set foreign_key_checks = 1;");;
		parent::tearDown();
	}
	
	public function testGetCountriesFromServer()
	{
		$countries = CountryDAO::get_countries_from_server();
//		print_r($countries);
		$this->assertTrue(is_array($countries));
		$this->assertTrue(!empty($countries));
	}
	
	public function testGetCountries()
	{
		$countries = CountryDAO::get_countries();
//		print_r($countries);
		$this->assertTrue(is_array($countries));
		$this->assertTrue(!empty($countries));
	}
	
	public function testGetCountryData()
	{
		$countryID = 199;
		$country_data = CountryDAO::get_country_data($countryID);
//		print_r($country_data);
		$this->assertTrue(!empty($country_data));
		
		$result = CountryDAO::get_country_name($countryID);
		$this->assertEquals($result, 'Singapore');
		
		$result = CountryDAO::get_country_idd($countryID);
		$this->assertEquals($result, '65');
		
		$result = CountryDAO::get_currency($countryID);
		$this->assertEquals($result, 'SGD');
		
		$result = CountryDAO::get_currency_cc($countryID);
		$this->assertEquals($result, 'USD');
		
		$result = CountryDAO::get_currency_lbd($countryID);
		$this->assertEquals($result, 'SGD');
		
		$result = CountryDAO::get_currency_wu($countryID);
		$this->assertEquals($result, 'SGD');
	}
	
	
}
?>
