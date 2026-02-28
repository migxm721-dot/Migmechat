<div id="subnavcontainer">
<ul id="subnavlist">
<!-- login required -->
<?php if (!$_SESSION['user']['type'] != 'MIG_33') { ?>
	<li id="<?php echo ($subnavdown == 4) ? $lisub : ''; ?>"><a href="/merch/center.php" id="<?php echo ($subnavdown == 4) ? $asub : ''; ?>"><strong>merchant center</strong></a></li>
	<li id="<?php echo ($subnavdown == 7) ? $lisub : ''; ?>"><a href="/wim/rate_form.php?merch2=yes" id="<?php echo ($subnavdown == 7) ? $asub : ''; ?>"><strong>pricing</strong></a></li>
	<li id="<?php echo ($subnavdown == 5) ? $lisub : ''; ?>"><a href="<?=$wikiPath?>/?q=node/84" id="<?php echo ($subnavdown == 5) ? $asub : ''; ?>"><strong>marketing</strong></a></li>
	<li id="<?php echo ($subnavdown == 9) ? $lisub : ''; ?>"><a href="/merch/acc_details.php" id="<?php echo ($subnavdown ==9) ? $asub : ''; ?>"><strong>account info/details</strong></a></li>
	<li id="<?php echo ($subnavdown == 10) ? $lisub : ''; ?>"><a href="/merch/logout.php" id="<?php echo ($subnavdown == 10) ? $asub : ''; ?>"><strong>logout</strong></a></li>
<?php } else { ?>
	<li id="<?php echo ($subnavdown == 1) ? $lisub : ''; ?>"><a href="/merch/index.php" id="<?php echo ($subnavdown == 1) ? $asub : ''; ?>"><strong>become a merchant</strong></a></li>
	<li id="<?php echo ($subnavdown == 3) ? $lisub : ''; ?>"><a href="/merch/register_form.php" id="<?php echo ($subnavdown == 3) ? $asub : ''; ?>"><strong>register now</strong></a></li>
	<li id="<?php echo ($subnavdown == 2) ? $lisub : ''; ?>"><a href="/wim/rate_form.php?merch=yes" id="<?php echo ($subnavdown == 2) ? $asub : ''; ?>"><strong>pricing</strong></a></li>
<?php } ?>
</ul>
</div>