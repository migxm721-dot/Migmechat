<?php

include_once("common-config.php");

// Function to display a user's profile.
// $userViewing: The username of the user who is viewing this profile
// $userToView: The username of the profile to display
// $userToViewData: The userToViewData object of the userToView. If not supplied, they will be loaded from the DB
// $showEditProfileLink: If 'true', the "Edit Profile" link will be shown (duh)
// $showPublicPhotos: If 'true', the user's public photos will be displayed (duh again)
// $mogileFSImagePath: URL prefix to images stored in MogileFS
function showUserProfile($userViewing, $userToView, $userToViewData, $showEditProfileLink, $showPublicPhotos, $mogileFSImagePath) {
	$ownProfile = ($userViewing == $userToView);

	try {
		$userProfile = soap_call_ejb('loadUserProfile', array((string)$userViewing, (string)$userToView));
	}catch(Exception $e){
		$error = $e->getMessage();
		if ($error != 'The user has not created a profile yet.') {
			print '<div style="margin:10px;"><h3>' . $error . '</h3></div>';
			return;
		}
	}

	if (!isset($userToViewData)) {
		try {
			$userToViewDataArray = soap_call_ejb('loadUserDetails', array((string) $userToView));
			$countryId = $userToViewDataArray['countryID'];
			$displayPicture = $userToViewDataArray['displayPicture'];
		} catch(Exception $e){
			$error = $e->getMessage();
			print $error;
			return;
		}
	}
	else {
		$countryId = $userToViewData->countryID;
		$displayPicture = $userToViewData->displayPicture;
	}

	$age = 0;
	if (isset($userProfile['dateOfBirth']))
		$age = yearsSince($userProfile['dateOfBirth']);

	unset($gender);
	if (isset($userProfile['gender']))
		$gender = substr($userProfile['gender'], 0, 1);
?>
<div id="container">
	<div id="header">
		<h1 style="float: left;"><?= $userToView ?></h1>
		<h2 style="float: right;">
<?php
	if ($age > 0)
		print $age . ", ";
	if (isset($gender))
		print $gender . ", ";
	print get_country($countryId);
?>
		</h2>
	</div>
	<div id="wrapper">
		<div id="profile_content">
			<table border=0 cellspacing=0 cellpadding=6>
				<tr>
					<td valign="top"><span class="blue">About&nbsp;Me:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['aboutMe']))
		print '<div class="about_me">' . $userProfile['aboutMe'] . '</div>';
	else
		if ($ownProfile)
			print "<span class=\"missing\">Edit your profile to tell people about yourself!</span>";
		else
			print "<span class=\"missing\">Nothing yet!</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">Hometown:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['homeTown']))
		print $userProfile['homeTown'];
	else
		print "<span class=\"missing\">Not specified</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">Interests:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['hobbies']))
		//if ($ownProfile)
		//	print $userProfile['hobbies'];
		//else
			print formatKeywordSearch($userProfile['hobbies'], "2");
	else
		print "<span class=\"missing\">None</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">I&nbsp;Love:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['likes']))
		//if ($ownProfile)
		//	print $userProfile['likes'];
		//else
			print formatKeywordSearch($userProfile['likes'], "3");
	else
		print "<span class=\"missing\">Nothing</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">I&nbsp;Hate:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['dislikes']))
		//if ($ownProfile)
		//	print $userProfile['dislikes'];
		//else
			print formatKeywordSearch($userProfile['dislikes'], "4");
	else
		print "<span class=\"missing\">Nothing</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">Birthday:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['dateOfBirth']))
		print date("F j, Y", $userProfile['dateOfBirth']);
	else
		print "<span class=\"missing\">Not specified</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">Schools:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['schools']))
		//if ($ownProfile)
		//	print $userProfile['schools'];
		//else
			print formatKeywordSearch($userProfile['schools'], "1");
	else
		print "<span class=\"missing\">Not specified</span>";
?>
					</td>
				</tr>
				<tr>
					<td valign="top"><span class="blue">Jobs:</span></td>
					<td valign="top">
<?php
	if (isset($userProfile['jobs']))
		//if ($ownProfile)
		//	print $userProfile['jobs'];
		//else
			print formatKeywordSearch($userProfile['jobs'], "5");
	else
		print "<span class=\"missing\">Not specified</span>";
