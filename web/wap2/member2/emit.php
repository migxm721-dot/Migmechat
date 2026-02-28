<?php

	session_start();

	function nocacher() {

		header("pragma: no-cache");
		header("cache-control: no-cache");
		header("Expires: Mon, 22 Jul 2002 11:12:01 GMT");

		return;
	}

	function emitHeader() {
			$accept = $_SERVER['HTTP_ACCEPT'];

			//debugFile("accept is " . $accept);

			if (strstr($accept, "application/vnd.wap.xhtml+xml")) {

					header("Content-Type: application/vnd.wap.xhtml+xml");

			} else if (strstr($accept, "application/xhtml+xml")) {

					header("Content-Type: application/xhtml+xml");

			} else {

					header("Content-Type: text/html");
			}

			nocacher();

			echo '<?xml version="1.0"?>' . "\n";
			echo '<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile10.dtd">' . "\n\n";

			echo '<html xmlns="http://www.w3.org/1999/xhtml">' . "\n";
	}

	function emitHeader_min() {
			$accept = $_SERVER['HTTP_ACCEPT'];
			echo '<?xml version="1.0"?>' . "\n";
			echo '<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile10.dtd">' . "\n\n";

			echo '<html xmlns="http://www.w3.org/1999/xhtml">' . "\n";
	}

	function getWapArgs() {

		// emit the args to the pagelets so they know where to return etc

		$prog = $_REQUEST['prog'];
		$cid = $_REQUEST['cid'];

		return("&amp;prog=$prog&amp;cid=$cid");
	}

?>
