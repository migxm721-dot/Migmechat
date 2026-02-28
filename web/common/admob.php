<?php
// AdMob Publisher Code
// Language: PHP
// Version: 20080401
// Copyright AdMob, Inc., All rights reserved

// change to "live" when ready to deploy
define("ADMOB_MODE", "live");

function admob_append_params(&$params, $key, $val) {
  if(!empty($val)) $params .= ("&" . urlencode($key) . "=" . urlencode($val));
}
function admob_append_headers(&$params) {
  $admob_ignore = array("HTTP_PRAGMA","HTTP_CACHE_CONTROL","HTTP_CONNECTION","HTTP_USER_AGENT","HTTP_COOKIE","HTTP_ACCEPT");
  foreach ( array_keys( $_SERVER ) as $var ) {
    if ( substr( $var, 0, 4 ) == "HTTP" && !in_array( $var, $admob_ignore ) ) {
      $params .= "&" . urlencode("h[" . $var . "]" ) . "=" . urlencode( $_SERVER[$var] );
    }
  }
}
function admob_ad($admob_params=array()) {
  // build url
  $admob_page_url  = sprintf("http%s://%s%s",
  (isset($_SERVER["HTTPS"]) && $_SERVER["HTTPS"] == TRUE ? "s": ""),
  $_SERVER["HTTP_HOST"],
  $_SERVER["REQUEST_URI"]
  );
  //$md5_sid = session_id();
  //if(!empty($md5_sid)) $md5_sid = md5($md5_sid);
  $md5_sid = "c83dd51d2f01c447d024136d5ede0225";
  $admob_post = "s=" . $admob_params[ADMOB_SITE_ID];
  admob_append_params($admob_post, "u", $_SERVER["HTTP_USER_AGENT"]);
  admob_append_params($admob_post, "i", $_SERVER["REMOTE_ADDR"]);
  admob_append_params($admob_post, "p", $admob_page_url);
  admob_append_params($admob_post, "t", $md5_sid);
  admob_append_params($admob_post, "e", "UTF-8");
  admob_append_params($admob_post, "ma", $admob_params[ADMOB_MARKUP]);
  admob_append_params($admob_post, "v", ADMOB_VERSION);
  admob_append_params($admob_post, "d[pc]", $admob_params[ADMOB_POSTAL_CODE]);
  admob_append_params($admob_post, "d[ac]", $admob_params[ADMOB_AREA_CODE]);
  admob_append_params($admob_post, "d[coord]", $admob_params[ADMOB_COORDINATES]);
  admob_append_params($admob_post, "d[dob]", $admob_params[ADMOB_DOB]);
  admob_append_params($admob_post, "d[gender]", $admob_params[ADMOB_GENDER]);
  admob_append_params($admob_post, "k", $admob_params[ADMOB_KEYWORDS]);
  admob_append_params($admob_post, "search", $admob_params[ADMOB_SEARCH]);
  admob_append_headers($admob_post);
  if(ADMOB_MODE == "test") $admob_post .= "&m=test";

  // request ad
  $admob_request = curl_init();
  curl_setopt( $admob_request, CURLOPT_URL, ADMOB_ENDPOINT );
  curl_setopt( $admob_request, CURLOPT_RETURNTRANSFER, 1 );
  curl_setopt( $admob_request, CURLOPT_TIMEOUT, ADMOB_TIMEOUT );
  curl_setopt( $admob_request, CURLOPT_CONNECTTIMEOUT, ADMOB_TIMEOUT );
  curl_setopt( $admob_request, CURLOPT_HTTPHEADER, array("Content-Type: application/x-www-form-urlencoded", "Connection: Close"));
  curl_setopt( $admob_request, CURLOPT_POSTFIELDS, $admob_post );

  $admob_contents = curl_exec( $admob_request );
  if(isset($admob_contents) && ($admob_contents === TRUE || $admob_contents === FALSE)) {
    $admob_contents = "";
  }
  $admob_error = curl_errno( $admob_request );

  curl_close( $admob_request );

  // output contents
  if (isset($admob_error) && $admob_error == CURLE_OPERATION_TIMEOUTED) {
    return "<img src=\"http://t.admob.com/li.php/c.gif/u/" . $admob_params[ADMOB_SITE_ID] . "/1/" . ADMOB_TIMEOUT . "/" . md5($admob_page_url) . "\" alt=\"\" width=\"1\" height=\"1\" />";
  } else {
    return $admob_contents;
  }
}
define("ADMOB_SITE_ID", "ADMOB_SITE_ID");
define("ADMOB_ENDPOINT", "http://r.admob.com/ad_source.php");
define("ADMOB_VERSION", "20080401-PHP-97039c92d234899e");
define("ADMOB_TIMEOUT", 1);
define("ADMOB_MARKUP", "ADMOB_MARKUP");
define("ADMOB_AREA_CODE", "ADMOB_AREA_CODE");
define("ADMOB_COORDINATES", "ADMOB_COORDINATES");
define("ADMOB_POSTAL_CODE", "ADMOB_POSTAL_CODE");
define("ADMOB_DOB", "ADMOB_DOB");
define("ADMOB_GENDER", "ADMOB_GENDER");
define("ADMOB_KEYWORDS", "ADMOB_KEYWORDS");
define("ADMOB_SEARCH", "ADMOB_SEARCH");

/*
$admob_params = array(
ADMOB_SITE_ID     => "a149065ba489d00",  // REQUIRED - get from admob.com
ADMOB_MARKUP      => "", // OPTIONAL - Your site markup, "xhtml", "wml", "chtml"
ADMOB_AREA_CODE   => "", // OPTIONAL - Area Code, e.g. "415"
ADMOB_COORDINATES => "", // OPTIONAL - Latitude and Longitude (comma separated), e.g. "37.563657,-122.324807"
ADMOB_POSTAL_CODE => "", // OPTIONAL - Postal Code, e.g. "90210"
ADMOB_DOB         => "", // OPTIONAL - Date of Birth formatted like YYYYMMDD, e.g. "19800229"
ADMOB_GENDER      => "", // OPTIONAL - Gender, m[ale] or f[emale]
ADMOB_KEYWORDS    => "", // OPTIONAL - keywords, e.g. "sports baseball la dodgers"
ADMOB_SEARCH      => ""  // OPTIONAL - visitor's search term. e.g. "free games"
);

echo admob_ad($admob_params); // display an ad
*/
// request more ads by copying this snippet elsewhere on your page
// echo admob_ad($admob_params);
