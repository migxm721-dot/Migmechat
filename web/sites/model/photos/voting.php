<?php
	fast_require("PhotosDAO", get_dao_directory() . "/photos_dao.php");

	class VotingModel extends Model
	{
		public function get_data($model_data)
		{
			$dao = new PhotosDAO();
			$session_user = get_value_from_array("session_user", $model_data);
			$id = get_value("sbid", "integer", 0);
			$type = get_value("like", "integer", 0);
			return $dao->like_photo($session_user, $id, $type);
		}
	}
?>