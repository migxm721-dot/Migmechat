<?php
require_once "PHPUnit/Extensions/Database/TestCase.php";
fast_require('AvatarDAO', get_dao_directory() . '/avatar_dao.php');

class MockDAO extends DAO
{
	public function get_static_userids()
	{
		return parent::$userids;
	}
	public function get_static_usernames()
	{
		return parent::$usernames;
	}
	public function user_can_view_wall($username_viewing, $username_being_viewed)
	{
		return parent::user_can_view_wall($username_viewing, $username_being_viewed);
	}
	public function user_is_member_of_group($username, $group_id)
	{
		return parent::user_is_member_of_group($username, $group_id);
	}
	public function auto_bind_params($stmt, $params_arr)
	{
		return parent::auto_bind_params($stmt, $params_arr);
	}
}

class DAOTest extends PHPUnit_Extensions_Database_TestCase
{
	/**
     * @var MockDAO
     */
    protected $object;

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
		$ds1 = $this->createMySQLXMLDataSet($test_base_dir.'/fixtures/frame252.xml');
		$compositeDs = new PHPUnit_Extensions_Database_DataSet_CompositeDataSet(array($ds1));
        return $compositeDs;
    }

    protected function setUp()
    {
		parent::setUp();
		Memcached::get_instance()->flush();
        $this->object = new MockDAO;
    }

	public function testUser_can_view_wall()
	{
		$dao = new MockDAO;
		$result = $dao->user_can_view_wall('cherrymoo', 'lesterchan');
		$this->assertFalse($result, "Cannot view lesterchan's wall");
		$result = $dao->user_can_view_wall('cherrymoo', 'sajnikanth');
		$this->assertFalse($result, "Cannot view sajnikanth's wall");
		$result = $dao->user_can_view_wall('cherrymoo', 'joker1');
		$this->assertTrue($result, "Can view joker1's wall");
	}
	
	public function testUsers_are_friends()
	{
		$result = $this->object->users_are_friends('cherrymoo', 'cherrymoo');
		$this->assertTrue($result, "Same user");
		$result = $this->object->users_are_friends('cherrymoo', 'lesterchan');
		$this->assertFalse($result, "Not a friend");
		$result = $this->object->users_are_friends('cherrymoo', 'joker1');
		$this->assertTrue($result, "Is a friend");
	}

	public function validUsers()
	{
		return array(
			  array("cherrymoo", 2160)
			, array("lesterchan", 1)
			, array("infn8loop", 9)
		);
	}

	/**
	 * @dataProvider validUsers
	 */
	public function testGetUserId($username, $userid)
	{
		$result = $this->object->get_userid($username);
		$this->assertTrue(is_numeric($result));
		$this->assertEquals($result, $userid);
		$this->assertEquals($result, $this->object->get_userid($username));
		$userids = $this->object->get_static_userids();
		$this->assertEquals($result, $userids[$username]);
	}

	/**
	 * @dataProvider validUsers
	 */
	public function testGetUserName($username, $userid)
	{
		$result = $this->object->get_username($userid);
		$this->assertEquals($result, $username);
		$this->assertEquals($result, $this->object->get_username($userid));
		$usernames = $this->object->get_static_usernames();
		$this->assertEquals($result, $usernames[$userid]);
	}

	public function testUser_is_member_of_group()
	{
		$group_id = 79;
		$username = "cherrymoo";
		$result = $this->object->user_is_member_of_group($username, $group_id);
		$this->assertTrue($result);

		$username = "joker2";
		$result = $this->object->user_is_member_of_group($username, $group_id);
		$this->assertTrue($result);

		$username = "lesterchan";
		$result = $this->object->user_is_member_of_group($username, $group_id);
		$this->assertFalse($result);
	}

	public function testAuto_bind_params()
	{
		$var1 = 10;
		$var2 = 20;
		$var3 = 'hello';
		$params_arr = array(
				  array('i' => $var1)
				, array('i' => $var2)
				, array('s' => $var3)
			);
		$m = $this->getMockBuilder('mysqli_stmt');
		$m->disableOriginalConstructor();
		$m->setMethods(array('bind_param'));
		$stmt = $m->getMock();

		$stmt->expects($this->once())
				->method('bind_param')
				->with($this->equalTo('iis')
					,  $this->equalTo(10)
					,  $this->equalTo(20)
					,  $this->equalTo('hello')
				);
		$this->object->auto_bind_params($stmt, $params_arr);
	}
}
?>
