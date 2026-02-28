<?php
require_once(PACKAGESPATH . 'mig33/libraries/Mig33view.php');

class MY_View extends Mig33view
{
	protected static $view_override = array(
		  parent::MIG33_AJAX      => parent::MIGBO_WEB
		, parent::MIG33_CORPORATE => parent::MIGBO_WEB
		, parent::MIGBO_WEB       => parent::MIGBO_WEB
		, parent::MIDLET          => parent::MIDLET
		, parent::TOUCH           => parent::WAP
		, parent::BLACKBERRY      => parent::WAP
		, parent::IOS             => parent::WAP
		, parent::WAP             => parent::WAP
		, parent::MTK_MRE         => parent::MIDLET
		//, parent::BLAAST          => parent::MIDLET
		//, parent::JSON            => parent::WAP
	);

	public static function get_override_view($view)
	{
		return array_key_exists($view, self::$view_override)
			? self::$view_override[$view]
			: $view;
	}

	public static function get_view()
	{
		// Check If In migCore
		$is_migcore = false;
		if(strpos($_SERVER['REQUEST_URI'], '/b/') !== FALSE)
			$is_migcore = true;

		// Check Whether Is There Any Decorators Passed In The URL
		$decorators = View::$views;
		$segments = explode('/', $_SERVER['REQUEST_URI']);
		if(sizeof($segments) > 0)
		{
			$decorator_position = 0;
			if($is_migcore)
				$decorator_position = 1;

			array_shift($segments);
			if(in_array($segments[$decorator_position], $decorators))
				return $segments[$decorator_position];
		}

		return MY_View::MIGBO_WEB;
	}
}
?>