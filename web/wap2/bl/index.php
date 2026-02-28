<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('Banglalink');
?>
	<div id="content">
		<div class="section">
			<p><img src="bl.gif" hspace="2" vspace="2" width="51" height="67" align="left" />Welcome to the BanglaLink cricket FanZone on mig33, with live ball-by-ball commentary on your phone. It's easy, fun and it's free!</p>
			<br>
			<p><a href="mig33v4.jad">Download BL-mig33 now</a></p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Download other versions of mig33</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>
