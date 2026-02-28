<?php
	/**
	*
	* Model abstract class
	*
	**/
	abstract class Model
	{
		public $initialised = false;
		protected $base_directory = "";

		/**
		*
		* constructor
		*
		**/
		public function __construct($initialise=false)
		{
			if( $this->initialised == false && $initialise)
			{
				$this->initialise();
				$this->initialised = true;
			}

		}

		/**
		*
		* Initialiser method
		*
		**/
		function initialise()
		{
		}

		/**
		*
		* Get the data for the view
		*
		**/
		abstract function get_data($data);
	}
?>
