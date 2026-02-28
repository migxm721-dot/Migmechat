<?php
	class Agreement
	{
		public	$id,
				$partner_id,
				$name,
				$date_created,
				$finder_fee,
				$revenue_share,
				$product_sms,
				$product_voice,
				$product_games,
				$product_vg,
				$product_others,
				$start_date,
				$end_date;

		public function __construct($data)
		{
			$this->id = get_value_from_array('ID', $data);
			$this->partner_id = get_value_from_array('PartnerID', $data);
			$this->name = get_value_from_array('Name', $data);
			$this->finder_fee = get_value_from_array('FinderFee', $data);
			$this->revenue_share = get_value_from_array('RevenueShare', $data);
			$this->product_sms = get_value_from_array('ProductSMS', $data);
			$this->product_voice = get_value_from_array('ProductVoice', $data);
			$this->product_games = get_value_from_array('ProductGames', $data);
			$this->product_vg = get_value_from_array('ProductVG', $data);
			$this->product_others = get_value_from_array('ProductOthers', $data);
			$this->start_date = get_value_from_array('StartDate', $data);
			$this->end_date = get_value_from_array('EndDate', $data);
			$this->date_created = get_value_from_array('DateCreated', $data);
		}

		public function start_date()
		{
			return date("d M Y", strtotime($this->start_date));
		}

		public function end_date()
		{
			return date("d M Y", strtotime($this->end_date));
		}
	}
?>