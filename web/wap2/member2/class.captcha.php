<?php

	/*
		Jax Captcha Class v1.o1 - Copyright (c) 2005, Andreas John aka Jack (tR)
		This program and it's moduls are Open Source in terms of General Public License (GPL) v2.0

		class.captcha.php 		(captcha class module)

		Last modification: 2005-09-05
	*/

	class captcha
	{
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
			$this->session_key = $session_key;
			$this->temp_dir    = $temp_dir;
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
     		imagefilledrectangle($img, 0, 0, $this->width, $this->height, imagecolorallocate( $img, 0, 0, 0 ));
			imagealphablending($img, 1);
			//imagecolortransparent( $img );

			// generate background of randomly built ellipses
			for ($i=1; $i<=15; $i++)
			{
				$r = round( rand( 0, 100 ) );
				$g = round( rand( 0, 100 ) );
				$b = round( rand( 0, 100 ) );
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
        		//$r = $color;
				//$g = $color;
				//$b = $color;
				$r = round( rand( 200, 255 ) );
				$g = round( rand( 200, 255 ) );
				$b = round( rand( 200, 255 ) );
				$y_pos = ($this->height/2)+round( rand( 5, 20 ) );

				$fontsize = round( rand( 18, $max_font_size) );
				$color = imagecolorallocate( $img, $r, $g, $b);
				$presign = round( rand( 0, 1 ) );
				$angle = round( rand( 0, 10 ) );
				if ($presign==true) $angle = -1*$angle;

				ImageTTFText( $img, $fontsize, $angle, $start_x+$i*$max_x_ofs, $y_pos, $color, $_SERVER['DOCUMENT_ROOT'].'/common/captcha/arial.ttf', substr($char_seq,$i,1) );
			}

			// create image file
			imagegif( $img, $location);
      		flush();
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

			// generate a picture file that displays the random string
			if ( $this->_generate_image( $this->temp_dir.'/'.'cap_'.md5( strtolower( $captcha_str )).'.gif' , $captcha_str ) )
			{
				//consoleOut('Generating capcha (' . $captcha_str . ') with session key:' . $this->session_key);
				$fh = fopen( $this->temp_dir.'/'.'cap_'.$this->session_key.'.txt', "w" );
				fputs( $fh, md5( strtolower( $captcha_str ) ) );
				return( md5( strtolower( $captcha_str ) ) );
			}
			else
			{
				return false;
			}
		}

		/**
		 * check hash of password against hash of searched characters
		 *
		 * @param string $char_seq
		 * @return boolean
		 */
		function verify( $char_seq )
		{
			//Remove spaces from the captcha
			$char_seq = str_replace(' ', '', $char_seq);

			//consoleOut('Verifying capcha (' . $char_seq . ') with session key:' . $this->session_key);
			//consoleOut('Hash for capcha:' . md5(strtolower($char_seq)));
			if(!file_exists($this->temp_dir.'/'.'cap_'.$this->session_key.'.txt'))
			{
				//Debug:
				//print 'file does not exist!';
				//print $this->temp_dir.'/'.'cap_'.$this->session_key.'.txt';
				//die();
        		return false;
			}

	      	$fh = fopen( $this->temp_dir.'/'.'cap_'.$this->session_key.'.txt', "r" );
			$hash = fgets( $fh );

	      	fclose($fh);

	      	unlink($this->temp_dir.'/'.'cap_'.$this->session_key.'.txt');
	      	//Check the Captcha. It either verifies or fails. Either way, close the file and remove the Catpcha TXT and IMG
	      	if (md5(strtolower($char_seq)) == $hash)
	      	{
	      		return true;
	      	}
	      	else
	      	{
	      		//Debug:
	      		//print 'Capcha Debug: char_seq=' . $char_seq . ' hash=' . $hash;
	      		//print ' char_hash=' . md5(strtolower($char_seq));
	      		//die();
	      		return false;
	      	}
		}
	}


?>