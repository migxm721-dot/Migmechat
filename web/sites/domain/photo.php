<?php
	fast_require('DateDifference', get_framework_common_directory() . '/date_time_difference.php');
	/**
	*
	* Domain Object for photo
	*
	**/
	class Photo
	{
		public $image_id;
		public $description;
		public $date_created;
		public $date_created_since;
		public $item_id;
		public $width;
		public $height;
		public $status;
		public $status_name;
		public $status_code;
		public $received_from;
		public $uploaded_by;

		public function __construct($data)
		{
			$description = get_value_from_array('description', $data);
			$this->image_id = get_value_from_array('fileid', $data);
			$this->date_created = get_value_from_array('scrapbookdatecreated', $data);
			$df = new DateDifference(strtotime($this->date_created), time());
			$this->date_created_since = $df->getDateTimeDifferenceString();
			$this->description = empty($description) ? date('d-M-Y', strtotime($this->date_created)) : strip_tags($description);
			$this->description = str_replace(':', ' ', $this->description);
			$this->item_id = get_value_from_array('id', $data, 'integer', 0);
			$this->width = get_value_from_array('width', $data, 'integer', 0);
			$this->height = get_value_from_array('height', $data, 'integer', 0);
			$this->status = get_value_from_array('status', $data, 'integer', 0);
			$this->status_name = $this->get_status($this->status);
			$this->status_code = $this->get_status_code($this->status);
			$this->received_from = get_value_from_array('receivedfrom', $data);
			$this->uploaded_by = get_value_from_array('uploadedby', $data);
		}

		protected function get_status($status)
		{
			//INACTIVE(0), PRIVATE(1), PUBLIC(2), CONTACTS_ONLY(3), REPORTED(4);
			$status_name = '';
			switch($status)
			{
				case 0:
					$status_name = _('Deleted');
					break;
				case 1:
					$status_name = _('Myself');
					break;
				case 2:
				case 4:
					$status_name = _('Everyone');
					break;
				case 3:
					$status_name = _('Friends');
					break;
			}
			return $status_name;
		}

		protected function get_status_code($status)
		{
			//INACTIVE(0), PRIVATE(1), PUBLIC(2), CONTACTS_ONLY(3), REPORTED(4);
			$status_code = '';
			switch($status)
			{
				case 0:
					$status_code = 'INACTIVE';
					break;
				case 1:
					$status_code = 'PRIVATE';
					break;
				case 2:
				case 4:
					$status_code = 'PUBLIC';
					break;
				case 3:
					$status_code = 'CONTACTS_ONLY';
					break;
			}
			return $status_code;
		}
	}
?>