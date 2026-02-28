<?php
include_once("../../common/common-inc.php");
include_once("../../common/admob.php");
include_once("../../common/ads.php");
?>

<html>
	<head>
		<title>Ads</title>
	</head>

	<body>
		<?php
			$currentUserProfile = soap_call_ejb('loadUserProfile', array($sessUser, $sessUser));
			$currentUserDetails = soap_call_ejb('loadUserDetails', array($sessUser));

			$adInfo = new AdInfo();
 			$adInfo->setGender( $currentUserProfile["gender"] );
 			$adInfo->setDOBFromDate( $currentUserProfile["dateOfBirth"] );
 			$adInfo->search = "Games";
 			$ads = new Ads();
 			$ads->createAd($adInfo, false);
 			//print "<br>";
 			$ads->showHeader = false;

 			$adInfo->search = "Action Games";
 			$ads->createAd($adInfo, false);
 			//print "<br>";

 			$adInfo->search = "Multi-player";
 			$ads->createAd($adInfo, false);
 			//print "<br>";

 			$adInfo->search = "Single Player";
 			$ads->createAd($adInfo, false);
 			//print "<br>";

 			$adInfo->search = "Puzzles";
 			$ads->createAd($adInfo, false);
 			//print "<br>";
		?>
	</body>
</html>

