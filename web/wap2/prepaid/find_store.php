<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");
include_once("../../includes/resellers.php");
emitHeader("Find a Store");
?>
	<div id="content">
		<div class="section">
<?php
if (empty($_GET['state'])) {
		showResellerStates(204, "WAP", false);
?>
		<br/>
		<a href="index.php">Back</a><br/>
<?php
}else {
		showResellersInState(204, "WAP", $_GET['state'], false);
?>
		<br/>
		<a href="find_store.php">Back</a><br/>
<?php
}
?>
		</div>
<?php emitFooter(); ?>
	</div>
<?php emitFooter_end(); ?>
