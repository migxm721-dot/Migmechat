<?php
	// extracts from migbo-web $i18n, $variable, $build_number
	extract(require_migbo_variables());
	$css_build_date = date('dmY'); // using day to make it rebuild every day automatically

	// migCore WAP Utilities
	require_once(get_framework_common_directory() . '/wap_utilities.php');

	// Page Title
	if(!isset($page_title))
		$page_title = _('migme');
	else
		$page_title = $page_title.' - ';

	if(empty($session_user))
		$title = $page_title;
	else
		$title = sprintf(_('%s%s - migme'), (isset($page_title) && $page_title != 'migme') ? $page_title : '', $session_user);

	$alerts = 0;
	if(isset($count))
		$alerts = $count['migalerts'] + $count['messages'];
?>
<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN" "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd">
<html>
	<head>
		<title><?=strip_tags($title)?></title>
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<meta name="author" content="migme" />
		<meta name="description" content="Share your thoughts anytime & anywhere, Add others and Connect with the world!" />
		<meta name="keywords" content="migme, miniblog, microblog, mobile games, chat, chatroom, social games, IM, mobile messaging, messenger aggregator, virtual gifts, avatar, text messaging" />
		<?php if(!DEBUG_MODE): ?>
			<link href="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/css/wap.min.css?v=<?=$css_build_date?>" rel="stylesheet" type="text/css" />
		<?php else: ?>
			<link href="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/css/wap.css" rel="stylesheet" type="text/css" />
		<?php endif; ?>
		<link href="<?=$GLOBALS['server_root']?>/sites/resources/css/migbo-wap.css" rel="stylesheet" type="text/css" />
		<link href="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/favicon.ico" rel="shortcut icon" type="image/x-icon" />
		<link href="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/favicon.ico" rel="icon" type="image/x-icon" />
	</head>
	<body>
		<div id="structure">
			<div id="header">
				<table cellpadding="0" cellspacing="0" border="0" height="100%">
					<?php if(!empty($session_user)): ?>
						<tr>
							<td>
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/home" title="<?=_('migme')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/mig33_logo.png" width="48" height="24" alt="<?=_('migme')?>" title="<?=_('migme')?>" /></a>
							</td>
							<td class="header-menu-icon selected">
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/discover" title="<?=_('Discover')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/icons/24x24/migworld.png" alt="<?=_('Discover')?>" title="<?=_('Discover')?>" /></a><br />
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/discover" title="<?=_('Discover')?>"><?=_('Discover')?></a>
							</td>
							<td class="header-menu-icon selected">
								<a href="<?=get_framework_url('bot', 'bot_list', 'wap')?>" title="<?=_('Games')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/icons/24x24/games.png" alt="<?=_('Games')?>" title="<?=_('Play')?>" /></a><br />
								<a href="<?=get_framework_url('bot', 'bot_list', 'wap')?>" title="<?=_('Games')?>"><?=_('Play')?></a>
							</td>
							<td class="header-menu-icon selected">
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/migalert" title="<?=_('Notifications')?>"><div class="red-circle-count"><?=($alerts > 99 ? 'N' : $alerts)?></div></a>
								&nbsp;
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/migalert" title="<?=_('Notifications')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/icons/24x24/alert.png" alt="<?=_('Notifications')?>" title="<?=_('Notifications')?>" /></a><br />
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/migalert" title="<?=_('Notifications')?>"><?=_('Notifications')?></a>
							</td>
							<td class="header-menu-icon selected">
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/search" title="<?=_('Search')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/icons/24x24/search.png" alt="<?=_('Search')?>" title="<?=_('Search')?>" /></a><br />
								<a href="<?=$GLOBALS['migbo_server_root']?>/wap/search" title="<?=_('Search')?>"><?=_('Search')?></a>
							</td>
							<td class="header-menu-icon selected">
								<a href="<?=get_framework_url('wap_portal', 'more', 'wap')?>" title="<?=_('More')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/icons/24x24/more.png" alt="<?=_('More')?>" title="<?=_('More')?>" /></a><br />
								More
							</td>
						</tr>
					<?php else: ?>
						<tr>
							<td>
								<a href="<?=$GLOBALS['login_server_root']?>/wap/login" title="<?=_('migme')?>"><img src="<?=$GLOBALS['migcore_migbo_server_root']?>/resources/img/mig33_logo.png" width="48" height="24" alt="<?=_('migme')?>" title="<?=_('migme')?>" /></a>
							</td>
							<td width="80%" id='welcome-to-migme'><?=_('Welcome to migme')?></td>
						</tr>
					<?php endif; ?>
				</table>
			</div>
			<?php if(1==2 && empty($session_user)): // header not to show login & sign up ?>
				<?php
			      	if(empty($custom_login_return_page))
				    {
					    $login_link_url = $GLOBALS['login_server_root'] . '/wap/login';
				    }
				    else
				    {
						$login_link_url = $custom_login_return_page;
				    }
				?>
				<div id="sub_header" style="padding:10px;">
					<a href="<?=$login_link_url?>" title="<?=_('Login')?>"><?=_('Login')?></a>
					&nbsp;|&nbsp;
				<?php if(empty($disable_decorator_outgoing_links)): ?>
					<a href="<?=get_framework_url('registration', 'register', 'wap')?>" title="<?=_('Sign Up')?>"><?=_('Sign Up')?></a>
				<?php else: ?>
					<span><?=_('Sign Up')?></span>
				<?php endif; ?>
				</div>
			<?php endif; ?>

			<?php if (1==2 && !empty($buzzcity_ad_header)) { Modules::include_module("buzzcity"); } ?>

			<?php include_once($body_template); ?>

			<?php if(empty($disable_decorator_outgoing_links)): ?>
			<div id="footer_nav">
				<?php if(!empty($session_user)): ?>
					<a href="<?=$GLOBALS['corporate_url']?>/downloads" title="<?=_('Download')?>"><?=_('Download')?></a>
					&nbsp;|&nbsp;
					<a href="<?=get_framework_url('settings', 'account_profile', 'wap')?>" title="<?=_('Settings')?>"><?=_('Settings')?></a>
					&nbsp;|&nbsp;
					<a href="<?=get_framework_url('account', 'home', 'wap')?>" title="<?=_('Credits')?>"><?=_('Credits')?></a>
					&nbsp;|&nbsp;
					<a href="<?=$GLOBALS['migbo_server_root']?>/wap/discover/emailrefer" title="<?=_('Invite')?>"><?=_('Invite')?></a>
					&nbsp;|&nbsp;
					<a href="<?=$GLOBALS['corporate_url']?>/faq" title="<?=_('Help')?>"><?=_('Help')?></a>
					&nbsp;|&nbsp;
					<a href="<?=get_logout_url('', false)?>" title="<?=_('Logout')?>"><?=_('Logout')?></a>
				<?php else: ?>
					<a href="<?=$GLOBALS['corporate_url']?>/downloads" title="<?=_('Download')?>"><?=_('Download')?></a>
					&nbsp;|&nbsp;
					<a href="<?=$GLOBALS['migbo_server_root']?>/wap/discover" title="<?=_('Discover')?>"><?=_('Discover')?></a>
					<?/* products are not ready for rebranding
					&nbsp;|&nbsp;
					<?=anchor('wap/discover', _('Discover'))?>
					<a href="<?=$GLOBALS['migbo_server_root']?>/products" title="<?=_('Learn More')?>"><?=_('Learn More')?></a>
					*/
					?>
					&nbsp;|&nbsp;
					<a href="<?=$GLOBALS['corporate_url']?>/faq" title="<?=_('Help')?>"><?=_('Help')?></a>
				<?php endif; ?>
			</div>
			<?php endif; ?>
			<div id="footer">
				<?=sprintf(_('Copyright &copy; %s Project Goth. All Rights Reserved.'), date('Y')); ?>
			</div>
		</div>
		<div id="ga">
			<img src="<?php echo googleAnalyticsGetImageUrl('wap'); ?>" />

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
		</div>
	</body>
</html>
