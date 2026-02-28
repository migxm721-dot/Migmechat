<?php
fast_require('Language', get_library_directory().'/lib/language/language.php');
/**
* constants to be used throughout the system
* used private static variables so they will be immutable as 'const' is only for simple variables
*/
class Constants
{
	/***** Merchant *****/

	const MERCHANT_COOKIE_NAME = 'sID',
		  MERCHANT_SESSION_STORAGE = 'Merchant/Sessions/corporate',
	      MERCHANT_ROOT_DIR = '/wordpress';

	private static $MERCHANT_USER_TYPES = array(self::MIG33_MERCHANT, self::MIG33_TOP_MERCHANT);

	/* private static $MERCHANT_BONUS_CALCULATOR_TIERS = array(
			array('min' => 70, 'max' => 3499, 'bonus' => 1.4),
			array('min' => 3250, 'max' => 19999, 'bonus' => 1.5),
			array('min' => 20000, 'max' => '?', 'bonus' => 1.6)
		); */
	private static $MERCHANT_BONUS_CALCULATOR_TIERS = array(
			70 => 1.4,
			3250 => 1.5,
			20000 => 1.6 );

	// store variables
	const STORE_PREMIUM_PRICE = 0.2, // USD
	      STORE_GIFT_SHOWER_PRICE = 0.4; // USD

	// account transaction types
	const TRANSFERS = 1,
	      VOUCHERS = 2,
	      CREDITS = 3,
	      INVITES = 4,
	      TRAILS = 5;

	private static  $ACCOUNT_TRANSACTION_TYPE_TRANSFERS = array(
							self::USER_TO_USER_TRANSFER
							),
	                $ACCOUNT_TRANSACTION_TYPE_CREDITS = array(
	                		self::CREDIT_CARD,
							self::VOUCHER_RECHARGE,
							self::TELEGRAPHIC_TRANSFER,
							self::BANK_TRANSFER,
							self::WESTERN_UNION,
							self::BLUE_LABEL_ONE_VOUCHER
							),
	                $ACCOUNT_TRANSACTION_TYPE_VOUCHERS = array(
                			self::VOUCHERS_CREATED,
						 	self::VOUCHERS_CANCELLED
						 	),
	                $ACCOUNT_TRANSACTION_TYPE_SALES = array(
                			self::USER_TO_USER_TRANSFER,
						 	self::VOUCHERS_CREATED
						 	),
	                $ACCOUNT_TRANSACTION_TYPE_ALL =  array(
	                		self::USER_TO_USER_TRANSFER,
							 self::CREDIT_CARD,
							 self::VOUCHER_RECHARGE,
							 self::TELEGRAPHIC_TRANSFER,
							 self::BANK_TRANSFER,
							 self::WESTERN_UNION,
							 self::BLUE_LABEL_ONE_VOUCHER,
							 self::VOUCHERS_CREATED,
							 self::VOUCHERS_CANCELLED,
							 self::MERCHANT_REVENUE_TRAIL,
							 self::MERCHANT_GAME_TRAIL,
							 self::MERCHANT_THIRD_PARTY_APP_TRAIL
							 ),
					$ACCOUNT_TRANSACTION_TYPE_TRAILS = array(
							self::MERCHANT_REVENUE_TRAIL,
							self::MERCHANT_GAME_TRAIL,
							self::MERCHANT_THIRD_PARTY_APP_TRAIL
							 ),
					$ACCOUNT_TRANSACTION_TYPE_CREDITPURCHASE = array(
							self::CREDIT_CARD,
							self::CREDIT_CARD_REFUND,
							self::TELEGRAPHIC_TRANSFER,
							self::BANK_TRANSFER,
							self::BANK_TRANSFER_REVERSAL,
							self::WESTERN_UNION,
							self::WESTERN_UNION_REVERSAL,
							),
					$ACCOUNT_TRANSACTION_TYPE_CREDITSPEND = array(
							self::SMS_CHARGE,
							self::CALL_CHARGE,
							self::SUBSCRIPTION,
							self::PRODUCT_PURCHASE,
							self::PREMIUM_SMS_FEE,
							self::REFUND,
							self::VOUCHERS_CREATED,
							self::VOUCHERS_CANCELLED,
							self::SYSTEM_SMS_CHARGE,
							self::CHATROOM_KICK_CHARGE,
							self::EMOTICON_PURCHASE,
							self::CONTENT_ITEM_PURCHASE,
							self::CONTENT_ITEM_REFUND,
							self::AVATAR_PURCHASE,
							self::GAME_ITEM_PURCHASE,
							self::VIRTUAL_GIFT_PURCHASE,
							self::GAME_START,
							self::GAME_START_REVERSAL,
							self::POT_ENTRY,
							self::POT_ENTRY_REVERSAL
					);

