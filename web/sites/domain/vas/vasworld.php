<?php
	class VASWorld
	{
		public	$id,
				//$partner_id,
				$agreement_id,
				$agreement,
				$name,
				$content,
				$status,
				$remarks,
				$date_created,
				$date_updated;

		public static $STATUS_DRAFT = 0;
		public static $STATUS_PENDING = 1;
		public static $STATUS_REJECTED = 2;
		public static $STATUS_CANCELLED = 3;
		public static $STATUS_WITHDREW = 4;
		public static $STATUS_APPROVED = 5;
		public static $STATUS_PUBLISHED = 6;

		public function __construct($data)
		{
			$this->id = get_value_from_array('ID', $data);
			//$this->partner_id = get_value_from_array('PartnerID', $data);
			$this->agreement_id = get_value_from_array('AgreementID', $data);
			$this->name = get_value_from_array('Name', $data);
			$this->content = get_value_from_array('Content', $data);
			$this->status = get_value_from_array('Status', $data);
			$this->remarks = get_value_from_array('Remarks', $data);
			$this->date_created = get_value_from_array('DateCreated', $data);
			$this->date_updated = get_value_from_array('DateUpdated', $data);
			$this->agreement = null;
		}

		public function status()
		{
			switch($this->status)
			{
				case self::$STATUS_DRAFT: $status = 'Draft'; break;
				case self::$STATUS_WITHDREW: $status = 'Withdrawn'; break;
				case self::$STATUS_CANCELLED: $status = 'Cancelled'; break;
				case self::$STATUS_PENDING: $status = 'Pending Approval'; break;
				case self::$STATUS_REJECTED: $status = 'Rejected'; break;
				case self::$STATUS_APPROVED: $status = 'Approved'; break;
				case self::$STATUS_PUBLISHED: $status = 'Published'; break;
			}

			return $status;
		}

		public function next_action()
		{
			switch($this->status)
			{
				case self::$STATUS_DRAFT: $action = 'Submit'; break;
				case self::$STATUS_WITHDREW: $action = null; break;
				case self::$STATUS_CANCELLED: $action = null; break;
				case self::$STATUS_PENDING: $action = 'Withdraw'; break;
				case self::$STATUS_REJECTED: $action = null; break;
				case self::$STATUS_APPROVED: $action = 'Publish'; break;
				case self::$STATUS_PUBLISHED: $action = null; break;
			}

			return $action;
		}

		public function date_submitted()
		{
			return date("d M Y g:i (A)", strtotime($this->date_created));
		}

		public function date_updated()
		{
			return date("d M Y g:i (A)", strtotime($this->date_updated));
		}
	}
?>