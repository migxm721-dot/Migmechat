class Photo

    @id
    @fileId
    @owner
    @status
    
    attr_accessor :id, :fileId, :owner, :status
    
    def initialize(args={})
        @id = args[:id] || nil
        @fileId = args[:fileId] || nil
        @owner = args[:owner] || nil
        @status = args[:status] || nil
    end

    def self.find_user_scrapbook_photo(username)
    
        photos = []
        
        db = Dbconn.get_instance
        stmt = db.prepare 'SELECT ID, FileID, Username, Status FROM scrapbook where Status=2 and Username = ?'
        stmt.execute username
        
        while row = stmt.fetch do
            photo = Photo.new(
                :id => row[0],
                :fileId => row[1],
                :owner => row[2],
                :status => row[3]
            )
            photos << photo
        end
        
        stmt.free_result
        stmt.close
        
        return photos
    end
end
