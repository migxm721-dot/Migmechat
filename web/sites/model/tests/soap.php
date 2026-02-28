<?php
	fast_require('UserDAO', get_dao_directory() . '/user_dao.php');

	class SoapModel extends Model
	{
		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args
		 */
		public function get_data($model_data, $model_args = array())
		{
			$data = array();
			$data['soap'] = array('success' => false);

			$dao = new UserDAO();
			try
			{
				$result = $dao->get_user_profile('chernjie', 'chernjie');
				if (! is_object($result) || ! property_exists($result, 'username') || $result->username != 'chernjie')
					throw new Exception('Soap call failed');
				$data['soap']['success'] = true;
			}
			catch (Exception $ex)
			{
				$data['soap']['error'] = $ex->getMessage();
			}

			return $data;
		}
	}
