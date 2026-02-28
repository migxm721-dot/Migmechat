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
define('DB_USER', 'webnn');

/** MySQL database password */
define('DB_PASSWORD', 'lfXVi0Zn5tNxWHSrHQ98UU6xRu4zPBk');

/** MySQL hostname */
define('DB_HOST', 'wpcl01');

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
define('AUTH_KEY',         '}&]?PVQC-[V@&+1yU(@W*j#Vw1Qvmhfv:5>&e$;(90%9[k4gV7}m8NM<RKKQQ2Rb');
define('SECURE_AUTH_KEY',  'J]oIgza oqAhSk/_8GhB4I2Sg`6{ldBWctVK5%U,J*SXg8HR;=#n:jnuF<2fl9bD');
define('LOGGED_IN_KEY',    'G9y$$/i { ]cVOE=K5!c6,jBo~(wP?`C)20k2ylJFH7y1Il8}s|HtbSJ[}d<{{Xc');
define('NONCE_KEY',        'Sxk`qR)f;w^8v=(Lb2h8nVG&+)`Ppc[#E];s-R#1mh@hd` T.U2B-nUZ+M^2UyN.');
define('AUTH_SALT',        '}}@Ar~<`sZ7Og-q|6cRqh-_#_ATNYFJ#F?phNS@,-$1&TT.URim!kOAIqyPJ0-TP');
define('SECURE_AUTH_SALT', '=~1Me>$UzkeI+.V-92O(B$mS /$A`{hO|SPK+F~vgSnN-D{ljhA!Z3G}}AnfWtG~');
define('LOGGED_IN_SALT',   'e+vU*D]31]bMP%#-|f xg76,TYemEExSa8FFR&+P[UfJ)%0rB0<N!^)qM-$X-Z_E');
define('NONCE_SALT',       'I_|oX[~LYS5xEP[=unhEG(0[x3Mq`}mU{u].AL?8he}`w0%;:w|?)d3Z&P}p?9Ac');

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
define('WP_CACHE', true);

/** Memcache Servers **/
global $memcached_servers;
$memcached_servers = array('default' => array('10.3.1.126:11211', '10.3.1.127:11211'));

/** We Need 128M */
define('WP_MEMORY_LIMIT', '128M');

/** mig33 custom variables */

// migbo
define('MIGBO_DATASVC_URL', 'http://jboss.vip.vljb:8080/fusion-rest/migbo-datasvc-proxy');
define('MIGBO_URL', 'http://b.mig33.com');
define('MIGBO_IMG_URL', 'http://img.mig33.com');
define('MIGBO_USER_ID', 141664257);

// mig33 web apps
define('MIGCORE_URL', 'http://www.mig33.com');
define('LOGIN_URL', 'https://login.mig33.com');
define('REGISTER_URL', 'https://register.mig33.com');
define('WAP_URL', 'http://wap.mig33.com');

// download urls
define('J2ME_DOWNLOAD_URL', MIGCORE_URL . '/wap2/v4_60/24x24/mig33v46.jad');
define('ANDROID_DOWNLOAD_URL', MIGCORE_URL . '/wap2/android/v3_10/mig33Droid.apk');
define('BB_DOWNLOAD_URL', MIGCORE_URL . '/wap2/blackberry/v5_00/mig33BB.jad');

// other urls
define('MERCHANT_URL', 'http://merchant.mig33.com');
define('DEVELOPER_URL', 'https://developer.mig33.com');
define('HELP_URL', 'http://blog.mig33.com/support');
define('MIGAZINE_URL', 'http://migazine.tv');

define('SHOW_MIGBO_FEED', true);

/** Absolute path to the WordPress directory. */
if ( !defined('ABSPATH') )
        define('ABSPATH', dirname(__FILE__) . '/');

/** Sets up WordPress vars and included files. */
require_once(ABSPATH . 'wp-settings.php');

/* That's all, stop editing! Happy blogging. */