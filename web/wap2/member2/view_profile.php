<?php
include_once("common-inc-kk.php");
include_once("./emit.php");
include_once("./check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];

include_once(getProgFile($prog));

debugFile("view_profile_wap: session is " . print_r($_SESSION, TRUE));

//Check async messages
checkServerSessionStatus();

#$debug = print_r($_SESSION, TRUE) . "----- cookies --- \n" . print_r($_COOKIE, TRUE) . " ------- request ----- \n" . print_r($_REQUEST, TRUE);
#echo "<!-- $debug -->\n"; exit;

ice_check_session();
$sessUser = ice_get_username();

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

	$keywordArray = explode(",", strtolower($keywordString));
	foreach ($keywordArray as $finalword) {
		$finalword = trim($finalword);
		$keywordOutput .= "<a href=\"search_profiles.php?keyword=$finalword&type=$type\">$finalword</a>, ";
	}

	#cut off the last , in the string.
	return  substr($keywordOutput,0,strlen($keywordOutput) - 1);
}

$username = $_GET['username'];

//Attempt to load the profile, either a requested profile or your own profile
try {
	if($username == ''){
		$userProfile = soap_call_ejb('loadUserProfile', array($sessUser, $sessUser));
		$userDetails = soap_call_ejb('loadUserDetails', array($sessUser));
	} else {
		$userProfile = soap_call_ejb('loadUserProfile', array($sessUser, $username));
		$userDetails = soap_call_ejb('loadUserDetails', array($username));
	}
}catch(Exception $e){
	//$error = 'User have not created a profile yet.';
	$error = $e->getMessage();
}

//A variable that indicates if the user is browsing his own profile
if($username == $sessUser || $username == ''){
	$viewOwn = true;
}


//Set username to whichever that the profile belongs to
if($username == ''){
	$username = $sessUser;
}

//Get the page number from search page.if available
if(!empty($_GET['pagenum'])){
	$pageNum = $_GET['pagenum'];
}

$keyword  = $_GET['keyword'];
$type  = $_GET['type'];
$age1  = $_GET['age_entry1'];
$age2  = $_GET['age_entry2'];
$searchCountry  = $_GET['searchCountry'];
$searchusername  = $_GET['searchusername'];

//Determine whether the referring page is search page or the browse profiles page
if (strlen($_POST['browse']) > 0)
	$browse = true;
if (strlen($_GET['browse']) > 0)
	$browse = true;

//Setup URI string for passing back and forth
if(!empty($pageNum)){
	$uri = 'keyword='.$keyword.'&type='.$type.'&age_entry1='.$age1.'&age_entry2='.$age2.'&searchCountry='.$searchCountry.'&searchusername='.$searchusername.'&searchhometown='.$searchhometown.'&pagenum='.$pageNum.'&browse='.$browse;
}

