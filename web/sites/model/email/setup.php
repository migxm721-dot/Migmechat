<?php
	class SetupModel extends Model
	{
		public function get_data($model_data)
		{
			$username = get_value_from_array('session_user', $model_data);
			$password = $model_data['session_user_detail']->get_password();
			$setup = get_value('setup', 'string', _('No'));

			try
			{
				if($setup == _('Yes'))
				{
					if(checkEmailCompatibleUsername($username) && checkEmailReservedAlias($username))
					{
						try
						{
							@mail_create_account($username, $password);
							return array('success' => sprintf(_('Your migme email address "%s@%s" has been setup.'), $username, $GLOBALS['imap_domain']));
						}
						catch(Exception $ex)
						{
							return array('error' => sprintf(_('We have problem setting up your migme email address "%s@%s".'), $username, $GLOBALS['imap_domain']));
						}
					}
					else
					{
						return array('error' => _('Your username is email incompatible. We hope to bring you this feature soon.'));
					}
				}
			}
			catch(Exception $e)
			{
				return array('error' => $e->getMessage());
			}
		}
	}
?>