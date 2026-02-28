<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$goto = $_GET['goto'];
$pf = $_GET['pf'];

emitHeader();
emitTitle("TT Help");
?>
		<small><b>What does the form require?</b></small><br/>
		<small>You enter the information outlined <a href="recharge_tt.php?goto=details&amp;pf=<?=$pf?>">here</a>.</small><br/>
		<br/>
		<small><b>Are there fees?</b></small><br/>
		<small>Ask your bank for amount. mig33 does not charge fees.</small><br/>
		<br/>
		<small><b>What happens if I my account is not credited?</b></small><br/>
		<small>Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</small><br/>
		<br/>
		<small><b>Do i need my own bank account?</b></small><br/>
		<small>No. Most banks accept cash.</small><br/>
		<br/>
		<?php
			if($goto == ''){
				print '<small><a href="recharge_tt.php?pf='.$pf.'">Back to TT</a></small><br/>';
			}else if($goto == 'details'){
				print '<small><a href="recharge_tt.php?goto=details&amp;pf='.$pf.'">Back to TT Details</a></small><br/>';
			}else if($goto == 'notify'){
				print '<small><a href="recharge_tt.php?goto=notify&amp;pf='.$pf.'">Back to TT Notification</a></small><br/>';
			}
		?>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>