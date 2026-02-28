<?php

	fast_require('Language', get_library_directory().'/lib/language/language.php');

	class WordPressDomain
	{
		/***
		 * Memcached expiry
		 *
		 * Format: Day * Hour * Min * Secs
		 *
		 **/

		public static $EXPIRY = 3600;      // 1*60*60
		public static $EXPIRY_EMPTY = 180; // 3*60

		public static $KEY = 'WP/';

		/***
		 * Page ID
		 *
		 **/

		// migWorld Page ID
		public static $MIGWORLD_AJAX_MAIN_PAGEID = 2916;
		public static $MIGWORLD_AJAX_WHATSNEW_SNIPPET_PAGEID = 2918;
		public static $MIGWORLD_AJAX_WHATSNEW_DETAILS_PAGEID = 2920; // No Longer In Use. Keeping ID For Future Use

		public static $MIGWORLD_MIDLET_WHATSNEW_SNIPPET_PAGEID = 16668;
		public static $MIGWORLD_MIDLET_WHATSNEW_DETAILS_v42_PAGEID = 2923;
		public static $MIGWORLD_MIDLET_WHATSNEW_DETAILS_PAGEID = 16672;

		public static $MIGWORLD_WAP_MARKETING_PAGEID = 3344;

		public static $MIGWORLD_TOUCH_MARKETING_PAGEID = 8932;

		// Help Page
		public static $HELP_MIDLET_HOME_PAGEID = 5151;
		public static $HELP_WAP_HOME_PAGEID = 4282;
		public static $HELP_TOUCH_HOME_PAGEID = 9352;

		// Help Pages For Midlet
		public static $HELP_MIDLET_MIGLEVEL_PAGEID = 5201;

		// Store Page ID
		public static $STORE_AJAX_FRONT_PAGEID = 2962;
		public static $STORE_MIDLET_FRONT_PAGEID = 2941;
		public static $STORE_WAP_FRONT_PAGEID = 3346;
		public static $STORE_MIDLET_GUIDED_TOUR = 23281;
		public static $STORE_MIDLET_GUIDED_TOUR_VGS = 23282;
		public static $STORE_MIDLET_GUIDED_TOUR_EMOTICONS = 23283;
		public static $STORE_MIDLET_GUIDED_TOUR_SUPER_EMOTICONS = 23284;
		public static $STORE_MIDLET_GUIDED_TOUR_AVATARS = 23285;
		public static $STORE_MIDLET_GUIDED_TOUR_THEMES = 23286;

		// Store's Service Page ID
		public static $SERVICE_AJAX_ALL_PAGEID = 2928;
		public static $SERVICE_AJAX_CALL_PAGEID = 2932;
		public static $SERVICE_AJAX_SMS_PAGEID = 2938;
		public static $SERVICE_AJAX_BUZZ_PAGEID = 2930;
		public static $SERVICE_AJAX_LOOKOUT_PAGEID = 2936;
		public static $SERVICE_AJAX_KICKING_PAGEID = 2934;

		public static $SERVICE_MIDLET_BUZZ_PAGEID = 2943;
		public static $SERVICE_MIDLET_LOOKOUT_PAGEID = 2950;
		public static $SERVICE_MIDLET_KICKING_PAGEID = 2948;
		public static $SERVICE_MIDLET_CALLTOP_PAGEID = 2945;
		public static $SERVICE_MIDLET_CALLBOT_PAGEID = 3123;
		public static $SERVICE_MIDLET_SMSTOP_PAGEID = 2952;
		public static $SERVICE_MIDLET_SMSBOT_PAGEID = 3125;

		public static $SERVICE_WAP_ALL_PAGEID = 3348;
		public static $SERVICE_WAP_CALL_PAGEID = 3353;
		public static $SERVICE_WAP_SMS_PAGEID = 3359;
		public static $SERVICE_WAP_BUZZ_PAGEID = 3351;
		public static $SERVICE_WAP_LOOKOUT_PAGEID = 3357;
		public static $SERVICE_WAP_KICKING_PAGEID = 3355;

		public static $SERVICE_TOUCH_BUZZ_PAGEID = 8943;
		public static $SERVICE_TOUCH_LOOKOUT_PAGEID = 8945;
		public static $SERVICE_TOUCH_KICKING_PAGEID = 8947;

		// Other Page ID
		public static $LANDING_WAP_REFERRAL = 6290;
		public static $LANDING_WAP_ABOUT_US = 6560;
		public static $LANDING_WAP_POPULAR_IM = 6562;
		public static $LANDING_WAP_FRIENDS = 6565;
		public static $LANDING_WAP_MEET_AND_CHAT = 6569;
		public static $LANDING_WAP_MOB_WAP_WEB = 6572;
		public static $LANDING_WAP_MORE_FEATURES = 6886;

		// New User Experience (NUE)
		public static $NUE_TOUCH_FUN_THINGS_TO_DO = 10443;
		public static $NUE_MIDLET_HOME = 12650;

		// Group Home
		public static $GROUP_MIDLET_MAKE_POPULAR = 16675;
		public static $GROUP_AJAX_MAKE_POPULAR = 16682;

		// Merchant dashboard messages
		public static $MERCHANT_DASHBOARD_MARKETING_PAGE = 21032;
		public static $MERCHANT_DASHBOARD_SALES_PAGE = 21167;

		// migGames page featured games
		public static $FEATURED_GAMES_PAGE = 28071;
	}

	/*
	 * We are now putting multiple languages on the same page using qTranslate WordPress Plugins
	 *
		// Detect Language & Point To The Correct WordPress Page ID
		switch(Language::get_instance()->get_language_pack())
		{
			case 'id_ID':
				// Help
				WordPressDomain::$HELP_MIDLET_HOME_PAGEID = 11979;
				WordPressDomain::$HELP_MIDLET_MIGLEVEL_PAGEID = 11935;

				// Store
				WordPressDomain::$STORE_MIDLET_FRONT_PAGEID = 13048;

				// Services
				WordPressDomain::$SERVICE_MIDLET_BUZZ_PAGEID = 13152;
				WordPressDomain::$SERVICE_MIDLET_LOOKOUT_PAGEID = 13157;
				WordPressDomain::$SERVICE_MIDLET_KICKING_PAGEID = 13159;
				WordPressDomain::$SERVICE_MIDLET_CALLTOP_PAGEID = 13161;
				//WordPressDomain::$SERVICE_MIDLET_CALLBOT_PAGEID = 3123;
				WordPressDomain::$SERVICE_MIDLET_SMSTOP_PAGEID = 13164;
				//WordPressDomain::$SERVICE_MIDLET_SMSBOT_PAGEID = 3125;

				// migWorld
				WordPressDomain::$MIGWORLD_MIDLET_WHATSNEW_PAGEID = 13640;
				break;
		}
	*/
?>