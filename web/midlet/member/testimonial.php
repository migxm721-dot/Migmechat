<?php
session_start();
include_once("../../common/common-inc.php");
include_once("../../includes/testimonial.php");
ice_check_session();
$userDetails = ice_get_userdata();

$index == '';
if($_GET['index']){
	$index = $_GET['index'];
}

?>
<html>
  <head>
    <title>Merchant Testimonial</title>
  </head>
  <body bgcolor="white">
  	<br>
	<?php
	if($index == ''){
		showTestimonials_main("midlet");
	?>
	<br>
	<p><a href="<?=$server_root?>/midlet/member/merchant.php">Back</a></p>
	<?php
	} else {
		showTestimonials_pages("midlet", $index);
	}
	?>
 	<br>
  </body>
</html>
