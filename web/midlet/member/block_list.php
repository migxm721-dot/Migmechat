<?php
include_once("../../common/common-inc.php");
include_once("../../common/common-config.php");
include_once("../../common/pageletFunctions.php");
ice_check_session();
$userName = ice_get_username();
$pageNumber = 1;
$resultsPerPage = 5;
$error="";
$unblockUser="";

if( isset( $_GET["unblock"] ) )
{
	$unblockUser = $_GET["unblock"];
	try
	{
		soap_call_ejb("unblockUser", array($userName, $unblockUser));
	}
	catch(Exception $ex)
	{
		$error = $ex->getMessage();
	}
}

if( isset( $_GET["page"] ) )
{
	$pageNumber = $_GET["page"];
	settype($pageNumber, "int");
}

try
{
	$blockedUsers = soap_call_ejb("getUserBlockedList",
				array($userName, "", $pageNumber, $resultsPerPage, true, true));
	$totalPages = $blockedUsers["totalPages"];
	$pageNumber = $blockedUsers["pageNumber"];
}
catch(Exception $ex)
{
	$error = $ex->getMessage();
}
?>

<html>
  <head>
    <title>Block List</title>
  </head>
  <body bgcolor="white">
  	<?php
  		if( $unblockUser != "" )
  		{
  			printf('<p style="color:green">%s has been unblocked.</p>', $unblockUser);
  		}
  	?>
  	<br>
	<p><em>Blocked Users</em></p>
	<br>
	<?php
		if( $error == "")
		{
			$users = $blockedUsers["blockedUsers"];
			$totalResults = $blockedUsers["totalResults"];
			settype( $totalResults, "integer");
			if( $totalResults > 0 )
			{
				foreach($users as $blockedUser)
				{
					printf("<p>%s: <a href='%s/midlet/member/block_list.php?unblock=%s&page=%d'>Unblock</a></p>", $server_root, $blockedUser, $blockedUser, $pageNumber);
				}


				if( $totalPages > 1 )
				{
					echo "<br><p><center>";
					if( $pageNumber > 1 )
					{
						printf("<a href='%s/midlet/member/block_list.php?page=1'>&lt;&lt;</a>", $server_root);
						printf("<a href='%s/midlet/member/block_list.php?page=%d'>&lt;</a>&nbsp;", $server_root, $pageNumber-1);
					}

					printf("%d/%d&nbsp;", $pageNumber, $totalPages);

					if( $pageNumber < $totalPages )
					{
						printf("<a href='%s/midlet/member/block_list.php?page=%d'>&gt;</a>", $server_root, $pageNumber+1);
						printf("<a href='%s/midlet/member/block_list.php?page=%d'>&gt;&gt;</a>", $server_root, $totalPages);
					}
					echo "</center></p>";
				}
			}
			else
			{
				echo '<p>You do not have any blocked users.</p>';
			}
		}
		else
		{
			echo '<p style="color:red">'.$error.'</p>';
		}
	?>
 	<br>
  </body>
</html>