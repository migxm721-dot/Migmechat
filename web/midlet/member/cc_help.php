<?php
require_once("../../common/common-config.php");

$pf = $_GET['pf'];
?>
<html>
  <head>
  <title>Credit and Debit Cards (Visa, Amex, Mastercard, JCB) Help</title>
  </head>
  <body bgcolor="white">
  	<p>You can recharge your mig33 account via Credit Card. We currently accept only Visa and Mastercard.</p>
  	<br>
	<p>The maximum amount that you can purchase each time is AUD$50. When you recharge, you will see the amount in your local currency, based on an estimated exchange rate.</p>
	<br>
	<p><a href="<?=$server_root?>/midlet/member/recharge_cc.php?pf=<?=$pf?>">Back to Credit and Debit Card</a> &gt;&gt;</p>
  </body>
</html>