mig33.Handler = function(transport)
{
	this.transport = transport;
	this.handlers = new Ext.util.MixedCollection();
	this.handlers.add(0, new mig33.Handler.ErrorHandler(this.transport));
	this.handlers.add(1, new mig33.Handler.OkHandler(this.transport) );
	this.handlers.add(5, new mig33.Handler.AlertHandler(this.transport) );
	this.handlers.add(8, new mig33.Handler.ServerQuestionHandler(this.transport) );
    this.handlers.add(16, new mig33.Handler.LoginCaptchaHandler(this.transport) );
	this.handlers.add(201, new mig33.Handler.LoginChallengeHandler(this.transport) );
	this.handlers.add(203, new mig33.Handler.LoginSuccessfulHandler(this.transport) );
	this.handlers.add(207, new mig33.Handler.IMStatusHandler(this.transport) );
	this.handlers.add(301, new mig33.Handler.SessionTerminatedHandler(this.transport) );
	this.handlers.add(401, new mig33.Handler.ContactGroupHandler(this.transport) );
	this.handlers.add(402, new mig33.Handler.ContactHandler(this.transport) );
	this.handlers.add(403, new mig33.Handler.ContactCompletedHandler(this.transport) );
	this.handlers.add(404, new mig33.Handler.PresenceHandler(this.transport) );
	this.handlers.add(406, new mig33.Handler.RemoveContactHandler(this.transport) );
	this.handlers.add(409, new mig33.Handler.RemoveGroupHandler(this.transport) );
	this.handlers.add(412, new mig33.Handler.ContactRequestHandler(this.transport) );
	this.handlers.add(418, new mig33.Handler.DefaultHandler(this.transport) );
	this.handlers.add(420, new mig33.Handler.DefaultHandler(this.transport));
	this.handlers.add(421, new mig33.Handler.ContactStatusMessageHandler(this.transport));
	this.handlers.add(422, new mig33.Handler.NotificationMessageHandler(this.transport));
	this.handlers.add(423, new mig33.Handler.DisplayPictureHandler(this.transport));
	this.handlers.add(500, new mig33.Handler.MessageHandler(this.transport));
	this.handlers.add(502, new mig33.Handler.FileReceivedHandler(this.transport));
	this.handlers.add(503, new mig33.Handler.MailInfoHandler(this.transport));
	this.handlers.add(603, new mig33.Handler.UserAvatarHandler(this.transport));
	this.handlers.add(701, new mig33.Handler.ChatroomHandler(this.transport));
	this.handlers.add(708, new mig33.Handler.ChatroomParticipantHandler(this.transport));
	this.handlers.add(714, new mig33.Handler.CategoryHandler(this.transport));
	this.handlers.add(715, new mig33.Handler.CategoryEndHandler(this.transport));
	this.handlers.add(717, new mig33.Handler.CategoryChatroomCompletedHandler(this.transport));
	this.handlers.add(718, new mig33.Handler.ChatroomStatisticsHandler(this.transport));
	this.handlers.add(719, new mig33.Handler.ChatroomUserEnterEventHandler(this.transport));
	this.handlers.add(750, new mig33.Handler.GroupChatHandler(this.transport));
	this.handlers.add(755, new mig33.Handler.ChatroomParticipantHandler(this.transport));
	this.handlers.add(902, new mig33.Handler.AccountBalanceHandler(this.transport) );
	this.handlers.add(916, new mig33.Handler.EmoticonHandler(this.transport) );
	this.handlers.add(920, new mig33.Handler.DefaultHandler(this.transport) );
	this.handlers.add(1000, new mig33.Handler.DefaultHandler(this.transport) );
};

Ext.extend(mig33.Handler, Ext.util.Observable,
{
	process: function(packet)
	{
		if( this.handlers.containsKey(packet.type) )
		{
			handler = this.handlers.get(packet.type);
			handler.handle(packet);
		}
		else
		{
			alert("Unhandled packet:" + packet.to_xml());
		}
	}
});

mig33.Handler.ErrorHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var error = packet.get_field_value(2);

        if( mig33.app_state == mig33.Application_State.logging_in )
        {
			if (mig33.SSOLoginEnabled)
			{
				mig33.show_logout_forced();
			}
			else
			{
				mig33.show_login_failed(error);
			}
        }
        else
        {
            Ext.MessageBox.alert("Error", error, function() {}, this);
        }
	}
	this.handle = handle;
}

