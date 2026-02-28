<?php
	abstract class Validator
	{
		/*
		*
		* Validate
		*
		**/
		public function validate($data, ValidatorError &$error, $args = array())
		{
			$this->validate_data($data, $error, $args);
			if( $error->is_error )
			{
				return $this->get_error_view($data, $error, $args);
			}
		}

		/**
		*
		* Child validate
		*
		**/
		public abstract function validate_data($data, ValidatorError &$error);

		/**
		*
		* Get the view if there is an error
		*
		**/
		public abstract function get_error_view($data, ValidatorError &$error);

		/**
		*
		* Validate the string for length and empty
		*
		**/
		protected function check_string($string, $length, $empty_valid = true)
		{
			if( strlen($string) > $length ) return false;

			if( ($empty_valid==false) && empty($string) ) return false;

			return true;
		}

		/**
		*
		* Check if the string is empty
		*
		**/
		protected function check_empty_string($string)
		{
			return empty($string);
		}

		protected function check_currency_amount($amount)
		{
			return preg_match('/^[0-9]+(.[0-9]{1,2})?$/', $amount);
		}
	}

	class ValidatorError
	{
		public $is_error = false;
		public $errors = array();

		/**
		*
		* Set the error for a key
		*
		**/
		public function set_error($key, $error_message)
		{
			$this->is_error = true;
			$this->errors[$key] = $error_message;
		}

		public function is_error($key)
		{
			return array_key_exists($key, $this->errors);
		}
	}
?>