emitHeader();
emitTitle("Profile");
  	if($error == ''){
  		if($userDetails['displayPicture'] != ""){
 			try{
 				$displayPic = soap_call_ejb('getFile', array((string) $userDetails['displayPicture']));
 			}catch (Exception $e){
 				$loadError = $e->getMessage();
 			}

 			if($loadError != "" || $displayPic == ""){
 				//MAKE SURE WE GET A GLOBAL LINK TO THIS IMAGE
 				echo '<img src="http://'.$_SERVER['HTTP_HOST'].'/images/nodisplaypic.gif" width="60" height="60" style="float:left">';
 			} else {
 				$originalWidth = $displayPic['width'];
				$originalHeight = $displayPic['height'];
				$result = getRatio($originalWidth, $originalHeight, 60, 60);
				print '<img src="'.$mogileFSImagePath.'/'.$userDetails['displayPicture'].'.jpeg?w='.$result[0].'&h='.$result[1].'" hspace="2" vspace="2" style="float:left" width="'.$result[0].'" height="'.$result[1].'">';
 			}

		?>
		<?php
		} else {
			print '<img src="http://'.$_SERVER['HTTP_HOST'].'/images/nodisplaypic.gif" width="60" height="60" style="float:left">';
		}
		?>
		<?=strip_tags($userProfile['aboutMe'],'<p></p><b></b><i></i><br/>')?>
	<br/><br/>

	<small><b>Username</b></small><br/>
	<small><?=$username?></small><br/><br/>

	<small><b>Sex</b></small><br/>
	<small>
		<?php
			if($userProfile['gender'] == ''){
				echo 'Undecided';
			} else {
				echo $userProfile['gender'];
			}
		?>
	</small><br/><br/>

	<small><b>Country</b></small><br/>
	<small>
	<?php
		try{
 			$country = get_country($userDetails['countryID']);
 			echo $country;
 		}catch(Exception $e){
 			echo $e.':Unknown';
 		}
 	?>
 	</small><br/><br/>

 	<small><b>Hometown</b></small><br/>
	<small>
		<?php
			if($userProfile['homeTown'] == ''){
				echo 'None';
			} else {
				echo $userProfile['homeTown'];
			}
		?>
	</small><br/><br/>

	<small><b>Birthday</b></small><br/>
	<small>
		<?php
			$dob = $userProfile['dateOfBirth'];
			if($dob != ""){
				echo date('M-d-Y', $dob);
			} else {
				echo 'None';
			}
		?>
	</small><br/><br/>

	<small><b>Relationship Status</b></small><br/>
	<small>
		<?php
			if($userProfile['relationshipStatus'] == ''){
				echo 'None';
			} else {
				if($userProfile['relationshipStatus'] == 'SINGLE'){
					echo 'Single';
				}else if($userProfile['relationshipStatus'] == 'IN_A_RELATIONSHIP'){
					echo 'In a Relationship';
				}else if($userProfile['relationshipStatus'] == 'DOMESTIC_PARTNER'){
					echo 'Domestic Partner';
				}else if($userProfile['relationshipStatus'] == 'MARRIED'){
					echo 'Married';
				}else if($userProfile['relationshipStatus'] == 'COMPLICATED'){
					echo 'Complicated';
				} else {
					echo $userProfile['relationshipStatus'];
				}
			}
		?>
	</small><br/><br/>

 	<small><b>Interests</b></small><br/>
 	<small><?php if ($userProfile['hobbies'] != "") { echo formatKeywordSearch($userProfile['hobbies'],"2");} else { echo 'None';} ?>
 	</small><br/><br/>

	<small><b>I Love</b></small><br/>
 	<small><?php if ($userProfile['likes'] != "") { echo formatKeywordSearch($userProfile['likes'],"3"); } else { echo 'None';} ?>
 	</small><br/><br/>

	<small><b>I Hate</b></small><br/>
 	<small><?php if ($userProfile['dislikes'] != "") { echo formatKeywordSearch($userProfile['dislikes'],"4");} else { echo 'None';} ?>
 	</small><br/><br/>

 	<small><b>Schools/colleges</b></small><br/>
 	<small><?php if ($userProfile['schools'] != "") { echo formatKeywordSearch($userProfile['schools'],"1"); } else { echo 'None';} ?>
 	</small><br/><br/>

 	<small><b>Jobs / Occupations</b></small><br/>
 	<small><?php if ($userProfile['jobs'] != "") { echo formatKeywordSearch($userProfile['jobs'],"5"); } else { echo 'None';} ?>
 	</small><br/><br/>

 	<small><b>Published Photos</b></small><br/>

 	<?php
 		//Retrieve published photos
 		try{
 			$content = soap_call_ejb('getGallery', array($sessUser, $username, 1, 3));
			$page = $content[0]['page'];
			$num_pages = $content[0]['numPages'];
			$num_entries = $content[0]['numEntries'];
			if($num_entries <= 0){
				echo '<small>User has no published photos.</small><br/><br/>';
			} else {
				for ($i = 1; $i < sizeof($content); $i++){
					$originalWidth = $content[$i]['file.width'];
					$originalHeight = $content[$i]['file.height'];
					$result = getRatio($originalWidth, $originalHeight, 50, 50);
					if($content[$i]['description'] == ''){
						$description = 'No description';
					} else {
						$description = $content[$i]['description'];
					}
					echo '<small><img src="'. $mogileFSImagePath.'/'.$content[$i]['file.id'].'.jpeg?w='.$result[0].'&h='.$result[1].'" vspace="2" hspace="2" width="'.$result[0].'" height="'.$result[1].'" style="float:left">'.$description.'</small><br/><br/>'."\n";
				}

				if($num_entries >= 3){
					echo '<small><a href="view_gallery.php?username='.$username.'">View User\'s Gallery</a></small><br/><br/>';
				}
			}
 		}catch(Exception $e){
 			echo '<small>Unable to retrieve user\'s published photos. '.$e->getMessage().'</small><br/><br/>';
 		}


 	?>

 	<?php
 		} else {
 			echo '<small>'.$error.'</small><br/><br/>';
 		}
 	?>

 	<?php
 		if(!empty($pageNum))
 		{
 			if ($browse)
 				print '<small><a href="search_profiles.php?'.$uri.'">Back to browse profiles</a></small><br/><br/>';
 			else
 				print '<small><a href="search_profiles.php?'.$uri.'">Back to search results</a></small><br/><br/>';
 		}

 		//Show edit profile if he is the owner of this profile
 		if($viewOwn == true){
 			print '<small><a href="edit_profile.php">Edit Profile</a></small><br/>';
 		} else {
 			print '<small><a href="view_profile.php">My Profile</a></small><br/>';
 		}
 	?>

	<small><a href="profile_search_main.php">Search Profiles</a></small><br/>
	<?php
	//If not viewing their own profile provide a link to report user
	if (!$viewOwn){
	?>
		<small><a href="report_user.php?username=<?=$username?>">Report User</a></small><br/>
	<?php
	}
	?>
	<small><a href="view_scrapbook.php">View Scrapbook</a></small><br/>

	<?php
		if(!empty($uri)){
			print '<small><a href="view_profile_help.php?username='.$username.'&'.$uri.'">Help</a></small><br/>';
		} else {
			print '<small><a href="view_profile_help.php?username='.$username.'">Help</a></small><br/>';
		}
	?>
  </body>
</html>
