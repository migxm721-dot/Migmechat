<?php
	$from = get_attribute_value("from");
	if( !empty($from) )
	{
		switch($from)
		{
		case "profile":
			$url = get_controller_action_url('profile', 'home', empty($username) ? '' : array('username' => $username));
			break;
		case "contact_invite":
			$page = get_attribute_value("page", "integer", 1);
			$url = get_controller_action_url('invite', 'invitation', array('page' => $page));
			break;
		case "group":
			$group_id = get_attribute_value("group_id", "integer", 0);
			$url = get_controller_action_url('group', 'home', array('group_id' => $group_id));
			break;
		case "friends":
			$url = get_controller_action_url('contacts', 'friends', array('username' => $username));
			break;
		}
		if( !empty($url) )
		{
?>
			<p><a href="<?=$url?>"><?=_('Back')?></a></p>
<?php
		}
	}
?>