<?php
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");
	fast_require("SystemProperty", get_library_directory() . "/system/system_property.php");
	fast_require("Logger", get_framework_common_directory()."/logger.php");

    /*
     * This class is used to detect and prevent user flooding.
     *
     * Certain user actions are rate-limited (e.g. a user is allowed no more than 10 status updates in 60 seconds).
     *
     * If a user exceeds the rate limit for a particular action, we notify the user's object in ObjectCache which will:
     *   - Disconnect the user,
     *   - Ban them from logging in for one hour, and
     *   - Send a notification email to report_abuse@mig33.com
     */
    class FloodControl
    {
        protected static $FLOOD_CONTROL_NAMESPACE = "FC";
        protected static $RECEIVER_FLOOD_CONTROL_NAMESPACE = "RFC";
        protected static $PERMABAN_FLOOD_CONTROL_NAMESPACE = "PBFC";
        protected static $THIRDPARTY_FLOOD_CONTROL_NAMESPACE = "TPFC";

        protected static $default_rate_limit = array(
			array(10, 60), // 10 per minute
			array(200, 3600), // 200 per hour
			array(2500, 86400), // 2500 per day
		);

        // $permaban_limit = (10,7); means that when user exceeded rate limits 10 times in 7 days, they get permanently banned
		protected static $permaban_limit = array(10,7);


        public static function detect_flooding($username, $controller_name, $action_name, $limits=array())
        {
			if (empty($limits)) $limits = self::$default_rate_limit;

			foreach($limits as $limit)
			{
				$max_hits = $limit[0];
				$duration = $limit[1];

				if ($max_hits <= 0 || $duration <= 0) continue;

				$key = self::$FLOOD_CONTROL_NAMESPACE . '/' . $username . '/' . $controller_name . '/' . $action_name . '/' . $duration;

				$memcache = Memcached::get_instance();

				// If we can add the key it means this is the first hit recorded, so we just return
				if ($memcache->add($key, 1, false, $duration)) continue;

				$hits = $memcache->increment($key);

				if ($hits > $max_hits)
				{
					/** Start permaban limit checking **/
					$permaban_max_limit_exceeded = self::$permaban_limit[0];
					$permaban_duration = self::$permaban_limit[1];

					// 1. Generate memcache keys for entire duration. index 0 is today, index 1 is today-1, etc.
					$permaban_keys = array();
					for ($i=0; $i<$permaban_duration;$i++)
					{
					  $permaban_keys[$i] = self::$PERMABAN_FLOOD_CONTROL_NAMESPACE . '/' . $username .'/'.  date("Ymd", time()-$i*86400);
					}

					// 2. Record this offense.

					if ($memcache->add($permaban_keys[0], 1, false, ($permaban_duration+1)*86400) == FALSE)
					{
						// if false, then key already exists, so we'll just increment it.
							$memcache->increment($permaban_keys[0]);
					}

					// 3. Sum up all offenses within duration
					$num_offenses = array_sum($memcache->get($permaban_keys));

					// 4. if num_offenses is greater than limit, disable this user
					if ($num_offenses > $permaban_max_limit_exceeded)
					{
						$userdao = new UserDAO();
						$reason = "\nFlooding $num_offenses times in $permaban_duration days, banned permanently (".date("Ymd").")";
						$userdao->disable_user_permanently($username, $reason);

						Logger::getLogger("flood.permabans")->info(
							sprintf("%s %s %s/%s %d/%d"
								, $username
								, get_view()
								, $controller_name
								, $action_name
								, $max_hits
								, $duration
							)
						);
					}

					/** End permaban limit checking **/

					// For WAP we need to manually clear the session as well as add the ban to the username in memcache
					if(is_wap_view())
					{
						$memcache->add_or_update(Memcached::$KEYSPACE_BAN_USERNAME.$username, 1, Memcached::$CACHEDURATION_BAN_USERNAME);
						fast_require("SessionUtilities", get_framework_common_directory() . "/session_utilities.php");
						SessionUtilities::destroy_session_in_cache();

						// logging memcache ban / suspend for the wap view
						Logger::getLogger("flood.memcache.suspend")->info(
							sprintf("%s (ip:%s) %s %s/%s hits:%d/%d duration:%d"
								, $username
								, getRemoteIPAddress()
								, get_view()
								, $controller_name
								, $action_name
								, $hits
								, $max_hits
								, $duration
							)
						);
					}
					else
					{
						fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');
						// The user is flooding
						$ice = new IceDAO();
						$ice->disconnect_flooder("Infraction: [" . get_view() . "] " .$controller_name . ":" . $action_name . " exceeded " . $max_hits . "/" . $duration . " secs");
					}
					
					Logger::getLogger("flood.disconnects")->info(
						sprintf("%s %s %s/%s %d/%d"
							, $username
							, get_view()
							, $controller_name
							, $action_name
							, $max_hits
							, $duration
						)
					);
					
					throw new Exception(_("You have been disconnected"));
				}
			}
        }


        /*
         * The difference between detect_flooding_by_receiver is that:
         * - we don't want to disconnect/ban the receiver (since he may be the victim, not the culprit)
         * - we will throw an exception, the sender will get a generic error page (sites/error/error.php)
         */
        public static function detect_flooding_by_receiver($receiver, $sender, $controller_name, $action_name, $limits=array())
        {
			if (empty($limits)) $limits = self::$default_rate_limit;

            $controller_action = $controller_name . '/' . $action_name;

			foreach($limits as $limit)
			{
				$max_hits = $limit[0];
				$duration = $limit[1];

				if ($max_hits <= 0 || $duration <= 0) continue;

				$key = self::$RECEIVER_FLOOD_CONTROL_NAMESPACE . '/' . $receiver . '/' . $controller_name . '/' . $action_name . '/' . $duration;

				$memcache = Memcached::get_instance();

				// If we can add the key it means this is the first hit recorded, so we just return
				if ($memcache->add($key, 1, false, $duration))
					return;

				$hits = $memcache->increment($key);

				// the user has received more than the allowed rate for this controller & action combination
				// e.g. received more than X hits per Y seconds of likes
				if ($hits > $max_hits)
				{
					if (SystemProperty::get_instance()->get_boolean(SystemProperty::LogFloodControlByReceiverEnabled, true))
					{
						Logger::getLogger("flood.control.by.receiver")->info(
							sprintf(
								"dropping %s/%s request on receiver %s by sender %s for limit %d/%d"
								, $controller_name
								, $action_name
								, $receiver
								, $sender
								, $max_hits
								, $duration
							)
						);
					}
					throw new Exception(sprintf(_("Rate Limit exceeded for recipient %s on %s."), $receiver, $controller_action));
				}
			}
        }

        /*
         * Third Party API, we rate limit base on client id, user id and action
         */
        public static function detect_flooding_by_third_party($client_id, $user_id, $action_name, $is_receiver = false)
        {
	    	// currently shares the default rate limits with the sender flood control
            // if this needs to change in the future, then this needs to be refactored.
            // probably, take the entire method into its own class
            $max_hits = FloodControl::$default_rate_limit[0][0];
            $duration = FloodControl::$default_rate_limit[0][1];

            $key = self::$THIRDPARTY_FLOOD_CONTROL_NAMESPACE . '/' . $client_id . '/' . $action_name . '/' . ($is_receiver ? 'R' : 'S') . '/' . $user_id;

   			$memcache = Memcached::get_instance();

			// If we can add the key it means this is the first hit recorded, so we just return
			if ($memcache->add($key, 1, false, $duration))
				return;

			$hits = $memcache->increment($key);

			// the user has received more than the allowed rate for this client & action combination
			if ($hits > $max_hits)
			{
				throw new Exception(sprintf(_('Rate Limit exceeded for %s for %s on %s.'), $user_id, $client_id, $action_name));
			}
        }
    }
?>
