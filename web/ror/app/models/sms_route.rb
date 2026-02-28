class SmsRoute
    
    @iddCode
    @areaCode
    @type
    @gatewayId
    @priority
    
    #derived
    @gateway
    
    DEFAULT = 1
    PRIMARY_BACKUP = 2
    SECONDARY_BACKUP = 3
    
    @@reasons = {
        '---- Testing ----' => [
            'Testing new Vendor'],
        '---- SMS Issue ----' => [
            'Unsuccessful SMS Delivery',
            'Message Queue'],
        '---- Nagios Alert ----' => [
            'SMS Gateway'],
        '---- Commercial Reason ----' => [
            'Cheaper Routes'],
        '---- Scheduled Maintenance ----' => [
            'Scheduled Maintenance Start',
            'Scheduled Maintenance End'],
        '---- SMS Route Fixed ----' => [
            'SMS Route Fixed']
    }
    
    ['Testing','SMS Issue','Nagios Alert','Commercial Reason','Scheduled Maintenance','SMS Route Fixed']
    
    @@types = {
        1 => 'System SMS',
        2 => 'System WAP',
        3 => 'System Premium SMS',
        4 => 'User SMS'
    }
    
    attr_accessor :iddCode, :areaCode, :type, :gatewayId, :priority, :gateway
    
    def initialize(args={})
        @iddCode = args[:iddCode] || nil
        @areaCode = args[:areaCode] || nil
        @type = args[:type] || nil
        @gatewayId = args[:gatewayId] || nil
        @priority = args[:priority] || nil
        @gateway = args[:gateway] || nil
    end

    def schedule_switch(start_date_time, end_date_time)
        
        sql = []
        sms_routes = SmsRoute.get_route_providers @iddCode, @areaCode, @type
        
        country = Country.find :iddCode => @iddCode
        
        area_code = @areaCode.empty? ? '""' : @areaCode
        
        default_provider = sms_routes[DEFAULT]
        backup_provider = sms_routes[PRIMARY_BACKUP]
        
        # delete previous schedule
        db = Dbconn.get_instance 'master'
        stmt = db.prepare 'DELETE FROM mislog WHERE objectid = ? AND action = ? AND section = ?'
        stmt.execute "#{@iddCode}_#{@areaCode}_#{@type}", "BACKUP_ROUTE_SCHEDULE", "SMS_ROUTE"
        stmt.close
        
        # switch
        
        sql << "DELETE FROM voiceroute WHERE iddcode = #{@iddCode} AND areacode = #{area_code} AND type = #{@type} AND priority IN (#{DEFAULT},#{PRIMARY_BACKUP});"
        sql << "INSERT INTO voiceroute(iddCode, areacode, gatewayid, type, priority) VALUES(#{@iddCode},#{area_code},#{default_provider.gatewayId}, #{@type},#{DEFAULT});"
        sql << "INSERT INTO voiceroute(iddCode, areacode, gatewayid, type, priority) VALUES(#{@iddCode},#{area_code},#{backup_provider.gatewayId}, #{@type},#{PRIMARY_BACKUP});"
        sql << "INSERT INTO mislog(description, section, objectid, action, staffid, datecreated) VALUES ('Scheduled backup for voice route. Switched default route form #{default_provider.gateway} to #{backup_provider.gateway} in country #{country.name}, areaCode: #{@areaCode}','VOICE_ROUTE', '#{@iddCode}_#{@areaCode}_#{@type}','SWITCH BACKUP ROUTES',0,NOW());"
        
        # register to cron
        CronEdit::Crontab.Remove "mis_sms_switch_route_#{@iddCode}_#{@areaCode}_#{@type}"
        CronEdit::Crontab.Add  "mis_sms_switch_route_#{@iddCode}_#{@areaCode}", "#{start_date_time.strftime('%M %H %d %m')} * mysql -u#{DB_SETTINGS[:master][:username]} -p#{DB_SETTINGS[:master][:password]} -h#{DB_SETTINGS[:master][:host]} -e'#{sql.join(' ')}' fusion"
    
        # revert
        
        sql = []
        sql << "DELETE FROM voiceroute WHERE iddcode = #{@iddCode} AND areacode = #{area_code} AND type = #{@type} AND priority IN (#{DEFAULT},#{PRIMARY_BACKUP});"
        sql << "INSERT INTO voiceroute(iddCode, areacode, gatewayid, type, priority) VALUES(#{@iddCode},#{area_code},#{default_provider.gatewayId}, #{@type},#{PRIMARY_BACKUP});"
        sql << "INSERT INTO voiceroute(iddCode, areacode, gatewayid, type, priority) VALUES(#{@iddCode},#{area_code},#{backup_provider.gatewayId}, #{@type},#{DEFAULT});"
        sql << "INSERT INTO mislog(description, section, objectid, action, staffid, datecreated) VALUES ('Reverted voice route. Switched default route form #{backup_provider.gateway} to #{default_provider.gateway} in country #{country.name}, areaCode: #{@areaCode}','VOICE_ROUTE', '#{@iddCode}_#{@areaCode}_#{@type}','REVERT BACKUP ROUTES',0,NOW());"
        
        sql << "DELETE FROM mislog WHERE objectid = #{@iddCode}_#{@areaCode}_#{@type} AND action = 'BACKUP_ROUTE_SCHEDULE' AND section = 'SMS_ROUTE'"

        CronEdit::Crontab.Remove "mis_revert_route_#{@iddCode}_#{@areaCode}_#{@type}"           
        CronEdit::Crontab.Add  "mis_revert_route_#{@iddCode}_#{@areaCode}", "#{end_date_time.strftime('%M %H %d %m')} * root mysql -u#{DB_SETTINGS[:master][:username]} -p#{DB_SETTINGS[:master][:password]} -h#{DB_SETTINGS[:master][:host]} -e'#{sql.join(' ')}' fusion"
        
    end
    
    def self.reasons
        return @@reasons
    end

    def self.get(idd_code, area_code, type)
        routes = self.search :iddCode => idd_code, :areaCode => area_code, :type => type
        return routes.first.to_a.last.first unless routes.empty?
    end 
    
    def self.find(args={})
        return self.search args
    end 
    
    def self.all
        return self.search
    end
    
    def self.set_default_route(idd_code, area_code, provider_id, type)
        return self.set_route idd_code, area_code, type, provider_id, DEFAULT
    end
    
    def self.set_primary_backup_route(idd_code, area_code, provider_id, type)
        return self.set_route idd_code, area_code, type, provider_id, PRIMARY_BACKUP
    end
    
    def self.set_secondary_backup_route(idd_code, area_code, provider_id, type)
        return self.set_route idd_code, area_code, type, provider_id, SECONDARY_BACKUP
    end
    
    def self.get_route_providers(idd_code, area_code, sms_type)
        
        priorities = [DEFAULT, PRIMARY_BACKUP, SECONDARY_BACKUP]
        routes = {}
        
        priorities.each{ |priority|
            rt = self.search :iddCode => idd_code, :areaCode => area_code, :type => sms_type, :priority => priority, :areaCodeExactMatch => true
            routes[priority] = rt.empty? ? nil : rt.first.to_a.last.first
        }
        
        return routes
        
    end
    
    def self.types
        return @@types
    end
    
    def self.display_type(sms_type)
        return @@types[sms_type.to_i] if @@types.has_key? sms_type.to_i
    end
    
    def get_scheduled_switch_route
        schedule = Log.list(:section => 'SMS_ROUTE', :objectid => "#{@iddCode}_#{@areaCode}_#{@type}", :action => 'BACKUP_ROUTE_SCHEDULE')
        return schedule.first[:description] unless schedule.empty?
    end
    
    def self.switch(countries, provider, swap_provider, sms_type)
    
        is_swapped = true
        error = ''
        countries_affected = []
        idd_codes = []
        
        begin
        
            db = Dbconn.get_instance 'master'
            stmt = db.prepare " SELECT country.id, country.name, areacode, country.iddcode, priority
                                FROM smsroute, country
                                WHERE smsroute.iddcode = country.iddcode
                                AND country.id IN (#{countries.join(',')})
                                AND smsroute.gatewayid = ?
                                AND smsroute.type = ?
                                GROUP BY country.name, areacode
                                ORDER BY country.name, areacode"
                                
            stmt.execute provider, sms_type
            
            while row = stmt.fetch do
                
                countries_affected << {
                        :id => row[0],
                        :country => row[1],
                        :areaCode => row[2],
                        :iddCode => row[3],
                        :priority => row[4]
                    }
                idd_codes << row[3]
            end
            
            stmt.free_result
            stmt.close
            
            if !idd_codes.empty?
                stmt = db.prepare " UPDATE smsroute
                                    SET gatewayid = ?
                                    WHERE iddcode IN (#{idd_codes.join(',')})
                                    AND gatewayid = ?
                                    AND type = ?"
                stmt.execute swap_provider, provider, sms_type
                stmt.close
            end 
            
        rescue Exception => e
            is_swapped = false
            error = e.message
        end
        
        if is_swapped
            return true, countries_affected
        else
            return false, error
        end
    
    end
    
    def self.restart_engine
        
        is_restarted = false
        response = ''
        
        AuditHelper::log "Restart SMS Engine: MISUser #{ApplicationController::get_session_user} is requesting restart SMS Engine."
        
        output = ''
        begin
            Net::SSH.start('app02', 'root', :keys => ['/var/www/htdocs/ror/lib/pub_key']) do |ssh|
                AuditHelper::log "Restart SMS Engine: Connected to app02."
                AuditHelper::log "Restart SMS Engine: Restarting SMS engine."
                output = ssh.exec!('/root/scripts/restartse.sh')
                AuditHelper::log "Restart SMS Engine: Success."
            end
            is_restarted = true
        rescue Exception => e
            AuditHelper::log "Restart SMS Engine Error: #{e.message}"
            throw e.message
        end
        
        response = output
        
        return is_restarted, response
            
    end
        
    private
    
    def self.search(args)
        
        sms_routes = {}
        filter = []
        filter_val = []
        area_code_exact_match = args[:areaCodeExactMatch] || false
        area_code = args[:areaCode] || 'NULL'
        
        if args.has_key? :iddCode
            filter << 'AND iddCode = ?'
            filter_val << args[:iddCode]
        end
        
        if area_code_exact_match or (args.has_key? :areaCode and !args[:areaCode].empty?)
            filter << 'AND areaCode = ?'
            filter_val << args[:areaCode]
        end
        
        if args.has_key? :type
            filter << 'AND smsroute.type = ?'
            filter_val << args[:type]
        end
        
        if args.has_key? :priority
            filter << 'AND priority = ?'
            filter_val << args[:priority]
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT iddCode, areacode, smsroute.type, gatewayid, priority, smsgateway.name
                            FROM smsroute, smsgateway
                            WHERE smsroute.gatewayid = smsgateway.id #{filter.join(' ')}
                            GROUP BY iddcode, areacode, smsroute.type
                            ORDER BY iddCode, areaCode, priority"
        stmt.execute *filter_val
        
        tmp_area_code = 0
        
        while row = stmt.fetch do
            
            if tmp_area_code != row[1]
                tmp_area_code = row[1] 
                sms_routes[tmp_area_code] = []
            end
            
            sms_route = SmsRoute.new(
                    :iddCode => row[0],
                    :areaCode => row[1],
                    :type => row[2],
                    :gatewayId => row[3],
                    :priority => row[4],
                    :gateway => row[5]
                )
        
            sms_routes[tmp_area_code] << sms_route
        end     
        
        stmt.free_result
        stmt.close
        
        return sms_routes
        
    end
    
    def self.set_route(idd_code, area_code, type, provider_id, priority)
        
        is_set = false
        error = ''
        
        begin   
        
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            stmt = db.prepare ' DELETE FROM smsroute 
                                WHERE iddcode = ?
                                AND areacode = ?
                                AND type = ?
                                AND priority = ?'
            
            stmt.execute idd_code, area_code, type, priority
            stmt.close
            
            stmt = db.prepare ' INSERT INTO smsroute(iddCode, areacode, gatewayid, priority, type)
                                VALUES(?,?,?,?,?)'
            stmt.execute idd_code, area_code, provider_id, priority, type
            stmt.close
            
            db.commit
            is_set = true
        rescue Exception => e
            db.rollback
            error = e.message
        end 
        
        db.autocommit true  
        
        return is_set, error
        
    end
    
end