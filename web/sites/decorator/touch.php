<?php
	require_once(get_framework_common_directory() . "/touch_utilities.php");

	$img_ga = googleAnalyticsGetImageUrl('touch');
	$js_extension = $this->debug?'.js':'.min.js';
	$js_controller = get_resources_directory() . '/js/touch/' . get_controller() . $js_extension;
	$js_controller = file_exists($js_controller) ? get_controller() . $js_extension : 'controller' . $js_extension;
?>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
		<link rel="stylesheet" type="text/css" href="/sites/resources/css/jquery.mobile.css" />
		<link rel="stylesheet" type="text/css" href="/sites/resources/css/jquery.mobile.scrollview.css" />


		<?php
			// NOTE : Patch to override v3 styles to v4 for only version 4. This is temporary solution
			// Till we move all pagelets to v4.
			  if(floor(ClientInfo::get_version_number()) == 4 && ClientInfo::is_android_client()) { ?>
					<link rel="stylesheet" type="text/css" href="/sites/resources/css/touch.overrides.min.css" />
					<? if (get_controller() == 'account' || get_controller() == 'payment' ) : ?>
						<link rel="stylesheet" type="text/css" href="/sites/resources/css/touch-v5.css" />
					<? endif; ?>
        <?php } elseif(floor(ClientInfo::get_version_number()) >= 5 && ClientInfo::is_android_client()) { ?>
					<link rel="stylesheet" type="text/css" href="/sites/resources/css/touch.overrides.min.css" />
					<link rel="stylesheet" type="text/css" href="/sites/resources/css/touch-v5.css" />
        <?php } else { ?>
					<link rel="stylesheet" type="text/css" href="/sites/resources/css/touch-v3-blue.css" />
    	<?php } ?>

        <?php
			if(isset($header_template))
				include_once($header_template);

			if($this->debug) //$this refers to the Controller instance
				echo '<script type="text/javascript">var DEBUG_MODE = true;</script>';

			/*
			 * Javascript example of the use of $.template
			 * @link http://api.jquery.com/jQuery.template/
			 *
			var markupMap = {
				"dataset1":{
					"tmpl":'<a href="${url}">${text}</a>'
					"target":".CSS#selector"
				},
				"dataset2":{
					"tmpl":'<li><a href="${url}">${text}</a> - ${description}</li>'
					"target":"tag#CSS.selector"
				}
			}

			in sites/view/{controller}/json/template/{view}_template.php:

			$json[get_controller().'_'.get_action()] = array(
				'dataset1' => array(//must be an array even if only one set of data
					array(
						'url'=>'http://blar',
						'text'=>'<b>html1</b>'
					),
				),
				'dataset2' => array(
					array(
						'url'=>'http://blar',
						'text'=>'<img src="..." />html2',
						'description'=>'description of description'
					),
					array(
						'url'=>'http://blar',
						'text'=>'<b>html1</b>',
						'description'=>'another description of <i>another</i> description'
					),
				)
			)

			/**/
		?>
		<title>migme</title>
    </head>
	<body<?php echo isset($body_class) ? ' class="' . $body_class . '"' : ' class="ui-mobile-viewport"' ; ?>>
		<?php if (1==2 && !empty($buzzcity_ad_header)) { Modules::include_module("buzzcity"); } ?>
		<div data-role="page" class="mig-container" style="position:relative;">
			<?php
				include_once($body_template);
				global $ga_account;
			?>
		</div>
		<script type="text/javascript" src="/sites/resources/js/jquery-1.4.4.min.js"></script>
		<script type="text/javascript" src="/sites/resources/js/jquery-tmpl.dev.js"></script>
		<script type="text/javascript">
			var controller = '<?php echo get_controller(); ?>';
			var action = '<?php echo get_action(); ?>';
			var view = 'json';
			<?=isset($mig33_param)?"var param = '".$mig33_param."';":''; ?>
		</script>
		<script type="text/javascript" src="/sites/resources/js/touch/mig33<?=$js_extension?>"></script>
		<script type="text/javascript" src="/sites/resources/js/touch/<?=$js_controller?>"></script>
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
