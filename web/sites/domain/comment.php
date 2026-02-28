<?php
	fast_require("DateDifference", get_framework_common_directory() . "/date_time_difference.php");
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");

	abstract class Comment
	{

		//Only the common attribute related to Comment in general
		public $id;
		public $userid;
		public $username;
		public $date_created;
		public $date_created_since;
		public $comment;
		public $status;

		public function __construct($data)
		{
			$this->id = get_value_from_array("ID", $data, "integer", 0);

			$this->userid = get_value_from_array("UserID", $data, "integer", 0);
			$this->username = get_value_from_array("Username", $data);
			$this->date_created = get_value_from_array("DateCreated", $data);

			$df = new DateDifference(strtotime($this->date_created), time());
			$this->date_created_since = $df->getDateTimeDifferenceString();

			$this->comment = strip_tags(get_value_from_array("Comment", $data));
			$this->status = get_value_from_array("Status", $data, "integer", 0);
		}

        public function get_display_picture_url()
		{
			global $mogileFSImagePath;
			return $mogileFSImagePath . "/u/" . $this->userid;
		}

        protected function get_avatar_picture()
		{
			if( $this->avatar_dao == null )
				$this->avatar_dao = new AvatarDAO();

			$uuid = $this->avatar_dao->get_user_avatar_body_uuid($this->author_username);
			$head_key = get_value_from_array("head_key", $uuid);
			return $head_key;
		}
	}
?>