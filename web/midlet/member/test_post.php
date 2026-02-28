<?php

include_once("../../common/common-inc.php");

global $imap_server;
global $imap_admin_port;
global $imap_admin_username;
global $imap_admin_password;

$sock = fsockopen($imap_server, $imap_admin_port);
if (!$sock)
	throw new Exception("Could not connect to mail server.");

$data = "cmd=" . urlencode("cmd_user_login") .
		"&lcmd=" . urlencode("user_change_pass") .
		"&username=" . urlencode($imap_admin_username) .
		"&password=" . urlencode($imap_admin_password) .
		"&lusername=" . urlencode("danielgoth") .
		"&lpassword=" . urlencode("gothika") .
		"&lpassword_again=" . urlencode("gothika");

fwrite($sock, "POST /cgi/admin.cgi HTTP/1.0\r\n");
fwrite($sock, "Host: " . $_SERVER['HTTP_HOST'] . "\r\n");
fwrite($sock, "Content-Length: " . strlen($data) . "\r\n");
fwrite($sock, "Content-type: application/x-www-form-urlencoded\r\n");
fwrite($sock, "\r\n");
fwrite($sock, "$data\r\n");

fclose($sock);

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<HTML><HEAD></HEAD>
<BODY>
<FORM NAME="Main" METHOD="POST" ACTION="http://surge.reality.com.au:7026/cgi/user.cgi">
<INPUT TYPE="hidden" NAME="cmd" VALUE="cmd_user_login">
<INPUT TYPE="hidden" NAME="lcmd" VALUE="user_change_pass">
<INPUT TYPE="hidden" NAME="username" VALUE="daniel"><br>
<INPUT TYPE="hidden" NAME="password" VALUE="daniel"><br>

Name <INPUT TYPE="text" NAME="lusername" VALUE=""><br>
Pass <INPUT TYPE="text" NAME="lpassword" VALUE=""><br>
Pass2 <INPUT TYPE="text" NAME="lpassword_again" VALUE=""><br>
<INPUT TYPE="submit" NAME="Submit" VALUE="Update">
</FORM>
</BODY>
</HTML>