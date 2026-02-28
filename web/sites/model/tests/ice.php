<?php
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');

	class IceModel extends Model
	{
		/**
		 * @see Model::get_data()
		 * @param array $model_data
		 * @param array $model_args
		 */
		public function get_data($model_data, $model_args = array())
		{
			$data = array();
			$data['ice'] = array('success' => false);

			$ice = new IceDAO();
			try
			{
				$data['ice']['stats'] = $ice->get_stats();
				try
				{
					$data['ice']['check_session'] = $ice->check_session();
				}
				catch (Exception $e)
				{
					$data['ice']['check_session'] = $e->getMessage();
				}

				if (property_exists($data['ice']['stats'], 'objectCaches'))
					unset($data['ice']['stats']->objectCaches);
				$publishing_privacy_setting = $ice->get_publishing_privacy_setting('chernjie');
				if (! ($publishing_privacy_setting instanceof com_projectgoth_fusion_slice_EventPrivacySettingIce))
					throw new Exception('publishing_privacy_setting not an instance of com_projectgoth_fusion_slice_EventPrivacySettingIce');
				$data['ice']['success'] = true;
			}
			catch (Exception $ex)
			{
				$data['ice']['error'] = $ex->getMessage();
			}

			return $data;
		}
	}
