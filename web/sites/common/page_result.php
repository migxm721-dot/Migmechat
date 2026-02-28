<?php
	/**
	*
	* Class containing the page results
	*
	**/
	class PageResult
	{
		public $total_pages;
		public $total_results;
		public $current_page;

		public function __construct($total_pages, $total_results, $current_page)
		{
			$this->total_pages = $total_pages;
			settype($this->total_pages, "integer");

			$this->total_results = $total_results;
			settype($this->total_results, "integer");

			$this->current_page = $current_page;
			settype($this->current_page, "integer");
		}
	}
?>