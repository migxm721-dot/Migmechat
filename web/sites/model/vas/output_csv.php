<?php
	class OutputCsvModel
	{
		public function get_data($model_data)
		{
			$partner = $model_data['partner'];
			$agreement_id = get_value('agreement_id', 'integer', 0);

			$data = array();
			$data['period'] = $period = get_attribute_value('period', 'string', '');
			$data['type'] = $type = get_attribute_value('type', 'string', '');

			$partner_dao = new PartnerDAO();

			if ($period == 'weekly')
			{
				$data['week'] = $week = get_value('week', 'integer', date('W'));
				$data['year'] = $year = get_value('year', 'integer', date('Y'));

				$result = $partner_dao->get_agreement_stats_weekly($agreement_id, $partner->id, $week, $year);

				$data['stats'] = $result;
			}
			else if ($period == 'monthly')
			{
				$data['year'] = $year = get_attribute_value('year', 'integer', date('Y'));

				$result = $partner_dao->get_agreement_stats_monthly($agreement_id, $partner->id, $year);

				$data['stats'] = $result;
			}
			else if ($period == 'quarterly')
			{
				$page = get_attribute_value('page', 'integer', 1);
				$num_of_entries = 4;

				$result = $partner_dao->get_agreement_stats_quarterly($agreement_id, $partner->id, $num_of_entries, $page);

				$data['stats'] = empty($result['agreement_stat_quarters']) ? null : array_reverse($result['agreement_stat_quarters']);
			}

			return $data;
		}
	}
?>