Ext.namespace("mig33");

Ext.Container.prototype.bufferResize = false;

mig33.command = null;

mig33.Presence = {online:1, busy:3, away:4, appear_offline:99};
mig33.IM_Type = {fusion: 1, msn: 2, yahoo:4, gtalk:6, facebook:7 };
mig33.Content_Type = {text: 1, image: 2, audio: 3, video: 4, file: 5, emote: 6, system: 7};
mig33.Application_State = {check_session:1, logging_in:2, logged_in:3};
mig33.DestinationType = {individual:1, group:2, public_chat:3};
mig33.SettingType = { password: 1, mobilephone: 2, securityQuestion: 3 };

mig33.app_state = mig33.Application_State.check_session;
mig33.onbeforeunload_confirm = true;

mig33.next_win_pos = {x:0, y:0};

mig33.no_avatar_display_picture = "/sites/resources/images/avatar/default-avatar.png";
mig33.no_avatar_display_picture_small = "/sites/resources/images/avatar/default-avatar-24x24.png";

mig33.desktop = null;
mig33.emoticon_picker = null;
mig33.window_group = new Ext.WindowGroup();

mig33.menus = new Ext.util.MixedCollection();

mig33.loading_window = null;
mig33.chatroom_manager = null;

mig33.xTickSize = 20;
mig33.yTickSize = 20;

mig33.popout = false;
mig33.popout_type = null;
mig33.master = true;

mig33.bypass_logout = false;

mig33.init = function(desktop)
{
	Ext.WindowMgr.zseed = 15000;
	Ext.DomHelper.useDom = false;
	
	mig33.chatroom_manager = new mig33.ChatroomManager(mig33.desktop);
	mig33.chatroom_manager.init();
	mig33.emoticon_picker = new mig33.EmoticonPicker();
	
	mig33.create_menus();
	
	mig33.notification_menu = new mig33.NotificationMenu({
		id: 'notification-menu',
		items: [{
			win_id: "",
			id: 'total-new-msg',
			counter: 0,
			text:'<div id="notification-header">New Messages <span id="notification-total-ctr">0</span></div>'
		}],
		
		container_class: 'new-msg-container',
		hidden_class: 'x-hidden',
		counter_class: 'notification-menu-counter',
			
		total_id: 'total-new-msg',
		total_counter: 0,
		total_template: '<div id="notification-header">New Messages <span id="notification-total-ctr">{counter}</span></div>'
	});
	
	mig33.EventManager.on('sess_terminated', this.show_logout_forced, this);

    mig33.EventManager.on('updt_contact_status_msg', this.on_update_contact_status, this);
	
	// There is a bug with ExtJS, need to use window.onbeforeunload for cross browser compatibility. This method is being used by Gmail as well
//	window.onunload = mig33.logout;
//	window.onbeforeunload = this.onbeforeunload_mig;
    window.onunload = function(){

    }
    window.onbeforeunload = function(){

    }
}

mig33.onbeforeunload_mig = function(e)
{
	if(mig33.onbeforeunload_confirm)
		return "You are about to leave mig33.";
}

mig33.close_mig = function()
{
	mig33.command.stop_poll();
	mig33.bypass_logout = true;
	window.location = "/";
}

mig33.event_login_success = function(msn_detail, yahoo_detail, gtalk_detail)
{
	mig33.command.transport.send_presence();

	if (msn_detail == 2) mig33.command.msn_signed_in = true;
	if (yahoo_detail == 2) mig33.command.yahoo_signed_in = true;
	if (gtalk_detail == 2) mig33.command.gtalk_signed_in = true;
	
	if(mig33.app_state != mig33.Application_State.logged_in)
		mig33.init(mig33.desktop);
	mig33.app_state = mig33.Application_State.logged_in;
	
	if (!mig33.SSOLoginEnabled)
	{
		//Ext.util.Cookies.set("sid", mig33.command.session_id);
		//mig33.create_cookie("sid", mig33.command.session_id);
		var lw = mig33.login_window;
		if(lw) lw.destroy();
	}
	mig33.command.login_successful();
	mig33.command.desktop.login_successful();
	
	if (mig33.SSOLoginEnabled)
		setInterval(mig33.command.transport.check_session, 5*60*1000);
	
	if(mig33.command.emoticon_hotkeys == null)
		mig33.command.transport.send_get_emoticons();
	
	mig33.command.transport
		// We need account balance (Packet 902)
		.send_get_account_balance()
		// We need notifications (Packet 921)
		.send_user_event_setting(true)
		// We need contact requests (Packet 412)
		.send_get_contact_request();
}

mig33.set_command = function(popout)
{
	if( popout )
	{
		if( window.opener && !window.opener.closed )
		{
			mig33.command = window.opener.mig33.command;
		}
	}
	else
	{
		mig33.command = new mig33.Command();
	}
	
	mig33.popout = popout;
}

