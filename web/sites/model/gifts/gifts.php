<?php
	fast_require('VirtualGiftDao', get_dao_directory() . '/virtual_gift_dao.php');

	class GiftsModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = $model_data['session_user'];
			$username = get_attribute_value('username', 'string', $session_user);
			$page = get_attribute_value('page', 'integer', 1);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 10);
			$can_view_gifts_module = get_value_from_array('can_view_gifts_module', $model_data, 'boolean', false);

			try
			{
				if(empty($username))
					throw new Exception(_('Username is empty'));

				if(empty($session_user))
				{
					fast_require('UserDAO', get_dao_directory() . '/user_dao.php');
					$user_dao = new UserDAO();
					$user_profile = $user_dao->get_user_profile('', $username);

					if(!empty($user_profile))
					{
						if($user_profile->is_private_profile())
							throw new Exception(_('User profile is private'));
					}
					else
					{
						throw new Exception(_('Error loading user profile'));
					}
				}

				$dao = new VirtualGiftDao();
				$data = $dao->get_virtual_gifts_received($session_user, $username, $page, $number_of_entries);
				$data['page'] = $page;
				return $data;
			}
			catch(Exception $ex)
			{
				$data['gifts_received'] = array();
				return $data;
			}
		}
	}
?>