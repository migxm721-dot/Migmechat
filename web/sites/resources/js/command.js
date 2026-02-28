mig33.Command = function(config)
{
	config = config || {};
	
	this.is_connection_error=false;
	
	this.session_id = 0;
	this.session_user = null;
	this.session_user_status = null;
	this.session_p = null;
	this.session_user_level = null;
	this.session_user_level_image = null;
	this.session_user_presence = mig33.Presence.online;
	this.session_user_display_picture = null;
	this.session_user_type = null;
	this.login_invisible = false;
	
	this.allow_like = false;
	this.allow_like_level = 0;
	this.allow_add_to_photo_wall = false;
	this.avatar_created = false;
	this.avatar_used = false;
	this.allow_user_display_picture = false;
	
	this.msn_signed_in = false;
	this.yahoo_signed_in = false;
	this.gtalk_signed_in = false;
	this.facebook_signed_in = false;
	this.allow_email = false;
	
	this.chatrooms = new Ext.util.MixedCollection();
	
	this.emoticon_hotkeys = null;
	
	this.contacts = null;
	this.contact_groups = new Ext.util.MixedCollection();
	
	this.transport = null;
	
	this.parent_command = null;
	
	this.login_errors = 0;
	this.max_login_errors = 10;
	this.reconnecting = false;
	
	this.is_logged_in = false;
	this.url_hash_param = null;
	
	this.addEvents({
		//"disconnected":true,
		"reconnected": true,
		// "loggedIn": true,
		"destroy" : true		
	});
		
	this.init(config);
};