	// moneybookers
	const MONEYBOOKERS_TITLE = 'MoneyBookers',
	      MONEYBOOKERS_PAYMENT_EMAIL = 'sales@mig.me',
	      MONEYBOOKERS_PAYMENT_RECIEVED_EMAIL = 'sales@mig.me';

	// email addresses
	const CUSTOMER_SERVICE_EMAIL = "contact@mig.me"
		, MERCHANT_EMAIL = 'merchant@mig.me'
		, MIGGAMES_EMAIL = 'miggames@mig.me'
		, MARKETING_EMAIL = 'marketing@mig.me';



	/***** Account Entry Types *****/

	// account entry types
	const CREDIT_CARD 				= 1,
	      VOUCHER_RECHARGE			= 2,
	      SMS_CHARGE				= 3,
	      CALL_CHARGE				= 4,
	      SUBSCRIPTION				= 5,
	      PRODUCT_PURCHASE			= 6,
	      REFERRAL_CREDIT			= 7,
	      ACTIVATION_CREDIT			= 8,
	      BONUS_CREDIT				= 9,
	      REFUND					= 10,
	      PREMIUM_SMS_RECHARGE		= 11,
	      PREMIUM_SMS_FEE			= 12,
	      CREDIT_CARD_REFUND		= 13,
	      USER_TO_USER_TRANSFER		= 14,
	      TELEGRAPHIC_TRANSFER		= 15,
	      CREDIT_CARD_CHARGEBACK	= 16,
	      VOUCHERS_CREATED			= 17,
	      VOUCHERS_CANCELLED		= 18,
	      CURRENCY_CONVERSION		= 19,
	      SYSTEM_SMS_CHARGE			= 20,
	      BANK_TRANSFER				= 21,
	      BANK_TRANSFER_REVERSAL	= 22,
	      CHATROOM_KICK_CHARGE		= 23,
	      CREDIT_EXPIRED			= 24,
	      WESTERN_UNION				= 25,
	      WESTERN_UNION_REVERSAL	= 26,
	      EMOTICON_PURCHASE			= 27,
	      CONTENT_ITEM_PURCHASE 	= 28,
	      CONTENT_ITEM_REFUND		= 29,
	      DISCOUNT_TIER_ADJUSTMENT	= 30,
	      SUBSCRIPTION_CREDIT		= 31,
	      AVATAR_PURCHASE			= 32,
	      GAME_ITEM_PURCHASE		= 33,
	      GAME_REWARD				= 34,
	      MERCHANT_REVENUE_TRAIL	= 36,
	      BLUE_LABEL_ONE_VOUCHER	= 40,
	      VIRTUAL_GIFT_PURCHASE		= 41,
	      BOT_START					= 42,
	      GAME_START				= 43,
	      GAME_START_REVERSAL		= 44,
	      GAME_JOIN					= 45,
	      GAME_JOIN_REVERSAL		= 46,
	      POT_ENTRY					= 47,
	      POT_PAYOUT				= 48,
	      POT_ENTRY_REVERSAL		= 49,
	      MERCHANT_GAME_TRAIL		= 50,
		  MERCHANT_THIRD_PARTY_APP_TRAIL = 51,
	      MANUAL					= 99,
	      ACCOUNT_ENTRY_INVITE		= 200;


	/***** USER *****/

	// account types
	const MIG33 = 1,
	              MIG33_MERCHANT = 2,
	              MIG33_TOP_MERCHANT = 3,
	              MIG33_PREPAID_CARD = 4;



	/***** Voucher *****/

	// status codes
	const VOUCHER_INACTIVE = 0,
	      VOUCHER_ACTIVE = 1,
	      VOUCHER_CANCELLED = 2,
	      VOUCHER_REDEEMED = 3,
	      VOUCHER_EXPIRED = 4,
	      VOUCHER_FAILED = 5;

