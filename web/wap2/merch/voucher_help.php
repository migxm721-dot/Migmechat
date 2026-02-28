<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

emitHeader();
emitTitle("Voucher Help");
?>
		<small>You can recharge your mig33 account using mig33 vouchers. You can purchase these vouchers from an affiliate in your city. For an affiliate in your city, email customer service at contact@mig33.com.</small><br/>
		<br/>
		<small><a href="recharge_voucher.php">Back to Voucher</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>