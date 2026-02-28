<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

global $server_root;
$pf = $_GET['pf'];

emitHeader("Welcome");
?>
	<div id="content">
		<div class="section">
<p>
	<img src="http://img.mig33.com/0928fb34506941e09a7331522e4bfd8c.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

	Thanks for your support of the ID
</p>

<p>After you have registered and downloaded mig33, you will be able to get involved.</p>
<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&m=<?= $_GET['m'] ?>&g=5">Next</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>