mig33.read_cookie = function(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

mig33.erase_cookie = function(name) {
	mig33.create_cookie(name, null, -1);
}

mig33.create_cookie = function(name, value, days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/; domain="+mig33.session_cookie_domain;
}

mig33.get_friend_list = function() {
	var friends_array = new Array();

	mig33.command.contacts.each(
		function(item, index, length){
            if( Ext.isDefined(item.fusion_username) )
			    friends_array.push(item.fusion_username);
		});
	return friends_array;
}

mig33.create_menus = function()
{
	if (mig33.b_url)
	{
		mig33.menus.add("switch", [
			{iconCls: 'icon_24', icon: '/sites/resources/images/b/24x24/switch.png', tooltip: 'To b.mig33.com', handler: function(item, e) { window.open(mig33.b_url); }}
		]);
	}
	
	var merchant_url = 'http://merchant.mig33.com';
	if(mig33.command.session_user_type == 2 || mig33.command.session_user_type == 3)
	{
		merchant_url = '/sites/corporate/merchant/dashboard';
	}

	mig33.menus.add("invite", [{
		text: "Invite", handler: function(item, e) { mig33.show_refer_friend(); }}
	]);
	
	mig33.menus.add("migworld", [
		{text: "What's New", handler: function(item, e){ mig33.show_migworld('migworld', 'details'); }},
		{text: "Leaderboards", handler: function(item, e){ mig33.show_my_leaderboard(); }},							
		{text: "Merchant", handler: function(item, e){ window.open(merchant_url); }},
		{text: "Store", handler: function(item, e){ mig33.show_store(mig33.window_group, ''); }}
	]);	

	mig33.menus.add("mymig", [
		{text: "My Profile", handler: function(item, e) { mig33.show_migworld('profile', 'home'); }},
		{text: "Edit Profile", handler: function(item, e) { mig33.show_migworld('profile', 'edit'); }},
		{text: "Friends List", handler: function(item, e) { mig33.show_migworld('contacts', 'friends'); }},
		{text: "My Chat Rooms",handler: function(item, e){ mig33.show_migworld('chatroom', 'user_chatrooms'); }},
		{text: "My Avatar", handler: function(item, e) { mig33.show_migworld('avatar', 'view'); }},
		{text: "My Gifts", handler: function(item, e) { mig33.show_migworld('profile', 'gifts_received'); }},
		{text: "My Photos", handler: function(item, e){ mig33.show_migworld('photo', 'home'); }}, 
        {text: "My Footprints", handler: function(item, e){ mig33.show_migworld('profile', 'footprints'); }}
	]);

	mig33.menus.add("groups", [
		{text: "Groups Home", handler: function(item,e){ mig33.show_groups_home(); }},
		{text: "My Groups", handler: function(item, e){ mig33.show_migworld('group', 'list_my_groups'); }},
		{text: "Create New Group",handler: function(item, e){ mig33.show_create_group(); }}
	]);

	mig33.menus.add("games", [
		{text: "Paint Wars", handler: function(item, e){ mig33.show_paintwars(); }},
		{text: "Fashion Show", handler: function(item, e){ mig33.show_fashionshow(); }},
		{text: "Chat Room Games", handler: function(item, e){ mig33.show_games(); }}
	]);
	
	mig33.menus.add("credits", [
		{text: "Credit Balance", handler: function(item, e) { mig33.show_account_detail(); }},
		{text: "Transaction History",handler: function(item, e) { mig33.show_account_history(); }},
		{text: "Buy Credits", handler: function(item, e) { mig33.show_recharge(); }},
		{text: "Transfer Credits", handler: function(item, e) { mig33.show_transfer_credits(); }}
	]);
	
	mig33.menus.add("account", [
		{text: "Settings", menu: [
							{text: "Security Question",handler: function(item, e) {mig33.show_security_question();}},
							{text: "Change Password", handler: function(item, e) { mig33.change_setting(mig33.SettingType.password); }},
							{text: "Desktop", menu: [
								{text: "Minimise Windows", handler: function(item, e) { mig33.minimise_windows(); }},
								{text: "Maximise Windows", handler: function(item, e) { mig33.maximise_windows(); }},
								{text: "Arrange Windows", handler: function(item, e) { mig33.cascade(); }}
							]},
							{text: "Privacy", menu: [
								{text: "Profile", handler: function(item, e) { mig33.show_profile_privacy_setting(); }},
								{text: "Chat", handler: function(item, e) { mig33.show_chat_privacy_setting(); }},
								{text: "Block List", handler:function(item,e) { mig33.show_block_list(); }}
							]},
							{text: "Change Mobile Number", handler:function(item,e) {mig33.change_setting(mig33.SettingType.mobilephone);}}
		]},
		{text: "Help", menu: [
			{text: "Group Help", handler: function(item, e){ window.open("http://blog.mig33.com/support/whats-new-in-mig33/new-group-feature-update"); }},
			{text: "Chat Room Help", handler: function(item, e){ window.open("http://blog.mig33.com/support/chatting-in-mig33"); }},
			{text: "Game Help", handler: function(item, e){ window.open("http://blog.mig33.com/support/playing-games"); }},
			{text: "IM Help", handler: function(item, e){ window.open("http://blog.mig33.com/support/getting-started-with-mig33"); }},
			{text: "Merchant Help", handler: function(item, e){ window.open("http://blog.mig33.com/?page_id=591"); }},
			{text: "Account Help", handler: function(item, e){ window.open("http://blog.mig33.com/support/my-account"); }},
			{text: "Credits Help", handler: function(item, e){ window.open("http://blog.mig33.com/support/getting-more-credits"); }},
			{text: "More", handler: function(item, e){window.open("http://blog.mig33.com/support/other-mig33-questions"); }},
			{xtype:"menuseparator"},
			{text: "Privacy", handler: function(item, e){ window.open("http://www.mig33.com/privacy.php"); }},
			{xtype:"menuseparator"},
			{text: "Contact Us", handler: function(item, e){ mig33.show_contact_us("contact"); }},
			{xtype:"menuseparator"},
			{text: "About mig33", handler: function(item, e){ window.open("http://info.mig33.com/about-us"); }}
		]}
	]);
	mig33.menus.add("logout", [
		{text: "Logout", handler: function(item, e) { mig33.logout(); }}
	]);
	
	mig33.menus.add("search", [
		{icon: '/sites/resources/images/icons/16/16_search.png', handler: function(item, e) { mig33.show_search(); }}
	]);
	Ext.get("menu-container").show();
}


mig33.get_system_menu = function()
{
	return [
		{
			text: "Account",
			menu: mig33.menus.get("logout")
		}
	];
	var menu = [
		mig33.menus.get("invite"),
		{
			text: "migWorld",
			menu: mig33.menus.get("migworld")
		},
		{
			text: "My mig",
			menu: mig33.menus.get("mymig")
		},
		{
			text: "Groups",
			menu: mig33.menus.get("groups")
		},				
		{
			text: "Games",
			menu: mig33.menus.get("games") 
		},
		{
			text: "Credits",
			menu: mig33.menus.get("credits")
		},
		{
			text: "Account",
			menu: mig33.menus.get("account")
		},
		mig33.menus.get("search")
	];
	if (mig33.b_url)
		menu.unshift(mig33.menus.get("switch"));
	return menu;
}

mig33.cascade = function()
{
	var cascade = 0;
	var lastXY;
	
   	mig33.window_group.each(function(w) 
   	{
		if(w.getId() != "contact-window" && w.getId() != "updates-window" && !w.hidden)
   		{
   			var ctr = mig33.desktop.desktop_el;
            var size = { width: w.getWidth(), height: w.getHeight() };
            var ctSize = w.getEl().getAlignToXY(ctr.id, "tl-br");
            var offSetX = 15;
            var offSetY = -15;
            cascade += 1;
            
            if (cascade == 1) 
            {
            	lastXY = w.getEl().getAlignToXY(ctr.id, "tl-tl?", [offSetX,offSetY]);
            } 
            else
            {
            	lastXY[0] += 40;
	            lastXY[1] += 25;
    	        var testBR = {bottom: (size.height + lastXY[1]),
        	              right: (size.width + lastXY[0]) };
            	if (testBR.bottom > ctSize[1])
            	{
                	lastXY[1] = 0;
	            }
    	        if (testBR.right > ctSize[0]) 
    	        {
        	        lastXY[0] = 15;
            	}
            }
            
            w.setPosition(lastXY[0], lastXY[1]).toFront();
    	}
    }, this);
}

mig33.get_bytes_from_int = function( x )
{
	var bytes = [];
	var i = 4;
	do {
		bytes[--i] = x & (255);
		x = x>>8;
	} while ( i )
	return bytes;
}

// Stops looping when x = 0
mig33.get_all_bytes_from_int = function( x )
{
	var bytes = [];
	var i = 4;
	do {
		bytes[--i] = x&(255);
		x = x>>8;
	} while ( i && x > 0 )
	return bytes;
}

mig33.minimise_windows = function()
{
	mig33.window_group.each(function(win){
//		if(win.getId() != "contact-window" && win.getId() != "updates-window")
		{
			win.minimize();
		}
	}, this);
}

mig33.maximise_windows = function()
{
	mig33.window_group.each(function(win) {
//		if(win.getId() != "contact-window" && win.getId() != "updates-window")
		{
			win.show();
		}
	}, this);
}

mig33.show_loading_message_box = function(msg)
{
	if(mig33.loading_window==null)
	{
		var progress = new Ext.ProgressBar({text: msg, cls:"loading-progress"});
	
		mig33.loading_window = new Ext.Window({
						cls: "window-loader",
						modal: true,
						closable: false,
						border: false,
						resizable: false,
						minimizable : false,
    	                maximizable : false,
        	            stateful: false,
                	    shim:true,
                    	plain:true,
	                    footer:true,
    	                width:300,
        	            height:250,
            	        draggable: false,
            	        layout: "fit",
            	        shadow: false,
            	        items: [
            	        	{
            	        		xtype: "panel",
            	        		applyTo: "loading-panel",
            	        		items:[progress]
            	        	}
            	        ]
					});

    	mig33.loading_window.on("show", function(win){
			progress.wait({animate:false, interval:200, increment:10});
		});
	}
	var tel = Ext.get("loading-text");
	if( tel )
	{
		tel.dom.innerHTML = msg;
	}
	
	mig33.loading_window.show();
}
	
mig33.hide_loading_message_box = function()
{
	var loading_window = mig33.loading_window; 
	if(loading_window)
		loading_window.hide();
}

mig33.hash_code = function(string) 
{
	var hash = 0;	
	var n = string.length;
	var count = 0;
	for (var i = 0; i < n; i++) {
		hash = mig33.toInt32(mig33.toInt32(31 * hash) + mig33.toAscii(string.charAt(i)));
	}
	return hash;
}

mig33.toInt32 = function(x)
{
    var min = -2147483648; // -2^31
    var max = 2147483647; // 2^31 - 1

    var d = 4294967296; // 2^32

    while (x > max)
    {
        x -= d;
    }
    while (x < min)
    {
        x += d;
    }

    return x;
}

// Convert chars to decimal values
// Only accepted ascii range is 32-126, returns 0 if out of range 
mig33.toAscii = function(cha)
{
    var val = (cha + '').charCodeAt(0);
    if (val < 32 || val > 126) return 0;
    return val;
}

mig33.escape_str = function(str)
{
    return (str + '')
        .replace("'", "\\'")
        .replace('"', '\\"');
}

mig33.get_im_type = function(im_type)
{
	switch(im_type)
	{
	case mig33.IM_Type.fusion:
		return "fusion";
	case mig33.IM_Type.msn:
		return "msn";
	case mig33.IM_Type.gtalk:
		return "gtalk";
	case mig33.IM_Type.yahoo:
		return "yahoo";
	case mig33.IM_Type.facebook:
		return "facebook";
	default:
		return "";
	}
}

mig33.random = function(max)
{
	if(!Ext.isDefined(max) || max == null )
		max = 1000;
	return Math.floor(Math.random()*max+1);
}

mig33.is_scrollable = function(element)
{
	var dom = Ext.get(element).dom;
	if( dom == null || !Ext.isDefined(dom)) return false;
	return dom.scrollHeight > dom.clientHeight;
}

mig33.get_presence_icon_class = function(type, presence)
{
	switch(type)
	{
	case mig33.IM_Type.fusion:
	case mig33.IM_Type.msn:
	case mig33.IM_Type.gtalk:
	case mig33.IM_Type.yahoo:
	case mig33.IM_Type.facebook:
		return mig33.get_im_type(type) + "-" + mig33.get_icon_class(presence);	
	default:
		return "mobile";
	}
}
	
mig33.get_icon_class = function(status)
{
	switch(status)
	{
	case mig33.Presence.online:
		return "online";
	case mig33.Presence.busy:
		return "busy";
	case mig33.Presence.away:
		return "away";
	case mig33.Presence.appear_offline:
		return "offline";
	default:
		return "";
	}
}

mig33.is_contact = function(username)
{
	return (mig33.get_contact_id() != 0);
}

mig33.get_contact_id = function(username)
{
	var item_id = 0;
	if( mig33.command.contacts == null ) return item_id;
	
	mig33.command.contacts.each(function(item, index, length){
							if( item.fusion_username == username )
							{
								item_id = item.id;
							}
						});
	return item_id;
}

mig33.get_contact = function(username)
{
	var contact = null;
	
	if( mig33.command.contacts == null ) return contact;
	mig33.command.contacts.each(function(item, index, length){
							if( item.fusion_username == username )
							{
								contact = item;
							}
						});
	return contact;
}

mig33.get_display_picture = function(username, config)
{
	var x = (config && config.x)?config.x:"50";
	var y = (config && config.y)?config.y:"50";
	return mig33.image_server + "/a/" + username + "?w=" + x + "&h=" + y + "&a=1&c=1&r=" + mig33.random();
}

mig33.login = function(form_el_id)
{
    var form_el = Ext.get(form_el_id).dom;//document.forms[form_el_id]
    var valid_user = /^[A-Z0-9_.-]+$/i;
    var valid_p = /^.+$/;

    if(!valid_user.test(form_el.session_user.value)){
        Ext.MessageBox.alert('Warning', 'Invalid username. Please try again.',function(){
            form_el.session_user.focus();
        });
    }else if(!valid_p.test(form_el.session_p.value)){
        Ext.MessageBox.alert('Warning', 'No password entered. Please try again.',function(){
            form_el.session_p.focus();
        });
    }else{
        mig33.command.session_user = form_el.session_user.value.toLowerCase();
        mig33.command.session_p = form_el.session_p.value;
        mig33.command.login_invisible = form_el.login_invisible.checked;
        mig33.login_window.hide();
        mig33.show_loading_message_box("Logging in...");
        if(form_el.rememberme.checked)
            mig33.create_cookie("mig33-username",mig33.command.session_user,183);
        else
            mig33.erase_cookie("mig33-username");
        mig33.command.transport.send_login(mig33.command.login_invisible);
    }

    return false;
}

mig33.logout = function()
{
	mig33.onbeforeunload_confirm = false;
	if(mig33.bypass_logout) return;
	
	mig33.command.stop_poll();
	mig33.command.transport.allowed_packet_types = [300];
	mig33.show_loading_message_box("Logging out...");
	mig33.command.transport.send_logout(function(){
		mig33.command.tear_down();
		setTimeout(mig33.logout_callback, 3000);
	});
}

mig33.logout_callback = function()
{
    mig33.onbeforeunload_confirm = false;
	if (mig33.SSOLoginEnabled)
	{
		window.location = '/sites/ajax/session/logout';
	}
	else
	{
		mig33.erase_cookie('sid');
		mig33.erase_cookie('eid');
		window.location = "/";
	}
};

mig33.show_login_window = function()
{
	mig33.command.tear_down();
    if(mig33.login_window==null)
    {
        mig33.login_window = new Ext.Window({
            modal: true,
            closable: false,
            border: false,
            resizable: false,
            minimizable : false,
            maximizable : false,
            stateful: false,
            //plain:true,
            width:407+(407-397)-1,
            height:273,//273+(273-248)-1,
            draggable: false,
            layout: "fit",
            applyTo:"window-login",
            shadow: false,
            //onSubmit:"return mig33.login('login-form');",
            items: [{
                xtype:"form",
                formId:"login-form",
                labelWidth:80,
                frame:false,
                plain:true,
                standardSubmit: true,
                defaultType:'textfield',
                monitorValid:true,
                items:[{
                    xtype:"box",
                    html:"<br/>",
                    id:"login-error-msg"
                },{
                    fieldLabel:'Username',
                    value:mig33.read_cookie("mig33-username"),
                    //allowBlank:false,
                    name:'session_user'
                },{
                    fieldLabel:'Password',
                    name:'session_p',
                    //allowBlank:false,
                    inputType:'password'
                },{
                    xtype:'checkbox',
                    boxLabel:'Remember me on this computer',
                    name:'rememberme',
                    checked:true,
                    id:'rememberme'
                },{
                    xtype:'checkbox',
                    boxLabel:'Make me invisible',
                    name:'login_invisible',
                    id:'login_invisible'
                },{
                    value:' Login ',
                    name:'login',
                    id:'login-submit-button',
                    inputType:'submit'
                    //src:'/sites/resources/images/login_button.png'
                }]
            }]
        });
        Ext.DomHelper.insertAfter('login-submit-button',[
            {tag:'a',href:'/sites/corporate/forgot_password/details',html:'Forgot your password?'}
        ]);
        if(Ext.isMac && Ext.isGecko3){ //temp hack for AJAX-264
            Ext.get("login-submit-button").dom.style.border = 'none';
        }
		Ext.get("login-form").on('submit', function(){ mig33.login('login-form'); }, mig33, {preventDefault: true});
    }
    mig33.hide_loading_message_box();
    mig33.login_window.show();
    Ext.get("login-form").dom.session_user.focus();
}

mig33.show_logout_forced = function(reason)
{
	mig33.onbeforeunload_confirm = false;
	var text = (reason&&reason.length>0)?": " + reason: "";
	Ext.MessageBox.alert('Warning'
		, 'We are having trouble connecting to mig33. You will be sent back to the login screen.' + text
		, function()
		{
			if (mig33.SSOLoginEnabled)
			{
				Ext.Ajax.request({
					url: '/sites/ajax/session/logout',
					success: function(){window.location = mig33.login_url;},
					failure: function(){window.location = '/sites/ajax/session/logout';}
				});
			}
			else
			{
				window.location.reload();
			}
		}
	);
}

mig33.show_login_failed = function(error){
	mig33.command.login_errors++;

	mig33.show_login_window();
	Ext.get("login-form").show();
	Ext.get("login-error-msg").dom.innerHTML = error;
	Ext.get("login-error-msg").addClass("error");
	var captcha;
	if(captcha = Ext.get("captcha-form"))
		captcha.hide();
	Ext.get("login-form").dom.session_p.value = '';
}

mig33.show_captcha_challenge = function(msg,png_base64){

    mig33.hide_loading_message_box();
    mig33.login_window.show();
    Ext.get("login-form").dom.style.display = 'none';

    if (!mig33.captcha_form)
    {
        var captchaFormNode = Ext.DomHelper.createDom({
            tag:'form',
            id:'captcha-form',
            children: [{
                tag:'div',//xtype:'box',
                'class':'message-error',
                id:'captcha-message-error',
                html:msg
            },{
                tag:'label',
                'for':'captcha',
                html:'Enter the letters as shown in the image below:'
            },{
                tag:'img',
                src:'data:image/png;base64,'+png_base64,
                alt:'captcha',
                id: 'captcha-img'
            },{
                tag:'input',//xtype:'textfield',
                name:'captcha'
            },{
                tag:'input',//xtype:'textfield',
                type:'submit',
                name:'submit',
                value:'Submit'
            }]
        });

        mig33.captcha_form = (new Ext.Element(captchaFormNode))
            .insertAfter("login-form")
            .on('submit',
                function(ev)
                {
                    ev.preventDefault(); // Prevents the browsers default handling of the event
                    // ev.stopPropagation(); // Cancels bubbling of the event
                    // ev.stopEvent() // preventDefault + stopPropagation

                    mig33.submit_captcha_challenge(this.dom.captcha.value);
                    this.dom.captcha.value = '';
                }
            );
    }

    mig33.captcha_form.show();
    Ext.get('captcha-img').set({src: 'data:image/png;base64,' + png_base64});
    Ext.get('captcha-message-error').dom.innerHTML = msg;
    Ext.get('captcha-form').dom.captcha.focus();
}

mig33.submit_captcha_challenge = function(captcha_str){

    mig33.show_loading_message_box("Logging in...");
    mig33.login_window.hide();
    mig33.command.transport.send_captcha_challenge_response(captcha_str);
    return false;
}

mig33.IFrameComponent = Ext.extend(Ext.BoxComponent, {
	 itemId: "iframe",
     onRender : function(ct, position){
          this.el = ct.createChild({tag: 'iframe', id: 'iframe-'+ this.id, frameBorder: 0, src: this.url});
          this.iframe_id = 'iframe-' + this.id;
     },
     get_id: function() {
     	return this.iframe_id;
     },
     reload: function() {
     	document.getElementById(this.iframe_id).contentWindow.location.reload(true);
     },
     refresh: function() {
     	document.getElementById(this.iframe_id).contentWindow.location.href = document.getElementById(this.iframe_id).contentWindow.location.href;
     },
     update_url: function(url) {
     	document.getElementById(this.iframe_id).contentWindow.location.href = url;
     }
});
Ext.reg('migiframe', mig33.IFrameComponent);

// Window Functions 
mig33.show_ajax_window = function(config, url)
{
	mig33.get_next_window_pos(config.width, config.height);
	config.url = url || config.url;
	if (config.reload !== false) {
		config.reload = true;
	}
	config.x = mig33.next_win_pos.x;
	config.y = mig33.next_win_pos.y;

	mig33.WindowManager.show_window(mig33.WindowType.iframe, config);
}
mig33.close_ajax_window = function(id)
{
	var win = mig33.window_group.get(id);
	if( win )
	{
		win.close();
	}
}
mig33.close_window = function(id, type)
{
	if(type == null || type == 'iframe')
	{
		type = mig33.WindowType.iframe;
	}

	var win = mig33.WindowManager.get_window(type, id);
	win.close();
}
mig33.get_next_window_pos = function(width, height)
{
	var computed_width = mig33.popout?screen.availWidth:mig33.desktop.desktop_el.getComputedWidth();
	var computed_height = mig33.popout?screen.availHeight:mig33.desktop.desktop_el.getComputedHeight();
	
	if (mig33.next_win_pos.x == 0)
	{
		mig33.next_win_pos.x = 20;
		mig33.next_win_pos.y = 40;
	}
	else if (mig33.next_win_pos.x + 30 + width > computed_width 
			|| mig33.next_win_pos.y + 30 + height > computed_height)
	{
		mig33.next_win_pos.x = 20;
		mig33.next_win_pos.y = 40;
	}
	else
	{
		mig33.next_win_pos.x += 30;
		mig33.next_win_pos.y += 30;
	}
}
mig33.set_focus_unfocus_event = function(win)
{
	win.on("activate", function(window){
		window.removeClass("unfocus");
	}, this);
		
	win.on("deactivate", function(window){
		window.addClass("unfocus");
	}, this);
}

mig33.remove_child_nodes = function(node)
{
	while(node.firstChild)
	{
		node.removeChild(node.firstChild);
	}
}

mig33.set_window_connect_handler = function(win, options)
{
	options = options || {};
	win.mon(win, "show", function(w)
	{
		var init = w._connect_init||false;
		if(init==false)
		{
			var cmd = mig33.EventManager;
			cmd.on("disconnected", function()
			{
				if( options.dfunc != null )
					options.dfunc.call(options.scope, options.dfunc);
				w.body.mask("Reconnecting...", 'x-mask-loading');
			}, this);
			cmd.on("logged_in", function(){
				if( options.cfunc != null )
					options.cfunc.call(options.scope, options.cfunc);
				w.body.unmask();
			}, this);
			w._connect_init = true;
		}
	});
}

mig33.add_loading_treenode = function(tree, node, id)
{
	if(tree.getNodeById(id) != null )return;
	var n = new Ext.tree.TreeNode({
							id: id,
							icon: "/sites/resources/images/default/tree/loading.gif",
							text: "Loading ..."});
	node.appendChild(n);
}

mig33.remove_loading_treenode = function(tree, id)
{
	var n = tree.getNodeById(id);
	if( n )
		n.remove();
}

mig33.ToolbarDiv = Ext.extend(Ext.Toolbar.Item, {
	onRender: function(ct, position)
	{
		 this.el = ct.createChild({tag:'div', cls:this.cls}, position);
	}
});
Ext.reg('tbdiv', mig33.ToolbarDiv);

Ext.PagingToolbar.prototype.doRefresh = function() {
    this.doLoad(this.cursor);
};

Ext.tree.TreeNodeUI.prototype.initEvents =
Ext.tree.TreeNodeUI.prototype.initEvents.createSequence(function(){
	if(this.node.attributes.tipCfg && this.node.attributes.tipCfg.text != null){
		var o = this.node.attributes.tipCfg;
	    o.target = Ext.id(this.divId);
    	Ext.QuickTips.register(o);
    }
});

String.prototype.lpad = function(padString, length) {
	var str = this;
    while (str.length < length)
        str = padString + str;
    return str;
}

String.prototype.rpad = function(padString, length) {
	var str = this;
    while (str.length < length)
        str = str + padString;	
    return str;
}
/*!
Math.uuid.js (v1.4)
http://www.broofa.com
mailto:robert@broofa.com

Copyright (c) 2009 Robert Kieffer
Dual licensed under the MIT and GPL licenses.
*/

/*
 * Generate a random uuid.
 *
 * USAGE: mig33.uuid(length, radix)
 *   length - the desired number of characters
 *   radix  - the number of allowable values for each character.
 *
 * EXAMPLES:
 *   // No arguments  - returns RFC4122, version 4 ID
 *   >>> Math.uuid()
 *   "92329D39-6F5C-4520-ABFC-AAB64544E172"
 * 
 *   // One argument - returns ID of the specified length
 *   >>> Math.uuid(15)     // 15 character ID (default base=62)
 *   "VcydxgltxrVZSTV"
 *
 *   // Two arguments - returns ID of the specified length, and radix. (Radix must be <= 62)
 *   >>> Math.uuid(8, 2)  // 8 character ID (base=2)
 *   "01001010"
 *   >>> Math.uuid(8, 10) // 8 character ID (base=10)
 *   "47473046"
 *   >>> Math.uuid(8, 16) // 8 character ID (base=16)
 *   "098F4D35"
 */
mig33.uuid = (function() {
  // Private array of chars to use
  var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split(''); 

  return function (len, radix) {
    var chars = CHARS, uuid = [];
    radix = radix || chars.length;

    if (len) {
      // Compact form
      for (var i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
      // rfc4122, version 4 form
      var r;

      // rfc4122 requires these characters
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
      uuid[14] = '4';

      // Fill in random data.  At i==19 set the high bits of clock sequence as
      // per rfc4122, sec. 4.1.5
      for (var i = 0; i < 36; i++) {
        if (!uuid[i]) {
          r = 0 | Math.random()*16;
          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
        }
      }
    }

    return uuid.join('');
  };
})();

/**
 * Concatenates the values of a variable into an easily readable string
 * by Matt Hackett [scriptnode.com]
 * @param {Object} x The variable to debug
 * @param {Number} max The maximum number of recursions allowed (keep low, around 5 for HTML elements to prevent errors) [default: 10]
 * @param {String} sep The separator to use between [default: a single space ' ']
 * @param {Number} l The current level deep (amount of recursion). Do not use this parameter: it's for the function's own use
 */
function print_r(x, max, sep, l) {

	l = l || 0;
	max = max || 10;
	sep = sep || ' ';

	if (l > max) {
		return "[WARNING: Too much recursion]\n";
	}

	var
		i,
		r = '',
		t = typeof x,
		tab = '';

	if (x === null) {
		r += "(null)\n";
	} else if (t == 'object') {

		l++;

		for (i = 0; i < l; i++) {
			tab += sep;
		}

		if (x && x.length) {
			t = 'array';
		}

		r += '(' + t + ") :\n";

		for (i in x) {
			try {
				r += tab + '[' + i + '] : ' + print_r(x[i], max, sep, (l + 1));
			} catch(e) {
				return "[ERROR: " + e + "]\n";
			}
		}

	} else {

		if (t == 'string') {
			if (x == '') {
				x = '(empty)';
			}
		}

		r += '(' + t + ') ' + x + "\n";

	}

	return r;

};
var_dump = print_r;

function encodeRE(s)
{
	return s.replace(/([.*+?^${}()|[\]\/\\])/g, '\\$1');
}

mig33.clone = function(o) {
    if(!o || 'object' !== typeof o) {
        return o;
    }
    if('function' === typeof o.clone) {
        return o.clone();
    }
    var c = '[object Array]' === Object.prototype.toString.call(o) ? [] : {};
    var p, v;
    for(p in o) {
        if(o.hasOwnProperty(p)) {
            v = o[p];
            if(v && 'object' === typeof v) {
                c[p] = mig33.clone(v);
            }
            else {
                c[p] = v;
            }
        }
    }
    return c;
};

mig33.in_array = function(needle, haystack)
{
	var length = haystack.length;
	for(var i = 0; i < length; i++) {
		if(haystack[i] == needle) return true;
	}
	return false;
};

mig33.show_participant_menu = function(e, username, chatroom_id, chatroom_type)
{
	var position = [e.clientX, e.clientY];
	
	var menu = new mig33.ChatroomParticipantMenu(username, chatroom_id, chatroom_type);
	menu.init();
	menu.showAt(position);
};

mig33.addslashes = function(text)
{
	text = text.replace(/\\/g,'\\\\');
	text = text.replace(/\'/g,'\\\'');
	text = text.replace(/\"/g,'\\"');
	text = text.replace(/\0/g,'\\0');
	return text;
}

mig33.stripslashes = function(text)
{
	text = text.replace(/\\'/g,'\'');
	text = text.replace(/\\"/g,'"');
	text = text.replace(/\\0/g,'\0');
	text = text.replace(/\\\\/g,'\\');
	return text;
}

//TODO: update this function to handle "=" in param value
//TODO: update this function to handle query param with no value
mig33.QueryString = function(a) {
	if (typeof a === "undefined") a = window.location.search.substr(1);
	if (a == "") return {};
	a = a.split('&');
	var b = {};
	for (var i = 0; i < a.length; ++i)
	{
		var p=a[i].split('=');
		if (p.length != 2) continue;
		b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
	}
	return b;
}

//-------------------- Start Of Windows --------------------//
// Avatar
mig33.show_avatar_voters_list = function(user_id, reload)
{			
	mig33.WindowManager.show_window(mig33.WindowType.avatarvoterslist, {
			id: "avatarvoterslist-window",
			uuid: "avatarvoterslist-window",
			user_id: user_id,
			height: 400,
			width: 300,
			title: "avatarvoterslist",
			reload: reload
		});
}
mig33.avatar_created = function()
{
	mig33.command.avatar_created = true;
	mig33.show_avatar();
}
mig33.show_avatar = function()
{
	if( mig33.command.avatar_created )
	{
		mig33.WindowManager.show_window(mig33.WindowType.avatar, {
				id: "avatar-edit-window",
				width:650,
				height:450
		});
	}
	else
	{
		mig33.show_create_avatar();
	}
}
mig33.show_create_avatar = function()
{
	mig33.WindowManager.show_window(mig33.WindowType.avatar_create, {
			id: "avatar-create-window",
			width: 500,
			height: 400
	});
}
mig33.avatar_create_close = function()
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.avatar_create, "avatar-create-window");
	if(win)
	{
		win.close();
	}
}
mig33.avatar_create_show_face_feature = function(body_id)
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.avatar_create, "avatar-create-window");
	if(win)
	{
		win.save_body(body_id);
	}
}
mig33.avatar_unequip_item = function(id)
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.avatar, "avatar-edit-window");
	var create_win = mig33.WindowManager.get_window(mig33.WindowType.avatar_create, "avatar-create-window");
	Ext.Ajax.request({
   			url: '/sites/json/avatar_api/unequip',
			method: 'GET',
   			success: function(response, options){
   				var data = Ext.decode(response.responseText);
   				if( data.ok )
   				{
   					if(win)
   					{
   						win.refresh_avatar_image(data.body_key);
	   					win.refresh_items();
   					}
   					if(create_win)
   					{
   						create_win.refresh_avatar_image(data.body_key);
	   					create_win.refresh_items();
   					}
   				}
   			},
   			params: {
   				itid: id
   			},
   			scope: this
		});
}
mig33.avatar_equip_item = function(id)
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.avatar, "avatar-edit-window");
	var create_win = mig33.WindowManager.get_window(mig33.WindowType.avatar_create, "avatar-create-window");
	Ext.Ajax.request({
   			url: '/sites/json/avatar_api/equip',
			method: 'GET',
   			success: function(response, options){
   				var data = Ext.decode(response.responseText);
   				if( data.ok )
   				{
   					if(win)
   					{
   						win.refresh_avatar_image(data.body_key);
	   					win.refresh_items();
   					}
   					if(create_win)
   					{
   						create_win.refresh_avatar_image(data.body_key);
	   					create_win.refresh_items();
   					}
   				}
   			},
   			params: {
   				itid: id
   			},
   			scope: this
		});
}
mig33.avatar_equip_unequip_item = function(dom, id)
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.avatar, "avatar-edit-window");
	if(win)
	{
		var equipped = dom.getAttribute("item:equipped");
		if( equipped == 1 )
		{
			win.unequip_item(dom, id);
		}
		else
		{
			win.equip_item(dom, id);
		}
	}
}
mig33.use_avatar_as_profile = function()
{
	Ext.Ajax.request({
   			url: '/sites/json/avatar_api/use_avatar_as_profile',
			method: 'GET',
   			success: function(response, options){
   				mig33.EventManager.fire_event_avatar_updated();
   			},
   			params: {
   				itid: id
   			},
   			scope: this
		});
   			
}

