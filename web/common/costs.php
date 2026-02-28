<?php

include_once("common-inc.php");

function getBuzzCost($username)
{
	try
	{
		$cost = soap_call_ejb("getBuzzCost", array($username));
		return $cost;
	}
	catch(Exception $e)
	{
	}
}

function getLookoutCost($username)
{
	try
	{
		$cost = soap_call_ejb("getLookoutCost", array($username));
		return $cost;
	}
	catch(Exception $e)
	{
	}
}

function getEmailAlertCost($username)
{
	try
	{
		$cost = soap_call_ejb("getEmailAlertCost", array($username));
		return $cost;
	}
	catch(Exception $e)
	{
	}
}

?>