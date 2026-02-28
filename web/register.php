<?php
$campaign_query = '';
$campaign_name = htmlentities(strip_tags($_GET['cn']));
if(!empty($campaign_name))
	$campaign_query = "&cn=$campaign_name";

header('Location: '.$server_root.'/sites/index.php?c=registration&a=register&v=corporate'.$campaign_query);
exit();
?>