// Chat Rooms
mig33.show_chatroom_tab = function()
{
	var win_contact = mig33.WindowManager.get_window(mig33.WindowType.contact, 'contact-window');
	if(win_contact)
	{
		win_contact.show_chatroom();
	}
}
mig33.show_create_chatroom = function()
{
	mig33.show_ajax_window({
		id: "create-chatroom",
		title: "Create Chatroom",
		width: 400,
		height: 550,
		single: true
	}, '/sites/ajax/chatroom/create?win_id=create-chatroom');
}

mig33.show_chatroom_settings_multiids = function(room_name)
{
	mig33.show_ajax_window({
		id: "chatroom-settings-setup_multiids",
		title: "Settings to control multi-IDs",
		width: 500,
		height: 400,
		single: true
	}, '/sites/ajax/chatroom/setup_multiids/?roomName=' + room_name + '&win_id=chatroom-settings-setup_multiids');
}

// Accounts
mig33.show_account_detail = function()
{
	mig33.show_ajax_window({
		id: 'account-window',
		title: "Account",
		width: 600,
		height: 500,
		single: true
	}, '/sites/ajax/account/get_details?win_id=account-window');
}
mig33.show_recharge = function()
{
	mig33.show_ajax_window({
		id: 'account-window',
		title: "Account",
		width: 600,
		height: 500,
		single: true
	}, '/sites/ajax/account/recharge_credit?win_id=account-window');
}
mig33.show_account_history = function()
{
	mig33.show_ajax_window({
		id: 'account-window',
		title: "Account",
		width: 600,
		height: 500,
		single: true
	}, '/sites/ajax/account/get_history?win_id=account-window');
}
mig33.show_transfer_credits = function(username)
{
	if(!Ext.isDefined(username) || username == null || username == "undefined")
		username = '';
		
	mig33.show_ajax_window({
		id: 'account-window',
		title: "Account",
		width: 600,
		height: 500,
		single: true
	}, '/sites/ajax/account/transfer_credit?win_id=account-window&recipient_username=' + username);
}

