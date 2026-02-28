<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader("Welcome");
?>
	<div id="content">
		<div class="section">
<p>
	<img src="http://img.mig33.com/41b51330ca8643bab92bda717d950e6f.jpeg?w=40&h=40&a=1&c=1" hspace="2" vspace="2" align="left" />

Follow the Lions on their path to victory! Roar with fellow LionHearts! Join the party on your mobile.

</p>

<p><a href="mig33v4.jad">Download mig33 v4.2 Cricket Edition</a></p>
<p>If your download fails, please try this <a href="mig33v4.jar">link</a> instead.<br/><br/></p>
<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Download other versions of mig33</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>