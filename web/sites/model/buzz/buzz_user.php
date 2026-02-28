<?php
	class BuzzUserModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = $model_data['session_user'];
			$session_user_detail = $model_data['session_user_detail'];
			$contact_id = get_attribute_value('group_id', 'integer', 0);
			$message = get_value('message', 'string', '');
			try
			{
				$message = soap_call_ejb('sendBuzz', array($session_user, $contact_id, $message, getRemoteIPAddress(), getSessionID(), getMobileDevice(), getUserAgent()));
				$model_data['message'] = $message;
			}
			catch(Exception $e)
			{
				$model_data['error'] = $e->getMessage();
			}

			return $model_data;
		}
	}
?>