mig33.change_setting = function(type){
     
     Ext.Ajax.request({
             url: '/sites/json/security_question_api/has_security_question',
             success: function(result, request){
                     var jsonData = Ext.util.JSON.decode(result.responseText);
            if( jsonData.has_security_question ){
             
             switch(type){
                     case mig33.SettingType.password:
                             mig33.show_change_password();
                             break;
                     case mig33.SettingType.mobilephone:
                             mig33.show_change_mobile_number();
                             break;  
             }
             
            }
            else{
             mig33.show_security_question(type);
            }
        },
             failure: function ( result, request ) {
                     alert('Unable to request for security password.');
             }
     });
     
}

// Settings
mig33.show_change_password = function()
{
    mig33.show_ajax_window({
        id: "change-password",
        title: "Change Password",
        width: 320,
        height: 250,
        single: true
    }, '/sites/ajax/settings/change_password?win_id=change-password');
}
mig33.show_security_question = function(type)
{
     mig33.show_ajax_window({
        id: "security-question",
        title: "Security Question",
        width: 420,
        height: 250,
        single: true
    }, '/sites/ajax/security_question/create?win_id=change-password&securityQuestionRedirect=' + type);
}
mig33.show_profile_privacy_setting = function()
{
    mig33.show_ajax_window({
        id: "profile-privacy-setting",
        title: "Profile Privacy Settings",
        width: 320,
        height: 250,
        single: true
    }, '/sites/ajax/settings/profile_privacy_settings?win_id=profile-privacy-setting');
}
mig33.show_chat_privacy_setting = function()
{
    mig33.show_ajax_window({
        id: "chat-privacy-setting",
        title: "Chat Privacy Settings",
        width: 320,
        height: 250,
        single: true
    }, '/sites/ajax/settings/chat_privacy?win_id=chat-privacy-setting');
}
mig33.show_change_mobile_number = function()
{
    mig33.show_ajax_window({
        id: "change-mobile-number",
        title: "Change Mobile Number",
        width: 330,
        height: 250,
        single: true
    }, '/sites/ajax/settings/change_mobile?win_id=change-mobile-number');
}

