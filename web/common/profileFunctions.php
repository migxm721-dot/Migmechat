<?php
require_once("pageletFunctions.php");

$headerStyle;

/**
* Get the default header style
**/
function getHeaderStyleAttribute()
{
	global $headerStyle;
	if( empty($headerStyle) )
	{
		$headerStyle = new Style("black", "#CFCFCF");
		$headerStyle->setDisplayBlock();
	}
	return $headerStyle->showAsAttribute();
}

/**
* Print the profile header
**/
function showHeader($title)
{
	if( isMidletVersion4() )
	{
		printf('<div %s><b>%s</b></div><br>', getHeaderStyleAttribute(), $title);
		//printf('<div style="background-color:#CFCFCF;color:black"><b>%s</b></div><br>', $title);
	}
	else
	{
		printf('<p><b>%s</b></p>', $title);
	}
}

/**
* Get the profile data for a user
**/
function getProfileData($sessUser, $username, &$userProfile, &$userDetails, &$contact, &$error)
{
	$error = '';
	if( $username == '' ) $username = $sessUser;
	try
	{
		$userProfile = soap_call_ejb('loadUserProfile', array($sessUser, $username));
	}
	catch (Exception $e)
	{
	}

	try
	{
		if($username == $sessUser){
			$userDetails = soap_call_ejb('loadUserDetails', array($sessUser));
			$contact = array( "isContact" => true );
		} else {
			$userDetails = soap_call_ejb('loadUserDetails', array($username));
			settype($sessUser, "string");
			settype($username, "string");
			$contact = soap_call_ejb('getUserContactDetails', array($sessUser, $username));
		}
		$userDTO = new UserDTO( $userProfile, $userDetails, $contact );
		$userDTO->username = $username;
		return $userDTO;
	}
	catch(Exception $e)
	{
		$error = $e->getMessage();
	}
}

/**
* Displays the short profile of a user in most of the main pages
*/
function getShortProfile(&$userDTO, $viewOwn, $showEditLink=true) {
	global $mogileFSImagePath;
	global $server_root;

	/***************Display picture and short description of the user********************/
	//Public and Friends will show all details
	//Show display pic
	echo '<p>';
	$displayPicture = '';
	if( $userDTO->canViewProfile() )
		$displayPicture = $userDTO->displayPicture;

	showUserAvatar($displayPicture);

	//Show short description section
	//Username
	echo '<b>'.ellipsis($userDTO->username, 10).'</b>';
	if( $viewOwn || $userDTO->contact->isContact )
	{
		if( !empty( $userDTO->statusMessage ) )
		{
			printf('<br>"%s"', ellipsis($userDTO->statusMessage, 35));
		}
		if( $viewOwn && $showEditLink )
		{
			printf('<br>%s - <a href="%s/midlet/member/profile_help.php?loc=2&username=%s">[?]</a>', $userDTO->getStatus(), $server_root, $userDTO->username);
			printf('<br><a href="%s/midlet/member/edit_profile.php">Edit Profile</a>', $server_root);
		}
		else if( $userDTO->contact->isContact )
		{
			printf('<br>(Friend)');
		}
	}
	else
	{
		$from = "";
		if( isset($_GET["f"]) )
			$from = $_GET["f"];

		if( isMidletVersion4() && $from!="invite")
			printf('<br><a href="mig33:inviteFriend(%s)">Invite to be Friends</a>', $userDTO->username);
		else if( $from == "invite" )
		{
			$page = $_GET["p"];
			if( empty($page) ) $page = 1;
			settype($page, "integer");
			printf('<p><a href="%s/sites/index.php?c=invite&v=midlet&a=invitation&p=%d">Back to invitation</a></p>', $server_root, $page);
		}
	}

	echo '</p>';
}