Ext.extend(mig33.Command, Ext.util.Observable,
{
	normal_poll_time : 10,
	short_poll_time : 2,
	current_interval : 0,
	normal_poll_task : null,
	short_poll_task : null,
	current_poll_task : null,
	is_polling : false,
	task_runner: null,
	
	connection_error_count:0,
	max_connection_error:3,
	login_timeout: 5000,
	
	init: function(config)
	{
		if( this.contacts == null )
		{
			this.contacts = new Ext.util.MixedCollection();
		}
		
		if( config.poll )
			this.poll = config.poll;
		
		this.normal_poll_task = {
			    	run: this.do_poll,
		    		interval: this.normal_poll_time * 1000,
		    		scope: this
				};
				
		this.short_poll_task = {
			    	run: this.do_poll,
		    		interval: this.short_poll_time * 1000,
		    		scope: this
				};
				
		this.task_runner = new Ext.util.TaskRunner();
				
		this.transport = new mig33.Transport();
		
		mig33.EmoticonCache.init();
	},
	
	copy: function(object, do_poll)
	{
		this.session_user = object.session_user;
		this.session_p = object.session_p;
		this.session_id = object.session_id;
		
		this.session_user_level = object.session_user_level;
		this.session_user_level_image = object.session_user_level_image;
		this.session_user_presence = object.session_user_presence;
		this.session_user_display_picture = object.session_user_display_picture;
		this.login_invisible = object.login_invisible;
		
		this.allow_like = object.allow_like;
		this.allow_like_level = object.allow_like_level;
		this.allow_add_to_photo_wall = object.allow_add_to_photo_wall;
		this.avatar_created = object.avatar_created;
		this.avatar_used = object.avatar_used;
		this.allow_user_display_picture = object.allow_user_display_picture;
	
		this.msn_signed_in = object.msn_signed_in;
		this.yahoo_signed_in = object.yahoo_signed_in;
		this.gtalk_signed_in = object.gtalk_signed_in;
		this.facebook_signed_in = object.facebook_signed_in;
		this.allow_email = object.allow_email;
	
		this.emoticon_hotkeys = object.emoticon_hotkeys;
		
		object.contacts.each(function(item, index, length){
				var o = mig33.clone(item);
				this.contacts.add(o.id, o);
		}, this);
		
		object.contact_groups.each(function(item, index, length){
				var o = mig33.clone(item);
				this.contacts.add(o.id, o);
		}, this);
		
		this.reconnecting = object.reconnecting;
	
		this.is_logged_in = object.is_logged_in;
		
		this.current_interval = object.current_interval;
		
		mig33.EmoticonCache.init();
		mig33.EmoticonCache.copy(window.opener.mig33.EmoticonCache.get_cache());
		
		if( do_poll )
			this.start_poll();
	},
	
	tear_down: function()
	{
		this.session_user = null;
		this.session_p = null;
		this.session_id = null;
		mig33.localStorage.remove('_sess');
	},
	
	trigger_disconnect:function()
	{
		for(var i = 0; i <= this.max_connection_error; ++i )
		{
			this.connection_error();
		}
		/*
		this.is_logged_in = false;
		this.is_connection_error = true;
		this.fireEvent("disconnected");
		*/
	},
	
	trigger_logged_in: function()
	{
		this.is_logged_in = true;
		mig33.EventManager.fire_event_logged_in();
		//this.fireEvent("loggedIn");
	},
	
	trigger_reconnect: function()
	{
		this.is_connection_error = false;
		this.fireEvent("reconnected");
	},
	
	attempt_login: function()
	{
		mig33.command.login_errors += 1;
//		mig33.command.transport.send_login();
		mig33.command.reconnecting = true;
		mig33.command.launch_app();
	},
	
	launch_app: function()
	{
		mig33.command.session_id = mig33.read_cookie('sid');
		
		if (mig33.SSOLoginEnabled)
		{
			mig33.command.session_user_presence = mig33.config.presence;
			mig33.command.login_invisible = mig33.config.presence == 99;
			mig33.command.session_user = mig33.config.username;
			
			//start fusion packet 211
			mig33.app_state = mig33.Application_State.logging_in;
			mig33.command.transport.initiate_fusion_session();
		}
		else
		{
			mig33._sess = mig33.localStorage.get('_sess');
			mig33.command.transport.check_session();
		}
	},
	
	check_session_success: function(response, options)
	{
		var data = null;
		try
		{
			data = JSON.parse(response.responseText).data;
		}
		catch(e){
			if(window.console)
			{
				console.log('check_session_success:');
				console.log(e);
			}
		}

		if( data && data.is_logged_in )
		{
			if(mig33.app_state == mig33.Application_State.logged_in) return;
			
			mig33.command.session_user = data.username;
			
			if (mig33.SSOLoginEnabled)
			{
				var data = mig33.config;
				if(data)
				{
					mig33.command.allow_email = false; data.mailUrl ? true : false;
					mig33.command.session_user_level = data.reputationLevel;
					mig33.command.session_user_level_image = data.reputationImagePath;
					var new_email = data.mailCount,
					msn_detail = data.msnDetail,
					yahoo_detail = data.yahooDetail,
					gtalk_detail = data.gtalkDetail;
				}
			}
			else
			{
				var data = mig33._sess;
				if(data)
				{
					mig33.command.allow_email = data.a;
					mig33.command.session_user_level = data.l;
					mig33.command.session_user_level_image = data.i;
					mig33.command.session_user_type = data.t;
					var new_email = data.n,
					msn_detail = data.m,
					yahoo_detail = data.y,
					gtalk_detail = data.g;
					delete mig33._sess;
				}
			}
			
			// Fire Login Successful event (Packet 203)
			mig33.EventManager.fire_event_login_successful(new_email, msn_detail, yahoo_detail, gtalk_detail);
		}
		else
		{
			options.failure();
		}
	},
	
	check_session_failure: function(response, options)
	{
		if(mig33.SSOLoginEnabled)
		{
			return mig33.command.connection_error(null);
		}	
		
		if(mig33.app_state == mig33.Application_State.check_session)
		{
			mig33.app_state = mig33.Application_State.logging_in;
			mig33.show_login_window();
		}
		else
		{
			mig33.command.connection_error(null);
		}
	},
	
	connection_error: function(response)
	{
		// To prevent multiple different sessions
		if( mig33.command.session_id != mig33.read_cookie('sid') )
			mig33.show_logout_forced();
		
		this.is_connection_error = true;
		if( this.connection_error_count >= this.max_connection_error )
		{
			this.is_logged_in = false;
			this.transport.allowed_packet_types = [200];
			
			this.stop_poll();
			mig33.show_logout_forced();
		}
		else
		{
			this.connection_error_count += 1;
			if( this.is_logged_in == false )
			{
				setTimeout(this.attempt_login, this.login_timeout);
			}
			if( this.connection_error_count == this.max_connection_error )
				mig33.EventManager.fire_event_disconnected();
		}
	},
	
	connection_successful: function(response)
	{
		if(window.console && mig33.localStorage.get('debug'))
			console.log(response.status+': '+response.responseText)
		
		if( this.connection_error_count > 0 )
			this.fireEvent("reconnected");
		
		this.transport.allowed_packet_types = null;
		this.is_connection_error = false;
		
		this.connection_error_count = 0;
	},
	
	login_successful: function()
	{
		mig33.command.login_errors = 0;
		
		this.is_logged_in = true;
		Ext.Ajax.request({
   			url: '/sites/json/api/user_permissions',
   			method: 'GET',
   			success: function(response, options){
   				var data = Ext.decode(response.responseText);
   				if( data )
   				{
					if(data.allow_add_to_photo_wall != null && Ext.isDefined(data.allow_add_to_photo_wall) )
   						mig33.command.allow_add_to_photo_wall = data.allow_add_to_photo_wall;
   					
   					if(data.allow_add_to_photo_wall != null && Ext.isDefined(data.allow_add_to_photo_wall) )
   						mig33.command.allow_add_to_photo_wall = data.allow_add_to_photo_wall;
   					
   					if(data.allow_like != null && Ext.isDefined(data.allow_like) )
   						mig33.command.allow_like = data.allow_like;
   					
   					if(data.allow_like_level != null && Ext.isDefined(data.allow_like_level) )
   						mig33.command.allow_like_level = data.allow_like_level;
   					
   					if(data.avatar_created != null && Ext.isDefined(data.avatar_created) )
   						mig33.command.avatar_created = data.avatar_created;
   						
   					if(data.allow_use_display_picture != null && Ext.isDefined(data.allow_use_display_picture) )
   						mig33.command.allow_use_display_picture = data.allow_use_display_picture;
   				}
   				
				setInterval(this.open_window_from_url, 2000);
   			},
   			scope: this
		});
		//this.fireEvent("loggedIn");
		mig33.EventManager.fire_event_logged_in();
		this.start_poll();
	},
	
	open_window_from_url: function()
	{
		var param = window.location.hash.substr(1);
		if (mig33.command.url_hash_param == param) return;
		mig33.command.url_hash_param = param;
		
		var param = mig33.QueryString(param);
		if(param) param.id = param.id || 'welcome';
		
//		var whitelist = {
//			'avatar':1,
//			'profile':1
//		};
		if(param.show && mig33['show_'+param.show])//&& whitelist[param.show])
		{
			mig33['show_'+param.show].apply(mig33, param.args ? param.args.split('::') : []);
		}
//		else if(param.url)
//		{
//			mig33.show_ajax_window(param, param.url);
//			mig33.window_group.bringToFront(param.id);
//		}
		else if(mig33.command.session_user_level <= 5)
		{
			mig33.show_welcome();
		}
	},
	
	start_poll: function()
	{
		if( this.current_interval!=0 )
		{
			this.poll(this.current_interval);
		}
		else
		{
			this.start_normal_poll();
		}
	},
	
	stop_poll: function()
	{
		this.task_runner.stopAll();
	},
	
	start_normal_poll: function()
	{
		this.poll(this.normal_poll_time);
	},
	
	start_short_poll: function()
	{
		this.poll(this.short_poll_time);
	},
	
	poll: function(interval)
	{
		if( this.current_task != null )
		{
			if( this.current_task.interval == interval )
				return;
		}
		
		this.stop_poll();
		
		this.current_interval = interval;
		this.is_polling = true;
		
		if( interval == this.short_poll_time )
		{
			this.current_task = this.task_runner.start(this.short_poll_task);
		}
		else
		{
			this.current_task = this.task_runner.start(this.normal_poll_task);
		}
	},
	
	do_poll: function()
	{
		this.transport.send_poll();
	}
});
