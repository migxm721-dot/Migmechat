<?php
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");

$pf = $_GET['pf'];
session_start();

global $server_root;

emitHeader('Become a Merchant');
?>
	<div id="content">
		<div class="section">
			<p>By becoming a mig33 merchant, you can, like thousands of other users, make money by distributing and selling mig33 call credits to other people.</p><br/>

			<p>merchants are valuable distributors of mig33 credits as they provide other users with an easy way to pay for credits (through cash sales) to recharge their account. People who have a need to remain in contact with people overseas i.e. international students etc are perfect customers for merchants.</p><br/>

			<p>Register your interest on our website (http://www.mig33.com) and get started.</p><br/>

			<p><a href="merchant_v2/merchant.php?pf=<?=$pf?>">Read more</a></p>
			<br/>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=login&v=wap">Home</a></small>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>