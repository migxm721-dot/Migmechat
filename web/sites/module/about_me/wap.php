<?php
	if(!empty($user_profile) && $user_profile->status != 'PRIVATE')
	{
		//Show moderator if needed
		if($user_detail->is_moderator()){echo('<b>'._('Moderator').'</b><br/>');}

		//Generate dob/gender
		$gender_dob = "";
		if(!empty($user_profile->gender))
			$gender_dob = $user_profile->get_gender();
		if(strlen($user_profile->date_of_birth) !=0)
		{
			if( strlen($gender_dob) > 0 )
				$gender_dob = $gender_dob.", ";
			$gender_dob = sprintf("%s%d",$gender_dob, $user_profile->get_age());
		}

		if( !empty($user_profile->relationship_status) && $user_profile->relationship_status != "undefined")
		{
			if( strlen($gender_dob) > 0 )
				$gender_dob = $gender_dob.", ";
			$gender_dob = $gender_dob.$user_profile->relationship_status;
		}

		if(!empty($gender_dob) )
		{
?>
		<p>Gender: <?=$gender_dob?></p>
<?php
		}
		if(!empty($user_profile->hometown))
		{
?>
		<p>Hometown: <?=$user_profile->hometown?></p>
<?php
		}

		if($user_detail->countryID > 0)
		{
?>
		<p>Country: <?=$user_detail->get_country_name()?></p>
<?php
		}
?>
		<p><?=_('Member since')?>: <?=$user_detail->get_member_since_date()?></p>
<?php
	}
?>