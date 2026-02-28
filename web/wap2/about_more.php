<?php
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");

global $server_root;

emitHeader('Features');
?>
	<div id="content">
		<div class="section">

		<p align="center">
			<img src="img/<?=$_GET['q']?>.png" height="180" width="148"></img>
		</p>
	<?php if ($_GET['q'] == 1) { ?>
		<p>Keep your friends in the loop with what you're up to! Broadcast a short message for all your friends to see!</p><br/>
	<?php } else if ($_GET['q'] == 2) { ?>
		<p>Personalize your experience with new themes, wallpapers, and emoticons!</p><br/>
	<?php } else if ($_GET['q'] == 3) { ?>
		<p>Now available in 5 more languages: Indonesian, Russian, Hindi, Bengali, and (simplified) Chinese.</p><br/>
	<?php } else if ($_GET['q'] == 4) { ?>
		<p>Chat with millions of other mig33 users in our hundreds of thousands of chatrooms.</p><br/>
	<?php } ?>

		<p><a href="about.php">Back</a></p>
		<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=login&v=wap">Home</a></p>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>