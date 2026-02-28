<?php
	ini_set('memory_limit', '2048M');
	set_time_limit(30*60);
    $functions = array();
    $paths = array(
//     	  "/var/www/htdocs/sites/common"
//     	, "/var/www/htdocs/sites/controller"
//     	, "/var/www/htdocs/sites/dao"
//     	, "/var/www/htdocs/sites/decorator"
		  "/var/www/htdocs/sites/domain"
//     	, "/var/www/htdocs/sites/error"
// 		, "/var/www/htdocs/sites/lib/apn"
// 		, "/var/www/htdocs/sites/lib/cache"
// 		, "/var/www/htdocs/sites/lib/captcha"
// 		, "/var/www/htdocs/sites/lib/credential"
// 		, "/var/www/htdocs/sites/lib/encoder"
// 		, "/var/www/htdocs/sites/lib/excel_reader"
// 		, "/var/www/htdocs/sites/lib/facebook"
// 		, "/var/www/htdocs/sites/lib/fusion"
// 		, "/var/www/htdocs/sites/lib/ga.php"
// 		, "/var/www/htdocs/sites/lib/ga_midlet.php"
// 		, "/var/www/htdocs/sites/lib/identicon"
// 		, "/var/www/htdocs/sites/lib/image"
// 		, "/var/www/htdocs/sites/lib/instrumentation"
// 		, "/var/www/htdocs/sites/lib/language"
// 		, "/var/www/htdocs/sites/lib/migUI"
// 		, "/var/www/htdocs/sites/lib/mobile"
// 		, "/var/www/htdocs/sites/lib/mogilefs"
// 		, "/var/www/htdocs/sites/lib/oauth"
// 		, "/var/www/htdocs/sites/lib/rest"
// 		, "/var/www/htdocs/sites/lib/rss"
// 		, "/var/www/htdocs/sites/lib/search"
// 		, "/var/www/htdocs/sites/lib/shindig"
// 		, "/var/www/htdocs/sites/lib/system"
// 		, "/var/www/htdocs/sites/lib/translator"
// 		, "/var/www/htdocs/sites/lib/validator"
// 		, "/var/www/htdocs/sites/lib/wordpress"
// 		, "/var/www/htdocs/sites/lib/wurfl"
// 		, "/var/www/htdocs/sites/lib/zoho"
//     	, "/var/www/htdocs/sites/model"
//     	, "/var/www/htdocs/sites/module"
//     	, "/var/www/htdocs/sites/resources"
//     	, "/var/www/htdocs/sites/validation"
//     	, "/var/www/htdocs/sites/view"
    );
    $reference_path="/var/www/htdocs";
    foreach ($paths as $path)
    {
	    define_dir($path, $functions);
    }
    reference_dir($reference_path, $functions);
//     echo
//         "<table>" .
//                 "<tr>" .
//                         "<th>Name</th>" .
//                         "<th>Defined</th>" .
//                         "<th>Referenced</th>" .
//                 "</tr>";
    foreach ($functions as $name => $value) {
    	if (! empty($_GET['filter']) || $argc > 0)
    	{
    		$defined = (isset($value[0]) ? count($value[0]) : 0);
    		$referenced = (isset($value[1]) ? count($value[1]) : 0);
    		if ($defined == 0 || $referenced > 1) unset($functions[$name]);
    	}
//         echo
//                 "<tr>" .
//                         "<td>" . htmlentities($name) . "</td>" .
//                         "<td>" . (isset($value[0]) ? count($value[0]) : "-") . "</td>" .
//                         "<td>" . (isset($value[1]) ? count($value[1]) : "-") . "</td>" .
//                 "</tr>";
    }
//     echo "</table>";
	header('Content-Type: application/json');
	echo json_encode($functions);
    function define_dir($path, &$functions) {
        if ($dir = opendir($path)) {
                while (($file = readdir($dir)) !== false) {
                        if (substr($file, 0, 1) == ".") continue;
                        if (is_dir($path . "/" . $file)) {
                                define_dir($path . "/" . $file, $functions);
                        } else {
                                if (substr($file, - 4, 4) != ".php") continue;
                                define_file($path . "/" . $file, $functions);
                        }
                }
        }
    }
    function define_file($path, &$functions) {
        $tokens = token_get_all(file_get_contents($path));
        for ($i = 0; $i < count($tokens); $i++) {
                $token = $tokens[$i];
                if (is_array($token)) {
                        if ($token[0] != T_FUNCTION) continue;
                        $i++;
                        $token = $tokens[$i];
                        if ($token[0] != T_WHITESPACE) continue; //die("T_WHITESPACE");
                        $i++;
                        $token = $tokens[$i];
                        if ($token[0] != T_STRING) continue;//die("T_STRING");
                        $functions[$token[1]][0][] = array($path, $token[2]);
                }
        }
    }
    function reference_dir($path, &$functions) {
        if ($dir = opendir($path)) {
                while (($file = readdir($dir)) !== false) {
                        if (substr($file, 0, 1) == ".") continue;
                        if (is_dir($path . "/" . $file)) {
                                reference_dir($path . "/" . $file, $functions);
                        } else {
                                if (substr($file, - 4, 4) != ".php") continue;
                                reference_file($path . "/" . $file, $functions);
                        }
                }
        }
    }
    function reference_file($path, &$functions) {
        $tokens = token_get_all(file_get_contents($path));
        for ($i = 0; $i < count($tokens); $i++) {
                $token = $tokens[$i];
                if (is_array($token)) {
                        if ($token[0] != T_STRING) continue;
                        if ($tokens[$i + 1] != "(") continue;
                        $functions[$token[1]][1][] = array($path, $token[2]);
                }
        }
    }
?>