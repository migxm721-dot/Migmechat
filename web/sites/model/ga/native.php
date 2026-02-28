<?php
	class NativeModel extends Model
	{
		public function get_data($model_data)
		{
			// 1 Variable Kill Switch
			$disable_ga = 0;

			$ga_pages = array();
			$ga_pages[0] = 'ACTION_NA';
			$ga_pages[1] = 'ACTION_WELCOME1STTIMEUSER_PAGE';
			$ga_pages[2] = 'ACTION_WELCOME_PAGE';
			$ga_pages[3] = 'ACTION_REGISTER_STEP1_PAGE';
			$ga_pages[4] = 'ACTION_LOGIN_PAGE';
			$ga_pages[5] = 'ACTION_CHANGELANGUAGE_PAGE';
			$ga_pages[6] = 'ACTION_REGISTER_STEP2_PAGE';
			$ga_pages[7] = 'ACTION_REGISTER_STEP3_PAGE';
			$ga_pages[8] = 'ACTION_CONNECTIONSETTING_PAGE';
			$ga_pages[101] = 'ACTION_REGISTER_STEP1_NAMEXIST';
			$ga_pages[102] = 'ACTION_REGISTER_STEP1_USEDSUGGESTEDNAME';
			$ga_pages[103] = 'ACTION_REGISTER_STEP2_FAILED';
			$ga_pages[104] = 'ACTION_REGISTER_STEP3_CODEACTIVATED';
			$ga_pages[105] = 'ACTION_REGISTER_STEP3_RESENDSMSCODE';
			$ga_pages[106] = 'ACTION_REGISTER_STEP3_REMINDLATER';

			// Variables passed in
			$show_ga = 1;
			$salt = 'gamerz-';
			$utmr = trim(get_value('utmr'));
			$utmp = trim(get_value('utmp'));
			$utmvid = trim(get_value('utmvid'));
			$java_hash = trim(get_value('hash'));
			$php_hash = md5($salt.$utmvid);

			// If hash does not match, do not proceed further
			if($java_hash != $php_hash || $disable_ga == 1)
			{
				$show_ga = 0;
				$model_data['disable_ga'] = $disable_ga;
				$model_data['show_ga'] = $show_ga;
				return $model_data;
			}

			// Variables
			$ga_params = array();
			$ga_request_urls = array();
			$ga_url = urldecode(googleAnalyticsGetImageUrl('devlab'));
			$ga_url_array = explode('&', $ga_url);
			$ga_url_params = '';

			// Start of the GA URL (includes GA Account Number)
			$ga_url_start = $ga_url_array[0];

			// Remove the first element of the array which contains the URL
			unset($ga_url_array[0]);

			foreach($ga_url_array as $value)
			{
				$ga_key = substr($value, 0, stripos($value,'='));
				$ga_value = substr(stristr($value, '='), 1);
				$ga_params[$ga_key] = $ga_value;
			}

			// Set Referral
			$ga_params['utmr'] = $ga_pages[$ga_params['utmr']];

			// Set Current Path
			$ga_params['utmp'] = $ga_pages[$ga_params['utmp']];

			// Remove Unwanted Variables
			unset($ga_params['c']);
			unset($ga_params['a']);
			unset($ga_params['v']);
			unset($ga_params['hash']);
			unset($ga_params['username']);

			foreach($ga_params as $ga_param_key => $ga_params_value)
			{
				$ga_url_params .= $ga_param_key.'='.$ga_params_value.'&';
			}
			$ga_request_url = $ga_url_start.$ga_url_params.$ga_url_end;

			// Remove Trailing &
			$ga_request_url = substr($ga_request_url, 0, -1);

			$model_data['disable_ga'] = $disable_ga;
			$model_data['show_ga'] = $show_ga;
			$model_data['ga_url'] = $ga_request_url;
			return $model_data;
		}
	}
?>