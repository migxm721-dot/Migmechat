<?php
require_once realpath(dirname(__FILE__).'/..').'/common/utilities.php';
if (ClientInfo::is_j2me_client() || ClientInfo::is_mobile()): 
?>
<?=_('Sorry, the page you requested could not be found.');?>
<?php 
else:
	include(realpath(dirname(__FILE__).'/../..').'/404_web.php');
endif;
?>