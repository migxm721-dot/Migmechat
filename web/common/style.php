<?php

	class Style
	{
		private $useTheme = false;
		private $backgroundColor;
		private $foregroundColor;
		private $isBlock = true;

		public function __construct($foregroundColor="", $backgroundColor="")
		{
			$this->foregroundColor = $foregroundColor;
			$this->backgroundColor = $backgroundColor;
		}

		/**
		* Set the background color
		**/
		public function setBackgroundColor($color)
		{
			$this->backgroundColor = $color;
		}

		/**
		* Set the foreground color
		**/
		public function setForegroundColor($color)
		{
			$this->foregroundColor = $color;
		}

		/**
		* Enable themes
		**/
		public function enableThemes()
		{
			$this->useTheme = true;
		}

		/**
		* Disable themes
		**/
		public function disableThemes()
		{
			$this->useTheme = false;
		}

		public function setDisplayBlock()
		{
			$this->isBlock = true;
		}

		public function setDisplayInline()
		{
			$this->isBlock = false;
		}

		/**
		* Show the style tag
		**/
		public function show()
		{
			printf("<style %s>", ($this->useTheme)?("midletTheme"):"");
			printf("</style>");
		}

		/**
		* Show the style as an attribute
		**/
		public function showAsAttribute()
		{
			$showColon = false;
			$styleString = 'style="';
			if( !empty($this->backgroundColor) )
			{
				$styleString = sprintf("%sbackground-color:%s", $styleString, $this->backgroundColor);
				$showColon = true;
			}

			if( !empty($this->foregroundColor) )
			{
				if($showColon) $styleString = sprintf("%s; ", $styleString);
				$styleString = sprintf("%scolor:%s", $styleString, $this->foregroundColor);
				$showColon = true;
			}
			$styleString = sprintf('%s; display:%s"', $styleString, $this->isBlock?"block":"inline");
			if( $showColon ) return $styleString;
			return "";
		}
	}
?>