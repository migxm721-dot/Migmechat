<?php
fast_require("OAuthDAO", get_dao_directory() . "/oauth_dao.php");
fast_require("OAuthStoreMySQLi", get_library_directory() . "/oauth/store/OAuthStoreMySQLi.php");

/**
 * @group dao
 */
class OAuthDAOTest extends PHPUnit_Framework_TestCase
{

   private $key = null;

   public function setUp(){ }
   public function tearDown(){ }

   public function testDAOCreation()
   {
       $dao = new OAuthDAO();
       $this->assertTrue($dao instanceof OAuthDAO);
   }

   /**
   * @depends testDAOCreation
   */
   public function testGetOAuthStore()
   {
      $dao = new OAuthDAO();
      $this->assertTrue($dao->getOAuthStore() instanceof OAuthStoreMySQLi);
   }

   /**
   * @depends testDAOCreation
   */
   public function testConsumerKeyOperations()
   {
       $dao = new OAuthDAO();

       // should return null when any of the parameters are null
       $this->assertTrue(is_null($dao->create_consumer_key(null, "blah", "blah")));
       $this->assertTrue(is_null($dao->create_consumer_key(123, null, "blah")));
       $this->assertTrue(is_null($dao->create_consumer_key(123, "blah", null)));

       // should return null when owner_id is not an integer
       $this->assertTrue(is_null($dao->create_consumer_key("blah", "blah", "blah")));


       // should be able to create multiple keys with same id, email, name
       $k1 = $dao->create_consumer_key(123456, "foofooemail", "foofooname");
       $k2 = $dao->create_consumer_key(123456, "foofooemail", "foofooname");

       $this->assertTrue(is_array($k1) && is_array($k2));
       $this->assertArrayHasKey('consumer_key',$k1);
       $this->assertArrayHasKey('consumer_secret',$k1);

       $this->assertArrayHasKey('consumer_key',$k2);
       $this->assertArrayHasKey('consumer_secret',$k2);

       $k1_record = $dao->lookup_consumer($k1['consumer_key']);
       $k2_record = $dao->lookup_consumer($k2['consumer_key']);

       $this->assertTrue(is_array($k1_record) && is_array($k2_record));
       $this->assertArrayHasKey('consumer_key',$k1_record);
       $this->assertArrayHasKey('consumer_secret',$k1_record);

       $this->assertArrayHasKey('consumer_key',$k2_record);
       $this->assertArrayHasKey('consumer_secret',$k2_record);

       // delete keys when done
       $dao->delete_consumer_key($k1['consumer_key'],123456);
       $dao->delete_consumer_key($k2['consumer_key'],123456);

       try
       {
         $dao->consumer_key_exists($k2['consumer_key'],123456);
       }
       catch (OAuthException2 $e) { return; }

       $this->fail("OAuthException2 exception was expected, but not thrown.");
   }

}


?>
