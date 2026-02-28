class Store
    
    def self.get_currency_list
        
        currencies = []
            
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT currency FROM country GROUP BY currency"
        stmt.execute
        
        while row = stmt.fetch do
            currencies << row[0]
        end
        
        stmt.free_result
        stmt.close
        
        return currencies
    end
    
    def self.get_category_id_list
        
        categories = []
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT id FROM storecategory ORDER BY id ASC"
        stmt.execute
        
        while row = stmt.fetch do
            categories << row[0]
        end
        
        stmt.free_result
        stmt.close
        
        return categories
        
    end
    
    def self.get_store_categories
        
        categories = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT a.id, a.name, a.parentstorecategoryid, b.name, CONCAT_WS(' - ', b.name, a.name) displayname 
                            FROM storecategory a LEFT JOIN storecategory b ON a.parentstorecategoryid = b.id
                            WHERE a.parentstorecategoryid IS NOT NULL
                            ORDER BY displayname ASC"
        stmt.execute
        
        while row = stmt.fetch do
            category = {
                :id => row[0],
                :name => row[1],
                :parentCategoryId => row[2],
                :parentCategoryName => row[3],
                :displayName => row[4]
            }
            categories << category
        end
        
        stmt.free_result
        stmt.close
        
        return categories
        
    end
    
    def self.get_category(id)
      errors = ''
      category = {}
      query = "SELECT id, name, parentstorecategoryid FROM storecategory WHERE id = ?"
      begin
        db = Dbconn.get_instance
        stmt = db.prepare query
        stmt.execute id
        row = stmt.fetch
        category = {
          :id => row[0],
        :name => row[1],
        :parentCategoryId => row[2] 
        }
      rescue Exception => e
        errors = e.message
      end
      
      return category, errors
      
    end
    
    def self.create_category(args={})
      errors = ''
      query = "INSERT INTO storecategory (name, parentstorecategoryid) VALUES (?,?)"
      created = false
      begin
        db = Dbconn.get_instance 'master'
        stmt = db.prepare query
        stmt.execute args[:name], args[:parentcategory]
        id = stmt.insert_id
        created = stmt.affected_rows > 0
        self.flush_cache
      stmt.close
      rescue Exception => e
        errors = e.message
      end
      
      return id, errors
    end
    
    def self.update_category(args={})
      errors = ''
      query = "UPDATE storecategory SET name = ?, parentstorecategoryid = ? where id = ?"
      updated = false
      begin
        db = Dbconn.get_instance 'master'
      stmt = db.prepare query
      stmt.execute args[:name], args[:parentcategory], args[:id]
      throw "Unable to update category." if !(updated = stmt.affected_rows > 0)
      self.flush_cache
      stmt.close
      rescue Exception => e
        errors = e.message
      end
      
      return updated, errors
    end
    
    def self.get_item_id_list
        
        items = []
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT id FROM storeitem ORDER BY id ASC"
        stmt.execute
        
        while row = stmt.fetch do
            items << row[0]
        end
        
        stmt.free_result
        stmt.close
        
        return items
        
    end
    
    def self.flush_cache
        
        # flush cache; derived from MemcacheResetModel
            
        currency_list = self.get_currency_list
        item_type_list = [0,1,2,3,4,5]
        category_id_list = self.get_category_id_list
        item_id_list = self.get_item_id_list
        cache = CacheServer.get_instance
            
        currency_list.each{ |currency|
        
            item_type_list.each{ |item_type|
                cache.delete "Store/Featured/Type_#{item_type}/#{currency}"
                cache.delete "Store/New/Type_#{item_type}/#{currency}"
                cache.delete "Store/Free/Type_#{item_type}/#{currency}"
                cache.delete "Store/Popular/Type_#{item_type}/#{currency}"
                cache.delete "Store/All/Type_#{item_type}/#{currency}"
            }
                
            category_id_list.each{ |category_id|
                cache.delete "Store/ItemsInCategory/ID_#{category_id}/#{currency}"
                cache.delete "Store/Categories/ID_#{category_id}"
                cache.delete "Store/Category/Parent/ID_#{category_id}"
                cache.delete "Store/Categories/HasSub/ID_#{category_id}"
                cache.delete "Store/Category/ID_#{category_id}"
            }
                
        }   
            
        item_id_list.each{ |item_id|
            cache.delete "Store/Item/BreadCrumb/ID_#{item_id}"
        }
            
    end
    
    
end