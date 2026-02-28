<?php
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");
session_start();

$page = $_GET['page'];
$ppid = $_GET['ppid'];

emitHeader("mig33");
?>
	<div id="content">
		<div class="section">
<?php
if (!$page)
{
?>
		<h3>Merchant Center</h3>
		<ul>
			<li><a href="merchant_center.php?page=bc">Buying Credits</a></li>
			<li><a href="merchant_center.php?page=sc">Selling Credits</a></li>
			<li><a href="merchant_center.php?page=mktg">Marketing your Business</a></li>
			<li><a href="merchant_center.php?page=tax">Taxation</a></li>
		</ul>
		<p>For more info, visit our web site www.mig33.com and sign in to the Merchant Center</p>
		<a href="index.php">Members Home</a>
<?php
}
else if ($page == 'bc')
{
?>
		<h3>Buying Credits</h3>
		<p>mig33 sells bulk credits at discount rates starting at 25%, so you can make a profit when you resell credits to other mig33 users.
		<p><b>Discount rates (in AUD):</b></p>
		<ul>
			<li>$100 to $4,999: 25% discount</li>
			<li>$5,000 & above: 30% discount</li>
		</ul>
		<p><b>Payment Details:</b></p>
		<ul>
			<?php
				try{
					if(!isset($ppid)){
						$userDataSoap = soap_call_ejb('loadUserDetails', array($_SESSION['user']['username']));
						//Check if user's country of origin can support Bank transfer
						$countryID = $userDataSoap['countryID'];
						settype($countryID, "int");

						$ppid = soap_call_ejb('getBankTransferProductID', array($countryID));
					}

					if($ppid != '0'){
						print '<li><a href="member/recharge_bt.php?ppid='.$ppid.'">By Bank Transfer</a> (available in over 50 countries)</li>';
					}
				}catch(Exception $e){
					$error = $e->getMessage();
					print $error;
				}

				if($ppid == '0'){
					print '<li><a href="member/tttransfer.php">By Telegraphic Transfer</a></li>';
				}
			?>

			<li><a href="help.php?page=bc_cc">By Credit and Debit Cards</a></li>
		</ul>
		<hr>
		<a href="merchant_center.php">Merchant Home</a>
<?php
}
else if ($page == 'sc')
{
?>
		<h3>Selling Credits</h3>
		<p>Two ways to sell credits to mig33 customers:</p>
		<ol>
			<li><b>Transfer Credits</b>: Transfer credits from your account to your customer&#39;s</li>
			<li><b>Voucher Management</b>: Create and manage prepaid cards and vouchers using our online system.</li>
		</ol>
		<p>For more info, Visit www.mig33.com and sign in to the Merchant Center</p>
		<hr>
		<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'mktg')
{
?>
		<h3>Marketing Your Business</h3>
		<p><a href="merchant_center.php?page=who&ppid=<?=$ppid?>">Who are your customers?</a></p>
		<p><a href="merchant_center.php?page=bene&ppid=<?=$ppid?>">Customer Benefits</a></p>
		<?php include_once("gs_inc.php") ?>
		<hr>
		<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'who')
{
?>
	<h3>Who are your customers?</h3>
	<b><p>Finding and Keeping Customers</p></b>
	<p>Anyone who has a mobile phone is a potential mig33 customer</p>
	<ul>
	<li><a href="merchant_center.php?page=any&ppid=<?=$ppid?>">Everyone with a mobile phone</li>
	<li><a href="merchant_center.php?page=new&ppid=<?=$ppid?>">New mig33 members</li>
	<li><a href="merchant_center.php?page=cur&ppid=<?=$ppid?>">Current mig33 members</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=mktg&ppid=<?=$ppid?>">Back to Marketing Your Business</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Back to Merchant Center</a>
<?php
}
else if ($page == 'cur')
{
?>
	<h3>Current mig33 members</h3>
	<p>Members love mig33&#39;s free services, but they also want access to inexpensive international calls and SMS. In the future, we&#39;ll also offer new content and games that will require credits.  Find current members by</p>
	<ul>
		<li>Telling all your friends and family members, and all those that you referred mig33 to.</li>
		<li>For merchants with long history of recharging, we can help you by advertising you on mig33.</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=who&ppid=<?=$ppid?>">Back to Customers</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'new')
{
?>
	<h3>New mig33 members</h3>
	<p>Encourage friends and family to join mig33. We get several hundred thousand new users a month. And you can earn free credit every time you refer a friend to become a mig33 member!</p>
	<ul>
		<li>Use &#39;Invite Friend&#39;</li>
		<li>Enter your friend&#39;s mobile number and they will receive an SMS with an invitation to join mig33 for free.</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=who&ppid=<?=$ppid?>">Back to Customers</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'any')
{
?>
	<h3>Everyone with a mobile phone</h3>
	<p>There are some people that cannot use mig33 on their mobile, but can still take advantage of mig33 inexpensive calls through SMS Calling.</p>
	<ul>
		<li>On behalf of the customer, register a mig33 account for them.</li>
		<li>After registration, the customer's mig33 details will be sent to his mobile number via SMS. Keep these details!</li>
		<li>Once the customer has a mig33 account, he can use <a href="merchant_center.php?page=smsc">SMS callback</a> or <a href="merchant_center.php?page=smst">SMS throwback</a> to make calls.</li>
	</ul>
	<p>Your customers can now buy mig33 credits from you.</p>
	<hr>
	<a href="merchant_center.php?page=who&ppid=<?=$ppid?>">Back to Customers</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}

else if ($page == 'sms')
{
?>
	<h3>SMS Calling</h3></p>
	<p>You can start your mig33 merchant business using SMS calling. This is ideal for merchants with customers who cannot download & install mig33 on their mobile phone.</p>
	<p>With SMS Calling, users can make inexpensive, international calls via SMS simply by following these steps:</p>
	<ul>
	<li>On behalf of the customer, register a mig33 account for them. You will need to set up the username and password for your customer.</li>
	<li>After registration, mig33 will send an SMS to the customer with the username and password. Keep these details!</li>
	<li> Once the customer has a mig33 account, he can use <a href="help.php?page=smsc&ppid=<?=$ppid?>">SMS callback</a> or <a href="help.php?page=smst&ppid=<?=$ppid?>">SMS throwback</a> to make calls.</li>
	</ul>
	<p>Your customer can now buy mig33 credits from you to make cheap international calls.</p>
	<hr>
	<a href="merchant_center.php?page=who&ppid=<?=$ppid?>">Back to Customers</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'bene')
{
?>
	<h3>Customer Benefits</h3></p>
	<p>When you sell credits to your customers, remind them of mig33&#39;s many benefits, like:</p>
	<ul>
	<li><a href="merchant_center.php?page=convenience">Convenience</a></li>
	<li><a href="merchant_center.php?page=community">Community</li>
	<li><a href="merchant_center.php?page=cheapcalls">Cheap Calls</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=mktg&ppid=<?=$ppid?>">Back to Marketing Your Business</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'convenience')
{
?>
	<h3>Convenience</h3></p>
	<p>You can instantly access phone numbers you need on your Contact List.</p>
	<ul>
	<li>If you&#39;re traveling, calls can also be made easily via <a href="merchant_center.php?page=smsc">SMS calling</a>.</li>
	<li>You can charge your account by buying more credits from a mig33 merchant. (No need to buy prepaid cards or hassle with PIN numbers!)</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=bene&ppid=<?=$ppid?>">Back to Customer Benefits</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}

else if ($page == 'community')
{
?>
	<h3>Community</h3></p>
	<ul>
	<li>Keep in touch for less with inexpensive international calls and SMS, plus free instant messaging.</li>
	<li>Create a global community with mobile chat rooms, MSN and Yahoo! Instant Messengers, picture-sharing, and profiles.</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=bene&ppid=<?=$ppid?>">Back to Customer Benefits</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'cheapcalls')
{
?>
	<h3>Cheap Calls</h3></p>
	<p>Enjoy competitive calling rates to over 200 country destinations. For example, you can make a call from the UK to the USA for only 4-5p per minute.</p>
	<ul>
	<li>Save even more money by connecting your calls to landlines (these calls are cheaper than those solely connected to mobiles).</li>
	<li>Send any international SMS for only 10 Australian cents each. This is cheaper than what you usually pay your mobile operator.</li>
	</ul>
	<hr>
	<a href="merchant_center.php?page=bene&ppid=<?=$ppid?>">Back to Customer Benefits</a><br>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
else if ($page == 'tax')
{
?>
	<h3>Taxation</h3>
	<p>Many merchants accept cash for mig33 credits. When you sell mig33 prepaid credits, it is <i>your</i> responsibility to comply with your 	country&#39;s rules on business and taxation.</p>
	<p>If you are unsure of these laws, please check with the appropriate authorities in your country.</p>
	<hr>
	<a href="merchant_center.php?ppid=<?=$ppid?>">Merchant Home</a>
<?php
}
?>
		</div>
<?php emitFooter(); ?>
	</div>
<?php emitFooter_end(); ?>