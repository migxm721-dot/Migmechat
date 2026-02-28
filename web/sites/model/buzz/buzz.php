<?php
	class BuzzModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = $model_data['session_user'];
			$session_user_detail = $model_data['session_user_detail'];
			$contact_id = get_attribute_value('group_id', 'integer', 0);
			$contact_to = get_value('to','string','');
			$contact_pres = get_value('pres', 'integer', 0);

			/*
			 * IF contact_to is empty, then it is the contact id from the contacts list
			 * ELSE contact_to is the username and contact_id is the user id from the profile page
			 * TODO: pls do cleanup the mess
			 */

			try
			{
				// Ensure User Is Mobile Verified
				if(!$session_user_detail->is_mobile_verified()) {
					$model_data['error'] = _('Sorry, you need to authenticate your migme account before you can Buzz another user.');
				}

				if($contact_id != 0)
				{
					// if the contact_to is the username, use getContact based on session user and username
					// else get it using the contact id
					$contactData = (strlen($contact_to) > 0) ?
										soap_call_ejb('getContact', array($session_user, $contact_to)) :
										soap_call_ejb('getContact', array($contact_id));
					$model_data['contactData'] = $contactData;
					$contact_id = (int)$contactData['id'];
					$contact_to = $contactData['fusionUsername'];

					// Make sure the contact is a mig33 contact
					if($contactData['fusionUsername'] == "")
					{
						$model_data['error'] = _('You can only buzz a migme contact.');
					}

					// Is Buzz Possible
					soap_call_ejb('isBuzzPossible', array($session_user, $contact_id));
				}
				else
				{
					$model_data['error'] = _('You can only buzz a migme contact.');
				}

				// Buzz Cost
				$cost = soap_call_ejb("getBuzzCost", array($session_user, $contact_to));
				$model_data['cost'] = $cost;
			}
			catch(Exception $e)
			{
				$model_data['error'] = $e->getMessage();
			}

			$model_data['contact_id'] = $contact_id;
			$model_data['contact_pres'] = $contact_pres;

			return $model_data;
		}
	}
?>