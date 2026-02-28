class MisUser

    include ActiveModel::Validations
    
    @Id
    @Username
    @Password
    @AccessLevels = {}
    @currAccessLevel

    attr_accessor :Username, :Password, :Id, :AccessLevels, :Status, :currAccessLevel
    
    # access level to be populated only if necessary (lazy loading)
    
    def initialize(args={})
        @Username = args[:Username]
        @Password = args[:Password]
        @Id = args[:Id]
        @Status = args[:Status]
        @currAccessLevel = args[:currAccessLevel] || 0
    end
    
    def self.find(username)
        return search username
    end
    
    def self.find_all(args, paging)
        return search_users args, paging
    end
    
    def isAdmin
        
        # load access levels if not present
        if @AccessLevels.length.equal? 0
            @AccessLevels = AccessLevel.get_user_access_levels(@Id)
        end
        
        # note: admin has only one access level
        if @AccessLevels.length > 0 
            @AccessLevels.each { |key,acc_level|
                if acc_level.displayname.eql? 'Admin'
                    return true
                else
                    return false    
                end 
            }
        else
            return false
        end     
        
    end
    
    def isActive
        return @Status.equal? 1
    end
    
    def isDeleted
        return @Status.equal? 0
    end 
    
    def create
    
        db = Dbconn.get_instance('master')
        save_success = false
        error_message = ''
        
        begin
            
            stmt = db.prepare 'SELECT * FROM staff WHERE username = ? LIMIT 1'
            stmt.execute @Username
            
            if stmt.num_rows.equal? 0
                
                stmt.free_result
                stmt.close

                stmt = db.prepare 'INSERT INTO staff(Username, Password, Status, AccessLevel) VALUES(?,MD5(CONCAT(?, ?)),1,?)'
                #stmt.execute @Username, Digest::SHA1.hexdigest(@Password), @currAccessLevel
                stmt.execute @Username, @Password, @Username, @currAccessLevel
                
                @Id = stmt.insert_id
                
                stmt.close
                save_success = true
                
            else
        
                error_message = "Username #{@Username} already exists."
            end
            
        rescue Exception => e
            error_message = e.message
        ensure
            
        end
        
        return save_success, error_message
        
    end
    
    # accessLevels is an array of access level ids
    def update_access_levels(accessLevels=[])
    
        db = Dbconn.get_instance('master')
        save_success = false
        error_message = ''
        
        begin
            # db.begin_db_transaction
            
            # empty user access levels
            stmt = db.prepare 'DELETE FROM staffaccess WHERE staffid = ?'
            stmt.execute @Id
            stmt.close
            
            # save the new 
            accessLevels.each { |id|
                stmt = db.prepare 'INSERT INTO staffaccess(staffid, misaccessid) VALUES(?,?)'
                stmt.execute @Id, id
                stmt.close
            }
        
            # db.commit_db_transaction
            save_success = true
            
            @AccessLevels = AccessLevel.get_user_access_levels(@Id)
        
        rescue Exception => e
            error_message = e   
        
        ensure
            #stmt.close unless stmt.nil?
            # db.rollback_db_transaction 
        end
        
        return save_success, error_message
        
    end
    
    def destroy
    
        db = Dbconn.get_instance('master')
        save_success = false
        error_message = ''
        
        begin
        
            stmt = db.prepare 'UPDATE staff SET status = 0 WHERE Username = ?'
            stmt.execute @Username
            stmt.close
            
            save_success = true
            
        rescue Exception => e
            error_message = e
            
        ensure
        
        end
        
        return save_success, error_message  
    
    end
    
    def update_password(password)
    
        db = Dbconn.get_instance('master')
        save_success = false
        error_message = ''
        
        begin
        
            stmt = db.prepare 'UPDATE staff SET password = MD5(CONCAT(?, ?)) WHERE id = ?'
            # stmt.execute Digest::SHA1.hexdigest(password), @Id
            stmt.execute password, @Username, @Id
            
            stmt.close
            
            save_success = true
        
        rescue Exception => e
            error_message = e.message
        ensure
            
        end
        
        return save_success, error_message
        
    end
    
    private
    
    def self.search_users(args, paging)
        
        db = Dbconn.get_instance
        
        mis_users = []
        
        begin
            if args[:username].nil?
                stmt = db.prepare 'SELECT SQL_CALC_FOUND_ROWS Id, Username, Password, Status FROM staff ORDER BY Username LIMIT ?,?'
                stmt.execute paging.startRow, paging.RecordsPerPage
            else
                stmt = db.prepare 'SELECT SQL_CALC_FOUND_ROWS Id, Username, Password, Status FROM staff WHERE Username REGEXP ?  ORDER BY Username LIMIT ?,?'
                stmt.execute args[:username], paging.startRow, paging.RecordsPerPage
            end 
            
            stmtFr = db.prepare 'SELECT FOUND_ROWS() as totalRecords'
            stmtFr.execute
            
            while row = stmt.fetch do
                mis_user = MisUser.new(:Id=>row[0], :Username=>row[1], :Password=>row[2], :Status=>row[3])
                # load access levels
                mis_user.AccessLevels = AccessLevel.get_user_access_levels(mis_user.Id)
                mis_users.push mis_user
            end 
            
            while row = stmtFr.fetch do
                paging.TotalRecords = row[0]
            end
            
            stmt.free_result
            stmt.close
            stmtFr.close
            
        rescue Exception => e

        end
        
        return mis_users
        
    end
    
    def self.search(username)
    
        db = Dbconn.get_instance
        
        begin
            
            stmt = db.prepare 'SELECT Id, Username, Password, Status FROM staff WHERE Username = ?'
            stmt.execute username
        
            while row = stmt.fetch do
                mis_user = MisUser.new(:Id=>row[0], :Username=>row[1], :Password=>row[2], :Status=>row[3])
                mis_user.AccessLevels = AccessLevel.get_user_access_levels(mis_user.Id)
            end
    
            stmt.close
        
        ensure
        
        end
        
        return mis_user || nil
                
    end
    
end
