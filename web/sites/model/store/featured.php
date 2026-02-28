<?php
	fast_require("StoreItem", get_domain_directory() . "/store/store_item.php");
	fast_require("StoreDAO", get_dao_directory() . "/store_dao.php");

	class FeaturedModel extends Model
	{
		public function get_data($model_data)
		{
			$category_id = get_attribute_value("category_id");
			$session_user = get_value_from_array("session_user", $model_data);

			$number_entries = get_attribute_value("number_of_entries", "integer", 8);
			$page = get_attribute_value("page", "integer", 1);
			$storeitem_type = get_attribute_value("type", "integer", 0);

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
			$categories = $dao->get_categories($category_id, 20, 1, false);
			$storeitems_data = $dao->get_featured_items($storeitem_type, $number_entries, $page, $session_user, $currency);
			$storeitemsratings = $dao->get_items_ratings($storeitems_data["storeitemsids"]);
			$data = array();
			$data['total_featureditems_pages'] = get_value_from_array("total_pages", $storeitems_data, "integer", 0);
			$data['total_featureditems_results'] = get_value_from_array("total_results", $storeitems_data, "integer", 0);
			$data['featured_current_page'] = $page;
			$data['featureditems'] = $storeitems_data["storeitems"];
			$data['featureditemsratings'] = $storeitemsratings;

			// list view is now showing 'featured' items, so a quick hack is to move it to the browse item.
			$_GET['e'] = 5; // hack to get browse more
			$data['total_items_pages'] = get_value_from_array("total_pages", $storeitems_data, "integer", 0);
			$data['total_items_results'] = 10;
			$data['current_page'] = $page;
			$data['storeitems'] = $storeitems_data["storeitems"];
			$data['storeitemsratings'] = $storeitemsratings;
			$data['categories']['categories'] = $categories['categories'];

			return $data;
		}
	}
?>