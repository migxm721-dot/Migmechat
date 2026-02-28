<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");
$pf = $_GET['pf'];

emitHeader("Welcome");
?>
	<div id="content">
		<div class="section">
<p>
	<img src="http://img.mig33.com/0fa5499a261343f19ed3b8ac009c4681.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

Welcome to the Mumbai Indians Official Mobile Chat Community on mig33. Join and download to chat live with players, cheerleaders and win lots and lots of prizes including team merchandise!
</p>
<p><a href="mig33v4.jad">Download mig33 v4.2 Mumbai Indians Edition</a></p>
<p>If your download fails, please try this <a href="mig33v4.jar">link</a> instead.<br/><br/></p>
<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&g=13">Next</a></p>
		</div>
<?php emitFooter(); ?>
	</div>
<?php emitFooter_end(); ?>