// Block List
mig33.show_block_list = function()
{
    mig33.WindowManager.show_window(mig33.WindowType.block_list, {
		id: "block-list",
		uuid: "block-list",
		height: 550,
		width: 610,
		title: "Block List"
	});
}
mig33.unblock_user = function(username)
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.block_list, "block-list");

	if( win != null )
	{
		win.unblock_user(username);
	}
}

// Store
mig33.rate_item = function(item_id, rating, fn)
{
	if(rating > 0 && rating < 6) {
		Ext.Ajax.request({
	   			url: '/sites/json/api/storeitem_rate',
				method: 'GET',
	   			success: function(response, options){
	   				var data = Ext.decode(response.responseText);
   					if(fn)
   						fn.call(this, data.average, data.total, data.numratings);
	   				},
	   			params: { 
	   				siid: item_id,
	   				rate: rating
	   			},
	   			scope: this
			});
	}
}
mig33.show_store = function(itemType, username)
{
   	mig33.WindowManager.show_window(mig33.WindowType.store, {
		id: "store-window",
		height: 560,
		width: 650
	});
	
	var win = mig33.WindowManager.get_window(mig33.WindowType.store, "store-window");
	win.show_store(parseInt(itemType), username);
}
mig33.show_store_item = function(item_id, item_type, item_catid, username)
{
   	mig33.WindowManager.show_window(mig33.WindowType.store, {
		id: "store-window",
		height: 560,
		width: 650
	});
	
	var win = mig33.WindowManager.get_window(mig33.WindowType.store, "store-window");
	win.show_store_item(item_id, item_type, item_catid, username);
}
mig33.reload_store_content_from_side = function(url, itemType)
{
	var win = mig33.WindowManager.get_window(mig33.WindowType.store, "store-window");
	win.set_main_nav(url, itemType);
}

