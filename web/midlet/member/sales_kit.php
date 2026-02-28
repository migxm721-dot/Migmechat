<?php
include_once("../../common/common-inc.php");
ice_check_session();
$page = $_GET['page'];

if ($page == 'mktg')
{
?>
<html>
	<head>
		<title>mig33</title>
	</head>
	<body>
		<p><b>Marketing Your Business</b></p>
		<br>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=who">Who are your customers?</a></p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=bene">Customer Benefits</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sell_credits.php">Back to Sales Kit</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Who are your customers?</b></p>
		<br>
		<p>Finding and Keeping Customers</p>
		<p>Anyone who has a mobile phone is a potential mig33 customer:</p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=any">Everyone with a mobile phone</a></p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=new">New mig33 members</a></p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=cur">Current mig33 members</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=mktg">Back to Marketing Your Business</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Current mig33 members</b></p>
		<br>
		<p>Members love mig33's free services, but they also want access to inexpensive international calls and SMS. In the future, we'll also offer new content and games that will require credits.</p>
		<p>&gt;&nbsp;Telling all your friends and family members, and all those that you referred mig33 to.</p>
		<p>&gt;&nbsp;For merchants with a long history of recharging, we can help you by advertising you on mig33.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=who">Back to Customers</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>New mig33 members</b></p>
		<br>
		<p>Encourage friends and family to join mig33. We get several hundred thousand new users a month. And you can earn free credit every time you refer a friend to become a mig33 member!</p>
		<p>&gt;&nbsp;Use 'Invite Friend'</p>
		<p>&gt;&nbsp;Enter your friend’s mobile number and they will receive an SMS with an invitation to join mig33 for free.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=who">Back to Customers</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Everyone with a mobile phone</b></p>
		<br>
		<p>There are some people that cannot use mig33 on their mobile, but can still take advantage of mig33's inexpensive calls through SMS Calling.</p>
		<p>&gt;&nbsp;On behalf of the customer, register a mig33 account for them.</p>
		<p>&gt;&nbsp;After registration, the customer's mig33 details will be sent to his mobile number via SMS. Keep these details!</p>
		<p>&gt;&nbsp;Once the customer has a mig33 account, he can use <a href="<?=$server_root?>/midlet/member/sales_kit.php?page=smsc">SMS callback</a> or <a href="<?=$server_root?>/midlet/member/sales_kit.php?page=smst">SMS throwback</a> to make calls.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=who">Back to Customers</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>SMS Throwback</b></p>
		<br>
		<p>By using our SMS Throwback function, you can launch a call between any two destinations you don't even have to connect the call to your mobile!</p>
		<p>Once you've joined, all you need to do is send an SMS from your registered phone number with &lt;password&gt;*&lt;destination number&gt;*&lt;origin number&gt; to +447717989963. Then a call will be connected between the numbers you entered as the 'origin number' and the 'destination number'.</p>
		<p>Please remember to enter the phone numbers with their international codes (ie if you wish to dial a UK number 7711223344, enter 447711223344 (44 is the international code for the UK).</p>
		<p>A connection will then be made between the origin and destination numbers you have entered.</p>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=any">Back to Customers</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>SMS Balance</b></p>
		<br>
		<p>You can now check your mig33 balance by sending instructions via SMS.</p>
		<p>Simply SMS the word 'Balance' to +447717989963.</p>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=any">Back to Customers</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Customer Benefits</b></p>
		<br>
		<p>When you sell credits to your customers, remind them of 	mig33's many benefits, like:</p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=convenience">Convenience</a></p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=community">Community</a></p>
		<p>&gt;&nbsp;<a href="<?=$server_root?>/midlet/member/sales_kit.php?page=cheapcalls">Cheap Calls</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=mktg">Back to Marketing Your Business</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Convenience</b></p>
		<br>
		<p>You can instantly access phone numbers you need on your Contact List.</p>
		<p>&gt;&nbsp;If you're traveling, calls can also be made easily via SMS calling.</p>
		<p>&gt;&nbsp;You can charge your account by buying more credits from a mig33 merchant. (No need to buy prepaid cards or hassle with PIN numbers!)</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=bene">Back to Customer Benefits</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Community</b></p>
		<br>
		<p>Keep in touch for less with inexpensive international calls and SMS, plus free instant messaging.</p>
		<p>Create a global community with mobile chat rooms, MSN and Yahoo! Instant Messengers, picture-sharing, and profiles.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=bene">Back to Customer Benefits</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
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
		<p><b>Cheap Calls</b></p>
		<br>
		<p>Enjoy competitive calling rates to over 200 country destinations. For example, you can make a call from the UK to the USA for only 4-5p per minute.</p>
		<p>&gt;&nbsp;Save even more money by connecting your calls to landlines (these calls are cheaper than those solely connected to mobiles).</p>
		<p>&gt;&nbsp;Send SMS for only 10 Australian cents per message.</p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/sales_kit.php?page=bene">Back to Customer Benefits</a></p>
		<br>
		<p><a href="<?=$server_root?>/midlet/member/merchant_center.php">Merchant Home</a>&gt;&gt;</p>
		<p><a href="<?=$server_root?>/midlet/member/my_account.php">My Account Home</a>&gt;&gt;</p>
		<br>
	</body>
</html>
<?php
}
?>
