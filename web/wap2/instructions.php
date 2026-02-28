<?php
include_once($_SERVER['DOCUMENT_ROOT'] . "/common/common-inc.php");

//Nokia 6630, N70, N73, 3230
if (($mobile_vendor == 'Nokia') && ($mobile_model == '3230') || ($mobile_model == '6630') || ($mobile_model == 'N70') || ($mobile_model == 'N73') )
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to run mig33. This mobile phone requires a second Internet GPRS setting to connect to mig33 server via TCP/IP.  Call your network provider for setting, or setup manually following these steps:</p>
		<b>1.</b> Go to 'Tools' and 'Settings'<br/>
		<b>2.</b> Select 'Connection' and create new 'Access Point'<br/>
		<b>3.</b> Name your new connection and enter the APN for your network provider. See http://www.mig33.com for a list of APN for your country, or call your network provider.<br/>
		<b>4.</b> Save your new connection settings<br/>
		<b>5.</b> Go back to 'Connections' in Settings and select 'Packet Data'<br/>
		<b>6.</b> Leave 'Access Point' blank<br/>
	<p>When you login to mig33, you are prompted to select which connection you wish to choose.  Select the new connection that you have created or the Internet setting sent to you by your Telco. If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
	<p>* You can also get the settings sent to you from Nokia's website http://www.nokia.com.</p>
<?php
}
//ANY NOKIA MIDP 2.0 Phone
else if ( ($mobile_vendor == 'Nokia') && ($mobile_midp == '2.0') )
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to run mig33. This mobile phone requires a second Internet GPRS setting to connect to mig33 server via TCP/IP. Follow these steps to configure your phone correctly to connect to mig33 server:</p>
		<b>1.</b> You need to contact your network provider and ask them to send the 'Internet GPRS' settings to your mobile phone<br/>
		<b>2.</b> Once you get the settings sent to your phone, save it onto your phone.<br/>
		<b>3.</b> Go to 'Settings' and 'Configuration'<br/>
		<b>4.</b> Set the 'Default Configuration Settings' as the Internet GPRS settings sent to you by your network provider<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33 server.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
	<p>* You can also get the settings sent to you from Nokia's website http://www.nokia.com.</p>
<?php
}
//ANY Nokia MIDP 1.0 Phone
else if ( ($mobile_vendor == 'Nokia') && ($mobile_midp == '1.0') )
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to run mig33. Please note that you can only connect to mig33 server using HTTP connection. If you still cannot connect via HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}
//LG Chocolate
else if ( ($mobile_vendor == 'LG') && ($mobile_model == 'VX8500') )
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to run mig33. This mobile phone requires a second Internet GPRS setting to connect to mig33 server via TCP/IP. Call your network provider for settings, or setup manually following these steps:</p>
		<b>1.</b> Go to 'Menu' and 'My Stuff'<br/>
		<b>2.</b> Select 'Games and Apps' and go to 'Profiles'<br/>
		<b>3.</b> Create a new profile and enter the APN for your network provider. Go to http://www.mig33.com for a list of APN for your country, or contact your network provider for APN.<br/>
		<b>4.</b> Save your new profile and set it as default by activating it.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}

//O2 Atom
else if ( ($mobile_vendor == 'O2') && ($mobile_model == 'Atom') )
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to run mig33. You need a second Internet GPRS setting and your phone configured correctly to mig33 via TCP/IP. Follow these instructions to setup your mobile:</p>
		<b>1.</b> Go to 'Start', 'Settings' and go to 'Connections'<br/>
		<b>2.</b> Select 'Connections' and go to 'My Work Networks'<br/>
		<b>3.</b> Go to 'Add a new modem connection'<br/>
		<b>4.</b> Name your connection and select 'Cellular Line (GPRS) as your modem'<br/>
		<b>5.</b> Enter the 'Access point name' (APN) for your network provider. Go to http://www.mig33.com for a list of APN for your country, or contact your network provider for APN and select 'Finish'.<br/>
	<p>To configure your mobile phone:</p>
		<b>1.</b> Go to 'Start', 'Settings' and go to 'Connections'<br/>
		<b>2.</b> Select 'Connections' and go to 'Manage existing connections'<br/>
		<b>3.</b> Tap and hold on to the new connection that you have created.<br/>
		<b>4.</b> An option will pop up and select 'Connect'<br/>
	<p>You are now connecting using GPRS. Your mobile phone will be able to connect to mig33.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}
//ANY Motorola Phone
else if ($mobile_vendor == 'Motorola')
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a Motorola phone to connect to mig33 server. You need a second Internet GPRS setting and your phone configured correctly to connect to mig33 server via TCP/IP. Contact your network provider for settings or follow these instructions to manually setup your mobile:</p>
		<b>1.</b> Go to 'Web Access' and select 'Web Sessions'<br/>
		<b>2.</b> Select 'New Entry' and name it 'java session' (small caps)<br/>
		<b>3.</b> Enter the 'GPRS APN' for your network provider. Go to http://www.mig33.com for a list of APN for your country, or contact your network provider for APN<br/>
		<b>4.</b> Save your settings and set it as default<br/>
	<p>If you got your Internet GPRS settings from your network provider, follow these instructions to configure your mobile phone correctly:</p>
		<b>1.</b> Go to 'Web Access' and select 'Web Sessions'<br/>
		<b>2.</b> Select the Internet GPRS settings sent by your network provider and select 'Copy Settings'<br/>
		<b>3.</b> Rename your settings to 'java session' (small caps)<br/>
		<b>4.</b> Save your new settings and set it as default.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}
