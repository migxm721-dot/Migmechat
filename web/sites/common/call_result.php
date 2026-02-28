<?php
	class CallResult
	{
		public $status=1;
		public $message;
		public $data;

		public function set_error()
		{
			$this->status = 0;
		}

		public function is_error()
		{
			return ($this->status == 0);
		}

		//Use at your own risk. since not all ejb calls return consistently
		public function is_data_empty()
		{
			return empty($this->data);
		}
	}
?>