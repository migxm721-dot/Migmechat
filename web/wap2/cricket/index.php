<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('Welcome');
?>
	<div id="content">
		<div class="section">
			<p>
				<img src="http://img.mig33.com/a42d25a1088844b6985a32679713b831.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

			Become a member of mig33 and join the Cricket FanZone with with live ball-by-ball commentary on your phone. It's easy, fun and it's free!

			</p>

			<p>Plus, use mig33 to make cheap international phone calls, send SMS and chat with friends around the world.</p>
			<p><a href="../v4_00Cricket/mig33v4.jad">Download mig33 v4 Cricket Edition</a></p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Download other versions of mig33</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>