<?php
	// Start Timers
	$start_time = start_time();

	// Path
	$current_path		= str_replace('build/scripts', '', dirname(__FILE__));
	$path_to_resources	= $current_path.'htdocs/resources';
	$path_to_js 		= $path_to_resources.'/js';
	$path_to_css		= $path_to_resources.'/css';

	// Minify Javascript
	concat_minified_javascript($path_to_js, 'mig33.bundle.min.js', array('jquery.min.js', 'bootstrap.min.js', 'jschannel.min.js', 'jquery.guiders.min.js'));
	minify_javascript($path_to_js.'/web');
	concat_minified_javascript($path_to_js.'/web', 'web.min.js');

	// Minify CSS
	minify_css('web', $path_to_css, array('jquery.qtip.css', 'jquery.fancybox.css', 'jquery.guiders.css'));
	minify_css('wap', $path_to_css);

	// Functions
	function start_time()
	{
		$time = microtime();
		$time = explode(' ', $time);
		$time = $time[1] + $time[0];
		return $time;
	}

	function end_time($start_time)
	{
		$time = microtime();
		$time = explode(' ', $time);
		$time = $time[1] + $time[0];
		$finish = $time;
		echo 'Time Taken: '.round(($finish - $start_time), 4).' seconds'."\n\n";
	}

	function minify_css($platform, $path_to_css, $additional_css = array())
	{
		$css_array = array($path_to_css.'/'.$platform.'.css');
		if(sizeof($additional_css) > 0)
		{
			echo 'Combining additional non-minified CSS:'."\n";
			foreach($additional_css as $css)
			{
				$css_array[] = $path_to_css.'/'.$css;
				echo $path_to_css.'/'.$css."\n";
			}
			exec('cat '.implode(' ', $css_array).' > "'.$path_to_css.'/'.$platform.'.min.css"');
			echo "DONE\n\n";
		}

		echo 'Minifying platform non-minified CSS:'."\n";
		if(sizeof($additional_css) > 0)
		{
			exec('java -jar scripts/yuicompressor-2.4.6.jar "'.$path_to_css.'/'.$platform.'.min.css" --type css -o "'.$path_to_css.'/'.$platform.'.min.css"');
		}
		else
		{
			exec('java -jar scripts/yuicompressor-2.4.6.jar "'.$path_to_css.'/'.$platform.'.css" --type css -o "'.$path_to_css.'/'.$platform.'.min.css"');
		}
		echo 'Output: '.$path_to_css.'/'.$platform.'.min.css'."\n";
		echo "DONE\n\n";
	}

	function concat_minified_javascript($path_to_js, $output_js, $priority_js = array())
	{
		echo 'Concatenating minified JS:'."\n";
		$i= 1;
		$js_files = array();
		if(sizeof($priority_js) > 0)
		{
			echo 'Priority minified JS:'."\n";
			$i = 1;
			foreach($priority_js as $entry)
			{
				echo number_pad($i, 2).': '.$entry."\n";
				$js_files[] = $path_to_js.'/'.$entry;
				$i++;
			}
			echo "\n";
		}
		echo 'Normal minified JS:'."\n";
		if($handle = opendir($path_to_js))
		{
			while(false !== ($entry = readdir($handle)))
			{
				if
				(
					   $entry != '.'
					&& $entry != '..'
					&& $entry != $output_js
					&& substr($entry, -7) == '.min.js'
					&& !in_array($entry, $priority_js)
				)
				{
					echo number_pad($i, 2).': '.$entry."\n";
					$js_files[] = $path_to_js.'/'.$entry;
					$i++;
				}
			}
			closedir($handle);
		}
		exec('java -jar scripts/compiler.jar --js '.implode(' ', $js_files).' --js_output_file "'.$path_to_js.'/'.$output_js.'" --compilation_level WHITESPACE_ONLY');
		echo "\n";
		if(file_exists($path_to_js.'/'.$output_js))
			echo 'Concatenated minified JS: '.$output_js."\n";
		else
			echo 'Error concatenating minified JS'."\n";
		echo "DONE\n\n";
	}

	function minify_javascript($path_to_js)
	{
		echo 'Minify JS:'."\n";
		$i= 1;
		if($handle = opendir($path_to_js))
		{
			while(false !== ($entry = readdir($handle)))
			{
				if
				(
					   $entry != '.'
					&& $entry != '..'
					&& substr($entry, -3) == '.js'
					&& substr($entry, -7) != '.min.js'
				)
				{
					$new_js_file = str_replace('.js', '.min.js', $entry);
					exec('java -jar scripts/compiler.jar --js '.$path_to_js.'/'.$entry.' --js_output_file "'.$path_to_js.'/'.$new_js_file.'" --compilation_level SIMPLE_OPTIMIZATIONS');
					if(file_exists($path_to_js.'/'.$entry))
						echo number_pad($i, 2).': '.$entry.' (Minified)'."\n";
					else
						echo number_pad($i, 2).': '.$entry.' (Error)'."\n";
					$i++;
				}
			}
			closedir($handle);
		}
		echo "DONE\n\n";
	}

	function number_pad($number, $n)
	{
		return str_pad(intval($number), $n, '0', STR_PAD_LEFT);
	}

	// End Timer
	end_time($start_time);
?>
