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
$ppid = '';
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
if(isset($_GET['ppid'])){
	$ppid = $_GET['ppid'];
}

if(isset($_GET['pr'])){
	$pr = $_GET['pr'];
}
if(isset($_GET['ah'])){
	$ah = $_GET['ah'];
}
if(isset($_GET['bn'])){
	$bn = $_GET['bn'];
}
if(isset($_GET['ban'])){
	$ban = $_GET['ban'];
}

$url = ereg_replace(' ', '%20','step='.$step.'&amp;pr='.$pr.'&amp;ah='.$ah.'&amp;bn='.$bn.'&amp;ban='.$ban.'&amp;amount='.$amount.'&amp;yourname='.$yourname.'&amp;ppid='.$ppid.'&amp;pf='.$pf);

$brPageletOnly = lineBreak(true);
$br = lineBreak();
$MCWapPath = getMCWapPath();

$info = <<<END
	<p><b>What does the form look like?</b>$br
	You enter the information provided when you make a <a href="{$MCWapPath}recharge_bt.php?ppid={$ppid}">bank deposit request</a>. Ask the bank staff for help or email contact@mig33.com.</p>$brPageletOnly
	<p><b>Are there fees?</b>$br
	It's usually free. Some banks may charge a small fee. Please ask your bank.</p>$brPageletOnly
	<p><b>What happens if my account is not credited?</b>$br
	Sometimes this can happen if we have not been sent enough information. Send your name, amount and receipt number to contact@mig33.com.</p>$brPageletOnly
	<p><b>I have my own bank account.</b>$br
	Tell your bank to transfer funds to the mig33 bank account. Just give them all the same details that we provide you.</p>$brPageletOnly
	<p><b>Do I need my own bank account?</b>$br
	No. You can go to any branch of mig33's bank and they will let you deposit cash.</p>$brPageletOnly
END;

// output HTML
emitHeader();
if (isPagelet()) {
	emitTitleWithBody("Local Bank Deposit FAQ");
	echo $info;
} else {
	emitTitleWithBody("Local Bank Deposit FAQ", "help");
	?>
    <div id="content">
    <?php echo $info; ?>
    </div>
<?php
}
$backLink = getMCWapPath()."recharge_bt.php?".$url;
emitFooter($backLink, "", "merchant", "");
?>
</body>
</html>

<?php flushOutputBuffer(); ?>
