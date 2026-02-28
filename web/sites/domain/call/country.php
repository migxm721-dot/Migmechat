<?php
	class Country
	{
		//TODO: Add more as needed
		public $name;
		public $iddcode;

		public function __construct($data)
		{
			$this->name = get_value_from_array("name", $data);
			$this->iddcode = get_value_from_array("iddCode", $data);
		}
	}
?>