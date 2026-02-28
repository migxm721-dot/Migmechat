<?php
	fast_require('DateDifference', get_framework_common_directory() . '/date_time_difference.php');

	class Group
	{
		public $id;
		public $name;
		public $description;
		public $created_by;
		public $date_created;
		public $display_file_id;
		public $email;
		public $welcome_message;
		public $about;
		public $country_id;
		public $member_count;
		public $official;
		public $pending_invitation = 0;
		public $category;
		public $is_vip;
		public $type;
		public $is_featured;
		public $num_forumposts;
		public $likes = 0;
		public $num_photos;
		public $last_activity;

		public static $PUBLIC = 'OPEN';
		public static $PRIVATE = 'CLOSED';
		public static $UNLISTED = 'UNLISTED';
		public static $BY_APPROVAL = 'BY_APPROVAL';

		public function __construct($group_data)
		{
			$this->id = get_value_from_array('ID', $group_data, 'integer', 0);
			$this->name = htmlspecialchars_decode(clean_value(get_value_from_array('Name', $group_data)), ENT_QUOTES);
			$this->description = clean_value(get_value_from_array('Description', $group_data));
			$this->created_by = get_value_from_array('CreatedBy', $group_data);
			$this->email = get_value_from_array('EmailAddress', $group_data);
			$this->display_file_id = parse_display_picture(get_value_from_array('Picture', $group_data));
			$this->welcome_message = get_value_from_array('WelcomeMessage', $group_data);
			$this->date_created = get_value_from_array('DateCreated', $group_data);
			$this->country_id = get_value_from_array('CountryID', $group_data, 'integer', 0);
			$this->about = get_value_from_array('About', $group_data);
			$this->member_count = get_value_from_array('NumMembers', $group_data, 'integer', 0);
			$this->official = get_value_from_array('Official', $group_data, 'integer', 0);
			$this->is_vip = (get_value_from_array('SupportsVIPs', $group_data, 'integer', 0)==1);
			$this->is_featured = (get_value_from_array('Featured', $group_data, 'integer', 0)==1);
			$this->num_forumposts = get_value_from_array('NumForumPosts', $group_data, 'integer', 0);
			$this->num_photos = get_value_from_array('NumPhotos', $group_data, 'integer', 0);

			$this->type = $this->set_type(get_value_from_array('Type', $group_data, 'integer', -1));
		}

		public function is_official()
		{
			return $this->official == 1;
		}

		public function is_public()
		{
			return $this->type == Group::$PUBLIC || $this->type == Group::$UNLISTED;
		}

		public function is_by_approval()
		{
			return $this->type == Group::$BY_APPROVAL;
		}

		public function is_private()
		{
			return $this->type == Group::$PRIVATE;
		}

		public function get_type()
		{
			switch($this->type)
			{
				case Group::$PUBLIC:
				case Group::$UNLISTED:
					$type = 'PUBLIC';
					break;

				case Group::$PRIVATE:
					$type = 'PRIVATE';
					break;

				case Group::$BY_APPROVAL:
					$type = 'BY_APPROVAL';
					break;
			}

			return $type;
		}

		public function set_type($type)
		{
			$value = '';

			switch($type)
			{
				case 0:
					$value = Group::$PUBLIC;
					break;
				case 1:
					$value= Group::$PRIVATE;
					break;
				case 2:
					$value = Group::$UNLISTED;
					break;
				case 3:
					$value = Group::$BY_APPROVAL;
					break;
			}

			return $value;
		}

		public function get_type_name()
		{
			switch($this->type)
			{
				case Group::$PUBLIC:
				case Group::$UNLISTED:
					$type = 'Public';
					break;

				case Group::$PRIVATE:
					$type = 'Private';
					break;

				case Group::$BY_APPROVAL:
					$type = 'By Approval';
					break;
			}

			return $type;
		}

		public function official_badge($size = 14)
		{
			global $server_root;

			if($this->official)
			{
				switch (get_view())
				{
					case View::MIDLET:
					case View::MTK_MRE:
						return '<img src="'.$server_root.'/sites/resources/images/mig33-blue/badges/vip2_'.$size.'.png" height="'.$size.'" width="'.$size.'" vspace="2" hspace="2" style="float:left">&nbsp;';
						break;
					case View::WAP:
						return '<img src="'.$server_root.'/sites/resources/images/mig33-blue/badges/vip2_'.$size.'.png" height="'.$size.'" width="'.$size.'" />&nbsp;';
						break;
				default:
					return '<img src="'.$server_root.'/sites/resources/images/mig33-blue/badges/vip2_'.$size.'.png" height="'.$size.'" width="'.$size.'" />';
				}
			}
		}

		public function set_last_activity($timestamp){
			if(isset($timestamp)){
				$this->last_activity = intval($timestamp);
			}
		}

		public function get_last_activity_string()
		{
			if(isset($this->last_activity) && $this->last_activity > 0){
				$df = new DateDifference($this->last_activity, time());
				return $df->getDateTimeDifferenceString();
			}

			return '';
		}
	}
?>