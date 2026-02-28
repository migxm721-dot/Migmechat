<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();

$pf = $_GET['pf'];
$br = lineBreak(true);

$info = <<<END
<p>You can recharge your mig33 account via Credit Card. We currently accept only Visa and Mastercard.</p>$br
<p>The maximum amount that you can purchase each time is AUD$50. When you recharge, you will see the amount in your local currency, based on an estimated exchange rate.</p>$br
END;

// output HTML
emitHeader();
if (isPagelet()) {
	emitTitleWithBody("Credit Card Help");
	echo $info;
} else {
	emitTitleWithBody("Credit Card Help", "help");
	?>
    <div id="content">
    <?php echo $info; ?>
    </div>
<?php
}
$backLink = getMCWapPath()."recharge_cc.php?pf=".$pf;
emitFooter($backLink, "", "merchant", "");
?>
</body>
</html>

<?php flushOutputBuffer(); ?>
