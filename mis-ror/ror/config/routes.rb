Ror::Application.routes.draw do |map|

  # The priority is based upon order of creation:
  # first created -> highest priority.

  # Sample of regular route:
  #   match 'products/:id' => 'catalog#view'
  # Keep in mind you can assign values other than :controller and :action

  # Sample of named route:
  #   match 'products/:id/purchase' => 'catalog#purchase', :as => :purchase
  # This route can be invoked with purchase_url(:id => product.id)

  # Sample resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Sample resource route with options:
  #   resources :products do
  #     member do
  #       get :short
  #       post :toggle
  #     end
  #
  #     collection do
  #       get :sold
  #     end
  #   end

  # Sample resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Sample resource route with more complex sub-resources
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get :recent, :on => :collection
  #     end
  #   end

  # Sample resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end

  # You can have the root of your site routed with "root"
  # just remember to delete public/index.html.
  root :to => "login#index"

  # See how all your routes lay out with "rake routes"
  
  # test controller
  #match 'testme' => 'testme#index'
  
  # migbo
  match 'migbo_post/activate/:id' => 'migbo_post#activate', :constraints => {:id => /.*/}
  match 'migbo_post/delete/:id' => 'migbo_post#delete', :constraints => {:id => /.*/}
  match 'migbo_user/search' => 'migbo_user#search'
  match 'migbo_user/delete' => 'migbo_user#delete'
  match 'migbo_user/:username' => 'migbo_user#view', :constraints => {:username => /.*/}
  
  #user category
  match 'user_category/:category/destroy' => 'user_category#destroy', :constraints => {:category => /.*/}
  match 'user_category/create' => 'user_category#create'
  match 'user_category/:category' => 'user_category#update', :constraints => {:category => /.*/}
  
  # photo
  match 'photo/scrapbook/:username' => 'photo#scrapbook', :constraints => {:username => /.*/}
  
  # reports
  match 'report/download_merchant_operations/:date/:filename' => 'report#download_merchant_operations', :constraints => {:filename => /.*/}	
  match 'my_reports' => 'report#user_report'	
  match 'report/download/:filename' => 'report#download', :constraints => {:filename => /.*/}
  match 'report/schedule_report_edit/:id' => 'report#schedule_report_edit'
  match 'report/schedule_report_delete/:id/:title' => 'report#schedule_report_delete'
  # sms rate
  #match 'sms_rate/buzz' => 'sms_rate#system_rate'	
  #match 'sms_rate/lookout' => 'sms_rate#system_rate'	
  #match 'sms_rate/email_alert' => 'sms_rate#system_rate'	
  #match 'sms_rate/group_notification' => 'sms_rate#system_rate'	

  # call rate	
  match 'call_rate' => 'call_rate#index'
  match 'call_rate/call_back_rates/:countryId' => 'call_rate#call_back_rates'
  match 'call_rate/proposed' => 'call_rate#proposed'
  match 'call_rate/update/:countryId' => 'call_rate#update'
  match 'call_rate/manage/:countryId' => 'call_rate#manage'
  match 'call_rate/:countryId' => 'call_rate#index'
  
  # voice provider rates
  match 'voice_provider_rate/upload' => 'voice_provider_rate#upload'
  match 'voice_provider_rate/unapproved' => 'voice_provider_rate#unapproved'
  match 'voice_provider_rate/unapproved/:provider' => 'voice_provider_rate#unapproved'
  match 'voice_provider_rate/:provider' => 'voice_provider_rate#view'
  	
  # sms_route
  match 'sms_route/scheduled_switch/:areaCode/:iddCode/:type' => 'sms_route#scheduled_switch'
  match 'sms_route/scheduled_switch/:iddCode/:type' => 'sms_route#scheduled_switch'
  match 'sms_route/:areaCode/:iddCode/:type' => 'sms_route#view'
  match 'sms_route/:iddCode/:type' => 'sms_route#view'
  	
  # voice_routes
  match 'voice_route/switch' => 'voice_route#switch'
  match 'voice_route/update' => 'voice_route#update'
  match 'voice_route/scheduled_switch' => 'voice_route#scheduled_switch'
  # match 'voice_route/scheduled_switch/:iddCode/:areaCode' => 'voice_route#scheduled_switch'
  # match 'voice_route/scheduled_switch/:iddCode' => 'voice_route#scheduled_switch'
  # match 'voice_route/:iddCode/:areaCode' => 'voice_route#view'
  match 'voice_route/:iddCode' => 'voice_route#view'
  
  # fixed_call_rates
  match 'fixed_call_rate/create' => 'fixed_call_rate#create'
  match 'fixed_call_rate/delete/:sourceCountryId/:destinationCountryId' => 'fixed_call_rate#delete'
  match 'fixed_call_rate/update/:sourceCountryId/:destinationCountryId' => 'fixed_call_rate#update'
  match 'fixed_call_rate/:country' => 'fixed_call_rate#view'
  
  #merchant
  match 'merchant/tag/release/:username' => 'merchant#release_tag'
  match 'merchant/tag/:username' => 'merchant#tag'
  match 'merchant/view/:username' => 'merchant#view'
  match 'merchant/:id/update_username_color' => 'merchant#update_username_color', :constraints => {:id => /.*/}
  match 'merchant/update_username_color_fr_file' => 'merchant#update_username_color_fr_file'
  match 'merchant/view' => 'merchant#view'
  
  # merchant reward points
  match 'merchant_reward_point/redeem/:id' => 'merchant_reward_point#redeem'
  match 'merchant_reward_point/:id' => 'merchant_reward_point#view'

  # location
  match 'location/delete/:id' => 'location#delete'
  match 'location/rename' => 'location#rename'
  match 'location/create' => 'location#create'
  match 'location/:country/:location' => 'location#view', :constraints => {:country => /.*/}
  match 'location/:country' => 'location#view', :constraints => {:country => /.*/}
  
  # merchant_location
  match 'merchant_location/delete/:id' => 'merchant_location#delete'
  match 'merchant_location/move' => 'merchant_location#move'
  match 'merchant_location/update/:id' => 'merchant_location#update'
  # match 'merchant_location/create' => 'merchant_location#create'
  match 'merchant_location/create/:locationId' => 'merchant_location#create'
  match 'merchant_location/:countryId/:locationId' => 'merchant_location#view', :constraints => {:countryId => /.*/}
  match 'merchant_location/:countryId' => 'merchant_location#view', :constraints => {:countryId => /.*/}
  
  # credit card
  match 'credit_card_transaction/hml' => 'credit_card_transaction_hml#index'
  match 'credit_card_transaction/hml/:id/approve' => 'credit_card_transaction_hml#approve'
  match 'credit_card_transaction/hml/:id/reject' => 'credit_card_transaction_hml#reject' 
  match 'credit_card_transaction/reject/:id' => 'credit_card_transaction#reject'
  
  # western union transfer intent
  match 'western_union_transfer_intent' => 'account_transfer#western_union_transfer_intent'
  match 'western_union_transfer_intent/collect' => 'account_transfer#western_union_transfer_intent_collect'
  
  # bank transfer intent
  match 'bank_transfer_intent' => 'account_transfer#bank_transfer_intent'
  match 'bank_transfer_intent/view/:id' => 'account_transfer#view_bank_transfer_intent'
  
  # cash receipt
  match 'cash_receipt/match' => 'cash_receipt#match'
  match 'cash_receipt/create' => 'cash_receipt#create'
  match 'cash_receipt/create_confirm' => 'cash_receipt#create_confirm'
  match 'cash_receipt/c_receipt' => 'cash_receipt#c_receipt'
  match 'cash_receipt/a_receipt' => 'cash_receipt#a_receipt'
  match 'cash_receipt/reverse/:transactionId' => 'cash_receipt#reverse', :constraints => {:transactionId => /\d+/}
  match 'cash_receipt/reverse/:transactionId/confirm' => 'cash_receipt#reverse_confirm', :constraints => {:transactionId => /\d+/}
  match 'cash_receipt/:id' => 'cash_receipt#view'
  
  # advance cash receipt
  match 'advance_cash_receipt/create' => 'advance_cash_receipt#create'
  match 'advance_cash_receipt/manage' => 'advance_cash_receipt#manage'
  match 'advance_cash_receipt/:id' => 'advance_cash_receipt#view'
  
  # group
  match 'group/update_attribute/:id' => 'group#update_attribute', :constraints => {:id => /.*/}
  match 'group/:name' => 'group#view', :constraints => {:name => /.*/}
  match 'group' => 'group#index'
  
  # chatroom
  match 'chatroom/delete/:id' => 'chatroom#delete', :constraints => {:id => /.*/}
  match 'chatroom/manage_bans' => 'chatroom#manage_bans'
  match 'chatroom/:id' => 'chatroom#view', :constraints => {:id => /.*/}
  
  # emoticon
  match 'emoticon/destroy/:epID/:id' => 'emoticon#destroy'
  match 'emoticon/update/:epID/:id' => 'emoticon#update'
  match 'emoticon/upload/:epID' => 'emoticon#upload'
  match 'emoticon/:epID/:id' => 'emoticon#view'
  
  # sticker
  match 'sticker/destroy/:epID/:id' => 'sticker#destroy'
  match 'sticker/update/:epID/:id' => 'sticker#update'
  match 'sticker/upload/:epID' => 'sticker#upload'
  match 'sticker/:epID/:id' => 'sticker#view'

  # mig33 user
  match 'mig33_user/verify/:username' => 'user_verify#verify', :constraints => {:username => /.*/}
  match 'mig33_user/reset_sq/:id' => 'mig33_user#reset_sq', :constraints => {:id => /.*/}
  match 'mig33_user/logs' => 'mig33_user#logs'
  match 'mig33_user/reset_merchant_tag/:id' => 'mig33_user#reset_merchant_tag', :constraints => {:id => /.*/}
  match 'mig33_user/chatroom/:id' => 'mig33_user#chatroom', :constraints => {:id => /.*/}
  match 'mig33_user/group/:id' => 'mig33_user#group', :constraints => {:id => /.*/}
  match 'mig33_user/log/:id' => 'mig33_user#log', :constraints => {:id => /.*/}
  match 'mig33_user/transactions/:id' => 'mig33_user#transactions', :constraints => {:id => /.*/}
  match 'mig33_user/badges/:id' => 'mig33_user#badges', :constraints => {:id => /.*/}
  match 'mig33_user/reset_merchant_pin/:id' => 'mig33_user#reset_merchant_pin', :constraints => {:id => /.*/}
  match 'mig33_user/password/:id' => 'mig33_user#password', :constraints => {:id => /.*/}
  match 'mig33_user/mobile_phone/:id' => 'mig33_user#mobile_phone', :constraints => {:id => /.*/}
  match 'mig33_user/logs/:id' => 'mig33_user#logs', :constraints => {:id => /.*/}
  match 'mig33_user/spd_user_fr_file' => 'mig33_user#spd_user_fr_file'
  match 'mig33_user/clear_kick_fr_file' => 'mig33_user#clear_kick_fr_file'
  match 'mig33_user/update_type_fr_file' => 'mig33_user#update_type_fr_file'
  match 'mig33_user/authentication_code/:id' => 'mig33_user#authentication_code', :constraints => {:id => /.*/}
  match 'mig33_user/view_balance/:id' => 'mig33_user#view_balance', :constraints => {:id => /.*/}
  match 'mig33_user/view_security_question/:id' => 'mig33_user#view_security_question', :constraints => {:id => /.*/}
  match 'mig33_user/view_email_address/:id' => 'mig33_user#view_email_address', :constraints => {:id => /.*/}
  match 'mig33_user/view_mobile_number/:id' => 'mig33_user#view_mobile_number', :constraints => {:id => /.*/}
  match 'mig33_user/update_attribute/:id' => 'mig33_user#update_attribute', :constraints => {:id => /.*/}
  match 'mig33_user/dc_user' => 'mig33_user#dc_user'
  match 'mig33_user/spd_user' => 'mig33_user#spd_user'
  match 'mig33_user/search' => 'mig33_user#search'
  match 'mig33_user/update_miglevel_fr_file' => 'mig33_user#update_miglevel_fr_file'
  match 'mig33_user/view_miglevel_fr_file' => 'mig33_user#view_miglevel_fr_file'
  match 'mig33_user/dc_user_fr_file' => 'mig33_user#dc_user_fr_file'
  match 'mig33_user/auth_user_fr_file' => 'mig33_user#auth_user_fr_file'
  match 'mig33_user/reputation_details/:id' => 'mig33_user#reputation_details', :constraints => {:id => /.*/}
  match 'mig33_user/remove_emailaddress/:id' => 'mig33_user#remove_emailaddress', :constraints => {:id => /.*/}
  match 'mig33_user/resend_email_verification/:id' => 'mig33_user#resend_email_verification', :constraints => {:id => /.*/}
  match 'mig33_user/delete_custom_cover/:id' => 'mig33_user#delete_custom_cover', :constraints => {:id => /.*/}
  match 'mig33_user/reverse_transaction/:transactionId' => 'mig33_user#reverse_user_credit_transfer', :constraints => {:transactionId => /\d+/}
  match 'mig33_user/reverse_transaction/:transactionId/confirm' => 'mig33_user#reverse_user_credit_transfer_confirm', :constraints => {:transactionId => /\d+/}
  match 'mig33_user/recommended_people' => 'mig33_user#recommended_people'
  match 'mig33_user/:id' => 'mig33_user#view', :constraints => {:id => /.*/}

  # mis user
  match 'mis_user/logs/:id' => 'mis_user#logs', :constraints => {:id => /.*/}
  match 'mis_user/update/:username' => 'mis_user#update', :constraints => {:username => /.*/}
  match 'mis_user/update_confirmation/:username' => 'mis_user#update_confirmation', :constraints => {:username => /.*/}
  match 'mis_user/display_in_list/:id' => 'mis_user#display_in_list', :constraints => {:id => /.*/}
  match 'mis_user/destroy/:id' => 'mis_user#destroy', :constraints => {:id => /.*/}
  match 'mis_user/update_password/:id' => 'mis_user#update_password', :constraints => {:id => /.*/}
  
  # partner
  match 'partner' => 'partner#index'
  match 'partner/create' => 'partner#create'
  
  match 'partner/:id/create_agreement' => 'partner#create_agreement', :constraints => {:id => /.*/}
  
  match 'partner/:id/agreement/:agreement_id/remove_build/:build_id' => 'agreement#remove_build'
  match 'partner/:id/agreement/:agreement_id/add_build' => 'agreement#add_build', :constraints => {:id => /.*/}
  match 'partner/:id/agreement/:agreement_id/' => 'agreement#view', :constraints => {:id => /.*/}
  
  match 'partner/:id/remove_admin/:username' => 'partner#remove_admin', :constraints => {:id => /.*/, :username => /.*/}
  match 'partner/:id/add_admin' => 'partner#add_admin', :constraints => {:id => /.*/}
  match 'partner/:id/change_admin' => 'partner#change_admin', :constraints => {:id => /.*/}
  match 'partner/:id' => 'partner#view', :constraints => {:id => /.*/}

  # vasworld
  match 'vasworld' => 'vasworld#index'
  match 'vasworld/:id' => 'vasworld#view'
  match 'vasworld/:id/approve' => 'vasworld#approve', :constraints => {:id => /.*/}
  match 'vasworld/:id/reject' => 'vasworld#reject', :constraints => {:id => /.*/}
  
  #store
  match 'store/categories/:act/:id' => 'store#categories', :constraints => {:act => /.*/, :id => /.*/ }
  match 'store/categories/:act' => 'store#categories', :constraints => {:act => /.*/ }
  
  #recommendation
  match 'recommendation/blacklisted/:recommendation_type/:target_type/:sub_type/:target_identifier_int/:page' => 'recommendation#blacklisted', :constraints => {:recommendation_type => /.*/, :target_type => /.*/, :target_identifier_int => /.*/, :page => /.*/}
  match 'recommendation/featured/:recommendation_type/:target_type/:sub_type/:target_identifier_int/:page' => 'recommendation#featured', :constraints => {:recommendation_type => /.*/, :target_type => /.*/, :target_identifier_int => /.*/, :page => /.*/}
  match 'recommendation/:recommendation_type/:target_type/:sub_type/:target_identifier_int/:page' => 'recommendation#list', :constraints => {:recommendation_type => /.*/, :target_type => /.*/, :target_identifier_int => /.*/, :page => /.*/}
  
  #promoted post
  get 'promoted_post/post' => 'promoted_post#get'
  get 'promoted_post/archived' => 'promoted_post#archived'
  post 'promoted_post/post' => 'promoted_post#update'

  # This is a legacy wild controller route that's not recommended for RESTful applications.
  # Note: This route will make all actions in every controller accessible via GET requests.
  match ':controller(/:action(/:id(.:format)))'  
  
end
