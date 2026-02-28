class SmsProvider
    
    @id
    @name
    @type
    @url
    @port
    @method
    @iddPerfix
    @authorization
    @usernameParam
    @passwordParam
    @sourceParam
    @destinationParam
    @messageParam
    @unicodeMessageParam
    @unicodeParam
    @extraParam
    @unicodeCharset
    @successPattern
    @errorPattern
    @deliveryReporting
    @status
    
attr_accessor :id, :name, :type, :url, :port, :method, :iddPerfix, :authorization, :usernameParam, :passwordParam, :sourceParam, :destinationParam, :messageParam, :unicodeMessageParam, :unicodeParam, :extraParam, :unicodeCharset, :successPattern, :errorPattern, :deliveryReporting, :status
    
    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil
        @type = args[:type] || nil
        @url = args[:url] || nil
        @port = args[:port] || nil
        @method = args[:method] || nil
        @iddPerfix = args[:iddPerfix] || nil
        @authorization = args[:authorization] || nil
        @usernameParam = args[:usernameParam] || nil
        @passwordParam = args[:passwordParam] || nil
        @sourceParam = args[:sourceParam] || nil
        @destinationParam = args[:destinationParam] || nil
        @messageParam = args[:messageParam] || nil
        @unicodeMessageParam = args[:unicodeMessageParam] || nil
        @unicodeParam = args[:unicodeParam] || nil
        @extraParam = args[:extraParam] || nil
        @unicodeCharset = args[:unicodeCharset] || nil
        @successPattern = args[:successPattern] || nil
        @errorPattern = args[:errorPattern] || nil
        @deliveryReporting = args[:deliveryReporting] || nil
        @status = args[:status] || nil
    end
    
    def self.get(id)
        providers = self.search :id => id
    end
    
    def self.all
        return self.search
    end
    
    private
    
    def self.search(args={})
        
        sms_gateways = []
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT id, name, type, url, port, method, iddPrefix, authorization, usernameParam, passwordParam, sourceParam, destinationParam, messageParam, unicodeMessageParam, unicodeParam, extraParam, unicodeCharset, successPattern, errorPattern, deliveryReporting, status FROM smsgateway"
        stmt.execute
        
        while row = stmt.fetch do
            
            sms_provider = SmsProvider.new(
                :id => row[0],
                :name => row[1],
                :type => row[2],
                :url => row[3],
                :port => row[4],
                :method => row[5],
                :iddPerfix => row[6],
                :authorization => row[7],
                :usernameParam => row[8],
                :passwordParam => row[9],
                :sourceParam => row[10],
                :destinationParam => row[11],
                :messageParam => row[12],
                :unicodeMessageParam => row[13],
                :unicodeParam => row[14],
                :extraParam => row[15],
                :unicodeCharset => row[16],
                :successPattern => row[17],
                :errorPattern => row[18],
                :deliveryReporting => row[19],
                :status => row[20]
            )
            
            sms_gateways << sms_provider
            
        end
        
        stmt.free_result
        stmt.close
        
        return sms_gateways
    
    end
    
    
    
end