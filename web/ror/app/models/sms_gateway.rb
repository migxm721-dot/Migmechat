class SmsGateway
    
    @id
    @name
    @type
    @status
    
    attr_accessor :id, :name, :type, :status
    
    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil
        @type = args[:type] || nil
        @status = args[:status] || nil
    end
    
    def self.get(id)
        sms_gateways = self.search :id => id
        return sms_gateways.first unless sms_gateways.nil?
    end
    
    def self.all(args={})
        return self.search args
    end
    
    private
    
    def self.search(args={})
    
        sms_gateways = []
        filter = []
        filter_val = []
        app_stmt = ''
        
        if args.has_key? :id
            filter << 'id = ?'
            filter_val << args[:id]
        end
        
        if !filter.empty?
            app_stmt = 'WHERE'
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT id, name, type, status FROM smsgateway #{app_stmt} #{filter.join(' ')} ORDER BY name"
        stmt.execute *filter_val
        
        while row = stmt.fetch do
            
            gateway = SmsGateway.new(
                    :id => row[0],
                    :name => row[1],
                    :type => row[2],
                    :status => row[3]
                )       
            sms_gateways << gateway
        end
        
        stmt.free_result
        stmt.close
        
        return sms_gateways
        
    end

end