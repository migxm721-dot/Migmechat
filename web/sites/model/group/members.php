<?php
	fast_require("GroupMember", get_domain_directory() . "/group/group_member.php");
	fast_require("GroupDAO", get_dao_directory() . "/group_dao.php");

	class MembersModel extends Model
	{
		public function get_data($model_data)
		{
			$search = strip_tags(trim(get_attribute_value('search')));
			$group = $model_data['group'];
			$offset = 0;
			$mdata = array();
			$group_members = array();
			$group_members_count = 0;

			if( is_json_view())
			{
				$page = get_attribute_value("page", "integer", 1);
				$offset = $page;
				$number_of_entries = get_attribute_value("number_of_entries", "integer", 8);
			}
			else
			{
				$number_of_entries = get_attribute_value("number_of_entries", 'integer', 10);
				$page = get_attribute_value('page', 'integer', get_value_from_array("page", $model_data, "integer", 1));
				$offset = ($page-1)*$number_of_entries;
			}

			$dao = new GroupDAO();
			if(empty($search))
			{
				$group_members = $dao->get_group_members($group->id, $offset, $number_of_entries);
				$group_moderators = $dao->get_moderators($group->id);
				$group_members_count = $group->member_count;
			}
			elseif(!empty($search) && strlen($search) < 4)
			{
				$mdata['error'] = sprintf(_('Search term must be longer than %s characters'), 3);
			}
			else
			{
				$results = $dao->search_group_members($group->id, $search, $offset, $number_of_entries);
				$group_members = $results['members'];
				$group_members_count = $results['members_count'];
			}

			$mdata['members_total_pages'] = ceil($group_members_count/$number_of_entries);
			$mdata['members_total_results'] = $group_members_count;
			$mdata['members'] = $group_members;
			$mdata['members_page'] = $page;
			$mdata['members_search'] = $search;

			$mdata['moderators'] = $group_moderators;

			return $mdata;
		}
	}
?>