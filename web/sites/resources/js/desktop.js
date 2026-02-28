mig33.Desktop = function()
{
	var desktop_el = Ext.get("x-desktop");
	this.desktop_el = desktop_el;
	
	this.msgnotifier = null;
	this.msgnotifier_el = Ext.get("desktop-toolbar-menu-new-msg-wrapper");
	
	var toolbar = null;
	var toolbar_el = Ext.get("desktop-toolbar-menu-wrapper");
	
	var notifier_el = Ext.get("notification-container");
	this.notifier_el = notifier_el;
	
	this.notifier_message_el = null;
	
	var notifier = new Ext.Tip({
						title: "We seem to have lost connection to mig33. Please hang in there while we try to reconnect ...",
						dismissDelay:0
						});

    var contact = null;
    this.contact = null;

    var updates = null;
    this.updates = null;

	var contact_request = null;
	
	this.my_photos = null;
	this.mig_wall = null;
	this.community = null;
	this.store = null;
	
	this.parent_window = null;
	
	this.taskbar = null;
	var self = this;
	
	function init()
	{
		mig33.set_command(false);
		mig33.command.desktop = this;
		Ext.QuickTips.init({showDelay:100, anchorToTarget:true, defaultAlign: "t-b", anchor: "top"});
				
		mig33.EventManager.on('login_success', mig33.event_login_success, this);
		mig33.EventManager.on("new_message_notification", function(id, title){
			var itemId = id + '-notification-item';
			var item = mig33.notification_menu.findById(itemId);
	
			// Attach a window listener 
			var win = mig33.window_group.get(id);
			if(win)
			{
				win.on('show', function(component){	
					
					// Remove the item from list and the total counter
		        	var item = mig33.notification_menu.findById(component.getId() + '-notification-item');
		        	if(item)
		        	{
			        	var counter = item.counter;
		        		mig33.notification_menu.decrease_ctr(counter);
						mig33.notification_menu.remove(component.getId() + '-notification-item');	
					}						
				});
				
				win.on('activate', function(component){	
					
					// Remove the item from list and the total counter
		        	var item = mig33.notification_menu.findById(component.getId() + '-notification-item');
		        	if(item)
		        	{
			        	var counter = item.counter;
		        		mig33.notification_menu.decrease_ctr(counter);
						mig33.notification_menu.remove(component.getId() + '-notification-item');	
					}						
				});
			}
			
			if (item) 
			{	
				// Update the notification info
				item.counter += 1;
				item.text = title + ' <span class="notification-counters">' + item.counter + '</span>';
				
				// Increase the total counter
				mig33.notification_menu.increase_ctr(1);
				
				// Refresh the notification info
				mig33.notification_menu.remove(itemId);
				mig33.notification_menu.add({
					id: itemId,
					counter: item.counter,
					text: item.text,
					handler: item.handler				
				});
			} 
			else 
			{
				mig33.notification_menu.add({
					id: itemId,
					win_id: id,
					counter: 1, 
					text: title + ' <span class="notification-counters">1</span>', 
					handler: function(){
						// Show itself						
						mig33.window_group.get(id).show();
						mig33.window_group.bringToFront(id);
						
						// Remove itself
						mig33.notification_menu.remove(itemId);
					} 
				});
				
				mig33.notification_menu.increase_ctr(1);
			}
			
		}, this);
	}
	this.init = init;
	
	function login_successful()
	{
		if( mig33.command.reconnecting == false )
		{
			this.taskbar = new Ext.ux.TaskBar(this);
			create_desktop_toolbar();
		
			mig33.hide_loading_message_box();
		
            contact = mig33.WindowManager.show_window(mig33.WindowType.contact, {id:"contact-window"});
            this.contact = contact;
		}
		sign_in_im();
		layout();
	}
	this.login_successful = login_successful;
	
	function layout()
	{
        desktop_el.setHeight(Ext.lib.Dom.getViewHeight() - toolbar_el.getHeight());
        desktop_el.setWidth(Ext.lib.Dom.getViewWidth());
    }
    this.layout = layout;
    Ext.EventManager.onWindowResize(layout);
    
    function sign_in_im()
    {
    	if( mig33.command.msn_signed_in )
    	{
    		mig33.command.transport.send_im_sign_in( mig33.command.msn_signed_in );
    	}
    	if( mig33.command.yahoo_signed_in )
    	{
    		mig33.command.transport.send_im_sign_in( mig33.command.yahoo_signed_in );
    	}
    	if( mig33.command.gtalk_signed_in )
    	{
    		mig33.command.transport.send_im_sign_in( mig33.command.gtalk_signed_in );
    	}
    }
    
    function create_desktop_toolbar()
	{
		mig33.desktop.msgnotifier = new Ext.Toolbar({
			renderTo: mig33.desktop.msgnotifier_el,
			items: [{
				text: '<div id="notification-menu-counter">0</div>',
				menu: mig33.notification_menu,
				iconCls: 'new-msg-tab-icon'
			}]
		});

		toolbar = new Ext.Toolbar({
			renderTo: toolbar_el,
			items: mig33.get_system_menu()
		});
		
		mig33.EventManager.on("disconnected", function(){
			if( mig33.desktop.notifier_message_el!=null)return;
			mig33.desktop.notifier_message_el = mig33.desktop.notifier_el.createChild({
											tag: "div",
											cls: "notifier disconnect"
									});
			var da = mig33.desktop.notifier_message_el.createChild({
										tag: "div",
										cls: "anchor"
									});
			var p = mig33.desktop.notifier_message_el.createChild({
										tag: "p",
										html: "We've lost connection to mig33. Please hang in there while we reconnect..."
									});
			//notifier.showBy(toolbar_el, "r-l");
		}, this);
		
		mig33.EventManager.on("logged_in", function(){
			if( mig33.desktop.notifier_message_el != null )
			{
				mig33.desktop.notifier_message_el.remove();
				mig33.desktop.notifier_message_el = null;
			}
		}, this);
	}
	this.create_desktop_toolbar = create_desktop_toolbar;
	
	function cascade_windows()
	{
        var xTick = Math.max(this.xTickSize, 20);
        var yTick = Math.max(this.yTickSize, 20);
        var x = xTick;
        var y = yTick;
        windows.each(function(win) {
            if (win.isVisible() && !win.maximized) {
                win.setPosition(x, y);
                x += xTick;
                y += yTick;
            }
        }, this);
    }

    function tile_windows() 
    {
        var availWidth = desktop_el.getWidth(true);
        var x = this.xTickSize;
        var y = this.yTickSize;
        var nextY = y;
        windows.each(function(win) {
            if (win.isVisible() && !win.maximized) {
                var w = win.el.getWidth();

//              Wrap to next row if we are not at the line start and this Window will go off the end
                if ((x > this.xTickSize) && (x + w > availWidth)) {
                    x = this.xTickSize;
                    y = nextY;
                }

                win.setPosition(x, y);
                x += w + this.xTickSize;
                nextY = Math.max(nextY, y + win.el.getHeight() + this.yTickSize);
            }
        }, this);
    }
    
    function show_notification( title, body )
	{
		var w = new Ext.ux.window.MessageWindow({
				header: false,
	   			autoDestroy: true,
    		    autoHeight: true,
	   			autoHide: true,
    		    //bodyStyle: 'text-align:center',
	            closable: true,
	            hideFx: {
    	   			        delay: 3000,
	      			        mode: 'default',
			                useProxy: false 
            		    },
				html: body,
			    origin: {
            		        pos: "t-t",
			                offX: 0,
			                offY: 0,
            			    spaY: 5
			            },
            	pinOnClick: false,
			    showFx: {
            		        align: 't',
			                delay: 0,
            		        duration: 0.5,
			                mode: 'standard',
            		        useProxy: false
			             },
            	width: 250}).show(desktop_el);
	}
	this.show_notification = show_notification;
	
	function loadIframe(iframeName, url) {
		/**
		var iframeElement = document.getElementById(iframeName);
		document.getElementById("intro").
		iframeElement.location = 'www.google.com;'
		**/
		this.store.loadIframe(iframeName, url);

	
		//window.parent.mig33.store_window.iframeName.location = url;
		/**
		if ( window.frames[iframeName] ) {
		    window.frames[iframeName].location = url;   
		    return false;
		}
		**/
		return true;
	}
	this.loadIframe = loadIframe;
	
};
Ext.ToolTip.override({
	showDelay: 20
});