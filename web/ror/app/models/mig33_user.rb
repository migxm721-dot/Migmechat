class Mig33User
    
    @id
    @username
    @mobilePhone
    @displayPicture
    @coverPhoto
    @emailAddress
    @password
    @verificationCode
    @dateRegistered
    @lastLoginDate
    @displayName
    @utcOffset
    @msnUsername
    @yahooUsername
    @aimUsername
    @onMailingList
    @chatRoomAdmin
    @coachStatus
    @staffStatus
    @chatRoomBans
    @status
    @mobileVerified
    @firstAuthenticationDate
    @lastAuthenticationDate
    @emailActivated
    @emailAlert
    @emailActivationDate
    @failedLoginAttempts
    @failedActivationAttempts
    @type
    @mobileDevice
    @userAgent
    @affiliateID
    @referredBy
    @referralLevel
    @balance
    @registrationIPAddress
    @registrationDevice
    @notes
    @statusMessage
    @language
    @emailAlertSent
    @allowBuzz
    @merchantCreated
    @bonusProgramId
    @fundedBalance
    
    #references in table `user`
    @countryID
    @currencyCode
    
    #references out of table `user`
    @profileStatus
    
    # emailverified data from useremailaddress
    @emailVerified
    @emailVerifiedDate
    
    ### lazy loaded ###
    
    @country
    @currency
    @score
    
    ## user profile data ##
    @profileData
    
    # references in table `userverified`
    @verified
    @verifiedDescription
    @accountType
    
    @usernameColorType
    
    @@types = {
        1 => 'Member',
        2 => 'Merchant', 
        3 => 'Top Merchant', 
        4 => 'Prepaid Card'
    }

    @@statuses = {
        0 => 'Deactivated',
        1 => 'Active',
        2 => 'Suspended',
        3 => 'Disconnected'
    }
    
    
    INACTIVE = 0
    ACTIVE = 1
    SUSPENDED = 2
    
    INDIVIDUAL_ACCOUNT = 1
    ENTITY_ACCOUNT = 2
    
    attr_accessor :id, :username, :mobilePhone, :displayPicture, :coverPhoto, :emailAddress, :password, :verificationCode, :dateRegistered, :lastLoginDate, :displayName, :utcOffset, :msnUsername, :yahooUsername, :aimUsername, :onMailingList, :chatRoomAdmin, :coachStatus, :staffStatus, :chatRoomBans, :profileStatus, :status, :mobileVerified, :firstAuthenticationDate, :lastAuthenticationDate, :emailActivated, :emailAlert, :emailActivationDate, :failedLoginAttempts, :failedActivationAttempts, :type, :mobileDevice, :userAgent, :affiliateID, :referredBy, :referralLevel, :balance, :registrationIPAddress, :registrationDevice, :notes, :country, :countryID, :currency, :currencyCode, :statusMessage, :language, :emailAlertSent, :allowBuzz, :merchantCreated, :bonusProgramId, :fundedBalance, :verified, :verifiedDescription, :accountType, :emailVerified, :emailVerifiedDate, :usernameColorType, :score
    
    def initialize(args={})
        
        @id = args[:id] || nil
        @username = args[:username] || nil
        @mobilePhone = args[:mobilePhone] || nil
        @displayPicture = args[:displayPicture] || nil
        @coverPhoto = JSON.parse('{"selected":true,"type":2,"url":"http://devlab.projectgoth.com.s3.amazonaws.com/i/94b5/ec0d477a65491135aa95e5dcfee30cab9628/1377505051902_680x"}')
        @coverPhoto = CoverPhoto.get(@id)
        @emailAddress = args[:emailAddress] || nil
        @password = args[:password] || nil
        @verificationCode = args[:verificationCode] || nil
        begin
            @dateRegistered = DateTime.strptime(args[:dateRegistered], "%m/%d/%Y").to_time unless args[:dateRegistered].nil?
        rescue ArgumentError => e
            @dateRegistered = nil
        end
        begin
            @lastLoginDate = DateTime.strptime(args[:lastLoginDate], "%m/%d/%Y").to_time unless args[:lastLoginDate].nil? 
        rescue ArgumentError => e
            @lastLoginDate = nil
        end
        @displayName = args[:displayName] || nil
        @utcOffset = args[:utcOffset] || nil
        @msnUsername = args[:msnUsername] || nil
        @yahooUsername = args[:yahoousername] || nil
        @aimUsername = args[:aimUsername] || nil
        @onMailingList = args[:onMailingList] || nil
        @chatRoomAdmin = args[:chatRoomAdmin] || nil
        @coachStatus = args[:coachStatus] || nil
        @staffStatus = args[:staffStatus] || nil
        @chatRoomBans = args[:chatRoomBans] || nil
        @status = args[:status] || nil
        @mobileVerified = args[:mobileVerified] || nil
        begin
            @firstAuthenticationDate = DateTime.strptime(args[:firstAuthenticationDate], "%m/%d/%Y").to_time unless args[:firstAuthenticationDate].nil?
        rescue
            @firstAuthenticationDate = nil
        end
        begin
            @lastAuthenticationDate = DateTime.strptime(args[:lastAuthenticationDate], "%m/%d/%Y").to_time unless args[:lastAuthenticationDate].nil?
        rescue
            @lastAuthenticationDate = nil
        end
        @emailActivated = args[:emailActivated] || nil
        @emailAlert = args[:emailAlert] || nil
        begin
            @emailActivationDate = DateTime.strptime(args[:emailActivationDate], "%m/%d/%Y").to_time unless args[:emailActivationDate].nil?
        rescue
            @emailActivationDate = nil
        end
        @failedLoginAttempts = args[:emailActivationAttempts] || nil
        @failedActivationAttempts = args[:failedActivationAttempts] || nil
        @type = args[:type] || nil
        @mobileDevice = args[:mobileDevice] || nil
        @userAgent = args[:userAgent] || nil
        @affiliateID = args[:affiliateID] || nil
        @referredBy = args[:referredBy] || nil
        @referralLevel = args[:referralLevel] || nil
        @balance = args[:balance] || nil
        @registrationIPAddress = args[:registrationIPAddress] || nil
        @registrationDevice = args[:registrationDevice] || nil
        @notes = args[:notes] || nil
        @statusMessage = args[:statusMessage] || nil
        @language = args[:language] || nil
        @emailAlertSent = args[:emailAlertSent] || nil
        @allowBuzz = args[:allowBuzz] || nil
        @merchantCreated = args[:merchantCreated] || nil
        @bonusProgramId = args[:bonusProgramId] || nil
        @fundedBalance = args[:fundedBalance] || nil
    
        #references in table `user`
        @countryID = args[:countryID] || nil
        @currencyCode = args[:currencyCode] || nil
        
        #references out of table `user`
        @profileStatus = args[:profileStatus] || nil
        
        # emailverified data nmis-143
        @emailVerified = args[:emailVerified] || nil
        @emailVerifiedDate = DateTime.strptime(args[:emailVerifiedDate], "%Y-%m-%d %H:%M:%S").to_time unless args[:emailVerifiedDate].nil? 
        
        # lazy loaded
        @country = args[:country] || nil
        @currency = args[:currency] || nil
    
        @usernameColorType = args[:usernameColorType] || nil
        @score = args[:score] || 0
    
    end
    
    def self.find(args={})
        return self.search args
    end
    
    def self.get(username)
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  
                              user.Username, 
                                            user.MobilePhone, 
                                            user.DisplayPicture, 
                                            useremailaddress.emailAddress,
                                            user.password, 
                                            user.verificationcode, 
                                            date_format(user.DateRegistered,'%m/%d/%Y'), 
                                            date_format(user.LastLoginDate,'%m/%d/%Y'), 
                                            user.displayname, 
                                            country.name, 
                                            user.utcoffset, 
                                            user.MSNusername, 
                                            user.Yahoousername, 
                                            user.AIMusername, 
                                            user.OnMailingList, 
                                            user.chatRoomAdmin, 
                                            user.chatRoomBans, 
                                            userprofile.Status, 
                                            user.Status, 
                                            user.MobileVerified, 
                                            (select date_format(min(DateCreated),'%m/%d/%Y') from activation where username = ?), 
                                            (select date_format(max(DateCreated),'%m/%d/%Y') from activation where username = ?), 
                                            IF(useremailaddress.verified IS NULL, 0, useremailaddress.verified) EmailActivated, 
                                            user.EmailAlert, 
                                            IF(useremailaddress.verified IS NULL OR useremailaddress.verified = 0, NULL, date_format(useremailaddress.dateVerified,'%m/%d/%Y')) emailVerifiedDate, 
                                            user.FailedLoginAttempts, 
                                            user.FailedActivationAttempts, 
                                            user.Type, 
                                            user.MobileDevice, 
                                            user.userAgent, 
                                            user.AffiliateID, 
                                            user.ReferredBy, 
                                            user.ReferralLevel, 
                                            user.Balance, 
                                            currency.Name, 
                                            user.RegistrationIPAddress, 
                                            user.RegistrationDevice, 
                                            user.Notes, 
                                            user.countryId, 
                                            user.currency, 
                                            userid.id, 
                                            user.statusMessage, 
                                            user.language, 
                                            user.emailAlertSent, 
                                            user.allowBuzz, 
                                            user.merchantCreated, 
                                            user.bonusProgramId, 
                                            user.fundedBalance,
                                            useremailaddress.verified,
                                            IF(useremailaddress.verified IS NULL OR useremailaddress.verified = 0, NULL, date_format(useremailaddress.dateVerified,'%Y-%m-%d %T')) verifiedDate,
                                            IFNULL(username_color_type, 0) username_color_type,
                                            score.score
                                        FROM 
                                          user
                                          LEFT JOIN userprofile ON user.username = userprofile.username 
                                          LEFT JOIN userid ON userid.username = user.username 
                                          LEFT JOIN merchantdetails ON userid.id = merchantdetails.id
                                          LEFT JOIN score ON score.userid = userid.id
                                          LEFT OUTER JOIN useremailaddress on useremailaddress.userid = userid.id, 
                                          country,  
                                          currency
                                        WHERE 
                                          user.countryid = country.id AND 
                                          user.currency = currency.code AND 
                                          user.Username = ?"
        stmt.execute username, username, username
        
        getStatus = db.prepare "SELECT type FROM userlabel where userid = ( SELECT id from userid where username = ? )"
        getStatus.execute username

        status=[]
        while row = getStatus.fetch do
            status.push row[0]
        end

        getStatus.free_result
        getStatus.close

        while row = stmt.fetch do
            
            currency = Currency.new(
                :code => row[39],
                :name => row[34])
            
            country = Country.new(
                :id => row[38],
                :name => row[9])
            
            user = Mig33User.new( 
                :username => row[0],
                :mobilePhone => row[1],
                :displayPicture => row[2],
                :emailAddress => row[3],
                :password => row[4],
                :verificationCode => row[5],
                :dateRegistered => row[6],
                :lastLoginDate => row[7],
                :displayName => row[8],
                :utcOffset => row[10],
                :msnUsername => row[11],
                :yahooUsername => row[12],
                :aimUsername => row[13],
                :onMailingList => row[14],
                :chatRoomAdmin => row[15],
                :coachStatus => status.include?(1) ? 1 : 0, # label type 1 => Coach
                :staffStatus => status.include?(2) ? 1 : 0, # label type 2 => Staff
                :chatRoomBans => row[16],
                :profileStatus => row[17],
                :status => row[18],
                :mobileVerified => row[19],
                :firstAuthenticationDate => row[20],
                :lastAuthenticationDate => row[21],
                :emailActivated => row[22],
                :emailAlert => row[23],
                :emailActivationDate => row[24],
                :failedLoginAttempts => row[25],
                :failedActivationAttempts => row[26],
                :type => row[27],
                :mobileDevice => row[28],
                :userAgent => row[29],
                :affiliateID => row[30],
                :referredBy => row[31],
                :referralLevel => row[32],
                :balance => row[33],
                :registrationIPAddress => row[35],
                :registrationDevice => row[36],
                :notes => row[37],
                :country => country,
                :countryID => row[38],
                :currency => currency,
                :currencyCode => row[39],
                :id => row[40],
                :statusMessage => row[41],
                :language => row[42],
                :emailAlertSent => row[43],
                :allowBuzz => row[44],
                :merchantCreated => row[45],
                :bonusProgramId => row[46],
                :fundedBalance => row[47],
                :emailVerified => row[48],
                :emailVerifiedDate => row[49],
                :usernameColorType => row[50],
                :score => row[51])
                
        end
        
        stmt.free_result
        stmt.close
        
        return user || nil
    end
    
    def self.display_type(type)
        return @@types[type]
    end

    def self.types
        return @@types
    end

    def self.statuses
        return @@statuses
    end
    
    def update_attribute(field, value, opts={})
        
        errors = {}
        isSaved = true
        
        if field.eql? 'profileStatus'
            user_profile = Mig33UserProfile.new(:username => @username)
            isSaved, errors = user_profile.update_attribute('status',value)

        elsif field.downcase.eql? 'coachstatus' or field.downcase.eql? 'staffstatus'
            db = Dbconn.get_instance 'master'

            case field.downcase
                when 'coachstatus'
                    type = 1
                when 'staffstatus'
                    type = 2
            end

            findUserLabel = db.prepare "SELECT id from userlabel WHERE userid = (select id from userid where username = ?) AND type = ?"
            findUserLabel.execute @username, type
            exists = findUserLabel.num_rows > 0 ? true : false
 
            findUserLabel.free_result
            findUserLabel.close

            # record exists, check if status to be deactivated, else create a new record
            if exists
                if value.to_i == 0
                    stmt = db.prepare "DELETE from userlabel where userid = ( select id from userid where username = ? ) AND type = ?"
                    stmt.execute @username, type
                    stmt.close
                end
            else
                if value.to_i == 1
                    stmt = db.prepare "INSERT INTO userlabel (userid, type) values (
                                        (select id from userid where username = ?), ?)"
                    stmt.execute @username, type
                    stmt.close
                end
            end
        else

            begin
            
                db = Dbconn.get_instance 'master'
                db.autocommit false
                db.query 'begin'
                
                if field.eql? 'displayPicture'
                    stmt = db.prepare "UPDATE user SET displayPicture = null WHERE username = ?"
                    stmt.execute @username
                else
                    stmt = db.prepare "UPDATE user SET #{field} = ? WHERE username = ?"
                    stmt.execute value, @username
                end
                stmt.close

                if field.eql? 'type'
                
                    # register as merchant
                    if value.to_i > 1
                    
                        is_registered, error = Merchant.register(@username, opts[:mentor])
                        if !is_registered
                            isSaved = false
                            errors[:registerMerchant] = error
                        end
                    
                    # deregister from merchant network
                    elsif @type > 1 and value.to_i == 1
                        
                        is_deregistered, error = Merchant.deregister(@username)
                        if !is_deregistered
                            isSaved = false
                            errors[:registerMerchant] = error
                        end
                    
                    end
                    
                    # remove merchant tags if demoted from top merchant
                    if @type.eql? 3 and value.to_i < 3
                        MerchantTag.remove_tags(@id)
                        Merchant.remove_from_merchant_location(@username)
                    end
                    
                elsif field.downcase.eql? 'chatroombans' and value.to_i == Chatroom.get_bans_before_suspension - 1
                    
                    # we clear the 24-hour ban
                    cache = CacheServer.get_instance
                    cache.delete("#{CacheServer.key_space('CHATROOM_BAN')}/#{username}")
                
                elsif field.downcase.eql? 'emailaddress'    
                    isSaved, errors = set_email_address(value, 1)

                end
                
                if isSaved
                    
                    db.commit
                    # update user details in ice
                    opts = [@username]
                    is_updated, response = SoapClient.call('updateUserDetailsIce', opts)
                    errors[:up] = response
                    
                    # disconnect user
                    opts = [@username, 'Updating user details.']
                    is_disconnected, response = SoapClient.call('disconnectUserIce', opts)
                    errors[:dc] = response
                    
                else
                    db.rollback
                end
                
                db.autocommit true
                
            rescue Exception => e
                db.rollback
                db.autocommit true
                isSaved = false
                errors[field.to_sym] = "Error updating #{field}: #{e.message}"
            end
            
        end
        
        return isSaved, errors
        
    end
    
    # derived from MISbean.java:removeAllInstancesOfFile
    def remove_display_picture
    
        error = {}
        is_deleted = false
        
        begin
        
            # memcache client
            cache = CacheServer.get_instance
            
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            ps = db.prepare "SELECT id, username FROM scrapbook WHERE fileid = ? AND status != ?"
            ps.execute @displayPicture, 0
            
            psRemoveFile = db.prepare "UPDATE scrapbook SET status = ? WHERE id = ?"
            
            while row = ps.fetch do
                # Clear file id from scrapbooks
                psRemoveFile.execute 0, row[1]
                
                if psRemoveFile.affected_rows
                    # invalidate cache
                    cache.delete "#{CacheServer.key_space('SCRAPBOOK')}/#{row[1]}"
                    cache.delete "#{CacheServer.key_space('SCRAPBOOK')}/#{@username}"
                end
            end
            
            ps.free_result
            ps.close
            psRemoveFile.close
            
            # invalidate cache
            opts = [@username.to_s, nil]
            is_saved, response = SoapClient.call('updateUserDisplayPicture', opts)
            
            db.commit
            is_deleted = true
        rescue Exception => e
            
            db.rollback
            error[:delete] = e.message
        end
        
    end
    
    def get_chatrooms
        
        #opts = [@username.to_s, 1, 1000]
        #is_retrieved, response = SoapClient.call('getUserOwnedChatrooms', opts)
        #return response['chatrooms'] if is_retrieved
    
        return Chatroom.find :creator => @username.to_s
    
    end
    
    def security_question
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT sq.question FROM usersetting us, securityquestion sq WHERE us.value = sq.id AND us.type = 3 AND username = ?"
        stmt.execute @username
        
        question = nil
        
        while row = stmt.fetch do
            question = row[0]   
        end
        
        stmt.free_result
        stmt.close
        
        return question
        
    end
    
    def remove_security_question
        
        is_removed = false
        error = ''
        
        begin
            
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            stmt = db.prepare "DELETE FROM credential WHERE userid = ? AND passwordtype = 17"
            stmt.execute @id
            stmt.close
            
            stmt = db.prepare "DELETE FROM usersetting WHERE username = ? and type = 3"
            stmt.execute @username
            stmt.close
            
            db.commit
            db.autocommit true
            is_removed = true
        
        rescue Exception => e
            error = e.message
            db.autocommit true if !db.nil?
        end
        
        return is_removed, error        
    
    end
    
    def get_groups
        return Group.find :createdBy => @username.to_s
    end
    
    def get_activations
        return Activation.find :username => @username.to_s
    end
    
    def is_merchant?
        return [2,3].include? @type
    end
    
    def is_top_merchant?
        return 3 == @type
    end 
    
    def has_merchant_pin?
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT status FROM merchantpin WHERE userid = ?"
        stmt.execute @id
        
        if stmt.num_rows > 0
            while row = stmt.fetch do
                if row[0].to_i == 1
                    return 'Authenticated'
                else
                    return 'Unauthenticated'
                end 
            end
            stmt.free_result
            stmt.close
        else
            stmt.free_result
            stmt.close
            return false
        end
                
    end
    
    def is_flood_control_suspended?
        cache = CacheServer.get_instance
        return cache.read "#{CacheServer.key_space('FLOOD_CONTROL')}/#{@username}" || false
    end
    
    def get_status
        
        cache = CacheServer.get_instance
        
        if cache.read "#{CacheServer.key_space('LOGIN_BAN')}/#{username}" or cache.read "#{CacheServer.key_space('FLOOD_CONTROL')}/#{@username}"
            return SUSPENDED
        elsif @status.to_i.eql? 1
            return ACTIVE
        else
            return INACTIVE
        end
                    
    end
    
    def self.exists(username)
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT username FROM user WHERE username = ?"
        stmt.execute username
        
        exists = stmt.num_rows > 0 ? true : false
        stmt.free_result
        stmt.close
        
        return exists
        
    end
    
    def self.disconnect(username, comment)
        
        # disconnect user
        user = Mig33User.get(username);
        restClient = RestClient::Resource.new(FUSION_REST + "/user/#{user.id}/disconnect")
        post_data = { 
            :Comment => ApplicationHelper::sanitize_crlf(comment),
            :StaffId => ApplicationController::get_session_user_id 
        }
        begin
            response = JSON::parse(restClient.post post_data.to_json(), :content_type => :json, :accept => :json)
            return !response.has_key?(:error)
        rescue Exception => e
            return false
        end

    end
    
    
    def self.suspend(user, duration, comment, category='')
        
        is_suspended = false
        t_suspend = false
        
        if(!user.is_a? Mig33User)
          user =  Mig33User.get user
        end
        
        if !user.nil?
          
          username = user.username
          
            if duration.to_i > 0
            
                # suspend for a specific time
                cache = CacheServer.get_instance
                if (!cache.read "#{CacheServer.key_space('LOGIN_BAN')}/#{username}")
                  cache.write("#{CacheServer.key_space('LOGIN_BAN')}/#{username}", 'TRUE', :expires_in => duration.to_i.minutes.from_now ) 
                 
                  t_suspend = true if cache.read "#{CacheServer.key_space('LOGIN_BAN')}/#{username}"
                  action = 'SUSPEND'
        end
            
            else
                if (user.status.to_i == 1)
                db = Dbconn.get_instance 'master'
                stmt = db.prepare 'UPDATE user set status = 0 WHERE username = ?'
                stmt.execute username
                
                if stmt.affected_rows
                    # update user details in ice
                    opts = [username]
                    is_updated, response = SoapClient.call('updateUserDetailsIce', opts)
                    
                    t_suspend = true
                end
            
                stmt.free_result
                stmt.close
                
                action = 'DEACTIVATE'
            end
        
            end
            
            # disconnect user
            disconnect username, 'Deactivate and disconnect user'
        
            if t_suspend
            
                # comment
                comment = MisComment.new(:Description => "Category: #{category}", :Comment => comment, :Section => 'MIG33_USER', :ObjectId => user.id, :Action => action, :StaffId => ApplicationController::get_session_user_id)
                comment.create
            
                is_suspended = true
                
            end
        
        end
        
        return is_suspended
    
    end
    
    def self.activate(username, comment)
        
        is_activated = false
        
        user = Mig33User.get username
        
        if !user.nil?
            
            db = Dbconn.get_instance 'master'
            stmt = db.prepare 'UPDATE user set status = 1 WHERE username = ?'
            stmt.execute username
                
            if stmt.affected_rows
                # update user details in ice
                opts = [username]
                is_updated, response = SoapClient.call('updateUserDetailsIce', opts)
            end
        
            stmt.free_result
            stmt.close
        
            # comment
            comment = MisComment.new(:Comment => comment, :Section => 'MIG33_USER', :ObjectId => user.id, :Action => 'ACTIVATE', :StaffId => ApplicationController::get_session_user_id)
            comment.create
                
            # suspend for a specific time
            cache = CacheServer.get_instance
            cache.delete("#{CacheServer.key_space('LOGIN_BAN')}/#{username}") 
          
          #remove perma ban
          threshold = SystemProperty.get_string('PermaBanThreshold', '10,7')
          threshold_days = threshold.split(',')[1].to_i
          for day in 1..threshold_days
            key = "#{CacheServer.key_space('PERMA_BAN')}/#{username}/%s" % (Date.today-day).strftime("%Y%m%d")
            cache.delete(key)
          end
          
            # disconnect user
            opts = [username, ApplicationHelper::sanitize_crlf(comment)]
            is_disconnected, response = SoapClient.call('disconnectUserIce', opts)
                
            is_activated = true if cache.read("#{CacheServer.key_space('LOGIN_BAN')}/#{username}").nil?
        
        end
        
    end 
    
    def self.get_id(username)
        # get from cache
        cache = CacheServer.get_instance
        id = cache.read "#{CacheServer.key_space('USER_ID')}/#{username}"
        
        # get from db if it does not exist
        if id.nil?
            db = Dbconn.get_instance
            stmt = db.prepare "SELECT id FROM userid WHERE username = ?"
            stmt.execute username
            if stmt.num_rows > 0
                id = stmt.fetch.first
            end
            stmt.free_result
            stmt.close
        end
        return id
    end
    
    def transactions(startdate, enddate, paging, sortfield)
      
      db = Dbconn.get_instance
        query = "SELECT 
               SQL_CALC_FOUND_ROWS 
                  accountentry.id, 
                  accountentry.datecreated, 
                  accountentry.username, 
                  accountentry.amount, 
                  accountentry.currency, 
                  accountentry.reference, 
                  accountentry.description, 
                  CASE 
                    WHEN accountentry.type = 0 then 'Awaiting approval' 
                    WHEN accountentry.type = 1 THEN 'Credit Card' 
                    WHEN accountentry.type = 2 THEN 'Prepaid Card' 
                    WHEN accountentry.type = 3 THEN 'SMS Charge' 
                    WHEN accountentry.type = 4 THEN 'Call Charge' 
                    WHEN accountentry.type = 5 THEN 'Subscription' 
                    WHEN accountentry.type = 6 THEN 'Product Purchase' 
                    WHEN accountentry.type = 7 THEN 'Referral Credit' 
                    WHEN accountentry.type = 8 THEN 'Registration Credit' 
                    WHEN accountentry.type = 9 THEN 'Bonus Credit' 
                    WHEN accountentry.type = 10 THEN 'Refund' 
                    WHEN accountentry.type = 11 THEN 'Premium SMS Recharge' 
                    WHEN accountentry.type = 12 THEN 'Premium SMS Fee' 
                    WHEN accountentry.type = 13 THEN 'Credit Card Refund' 
                    WHEN accountentry.type = 14 THEN 'User to User Transfer' 
                    WHEN accountentry.type = 15 THEN 'Telegraphic Transfer' 
                    WHEN accountentry.type = 16 THEN 'Credit Card Chargeback' 
                    WHEN accountentry.type = 17 THEN 'Voucher(s) Created' 
                    WHEN accountentry.type = 18 THEN 'Voucher(s) Cancelled' 
                    WHEN accountentry.type = 19 THEN 'Currency Conversion' 
                    WHEN accountentry.type = 20 THEN 'SMS Alert Charge' 
                    WHEN accountentry.type = 21 THEN 'BANK TRANSFER' 
                    WHEN accountentry.type = 22 THEN 'BANK TRANSFER REVERSAL' 
                    WHEN accountentry.type = 23 THEN 'CHATROOM KICK CHARGE' 
                    WHEN accountentry.type = 24 THEN 'CREDIT EXPIRED' 
                    WHEN accountentry.type > 24 AND accountentry.type < 99 THEN 'Unknown Type' 
                    WHEN accountentry.type = 99 THEN 'Manual' 
                  end as Type 
               FROM 
                  accountentry, 
                  user, 
                  country 
               WHERE 
                  user.username=accountentry.username 
                  AND user.countryid = country.id 
                  AND accountentry.datecreated >= DATE(?) 
                  AND accountentry.dateCreated <= DATE(?) 
                  AND accountentry.username = ? 
               ORDER BY %s ASC 
               LIMIT ?, ?" % sortfield #adding order by to prepared statement does not work  NMIS-163
                           
        stmt = db.prepare query             
        stmt.execute startdate, enddate, @username, paging.startRow, paging.RecordsPerPage
        
        transactions = []
        while row = stmt.fetch do
            transactions << {
                :id => row[0],
                :datecreated => row[1],
                :username => row[2],
                :amount => row[3],
                :currency => row[4],
                :reference => row[5],
                :description => row[6],
                :type => row[7]
            }   
        end
        
        stmtPaging = db.prepare 'SELECT FOUND_ROWS() as totalRecords'
    stmtPaging.execute
    
        paging.TotalRecords = stmtPaging.fetch.first
        
        stmt.free_result
        stmt.close
        stmtPaging.free_result
        stmtPaging.close
        
        return transactions             
        
    end
    
    def update_score(score)
        
        updated = false
        msg = 'Not updated'
        
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "INSERT INTO score(userID, score, lastUpdated) VALUES(?, ?, NOW()) ON DUPLICATE KEY UPDATE score = ?, lastUpdated = NOW()"
            stmt.execute @id, score, score
        
            if stmt.affected_rows
                updated = true
                msg = 'Successfuly updated.'
            end
            
            # now invalidate memcache
            cache = CacheServer.get_instance
            cache.delete("LEVEL/#{@username}")
            cache.delete("RL/#{@username}")
            
        rescue Exception => e
            msg = e.message
        end
        
        return updated, msg
        
    end 
    
    def self.get_level(userID)
        
        level = 1
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT IFNULL(max(r.level), 1) level FROM reputationscoretolevel r, score s WHERE r.score <= s.score AND s.userID = ?"
        stmt.execute userID.to_i
        
        while row = stmt.fetch do
            level = row[0]
        end
        
        stmt.free_result
        stmt.close
        
        return level
        
    end

    def self.get_level_and_score(userID)

        level = 1
        score = 0

        db = Dbconn.get_instance
        stmt = db.prepare "SELECT IFNULL(max(r.level), 1) level, IFNULL(s.score, 0) score FROM reputationscoretolevel r, score s WHERE r.score <= s.score AND s.userID = ?"
        stmt.execute userID.to_i

        while row = stmt.fetch do
            level = row[0]
            score = row[1]
        end

        stmt.free_result
        stmt.close

        return level, score

    end

    def verify(type, description)
        return verify_account(1, type, description)
    end
    
    def disverify(type, description)
        return verify_account(0, type, description)
    end
    
    def set_categories(categories)
        
        errors = {}
        categories_set = false
        
        begin
            db = Dbconn.get_instance 'master'
            
            # we remove first the existing categories
            stmt = db.prepare "DELETE FROM usertousercategory WHERE userid = ?"
            stmt.execute @id
            
            # we add the current categories
            categoriesAppend = categories.map { |category| "(#{@id}, #{category})"}
            
            if !categoriesAppend.empty?
                stmt = db.prepare "INSERT INTO usertousercategory (userid, usercategoryid) VALUES #{categoriesAppend.join(',')}"
                stmt.execute 
            end
            
            db.commit
            
            categories_set = true
            
        rescue Exception => e
            errors[:verify] = e.message
        end
        
        return categories_set, errors

    end
    
    def categories
        
        categories = []
        db = Dbconn.get_instance
        
        stmt = db.prepare "SELECT uc.id, uc.name, uc.parent, uc.type
                           FROM usercategory uc, usertousercategory uuc
                           WHERE uuc.usercategoryid = uc.id 
                           AND uuc.userid = ?"
        stmt.execute @id
        
        while row = stmt.fetch do
            categories << UserCategory.new(
                                :id => row[0].to_i,
                                :name => row[1],
                                :parent => row[2].to_i,
                                :type => row[3].to_i
                            )
        end                
                           
        return categories
        
    end
    
    def send_email_verification
    
        begin
            restClient = RestClient::Resource.new(FUSION_REST + "/settings/#{@id}/email/activation_request")
            response = JSON.parse(restClient.get)
            
            if response.has_key? "error"
                return false, response["error"]["message"]
                
            elsif response.has_key? "data" and response["data"].downcase.eql? "ok"
                return true, "Successfully sent verification email to #{@emailAddress}"

            else
                return false, "Failed to send verification email to #{@emailAddress}"   

            end
        rescue Exception => e
            return false, "Failed to send verification email to #{@emailAddress}: #{e.message}" 
        end
    end
    
    def set_email_address(emailAddress, type)
    
        errors = {}
        is_set = false

            begin
                
                db = Dbconn.get_instance 'master'
                
                stmt = db.prepare "SELECT userid FROM useremailaddress WHERE emailaddress = ? AND type = ? AND userid != ?"
                stmt.execute emailAddress, type, @id
                
                if stmt.num_rows <= 0
                    is_set, errors = remove_email_address(type)
                    
                    stmt = db.prepare "INSERT INTO useremailaddress(userid, emailaddress, type) VALUES (?,?,?)"
                    stmt.execute @id, emailAddress, type
                
                    if stmt.affected_rows
                        return true, errors
                    end
                else
                    errors[:update_email] = "Email #{emailAddress} already used."

                end
            
            rescue Exception => e
                errors[:update_email] = e

            end
        return false, errors

    end
    
    def remove_email_address(type)
        
        errors = {}
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "DELETE FROM useremailaddress WHERE userid = ? AND type = ?"
            stmt.execute @id, type
            
            stmt = db.prepare "UPDATE user SET emailaddress = NULL WHERE username = ?"
            stmt.execute @username
            
            # we remove any validation token
            cache = CacheServer.get_instance
            cache.delete("FGT_PWD/#{@username}")
            return true, errors
        rescue Exception => e
            errors[:remove_email] = e.message
        end
        
        return false, errors
        
    end
    
    
    #----------- lazy loaded functionalities ------------#
    
    def verified?
        if @verified.nil?
            load_account_verification_details
        end
        return @verified.eql? 1
    end
    
    def verified
        if @verified.nil?
            load_account_verification_details
        end
        return @verified
    end
    
    def verifiedDescription
        if @verifiedDescription.nil?
            load_account_verification_details
        end
        return @verifiedDescription
    end
    
    def accountType
        if @accountType.nil?
            load_account_verification_details
        end
        return @accountType
    end
    

    def upgrade_to_level(new_level, levels=nil)

        levels = MigLevel.get_levels if levels.nil?

        new_score = levels[new_level]
        current_level, current_score = self.class.get_level_and_score(@id)

        current_level_score_baseline = levels[current_level]
        diff = current_score - current_level_score_baseline.score

        updated_score = new_score.score + diff
        level_updated, msg  = self.update_score(updated_score)

        return level_updated, msg

    end

  def self.get_score_reward_details(args={})
    
    details = []
    errors = {}
    if args[:type] == 1
      query = "select 
                 c.datecreated, 
                 r.description, 
                 c.scorereward
               from 
                 rewardprogram r, 
                 rewardprogramcompleted c, 
                 userid u 
               where 
                 c.userid = u.id 
                 and r.id = c.rewardprogramid 
                 and u.username = ?
                 and c.datecreated > ?
                 and c.datecreated < ?
                 and c.scorereward > 0"
    else
      query = "select
                 CASE r.category
                  WHEN 1 THEN 'Non-Monetizing'
                  WHEN 2 THEN 'Monetizing'
                 END as category, 
                 sum(c.scorereward) 
               from 
                 rewardprogram r, 
                 rewardprogramcompleted c, 
                 userid u 
               where 
                 c.userid = u.id 
                 and r.id = c.rewardprogramid 
                 and u.username = ?
                 and c.datecreated > ?
                 and c.datecreated < ?
                 and c.scorereward > 0
               group by r.category"
    end
    
    begin
      
      db = Dbconn.get_instance
      stmt = db.prepare query
      stmt.execute args[:username], args[:startdate], args[:enddate]
      
      while row = stmt.fetch  do
        if args[:type] == 1
          details << {:datecreated => row[0], :description => row[1], :scorereward => row[2]}
        else
          details << {:category => row[0], :scorereward => row[1]}
        end
      end
      
      stmt.close
    rescue Exception => e
      errors[:data] = e.message
    end
    
    return details, errors
  end
  
    
  
    private
    
    def self.search(args={})
        
        filter = []
        filter_val = []
        users = []
        app_cond = ''
        paging = args[:paging] || nil
        
        begin
            
            db = Dbconn.get_instance

            if args.has_key? :username
                if args[:username].include? '%'
                    filter << 'u.username LIKE ?'
                else
                    filter << 'u.username = ?'
                end     
                filter_val << args[:username]
            end
            
            if args.has_key? :mobilePhone
                filter << 'mobilePhone = ?'
                filter_val << args[:mobilePhone]
            end     
            
            if filter.length
                app_cond = "AND"
            end
            
            if !paging.nil? and paging.class.to_s.eql? 'Paginate'
                
                filter_val << paging.startRow
                filter_val << paging.RecordsPerPage
                
                stmt = db.prepare "SELECT SQL_CALC_FOUND_ROWS u.username, mobilePhone, displayPicture, emailAddress, password, verificationCode, date_format(dateRegistered,'%m/%d/%Y'), displayName, countryID, status, mobileVerified, type, balance, currency, (select date_format(max(DateCreated),'%m/%d/%Y') from activation where username = u.username), ui.id FROM user u, userid ui WHERE u.username = ui.username #{app_cond} #{filter.join ' AND '} LIMIT ?,?"
            else
                stmt = db.prepare "SELECT u.username, mobilePhone, displayPicture, emailAddress, password, verificationCode, date_format(dateRegistered,'%m/%d/%Y'), displayName, countryID, status, mobileVerified, type, balance, currency, (select date_format(max(DateCreated),'%m/%d/%Y') from activation where username = u.username), ui.id FROM user u, userid ui WHERE u.username = ui.username #{app_cond} #{filter.join ' AND '}"
            end
            
            stmt.execute *filter_val
            
            if !paging.nil? and paging.class.to_s.eql? 'Paginate'
                stmtPaging = db.prepare 'SELECT FOUND_ROWS() as totalRecords'
                stmtPaging.execute
                paging.TotalRecords = stmtPaging.fetch.first
                stmtPaging.free_result
                stmtPaging.close
            end
            
            while row = stmt.fetch do
                user = Mig33User.new( :username => row[0],
                                      :mobilePhone => row[1],
                                      :displayPicture => row[2],
                                      :emailAddress =>row[3],
                                      :password => row[4],
                                      :verificationCode => row[5],
                                      :dateRegistered => row[6],
                                      :displayName => row[7],
                                      :countryID => row[8],
                                      :status => row[9],
                                      :mobileVerified => row[10],
                                      :type => row[11],
                                      :balance => row[12],
                                      :currencyCode => row[13],
                                      :lastAuthenticationDate => row[14],
                                      :id => row[15] )
                users << user
            end
            
            stmt.free_result
            stmt.close
            
        rescue Exception => e
            throw e.message
        end
        
        return users, paging
        
    end

    def load_account_verification_details
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT verified, type, description 
                           FROM userverified
                           WHERE userid = ?"
        stmt.execute @id
        
        if stmt.num_rows > 0
            verifiedData = stmt.fetch
            @verified = verifiedData[0]
            @accountType = verifiedData[1]
            @verifiedDescription = verifiedData[2]
        else
            @verified = false
            @accountType = INDIVIDUAL_ACCOUNT
            @verifiedDescription = ''
        end
        
        stmt.free_result
        stmt.close
        
    end
    
    def verify_account(verified, type, description)
        
        is_verified = false
        errors = {}
        
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "INSERT INTO userverified(userid, verified, type, description) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE verified = ?, type = ?, description = ?"
            stmt.execute @id, verified, type, description, verified, type, description
        
            if stmt.affected_rows
                is_verified = true
            end
            
            db.commit
            stmt.close
            
        rescue Exception => e
            errors[:verify] = e.message
        end
        
        # clear cache
        begin
            restClient = RestClient::Resource.new(MIGBO_DATA_SVC + "/user/#{@id}/clear_cache")
            restClient.get
        rescue Exception => e
        end
        
        return is_verified, errors
            
    end
    
    def self.get_migbo_profile(username)
    
    profile_data = nil
    
    begin
      restClient = RestClient::Resource.new(MIGBO_DATA_SVC + "/user/#{username}?useUsername=1")
      profile_data = ActiveSupport::JSON.decode(restClient.get)
    rescue Exception => e
    end
    
    return (!profile_data.nil? and profile_data.key?("data")) ? profile_data["data"] : nil # datasvc returs key "error" if theres an error. 
    
    end
    
    def self.get_badges (username)
      
      badge_data = nil
      error = nil
      badges = nil
      
      userid = self.get_id username
      if (userid.nil? or userid.to_i <= 0)
        error = "Cannot find user #{username}"
      else
      begin
        restClient = RestClient::Resource.new(MIGBO_DATA_SVC + "/user/#{userid}/badges")
        badge_data = ActiveSupport::JSON.decode(restClient.get)
      rescue Exception => e
      end
      
      if(badge_data.nil?) 
        error = "Unable to retrieve badges. Please try again."
      elsif(badge_data.key?("error"))
        error = "Error while getting badges." + badge_data["error"]["message"]
      else
      
        badge_array = []
        badge_data["data"]["badges"].each { |badge|
          badge["url"] = BADGES_RESOURCE_PATH + "/48x48/"+ badge["imageName"]
          badge_array.push(badge)
        }
        badges = { "total" => badge_data["data"]["total"], "badges" => badge_array}
       
      end
    end
    return badges, error
    
    end
    
    def self.authenticate(username)
      
      errors = {}
      authenticated = false
      query = "UPDATE
                 user
              SET
                 MobileVerified = 1
              WHERE
                 MobilePhone IS NOT NULL
                 AND username = ?"
      begin
        db = Dbconn.get_instance 'master'
      stmt = db.prepare query
      stmt.execute username
  
      if stmt.affected_rows
        authenticated = true
      end

      stmt.close
      rescue Exception => e
      errors[:db] = e.message
      end
      
      if authenticated
      # update user ice
      opts = [username]
      is_updated, response = SoapClient.call('updateUserDetailsIce', opts)
      errors[:up] = response
      
      # disconnect user
      opts = [username, 'Updating user details.']
      is_disconnected, response = SoapClient.call('disconnectUserIce', opts)
      errors[:dc] = response
    end
      
      return authenticated, errors
      
    end #def
    
    public 
     
    def get_balance(balanceDate)
    
        delta = 0
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT SUM(Amount) as Amount FROM accountentry WHERE Username = ? AND DateCreated > ?"
        stmt.execute @username, balanceDate
       
        
        while row = stmt.fetch do
          delta = row[0]
          Rails.logger.info delta
        end
        
        stmt.free_result
        stmt.close
        
        if delta.nil?
          return @balance
        else
          return @balance + (delta * -1)
        end
    
    end

    def self.reverse_user_credit_transfer(misusername, transaction_id)
        begin
            restClient  = RestClient::Resource.new(FUSION_REST + "/account/reverse/transfer_credit")
            data_holder = {
                :misUserName => misusername,
                :accountEntryID => transaction_id
            }
            data = { :data => data_holder }.to_json
            response = JSON.parse(restClient.post data, :content_type => :json, :accept => :json)
            if(response['error'].nil?)
                return true, 'Succesfully reversed transfer credit'
            else
                return false, response['error']['message']
            end

        rescue Exception => e

            return false, e.message;
        end    
    end

    def self.censor_email_address(emailAddress)

        emailName, emailDomain = emailAddress.split("@")
        emailName[0..4] = '*' * [emailName.length, 5].min

        return emailName+'@'+emailDomain
        
    end

    
end

