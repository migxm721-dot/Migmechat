<?php
	fast_require("StoreDAO", get_dao_directory() . "/store_dao.php");

	class RatingModel extends Model
	{
		public function get_data($model_data)
		{
			$successes = $model_data['successes'];

			$dao = new StoreDAO();
			$session_user = get_value_from_array("session_user", $model_data);
			$id = get_value("siid", "integer", 0);
			$rating = get_value("rate", "integer", 0);

			$data = array();

			if($rating > 0)
			{
				$data = array_merge($data, $dao->rate_item($session_user, $id, $rating));
				$successes[] = _('Item rated successfully');
				$data['successes'] = $successes;

				return $data;
			}

		}
	}
?>