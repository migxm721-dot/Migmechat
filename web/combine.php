<?php

	/************************************************************************
	 * CSS and Javascript Combinator 0.5
	 * Copyright 2006 by Niels Leenheer
	 *
	 * Permission is hereby granted, free of charge, to any person obtaining
	 * a copy of this software and associated documentation files (the
	 * "Software"), to deal in the Software without restriction, including
	 * without limitation the rights to use, copy, modify, merge, publish,
	 * distribute, sublicense, and/or sell copies of the Software, and to
	 * permit persons to whom the Software is furnished to do so, subject to
	 * the following conditions:
	 *
	 * The above copyright notice and this permission notice shall be
	 * included in all copies or substantial portions of the Software.
	 *
	 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
	 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
	 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
	 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
	 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
	 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
	 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
     *
	 */
	//RewriteRule ^/combine/(.*)/(.*)/(.*) /combine.php?type=$1&files=$3&version=$2 [NC,L]


	$cache 	     = true;
	$cachedir    = '/var/www/cache/css_js';
	$cssdir      = dirname(__FILE__) . '/sites/resources/css';
	$jsdir       = dirname(__FILE__) . '/sites/resources/js';
    $expiry_time = 604800; // 7 days 60*60*24*7


	// Determine the directory and type we should use
	switch ($_GET['type']) {
		case 'css':
			$base = realpath($cssdir);
			break;
		case 'js':
			$_GET['type'] = 'javascript';
			$base = realpath($jsdir);
			break;
		default:
			header ("HTTP/1.0 503 Not Implemented");
			exit;
	};


    $version_request = $_GET['version'];
    $type            = $_GET['type'];
	$elements        = explode(',', $_GET['files']);

	// Determine conditions where request cannot be honoured
    // and compute max of last-modified at the same time
    $lastmodified = 0;
    foreach($elements as $index=>$element)
    {
		switch ($type)
		{
			case 'css':
				$elements[$index] = $element = $element . '.min.css';
				break;
			case 'javascript':
			default:
				// if javascript extension is not provided, add .min.js
				if (substr($element, -3) != '.js')
					$elements[$index] = $element = $element . '.min.js';
				break;
		}
		$path = realpath($base . '/' . $element);

		if (($type == 'javascript' && substr($path, -3) != '.js') ||
			($type == 'css' && substr($path, -4) != '.css')) {
			header ("HTTP/1.0 403 Forbidden");
			exit;
		}

		if (substr($path, 0, strlen($base)) != $base || !file_exists($path)) {
			header ("HTTP/1.0 404 Not Found");
			exit;
		}
        $lastmodified = max($lastmodified, filemtime($path));
	}


    // Determine supported compression method
    $gzip = strstr($_SERVER['HTTP_ACCEPT_ENCODING'], 'gzip');
    $deflate = strstr($_SERVER['HTTP_ACCEPT_ENCODING'], 'deflate');

    // Determine used compression method
    $encoding = $gzip ? 'gzip' : ($deflate ? 'deflate' : 'none');

    // Check for buggy versions of Internet Explorer
    if (!strstr($_SERVER['HTTP_USER_AGENT'], 'Opera') &&
        preg_match('/^Mozilla\/4\.0 \(compatible; MSIE ([0-9]\.[0-9])/i', $_SERVER['HTTP_USER_AGENT'], $matches)) {
        $version = floatval($matches[1]);

        if ($version < 6)
            $encoding = 'none';

        if ($version == 6 && !strstr($_SERVER['HTTP_USER_AGENT'], 'EV1'))
            $encoding = 'none';
    }

	$hash = $lastmodified . '-' . md5($encoding . $_GET['files']);


    // Always send Etag and expiry headers
    header ("Etag: \"" . $hash . "\"");
    header ("Cache-Control: max-age=" . $expiry_time);
    header ("Expires: " . date('r', time() + $expiry_time));


	if (isset($_SERVER['HTTP_IF_NONE_MATCH']) &&
		stripslashes($_SERVER['HTTP_IF_NONE_MATCH']) == '"' . $hash . '"')
	{
		// Return visit and no modifications, so do not send anything
		header ("HTTP/1.0 304 Not Modified");
		header ('Content-Length: 0');
	}
	else
	{
		// First time visit, or files were modified

        // we have to send content, set type now
        header ("Content-Type: text/" . $type);

        // set content encoding if required
        if ($encoding != 'none') {
            header ("Content-Encoding: " . $encoding);
        }

        // Try the cache first to see if the combined files were already generated
        $cachefile = 'cache-' . $hash . '.' . $type . ($encoding != 'none' ? '.' . $encoding : '');


		if ($cache && is_readable($cachedir . '/' . $cachefile))
		{
			header ("Content-Length: " . filesize($cachedir . '/' . $cachefile));
            readfile($cachedir . '/' . $cachefile);
			exit();
		}

		// Cache is disabled or not-present, generate content
		$contents = '';
        foreach($elements as $element)
        {
			$path = realpath($base . '/' . $element);
			$contents .= "\n\n" . file_get_contents($path);
		}

		if ($encoding != 'none')
		{
			// Indicates content is compressed
			$contents = gzencode($contents, 9, $gzip ? FORCE_GZIP : FORCE_DEFLATE);
		}

        // Send content
        header ('Content-Length: ' . strlen($contents));
        echo $contents;

		// Store cache
		if ($cache) {
			if ($fp = fopen($cachedir . '/' . $cachefile, 'wb')) {
				fwrite($fp, $contents);
				fclose($fp);
			}
		}
	}

