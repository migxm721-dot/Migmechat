class CallRate
    
    @countryId
    @country
    @iddCode
    
    @currency
    @callThroughSignallingFee
    @callThroughRate
    @mobileSignallingFee
    @mobileRate
    @callSignallingFee
    @callRate
    
    DEFAULT_CURRENCY = 'AUD'
    
    attr_accessor :countryId, :country, :currency, :callThroughSignallingFee, :callThroughRate, :mobileSignallingFee, :mobileRate, :callSignallingFee, :callRate, :iddCode
    
    def initialize(args)
        @countryId = args[:countryId] || nil
        @currency = args[:currency] || nil
        @callThroughSignallingFee = args[:callThroughSignallingFee] || nil
        @callThroughRate = args[:callThroughRate] || nil
        @mobileSignallingFee = args[:mobileSignallingFee] || nil
        @mobileRate = args[:mobileRate] || nil
        @callSignallingFee = args[:callSignallingFee] || nil
        @callRate = args[:callRate] || nil
        @iddCode = args[:iddCode] || nil
        @country = args[:country] || nil
    end
    
    def self.call_costs
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT iddCode, area"
        
    end
    
    def self.all(args={})
        return self.search args
    end
    
    def self.get(countryId)
        call_rates = self.search :countryId => countryId
        return call_rates.first unless call_rates.empty?
    end
    
    def propose(rates, comment)
        
        is_created = false
        error = ''
        
        # create file 
        begin
            File.open(CALL_RATE_PROPOSED + "/#{@countryId}.csv", 'w+') do |output|
                output.puts rates.callThroughSignallingFee
                output.puts rates.callThroughRate
                output.puts rates.mobileSignallingFee
                output.puts rates.mobileRate
                output.puts rates.callSignallingFee
                output.puts rates.callRate
            end
            FileUtils.chmod 0666, CALL_RATE_PROPOSED + "/#{@countryId}.csv"
            
            # send email to whoever will approve
            MailNotifier.deliver_update_voice_rate(@country, @countryId, rates, comment)
            is_created = true
        rescue Exception => e
            error = e.message
        end
        
        return is_created, error
        
    end
    
    def self.get_unapproved_rates(country_id)
        return self.get_rates country_id
    end
    
    def approve
        
        is_approved, error = update
        
        if is_approved
            FileUtils.rm "#{CALL_RATE_PROPOSED}/#{@countryId}.csv" if File.exists? "#{CALL_RATE_PROPOSED}/#{@countryId}.csv"
        end
        
        return update
        
    end
    
    def reject(comment)
        # send email to whoever will approve
        MailNotifier.deliver_reject_voice_rate(@country, @countryId, self, comment)
        FileUtils.rm "#{CALL_RATE_PROPOSED}/#{@countryId}.csv" if File.exists? "#{CALL_RATE_PROPOSED}/#{@countryId}.csv"
    end
    
    def get_call_through_rates
        
        costs = {}
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT   country.name,
                                    country.id, 
                                    voicewholesalerate.iddcode, 
                                    voicewholesalerate.areacode, 
                                    voicewholesalerate.rate, 
                                    voicewholesalerate.rate / currency.exchangerate rate, 
                                    voiceroute.providerid,
                                    voiceprovider.name,
                                    priority,
                                    mobilesignallingfee,
                                    mobilerate,
                                    callsignallingfee,
                                    callrate
                            FROM voicewholesalerate, currency, country, voiceroute, voiceprovider 
                            WHERE voicewholesalerate.currency = currency.code
                            AND voicewholesalerate.iddcode = voiceroute.iddcode
                            AND voicewholesalerate.areacode = voiceroute.areacode
                            AND voiceroute.iddcode = country.iddcode
                            AND voicewholesalerate.iddcode = country.iddcode
                            AND voiceroute.providerid = voiceroute.providerid
                            AND voiceprovider.id = voiceroute.providerid
                            AND country.id = ?
                            GROUP BY iddcode, areacode, providerid
                            ORDER BY country.name, areacode, priority"
        stmt.execute @countryId
        
        while row = stmt.fetch do
            costs[row[1]] = [] unless costs.has_key? row[1]
            cost = {
                    :country => row[0],
                    :countryId => row[1],
                    :iddCode => row[2],
                    :areaCode => row[3],
                    :rate => row[4],
                    :actualRate => row[5],
                    :providerId => row[6],
                    :provider => row[7],
                    :priority => row[8],
                    :mobileSignallingFee => row[9],
                    :mobileRate => row[10],
                    :landlineSignallingFee => row[11],
                    :landlineRate => row[12]
                }
            costs[row[1]] << cost
        end                 
        
        stmt.free_result                    
        stmt.close
        
        return costs
        
    end
    
    def self.get_proposed_rates
        
        rates = []
        country_ids = Dir.new(CALL_RATE_PROPOSED).entries.delete_if{ |file| !/^[0-9]+\.csv$/.match file }.map{ |country_id| country_id.gsub!('.csv','') }
        
        return rates if country_ids.empty?
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT username, comment, mislog.datecreated, description, country.name, country.id
                            FROM (
                                SELECT MAX(datecreated) datecreated
                                FROM mislog 
                                WHERE action = 'PROPOSE'
                                AND section = 'VOICE_RATE'
                                AND objectid IN (#{country_ids.join(',')})
                                GROUP BY objectid
                            ) AS edt INNER JOIN mislog ON edt.datecreated = mislog.datecreated
                            LEFT JOIN staff ON mislog.staffid = staff.id, country
                            WHERE mislog.objectid = country.id
                            ORDEr BY country.name"
        stmt.execute
        
        while row = stmt.fetch do
            rates << {
                :staff => row[0],
                :comment => row[1],
                :dateCreated => row[2],
                :description => row[3],
                :country => row[4]  ,
                :countryId => row[5]
            }
        end
        
        stmt.free_result
        stmt.close
        
        return rates
        
    end
    
    def self.get_costs(country_id=nil)
        
        costs = {}
        
        app_stmt = "AND country.id = #{country_id}" unless country_id.nil?
    
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT   country.name,
                                    country.id, 
                                    voicewholesalerate.iddcode, 
                                    voicewholesalerate.areacode, 
                                    voicewholesalerate.rate, 
                                    voicewholesalerate.rate / currency.exchangerate rate, 
                                    voiceroute.providerid,
                                    voiceprovider.name,
                                    priority,
                                    mobilesignallingfee,
                                    mobilerate,
                                    callsignallingfee,
                                    callrate
                            FROM voicewholesalerate, currency, country, voiceroute, voiceprovider 
                            WHERE voicewholesalerate.currency = currency.code
                            AND voicewholesalerate.iddcode = voiceroute.iddcode
                            AND voicewholesalerate.areacode = voiceroute.areacode
                            AND voiceroute.iddcode = country.iddcode
                            AND voicewholesalerate.iddcode = country.iddcode
                            AND voiceroute.providerid = voiceroute.providerid
                            AND voiceprovider.id = voiceroute.providerid
                            #{app_stmt}
                            GROUP BY iddcode, areacode, providerid
                            ORDER BY country.name, areacode, priority"
        stmt.execute
        
        while row = stmt.fetch do
            costs[row[1]] = [] unless costs.has_key? row[1]
            cost = {
                    :country => row[0],
                    :countryId => row[1],
                    :iddCode => row[2],
                    :areaCode => row[3],
                    :rate => row[4],
                    :actualRate => row[5],
                    :providerId => row[6],
                    :provider => row[7],
                    :priority => row[8],
                    :mobileSignallingFee => row[9],
                    :mobileRate => row[10],
                    :landlineSignallingFee => row[11],
                    :landlineRate => row[12]
                }
            costs[row[1]] << cost
        end                 
        
        stmt.free_result                    
        stmt.close
        
        return costs
        
    end
    
    def self.get_callback_costs(country_id)
        
        mobile_prefixes = VoiceProvider.get_mobile_prefixes
        costs = {}
        callback_costs = []
        
        # get country cost
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT   country.name,
                                    country.id, 
                                    voicewholesalerate.iddcode, 
                                    voicewholesalerate.areacode, 
                                    voicewholesalerate.rate, 
                                    voicewholesalerate.rate / currency.exchangerate rate, 
                                    voiceroute.providerid,
                                    voiceprovider.name,
                                    priority,
                                    mobilesignallingfee,
                                    mobilerate,
                                    callsignallingfee,
                                    callrate
                            FROM voicewholesalerate, currency, country, voiceroute, voiceprovider 
                            WHERE voicewholesalerate.currency = currency.code
                            AND voicewholesalerate.iddcode = voiceroute.iddcode
                            AND voicewholesalerate.areacode = voiceroute.areacode
                            AND voiceroute.iddcode = country.iddcode
                            AND voicewholesalerate.iddcode = country.iddcode
                            AND voiceroute.providerid = voiceroute.providerid
                            AND voiceprovider.id = voiceroute.providerid
                            GROUP BY iddcode, areacode, providerid
                            ORDER BY country.name, areacode, priority"
        stmt.execute
        
        while row = stmt.fetch do
            costs[row[1]] = [] unless costs.has_key? row[1]
            cost = {
                    :country => row[0],
                    :countryId => row[1],
                    :iddCode => row[2],
                    :areaCode => row[3],
                    :rate => row[4],
                    :actualRate => row[5],
                    :providerId => row[6],
                    :provider => row[7],
                    :priority => row[8],
                    :mobileSignallingFee => row[9],
                    :mobileRate => row[10],
                    :landlineSignallingFee => row[11],
                    :landlineRate => row[12]
                }
            costs[row[1]] << cost
        end 
        
        stmt.free_result
        stmt.close
        
        if costs.has_key? country_id
            costs.each{ |cost_country_id, country_costs|
                if cost_country_id != country_id
                    country_costs.each{ |country_cost|
                        costs[country_id].each{ |cost|
                                
                            # get source details
                            if mobile_prefixes.include?("#{country_cost[:iddCode]}#{country_cost[:areaCode]}") and !country_cost[:areaCode].empty?
                                source_type = 'M'
                                source_charge = country_cost[:mobileRate] 
                                source_signalling_fee = country_cost[:mobileSignallingFee]
                            else
                                source_type = 'L'
                                source_charge = country_cost[:landlineRate]
                                source_signalling_fee = country_cost[:landlineSignallingFee]
                            end
                            source_cost = country_cost[:actualRate]
                            
                            # get destination details
                            if mobile_prefixes.include?("#{cost[:iddCode]}#{cost[:areaCode]}") and !cost[:areaCode].empty?
                                destination_type = 'M'
                                destination_charge = cost[:mobileRate] 
                                destination_signalling_fee = cost[:mobileSignallingFee]
                            else
                                destination_type = 'L'
                                destination_charge = cost[:landlineRate]
                                destination_signalling_fee = cost[:landlineSignallingFee]
                            end
                            destination_cost = cost[:actualRate]
                            
                            # compute margin
                            price = source_cost + destination_cost
                            margin = (source_charge + source_signalling_fee + destination_charge + destination_signalling_fee) - price
                            margin_rate = (margin/price)*100
                            
                            callback_costs << {
                                :source => country_cost[:country],
                                :sourceAreaCode => country_cost[:areaCode],
                                :sourceType => source_type,
                                :sourceProvider => country_cost[:provider],
                                :sourcePriority => country_cost[:priority],
                                :sourceCost => source_cost,
                                :sourceSignallingFee => source_signalling_fee,
                                :destination => cost[:country],
                                :destinationAreaCode => cost[:areaCode],
                                :destinationType => destination_type,
                                :destinationProvider => cost[:provider],
                                :destinationPriority => cost[:priority],
                                :destinationCost => destination_cost,
                                :destinationSignallingFee => destination_signalling_fee,
                                :margin => margin,
                                :rate => margin_rate,
                                :sourceCharge => source_charge,
                                :destinationCharge => destination_charge
                            }

                        }   
                    }
                end
            }
        
        end
        
        return callback_costs
        
    end
    
    private
    
    def self.search(args={})
    
        filter_val = []
        filter = []
        app_sql = ''
        call_rates = []
        
        if args.has_key? :countryId
            filter << 'id = ?'
            filter_val << args[:countryId]
        end
        
        if !filter.empty?
            app_sql = 'WHERE'
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  id, 
                                    name, 
                                    currency, 
                                    callthroughsignallingfee, 
                                    callthroughrate, 
                                    mobilesignallingfee, 
                                    mobilerate, 
                                    callsignallingfee, 
                                    callrate, 
                                    iddCode
                            FROM country #{app_sql} #{filter.join(' ')} ORDER BY name"
        stmt.execute *filter_val
        
        while row = stmt.fetch do
            call_rate = CallRate.new(
                    :countryId => row[0],
                    :country => row[1],
                    :currency => row[2],
                    :callThroughSignallingFee => row[3],
                    :callThroughRate => row[4],
                    :mobileSignallingFee => row[5],
                    :mobileRate => row[6],
                    :callSignallingFee => row[7],
                    :callRate => row[8],
                    :iddCode => row[9]
                )
            call_rates << call_rate 
        end
        
        stmt.free_result
        stmt.close
        
        return call_rates
        
    end
    
    def self.get_rates(country_id)
        
        return nil unless File.exists? "#{CALL_RATE_PROPOSED}/#{country_id}.csv"
        
        rates = []
        File.open("#{CALL_RATE_PROPOSED}/#{country_id}.csv").each_line{ |line|
            rates << line.to_f
        }
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT id, name, currency, iddCode
                            FROM country 
                            WHERE id = ?"
        stmt.execute country_id
        
        while row = stmt.fetch do
            call_rate = CallRate.new(
                    :countryId => row[0],
                    :country => row[1],
                    :currency => row[2],
                    :iddCode => row[3]
                )
        end
        
        call_rate.callThroughSignallingFee = rates[0]
        call_rate.callThroughRate = rates[1]
        call_rate.mobileSignallingFee = rates[2]
        call_rate.mobileRate = rates[3]
        call_rate.callSignallingFee = rates[4]
        call_rate.callRate = rates[5]
        
        stmt.free_result
        stmt.close
        
        return call_rate        
    end
    
    def update
        
        is_updated = false
        error = ''
        
        begin
        
            db = Dbconn.get_instance 'master'
            stmt = db.prepare " UPDATE country
                                SET callthroughsignallingfee = ?,
                                    callthroughrate = ?,
                                    mobilesignallingfee = ?,
                                    mobilerate = ?,
                                    callsignallingfee = ?,
                                    callrate = ?
                                WHERE id = ?"
            
            stmt.execute @callThroughSignallingFee, @callThroughRate, @mobileSignallingFee, @mobileRate, @callSignallingFee, @callRate, @countryId
            stmt.close
            
            is_updated = true
                                
        rescue Exception => e
            error = e.message
        end
        
        return is_updated, error
    
    end

    
end