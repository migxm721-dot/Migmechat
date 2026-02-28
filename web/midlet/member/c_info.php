<?php
include_once("../../common/common-inc.php");

$attributes = getAttributeArray();
$headers = apache_request_headers();

/**
* Iterate through _REQUEST and build array
*/
function getAttributeArray()
{
	if( $_POST )
		return buildAttributeArray($_POST);
	else
		return buildAttributeArray($_GET);
}

/**
* Build an array of attributes from $ar
**/
function buildAttributeArray(&$ar)
{
	$a = array();
	foreach($ar as $ind=>$val )
	{
		$a[$ind] = $val;
	}
	return $a;
}

function appendArrayToFile($array, $filename)
{
	if( sizeof($array) == 0 ) return;
	foreach($array as $ind=>$val )
	{
		appendToFile(sprintf("\t%s => %s", $ind, $val), $filename);
	}
}

function appendArrayValueToFile($array, $key, $filename)
{
	if( !empty( $array[$key] ) )
		appendToFile(sprintf("\t%s => %s", $key, $array[$key]), $filename);
}

function appendAttributesToFile($filename)
{
	global $attributes;
	appendToFile("Attributes:", $filename);
	appendArrayValueToFile( $attributes, "awtrack", $filename);
	//appendArrayToFile($attributes, $filename);
}

function appendHeadersToFile( $filename )
{
	global $headers;
	appendToFile("Headers:", $filename);
	//appendArrayToFile($headers, $filename);
	appendArrayValueToFile($headers, "x-up-devcap-accept-language", $filename );
}

function appendCookiesToFile($filename)
{
	appendToFile("Cookies:", $filename);
	appendArrayToFile($_COOKIE, $filename);
}

function appendServerToFile($filename)
{
	appendToFile("Server:", $filename);
	appendArrayValueToFile($_SERVER, "HTTP_USER_AGENT", $filename);
	//appendArrayValueToFile($_SERVER, "REMOTE_ADDR", $filename );
	appendToFile("\tREMOTE_ADDR => ".getRemoteIPAddress(), $filename);
	//appendArrayToFile($_SERVER, $filename);
}

function appendToFile($string, $filename)
{
	$fp = fopen( $filename, "a" ) or die("can't open file");
	fwrite( $fp, sprintf("%s: %s\n", date("Ymd H:i:s"), $string) );
	fclose($fp);
}
?>

<html>
	<body>
		<p><b>Capture:</b></p>
		<?php
			global $apache_dir;
			$filename = sprintf("%s/logs/midlet_info.log", $apache_dir);
			appendToFile("Begin Capture ----------------", $filename);
			appendAttributesToFile($filename);
			appendServerToFile($filename);
			appendHeadersToFile($filename);
			//appendCookiesToFile($filename);
			appendToFile("Raw:", $filename);
			$rawPOSTData = file_get_contents("php://input");
			appendToFile( sprintf("\t%s", $rawPOSTData), $filename );
			appendToFile("End Capture ------------------\n", $filename);
		?>

		<form method="POST" action="capture_information.php">
			<input type="hidden" name="post_test" value="this is a post test">
			<input type="submit" value="Submit" >
		</form>
	</body>
</html>