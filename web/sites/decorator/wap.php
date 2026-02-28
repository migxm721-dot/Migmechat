<?php require_once(get_framework_common_directory() . '/wap_utilities.php'); ?>
<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN" "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="cache-control" content="no-store, no-cache, must-revalidate, post-check=0, pre-check=0" />
		<meta http-equiv="pragma" content="no-cache" />
		<title>migme - Wap</title>

		<link href="/sites/resources/css/wap-blue-common.css" rel="stylesheet" type="text/css" />

		<?php if(true)://isset($device_info) && $device_info->resolution_width >= 200): ?>
			<link href="/sites/resources/css/wap-blue.css" rel="stylesheet" type="text/css" />
		<?php else: ?>
			<link href="/sites/resources/css/wap-blue-small.css" rel="stylesheet" type="text/css" />
		<?php endif; ?>
	</head>
	<body>
<?php
		include_once($body_template);
?>
	</body>
</html>