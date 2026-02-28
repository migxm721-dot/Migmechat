<?php
include_once("../../common/common-inc.php");
//include_once("../member2/emit.php");
//include_once("../member2/check.php");

//$pf = $_GET['pf'];

//emitHeader_min();
//emitTitle("Welcome");
include_once("wap_includes/wap_functions.php");

emitHeader("Terms and Conditions");
?>
<div id="content">
	<div class="section">
		<p>
		<img src="http://img.mig33.com/8e8d84e0c3184a6fa79533c25116616b.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />
		The champions beckon you to join them as they fight to hold their fort and hold claim to what is rightfully theirs!!
		</p>

		<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap">Next</a></p>
	</div>
	<?php emitFooter() ?>
</div>
<?php emitFooter_end(); ?>