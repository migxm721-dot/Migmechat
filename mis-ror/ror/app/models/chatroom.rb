class Chatroom
    
    @id
    @name
    @creator
    @dateCreated
    @category
    @status
    @type
    @groupId
    @maximumSize
    @size
    @allowBots
    @userOwned
    @primaryCountryID
    @secondaryCountryID
    @allowKicking
    @adultOnly
    @dateLastAccessed
    @allowUserKeywords
    
    
    # non-db
    @group
    @moderators
    
    @@types = {
        1 => 'CHATROOM',
        2 => 'STADIUM'
    }
    
    BANNED = 2
    SUSPENDED = 1
    ALLOWED = 0
    
attr_accessor :id, :name, :creator, :dateCreated, :category, :status, :type, :groupId, :group, :maximumSize, :moderators, :size, :allowBots, :userOwned, :primaryCountryID, :secondaryCountryID, :allowKicking, :adultOnly, :dateLastAccessed, :allowUserKeywords
    
    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil
        @creator = args[:creator] || nil
        begin
            @dateCreated = DateTime.strptime(args[:dateCreated], "%m/%d/%Y %H:%M").to_time unless args[:dateCreated].nil? 
        rescue ArgumentError => e
            @dateLastAccessed = nil
        end
        @category = args[:category] || nil
        @status = args[:status]
        @type = args[:type]
        @groupId = args[:groupId]
        @group = args[:group]
        @maximumSize = args[:maximumSize]
        @moderators = args[:moderators] || []
        @allowBots = args[:allowBots] 
        @userOwned = args[:userOwned]
        @primaryCountryID = args[:primaryCountryID]
        @secondaryCountryID = args[:secondaryCountryID]
        @allowKicking = args[:allowKicking]
        @adultOnly = args[:adultOnly]
        begin
            @dateLastAccessed = DateTime.strptime(args[:dateLastAccessed], "%m/%d/%Y").to_time unless args[:dateLastAccessed].nil? 
        rescue ArgumentError => e
            @dateLastAccessed = nil
        end
        @allowUserKeywords = args[:allowUserKeywords]
    end
    
    def self.find(args={})
        return self.search args
    end
    
    def self.get(chatroom)
        rooms = self.search :name => chatroom
        return rooms.first unless rooms.empty?
    end
    
    def display_type
        return Chatroom.display_type @type
    end
    
    def self.display_type(type)
        return @@types[type] if @@types.has_key? type
    end
    
    def self.delete(chatroom)
        
        is_deleted = false
        error = nil
        
        begin
        
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            # update db 
            stmt = db.prepare 'UPDATE chatroom SET status = 0 WHERE name = ?'
            stmt.execute chatroom
            stmt.free_result
            stmt.close
            
            # deregister chatroom
            opts = [chatroom]
            is_deregistered, response = SoapClient.call('deregisterChatroomIce', opts)
            
            db.commit
            is_deleted = true
            
        rescue Exception => e
            
            db.rollback
            error = e.message
        end
        
        return is_deleted, error
        
    end
    
    def self.get_max_chatroom_kicks
        cache = CacheServer.get_instance
        max = cache.read("#{CacheServer.key_space('SYSTEM_PROPERTY')}/chatRoomBans")
        if max.nil?
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "SELECT propertyvalue FROM system WHERE propertyname = 'maxchatroombans'"
            stmt.execute 
            
            max = stmt.fetch.first.to_i unless stmt.num_rows == 0
            stmt.free_result
            stmt.close
        end
        
        return max || 0
        
    end
    
    def self.is_banned_in_chatroom_cache(username)
        
        max_kicks = Chatroom.get_max_chatroom_kicks
        
        # read from cache
        cache = CacheServer.get_instance
        banned_in_cache = cache.read("#{CacheServer.key_space('CHATROOM_BAN')}/#{username}").eql? 'TRUE'
        
        if banned_in_cache
            return true
        end
            
        return false
        
    end
    
    def self.get_bans_before_suspension
        cache = CacheServer.get_instance
        max = cache.read("#{CacheServer.key_space('SYSTEM_PROPERTY')}/ChatRoomBansBeforeSuspension")
        if max.nil?
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "SELECT propertyvalue FROM system WHERE propertyname = 'ChatRoomBansBeforeSuspension'"
            stmt.execute 
            
            max = stmt.fetch.first.to_i unless stmt.num_rows == 0
            stmt.free_result
            stmt.close
        end
        
        return max || 0
        
    end
    
    def self.get_chatroom_ban_level(username, bans)
        
        cache = CacheServer.get_instance
        
        if bans >= self.get_max_chatroom_kicks
            return BANNED
        elsif cache.read("#{CacheServer.key_space('CHATROOM_BAN')}/#{username}").eql? 'TRUE'
            return SUSPENDED
        else
            return ALLOWED
        end
        
    end
    
    def self.ban_from_chatrooms(username, duration=1, comment='', category='')
        
        is_suspended = false
        chatroom_bans = 0
        
        db = Dbconn.get_instance 'master'
        
        # get previous chatroomban value
        stmt = db.prepare "SELECT userid.id, chatroombans FROM user, userid WHERE user.username = userid.username AND userid.username = ?"
        stmt.execute username
        
        while row = stmt.fetch do
            id, chatroom_bans = row
        end
        
        stmt.free_result
        stmt.close
        
        if duration.to_i > 0
            # set ban in chatroom
            cache = CacheServer.get_instance
            cache.write("#{CacheServer.key_space('CHATROOM_BAN')}/#{username}", 'TRUE', :expires_in => duration.to_i.minutes.from_now )
        else
            stmt = db.prepare 'UPDATE user SET chatRoombans = ? WHERE username = ?'
            stmt.execute self.get_max_chatroom_kicks, username
            stmt.close
            
            # update user details in ice
            opts = [username]
            is_updated, response = SoapClient.call('updateUserDetailsIce', opts)
        end
        
        if id
            # disconnect user
            opts = [username, ApplicationHelper::sanitize_crlf(comment)]
            is_disconnected,response = SoapClient.call('disconnectUserIce', opts)
        
            # comment
            comment = MisComment.new(:Description => "Category: #{category}", :Comment => comment, :Section => 'MIG33_USER', :ObjectId => id, :Action => 'CHATROOM_SUSPEND', :StaffId => ApplicationController::get_session_user_id)
            comment.create
            
            is_suspended = true
        end
        
        return is_suspended

    end
    
    private
    
    def self.search(args={})
        
        rooms = []
        db = Dbconn.get_instance
        
        # filter = ['c.status = 1']
        filter = ['1=1']
        
        if args.has_key? :name
            filter.push args[:name].include?('%') ? "c.name LIKE ?" : "c.name = ?"
        end
        
        if args.has_key? :creator
            filter.push "c.creator = ?"
        end
        
        if args.has_key? :groupId
            filter.push "c.groupId = ?"
        end
        
        stmt = db.prepare "SELECT c.id, c.name, c.creator, date_format(c.dateCreated,'%m/%d/%Y %H:%i'), cc.name, c.status, c.type, c.maximumsize, GROUP_CONCAT(cm.username), g.name, c.groupId, allowbots, userowned, primarycountryid, secondarycountryid, allowkicking, adultonly, date_format(c.datelastaccessed,'%m/%d/%Y'), allowuserkeywords FROM chatroom c LEFT JOIN chatroomcategory cc ON c.chatRoomCategoryID = cc.ID LEFT JOIN chatroommoderator cm ON cm.chatroomID = c.id LEFT JOIN groups g ON g.ID = c.groupId WHERE #{filter.join(' AND ')} GROUP BY c.id ORDER BY c.name"
        stmt.execute *args.values
        
        while row = stmt.fetch do
            room = Chatroom.new(
                :id => row[0],
                :name => row[1],
                :creator => row[2],
                :dateCreated => row[3],
                :category => row[4],
                :status => row[5],
                :type => row[6],
                :maximumSize => row[7],
                :moderators => row[8],
                :group => row[9],
                :groupId => row[10],
                :allowBots => row[11],
                :userOwned => row[12],
                :primaryCountryID => row[13],
                :secondaryCountryID => row[14],
                :allowKicking => row[15],
                :adultOnly => row[16],
                :dateLastAccessed => row[17],
                :allowUserKeywords => row[18]
            )
            rooms.push room
        end
        
        stmt.free_result
        stmt.close
        
        return rooms
        
    end
    
end