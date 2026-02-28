<?php
	class RedisModel extends Model
	{
		const KEY_NAME = '__TEST_REDIS__';
		protected static $redis_parameters_query = "?connection_timeout=2&read_write_timeout=3";

		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args
		 */
		public function get_data($model_data, $model_args = array())
		{
			$data = array();
			$data['redis'] = array('success' => false);
			$data['redis']['instances'] = array();

			try
			{
				$redis_settings = Redis::get_shard_settings_from_source();
				foreach($redis_settings['shard_masters'] as $shard_id => $uri)
				{
					$data['redis']['instances']['master'][$shard_id] = $this->run_test(
						new Predis_Client('redis://' . $uri . self::$redis_parameters_query)
					);
				}
				foreach($redis_settings['shard_slaves'] as $shard_id => $uri)
				{
					$data['redis']['instances']['slave'][$shard_id] = $this->run_test(
						new Predis_Client('redis://' . $uri . self::$redis_parameters_query)
					);
				}
				$data['redis']['instances']['master']['leaderboards'] = $this->run_test(
					Redis::get_master_instance_for_leaderboards()
				);
				$data['redis']['instances']['slave']['leaderboards'] = $this->run_test(
					Redis::get_slave_instance_for_leaderboards()
				);
				$data['redis']['instances']['master']['games'] = $this->run_test(
					Redis::get_master_instance_for_games()
				);
				$data['redis']['instances']['slave']['games'] = $this->run_test(
					Redis::get_slave_instance_for_games()
				);

				$errors = array();
				foreach ($data['redis']['instances'] as $master_slave => $instances)
				{
					foreach ($instances as $name => $instance)
					{
						$instance['success'] ||
							$errors[] = $master_slave . '-' . $name;
					}
				}
				if (! empty($errors)) throw new Exception('The following shards are experiencing some problems: ' . implode(' ', $errors));
				$data['redis']['success'] = true;
			}
			catch (Exception $ex)
			{
				$data['redis']['error'] = $ex->getMessage();
			}

			return $data;
		}

		protected function run_test(Predis_Client $inst)
		{
			$val = rand();
			$res = $inst->pipeline()
				->set(self::KEY_NAME, $val)
				->get(self::KEY_NAME)
				->del(self::KEY_NAME)
				->execute();

			return array(
				  'success' => $res[0] && $res[1] == $val && $res[2]
				, 'set' => $res[0]
				, 'get' => $res[1] == $val
				, 'del' => $res[2]
			);
		}
	}
