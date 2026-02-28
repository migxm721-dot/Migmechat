<?php
// let's send the baby to the browser
header( 'Content-type: image/gif' );
header( 'Content-Disposition: inline; filename="t.gif"' );
header( 'Content-length: '.(string)(filesize($_SERVER['DOCUMENT_ROOT']."/images/t.gif")) );
$fd = fopen( $_SERVER['DOCUMENT_ROOT']."/images/t.gif" , 'r' );
fpassthru( $fd );
?>