<?php

	function doSend($xml, $sessionId) {

		global $destUri;

		if ($sessionId != "") {
                        $sid = "$sessionId";
                } else {
                        $sid = '';
                }

		$header = "Content-Type: text/xml";
		$curlHand = curl_init("$destUri/$sid");
		curl_setopt($curlHand, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($curlHand, CURLOPT_HTTPHEADER, $header);
		curl_setopt($curlHand, CURLOPT_POSTFIELDS, $xml);
		$res = curl_exec($curlHand);
		curl_close($curlHand);
	}
?>