<?php

	$json = array();

	if(file_exists($body_template))
		include_once($body_template);

	$json_encoded = json_encode($json);
	header('Cache-Control: no-store, must-revalidate');
	header('Expires: ' . gmdate('D, d M Y H:i:s', time()-365*24*60*60) . ' GMT');
	header('Content-Type: application/json');
	header('Content-Length: '.strlen($json_encoded));
	echo $json_encoded;

?>