<?php
	abstract class Forum
	{
		public $id;
		public $num_posts;
		public $num_comments;
		public $num_likes;
		public $num_dislikes;

		public $date_created;
		public $date_created_since;

		public $last_updated;
		public $last_updated_since;

		public function __construct($forum_data)
		{
			$this->id = get_value_from_array("ID", $forum_data, "integer", 0);
			$this->num_posts = get_value_from_array("NumPosts", $forum_data, "integer", 0);
			$this->num_comments = get_value_from_array("NumComments", $forum_data, "integer", 0);
			$this->num_likes = get_value_from_array("NumLikes", $forum_data, "integer", 0);
			$this->num_dislikes = get_value_from_array("NumDislikes", $forum_data, "integer", 0);

			$this->date_created = get_value_from_array("DateCreated", $forum_data);
			$df = new DateDifference(strtotime($this->date_created), time());
			$this->date_created_since = $df->getDateTimeDifferenceString();

			$this->last_updated = get_value_from_array("LastUpdated", $forum_data);
			$df = new DateDifference(strtotime($this->last_updated), time());
			$this->last_updated_since = $df->getDateTimeDifferenceString();
		}
	}
?>