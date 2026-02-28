<?php
	require_once(get_common_inc_location());
	fast_require("Photo", get_domain_directory() . "/photo.php");
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");

	class WallModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$username = get_value_from_array("username", $model_data);
			if( ! is_json_view() )
			{
				$page_number = get_attribute_value("page", "integer", get_attribute_value("p", "integer", 1));
				$number_of_entries = get_attribute_value("number_of_entries", "integer", 10);
			}
			else
			{
				$record = get_attribute_value("page", "integer", 1);
				$number_of_entries = get_attribute_value("number_of_entries", "integer", 8);
				$page_number = intval($record/$number_of_entries + 1);
			}

			$show_reported_only =get_attribute_value("reported_only", "integer", 0)==1;

			$content = $this->make_soap_call('getWall', array( $username, $page_number, $number_of_entries, $show_reported_only));

			$ar = array();
			$photos = array();

			$ar["page"] = get_value_from_array("page", $content->data, "integer", 1);

			foreach ($content->data['wall'] as $wall)
			{
				$photo = new Photo();
				$photo->set_photo_detail($wall);
				$photos[] = $photo;
			}

			$ar["photos"] = $photos;
			return $ar;
		}
	}
?>