// Contact Us
mig33.show_contact_us = function(t)
{
	var url='/sites/ajax/help/contact_us'
	if(t=="contact")
		url='/sites/ajax/help/contact_us?t=1'
    mig33.show_ajax_window({
        id: "contact_us",
        title: "Contact Us",
        width: 450,
        height: 400,
        single: true
    }, url);
}

// Facebook Auth
mig33.facebook_auth = function(url)
{
	var width = 500;
	var left = (screen.width - width) / 2;
	var top = (screen.height - width) / 2;
    var facebook_auth_window = window.open(url, "FacebookAuthentication", "status=no,height=" + width + ",width=" + width + ",resizable=yes,left=" + left + ",top=" + top + ",toolbar=no,menubar=no,scrollbars=no,location=no,directories=no");
	facebook_auth_window.focus();
}

// Auth
mig33.show_auth = function(from)
{
	mig33.show_ajax_window({
		id: "auth-inapp",
		title: "Mobile Verification",
		width: 420,
		height: 350,
		single: true
	}, "/sites/ajax/registration/auth_inapp?win_id=auth-inapp&f=" + from);
}

mig33.show_auth_infootprints = function(){
	mig33.show_ajax_window({
		id: "auth-infootprints",
		title: "Mobile Verification",
		width: 420,
		height: 350,
		single: true
		},"/sites/ajax/registration/footprints_auth?win_id=auth-infootprints"
	);
}
mig33.close_auth_infootprints = function(username){
	var win_id = "auth-infootprints";
	var type = "iframe";
	var win_footprints_id = "profile-footprints-" + username;
	
	var windowfootprint = mig33.window_group.get(win_footprints_id);
	var win = mig33.WindowManager.get_window(type,win_id);
	
	mig33.close_ajax_window(win_id);
	mig33.close_ajax_window(win_footprints_id);
	mig33.show_footprints(username);

}

