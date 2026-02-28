class Log

    @date
    @comment
    @section
    @objectId
    @staffId
    @action
    @description
    @staff  
    
    attr_accessor :date, :comment, :section, :objectId, :staffId, :action, :description, :staff
    
    def initialize(args)
        @comment = args[:comment]
        @section = args[:section]
        @objectId = args[:objectId]
        begin
            @date = DateTime.strptime(args[:date], "%m/%d/%Y").to_time unless args[:date].nil?
        rescue
            @date = nil
        end
        @action = args[:action]
        @staffId = args[:staffId]
        @description = args[:description]
        @staff = args[:staff]
    end
    
    def create
    
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "INSERT INTO mislog(description, section, objectid, action, staffid, datecreated, comment) VALUES (?,?,?,?,?,NOW(),' ')"
            stmt.execute @description, @section, @objectId, @action, @staffId
            stmt.close
        rescue Exception => e
            throw e.message
        end
        
    end
    
    def self.list(args)
    
        comments = []
        
        filterColumn = ['section = ? AND objectid = ?']
        filter = [args[:section], args[:objectid]]
        limit = ''
        
        if !args[:action].nil?
            filterColumn << "AND action = ? "
            filter << args[:action]
        end
        
        if args.has_key? :dateFrom
            filterColumn << "AND dateCreated >= ? "
            filter << args[:dateFrom]
        end
        
        if args.has_key? :dateTo
            filterColumn << "AND dateCreated <= ? "
            filter << args[:dateTo]
        end
        
        if args.has_key? :limit
            limit = "LIMIT #{args[:limit]}"
        end
        
        order_by =  args.has_key?(:orderBy) ? args[:orderBy] : 'DESC'
        
        begin
            
            db = Dbconn.get_instance
        
            stmt = db.prepare " SELECT username, comment, action, datecreated, description
                                FROM mislog LEFT JOIN staff ON staff.id = mislog.staffid   
                                WHERE #{filterColumn.join(' ')}
                                ORDER BY datecreated #{order_by}
                                #{limit}"
                                
            stmt.execute *filter
            
            while row = stmt.fetch do
                comment_entry = {:staff => row[0], :comment => row[1], :action => row[2], :date => row[3], :description => row[4]}
                comments << comment_entry
            end                     
            
            stmt.free_result
            stmt.close
        
        rescue Exception => e
            throw e.message
        end
        
        return comments
        
    end
    
end