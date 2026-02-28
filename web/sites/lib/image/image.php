<?php
	require_once $_SERVER['DOCUMENT_ROOT']."/common/common-config.php";
	require_once $_SERVER['DOCUMENT_ROOT']."/sites/common/Predis.php";

	class Image
	{
		/**
		 * @var Image
		 */
		private static $instance;

		private $redis;

		/**
		*
		* Private functions
		*
		**/
		private function __construct()
		{
			$array_conn_settings = explode(':', $GLOBALS['redis_shard_directory_slave_server']);
			$redis_host = $array_conn_settings[0];
			$redis_port = $array_conn_settings[1];

			$this->redis = new Predis_Client(array(
					  'scheme'=> 'tcp'
					, 'host'  => $redis_host
					, 'port'  => $redis_port)
			);
		}

		private function __clone()
		{
		}

		/**
		*
		* Public static functions
		* @return Image
		**/
		public static function get_instance()
		{
			if( !self::$instance )
			{
				self::$instance = new Image();
			}
			return self::$instance;
		}

		/**
		*
		* Public functions
		*
		**/
		public function get_image_resource($filename)
		{
			$extension = end(explode('.', $filename));

			if(file_exists($filename))
			{
				switch($extension)
				{
					case 'gif':
						$type = 1;
						$resource = imagecreatefromgif($filename);
						break;
					case 'png':
						$type = 3;
						$resource = imagecreatefrompng($filename);
						break;
					default:
						throw new Exception("Unknown image type for " . $filename);
						break;
				}
			}
			else
			{
				switch($extension)
				{
					case 'gif':
						$type = 1;
						break;
					case 'png':
						$type = 3;
						break;
					default:
						throw new Exception("Unknown image type for " . $filename);
						break;
				}

				$resource = imagecreatefromstring($this->get_image_resource_from_redis($filename));
			}
			imagealphablending($resource, true);
			imagesavealpha($resource, true);
			return new ImageDetail($resource, $type);
		}

		/**
		*
		* Create the transparent image
		*
		**/
		public function create_transparent_image($width, $height, $opacity=127)
		{
			$image_resource = imagecreatetruecolor($width, $height);
		    imagesavealpha($image_resource, true);

	    	$trans_colour = imagecolorallocatealpha($image_resource, 0, 0, 0, $opacity);
	    	imagefill($image_resource, 0, 0, $trans_colour);

	    	$res = new ImageDetail($image_resource, IMAGETYPE_PNG);
	    	$res->set_width($width);
	    	$res->set_height($height);
	    	return $res;
		}

		/**
		*
		* Destroy the image
		*
		**/
		public function destroy_image($image_resource)
		{
			//imagedestroy($image_resource->get_resource());
			unset($image_resource);
		}

		/**
		*
		* Layer the image
		*
		**/
		public function layer_image($image_resource, $image_filename)
		{
			$layer_resource = $this->get_image_resource($image_filename);
			//list($width, $height, $type, $attr) = getimagesize($image_filename);
			imagecopy($image_resource->get_resource(), $layer_resource->get_resource(), 0, 0, 0, 0,
							$layer_resource->get_width(), $layer_resource->get_height());

			$this->destroy_image($layer_resource);
		}

		public function slice($source, $source_x, $source_y, $source_width, $source_height)
		{
			$resource = $this->create_transparent_image($source_width, $source_height);
			imagecopy($resource->get_resource(), $source->get_resource(), 0, 0, $source_x, $source_y, $source_width, $source_height);
			return $resource;
		}

		/**
		*
		* Save the image
		*
		**/
		public function save_image($image_resource, $filename = '')
		{
			$success = false;
			if( $image_resource->is_png())
				$success = imagepng($image_resource->get_resource(), $filename);
			if( $image_resource->is_gif())
				$success = imagegif($image_resource->get_resource(), $filename);
			if( !$success )
			{
				throw new Exception ("Unable to save image: " . $filename);
			}
		}

		/**
		*
		* Output the image
		*
		**/
		public function output_image($image_resource)
		{
			$this->save_image($image_resource);
		}

		public function resize_image($filename, $width, $height=0)
		{
			$resource = $this->get_image_resource($filename);
			return $this->resize_image_resource($resource, $width, $height);
		}

		public function resize_image_resource($resource, $width, $height=0)
		{
			if($height == 0)
			{
				$aspect_ratio = $resource->get_width()/$resource->get_height();
				$height = $width / $aspect_ratio;
			}

			$resized_resource = $this->create_transparent_image($width, $height);
			imagecopyresampled($resized_resource->get_resource(), $resource->get_resource(), 0, 0, 0, 0,
									$width, $height, $resource->get_width(), $resource->get_height());

			return $resized_resource;
		}

		public function get_image_resource_from_redis($filename)
		{
			return $this->redis->get(str_replace($_SERVER['DOCUMENT_ROOT'].'/', 'WR:', $filename));
		}
	}

	class ImageDetail
	{
		private $image_resource;
		private $image_type;
		private $height = 0;
		private $width = 0;

		public function __construct($resource, $type)
		{
			$this->image_resource = $resource;
			$this->image_type = $type;
		}

		public function __destruct()
		{
			imagedestroy($this->image_resource);
		}

		public function get_resource()
		{
			return $this->image_resource;
		}

		public function get_type()
		{
			return $this->image_type;
		}

		public function set_width($width)
		{
			$this->width = $width;
		}

		public function get_width()
		{
			if( $this->width ==0 ) $this->width = imagesx($this->image_resource);
			return $this->width;
		}

		public function set_height($height)
		{
			$this->height = $height;
		}

		public function get_height()
		{
			if($this->height == 0 ) $this->height = imagesy($this->image_resource);
			return $this->height;
		}

		public function is_png()
		{
			return $this->image_type == IMAGETYPE_PNG;
		}

		public function is_gif()
		{
			return $this->image_type == IMAGE_GIF;
		}
	}
?>