// Contact / Contact Lists
mig33.show_contacts_tab = function()
{
	var win_contact = mig33.WindowManager.get_window(mig33.WindowType.contact, 'contact-window');
	if(win_contact)
	{
		win_contact.show_contacts();
	}
}
mig33.on_update_contact_status = function(contact_id, message)
{
    if( mig33.command.contacts.containsKey(contact_id) )
    {
        var contact = mig33.command.contacts.get(contact_id);
    }
}
mig33.show_contact_request = function()
{
	mig33.show_ajax_window({
			id: "contact-request",
			title: "System Message",
			width: 500,
			height: 290,
			single: true,
			reload: false
		}, "/sites/ajax/invite/invitation?win_id=contact-request");
}
mig33.show_add_contact = function(username, type, type_string)
{
	var params = "";
	if(typeof(username) == 'string')
	{
		params = "username="+username+"&sharemobile=0";
	} 
	else 
	{
		return;
	}

	mig33.show_ajax_window({
		id: "add-contact",
		width: 400,
		height: 260,
		title: "Add A User",
		single: true
	}, "/sites/ajax/contacts/add_mig33_user_submit?win_id=add-contact&" + params);
}
mig33.show_edit_contact = function(contact_id)
{
	mig33.show_ajax_window({
		id: "edit-contact-" + contact_id,
		title: "Edit Contact",
		width: 500,
		height: 350
	}, "/sites/ajax/contacts/edit?win_id=edit-contact-" + contact_id + "&contact_id=" + contact_id);
}
mig33.show_add_contact_group = function()
{
	mig33.show_ajax_window({
		title: "Add Contact Group",
		id: "add-contact-group",
		width: 400,
		height: 220,
		single: true
	}, "/sites/ajax/contacts/add_contact_group?win_id=add-contact-group&action=add");
}
mig33.show_rename_contact_group = function(id, name)
{
	mig33.show_ajax_window({
		id: "rename-contact-group",
		title: "Rename Contact Group",
		width: 400,
		height: 180,
		single: true
	}, '/sites/ajax/contacts/add_contact_group?win_id=rename-contact-group&action=rename&group_id=' + id + '&group_name=' + name);
}
mig33.show_setup_im_window = function()
{
	mig33.show_ajax_window({
		id: "setup-im",
		title: "Setup IM",
		width: 400,
		height: 385,
		single: true
	}, '/sites/ajax/newuser/setup_im?win_id=setup-im');
};
mig33.show_edit_im = function(im_type)
{
	var title = "";
	switch(im_type)
	{
		case mig33.IM_Type.msn:
			title = "Edit MSN Details";
			break;
		case mig33.IM_Type.gtalk:
			title = "Edit GTalk Details";
			break;
		case mig33.IM_Type.yahoo:
			title = "Edit Yahoo Details";
			break;
		case mig33.IM_Type.facebook:
			title = "Edit Facebook Details";
			break;
	}
	mig33.show_ajax_window({
		id: "edit-im-" + im_type,
		title: title,
		width: 400,
		height: 250
	}, "/sites/ajax/im/edit?win_id=edit-im-" + im_type + "&imtype=" + im_type);
}

mig33.show_buzz_contact = function(destination, contact_id)
{
	mig33.show_ajax_window({
		id: "buzz-" + contact_id,
		title: "Buzz",
		width: 500,
		height: 350
	}, "/sites/ajax/buzz/home?win_id=buzz-"+contact_id+"&to=" + destination + "&cid=" + contact_id);
}
mig33.show_lookout_contact = function(destination, contact_id)
{
	mig33.show_ajax_window({
		id: "lookout-" + contact_id,
		title: "Lookout",
		width: 500,
		height: 350
	}, "/sites/ajax/lookout/home?win_id=lookout-"+contact_id+"&to=" + destination + "&cid=" + contact_id);
}
mig33.show_sms_user = function(username)
{
	mig33.show_ajax_window({
		id: "sms-user-"+username,
		width: 550,
		height: 380,
		title: "SMS "+username,
		single: true
	}, "/sites/ajax/sms/home?win_id=sms-user-"+username+"&user_contact="+username);
}

// Misc
mig33.show_virtualgift_received = function(username, virtualgiftreceived_id)
{
	mig33.show_ajax_window({
					id: "virtualgift_received-" + virtualgiftreceived_id,
					width:400, 
					height:400,
					title: "Virtual Gift Received"
				}, "/sites/ajax/virtual_gift_received/view?win_id=virtualgift_received&username=" + username + "&vgid=" + virtualgiftreceived_id);
}

mig33.show_my_leaderboard = function(nav, reload)
{
	mig33.WindowManager.show_window(mig33.WindowType.my_leaderboard, {
		id: "my-leaderboard-win",
		uuid: "my-leaderboard-win",
		height: 600,
		width: 620,
		title: "My Leaderboard",
		nav_selected: nav,
		reload: reload
	});
}
mig33.show_games = function()
{
	mig33.show_ajax_window({
		id: "games",
		title: "Games",
		width: 490,
		height: 420,
		single: true
	}, "/sites/ajax/wordpress/get_page?page_id=2574");
}
mig33.show_received_photo = function(username, file_id)
{
	mig33.show_ajax_window({
		id: "photo-received-" + file_id,
		title: "Photo Received",
		width: 420,
		height: 350
	}, "/sites/ajax/photos/received?original=1&nid=" + file_id + "&sender=" + username + "&win_id=photo-received-" + file_id);
}
mig33.show_report_abuse = function(type, offender, subject, subject_id)
{
	mig33.show_ajax_window({
		id: "report_abuse",
		width:370, 
		height:400,
		title: "Report Abuse",
		single: true
	}, "/sites/ajax/report/report_abuse?win_id=report_abuse&type=" + type + "&offender=" + offender + "&subject=" + subject + "&subject_id=" + subject_id);
}
mig33.show_refer_friend = function(mobile)
{
	mobile = mobile || "";
	mig33.show_ajax_window({
		id: "refer-friend",
		width: 500,
		height: 350,
		title: "Refer Friend",
		single: true
	}, "/sites/ajax/invite/refer_friend?win_id=refer-friend&mobile=" + mobile);
}
mig33.show_welcome = function()
{
	mig33.welcome_window = mig33.WindowManager.show_window(mig33.WindowType.welcome, {
		id: "welcome",
		width: 500,
		height: 500,
		centered: true
	});
}

// Paint Wars
mig33.show_paintwars = function()
{		
	mig33.show_ajax_window({
		id: "paintwars-windows",
		title: "Paint Wars",
		width: 550,
		height: 550,
		single: true
	}, '/sites/ajax/paintwars/home');
}

// Fashion Show
mig33.show_fashionshow = function()
{
	mig33.WindowManager.show_window(mig33.WindowType.fashionshow, {
			id: "fashionshow-window",
			uuid: "fashionshow-window",
			height: 550,
			width: 630,
			title: "fashionShow"
		});
}
mig33.show_fashionshow_vote = function(vote_for_user_id, vote_for_user_name, bid, reload)
{
	if(typeof reload === 'undefined'){
		reload = false;
	}
			
	mig33.WindowManager.show_window(mig33.WindowType.fashionshow, {
		id: "fashionshow-window",
		uuid: "fashionshow-window",
		vote_for_user_id: vote_for_user_id,
		vote_for_user_name: vote_for_user_name,
		bid: bid,
		height: 550,
		width: 630,
		title: "fashionShow",
		reload: reload
	});	
}

// migWorld
mig33.show_migworld = function(controller, action)
{
	controller = controller || 'migworld';
	action = action || 'home';
	
	var win_id = 'migworld-home';
	var title = mig33.command.session_user;
	if(controller == 'migworld' && action == 'details')	
	{
		title = 'migWorld';
		win_id = 'migworld-details';
	}

	var win = mig33.WindowManager.get_window(mig33.WindowType.migworld, win_id);
	if(win == null)
	{
		win = mig33.WindowManager.show_window(mig33.WindowType.migworld, {
			id: win_id,
			uuid: win_id,
			username: mig33.command.session_user,
			title: title,
			controller: controller,
			action: action
		});	
	}
	var page = controller + '|' + action;
	switch(page)
	{
		case 'migworld|home':
			win.show_home();
			break;
		case 'profile|home':
			win.show_profile();
			break;
		case 'profile|edit':
			win.show_edit_profile();
			break;
		case 'group|list_my_groups':
			win.show_groups();
			break;
		case 'group|list_friends_groups':
			win.show_friends_groups();
			break;
		case 'contacts|friends':
			win.show_friends_list();
			break;
		case 'chatroom|user_chatrooms':
			win.show_chatrooms();
			break;
		case 'avatar|view':
			win.show_avatar();
			break;
		case 'profile|gifts_received':
			win.show_gifts();
			break;
		case 'photo|home':
			win.show_photos();
			break;
		case 'profile|footprints':
			win.show_footprints();
			break;
	}
	mig33.window_group.bringToFront(win_id);
}

