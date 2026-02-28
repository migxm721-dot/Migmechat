<?php
// Function to display the list of states/provinces where migme credit resellers
// are located in the given country.
// $countryID: The ID of the country in which the resellers we'll display are in
// $clientType: Where this is being displayed. May be "AJAX", "WAP" or "Midlet"
// $loggedIn: Whether this page is being displayed to public users or members logged in
function showResellerStates($countryID, $clientType, $loggedIn) {
	settype($countryID, "int");

	try {
		$states = soap_call_ejb('getResellerStates', array($countryID));
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return;
	}

	if (empty($states[0])) {
		print '<p>Sorry, there are no resellers in your country yet.</p>';
		return;
	}

	if ($countryID == 204) {  // If the country is South Africa
		if ($clientType == "WAP"){
			print '<p><small>Buy a migme prepaid card in stores nationwide in South Africa.<br/>We\'re adding new store locations everyday so email call@mig33.com if you do not see a store listed near you.</small></p>';
		} else {
			print '<p>Buy a migme prepaid card in stores nationwide in South Africa.</p><p>We\'re adding new store locations everyday so email call@mig33.com if you do not see a store listed near you.</p>';
		}
	}
	else {
		if ($clientType == "WAP"){
			print '<p><small>Find a store in your state</small></p>';
		} else {
			print '<p>Find a store in your state</p>';
		}
	}

	$lineBreak = '<br/>';
	if ($clientType == 'Midlet')
		$lineBreak = '<br>';

	for ($i = 0; $i < sizeof($states); $i++) {
		if ($clientType == 'Midlet'){
			print '<a href="'.$server_root.'/midlet/member/buy_prepaid_card.php?state=' . urlencode($states[$i]) . '">' . $states[$i] . '</a>' . $lineBreak;
		}else if ($clientType == "WAP"){
			print '<small><a href="?state=' . urlencode($states[$i]) . '">' . $states[$i] . '</a></small>' . $lineBreak;
		} else {
			print '<a href="?state=' . urlencode($states[$i]) . '">' . $states[$i] . '</a>' . $lineBreak;
		}
	}
}

// Function to display migme credit resellers in the given country and state.
// $countryID: The ID of the country in which the resellers we'll display are in
// $clientType: Where this is being displayed. May be "AJAX", "WAP" or "Midlet"
// $state: The state in which the sellers we'll display are in
// $loggedIn: Whether this page is being displayed to public users or members logged in
function showResellersInState($countryID, $clientType, $state, $loggedIn) {
	settype($countryID, "int");

	try {
		$resellers = soap_call_ejb('getResellersInState', array($countryID, $state));
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return;
	}

	if (empty($resellers[0]['id'])) {
		print '<p>Sorry, there are no resellers in your country yet.</p>';
		return;
	}

	if ($countryID == 204) {  // If the country is South Africa
		if ($clientType == "WAP"){
			print '<p><small>We\'re adding new store locations everyday so email call@mig33.com if you do not see a store listed near you.</small></p>';
			print '<small><b>Resellers in ' . $state . ' Province</b></small><br/><br/>';
		} else {
			print '<p>We\'re adding new store locations everyday so email call@mig33.com if you do not see a store listed near you.</p>';
			print '<p><b>Resellers in ' . $state . ' Province</b></p>';
		}
	}

	$prevCity = '';
	$lineBreak = '<br/>';
	if ($clientType == 'Midlet')
		$lineBreak = '<br>';

	for ($i = 0; $i < sizeof($resellers); $i++) {
		if ($prevCity != $resellers[$i]['city']) {
			if ($prevCity != ''){
				if ($clientType == "WAP"){
					print '</small></p>';
				} else {
					print '</p>';
				}
			}

			if ($clientType == "WAP"){
				print '<p><small><b>' . htmlspecialchars($resellers[$i]['city']) . '</b>' . $lineBreak;
			} else {
				print '<p><b>' . htmlspecialchars($resellers[$i]['city']) . '</b>' . $lineBreak;
			}
			$prevCity = $resellers[$i]['city'];
		}
		else {
			print $lineBreak;
		}

		print htmlspecialchars($resellers[$i]['name']) . $lineBreak;

		if ($resellers[$i]['address'] != '') {
			print htmlspecialchars($resellers[$i]['address']) . $lineBreak;
		}

		if ($resellers[$i]['phonenumbertodisplay'] != '') {
			if (!$loggedIn || $clientType == "Midlet") {
				print $resellers[$i]['phonenumbertodisplay'] . $lineBreak;
			}
			else {
				// Create the "Call" link
				if ($clientType == "AJAX")
					$link = 'javascript:window.parent.showCallback(' . $resellers[$i]['phonenumber'] . ')';
				elseif ($clientType == "WAP")
					$link = '../member2/t.php?cmd=makecall&amp;destination=' . $resellers[$i]['phonenumber'];

				print '<a href="' . $link . '">' . $resellers[$i]['phonenumbertodisplay'] . '</a>' . $lineBreak;
			}
		}

		if ($resellers[$i]['phonenumber2todisplay'] != '') {
			if (!$loggedIn || $clientType == "Midlet") {
				print $resellers[$i]['phonenumber2todisplay'] . $lineBreak;
			}
			else {
				// Create the "Call" link
				if ($clientType == "AJAX")
					$link = 'javascript:window.parent.showCallback(' . $resellers[$i]['phonenumber2'] . ')';
				elseif ($clientType == "WAP")
					$link = '../member2/t.php?cmd=makecall&amp;destination=' . $resellers[$i]['phonenumber2'];

				print 'or <a href="' . $link . '">' . $resellers[$i]['phonenumber2todisplay'] . '</a>' . $lineBreak;
			}
		}
	}

	if ($clientType == "WAP"){
		print '</small></p>';
	} else {
		print '</p>';
	}
}

