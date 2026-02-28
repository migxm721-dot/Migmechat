<?php
	fast_require('UserDAO', get_dao_directory() . '/user_dao.php');
	fast_require('AvatarDAO', get_dao_directory() . '/avatar_dao.php');
	fast_require('MogilefsDAO', get_dao_directory() . '/mogilefs_dao.php');

	class AvatarModel extends Model
	{
		public function get_data($model_data)
		{
			global $mogileFSImagePath, $server_root;
			$dao = new UserDAO();
			$session_user = get_value_from_array('session_user', $model_data);
			$username = get_attribute_value('username', 'string', $session_user);
			$user_id = $dao->get_user_id($username);

			$user_level = $dao->get_user_level($username);

			$avatar_dao = new AvatarDAO();
			$user_avatar_created = $avatar_dao->user_has_avatar($user_id);

			if ($user_avatar_created)
			{
				$uuid = $avatar_dao->get_user_avatar_body_uuid_by_user_id($user_id, true);
				$body_key = get_value_from_array('body_key', $uuid);
				$head_key = get_value_from_array('head_key', $uuid);
				$cached = MogilefsDAO::get_instance()->check_file($body_key);
				if(is_ajax_view())
				{
					$url = $mogileFSImagePath.'/'.$body_key;
				}
				elseif(is_midlet_view() || is_mre_view())
				{
					$url = $mogileFSImagePath.'/'.$body_key.'?w=75&h=150&a=1&c=1';
				}
			}
			else
			{
				if(is_ajax_view())
				{
					$url = $server_root.'/sites/resources/images/avatar/avatar_ajax_default_full.png';
				}
				elseif(is_midlet_view() || is_mre_view())
				{
					$url = $server_root.'/sites/resources/images/avatar/avatar_midlet_default_full.png';
				}
			}

			return array('username'=>$username,
						'user_level' => $user_level,
						'user_id'=>$user_id,
						'body_key'=> $body_key,
						'head_key'=> $head_key,
						'cached'=> $cached,
						'user_avatar_created' => $user_avatar_created,
						'avatar_url' => $url
						);
		}
	}
?>