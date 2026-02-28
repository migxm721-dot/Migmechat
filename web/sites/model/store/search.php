<?php
	fast_require('StoreDAO', get_dao_directory() . '/store_dao.php');

	class SearchModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array('session_user', $model_data);

			$page = get_attribute_value('page', 'integer', 1);
			$type = get_attribute_value('type', 'integer', 0);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 10);
			$search = trim(get_value('search'));

			// Ensure That Search Word Is More Than 3 Characters
			if(strlen($search) < 3)
				return $data;

			$offset = ($page-1)*$number_of_entries;

			$dao = new StoreDAO();
			$results = $dao->search_items($search, $session_user, $type, $offset, $number_of_entries);

			$total_entries = $results['total_storeitems'];
			$number_of_pages = ceil($total_entries/$number_of_entries);

			$display_on_page = $offset + 1;
			$max_on_page = $offset + $number_of_entries;
			if($max_on_page > $total_entries)
				$max_on_page = $total_entries;

			$data = array();
			$data['storeitems'] = $results['storeitems'];
			$data['storeitems_page'] = $page;
			$data['storeitems_number_of_entries'] = $number_of_entries;
			$data['storeitems_display_on_page'] = $display_on_page;
			$data['storeitems_max_on_page'] = $max_on_page;
			$data['storeitems_total_pages'] = $number_of_pages;
			$data['storeitems_total_entries'] = $total_entries;
			$data['storeitems_search'] = $search;
			return $data;
		}
	}
?>