//Samsung D500 and D600 ONLY
else if ( ($mobile_vendor == 'Samsung') && (($mobile_model == 'D500') || ($mobile_model == 'D600') ))
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to connect to mig33 server. Your mobile phone will need to be configured correctly with a second Internet GPRS setting to connect to mig33 server.  Follow these instructions to configure your mobile phone:</p>
		<b>1.</b> Go to 'Applications' and select 'JAVA World'<br/>
		<b>2.</b> Go to 'Settings' and select 'APN'<br/>
		<b>3.</b> Set 'APN' to 'Internet' and leave all other fields blank.<br/>
		<b>4.</b> Disable 'Proxy'<br/>
	<p></p>
		<b>1.</b> Go to 'Settings' and 'Network services'.<br/>
		<b>2.</b> Go to 'Network Selection' and select 'Manual'. Select your network provider.<br/>
		<b>3.</b> Go to 'Settings' and 'Network services'<br/>
		<b>4.</b> Go to 'Band Selection' and select 'GSM900/1800'<br/>
		<b>5.</b> Restart your mobile phone by turning it off and on.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}
//ANY Windows Mobile
else if (($mobile_vendor == 'msie'))
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a Windows Mobile 5.0 phone to connect to mig33 server. Your mobile phone needs to be setup with a second Internet GPRS setting and configured properly to connect to mig33 server.  Follow these instructions to setup and configure your mobile phone:</p>
		<b>1.</b> Press 'Start' and go to 'Settings' and 'More'<br/>
		<b>2.</b> Go to 'Data Connections' and highlight 'Internet Connections'<br/>
		<b>3.</b> Press 'Menu' and go to 'Edit Connections'<br/>
		<b>4.</b> Press 'Menu' and go to 'Add'<br/>
		<b>5.</b> Enter the 'Access point' (APN) for your network provider. Go to http://www.mig33.com for a list of APN for your country, or contact your network provider for APN and select 'Done'.<br/>
		<b>6.</b> Select 'My GPRS' on the screen where you selected 'Internet Connections' earlier and select 'Done'.<br/>
		<b>7.</b> Restart your mobile phone by turning it off and on.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}
//Blackberry
else if ($mobile_vendor == 'Blackberry')
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to connect to mig33 server. Your Blackberry needs to be setup with a second Internet GPRS setting to connect to mig33 server via TCP/IP. Follow these instructions to setup your Blackberry:</p>
		<b>1.</b> Go to 'Options' and select 'Advanced Options'<br/>
		<b>2.</b> Select 'TCP'<br/>
		<b>3.</b> Enter the 'Access Point Name' for your network provider. Go to http://www.mig33.com for a list of APN for your country, or contact your network provider for APN.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
<?php
}
//All Sony Ericsson MIDP 1.0 Phone
else if (($mobile_vendor == 'Sony Ericsson') &&  ($mobile_midp = '1.0'))
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to connect to mig33 server. Your mobile phone requires a second Internet GPRS setting to connect to mig33 server.  Contact your network provider to ask them to send this setting to your mobile phone and follow these instructions to configure your mobile phone:</p>
		<b>1.</b> Go to 'Menu' and select 'Connectivity'<br/>
		<b>2.</b> Under 'WAP profiles', select the second Internet GPRS settings that was sent to you by your network provider.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33.</p>
	<p>Your mobile phone can only connect via HTTP and not TCP/IP.</p>
	<p>If you still cannot connect via HTTP, please try manually changing your connection to Port 9119.</p>
	<p>*You can also get the settings sent to you from Sony Ericsson's corporate website at http://www.sonyericsson.com under support.</p>
<?php
}
//All Sony Ericsson MIDP 2.0 Phone
else if (($mobile_vendor == 'Sony Ericsson') &&  ($mobile_midp = '2.0'))
{
?>
	<p align="center"><b>Instructions</b></p>
	<p>We have detected that you are using a <?php print $mobile_vendor . ' ' . $mobile_model ?> to connect to mig33 server. Your mobile phone requires a second Internet Setting to connect via TCP/IP and your mobile phone configured correctly.  Call your network provider for setting, or setup and configure manually following these steps:</p>
		<b>1.</b> Go to 'Menu' and select 'Connectivity'<br/>
		<b>2.</b> Go to 'Data Comm.' and select 'Data Accounts'<br/>
		<b>3.</b> Create your new profile by entering the Access Point Name for your network provider. Go to http://www.mig33.com a list of APN for your country, or contact your network provider for the APN.<br/>
	<p>Once you have the settings (either manually created or sent by your network provider:</p>
		<b>1.</b> Go back to 'Connectivity' and select 'Settings for Java'<br/>
		<b>2.</b> Highlight the new profile that you have created and select it.<br/>
	<p>When you login to mig33, your phone should access this Internet GPRS setting and allow you to connect to mig33.</p>
	<p>If you still cannot connect via TCP/IP or HTTP, please try manually changing your connection to Port 9119.</p>
	<p>*You can also get the settings sent to you from Sony Ericsson's corporate website at http://www.sonyericsson.com under support.</p>
<?php
}
?>