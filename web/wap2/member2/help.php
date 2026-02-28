<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");
$page = $_GET['page'];
$pf = $_GET['pf'];

if (!$page)
{
emitHeader("Help");
?>
		<ul>
			<li><small><a href="help.php?page=rr&amp;pf=<?=$pf?>">Register and Download</a></small></li>
			<li><small><a href="../hinstructions.php?pf=<?=$pf?>">Connection Error</a></small></li>
			<li><small><a href="help.php?page=uw&amp;pf=<?=$pf?>">Using mig33 WAP site</a></small></li>
			<li><small><a href="help.php?page=if&amp;pf=<?=$pf?>">Invite Friends</a></small></li>
			<li><small><a href="help.php?page=bc&amp;pf=<?=$pf?>">Buy Credits</a></small></li>
			<li><small><a href="../affiliate.php?pf=<?=$pf?>">Become a Merchant</a></small></li>
			<li><small><a href="../contact.php?pf=<?=$pf?>_HELP">Contact us</a></small></li>
			<li><small><a href="help.php?page=usms&amp;pf=<?=$pf?>">Using mig33 SMS service</a></small></li>
			<li><small><a href="../privacy.php?pf=<?=$pf?>_HELP">Privacy policy</a></small></li>
		</ul>
<?php
		if($pf == 'DOWNLOAD'){
?>
		<small><a href="../download.php">Back</a></small><br/>
<?php
		}else if($pf == 'JOIN'){
?>
		<small><a href="<?=get_server_root()?>/sites/index.php?c=registration&a=register&v=wap">Back</a></small><br/>
<?php
		}else if($pf == 'MYACCOUNT'){
?>
		<small><a href="../member/index.php">Back</a></small><br/>
<?php
		}else if($pf == 'PREPAID'){
?>
		<small><a href="../prepaid/index.php">Back</a></small><br/>
<?php
		}
?>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'rr')
{
emitHeader("Help");
?>
		<small><center><b>Register and Download</b></center></small><br/><br/>
		<small>1. Select 'Download now' to register and install mig33 on your phone.</small><br/>
		<small>2. After registering, you'll receive an Activation Code via SMS.</small><br/>
		<small>3. Download mig33 and open it. Choose your test connection. If you are unsure, pick TCP first.</small><br/>
		<small>4. Enter the Activation Code that was sent to you via SMS to get full use of mig33, including our VoIP call services, SMS and chat rooms.</small><br/>
		<small>If you get an 'error', you may need a second mobile internet setting. Contact your Telco to get your 'Mobile Internet Settings' or go to http://www.mig33.com, for more help.</small><br/><br/>
		<small><a href="help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'uw')
{
emitHeader("Help");
?>
		<small><center><b>Using mig33 WAP site</b></center></small><br/>
		<small>If you prefer not to download the mig33 client, or the download will not work, you can use all the mig33 features - including chat, calls and sms - right from your phone browser.</small><br/><br/>
		<small>First <a href="<?=get_server_root()?>/sites/index.php?c=registration&a=register&v=wap">Register</a> for a mig33 account. Once you have an account you can log into the mig33 WAP site from the home page.</small><br/><br/>
		<small><a href="help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'if')
{
emitHeader("Help");
?>
		<small><center><b>Invite Friends</b></center></small><br/>
		<small>You can invite your friends to join mig33 to earn more free credits.</small><br/>
		<small>All you need to do is select 'Invite Friend' in mig33 and follow the instructions. Your friend will be sent an SMS asking them to join mig33. Once they join and authenticate their account, you will automatically receive bonus mig33 credit.</small><br/>
		<small><a href="help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'bc')
{
emitHeader("Help");
?>
		<small><center><b>Buy Credits</b></center></small><br/>
		<small>There are four methods to choose from to buy credits and recharge your mig33 account:</small><br/>
		<ul>
			<li><small><a href="help.php?page=bc_bt&amp;pf=<?=$pf?>">Local Bank Deposit</a></small></li>
			<li><small><a href="help.php?page=bc_wu&amp;pf=<?=$pf?>">Western Union</a></small></li>
			<li><small><a href="help.php?page=bc_cc&amp;pf=<?=$pf?>">Credit and Debit Cards</a></small></li>
			<li><small><a href="help.php?page=bc_vc&amp;pf=<?=$pf?>">Redeeming a Voucher</a></small></li>
		</ul>
		<small><a href="help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'bc_cc')
{
emitHeader("Help");
?>
		<small><center><b>Credit and Debit Cards (Visa, Amex, Mastercard, JCB)</b></center></small><br/>
		<small>You can easily recharge your mig33 account via credit card. To do this:</small><br/>
		<small><b>1.</b>Select 'Recharge' in 'My Account' when you login to mig33 and select 'Credit and Debit Cards'</small><br/>
		<small><b>2.</b>Enter your name as it appears on your credit card, your credit card number, your card's verification number, and expiry date.</small><br/><br/>
		<small>You can also view the relevant exchange rate at the time of your credit purchase. </small><br/><br/>
		<small>Alternatively, you can also recharge your account with your credit card through our website (http://www.mig33.com).</small><br/><br/>
		<small><a href="help.php?page=bc&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'bc_bt')
{
emitHeader("Help");
?>
		<small><center><b>Local Bank Deposit</b></center></small><br/>
		<small>You can purchase more credits for your mig33 account by transferring money into our bank. It is convenient and secure to use.</small><br/>
		<small>1. Simply select "Recharge" in "My Account" when you log into mig33, and select "Local Bank Deposit".</small><br/>
		<small>2. Fill in a short form to get all bank details and instructions on how to complete the deposit form.</small><br/>
		<small>3. We will send you an SMS when we have recharged your account in 3-5 days.</small><br/><br/>
		<small><a href="help.php?page=bc&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'bc_wu')
{
emitHeader("Help");
?>
		<small><center><b>Western Union</b></center></small><br/>
		<small>You can purchase more credits for your mig33 account by transferring money from a Western Union branch near you. Western Union is the trusted partner for secure money transfer.</small><br/>
		<small>1. Simply select "Recharge" in "My Account" when you log into mig33, and select "Western Union".</small><br/>
		<small>2. fill in a short form to get all bank details and instructions on how to complete the transfer form.</small><br/>
		<small>3. We will send you an SMS when we have recharged your account. Recharge is usually instant.</small><br/><br/>
		<small><a href="help.php?page=bc&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'bc_vc')
{
emitHeader("Help");
?>
		<small><center><b>Redeem Voucher</b></center></small><br/>
		<small>You can purchase more credits for your mig33 account from a mig33 merchant partner. To find a merchant near you, email contact@mig33.com</small><br/>
		<small>1. Simply select "Recharge" in "My Account" when you log into mig33, and select "Redeem a Voucher".</small><br/>
		<small>2. Enter the voucher number into the form and you account will be instantly credited with the value of the voucher.</small><br/><br/>
		<small><a href="help.php?page=bc&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'smsc')
{
emitHeader("Help");
?>
		<small><center><b>SMS Callback</b></center></small><br/>
		<small>If you want to make a call and are not logged in to mig33, you can still take advantage of our low call rates by using 'SMS Callback'.</small><br/><br/>
		<small>All you need to do is SMS the phone number you want to call to +447717989963. (Don't forget to enter the international code!).</small><br/><br/>
		<small>For example, if you were in London (country code +44) and you wanted to call 07722222222, you would SMS '447722222222' to +447717989963.</small><br/><br/>
		<small>We'll then connect your mobile to the number you want to call.</small><br/><br/>
		<small><a href="help.php?page=usms&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'usms')
{
emitHeader("Help");
?>
		<small><center><b>Using mig33 SMS service</b></center></small><br/>
		<small>If you want to make cheap calls you do not need to log into mig33. You can instead take advantage of our low call rates by using the SMS service.</small><br/>
		<ul>
			<li><small><a href="help.php?page=smsc&amp;pf=<?=$pf?>">SMS Callback</a></small></li>
			<li><small><a href="help.php?page=smst&amp;pf=<?=$pf?>">SMS Throwback</a></small></li>
			<li><small><a href="help.php?page=smsb&amp;pf=<?=$pf?>">SMS Balance</a></small></li>
		</ul>
		<small><a href="help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'smst')
{
emitHeader("Help");
?>
		<small><center><b>SMS Throwback</b></center></small><br/>
		<small>By using our SMS Throwback function, you can launch a call between any two destinations – you don't even have to connect the call to your mobile!</small><br/><br/>
		<small>Once you've joined, all you need to do is send an SMS from your registered phone number with &lt;password&gt;*&lt;destination number&gt;*&lt;origin number&gt; to +447717989963. Then a call will be connected between the numbers you entered as the 'origin number' and the 'destination number'.</small><br/><br/>
		<small>Please remember to enter the phone numbers with their international codes (ie if you wish to dial a UK number 7711223344, enter 447711223344 (44 is the international code for the UK).</small><br/><br/>
		<small>A connection will then be made between the origin and destination numbers you have entered.</small><br/><br/>
		<small><a href="help.php?page=usms&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'smsb')
{
emitHeader("Help");
?>
		<small><center><b>SMS Balance</b></center></small><br/>
		<small>You can now check your mig33 balance by sending instructions via SMS.</small><br/><br/>
		<small>Simply SMS the word 'Balance' to +447717989963.</small><br/><br/>
		<small><a href="help.php?page=usms&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
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
		<small><center><b>Connection Error</b></center></small><br/>
		<small>A connection error happens because mig33 was unable to connect to the mig33 service. This may be because:</small><br/>
		<small>1. Your phone may require the right Settings. <a href="help.php?page=ce1">(more)</a></small><br/>
		<small>2. Your phone may need to be configured correctly. <a href="help.php?page=ce2">(more)</a></small><br/>
		<small>3. Your Network Service Provider needs to let you access the Mobile Internet. <a href="help.php?page=ce3">(more)</a></small><br/>
		<small>4. Your Network Service Provider may have blocked mig33. <a href="help.php?page=ce4">(more)</a></small><br/><br/>
		<small><a href="help.php?pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'ce1')
{
emitHeader("Help");
?>
		<small><center><b>Correct Settings</b></center></small><br/>
		<small>Your phone may require another network setting (called an APN) for you to be able to connect to the mobile internet.</small><br/><br/>
		<small>Contact your Network Service Provider and ask for the settings to be sent to you via SMS.</small><br/><br/>
		<small><a href="help.php?page=ce&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'ce2')
{
emitHeader("Help");
?>
		<small><center><b>Correct Configuration</b></center></small><br/>
		<small>Some phones (older Sony Ericssons and Motorola phones, for instance) may require you to manually set which APN and Java application you wish to use to connect to the mobile internet. This can be set in your mobile web options menu.</small><br/><br/>
		<small>For additional details, please check http://www.mig33.com</small><br/><br/>
		<small><a href="help.php?page=ce&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'ce3')
{
emitHeader("Help");
?>
		<small><center><b>Mobile Internet</b></center></small><br/>
		<small>In order to take advantage of all that mig33 has to offer, your network service needs to allow you to access the mobile internet.</small><br/><br/>
		<small>You should contact your network operator to confirm that your service plan allows you to access the internet and that the service is authenticated.</small><br/><br/>
		<small><a href="help.php?page=ce&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
else if ($page == 'ce4')
{
emitHeader("Help");
?>
		<small><center><b>Blocked Service</b></center></small><br/>
		<small>Some networks are configured differently and may block the port number that mig33 uses to connect to our servers.</small><br/><br/>
		<small>You can manually change this by selecting 'Connection' and 'Manual' on login screen. Port number available:</small><br/>
		<small><b>25<br/>
				9119
				</b>
		</small><br/><br/>
		<small><a href="help.php?page=ce&amp;pf=<?=$pf?>">Back</a></small><br/>
		<small><a href="t.php?cmd=home">Home</a></small><br/>
		<?php include_once("../gs_inc.php") ?>
	</body>
</html>
<?php
}
?>
