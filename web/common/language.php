<?php
$languages = array
				(
					"en-us" => "en"
				);

/** Include the language pack **/
function includeLanguagePack()
{
	$headers = apache_request_headers();
	$lang = "en";
	$langToTest = "en-US";
	if( isset($headers['lang']) )
	{
		$langToTest = $headers['lang'];
	}
	else if( isset($headers['Accept-Language']) )
	{
		$al = $headers['Accept-Language'];
		$langToTest = strtok($al, ",");
	}

	if( isset($languages[strtolower($langToTest)]) )
	{
		$lang = $languages[strtolower($langToTest)];
	}

	//$languagePack = "http://".$_SERVER['SERVER_ADDR']."/includes/lang/".$lang.".inc";
	$languagePack = $_SERVER['DOCUMENT_ROOT']."/includes/lang/".$lang.".inc";
	include_once($languagePack);

	//include_once( $languagePack );
}

?>