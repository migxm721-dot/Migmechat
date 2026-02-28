<?php
require_once("common/common-config.php");
require_once("sites/common/utilities.php");

if(ClientInfo::is_mobile())
{
	include('404_wap.php');
	exit;
}

include('404_web.php');