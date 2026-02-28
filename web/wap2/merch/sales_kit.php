<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
check_session_merchant();

$page = $_GET['page'];

emitHeader();
emitTitle("mig33");
if ($page == 'mktg'){
?>
		<small><b>Marketing Your Business</b></small><br/>
		<ul>
			<li><small><a href="sales_kit.php?page=who">Who are your customers?</a></small></li>
			<li><small><a href="sales_kit.php?page=bene">Customer Benefits</a></small></li>
		</ul>
		<small><a href="sell_credits.php">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'who')
{
?>
		<small><b>Who are your customers?</b></small><br/><br/>
		<small>Finding and Keeping Customers.</small><br/>
		<small>Anyone who has a mobile phone is a potential mig33 customer:</small><br/>
		<ul>
			<li><small><a href="sales_kit.php?page=any">Everyone with a mobile phone</a></small></li>
			<li><small><a href="sales_kit.php?page=new">New mig33 members</a></small></li>
			<li><small><a href="sales_kit.php?page=cur">Current mig33 members</a></small></li>
		</ul>
		<small><a href="sales_kit.php?page=mktg">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'cur')
{
?>
		<small><b>Current mig33 members</b></small><br/><br/>
		<small>Members love mig33's free services, but they also want access to inexpensive international calls and SMS. In the future, we'll also offer new content and games that will require credits.</small><br/>
		<ul>
			<li><small>Telling all your friends and family members, and all those that you referred mig33 to.</small></li>
			<li><small>For merchants with a long history of recharging, we can help you by advertising you on mig33.</small></li>
		</ul>
		<small><a href="sales_kit.php?page=who">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'new')
{
?>
		<small><b>New mig33 members</b></small><br/><br/>
		<small>Encourage friends and family to join mig33. We get several hundred thousand new users a month. And you can earn free credit every time you refer a friend to become a mig33 member!</small><br/>
		<ul>
			<li><small>Use 'Invite Friend'</small></li>
			<li><small>Enter your friend's mobile number and they will receive an SMS with an invitation to join mig33 for free.</small></li>
		</ul>
		<small><a href="sales_kit.php?page=who">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'any')
{
?>
		<small><b>Everyone with a mobile phone</b></small><br/><br/>
		<small>There are some people that cannot use mig33 on their mobile, but can still take advantage of mig33's inexpensive calls through SMS Calling.</small><br/>
		<ul>
			<li><small>On behalf of the customer, register a mig33 account for them.</small></li>
			<li><small>After registration, the customer's mig33 details will be sent to his mobile number via SMS. Keep these details!</small></li>
			<li><small>Once the customer has a mig33 account, he can use <a href="sales_kit.php?page=smsc">SMS callback</a> or <a href="sales_kit.php?page=smst">SMS throwback</a> to make calls.</small></li>
		</ul>
		<small><a href="sales_kit.php?page=who">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'smst')
{
?>
		<small><b>SMS Throwback</b></small><br/><br/>
		<small>By using our SMS Throwback function, you can launch a call between any two destinations you don't even have to connect the call to your mobile!</small><br/><br/>
		<small>Once you've joined, all you need to do is send an SMS from your registered phone number with &lt;password&gt;*&lt;destination number&gt;*&lt;origin number&gt; to +447717989963. Then a call will be connected between the numbers you entered as the 'origin number' and the 'destination number'.</small><br/><br/>
		<small>Please remember to enter the phone numbers with their international codes (ie if you wish to dial a UK number 7711223344, enter 447711223344 (44 is the international code for the UK).</small><br/><br/>
		<small>A connection will then be made between the origin and destination numbers you have entered.</small><br/><br/>
		<small><a href="sales_kit.php?page=any">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
  	</body>
</html>
<?php
}
else if ($page == 'smsb')
{
?>
		<small><b>SMS Balance</b></small><br/><br/>
		<small>You can now check your mig33 balance by sending instructions via SMS.</small><br/><br/>
		<small>Simply SMS the word 'Balance' to +447717989963.</small><br/><br/>
		<small><a href="sales_kit.php?page=any">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
  	</body>
</html>
<?php
}
else if ($page == 'smsc')
{
?>
		<small><b>SMS Callback</b></small><br/><br/>
		<small>If you want to make a call and are not logged in to mig33, you can still take advantage of our low call rates by using 'SMS Callback'.</small><br/><br/>
		<small>All you need to do is SMS the phone number you want to call to +447717989963. (Don't forget to enter the international code!)</small><br/><br/>
		<small>For example, if you were in London (country code +44) and you wanted to call 07722222222, you would SMS '447722222222' to +447717989963.</small><br/><br/>
		<small>We'll then connect your mobile to the number you want to call.</small><br/><br/>
		<small><a href="sales_kit.php?page=any">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
  	</body>
</html>
<?php
}
else if ($page == 'bene')
{
?>
		<small><b>Customer Benefits</b></small><br/><br/>
		<small>When you sell credits to your customers, remind them of 	mig33's many benefits, like:</small><br/>
		<ul>
			<li><small><a href="sales_kit.php?page=convenience">Convenience</a></small></li>
			<li><small><a href="sales_kit.php?page=community">Community</a></small></li>
			<li><small><a href="sales_kit.php?page=cheapcalls">Cheap Calls</a></small></li>
		</ul>
		<small><a href="sales_kit.php?page=mktg">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'convenience')
{
?>
		<small><b>Convenience</b></small><br/><br/>
		<small>You can instantly access phone numbers you need on your Contact List.</small><br/>
		<ul>
			<li><small>If you're traveling, calls can also be made easily via SMS calling.</small></li>
			<li><small>You can charge your account by buying more credits from a mig33 merchant. (No need to buy prepaid cards or hassle with PIN numbers!)</small></li>
		</ul>
		<small><a href="sales_kit.php?page=bene">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'community')
{
?>
		<small><b>Community</b></small><br/><br/>
		<small>Keep in touch for less with inexpensive international calls and SMS, plus free instant messaging.</small><br/><br/>
		<small>Create a global community with mobile chat rooms, MSN and Yahoo! Instant Messengers, picture-sharing, and profiles.</small><br/>
		<br/>
		<small><a href="sales_kit.php?page=bene">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
else if ($page == 'cheapcalls')
{
?>
		<small><b>Cheap Calls</b></small><br/><br/>
		<small>Enjoy competitive calling rates to over 200 country destinations. For example, you can make a call from the UK to the USA for only 4-5p per minute.</small><br/>
		<ul>
			<li><small>Save even more money by connecting your calls to landlines (these calls are cheaper than those solely connected to mobiles).</small></li>
			<li><small>Send SMS for only 10 Australian cents per message.</small></li>
		</ul>
		<small><a href="sales_kit.php?page=bene">Back</a></small><br/>
		<br/>
		<small><a href="merchant_center.php">Merchant Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
?>
