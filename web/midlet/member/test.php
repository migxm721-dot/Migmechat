<?php
define('LOG4PHP_DIR', '../../common/log4php');
define('LOG4PHP_CONFIGURATION', '../../common/log4php.xml');
require_once(LOG4PHP_DIR.'/LoggerManager.php');
?>
<html>
  <head>
    <title>Test page</title>
  </head>
  <body bgcolor="white">
  	<p>Logging</p>
  	<?php
  		$logger = LoggerManager::getLogger('default');
  		$logger->debug("This is a debug");
  		$logger->info("This is a info");
  		$logger->warn("This is a warn");
  		$logger->error("This is a error");
  		$logger->fatal("This is a fatal");
  	?>
  </body>
</html>