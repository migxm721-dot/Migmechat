<?php
require_once("common-inc.php");
require_once("dateFunctions.php");
require_once("user.php");
require_once("style.php");

//Pagelet generated menu's max title length. This is only for the core contextual text, i.e. username.
//There might be more text attached to it after the contextual text
$menuContextualTitleMaxLength = 8;
$commonStyle;

if(!function_exists('getAorAn'))
{
	function getAorAn($word, $uc=false)
	{
		$vowels = array("a", "e", "i", "o", "u", "A", "E", "I", "O", "U");
		if(empty($word)) return ($uc?"A":"a");
		$fc = $word{0};

		$ret = $uc?"A":"a";
		if( in_array($fc, $vowels) )
		{
			$ret = $uc?"An":"an";
		}
		return $ret;
	}
}

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

function getAttribute($attributeName, $defaultValue = NULL )
{
	if( $_POST )
		return getAttributeFromArray( $_POST, $attributeName, $defaultValue );
	else
		return getAttributeFromArray($_GET, $attributeName, $defaultValue );
}

function getAttributeFromArray( $array, $attributeName, $defaultValue = NULL )
{
	if( isset( $array[$attributeName] ) ) return $array[$attributeName];
	return $defaultValue;
}

/*
* Determine if the current midlet version is version 4
*/
function isMidletVersion4()
{
	return isMidletVersion(4.00);
}

function isMidletVersion41()
{
	return isMidletVersion(4.10);
}

function canPrefetch()
{
	return isMidletVersion(4.10);
}

/*
* Check if the midlet version is greater or equal as the version that is passed.
*/
function isMidletVersion($version)
{
	$midletVersion = getMidletVersion();
	settype($midletVersion, "float");
	return (is_numeric($midletVersion) && $midletVersion >= $version );
}

/*
*Get http header
*/
function getHeader()
{
	return apache_request_headers();
}

function showCommonStyle()
{
	if( !isMidletVersion4() ) return;
	if( empty($commonStyle) )
		$commonStyle = new Style();
	$commonStyle->enableThemes();
	$commonStyle->show();
}

function showExternalLink($url, $text)
{
	if( isMidletVersion4() )
	{
		printf('<p><a href="mig33:invokeNativeBrowser(%s)">%s</a></p>', $url, $text);
	}
	else
	{
		printf('<p><tag type="4" href="%s">%s</tag></p>', $url, $text);
	}
}

/**
*
*Retrieve midlet version from header.
*/
function getMidletVersion()
{
	if(empty($headers)){
		$headers = getHeader();
	}

	//Get header information, it has information on midlet version and screen sizes
	$midletVersion = $headers['ver'];
	return $midletVersion;
}

function getMeasurement( $measure )
{
	if( $measure <= 27 ) return 24;
	else if( $measure > 27 && $measure <= 37 ) return 32;
	else if( $measure > 37 && $measure <= 58 ) return 48;
	else return 60;
}

function getAdjustedMeasureFromScreen( $type, $percentage )
{
	$headers = apache_request_headers();
	$mType = ($type=="width")?"sw":"sh";
	$measure = $headers[$mType];
	settype($measure, "integer");
	$returnM = $measure * ($percentage/100);
	return $returnM;
}

function getAdjustedHeightFromScreen( $percentage )
{
	$height = getAdjustedMeasureFromScreen( "height", $percentage );
	if( $height == 0 ) $height = 60;
	return getMeasurement( $height );
}

function getAdjustedWidthFromScreen( $percentage )
{
	$width = getAdjustedMeasureFromScreen( "width", $percentage );
	if($width == 0 ) $width = 60;
	return getMeasurement( $width );
}

/**
* Display the user avatar
**/
function showUserAvatar($filename, $percentage=0)
{
	global $mogileFSImagePath;

	if( $percentage == 0 ) $percentage = 25;
	$height = getAdjustedHeightFromScreen($percentage);
	$width = getAdjustedWidthFromScreen($percentage);
	if($filename != "")
	{
		//Get a proper image ratio without stretching and request the exact width and height for it
		$headers = apache_request_headers();
		$originalWidth = $displayPic['width'];
		$originalHeight = $displayPic['height'];
		//$result = getRatio($originalWidth, $originalHeight, $width, $height );
		//echo '<img src="'.$mogileFSImagePath.'/'.$filename.'.jpeg?w='.$result[0].'&h='.$result[1].'&c=1" hspace="2" vspace="2" style="float:left" width="'.$result[0].'" height="'.$result[1].'">';
		//printf("<p>%s</p>", sprintf('"%s/%s.jpeg?w=%d&h=%d&c=1 width="%d" height="%d"',
		//	$mogileFSImagePath, $filename, $width, $width, $width, $width));

		printf('<img src="%s/%s?w=%d&h=%d&a=1&c=1" hspace="2" vspace="2" style="float:left" width="%d" height="%d">',
			$mogileFSImagePath, $filename, $width, $width, $width, $width);
	}
	else
	{
		//No display picture, show default picture

		printf( '<img src="http://%s/images/nodisplaypic_%d.png" width="%d" height="%d" style="float:left">',
					$_SERVER['HTTP_HOST'], $width, $width, $width);
	}
}

