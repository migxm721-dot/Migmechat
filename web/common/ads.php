<?php
require_once("admob.php");
require_once("user.php");
require_once("pageletFunctions.php");

	class AdInfo
	{
		public $dob = "";
		public $gender = "";
		public $keywords = "";
		public $search = "";

		public function setDOBFromDate( $date )
		{
			if(!empty($date) )
				$this->dob = date("Ymd", $date);
		}

		public function setGender( $gender )
		{
			if( !empty($gender) )
			{
				if( strtolower($gender) == "male" )
					$this->gender = "m";
				else if( strtolower($gender) == "female" )
					$this->gender = "f";
			}
		}
	}

	class Ads
	{
		public $showHeader = true;

		public function createAd( $adInfo, $applyStyle=true )
		{
			return "";
			$adInfo->search = "";
			$admob_params = array(
				ADMOB_SITE_ID     => "a149065ba489d00",  // REQUIRED - get from admob.com
				ADMOB_MARKUP      => "html", // OPTIONAL - Your site markup, "xhtml", "wml", "chtml"
				ADMOB_AREA_CODE   => "", // OPTIONAL - Area Code, e.g. "415"
				ADMOB_COORDINATES => "", // OPTIONAL - Latitude and Longitude (comma separated), e.g. "37.563657,-122.324807"
				ADMOB_POSTAL_CODE => "", // OPTIONAL - Postal Code, e.g. "90210"
				ADMOB_DOB         => $adInfo->dob, // OPTIONAL - Date of Birth formatted like YYYYMMDD, e.g. "19800229"
				ADMOB_GENDER      => $adInfo->gender, // OPTIONAL - Gender, m[ale] or f[emale]
				ADMOB_KEYWORDS    => $adInfo->keywords, // OPTIONAL - keywords, e.g. "sports baseball la dodgers"
				ADMOB_SEARCH      => $adInfo->search  // OPTIONAL - visitor's search term. e.g. "free games"
			);

			$ad =  admob_ad($admob_params);
			if( !empty($ad) )
			{
				$ar = array();
				preg_match('/href="(.*)"/Ui',$ad,$ar);
				$href = $ar[1];

				$matches = array();
				preg_match_all('/<a(.*)>(.*)<\/a>/Ui', $ad, $matches);
				$headers = apache_request_headers();
				$midletVersion = $headers['ver'];
				settype($midletVersion, "float");
				if( isMidletVersion(3.07) )
				{
					if( $this->showHeader )
						printf('<p style="color:gray"><i>Advertisement</i></p>');
					if( isMidletVersion4() )
					{
						$style = $applyStyle?'style="background-color:#CFCFCF;color:blue"':'style="color:blue"';
						$center = $applyStyle?"<center>":"";
						$closecenter = $applyStyle?"</center>":"";
						printf('<div %s><p>%s<a href="mig33:invokeNativeBrowser(%s)">%s</a>%s</p></div>',
								$style, $center, $href, $matches[2][0], $closecenter);
					}
					else
					{
						printf('<p><tag type="4" href="%s">%s</tag></p>', $href, $matches[2][0]);
					}
				}
			}
			else
			{
				//printf('<p>there are no ads using %s</p>', print_r($adInfo));
			}
		}
	}
?>