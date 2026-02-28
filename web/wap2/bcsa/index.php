<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('Join mig33');
?>
	<div id="content">
		<div class="section">
			<p><b>mig33 is the popular mobile social network, that everyone is talking about in South Africa.</b></p>
			<br/>

			<p><b>FREE access to:</b>
			<ul>
				<li>Instant messaging (mig33, MSN, Yahoo, Google Talk)</li>
				<li>Chat rooms</li>
				<li>Photo sharing</li>
				<li>And much more!</li>
			</ul>
			</p>
			<br/>

			<p><b><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&from=bcsad">Download</a>, register and get R 3.5 for FREE mobile downloads of:</b>
			<ul>
				<li>Pictures, Ringtones and other mig33 content</li>
				<li>Smileys, pictures and kicks for chat rooms</li>
			</ul>
			</p>
			<br/>

			<p><b>Get even more once you're a member!</b><br/>
			<b><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&from=bcsad">Download mig33 NOW</a>,<br/>
			Register &amp; you're a member in minutes!</b></p>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>
