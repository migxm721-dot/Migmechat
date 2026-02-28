class MerchantTag

    @id
    @userID
    @merchantUserID
    @dateCreated
    @lastSaleDate
    @status
    @username
    
    attr_accessor :id, :userID, :merchantUserID, :dateCreated, :lastSaleDate, :status, :username
    
    def initialize(args={})
        @id = args[:id]
        @userID = args[:userId]
        @merchantUserID = args[:merchantUserId]
        begin
            @dateCreated = DateTime.strptime(args[:dateCreated], "%B %d, %Y").to_time unless args[:dateCreated].nil?
        rescue
            @dateCreated = nil
        end
        begin
            @lastSaleDate = DateTime.strptime(args[:lastSaleDate], "%B %d, %Y").to_time unless args[:lastSaleDate].nil?
        rescue
            @lastSaleDate = nil
        end
        @status = args[:status]
        @username = args[:username]
    end
    
    def self.remove_tags(merchantUserID)
        
        db = Dbconn.get_instance 'master'
        stmt = db.prepare "SELECT m.id
                                  , username 
                           FROM merchanttag m
                                , userid ui
                           WHERE m.userID = ui.id 
                           AND m.merchantuserID = ? 
                           AND status = 1
                           GROUP BY m.userID"
        stmt.execute merchantUserID                
        
        tags = []
        
        return true if stmt.num_rows.eql? 0
        
        cache = CacheServer.get_instance
        
        while row = stmt.fetch do
            tags << row[0]
            # clear cache
            cache.delete "#{CacheServer.key_space('MERCHANT_TAG')}/#{row[1]}"
        end
    
        stmt.free_result
        stmt = db.prepare "UPDATE merchanttag
                           SET status = 0
                           WHERE id IN (#{tags.join(',')})"
        stmt.execute
        
        return stmt.affected_rows > 0 ? true : false
        
    end
    
    
end