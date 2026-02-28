class VoiceRoute
    
    @iddCode
    @areaCode
    @gatewayId
    @providerId
    @priority
    
    # derived
    @gateways
    @country
    @provider
    
    DEFAULT = 1
    PRIMARY_BACKUP = 2
    SECONDARY_BACKUP = 3
    
    attr_accessor :iddCode, :areaCode, :gatewayId, :providerId, :priority, :gateways, :country, :provider
    
    @@reasons = {
        '---- Call Issue ----' => [
            'Unable to connect',
            'Echo',
            'Lagging',
            'Distorted',
            'Low Volume',
            'Drop Call'],
        '---- Test Call ----' => [
            'Unable to connect',
            'Echo',
            'Lagging',
            'Distorted',
            'Low Volume',
            'Drop Call',
            'Testing New Vendor'],
        '---- Nagios Alert ----' => [
            'Abused Numbers',
            'Global stuck call count',
            'Global successful call count',
            'Low Country ASR', 
            'Low Trunk ASR',
            'Voice mgmt app',
            'SMS Gateway',
            'Host Down'],
        '---- Commercial Reason ----' => [
            'Cheaper Routes'],
        '---- Scheduled Maintenance ----' => [
            'Scheduled Maintenance Start',
            'Scheduled Maintenance End'],
        '---- Class 5 Errors ----' => [
            'Error 500: Server Internal Error',
            'Error 501: Not implemented',
            'Error 502: Bad Gateway',
            'Error 503: Service Unavailable',
            'Error 504: Server Time-out',
            'Error 505: Version not supported',
            'Error 513: Message too large',
            'Error 580: Precondition Failture'],
        '---- Voice Route Fixed ----' => [
            'Voice Route Fixed']
    }
    
    def initialize(args={})
        @iddCode = args[:iddCode] || nil
        @areaCode = args[:areaCode] || nil
        @gatewayId = args[:gatewayId] || nil
        @providerId = args[:providerId] || nil
        @priority = args[:priority] || nil
        
        # derived
        @gateways = args[:gateways] || nil
        @country = args[:country] || nil
        @provider = args[:provider] || nil
        
    end
    
    def self.set_default_route(idd_code, area_code, provider_id)
        return set_route idd_code, area_code, provider_id, DEFAULT
    end
    
    def self.set_primary_backup_route(idd_code, area_code, provider_id)
        return set_route idd_code, area_code, provider_id, PRIMARY_BACKUP
    end
    
    def self.set_secondary_backup_route(idd_code, area_code, provider_id)
        return set_route idd_code, area_code, provider_id, SECONDARY_BACKUP
    end
    
    def get_gateways
    
        gateways = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT voicegateway.id, voicegateway.name, voiceprovider.id, voiceprovider.name, voiceroute.priority 
                                    FROM voicegateway, voiceroute, voiceprovider
                                    WHERE voiceroute.iddcode = ?
                                    AND voiceroute.areacode = ?
                                    AND voicegateway.id = voiceroute.gatewayid
                                    AND voiceprovider.id = voiceroute.providerid
                                    ORDER BY priority"
        stmt.execute @iddCode, @areaCode
        
        while row = stmt.fetch do
            gateways << {:gatewayId => row[0], :gatewayName => row[1], :providerId => row[2], :providerName => row[3], :priority => row[4]}
        end
        
        @gateways = gateways
        
        stmt.free_result
        stmt.close
        
        return @gateways
        
    end
    
    def self.reasons
        return @@reasons
    end
    
    def self.get(idd_code, area_code, priority)
        voice_routes = self.search :iddCode => idd_code, :areaCode => area_code || '', :priority => priority || ''
        return voice_routes.first unless voice_routes.empty?
    end
        
    def self.find(args={})
        return self.search args
    end
    
    def self.get_route_providers(idd_code, area_code)
        
        priorities = [DEFAULT, PRIMARY_BACKUP, SECONDARY_BACKUP]
        routes = {}
        
        priorities.each{ |priority|
            rt = self.search :iddCode => idd_code, :areaCode => area_code, :priority => priority
            routes[priority] = rt.empty? ? nil : rt.first
        }
        
        return routes
        
    end
    
    def self.run_scheduled_switch(provider_id, swap_provider_id, revert=false)
    
        db = Dbconn.get_instance 'master'
        db.autocommit false
        db.query "begin"
        
        begin
        
            # get provider details
            default_provider = VoiceProvider.get provider_id
            backup_provider = VoiceProvider.get swap_provider_id
        
            if !default_provider.nil? and !backup_provider.nil?
                
                stmt = db.prepare " SELECT iddCode, areaCode, priority
                                    FROM voiceroute
                                    WHERE providerid = ?"
                stmt.execute provider_id
            
                log_stmt = db.prepare " INSERT INTO mislog(description, section, objectid, action, staffid, datecreated) 
                                    VALUES ('Scheduled backup for voice route. Switched default route form #{default_provider.provider} to #{bac    kup_provider.provider} with iddCode: ?, areaCode: ?, priority: ?','VOICE_ROUTE', ?,'SCHEDULED SWITCH',0,NOW());"
        
                # insert into mislog
                while row = stmt.fetch do
                    log_stmt.execute row[0], row[1], row[2], "#{row[0]}_#{row[1]}"
                end 
                
                stmt.free_result
                stmt.close
        
                log_stmt.close
            
                # update voiceroute
                stmt = db.prepare "UPDATE voiceroute SET providerId = ? WHERE providerId = ?"
                stmt.execute swap_provider_id, provider_id
                stmt.close
                
                if revert
                    stmt = db.prepare " DELETE FROM mislog 
                                        WHERE objectid = ? 
                                        AND action = 'BACKUP_ROUTE_SCHEDULE' 
                                        AND section = 'VOICE_ROUTE'"
                    stmt.execute '#{provider_id}_#{swap_provider_id}'
                    stmt.close                  
                end
                
            else
                AuditHelper::log "SCHEDULED SWITCH VOICE PROVIDER ERROR:: Default Provider: #{default_provider.provder}, Backup Provider: #{backup_provider.provider}"
            end
            
            # restart engine
            is_restarted, response = VoiceRoute.restart_engine
            MailNotifier.deliver_schedule_voice_provider(default_provider.provider, backup_provider.provider, restart)
        
            db.rollback
            
        rescue Exception => e
            db.rollback
            AuditHelper::log "SCHEDULED SWITCH VOICE PROVIDER ERROR:: #{e.message}"
        end
        
        db.autocommit true
        
    end
    
    def schedule_switch(provider_id, swap_provider_id, start_date_time, end_date_time)
        
        
            
    end
    
    def get_scheduled_switch_route
        
        schedule = Log.list(:section => 'VOICE_ROUTE', :objectid => "#{@iddCode}_#{@areaCode}", :action => 'BACKUP_ROUTE_SCHEDULE')
        return schedule.first[:description] unless schedule.empty?
    
    end
    
    def self.switch(countries, provider, swap_provider)
    
        is_swapped = true
        error = ''
        countries_affected = []
        idd_codes = []
        
        begin
        
            new_provider = VoiceProvider.get swap_provider
            
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            stmt = db.prepare " SELECT  country.id, 
                                        country.name, 
                                        voiceroute.areacode, 
                                        country.iddcode, 
                                        priority, 
                                        backup
                                FROM voiceroute LEFT JOIN ( SELECT DISTINCT country.iddCode, areacode, CASE WHEN priority = 2 THEN 'Primary Backup' ELSE 'Secondary Backup' END backup FROM voiceroute, country WHERE providerid = ? 
                                                            AND priority != ? 
                                                            AND country.id IN (#{countries.join(',')}) ) excludedRoutes 
                                                ON excludedRoutes.iddCode = voiceroute.iddcode AND excludedRoutes.areacode = voiceroute.areacode, country 
                                WHERE voiceroute.iddcode = country.iddcode 
                                AND country.id IN (#{countries.join(',')}) 
                                AND voiceroute.providerid = ? 
                                AND voiceroute.priority = ? 
                                GROUP BY country.name, voiceroute.areacode 
                                ORDER BY country.name, voiceroute.areacode"
            
            # AND excludedRoutes.iddCode IS NULL 
            # AND excludedRoutes.areacode IS NULL 
                                
            
            stmt.execute swap_provider, DEFAULT, provider, DEFAULT
            
            while row = stmt.fetch do
                
                countries_affected << {
                        :id => row[0],
                        :country => row[1],
                        :areaCode => row[2],
                        :iddCode => row[3],
                        :priority => row[4],
                        :backup => !row[5].nil? ? "Not swapped: #{new_provider.name} is #{row[5]}" : nil 
                    }
                idd_codes << row[3]
            end
            
            stmt.free_result
            stmt.close
        
            changed = []
            if !countries_affected.empty?
            
                stmt = db.prepare " UPDATE voiceroute
                                    SET providerid = ?
                                    WHERE iddcode = ?
                                    AND areaCode = ?
                                    AND providerid = ?
                                    AND priority = ?"
                countries_affected.each{ |country_affected|
                    if country_affected[:backup].nil?
                        stmt.execute swap_provider, country_affected[:iddCode], country_affected[:areaCode], provider, DEFAULT  
                    end
                }
                stmt.close
            end 
            
            db.commit
            
        rescue Exception => e
            db.rollback
            is_swapped = false
            error = e.message
        end
        
        db.autocommit true
        
        if is_swapped
            return true, countries_affected
        else
            return false, error
        end
    end
    
    def self.restart_engine
        
        is_restarted = false
        response = ''
        
        AuditHelper::log "Restart Voice Engine: MISUser #{ApplicationController::get_session_user} is requesting restart Voice Engine."
        
        output = ''
        begin
            Net::SSH.start('app02', 'root', :keys => ['/var/www/htdocs/ror/lib/pub_key']) do |ssh|
                AuditHelper::log "Restart Voice Engine: Connected to app02."
                AuditHelper::log "Restart Voice Engine: Restarting voice engine."
                output = ssh.exec!('/root/scripts/restartve.sh')
                AuditHelper::log "Restart Voice Engine: Success."
            end
            is_restarted = true
        rescue Exception => e
            AuditHelper::log "Restart Voice Engine Error: #{e.message}"
            throw e.message
        end
        
        response = output
        
        return is_restarted, response
        
    end
    
    private
    
    def self.search(args)
        
        filter = []
        filter_val = []
        voice_routes = []
        
        if args.has_key? :iddCode
            filter << 'AND iddCode = ?'
            filter_val << args[:iddCode]
        end
        
        if args.has_key? :areaCode
            filter << 'AND areacode = ?'
            filter_val << args[:areaCode]
        end
        
        if args.has_key? :priority
            filter << 'AND priority = ?'
            filter_val << args[:priority]
        end
        
        db = Dbconn.get_instance 
        stmt = db.prepare " SELECT iddCode, areacode, gatewayid, providerid, priority, voiceprovider.name
                    FROM voiceroute LEFT JOIN voiceprovider ON voiceprovider.id = voiceroute.providerid, voicegateway
                    WHERE voiceroute.gatewayid = voicegateway.id #{filter.join(' ')}
                    GROUP BY iddCode, areacode
                    ORDER BY iddcode, areaCode, priority"
        
        stmt.execute *filter_val
        
        while row = stmt.fetch do
            
            voice_route = VoiceRoute.new(
                    :iddCode => row[0],
                    :areaCode => row[1],
                    :gatewayId => row[2],
                    :providerId => row[3],
                    :priority => row[4],
                    :provider => row[5]
                )
            voice_routes << voice_route
        end 
        
        stmt.free_result
        stmt.close
        
        return voice_routes
        
    end
    
    def self.set_route(idd_code, area_code, provider_id, priority)
        
        is_set = false
        error = ''
        
        begin
            
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            stmt = db.prepare ' DELETE FROM voiceroute 
                                WHERE iddcode = ?
                                AND areacode = ?
                                AND priority = ?'
            stmt.execute idd_code, area_code, priority
            stmt.close
            
            stmt = db.prepare ' INSERT INTO voiceroute(iddCode, areacode, gatewayid, providerid, priority)
                                VALUES(?,?,?,?,?)'
            
            voice_gateways = VoiceGateway.all                   
                                
            voice_gateways.each{ |voice_gateway|
                stmt.execute idd_code, area_code, voice_gateway.id, provider_id, priority
            }
            
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