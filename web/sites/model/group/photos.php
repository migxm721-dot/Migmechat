<?php
	fast_require('GroupPhotoDao', get_dao_directory() . '/group_photo_dao.php');

	class PhotosModel extends Model
	{
		public function get_data($model_data)
		{
			$group_id = 	get_attribute_value('group_id', 'integer', 1);
			$page = get_attribute_value('page', 'integer', 1);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 10);
			$offset = ($page - 1) * $number_of_entries;

			$dao = new GroupPhotoDao();
			$photos = $dao->get_photos($group_id, $offset, $number_of_entries);
			$total_entries = $dao->get_photo_count($group_id);

			$number_of_pages = ceil($total_entries/$number_of_entries);

			$data = array();
			$data['photos'] = $photos;
			$data['photos_page'] = $page;
			$data['photos_number_of_entries'] = $number_of_entries;
			$data['photos_total_pages'] = $number_of_pages;
			$data['photos_total_entries'] = $total_entries;
			$data['group_id'] = $group_id;
			return $data;
		}
	}
?>