/**
* Get the updates for a user
**/
function getUpdates(UserPagingObject &$pagingObject)
{
	try
	{
		$eventsDetail = soap_call_ejb('getPagingUserEventGeneratedByUser',
				array($pagingObject->username, $pagingObject->pageNumber, $pagingObject->numberOfEntries));
		$totalEventCount = $eventsDetail['totalEventCount'];
		settype($totalEventCount, "integer");
		showHeader("Activity");
		$return_data = array();
		if( $totalEventCount == 0 )
		{
			echo '<p>User does not have any updates.</p><br>';
			$return_data['total_pages'] = 0;
		}
		else
		{
			$events = $eventsDetail["events"];
			$current = time();
			for( $i = 0; $i < sizeof($events); $i++ )
			{
				$event = $events[$i];
				$timestamp = $event["timestamp"];
				settype($timestamp, "integer");

				$diff = new DateTimeDifference( round($timestamp/1000), $current );
				if( isMidletVersion4() )
				{
					printf('<p><span style="color:grey">%s :</span> %s</p>', $diff->getDateTimeDifferenceString(), $event["text"]);
				}
				else
				{
					printf("<p>%s: %s</p>", $diff->getDateTimeDifferenceString(), $event["text"]);
				}
			}
			$return_data['total_pages'] = $eventsDetail["totalPages"];
			$return_data['total_results'] = $eventsDetail['totalEventCount'];
		}
		return $return_data;
	}
	catch(Exception $e)
	{
		printf('<p style="color:red">%s</p>', $e->getMessage());
	}
}

function getCommunities(UserPagingObject $pagingObject, $view_own = false)
{
	global $server_root;

	$content = soap_call_ejb('getGroups', array($pagingObject->username, $pagingObject->pageNumber, $pagingObject->numberOfEntries));
	showHeader("Groups");
	$communities = $content['groups'];
	$total_results = $content['totalresults'];
	settype($total_results, "integer");
	if( $total_results == 0 )
	{
		if($view_own)
		{
			printf('<p>You don\'t belong to any groups yet. <a href="%s/sites/index.php?c=group&v=midlet&a=list">Browse groups.</a></p>', $server_root);
		}
		else
		{
			printf("User does not belong to any groups.");
		}
	}
	else
	{
		for($i=0; $i < sizeof($communities); $i++)
		{
			echo "<p>";
			//showUserAvatar($communities[$i]['picture'], 20);
			$pending = $communities[$i]['pendingInvitation'];
			settype($pending, "integer");
			if( $pending == 1 && $view_own)
			{
				printf('You have been invited to join the <a href="%s/sites/index.php?c=group&v=midlet&a=home&cid=%s">%s</a> group', $server_root, $communities[$i]["id"], $communities[$i]["name"]);
				echo "<br>";
				printf('(<a href="%s/sites/index.php?c=group&v=midlet&a=join_group&cid=%s">Join</a> | ', $server_root, $communities[$i]['id']);
				printf('<a href="%s/sites/index.php?c=group&v=midlet&a=ignore&cid=%s">Ignore</a>)', $server_root, $communities[$i]['id']);
			}
			else
			{
				printf('<a href="%s/sites/index.php?c=group&v=midlet&a=home&cid=%s">%s</a>', $server_root, $communities[$i]["id"], $communities[$i]["name"]);
			}
			echo "</p>";
		}
	}
	return $content;
	//var_dump($content);
}

// Get the friends list for a particular user
function getFriends($username, UserPagingObject &$pagingObject, $percentage=20 )
{
	try
	{
		global $server_root;
		$content = soap_call_ejb('getFriends', array($username, $pagingObject->username, '',
							$pagingObject->pageNumber, $pagingObject->numberOfEntries));
		$page = $content['pageNumber'];
		$num_pages = $content['totalPages'];
		$num_entries = $content['totalResults'];
		showHeader("Friends");

		if($num_entries <= 0)
		{
			echo '<p>User does not have any friends.</p><br>';
		}
		else
		{
			$friendsList = $content['friends'];

			foreach( $friendsList as $friend )
			{
				$contactname = $friend["fusionUsername"];
				$contactpicture = $friend["displayPicture"];
				$contactdisplay = $friend["fusionUsername"];
				$contactprivacy = $friend["privacy"];
				if( $contactprivacy == "PRIVATE" ) $contactpicture = "";
				echo "<p>";
				showUserAvatar($contactpicture, $percentage);
				echo "<a href='".$server_root."/profile/".$contactdisplay."/?v=midlet'>".$contactdisplay."</a>";
				echo "</p>";
			}
		}
		return $content;
	}
	catch(Exception $e)
	{
		$pagingObject->error = '<p>Unable to retrieve the user\'s friends list. '.$e->getMessage().'</p><br>';
	}
}
?>