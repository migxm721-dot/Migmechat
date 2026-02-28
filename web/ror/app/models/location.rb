class Location
    
    @id
    @parentLocationId
    @countryId
    @name
    @level
    
    # derived
    @numLocation
    @countryName
    @numMerchant
    @path
    
    attr_accessor :id, :name, :parentLocationId, :countryId, :level, :numLocation, :countryName, :numMerchant, :path
    
    def initialize(args={})
        @id = args[:id] || nil
        @parentLocationId = args[:parentLocationId] || nil
        @countryId = args[:countryId] || nil
        @name = args[:name] || nil
        @level = args[:level] || nil
        @numLocation = args[:numLocation] || nil    
        @countryName = args[:countryName] || nil
        @numMerchant = args[:numMerchant] || nil    
        @path = args[:path] || nil
    end
    
    def create
    
        is_created = false
        error = ''
        
        begin
            db = Dbconn.get_instance 'master'
            stmt = db.prepare " INSERT INTO location (parentlocationid, countryid, name, level) 
                            VALUES (?, ?, ?, ?)"
            stmt.execute @parentLocationId, @countryId, @name, @level
            is_created = true
        rescue Exception => e
            error = e.message
        end
        
        return is_created, error
        
    end
    
    def update_attribute(field, val)
        
        errors = ''
        isSaved = false
        
        begin
            
            # TODO: logging
            db = Dbconn.get_instance 'master'
                
            stmt = db.prepare "UPDATE location SET #{field} = ? WHERE id = ?"
            stmt.execute val, @id
            stmt.close
            
            isSaved = true
                
        rescue Exception => e
            errors = "Error updating #{field}: #{e.message}"
        end
        
        return isSaved, errors
    end
    
    def delete
        
        errors = ''
        isDeleted = false
        
        begin
            
            # TODO: logging
            db = Dbconn.get_instance 'master'
                
            stmt = db.prepare "DELETE FROM location WHERE id = ?"
            stmt.execute @id
            stmt.close
            
            isDeleted = true
                
        rescue Exception => e
            errors = e.message
        end
        
        return isDeleted, errors
        
    end
    
    def self.exist?(name, parentLocationID=0, countryID=0) 
        
        location_id = nil
        
        db = Dbconn.get_instance
    
        if parentLocationID.nil?
            stmt = db.prepare " SELECT id 
                                FROM location 
                                WHERE countryid = ? 
                                AND parentlocationid IS NULL 
                                AND name = ? 
                                LIMIT 1"
            stmt.execute countryID, name
        else 
            stmt = db.prepare " SELECT id
                                FROM location 
                                WHERE parentlocationid = ? 
                                AND name = ? 
                                LIMIT 1"
            stmt.execute parentLocationID, name
        end
        
        while row = stmt.fetch do
            location_id = row[0]
        end
        
        stmt.free_result
        stmt.close
        
        return location_id.nil? ? false : location_id
    end
    
    def self.parents(id)
        
        parents = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  country.name AS countryname, 
                                    country.id AS countryid,        
                                    l1.id AS id1,   
                                    l1.name AS name1, 
                                    l2.id AS id2, 
                                    l2.name AS name2, 
                                    l3.id AS id3, 
                                    l3.name AS name3 
                            FROM country INNER JOIN location as l1 ON country.id=l1.countryid 
                                 LEFT JOIN location AS l2 ON l2.id = l1.parentlocationid 
                                 LEFT JOIN location AS l3 ON l3.id = l2.parentlocationid 
                            WHERE l1.id = ?"
        stmt.execute id
        
        while row = stmt.fetch do
            
            if !row[2].nil?
                parents << {
                    :id => row[2],
                    :name => row[3],
                    :country => row[0]
                }
                if !row[4].nil?
                    parents << {
                        :id => row[4],
                        :name => row[5],
                        :country => row[0]
                    }
                    if !row[6].nil?
                        parents << {
                            :id => row[6],
                            :name => row[7],
                            :country => row[0]
                        }
                    end 
                end
            end
        end     
        
        stmt.free_result
        stmt.close          
        
        return parents.reverse
    end
    
    def self.map_locations
    
        locations = {}
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  CASE 
                                        WHEN l1.name IS NOT NULL THEN l1.name
                                        WHEN l2.name IS NOT NULL THEN l1.name
                                        WHEN l3.name IS NOT NULL THEN l1.name
                                        ELSE country.name 
                                    END lname,
                                    country.name,
                                    country.id,                                     
                                    CASE 
                                        WHEN l3.id IS NOT NULL THEN l3.id
                                        WHEN l2.id IS NOT NULL THEN l2.id
                                        ELSE l1.id
                                    END id1,
                                    CASE 
                                        WHEN l3.name IS NOT NULL THEN l3.name
                                        WHEN l2.name IS NOT NULL THEN l2.name
                                        ELSE l1.name
                                    END name1,
                                    CASE 
                                        WHEN l3.id IS NOT NULL THEN l2.id
                                        WHEN l2.id IS NOT NULL THEN l1.id
                                        ELSE NULL
                                    END id2,
                                    CASE 
                                        WHEN l3.name IS NOT NULL THEN l2.name
                                        WHEN l2.name IS NOT NULL THEN l1.name
                                        ELSE NULL
                                    END name2,
                                    CASE 
                                        WHEN l3.id IS NOT NULL THEN l1.id
                                        ELSE NULL
                                    END id3,
                                    CASE 
                                        WHEN l3.name IS NOT NULL THEN l1.name
                                        ELSE NULL
                                    END name3
                            FROM country 
                            LEFT JOIN location as l1 ON country.id = l1.countryid
                            LEFT JOIN location AS l2 ON l2.id = l1.parentLocationID
                            LEFT JOIN location AS l3 ON l3.id = l2.parentLocationID
                            ORDER BY name, name1, name2, name3"
        stmt.execute
        
        while row = stmt.fetch do
            
            # add country
            if !locations.has_key? row[1]
                    
                locations[row[1]] = {
                    :details => {
                            :name => row[1],
                            :id => row[2]
                        },
                    :locations => {}    
                }
            end 
                
            # level 1   
            if row[5].nil? and !row[3].nil?
            
                locations[row[1]][:locations][row[4]] = {
                        :details => {
                                :name => row[4],
                                :id => row[3]
                            },
                        :locations => {}    
                    }
                
            # level 2
            elsif row[7].nil? and !row[5].nil?
            
                locations[row[1]][:locations][row[4]][:locations][row[6]] = {
                        :details => {
                                :name => row[6],
                                :id => row[5]
                            },
                        :locations => {}    
                    }
                
            #level 3
            elsif !row[7].nil?
            
                locations[row[1]][:locations][row[4]][:locations][row[6]][:locations][row[8]] = {
                        :details => {
                                :name => row[8],
                                :id => row[7]
                            },
                        :locations => {}    
                    }
                    
            end
        
        end
        
        stmt.free_result
        stmt.close                  
        
        return locations
        
    end
    
    def self.get_country_locations
        locations = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT country.id, 
                                   country.name countryname, 
                                   (SELECT COUNT(*) FROM location WHERE parentlocationid IS NULL AND countryid = country.id),
                                   IFNULL((SELECT COUNT(*)
                                    FROM location, merchantlocation 
                                    WHERE country.id = location.countryid 
                                    AND location.id = merchantlocation.locationid 
                                    GROUP BY country.id ),0)
                            FROM country  
                            ORDER BY countryname"
        stmt.execute
        
        while row = stmt.fetch do
            locations << {
                    :id => row[0],
                    :name => row[1],
                    :numLocation => row[2],
                    :numMerchant => row[3]
                }
        end 
        
        stmt.free_result
        stmt.close              
        
        return locations
    end
    
    def self.all(args={})
        return self.search args
    end
    
    def self.get_location_in_country(country_id)
        
        locations = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  country.name AS countryname,            
                                    country.id AS countryid,        
                                    location.id AS locationid,      
                                    location.name AS locationname, 
                                    location.level AS locationlevel, 
                                    COUNT(childlocation.id) AS haschildren,
                                    (SELECT COUNT(*) FROM merchantlocation WHERE locationid = location.id ) merchantNum     
                            FROM    country LEFT JOIN location ON country.id=location.countryid LEFT JOIN location AS childlocation ON childlocation.parentlocationid=location.id 
                            WHERE location.countryid = ? 
                            AND location.parentlocationid IS NULL       
                            GROUP BY countryname, countryid, locationid, locationname, locationlevel    
                            ORDER BY locationname"
        stmt.execute country_id 
        
        while row = stmt.fetch do
            location = Location.new(
                    :id => row[2],
                    :name => row[3],
                    :parentLocationId => nil,
                    :level => row[4],
                    :countryId => row[1],
                    :numLocation => row[5],
                    :countryName => row[0],
                    :numMerchant => row[6]
                )
            locations << location   
        end
        
        stmt.free_result
        stmt.close
        
        return locations
                        
    end
    
    def self.get_sub_locations(id)
        
        locations = []
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  a.id, 
                                    a.name, 
                                    a.level,
                                    a.parentlocationid,
                                    a.countryid,
                                    country.name,
                                    (SELECT COUNT(*) FROM location WHERE parentLocationID = a.id) childnum,
                                    (SELECT COUNT(*) FROM merchantlocation WHERE locationid = a.id) merchantNum
                            FROM location a, country 
                            WHERE a.countryid = country.id 
                            AND parentlocationid=?
                            ORDER BY a.name"
        stmt.execute id
        
        while row = stmt.fetch do
            location = Location.new(
                    :id => row[0],
                    :name => row[1],
                    :level => row[2],
                    :parentLocationId => row[3],
                    :countryId => row[4],
                    :countryName => row[5],
                    :numLocation => row[6],
                    :numMerchant => row[7]
                )   
            locations << location   
        end 
        
        stmt.free_result
        stmt.close
        
        return locations
        
    end
    
    def self.get(id)
        locations = self.search :id => id
        return locations.first unless locations.empty?
    end
    
    private
    
    def self.search(args)
    
        locations = []
        filters = []
        
        if !args[:id].nil?
            filters << " AND a.id = ?"
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare " SELECT  a.id, 
                                    a.name, 
                                    a.level,
                                    a.parentlocationid,
                                    a.countryid,
                                    country.name, 
                                    (SELECT COUNT(*) FROM location WHERE parentLocationID = a.id) childnum,
                                    (SELECT COUNT(*) FROM merchantlocation WHERE locationid = a.id) merchantNum,
                                    (SELECT CASE 
                                                WHEN l3.name IS NOT NULL AND l2.name IS NOT NULL THEN CONCAT_WS(' > ',l3.name, l2.name, l1.name)
                                                WHEN l2.name IS NOT NULL THEN CONCAT_WS(' > ',l2.name, l1.name)
                                                ELSE l1.name
                                            END path
                                     FROM location as l1
                                     LEFT JOIN location AS l2 ON l2.id = l1.parentLocationID
                                     LEFT JOIN location AS l3 ON l3.id = l2.parentLocationID
                                     WHERE l1.id = a.id) path
                            FROM location a, country 
                            WHERE a.countryid = country.id #{filters.join(' ')}
                            ORDER BY a.name"
        if filters.empty?
            stmt.execute
        else    
            stmt.execute *args.values
        end
        
        while row = stmt.fetch do
            location = Location.new(
                    :id => row[0],
                    :name => row[1],
                    :level => row[2],
                    :parentLocationId => row[3],
                    :countryId => row[4],
                    :countryName => row[5],
                    :numLocation => row[6],
                    :numMerchant => row[7],
                    :path => row[8]
                )   
            locations << location   
        end 
        
        stmt.free_result
        stmt.close              
        
        return locations
    
    end
    
    

end