class Security

    def self.get_session_user(cookies)
        
        cacheServer = CacheServer.get_instance
        
        if cacheServer.read("mis_user_#{cookies[:sess_key]}").nil?
            return nil
        else
            
            uname = cacheServer.read("mis_user_#{cookies[:sess_key]}")
            
            mis_user = MisUser.find(uname)
            
            # check status in case it's changed by he admin while user is logged in
            if !mis_user.nil? and mis_user.isActive
                cookies[:sess_key] = {:value => cookies[:sess_key], :expire => 20.minutes.from_now}
                cacheServer.write "mis_user_#{cookies[:sess_key]}", uname, :expires_in => 20.minutes 
                return mis_user
            else
                self.destroy_session(cookies)
                return nil  
            end 
            
        end 
        
    end
    
    def self.create_session(username, password, cookies, request)
    
        if self.validate_credentials username, password
            
            # generate session key
            sess_key = ActiveSupport::SecureRandom.base64(30)
            
            cookies[:sess_key] = {:value => sess_key, :expire => 20.minutes.from_now}
            
            begin
                cacheServer = CacheServer.get_instance
                cacheServer.write "mis_user_#{sess_key}", username, :expires_in => 20.minutes 
            rescue
				Rails.logger.info "#{Time.now.to_formatted_s(:db)} session creation failed: [#{username}] IP: [#{request.remote_ip}] userAgent: #{request.headers['HTTP_USER_AGENT']}"
                return LOGIN_UNABLE_CREATE_SESSION
            end
            
			Rails.logger.info "#{Time.now.to_formatted_s(:db)} session created: [#{username}] IP: [#{request.remote_ip}] userAgent: #{request.headers['HTTP_USER_AGENT']}"
			return LOGIN_SUCCESS
        end
        
		Rails.logger.info "#{Time.now.to_formatted_s(:db)} login failed: [#{username}] IP: [#{request.remote_ip}] userAgent: #{request.headers['HTTP_USER_AGENT']}"
        return LOGIN_INVALID_USERNAME_PASSWORD
        
    end
    
    def self.destroy_session(cookies)
        
        begin
            cacheServer = CacheServer.get_instance
            cacheServer.delete "mis_user_#{cookies[:sess_key]}"
            cookies.delete :sess_key
        rescue
            return 0 #LOGOUT_UNABLE_DESTROY_SESSION
        end     
        
        return 1 #LOGOUT_SUCCESS 
    
    end
    
    
    def self.get_access(user,access)
    
        if user.isAdmin
            return 'approve'
        else    
            user.AccessLevels.each { |id,acc_level|
                if acc_level.displayname.upcase.eql? access
                    return acc_level.access
                end 
            }   
        end
        
        return false
            
    end
    
    # all methods below are private
    private 
    
    def self.validate_credentials(username, password)
        db = Dbconn.get_instance
        stmt = db.prepare 'SELECT Password, Status FROM staff WHERE username = ? and password = MD5(CONCAT(?, ?)) and status = 1'
        stmt.execute username, password, username

        if stmt.fetch != nil
            return true
        else
            return false
        end
    end
    
    

end
