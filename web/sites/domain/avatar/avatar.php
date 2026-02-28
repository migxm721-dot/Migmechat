<?php
	class Avatar
	{
		public $body;
		public $items;

		public function __construct($body, $items)
		{
			$this->body = $body;
			$this->items = $items;
		}
	}
?>