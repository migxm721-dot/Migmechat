<?php
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');
	fast_require('AccountDAO', get_dao_directory() . '/account_dao.php');

	class CompatModel
	{
		public function get_data($model_data)
		{
			$session_user = $model_data['session_user'];
			$session_user_detail = $model_data['session_user_detail'];

			$data = array();

			$ice_dao = new IceDAO();
			$data['presence'] = $ice_dao->get_overall_presence($session_user, $session_user);

			$account_dao = new AccountDAO();
			$data['balance']  = $account_dao->get_balance($session_user);

			return $data;
		}
	}