// Function to display migme credit resellers in the given country.
// $countryID: The ID of the country in which the resellers we'll display are in
// $clientType: Where this is being displayed. May be "AJAX", "WAP" or "Midlet"
function showResellers($countryID, $clientType) {
	settype($countryID, "int");

	try {
		$resellers = soap_call_ejb('getResellers', array($countryID));
	} catch(Exception $e) {
		print 'Sorry, an error occurred. ' . $e->getMessage();
		return;
	}

	if (empty($resellers[0]['id'])) {
		print '<p>Sorry, there are no resellers in your country yet.</p>';
		return;
	}

	$prevCity = '';
	$lineBreak = '<br/>';
	if ($clientType == 'Midlet')
		$lineBreak = '<br>';

	for ($i = 0; $i < sizeof($resellers); $i++) {
		if ($prevCity != $resellers[$i]['city']) {
			if ($prevCity != '')
				print '</p>';
			print '<p><b>' . $resellers[$i]['city'] . '</b>' . $lineBreak;
			$prevCity = $resellers[$i]['city'];
		}

		print $resellers[$i]['name'] . $lineBreak;

		if ($resellers[$i]['phonenumbertodisplay'] != '') {
			if ($clientType == "Midlet") {
				print $resellers[$i]['phonenumbertodisplay'] . $lineBreak;
			}
			else {
				// Create the "Call" link
				if ($clientType == "AJAX")
					$link = 'javascript:window.parent.showCallback(' . $resellers[$i]['phonenumber'] . ')';
				elseif ($clientType == "WAP")
					$link = '../member2/t.php?cmd=makecall&amp;destination=' . $resellers[$i]['phonenumber'];

				print '<a href="' . $link . '">' . $resellers[$i]['phonenumbertodisplay'] . '</a>' . $lineBreak;
			}
		}

		if ($resellers[$i]['phonenumber2todisplay'] != '') {
			if ($clientType == "Midlet") {
				print $resellers[$i]['phonenumber2todisplay'] . $lineBreak;
			}
			else {
				// Create the "Call" link
				if ($clientType == "AJAX")
					$link = 'javascript:window.parent.showCallback(' . $resellers[$i]['phonenumber2'] . ')';
				elseif ($clientType == "WAP")
					$link = '../member2/t.php?cmd=makecall&amp;destination=' . $resellers[$i]['phonenumber2'];

				print 'or <a href="' . $link . '">' . $resellers[$i]['phonenumber2todisplay'] . '</a>' . $lineBreak;
			}
		}
	}
	print '</p>';
}
?>