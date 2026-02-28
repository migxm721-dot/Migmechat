#!/usr/bin/php
<?php
require_once("/var/www/web/sites/common/query_string_field.php");

class QueryStringFieldList extends QueryStringField
{
	public static function _list()
	{
		array_map(function($a){ echo $a . "\n"; },
			array_unique(array_merge(
				  array_keys(self::$fields)
				, array_values(self::$fields)
			))
		);
	}
}

QueryStringFieldList::_list();
