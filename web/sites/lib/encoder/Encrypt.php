<?php
fast_require('CI_Encrypt', BASEPATH . 'libraries/Encrypt.php');

function log_message($level = 'error', $message, $php_error = FALSE)
{
	Logger::getLogger('encrypt')->$level($message);
}

class Encrypt extends CI_Encrypt {

	var $encryption_method	= '';

	/**
	 * @var Encrypt
	 */
	private static $instance;

	/**
	 * @return Encrypt
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

	/**
	 * Constructor
	 *
	 * Simply determines whether the mcrypt library exists.
	 *
	 */
	public function __construct()
	{
		$this->encryption_key = SystemProperty::get_instance()->get_string(SystemProperty::SSO_EncryptedSessionIDEncryptionKey, '`L3N5@fEf9WsUfJx%%E!7$ic{7YTErMO');
		$this->_mcrypt_exists = ( ! function_exists('mcrypt_encrypt')) ? FALSE : TRUE;
		if (isset($GLOBALS['encryption_method']))
			$this->encryption_method = $GLOBALS['encryption_method'];
		if (defined('MCRYPT_RIJNDAEL_128'))
			$this->set_cipher(MCRYPT_RIJNDAEL_128);
//		log_message('info', "Encrypt Class Initialized");
	}

	// --------------------------------------------------------------------

	/**
	 * Fetch the encryption key
	 *
	 * Returns it as MD5 in order to have an exact-length 128 bit key.
	 * Mcrypt is sensitive to keys that are not the correct length
	 *
	 * @access	public
	 * @param	string
	 * @return	string
	 */
	function get_key($key = '')
	{
		if ($key == '')
		{
			if ($this->encryption_key != '')
			{
				$key = $this->encryption_key;
			}
		}

		return md5($key);
	}

	// --------------------------------------------------------------------

	/**
	 * Generate an SHA1 Hash
	 *
	 * @access	public
	 * @param	string
	 * @return	string
	 */
	function sha1($str)
	{
		if ( ! function_exists('sha1'))
		{
			if ( ! function_exists('mhash'))
			{
				require_once(BASEPATH.'libraries/Sha1.php');
				$SH = new CI_SHA;
				return $SH->generate($str);
			}
			else
			{
				return bin2hex(mhash(MHASH_SHA1, $str));
			}
		}
		else
		{
			return sha1($str);
		}
	}

	function encode($string, $key = '')
	{
		if ($this->encryption_method === 'plain')
		{
			return base64_encode($string);
		}
		else
		{
			return parent::encode($string, $key);
		}
	}

	function decode($string, $key = '')
	{
		if ($this->encryption_method === 'plain')
		{
			return base64_decode($string);
		}
		else
		{
			return parent::decode($string, $key);
		}
	}

}

// END CI_Encrypt class

/* End of file Encrypt.php */
/* Location: ./system/libraries/Encrypt.php */
