<?php
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');

	class PresenceModel
	{
		public function get_data($model_data)
		{
			$session_user = $model_data['session_user'];
			$session_user_detail = $model_data['session_user_detail'];

			$ice_dao = new IceDAO();

			$data = array();
			try
			{
				$data['presence'] = $ice_dao->get_overall_presence($session_user, $session_user);
			}
			catch(Exception $e)
			{
				error_log('unable to query user presence');
				$data['presence'] = 99;
			}

			return $data;
		}
	}
?>