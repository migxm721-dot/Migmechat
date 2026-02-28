<?php
	fast_require("RestJsonResponse", get_library_directory() . "/rest/rest_json_response.php");
    fast_require("OAuthServer", get_library_directory() . "/oauth/OAuthServer.php");
    fast_require("OAuthStore", get_library_directory() . "/oauth/OAuthStore.php");
    fast_require("OAuthDAO", get_dao_directory() . "/oauth_dao.php");

	class OauthController
	{
		protected $response;

		public function __construct()
		{
                /*
                 * Always announce XRDS OAuth discovery
                 */
                	header('X-XRDS-Location: http://' . $_SERVER['SERVER_NAME'] . '/services.xrds');

                        $this->response = new RestJsonResponse();

                        $inst = SystemProperty::get_instance();

                        $enabled = $inst->get_boolean(SystemProperty::OpensocialEnabled, false);

                        if (!$enabled)
                        {
                                return $this->response->send(503, array("error"=>"Not Implemented"));
                        }
		}


        public function oauth_hello()
        {
                $authorized = false;
                $server = new OAuthServer();

                $error = "";
                try
                {
                    if ($server->verifyIfSigned())
                    {
                        $authorized = true;
                    }
                }
                catch (OAuthException2 $e)
                {
                        $error = $e->getMessage();
                }

                if (!$authorized)
                {
                    header('HTTP/1.1 401 Unauthorized');
                    header('Content-Type: text/plain');

                    echo "OAuth Verification Failed. " . $error;
                    die;
                }

                // From here on we are authenticated with OAuth.

                header('Content-type: text/plain');
                echo 'OK';
        }

        public function oauth()
        {

                // this is needed so that the OAuthStore MySQL instance is initialized.
                //
                $dao = new OAuthDAO();

                $server = new OAuthServer();

                $action = get_action();

                switch($action)
                {


                // given a consumer_key, return a 'request token'
                case 'request_token':

                    $server->requestToken();
                    exit;

                // exchanges a 'request token' for an 'access token'
                //
                // only possible if the user has 'authorized' the 'request token'
                case 'access_token':

                    $server->accessToken();
                    exit;


                // authorizes a 'request token' so that access tokens
                // can be exchanged via this 'request token'
                case 'authorize':

                    $this->assert_logged_in();

                    try
                    {
                        $server->authorizeVerify();
                        $server->authorizeFinish(true, 1);
                    }
                    catch (OAuthException2 $e)
                    {
                        header('HTTP/1.1 400 Bad Request');
                        header('Content-Type: text/plain');

                        echo "Failed OAuth Request: " . $e->getMessage();
                    }
                    exit;


                default:
                    header('HTTP/1.1 500 Internal Server Error');
                    header('Content-Type: text/plain');
                    echo "Unknown request";
                }
        }

        public function register_submit($model_data)
        {
                $this->assert_logged_in($model_data);

                if ($_SERVER['REQUEST_METHOD'] == 'POST')
                {
                    try
                    {
                        $dao = new OAuthDAO();
                        $store = $dao->getOAuthStore();

                        if (empty($store)) {
                            throw new Exception("Unable to initialize OAuthStore.");
                        }

                        // get user_id from logged-in user's session
                        $user_id = $model_data['session_user_id'];

                        if (empty($user_id) || $user_id <= 0 ) {
                            throw new Exception("Can't find userid for username '".$$model_data['session_user']."'");
                        }

                        // updateConsumer() takes in :
                        // - an array of consumer options
                        // - the userid
                        // - a boolean indicating whether this user should be 'admin' for this consumer key/secret
                        //
                        // consumer options consists of:
                        // - requester_name (required)
                        // - requester_email (required)
                        // - id (e.g. id of the server/application definition - is empty for registration)
                        // - application_uri (optional)
                        // - application_title (optional)
                        // - application_descr (optional)
                        // - application_notes (optional)
                        // - application_type (optional)
                        // - application_commercial (optional)
                        //
                        // since this is the registration, the user is considered an admin, e.g. owner for this key
                        //
                        $key   = $store->updateConsumer($_POST, $user_id, true);

                        $c = $store->getConsumer($key, $user_id);
                        echo 'Your consumer key is: <strong>' . $c['consumer_key'] . '</strong><br />';
                        echo 'Your consumer secret is: <strong>' . $c['consumer_secret'] . '</strong><br />';
                    }
                    catch (OAuthException2 $e)
                    {
                        echo '<strong>Error: ' . $e->getMessage() . '</strong><br />';
                    }
                }


                // if request method is a post, the view is going to be displayed
      }

      /////////////// PRIVATE HELPER METHODS ///////////////////

      private function assert_logged_in($model_data)
      {
      		fast_require("SessionUtilities", get_framework_common_directory() . "/session_utilities.php");
            $is_logged_in = SessionUtilities::is_logged_in();
			$is_admin_user = DEBUG_MODE
				? $model_data['session_user'] == "oauth_dev_user"
				: $model_data['session_user'] == "oauth_prod_user";

            if (!$is_logged_in || !$is_admin_user) {
                header ('HTTP/1.1 403 Access Denied');
                echo 'Access Denied';
                error_log("Access Denied for user [".$model_data['session_user']."] is_admin[$is_admin_user] is_logged_in[$is_logged_in]");
                exit();
            }
      }

      private function assert_request_vars()
      {
            foreach(func_get_args() as $a)
            {
                if (!isset($_REQUEST[$a]))
                {
                    header('HTTP/1.1 400 Bad Request');
                    echo 'Bad request.';
                    exit;
                }
            }
      }

      private function assert_request_vars_all()
      {
            foreach($_REQUEST as $row)
            {
                foreach(func_get_args() as $a)
                {
                    if (!isset($row[$a]))
                    {
                        header('HTTP/1.1 400 Bad Request');
                        echo 'Bad request.';
                        exit;
                    }
                }
            }
      }


    } // class OAuthController
?>
