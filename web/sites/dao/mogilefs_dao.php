<?php
fast_require('MogileFS', get_library_directory() . '/mogilefs/MogileFS.class.php');

class MogilefsDAO extends DAO
{
	/**
	 * @var MogilefsDAO
	 */
	private static $instance;
	/**
	 * @var MogileFS
	 */
	public $mfs;
	const MFS_DOMAIN = 'mig33';
	const MFS_CLASS = 'image';

	public function __construct()
	{
		parent::__construct();
		global $perl_mogilefs_tracker1, $perl_mogilefs_tracker2, $perl_mogilefs_tracker3, $perl_mogilefs_tracker4;
		$trackers = array();

		for($i=1;$i<4;$i++)
		{
			$_thost_name = 'perl_mogilefs_tracker' . $i;
			if (! isset($$_thost_name)) continue;
			if (! empty($$_thost_name))
			{
				$tracker_uri = $$_thost_name;
				$trackers[$tracker_uri] = $$_thost_name;
			}
		}
		$this->mfs = new MogileFS(
						  self::MFS_DOMAIN
						, self::MFS_CLASS
						, $trackers
					);
		$this->mfs->setRequestTimeout(60);
		$this->mfs->setPutTimeout(30);
	}

	/**
	 * @return MogilefsDAO
	 */
	public static function get_instance()
	{
		if (! isset(self::$instance))
		{
			$c = __CLASS__;
			self::$instance = new $c;
		}
		return self::$instance;
	}

	public function save_file($filepath,$destkey)
	{
		try
		{
			if (! $this->mfs->setFile($destkey, $filepath))
				throw new Exception('Unable to save file.');
		}
		catch(Exception $e)
		{
			die('(' . $e->getCode() . ')' . $e->getMessage());
		}
	}

	function delete_file($key)
	{
		try
		{
			if($this->mfs->exists($key))
			{
				if (! $this->mfs->delete($key))
					throw new Exception('Unable to delete file.');
			}
		}
		catch(Exception $e)
		{
			die('(' . $e->getCode() . ')' . $e->getMessage());
		}
	}

	function check_file($key)
	{
		try
		{
			return $this->mfs->exists($key);
		}
		catch(Exception $e)
		{
			die('(' . $e->getCode() . ')' . $e->getMessage());
		}
	}
}

?>
