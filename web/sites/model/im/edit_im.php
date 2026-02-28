<?php
	class EditImModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = $model_data['session_user_detail']->username;//get_value_from_array('session_user', $model_data);
			$session_user_id = get_value_from_array('session_user_id', $model_data, "integer");
			$im_type = get_value('imtype', 'integer', 0);
			$im_name = '';
			$im_username = get_value('username', 'string', '');
			$im_password = get_attribute_value('password', 'string', '');
			$edit = get_value('edit', 'integer', 0);
			try {
				if($edit) {
					if ((strlen($im_username) < 1) || (strlen($im_password) < 1)) {
						$model_data['error'] = _('Please enter both username and password details');
					} else {
						if ($im_type == 2) {
							$im_name = 'MSN';
							$userData['msnUsername'] = removeScriptFrom($im_username);
							$userData['msnPassword'] = removeScriptFrom($im_password);
						}
						if ($im_type == 4) {
							$im_name = 'Yahoo';
							$userData['yahooUsername'] = removeScriptFrom($im_username);
							$userData['yahooPassword'] = removeScriptFrom($im_password);
						}
						if ($im_type == 6) {
							$im_name = 'GTalk';
							$userData['gtalkUsername'] = removeScriptFrom($im_username);
							$userData['gtalkPassword'] = removeScriptFrom($im_password);
						}
						if ($im_type == 7) {
							$im_name = 'Facebook';
							$userData['facebookUsername'] = removeScriptFrom($im_username);
							$userData['facebookPassword'] = removeScriptFrom($im_password);
						}
						$result = soap_call_ejb('addIMDetail', array($session_user, $im_type, $im_username, $im_password));
						$model_data['message'] = sprintf(_('Your %s details have been updated.'), $im_name);
					}
				} else {
					switch($im_type) {
					case 2: // msn
						$cred_type = 11;
						break;
					case 4: //yahoo
						$cred_type = 13;
						break;
					case 6: // gtalk
						$cred_type = 12;
						break;
					case 7: // FB
						$cred_type = 14;
						break;
					}
					fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');
					$ice = new IceDAO();
					$credentials = $ice->get_im_credentials($cred_type);
					if( $credentials != null ) 	{
						$im_username = $credentials->username;
						$im_password = $credentials->password;
					}
				}
				$model_data['im_username'] = $im_username;
				$model_data['im_password'] = $im_password;
				$model_data['edit']= $edit;
			}
			catch(Exception $e) {
				$model_data['error'] = $e->getMessage();
			}
			return $model_data;
		}
	}
?>