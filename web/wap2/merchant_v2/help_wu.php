<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();

$step = 1;
$amount = '';
$lastname = '';
$pf = $_GET['pf'];

if(isset($_GET['step'])){
	$step = $_GET['step'];
}

if(isset($_GET['amount'])){
	$amount = $_GET['amount'];
}

if(isset($_GET['yourname'])){
	$yourname = $_GET['yourname'];
}

if(isset($_GET['cn'])){
	$cn = $_GET['cn'];
}
if(isset($_GET['cc'])){
	$cc = $_GET['cc'];
}
if(isset($_GET['ctry'])){
	$ctry = $_GET['ctry'];
}
if(isset($_GET['pr'])){
	$pr = $_GET['pr'];
}

$url = ereg_replace(' ', '%20','pf='.$pf.'&amp;pr='.$pr.'&amp;cn='.$cn.'&amp;cc='.$cc.'&amp;ctry='.$ctry.'&amp;amount='.$amount.'&amp;yourname='.$yourname.'&amp;step='.$step);

$brPageletOnly = lineBreak(true);
$br = lineBreak();
$MCWapPath = getMCWapPath();

$info = <<<END
	<p><b>Which form is required?</b>$br
	Use the GREEN QuickCash form. You enter the information provided when you make a request <a href="{$MCWapPath}recharge_wu.php">here</a>. Ask your Western Union agent for help or email contact@mig33.com.</p>$brPageletOnly
	<p><b>What is a Photo ID?</b>$br
	An unexpired driver's license, passport or country identity card.</p>$brPageletOnly
	<p><b>Are there fees?</b>$br
	Agent fee is usually USD$15. mig33 does not charge fees.</p>$brPageletOnly
	<p><b>What happens if my account is not credited?</b>$br
	Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</p>$brPageletOnly
	<p><b>Where can I find a Western Union Agent?</b>$br
	Email your country to contact@mig33.com. We will help you.</p>$brPageletOnly
END;

// output HTML
emitHeader();
if (isPagelet()) {
	emitTitleWithBody("Western Union FAQ");
	echo $info;
} else {
	emitTitleWithBody("Western Union FAQ", "help");
	?>
    <div id="content">
    <?php echo $info; ?>
    </div>
<?php
}
$backLink = getMCWapPath()."recharge_wu.php?".$url;
emitFooter($backLink, "", "merchant", "");
?>
</body>
</html>
<?php flushOutputBuffer(); ?>
