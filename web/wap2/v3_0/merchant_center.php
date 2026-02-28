<?php
include_once("../common/common-inc.php");
session_start();

$page = $_GET['page'];
if (!$page)
{

?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h2>Merchant Center</h2>
	<!--<p>Welcome to the mig33 Merchant Center. Here you&#39;ll find information on:</p>-->
	<ul>
	<li><a href="merchant_center.php?page=bc">Buying Credits</a></li>
	<li><a href="merchant_center.php?page=sc">Selling Credits</a></li>
	<li><a href="merchant_center.php?page=mktg">Marketing your Business</a></li>
	<li><a href="merchant_center.php?page=tax">Taxation</a></li>
	</ul>
	<p>For more info, visit our web site www.mig33.com and sign in to the Merchant Center</p>
	<?php if ($_SESSION['user']) { ?>
		<a href="member/index.php">Home</a>
	<?php } else { ?>
		<a href="index.php">Home</a>
	<?php } ?>
  <?php include_once("gs_inc.php") ?>

  </body>
</html>
<?php
}
else if ($page == 'bc')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h2>Buying Credits</h2>
	<!--<p><b>Buy bulk credits at a discount</b></p>-->
	<p>mig33 sells bulk credits at discount rates starting at 25%, so you can make a profit when you resell credits to other mig33 users. </p>
	<p><a href="">discount rate table</a></p>
	<p><b>Payment Info:</b></p>
		<ul><li>By Bank Transfer (available in over 50 countries)</a></li>
		<li><a href="btransfer.php">By Telegraphic Transfer</a></li>
		<li><a href="help.php?page=bc_cc">By Credit and Debit Cards</a></li>
  	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'sc')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h2>Selling Credits</h2>
	<p>Two ways to sell credits to mig33 customers:</p>
	<ol>
	<li><b>Transferring Credits</b>: Transfer credits from your mig33 account to a user&#39;s</li>
	<li><b>Voucher Management</b>: Create and manage prepaid cards and vouchers using our online system. Visit www.mig33.com and sign in to the Merchant Center for more info.</li>
	</ol>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'bc')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h2>Marketing Your Business</h2>
	<p><a href="merchant_center.php?page=who">Who are your customers?</a></p>
	<p><a href="merchant_center.php?page=bene">Customer Benefits</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'who')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h2>Who are your customers?</h2>
	<b><p>Finding and Keeping Customers</p></b>
	<p>Anyone who has a mobile phone is a potential mig33 customer:</p>
	<ul>
	<li><a href="merchant_center.php?page=cur">Current mig33 members</li>
	<li><a href="merchant_center.php?page=new">New mig33 members</li>
	<li><a href="merchant_center.php?page=any">Anyone else with a mobile phone</li>
	<li><a href="merchant_center.php?page=sms">SMS calling</li>
	</ul>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'bc_bt')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Bank Transfer</b></p>
	<p>You can purchase more credits for your mig33 account by bank money transfer. Simply go to your bank and transfer monies to our bank account (details of which can be found on our website).</p>
	<p>After sending the bank transfer, go to the 'mig33 members' section of this site, then enter the transfer details into the 'Transfer Notification' form. We will process the Bank Transfer in 3 to 5 working days and credit your account.</p>
	<p><a href="help.php?page=bc">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'bc_vc')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Voucher/Prepaid Card</b></p>
	<p>To buy more credits, you can purchase vouchers/prepaid cards from any of our Affiliate partners. You can easily redeem your voucher by entering the details when you login to mig33 on your phone and access our WAP Members' Area. Alternatively, you can go to our website (http://www.mig33.com), go to 'Recharge' in 'My Account' and enter your voucher number as requested.</p>
	<p><a href="help.php?page=bc">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'smsc')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>SMS Callback</b></p>
	<p>If you want to make a call and are not logged in to mig33, you can still take advantage of our low call rates by using 'SMS Callback'.</p>
	<p>All you need to do is SMS the phone number you want to call to +447717989963. (Don't forget to enter the international code!)</p>
	<p>For example, if you were in London (country code +44) and you wanted to call 07722222222, you would SMS '447722222222' to +447717989963.</p>
	<p>We'll then connect your mobile to the number you want to call.</p>
	<?php include_once("gs_inc.php") ?>
	<p><a href="help.php">Back</a></p>
  </body>
</html>
<?php
}

