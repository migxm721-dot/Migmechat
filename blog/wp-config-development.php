<?php
/**
 * The base configurations of the WordPress.
 *
 * This file has the following configurations: MySQL settings, Table Prefix,
 * Secret Keys, WordPress Language, and ABSPATH. You can find more information
 * by visiting {@link http://codex.wordpress.org/Editing_wp-config.php Editing
 * wp-config.php} Codex page. You can get the MySQL settings from your web host.
 *
 * This file is used by the wp-config.php creation script during the
 * installation. You don't have to use the web site, you can just copy this file
 * to "wp-config.php" and fill in the values.
 *
 * @package WordPress
 */

// ** MySQL settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define('DB_NAME', 'wordpress-blog');

/** MySQL database username */
define('DB_USER', 'fusion');

/** MySQL database password */
define('DB_PASSWORD', 'abalone5KG');

/** MySQL hostname */
define('DB_HOST', 'dev-db-1');

/** Database Charset to use in creating database tables. */
define('DB_CHARSET', 'utf8');

/** The Database Collate type. Don't change this if in doubt. */
define('DB_COLLATE', '');

/**#@+
 * Authentication Unique Keys and Salts.
 *
 * Change these to different unique phrases!
 * You can generate these using the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}
 * You can change these at any point in time to invalidate all existing cookies. This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define('AUTH_KEY',         'IV_VU,3@S:Y#m{u~_h.$@}HFLUt3W{}UW|:,:9pR[j-2Pyr0>F#nW++yO?cxxOv`');
define('SECURE_AUTH_KEY',  'kHuI]G(%Z3((|cFsHjz;#%Ygf+[ST.8m~u~}9.`3|xxMLt0Cn<xa)i;D6NPot1oK');
define('LOGGED_IN_KEY',    '7PaA5Gj=66A#o/*k/bZW3 G?8-eh!-;gtHD]*kP_TSjk5zm++*ye:U$D8  [RS8i');
define('NONCE_KEY',        'B^s:nsh:`!<dK/*n(Bos_Hsit^1+~3~gvni?^7@v;[6w@7As7qp8:.W/hte62U%7');
define('AUTH_SALT',        ')H|j<9L-]Iu*1?;5&JbV6Mu;<rYPa)C5XdRH95m5q+J*-T@GMd.Q2|Z=Y]5#}?(J');
define('SECURE_AUTH_SALT', '-Nr]D,3,e!||KRz+d*hy+Sf8_&##YnZZ-9nKUUF[AT|~7UD=Q9sfq6)RZJ7(CXUe');
define('LOGGED_IN_SALT',   '83CJS~}yj@.1|1kZANvF]lT+LRm?_C.Pugp?zRY`+w-.2B(c+wm;QA`VyNz.@wMP');
define('NONCE_SALT',       'M`tUM*XtX&FUl3r|,9XzwTkgko#*iT>`o]q$[F)[60y8Y./]jYLY-57SzPB]R?sD');

/**#@-*/

/**
 * WordPress Database Table prefix.
 *
 * You can have multiple installations in one database if you give each a unique
 * prefix. Only numbers, letters, and underscores please!
 */
$table_prefix  = 'wp_';

/**
 * WordPress Localized Language, defaults to English.
 *
 * Change this to localize WordPress. A corresponding MO file for the chosen
 * language must be installed to wp-content/languages. For example, install
 * de_DE.mo to wp-content/languages and set WPLANG to 'de_DE' to enable German
 * language support.
 */
define('WPLANG', '');

/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 */
define('WP_DEBUG', false);

/** Number of Post Revision to store **/
define('WP_POST_REVISIONS', 10);

/** Number of seconds before autosave kicks in **/
define('AUTOSAVE_INTERVAL', 600);

/** Memcache **/
define('WP_CACHE', false);

/** Memcache Servers **/
global $memcached_servers;
$memcached_servers = array('default' => array('dev-db-1:11216'));

/** We Need 128M */
define('WP_MEMORY_LIMIT', '128M');


/** mig33 custom variables */

// migbo
define('MIGBO_DATASVC_URL', 'http://dev-app-1:8088/fusion-rest/migbo-datasvc-proxy');
define('MIGBO_URL', 'http://migbo.localhost-devlab.projectgoth.com');
define('MIGBO_IMG_URL', 'http://imglab.projectgoth.com');
define('MIGBO_USER_ID', 6);

// mig33 web apps
define('MIGCORE_URL', 'http://corporate.devlab.projectgoth.com');
define('LOGIN_URL', 'https://login.devlab.projectgoth.com');
define('REGISTER_URL', 'https://register.devlab.projectgoth.com');
define('WAP_URL', 'http://m.corporate.devlab.projectgoth.com');

// download urls
define('J2ME_DOWNLOAD_URL', MIGCORE_URL . '/wap2/v4_60/24x24/mig33v46.jad');
define('ANDROID_DOWNLOAD_URL', MIGCORE_URL . '/wap2/android/v3_10/mig33Droid.apk');
define('BB_DOWNLOAD_URL', MIGCORE_URL . '/wap2/blackberry/v5_00/mig33BB.jad');

// other urls
define('MERCHANT_URL', 'http://merchant.devlab.projectgoth.com');
define('DEVELOPER_URL', 'http://developer.devlab.projectgoth.com:9902');
define('HELP_URL', 'http://blog.devlab.projectgoth.com/support');
define('MIGAZINE_URL', 'http://migazine.devlab.projectgoth.com');

define('SHOW_MIGBO_FEED', false);

define('ABSPATH', dirname(__FILE__) . '/');

require_once(ABSPATH . 'wp-settings.php');
/* That's all, stop editing! Happy blogging. */