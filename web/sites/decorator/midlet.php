<?php
	require_once(get_common_config_location());
	require_once(get_framework_common_directory() . "/web_utilities.php");
	require_once(get_framework_common_directory() . "/pagelet_utilities.php");
	fast_require('PageletMenu', get_framework_common_directory() . '/pagelet_menu.php');

	global $mogileFSImagePath, $server_root;

	// Page Title
	if(!isset($page_title))
	{
		$page_title = 'migme';
	}
	else
	{
		$username = get_attribute_value('username', 'string', $session_user);
		$page_title = str_replace(array('%USERNAME%'), array($username), $page_title);
	}
?>

<html>
	<head>
		<title><?=$page_title?></title>
	</head>
	<body>
		<?php include_once($body_template); ?>
		<br>
		<img src="<?php echo googleAnalyticsGetImageUrl('midlet'); ?>">

		<?php
			// GA measurement Protocol
			require_once (PACKAGESPATH . 'mig33/libraries/composer/vendor/autoload.php');
			$config = array();
			global $ga_account;
			$ga_account = substr_replace($ga_account, "7", -1); // remove this once MP is proven to be working fine
			$client = Krizon\Google\Analytics\MeasurementProtocol\MeasurementProtocolClient::factory($config);
			try {
				// derive the user ip, check if its behind a proxy (j2me uses XFF)
				if (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
					$ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
				} else {
					$ip = $_SERVER['REMOTE_ADDR'];
				}

				$client->pageview(array(
					'v'   => '1',
					'tid' => $ga_account, // Tracking Id
					'cid' => get_cid(), // Customer Id
					't'   => 'pageview',
					'dh'  => $_SERVER['SERVER_NAME'],
					'dp'  => $_SERVER['REQUEST_URI'],
					'uip' => $ip
				));
			} catch (Exception $ex) {
				if (DEBUG) {
					error_log("GA MeasurementProtocol Exception: " . $ex->getMessage());
				}
			}
		?>
	</body>
</html>