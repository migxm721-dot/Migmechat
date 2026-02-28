<?php
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class SearchModel extends Model
	{
		public function get_data($model_data)
		{
			$search_string = trim(get_attribute_value("name"));
			$last_group_id = get_attribute_value("group_id", 'integer', 0);
			$num_entries = get_attribute_value("number_of_entries", "integer", 8);
			$sort_by = get_attribute_value("sort_by", "string", "datecreated");
			$sort_order = get_attribute_value("sort_order", "string", "desc");

			$sort_by_array = array('name', 'datecreated', 'nummembers','numforumposts');
			$sort_order_array = array('asc', 'desc');

			// Valid Sort By
			if(!in_array($sort_by, $sort_by_array))
			{
				$sort_by = 'datecreated';
			}

			// Valid Sort Order
			if(!in_array($sort_order, $sort_order_array))
			{
				$sort_order = 'desc';
			}

			// Not Empty Search String
			if(!empty($search_string))
			{
				$dao = new GroupDAO();
				$result = $dao->search_groups($search_string, $num_entries, $last_group_id, $sort_by, $sort_order);

				//Set the search criterias
				$result['name'] = $search_string;
				$result['group_id'] = $last_group_id;
			}
			return $result;
		}
	}
?>