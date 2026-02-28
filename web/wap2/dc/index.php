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
				<img src="http://img.mig33.com/5f68023dd5fa4e2aac8c1a08c0c983f1.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

				Don't take this warning lightly. If you are a true blooded bull fan then sign on and join the stampede in overtaking the other herd. Make your presence felt.
			</p>
			<p><a href="mig33v4.jad">Download migme v4 Cricket Edition</a></p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Download other versions of migme</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>

