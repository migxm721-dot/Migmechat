<?php
	class Rate
	{
		public $id;
		public $idd_code;
		public $call_signalling_fee;
		public $mobile_signalling_fee;
		public $mobile_rate;
		public $country;
		public $call_rate;

		public function __construct($data)
		{
			$this->id = get_value_from_array("id", $data, "integer", 0);
			$this->idd_code = get_value_from_array("iddcode", $data, "integer", 0);
			$this->call_signalling_fee = get_value_from_array("callSignallingFee", $data, "float", 0);
			$this->mobile_signalling_fee = get_value_from_array("mobileSignallingFee", $data, "float", 0);
			$this->mobile_rate = get_value_from_array("mobileRate", $data, "float", 0);
			$this->country = get_value_from_array("country", $data);
			$this->call_rate = get_value_from_array("callRate", $data, "float", 0);
		}
	}
?>