<?php
	$message = "";
	if( isset($_GET["msg"]))
		$message = $_GET["msg"];
	$msisdn = "";
	if( isset($_GET["m"]) )
		$msisdn = $_GET["m"];
?>

<html>
	<head>
<meta http-equiv="Content-Language" content="en-us">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<link href="indosat.css" rel="stylesheet" type="text/css" />

		<title>Indosat</title>
	</head>

<body marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">

<table border="0" cellpadding="0" cellspacing="0" align="center" width="220" >
  <tr>
    <td>
       <table border="0" cellpadding="0" cellspacing="0" width="100%">
      	 <td width="100%"  align="left" >
             <p align="left"><img border="0" src="isat_icon.gif" ></p>
          </td>
          <td width="100%"  align="right" >
             <p align="right"><img border="0" src="im3.jpg" ></p>
          </td>
       </table>
    </td>
  </tr>
  <tr>
    <td  align="center" width="100%" bgcolor="#00FF00" height="2"></td>
  </tr>
<tr>
<td  align="center" width="100%" colspan="3" height="5"><img border="0" src="spacer.gif" width="1" height="5"></td>
</tr>
  <tr>
    <td width="100%">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	  <tr>
<?php
		if( !empty($message) )
		{
?>
			<td align="center">
				<p>Sorry, your phone is unrecognized.</p>
				<p>Make sure your GPRS Setting uses IP Proxy 10.19.19.19 and port 8080.</p>
			</td>
<?php
		}
		else
		{
			$phone_number = base64_decode($msisdn);
?>
			<td  align="center" ><p>Nomor Anda kami kenali sebagai <?=$phone_number?>, namun nomor ini tidak terdaftar dalam mig33. Jika Anda adalah anggota mig33, silakan perbaharui data Anda dengan nomor ini sebelum kami dapat menggabungkan Anda ke VIP Access.</p>
			<p>Untuk memperbaharui data Anda, silakan ke menu 'Account Settings/Manage Accounts' dan pilih 'Change mobile phone number.' Setelah Anda meng-autentikasi-kan dan memperbaharui datamu dengan nomor ini, Anda akan otomatis bergabung ke Indosat VIP Access. Untuk keterangan lebih lanjut email ke contact@mig33.com</p>
			</td>
<?php
		}
?>
	  </tr>
	  	</table>
    </td>
  </tr>
<tr>
    <td  align="center" width="100%" bgcolor="#000000" height="1"></td>
  </tr>

 <tr>
   <td>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
         <td width="100%" height="40" nowrap ><font face="Arial" size="2">Copyright &copy;2009. PT Indosat, tbk.<br>All right reserved</font>
         </td>
        </tr>
       </table>
   </td>
  </tr>
</table>
<br><br>
</body>
</html>