	private static $VOUCHER_STATUS_CODES = array(
	    self::VOUCHER_INACTIVE => 'INACTIVE',
	    self::VOUCHER_ACTIVE => 'ACTIVE',
	    self::VOUCHER_CANCELLED => 'CANCELLED',
	    self::VOUCHER_REDEEMED => 'REDEEMED',
	    self::VOUCHER_EXPIRED => 'EXPIRED',
	    self::VOUCHER_FAILED => 'FAILED'
	);

	// 213 = United States
	const DEFAULT_COUNTRY = 213;

	// call types
	const SMS_CALLBACK = 1,
	      MIDLET_CALLBACK = 2,
	      WEB_CALLBACK = 3,
	      WAP_CALLBACK = 4,
	      TOOLBAR_CALL = 5,
	      MIDLET_CALL_THROUGH = 6,
	      DIRECT_CALL_THROUGH = 7,
	      MISSED_CALL_CALLBACK = 8,
	      MIDLET_ANONYMOUS_CALLBACK = 9;

	// call status
	const CALL_PENDING = 0,
	      CALL_IN_PROGRESS = 1,
	      CALL_COMPLETED = 2,
	      CALL_FAILED = 3;


	// wordpress wap help pages
	const MERCHANT_HELP_OVERVIEW_PAGE_ID = 5042,
		  MERCHANT_HELP_HOW_MUCH_CAN_YOU_MAKE_PAGE_ID = 5259,
		  MERCHANT_HELP_SALES_KIT_PAGE_ID = 5283,
		  MERCHANT_HELP_SELLING_IS_EASY_PAGE_ID = 5270,
		  MERCHANT_HELP_SUCCESS_STORIES_PAGE_ID = 5275,
		  MERCHANT_HELP_TESTIMONIAL_NIGHT020_PAGE_ID = 5277,
		  MERCHANT_HELP_TESTIMONIAL_TUSOQUERO_PAGE_ID = 5279,
		  MERCHANT_HELP_TESTIMONIAL_USYCUTE323_PAGE_ID = 5281,
		  MERCHANT_HELP_MIG33_SERVICES_ID = 5403,
		  MERCHANT_HELP_SMS_CALL_BACK_PAGE_ID = 5425,
		  MERCHANT_HELP_SMS_THROWBACK_PAGE_ID = 5427,
		  MERCHANT_HELP_EVERYONE_WITH_A_MOBILE_PHONE_PAGE_ID = 5430,
		  MERCHANT_HELP_VOUCHER_PAGE_ID = 5419,
		  MERCHANT_HELP_NEW_MIG33_USERS_PAGE_ID = 5434,
		  MERCHANT_HELP_CURENT_MIG33_USERS_PAGE_ID = 5439,
		  MERCHANT_HELP_WHO_ARE_YOUR_CUSTOMERS_PAGE_ID = 5445,
		  MERCHANT_HELP_CONVENIENCE_PAGE_ID = 5459,
		  MERCHANT_HELP_COMMUNITY_PAGE_ID = 5466,
		  MERCHANT_HELP_CHEAP_CALLS_PAGE_ID = 5470,
		  MERCHANT_HELP_CUSTOMER_BENEFITS_PAGE_ID = 5476,
		  MERCHANT_HELP_SALES_TIPS_PAGE_ID = 5424,
		  MERCHANT_HELP_TOP_MERCHANTS_PAGE_ID = 5496,
		  MERCHANT_HELP_MERCHANT_MENTOR_PROGRAM_PAGE_ID = 5514,
		  MERCHANT_HELP_THE_MIG33_OPPORTUNITY_PAGE_ID = 5517,
		  MERCHANT_HELP_HOW_IT_WORKS_PAGE_ID = 5520,
		  MERCHANT_HELP_REWARDS_PROGRAM_PAGE_ID = 8576,
		  HELP_ABOUT_MIG33_PAGE_ID = 5306;

