class Banner

  # Banner object
  @name
  @startDate
  @endDate
  @assets
  @id

  attr_accessor :name, :startDate, :endDate, :assets, :id

  def initialize(args={})
      @name = args[:name]
      @startDate = DateTime.strptime(args[:startDate], "%m/%d/%Y").to_time unless args[:startDate].nil?
      @endDate = DateTime.strptime(args[:endDate], "%m/%d/%Y").to_time unless args[:endDate].nil?
      @assets = args[:assets] || nil
      @id = args[:id]
  end

  def create
    is_created = false
    # validate
    errors = create_validation

    if errors.empty?
      begin
        db = Dbconn.get_instance('master')
        db.autocommit false
        db.query "begin"

        # insert into banner table
        stmt = db.prepare "INSERT INTO banner (`Name`, `StartDate`, `EndDate`) VALUES (?, ?, ?)"
        stmt.execute @name, @startDate.strftime('%Y-%m-%d'), @endDate.strftime('%Y-%m-%d')
        @id = stmt.insert_id
        stmt.close

        db.commit

        @assets.each{ |key,asset|
          if asset != nil
            Rails.logger.debug "uploading banner asset #{key}"
            asset.upload(id)
          end
        }

        is_created = true
          
        rescue Exception => e
          
          db.rollback unless db.nil?
          errors[:create] = e.message
          return false, nil, errors
        
        end 
      end
      
      return is_created, @id, errors 
  end
    
  def destroy
    
    errors = {}
      
    begin
      
      db = Dbconn.get_instance('master')
      db.autocommit false
      db.query "begin"

      stmt = db.prepare "DELETE FROM 
                          bannerassets
                         WHERE 
                          bannerid = ?"
      stmt.execute @id
      stmt.close

      stmt = db.prepare "DELETE FROM 
                          banner
                         WHERE 
                          id = ?"
      stmt.execute @id 
      stmt.close
      
      db.commit 
        
    rescue Exception => e
      
      db.rollback unless db.nil?
      
      errors[:destroy] = e.message
      return false, errors
    
    end
    
    return true, errors     
  end
    
  def self.all
  end
  
  def self.find(args={})
  end
  
  def self.all(args={})
    return self.search args
  end
  
  def self.find(args={})
    banners = self.search args
    return banners.shift unless banners.empty?
  end

  private
  
  def self.search(args={})
    banners = []
    assets = []
  
    db = Dbconn.get_instance
  
    if args[:id].nil?
      
      stmt = db.prepare "SELECT
                           id,  
                           name, 
                           date_format(startdate,'%m/%d/%Y'), 
                           date_format(enddate,'%m/%d/%Y') 
                         FROM 
                           banner"
      stmt.execute
    else
      stmt = db.prepare "SELECT 
                           id,
                           name,
                           date_format(startdate,'%m/%d/%Y'),
                           date_format(enddate,'%m/%d/%Y')
                         FROM 
                           banner
                         WHERE 
                           id = ?"
      stmt.execute args[:id]

      getAssets = db.prepare "SELECT 
                           platform,
                           placement,
                           size,
                           imageUrl,
                           url
                         FROM 
                           bannerassets
                         WHERE 
                           bannerid = ?"
      getAssets.execute args[:id]

      while row = getAssets.fetch do
        asset = BannerAsset.new(:platform => row[0],
                                :placement => row[1],
                                :size => row[2],
                                :img => row[3],
                                :url => row[4])
        assets.push asset
      end

      getAssets.free_result
      getAssets.close

    end 

    while row = stmt.fetch do
      banner = Banner.new(:id => row[0],
                          :name => row[1], 
                          :startDate => row[2], 
                          :endDate => row[3],
                          :assets => assets)
      banners.push banner
    end

    stmt.free_result
    stmt.close
    
    return banners
  end
  
  # validations
  def create_validation

    errors = {}

    # Check that the start date is not in the past
    if (Date.parse(@startDate.strftime('%Y-%m-%d')) < Date.today) 
      errors[:invalid_date] = "Start date is invalid. Please select a current or future date for Start Date"
    end

    # Check that the end date is not in the past
    if (Date.parse(@endDate.strftime('%Y-%m-%d')) < Date.today) 
      errors[:invalid_date] = "End date is invalid. Please select a current or future date for End Date"
    end

    # Check that the start date is prior to end date
    if (Date.parse(@startDate.strftime('%Y-%m-%d')) > Date.parse(@endDate.strftime('%Y-%m-%d')))
      errors[:invalid_date] = "Please ensure that the start date is prior to the end date"
    end

    # check if name is already in use
    db = Dbconn.get_instance

    stmt = db.prepare "SELECT name FROM banner WHERE name = ? "
    stmt.execute @name

    if stmt.num_rows > 0
      errors[:name] = "Banner labeled '#{@name}' already exists. Please use another."
    end

    stmt.free_result
    stmt.close

    if @assets.nil? 
      errors[:no_banner] = "A banner must be created for at least one platform"
    end

    return errors
  
  end
    
  def startDate=(dateStart)
      dateStart = DateTime.strptime(dateStart, "%m/%d/%Y").to_time unless dateStart.nil?
      @startDate = dateStart
  end

  def endDate=(dateEnd)
      dateEnd = DateTime.strptime(dateEnd, "%m/%d/%Y").to_time unless dateEnd.nil?
      @endDate = dateEnd
  end

end
