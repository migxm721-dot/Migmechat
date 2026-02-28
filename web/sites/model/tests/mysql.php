<?php
	fast_require('UserDAO', get_dao_directory() . '/user_dao.php');

	class MysqlModel extends Model
	{
		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args
		 */
		public function get_data($model_data, $model_args = array())
		{
			$data = array();
			$data['mysql'] = array();

			$dao = new UserDAO();
			try
			{
				$result = $dao->get_user_detail('chernjie');
				if ($result->username != 'chernjie')
					throw new Exception('Mysql and given results do not match!');
				$data['mysql']['success'] = true;
			}
			catch (Exception $ex)
			{
				$data['mysql']['error'] = $ex->getMessage();
			}

			return $data;
		}
	}
