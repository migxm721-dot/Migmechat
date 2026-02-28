<?php



$merchantLink = '/merch/index.php';
//If a merchant is logged in change the link to point to merchant center
if (($_SESSION['user']) && ($_SESSION['user']['type'] != 'MIG_33'))
	$merchantLink = '/merch/center.php';
?>
<br clear="all">
<?php
$ds = '-over';
$lisub = 'active';
$asub = 'current';
/*
	echo ($navdown == 1) ? $ds: '';
	echo ($subnavdown == 1) ? $lisub : '';
	echo ($subnavdown == 1) ? $asub : '';
*/
?>
<div id="backdiv" style="background-color:transparent; width:900px; padding:0px 0px 0px 0px; margin-bottom:-20px">
<div id="navcontainer" style="position:relative;">
<ul id="navlist">
	<li><a href="/wim/index.php" id=""><img name="whatismig33" src="/images/nav/whatismig33<?php echo ($navdown == 1) ? $ds: ''; ?>.gif" width="148" height="30" border="0" alt="what is migme?" id="whatismig33" onmouseover="swapImage('whatismig33','','/images/nav/whatismig33-over.gif',1)" onmouseout="swapImgRestore()"></a></li>
	<li><a href="/join/index.php" id=""><img name="jointoday" src="/images/nav/jointoday<?php echo ($navdown == 2) ? $ds: ''; ?>.gif" width="137" height="30" border="0" alt="join today" onmouseover="swapImage('jointoday','','/images/nav/jointoday-over.gif',1)" onmouseout="swapImgRestore()" onmouseout="swapImgRestore()"></a></li>
	<li><a href="<?=$merchantLink?>"><img name="merchants" src="/images/nav/merchants<?php echo ($navdown == 3) ? $ds: ''; ?>.gif" width="133" height="30" border="0" alt="merchants" onmouseover="swapImage('merchants','','/images/nav/merchants-over.gif',1)" onmouseout="swapImgRestore()" onmouseout="swapImgRestore()"></a></li>
	<li><a href="http://info.mig33.com/support"><img name="help" src="/images/nav/help<?php echo ($navdown == 4) ? $ds: ''; ?>.gif" width="83" height="30" border="0" alt="help" onmouseover="swapImage('help','','/images/nav/help-over.gif',1)" onmouseout="swapImgRestore()"></a></li>
	<li><a href="/about_mig33/index.php"><img name="about" src="/images/nav/about<?php echo ($navdown == 5) ? $ds: ''; ?>.gif" width="148" height="30" border="0" alt="about migme" onmouseover="swapImage('about','','/images/nav/about-over.gif',1)" onmouseout="swapImgRestore()"></a></li>
</ul>
</div>