class Country

    @id
    @name
    @iddCode
    @currency
    @population
    @allowCreditCard
    @allowZeroAfterIddCode
    @allowEmail
    @allowPhoneCall
    @lowAsrDestination
    @callRetries
    
    
    attr_accessor :id, :name, :iddCode, :currency, :population, :allowCreditCard, :allowZeroAfterIddCode, :allowEmail, :allowPhoneCall, :lowAsrDestination, :callRetries
    
    def initialize(args={})
        @id = args[:id]
        @name = args[:name]                                 
        @iddCode = args[:iddCode]
        @currency = args[:currency]
        @population = args[:population]
        @allowCreditCard = args[:allowCreditCard]
        @allowZeroAfterIddCode = args[:allowZeroAfterIddCode]
        @allowEmail = args[:allowEmail]
        @allowPhoneCall = args[:allowPhoneCall]
        @lowAsrDestination = args[:lowAsrDestination]
        @callRetries = args[:callRetries]
    end
    
    def self.get(id)
        countries = self.search :id => id
        return countries.first unless countries.nil?
    end
        
    def self.getCountries
        opts = []
        countries = SoapClient.call('getCountries', opts)
        return countries
    end
    
    def self.all(args={})
        
        return self.search args
    end
    
    def self.find(args={})
        countries = self.search args
        return countries.first unless countries.empty?
    end
    
    def update
    
        is_updated = true
        error = ''
        
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare " UPDATE country SET
                                    population = ?,
                                    allowCreditCard = ?,
                                    allowZeroAfterIddCode = ?,
                                    allowEmail = ?,
                                    allowPhoneCall = ?,
                                    lowAsrDestination = ?,
                                    callRetries = ?
                                WHERE id = ?"
            stmt.execute @population, @allowCreditCard, @allowZeroAfterIddCode, @allowEmail, @allowPhoneCall, @lowAsrDestination, @callRetries, @id
            stmt.close
        rescue Exception => e
            is_updated = false
            error = e.message
        end
        
        return is_updated, error
    
    end
    
    private
    
    def self.search(args={})
    
        filter = []
        filter_val = []
        countries = []
        
        if args.has_key? :id
            filter << 'AND id = ?'
            filter_val << args[:id]
        end
        
        if args.has_key? :name
            filter << 'AND name = ?'
            filter_val << args[:name]
        end
        
        if args.has_key? :iddCode
            filter << 'AND iddcode = ?'
            filter_val << args[:iddCode]
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT id, 
                                   name, 
                                   iddcode, 
                                   currency,
                                   population,
                                   allowCreditCard,
                                   allowZeroAfterIddCode,
                                   allowEmail,
                                   allowPhoneCall,
                                   lowAsrDestination,
                                   callRetries 
                            FROM country WHERE 1=1 #{filter.join(' ')} 
                            ORDER BY name"
        stmt.execute *filter_val
        
        while row = stmt.fetch do
            country = Country.new(
                :id => row[0],
                :name => row[1],
                :iddCode => row[2],
                :currency => row[3],
                :population => row[4],
                :allowCreditCard => row[5],
                :allowZeroAfterIddCode => row[6],
                :allowEmail => row[7],
                :allowPhoneCall => row[8],
                :lowAsrDestination => row[9],
                :callRetries => row[10]
            )
            countries.push country
        end
        
        stmt.free_result
        stmt.close
        
        return countries
        
  end

  def self.getCountriesISO
    countries = { '-1' => 'All'}
    db = Dbconn.get_instance
    stmt = db.prepare "SELECT ISOCountryCode, Name FROM country ORDER BY Name"
    stmt.execute
    
    while row = stmt.fetch do
      countries[row[0]] = row[1]
    end
    
    return countries
  end
    
end