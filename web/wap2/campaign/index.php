<?php
$campaign_query = '';
$campaign_name = htmlentities(strip_tags($_GET['cn']));
if(!empty($campaign_name))
	$campaign_query = "&cn=$campaign_name";

header('Location: http://www.mig33.com/sites/index.php?c=wap_portal&a=login&v=wap'.$campaign_query);
exit();
?>