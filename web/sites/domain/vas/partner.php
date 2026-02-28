<?php
	class Partner
	{
		public	$id,
				$name,
				$date_created;

		public function __construct($data)
		{
			$this->id = get_value_from_array('ID', $data);
			$this->name = get_value_from_array('Name', $data);
			$this->date_created = get_value_from_array('DateCreated', $data);
		}
	}
?>