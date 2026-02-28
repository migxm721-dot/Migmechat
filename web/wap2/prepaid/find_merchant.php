<?php
include_once("../../common/common-inc.php");
include_once("../wap_includes/wap_functions.php");
include_once("../../includes/find_merchant.php");
emitHeader("Find a Local Store / Merchant");
?>
	<div id="content">
		<div class="section">
<?php
// Try to detect the user's country
try {
	$countryData = get_country_from_ip(getRemoteIPAddress());
} catch(Exception $e) {}

$countryID = 0;
if (strlen($countryData['id']) > 0)
	$countryID = $countryData['id'];

showMerchantLocator($countryID, "WAP", false);
?>
		</div>
<?php emitFooter(); ?>
	</div>
<?php emitFooter_end(); ?>