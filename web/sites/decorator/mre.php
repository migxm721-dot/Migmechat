<?php
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
		<title><?=_($page_title)?></title>
	</head>
	<body>
		<?php include_once($body_template); ?>
		<br>
		<img src="<?php echo googleAnalyticsGetImageUrl('mre'); ?>">
	</body>
</html>