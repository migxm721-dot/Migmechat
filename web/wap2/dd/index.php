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
				<img src="http://img.mig33.com/5a3c0b2e776f46bb8612687c1667da77.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

			Be a Trailblazer, join the DareDevils in their revelry and like-minded D-cuber's in following your favorite teams path to absolute glory.

			</p>

			<p><a href="mig33v4.jad">Download mig33 v4 Cricket Edition</a></p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Download other versions of mig33</a></p>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>


