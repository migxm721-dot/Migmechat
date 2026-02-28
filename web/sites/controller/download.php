<?php
class DownloadController
{
	public function client(&$model_data)
	{
		if ($model_data['get_new_version'] == true)
		{
			printf('%s|%s', $model_data['latest_version'], $model_data['download_url']);
		}
		else
		{
			echo "";
		}
		exit();
	}
}
?>