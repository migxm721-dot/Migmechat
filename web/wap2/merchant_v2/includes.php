<?php
$member2_dir = "/member2";

// check if we're displaying for WAP or Midlet
$TYPE_PAGELET = 'pagelet';
$TYPE_WAP = 'wap';
$outputType = (($_GET['output'] == $TYPE_PAGELET || $_POST['output'] == $TYPE_PAGELET) ? $TYPE_PAGELET : $TYPE_WAP);

// for pagelets, set up absolute paths
global $server_root;

// set up includes
if (empty($commonIncPath)) {
	include_once("..".$member2_dir."/common-inc-kk.php");
} else {
	include_once($commonIncPath);
}

include_once("..".$member2_dir."/emit.php");
include_once("..".$member2_dir."/check.php");
require_once("../../common/merchantFunctions.php");
require_once("../../common/language.php");
require_once("mc_functions.php");

// for pagelets, url rewrite to add output type to ensure correct output format is displayed
if ($outputType == $TYPE_PAGELET) {
	ob_start("rewriteServerPaths");
	output_add_rewrite_var('output', 'pagelet');
}
?>