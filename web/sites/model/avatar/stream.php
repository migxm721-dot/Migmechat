<?php
	class StreamModel extends Model
	{
		public function get_data($data)
		{
			//header("Cache-Control: no-cache, must-revalidate");
			$filename = value_exists("head")?get_value_from_array("head_filename", $data):get_value_from_array("filename", $data);

			//FB::log($filename);

			$resource = Image::get_image_resource($filename);

			header( "Last-Modified: " . gmdate( "D, j M Y H:i:s" ) . " GMT" );
			header( "Expires: " . gmdate( "D, j M Y H:i:s", time() ) . " GMT" );
			header( "Cache-Control: no-store, no-cache, must-revalidate" ); // HTTP/1.1
			header( "Cache-Control: post-check=0, pre-check=0", FALSE );
			header( "Pragma: no-cache" ); // HTTP/1.0
			header("Content-type: image/png");

			if($resource->is_png())
			{
				header("Content-type: image/png");
			}
			else
			{
				header("Content-type: image/gif");
			}

			Image::get_instance()->output_image($resource);
			Image::get_instance()->destroy_image($resource);
			exit;
		}

	}
?>