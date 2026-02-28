class Currency

    @code
    @name
    @symbol
    @exchangeRate
    
    DEFAULT_CURRENCY = 'AUD'
    
    attr_accessor :code, :name, :symbol, :exchangeRate
    
    def initialize(args={})
        @code = args[:code] || nil
        @name = args[:name] || nil  
        @symbol = args[:symbol] || nil
        @exchangeRate = args[:exchangeRate] || nil
    end
    
    def self.all(args={})
        return self.search args
    end
    
    private
    
    def self.search(args)
        
        currencies = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT code, name, symbol, exchangeRate
                            FROM currency
                            ORDER BY name"
        stmt.execute
        
        while row = stmt.fetch do
            currency = Currency.new(
                    :code => row[0],
                    :name => row[1],
                    :symbol => row[2],
                    :exchangeRate => row[3]
                )
            currencies << currency  
        end
        
        stmt.free_result
        stmt.close                  
                            
        return currencies   
        
    end
    
end