<?php
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class AddFeedModel extends Model
	{
		public function get_data($model_data)
		{
			$group = $model_data["group"];
			$url = get_attribute_value("url");

			$dao = new GroupDAO();

			$dao->add_group_rss($group->id, $url);

			return array("master"=>true, "success" => _('Group feed has been added'));
		}
	}
?>