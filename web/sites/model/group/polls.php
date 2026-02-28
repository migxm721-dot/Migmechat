<?php
	fast_require("GroupPollDao", get_dao_directory() . "/group_poll_dao.php");

	class PollsModel extends Model
	{
		public function get_data($model_data)
		{
			$group_id = 	get_attribute_value("group_id", "integer", 1);
			$page = get_attribute_value("page", "integer", 1);
			$number_of_entries = get_attribute_value("number_of_entries", "integer", 10);

			$offset = ($page-1)*$number_of_entries;

			$dao = new GroupPollDao();
			$polls = $dao->get_polls($group_id, $offset, $number_of_entries);

			$total_entries = $dao->get_poll_count($group_id);
			$number_of_pages = ceil($total_entries/$number_of_entries);

			$data = array();
			$data['polls'] = $polls;
			$data['polls_page'] = $page;
			$data['polls_number_of_entries'] = $number_of_entries;
			$data['polls_total_pages'] = $number_of_pages;
			$data['polls_total_entries'] = $total_entries;
			$data['group_id'] = $group_id;
			return $data;
		}
	}
?>