mig33.Handler.AlertHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var type = parseInt(packet.get_field_value(1));
		var content = packet.get_field_value(2);
		var content_type = packet.get_field_value(3);
		
		var title = "Alert";
		switch(type)
		{
			case 1:
				title = "Information";
				break;
			case 2:
				title = "Warning";
				break;
			case 3:
				title = "Error";
				break;
		}
		
		Ext.MessageBox.alert(title, content);
	}
	this.handle = handle;

}

mig33.Handler.ServerQuestionHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var question = packet.get_field_value(2); 
		var url = packet.get_field_value(3); 
		var title = packet.get_field_value(4);
		url = url.replace('v=midlet', 'v=ajax').replace("\/midlet\/", "\/ajax\/");
		
		Ext.MessageBox.confirm(title, question, 
			function(btn)
			{
				if(btn == 'yes')
				{

					mig33.show_ajax_window({
						id: "server-question-" + mig33.uuid(6, 10),
						title: "mig33 Notification",
						width: 500,
						height: 400
					}, url);
				}
			}
		);
	}
	this.handle = handle;
}

mig33.Handler.OkHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		
	}
	this.handle = handle;

}

mig33.Handler.RemoveGroupHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var group_id = packet.get_field_value(1);
		
		mig33.EventManager.fire_event_remove_contact_group(group_id);
	}
	this.handle = handle;

}


mig33.Handler.MailInfoHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var new_email = packet.get_field_value(1); // getting the number of new email in the user's inbox
		
		mig33.EventManager.fire_event_new_email(new_email);
	}
	this.handle = handle;

}


mig33.Handler.CategoryChatroomCompletedHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var more_message = packet.get_field_value(1);
		mig33.EventManager.fire_event_get_chatrooms_completed(packet.transaction_id, more_message);
	}
	this.handle = handle;

}

mig33.Handler.ContactStatusMessageHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var contact_id = packet.get_field_value(1);
		var status = packet.get_field_value(2);
		
		mig33.EventManager.fire_event_update_contact_status_message(contact_id, status);		
	}
	this.handle = handle;

}

mig33.Handler.NotificationMessageHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var seconds_ago = packet.get_field_value(1);
		var type = packet.get_field_value(2);
		var image = packet.get_field_value(3);
		var text = packet.get_field_value(4);
		
		mig33.EventManager.fire_event_user_event(seconds_ago, type, image, text);
	}
	this.handle = handle;

}


mig33.Handler.FileReceivedHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var source_type = packet.get_field_value(1);
		var source = packet.get_field_value(2);
		var contact_id = packet.get_field_value(3);
		var info_message = packet.get_field_value(4);
		var url = packet.get_field_value(5);
		var file_id = url.substring(url.lastIndexOf('=') + 1, url.length);
		
		mig33.EventManager.fire_event_file_received(source_type, source, contact_id, info_message, url, file_id);
	}
	this.handle = handle;

}

mig33.Handler.AccountBalanceHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var account_balance = packet.get_field_value(1);
			
		mig33.EventManager.fire_event_show_account_balance(account_balance);
	}
	this.handle = handle;

}

mig33.Handler.SessionTerminatedHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var reason = packet.get_field_value(1);
		
		mig33.EventManager.fire_event_session_terminated(reason);
	}
	this.handle = handle;

}

mig33.Handler.DisplayPictureHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var contact_id = packet.get_field_value(1);
		var display_picture = packet.get_field_value(2);
		var timestamp = packet.get_field_value(3);
	}
	this.handle = handle;

}

mig33.Handler.ContactRequestHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var username = packet.get_field_value(1);
		var profile_url = packet.get_field_value(2);
		
		mig33.EventManager.fire_event_show_contact_request(username, profile_url);
	}
	this.handle = handle;

}

mig33.Handler.RemoveContactHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var contact_id = packet.get_field_value(1);
		
		mig33.EventManager.fire_event_remove_contact(contact_id);
	}
	this.handle = handle;

}


mig33.Handler.IMStatusHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var im_type = packet.get_field_value(1);
		var online = packet.get_field_value(2)==1;
		var message = packet.get_field_value(3);
		
		mig33.EventManager.fire_event_im_status(im_type, online, message);
	}
	this.handle = handle;

}

mig33.Handler.ChatroomUserEnterEventHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var chatroom = packet.get_field_value(1);
		var user = packet.get_field_value(2);
		
		mig33.EventManager.fire_event_chatroom_user_enter(chatroom, user);
	}
	this.handle = handle;
}

mig33.Handler.ChatroomStatisticsHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var stats = packet.get_field_value(1);
		
		/*
		if(mig33.desktop.contact)
		{
			var stats = packet.get_field_value(1);
			mig33.desktop.contact.show_chatroom_statistics(stats);
		}
		*/
		
		mig33.EventManager.fire_event_show_chatroom_stats(stats);
	}
	this.handle = handle;

}


mig33.Handler.DefaultHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
	/*
		var group_chat_id = packet.get_field_value(1);
		var creator = packet.get_field_value(2);
		mig33.desktop.chatroom_manager.create_group_chat(group_chat_id, creator);
	*/
	}
	this.handle = handle;
}

mig33.Handler.GroupChatHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
	
	}
	this.handle = handle;
}


mig33.Handler.ChatroomParticipantHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var chatroom_name = packet.get_field_value(1);
		var p = packet.get_field_value(2);
		var admins = packet.get_field_value(3);
		
		mig33.EventManager.fire_event_show_chatroom_participants(chatroom_name, p, admins);
	}
	this.handle = handle;
}


mig33.Handler.MessageHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var message_type = packet.get_field_value(1);
		var source = packet.get_field_value(2);
		var destination_type = packet.get_field_value(3);
		var destination = packet.get_field_value(4);
		var contact_id = packet.get_field_value(5);
		var content_type = packet.get_field_value(6);
		var message = packet.get_field_value(8);
		var admin_message = packet.get_field_value(9);
		var allow_emoticons = packet.get_field_value(11);
		var source_colour = packet.get_field_value(12);
		var message_colour = packet.get_field_value(13);
		
		mig33.EventManager.fire_event_message_received(message_type, source, destination_type, destination, contact_id, message, content_type, admin_message, allow_emoticons, source_colour, message_colour);
	}
	this.handle = handle;
}

mig33.Handler.UserAvatarHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var status_message = packet.get_field_value(1);
		var display_picture = packet.get_field_value(2);
		var badge_hotkey = packet.get_field_value(3);
		
		mig33.EventManager.fire_event_update_avatar(status_message, display_picture);
	}
	this.handle = handle;

}

mig33.Handler.CategoryHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var id = packet.get_field_value(1);
		var name = packet.get_field_value(2);
		var should_replace = packet.get_field_value(3);
		var can_delete = packet.get_field_value(5)==1;
		
		mig33.EventManager.fire_event_add_chatroom_category(id, name, can_delete);
	}
	this.handle = handle;

}

mig33.Handler.CategoryEndHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		mig33.EventManager.fire_event_disable_loading_chatroom_category();
	}
	this.handle = handle;

}

mig33.Handler.ChatroomHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var name = packet.get_field_value(1);
		var description = packet.get_field_value(2);
		var max_size = packet.get_field_value(3);
		var size = packet.get_field_value(4);
		var adult_only = packet.get_field_value(5);
		var category = packet.get_field_value(6);
		var user_owned = packet.get_field_value(7);
		var group_id = packet.get_field_value(8);
		var id = packet.get_field_value(9);
		var creator = packet.get_field_value(10);
		
		if( Ext.isDefined(group_id) == false )
			group_id = 0;
			
		mig33.EventManager.fire_event_add_chatroom(id, name, description, max_size, size,
							adult_only, category, (user_owned==1), group_id);
	}
	this.handle = handle;

}

mig33.Handler.EmoticonHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var hotkeys = packet.get_field_value(1) + ' ' + packet.get_field_value(2);
		mig33.command.emoticon_hotkeys = packet.get_field_value(1).split(' ');
		mig33.command.emoticon_hotkeys_alt = packet.get_field_value(2).split(' ');
		mig33.command.emoticon_hotkeys_all = hotkeys.split(' ');
		mig33.command.emoticon_hotkeys_array = [];

		mig33.EventManager.fire_event_get_emoticon(hotkeys);
	}
	this.handle = handle;
}

