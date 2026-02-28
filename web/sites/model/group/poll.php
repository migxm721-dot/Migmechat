<?php
	fast_require("GroupPollDao", get_dao_directory() . "/group_poll_dao.php");

	class PollModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$session_userid = get_value_from_array("session_user_id", $model_data, "integer", 0);
			$group_id = 	get_attribute_value("group_id", "integer", 1);
			$poll_id = get_attribute_value("poll_id", "integer", 0);
			if(!$poll_id)
				$poll_id = get_value_from_array("poll_id", $model_data, "integer", 0);

			$dao = new GroupPollDao();
			$poll = $dao->get_poll($group_id, $poll_id);
			$model_data['poll'] = $poll;

			return $model_data;
		}
	}
?>