class VirtualGift

    @name
    @shortName
    @dateRelease
    @price
    @quantity
    @category
    @currency
    @images
    @attachment
    @id
    @migLevelMin
    @revenueShare
    
    @@imgDimType = { 
                'gif' => [[12,12],[14,14],[16,16]],
                'png' => [[12,12],[14,14],[16,16],[24,24],[48,48],[64,64]] 
    }
    

    attr_accessor :name, :shortName, :dateRelease, :price, :quantity, :category, :currency, :images, :attachment, :migLevelMin, :revenueShare
    
    def initialize(args={})
        @id = args[:id]
        @name = args[:name]
        @shortName = args[:shortName]
        @dateRelease = DateTime.strptime(args[:dateRelease], "%m/%d/%Y").to_time unless args[:dateRelease].nil?
        @price = args[:price]
        @quantity = args[:quantity]
        @category = args[:category]
        @currency = args[:currency]
        @images = args[:images] || {}
        @attachment = args[:attachment]
        @migLevelMin = args[:migLevelMin] || 1
        @revenueShare = args[:revenueShare] || nil
    end 
    
    def dateRelease=(releaseDate)
        releaseDate = DateTime.strptime(releaseDate, "%m/%d/%Y").to_time unless releaseDate.nil?
        @dateRelease = releaseDate
    end
    
    # static functions
    
    def self.search_user_virtual_gifts(args={})
    
        sent = args[:sent] || ''
        received = args[:received] || ''
        gift = args[:gift] || ''
        dateFrom = args[:dateFrom] || ''
        dateTo = args[:dateTo] || ''
        
        db = Dbconn.get_instance
        
        result = []
        
        qry = "SELECT user.username, country.name, COUNT(*) AS giftCount FROM user, country, virtualgiftreceived, virtualgift WHERE user.countryId = country.id AND virtualgift.id = virtualgiftreceived.virtualgiftid "
        
        qryArgs = []        
        
        if !sent.empty?
            qry = "#{qry} AND virtualgiftreceived.sender = user.username AND virtualgiftreceived.sender = ? "
            qryArgs.push sent
        end 
        if !received.empty?
            qry = "#{qry} AND virtualgiftreceived.username = user.username AND virtualgiftreceived.username = ? "
            qryArgs.push received
        end 
        if !gift.empty?
            qry = "#{qry} AND virtualgift.name = ? "    
            qryArgs.push gift
        end
        if !dateFrom.empty?
            qry = "#{qry} AND virtualgiftreceived.datecreated >= ? "
            qryArgs.push DateTime.strptime(dateFrom, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        end
        if !dateTo.empty?
            qry = "#{qry} AND virtualgiftreceived.datecreated <= ? "
            qryArgs.push DateTime.strptime(dateTo, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        end
        
        if !sent.empty?
            qry = "#{qry} GROUP BY user.countryID, virtualgiftreceived.sender"
        end 
        if !received.empty?
            qry = "#{qry} GROUP BY user.countryID, virtualgiftreceived.username"
        end 
        
        stmt = db.prepare qry
        if qryArgs.empty?
            stmt.execute
        else    
            stmt.execute *qryArgs       
        end
        
        while row = stmt.fetch do
            rowResult = { :username => row[0], :country => row[1], :giftCount => row[2] }
            result.push rowResult
        end
        
        stmt.free_result
        stmt.close
        
        return result
        
    end
    
    def self.imgDimType
        return @@imgDimType
    end
    
    def self.vgCategories
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT id, name FROM storecategory WHERE parentstorecategoryid = 1 ORDER BY name"
        stmt.execute
        
        categories = {}
        
        while row = stmt.fetch do
            categories[row[0]] = row[1]
        end
        
        stmt.free_result
        stmt.close
        
        return categories
    end
        
end