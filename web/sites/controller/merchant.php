<?php
	fast_require('Constants', get_framework_common_directory() . '/constants.php');
    class MerchantController{

        public function create_voucher_submit($model_data){

			if (!empty($model_data['error'])) {
				return new ControllerMethodReturn('create_voucher', $model_data);
			} else {
				header('Location: ' . get_action_url('voucher_batch', array('batchid'=>$model_data['batchid']) ) );
				exit();
			}
		}

        public function modify_notes($model_data){

			if( !$model_data['is_voucher_batch_owner']){
				return new ControllerMethodReturn('vouchers');
			}

		}

		public function modify_notes_submit($model_data){

			if( $model_data['isSaved'] )
				return new ControllerMethodReturn('voucher_batch', array('batchid' => $model_data['batchId']));

		}

        public function transfer_credit_submit($model_data){

			if( count($model_data['errors']) ){
				$model_data['error_message'] = implode('<br />', $model_data['errors']);
				return new ControllerMethodReturn('transfer_credit', $model_data);
			}

		}

        public function create_user_submit($model_data){

			if( !$model_data['isUserCreated'] )
				return new ControllerMethodReturn('create_user', $model_data);

		}
		
        public function bank_transfer_submit($model_data){

            if( $model_data['error_message'] )
                return new ControllerMethodReturn('bank_deposit_form', $model_data);
            else
            {
                redirect(get_action_url('show_bank_transfer_success_details'
                	, array('det' => 1) + $model_data['url']
                ));
            }
        }

        public function western_union_form_submit($model_data){

            if( count($model_data['errors']) || $model_data['error_message'] )
                return new ControllerMethodReturn('western_union_form', $model_data);
        }

        public function credit_debit_form_submit($model_data){

            if( count($model_data['errors']) || $model_data['error_message'] )
                return new ControllerMethodReturn('credit_debit', $model_data);
        }

        public function merchant_signup_submit($model_data){

            if( !$model_data['isUserCreated'] || $model_data['error_message'] )
                return new ControllerMethodReturn('merchant_signup', $model_data);
			else if (ClientInfo::is_midlet_version_42_or_higher())
			{
				// Custom hack for 4.2 GA tracking
				$_SERVER['REQUEST_URI'] = str_replace('merchant_signup_submit'
					, 'merchant_signup_success'
					, $_SERVER['REQUEST_URI']
				);
				return new ControllerMethodReturn('merchant_signup_success', $model_data);
			}
			else
				redirect(get_action_url('merchant_signup_success'));
        }

		public function merchant_signup_submit_campaign($model_data){

			if( !$model_data['is_merchant_created'] || $model_data['error_message'] )
				return new ControllerMethodReturn('merchant_signup_campaign', $model_data);
			else
				redirect(get_action_url('merchant_signup_success_campaign'));
		}

		public function merchant_campaign(&$model_data)
		{
			$referer = get_value('advt_referer', 'string', Constants::MERCHANT_CAMPAIGN_DEFAULT_REFERER);
			$model_data["advt_referer"] = $referer;
		}
    }

?>
