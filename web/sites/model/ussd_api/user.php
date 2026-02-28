<?php

	fast_require('USSDPartnerDAO', get_dao_directory() . '/ussd_partner_dao.php');

	class UserModel extends Model{

		public function get_data($model_data){

			$msisdn = get_value('msisdn', 'string', '');
			$data = array();

			$dao = new USSDPartnerDAO();
			$data['user'] = $dao->get_partner_user($model_data['partner']->id, $msisdn);

			return $data;

		}

	}
?>