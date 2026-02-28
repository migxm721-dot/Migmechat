<?php
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");

$pf = $_GET['pf'];

global $server_root;

emitHeader('What is mig33');
?>
	<div id="content">
		<div class="section">

		<small>mig33 is the largest global community that brings you the power of the internet right to your mobile phone.</small><br/><br/>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=download&v=wap">Download mig33</a></small><br/>
		<ul>
			<li><small>Chat with millions of mig33 users</small></li>
			<li><small>Keep friends in the loop with new status updates</small></li>
			<li><small>Make cheap calls to any phone, anywhere, anytime!</small></li>
			<li><small>SMS friends instantly with a cheap flat-rate</small></li>
			<li><small>Personalize with cool themes, wallpapers and ringtones</small></li>
			<li><small>Express yourself with tons of different emoticon packs</small></li>
			<li><small>Share photos directly with all your friends and save them online</small></li>
			<li><small>Free credits for inviting friends to join</small></li>
		</ul>

		<small>
			<p align="center">
				<a href="about_more.php?q=1"><img src="img/1_4.png" height="45" width="37"></img></a>
				<a href="about_more.php?q=2"><img src="img/2_4.png" height="45" width="37"></img></a>
			<br/>
			</p>
			<p align="center">
				<a href="about_more.php?q=3"><img src="img/3_4.png" height="45" width="37"></img></a>
				<a href="about_more.php?q=4"><img src="img/4_4.png" height="45" width="37"></img></a>
				<br/>
			</p>
		</small>

		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap"><b>Join Now</b></a></small><br/>

		<?php
		if($pf == 'LOGIN'){
		?>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=login&v=wap">Back</a></small><br/>
		<?php
		}else if($pf == 'JOIN'){
		?>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap">Back</a></small><br/>
		<?php
		} else {
		?>
		<small><a href="<?php echo get_server_root(); ?>/sites/index.php?c=wap_portal&a=login&v=wap">Home</a></small><br/>
		<?php
		}
		?>

		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>