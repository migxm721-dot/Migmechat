<?php
	fast_require("View", get_framework_common_directory()."/view.php");
	require_once("utilities.php");

	/**
	*
	* Utility class to handle modules
	*
	**/
	class Modules
	{
		/**
		*
		* include_once for a module
		*
		**/
		public static function include_module($module_name, $data = array())
		{
			foreach( $data as $key => $value )
				$$key = $value;

			$filename_parts = array(
				  get_view()
				, View::get_override_view(get_view())
				, 'common'
				, $module_name
			);

			foreach($filename_parts as $filename_part)
			{
				$file = sprintf("%s/%s/%s.php", get_module_directory(), $module_name, $filename_part);
				if(file_exists($file))
				{
					include($file);
					break;
				}
			}

			if( !empty($return_data) )
			{
				return $return_data;
			}
		}
	}
?>