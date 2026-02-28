class Merchant < Mig33User
    
    @mentor
    
    attr_accessor :mentor
    
    @@username_color_types = {
        0 => 'DEFAULT',
        1 => 'RED',
    2 => 'PINK'
    }
    
    def initialize(args={})
        super args
        @mentor = args[:mentor] || nil
    end
    
    def self.username_color_types
      return @@username_color_types
    end
    
    def self.display_username_color(type)
        return @@username_color_types[type] if @@username_color_types.has_key? type
    end
    
    def get_account_transactions(startdate, enddate)
    
        transactions = { :payments => [], :transfers => []}
        trans_join = [AccountEntry::ACCOUNT_TRANSACTION_TYPE_TRANSFERS,AccountEntry::ACCOUNT_TRANSACTION_TYPE_VOUCHERS,AccountEntry::ACCOUNT_TRANSACTION_TYPE_CREDITS].flatten!
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  dateCreated, 
                                    description, 
                                    amount, 
                                    currency,
                                    type AS transactionType,
                                    CASE
                                        WHEN type IN (#{AccountEntry::ACCOUNT_TRANSACTION_TYPE_TRANSFERS.map{ '?' }.join(',')}) THEN 'Transfer'
                                        WHEN type IN (#{AccountEntry::ACCOUNT_TRANSACTION_TYPE_VOUCHERS.map{ '?' }.join(',')}) THEN 'Vouchers'
                                        WHEN type IN (#{AccountEntry::ACCOUNT_TRANSACTION_TYPE_CREDITS.map{ '?' }.join(',')}) THEN 'Credits'
                                    END AS `type`, 
                                    exchangeRate,
                                    if(type = #{AccountEntry::USER_TO_USER_TRANSFER}, substring_index(description, ' ', -1), null) as destusername
                            FROM accountentry
                            WHERE username = ? 
                            AND amount != 0 
                            AND type IN (#{trans_join.map{ '?' }.join(',')}) 
                            AND datecreated >= ? 
                            AND datecreated < ? 
                            
                            ORDER BY dateCreated DESC"
                            
        stmt.execute *[trans_join, @username, trans_join, startdate, enddate].flatten!
        
        while row = stmt.fetch do
            
            if row[5].eql? 'Credits'
                transactions[:payments] << {
                    :dateCreated => row[0],
                    :description => row[1],
                    :amount => row[2],
                    :currency => row[3],
                    :type => row[5],
                    :exchangeRate => row[6],
                    :destinationUsername => row[7]
                }
            else
                transactions[:transfers] << {
                    :dateCreated => row[0],
                    :description => row[1],
                    :amount => row[2],
                    :currency => row[3],
                    :type => row[5],
                    :exchangeRate => row[6],
                    :destinationUsername => row[7]
                }
            end
        
        end
        
        stmt.free_result
        stmt.close
        
        return transactions
        
    end
    
    def get_account_transactions_summary(startdate, enddate)
        
        summary = { :numberOfSales => 0, :totalSale => 0 }
        
        trans_join = [AccountEntry::ACCOUNT_TRANSACTION_TYPE_TRANSFERS,AccountEntry::ACCOUNT_TRANSACTION_TYPE_VOUCHERS,AccountEntry::ACCOUNT_TRANSACTION_TYPE_CREDITS].flatten!
        
        db = Dbconn.get_instance
        stmt= db.prepare "  SELECT  COUNT(*) AS numberOfSales, 
                                    -sum(   CASE WHEN user.currency = accountentry.currency THEN amount 
                                            ELSE amount/exchangerate END) AS totalSales
                            FROM accountentry, user 
                            WHERE accountentry.username = ?
                            AND user.username = accountentry.username
                            AND accountentry.type IN (#{trans_join.map{ '?' }.join(',')})
                            AND amount < 0 
                            AND accountentry.datecreated >= ? 
                            AND accountentry.datecreated < ? "
        stmt.execute *[@username, trans_join, startdate, enddate].flatten!
        
        while row = stmt.fetch do
            summary = {
                :numberOfSales => row[0],
                :totalSales => row[1]
            }
        end 
        
        stmt.free_result
        stmt.close              
        
        return summary    
            
    end
    
    def self.update_username_color_type (user, type)
        
        begin
          if type.to_i != user.usernameColorType 
            restClient = RestClient::Resource.new(FUSION_REST + "/merchant/#{user.id}/details/username_color")
            jsonReject = JSON.parse(restClient.post ({:data => {:color => type}}.to_json, {:content_type => :json}))
            if jsonReject["error"]
                return false, jsonReject["error"]["message"]
            end
            end
            
            opts = [user.username]
            is_updated, response = SoapClient.call('updateUserDetailsIce', opts)
            if !is_updated
                throw response
            end 
            
            opts = [user.username, 'Updating user details.']
            is_disconnected, response = SoapClient.call('disconnectUserIce', opts)
            if !is_disconnected
                throw response
            end
        
        rescue Exception => e
            return false, e.message
        end
        return true, ""
    end
    
    def self.register(username, mentor)
        
        is_registered = false
        error = nil
        
        db = Dbconn.get_instance 'master'
        stmt = db.prepare "SELECT userid.ID, user.type FROM userid, user WHERE userid.username = user.username AND user.username = ?"
        stmt.execute username
        
        details = {}
        
        while row = stmt.fetch do
            details = {
                :id => row[0],
                :type => row[1]
            }
        end
        
        stmt.free_result
        stmt.close
        
        if details.empty?
            throw "User must be a merchant."
        else
            
            begin 
                stmt = db.prepare "INSERT INTO merchantdetails(id, logincount, mentor) VALUES (?,0,(SELECT username FROM user WHERE username = ? AND type IN (2,3))) ON DUPLICATE KEY UPDATE mentor = ?"
                stmt.execute details[:id], mentor, mentor
                stmt.close
                is_registered = true
            rescue Exception => e
                error = e.message
            end 
        
        end
        
        return is_registered, error
            
    end
    
    def self.deregister(username)
        
        is_deregistered = false
        error = nil
        
        begin 
            db = Dbconn.get_instance 'master'
            
            # remove from merchant details
            stmt = db.prepare "DELETE merchantdetails FROM merchantdetails, userid WHERE userid.id = merchantdetails.id AND userid.username = ?"
            stmt.execute username
            stmt.close
            
            is_deregistered = true
        rescue Exception => e
            db.rollback
            error = e.message
        end 
        
        return is_deregistered, error
        
    end
    
    def self.get_mentor(username)
        
        mentor = nil
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT mentor FROM userid, merchantdetails WHERE userid.id = merchantdetails.id AND userid.username = ?"
        stmt.execute username
        
        while row = stmt.fetch do
            mentor = row[0]
        end
        
        stmt.free_result
        stmt.close
        
        return mentor
        
    end
    
    def self.exists(username)
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT username FROM user WHERE username = ? AND type IN (2,3)"
        stmt.execute username
        
        exists = stmt.num_rows > 0 ? true : false
        stmt.free_result
        stmt.close
        
        return exists
        
    end
    
    def self.is_top_merchant?(username)
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT username FROM user WHERE username = ? AND type = 3 AND status = 1"
        stmt.execute username
        
        exists = stmt.num_rows > 0 ? true : false
        stmt.free_result
        stmt.close
        
        return exists
        
    end
    
    def self.get_merchant_tag_by_username(username)
        
        cutoff = Time.now - (SystemProperty.get_float('MerchantTagValidPeriod', 43200) * 60) # MerchantTagValidPeriod is in minutes

        db = Dbconn.get_instance
        stmt = db.prepare " SELECT t.id, t.merchantuserid, t.userid, DATE_FORMAT(t.lastsalesdate,'%M %d, %Y'), DATE_FORMAT(t.datecreated,'%M %d, %Y'), t.status, uid_tagger.username 
                            FROM merchanttag t, userid uid_user, userid uid_tagger 
                            WHERE t.userid = uid_user.id
                            AND t.merchantuserid = uid_tagger.id 
                            AND uid_user.username = ? 
                            AND t.lastsalesdate > ? 
                            AND t.status = 1"
        stmt.execute username, cutoff.strftime('%Y-%m-%d %H:%M:%S')
        
        while row = stmt.fetch do
            merchant_tag = MerchantTag.new(
                :id => row[0],
                :merchantUserId => row[1],
                :userId => row[2],
                :lastSaleDate => row[3],
                :dateCreated => row[4],
                :status => row[5],
                :username => row[6]
            )
        end
        
        stmt.free_result
        stmt.close
        
        return merchant_tag
                            
    end
    
    # user and merchant need to be incances of Mig33User
    def self.reset_tag(user, merchant=nil)
        
        is_reset = false
        error = ''
        
        begin
        
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            cache = CacheServer.get_instance
            
            # disable active merchant tag
            
            stmt = db.prepare " UPDATE merchanttag 
                                SET status = 0 
                                WHERE userid = ?"
            stmt.execute user.id
            stmt.close
                
            # remove entry
            cache.delete "#{CacheServer.key_space('MERCHANT_TAG')}/#{user.username}"
            
            # tag to merchant
            if merchant
                stmt = db.prepare "INSERT INTO merchanttag (userid, merchantuserid, datecreated, lastsalesdate, status) VALUES (?,?,NOW(),NOW(),1)"
                stmt.execute user.id, merchant.id
            end
            
            db.commit
            db.autocommit true
                        
            is_reset = true
            
        rescue Exception => e
            error = e.message
        end
        
        return is_reset, error
        
    end
    
    def self.remove_from_merchant_location(username)
        
        is_removed = false
        error = nil
        
        begin 
            db = Dbconn.get_instance 'master'
            
            # remove from merchantlocation
            stmt = db.prepare "DELETE FROM merchantlocation WHERE username = ?"
            stmt.execute username
            stmt.close
            
            # remove 
            is_removed = true
        rescue Exception => e
            error = e.message
        end 
        
        return is_removed, error
        
    end
    
    def self.get(username)
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  user.Username, 
                                    user.MobilePhone, 
                                    user.DisplayPicture, 
                                    user.emailaddress, 
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
                                    user.EmailActivated, 
                                    user.EmailAlert, 
                                    date_format(user.EmailActivationDate,'%m/%d/%Y'), 
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
                                    mentor,
                                    IFNULL(username_color_type, 0) username_color_type 
                            FROM User 
                                 LEFT JOIN userprofile ON user.username = userprofile.username 
                                 LEFT JOIN userid ON userid.username = user.username 
                                 LEFT JOIN merchantdetails ON merchantdetails.id = userid.id, 
                                 country, 
                                 currency 
                            WHERE user.countryid = country.id 
                            AND user.currency = currency.code 
                            AND user.Username = ?"
        stmt.execute username, username, username
        
        while row = stmt.fetch do
            
            currency = Currency.new(
                :code => row[39],
                :name => row[34])
            
            country = Country.new(
                :id => row[38],
                :name => row[9])
                        
            user = Merchant.new( 
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
                :mentor => row[48],
                :usernameColorType => row[49])
                
        end
        
        stmt.free_result
        stmt.close
        
        return user || nil
    end
    
    # reports
    
    def self.rp_get_registered_merchants_by_date(startDate, endDate)
    
        merchants = []
        startDate = DateTime.strptime(startDate, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        endDate = DateTime.strptime(endDate, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT   date_format(affiliate.dateregistered,'%m/%d/%Y'), 
                                    affiliate.Username, 
                                    user.MobilePhone, 
                                    affiliate.EmailAddress, 
                                    FirstName, 
                                    LastName, 
                                    if(mobileverified = 1, 'Yes', 'No') Authenticated, 
                                    (   SELECT name 
                                        FROM country 
                                        WHERE id=affiliate.countryiddetected) RegistrationIPCountry,  
                                    c2.name UserCountry,
                                    affiliate.RegistrationIpAddress, 
                                    AdditionalInfo,
                                    (   SELECT mentor 
                                        FROM userID, merchantdetails 
                                        WHERE userID.id = merchantdetails.id
                                        AND userid.username = affiliate.username ) Mentor,
                                    user.type,
                                    CASE user.type
                                        WHEN 1 THEN 'Regular User'
                                        WHEN 2 THEN 'Merchant'
                                        WHEN 3 THEN 'Top Merchant'
                                    END rpType      
                            FROM affiliate, country c2, user 
                            WHERE user.countryid = c2.id
                            AND affiliate.username = user.username
                            AND affiliate.dateregistered >= ?
                            AND affiliate.dateregistered < adddate(?, 1)"
                            
        stmt.execute startDate, endDate
        
        while row = stmt.fetch do
            merchants << {
                :dateRegistered => DateTime.strptime(row[0], "%m/%d/%Y").to_time.strftime('%b %d, %Y'),
                :username => row[1],
                :mobilePhone => row[2],
                :emailAddress => row[3],
                :firstName => row[4],
                :lastName => row[5],
                :authenticated => row[6],
                :registrationIpCountry => row[7],
                :country => row[8],
                :registrationIpAddress => row[9],
                :additionalInfo => row[10],
                :mentor => row[11],
                :type => row[12],
                :rpType => row[13]
            }
        end         
        
        stmt.free_result
        stmt.close      
                    
        return merchants
        
    end 
    
    # reports
    
    def self.rp_get_authenticated_merchants_by_date(startDate, endDate)
    
        merchants = []
        startDate = DateTime.strptime(startDate, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        endDate = DateTime.strptime(endDate, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT   (   SELECT date_format(MAX(datecreated), '%m/%d/%Y')
                                        FROM activation
                                        WHERE username = affiliate.username) dateAuthenticated,
                                    affiliate.Username, 
                                    user.MobilePhone, 
                                    affiliate.EmailAddress, 
                                    FirstName, 
                                    LastName, 
                                    (   SELECT name 
                                        FROM country 
                                        WHERE id=affiliate.countryiddetected) RegistrationIPCountry,  
                                    c2.name UserCountry,
                                    (   SELECT ipaddress
                                        FROM activation
                                        WHERE username = affiliate.username
                                        GROUP BY username
                                        ORDER BY datecreated DESC) ipaddress, 
                                    AdditionalInfo,
                                    (   SELECT mentor 
                                        FROM userID, merchantdetails 
                                        WHERE userID.id = merchantdetails.id
                                        AND userid.username = affiliate.username ) Mentor,
                                    user.type,
                                    CASE user.type
                                        WHEN 1 THEN 'Regular User'
                                        WHEN 2 THEN 'Merchant'
                                        WHEN 3 THEN 'Top Merchant'
                                    END rpType      
                            FROM affiliate, country c2, user
                            WHERE user.countryid = c2.id
                            AND affiliate.username = user.username
                            AND affiliate.dateregistered >= ?
                            AND affiliate.dateregistered < adddate(?, 1)
                            AND mobileverified = 1"
                            
        stmt.execute startDate, endDate
        
        while row = stmt.fetch do
            begin
                dateAuthenticated = DateTime.strptime(row[0], "%m/%d/%Y").to_time.strftime('%b %d, %Y')
            rescue 
                dateAuthenticated = nil
            end 
            merchants << {
                :dateAuthenticated => dateAuthenticated,
                :username => row[1],
                :mobilePhone => row[2],
                :emailAddress => row[3],
                :firstName => row[4],
                :lastName => row[5],
                :registrationIpCountry => row[6],
                :country => row[7],
                :authenticationIpAddress => row[8],
                :additionalInfo => row[9],
                :mentor => row[10],
                :type => row[11],
                :rpType => row[12]
            }
        end 
        
        stmt.free_result
        stmt.close              
                    
        return merchants
        
    end 
    
    def self.rp_get_top_merchants
        
        merchants = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  user.Username, 
                                    DATE_FORMAT(user.dateregistered, '%m/%d/%Y') DateRegistered, 
                                    user.MobilePhone, 
                                    IF(mobileverified = 1, 'Yes', 'No') Authenticated, 
                                    c2.name UserCountry
                            FROM country c2, user 
                            WHERE user.type = 3
                            AND c2.id = user.countryid 
                            ORDER BY username ASC"
        
        stmt.execute 
        
        while row = stmt.fetch do
            begin
                date_registered = DateTime.strptime(row[1], "%m/%d/%Y").to_time.strftime('%b %d, %Y')
            rescue 
                date_registered = nil
            end 
            
            merchants << {
                :dateRegistered => date_registered,
                :username => row[0],
                :mobilePhone => row[2],
                :authenticated => row[3],
                :country => row[4]}
        end 
        
        stmt.free_result
        stmt.close              
                    
        return merchants    
        
    end
    
    def self.rp_get_merchants_with_advert
        
        merchants = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  user.Username, 
                                    DATE_FORMAT(user.dateregistered, '%m/%d/%Y') DateRegistered, 
                                    user.MobilePhone, 
                                    IF(mobileverified = 1, 'Yes', 'No') Authenticated, 
                                    c2.name UserCountry,
                                    CASE user.type
                                        WHEN 2 THEN 'Merchant'
                                        WHEN 3 THEN 'Top Merchant'
                                    END rpType,                                     
                                    alertmessage.id,
                                    alertmessage.content,
                                    c2.name
                            FROM country c2, user, alertmessage, userid
                            WHERE userid.username = user.username
                            AND userid.id = alertmessage.userid
                            AND c2.id = alertmessage.countryid 
                            AND alertmessage.status = 1
                            ORDER BY username ASC"
        stmt.execute 
        
        while row = stmt.fetch do
            begin
                date_registered = DateTime.strptime(row[1], "%m/%d/%Y").to_time.strftime('%b %d, %Y')
            rescue 
                date_registered = nil
            end 
            
            merchants << {
                :dateRegistered => date_registered,
                :username => row[0],
                :mobilePhone => row[2],
                :authenticated => row[3],
                :country => row[4],
                :type => row[5],
                :alertMessageId =>row[6],
                :alertMessage => row[7],
                :location => row[8] }
        end 
        
        stmt.free_result
        stmt.close              
                    
        return merchants    
        
    end
    
    def self.rp_get_merchants_with_locator
        
        merchants = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  user.Username, 
                                    DATE_FORMAT(user.dateregistered, '%m/%d/%Y') DateRegistered, 
                                    user.MobilePhone, 
                                    IF(mobileverified = 1, 'Yes', 'No') Authenticated, 
                                    CASE user.type
                                        WHEN 2 THEN 'Merchant'
                                        WHEN 3 THEN 'Top Merchant'
                                    END rpType,                                     
                                    merchantlocation.id,
                                    merchantlocation.notes,
                                    location.name
                            FROM user, merchantlocation, location
                            WHERE user.username = merchantlocation.username
                            AND location.id = merchantlocation.locationid
                            AND merchantlocation.status = 1
                            ORDER BY username ASC"
        stmt.execute 
        
        while row = stmt.fetch do
            begin
                date_registered = DateTime.strptime(row[1], "%m/%d/%Y").to_time.strftime('%b %d, %Y')
            rescue 
                date_registered = nil
            end 
            
            merchants << {
                :dateRegistered => date_registered,
                :username => row[0],
                :mobilePhone => row[2],
                :authenticated => row[3],
                :type => row[4],
                :merchantLocationId =>row[5],
                :notes => row[6],
                :location => row[7] }
        end
        
        stmt.free_result
        stmt.close                  
                    
        return merchants    
        
    end
    
    def self.rp_get_sub_merchants(mentor)
        
        merchants = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  SQL_CALC_FOUND_ROWS user.username,
                                DATE_FORMAT(user.dateregistered, '%m/%d/%Y') DateRegistered,
                                userid.id,
                                user.mobilephone,
                                country.name,
                                IF(mobileverified = 1, 'Yes', 'No') Authenticated    
                            FROM user, merchantdetails, userid, country
                            WHERE user.username = userid.username
                            AND country.id = user.countryid
                            AND merchantdetails.id = userid.id
                            AND merchantdetails.mentor = ?
                            AND country.id = user.countryid "
        
        stmt.execute mentor
        
        transaction_stmt = db.prepare " SELECT datecreated, ABS(SUM(amount)), ABS(amount), currency
                                        FROM 
                                            (   SELECT a1.datecreated, a1.amount, a1.username, a1.currency 
                                                FROM accountentry a1, accountentry a2
                                                WHERE a1.type = ?
                                                AND a1.reference = CAST(a2.id AS CHAR)
                                                AND a2.username = ?
                                                AND a2.amount < 0
                                                AND a1.username = ?
                                                AND a2.type = ?) transactions
                                        GROUP BY username
                                        ORDER BY datecreated DESC"
        
        
        while row = stmt.fetch do
            
            begin
                date_registered = DateTime.strptime(row[1], "%m/%d/%Y").to_time.strftime('%b %d, %Y')
            rescue 
                date_registered = nil
            end 
            
            merchant = {
                :dateRegistered => date_registered,
                :username => row[0],
                :mobilePhone => row[3],
                :authenticated => row[5],
                :country => row[4]}
            
            transaction_stmt.execute AccountEntry::USER_TO_USER_TRANSFER, mentor, row[0], AccountEntry::USER_TO_USER_TRANSFER
            
            while t_row = transaction_stmt.fetch do
                merchant[:dateOfLastTransfer] = t_row[0]
                merchant[:totalTransfer] = t_row[1]
                merchant[:lastAmountTransfer] = t_row[2]
                merchant[:currency] = t_row[3]
            end
            
            merchants << merchant
                
        end     
        
        transaction_stmt.free_result
        transaction_stmt.close
        stmt.free_result
        stmt.close          
                    
        return merchants
        
    end
    
end