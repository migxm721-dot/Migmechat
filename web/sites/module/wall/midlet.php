<?php
	$stats_icons = array();
	$action_links = array();
?>
<p>
	<img src="<?=$image_server?>/a/<?=$username?>?w=<?=$avatar_width?>&h=<?=$avatar_height?>&a=1&c=1" hspace="2" vspace="2" style="float: left;" width="<?=$avatar_width?>" height="<?=$avatar_height?>">
	<a href="<?=get_controller_action_url('profile', 'home', array('username' => $username))?>"><?=$username?></a>:&nbsp;
	<?php
		if($post_limit):
			echo ellipsis($post, $post_limit);
			if(strlen($post) > $post_limit):
				echo '<a href="'.$comment_url.'">'._('more').'</a>';
			endif;
		else:
			echo $post;
		endif; ?>&nbsp;
	<?php if(isset($all_groups_wall) && $all_groups_wall): ?>
		<span style="color:grey"><?=_('from group')?> </span><a href="<?=$group_url?>"><?=ellipsis($group_name,15)?></a>&nbsp;
	<?php endif; ?>
	<span style="color:grey"><?=$date_created_since?></span>
</p>
<p>
	<?php
		if (ClientInfo::is_midlet_version_45_or_higher())
		{
			// Stats Icons
			if($allow_comment)
				$stats_icons[] = '<img src="'.$server_root.'/sites/resources/images/mig33-blue/misc/chat.png" width="16" height="16"  hspace="2">'.$num_comments;
			if($allow_like)
				$stats_icons[] = '<img src="'.$server_root.'/sites/resources/images/icons/16/16_likes.png" width="16" height="16" hspace="2">'.$num_likes.'&nbsp;&nbsp;';

			echo implode(' ', $stats_icons);

			// Action Links
			if($allow_comment)
				$action_links[] = '<a href="'.$comment_url.'">'._('Comment').'</a>';
			if($allow_like)
				$action_links[] = '<a href="'.$like_url.'">'._('Like').'</a>';
			if($allow_remove)
				$action_links[] = '<a href="'.$remove_url.'">'._('Remove').'</a>';

			// echo implode(' - ', $action_links); //disabling wallpost MGBO-569
		}
		else
		{
			// Action Links
			if($allow_comment)
			{
				if($num_comments > 1)
					$action_links[] = '<a href="'.$comment_url.'">'._('Comments').'</a> ('.$num_comments.')';
				elseif($num_comments == 1)
					$action_links[] = '<a href="'.$comment_url.'">'._('Comment').'</a> (1)';
				elseif($num_comments == 0)
					$action_links[] = '<a href="'.$comment_url.'">'._('Comment').'</a>';
			}
			if($allow_like)
			{
				if($num_likes > 1)
					$action_links[] = '<a href="'.$like_url.'">'._('Likes').'</a> ('.$num_likes.')';
				elseif($num_likes == 1)
					$action_links[] = '<a href="'.$like_url.'">'._('Like').'</a> (1)';
				elseif($num_likes == 0)
					$action_links[] = '<a href="'.$like_url.'">'._('Like').'</a>';
			}
			if($allow_remove)
				$action_links[] = '<a href="'.$remove_url.'">'._('Remove').'</a>';

			//  echo implode(' - ', $action_links); //disabling wallpost MGBO-569
		}
	?>
</p>
