<?php
include_once("../../common/common-inc.php");
include_once("../../common/pageletFunctions.php");
include_once("../../common/style.php");

ice_check_session();
$username = ice_get_username();
$attributes = getAttributeArray();
$pagetype = 0;
$id = 0;
if( isset($attributes['pagetype']) )
	$pagetype = $attributes['pagetype'];
settype( $pagetype, "integer" );

if( isset($attributes['id']) )
{
	$id = $attributes['id'];
}
settype( $id, "integer" );

$pageNumber = 1;
if( isset($attributes['pagenum']) )
{
	$pageNumber = $attributes['pagenum'];
}
settype($pageNumber, "integer");

function showTheme( $id, $name )
{
	global $server_root;
	global $pageNumber;
	printf('<p><img src="http://%s/img/theme_packs/%s/thumb.png" width="50" height="40" vspace="2" hspace="3" style="float:left"><p>', $_SERVER['HTTP_HOST'], $id );
	printf('<p><a href="%s/midlet/member/view_theme.php?pagetype=1&id=%s&pagenum=%d&awtrack=theme_changed_%s">%s</a></p>', $server_root, $id, $pageNumber, $id, $name );
}
?>

<html>
	<head>
		<title>Themes</title>
	</head>

	<?php
		if( isMidletVersion4() )
			showCommonStyle();
	?>

	<body>
		<?php
			$errorSuccess = new ErrorSuccess();
			try
			{
				switch( $pagetype )
				{
					case 1:
						soap_call_ejb("changeTheme", array($username, $id) );
						$errorSuccess->success = "Theme has been successfully changed.";
						get_ice_dao()->update_theme_metric();
						break;
				}
			}
			catch(Exception $ex)
			{
				$errorSuccess->error = "Failed to modify theme.";
			}

			$errorSuccess->show();

			printf("<p><b>Select a theme to apply:</b></p><br>");
			$content = soap_call_ejb("getThemes", array($username, $pageNumber, 3));
			$totalResults = $content['totalResults'];
			settype($totalResults, "integer");
			showTheme(0, "Default");
			if( $totalResults > 0 )
			{
				$themes = $content['themes'];
				for( $i = 0; $i < sizeof($themes); $i++ )
				{
					showTheme( $themes[$i]['id'], $themes[$i]['name'] );
				}
				showNavigation($server_root . "/midlet/member/view_theme.php?pagetype=0", $pageNumber, $content["totalPages"], false);
			}
		?>
	</body>
</html>