// Profile
mig33.show_profile = function(username, controller, action)
{
	username = username || mig33.command.session_user;

	controller = controller || 'profile';
	action = action || 'home';
	
	if(username == mig33.command.session_user)
	{
		mig33.show_migworld(controller, action);
	}
	else
	{
		var win = mig33.WindowManager.get_window(mig33.WindowType.profile, "profile-" + username);
		
		if( win == null )
		{
			win = mig33.WindowManager.show_window(mig33.WindowType.profile, {
				id: "profile-" + username,
				uuid: "profile-" + username,
				username: username,
				title: username,
				controller: controller,
				action: action
			});
		}

		var page = controller + '|' + action;
		switch(page)
		{
			case 'migworld|home':
				win.show_home();
				break;
			case 'profile|home':
				win.show_profile();
				break;
			case 'profile|edit':
				win.show_edit_profile();
				break;
			case 'group|list_my_groups':
				win.show_groups();
				break;
			case 'group|list_friends_groups':
				win.show_friends_groups();
				break;
			case 'contacts|friends':
				win.show_friends_list();
				break;
			case 'chatroom|user_chatrooms':
				win.show_chatrooms();
				break;
			case 'avatar|view':
				win.show_avatar();
				break;
			case 'profile|gifts_received':
				win.show_gifts();
				break;
			case 'photo|home':
				win.show_photos();
				break;
			case 'profile|footprints':
				win.show_footprints();
				break;
		}
		mig33.window_group.bringToFront("profile-" + username);
	}
}
mig33.get_profile_widget = function(username) 
{ 
	return new Ext.Panel({ 
		border: false, 
		bodyBorder: false, 
		autoLoad: "/sites/ajax/widget/profile?" + Ext.urlEncode({username: username}),
		height: 75 
	});      
} 
mig33.reload_profile_widget = function(username, widget) 
{ 
	widget.load({ 
		url: "/sites/ajax/widget/profile", 
		method: 'GET', 
		params: {username:username} 
	}); 
}

// Search
mig33.show_search = function()
{
	mig33.show_ajax_window({
		id: "search",
		title: "Search",
		width: 320,
		height: 250,
		single: true
	}, '/sites/ajax/search/home?win_id=search');
};
mig33.show_search_profile = function(name)
{
    name = name || "";

    if( name.length > 0 )
    {
        mig33.show_ajax_window({
            id: "search-profile",
            width: 300,
            height: 500,
            title: "Search Profile",
            single: true,
			reload: true
        }, "/sites/ajax/profile/search_submit?win_id=search-profile&search="+name);
    }
    else
    {
        mig33.show_ajax_window({
            id: "search-profile",
            width: 300,
            height: 150,
            title: "Search Profile",
            single: true,
			reload: true
        }, "/sites/ajax/profile/search?win_id=search-profile");
    }
}
mig33.show_chatroom_search = function(search_string)
{
	if( !Ext.isString(search_string) )
		search_string = "";
		
	if( search_string.length > 0 )
	{
		mig33.show_ajax_window({
			id: "chatroom-search",
			width: 400,
			height: 400,
			title: "Search Chatroom",
			single: true,
			reload: true
		}, "/sites/ajax/chatroom/search_submit?" + Ext.urlEncode({win_id:'chatroom-search', search_keywords:1, search: search_string}));
	}
	else
	{	
		mig33.show_ajax_window({
			id: "chatroom-search",
			width: 400,
			height: 400,
			title: "Search Chatroom",
			single: true,
			reload: true
		}, "/sites/ajax/chatroom/search?" + Ext.urlEncode({win_id:'chatroom-search', search_keywords:1, search: search_string}));
	}
}

// Chat
mig33.show_private_chat = function(username)
{
	mig33.chatroom_manager.join_private_chat(1, username, username);
}

// Group
mig33.show_group = function(group_id, group_name, action)
{
	group_name = group_name || "Group";
	
	url = '';
	if(action === undefined){
		url = '/sites/ajax/group/home?cid=' + group_id + '&win_id=group-' + group_id;
	}else{
		url = '/sites/ajax/group/'+action+'?cid=' + group_id + '&win_id=group-' + group_id;
	}
	
	mig33.show_ajax_window({
		id: "group-" + group_id,
		height: Ext.lib.Dom.getViewHeight() - 15*3 - 40,
		width: 625,
		title: Ext.util.Format.htmlDecode(group_name),
		single: true,
		maximizable : true
	}, url);
}

mig33.show_browse_groups = function()
{
	mig33.show_ajax_window({
		id: "browse-groups",
		width: 500,
		height: 550,
		title: "Browse Groups",
		single: true
	}, "/sites/ajax/group/list_groups_home?win_id=browse-groups");
}

mig33.show_groups_link_unlink_chatrooms = function(linkunlink, group_id, group_name)
{
	mig33.show_ajax_window({
		id: "group-link-chatrooms-" + group_id,
		width: 500,
		height: 400,
		title: Ext.util.Format.htmlDecode(group_name),
		single: true
	}, '/sites/ajax/group/' + linkunlink + '_chatrooms?cid=' + group_id);
}
mig33.show_groups_gnrl = function(url)
{
	mig33.show_ajax_window({
		id: "groups-home",
		height: Ext.lib.Dom.getViewHeight() - 15*3 - 40,
		width: 625,
		title: "Group",
		single: true
	}, url);
}
mig33.show_groups_home = function()
{
	mig33.show_groups_gnrl("/sites/ajax/group/list_groups_home?win_id=groups-home");
}
mig33.show_official_groups = function()
{
	mig33.show_groups_gnrl("/sites/ajax/group/list_featured_groups?win_id=groups-home");
}
mig33.show_search_groups = function(name)
{
	if (name !== "undefined" && name.length > 0)
	{
		mig33.show_groups_gnrl("/sites/ajax/group/search?win_id=groups-home&name=" + name);
	}
	else
	{
		mig33.show_groups_gnrl("/sites/ajax/group/search?win_id=groups-home");
	}
}
mig33.show_active_groups = function()
{
	mig33.show_groups_gnrl("/sites/ajax/group/list_active_groups?win_id=groups-home");
}
mig33.show_my_groups = function(){
	mig33.show_migworld('group', 'list_my_groups');
}
mig33.show_pending_groups = function(){
	mig33.show_migworld('group', 'list_pending_groups');
}
mig33.show_friends_groups = function(){
	mig33.show_migworld('group', 'list_friends_groups');
}
mig33.show_search_groups = function(name)
{
	mig33.show_groups_gnrl("/sites/ajax/group/search?name=" + name + "&win_id=groups-home");
}
mig33.show_create_group = function(chatroom_id, chatroom_name)
{
	chatroom_id = chatroom_id || 0;
	chatroom_name = chatroom_name || "";
	mig33.show_ajax_window({
		id: "create-group",
		width: 500,
		height: 370,
		title: "Create Group",
		single: true
	}, "/sites/ajax/group/create?win_id=create-group&rm=" + chatroom_name + "&rmid=" + chatroom_id);
}
mig33.close_create_group = function()
{
	var win = mig33.window_group.get("create-group"); 
	if( win )
		win.close();
}
mig33.update_group_avatar = function(group_id)
{
	mig33.show_ajax_window({id:"update-group-avatar-" + group_id, 
							width:500, height:350,
							title: "Update Group Avatar", single:true}, 
						"/sites/ajax/group/choose_avatar?win_id=update-group-avatar-" + group_id + "&cid=" + group_id);
}

// Email
mig33.show_email = function()
{
	mig33.show_ajax_window({
		id: "mail",
		title: "Mail",
		width: 660,
		height: 540,
		single: true
	}, "/members/mail_main.php?win_id=mail");
}
mig33.show_send_email = function(destination, subject)
{
	url = "/members/mail_compose.php?win_id=send-mail&to=" + destination;
	if(typeof subject !== 'undefined'){
		url += "&subject=" + escape(subject);
	}
	
	mig33.show_ajax_window({
		id: "send-mail",
		title: "Mail",
		width: 500,
		height: 450,
		single: true
	}, url);
}
mig33.show_email_view = function(url, title)
{
	mig33.show_ajax_window({
		id: "email-view",
		title: title,
		width:550,
		height: 550,
		single: true
	}, url + '&win_id=email-view');
}
