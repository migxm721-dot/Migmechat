<?php
include("includes.php");
putenv("pagelet=true");

session_start();

//Check async messages
if (!isPagelet()) {
	checkServerSessionStatus();
}
ice_check_session();

$page = $_GET['page'];
$pf = $_GET['pf'];

$br = lineBreak(true); // displays <br> only if it's a pagelet
$dash = (isPagelet() ? " - " : "");
$helpLink = "";
$parent = "merchant";
$footerType = "";
$pageId = "help";
$ps = paramSeparator();
$MCWapPath = getMCWapPath();

// check if user is a merchant
$userDetails = ice_get_userdata();
if($userDetails->type == 1){
	$footerType = "nosell,nobuy";
	$parent = "";
}

if ($page == 'whatis'){
	$pageTitle = "What is the Merchant Program";
	$backLink = ($pf == 'RA' ? "merchant_center.php":"help_mc.php");

	$info = <<<END
	<p>The mig33 merchant program allows entrepreneurs like yourself to start your own business and make money.</p>$br
	<p>Make money using your mobile and selling mig33 currency called 'mig33 credits'.</p>$br
	<p>The credits allow you to pay for services such as cheap international phone calls, SMS, ringtones and wallpapers and other mig33 services.</p>$br
	<p>Buy mig33 credits at a discounted rate, and resell them to millions of other mig33 users.</p>$br
	<p>To find out more, login to the merchant center, and find all the tools that you need to
	make your merchant business a success.</p>
END;

}else if ($page == 'purch'){
	$pageTitle = "How do I purchase credit?";
	$backLink = "help_mc.php";

	$info = <<<END
	<p>There are several methods of payment that is available in your country:</p>$br
	<ul>
		<li><b>Bank Transfer</b> - transfer payment to mig33 local bank account in your country</li>$br$br
		<li><b>Western Union</b> - go to your local Western Union agent and transfer payment into mig33 Western Union account</li>$br$br
		<li><b>Telegraphic Transfer </b> - send an international payment to mig33 bank account</li>$br
	</ul>$br
	<p>Payments will take 3 - 5 days to process and we will send you an SMS as soon as your account is credited.</p>$br
	<p>It is IMPORTANT that you keep your receipt as proof of payment as we may require this to track your payment progress.</p>
END;

}else if ($page == 'sell'){
	$pageTitle = "How do I purchase credit?";
	$backLink = "help_mc.php";

	$info = <<<END
	<p>We provide you with many tools to help you get customers and sell credits to them. This includes:</p>$br
	<ul>
		<li><b><a href="{$MCWapPath}merchant_createuser.php?pf=HELP">Create Instant Account</a></b> - create an account for your customer instantly, without the need for any downloads or lengthy set-up. This is ideal for customers who do not have access to a compatible mobile phone but need to make cheap phone calls.</li>$br$br
		<li><b><a href="{$MCWapPath}invite_customers.php?pf=HELP">Invite Customer</a></b> - build your customer list by introducing your friends and family to mig33 and get bonus credits for every new person you refer and successfully signs up.</li>$br$br
		<li><b><a href="{$MCWapPath}transfer_credit.php?pf=HELP">Transfer Credit</a></b> - once you receive payment from your customer, complete your sale by simply transferring the credit from your account to their account.</li>$br$br
		<li><b><a href="{$MCWapPath}info.php?pf=HELP{$ps}page=freead">Free Advertising</a></b> - advertise your contact details to all mig33 users in your country. They can contact you to purchase more credits.</li>$br
	</ul>
END;

}else if ($page == 'profit'){
	$pageTitle = "How do I make a profit?";
	$backLink = "help_mc.php";

	$info = <<<END
	<p>The mig33 merchant program is centered on buying and selling mig33 currency called 'mig33 credits'.</p>$br
	<p>The credits allow you to pay for services such as cheap international phone calls, SMS, mobile content, like ringtones and wallpaper and other mig33 services.</p>$br
	<p>Profits are made from buying mig33 credits at a discounted rate, and then reselling them at full value to millions of mig33 users.</p>$br
	<p>How does this work? Get a 30% discount rate when you buy mig33 credits from USD$100 in value.</p>$br
	<p>For example, buy $500 worth of mig33 credits, and pay only $350 (30% discount).  You sell them at full value, worth $500.  In this case, you have made $150 dollars profit.</p>
END;

}else if ($page == 'cust'){
	$pageTitle = "Where do I find my customers?";
	$backLink = "help_mc.php";

	$info = <<<END
	<p>Your customers are everyday people who have the need for many services such as cheap international phone call, SMS, mobile content, ringtones and wallpapers and other mig33 services.</p>$br
	<p>This can be anyone from your family, your friends, your employees or mig33 users who you may reach via advertising.</p>$br
	<p>Turn them into mig33 users either using the 'Quick Create / Instant Account' feature or the 'Invite Customer'
	feature.</p>
END;

}else if ($page == 'more'){
	$pageTitle = "Where can I go for more help";
	$backLink = "help_mc.php";

	$info = <<<END
	<p>If you have more questions or wish for our sales team to contact you, please drop us an email at merchant@mig33.com with your contact details.</p>
END;

// default info page
} else {
	$pageTitle = "Merchant Center Help";
	$backLink = "merchant_center.php";

	$info = <<<END
	<ul>
		<li>$dash<a href="{$MCWapPath}help_mc.php?page=whatis">What is the merchant program?</a></li>$br
		<li>$dash<a href="{$MCWapPath}buy_credits.php">How do I purchase credits?</a></li>$br
		<li>$dash<a href="{$MCWapPath}help_mc.php?page=sell">How do I sell credits?</a></li>$br
		<li>$dash<a href="{$MCWapPath}help_mc.php?page=profit">How do I make a profit?</a></li>$br
		<li>$dash<a href="{$MCWapPath}help_mc.php?page=cust">Where do I find my customers? </a></li>$br
		<li>$dash<a href="{$MCWapPath}help_mc.php?page=more">Where can I go for more help?</a></li>$br
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