	// wordpress midlet help pages
	const MERCHANT_MIDLET_HELP_HOW_MUCH_YOU_CAN_MAKE_PAGE_ID = 5574,
		  MERCHANT_MIDLET_HELP_HOW_IT_WORKS_PAGE_ID = 5583,
		  MERCHANT_MIDLET_HELP_TESTIMONIAL_TUSOQUERO_PAGE_ID = 5585,
		  MERCHANT_MIDLET_HELP_TESTIMONIAL_NIGHT020_PAGE_ID = 5587,
		  MERCHANT_MIDLET_HELP_TESTIMONIAL_USYCUTE323_PAGE_ID = 5589,
		  MERCHANT_MIDLET_HELP_TESTIMONIAL_SUCCESS_STORIES_PAGE_ID = 5591,
		  MERCHANT_MIDLET_HELP_SALES_TIPS_PAGE_ID = 5636,
		  MERCHANT_MIDLET_HELP_SALES_KIT_PAGE_ID = 5641,
		  MERCHANT_MIDLET_HELP_WHO_ARE_YOUR_CUSTOMERS_PAGE_ID = 5643,
		  MERCHANT_MIDLET_HELP_CUSTOMER_BENEFITS_PAGE_ID = 5644,
		  MERCHANT_MIDLET_HELP_EVERYONE_WITH_A_MOBILE_PHONE_PAGE_ID = 5648,
		  MERCHANT_MIDLET_HELP_NEW_MIG33_USERS_PAGE_ID = 5650,
		  MERCHANT_MIDLET_HELP_CURRENT_MIG33_USERS_PAGE_ID = 5652,
		  MERCHANT_MIDLET_HELP_SMS_CALL_BACK_PAGE_ID = 5658,
		  MERCHANT_MIDLET_HELP_SMS_THROWBACK_PAGE_ID = 5660,
		  MERCHANT_MIDLET_HELP_CUSTOMER_BENIFITS_PAGE_ID = 5644,
		  MERCHANT_MIDLET_HELP_CHEAP_CALLS_PAGE_ID = 5669,
		  MERCHANT_MIDLET_HELP_COMMUNITY_PAGE_ID = 5667,
		  MERCHANT_MIDLET_HELP_CONVENIENCE_PAGE_ID = 5665,
		  MERCHANT_MIDLET_HELP_VOUCHER_PAGE_ID = 5678,
		  MERCHANT_MIDLET_HELP_THE_MIG33_OPPORTUNITY_PAGE_ID = 5579,
		  MERCHANT_MIDLET_HELP_REWARDS_PROGRAM_PAGE_ID = 8573,
		  HELP_MIDLET_ABOUT_MIG33_PAGE_ID = 5764;

	const BONUS_CALCULATOR_RATES_PAGE_ID = 12134;

	// user settings
	const USER_SETTING_ANONYMOUS_CALL = 1,
		  USER_SETTING_MESSAGE = 2,
		  USER_SETTING_SECURITY_QUESTION = 3;

	// security settings
	const SECURITY_SETTING_PASSWORD = 1,
		  SECURITY_SETTING_MOBILE_NUMBER = 2;

	const MERCHANT_TAG_ACTIVE = 1
	      , MERCHANT_TAG_INACTIVE = 0
	      , MERCHANT_TAG_PENDING = 2;

	//Merchant Preffered Languages:
	const ENGLISH='english'
		, ARABIC ='arabic'
		, INDONESIA = 'indonesia'
		, HINDI = 'hindi'
		, URDU = 'urdu';

	private static $MERCHANT_LANGUAGES = array(
		  self::ENGLISH 	=> 'English'
		, self::ARABIC 		=> 'Arabic'
		, self::INDONESIA	=> 'Bahasa Indonesia'
		, self::HINDI		=> 'Hindi'
		, self::URDU		=> 'Urdu'
	);

	const MERCHANT_CAMPAIGN_MESSAGE_TILL_LOGIN_COUNT = 10;

	const MERCHANT_CAMPAIGN_DEFAULT_REFERER = "Admob_campaign_2012";

	/***** We Need Public Static Variables Instead Of Constants For Pages That Will Get Translated *****/

	// Merchant - Midlet Help Pages
	public static $MERCHANT_MIDLET_HELP_MERCHANT_MENTOR_PROGRAM_PAGE_ID = 5598;
	public static $MERCHANT_MIDLET_HELP_OVERVIEW_PAGE_ID = 5573;

	/**
	* @name get_value
	* @parameter $name string - name of constant to be retrieved
	*/
	public static function get_value($name)
	{
	    if( isset(self::$$name) )
			return self::$$name;
	    elseif( defined('self::'.$name) )
	        return constant('self::'.$name);
		else
			return null;
	}
}

// Detect Language & Point To The Correct WordPress Page ID
switch(Language::get_instance()->get_language_pack())
{
	case 'id_ID':
		// Merchant - Midlet Help Pages
		Constants::$MERCHANT_MIDLET_HELP_MERCHANT_MENTOR_PROGRAM_PAGE_ID = 14961;
		Constants::$MERCHANT_MIDLET_HELP_OVERVIEW_PAGE_ID = 14931;
		break;
}
?>