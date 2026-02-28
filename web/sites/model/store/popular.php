<?php
	fast_require("StoreItem", get_domain_directory() . "/store/store_item.php");
	fast_require("StoreDAO", get_dao_directory() . "/store_dao.php");

	class PopularModel extends Model
	{
		public function get_data($model_data)
		{
			$side_bar_mode = get_value_from_array("side_bar_mode", $model_data, "bool", false);
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
			$storeitems_data = $dao->get_popular_items($storeitem_type, $number_entries, $page, $session_user, $currency);
			$storeitemsratings = $dao->get_items_ratings($storeitems_data["storeitemsids"]);
			$data = array();
			$data['total_popularitems_pages'] = get_value_from_array("total_pages", $storeitems_data, "integer", 0);
			$data['total_popularitems_results'] = get_value_from_array("total_results", $storeitems_data, "integer", 0);

			$data['popular_current_page'] = $page;
			$data['popularitems'] = $storeitems_data["storeitems"];
			$data['popularitemsratings'] = $storeitemsratings;
			return $data;
		}
	}
?>