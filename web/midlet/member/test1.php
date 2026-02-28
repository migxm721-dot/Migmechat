<?php
include_once("../../common/common-inc.php");
//ice_check_session();
//$headers = apache_request_headers();
/*
$sessionID = $headers['sid'];
$mig33 = $headers['mig33'];
$ver = $headers['ver'];
$sw = $headers['sw'];
$sh = $headers['sh'];
?>
<html>
  <head>
    <title>My Scrapbook</title>
  </head>
  <body bgcolor="white">
  <p>ver:<?=$ver?></p>
  <p>sid:<?=$sessionID?></p>
  <p>mig33:<?=$mig33?></p>
  <p>sw:<?=$sw?></p>
  <p>sh:<?=$sh?></p>
  </body>
</html>

<?php
die();
*/




?>
<html>
  <head>
    <title>My Scrapbook</title>
  </head>
  <body bgcolor="white">
  	<?php
  		if($_POST){
			print '<p>to:'.$_POST['to'].'</p>';
			print '<p>sub:'.$_POST['subject'].'</p>';
			print '<p>but1:'.$_POST['but1'].'</p>';
			print '<p>but2:'.$_POST['but2'].'</p>';
			print_r($_POST);

		}

  	?>
	<form method="POST" action="test1.php">
		<p><b>First</b></p>
		<p><input type="text" name="to" value="<?=$to?>" size="10" >
		<p><input type="submit" name="but1" value="First"></p>
	</form>
	<br>
	<form method="POST" action="test1.php">
		<p><b>Second</b></p>
		<p><input type="text" name="subject" value="<?=$subject?>" size="10"></p>
		<p><input type="submit" name="but2" value="Second"></p>
	</form>

  </body>
</html>