else if ($page == 'smst')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>SMS Throwback</b></p>

	<p>By using our SMS Throwback function, you can launch a call between any two destinations – you don't even have to connect the call to your mobile!</p>
	<p>Once you've joined, all you need to do is send an SMS from your registered phone number with &lt;password&gt;*&lt;destination number&gt;*&lt;origin number&gt; to +447717989963. Then a call will be connected between the numbers you entered as the 'origin number' and the 'destination number'.</p>
	<p>Please remember to enter the phone numbers with their international codes (ie if you wish to dial a UK number 7711223344, enter 447711223344 (44 is the international code for the UK).</p>
	<p>A connection will then be made between the origin and destination numbers you have entered.</p>
	<p><a href="help.php">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'smsb')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>SMS Balance</b></p>

	<p>You can now check your mig33 balance by sending instructions via SMS.</p>
	<p>Simply SMS the word 'Balance' to +447717989963.</p>
	<p><a href="help.php">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'camera')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Using your mobile phone Camera</b></p>

	<p>The camera feature is only available for mig33 version <?=$cur_midlet?> and compatible mobile phones.</p>
	<p>A default resolution has been set for photos taken with your mobile phone. To change this, simply go to 'Camera Settings' in mig33 and run a camera test to see what resolution your camera allows. Note: larger resolution photos may take longer to send, and may incur more GPRS traffic.</p>
	<p><a href="help.php">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}

else if ($page == 'ce')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Connection Error</b></p>

	<p>A connection error happens because mig33 was unable to connect to the mig33 service. This may be because:</p>
	<p>1. Your phone may require the right Settings. <a href="help.php?page=ce1">(more)</a></p>
	<p>2. Your phone may need to be configured correctly. <a href="help.php?page=ce2">(more)</a></p>
	<p>3. Your Network Service Provider needs to let you access the Mobile Internet. <a href="help.php?page=ce3">(more)</a></p>
	<p>4. Your Network Service Provider may have blocked mig33. <a href="help.php?page=ce4">(more)</a></p>
	<p><a href="help.php">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'ce1')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Correct Settings</b></p>
	<p>Your phone may require another network setting (called an APN) for you to be able to connect to the mobile internet.</p>
	<p>Contact your Network Service Provider and ask for the settings to be sent to you via SMS.</p>
	<p><a href="help.php?page=ce">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'ce2')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Correct Cofiguration</b></p>
	<p>Some phones (older Sony Ericssons and Motorola phones, for instance) may require you to manually set which APN and Java application you wish to use to connect to the mobile internet. This can be set in your mobile web options menu.</p>
	<p>For additional details, please check http://www.mig33.com</p>
	<p><a href="help.php?page=ce">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'ce3')
{
?>

<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Mobile Internet</b></p>
	<p>In order to take advantage of all that mig33 has to offer, your network service needs to allow you to access the mobile internet.</p>
	<p>You should contact your network operator to confirm that your service plan allows you to access the internet and that the service is authenticated.</p>
	<p><a href="help.php?page=ce">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'ce4')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<p align="center"><b>Blocked Service</b></p>
	<p>Some networks are configured differently and may block the port number that mig33 uses to connect to our servers.</p>
	<p>You can manually change this by selecting 'Connection' and 'Manual' on login screen. Port number available:</p>
	<p>
		<b>
			25<br/>
			9119
		</b>
	</p>
	<p><a href="help.php?page=ce">Back</a></p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
?>
>
