<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");
emitHeader("Help");
?>
	<div id="content">
		<div class="section">
		<small>You need to be a registered mig33 user first. Register <a href="<?=get_server_root()?>/sites/index.php?c=registration&a=register&v=wap">here</a>.</small><br/><br/>

		<small>When you purchase a mig33 voucher/card, you can redeem it on:</small>
		<ol>
			<li>
				<small>mig33 Application</small>
				<ol type="a">
					<li><small>Log into mig33</small></li>
					<li><small>Select 'add credits'</small></li>
					<li><small>Select 'redeem a voucher'</small></li>
					<li><small>Enter the 10 digit PIN number on your voucher and submit. Your mig33 account will be credited immediately</small></li>
				</ol>
			</li>
			<li>
				<small>WAP Site</small>
				<ol type="a">
					<li><small>Log into your mig33 account on your mobile phone browser at <a href="http://wap.mig33.com/">http://wap.mig33.com</a></small></li>
					<li><small>Go to 'add credits'</small></li>
					<li><small>Select 'redeem a voucher'</small></li>
					<li><small>Enter the 10 digit PIN number on your voucher and submit.  Your mig33 account will be credited immediately</small></li>
				</ol>
			</li>
		</ol>
	</div>
<?php emitFooter_end(); ?>