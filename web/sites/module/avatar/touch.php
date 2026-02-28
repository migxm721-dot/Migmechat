<?php
	require_once("avatar_model.php");

	global $mogileFSImagePath;

	if( empty($avatar_filename) )
		$avatar_filename = $data['avatar_filename'];

	if( !isset($show_avatar) )
		$show_avatar = true;

	$filename= $avatar_filename;

	if( empty($avatar_filename) || $show_avatar == false )
	{
		if( $hide_if_not_available == false )
		{
			if(get_controller() == 'group')
			{
				if($use_sprites) {
					echo "<i class='icon icon-group'></i>";
				} else { ?>
					<img src="/sites/resources/images/group/touch-default-group-<?=$width?>.png" />
				<?php }
				
			}
			else
			{
?>
				<img src="/sites/resources/images/avatar/touch-default-avatar-<?=$width?>.png" />
<?php
			}
		}
	}
	else
	{
?>
		<img src="<?=$mogileFSImagePath?>/<?=$filename?>?w=<?=$width?>&h=<?=$width?>&a=1&c=1" />
<?php
	}
?>