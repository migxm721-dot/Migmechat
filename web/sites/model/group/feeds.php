<?php
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class FeedsModel extends Model
	{
		public function get_data($model_data)
		{
			$group = $model_data["group"];
			$master = get_value_from_array("master", $model_data, "boolean", false);

			$dao = new GroupDAO();

			$data = array("feeds"=>$dao->get_group_rss($group->id, $master));
			return $data;
		}
	}
?>