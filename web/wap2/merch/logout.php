<?php
include_once("../../common/common-inc.php");
session_start();
unset($_SESSION['user']);
unset($_SESSION['reg_captcha']);
header("Location: ../member2/t.php");
?> 