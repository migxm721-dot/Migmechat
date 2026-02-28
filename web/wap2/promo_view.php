<?php
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");
emitHeader("mig33");
?>
	<div id="content">
		<div class="section">
<?php
	if ($_GET['f'] == 'c')
		$link = 'promo_chat';
	else
		$link = 'promo_voip';
?>
	<p>
		<img src="img/<?=$link?>_<?=$_GET['q']?>.png" height="179" width="148">
	</p>
	<p><a href="<?=$link?>.php">Back</a></p>
		</div>
<?php emitFooter(); ?>
	</div>
<?php emitFooter_end(); ?>