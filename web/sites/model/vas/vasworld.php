<?php
	fast_require("PartnerDAO", get_dao_directory() . "/partner_dao.php");

	fast_require("MobileCommonTranslator", get_library_directory() . "/translator/translator/mobilecommon_translator.php");
	fast_require("MobileNewTranslator", get_library_directory() . "/translator/translator/mobilenew_translator.php");
	fast_require("MobileOldTranslator", get_library_directory() . "/translator/translator/mobileold_translator.php");
	fast_require("MobileDataTranslator", get_library_directory() . "/translator/translator/mobiledata_translator.php");

	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");

	class VasworldModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user_id = get_value_from_array('session_user_id', $model_data, 'integer');

            // retrieve user agent, check query string as well in case user agent is passed in manually during testing
            // user agent string should be of the form "J2MEv4.30.302"
            // see table partnerbuild for details
            /*
            $user_agent = isset( $_SERVER['HTTP_UA'] ) ?
                    $_SERVER['HTTP_UA'] :
                    (isset($_GET['build_user_agent']) ?
                        $_GET['build_user_agent'] :
                        ""
                    );
			*/

			$user_agent = '';
			if (empty($_GET['user_agent']))
			{
				$result = explode(' ', $_SERVER['HTTP_USER_AGENT']);
				$user_agent = $result[0];
			}
			else
			{
				$user_agent = $_GET['user_agent'];
			}

            if (empty($user_agent))
            {
                throw new Exception("Unable to identify build to determine partner");
            }

			/*
			 * VasWorld
			 */
			$partner_dao = new PartnerDAO();

			$agreement = $partner_dao->get_agreement_by_user_agent($user_agent);

            // get partner id based on the client's user agent
            $partner = $partner_dao->get_partner_by_agreement_id( $agreement->id );
            if ( empty($partner) )
            {
                // error 400 bad request
                throw new Exception("No partner is linked to this build");
            }

            $partner_id = $partner->id;

            // check if current mig user is a member of the current partner (special treatment for them)
			$partner_user = $partner_dao->get_partner_user($partner_id, $session_user_id);

			$view_draft = false;
			if(!empty($partner_user))
			{
				if($partner_user->is_admin() || $partner_user->is_editor())
				{
                    // make sure there is a draft to display before enabling the link
                    $draft = $partner_dao->get_vasworld_draft_by_agreement_id($agreement->id);
                    if (!is_null($draft))
                    {
					    $view_draft = true;
                    }
				}
			}

            $vasworld = false;

			// Check if the vasWorld in in memcache
			if (is_midlet_view() || is_mre_view())
			{
				if (ClientInfo::is_midlet_version_4_or_higher() || is_mre_view())
				{
					$type = "midlet/new";
				}
				else
				{
					$type = "midlet/old";
				}

                $key = "VAS/" . $partner_id . "/" . $type;
                $vasworld = Memcached::get_instance()->get($key);
			}


			if(true || $vasworld == false)
			{
                // Fetch and process the data
				$vasworld = $partner_dao->get_published_vasworld_by_agreement_id($agreement->id);

                if (is_midlet_view() || is_mre_view())
				{
					$translator = new MobileCommonTranslator();
					$text = $translator->parse($vasworld->content);

					if (ClientInfo::is_midlet_version_4_or_higher())
					{
						$translator = new MobileNewTranslator();
					}
					else
					{
						$translator = new MobileOldTranslator();
					}

                    $vasworld->content = $translator->parse($text);
				}
			}

			$translator = new MobileDataTranslator();
			$vasworld->content = $translator->parse($vasworld->content, $model_data);

			return array('vasworld'=>$vasworld, 'view_draft'=>$view_draft);
		}
	}
?>