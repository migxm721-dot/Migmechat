<?php
	fast_require("CallResult", get_framework_common_directory() . "/call_result.php");
    fast_require("DateDifference", get_framework_common_directory() . "/date_time_difference.php");

	/**
	*
	* Show the error message
	*
	**/
	function show_error($message)
	{
		print '<div class="message-box message-error">'.$message.'</div>';
	}

	/**
	*
	* Show the success message
	*
	**/
	function show_success($message)
	{
		print '<div class="message-box message-success">'.$message.'</div>';
	}

	/**
	*
	* Show the information message
	*
	**/
	function show_info($message)
	{
		print '<div class="message-box message-info">'.$message.'</div>';
	}

	/**
	*
	* Show the focus message
	*
	**/
	function show_focus($message, $el_id=null)
	{
		$id_str = "";
		if($el_id !== null)
		{
			$id_str = 'id="'.$el_id.'"';
		}

		print '<div class="message-box message-focus"'.$id_str.' >'.$message.'</div>';
	}

	/**
	*
	* Show the user avatar
	*
	**/
	function show_avatar($avatar_filename, $data, $width=0)
	{
		if($width!=0)
			$data['width'] = $width;

		//$avatar_created = get_value_from_array("avatar_created", $data, "boolean", false);
		$data["avatar_filename"] = $avatar_filename;

		Modules::include_module("avatar", $data);
	}

	/**
	*
	*	Function to show group avatar
	*
	**/
	function show_group_avatar($avatar_filename, $data, $width = 48)
	{
		$data["avatar_filename"] = $avatar_filename;
		$data['width'] = $width;
		Modules::include_module("group_avatar", $data);
	}

	/**
	*
	* Show small user avatar
	*
	**/
	function show_small_avatar($avatar_filename, $data)
	{
		Modules::include_module("avatar", array("avatar_filename" => $avatar_filename, "width" => $data['width']));
	}

	/**
	*
	* Creates an ellipsis from a string length
	*
	**/
	if(!function_exists('ellipsis'))
	{
		function ellipsis( $string, $length )
		{
			if(function_exists('mb_strlen') && function_exists('mb_substr'))
			{
				if($length > mb_strlen($string, 'UTF-8'))
					return $string;

				$substring = mb_substr($string, 0, $length - 3, 'UTF-8');
				return $substring."...";
			}
			else
			{
				if($length > strlen($string))
					return $string;

				$substring = substr($string, 0, $length - 3);
				return $substring."...";
			}
		}
	}

	/**
	*
	* Get the word "A" or "An" depending following $word
	*
	**/
	if(!function_exists('getAorAn'))
	{
		function getAorAn($word, $uc=false)
		{
			$vowels = array("a", "e", "i", "o", "u", "A", "E", "I", "O", "U");
			if(empty($word)) return ($uc?"A":"a");
			$fc = $word{0};

			$ret = $uc?"A":"a";
			if( in_array($fc, $vowels) )
			{
				$ret = $uc?"An":"an";
			}
			return $ret;
		}
	}

	/**
	*
	* Make a soap call
	*
	**/
	function make_soap_call($method_name, $parameters)
	{
		$call_result = new CallResult();
		try
		{
			$call_result->data = soap_call_ejb($method_name, $parameters);
			return $call_result;
		}
		catch(Exception $e)
		{
			$call_result->set_error();
			$call_result->message = $e->getMessage();
			return $call_result;
		}
	}

	/**
	*
	* Formats the date for display
	*
	**/
	function format_date_string($date)
	{
		return date('M y', $date);
	}
	
	/**
	*
	* Get win id
	*
	**/
	function get_win_id()
	{
		return get_attribute_value("window_id");
	}

	function backslashit($string) {
		$string = preg_replace('/^([0-9])/', '\\\\\\\\\1', $string);
		$string = preg_replace('/([a-z])/i', '\\\\\1', $string);
		return $string;
	}
	
	function get_label_image_tag($image, $params = array())
	{
		
		global $migbo_server_root;
		$valid_labels = array( 'v'  => _('Verified')
		                      ,'s'  => _('Staff')
		                      ,'m'  => _('Merchant')
		                      ,'mt' => _('Merchant Mentor')
		                      ,'a'  => _('Admin')
		                      ,'ga' => _('Group Admin')
		                      ,'ca' => _('Chatroom Admin'));
		
		$image = strtolower($image);
		if(! array_key_exists($image, $valid_labels)) 
		{
			return null;
		}

		$size = array_key_exists("size", $params) && in_array($params['size'], array(16,24,48)) ? $params['size'] : 16;
		$root_url = array_key_exists("root_url",$params) ? $params['root_url'] : $migbo_server_root;
		return sprintf("<img src='%s/b/resources/img/labels/%sx%s/%s.png' title='%s' alt='%' width='%s' height='%s' />", $root_url, $size, $size, $image, $valid_labels[$image], $valid_labels[$image], $size, $size); 
	}
	
	
	function get_gender_display_string($gender_abbr) 
	{
		switch(strtolower($gender_abbr))
		{
			case 'm':
				return _('Male');
			
			case 'f':
				return _('Female');
			
			default:
				return null;	
		}
	}
	
?>