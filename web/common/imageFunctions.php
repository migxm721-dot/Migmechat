<?php
	class ImageResource
	{
		public $filename;
		public $cache_threshhold;
		public $disk_filename;
		public $use_cache;
		private $fp;
		private $image_resource;
		private $image_size;
		private $border = 0;

		public function __construct($filename, $cache_threshhold, $border=0)
		{
			global $sharedStoragePath;
			$this->filename = $filename;
			$this->cache_threshhold = $cache_threshhold;
			$this->border = $border;
			$this->disk_filename = sprintf("%simage_cache/%s", $sharedStoragePath, $this->filename);
			$this->use_cache = $this->check_use_cache();
			$this->fp = $this->get_file_pointer();
		}

		private function check_use_cache()
		{
			$timediff = $this->get_create_time_difference();
			if( $timediff == 0 || $timediff>$this->cache_threshhold )
			{
				return false;
			}
			else
				return true;
		}

		private function get_create_time_difference()
		{
			if( !file_exists($this->disk_filename) )
			{
				return 0;
			}

			$current = time();
			$stats = stat($this->disk_filename);
			$ctime = $stats['ctime'];
			return abs($current-$ctime);
		}

		private function get_file_pointer()
		{
			if( !$this->use_cache )
			{
				if( file_exists($this->disk_filename) )
					unlink($this->disk_filename);
				return 0;
			}
			return 0;
		}

		public function create_transparent_png($width, $height)
		{
			$this->image_resource = imagecreatetruecolor($width + ($this->border*2), $height + ($this->border*2));
		    imagesavealpha($this->image_resource, true);

	    	$trans_colour = imagecolorallocatealpha($this->image_resource, 0, 0, 0, 127);
	    	imagefill($this->image_resource, 0, 0, $trans_colour);
		}

		public function copy_image($filename, $xpos, $ypos, $xgap=0)
		{
			$image_info = getimagesize($filename);
			$width = $image_info[0];
			$height = $image_info[1];

			$img = imagecreatefromgif($filename);
			$startx = $xpos + ($xpos==0?0:$xgap) + $this->border;
			imagecopy($this->image_resource, $img, $startx, $ypos + $this->border, 0, 0, $width, $height );

			imagedestroy($img);
		}

		public function save_png()
		{
			if( !imagepng($this->image_resource, $this->disk_filename) )
				die("Unable to create image");
			imagedestroy($this->image_resource);
		}

		public function get_url()
		{
			global $sharedStorageURL;
			return sprintf("%simage_cache/%s", $sharedStorageURL,  $this->filename);
		}

		public function get_height_width_string()
		{
			if( empty($this->image_size) )
				$this->image_size = getimagesize($this->disk_filename);
			return $this->image_size[3];
		}

		public function get_width()
		{
			if( empty($this->image_size) )
				$this->image_size = getimagesize($this->disk_filename);
			return $this->image_size[0];
		}

		public function get_height()
		{
			if( empty($this->image_size) )
				$this->image_size = getimagesize($this->disk_filename);
			return $this->image_size[1];
		}
	}
?>