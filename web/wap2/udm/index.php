<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

//include_once("../member2/emit.php");
//include_once("../member2/check.php");

//$pf = $_GET['pf'];

//emitHeader_min();
//emitTitle("Welcome");
emitHeader("Welcome");
?>
<div id="content">
	<div class="section">
	<p>
		<img src="http://img.mig33.com/232c3a245d7e44bfa16c9103f48682a3.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

		Thanks for your support of the UDM
	</p>

	<p>After you have registered and downloaded mig33, you will be able to get involved.</p>
	<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap">Next</a></p>
	</div>
	<?php emitFooter() ?>
</div>
<?php emitFooter_end(); ?>