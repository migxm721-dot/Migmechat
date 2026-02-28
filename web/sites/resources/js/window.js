mig33.Window = function(window_group, options)
{
	this._win = null;
	this._type = null;
	this._window_group = window_group;
	this._options = options;
	this._id = options.id;
	
	this._title = "";
	
	this._show_in_taskbar = true;
	this._show_focus_unfocus = true;

    this._construct_window_before_ajax = false;
	
	this.addEvents("ready");
}

Ext.extend(mig33.Window, Ext.util.Observable, {
	/**
	* Public Abstract Functions
	**/
	init: function()
	{
	},
	
	/**
	* Dispose class
	**/
	dispose: function()
	{
		delete this._win;
		
		this._win = null;
		this._type = null;
		this._window_group = null;
		this._options = null;
		this._id = null;
	},
	
	/**
	* Public Functions
	**/
	
	/**
	* Get the id for the window
	**/
	get_id: function()
	{
		return this._id;
	},
	
	/**
	*
	* Get the window object
	*
	**/
	get_window: function()
	{
		return this._win;
	},
	
	/**
	*
	* Get the window title
	*
	**/
	get_window_title: function()
	{
		return this._title;
	},
	
	/**
	*
	* Get the type of window
	*
	**/
	get_type: function()
	{
		if(this._type == null || this._type.length == 0)
		{
			throw "Invalid window type";
		}
		return this._type;
	},
	
	/**
	* Show the window
	**/
	show: function()
	{
		if( this._win.isVisible() )
			this._window_group.bringToFront(this._win);
		else
			this._win.show();
	},
	/**
	*
	* Close the window
	*
	**/
	close: function()
	{
		if(this._win)
			this._win.close();
	},
	
	/**
	* Private Abstract Functions
	**/
	_create_window: function()
	{
		// NOOP
	},
	
	_on_window_created: function()
	{
		// NOOP
	},
	
	/**
	* Private Functions
	**/
	/**
	* Initialise the window
	**/
	_init: function(ajax_options, callback, scope)
	{
		if( Ext.isDefined(ajax_options) && ajax_options != null )
		{
            var construct_window = this._construct_window_before_ajax;
            if( construct_window )
            {
                this._construct_window();
                this._win.el.mask('Loading ...', 'x-mask-loading');
            }

            ajax_options.success = function(response, options)
									{
										if( Ext.isDefined(callback) && callback != null )
											callback.call(scope||this, response);
                                        if( construct_window == false )
										    this._construct_window();
                                        else
                                            this._win.el.unmask();
									};
			
			if( Ext.isDefined(scope) )
				ajax_options.scope = scope;
									
			Ext.Ajax.request(ajax_options);
		}
		else
		{
			this._construct_window();
		}
	},
	
	/**
	* get x,y co-ordinates
	**/
	_get_xy : function(width, height)
	{
		mig33.get_next_window_pos(width, height);
		return {x:mig33.next_win_pos.x, y:mig33.next_win_pos.y};
	},
	
	/**
	* construct the window
	**/
	_construct_window: function()
	{
		this._win = this._create_window();
		
		this._title = this._win.initialConfig.title || "";
		
		this._on_window_created();
		
		if( this._options.created_callback != null && Ext.isDefined(this._options.created_callback) )
		{
			this._options.created_callback.call(this._options.scope||this, this._win);
		}
		
		//this._window_group.register(this._win);
		this.fireEvent("ready");
		
		if( this._win )
		{
			var active = this._window_group.getActive();
			
			if( this._show_in_taskbar )
			{
				this._win.mon(this._win, "show", function(win){
					if( mig33.command.desktop.taskbar )
						mig33.command.desktop.taskbar.removeTaskButton(win);
						
					if(Ext.isDefined(this._options.focus) && this._options.focus != null)
					{
						if( this._options.focus == false )
						{
							if(active)
								active.show();
						}
					}
				}, this);
				
				this._win.mon(this._win, "minimize", function(win){
					if(!win.hidden)
					{
						win.hide();
						if( mig33.command.desktop.taskbar )
							mig33.command.desktop.taskbar.addTaskButton(win);
					}
				}, this);
	
				this._win.mon(this._win, "beforeclose", function(win){
					if( mig33.command.desktop.taskbar )
						mig33.command.desktop.taskbar.removeTaskButton(win);
				}, this);
			}
			
			if( this._show_focus_unfocus )
			{
				this._win.mon(this._win, "activate", function(win){
					(win).removeClass("unfocus");
				}, this);
		
				this._win.mon(this._win, "deactivate", function(win){
					(win).addClass("unfocus");
				}, this);
			}
			
			if(this._options.show == true )
			{
				this._win.show();
				if(this._options.focus)
					this._window_group.bringToFront(this._win);
			}
		}
	},
	/**
	*
	* Set the show focus unfocus
	*
	**/
	_set_show_focus_unfocus: function(value)
	{
		this._show_focus_unfocus = value;
	}
});