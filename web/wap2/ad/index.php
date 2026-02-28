<?= '<?xml version="1.0"?>' ?>
<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('Welcome to mig33');
?>
	<div id="content">
		<div class="section">
			<p><b>mig33 is the Most Popular Mobile Social Network in India!</b></p>
			<br/>

			<p>mig33 allows <b>Free Instant Messaging, SMSes, Photo Sharing, Intl calling.</b> What's more...<br/>
			Get access to Better CHAT &amp; STAY connected with your FRIENDS on MSN, Yahoo &amp; GTalk at the same time!</p>
			<br/>

			<p><b><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&from=add">Download</a> to get FREE Mobile Content worth Rs. 20/-!!!</b>
			<ul>
				<li>Latest Ringtones &amp; Wallpapers</li>
				<li>Smileys &amp; Pictures</li>
				<li>Kicking your friends in chatrooms</li>
				<li>International calls &amp; more...</li>
			</ul>
			</p>
			<br/>

			<p>There would be more served once you join!<br/>
			<b><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&from=add">Download mig33 NOW</a>,<br/>
			Register &amp; you're a member in minutes!</p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>