/**
* Creates an ellipsis from a string length
**/
if(!function_exists('ellipsis')) {
	function ellipsis( $string, $length )
	{
		if( $length > strlen($string) ) return $string;
		$substring = substr( $string, 0, $length );
		return $substring."...";
	}
}

//Function to get the right ratio sized thumbnail
function getRatio($originalWidth,$originalHeight,$requiredWidth,$requiredHeight){
	$originalRatio = $originalWidth / $originalHeight;
	$newRatio = $requiredWidth / $requiredHeight;

	if($originalRatio > $newRatio){
		$requiredHeight = $requiredWidth / $originalRatio;
	}else if($originalRatio < $newRatio){
		$requiredWidth = $requiredHeight * $originalRatio;
	}

	return array((int)$requiredWidth,(int)$requiredHeight);
}

#Search Keyword formatted Output
function formatKeywordSearch($keywordString,$type){
	global $server_root;

	$keywordArray = explode(",", strtolower($keywordString));

	$keywordOutput = '';

	foreach ($keywordArray as $finalword) {
		$finalword = trim($finalword);
		$finalwordenc = urlencode($finalword);
		if($keywordOutput != '')
		{
			$keywordOutput = $keywordOutput.", <a href=\"$server_root/midlet/member/search_profiles.php?keyword=$finalwordenc&type=$type\">$finalword</a>";
		}
		else
		{
			$keywordOutput = "<a href=\"$server_root/midlet/member/search_profiles.php?keyword=$finalwordenc&type=$type\">$finalword</a>";
		}
	}
	#cut off the last , in the string.
	//return  substr($keywordOutput,0,strlen($keywordOutput) - 1);
	return  $keywordOutput;
}

/**
* Show the navigation footer for pages
**/
function showNavigation($urlPrefix, $page, $numPages, $showNavigationJump)
{
	if( empty($urlPrefix) ) return;

	$amp = (stripos($urlPrefix, "?")>=0)?"&":"";
	print '<p><center>';

	if ($page > $numPages )
		$page = $numPages;

	//First Page
	if (($page > 1) && $showNavigationJump){
		printf('<a href="%s%spagenum=1">&lt;&lt;</a>&nbsp;', $urlPrefix, $amp);
	}

	//The page before
	if ($page > 1){
		printf('<a href="%s%spagenum=%d">&lt;Previous</a>&nbsp;', $urlPrefix, $amp, ($page-1));
	}

	//The current page out of how many in total
	print ''.($page).'/'.($numPages).'&nbsp;';

	//the page after
	if ($page < ($numPages)){
		printf('<a href="%s%spagenum=%d">Next&gt;</a>&nbsp;', $urlPrefix, $amp, ($page+1));
	}

	//Last Page
	if (($page < $numPages) && $showNavigationJump){
		printf('<a href="%s%spagenum=%d">&gt;&gt;</a>', $urlPrefix, $amp, $numPages);
	}
	print '</center></p><br>';
}

class ErrorSuccess
{
	public $error = "";
	public $success = "";

	public function show()
	{
		//show errors or message if needed
		if(!empty($this->error) && $this->error != null )
		{
			echo '<p style="color:red">'.$this->error.'</p><br>';
		}

		//Show success message if needed
		else if(!empty($this->success))
		{
			echo '<p style="color:green">'.$this->success.'</p><br>';
		}
	}
}

class UserPagingObject extends ErrorSuccess
{
	public $username;
	public $pageNumber = 1;
	public $numberOfEntries = 10;

	public function __construct( $username, $pageNumber, $numberOfEntries )
	{
		$this->setPaging($username, $pageNumber, $numberOfEntries );
	}

	public function setPaging($username, $pageNumber, $numberOfEntries )
	{
		$this->username = $username;
		$this->pageNumber = $pageNumber;
		$this->numberOfEntries = $numberOfEntries;
		$this->error = "";
		$this->success = "";
	}
}

?>