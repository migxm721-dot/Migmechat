<?php
  // only allow CLI access - die a painful horrible death otherwise
  if (!defined('STDIN') || function_exists("apache_request_headers"))
  {
     echo "Access Denied\n";
     die();
  }

  $app_base_dir=dirname(dirname(__FILE__))."/";
  $test_base_dir=dirname(dirname(__FILE__))."/tests/";
  set_include_path(get_include_path() . PATH_SEPARATOR . $app_base_dir . PATH_SEPARATOR . $test_base_dir);

  // this is to trick the PHP framework
  $_SERVER = array();
  $_SERVER["DOCUMENT_ROOT"] = $app_base_dir;
  $_SERVER["REQUEST_TIME"] = time();

  if (!function_exists("apache_request_headers")) {
     function apache_request_headers()
	 {
		 return array();
	 }
  }

  // common includes go here
  require_once("common/common-inc.php");
  require_once("sites/common/utilities.php");

$db_settings = array(
"master"=>array("host"=>"localhost",
	"username"=>"fusion",
	"password"=>"abalone5KG",
	"database"=>"fusion_test"),
"slave"=>array("host"=>"localhost",
	"username"=>"fusion",
	"password"=>"abalone5KG",
	"database"=>"fusion_test"),
);

$memcache_servers = array(
	'common'  => array("localhost"=>11221),
	'captcha' => array("localhost"=>11221)
);
?>
