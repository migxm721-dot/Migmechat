<?php
	$base_dir = ".";
	$script_dir = ".";
	$options = getopt("d:s:");
	if( isset($options["d"]) )
		$base_dir = $options["d"];
	if( isset($options["s"]) )
		$script_dir = $options["s"];

	// Minify Framework JS
	minify_js($base_dir, $script_dir);

	echo "\n";

	// Minify Touch JS
	minify_js($base_dir.'/touch', $script_dir);

	function minify_js($base_dir, $script_dir)
	{
		printf("Base Dir: %s\n", $base_dir);
		printf("Script Dir: %s\n", $script_dir);

		foreach( glob($base_dir . "/*.js") as $filename )
		{
			if( strstr($filename, ".min.js") == FALSE && strstr($filename, ".dev.js") == FALSE && strstr($filename, "ext") == FALSE )
			{

				$scr = basename($filename, ".js") . ".min.js";
				$script_name = $base_dir . "/" . $scr;
				printf("Processing: %s\n", basename($filename));
				printf("Minify: %s\n", $scr);

				exec("java -jar " . $script_dir . "/closure_compiler_20110405.jar --js=" . $filename . " --js_output_file=" . $script_name);
			}
		}
	}
?>
