<?php
	fast_require("PartnerDAO", get_dao_directory() . "/partner_dao.php");

	class EditUserModel extends Model
	{
		public function get_data($model_data)
		{
			$partner = $model_data['partner'];
			$user_id = get_attribute_value('user_id', 'integer', 0);
			$membership = get_attribute_value('membership', 'integer', 0);

			$partner_dao = new PartnerDAO();

			$data = array();
			if($partner_dao->set_partner_user($partner->id, $user_id, $membership) == null)
			{
				$data['errors'][] = 'User does not exists!';
			}
			else
			{
				$data['successes'][] = 'User modified successfully';
			}

			return $data;
		}
	}
?>