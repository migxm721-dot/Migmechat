<?php
	require_once("./common/utilities.php");
	fast_require("ActionRouter", get_framework_common_directory()."/action_router.php");

     /**
     * Convert "amp;FOO" to "FOO" in query string keys
     * See JIRA-742 and MIDP-479
     **/
    if (strpos($_SERVER['QUERY_STRING'], "&amp;") !== FALSE)
    {
        foreach ($_GET as $k=>$v)
        {
                // does this query parameter start with 'amp;' ?
                if (substr($k,0,4) == "amp;")
                {
                    // if yes, convert the key to one without 'amp;'
                    $k2 = substr($k,4);
                    $_GET[$k2] = $v;
                    unset($_GET[$k]);
                }
        }
	}

	$action_router = ActionRouter::get_instance();
	$action_router->execute();
?>
