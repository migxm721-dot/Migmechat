<?php
	fast_require('UserSettingsDAO', get_dao_directory() . '/user_settings_dao.php');

	class PrivacyModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array('session_user', $model_data);
			$username = get_attribute_value('username', 'string', $session_user);
			$session_user_detail = $model_data['session_user_detail'];

			$user_profile = empty($model_data['user_profile']) ? null : $model_data['user_profile'];
			$user_id = null;

			$user_settings_dao = new UserSettingsDAO();
			/*
			 * Settings -> Private, Profile & Friend Only
			 * Views -> Own profile, Friends Profile, Strangers Profile
			 *
			 * Heirarchy:
			 * Own
			 * Private
			 * * Stranger
			 * * Friend
			 * Public
			 * * Stranger
			 * * Friend
			 */

			$own_profile = false;

			// we'll need to find the userid to get the privacy settings
			if($session_user == $username)
			{
				$own_profile = true;
				$user_id = get_value_from_array('session_user_id', $model_data);
			}
			elseif (isset($model_data['user_detail']))
			{
				$user_id = $model_data['user_detail']->userID;
			}
			else
			{
				$user_id = $user_settings_dao->get_userid($username);
			}

			$is_friend = $user_settings_dao->users_are_friends($session_user, $username);

			if(empty($user_profile))
			{
				fast_require('UserDAO', get_dao_directory() . '/user_dao.php');
				$user_dao = new UserDAO();
				$user_profile = $user_dao->get_user_profile($session_user, $username);
			}
			$profile_public       = is_null($user_profile) ? false : $user_profile->is_public_profile();
			$profile_private      = is_null($user_profile) ? true  : $user_profile->is_private_profile();
			$profile_only_friends = is_null($user_profile) ? false : $user_profile->is_friend_only_profile();

			$profile_stranger = ($profile_public && !$is_friend);
			$profile_friends_public = ($is_friend && $profile_public);
			$profile_friends_only = ($is_friend && $profile_only_friends);
			/*
			$model_data['is_total_private'] = (!$own_profile && ($profile_private || !$profile_friends_only));

			$model_data['can_view_stats_link'] = ($own_profile || $profile_public || $profile_friends_public || $profile_friends_only);
			*/

			$model_data['is_own_profile_verified'] = (!empty($session_user_detail) ? $session_user_detail->is_verified() : false);
			$model_data['own_profile'] = $own_profile;
			$model_data['is_friend'] = $is_friend;
			$model_data['is_stranger'] = $profile_stranger;
			$model_data['is_profile_public'] = $profile_public;
			$model_data['is_profile_private'] = $profile_private;
			$model_data['is_profile_only_friends'] = $profile_only_friends;
			$model_data['is_profile_friends_only'] = $profile_friends_only;
			$model_data['is_profile_friends_public'] = $profile_friends_public;

			$model_data['can_view_about_me'] = ($own_profile || $profile_public || $profile_friends_only);
			$model_data['can_view_full_profile'] = ($own_profile || $profile_public || $profile_friends_only);
			$model_data['can_view_communication_options'] = (!$own_profile && ($profile_friends_public || $profile_friends_only));
			$model_data['can_view_profile_chatrooms'] = ($own_profile || $is_friend );

			//==STATS LINK==
			$model_data['can_view_stats_link'] = ($own_profile || $profile_public || $profile_friends_only);

			//==GROUPS==
			$model_data['can_view_groups_module'] = ($own_profile || $profile_public || $profile_friends_only);

			//==FRIENDS==
			$model_data['can_view_friends_module'] = ($own_profile || $profile_public || $profile_friends_only);

			//==GAMES==
			$model_data['can_view_games_module'] = ($own_profile || $profile_public || $profile_friends_only);

			//==GIFTS RECEIVED==
			$model_data['can_view_gifts_module'] = true;

			//==EXPLORE==
			$model_data['can_view_explore_module'] = ($own_profile || $profile_public || $profile_friends_only);

			$model_data['can_view_wall_module'] = ($own_profile || $profile_public || $profile_friends_only);

			$model_data['can_post_wall_post'] = ($own_profile || $profile_friends_public || $profile_friends_only);
			$model_data['can_like_wall_post'] = ($own_profile || $profile_friends_public || $profile_friends_only);
			$model_data['can_remove_wall_post'] = $own_profile;
			$model_data['can_post_wall_post_comment'] = ($own_profile || $profile_friends_public || $profile_friends_only);
			$model_data['can_remove_wall_post_comment'] = $own_profile;

			//==user settings==
			$dob_privacy           = intval( $user_settings_dao->get_dob_privacy($user_id) );

			$model_data['can_show_mobile'] = $own_profile;
			$model_data['dob_privacy'] = $dob_privacy;
			$model_data['can_show_full_dob'] = $own_profile || ($dob_privacy == 1);
			$model_data['can_show_partial_dob'] = $own_profile || ($dob_privacy == 2);

			return $model_data;
		}
	}
?>
