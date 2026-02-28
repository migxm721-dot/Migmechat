<?php
include_once("../../common/common-inc.php");
session_start();
unset($_SESSION['user']);
unset($_SESSION['displayAchievementMsg']);

header("Location: ../member2/t.php?cmd=logout");
?> 