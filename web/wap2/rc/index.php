<?php
/*include_once("../../common/common-inc.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");

$pf = $_GET['pf'];

emitHeader_min();
emitTitle("Welcome");*/
?>
<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

emitHeader("Welcome");
?>
<div id="content">
	<div class="section">
<p>
	<img src="http://img.mig33.com/fe5f491bb92b4647b9d96cf0eeb9b83c.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

Welcome to the Royal Challengers Official Fan Group on mig33. Join and download to chat live with RC players, cheerleaders and win lots and lots of prizes including team merchandise!
</p>

<p><a href="mig33v4.jad">Download mig33 v4 Cricket Edition</a></p>
<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&v=wap&a=download">Download other versions of mig33</a></p>
	</div>
	<?php emitFooter() ?>
</div>
<?php emitFooter_end(); ?>