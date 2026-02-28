<?php
include_once("../../common/common-inc.php");

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

$username = $_GET['username'];
$pageNum = 1;
if($_GET['pagenum'] != ''){
	$pageNum = $_GET['pagenum'];
}
$numEntries = 5;
settype($pageNum, 'int');

?>
<html>
  <head>
    <title>View Gallery</title>
  </head>
  <body bgcolor="white">

<?php
	//Retrieve and show the gallery items
	if($username != ''){
		try
		{
			$content = soap_call_ejb('getGallery', array($sessUser, $username, $pageNum, $numEntries));
			$page = $content[0]['page'];
			$num_pages = $content[0]['numPages'];
			$num_entries = $content[0]['numEntries'];

			if($num_entries == 0){
				$error = 'User does not have any published photos.';
			} else {
				for ($i = 1; $i < sizeof($content); $i++){
					$originalWidth = $content[$i]['file.width'];
					$originalHeight = $content[$i]['file.height'];
					$result = getRatio($originalWidth, $originalHeight, 50, 50);
					echo '<p><img src="'. $mogileFSImagePath.'/'.$content[$i]['file.id'].'.jpeg?w='.$result[0].'&h='.$result[1].'" vspace="2" hspace="2" width="'.$result[0].'" height="'.$result[1].'" style="float:left">'.strip_tags($content[$i]['description']).'<a href="view_gallery_photo.php?image_id='.$content[$i]['file.id'].'&owidth='.$originalWidth.'&oheight='.$originalHeight.'&description='.ereg_replace(' ', '%20',strip_tags($content[$i]['description'])).'&username='.$username.'&datetime='.$content[$i]['dateCreated'].'">View</a></p><br>'."\n";
				}
			}
		}catch(Exception $e){
			$error = $e->getMessage();
		}
	} else {
		$error = 'No username has been specified. No gallery selected.';
	}


	//Don't show nav if theres an error
	if($error == ''){
		if (sizeof($content) > 1){
			print '<p><center>';
			//First Page
			if ($page > 1){
				print '<a href="view_gallery.php?username='.$username.'&pagenum=1">&lt;&lt;</a>&nbsp;';
			}

			//The page before
			if ($page > 1){
				print '<a href="view_gallery.php?username='.$username.'&pagenum='.($page-1).'">&lt;</a>&nbsp;';
			}

			//The current page out of how many in total
			print ''.($page).'/'.($num_pages).'&nbsp;';

			//the page after
			if ($page < ($num_pages)){
				print '<a href="view_gallery.php?username='.$username.'&pagenum='.($page+1).'">&gt;</a>&nbsp;';
			}

			//Last Page
			if ($page < ($num_pages)){
				print '<a href="view_gallery.php?username='.$username.'&pagenum='.($num_pages).'">&gt;&gt;</a>';
			}
			print '</center></p><br>';
		}
	} else {
		echo '<p>'.$error.'</p><br>';
	}

	print '<p><a href="view_profile.php?username='.$username.'">Back to Profile</a> &gt;&gt;</p>';
?>
	<p><a href="view_profile.php">My Profile</a> &gt;&gt;</p>
	<p><a href="profile_search_main.php">Search Profiles</a> &gt;&gt;</p>
	<p><a href="view_scrapbook.php">View Scrapbook</a>&gt;&gt;</p>
	<p><a href="view_scrapbook_help.php">Help</a>&gt;&gt;</p>
  </body>
</html>