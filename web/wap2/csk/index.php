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
				<img src="http://img.mig33.com/e93db33ecc1842c6b10d9d0c7b6a7a09.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

			Walk through the lives of your favourite Super King on the mig33 Chennai Super Kings group. Sign on now, and become royalty!
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

