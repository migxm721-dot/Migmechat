<?php
	fast_require("DateDifference", get_framework_common_directory() . "/date_time_difference.php");
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");

	abstract class Post
	{
		public $id;
		public $author_userid;
		public $author_username;
		public $date_created;
		public $date_created_since;
		public $body;
		public $num_comments;
		public $num_likes;
		public $num_dislikes;
		public $status;
		public $last_updated;
		public $last_updated_since;

		protected static $avatar_dao;

		public function __construct($data)
		{
			$this->id = get_value_from_array("ID", $data, "integer", 0);
			$this->author_userid = get_value_from_array("AuthorUserID", $data, "integer", 0);
			$this->author_username = get_value_from_array("AuthorUsername", $data);

			$this->date_created = get_value_from_array("DateCreated", $data);

			$df = new DateDifference(strtotime($this->date_created), time());
			$this->date_created_since = $df->getDateTimeDifferenceString();

			//$this->body = strip_tags(get_value_from_array("Body", $data));
			$this->body = get_value_from_array("Body", $data);
			$this->num_comments = get_value_from_array("NumComments", $data, "integer", 0);
			$this->num_likes = get_value_from_array("NumLikes", $data, "integer", 0);
			$this->num_dislikes = get_value_from_array("NumDislikes", $data, "integer", 0);
			$this->status = get_value_from_array("Status", $data, "integer", 1);

			$this->last_updated = get_value_from_array("LastUpdated", $data);

			$df = new DateDifference(strtotime($this->last_updated), time());
			$this->last_updated_since = $df->getDateTimeDifferenceString();
		}

		public function get_display_picture_url()
		{
			global $mogileFSImagePath;
			return $mogileFSImagePath . "/u/" . $this->author_userid;
		}
	}
?>