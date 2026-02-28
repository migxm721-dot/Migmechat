<?php
	fast_require("GroupPhotoDao", get_dao_directory() . "/group_photo_dao.php");

	class PhotoModel extends Model
	{
		public function get_data($model_data)
		{
			$group_id = 	get_attribute_value("group_id", "integer", 1);
			$photo_id = get_attribute_value("photo_id", "integer", 0);

			$dao = new GroupPhotoDao();
			$photo = $dao->get_photo($group_id, $photo_id);
			$model_data['photo'] = $photo;
			return $model_data;
		}
	}
?>