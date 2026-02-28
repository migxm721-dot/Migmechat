mig33.ContactGroup = function(id, name, path)
{
	this.id = id;
	this.name = name;
	this.path = path;
};

mig33.Contact = function(
					id, 
					group_id,
					display_name, 
					mobile_phone,
					fusion_username,
					fusion_presence,
					msn_username,
					msn_presence,
					yahoo_username,
					yahoo_presence,
					gtalk_username,
					gtalk_presence,
					display_picture,
					facebook_username,
					facebook_presence,
					status_message)
{
	this.id = id;
	this.group_id = group_id;
	this.display_name = display_name;
	this.mobile_phone = mobile_phone;
	this.fusion_username = fusion_username;
	this.fusion_presence = parseInt(fusion_presence);
	this.msn_username = msn_username;
	this.msn_presence = parseInt(msn_presence);
	this.yahoo_username = yahoo_username;
	this.yahoo_presence = parseInt(yahoo_presence);
	this.gtalk_username = gtalk_username;
	this.gtalk_presence = parseInt(gtalk_presence);
	this.display_picture = display_picture;
	this.facebook_username = facebook_username;
	this.facebook_presence = parseInt(facebook_presence);
	this.status_message = status_message;
	
	function get_username()
	{
		//return this.display_name;
		var username = this.fusion_username;
		if( this.fusion_username == null )
		{
			if( this.msn_username != null )
			{
				username = this.msn_username;
			}
			else if(this.yahoo_username != null )
			{
				username = this.yahoo_username;
			}
			else if(this.gtalk_username != null)
			{
				username = this.gtalk_username;
			}
			else if( this.facebook_username != null )
			{
				username = this.facebook_username;
			}
		}
		return username;
	}
	this.get_username = get_username;
	
	function get_type()
	{
		var type = mig33.IM_Type.fusion;
		if( this.fusion_username == null )
		{
			if( this.msn_username != null )
			{
				type = mig33.IM_Type.msn;
			}
			else if(this.yahoo_username != null )
			{
				type = mig33.IM_Type.yahoo;
			}
			else if(this.gtalk_username != null)
			{
				type = mig33.IM_Type.gtalk;
			}
			else if(this.facebook_username != null)
			{
				type = mig33.IM_Type.facebook;
			}
			else
			{
				type = "mobile";
			}
		}
		return type;
	}
	this.get_type = get_type;
	
	function get_presence()
	{
		if( this.fusion_username == null )
		{
			if( this.msn_username != null )
			{
				return this.msn_presence;
			}
			else if(this.yahoo_username != null )
			{
				return this.yahoo_presence;
			}
			else if(this.gtalk_username != null)
			{
				return this.gtalk_presence;
			}
			else if(this.facebook_username != null)
			{
				return this.facebook_presence;
			}
		}
		return this.fusion_presence;
	}
	this.get_presence = get_presence;
	
	function get_presence_class()
	{
		return mig33.get_icon_class(this.get_presence());
	}
	this.get_presence_class = get_presence_class;
	
	function get_presence_icon_class()
	{
		return mig33.get_presence_icon_class(this.get_type(), this.get_presence());
	}
	this.get_presence_icon_class = get_presence_icon_class;	
};