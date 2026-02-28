<?php
	class WallModel extends Model
	{
		public function get_data($model_data)
		{
			return array(
				'type' => get_attribute_value('type'),
				'page' => get_attribute_value('page', 'integer', 1),
				'number_of_entries' => get_attribute_value('number_of_entries', 'integer', 10)
			);
		}
	}
?>