<?php

class XCacheException extends Exception {};

class XCache
{
	// constant for locking behavior
	const XC_LOCKS_FS_PATH = '/cache/xcache-locks';
	const XC_LOCK_ACQUISITION_TIMEOUT = 30; // 20s
	const XC_LOCK_ACQUISITION_STEP_TIME = 0.1; // 100ms
	const XC_LOCK_EXPIRY = 20; // 20s

	/**
	 * singleton instance
	 * @var XCache
	 */
	private static $xcobj;

	// userspace keyspaces, and timeout definitions below
	const KEYSPACE_SYSTEM_PROPERTIES        = 'SYSTEM_PROPERTIES';
	const KEYSPACE_REDIS_SHARD_SETTINGS     = 'REDIS_SETTINGS';
	const KEYSPACE_THIRD_PARTY_APPS         = 'THIRD_PARTY_APPS';
	const KEYSPACE_TPA_LINKED_GROUPS        = 'TPA_LINKED_GROUPS';
	const KEYSPACE_CHATROOM_GAME_BOTS       = 'CHATROOM_GAME/BOTS';
	const KEYSPACE_CHATROOM_GAME_ROOMS      = 'CHATROOM_GAME/ROOMS/';
	const KEYSPACE_VAS                      = 'VAS/';
	const KEYSPACE_VAS_MENU                 = 'MENU/';
	const KEYSPACE_MERCHANT_EMAIL_WELCOME	= 'MERCHANT_EMAIL/WELCOME/'; //+language
	const KEYSPACE_COUNTRIES_HASH           = 'COUNTRIES_HASH';
	const KEYSPACE_WHITELISTED_DOMAINS      = 'WHITELISTED_DOMAINS';
	const KEYSPACE_CLIENT_DOWNLOAD_VERSION  = 'DOWNLOAD/CLIENT/LATEST/%s/%s';
	

	// instance variables
	private $locks = null;
	private $default_options = null;

	private $debug = DEBUG_MODE;


	private function __construct()
	{
		// all locks will be stored in this array
		$this->locks = array();

		// clients are allow to fine-tune behavior
		$this->default_options = array
		(
			"use_lock"                 => true,
			"lock_acquisition_timeout" => self::XC_LOCK_ACQUISITION_TIMEOUT,
			"lock_expiry"              => self::XC_LOCK_EXPIRY,
			"fetch_without_lock"       => false,
			"set_without_lock"         => false,
			"callback_args"			   => array()
		);
	}

	public final function __clone()
	{
		throw new BadMethodCallException("Clone is not allowed");
	}

	public function __destruct()
	{
		// on destruction, we explicitly close all dangling locks (if any)
		foreach($this->locks as $lock)
		{
			flock($lock['file_pointer'], LOCK_UN);
			fclose($lock['file_pointer']);
		}

		$this->locks = null;
	}

	/**
	* getInstance
	*
	* @static
	* @access public
	* @return XCache instance
	*/
	public static function getInstance()
	{
		if (!(self::$xcobj instanceof XCache))
		{
			self::$xcobj = new XCache;
		}
		return self::$xcobj;
	}

	/**
	* set
	*
	* @param mixed $name
	* @param mixed $value
	* @param integer $tll time-to-live for this cache entry
	* @param boolean $useLock, true will acquire a lock before writing
	* @access public
	* @return bool
	*/
	public function set($name, $value, $ttl=3600, $options=array())
	{
		if (empty($name))
		{
			throw new BadMethodCallException("cache entries must have a name");
		}

		if ($this->debug) return true; // in debug mode we do not store anything in XCache


		$res = false;
		$lock_value = null;
		$options = array_merge($this->default_options, $options);

		if($options['use_lock'])
		{
			$lock_value = $this->acquire_lock($name, $options);

			if (is_null($lock_value) && !$options['set_without_lock'])
			{
				// unable to acquire lock AND not allowed to proceed without lock
				return false;
			}
		}

		$res = xcache_set($name, $value, $ttl);

		$this->release_lock($name, $lock_value);

		return $res;
	}

	/**
	 * get
	 *
	 * get a data from cache by name
	 * if the data is not in cache, and a data provider function fetch_data_callback is provided
	 * the method get will attempt to call the data while implementing a safe double-check lock
	 *
	 *
	 * @param mixed $name
	 * @param callback $fetch_data_callback acts as a data provider to populate the cache when found empty
	 * @param integer $ttl time to live
	 * @param mixed $name
	 * @access public
	 * @return void
	**/
	public function get($name, $fetch_data_callback=null, $ttl=3600, $options=array())
	{
		if (empty($name))
		{
			throw new BadMethodCallException("cache entries must have a name");
		}

		if (!$this->debug)
		{
			$data = xcache_get($name);

			// we have data, just return it
			if (!is_null($data))
			{
				return $data;
			}
		}

		// no data in cache, check if there is a data provider function
		if (is_null($fetch_data_callback) || !is_callable($fetch_data_callback))
		{
			return null;
		}

		// we have a data provider function
		// let's try to implement the double-check locking to get the data
		$lock_value = null;
		$options = array_merge($this->default_options, $options);

		if ($this->debug)
		{
			return call_user_func_array($fetch_data_callback, $options['callback_args']);
		}

		if($options['use_lock'])
		{
			$lock_value = $this->acquire_lock($name, $options);
		}

		// at this point, we have either acquired the lock, or waited a while for the lock to be released, but couldn't get it
		// verify if the previous lock owner has re-populated the data since the last check
		$data = xcache_get($name);
		if (!is_null($data))
		{
			$this->release_lock($name, $lock_value);
			return $data;
		}

		// cache is still empty,
		// verify if we are allowed to fetch the data from the data provider function
		if (is_null($lock_value) && !$options['fetch_without_lock'])
		{
			return null;
		}

		// we call $fetch_data_callback as the single function inside a try..catch block
		// because the exceptions thrown are client-known and must be re-thrown accordingly
		try
		{
			$data = call_user_func_array($fetch_data_callback, $options['callback_args']);
		}
		catch(Exception $e)
		{
			$this->release_lock($name, $lock_value);
			throw $e; // throw client exception back to client!
		}

		// We have data!
		// check if we are allowed to set if we hadn't acquired the lock
		// we store anything EXCEPT null (including empty strings empty arrays, and boolean false)
		if (!is_null($data))
		{
			if (!is_null($lock_value) || $options['set_without_lock'])
			{
				$this->set($name, $data, $ttl, array('use_lock'=>false));
			}
		}

		$this->release_lock($name, $lock_value);
		return $data;
	}

