<?php
include_once("../member2/common-inc-kk.php");
//include_once("../../common/common-inc.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
include_once("../../includes/testimonial.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(('../member2/'.getProgFile($prog)));

$index == '';
if($_GET['index']){
	$index = $_GET['index'];
}

//Check async messages
checkServerSessionStatus();

ice_check_session();
$userDetails = ice_get_userdata();

emitHeader();

if($index == ''){
	emitTitle("Merchant Testimonials");
	showTestimonials_main("WAP");
?>
		<p><small><a href="../merch/merchant.php">Back</a></small></p>
	</body>
</html>
<?php
} else {
	emitTitle("Merchant Testimonials");
	showTestimonials_pages("WAP", $index);
?>
	</body>
</html>
<?php
}
?>