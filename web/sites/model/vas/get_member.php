<?php
	fast_require("PartnerDAO", get_dao_directory() . "/partner_dao.php");

	class GetMemberModel extends Model
	{
		public function get_data($model_data)
		{
			$partner = $model_data['partner'];
			$user_id = get_attribute_value('user_id', 'integer', 0);

			$partner_dao = new PartnerDAO();
			$member = $partner_dao->get_partner_user($partner->id, $user_id);

			return array('member'=>$member);
		}
	}
?>