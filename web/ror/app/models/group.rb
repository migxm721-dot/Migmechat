class Group
    
    @@type = {
        0 => 'Public',
        1 => 'Private',
        3 => 'By Approval' 
    }
    
    @id
    @name
    @deteCreated
    @description
    @picture
    @numMembers
    @createdBy
    @status
    @type
    
    # derived values
    @chatrooms
    
    attr_accessor :id, :name, :dateCreated, :description, :picture, :numMembers, :chatrooms, :createdBy, :status, :type

    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil
        begin
            @dateCreated = DateTime.strptime(args[:dateCreated], "%m/%d/%Y").to_time unless args[:dateCreated].nil? 
        rescue ArgumentError => e
            @dateCreated = nil
        end
        @description = args[:description] || nil
        @picture = args[:picture] || nil
        @numMembers = args[:numMembers] || nil
        @chatrooms = args[:chatrooms] || []
        @createdBy = args[:createdBy] || nil
        @status = args[:status] || nil
        @type = args[:type] || nil
    end
    
    def update_attribute(field, value)
        
        errors = {}
        isSaved = false
        
        begin
            # TODO: logging
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "UPDATE groups SET #{field} = ? WHERE id = ?"
            stmt.execute value, @id
            stmt.close
                
            isSaved = true
        rescue Exception => e
            errors[field.to_sym] = "Error updating #{field}: #{e.message}"
            AuditHelper::log "ERROR:: USER:#{ApplicationController::get_session_user}; Error updating #{field}: #{e.message}"
        end
    
        return isSaved, errors
        
    end
    
    def remove_display_picture
    end
    
    def self.display_type(type)
        return @@type[type] if @@type.has_key? type
    end
    
    def self.get(name)
        groups = self.search :name => name
        return groups.first unless groups.empty?
    end
    
    def self.get_by_id(id)
        groups = self.search :id => id
        return groups.first unless groups.empty?
    end
        
    def self.find(args={})
        return self.search args
    end
    
    def self.group_member_registrations(args)
    
        groupName = args[:groupName] || ''
        dateFrom = args[:dateFrom] || ''
        dateTo = args[:dateTo] || ''
        
        db = Dbconn.get_instance
        
        result = []
        qryArgs = []
        member_qryArgs = []
        
        # TODO: mySql GROUP_CONCAT might be of help for getting the members - might get truncated due to group_concat_max_len or max_allowed_packet limit
        qry = "SELECT groups.id, groups.name, groups.datecreated, COUNT(DISTINCT groupmember.username) as numMembers FROM groups, groupmember, user, country WHERE groups.ID = groupmember.groupid AND groupmember.username = user.username AND user.countryID = country.id "
        
        member_qry = "SELECT groupmember.username, country.name FROM groupmember, user, country WHERE groupmember.username = user.username AND user.countryID = country.id AND groupmember.groupid = ? "

        if !groupName.empty?
            qry = "#{qry} AND groups.name = ? "
            qryArgs.push groupName
        end 
        
        if !dateFrom.empty?
            qry = "#{qry} AND groupmember.datecreated >= ? "
            qryArgs.push DateTime.strptime(dateFrom, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
            
            member_qry = "#{member_qry} AND groupmember.datecreated >= ? "
            member_qryArgs.push DateTime.strptime(dateFrom, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        end
        
        if !dateTo.empty?
            qry = "#{qry} AND groupmember.datecreated <= ? "
            qryArgs.push DateTime.strptime(dateTo, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        
            member_qry = "#{member_qry} AND groupmember.datecreated <= ? "
            member_qryArgs.push DateTime.strptime(dateTo, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        end
        
        qry = "#{qry} GROUP BY groups.id"
        
        stmt = db.prepare qry
        if qryArgs.empty?
            stmt.execute
        else    
            stmt.execute *qryArgs       
        end
        
        while row = stmt.fetch do
            
            members = []
            member_stmt = db.prepare member_qry
            
            if member_qryArgs.empty?
                member_stmt.execute row[0].to_s
            else    
                member_stmt.execute row[0].to_s, *member_qryArgs        
            end
            
            while member_row = member_stmt.fetch do
                member = {:username => member_row[0], :country => member_row[1]}
                members.push member
            end 
            
            member_stmt.free_result
            member_stmt.close
            
            rowResult = { :groupName => row[1], :dateCreated => row[2], :memberCount => row[3], :members => members }
            result.push rowResult
        end
        
        stmt.free_result
        stmt.close
        
        return result
        
    end
    
    private
    
    def self.search(args)
    
        filter = []
        groups = []
        
        if args[:createdBy]
            filter << "createdBy = ?"
        end
        
        if args[:name]
            if args[:name].include? '%'
                filter << "name LIKE ?"
            else
                filter << 'name = ?'
            end     
        end
        
        if args[:id]
            filter << "id=?"
        end
        
        if !filter.empty?
            db = Dbconn.get_instance
            stmt = db.prepare "SELECT id, name, description, date_format(dateCreated,'%m/%d/%Y'), picture, numMembers, status, createdby, type FROM groups WHERE #{filter.join(' AND ')}"
            stmt.execute *args.values
        
            while row = stmt.fetch do
            
                chatrooms = Chatroom.find :groupId => row[0]
            
                group = Group.new(
                    :id => row[0],
                    :name => row[1],
                    :description => row[2],
                    :dateCreated => row[3],
                    :picture => row[4],
                    :numMembers => row[5],
                    :chatrooms => chatrooms,
                    :status => row[6],
                    :createdBy => row[7],
                    :type => row[8]
                )
            
                groups << group
            
            end
            
            stmt.free_result
            stmt.close
        
        end
        
        return groups
    end

end