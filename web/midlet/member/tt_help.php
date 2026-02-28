<?php
require_once("../../common/common-config.php");
$goto = $_GET['goto'];
$pf = $_GET['pf'];
?>

<html>
  <head>
    <title>TT Help</title>
  </head>
  <body bgcolor="white">
  	<p><b>What does the form require?</b></p>
	<p>You enter the information outlined <a href="<?=$server_root?>/midlet/member/recharge_tt.php?goto=details&pf=<?=$pf?>">here</a>.</p>
	<br>
	<p><b>Are there fees?</b></p>
	<p>Ask your bank for amount. mig33 does not charge fees.</p>
	<br>
	<p><b>What happens if I my account is not credited?</b></p>
	<p>Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</p>
	<br>
	<p><b>Do i need my own bank account?</b></p>
	<p>No. Most banks accept cash.</p>
	<br>
	<?php
		if($goto == ''){
			print '<p><a href="'.$server_root.'/midlet/member/recharge_tt.php?pf='.$pf.'">Back to TT</a> &gt;&gt;</p>';
		}else if($goto == 'details'){
			print '<p><a href="'.$server_root.'/midlet/member/recharge_tt.php?goto=details&pf='.$pf.'">Back to TT Details</a> &gt;&gt;</p>';
		}else if($goto == 'notify'){
			print '<p><a href="'.$server_root.'/midlet/member/recharge_tt.php?goto=notify&pf='.$pf.'">Back to TT Notification</a> &gt;&gt;</p>';
		}
	?>
	<br>
  </body>
</html>