?>
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="profile_pic">
<?php if (isset($displayPicture) && strlen($displayPicture) > 2) { ?>
		<a href="javascript:window.parent.showPhoto('<?= $userToView ?>', '<?= $displayPicture ?>')"><img src="<?= $mogileFSImagePath ?>/<?= $displayPicture ?>?w=96&h=96&a=1" class="profile_pic" /></a>
		<!-- <img src="<?= $mogileFSImagePath ?>/<?= $displayPicture ?>?w=96&h=96&a=1" class="profile_pic" /> -->
<?php } else { ?>
		<img src="/img/no_display_pic_96.gif" alt="No display picture" title="No display picture" class="profile_pic" />
<?php } ?>

<?php
	if ($ownProfile) {
		print '<p class="small">Your profile is<br/>';

		if ($userProfile['status'] == 'PRIVATE')
			print  '<span class="red_small">Private</span>';
		else if ($userProfile['status'] == 'CONTACTS_ONLY')
			print  'Friends Only';
		else
			print  '<span class="green_small">Public</span>';
		print '</p>';

		if ($showEditProfileLink)
			print '<p><a href="javascript:window.parent.showEditProfile();">Edit Profile</a></p>';
	}

//	if (!isset($userProfile['numProfileViews']) || $userProfile['numProfileViews'] == 0)
//		print "<p>No one has viewed your profile yet</p>";
//	else if ($userProfile['numProfileViews'] > 1)
//		print "<p>" . $userProfile['numProfileViews'] . " people have viewed your profile</p>";
//	else if ($userProfile['numProfileViews'] == 1)
//		print "<p>1 person has viewed your profile</p>";
?>
	</div>

<?php
	if ($showPublicPhotos) {
		print '<div id="public_photos">';
		if ($ownProfile)
			print '<p><span class="green">My Public Photos:</span></p>';
		else
			print '<p><span class="green">' . $userToView . '\'s Photos:</span></p>';

		// Retrieve public photos
		try {
			$content = soap_call_ejb('getGallery', array($userViewing, $userToView, 1, 8));
			$num_entries = $content[0]['numEntries'];
		} catch (Exception $e) {
			// Ignore
		}

		if (!isset($num_entries) || $num_entries <= 0) {
			if ($ownProfile)
				print "<span class=\"missing\">You do not have any photos that can be seen by other users.<br/>Use your <a href=\"javascript:window.parent.showScrapbook()\">Scrapbook</a> to make some of your photos public!</span>";
			else
				print "<span class=\"missing\">No photos to see</span>";
		}
		else {
			$numPhotos = sizeof($content) - 1;

			for ($i = 1; $i <= $numPhotos && $i <= 8; $i++) {
				print "<div class=\"thumbnail\">";
				print "<a href=\"javascript:window.parent.showScrapbookPhoto('" . $userToView . "','" . $content[$i]['id'] . "')\"><img src=\"" . $mogileFSImagePath . "/" . $content[$i]['fileID'] . "?w=96&h=96&a=1\"/></a>";
				print "</div>";
			}

			if ($numPhotos > 8) {
				if ($ownProfile)
					print "<p class=\"center\"><a href=\"javascript:window.parent.showScrapbook();\">View your Scrapbook</a></p>";
				else
					print "<p class=\"center\"><a href=\"javascript:window.parent.showPublicPhotos('" . $userToView . "');\">View more of " . $userToView . "'s photos</a></p>";
			}
		}
	}


	print '</div>';

	if (!$ownProfile) {
		print '<div style="clear:both"><a href="javascript:window.parent.showReportAbuse(2, \''.$userToView.'\', \'\', \'\')">Report this profile</a></div>';
	}
?>
</div>
<?php
}

function formatKeywordSearch($keywordString, $type) {
	$keywordArray = explode(",", $keywordString);

	foreach ($keywordArray as $finalword) {
		$finalword = trim($finalword);
		$keywordOutput .= '<a class="keyword" href="javascript:window.parent.showSearchProfiles(\'keyword=' . strtolower($finalword) . '&type=' . $type . '\');">' . $finalword . '</a>, ';
	}

	// Cut off the last , in the string
	return substr($keywordOutput, 0, strlen($keywordOutput) - 2);
}
?>