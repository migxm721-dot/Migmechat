<?php
	require_once("../../common/common-inc.php");
	require_once("../../common/pageletFunctions.php");

	$loc=1;
	if(isset($_GET["loc"]))
	{
		$loc = $_GET["loc"];
		settype($loc,"integer");
	}

	$username = "";
	if(isset($_GET["username"]))
		$username = $_GET["username"];
?>
<html>
	<head>
		<title>Edit Profile Help</title>
	</head>
	<body>
		<p>Your profile privacy settings control what people are allowed to see on your profile and other parts of mig33 such as the Friends list and the Updates tab in later versions. Here is an explanation of how each setting works.</p><br>
		<p><b>Share with only Myself:</b></p>
		<p>Only you will see your profile, activity and display picture. The pictures in your Photos area on your profile page are controlled individually.</p><br>
		<p><b>Share with Friends:</b></p>
		<p>Only you and your friends will see your profile, activity and display picture. The pictures in your Photos area on your profile page are controlled individually. You can further customize who can see your activity on mig33 in the <a href="<?=$server_root?>/midlet/member/view_update_settings.php">Updates Settings</a> page.</p><br>
		<p><b>Share with Everyone:</b></p>
		<p>Everyone on mig33 can search for you and see your profile and display picture, but only your friends and you will see the Activity and Friends parts of your profile page. The pictures in your Photos area on your profile page are controlled individually. You can further customize who can see your activity on mig33 on the <a href="<?=$server_root?>/midlet/member/view_update_settings.php">Updates Settings</a> page.</p><br>
<?php
		if( !isMidletVersion4() )
		{
			switch($loc)
			{
			case 1:
?>
			<p><a href="<?=$server_root?>/midlet/member/edit_profile.php">Back to edit profile</a></p>
<?php
				break;
			case 2:
?>
			<p><a href="<?=$server_root?>/profile/<?=$username?>/?v=midlet">Back to profile</a></p>
<?php
				break;
			}
		}
?>
		<br>
	</body>
</html>