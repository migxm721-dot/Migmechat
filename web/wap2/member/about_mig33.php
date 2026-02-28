<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

ice_check_session();

emitHeader();
emitTitle("About mig33");

?>
<br/>
		<small>mig33 is the first global mobile community, integrating the most popular internet applications
		together for anyone with a mobile phone.</small><br/><br/>

		<small>Founded in December 2005, mig33 has quickly spread around the world, growing to millions of
		users in over 200 countries.</small><br/><br/>


		<small>mig33 is currently headquartered in Burlingame, California, USA
		and is funded by private investors and venture capital firms.</small><br/><br/>


		<small><a href="merchant_center.php">Back</a></small><br/>

		<small><a href="center_help.php">Help</a></small><br/>
		<br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
		<br/>
	</body>
</html>
