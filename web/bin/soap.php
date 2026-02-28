#!/usr/bin/php
<?php require_once('/var/www/htdocs/common/common-inc.php');

if ($argc < 2)
	exit($argv[0] . ' <soapMethod> [args...]' . "\n");

$web_bean = isset($argv[1]) ? $argv[1] : 'loadUserProfile';
$arguments = $argv;
array_shift($arguments);
array_shift($arguments);

try
{
	$result = soap_call_ejb($web_bean, $arguments);
	echo json_encode($result);
	//print_r($result);
}
catch (Exception $ex)
{
	echo $ex->getMessage();
}

