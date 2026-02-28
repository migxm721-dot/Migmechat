<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

emitHeader();
emitTitle("Buying Credits Help");

?>
		<small>this is bc_help.php Need content.</small><br/><br/>

		<small><a href="buy_credits.php">Back to Buying Credits</a></small><br/><br/>

		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
  	</body>
</html>