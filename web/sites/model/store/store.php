<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");
	fast_require("UserDetail", get_domain_directory() . "/user/user_detail.php");
	fast_require("Balance", get_domain_directory() . "/account/balance.php");

	class StoreModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$data = array();
			$data['prev_type'] = get_value("ptype", "integer", 0);
			$data['prev_entries'] = get_value("pentries", "integer", 0);
			$data['prev_catid'] = get_attribute_value("parent_catid", "integer", 0);
			$data['prev_page'] = get_value("ppage", "integer", 1);

			return $data;
		}
	}
?>