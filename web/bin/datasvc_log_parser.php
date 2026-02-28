<?php

$_SERVER['DOCUMENT_ROOT'] = $_SERVER['PWD'];
$_SERVER['HTTPS'] = null;
$_SERVER['SERVER_PORT'] = 80;
$_SERVER['SERVER_NAME'] = 'localhost-devlab.projectgoth.com';
if (! function_exists('apache_request_headers'))
{
	function apache_request_headers(){ return array(); }
}

class SessionUtilities {/* mock SessionUtilities */}
require_once 'sites/common/utilities.php';
require_once '../migbo-web/application/libraries/migbodatasvc.php';
require_once 'sites/lib/fusion/fusion_rest.php';
//require_once 'sites/lib/fusion/migbo_datasvc.php';
require_once 'sites/lib/fusion/migbo_web.php';

function api_exists($path, $query, $check=false)
{
	foreach(array('/fusion-rest/migbo-datasvc-proxy', '/fusion-rest', '/migbo_datasvc', '/b/midlet', '/b/wap') as $api_path)
		if (strpos($path, $api_path) === 0)
		{
			$path = str_replace($api_path, '', $path);
			break;
		}
	foreach(array('FusionRest', 'MigboDatasvc', 'MigboWeb') as $class)
		if ($class::api_exists($path))
			return array($check ? false : $class::api_exists($path), $api_path);

	parse_str($query, $query);
	foreach ($query as $key=>&$value)
	{
		if ($value !== "1") $value = '%s';
		if ($key == "days") $value = '%d';
	}
	$query = str_replace('%25d', '%d', str_replace('%25s', '%s', http_build_query($query)));

	$path = $path . (empty($query) ? '' : '?' . $query);
	foreach(array('FusionRest', 'MigboDatasvc', 'MigboWeb') as $class)
		if ($class::api_exists($path))
			return array($check ? false : $class::api_exists($path), $api_path);
	return array($path, $api_path);
}

if (! is_readable($argv[1])) exit('Not readable' . "\n");

$check = in_array('--check', $argv);
$sourcefile = $argv[1];
$reportfile = $sourcefile . '.report.csv';

$handle = fopen($sourcefile, 'r');
$report = fopen($reportfile, 'a');
while (($buff = fgetcsv($handle)) !== false)
{
	list($buff[3], $buff[8]) = api_exists($buff[3], $buff[8], $check);
	if ($check)
	{
		// only output those that fails the api_exists;
		if (! empty($buff[3])) echo $buff[8] . $buff[3] . "\n";
	}
	else
	{
		fputcsv($report, $buff);
	}
}
fclose($handle);
fclose($report);
