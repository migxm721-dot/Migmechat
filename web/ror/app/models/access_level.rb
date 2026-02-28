class AccessLevel

    attr_accessor :Id, :Name, :Description
    
    def initialize(args={})
        @Id = args[:Id]
        @Name = args[:Name]
        @Description = args[:Description]
    end
    
    def self.find_all
    
        db = Dbconn.get_instance
        accessLevels = []
        
        stmt = db.prepare " SELECT accesslevel, name, description FROM misaccess"
        stmt.execute
        
        while row = stmt.fetch do
            accessLevels.push AccessLevel.new(:Id=>row[0], :Name=>row[1], :Description=>row[2])
        end     
        
        stmt.free_result
        stmt.close
        
        return accessLevels
        
    end

    def self.get_user_access_levels(userId)
        
        db = Dbconn.get_instance
        
        accessLevels = {}
        
        stmt = db.prepare " SELECT misaccessid, name, description 
            FROM staffaccess, misaccess
                WHERE staffaccess.misaccessid = misaccess.id
                AND staffaccess.staffid = ?"
        stmt.execute userId
            
        while row = stmt.fetch do
            accessLevels[row[0]] = AccessLevel.new(:Id=>row[0], :Name=>row[1], :Description=>row[2])
        end                 
        
        stmt.free_result
        stmt.close
        
        return accessLevels
        
    end
    
    def self.extractVal
        
        db = Dbconn.get_instance
        access_levels = {}
        
        stmt = db.prepare " SELECT id, name, description FROM misaccess ORDER BY name"
        stmt.execute
        
        while row = stmt.fetch do
            access_level = AccessLevel.new(:Id=>row[0], :Name=>row[1], :Description=>row[2])
            access_levels[access_level.displayname] = {} unless access_levels.has_key? access_level.displayname
            access_levels[access_level.displayname][access_level.access] = access_level.Id  unless access_level.displayname.eql? 'Admin'
        end     
        
        stmt.free_result
        stmt.close
        
        return access_levels
        
    end
    
    def self.add_access_level_to_user
    end
    
    def self.remove_access_level_to_user
    end
    
    def displayname
        
        if @Name.downcase.eql? 'admin'
            return 'Admin'
        else
            return @Name.sub(/(READ|WRITE|APPROVE)/,'').tr_s('_',' ').titleize.strip    
        end     
        
    end
    
    def access
        if @Name.downcase.eql? 'admin'
            return 'admin'
        else
            return @Name.split('_').pop.downcase
        end
    end
    
    def displaynameCompressed
        if @Name.downcase.eql? 'admin'
            return 'Admin'
        else
            return @Name.sub(/(READ|WRITE|APPROVE)/,'').tr_s('_','').titleize.strip 
        end     
    end
    
end