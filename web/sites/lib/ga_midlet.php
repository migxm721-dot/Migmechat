<?php
require_once($_SERVER['DOCUMENT_ROOT'] . '/sites/common/utilities.php');
global $ga_account;

/**
  Copyright 2009 Google Inc. All Rights Reserved.
**/

  // Tracker version.
  define("VERSION", "4.4sh");

  // GA Midlet Account Number
  $img_ga_account = str_replace('UA', 'MO', $ga_account);
  define('ACCOUNT_NUMBER', $img_ga_account);

  // GA Disabled
  define('DISABLED', 0);

  // GA Hash Salt
  define('SALT', 'gamerz-');

  // The last octect of the IP address is removed to anonymize the user.
  function getIP($remoteAddress) {

  	if (empty($remoteAddress)) {
		return '';
    }

    // Capture the first three octects of the IP address and replace the forth
    // with 0, e.g. 124.455.3.123 becomes 124.455.3.0
    $regex = "/^([^.]+\.[^.]+\.[^.]+\.).*/";
    if (preg_match($regex, $remoteAddress, $matches)) {
      return $matches[1] . "0";
    } else {
      return "";
    }
  }

  // Generate a visitor id for this hit.
  // If there is a visitor id in the cookie, use that, otherwise
  // use the guid if we have one, otherwise use a random number.
  function getVisitorId($guid, $account, $userAgent, $cookie) {

    // If there is a value in the cookie, don't change it.
    if (!empty($cookie)) {
      return $cookie;
    }

    $message = "";
    if (!empty($guid)) {
      // Create the visitor id using the guid.
      $message = $guid . $account;
    } else {
      // otherwise this is a new user, create a new random id.
      $message = $userAgent . uniqid(getRandomNumber(), true);
    }

    $md5String = md5($message);

    return "0x" . substr($md5String, 0, 16);
  }

  // Get a random number string.
  function getRandomNumber() {
    return rand(0, 0x7fffffff);
  }

  // Make a tracking request to Google Analytics from this server.
  // Copies the headers from the original request to the new one.
  // If request containg utmdebug parameter, exceptions encountered
  // communicating with Google Analytics are thown.
  function sendRequestToGoogleAnalytics($utmUrl) {
    $options = array(
      "http" => array(
          "method" => "GET",
          "user_agent" => $_SERVER["HTTP_USER_AGENT"],
          "header" => ("Accept-Language: " . $_SERVER["HTTP_ACCEPT_LANGUAGE"]))
    );

    if (!empty($_GET["utmdebug"])) {
      $data = file_get_contents(
          $utmUrl, false, stream_context_create($options));
    } else {
      $data = @file_get_contents(
          $utmUrl, false, stream_context_create($options));
    }
  }

  // Track a page view, updates all the cookies and campaign tracker,
  // makes a server side request to Google Analytics and writes the transparent
  // gif byte data to the response.
  function trackPageView() {
    $timeStamp = time();
    $domainName = $_SERVER["SERVER_NAME"];
    if (empty($domainName)) {
      $domainName = "";
    }

    // Get the referrer from the utmr parameter, this is the referrer to the
    // page that contains the tracking pixel, not the referrer for tracking
    // pixel.
    $documentReferer = $_GET["utmr"];
    if (empty($documentReferer) && $documentReferer !== "0") {
      $documentReferer = "-";
    } else {
      $documentReferer = urldecode($documentReferer);
    }
    $documentPath = $_GET["utmp"];
    if (empty($documentPath)) {
      $documentPath = "";
    } else {
      $documentPath = urldecode($documentPath);
    }

    $userAgent = $_SERVER["HTTP_USER_AGENT"];
    if (empty($userAgent)) {
      $userAgent = "";
    }

	$apache_headers = apache_request_headers();
	$fusion_session_id = $apache_headers["sid"];

    $guidHeader = $_SERVER["HTTP_X_DCMGUID"];
    if (empty($guidHeader)) {
      $guidHeader = $_SERVER["HTTP_X_UP_SUBNO"];
    }
    if (empty($guidHeader)) {
      $guidHeader = $_SERVER["HTTP_X_JPHONE_UID"];
    }
    if (empty($guidHeader)) {
      $guidHeader = $_SERVER["HTTP_X_EM_UID"];
    }
    if (empty($guidHeader)) {
    	$guidHeader = $fusion_session_id;
    }

	$visitorId = $_GET["utmvid"];
	if(isset($fusion_session_id) && !empty($fusion_session_id))
	{
		$visitorId = '0x'.substr(md5($fusion_session_id), 0, 16);
	}
	elseif(!isset($visitorId) || empty($visitorId))
	{
		$visitorId = getVisitorId($guidHeader, ACCOUNT_NUMBER, $userAgent, $cookie);
	}

    $utmGifLocation = "http://www.google-analytics.com/__utm.gif";

    // Construct the gif hit url.
    $utmUrl = $utmGifLocation . "?" .
        "utmwv=" . VERSION .
        "&utmn=" . getRandomNumber() .
        "&utmhn=" . urlencode($domainName) .
        "&utmr=" . urlencode($documentReferer) .
        "&utmp=" . urlencode($documentPath) .
        "&utmac=" . ACCOUNT_NUMBER .
        "&utmcc=__utma%3D999.999.999.999.999.1%3B" .
        "&utmvid=" . $visitorId .
        "&utmul=". $_SERVER["HTTP_ACCEPT_LANGUAGE"] .
        "&utmip=" . getIP(getRemoteIPAddress()) .
        "&guid=ON";

	// Check Hash
	$java_hash = trim($_GET['hash']);
	$php_hash = md5(SALT.$visitorId);

	if(DISABLED == 1)
	{
		echo '1';
	}
	elseif($java_hash == $php_hash)
	{
		sendRequestToGoogleAnalytics($utmUrl);
	   	echo '0';
    }
  }

  trackPageView();
?>