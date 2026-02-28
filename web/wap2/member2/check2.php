<?php
	function checkForMsgs($user, $acid) {

		global $wapDestUrl;
		global $prog;
		static $doneAlready = 0;
		static $msgCounts = array();

		//debugFile("checkForMsgs: SESSION is " . print_r($_SESSION, TRUE));

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

		$doneAlready = 1;

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

				if (strlen($testCid) > 0) {
					if ($testCid == $cid) {					// the cid is who they are currently chatting with so skip
						debugFile("checkForMsgs: skipping msg from sourceUser ($sourceUser) cid ($testCid)  because it matches current chatting user: " . $_SESSION['currentChatBuddy'] . " cid ($cid) ");
						continue;
					}
				} else {							// if there is no cid, then check for the buddy name
					$testBnKey = getField($key, 0) . "~source~" . getField($key, 2);
					$testBn = $_SESSION[$testBnKey];
					if (strlen($testBn) > 0) {
						$currentlyChattingName = $_SESSION['currentChatBuddyName'];			// name of current chat buddy
						if ($testBn == $currentlyChattingName) {
							continue;
						}
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
					$res = "<small>New Msgs:<br /></small>";		// non home page has New Msg: (more than 1- single user)
				} else {
					$res = "<small>New Msg:<br /></small>";		// non home page has New Msg: - just 1 from 1 user
				}
			} else {
				$res = "";				// home page just has msg count and username for single user
			}

			//  instead of using getContactID  - get the contact ID from the msg array

			$cid = getCidFromMsgArray($bu);

			//$cid = getContactID($bu);


			debugFile("checkForMsgs: result is $cid");
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

			if (strlen($cid) > 0) {
				$dispBu = str_replace(" ", "+", $_SESSION['cid2disp' . $cid]);
			} else {		// if there is no cid this user came from a chatroom of external im not on contact list
				$dispBu = $bu;
			}

			//$res = $res . "<a href=\"$prog" . "?cmd=buddychat&amp;buddyuser=$bu&amp;cid=$cid\"><small>$dn</small></a><small> ($c)</small>";

			$dispBuVis = str_replace("+", " ", $dispBu);
			$res = $res . "<a href=\"$prog" . "?cmd=buddychat&amp;buddyuser=$dispBu&amp;cid=$cid\"><small>$dispBuVis</small></a><small> ($c)</small>";
			return($res);
		}

		// msgs from more than 1 user

		// now - the link must change based on homepage or not

		if (getenv("homepage") == "true") {

			$res = "<small>New Msgs:<br /></small>";

			foreach ($list as $bu) {
				$c = $msgCounts[$bu];
				debugFile("checkForMsgs: j is $j; buddy user is $bu; count is $c");

				//$cid = getContactID($bu);
				$cid = getCidFromMsgArray($bu);

				if (!strlen($cid)) {
					// getContactID needs the display name - if that fails - try the fusion user to get the cid
					$cid = $_SESSION["name2cid" . $bu];
				}
				//$cid = $_SESSION["name2cid" . $bu];
				//$dn = $_SESSION["name2disp" . $bu];
				//$dn = $_SESSION["cid2disp" . $cid];
				$dn = $bu;

				if (strlen($cid) > 0) {
					$dispBu = str_replace(" ", "+", $_SESSION['cid2disp' . $cid]);
				} else {
					$dispBu = $bu;
				}

				$res = $res . "<a href=\"$prog" . "?cmd=buddychat&amp;buddyuser=$dispBu&amp;cid=$cid\"><small>$dispBu</small></a><small> ($c)</small><br />";

				//$res = $res . "<a href=\"$prog" . "?cmd=buddychat&amp;buddyuser=$bu&amp;cid=$cid\"><small>$dn</small></a><small> ($c)</small><br />";
			}

		} else {				// not home page - msgs from more than 1 user

			$urlString = "";
			$j = 0;
			foreach ($list as $bu) {
	   			$urlString = $urlString . "user" . $j . "=" . $bu . "&amp;";
				$j++;
			}

                	$res = "<a href=\"$prog" . "?cmd=msgDirect&amp;$urlString\"><small>New msgs</small></a>";
		}

		return($res);
	}

	function debugFile($msg) {

		global $debugOnly, $DEBUGFILE, $debugOn, $debugIgnore;

		if (!$debugOn) return;

		if (strlen($debugOnly)) {
			sscanf($msg, "%s", $firstToken);
			if (strstr($debugOnly, $firstToken)) {
				$doit = 1;
			} else {
				$doit = 0;
			}
		}  else if (strlen($debugIgnore)) {
			if (strstr($msg, $debugIgnore)) {
				return;
			}
			$doit = 1;
		} else {
			$doit = 1;
		}

		if ($doit) {
			$user = getuser();
	                $dfile = $DEBUGFILE;
	                $f = fopen($dfile, "a");
	                fwrite($f, "\n" . date("r : ") . " ($user) " . $msg . "\n");
	                fclose($f);
		}
	}

	function getField($text, $fno) {

		$list = explode("~", $text);
		return($list[$fno]);

	}

	function getCidFromMsgArray($bu) {

		$keys = array_keys($_SESSION);
		foreach ($keys as $key) {
			if (strstr($key, $bu . "~cid~")) {
				$cid = $_SESSION[$key];
				return($cid);
			}
		}

		debugFile("getCidFromMsgArray: could not find cid for buddy $bu");
		return("");
	}

	function checkServerSessionStatus() {
		global $globalResponse, $globalLoginArray, $globalParserIndex;

		debugFile("checkServerSessionStatus: starting");

		$args = array();
		$args[0] = "";

		$result = doSend("", getsid());

		if ($result == "") {
			badResponse(301);
			exit;
		}

		parseResponsePacket($result);
		if (gotResponse(0) != -1) {
			badResponse(301);
			exit;
		}

		// now - any other asynch messages that came in must be processed or put in the relevant queue
		processAsyncMessages("");

		debugFile("checkServerSessionStatus: returning...");
		return;
	}

	function doPacket($cmd, $args, $expectedResponse, $expectedResponseField) {

		// do a simple command and get the response

		global $globalResponse;

		$fp = new fusionPacket();
		$fp->type = $cmd;
		$fp->sessionId = getsid();

		$argCount = count($args);
		for ($j = 1; $j <= $argCount; $j++) {
			$fp->fusionPacketAddField($j, $args[$j]);
		}

		$debugArgs = print_r($args, TRUE);
		debugFile("doPacket: cmd is $cmd, argCount: $argCount, args: $debugArgs, expected: $expectedResponse");

		$xmlResponse = $fp->fusionSendPacket();

		parseResponsePacket($xmlResponse);

		debugFile("doPacket: xml response: $xmlResponse");

		$index = gotResponse($expectedResponse);

		if (getenv("ignoreError") == "1" ) return;		// ignore error - used for debugging and testing

		if ($index < 0)	{			// did not get a valid response

			$e = $globalResponse[0]['PacketType'];
			badResponse($e);
			exit;
		}

		debugFile("doPacket: value of field is " . $globalResponse[$index][$expectedResponseField]);

		putenv("grIndex=$index");		// needed late in the game by some functions to know what index the response is in

		return($globalResponse[$index][$expectedResponseField]);
	}

	class fusionPacket {

		var $type = 0;
		var $fields = array(10);
		var $maxFieldNum = 0;
		var $sessionId = 0;

		function fusionPacketAddField($number, $value) {
			$this->fields[$number] = "$value";
			if ($this->maxFieldNum < $number) {
				$this->maxFieldNum = $number;
			}
			return;
		}

		function fusionPacketGetFieldValue($number) {
	                return($this->fields[$number]);
	        }


	        function fusionPacketToXML() {


			$xml = '<P T="' . $this->type . '">';


			for ($i = 1; $i <= $this->maxFieldNum; $i++) {

				debugFile("fusionPacketToXML: incoming field is: " . $this->fields[$i]);

				if ($this->fields[$i] != null) {


					$value = '' . $this->fields[$i];
					$value = str_replace("&", "&amp;", $value);
					$value = str_replace("<", "&lt;", $value);
					$value = str_replace(">", "&gt;", $value);
					$value = str_replace("\"", "&quot;", $value);
					$value = str_replace("'", "&apos;", $value);

					$xml = $xml . '<F N="' . $i . '">' . $value . '</F>';
				}
			}

			$xml = $xml . '</P>';
			return $xml;

	        }


	        function fusionSendPacket() {

			$xml = $this->fusionPacketToXML();
			$r = doSend($xml, $this->sessionId);
			return($r);
	        }
	}

	function getsid() {		// return session id

		global $globalSessionId;		// used to facilitate debugging without a real wap session

		debugFile("getsid(): starting...");

		$cook = $_SESSION['mig33sid'];

		debugFile("getsid(): cook is $cook");

		if ( !strlen($cook) ) {

			if (strlen($globalSessionId)) {
				return($globalSessionId);
			} else {
				return("");
			}
		}

		else return($cook);
	}

	function doSend($xml, $sessionId) {

		global $destUri;

		debugFile("doSend: xml is $xml, sessionId is $sessionId");

		if ($sessionId != "") {
                        $sid = "$sessionId";
                } else {
			if (strlen(getsid()) > 0) {
				$sid = getsid();
			} else {
	                        $sid = '';
			}
                }

		$header = array("Content-Type: text/xml");

		$curlHand = curl_init("$destUri/$sid");
		curl_setopt($curlHand, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($curlHand, CURLOPT_HTTPHEADER, $header);
		curl_setopt($curlHand, CURLOPT_POSTFIELDS, $xml);
		$res = curl_exec($curlHand);
		debugFile("doSend: curl desturi/sid is $destUri/$sid");
		debugFile("doSend: res from curl is $res");
		curl_close($curlHand);

		return($res);
	}

	function parseResponsePacket($xmlPacket) {

		global $globalParserIndex, $globalResponse;

		$globalResponse = array();
		$globalParserIndex = -1;

		$parser = xml_parser_create();
		xml_set_element_handler($parser, "xml_start", "xml_stop");		// event handler
		xml_set_character_data_handler($parser, "xml_char");			// char data handler

		xml_parse($parser, $xmlPacket, TRUE);
		xml_parser_free($parser);					// free parser

	}

	function gotResponse($code) {

		global $globalResponse, $globalLoginArray, $globalParserIndex;

		// check to see if the most recent response had the required code

		$gotCode = 0;

		for ($j = 0; $j <= $globalParserIndex; $j++ ) {

			debugFile("gotResponse: got packet type: " . $globalResponse[$j]['PacketType']);

			if ( $globalResponse[$j]['PacketType'] == $code ) {

				return($j);			// return the index of the array that had the first occurence
			}
		}

		// didn't find code

		return(-1);
	}

	function badResponse($e) {

		global $globalResponse;

		if ( $e == 301 ) {		// session terminated

			loginMenu("Session timed out - please login again.");
			exit;
		}

		if ( $e == 0) {			// error code returned

			if (strstr($globalResponse[0][2], "Internal server error")) {
				loginMenu("Internal error - please login again.");
			} else {
				debugFile("badResponse: calling errorMenu  - e is $e, globalResponse is: " . print_r($globalResponse, TRUE));
				errorMenu($globalResponse[0][2]);
			}
			exit;
		}
	}

	function errorMenu($msg) {

		debugFile("errorMenu: msg is $msg");

		emitHeader();
		emitTitle("Error!");

		$res = "<p>" . makeLink("Home", "home") . "<br />";
		$res = $res . "$msg </p>";
		emitBody($res);
		emitClose();
		exit;
	}

	function xml_start($parser, $element_name, $element_attrs) {

		global $globalCurrentField, $globalResponse, $globalParserIndex;

		switch($element_name) {

			case "A":
				//echo "got A element\n";
				//print_r($element_attrs);

				//ignore A element for now
				break;

			case "P":
				$globalParserIndex++;

				//debugFile("xml_start: got P element");
				//print_r($element_attrs);

				// get the T attribute for the P element
				$globalResponse[$globalParserIndex]['PacketType'] = $element_attrs['T'];
				debugFile("xml_start: setting globalResponse sub $globalParserIndex to " . $element_attrs['T']);
				break;

			case "F":
				//echo "got F element\n";
				//print_r($element_attrs);

				// get the N attribute for the F element
				$globalCurrentField = $element_attrs['N'];
				break;
		}
	}

	function xml_stop($parser, $element_name) {

		return;
  	}

	function xml_char($parser, $data) {		// character data

		global $globalResponse, $globalCurrentField, $globalParserIndex;

		//debugFile("xml_char: data is: $data for field $globalCurrentField");

		$data = str_replace("<", "&lt;", $data);
		$data = str_replace(">", "&gt;", $data);

		$globalResponse[$globalParserIndex][$globalCurrentField] = $globalResponse[$globalParserIndex][$globalCurrentField] . $data;
		//$globalResponse[$globalParserIndex][$globalCurrentField] = $data;

		return;
	}

	function loginMenu($msg) {

		//$msg is required but can be blank - is possible error message to display before form.

		global $wapDestUrl;

		putenv("loginMenu=true");			// this sets a flag so that showBody doesn't emit the msgs waiting indicator

		debugFile("loginMenu: starting...");

		emitHeader();
		emitTitle("Sign in to mig33");

		//$priorUsername = $_COOKIE['mig33usn'];
		//$priorUsername = $_SESSION['mig33usn'];

		if (strlen($_COOKIE['rememberedUsername'])) {
			$priorUsername = $_COOKIE['rememberedUsername'];
		} else {
			$priorUsername = '';
		}

		if (strlen($msg)) {
			$myMsg = "<p>$msg</p>";
		} else {
			$myMsg = '';
		}

		$automsn = "";
		$autoyahoo = "";
		$autoaim = "";

		if ($_COOKIE['msnautologincookie'] == "on") $automsn = "checked=\"checked\"";
		if ($_COOKIE['yahooautologincookie'] == "on") $autoyahoo = "checked=\"checked\"";
		if ($_COOKIE['aimautologincookie'] == "on") $autoaim = "checked=\"checked\"";

		$body = "
$myMsg
<form method=\"post\" action=\"$wapDestUrl\">

	<p>
		<small><strong>Username</strong></small><br />
		<small><input type=\"text\" name=\"username\" value=\"$priorUsername\" /></small><br />
		<small><strong>Password</strong></small><br />
		<small><input type=\"password\" name=\"password\"/></small><br />
		<small><input type=\"checkbox\" name=\"rememberName\" value=\"on\" checked=\"checked\" /></small>
		<small>Remember me</small><br />
		<small><input type=\"checkbox\" name=\"invisible\" value=\"on\" /></small>
		<small>Sign in as invisible</small><br />
		<small>Connect to:</small><br />
		<small><input type=\"checkbox\" name=\"msnautologin\"  $automsn /></small><small>MSN</small><br />
		<small><input type=\"checkbox\" name=\"yahooautologin\" $autoyahoo /></small><small>Yahoo!</small><br />
		<small><input type=\"checkbox\" name=\"aimautologin\" $autoaim /></small><small>AIM</small><br />
	</p>

	<p>
		<input type=\"submit\" value=\"Sign in\"/>
		<input type=\"hidden\" name=\"cmd\" value=\"login\"/>
	</p>


</form>\n";

		//debugFile("body from loginMenu is " . $body);

		emitBody($body);
		emitClose();
		return;
	}

	function emitBody($body) {

		global $IMGURL;

		// do not check for msgs if this is called from loginMenu

		if (getenv("loginMenu") != "true") {
			$msgs = checkForMsgs("", "");
			$pends = outstandingContactReqs();
			debugFile("emitBody: pends is $pends");
		} else {						// ignore the login menu - obviously
			$msgs = "";
			$pends = "";
		}

		debugFile("emitBody: msgs is $msgs");
		debugFile("emitBody: pends is $msgs");

		// if not on the home page put a comma between new msgs and invites

		if (getenv("homepage") != "true") {
			if (strlen($msgs) && strlen($pends)) {
				$comma = ",";
			} else {
				$comma = "";
			}
		} else {
			if (strlen($msgs)) {
				$comma = "<br />";		// on homepage makes sure extra blank line after list of new msgs users
			} else {
				$comma = "";
			}
		}

		//$out =  "<body>\n";
		//$out = $out . "<p><img src=\"$IMGURL/logo_tiny.gif\" alt=\"logo\" />$msgs$comma $pends</p>";
		//$out = $out . "<p><img src=\"http://m.mig33.com/img/logo_mobile_16.gif\" alt=\"logo\" />$msgs$comma $pends</p>";
		$out = $out . "$body";
		$out = $out . "</body>\n";
		echo $out;
		//echo "<!-- " . $_SESSION['mig33sid'] . " -->\n";
		return;
	}

	function emitClose() {
		echo "</html>\n";
		return;
	}
?>
