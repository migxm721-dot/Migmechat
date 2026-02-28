<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

emitHeader();
emitTitle("Free Advertising Help");

$step = $_GET['step'];

if(isset($_GET['contactName'])){
	$contactName = $_GET['contactName'];
}

if(isset($_GET['contactNumber'])){
	$contactNumber = $_GET['contactNumber'];
}

if(isset($_GET['contactBy'])){
	$contactBy = $_GET['contactBy'];
}

if(isset($_GET['email'])){
	$email = $_GET['email'];
}

if(isset($_GET['location'])){
	$location = $_GET['location'];
}

if(isset($_GET['location1'])){
	$location1 = $_GET['location1'];
}

if(isset($_GET['location2'])){
	$location2 = $_GET['location2'];
}

if(isset($_GET['min_reload'])){
	$min_reload = $_GET['min_reload'];
}


//Construct URL back link
$url = 'contactName='.$contactName.'&amp;contactNumber='.$contactNumber.'&amp;contactBy='.$contactBy.'&amp;email='.$email.'&amp;location='.$location.'&amp;location1='.$location1.'&amp;location2='.$location2.'&amp;min_reload='.$min_reload;

//Strip blank space
$url = ereg_replace(' ', '%20', $url);

?>
		<small>this is advertise_help.php Need content.</small><br/>
		<br/>
		<small><a href="advertise.php?<?=$url?>&amp;step=<?=$step?>">Back to Free Advertising</a></small><br/>
		<br/>
	</body>
</html>