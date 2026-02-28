<?php
	class MemcacheModel extends Model
	{
		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args
		 */
		public function get_data($model_data, $model_args = array())
		{
			$data = array();
			$data['memcache'] = array('success' => false);

			try
			{
				$random = mt_rand(1, 1e6);
				if (! Memcached::get_instance()->set($random, $random, false, 60))
					throw new Exception('Memcache set operations unsuccessful!');

				if ($random != Memcached::get_instance()->get($random))
					throw new Exception('Memcache get operations unsuccessful!');

				if (! Memcached::get_instance()->delete($random))
					throw new Exception('Memcache delete operations unsuccessful!');

				$data['memcache']['success'] = true;
			}
			catch (Exception $ex)
			{
				$data['memcache']['error'] = $ex->getMessage();
			}

			return $data;
		}
	}