	/**
	* is_set
	*
	* @param string $name
	* @access public
	* @return bool
	*/
	public function is_set($name)
	{
		if ($this->debug) return false;

		return xcache_isset($name);
	}

	/**
	* delete
	*
	* @param string $name
	* @access public
	* @return bool
	*/
	public function delete($name)
	{
		if ($this->debug) return true;

		return xcache_unset($name);
	}

	/**
	 * acquire_lock
	 *
	 * acquire a server lock for the given name
	 *
	 * @param string $lock_id
	 * @access public
	 * @return int (lock value) or null
	**/
	public function acquire_lock($lock_id, $options=array())
	{
		if(empty($lock_id))
		{
			throw new BadMethodCallException("lock_id cannot be empty");
		}

		if (isset($this->locks[$lock_id]))
		{
			// this lock is already taken
			return null;
		}

		global $apache_dir;

		// the file names must be obscure and file-system safe, sha1 should do the trick just nice
		$lock_file_path = $apache_dir . self::XC_LOCKS_FS_PATH . '/' . sha1($lock_id) . '.lock';

		// we open the file with 'c', such that the file modification time is not affected
		$fp = @fopen($lock_file_path, 'a');
		if (!$fp)
		{
			// if we are not able to get a file pointer, something is seriously wrong (permissions? directory missing?)
			error_log("Unable to get file pointer for xcache lock on [$lock_id] at [$lock_file_path].");
			return null;
		}

		$iterations_left = ceil($options['lock_acquisition_timeout'] / self::XC_LOCK_ACQUISITION_STEP_TIME);
		$sleep_time = ceil(self::XC_LOCK_ACQUISITION_STEP_TIME * 1000000); // converts seconds to microseconds

		while(!flock($fp, LOCK_EX | LOCK_NB))
		{
			if ($iterations_left-- < 0)
			{
				// failed to get the lock in an acceptable amount of time
				// verify if the lock itself is expired
				clearstatcache();
				$current_lock_value = @filemtime($lock_file_path);
				if ($current_lock_value < time())
				{
					// lock is expired, we'll destroy the file and
					// attempt to get a new lock
					// question: is this introducing new race conditions? probably... :(
					fclose($fp);
					unlink($lock_file_path);
					return $this->acquire_lock($lock_id, $options);
				}
				else
				{
					// lock is still valid and held by someone else
					error_log("Unable to acquire an xcache lock on [$lock_id] at [$lock_file_path].");
					return null;
				}
			}

			usleep($sleep_time);
		}

		// we have the lock! set lock value
		$lock_value = time() + $options['lock_expiry'];
		touch($lock_file_path, $lock_value);

		// finally, we can record the lock details internally
		$this->locks[$lock_id] = array(
			'file_path' => $lock_file_path,
			'file_pointer' => $fp
		);

		return $lock_value;
	}

	/**
	 * release_lock
	 *
	 * release a lock
	 *
	 * @param string $lock_id
	 * @param string $lock_value to identify whether the process that tries to release the lock is still the owner of the lock
	 * @access public
	 * @return bool
	**/
	public function release_lock($lock_id, $lock_value=null)
	{
		if(empty($lock_id))
		{
			throw new BadMethodCallException("lock_id cannot be empty");
		}

		$result = false;

		if(!isset($this->locks[$lock_id]) || is_null($lock_value))
		{
			return $result;
		}

		$lock = &$this->locks[$lock_id];
		$is_lock_owner = true;

		// verify if we are still owner of the lock
		// only another process can steal ownership
		clearstatcache();
		$is_lock_owner = (@filemtime($lock['file_path']) === $lock_value);

		// release lock and close file pointer
		if ($is_lock_owner && isset($lock['file_pointer']))
		{
			touch($lock['file_path'], 0);
			$fp = $lock['file_pointer'];
			$result = flock($fp, LOCK_UN);
			fclose($fp);
		}

		// remove lock details from locks pool
		unset($this->locks[$lock_id]);

		// NOTE: we do NOT remove the lock file itself because another process
		// might need the file to still exist (i.e. it was competing for the lock)

		return $result;
	}
}
