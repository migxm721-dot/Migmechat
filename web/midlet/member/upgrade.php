<html>
  <head>
    <title>Get New Version</title>
  </head>
  <body bgcolor="white">
    <p><b>Get New mig33 Version</b></p><br>
    <p>Don't wait and download an updated mig33 application! There is a ton of new features like instant status updates, extended profiles, and app. themes!</p><br>

<?php
	$headers = apache_request_headers();
	$midletVersion = $headers['ver'];

	if (is_numeric($midletVersion)) {
		if( $midletVersion >= 4.00 )
			print '<p><a href="mig33:invokeNativeBrowser(http://wap.mig33.com/download.php)">Get new mig33 now!</a></p>';
		else
			print '<p><tag type="4" href="http://wap.mig33.com/download.php">Get new mig33 now!</a></p>';
	}
?>
  </body>
</html>