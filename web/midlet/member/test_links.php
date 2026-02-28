<?php
include_once("../../common/common-inc.php");
include_once("../../common/pageletFunctions.php");
include_once("../../common/user_level.php");

ice_check_session();
$sessUser = ice_get_username();

if(isset($_GET['username'])){
	$username = strip_tags($_GET['username']);
}
?>

<html>
  <head>
    <title>Prototypes</title>
  </head>
  <body bgcolor="white">

<?php
	if( UserLevel::can_access($sessUser, 2) )
  	{
		printf('<p><a href="%s/midlet/member/indosat_test.php">Indosat Tryout</a></p>', $server_root);
	}
?>
	<p><a href="<?=$server_root?>/midlet/member/test.php">Test Set Display Picture</a></p>

  </body>
</html>