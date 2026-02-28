<?php
	require_once(get_framework_common_directory() . '/wap_utilities.php');
	$js_version = SystemProperty::get_instance()->get_string(SystemProperty::mig33JSClient_Version, '33');
	// If it is a hash, we are in QA mode
	if(strlen($js_version) === 7)
	{
		$js_version = 'QA/'.$js_version;
	}
    $http_protocol = 'http://';
	if (! empty($_SERVER['HTTPS']))
    {
        $http_protocol = 'https://';
        foreach (array('migbo_cdn_url', 'cdn_url') as $url)
            $GLOBALS[$url] = str_replace('http://', 'https://', $GLOBALS[$url]);
    }

	global $migcore_migbo_server_root, $cdn_url;
?>
<!DOCTYPE html>
<html>
	<head>
		<?php $noredir = get_value('noredir'); ?>
		<?php if (empty($_SERVER['HTTPS']) && empty($noredir)): ?>
			<script type="text/javascript">
				(function(w){ if (self==top && w.match(/\/(sites|debug)\//) && ! w.match(/\/v4\//)) window.location = w.replace(/\/(sites|debug)\//, "/v4/$1/"); })(window.location.href);
			</script>
		<?php endif; ?>
		<title>migme<?=isset($page_title) ? ' - ' . $page_title : '' ?></title>
		<script type="text/javascript" src="<?=$server_root?>/sites/resources/js/libs/jquery-1.10.1.min.js"></script>
		<?php if (DEBUG_MODE): ?>
			<link href="<?=$migcore_migbo_server_root?>/debug/app/styles/web.less" rel="stylesheet/less" type="text/css" />
			<script type="text/javascript">
				var less = {
					  env: "development"			// or "production"
					, async: false					// load imports async
					, fileAsync: false				// load imports async when in a page under
					, poll: 1000         			// when in watch mode, time in ms between polls
					, functions: {}      			// user functions, keyed by name
					, dumpLineNumbers: "comments"	// or "mediaQuery" or "all"
					, relativeUrls: false
				};
			</script>
			<script src="<?=$migcore_migbo_server_root?>/debug/app/components/less.js/dist/less-1.7.0.min.js"></script>
		<?php else: ?>
			<?php if(empty($_SERVER['HTTPS'])): ?>
				<link href="<?=$http_protocol?>web<?=$GLOBALS['session_cookie_domain']?>/<?=$js_version?>/app/styles/web.min.css" rel="stylesheet" type="text/css" />
			<?php else: ?>
				<link href="<?=$http_protocol?>dpr5osk8xbowq.cloudfront.net/<?=$js_version?>/app/styles/web.min.css" rel="stylesheet" type="text/css" />
			<?php endif; ?>
		<?php endif; ?>
		<link href="<?=$server_root?>/sites/resources/css/migbo.css" rel="stylesheet" type="text/css" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<?php
			if(isset($header_template))
				include_once($header_template);
		?>
	</head>
	<body class="<?=$body_class?>" style="padding: 0 10px;">
		<?php
			include_once($body_template);
			global $ga_account;
			$domain_no_dot = substr($GLOBALS['session_cookie_domain'], 1);
		?>
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