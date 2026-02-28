<?php
	require_once(get_framework_common_directory() . "/touch_utilities.php");
	$img_ga = googleAnalyticsGetImageUrl('touch');
?>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0" />
        <link rel="stylesheet" type="text/css" href="/sites/resources/css/touch.min.css" />
        <?php
			if(isset($header_template))
				include_once($header_template);

			if($this->debug) //$this refers to the Controller instance
				echo '<script type="text/javascript">var DEBUG_MODE = true;</script>';

		?>
		<title>migme</title>
    </head>
	<body<?php echo isset($body_class) ? ' class="' . $body_class . '"' : ' class="ui-mobile-viewport"' ; ?>>
		<div data-role="page" class="container" >
			<?php include_once($body_template); ?>
		</div>
		<script type="text/javascript">
			var controller = '<?php echo get_controller(); ?>';
			var action = '<?php echo get_action(); ?>';
			var view = 'json';
			var user_id = '<?php echo $session_user_id; ?>';
			var user_name = '<?php echo $session_user; ?>';
			<?=isset($mig33_param)?"var param = '".$mig33_param."';":''; ?>
		</script>
        <script data-main="/sites/resources/js/touch/v4/main" src="/sites/resources/js/touch/v4/lib/require.min.js"></script>
        <script src="/sites/resources/js/touch/v4/lib/relative.time.js"></script>
        <?php global $ga_account; ?>
		<script>
			(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
				(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
				m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
			})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

			ga('create', '<?=$ga_account?>', 'auto');
			ga('send', 'pageview');
		</script>
    </body>
</html>
