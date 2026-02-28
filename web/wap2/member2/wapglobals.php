<?php
	//$wapDestUrl = "http://" . $_SERVER['HTTP_HOST'] . $_SERVER['SCRIPT_NAME'];
	$wapDestUrl = $_SESSION['prog'];

	$CHATROOM_REFRESH = 10;			// in seconds
	$PRIVATE_REFRESH = 20;			// in seconds
	$MAX_CHATLOG_LENGTH = 1000;		// in bytes
	$BUDDYLIST_REFRESH = 40;		// in seconds

	$globalResponse = array();
	$globalCurrentField = -1;
	$globalParserIndex = -1;
	$globalLoginArray = array();
	$globalSessionId = '';

	$prog = "";
?>
