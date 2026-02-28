mig33.JSONThreadWorker = function(transport, json_data, options)
{
	var data = json_data;
	var options = options;
	var transport = transport;
	
	this.batch_id = 0;
	this.working = false;
	
	this.execute = function()
	{
		var check_queue = false;
		var packet = transport.create_packet_from_json(data);
		if(transport.queue.remove_packet(packet))
		{
			check_queue = true;
		}
				
		if( options.extraParams.callback == null || (options.extraParams.callback && packet.is_error_packet()==false ) )
			transport.handler.process(packet);
		if( options.extraParams.callback && packet.transaction_id == options.extraParams.transaction_id )
		{
			options.extraParams.callback.call(options.extraParams.scope||transport, packet?(!packet.is_error_packet()):true, packet);
		}
		
		if( check_queue )
			transport.queue.check_queue();
		
		check_queue = null;
		packet = null;
	}
}

mig33.XMLThreadWorker = function(transport, xml_data, options)
{
	var data = xml_data;
	var options = options;
	var transport = transport;
	
	this.batch_id = 0;
	this.working = false;
	
	this.execute = function()
	{
		var packet = transport.create_packet_from_xml(data);
		var check_queue = false;
			
		if(transport.queue.remove_packet(packet))
			check_queue = true;
		
		if( options.extraParams.callback == null || (options.extraParams.callback && packet.is_error_packet()==false ) )
			transport.handler.process(packet);
		if( options.extraParams.callback && packet.transaction_id == options.extraParams.transaction_id )
		{
			options.extraParams.callback.call(options.extraParams.scope||transport, packet?(!packet.is_error_packet()):true, packet);
		}
		
		if( check_queue )
			transport.queue.check_queue();

		check_queue = null;
		packet = null;
	}
}

mig33.ThreadManager = function()
{
	var queue = [];
	var timeout = 40;
	var timer = null;
    var self = this;
	
	this.queue_worker = function(worker, begin_execute)
	{
		queue.push(worker);
		if( Ext.isDefined(begin_execute) && begin_execute == true )
		{
			this.start();
		}
	}
	
	this.execute = function()
	{
		timer = null;
		var worker = queue.shift();
		if( worker.working == false )
		{
			worker.working = true;
			worker.execute();
			worker.working = false;
		}
		else
		{
			queue.unshift(worker);
		}
		
		if( queue.length > 0 )
			self.start();
		else
			self.stop();
	}
	
	this.start = function()
	{
		if( timer == null )
			timer = setTimeout(this.execute, timeout);
	}
	
	this.stop = function()
	{
		if(timer != null )
			clearTimeout(timer);
		timer = null;
	}
}