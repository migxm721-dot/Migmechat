<?php

	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");

	/*
		Jax Captcha Class v1.o1 - Copyright (c) 2005, Andreas John aka Jack (tR)
		This program and it's moduls are Open Source in terms of General Public License (GPL) v2.0

		class.captcha.php 		(captcha class module)

		Last modification: 2005-09-05
	*/

	class captcha
	{
		const DELIMITER = ',';
		const CAPTCHA_EXPIRY = 300; // 300s

		var $session_key = null;
		var $temp_dir    = null;

		var $width       = 90;
		var $height      = 45;
		var $jpg_quality = 30;



		/**
		 * Constructor - Initializes Captcha class!
		 *
		 * @param string $session_key
		 * @param string $temp_dir
		 * @return captcha
		 */
		function captcha( $session_key, $temp_dir )
		{
			$this->temp_dir    = $temp_dir;
			$this->session_key = $session_key;
		}

		/**
		 * Generates Image file for captcha
		 *
		 * @param string $location
		 * @param string $char_seq
		 * @return unknown
		 */
		function _generate_image( $location, $char_seq )
		{
      		global $apache_dir;

			$num_chars = strlen($char_seq);

			$img = imagecreate( $this->width, $this->height );
     		imagefilledrectangle($img, 0, 0, $this->width, $this->height, imagecolorallocate( $img, 255, 255, 255 ));
			imagealphablending($img, 1);

			// generate background of randomly built ellipses
			for ($i=1; $i<=15; $i++)
			{
				$r = round( rand( 0, 200 ) );
				$g = round( rand( 0, 200 ) );
				$b = round( rand( 0, 200 ) );
				$color = imagecolorallocate( $img, $r, $g, $b );
				imagefilledellipse( $img,round(rand(0,$this->width)), round(rand(0,$this->height)), round(rand(0,$this->width/16)), round(rand(0,$this->height/4)), $color );
			}

			$start_x = round($this->width / $num_chars);
		 	$max_font_size = $start_x;
      		$start_x = round(0.5*$start_x);
			$max_x_ofs = round($max_font_size*0.9);
      		$max_font_size = $start_x * 0.75;

			// set each letter with random angle, size and color
			for ($i=0;$i<=$num_chars;$i++)
			{
        		//$color = ( rand( 127, 255 ) );
        		$r = 0;
				$g = 0;
				$b = 0;
				//$r = round( rand( 0, 200 ) );
				//$g = round( rand( 0, 200 ) );
				//$b = round( rand( 0, 200 ) );
				$y_pos = ($this->height/2)+round( rand( 5, 20 ) );

				$fontsize = round( rand( 18, $max_font_size) );
				$color = imagecolorallocate( $img, $r, $g, $b);
				$presign = round( rand( 0, 1 ) );
				$angle = round( rand( 0, 10 ) );
				if ($presign==true) $angle = -1*$angle;

				ImageTTFText( $img, $fontsize, $angle, $start_x+$i*$max_x_ofs, $y_pos, $color, get_file_location('common/captcha/arial.ttf'), substr($char_seq,$i,1) );
			}

			// create image file
			imageantialias($img, true);

			imagepng( $img, $location);

			imagedestroy( $img );

			return true;
		}


		/**
		 * Returns name of the new generated captcha image file
		 *
		 * @param unknown_type $num_chars
		 * @return unknown
		 */
		function get_pic( $num_chars=8 )
		{
			// define characters of which the captcha can consist
			$alphabet = array(
				'A','B','C','D','E','F','G','H','J','K','L','M',
				'N','P','R','S','T','U','V','W','X','Y','Z');

			$max = sizeof( $alphabet );

			// generate random string
			$captcha_str = '';
			for ($i=1;$i<=$num_chars;$i++) // from 1..$num_chars
			{
				// choose randomly a character from alphabet and append it to string
				$chosen = rand( 1, $max );
				$captcha_str .= $alphabet[$chosen-1];
			}


			// we need to ensure the image captcha will not be cache, so we'll generate a unique names for each image
			// and we'll salt it for higher entropy
			$img_key = md5( $this->session_key . $captcha_str .  mt_rand() );

			// generate a picture file that displays the random string
			if ( $this->_generate_image( $this->get_image_path($img_key) , $captcha_str ) )
			{
				//consoleOut('Generating capcha (' . $captcha_str . ') with session key:' . $this->session_key);
				$memcache = Memcached::get_instance('captcha');
				$memcache->add_or_update(
						$this->session_key,
						array($this->get_captcha_hash($captcha_str), $img_key),
						self::CAPTCHA_EXPIRY
				);
				return( $img_key );
			}
			else
			{
				return false;
			}
		}

		private function get_image_path( $img_key )
		{
			return $this->temp_dir.'/cap_'.$img_key.'.png';
		}

		private function get_captcha_hash( $captcha_str )
		{
			return md5(strtolower($captcha_str));
		}

		/**
		 * check hash of password against hash of searched characters
		 *
		 * both txt file and img file are removed immediately
		 *
		 * @param string $char_seq
		 * @return boolean
		 */
		function verify( $char_seq )
		{
			// reads stored hash and image key
			$memcache = Memcached::get_instance('captcha');
			list($correct_hash, $img_key) = $memcache->get( $this->session_key );

			if (is_null($correct_hash) || empty($correct_hash))
			{
				return false;
			}

			// clear memcache captcha entry immediately
			$memcache->remove_item( $this->session_key );

			// delete image if it's present
			if (!empty($img_key))
			{
				@unlink( $this->get_image_path($img_key) );
			}

			// remove white spaces
			$char_seq = str_replace(' ', '', $char_seq);

			// returns true is verification matches
	      	return ($this->get_captcha_hash($char_seq) == $correct_hash);
		}
	}

?>