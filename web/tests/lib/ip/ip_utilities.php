<?php
fast_require('IPv4Utilities', get_library_directory() . '/ip/ip_utilities.php');

/**
 * @group lib
 */
class IPv4UtilitiesTest extends PHPUnit_Framework_TestCase
{
	private $whitelist = array(
		  '127.0.0.1'     => 2130706433       // localhost
		, '10.3.1.0/24'   => array(656129, 8) // SJC
		, '10.3.3.0/24'   => array(656131, 8) // SJC
		, '10.3.4.0/24'   => array(656132, 8) // SJC
		, '211.25.207.10' => 3541683978       // KL office //-753283318
		, '211.25.207.14' => 3541683982       // KL Office //-753283314
		, '61.8.225.97'   => 1023992161       // SG New Office (3Mbps)
		, '61.8.217.97'   => 1023990113       // SG Office (10Mbps)
		, '42.61.25.58'   => 708647226        // SG Office (SingTel 10Mbps)
	);
	private $test_ips = array(
		  '127.0.0.1'     => true
		, '10.3.1.0'      => true
		, '10.3.1.65'     => true
		, '10.3.4.128'    => true
		, '10.3.4.255'    => true
		, '211.25.207.10' => true
		, '211.25.207.14' => true
		, '61.8.225.97'   => true
		, '61.8.217.97'   => true
		, '42.61.25.58'   => true
		, '1.2.3.4'       => false
		, '127.0.0.2'     => false
		, '49.128.60.68'  => false
	);
	private $preparedWhitelist = array();

	public function setUp()
	{
		$this->preparedWhitelist = IPv4Utilities::prepareWhitelist(array_keys($this->whitelist));
	}

	public function tearDown()
	{
		$this->preparedWhitelist = array();
	}

	/**
	 * @use IPv4Utilities::prepareWhitelist
	 */
	public function testPrepareWhitelist()
	{
		foreach($this->whitelist as $whitelisted_ip => $expected_result)
		{
			$this->assertEquals(
				  array($expected_result)
				, IPv4Utilities::prepareWhitelist(array($whitelisted_ip))
				, 'Failure while attempting to prepare whitelite IP: ' . $whitelisted_ip
			);
		}
	}

	/**
	 * @use IPv4Utilities::isIPinWhitelist
	 * @depends testPrepareWhitelist
	 */
	public function testIsIPinWhitelist()
	{
		foreach($this->test_ips as $ip => $expected_result)
		{
			$this->assertEquals(
				  $expected_result
				, IPv4Utilities::isIPinWhitelist($ip, $this->preparedWhitelist)
				, $ip . ' is not in internal IP whitelist range'
			);
		}
	}
}
