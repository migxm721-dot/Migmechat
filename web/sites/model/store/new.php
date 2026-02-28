<?php
	fast_require("StoreItem", get_domain_directory() . "/store/store_item.php");
	fast_require("StoreDAO", get_dao_directory() . "/store_dao.php");

	class NewModel extends Model
	{
		public function get_data($model_data)
		{
			$number_entries = get_attribute_value("number_of_entries", "integer", 10);
			$page = get_attribute_value("page", "integer", 1);
			$storeitem_type = get_attribute_value("type", "integer", 0);
			$session_user = get_value_from_array("session_user", $model_data);
			$session_user_detail = $model_data['session_user_detail'];
			if(empty($session_user_detail))
			{
				$currency = 'USD';
			}
			else
			{
				$currency = $session_user_detail->currency;
			}

			$dao = new StoreDAO();
			$storeitems_data = $dao->get_new_items($storeitem_type, $number_entries, $page, $session_user, $currency);
			$storeitemsratings = $dao->get_items_ratings($storeitems_data["storeitemsids"]);
			$data = array();
			$data['total_newitems_pages'] = get_value_from_array("total_pages", $storeitems_data, "integer", 0);
			$data['total_newitems_results'] = get_value_from_array("total_results", $storeitems_data, "integer", 0);

			$data['new_current_page'] = $page;
			$data['newitems'] = $storeitems_data["storeitems"];
			$data['newitemsratings'] = $storeitemsratings;
			return $data;
		}
	}
?>