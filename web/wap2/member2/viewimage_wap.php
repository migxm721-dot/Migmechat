<?php
include_once("./common-inc-kk.php");
include_once("./emit.php");
include_once("./check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(getProgFile($prog));

//Try to get thumbnail for the image node provided
$nid = $_GET['nid'];
$filename = $_GET['filename'];

//Check if the client is web or midlet

$headers = apache_request_headers();
$mig33header = $headers['mig33'];

//Page content for wap

$w = '80';
$h = '80';

$docprefix = $_SESSION['DOCPREFIX'];

function bigImageOption() {

	global $nid, $prog;
	 if ($_GET['image'] != 'big') {
		echo "<small><center><a href=\"viewimage_wap.php?image=big&amp;nid=$nid\">View Larger Image</a></center></small><br/><br/>";
	}
	return;
}

if ($_GET['image'] == 'big')
{
	$w = '160';
	$h = '160';
}
emitHeader();
emitTitle("View Image");
?>
		<center><img alt="image" src="<?=$mogileFSImagePath?>/<?=$nid?>?w=<?=$w?>&amp;h=<?=$h?>&amp;a=1" /></center><br/><br/>
		<?php bigImageOption(); ?>
		<small><a href="<?=$prog?>?cmd=home">Home</a></small><br/>
	</body>
</html>
