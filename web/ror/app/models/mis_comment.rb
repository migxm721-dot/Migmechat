class MisComment

    attr_accessor :Date, :Comment, :Section, :ObjectId, :StaffId, :Action, :Description
    
    def initialize(args)
        @Comment = args[:Comment]
        @Section = args[:Section]
        @ObjectId = args[:ObjectId]
        @Date = args[:Date]
        @Action = args[:Action]
        @StaffId = args[:StaffId]
        @Description = args[:Description]
    end
    
    def create
    
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare 'INSERT INTO mislog(comment, section, objectid, action, staffid, datecreated, description) VALUES (?,?,?,?,?,NOW(),?)'
            stmt.execute @Comment, @Section, @ObjectId, @Action, @StaffId, @Description
            db.commit
            stmt.close
            
        rescue Exception => e
            db.rollback
            throw e.message
        end
        
    end
    
    def self.list(args)
    
        comments = []
        
        filterColumn = 'AND section = ? AND objectid = ? '
        filter = [args[:section], args[:objectid]]

        if !args[:action].nil?
            filterColumn = "#{filterColumn} AND action = ? "
            filter.push args[:action]
        end
                
        begin
        
            db = Dbconn.get_instance
            stmt = db.prepare " SELECT username, comment, action, datecreated, description
                                FROM staff, mislog 
                                WHERE staff.id = mislog.staffid 
                                #{filterColumn}
                                ORDER BY datecreated"
            stmt.execute *filter
            
            while row = stmt.fetch do
                comment_entry = {:Staff => row[0], :Comment => row[1], :Action => row[2], :Date => row[3]}
                comments.push comment_entry
            end                     
            
            stmt.free_result
            stmt.close
            
        rescue Exception => e
            throw e.message
        end
        
        return comments
        
    end
    
end