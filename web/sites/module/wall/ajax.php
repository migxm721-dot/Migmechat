<?php
	$stats_icons = array();
	$action_links = array();

	// Stats Icons
	if($allow_comment)
		$stats_icons[] = '<span class="wall_post_comment_number">'.$num_comments.'</span>';
	if($allow_like)
		$stats_icons[] = '<span class="wall_post_likes_number">'.$num_likes.'</span>';

	// Action Links
	if($allow_comment && (!isset($display_more_comments) || $display_more_comments) )
	{
		if($num_comments > 2)
			$action_links[] = '<a href="'.$comment_url.'">'._('View More Comments').'</a>';
		else
			$action_links[] = '<a href="#" onclick="$(\'#'.$from.'-wall_post_comments_add-'.$id.'\').toggle(); return false;">'._('Comment').'</a>';
	}
	if($allow_like)
		$action_links[] = '<a class="action_like" href="'.$like_url.'">'._('Like').'</a>';
	if($allow_remove)
		$action_links[] = '<a class="action_remove" href="'.$remove_url.'">'._('Remove').'</a>';
?>
<li class="wall_post">
	<div class="img_block">
		<img class="image_thumbnail_with_frame" src="<?=$image_server?>/a/<?=$username?>?w=<?=$avatar_width?>&h=<?=$avatar_height?>&a=1&c=1" width="<?=$avatar_width?>" height="<?=$avatar_height?>" />
	</div>
	<div class="img_block_content">
		<div <?php if(isset($show_full_wrapper)): echo 'class="img_block_content_wrapper full_wrapper"'; else: echo 'class="img_block_content_wrapper partial_wrapper"'; endif; ?>>
			<span class="user_who_posted">
				<a href="<?=get_controller_action_url('profile', 'home', array('username' => $username))?>"><?=$username?></a>
			</span>
			&nbsp;<?=$post?>&nbsp;&nbsp;
			<span class="wall_post_timestamp"><?=$date_created_since?></span>
			<?php if(isset($all_groups_wall) && $all_groups_wall): ?>
				<br /><span class="wall_post_timestamp"><?=_('From group')?></span> <a href="#" onclick="<?=$group_url?>"><?=ellipsis($group_name,15)?></a>&nbsp;
			<?php endif; ?>
			<div class="wall_post_action">
				<?php echo implode(' ', $stats_icons); ?>
				<?php echo implode(' &nbsp;&nbsp; ', $action_links); ?>
			</div>
		</div>
	</div>
	<?php if(sizeof($comments) > 0): ?>
		<div class="clear"></div>
		<ul class="wall_post_comment_list">
			<?php foreach($comments as $comment): ?>
				<li class="comment">
					<div class="img_block">
						<img class="image_thumbnail_with_frame" src="<?=$image_server?>/a/<?=$comment->author_username?>?w=<?=$comment_avatar_width?>&h=<?=$comment_avatar_height?>&a=1&c=1" width="<?=$comment_avatar_width?>" height="<?=$comment_avatar_height?>" />
					</div>
					<div class="img_block_content">
						<div <?php if(isset($show_full_wrapper)): echo 'class="img_block_content_wrapper comment_full_wrapper"'; else: echo 'class="img_block_content_wrapper comment_partial_wrapper"'; endif; ?>>
							<span class="user_who_posted">
								<a href="<?=get_controller_action_url('profile', 'home', array('username' => $comment->author_username))?>"><?=$comment->author_username?></a>
							</span>
							&nbsp;<?=$comment->comment?>&nbsp;&nbsp;
							<span class="wall_post_timestamp"><?=$comment->date_created_since?></span>
							<?php
							//allow removal of comments if own_profile or owner of comment
							if($comment_allow_remove): ?>
								<span class="wall_post_action">&nbsp;<a href="<?=get_action_url($comment_remove_action, array('post_id' => $id, 'comment_id' => $comment->id, 'number_of_entries' => $number_of_entries, 'username' => $current_user, 'from' => $from, 'rec' => $comment_sort)); ?>"><?=_('Remove')?></a></span>
							<?php endif; ?>
						</div>
					</div>
					<br class="clear" />
				</li>
			<?php endforeach; ?>
			<?php if($allow_user_wall_comment && $can_post_wall_post_comment): ?>
				<li class="form_new_comment">
					<form method="post" action="<?=$comment_add_url?>">
						<input class="writebox new_comment" type="text" name="bd" value="<?=_('Add Your Comment')?>" size="30" style="width: 230px;" onfocus="if($(this).val() == '<?=_('Add Your Comment')?>') { $(this).val(''); }" onblur="if($(this).val() == '') { $(this).val('<?=_('Add Your Comment')?>'); }" /><!--&nbsp;<input type="submit" value="<?=_('Share')?>">-->
					</form>
					<br class="clear" />
				</li>
			<?php endif; ?>
		</ul>
	<?php elseif($allow_user_wall_comment && $can_post_wall_post_comment): ?>
		<div id="<?=$from?>-wall_post_comments_add-<?=$id?>" class="hide">
			<br class="clear" />
			<ul class="wall_post_comment_list">
				<li class="form_new_comment">
					<form method="post" action="<?=$comment_add_url?>">
						<input type="text" name="bd" class="new_comment" value="<?=_('Add Your Comment')?>" size="30" style="width: 230px;" onfocus="if($(this).val() == '<?=_('Add Your Comment')?>') { $(this).val(''); }" onblur="if($(this).val() == '') { $(this).val('<?=_('Add Your Comment')?>'); }" />&nbsp;<input type="submit" value="<?=_('Share')?>">
					</form>
					<br class="clear" />
				</li>
			</ul>
		</div>
	<?php endif; ?>
	<div class="clear"></div>
</li>