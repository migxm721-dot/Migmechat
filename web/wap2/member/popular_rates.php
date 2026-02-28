<?php
include_once("../member2/common-inc-kk.php");
include_once("../member2/emit.php");
include_once("../member2/check.php");
putenv("pagelet=true");

session_start();
global $cid, $prog;
$cid=$_REQUEST['cid'];
$prog = $_SESSION['prog'];
include_once(('../member2/'.getProgFile($prog)));

//Check async messages
checkServerSessionStatus();

ice_check_session();

$userDetails = ice_get_userdata();
$country = '';
$error = '';
$pf = $_GET['pf'];

//read rategrid data from file system
$filename =  $apache_dir. '/cache/objects/rategrid.ser';
$handle = fopen($filename, 'r');
$rateGrid_serialized = fread($handle, filesize($filename));
fclose($handle);
$rateGrid = unserialize($rateGrid_serialized);
$country = get_country($userDetails->countryID);

// Load any fixed call rates from the origin country
try {
	$fixedCallRates = soap_call_ejb('getFixedCallRates', array($userDetails->countryID));
} catch(Exception $e) {}

//Get the source data by looping through the rategrid data
for ($i = 0; $i < sizeof($rateGrid); $i++){
	if($rateGrid[$i]['id'] == $userDetails->countryID){
		$ocountryid = $userDetails->countryID;
		$ocountry	= $rateGrid[$i]['country'];
		$ocsf 		= $rateGrid[$i]['callSignallingFee'];
		$omsf		= $rateGrid[$i]['mobileSignallingFee'];
		$omr		= $rateGrid[$i]['mobileRate'];
		$ocr		= $rateGrid[$i]['callRate'];
		break;
	}
}

emitHeader();
emitTitle("Popular Call Rates");

