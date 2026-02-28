<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();

$goto = $_GET['goto'];
$pf = $_GET['pf'];
$brPageletOnly = lineBreak(true);
$br = lineBreak();
$ps = paramSeparator();
$MCWapPath = getMCWapPath();

$info = <<<END
	<p><b>What does the form require?</b>$br
	You enter the mig33's <a href="{$MCWapPath}recharge_tt.php?goto=details{$ps}pf={$pf}">bank account details</a>.</p>$brPageletOnly
	<p><b>Are there fees?</b>$br
	Ask your bank for amount. mig33 does not charge fees.</p>$brPageletOnly
	<p><b>What happens if my account is not credited?</b>$br
	Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</p>$brPageletOnly
	<p><b>Do I need my own bank account?</b>$br
	No. Most banks accept cash.</p>$brPageletOnly
END;

// output HTML
emitHeader();
if (isPagelet()) {
	emitTitleWithBody("TT Help");
	echo $info;
} else {
	emitTitleWithBody("TT Help", "help");
	?>
    <div id="content">
    <?php echo $info; ?>
    </div>
<?php
}
if($goto == ''){
	$backLink = getMCWapPath()."recharge_tt.php?pf=".$pf;
}else if($goto == 'details'){
	$backLink = getMCWapPath()."recharge_tt.php?goto=details&amp;pf=".$pf;
}else if($goto == 'notify'){
	$backLink = getMCWapPath()."recharge_tt.php?goto=notify&amp;pf=".$pf;
}
emitFooter($backLink, "", "merchant", "");
?>
</body>
</html>
<?php flushOutputBuffer(); ?>
