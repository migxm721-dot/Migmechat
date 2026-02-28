<?php
	class TestsController
	{
		public function services($model_data)
		{
			$model_data['time_taken'] = time() - $_SERVER['REQUEST_TIME'];
			return new RestResult($model_data);
		}
	}
?>