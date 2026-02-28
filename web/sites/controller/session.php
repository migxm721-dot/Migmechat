<?php
fast_require('SessionUtilities', get_framework_common_directory() . '/session_utilities.php');

class SessionController
{
	public function check(&$model_data)
	{
		$data = array('is_logged_in' => isset($model_data['session_user']));
		if ($data['is_logged_in'])
			$data['username'] = $model_data['session_user'];

		if ($data['is_logged_in'] && (DEBUG_MODE || get_value('debug')))
			$data['sid'] = SessionUtilities::get_session_id();

		/**
		$delete = get_value('delete');
		if ($delete == 'sso')
		{
			setcookie(SessionUtilities::ENCRYPTED_SESSION_COOKIE_KEY, null, 1, '/', $GLOBALS['session_cookie_domain']);
			Memcached::get_instance()->remove_item(
				Memcached::$KEYSPACE_SSO_SESSION . SessionUtilities::get_session_id()
			);
		}
		if ($delete == 'slim')
		{
			Memcached::get_instance()->remove_item(
				Memcached::$KEYSPACE_SLIM_SESSION . SessionUtilities::get_session_id()
			);
		}
		/**/

		return new RestResult($data);
	}

	public function logout(&$model_data)
	{
		//Remove session from memcache
		SessionUtilities::destroy_session_in_cache();

		$return_url = get_value('return_url');
		if (empty($return_url)) $return_url = '/';

		redirect($return_url);
	}
}
?>
