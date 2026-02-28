<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

emitHeader();
emitTitle("Selling Credits Help");

$step = $_GET['step'];

?>
		<small>this is sc_help.php Need content.</small><br/>
		<br/>
		<small><a href="sell_credits.php?step=<?=$step?>">Back to Selling Credits</a></small><br/>
		<br/>
	</body>
</html>