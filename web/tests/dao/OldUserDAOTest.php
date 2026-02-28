<?php
fast_require('UserDAO', get_dao_directory() . '/user_dao.php');

/**
 * @group dao
 */
class OldUserDAOTest extends PHPUnit_Framework_TestCase
{

	private $key = null;

	public function setUp()
	{
	}

	public function tearDown()
	{
	}

	public function testDAOCreation()
	{
		$dao = new UserDAO();
		$this->assertTrue($dao instanceof UserDAO);
	}

	/**
	 * @depends testDAOCreation
	 */
	public function testIsValidAlias()
	{
		$dao = new UserDAO();
		$this->assertEquals(1, (int) $dao->is_valid_alias(2, 'gamerz84'));
		$this->assertEquals(1, (int) $dao->is_valid_alias_via_rest(2, 'gamerz84'));
		$this->assertEquals(1, (int) $dao->is_valid_alias(2, 'lesterchan'));
		$this->assertEquals(0, (int) $dao->is_valid_alias_via_rest(2, 'lesterchan'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'c.'));
		$this->assertEquals(1, (int) $dao->is_valid_alias(2, 'c_c_c_'));
		$this->assertEquals(1, (int) $dao->is_valid_alias(2, 'c.cc.c'));
		$this->assertEquals(1, (int) $dao->is_valid_alias(2, 'c._c._'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'c..c..'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, '0es0es'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'les..chan'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'l.e.s.t.e.r.c.h.a.n.'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'l.e.s.t.e.r.c.h.a.n.l.e.s.t.e.r.c.h.a.n.l.e.s.t.e.r.c.h.a.n'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'les..ch!an'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(2, 'x...............................'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(258, 'saj.....'));
		$this->assertEquals(0, (int) $dao->is_valid_alias(258, 'saj1'));
		$this->assertEquals(1, (int) $dao->is_valid_alias(258, 'saj111'));
		$this->assertEquals(1, (int) $dao->is_valid_alias(428, 'batm4n'));
		$this->assertEquals(1, (int) $dao->is_valid_alias_via_rest(428, 'batm4n'));
	}

	/**
	 * @depends testDAOCreation
	 */
	public function testGetUserAlias()
	{
		$dao = new UserDAO();
		$result = $dao->get_user_alias(195713790);
		$this->assertEquals('philly01', $result);
	}

	/**
	 * @depends testDAOCreation
	 */
	public function testGetUserDetail()
	{
		$dao = new UserDAO();
		$this->assertObjectHasAttribute('username', $dao->get_user_detail('lesterchan'));
		$this->assertObjectHasAttribute('userID', $dao->get_user_detail('lesterchan'));
	}

}

?>