mig33.Handler.PresenceHandler = function(transport)
{
	var transport = transport;

    function handle(packet)
	{
		var id = packet.get_field_value(1);
		var fusion_presence = packet.get_field_value(2);
		var msn_presence = packet.get_field_value(3);
		var yahoo_presence = packet.get_field_value(5);
		var gtalk_presence = packet.get_field_value(7);
		var facebook_presence = packet.get_field_value(8);

        mig33.EventManager.fire_event_presence(id, fusion_presence, msn_presence, yahoo_presence, gtalk_presence, facebook_presence);
	}
	this.handle = handle;

}

mig33.Handler.ContactGroupHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var id = packet.get_field_value(1);
		var name = packet.get_field_value(2);
		
		mig33.EventManager.fire_event_add_contact_group(id, name);
	}
	this.handle = handle;
}

mig33.Handler.ContactHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		if( mig33.command.contacts == null )
		{
			mig33.command.contacts = new Ext.util.MixedCollection();
		}
		
		var contact_status = packet.get_field_value(27);

		var contact = new mig33.Contact(packet.get_field_value(1), packet.get_field_value(2), packet.get_field_value(3),
			packet.get_field_value(8), packet.get_field_value(12), packet.get_field_value(13), packet.get_field_value(14),
			packet.get_field_value(15), packet.get_field_value(18), packet.get_field_value(19), packet.get_field_value(22), 
			packet.get_field_value(23), packet.get_field_value(28), packet.get_field_value(29), packet.get_field_value(30),
			contact_status);

		mig33.EventManager.fire_event_add_contact(contact.id, contact);
	}
	this.handle = handle;
}

mig33.Handler.ContactCompletedHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		mig33.EventManager.fire_event_get_contact_completed();
	}
	this.handle = handle;
}

mig33.Handler.LoginSuccessfulHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var msn_detail = packet.get_field_value(6);
		var yahoo_detail = packet.get_field_value(8);
		var gtalk_detail = packet.get_field_value(17);
		var facebook_detail = packet.get_field_value(30);
		
		var new_email = packet.get_field_value(14);
		
		if (msn_detail == 2) mig33.command.msn_signed_in = true;
		if (yahoo_detail == 2) mig33.command.yahoo_signed_in = true;
		if (gtalk_detail == 2) mig33.command.gtalk_signed_in = true;
		if (facebook_detail == 2) mig33.command.facebook_signed_in = true;
	
		if (packet.get_field_value(13) != null)
			if (packet.get_field_value(13).length > 0)
				mig33.command.allow_email = true;
		
		//process login alert message from MIS
		var login_message = packet.get_field_value(3);   		// get message string or URL
		var login_mtype = packet.get_field_value(4);			// 1 = string, 2 = URL
		
		mig33.command.session_user_level = packet.get_field_value(31);
		mig33.command.session_user_level_image = packet.get_field_value(32);
		mig33.command.session_user_type = packet.get_field_value(27);

		mig33.localStorage.set('_sess', {
			m:msn_detail,
			y:yahoo_detail,
			g:gtalk_detail,
			f:facebook_detail,
			n:0,//new_email,
			a:mig33.command.allow_email,
//			lm:login_message,
//			lt:login_mtype,
			l:mig33.command.session_user_level,
			i:mig33.command.session_user_level_image,
			t:mig33.command.session_user_type
		});
		
		mig33.EventManager.fire_event_login_successful(new_email, msn_detail, yahoo_detail, gtalk_detail);
	}
	this.handle = handle;
}

mig33.Handler.LoginChallengeHandler = function(transport)
{
	var transport = transport;
	
	function handle(packet)
	{
		var challenge = packet.get_field_value(1);
		mig33.command.session_id = packet.get_field_value(2);
			
		mig33.create_cookie('sid', mig33.command.session_id);
	
		var challenge_hash = challenge + mig33.command.session_p;
		var password_hash = mig33.hash_code(challenge_hash);
		
		transport.send_login_challenge_response(password_hash);
	}
	this.handle = handle;
}

mig33.Handler.LoginCaptchaHandler = function(transport)
{
	var transport = transport;

	function handle(packet)
	{
		mig33.show_captcha_challenge(packet.get_field_value(1),packet.get_field_value(2));
	}
	this.handle = handle;
}