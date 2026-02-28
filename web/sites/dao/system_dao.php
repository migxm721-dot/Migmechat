<?php
	fast_require('XCache', get_framework_common_directory() . '/xcache.php');
	fast_require('DAO', get_dao_directory() . '/dao.php');

	class SystemDAO extends DAO
	{
		const XCACHE_TTL_IN_SECONDS=60;

		public function load_properties()
		{
			$xcache = XCache::getInstance();

			return $xcache->get
			(
				XCache::KEYSPACE_SYSTEM_PROPERTIES
				, array(&$this, 'load_properties_from_source')
				, self::XCACHE_TTL_IN_SECONDS
			);
		}

        public function load_properties_from_source()
        {
			try
			{
				$query = 'SELECT * FROM system';

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

				while($stmt->fetch())
				{
				   // keys will be case-insensitive, normalized to lower case
				   $property_values[ strtolower($data['PropertyName']) ] = $data['PropertyValue'];
				}
				$stmt->close();
				$this->closeSlaveConnection();

				return $property_values;
			}
			catch (Exception $e)
			{
				error_log("unable to fetch System table from database");
			}

			return null;
        }
    }
?>
