<?php

	class Style
	{
		private $use_theme = false;
		private $background_color;
		private $foreground_color;
		private $is_block = true;

		public function __construct($foreground_color="", $background_color="")
		{
			$this->foreground_color = $foreground_color;
			$this->background_color = $background_color;
		}

		/**
		* Set the background color
		**/
		public function set_background_color($color)
		{
			$this->background_color = $color;
		}

		/**
		* Set the foreground color
		**/
		public function set_forground_color($color)
		{
			$this->foreground_color = $color;
		}

		/**
		* Enable themes
		**/
		public function enable_themes()
		{
			$this->use_theme = true;
		}

		/**
		* Disable themes
		**/
		public function disable_themes()
		{
			$this->use_theme = false;
		}

		public function set_display_block()
		{
			$this->is_block = true;
		}

		public function set_display_inline()
		{
			$this->is_block = false;
		}

		/**
		* Show the style tag
		**/
		public function show()
		{
			printf("<style %s>", ($this->use_theme)?("midletTheme"):"");
			printf("</style>");
		}

		/**
		* Show the style as an attribute
		**/
		public function show_as_attribute()
		{
			$showColon = false;
			$styleString = 'style="';
			if( !empty($this->background_color) )
			{
				$styleString = sprintf("%sbackground-color:%s", $styleString, $this->background_color);
				$showColon = true;
			}

			if( !empty($this->foreground_color) )
			{
				if($showColon) $styleString = sprintf("%s; ", $styleString);
				$styleString = sprintf("%scolor:%s", $styleString, $this->foreground_color);
				$showColon = true;
			}
			$styleString = sprintf('%s; display:%s"', $styleString, $this->is_block?"block":"inline");
			if( $showColon ) return $styleString;
			return "";
		}
	}
?>