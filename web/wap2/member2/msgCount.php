	function checkForMsgs($user, $acid) {

		global $wapDestUrl;
		static $msgCounts = array();

		// the buddysms and buddy call pages set this environment variable to indicate no msg info is to be displayed
		// $user used to be used - now cid is used

		if (getenv("showMsgs") == "false") {
			return("");
		}

		// if user is not blank then return msg count for that user/(cid)
		// if user is blank, then return a list with links to all users who have unread chat msgs to this user

		// get the current session index to see if there is something stored

		debugFile("checkForMsgs: user is '$user' - blank means check for all users acid is $acid");

		// get all the session keys

		$keyList = array_keys($_SESSION);
		$count = count($keyList);

		// go through all the keys looking for messages

		$list = array();		// stores the results

		$sourceCount = 0;		// stores how many results for the selected $user/cid, if supplied

		debugFile("checkForMsgs: count of session keys is: $count");

		for ($j = 0; $j < $count; $j++) {

			$key = $keyList[$j];						// get the key - for readability

			//debugFile("checkForMsgs: key is $key");
			//debugFile("checkForMsgs: value is " . $_SESSION[$key]);

			if (strstr($key, "~source~")) {					// source key found

				//skip this if it is of type 3 (dest = public chat room)

				$testKey = getField($key, 0) . "~dstType~" . getField($key, 2);

				debugFile("checkForMsgs: testKey is $testKey");

				if ($_SESSION[$testKey] == 3) {
					debugFile("checkForMsgs: skipping since target is public chatroom");
					continue;
				} else {
					debugFile("checkForMsgs: NOT skipping since target is " . $_SESSION[$testKey]);
				}

				debugFile("checkForMsgs: found source msg key: $key");

				$sourceUser = $_SESSION[$key];

				// since the target is a 1:1 chat (need to enforce this) see if it is who they are currently chatting with

				$cid = $_SESSION['currentChatBuddy'];			// cid of current chat buddy

				debugFile("checkForMsgs: cid of current chat buddy is: $cid");

				//$testCid = $_SESSION['name2cid' . $sourceUser];		// get the cid of the sending user

				// get the cid from the incoming msg packet

				$testCidKey = getField($key, 0) . "~cid~" . getField($key, 2);
				$testCid = $_SESSION[$testCidKey];

				debugFile("checkForMsgs: cid of the sending buddy is: $testCid");

				if (strlen($testCid)) {
					if ($testCid == $cid) {					// the cid is who they are currently chatting with so skip
						debugFile("checkForMsgs: skipping msg from sourceUser ($sourceUser) cid ($testCid)  because it matches current chatting user: " . $_SESSION['currentChatBuddy'] . " cid ($cid) ");
						continue;
					}
				}


				// if the testCid has no length, then the msg is from a private chat initiated to a user not on the contact list

				debugFile("checkForMsgs: sourceUser is now: $sourceUser");

				//if (strlen($user)) {					// only get msg count for a specific user
				if (strlen($acid)) {					// only get msg count for a specific user
					//if ($cid == $sourceUser) {
					if ($acid == $testCid) {
						$sourceCount++;
					}
				} else {						// track msg count for all users
					$msgCounts[$sourceUser] = $msgCounts[$sourceUser] + 1;
				}

				array_push($list, $sourceUser);
			}
		}

		// if just getting user count - return the result

		//if (strlen($user)) {
		if (strlen($acid)) {

			debugFile("checkForMsgs: returning sourcecount $sourceCount");
			return($sourceCount);
		}

		$res = "";

		debugFile("checkForMsgs: list before unique is: " . print_r($list, TRUE));

		$list = array_merge(array_unique($list));
		$c = count($list);

		if ($c == 0) {
			return("");
		}

		debugFile("checkForMsgs: count is $c");
		debugFile("checkForMsgs: list is " . print_r($list, TRUE));
		debugFile("checkForMsgs: msgCounts is " . print_r($msgCounts, TRUE));

		$urlString = "";

		// make direct chat url if only 1 user

		//debugFile("checkForMsgs: SESSION is " . print_r($_SESSION, TRUE));

		// at this point the source user is the fusion username if the msg was from a fusion
		// but it is  the display name (e.g. msn email address) if from external IM

		if ($c == 1) {			// only 1 user has sent msgs

			$bu = $list[0];
			$c = $msgCounts[$bu];	// get number of msgs from that user

			debugFile("checkForMsgs: count of messages for user $bu is $c");

			if (getenv("homepage") != "true") {
				if ($c > 1) {				// more than 1 msg from a single user
					$res = "<small>New Msgs:</small><br/>";		// non home page has New Msg: (more than 1- single user)
				} else {
					$res = "<small>New Msg:</small><br/>";		// non home page has New Msg: - just 1 from 1 user
				}
			} else {
				$res = "";				// home page just has msg count and username for single user
			}

			$cid = getContactID($bu);
			if (!strlen($cid)) {
				// getContactID needs the display name - if that fails - try the fusion user to get the cid
				$cid = $_SESSION["name2cid" . $bu];
			}
			if (!strlen($cid)) {
				debugFile("checkForMsgs: CID HAS NO LENGTH! - bu is $bu");
			}
			//$cid = $_SESSION["name2cid" . $bu];
			//$dn = $_SESSION["name2disp" . $bu];
			$dn = $bu;
			$res = $res . "<a href=\"$wapDestUrl" . "?cmd=buddychat&amp;buddyuser=$bu&amp;cid=$cid\"><small>$dn</small></a><small> ($c)</small>";
			return($res);
		}

		// msgs from more than 1 user

		// now - the link must change based on homepage or not

		if (getenv("homepage") == "true") {

			$res = "<small>New Msgs:<br /></small>";

			foreach ($list as $bu) {
				$c = $msgCounts[$bu];
				debugFile("checkForMsgs: j is $j; buddy user is $bu; count is $c");
				$cid = getContactID($bu);
				if (!strlen($cid)) {
					// getContactID needs the display name - if that fails - try the fusion user to get the cid
					$cid = $_SESSION["name2cid" . $bu];
				}
				//$cid = $_SESSION["name2cid" . $bu];
				//$dn = $_SESSION["name2disp" . $bu];
				//$dn = $_SESSION["cid2disp" . $cid];
				$dn = $bu;
				$res = $res . "<a href=\"$wapDestUrl" . "?cmd=buddychat&amp;buddyuser=$bu&amp;cid=$cid\"><small>$dn</small></a><small> ($c)</small><br />";
			}

		} else {				// not home page - msgs from more than 1 user

			$urlString = "";
			$j = 0;
			foreach ($list as $bu) {
	   			$urlString = $urlString . "user" . $j . "=" . $bu . "&amp;";
				$j++;
			}

                	$res = "<a href=\"$wapDestUrl" . "?cmd=msgDirect&amp;$urlString\"><small>New msgs</small></a>";
		}

		return($res);
	}
