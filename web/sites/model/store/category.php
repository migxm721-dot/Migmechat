<?php
	fast_require("StoreCategory", get_domain_directory() . "/store/store_category.php");
	fast_require("StoreDAO", get_dao_directory() . "/store_dao.php");

	class CategoryModel extends Model
	{
		public function get_data($model_data)
		{
			$category_id = get_attribute_value("category_id", "integer", 0);

			$dao = new StoreDAO();
			$category_data = $dao->get_category($category_id);
			$parent_category_data = $dao->get_parent_category($category_data->parent_id);
			$data = array();
			$data['category'] = $category_data;
			$data['parent_category'] = $parent_category_data;

			return $data;
		}
	}
?>