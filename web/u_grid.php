<?php
include_once("common/common-inc.php");

$pass =  file_get_contents("php://input");

//Security and IP Checks
//if ($pass != 'bidj313asd238asdj323asd')
//	die();

//if ($_SERVER['REMOTE_ADDR'] != "10.1.1.45" )
//	die();

//Generate the rategrid cache file
global $apache_dir;
$filename =  $apache_dir.'/cache/objects/rategrid.ser';

//Call the EJB and serialize the rategrid to the cache file
{
	try
	{
		$rateGrid = soap_call_ejb('getRateGrid', array());
		$rateGrid_serialized = serialize($rateGrid);
		$handle = fopen($filename, 'w');
		fwrite($handle,$rateGrid_serialized);
		fclose($handle);
	}catch(Exception $e)
	{
		$error = $e->getMessage();
		print $error;
	}
}

?>