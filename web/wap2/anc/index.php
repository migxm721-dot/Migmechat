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
				<img src="http://img.mig33.com/8d0273a4b10048f2aaaba72181263f62.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

				Thanks for your support of the ANC
			</p>

			<p>After you have registered and downloaded mig33, you will be able to get involved.</p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&m=<?= $_GET['m'] ?>&g=1">Next</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>