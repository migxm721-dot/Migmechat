<?php
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class FeedModel extends Model
	{
		public function get_data($model_data)
		{
			$feed_id = get_attribute_value("feed_id", "integer", 0);

			$dao = new GroupDAO();

			return array("feed"=>$dao->get_rss_detail($feed_id));
		}
	}
?>