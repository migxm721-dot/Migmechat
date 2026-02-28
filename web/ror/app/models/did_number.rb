class DidNumber
    
    @countryId
    @number
    @dialNumber
    @status
    
    # derived
    @country
    @iddCode
    
    attr_accessor :countryId, :number, :dialNumber, :status, :country, :iddCode
    
    def initialize(args={})
        @attributes = args
        @countryId = args[:countryId] || nil
        @number = args[:number] || nil
        @dialNumber = args[:dialNumber] || nil
        @status = args[:status] || nil
        @country = args[:country] || nil
        @iddCode = args[:iddCode] || nil
    end
    
    def create
        
        is_created = false
        error = ''
        
        begin
        
            db = Dbconn.get_instance 'master'
            
            # check for duplicate
            stmt = db.prepare "SELECT COUNT(*) FROM didnumber WHERE countryid = ? AND number = ?"
            stmt.execute @countryId, @number
            num_rows = stmt.fetch.first
            
            if num_rows > 0
                stmt.free_result
                stmt.close
                error = 'DID Number already exists.'
            else
                stmt = db.prepare " INSERT INTO DIDNumber (CountryId, Number, DialNumber, Status) VALUES (?,?,?,?)"
                stmt.execute @countryId, @number, @dialNumber, @status
                stmt.free_result
                stmt.close
            
                is_created = true
            end
            
        rescue Exception => e
            error = e.message
        end
        
        return is_created, error
        
    end
    
    # we allow multiple did numbers in a country so we need to pass the old did number when we do an update
    def update(old_number)
        is_updated = false
        error = ''
        
        begin
        
            db = Dbconn.get_instance 'master'
            stmt = db.prepare " UPDATE DIDNumber 
                                SET Number = ?, Status = ?, DialNumber = ? 
                                WHERE CountryId = ?
                                AND Number = ?"
            stmt.execute @number, @status, @dialNumber, @countryId, old_number
            stmt.close
            
            is_updated = true
        rescue Exception => e
            error = e.message
        end
        
        return is_updated, error
    end
    
    def delete
        is_deleted = false
        error = ''
        
        begin
        
            db = Dbconn.get_instance 'master'
            stmt = db.prepare " DELETE FROM DIDNumber WHERE CountryId = ? AND Number = ?"
            stmt.execute @countryId, @number
            stmt.close
            
            is_deleted = true
        rescue Exception => e
            error = e.message
        end
        
        return is_deleted, error
    end
    
    def self.all(args={})
        return self.search args
    end
    
    def validate
        validates_with DidNumberValidator
    end
    
    def self.get(countryId, number)
        did_numbers = DidNumber.search :countryId => countryId, :number => number
        return did_numbers.first unless did_numbers.empty?
    end
    
    def self.get_available_countries_to_add
        
        countries = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT id, name, iddcode, currency 
                            FROM country 
                            WHERE id NOT IN (SELECT countryId FROM didnumber) 
                            ORDER BY name"
        stmt.execute 
        
        while row = stmt.fetch do
            country = Country.new(
                :id => row[0],
                :name => row[1],
                :iddCode => row[2],
                :currency => row[3]
            )
            countries.push country
        end
 
        stmt.free_result
        stmt.close
        
        return countries
        
    end

    private
    
    def self.search(args)
    
        filter = []
        filter_val = []
        
        if args.has_key? :countryId
            filter << 'AND countryId = ?'
            filter_val << args[:countryId]
        end
        
        if args.has_key? :number
            filter << 'AND number = ?'
            filter_val << args[:number]
        end
    
        did_numbers = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT countryid, number, dialnumber, status, country.name, country.iddCode
                            FROM didnumber, country
                            WHERE didnumber.countryId = country.id #{filter.join(' ')}
                            ORDER BY country.name"
        stmt.execute *filter_val
        
        while row = stmt.fetch do
            did_number = DidNumber.new(
                    :countryId => row[0],
                    :number => row[1],
                    :dialNumber => row[2],
                    :status => row[3],
                    :country => row[4],
                    :iddCode => row[5]
                )
            did_numbers << did_number       
        end 
        
        stmt.free_result
        stmt.close      
        
        return did_numbers      
                            
    end
    
    def read_attribute_for_validation(key)
        @attributes[key]
    end
        
end