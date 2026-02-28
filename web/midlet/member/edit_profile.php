<?php
include_once("../../common/common-inc.php");
include_once("../../sites/common/utilities.php");
include_once("../../common/pageletFunctions.php");
include_once("../../sites/dao/user_dao.php");
include_once("../../sites/dao/avatar_dao.php");

ice_check_session();
$username = ice_get_username();
$errorSuccess = new ErrorSuccess();

$dao = new UserDAO();
$avatar_dao = new AvatarDAO();

$data = $dao->get_user_level_and_reputation_level_permission($username, 'UseDisplayPicture');
$user_level = $data["user_level"];
settype($user_level, "integer");
$required_level = $data["required_level"];
settype($required_level, "integer");
$can_use_display_picture = $user_level >= $required_level;

//If it is posted, process it
if($_POST){
	$gender 		= strip_tags($_POST['gender']);
	$firstname		= strip_tags($_POST['firstname']);
	$lastname		= strip_tags($_POST['lastname']);
	$hometown		= strip_tags($_POST['hometown']);
	$day_dob 		= strip_tags($_POST['day_dob']);
	$month_dob 		= strip_tags($_POST['month_dob']);
	$about_you		= strip_tags($_POST['about_you']);
	$year_dob		= strip_tags($_POST['year_dob']);
	$hobbies 		= strip_tags($_POST['hobbies']);
	$likes 			= strip_tags($_POST['likes']);
	$dislikes 		= strip_tags($_POST['dislikes']);
	$schools 		= strip_tags($_POST['schools']);
	$jobs 			= strip_tags($_POST['jobs']);
	$profile_status = $_POST['profile_status'];
	$delete_display_pic = $_POST['delete_display_pic'];
	$relationshipstatus = $_POST['relationshipstatus'];
	$profile_id		= $_POST['profile_id'];

	if ( (is_numeric($day_dob)) && (is_numeric($month_dob)) && (is_numeric($year_dob))  )
		$DOB = mktime(0,0,0,$month_dob, $day_dob,$year_dob);

	//delete the existing dsiplay pic if delete requested
	if ($delete_display_pic != "") {
		$newImageID = "none";
	}

	//Input Data User Data
	try{
		//Load data user data
		$userDetails = soap_call_ejb('loadUserDetails', array( $username));

		//Update User Details
		if ($newImageID != "") {
			if ($newImageID === "none") {
				unset($userDetails['displayPicture']);
			} else {
				$userDetails['displayPicture'] = (string) $newImageID;
        	}
		}
		soap_call_ejb('updateUserDetails',soap_prepare_call($userDetails));
	}catch(Exception $e){
		$errorSuccess->error = "We are unable to update your profile at this time.";
	}

	//Load Data Profile Data
	try{
		$userProfile = soap_call_ejb('loadUserProfile', array($username, $username));
	}catch(Exception $e){
		//$error = 'User have not created a profile yet.';
	}


	//Input Data Profile Data
	try{
		//Update User Details
		$userProfile['username']    = $username;
		if (strlen($DOB) > 0){
			$userProfile['dateOfBirth'] = (string) $DOB;
		}

		if (strlen($firstname) > 0){
			$userProfile['firstName'] 	= $firstname;
		}
		else
		{
			unset($userProfile['firstName']);
		}

		if (strlen($lastname) > 0){
			$userProfile['lastName'] 	= $lastname;
		}
		else
		{
			unset($userProfile['lastName']);
		}

		if (!empty($gender) || strlen($gender) > 0){
			$userProfile['gender'] 		= $gender;
		} else {
			unset($userProfile['gender']);
		}

		if (strlen($about_you) > 0){
			$userProfile['aboutMe'] 	= $about_you;
		}
		else
		{
			unset($userProfile['aboutMe']);
		}

		if (strlen($schools) > 0){
			$userProfile['schools'] 	= $schools;
		}
		else
		{
			unset($userProfile['schools']);
		}

		if (strlen($hobbies) > 0){
			$userProfile['hobbies'] 	= $hobbies;
		}
		else
		{
			unset($userProfile['hobbies']);
		}

		if (strlen($likes) > 0){
			$userProfile['likes'] 		= $likes;
		}
		else
		{
			unset($userProfile['likes'] );
		}

    	if (strlen($dislikes) > 0){
			$userProfile['dislikes'] 	= $dislikes;
		}
		else
		{
			unset($userProfile['dislikes']);
		}

		if (strlen($profile_status) > 0){
			$userProfile['status'] 		= $profile_status;
		}

		if (strlen($hometown) > 0){
			$userProfile['homeTown'] 	= $hometown;
		}
		else
		{
			unset($userProfile['homeTown']);
		}

		if (strlen($relationshipstatus) > 0){
			$userProfile['relationshipStatus'] 	= $relationshipstatus;
		}

		if (strlen($jobs) > 0){
			$userProfile['jobs'] 		= $jobs;
		}
		else
		{
			unset($userProfile['jobs']);
		}

		soap_call_ejb('updateUserProfile',soap_prepare_call($userProfile));
		$errorSuccess->success = 'Profile Updated successfully.';
		get_ice_dao()->edit_profile_metric();
	}catch(Exception $e){
		$errorSuccess->error = "We are unable to update your profile at this time.";
	}
} else {
	try {
		$userProfile = soap_call_ejb('loadUserProfile', array($username, $username));
		$userDetails = soap_call_ejb('loadUserDetails', array($username));
	}catch(Exception $e){
		//$errorSuccess->error = "We are unable to update your profile at this time.";
	}

	$dob = $userProfile['dateOfBirth'];
	$day_dob = date("j",$dob);
	$month_dob = date("n",$dob);
	$year_dob = date("Y",$dob);
	$lastname = strip_tags($userProfile['lastName']);
	$firstname = strip_tags($userProfile['firstName']);
	$gender = strip_tags($userProfile['gender']);
	$hobbies = strip_tags($userProfile['hobbies']);
	$likes = strip_tags($userProfile['likes']);
	$dislikes = strip_tags($userProfile['dislikes']);
	$schools = strip_tags($userProfile['schools']);
	$profile_status = strip_tags($userProfile['status']);
	$hometown = strip_tags($userProfile['homeTown']);
	$relationshipstatus = strip_tags($userProfile['relationshipStatus']);
	$jobs = strip_tags($userProfile['jobs']);
	$about_you = strip_tags($userProfile['aboutMe']);

}

