<?php
include("includes.php");
putenv("pagelet=true");

session_start();
$page = $_GET['page'];
$pf = $_GET['pf'];

$br = lineBreak(true);
$dash = (isPagelet() ? " - " : "");
$helpLink = "";
$footerType = "nologout";
$parent = "merchant";
$MCWapPath = getMCWapPath();

if ($page == "aboutmig") {
	$pageTitle = "About mig33";
	$pageId = "about";
	$backLink = $pf == "LOGIN" ? "merchant.php" : "merchant_center.php";

	$info = <<<END
	<p>mig33 is the first global mobile community, integrating the most popular internet applications together for anyone with a mobile phone.</p>$br
	<p>Founded in December 2005, mig33 has quickly spread around the world, growing to millions of users in over 200 countries.</p>$br
	<p>mig33 is currently headquartered in Burlingame, California, USA and is funded by private investors and venture capital firms.</p>
END;

} else if ($page == "aboutmc") {
	$pageTitle = "About Merchant Program";
	$pageId = "about";
	$backLink = $pf == "LOGIN" ? "merchant.php" : "merchant_center.php";

	$info = <<<END
	<p>Merchants are users who buy credits in bulk through one of our many international payment options to resell / transfer them to other users.</p>$br
	<p>mig33 has merchants all over the world. Many are students, mobile phone resellers, community leaders and businessmen... people like you. They use the service to make money and help mig33 grow or to buy credits in bulk and share amongst friends and family.</p>$br
	<p>We have merchants who have <b>started with less than USD$100 and grown to selling over USD$20,000 a month</b> within 6 months by trading mig33 credits and re-investing their profits.</p>
END;

// How much can you make
} else if ($page == 'profit'){
	$pageTitle = "How much can you make";
	$pageId = "help";
	$backLink = "merchant.php";
	$footerType = "nologout,nosell,nobuy,nohome";
	$parent = "";

	$info = <<<END
	<p>The mig33 merchant program is centered on buying and selling mig33 currency called 'mig33 credits'.</p>$br
	<p>Profits are made when you, as a merchant, buy mig33 credits at a discounted rate, and then resell them at full value to millions of mig33 users.</p>$br
	<p>You get a 30% discount rate when you buy mig33 credits from USD$100 in value. There are also discounts for first-time merchants to help you get started.</p>$br
	<p><b>Here is an example</b> - let's say you buy $500 worth of mig33 credits, and pay only $350 (30% discount). You sell them at full value, worth $500.  In this case, you have made $150 dollars profit!</p>
END;

// Selling is easy
} else if ($page == 'easysell') {
	$pageTitle = "Selling is easy";
	$pageId = "help";
	$backLink = "merchant.php";
	$footerType = "nologout,nosell,nobuy,nohome";
	$parent = "";

    $info = <<<END
	<p>Selling credits is easy, here's why:</p>$br
	<ul>
		<li>$dash Anyone with a mobile phone can be your customer.</li>$br
		<li>$dash mig33 calling rates are cheaper than calling cards, and have no hidden fees.</li>$br
		<li>$dash Merchants receive free promotion to mig33 users. You decide how to help users find you. When your customer's credit balance runs low, we send them a reminder SMS to buy more from you.</li>$br
		<li>$dash People really want it! mig33 is the ultimate in prepaid calling and online messaging services. Chat is free. Long distance calls are competitively priced and there are many different options for starting a call (SMS, WAP, Web or mobile client). People can keep in contact with friends as well as buy emoticons and soon other mobile content.</li>$br
		<li>$dash The mig33 Merchant Center provides detailed step-by-step instructions, tools and promotional material to you help you sell.</li>$br
	</ul>
END;

// mig33 feature list
} else if ($page == 'featurelist') {
	$pageTitle = "Features";
	$pageId = "help";
	$backLink = "merchant.php";
	$footerType = "nologout,nosell,nobuy,nohome";
	$parent = "";

	$info = <<<END
	<p>mig33 is a free global community that keeps you in touch with friends and family, anyway you want, all from your mobile phone.</p>
	<p><a href="../download.php">Download mig33</a></p>$br
	<ul>
		<li>$dash Chat with millions of mig33 users</li>$br
		<li>$dash Keep friends in the loop with new status updates</li>$br
		<li>$dash Make cheap calls to any phone, anywhere, anytime!</li>$br
		<li>$dash SMS friends instantly with a cheap flat-rate</li>$br
		<li>$dash Personalize with cool themes, wallpapers and ringtones</li>$br
		<li>$dash Express yourself with tons of different emoticon packs</li>$br
		<li>$dash Share photos directly with all your friends and save them online</li>$br
		<li>$dashFree credits for inviting friends to join</li>$br
	</ul>
	<p align="center">
		<a href="{$MCWapPath}info.php?page=feature&amp;q=1"><img src="../img/1_4.png" height="45" width="37"></img></a>
		<a href="{$MCWapPath}info.php?page=feature&amp;q=2"><img src="../img/2_4.png" height="45" width="37"></img></a>
		<br/>
		<a href="{$MCWapPath}info.php?page=feature&amp;q=3"><img src="../img/3_4.png" height="45" width="37"></img></a>
		<a href="{$MCWapPath}info.php?page=feature&amp;q=4"><img src="../img/4_4.png" height="45" width="37"></img></a>
	</p>
END;

// mig33 feature page - nested page off feature list page
} else if ($page == 'feature') {
	$pageTitle = "Features";
	$pageId = "help";
	$backLink = "info.php?page=featurelist";
	$footerType = "nologout,nosell,nobuy,nohome";
	$parent = "";

	$featureString;
	if ($_GET['q'] == 1) {
		$featureString = "<p>Keep your friends in the loop with what you're up to! Broadcast a short message for all your friends to see!</p>";
	} else if ($_GET['q'] == 2) {
		$featureString = "<p>Personalize your experience with new themes, wallpapers, and emoticons!</p>";
	} else if ($_GET['q'] == 3) {
		$featureString = "<p>Now available in 5 more languages: Indonesian, Russian, Hindi, Bengali, and (simplified) Chinese.</p>";
	} else if ($_GET['q'] == 4) {
		$featureString = "<p>Chat with millions of other mig33 users in our hundreds of thousands of chatrooms.</p>";
	}

	$info = <<<END
	<p align="center">
		<img src="../img/{$_GET['q']}.png" height="180" width="148" />
	</p>$br
    $featureString
END;

// free advertising
} else if ($page == 'freead') {
	$pageTitle = "Free Advertising";
	$pageId = "mc-ad";
	$backLink = $pf == 'HELP' ? 'help_mc.php?page=sell' : 'sell_credits.php';
	$helpLink = getMCWapPath()."help_mc.php";
	$footerType = "";

	$info = <<<END
	<p>mig33 can help you find more users within our community by advertising your business
	in our application.  This means that every time a mig33 user logs into mig33 on their mobile phone,
	they will see an announcement with your name and contact details and they will know to contact you
	to purchase credits.</p>$br
	<p>This way, when a user needs to find a merchant to purchase their credits,
	they will have easy access to your contact details.</p>$br
	<p><b><a href="{$MCWapPath}advertise.php?pf=SC">Apply to Advertise Now</a></b></p>
END;

// default info page
} else {
	$pageTitle = "Merchant Information";
	$pageId = "help";
	$backLink = "merchant.php";

	$info = <<<END
	<ul>
		<li>$dash <a href="{$MCWapPath}info.php?page=profit">See how much you can make</a></li>$br
		<li>$dash <a href="{$MCWapPath}info.php?page=easysell">Learn how easy it is to sell credits</a></li>$br
		<li>$dash <a href="{$MCWapPath}info.php?page=featurelist">mig33 features</a></li>$br
	</ul>
END;
}


// output HTML
emitHeader();

if (isPagelet()) {
	emitTitleWithBody($pageTitle, "");
	echo $info;
	echo $br;
} else {
	emitTitleWithBody($pageTitle, $pageId);
	?>
    <div id="content">
    <?php echo $info; ?>
    </div>
<?php
}

emitFooter(getMCWapPath().$backLink, $helpLink, $parent, $footerType);
include_once("../gs_inc.php");
?>
</body>
</html>
<?php flushOutputBuffer(); ?>

