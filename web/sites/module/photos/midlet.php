<?php
	global $mogileFSImagePath;

	$username = get_attribute_value('username');
	if(empty($username))
		$username = $session_user;

	if( sizeof($photos) == 0 )
	{
?>
		<p><?=sprintf(_('No photos found for %s'), $username)?></p><br>
<?php
	}
	else
	{
		$i = 0;
		foreach($photos as $photo)
		{
			$image_ratio = get_image_ratio($photo->width, $photo->height, $requestedWidth, $requestedHeight);
	?>
				<p>
					<img src="<?=$mogileFSImagePath?>/<?=$photo->image_id?>?w=<?=$image_ratio['width']?>&h=<?=$image_ratio['width']?>&c=1&a=1" width="<?=$image_ratio['width']?>" height="<?=$image_ratio['width']?>" vspace="2" hspace="2" style="float:left">
					<span class="sec"><?=$photo->description ?></span><br>
					<?php
						if(get_attribute_value('from', 'string', '') == 'sendfromphoto' || $show_send)
						{
							if (ClientInfo::can_support_scrapbook_photo())
							{
								printf('<a href="mig33:sendScrapbookPhoto(%s)">'._('Send to Friend').'</a><br>', $photo->image_id);
							}
							else
							{
								printf('<tag id="%s" type="2">'._('Send to Friend').'</tag><br>', $photo->image_id);
							}
						}
						elseif($session_user == $username || $show_set_profile)
						{
							echo '<a href="'.get_action_url('set_profile_picture', array('image_id' => $photo->image_id, 'item_id' => $photo->item_id)).'">'._('Set as profile picture').'</a><br>';
						}
					?>
						<a href="<?=get_action_url('view_photo', array('image_id' => $photo->image_id, 'item_id' => $photo->item_id, 'username' => $username, 'index' => ($offset + $i)))?>"><?=_('More actions')?></a>
				</p>
				<br>
	<?php
			$i++;
		}
		$data['url_prefix_bare'] = get_action_url("view_photos", array('username' => $username));
		$data['url_prefix_prev'] = get_action_url("view_photos", array('username' => $username, 'page' => (get_attribute_value('page', 'integer', 1) - 1)));
		$data['url_prefix_next'] = get_action_url("view_photos", array('username' => $username, 'page' => (get_attribute_value('page', 'integer', 1) + 1)));

		Modules::include_module("navigation_photos", $data);
	}
?>