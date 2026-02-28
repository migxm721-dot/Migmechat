<?php

	class CommonValidator
	{
		const EMAIL_NO_AT                     = 1;
		const EMAIL_LOCAL_INVALID_LENGTH      = 2;
		const EMAIL_LOCAL_DOUBLE_DOTS         = 4;
		const EMAIL_LOCAL_DOT_AT_START_OR_END = 8;
		const EMAIL_LOCAL_INVALID_CHARS       = 16;
		const EMAIL_DOMAIN_INVALID_LENGTH     = 32;
		const EMAIL_DOMAIN_DOUBLE_DOTS        = 64;
		const EMAIL_DOMAIN_INVALID_FORMAT     = 128;

		private $error = 0;

		/**
		* Validate an email address
		* @param	string	$email	The email address to validate
		* @return	boolean	Whether email address is valid
		*/
		public function check_email($email)
		{
		   $this->error = 0;

		   $atIndex = strrpos($email, "@");
		   if (false === $atIndex)
		   {
		      $this->error = self::EMAIL_NO_AT;
		   }
		   else
		   {
		      $domain = substr($email, $atIndex+1);
		      $local = substr($email, 0, $atIndex);
		      $localLen = strlen($local);
		      $domainLen = strlen($domain);

		      if ($localLen < 1 || $localLen > 64)
		      {
		         // local part length exceeded
				  $this->error = self::EMAIL_LOCAL_INVALID_LENGTH;
		      }
		      else if ($domainLen < 4 || $domainLen > 255)
		      {
		         // domain part length invalid
				 // minimum length is 4 because we validate against internet hosts, NOT in local network
				 // that means at last one letter, one dot, and 2 letter for the final domain token
		         $this->error = self::EMAIL_DOMAIN_INVALID_LENGTH;
		      }
		      else if ($local[0] == '.' || $local[$localLen-1] == '.')
		      {
		         // local part starts or ends with '.'
		         $this->error = self::EMAIL_LOCAL_DOT_AT_START_OR_END;
		      }
		      else if (strpos($local, '..') !== false)
		      {
		         // local part has two consecutive dots
		         $this->error = self::EMAIL_LOCAL_DOUBLE_DOTS;
		      }
			  else if (strpos($domain, '..') !== false)
			  {
				 // domain part has two consecutive dots
				 // note: this test is for us to return meaningful error messages
				 // the regex of the next test ensures there cannot be consecutive dots in the domain
				 $this->error = self::EMAIL_DOMAIN_DOUBLE_DOTS;
			  }
			  else if (!preg_match('/^([a-z0-9]([a-z0-9-]*[a-z0-9])?\.)+[a-z]{2,4}$/i', $domain))
			  {
				 // invalid format for domain part
				 $this->error = self::EMAIL_DOMAIN_INVALID_FORMAT;
			  }
		      else if (!preg_match('/^(\\\\.|[A-Za-z0-9!#%&`_=\/$\'*+?^{}|~.-])+$/', str_replace("\\\\","",$local)))
		      {
				 // TODO: revisit this whole validation of invalid characters in local part, this looks dodgy
		         // character not valid in local part unless
		         // local part is quoted
		         if (!preg_match('/^"(\\\\"|[^"])+"$/', str_replace("\\\\","",$local)))
		         {
		            $this->error = self::EMAIL_LOCAL_INVALID_CHARS;
		         }
		      }
		   }

		   return ($this->error == 0);
		}

		public function get_last_error()
		{
			return $this->error;
		}
	}
?>