if(empty($error) || strlen($error) == 0){
?>
		<small><b>From: </b><?=$country?></small><br/>
		<small><b>Currency: </b><?=$userDetails->currency?></small><br/>
		<br/>
		<small><b>Per-min (From mobile):</b></small><br/>
		<small><b>Country: mobile / landline</b></small><br/>
		<?php
			//Loop through the rate grid data to calculate overall cost
			for ($i = 0; $i < sizeof($rateGrid); $i++)
			{
				$result = 0;
				$ml = number_format(($omr * get_exchangeRate($userDetails->currency)) + ($rateGrid[$i]['callRate'] * get_exchangeRate($userDetails->currency)),3);
				$mm = number_format(($omr * get_exchangeRate($userDetails->currency)) + ($rateGrid[$i]['mobileRate'] * get_exchangeRate($userDetails->currency)),3);

				if($userDetails->countryID == 1){
					if($rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 3){
					if($rateGrid[$i]['id'] == 247 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 3 ||
					   $rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 132 ||
					   $rateGrid[$i]['id'] == 155 ||
					   $rateGrid[$i]['id'] == 6){
						$result = 1;
					}
				}else if($userDetails->countryID == 6){
					if($rateGrid[$i]['id'] == 6){
						$result = 1;
					}
				}else if($userDetails->countryID == 10){
					if($rateGrid[$i]['id'] == 10 ||
					   $rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 192 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 62){
						$result = 1;
					}
				}else if($userDetails->countryID == 14){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 157 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 230){
						$result = 1;
					}
				}else if($userDetails->countryID == 16){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 184 ||
					   $rateGrid[$i]['id'] == 16){
						$result = 1;
					}
				}else if($userDetails->countryID == 17){
					if($rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 17 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 19){
					if($rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 19 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 71 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 229){
						$result = 1;
					}
				}else if($userDetails->countryID == 20){
					if($rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 121){
						$result = 1;
					}
				}else if($userDetails->countryID == 23){
					if($rateGrid[$i]['id'] == 23 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 158){
						$result = 1;
					}
				}else if($userDetails->countryID == 24){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 16 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 114){
						$result = 1;
					}
				}else if($userDetails->countryID == 28){
					if($rateGrid[$i]['id'] == 20){
						$result = 1;
					}
				}else if($userDetails->countryID == 29){
					if($rateGrid[$i]['id'] == 208 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 29 ||
					   $rateGrid[$i]['id'] == 116){
						$result = 1;
					}
				}else if($userDetails->countryID == 30){
					if($rateGrid[$i]['id'] == 30 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40){
						$result = 1;
					}
				}else if($userDetails->countryID == 31){
					if($rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 31 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116){
						$result = 1;
					}
				}else if($userDetails->countryID == 32){
					if($rateGrid[$i]['id'] == 32 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 72 ||
					   $rateGrid[$i]['id'] == 46 ||
					   $rateGrid[$i]['id'] == 214 ||
					   $rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 159){
						$result = 1;
					}
				}else if($userDetails->countryID == 34){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 216 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 103 ||
					   $rateGrid[$i]['id'] == 229){
						$result = 1;
					}
				}else if($userDetails->countryID == 38){
					if($rateGrid[$i]['id'] == 38 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 47 ||
					   $rateGrid[$i]['id'] == 216 ||
					   $rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 213 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 50){
						$result = 1;
					}
				}else if($userDetails->countryID == 39){
					if($rateGrid[$i]['id'] == 39 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116){
						$result = 1;
					}
				}else if($userDetails->countryID == 40){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 61 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 194){
						$result = 1;
					}
				}else if($userDetails->countryID == 41){
					if($rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 46){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 46){
						$result = 1;
					}
				}else if($userDetails->countryID == 47){
					if($rateGrid[$i]['id'] == 47 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 108 ||
					   $rateGrid[$i]['id'] == 186 ||
					   $rateGrid[$i]['id'] == 34){
						$result = 1;
					}
				}else if($userDetails->countryID == 50){
					if($rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 50){
						$result = 1;
					}
				}else if($userDetails->countryID == 52){
					if($rateGrid[$i]['id'] == 52){
						$result = 1;
					}
				}else if($userDetails->countryID == 53){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 53 ||
					   $rateGrid[$i]['id'] == 34){
						$result = 1;
					}
				}else if($userDetails->countryID == 56){
					if($rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 56 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 89 ||
					   $rateGrid[$i]['id'] == 26 ||
					   $rateGrid[$i]['id'] == 36 ||
					   $rateGrid[$i]['id'] == 131 ||
					   $rateGrid[$i]['id'] == 125){
						$result = 1;
					}
				}else if($userDetails->countryID == 57){
					if($rateGrid[$i]['id'] == 57){
						$result = 1;
					}
				}else if($userDetails->countryID == 61){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 91){
						$result = 1;
					}
				}else if($userDetails->countryID == 62){
					if($rateGrid[$i]['id'] == 62){
						$result = 1;
					}
				}else if($userDetails->countryID == 63){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 179 ||
					   $rateGrid[$i]['id'] == 109 ||
					   $rateGrid[$i]['id'] == 178){
						$result = 1;
					}
				}else if($userDetails->countryID == 66){
					if($rateGrid[$i]['id'] == 221 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 185){
						$result = 1;
					}
				}else if($userDetails->countryID == 71){
					if($rateGrid[$i]['id'] == 71 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 121 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 222 ||
					   $rateGrid[$i]['id'] == 210 ||
					   $rateGrid[$i]['id'] == 204){
						$result = 1;
					}
				}else if($userDetails->countryID == 80){
					if($rateGrid[$i]['id'] == 20){
						$result = 1;
					}
				}else if($userDetails->countryID == 81){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 228 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 152 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 83){
					if($rateGrid[$i]['id'] == 222 ||
					   $rateGrid[$i]['id'] == 83){
						$result = 1;
					}
				}else if($userDetails->countryID == 85){
					if($rateGrid[$i]['id'] == 85 ||
					   $rateGrid[$i]['id'] == 20){
						$result = 1;
					}
				}else if($userDetails->countryID == 87){
					if($rateGrid[$i]['id'] == 87){
						$result = 1;
					}
				}else if($userDetails->countryID == 88){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 88){
						$result = 1;
					}
				}else if($userDetails->countryID == 89){
					if($rateGrid[$i]['id'] == 89 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 158 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 91){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 91 ||
					   $rateGrid[$i]['id'] == 71 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231){
						$result = 1;
					}
				}else if($userDetails->countryID == 98){
					if($rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 101){
					if($rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 101 ||
					   $rateGrid[$i]['id'] == 1 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 154){
						$result = 1;
					}
				}else if($userDetails->countryID == 103){
					if($rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 19 ||
					   $rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 194){
						$result = 1;
					}
				}else if($userDetails->countryID == 104){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 184 ||
					   $rateGrid[$i]['id'] == 196 ||
					   $rateGrid[$i]['id'] == 104){
						$result = 1;
					}
				}else if($userDetails->countryID == 106){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 121 ||
					   $rateGrid[$i]['id'] == 194){
						$result = 1;
					}
				}else if($userDetails->countryID == 107){
					if($rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 103 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 199 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 194){
						$result = 1;
					}
				}else if($userDetails->countryID == 108){
					if($rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 17 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 206 ||
					   $rateGrid[$i]['id'] == 235){
						$result = 1;
					}
				}else if($userDetails->countryID == 109){
					if($rateGrid[$i]['id'] == 109 ||
					   $rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 88 ||
					   $rateGrid[$i]['id'] == 212 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 162){
						$result = 1;
					}
				}else if($userDetails->countryID == 110){
					if($rateGrid[$i]['id'] == 110 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 111){
					if($rateGrid[$i]['id'] == 110 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 215 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 205 ||
					   $rateGrid[$i]['id'] == 47){
						$result = 1;
					}
				}else if($userDetails->countryID == 112){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 47){
						$result = 1;
					}
				}else if($userDetails->countryID == 113){
					if($rateGrid[$i]['id'] == 113 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 10 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 230){
						$result = 1;
					}
				}else if($userDetails->countryID == 114){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 114 ||
					   $rateGrid[$i]['id'] == 34){
						$result = 1;
					}
				}else if($userDetails->countryID == 115){
					if($rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 212 ||
					   $rateGrid[$i]['id'] == 228 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 34){
						$result = 1;
					}
				}else if($userDetails->countryID == 116){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 1 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 206 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 136){
						$result = 1;
					}
				}else if($userDetails->countryID == 117){
					if($rateGrid[$i]['id'] == 117 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 211){
						$result = 1;
					}
				}else if($userDetails->countryID == 120){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 120 ||
					   $rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 121){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 152 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 121 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 115){
						$result = 1;
					}
				}else if($userDetails->countryID == 122){
					if($rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 223 ||
					   $rateGrid[$i]['id'] == 122 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 108 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 171){
						$result = 1;
					}
				}else if($userDetails->countryID == 123){
					if($rateGrid[$i]['id'] == 123 ||
					   $rateGrid[$i]['id'] == 216 ||
					   $rateGrid[$i]['id'] == 114 ||
					   $rateGrid[$i]['id'] == 120 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 116){
						$result = 1;
					}
				}else if($userDetails->countryID == 124){
					if($rateGrid[$i]['id'] == 124 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 71 ||
					   $rateGrid[$i]['id'] == 31 ||
					   $rateGrid[$i]['id'] == 206 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 12 ||
					   $rateGrid[$i]['id'] == 195){
						$result = 1;
					}
				}else if($userDetails->countryID == 125){
					if($rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 154){
						$result = 1;
					}
				}else if($userDetails->countryID == 127){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 20){
						$result = 1;
					}
				}else if($userDetails->countryID == 128){
					if($rateGrid[$i]['id'] == 128 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 222 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 71){
						$result = 1;
					}
				}else if($userDetails->countryID == 130){
					if($rateGrid[$i]['id'] == 13 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 47 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 130){
						$result = 1;
					}
				}else if($userDetails->countryID == 131){
					if($rateGrid[$i]['id'] == 131){
						$result = 1;
					}
				}else if($userDetails->countryID == 133){
					if($rateGrid[$i]['id'] == 91 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 133 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 196){
						$result = 1;
					}
				}else if($userDetails->countryID == 134){
					if($rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 134 ||
					   $rateGrid[$i]['id'] == 34){
						$result = 1;
					}
				}else if($userDetails->countryID == 135){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 245 ||
					   $rateGrid[$i]['id'] == 204){
						$result = 1;
					}
				}else if($userDetails->countryID == 136){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 103){
						$result = 1;
					}
				}else if($userDetails->countryID == 137){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 206 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 19){
						$result = 1;
					}
				}else if($userDetails->countryID == 138){
					if($rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116){
						$result = 1;
					}
				}else if($userDetails->countryID == 141){
					if($rateGrid[$i]['id'] == 141){
						$result = 1;
					}
				}else if($userDetails->countryID == 143){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 1 ||
					   $rateGrid[$i]['id'] == 143 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 137){
						$result = 1;
					}
				}else if($userDetails->countryID == 145){
					if($rateGrid[$i]['id'] == 104 ||
					   $rateGrid[$i]['id'] == 145 ||
					   $rateGrid[$i]['id'] == 171){
						$result = 1;
					}
				}else if($userDetails->countryID == 148){
					if($rateGrid[$i]['id'] == 124 ||
					   $rateGrid[$i]['id'] == 148 ||
					   $rateGrid[$i]['id'] == 230){
						$result = 1;
					}
				}else if($userDetails->countryID == 149){
					if($rateGrid[$i]['id'] == 149 ||
					   $rateGrid[$i]['id'] == 210){
						$result = 1;
					}
				}else if($userDetails->countryID == 153){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 153 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 227){
						$result = 1;
					}
				}else if($userDetails->countryID == 154){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 122 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 170){
						$result = 1;
					}
				}else if($userDetails->countryID == 155){
					if($rateGrid[$i]['id'] == 155 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 6 ||
					   $rateGrid[$i]['id'] == 170 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 231){
						$result = 1;
					}
				}else if($userDetails->countryID == 157){
					if($rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 157 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 103){
						$result = 1;
					}
				}else if($userDetails->countryID == 158){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 158 ||
					   $rateGrid[$i]['id'] == 38){
						$result = 1;
					}
				}else if($userDetails->countryID == 159){
					if($rateGrid[$i]['id'] == 159 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 238 ||
					   $rateGrid[$i]['id'] == 32 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 46 ||
					   $rateGrid[$i]['id'] == 145 ||
					   $rateGrid[$i]['id'] == 113){
						$result = 1;
					}
				}else if($userDetails->countryID == 162){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 162){
						$result = 1;
					}
				}else if($userDetails->countryID == 163){
					if($rateGrid[$i]['id'] == 163 ||
					   $rateGrid[$i]['id'] == 238 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 169 ||
					   $rateGrid[$i]['id'] == 55 ||
					   $rateGrid[$i]['id'] == 178){
						$result = 1;
					}
				}else if($userDetails->countryID == 164){
					if($rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 164 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 165){
					if($rateGrid[$i]['id'] == 165 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 216 ||
					   $rateGrid[$i]['id'] == 158 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 110 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 169){
					if($rateGrid[$i]['id'] == 169 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 216 ||
					   $rateGrid[$i]['id'] == 158 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 110 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 170){
					if($rateGrid[$i]['id'] == 170 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 215 ||
					   $rateGrid[$i]['id'] == 243 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 154 ||
					   $rateGrid[$i]['id'] == 231){
						$result = 1;
					}
				}else if($userDetails->countryID == 171){
					if($rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 121){
						$result = 1;
					}
				}else if($userDetails->countryID == 173){
					if($rateGrid[$i]['id'] == 111){
						$result = 1;
					}
				}else if($userDetails->countryID == 174){
					if($rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 112){
						$result = 1;
					}
				}else if($userDetails->countryID == 176){
					if($rateGrid[$i]['id'] == 163 ||
					   $rateGrid[$i]['id'] == 205 ||
					   $rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 176){
						$result = 1;
					}
				}else if($userDetails->countryID == 177){
					if($rateGrid[$i]['id'] == 177){
						$result = 1;
					}
				}else if($userDetails->countryID == 178){
					if($rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 19 ||
					   $rateGrid[$i]['id'] == 121 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 38 ||
					   $rateGrid[$i]['id'] == 204){
						$result = 1;
					}
				}else if($userDetails->countryID == 179){
					if($rateGrid[$i]['id'] == 179 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 230){
						$result = 1;
					}
				}else if($userDetails->countryID == 180){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 170 ||
					   $rateGrid[$i]['id'] == 180 ||
					   $rateGrid[$i]['id'] == 20){
						$result = 1;
					}
				}else if($userDetails->countryID == 182){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 157 ||
					   $rateGrid[$i]['id'] == 182 ||
					   $rateGrid[$i]['id'] == 170 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 229){
						$result = 1;
					}
				}else if($userDetails->countryID == 184){
					if($rateGrid[$i]['id'] == 184 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 62 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 205){
						$result = 1;
					}
				}else if($userDetails->countryID == 185){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 1 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 206 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 136){
						$result = 1;
					}
				}else if($userDetails->countryID == 186){
					if($rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 24 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 186){
						$result = 1;
					}
				}else if($userDetails->countryID == 192){
					if($rateGrid[$i]['id'] == 192){
						$result = 1;
					}
				}else if($userDetails->countryID == 194){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 178 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 136){
						$result = 1;
					}
				}else if($userDetails->countryID == 195){
					if($rateGrid[$i]['id'] == 195 ||
					   $rateGrid[$i]['id'] == 32 ||
					   $rateGrid[$i]['id'] == 158 ||
					   $rateGrid[$i]['id'] == 120 ||
					   $rateGrid[$i]['id'] == 177){
						$result = 1;
					}
				}else if($userDetails->countryID == 196){
					if($rateGrid[$i]['id'] == 196 ||
					   $rateGrid[$i]['id'] == 210 ||
					   $rateGrid[$i]['id'] == 200 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 88 ||
					   $rateGrid[$i]['id'] == 17){
						$result = 1;
					}
				}else if($userDetails->countryID == 197){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 197 ||
					   $rateGrid[$i]['id'] == 108 ||
					   $rateGrid[$i]['id'] == 117){
						$result = 1;
					}
				}else if($userDetails->countryID == 199){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 199 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 47 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 153 ||
					   $rateGrid[$i]['id'] == 194){
						$result = 1;
					}
				}else if($userDetails->countryID == 200){
					if($rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 204){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 205){
						$result = 1;
					}
				}else if($userDetails->countryID == 205){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 205 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 161 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 206){
					if($rateGrid[$i]['id'] == 206 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 14){
						$result = 1;
					}
				}else if($userDetails->countryID == 207){
					if($rateGrid[$i]['id'] == 207 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 71 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 171){
						$result = 1;
					}
				}else if($userDetails->countryID == 209){
					if($rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 209 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 215 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 177 ||
					   $rateGrid[$i]['id'] == 162 ||
					   $rateGrid[$i]['id'] == 245 ||
					   $rateGrid[$i]['id'] == 107){
						$result = 1;
					}
				}else if($userDetails->countryID == 210){
					if($rateGrid[$i]['id'] == 196 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 91 ||
					   $rateGrid[$i]['id'] == 57 ||
					   $rateGrid[$i]['id'] == 108 ||
					   $rateGrid[$i]['id'] == 207 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 210){
						$result = 1;
					}
				}else if($userDetails->countryID == 211){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106){
						$result = 1;
					}
				}else if($userDetails->countryID == 212){
					if($rateGrid[$i]['id'] == 212 ||
					   $rateGrid[$i]['id'] == 121 ||
					   $rateGrid[$i]['id'] == 211 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 88 ||
					   $rateGrid[$i]['id'] == 128 ||
					   $rateGrid[$i]['id'] == 199 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 111){
						$result = 1;
					}
				}else if($userDetails->countryID == 213){
					if($rateGrid[$i]['id'] == 107 ||
					   $rateGrid[$i]['id'] == 126){
						$result = 1;
					}
				}else if($userDetails->countryID == 214){
					if($rateGrid[$i]['id'] == 214 ||
					   $rateGrid[$i]['id'] == 108 ||
					   $rateGrid[$i]['id'] == 230){
						$result = 1;
					}
				}else if($userDetails->countryID == 215){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 215 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 110){
						$result = 1;
					}
				}else if($userDetails->countryID == 216){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 216 ||
					   $rateGrid[$i]['id'] == 38 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 143){
						$result = 1;
					}
				}else if($userDetails->countryID == 218){
					if($rateGrid[$i]['id'] == 89){
						$result = 1;
					}
				}else if($userDetails->countryID == 222){
					if($rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 222 ||
					   $rateGrid[$i]['id'] == 218 ||
					   $rateGrid[$i]['id'] == 81 ||
					   $rateGrid[$i]['id'] == 165 ||
					   $rateGrid[$i]['id'] == 26 ||
					   $rateGrid[$i]['id'] == 150 ||
					   $rateGrid[$i]['id'] == 91 ||
					   $rateGrid[$i]['id'] == 89){
						$result = 1;
					}
				}else if($userDetails->countryID == 223){
					if($rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 223 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 228 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 91 ||
					   $rateGrid[$i]['id'] == 136){
						$result = 1;
					}
				}else if($userDetails->countryID == 227){
					if($rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 227 ||
					   $rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 117 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 191 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 107){
						$result = 1;
					}
				}else if($userDetails->countryID == 229){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 229 ||
					   $rateGrid[$i]['id'] == 204){
						$result = 1;
					}
				}else if($userDetails->countryID == 230){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 165 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 205){
						$result = 1;
					}
				}else if($userDetails->countryID == 231){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 111 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 61 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 194){
						$result = 1;
					}
				}else if($userDetails->countryID == 234){
					if($rateGrid[$i]['id'] == 195 ||
					   $rateGrid[$i]['id'] == 234 ||
					   $rateGrid[$i]['id'] == 10){
						$result = 1;
					}
				}else if($userDetails->countryID == 235){
					if($rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 108 ||
					   $rateGrid[$i]['id'] == 122 ||
					   $rateGrid[$i]['id'] == 137 ||
					   $rateGrid[$i]['id'] == 1 ||
					   $rateGrid[$i]['id'] == 115){
						$result = 1;
					}
				}else if($userDetails->countryID == 237){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 171 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 112 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 237 ||
					   $rateGrid[$i]['id'] == 205){
						$result = 1;
					}
				}else if($userDetails->countryID == 238){
					if($rateGrid[$i]['id'] == 238 ||
					   $rateGrid[$i]['id'] == 145 ||
					   $rateGrid[$i]['id'] == 32 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 205 ||
					   $rateGrid[$i]['id'] == 40){
						$result = 1;
					}
				}else if($userDetails->countryID == 239){
					if($rateGrid[$i]['id'] == 239 ||
					   $rateGrid[$i]['id'] == 14 ||
					   $rateGrid[$i]['id'] == 34 ||
					   $rateGrid[$i]['id'] == 120 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 161 ||
					   $rateGrid[$i]['id'] == 123 ||
					   $rateGrid[$i]['id'] == 88 ||
					   $rateGrid[$i]['id'] == 47){
						$result = 1;
					}
				}else if($userDetails->countryID == 243){
					if($rateGrid[$i]['id'] == 20 ||
					   $rateGrid[$i]['id'] == 136 ||
					   $rateGrid[$i]['id'] == 243 ||
					   $rateGrid[$i]['id'] == 194 ||
					   $rateGrid[$i]['id'] == 71 ||
					   $rateGrid[$i]['id'] == 185 ||
					   $rateGrid[$i]['id'] == 116 ||
					   $rateGrid[$i]['id'] == 115 ||
					   $rateGrid[$i]['id'] == 207){
						$result = 1;
					}
				}else if($userDetails->countryID == 245){
					if($rateGrid[$i]['id'] == 245 ||
					   $rateGrid[$i]['id'] == 106 ||
					   $rateGrid[$i]['id'] == 231 ||
					   $rateGrid[$i]['id'] == 40 ||
					   $rateGrid[$i]['id'] == 204 ||
					   $rateGrid[$i]['id'] == 230 ||
					   $rateGrid[$i]['id'] == 53 ||
					   $rateGrid[$i]['id'] == 155 ||
					   $rateGrid[$i]['id'] == 210 ||
					   $rateGrid[$i]['id'] == 171){
						$result = 1;
					}
				}else if($userDetails->countryID == 247){
					if($rateGrid[$i]['id'] == 204){
						$result = 1;
					}
				}

				if($result == 1){
					// Override the call rate if there is a temporary fixed rate to the destination
					if (is_array($fixedCallRates)) {
						for ($j = 0; $j < sizeof($fixedCallRates); $j++) {
							if ($fixedCallRates[$j]['DestinationCountryID'] == $rateGrid[$i]['id']) {
								$exchangeRate = get_exchangeRate($userDetails->currency);

								//if (is_numeric($fixedCallRates[$j]['LandlineToLandline']))
								//	$ll = number_format($fixedCallRates[$j]['LandlineToLandline'] * $exchangeRate,3);
								//if (is_numeric($fixedCallRates[$j]['LandlineToMobile']))
								//	$lm = number_format($fixedCallRates[$j]['LandlineToMobile'] * $exchangeRate,3);
								if (is_numeric($fixedCallRates[$j]['MobileToLandline']))
									$ml = number_format($fixedCallRates[$j]['MobileToLandline'] * $exchangeRate,3);
								if (is_numeric($fixedCallRates[$j]['MobileToMobile']))
									$mm = number_format($fixedCallRates[$j]['MobileToMobile'] * $exchangeRate,3);
							}
						}
					}

					print '<small><b>'.$rateGrid[$i]['country'].':</b> '.$mm.' / '.$ml.'</small><br/>';
				}
			}
		?>
		<br/>
		<small>For all call rates, visit our web site: www.mig33.com/rates.</small><br/>
		<br/>
		<small>If you have questions or comments about our rates, please email merchant@mig33.com.</small><br/>
		<br/>
		<?php
			if($pf == 'BM'){
				print '<small><a href="../merch/merchant.php">Back to Becoming a Merchant</a></small><br/>';
			}else if($pf == 'MC'){
				print '<small><a href="merchant_center.php">Back to Merchant Center</a></small><br/>';
			}else if($pf == 'SC'){
				print '<small><a href="sell_credits.php">Back to Selling Credits</a></small><br/>';
			}
  	  	?>
  	  	<br/>
  	  	<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>

	</body>
</html>
<?php
} else {
?>
  	  	<small>There is a problem showing popular call rates at the moment. Please try <a href="popular_rates.php?pf=<?=$pf?>">again</a>.</small><br/>
  	  	<br/>
		<?php
			if($pf == 'BM'){
				print '<small><a href="merchant.php">Back to Becoming a Merchant</a></small><br/>';
			}else if($pf == 'MC'){
				print '<small><a href="merchant_center.php">Back to Merchant Center</a></small><br/>';
			}else if($pf == 'SC'){
				print '<small><a href="sell_credits.php">Back to Selling Credits</a></small><br/>';
			}
		?>
  	  	<br/>
  	  	<small><a href="index.php">My Account Home</a></small><br/>
		<small><a href="../member2/t.php?cmd=home">Home</a></small><br/>
		<small><a href="logout.php">Logout</a></small><br/>
		<br/>
	</body>
</html>
<?php
}
?>

