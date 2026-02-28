<?php
	require_once(get_common_inc_location());

	class PhotosModel extends Model
	{
		public function get_data($model_data)
		{
			global $mogileFSImagePath;

			$session_user = $model_data['session_user'];
			$username = get_attribute_value("username");

			if( empty($username) )
				$username = $session_user;

			$data = array();
			$data['session_user'] = $session_user;
			$data['username'] = $username;
			$data['mogile_path'] = $mogileFSImagePath;
			return $data;
		}
	}
?>