<?php
	fast_require("MerchantDAO", get_dao_directory() . "/merchant_dao.php");

	class TagsModel extends Model{

		public function get_data($model_data){

			$page = get_attribute_value('page','integer',1);
			$records_per_page = 20;
			
			$merchant_dao = new MerchantDAO();
			$result = $merchant_dao->get_merchant_tag_stats($model_data['session_user_detail']->userID, $page, $records_per_page);
			
			$data['tags'] = $result['tags'];
			$num_pages = ceil($result['total_tags']/$records_per_page);
			
			$exchange_rate=$merchant_dao->get_exchange_rate($model_data['session_user_detail']->currency,'USD');
			$data['currency_code'] = $model_data['session_user_detail']->currency;
			$data['page'] = $page;
			$data['num_pages'] = $num_pages;

			return $data;
		}

	}
?>