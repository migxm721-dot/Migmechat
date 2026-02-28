#!/usr/bin/php
<?php 	
function get_framework_common_directory()
{
	return get_framework_base_directory() . '/common';
}

function get_framework_base_directory()
{
	return 'sites';
}

function get_controller_directory()
{
	return get_framework_base_directory() . '/controller';
}

function clean_up()
{
	$filenames = glob(get_controller_directory() . "/" . "*" . ".yaml");
	foreach($filenames as $filename)
	{
		if( file_exists($filename) )
		{
			require_once(get_framework_common_directory() . "/spyc.php");
	
			$definitions = Spyc::YAMLLoad($filename);
			
			foreach ($definitions as $name => $action)
			{
				$components = array();
				
				if(isset($action['models']['init']))
				{
					foreach($action['models']['init'] as $init_model)
						array_push($components, $init_model);
					
					unset($action['models']['init']);
				}
				
				if(isset($action['validation']['pre']))
				{
					foreach($action['validation']['pre'] as $pre_validator)
						array_push($components, $pre_validator." ?");
					
					unset($action['validation']['pre']);
				}

				if(isset($action['models']))
				{
					foreach($action['models'] as $models)
						array_push($components, $models);
					
					unset($action['models']);
				}
				
				if(isset($action['validation']['post']))
				{
					foreach($action['validation']['post'] as $post_validator)
						array_push($components, $post_validator." ?");
					
					unset($action['validation']);
				}
				
				if(!empty($components))
					$definitions[$name]['components'] = $components;
				unset($definitions[$name]['models']);
				unset($definitions[$name]['validation']);
					
				
					
			}
			$fh = fopen($filename, 'w');
			$content = Spyc::YAMLDump($definitions);
		//	var_dump($filename);
		//	var_dump($content);
			fwrite($fh, $content);
			
		}
	}
}
clean_up();