<?php
	/**
	*
	* Class representing the view
	*
	**/
	class View extends Mig33view {}
	class Mig33view
	{
		const MIDLET = 'midlet';
		const MIG33_AJAX = 'ajax';
		const MIG33_CORPORATE = 'corporate';
		const WAP = 'wap';
		const TOUCH = 'touch';
		const BLACKBERRY = 'blackberry';
		const IOS = 'ios';
		const MTK_MRE = 'mre';
		const BLAAST = 'blaast';
		const MIGBO_WEB = 'web';
		/**
		 * @deprecated
		 */
		const JSON = 'json';

		public static $views = array(
			  self::MIDLET
			, self::MIG33_AJAX
			, self::MIG33_CORPORATE
			, self::WAP
			, self::TOUCH
			, self::BLACKBERRY
			, self::IOS
			, self::MTK_MRE
			, self::BLAAST
			, self::MIGBO_WEB
			, self::JSON
		);

		/**
		 * com.projectgoth.fusion.restapi.data.SSOEnums
		 */
		public static $sso_views = array(
			  'MIG33_AJAX'
			, 'MIG33_CORPORATE'
			, 'MIG33_BLAAST'
			, 'MIG33_BLACKBERRY'
			, 'MIG33_MIDLET'
			, 'MIG33_MRE'
			, 'MIG33_TOUCH'
			, 'MIG33_WAP'
			, 'MIG33_WINDOWS_MOBILE'
			, 'MIG33_IOS'
			, 'MIGBO_BLAAST'
			, 'MIGBO_BLACKBERRY'
			, 'MIGBO_MIDLET'
			, 'MIGBO_MRE'
			, 'MIGBO_TOUCH'
			, 'MIGBO_WAP'
			, 'MIGBO_WINDOWS_MOBILE'
			, 'MIGBO_WEB'
			, 'MIGBO_IOS'
		);

		protected static $view_override = array(
			  self::BLACKBERRY      => self::TOUCH
			, self::IOS             => self::TOUCH
			, self::MTK_MRE         => self::MIDLET
			, self::MIG33_AJAX      => self::WAP
			, self::MIG33_CORPORATE => self::WAP
		);

		public static function get_override_view($view)
		{
			return array_key_exists($view, self::$view_override)
				? self::$view_override[$view]
				: $view;
		}

		// as per com.projectgoth.migbo.datasvc.enums.PostApplicationEnum
		// WEB(-1), WAP(-2), J2ME(-3), ANDROID(-4), SYSTEM(-5), BLACKBERRY(-6), BLAAST(-7) ,MRE(-8);
		public static $PostApplicationEnum = array(
			  self::MIGBO_WEB => -1
			, self::WAP => -2
			, self::MIDLET => -3
			, self::TOUCH => -4
			, 'system' => -5
			, self::BLACKBERRY => -6
			, self::BLAAST => -7
			, self::MTK_MRE => -8
			, self::IOS => -9
		);
		/**
		 * @param string $view
		 * @return number
		*/
		public static function get_post_application_enum($view)
		{
			return empty(self::$PostApplicationEnum[$view])
				? self::$PostApplicationEnum['system']
				: self::$PostApplicationEnum[$view];
		}

		public static function get_view()
		{
			return self::MIGBO_WEB;
		}
		public static function is_web_view()
		{
			return (MY_View::get_view() == self::MIGBO_WEB);
		}
		public static function is_wap_view()
		{
			return (MY_View::get_view() == self::WAP);
		}
		public static function is_touch_view()
		{
			return (MY_View::get_view() == self::TOUCH);
		}
		public static function is_blackberry_view()
		{
			return (MY_View::get_view() == self::BLACKBERRY);
		}
		public static function is_ios_view()
		{
			return (MY_View::get_view() == self::IOS);
		}
		public static function is_midlet_view()
		{
			return (MY_View::get_view() == self::MIDLET);
		}
		public static function is_mre_view()
		{
			return (MY_View::get_view() == self::MTK_MRE);
		}
	}
?>