?>

<html>
  <head>
    <title>Edit Profile</title>
  </head>
  <?php
  	showCommonStyle();
  ?>

  <body bgcolor="white">
  	<?php
  	$errorSuccess->show();
	?>

	<form method="POST" action="<?=$server_root?>/midlet/member/edit_profile.php">
	<input type="hidden" name="submit_action" value"true">
	<p><b>Current display pic</b></p>
	<?php
		echo "<p>";
		$height = getAdjustedHeightFromScreen(25);
		$width = getAdjustedWidthFromScreen(25);
		if( !empty($userDetails['displayPicture']) )
		{
			showUserAvatar($userDetails['displayPicture']);
		}
		else
		{
?>
			<img src="<?=$server_root?>/images/nodisplaypic_<?=$width?>.png" width="<?=$width?>" height="<?=$width?>">
<?php
		}
		if( $can_use_display_picture )
		{
			printf('<a href="%s/setDisplayFromPhotos/?v=midlet">Change My Picture</a>', $server_root);
			echo '<br>';
		}
		printf('<a href="%s/sites/index.php?c=avatar&v=midlet&a=home">My Avatar</a>', $server_root);
		echo "</p>";
	?>
	<p><b>First Name</b></p>
	<p><input type="text" name="firstname" value="<?=$firstname?>" size="7" alt="First Name"></p>

	<p><b>Last Name</b></p>
	<p><input type="text" name="lastname" value="<?=$lastname?>" size="7" alt="Last Name"></p>

	<p><b>Sex</b></p>
 	<p>
		<select name="gender">
			<option value="" <?php if ($gender == '') { echo "Selected";} ?>>Undecided</option>
			<option value="MALE" <?php if ($gender == "MALE") { echo "Selected";} ?>>Male</option>
			<option value="FEMALE" <?php if ($gender == "FEMALE") { echo "Selected";} ?>>Female</option>
		</select>
	</p>

	<p><b>Relationship Status</b></p>
	<p>
		<select name="relationshipstatus">
			<option value="SINGLE" <?php if ($relationshipstatus == 'SINGLE') { echo "Selected";} ?>>Single</option>
			<option value="IN_A_RELATIONSHIP" <?php if ($relationshipstatus == 'IN_A_RELATIONSHIP') { echo "Selected";} ?>>In a Relationship</option>
			<option value="DOMESTIC_PARTNER" <?php if ($relationshipstatus == 'DOMESTIC_PARTNER') { echo "Selected";} ?>>Domestic Partner</option>
			<option value="MARRIED" <?php if ($relationshipstatus == 'MARRIED') { echo "Selected";} ?>>Married</option>
			<option value="COMPLICATED" <?php if ($relationshipstatus == 'COMPLICATED') { echo "Selected";} ?>>Complicated</option>
		</select>
	</p>

	<p><b>Date of Birth</b></p>
	<p>Day : </p>
	<p>
		<select name="day_dob">
			<option value="">DD</option>
			<?php
				for ($c=1;$c<=31;$c++){
			?>
					<option <?php if ($day_dob == $c) {echo "selected";} ?> value="<?=$c?>"><?=$c?></option>
			<?php
				}
			?>
		</select>
	</p>
	<p>Month :</p>
	<p>
		<select name="month_dob">
			<option value="">MM</option>
			<?php
				for ($c=1;$c<=12;$c++){
			?>
					<option <?php if ($month_dob == $c) {echo "selected";} ?> value="<?=$c?>"><?=$c?></option>
			<?php
				}
			?>
		</select>
	</p>
	<p>Year :</p>
	<p>
		<select name="year_dob">
			<option value="">YYYY</option>
			<?php
				for ($c=date(Y)-110;$c < date(Y)-4;$c++){
			?>
					<option <?php if ($year_dob == $c) {echo "selected";} ?> value="<?=$c?>"><?=$c?></option>
			<?php
				}
			?>
		</select>
	</p><br>

	<p><b>Hometown</b></p>
	<p><input type="text" name="hometown" value="<?=$hometown?>" size="7" alt="Hometown"></p>

	<p><b>Interests</b></p>
	<p><input type="text" name="hobbies" value="<?=$hobbies?>" size="7" alt="Hobbies"></p>

	<p><b>I Love</b></p>
	<p><input type="text" name="likes" value="<?=$likes?>" size="7" alt="Likes"></p>

	<p><b>I Hate</b></p>
	<p><input type="text" name="dislikes" value="<?=$dislikes?>" size="7" alt="Dislikes"></p>

	<p><b>Schools</b></p>
	<p><input type="text" name="schools" value="<?=$schools?>" size="7" alt="Schools"></p>

	<p><b>Jobs / Occupations</b></p>
	<p><input type="text" name="jobs" value="<?=$jobs?>" size="7" alt="Jobs"></p>

	<p><b>About you</b></p>
	<p>
		<textarea name="about_you" cols="10" rows="5" alt="About You">
		<?=$about_you?>
		</textarea>
	</p>

	<p><b>Share Profile and activity with:</b></p>
	<p>
		<select name="profile_status">
			<option value="PUBLIC" <?php if ($profile_status == 'PUBLIC') { echo "Selected";} ?> >Everyone</option>
			<option value="CONTACTS_ONLY" <?php if ($profile_status == 'CONTACTS_ONLY') { echo "Selected";} ?> >Friends</option>
			<option value="PRIVATE" <?php if ($profile_status == 'PRIVATE') { echo "Selected";} ?> >Myself</option>
		</select>
		<a href="<?=$server_root?>/midlet/member/profile_help.php">What does this mean?</a>
	</p><br>
	<p><input type="submit" value="Submit"></p><br>
	</form>

	<p><a href="<?=$server_root?>/my/profile/?v=midlet">Back to My Profile</a> &gt;&gt;</p>
	<p><a href="<?=$server_root?>/midlet/member/profile_search_main.php">Search Profiles</a> &gt;&gt;</p>
	<p><a href="<?=$server_root?>/my/photos/?v=midlet">View My Photos</a>&gt;&gt;</p>
	<?php
		print '<p><a href="'.$server_root.'/profile/'.$username.'/?v=midlet">Help</a>&gt;&gt;</p>';
	?>
	</body>
</html>