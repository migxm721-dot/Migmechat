<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('mig33');
?>
	<div id="content">
		<div class="section">

			<p><img src="http://www.mig33.com/img/logo_mobile_4.gif" width="54" height="17" align="left" vspace="3"><img src="bsnl.png" width="54" height="40" align="right" vspace="3"></p>
			<br/><br>

			<p>Come join FREE the world's best mobile chat community and fun flirt with millions of Friends! mig33 also allows you to connect to your IMs (MSN, Yahoo!, GTalk), share emoticons, lovely gifts, and photos.</p>
			<br/>

			<p><a href="mig33v4.jad">Click here to download mig33 for FREE</a></p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap&pf=JOIN">What is mig33?</a></p>
			<p>For other versions (including Windows Mobile and BlackBerry), please <a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=versions&v=wap&pf=DOWNLOAD">click here</a></p>
			<p><a href="../member2/help.php?pf=JOIN">Help and more info</a></p>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>
