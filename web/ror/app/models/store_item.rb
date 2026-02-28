class StoreItem

    @@types = {
        1 => 'Virtual Gift',
        2 => 'Avatar',
        3 => 'Emoticon Pack',
        4 => 'Super Emoticon Pack',
        5 => 'Theme',
        6 => 'Sticker Pack'
    }
 
    @@controllerTypes = {
        1 => 'virtual_gift',
        2 => 'avatar',
        3 => 'emoticon_pack',
        6 => 'sticker_pack'
    }   


    
    @id
    @name
    @type
    @price
    @featured
    @currency
    @status
    @referenceId
    @groupId
    @numSold
    @migLevelMin
    @category
    @revenueShare
    @shortname
    #derived
    @group
    
    attr_accessor :id, :name, :type, :price, :featured, :currency, :status, :referenceId, :groupId, :group, :numSold, :migLevelMin, :category, :revenueShare, :shortname
    
    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil      
        @type = args[:type] || nil
        @price = args[:price] || nil
        @featured = args[:featured] || nil
        @currency = args[:currency] || nil
        @status = args[:status] || nil
        @referenceId = args[:referenceId] || nil
        @groupId = args[:groupId] || nil
        @group = args[:group] || nil
        @numSold = args[:numSold] || nil
        @migLevelMin = args[:migLevelMin] || nil
        @category = args[:category] || nil
        @revenueShare = args[:revenueShare] || nil
        @shortname = args[:shortname] || nil
    end
    
    def update
        
        is_updated = false
        error = ''
        
        begin
            
            db = Dbconn.get_instance 'master'
            db.autocommit false
            db.query 'begin'
            
            stmt = db.prepare "UPDATE storeitem SET name = ?, price = ?, featured = ?, currency = ?, status = ?, forsale = ?, groupid = ?, miglevelmin = ? WHERE id = ?"
            stmt.execute @name, @price, @featured, @currency, @status, @status, @groupId, @migLevelMin, @id
            stmt.close
            
            case StoreItem.controller_type(@type.to_i)
                # would've prefer constants over this but dont want to be redundant 
                when 'virtual_gift'
                    stmt = db.prepare "UPDATE virtualgift SET status = ?, price = ?, currency = ?, groupid = ? WHERE id = ?"
                    stmt.execute @status, @price, @currency, @groupId, @referenceId
                    stmt.close
            
                when 'avatar'
                    stmt = db.prepare "UPDATE avataritem SET status = ? WHERE id = ?"
                    stmt.execute @status, @referenceId
                    stmt.close
                
                when 'emoticon_pack', 'sticker_pack' 
                    stmt = db.prepare "UPDATE emoticonpack SET status = ?, price = ?, groupid = ? WHERE id = ?"
                    stmt.execute @status, @price, @groupId, @referenceId
                    stmt.close
            
            end 
            
            if !@revenueShare.nil?
              query = "UPDATE storeitemrevenueshare set revenueShare = ? where storeItemID = ?"
              stmt = db.prepare query
              stmt.execute @revenueShare, @id
              stmt.close
            end
            
            db.commit
            db.autocommit true
            is_updated = true
        rescue Exception => e
            db.rollback
            error = e.message
        end
        
        return is_updated, error
        
    end
    
    def update_item_category(categories = [])
      
      error = ''
      success = false
      
      begin
        db = Dbconn.get_instance 'master'
        db.autocommit false
      db.query 'begin'
      
      query = ""
      if @type == 2
        stmt = db.prepare "DELETE FROM storeitemcategory where StoreItemID = ?"
        stmt.execute @id
        stmt.close
      end
      
      if @type == 2
        categories.collect! { |category| '(%d , %d)' % [@id, category]}
        values = categories.join(',')
        query = "INSERT INTO storeitemcategory (StoreItemID, StoreCategoryID) VALUES #{values}"
      else
        query = "UPDATE storeitemcategory SET StoreCategoryID = #{categories[0]} where StoreItemID = #{@id}"
      end
      
      stmt = db.prepare query
      stmt.execute
      stmt.close
      db.commit
      db.autocommit true
      success = true
      Store.flush_cache
      rescue Exception => e
        db.rollback
        error = e.message
      end
      
      return success, error
      
    end
    
    def logs
        return @logs.nil? ? (Log.list :section => 'STORE_ITEM', :objectid => @id, :orderBy => 'ASC') : @logs
    end
    
    def is_type? type
        return type.downcase.eql? @@types[@type].downcase
    end
    
    def self.types
        return @@types
    end
    
    def self.display_type(type)
        return @@types[type] if @@types.has_key? type
    end
    
    def self.controller_type(type)
        return @@controllerTypes[type] if @@controllerTypes.has_key? type
    end    

    def self.all(args={})
        return self.search(args)    
    end
    
    def self.get(id)
        store_items = self.search :id => id
        return store_items.first unless store_items.empty?
    end
    
    def self.find(args={})
        store_items = self.search(args)
        return store_items.first unless store_items.empty?
    end
    
    private
    
    def self.search(args)
    
        filter = []
        filterVal = []
        store_items = []
        
        return store_items if args.empty?
        
        if args.has_key? :id
            filter << 'storeitem.id = ?'
            filterVal << args[:id]
        end
        
        if args.has_key? :ids
            filter << "storeitem.id IN (#{args[:ids].map{ |id| '?' }.join(',')})"
            filterVal << args[:ids]
        end                     
        
        if args.has_key? :name
            filter << 'storeitem.name LIKE ?'
            filterVal << args[:name]
        end
        
        if args.has_key? :category
            filter << 'storecategoryid = ?'
            filterVal << args[:category]
        end
        
        if args.has_key? :numsold
            filter << 'numsold <= ?'
            filterVal << args[:numsold]
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT storeitem.id, storeitem.name, storeitem.type, price, storeitem.featured, currency, storeitem.status, referenceid, groupid, groups.name, numsold, migLevelMin, storecategory.id as storecategoryid, revenueshare, catalogimage
                            FROM storeitem 
                            LEFT JOIN storeitemcategory ON storeitemcategory.storeitemid = storeitem.id
                            LEFT JOIN groups ON groups.id = storeitem.groupid
                            LEFT JOIN storeitemrevenueshare ON storeitemrevenueshare.storeitemid = storeitem.id
                            LEFT OUTER JOIN storecategory ON storecategory.id = storeitemcategory.storecategoryid
                            WHERE #{filter.join(' AND ')}"
        stmt.execute *filterVal
        
        while row = stmt.fetch do
            store_item = StoreItem.new( 
                :id => row[0],
                :name => row[1],
                :type => row[2],
                :price => row[3],
                :featured => row[4],
                :currency => row[5],
                :status => row[6],
                :referenceId => row[7],
                :groupid => row[8],
                :group => row[9],
                :numSold => row[10],
                :migLevelMin => row[11],
                :category => row[12],
                :revenueShare => row[13],
                :shortname => row[14])
            store_items << store_item       
        end
        
        stmt.free_result
        stmt.close
        
        return store_items
        
    end

end