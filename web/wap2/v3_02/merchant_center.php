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
	<h3>Merchant Center</h3>
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
	<h3>Buying Credits</h3>
	<!--<p><b>Buy bulk credits at a discount</b></p>-->
	<p>mig33 sells bulk credits at discount rates starting at 25%, so you can make a profit when you resell credits to other mig33 users. <a href="">See the discount rate table</a></p>
	<p><b>Payment Info:</b></p>
		<ul><li>By Bank Transfer (available in over 50 countries)</a></li>
		<li><a href="http://wap.mig33.com/member/btransfer.php">By Telegraphic Transfer</a></li>
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
	<h3>Selling Credits</h3>
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
else if ($page == 'mktg')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Marketing Your Business</h3>
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
	<h3>Who are your customers?</h3>
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
else if ($page == 'cur')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Current mig33 members</h3>
	<p>Members love mig33&#39;s free services, but they also want access to inexpensive international calls and SMS. In the future, we&#39;ll also offer new content and games that will require credits.</p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'new')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>New mig33 members</h3>
	<p>Encourage friends and family to join mig33. We get several hundred thousand new users a month. And you can earn free credit every time you refer a friend to become a mig33 member!</p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'any')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Anyone else with a mobile phone</h3>
	<p>There are some people that cannot use all of mig33&#39;s services on their phone yet, but can still take advantage of our inexpensive, convenient calls through SMS calling.</p>
	<?php include_once("gs_inc.php") ?>

  </body>
</html>
<?php
}

else if ($page == 'sms')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>SMS Calling</h3></p>
	<p>You can start your mig33 merchant business using SMS calling. This is ideal for merchants with customers who cannot download & install mig33 on their mobile phone.</p>
	<p>With SMS Calling, users can make inexpensive, international calls even if they do not have a compatible phone or mobile network.</p>
	<ul>
	<li>On behalf of the customer, register a mig33 account for them. You will need to set up the username and password for your customer.</li>
	<li>After registration, the customer&#39;s mig33 details will be sent to his mobile number via SMS. Keep these details!</li>
	<li> Once the customer has a mig33 account, he can use <a href="http://wap.mig33.com/help.php?page=smsc">SMS callback</a> or <a href="http://wap.mig33.com/help.php?page=smst">SMS throwback</a> to make calls. </li></ul>
	<p>Your customer can now buy mig33 credits from you to 	make cheap international calls.</p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'bene')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Customer Benefits</h3></p>
	<p>When you sell credits to your customers, remind them of 	mig33&#39;s many benefits, like:</p>
	<ul>
	<li><a href="merchant_center.php?page=convenience">Convenience</a></li>
	<li><a href="merchant_center.php?page=community">Community</li>
	<li><a href="merchant_center.php?page=cheapcalls">Cheap Calls</li>
	</ul>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'convenience')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Convenience</h3></p>
	<p>You can instantly access phone numbers you need on your Contact List.</p>
	<ul>
	<li>If you&#39;re traveling, calls can also be made easily via SMS calling.</li>
	<li>You can charge your account by buying more credits from a mig33 merchant. (No need to buy prepaid cards or hassle with PIN numbers!)</li>
	</ul>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}

else if ($page == 'community')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Community</h3></p>
	<p>Keep in touch for less with inexpensive international calls and SMS, plus free instant messaging.</p>
	<p>Create a global community with mobile chat rooms, MSN and Yahoo! Instant Messengers, picture-sharing, and profiles.</p>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>
<?php
}
else if ($page == 'cheapcalls')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
	<h3>Cheap Calls</h3></p>
	<p>Enjoy competitive calling rates to over 200 country destinations. For example, you can make a call from the UK to the USA for only 4-5p per minute.</p>
	<ul>
	<li>Save even more money by connecting your calls to landlines (these calls are cheaper than those solely connected to mobiles).</li>
	<li>Send SMS for only 10 Australian cents per message.</li>
	</ul>
	<?php include_once("gs_inc.php") ?>
  </body>
</html>

<?php
}
else if ($page == 'tax')
{
?>
<html>
  <head>
    <title>mig33</title>
  </head>
  <body>
<h3>Taxation</h3>
<p>Many merchants accept cash for mig33 credits. When you sell mig33 prepaid credits, it is <i>your</i> responsibility to comply with your country&#39;s rules on business and taxation.</p>
<p>If you are unsure of these laws, please check with the appropriate authorities in your country.</p>
  </body>
</html>
<?php
}
?>
>
