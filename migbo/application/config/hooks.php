<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');
/*
| -------------------------------------------------------------------------
| Hooks
| -------------------------------------------------------------------------
| This file lets you define "hooks" to extend CI without hacking the core
| files.  Please see the user guide for info:
|
|	http://codeigniter.com/user_guide/general/hooks.html
|
*/

$hook['pre_controller'] = array(
 	  'class'	=> 'XHProf'
 	, 'function'=> 'xhprof_start'
 	, 'filename'=> 'xhprof.php'
 	, 'filepath'=> 'hooks'
 	, 'params'	=> array()
);
$hook['post_controller'] = array(
	  'class'	=> 'XHProf'
	, 'function'=> 'xhprof_end'
	, 'filename'=> 'xhprof.php'
	, 'filepath'=> 'hooks'
	, 'params'	=> array()
);
$hook['post_controller_constructor'][] = array(
	  'class'    => 'RateLimit_FloodControl'
	, 'function' => 'detect_flooding'
	, 'filename' => 'RateLimit_FloodControl.php'
	, 'filepath' => 'libraries/floodcontrol'
);
$hook['post_controller_constructor'][] = array(
	  'class'    => 'Captcha_FloodControl'
	, 'function' => 'detect_flooding'
	, 'filename' => 'Captcha_FloodControl.php'
	, 'filepath' => 'libraries/floodcontrol'
);

/* End of file hooks.php */
/* Location: ./application/config/hooks.php */