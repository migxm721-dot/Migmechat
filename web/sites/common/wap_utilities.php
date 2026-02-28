<?php
require_once("utilities.php");
fast_require("Style", get_framework_common_directory() . "/style.php");

function show_wap_avatar($avatar_filename, $data, $width = 48)
{
	//TODO: Get width data from session data
	if ($width != 0)
		$data['width'] = $width;

	$data["avatar_filename"] = $avatar_filename;

	Modules::include_module("avatar", $data);
}

function show_header_2($title, $class = '')
{
	if (empty($class)) {
		printf('<h2>' . $title . '</h2>');
	} else {
		printf('<h2 class="' . $class . '">' . $title . '</h2>');
	}
}

function show_header_3($title)
{
	printf('<h3>' . $title . '</h3>');
}

function show_header_4($title)
{
	printf('<h4>' . $title . '</h4>');
}

/**
 *
 *    Shows title, with defined title
 *
 **/
function show_wap_title($title, $class = '')
{
	show_header_2($title, $class);
}

/**
 *
 *
 *    Shows a info block section with info and possible link
 *
 **/
function show_wap_notification_string($content)
{
	//printf('<div class="notif border_all">'.$content.'</div>');
	printf('<div class="positive message-info">' . $content . '</div>');
}


function get_wap_highlight_div()
{
	printf('<div class="highlight border_highlight_all">');
}

function get_wap_closing_div()
{
	printf('</div>');
}

/**
 *
 * Print out a success message in blue
 *
 **/
function show_wap_success_message($message)
{
	printf('<p class="success message-success">%s<br class="clear" /></p>', $message);
}

/**
 *
 * Print out a error message in red
 *
 **/
function show_wap_error_message($message)
{
	printf('<p class="error message-error">%s<br class="clear" /></p>', $message);
}

//Converts new line to double br tag
if (!function_exists('nl_conversion_to_br')) {
	function nl_conversion_to_br($string)
	{
		//return preg_replace('/\<br(\s*)?\/?\>/i', "\n", $string);
		return preg_replace('/\n/', "<br/><br/>", $string);
	}
}
?>