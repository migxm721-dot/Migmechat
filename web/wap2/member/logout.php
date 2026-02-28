<?php
include_once("../../common/common-inc.php");
session_start();
unset($_SESSION['user']);

header("Location: ../member2/t.php");
?> 