<?php
session_start();

//Check the actual URL the user is viewing. If the WWW prefix is missing, redirect.
$server = $_SERVER['HTTP_HOST'];
$request_uri = $_SERVER['REQUEST_URI'];
if (strtolower($server) == $redirectFrom)
{
	header('Location: ' . $actualPath . $request_uri);
	die();
}


//TODO - Check for AJAX Session Timeout somehow.

//Determine error message to display if any
if ($_GET['message'] == 1)
	$message = 'Please enter a valid username and password';
if ($_GET['message'] == 2)
	$message = 'Account inactive or suspended.';

$captcha_check = false;
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>migme homepage</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<meta name="Keywords" content="content" lang="en-us" xml:lang="en-us" />
		<meta name="Description" content="(actual description)" />

		<link rel="stylesheet" type="text/css" href="<?=$actualPath?>/css/main.css" media="screen" />

		<script type="text/javascript" src="<?=$actualPath?>/common/jscripts/main.js"></script>
		<script type="text/javascript" src="<?=$actualPath?>/common/jscripts/menu.js"></script>
		<script type="text/javascript" src="<?=$actualPath?>/common/jscripts/dom-drag.js"></script>
		 <script type="text/javascript" src="<?=$actualPath?>/common/jscripts/scroller.js"></script>

 </head>
  <body>
		<!-- maintable -->
		<table class="main" cellspacing="0" cellpadding="0">
			<tr>
				<td colspan="2" class="pad_b10">
					<!-- table # -->
					<table class="head_table" cellspacing="0" cellpadding="0">
						<tr>
							<td rowspan="2" class="logo_cell"></td>
							<td class="empty_cell_1">&nbsp;</td>
							<td class="banner_cell">
								<img src="<?=$actualPath?>/img/banner.png" height="60" width="468" alt="Roam the earth. Without spending it." />
							</td>
						</tr>
						<tr>
							<td class="lang_cell">
								<!-- table # -->
								<table style="width: 194px; margin: 4px 0px 0px 64px;" cellspacing="0" cellpadding="0">
									<tr>
										<td class="bold" colspan="7"><!--language:--></td>
									</tr>
									<tr>
										<td>
										</td>
									</tr>
								</table>
								<!-- /table # -->
							</td>
							<td>

								<?php
								if (!$captcha_check)
								{
								?>

								<!-- table login / logged in -->
								<table class="log_table" cellspacing="0" cellpadding="0">
									<?php
									if (strlen($message) > 0)
									{
										print '<td colspan="4" align="center"><font color="red">' . $message . '<br/></td>';
									}
									?>

									<tr>
										<?php
										if (!isset($_SESSION['user']))
										{
										?>
											<form action="member/login.php" method="POST">
											<td rowspan="2" class="v_separator">&nbsp;</td>
											<td class="username_cell"><div class="username_block">username</div></td>
											<td colspan="2" class="pass_cell"><div class="pass_block">password</div></td>
										<?php } else { ?>
											<td rowspan="2" class="v_separator">&nbsp;</td>
											<td colspan="2"><div class="username_block">logged in</div></td>
										<?php
										}
										?>
										<td rowspan="2" class="v_separator">&nbsp;</td>
										<td class="search_cell" colspan="2">&nbsp;</td>
									</tr>
									<tr>
										<?php
										if (!isset($_SESSION['user']))
										{
										?>
											<td class="valign_t">
												<input class="ctrl_field" type="text" name="username" size="10" />
											</td>
											<td class="valign_t">
												<input class="ctrl_field" type="password" name="password" size="10" />
											</td>
											<td class="log_button_cell">
												<input type="image" src="<?=$actualPath?>/img/log_button.png" />
											</td>
											</form>
										<?php } else { ?>

											<td class="welcome_cell">
											welcome back, <b> <?php print $_SESSION['user']['username'] ?></b>
											</td>
											<td class="action_cell">
											<input type="image" src="<?=$actualPath?>/img/logout_button.png" onclick="globalRedirect('<?=$actualPath?>/member/logout.php')" title="log out" />
											</td>
										<?php
										}
										if (!isset($_SESSION['user']))
										{
										?>
											<td class="search_cell_2" valign="center">
												<a href="/forgotpassword.php">Forgot Details</a>
											</td>
										<?php
										}
										?>
									</tr>
								</table>
								<!-- /table login / logged in -->
								<?php } ?>
							</td>
						</tr>
					</table>
					<!-- /table # -->
				</td>
			</tr>
