<?php
	fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
	fast_require("PartnerDAO", get_dao_directory() . "/partner_dao.php");

	class AddUserModel extends Model
	{
		public function get_data($model_data)
		{
			$partner = $model_data['partner'];
			$username = get_attribute_value('username', 'string','');
			$membership = get_attribute_value('membership', 'integer', 0);

			$user_dao = new UserDAO();
			$user_id = $user_dao->get_user_id($username);

			$partner_dao = new PartnerDAO();

			$data = array();
			if($partner_dao->add_partner_user($partner->id, $user_id, $membership) == null)
			{
				$data['errors'][] = 'User does not exists!';
			}
			else
			{
				$data['successes'][] = 'User added successfully';
			}

			return $data;
		}
	}
?>