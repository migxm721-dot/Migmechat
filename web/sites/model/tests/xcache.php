<?php
	class XcacheModel extends Model
	{
		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args
		 */
		public function get_data($model_data, $model_args = array())
		{
			$data = array();
			$data['xcache'] = array('success' => false);

			try
			{
				$random = mt_rand(1, 1e6);
				if (! XCache::getInstance()->set($random, $random, 300))
					throw new Exception('XCache set operations unsuccessful!');

				if ($random != XCache::getInstance()->get($random))
					throw new Exception('XCache get operations unsuccessful!');

				if (! XCache::getInstance()->delete($random))
					throw new Exception('XCache delete operations unsuccessful!');

				$data['xcache']['success'] = true;
			}
			catch (Exception $ex)
			{
				$data['xcache']['error'] = $ex->getMessage();
			}

			return $data;
		}
	}
