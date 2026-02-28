<?php
	fast_require("AvatarDAO", get_dao_directory() . "/avatar_dao.php");

	class RatingModel extends Model
	{
		public function get_data($model_data)
		{
			$dao = new AvatarDAO();
			$session_user = get_value_from_array("session_user", $model_data);
			$id = get_value("aid", "integer", 0);
			$rating = get_value("rate", "integer", 0);
			if($rating > 0)
				return $dao->rate_avatar($session_user, $id, $rating);
		}
	}
?>