<?php
	require_once("sfYaml.php");

	$base_dir = ".";

	$options = getopt("d:");
	if( isset($options["d"]) )
		$base_dir = $options["d"];

	$gen_dir = $base_dir . "/_gen";

	printf("Base Dir: %s\n", $base_dir);
	printf("Gen Dir: %s\n", $gen_dir);


	if( file_exists($gen_dir) == false )
	{
		mkdir($gen_dir);
	}

	foreach( glob($base_dir . "/*.yaml") as $filename )
	{
		printf("Processing : %s\n", basename($filename));
		$yaml = sfYaml::load($filename);

		$controller_name = basename($filename, ".yaml");
		$config_file = $gen_dir . "/" . $controller_name . "_config.php";

		printf("Generating: %s\n", basename($config_file));

		$fp = fopen($config_file, "w");	
		fwrite($fp, "<?php\n");
		fwrite($fp, "\$config = ");
		fwrite($fp, var_export($yaml, true));
		fwrite($fp, ";\n");
		fwrite($fp, "?>");
		fclose($fp);
	}
?>
