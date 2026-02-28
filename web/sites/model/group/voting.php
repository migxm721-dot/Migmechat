<?php
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class VotingModel extends Model
	{
		public function get_data($model_data)
		{
			$dao = new GroupDAO();
			$session_user = get_value_from_array("session_user", $model_data);
			$group_id = get_attribute_value("group_id", "integer", 0);
			return $dao->like_group($session_user, $group_id, 1);
		}
	}
?>