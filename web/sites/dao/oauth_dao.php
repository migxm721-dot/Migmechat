<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("Redis", get_framework_common_directory() . "/redis.php");
    fast_require("OAuthStore", get_library_directory() . "/oauth/OAuthStore.php");


	class OAuthDAO extends DAO
    {

        private $oauth_store = null;

        public function __construct()
        {
            $this->oauth_store = OAuthStore::instance('MySQLi', array('conn' => $this->getMasterConnection()->get_connection()));
        }

        public function getOAuthStore() {
            return $this->oauth_store;
        }

        /** the following methods are interfaces to OAuthStore that we are exposing
            via this DAO **/

        public function lookup_consumer($consumer_key) {
            // get the oauth_consumer_key
            $consumer_key = $this->getSlaveConnection()->escape_string($consumer_key);
            $oauth_consumer_record = $this->oauth_store->getConsumer($consumer_key, null, true);
            return $oauth_consumer_record;
        }

        public function lookup_token($consumer_key, $token_type, $token)  {

            // get the oauth_consumer_key
            $consumer_key = $this->getSlaveConnection()->escape_string($consumer_key);
            $token = $this->getSlaveConnection()->escape_string($token);
            $token_type = $this->getSlaveConnection()->escape_string($token_type);

            $token_data = null;
            switch($token_type) {
                case 'access':
                    $token_data = $this->oauth_store->getSecretsForVerify($consumer_key, $token, 'access');
                    break;
                case 'request':
                    $token_data = $this->oauth_store->getSecretsForVerify($consumer_key, $token, 'request');
                    break;
                default:
                    $token_data = null;
                    break;
            }

            return $token_data;
        }

        public function consumer_key_exists($consumer_key, $key_owner_id) {

            if (empty($key_owner_id) || empty($consumer_key)) {
                return null;
            }

            if (!is_int($key_owner_id)) {
                return null;
            }

            $consumer_key = $this->getSlaveConnection()->escape_string($consumer_key);

            // is_admin is set to false so that if the owner_id does not match
            // an OAuthException2 will be thrown
            $this->oauth_store->getConsumer($consumer_key, $key_owner_id, false);
        }

        public function delete_consumer_key($consumer_key, $key_owner_id) {

            if (empty($key_owner_id) || empty($consumer_key)) {
                return null;
            }

            if (!is_int($key_owner_id)) {
                return null;
            }

            $consumer_key = $this->getSlaveConnection()->escape_string($consumer_key);

            // is_admin is set to false so that if the owner_id does not match
            // an OAuthException2 will be thrown
            $this->oauth_store->deleteConsumer($consumer_key, $key_owner_id, false);
        }

        public function create_consumer_key($key_owner_id, $owner_email, $owner_name) {

            if (empty($key_owner_id) || empty($owner_email) || empty($owner_name)) {
                return null;
            }

            if (!is_int($key_owner_id)) {
                return null;
            }

            $key_owner_id = $this->getSlaveConnection()->escape_string($key_owner_id);
            $owner_email = $this->getSlaveConnection()->escape_string($owner_email);
            $owner_name = $this->getSlaveConnection()->escape_string($owner_name);

            // updateConsumer() takes in :
            // - an array of consumer options
            // - the userid
            // - a boolean indicating whether this user should be 'admin' for this consumer key/secret
            //
            // consumer options consists of:
            // - requester_name (required)
            // - requester_email (required)
            // - id (e.g. id of the server/application definition - is empty for new keys)
            // - application_uri (optional)
            // - application_title (optional)
            // - application_descr (optional)
            // - application_notes (optional)
            // - application_type (optional)
            // - application_commercial (optional)
            //
            // since this is the key creation, the user is considered an admin, e.g. owner for this key
            //
            // we are only sending 'requester_name' and 'requester_email' for now, but may use
            // other options in the future

            $consumer = array();
            $consumer['requester_name'] = $owner_name;
            $consumer['requester_email'] = $owner_email;

            $key   = $this->oauth_store->updateConsumer($consumer, $key_owner_id, true);
            $c = $this->oauth_store->getConsumer($key, $key_owner_id);

            return $c;
        }


    }	// end class



?>
