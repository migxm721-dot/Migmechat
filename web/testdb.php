<?php
include_once("common/common-inc.php");

try{
    $languages = soap_call_ejb('getLanguages', array());
    if (strlen($languages[0]['code']) == 3)
        echo 'OK';
    else
        echo 'Error';
} catch(Exception $e) {
    echo 'Error:';
    echo $e;
    echo $e->getMessage();
}
?>