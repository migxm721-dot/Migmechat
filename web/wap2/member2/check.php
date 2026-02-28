<?php
	include_once("wapglobals.php");

	function checkForMsgs($user, $acid) {

		global $wapDestUrl;
		global $prog;
		static $doneAlready = 0;
		static $msgCounts = array();

		//$prog = getprog();
		debugFile("checkForMsgs: prog is $prog");
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
							debugFile("checkForMsgs: testBn $testBn matches currentlyChattingName ($currentlyChattingName) so contining");
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

			debugFile("checkForMsgs: msgs from just 1 user");

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


			debugFile("checkForMsgs: result cid is $cid");
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
				debugFile("checkForMsgs: cid has length...");
				$dispBu = str_replace(" ", "+", $_SESSION['cid2disp' . $cid]);
			} else {		// if there is no cid this user came from a chatroom of external im not on contact list
				debugFile("checkForMsgs: cid has no length...");
				$dispBu = $bu;
			}

			//$res = $res . "<a href=\"$prog" . "?cmd=buddychat&amp;buddyuser=$bu&amp;cid=$cid\"><small>$dn</small></a><small> ($c)</small>";

			$dispBuVis = str_replace("+", " ", $dispBu);
			$res = $res . "<a href=\"$prog" . "?cmd=buddychat&amp;buddyuser=$dispBu&amp;cid=$cid\"><small>$dispBuVis</small></a><small> ($c)</small>";

			debugFile("checkForMsgs: res is now: $res");
			return($res);
		}

		// msgs from more than 1 user

		// now - the link must change based on homepage or not

		debugFile("checkForMsgs: msgs from more than 1 user");

		if (getenv("homepage") == "true") {

			$res = "<small>New Msgs:<br /></small>";

			debugFile("checkForMsgs: homepage true - doing foreach");
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

				debugFile("checkForMsgs: res is now $res");

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
		global $wapDestUrl;

		// handle errors from merchant center v2 separately
		if (strstr($wapDestUrl, "merchant_v2")) {
			$eMsg = ($e == 301 ? 'Session timed out - please login again.' : 'Your session may have timed out - please login again.');
			loginMenu($eMsg);
			exit;
		} else {

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
	}

	function errorMenu($msg) {

		debugFile("errorMenu: msg is $msg");

		emitHeaderLocal();
		emitTitle("Error!");

		$res = "<small>" . makeLink("Home", "") . "</small><br/><br/>";
		$res = $res."<small>$msg </small>";
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

	function emitTitle($title) {

		echo "<head><title>mig33 - $title</title></head>\n";
		echo "<body>";
		echo '<div style="width: 100%; background-color: #e6e6e6; padding:2px; font-size:1px;"><img src="http://www.mig33.com/img/logo_mobile.png" alt="mig33" /><font style="font-size:10px;">&nbsp;<b>'.$title.'</b></font></div>';
		//echo "<p><img src=\"/img/logo_mobile_16.gif\" /></p>";
	        return;
	}

	function processAsyncMessages($specificItem) {

		// if specificItem is supplied - only use that index for globalParser - otherwise do all in globalResponse

		global $globalResponse, $globalLoginArray, $globalParserIndex;

		// this function will go through the response list and see if any relevant things need to be handled - some are stored away for use by other functions

		debugFile("processAsyncMessages: specificItem is $specificItem, len " . strlen($specificItem));

		if (strlen($specificItem) > 0) {
			$startPoint = $specificItem;
			$endPoint = $specificItem;
		} else {
			$startPoint = 0;
			$endPoint = $globalParserIndex;
		}

		debugFile("processAsyncMessages: startPoint is $startPoint, endPoint is $endPoint");

		debugFile("processAsyncMessages: globalResponse is: " . print_r($globalResponse, TRUE));

		for ($j = $startPoint; $j <= $endPoint; $j++ ) {

			debugFile("processAsyncMessages:  j is $j; got packet type: " . $globalResponse[$j]['PacketType']);

			$pt = $globalResponse[$j]['PacketType'];

			if ( $pt == 301 ) {				// session timed out
				badResponse($pt);
				exit;
			}

			if ($pt == 5) {					// alert
				processAlert($globalResponse[$j][2]);
				continue;
			}

			if ($pt == 207) {				// IM Session Status [change]
				imSessionStatus($j);
				continue;
			}

			if ($pt == 401) {				// Group - group altered
				groupAltered();
				continue;
			}

			if ($pt == 402) {
				contactAltered($j);			// Contact - contact altered
				continue;
			}

			if ($pt == 404) {
				presenceAltered($j);			// Presence - presence altered [*]
				continue;
			}

			if ($pt == 412) {
				debugFile("processAsyncMessage: got 412 contact added request");
				contactAdded($j);			// Another user added this user as a contact
				continue;
			}

			if ($pt == 418) {
				voiceChanged($j);				// Voice capability changed
				continue;
			}

			if ($pt == 500) {

				debugFile("processAsyncMessages: got 500, calling msgReceived()");

				msgReceived($j);			// Message Received [*]
				continue;
			}

			if ($pt == 502) {
				fileReceived($j);			// File Received
				continue;
			}

			if ($pt == 503) {
				mailInfo($j);				// Mail status changed
				continue;
			}

			if ($pt == 701) {
				chatRoomChg();				// Chat room changed
				continue;
			}
		}

		debugFile("processAsyncMessages: complete - returning...");
	}

	function msgReceived($j) {

		// some 1:1 or pub/group chat message has been received - must save for use by the functions that display and handle messages!!!!

		global $globalResponse, $globalLoginArray, $globalParserIndex;

		debugFile("msgReceived: starting with index $j");

		// save every message into the SESSION with the key being the source user, message number, and parm
		// there is a SESSION key for each source user that indicates the number of messages from that user
		// for example SESSION['kevnewsCount'] has the number of SESSION entries for user kevnews
		// each message key is SESSION['kevnews~cid~1'], SESSION['kevnews~fname~2'], etc.

		$source = $globalResponse[$j][2];

		// first setup the count

		$countKey = $source . "Count";

		$count = $_SESSION[$countKey];
		if (!strlen($count)) {
			$count = 0;
		}

		// setup the user key

		$count++;				// bump the count

		$key = $source . "~msgType~" . $count;
		$_SESSION[$key] = $globalResponse[$j][1];

		$key = $source . "~source~" . $count;
		$_SESSION[$key] = $globalResponse[$j][2];

		debugFile("msgReceived: creating entry for dstType: ". $globalResponse[$j][3]);
		$key = $source . "~dstType~" . $count;
		$_SESSION[$key] = $globalResponse[$j][3];

		$key = $source . "~dest~" . $count;
		$_SESSION[$key] = $globalResponse[$j][4];

		$key = $source . "~cid~" . $count;
		$_SESSION[$key] = $globalResponse[$j][5];

		$key = $source . "~contentType~" . $count;
		$_SESSION[$key] = $globalResponse[$j][6];

		$key = $source . "~fname~" . $count;
		$_SESSION[$key] = $globalResponse[$j][7];

		debugFile("msgReceived: creating entry with content: ". $globalResponse[$j][8]);
		$key = $source . "~content~" . $count;
		$_SESSION[$key] = $globalResponse[$j][8];

		$key = $source . "~fromAdmin~" . $count;
		$_SESSION[$key] = $globalResponse[$j][9];

		$key = $source . "~dispPicture~" . $count;
		$_SESSION[$key] = $globalResponse[$j][10];

		$_SESSION[$countKey] = $count;;

		//debugFile("msgReceived: SESSION is now: " . print_r($_SESSION, TRUE));
		return;
	}

	function makeLink($linkName, $cmdName) {

		global $wapDestUrl;

		// cmd can have "@@accesskey=N" at the END and that is the accesskey assigned, if any (N is a number for the key)

		if (strstr($cmdName, "@@accesskey")) {

			$ak = strstr($cmdName, "@@accesskey");
			$akn = substr($ak, 12, 1);		// key number

			// now fix the cmd by removing this gibberish!

			$pos = strpos($cmdName, "@@accesskey");
			$myCmd = substr($cmdName, 0, $pos);
			$aks = "accesskey=\"$akn\"";

		} else {
			$myCmd = $cmdName;
			$aks = "";
		}

		return("<small><a $aks href='" . $wapDestUrl . "?cmd=$myCmd'>$linkName</a></small> \n");
	}

	function outstandingContactReqs() {

		global $wapDestUrl;

		// check to see if there are any outstanding contact requests

		// just return if msg indicator is not relevant (for example on the make call or sms page)

		if (getenv("showMsgs") == "false") {
			return("");
		}

		$list = array_keys($_SESSION);

		// first count the number of pending requests to see if the user should get directly placed
		// into that form or a list of users form

		$pendList = array();

		foreach ($list as $key) {

			if (strstr($key, "contactAdded#")) {			// there is a contact added pending
				debugFile("outstandingContactReqs: found a request: $key");
				$user = substr($key, strpos($key, "#")+1);
				array_push($pendList, $user);
			}
		}

		$count = count($pendList);

		if ($count == 1) {						// only 1 pending request - send them directly there

			debugFile("outstandingContactReqs: found a single request: " . $pendList[0]);
			$destBuddy = $pendList[0];

		} else {							// multiple requests - send them to a form to select

			foreach ($list as $key) {

				if (strstr($key, "contactAdded#")) {			// there is a contact added pending
					debugFile("outstandingContactReqs: found a request: $key");
				}
			}
		}

		return("");			// nothing
	}

	function _XXX_getprog() {

		if (strstr($_SERVER['REQUEST_URI'], "?")) {
			$prog = substr($_SERVER['REQUEST_URI'], 0, strpos($_SERVER['REQUEST_URI'], "?"));
		} else {
			$prog = $_SERVER['REQUEST_URI'];
		}
		return($prog);
	}

	function getprog() {

		return($_SESSION['prog']);
	}

	function presenceAltered($index) {

		global $globalResponse, $globalLoginArray, $globalParserIndex;

		$cid = $globalResponse[$index][1];			// field 1 is contact id

		debugFile("presenceAltered: starting with cid $cid");
		debugFile("presenceAltered: processing display name: " . $_SESSION["cid2disp" . $cid]);

		for ($j = 2; $j <= 7; $j++) {
			debugFile("presenceAltered: value for index $j is: " . $globalResponse[$index][$j]);
		}

		if ( $globalResponse[$index][2] > 0) setContactPresence($cid, $globalResponse[$index][2], "fusion");
		if ( $globalResponse[$index][3] > 0) setContactPresence($cid, $globalResponse[$index][3], "msn");
		if ( $globalResponse[$index][4] > 0) setContactPresence($cid, $globalResponse[$index][4], "aim");
		if ( $globalResponse[$index][5] > 0) setContactPresence($cid, $globalResponse[$index][5], "yahoo");
		if ( $globalResponse[$index][6] > 0) setContactPresence($cid, $globalResponse[$index][6], "icq");
		if ( $globalResponse[$index][7] > 0) setContactPresence($cid, $globalResponse[$index][7], "jabber");
		if ( $globalResponse[$index][8] > 0) setContactPresence($cid, $globalResponse[$index][8], "gtalk");

		return;
	}

	function processAlert($content) {

                global $globalResponse;

                $now = gettimeofday();
                $key = $now['sec'].$now['usec'];

                debugFile("processAlert: alert arrived, setting key $key with value $content");

                $_SESSION['alerts'][$key] = $content;
                debugFile("processAlert: returning from processAlert");
                return;
        }

	function imSessionStatus($item) {

			global $globalResponse;

			$which = $globalResponse[$item][1];
			$action = $globalResponse[$item][2];

			debugFile("imSessionStatus: status change which $which action $action");
			return;
	}

	function groupAltered() {
	}

	function getProgFile($prog){

		$prog = substr($prog, strripos($prog, '/') + 1, strlen($prog));
		return $prog;
	}

?>
