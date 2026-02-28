<?php
	fast_require('GroupDAO', get_dao_directory() . '/group_dao.php');
	fast_require('ChatroomDAO', get_dao_directory() . '/chatroom_dao.php');
	fast_require('Memcached', get_framework_common_directory() . '/memcached.php');
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');

	class StatsModel extends Model
	{
		public function get_data($model_data)
		{
			$numFriendProxies = 0;
			$key = 'Stats/Common';

			$memcached = Memcached::get_instance();
			$stats = $memcached->get($key);

			if ($stats == false)
			{
				$ice = new IceDAO();
				try
				{
					//$numFriendProxies = $ice->get_online_contacts_count();
					$result = $ice->get_stats();
				}
				catch (Exception $e)
				{
				}

				$stats['numUserProxies'] = $result->numUserProxies;
				//$stats['numFriendProxies'] = $numFriendProxies;

				$memcached->add_or_update($key, $stats, 60 * 60);
			}

			$chatroom_dao = new ChatroomDAO();
			$total_chatrooms = $chatroom_dao->get_total_chatrooms_count();
			$stats['total_chatrooms'] = $total_chatrooms;

			$group_dao = new GroupDAO();
			$total_groups = $group_dao->get_total_groups_count();
			$stats['total_groups'] = $total_groups;

			return array('stats' => $stats);
		}
	}
?>