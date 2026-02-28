<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");
    fast_require("OAuthStore", get_library_directory() . "/oauth/OAuthStore.php");
    fast_require("ThirdPartyApiDAO", get_dao_directory() . "/third_party_api_dao.php");

	class OpensocialDAO extends DAO
    {
        static $APPDATA_REDIS_KEY_TEMPLATE = "U:_UID_:Application:_APPID_";


        private function get_index_key($userid) {
            return str_replace("_UID_",$userid, str_replace("_APPID_","INDEX", self::$APPDATA_REDIS_KEY_TEMPLATE));
        }


        private function create_key($userid, $appid)
        {
            if (empty($userid) || $userid <= 0)
            {
                throw new Exception("Invalid user id ($userid)");
            }

            $dao = new ThirdPartyApiDAO();

            if (!$dao->is_valid_application($appid)) {
                throw new Exception("Invalid application id ($appid)");
            }

            return str_replace("_UID_",$userid, str_replace("_APPID_",$appid, self::$APPDATA_REDIS_KEY_TEMPLATE));
        }

        /**
          * Given a userid, an appid, and a mapping of 'fieldname' -> 'value'
          * updates the contents of the appdata store
          *
          **/
        public function update_appdata_store($userid, $appid, $store)
        {
            if (!$this->user_has_app($userid, $appid))
            {
                throw new Exception ("Application ($appid) is not authorized to access data belonging to User ($userid)");
            }

            $key = $this->create_key($userid, $appid);

            $redis_instance = Redis::get_master_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                throw new Exception ("master redis instance for user $userid not found");
            }

            $now = time();

            $pipe = $redis_instance->pipeline();
            foreach ($store as $fn=>$fv) {
                    if ($fn != "accessed_on" && $fn != "created_on") {
                        $pipe->hset($key, $fn, $fv);
                    }
            }
            $pipe->hset($key, "accessed_on", $now);
            $pipe->execute();

            $redis_instance->disconnect();

            return true;
        }

        /**
          * Delete keys from an appdata store belonging to a user/app combination
          *
          *
          **/
        public function delete_from_appdata_store($userid, $appid, $keys_to_delete)
        {

            if (!$this->user_has_app($userid, $appid))
            {
                throw new Exception ("Application ($appid) is not authorized to access data belonging to User ($userid)");
            }

            $key = $this->create_key($userid, $appid);

            $redis_instance = Redis::get_master_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                throw new Exception ("master redis instance for user $userid not found");
            }

            $now = time();

            $pipe = $redis_instance->pipeline();
            foreach ($keys_to_delete as $fn) {
                    if ($fn != "accessed_on" && $fn != "created_on") {
                        $pipe->hdel($key, $fn);
                    }
            }
            $pipe->hset($key, "accessed_on", $now);
            $pipe->execute();

            $redis_instance->disconnect();

            return true;
        }

        public function get_appdata_keys($userid, $appid)
        {
            if (!$this->user_has_app($userid, $appid))
            {
                throw new Exception ("Application ($appid) is not authorized to access data belonging to User ($userid)");
            }

            $key = $this->create_key($userid, $appid);

            $redis_instance =  Redis::get_slave_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                throw new Exception ("slave redis instance for user $userid not found");
            }

            $now = time();

            $pipe = $redis_instance->pipeline();
            $pipe->hkeys($key);
            $pipe->hset($key, "accessed_on", $now);

            $result = $pipe->execute();

            $redis_instance->disconnect();

            return $result[0]; // return the result of hkeys()
        }

        /**
          * This method WILL CREATE a new redis entry if
          * one does not exist already. Should only be called from start_app
          **/
        public function get_or_create_appdata_store($userid, $appid)
        {
            $key = $this->create_key($userid, $appid);

            $index_key = $this->get_index_key($userid);

            $redis_instance = Redis::get_master_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                throw new Exception ("master redis instance for user $userid not found");
            }

            $now = time();

            $result = $redis_instance->pipeline()
                ->hsetnx($key, "created_on", $now)
                ->hset($key, "accessed_on", $now)
                ->hgetall($key)
                ->sadd($index_key, $appid)
                ->execute();

            $store = $result[2]; // the result of hgetall()

            // be a good boy and disconnect
            $redis_instance->disconnect();

            return $store;
        }

        /**
          * Get the application ID of the apps that this user has played
          **/
        public function get_user_applications($userid)
        {
            $index_key = $this->get_index_key($userid);

            $redis_instance = Redis::get_slave_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                return false; // if no redis slave instance found, return false to indicate error
            }

            $result = $redis_instance->smembers($index_key);

            return $result;
        }

        /**
          * Returns timestamp of when user last played the game
          * identified by $appid
          **/
        public function user_last_played($userid, $appid)
        {
            $key = $this->create_key($userid, $appid);

            $redis_instance = Redis::get_slave_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                return false; // if no redis slave instance found, return false to indicate error
            }

            $last_played =  $redis_instance->hget($key, "accessed_on");

            // be a good boy and disconnect
            $redis_instance->disconnect();

            return $last_played;
        }

        public function user_has_app($userid, $appid) {
            $key = $this->create_key($userid, $appid);

            $redis_instance = Redis::get_slave_instance_for_user_id($userid);

            if (!isset($redis_instance))
            {
                return false; // if no redis slave instance found, this means user does not have app
            }

            $hasapp =  (1 == $redis_instance->exists($key));

            // be a good boy and disconnect
            $redis_instance->disconnect();

            return $hasapp;
        }


    }	// end class



?>
