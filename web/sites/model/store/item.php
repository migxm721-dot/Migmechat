<?php
	fast_require("StoreItem", get_domain_directory() . "/store/store_item.php");
	fast_require("StoreDAO", get_dao_directory() . "/store_dao.php");

	class ItemModel extends Model
	{
		public function get_data($model_data)
		{

			$session_user = get_value_from_array("session_user", $model_data);
			$storeitem_id = get_attribute_value("item_id", "integer", 0);

			$dao = new StoreDAO();
			$storeitem_data = $dao->get_item($storeitem_id, $session_user);
			$storeitem_ratings = $dao->get_item_ratings($storeitem_id);

			$data = array();
			$data['storeitem'] = $storeitem_data;
			$data['storeitemratings'] = $storeitem_ratings;
			return $data;
		}
	}
?>