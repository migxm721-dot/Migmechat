<?php

	class DisplayModel extends Model
	{
		public function get_data($model_data)
		{
			$data['$show_quick_access'] = false;
			return $data;
		}
	}
?>