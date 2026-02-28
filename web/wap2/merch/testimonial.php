<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
include_once("../../includes/testimonial.php");
putenv("pagelet=true");

session_start();
$index == '';
if($_GET['index']){
	$index = $_GET['index'];
}

//if user is still logged in, go to merchant home page
if ( (isset($_SESSION['user'])) &&  (!$_SESSION['user']['type'] != 'MIG_33') ){
	header('Location: ' . $actualPath . '/wap2/merch/merchant_center.php?pf='.$pf);
	die();
}else if($index == ''){
	emitHeader();
	emitTitle("Merchant Testimonials");
	showTestimonials_main("WAP");
?>
		<p><small><a href="merchant.php">Back</a></small></p>
	</body>
</html>
<?php
} else {
	emitHeader();
	emitTitle("Merchant Testimonials");
	showTestimonials_pages("WAP", $index);
?>
	</body>
</html>
<?php
}
?>