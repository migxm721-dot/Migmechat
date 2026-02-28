<?php
	require_once("avatar_model.php");

	/**
	*
	* Requires:
	*	$avatar_filename => String
	*
	**/
	global $mogileFSImagePath, $server_root;

	if( empty($avatar_filename) )
		$avatar_filename = $data['avatar_filename'];

	if( !isset($show_avatar) )
		$show_avatar = true;

	if( !isset($width) )
	{
		$width = getAdjustedWidthFromScreen(20);
	}
	$filename= $avatar_filename;

	if(get_controller() == 'group')
	{
		$small_src = 'group/default-group-24.png';
		$big_src = 'group/default-group-48.png';
	}
	else
	{
		$small_src = 'avatar/default-avatar-24x24.png';
		$big_src = 'avatar/default-avatar.png';
	}
?>

<?php
	if( empty($avatar_filename) || $show_avatar == false )
	{
		if (empty($hide_if_not_available))
		{
			if($width < 30)
				$src = $small_src;
			else
				$src = $big_src;
?>
		<img src="<?=$server_root?>/sites/resources/images/<?=$src?>" hspace="2" vspace="2" width="<?=$width?>" height="<?=$width?>" style="float:left">
<?php
		}
	}
	else
	{
		if(gettype(strpos($avatar_filename,'http://'))=="integer")
		{
			$filename = str_replace('=48','='.$width,$filename); ?>
			<img src="<?=$filename?>" hspace="2" vspace="2" style="float:left" width="<?=$width?>" height="<?=$width?>">
		<?php } else { ?>
			<img src="<?=$mogileFSImagePath?>/<?=$filename?>?w=<?=$width?>&h=<?=$width?>&a=1&c=1" hspace="2" vspace="2" style="float:left" width="<?=$width?>" height="<?=$width?>">
		<?php
		}
	}
?>