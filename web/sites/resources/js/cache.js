mig33.CacheProxy = Ext.extend(Ext.util.Observable,{
	_cache: null,
	
	_name: null,
	_master: false,
	
	_use_local_cache: true,
	
	constructor: function(config)
	{
		config = config || {};
		
		if( Ext.isDefined(config.master) && config.master != null )
			this._master = config.master;
		
		if( Ext.isDefined(config.name) && config.name != null )
			this._name = config.name;
			
		if( this._master || this._use_local_cache )
			this._cache = new Ext.util.MixedCollection();
		else
			mig33.message_bus.cache_create(this._name);
			
		mig33.CacheProxy.superclass.constructor.call(config);
	},
	
	add: function(key, value)
	{
		if(this._master || this._use_local_cache)
			return this._cache.add(key, value);
		else
			return mig33.message_bus.cache_add(this._name, key, value);
	},
	
	removeKey: function(key)
	{
		if( this._master || this._use_local_cache )
			return this._cache.removeKey(key);
		else
			return mig33.message_bus.cache_remove_key(this._name, key);
	},
	
	containsKey: function(key)
	{
		if( this._master || this._use_local_cache )
			return this._cache.containsKey(key);
		else
			return mig33.message_bus.cache_contains_key(this._name, key);
	},
	
	get: function(key)
	{
		if( this._master || this._use_local_cache )
			return this._cache.get(key);
		else
			return mig33.message_bus.cache_get(this._name, key);
	},
	
	getCache: function()
	{
		return this._cache;
	}
});

mig33.Cache = Ext.extend(Ext.util.Observable,
{
	constructor: function(config)
	{
		config = config || {};
		this.cache = new Ext.util.MixedCollection();
		this.timeout = config.timeout || 300;
		mig33.Cache.superclass.constructor.call(config)
	},
	
	add : function(key, value)
	{
		if( this.cache.containsKey(key) )
		{
			var cached = this.cache.get(key);
		}
		else
		{
			var cached = {};
		}
		
		cached.timestamp = new Date().getTime();
		cached.value = value;
		this.cache.add(key, cached);
	},
	
	remove : function(key)
	{
		if( this.cache.containsKey(key) )
			this.cache.removeKey(key);
	},
	
	get : function(key)
	{
		var value = null;
		if( this.cache.containsKey(key) )
		{
			var cached = this.cache.get(key);
			var currenttime = new Date().getTime() / 1000;
			var cachedtime = cached.timestamp / 1000;
			if( (currenttime - cachedtime) <= this.timeout )
				value = cached.value;
		}
		
		return value;
	}
});

mig33.AvatarCache = (function(){
	var cache = new mig33.Cache();
	
	function get_avatar_data(username, callback, scope)
	{
		var self = this;
		Ext.Ajax.request({
   			url: '/sites/json/avatar_api/get_image_key',
   			method: 'GET',
   			success: function(response, options){
   				var data = Ext.decode(response.responseText);
   				if( callback )
   					callback.call(scope, username, data);
   			},
   			params: { 
   				username: username
   			},
   			scope: this
		});
	}
	
	return {
		copy: function(obj_cache)
		{
			obj_cache.cache.eachKey(function(username, data){
				cache.add(username, mig33.clone(data));
			}, this);
			obj_cache = null;
		},
		
		get_avatar_key : function(username)
		{
			var data = cache.get(username);
			if( data == null )
			{
				get_avatar_data(username, this.save_data, this);
			}
			else
			{
				this.fireEvent("head_key", username, data.head_key);
				this.fireEvent("body_key", username, data.body_key);
			}
		},
		
		save_data: function(username, data)
		{
			cache.add(username, {head_key:data.head_key, body_key:data.body_key});
			this.fireEvent("head_key", username, data.head_key);
			this.fireEvent("body_key", username, data.body_key);
		},
		
		get_cache: function()
		{
			return cache;
		}
	}
})();

Ext.apply(mig33.AvatarCache, new Ext.util.Observable());

mig33.DisplayNameCache = (function(){
	var cache = new mig33.Cache();
	
	function get_contact_displayname(username)
	{
		var displayname = null;
		
		if( mig33.command.contacts == null)
		{
			return username;
		}
		
		mig33.command.contacts.each(function(item, index, length){
			if(mig33.in_array(username, [item.facebook_username, item.fusion_username, item.gtalk_username, item.msn_username, item.yahoo_username]))
			{
				displayname = item.display_name;
				return false;
			}
		});
		
		if(displayname == null)
			return username;
	
		return displayname;
	}
	
	return {
		get_displayname : function(username)
		{
			var displayname = cache.get(username);
			
			if(displayname == null)
			{
				displayname = get_contact_displayname(username);
				cache.add(username, displayname);
			}
			
			return displayname;
		}
	}
})();
Ext.apply(mig33.DisplayNameCache, new Ext.util.Observable());