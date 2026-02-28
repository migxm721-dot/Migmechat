class Activation
    
    @id
    @username
    @dateCreated
    @mobilePhone
    @ipAddress
    
    attr_accessor :id, :username, :dateCreated, :mobilePhone, :ipAddress
    
    def initialize(args={})
        @id = args[:id] || nil
        @username = args[:username] || nil
        begin
            @dateCreated = DateTime.strptime(args[:dateCreated], "%m/%d/%Y").to_time unless args[:dateCreated].nil? 
        rescue ArgumentError => e
            @dateCreated = nil
        end
        @mobilePhone = args[:mobilePhone] || nil
        @ipAddress = args[:ipAddress] || nil
    end
    
    def self.find(args={})
        return self.search args
    end
    
    private
    
    def self.search(args)
    
        activations = []
        filters = []
        
        return activations if args.empty? 
        
        if args.has_key? 'username'.to_sym
            filters << 'username = ?'
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT id, username, date_format(dateCreated,'%m/%d/%Y'), mobilePhone, ipAddress FROM activation WHERE #{filters.join(' AND ')} ORDER BY dateCreated DESC"
        stmt.execute *args.values
        
        while row = stmt.fetch do
            activation = Activation.new(
                :id => row[0],
                :username => row[1],
                :dateCreated => row[2],
                :mobilePhone => row[3],
                :ipAddress => row[4]
            )
            
            activations << activation
        end
        
        stmt.free_result
        stmt.close
        
        return activations
        
    end
    
end