class Avatar

    @id
    @name
    @shortName
    @currency
    @price
    @dateRelease
    @category
    @type
    @gender
    @usedOnBody
    @storeCategory
    @zOrder
    @images
    @revenueShare
    
    
    @@imgDimType = {    'png' => [0,24,48,68,96] }
    
    # category is hard-coded in script parse_avatar.php
    @@categories = {
        7 => 'tank-top',
        9 => 'short-sleeves',
        10 => 'long-sleeves',
        11 => 'coat',
        12 => 'jeans',
        13 => 'pants',
        14 => 'shorts',
        15 => 'skirt',
        16 => 'suit',
        17 => 'dress',
        18 => 'male-shoes',
        19 => 'female-shoes',
        20 => 'dog',
        21 => 'cat',
        22 => 'eyes',
        23 => 'eyebrows',
        24 => 'nose',
        25 => 'hair',
        26 => 'mouth',
        28 => 'musical',
        29 => 'hats',
        31 => 'seasons',
        32 => 'jewelry',
        33 => 'bags',
        34 => 'architecture',
        35 => 'sports',
        36 => 'zodiac',
        37 => 'places',
        38 => 'others',
        39 => 'others-accessories',
        40 => 'others-pets',
        41 => 'interior',
        42 => 'football',  
        43 => 'skin-color',
        44 => 'face-paint' 
    }
    
    # store category is hard-coded in script parse_avatar.php
    @@store_categories = {
        33 => 'top',
        34 => 'face', 
        32 => 'background',
        35 => 'bottom',
        36 => 'full',
        37 => 'accessories',
        38 => 'shoes',
        39 => 'pets',
        41 => 'football',
        43 => 'special'
    }
    
    # type is hard-coded in script parse_avatar.php
    @@types = {
        1 => 'eyebrows',
        2 => 'eyes',
        3 => 'hair',
        4 => 'mouth',
        5 => 'nose',
        6 => 'pants',
        7 => 'shoes',
        8 => 'shirts',
        9 => 'background',
        10 => 'accessories',
        11 => 'pets',
        12 => 'carry-in-hands',
        13 => 'subtitles-on-top',
        14 => 'decorations',
        15 => 'skin-color',
        16 => 'face-paint'
    }
    
    # zorder is hard-coded in script parse_avatar.php
    @@zorder = {
        600 => 'body-part',
        700 => 'hair',
        1100 => 'shirt-above',
        1000 => 'pants',
        900 => 'shirt-below',
        100 => 'background',
        200 => 'behind-body',
        2000 => 'in-front-of-body',
        2100 => 'in-front-of-body-1',
        2200 => 'in-front-of-body-2',
        2300 => 'in-front-of-body-3',
        2400 => 'in-front-of-body-4',
        2500 => 'in-front-of-body-5',
        1200 => 'accessories',
        800 => 'shoes',
        550 => 'skin-color',
        580 => 'face-paint',
        130 => 'behind-body-3',
        150 => 'behind-body-2',
        170 => 'behind-body-1',
        2200 => 'in-front-of-body-1',
    }
    
    # gender
    @@gender = {
        1 => 'male',
        2 => 'female',
        3 => 'both'
    }

    
    attr_accessor :name, :shortName, :currency, :price, :dateRelease, :category, :type, :gender, :usedOnBody, :storeCategory, :zOrder, :images, :revenueShare
    
    def initialize(args={})
        @id = args[:id]
        @name = args[:name]
        @shortName = args[:shortName]
        @dateRelease = DateTime.strptime(args[:dateRelease], "%m/%d/%Y").to_time unless args[:dateRelease].nil?
        @price = args[:price]
        @category = args[:category]
        @currency = args[:currency]
        @type = args[:type]
        @gender = args[:gender]
        @usedOnBody = args[:usedOnBody]
        @storeCategory = args[:storeCategory]
        @zOrder = args[:zOrder]
        @images = args[:images] || {}
        @revenueShare = args[:revenueShare] || nil
    end
    
    def dateRelease=(releaseDate)
        releaseDate = DateTime.strptime(releaseDate, "%m/%d/%Y").to_time unless releaseDate.nil?
        @dateRelease = releaseDate
  end
    
  def self.search_user_purchase_avatar_count(args)
        
        username = args[:username]
        avatarItem = args[:avatarItem]
        dateFrom = args[:dateFrom]
        dateTo = args[:dateTo]
        
        db = Dbconn.get_instance
        
        result = []
        qryArgs = [32]
        
        qry = "SELECT user.username, country.name, COUNT(*) as avatarItemCount FROM avataruseritem, userid, accountentry, user, country, avataritem WHERE avataruseritem.userid = userid.id AND accountentry.username = userid.username AND accountentry.type = ? AND accountentry.reference = avataruseritem.avataritemid AND user.username = userid.username AND country.id = user.countryID AND avataruseritem.avataritemid = avataritem.id "

        if !username.empty?
            qry = "#{qry} AND accountentry.username = ? "
            qryArgs.push username
        end 
        
        if !avatarItem.empty?
            qry = "#{qry} AND avataritem.name = ? "     
            qryArgs.push avatarItem
        end
        
        if !dateFrom.empty?
            qry = "#{qry} AND accountentry.datecreated >= ? "
            qryArgs.push DateTime.strptime(dateFrom, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        end
        if !dateTo.empty?
            qry = "#{qry} AND accountentry.datecreated <= ? "
            qryArgs.push DateTime.strptime(dateTo, "%m/%d/%Y").to_time.strftime("%Y-%m-%d")
        end
        
        qry = "#{qry} GROUP BY username"
        
        stmt = db.prepare qry
        if qryArgs.empty?
            stmt.execute
        else    
            stmt.execute *qryArgs       
        end
        
        while row = stmt.fetch do
            rowResult = { :username => row[0], :country => row[1], :purchaseCount => row[2] }
            result.push rowResult
        end
        
        stmt.free_result
        stmt.close
        
        return result

    end
    
    def self.all_in_upload_list(args={})
        return self.search_in_upload_list args
    end
    
    def self.find_in_upload_list(args={})
        avatars = self.search_in_upload_list args
        return avatars.shift unless avatars.empty?
    end
    
    def self.imgDimType
        return @@imgDimType
    end
    
    def self.gender
        return @@gender
    end
    
    def self.categories
        return @@categories
    end 
    
    def self.store_categories
        return @@store_categories
    end
    
    def self.types
        return @@types
    end 
    
    def self.zorder
        return @@zorder
    end
    
end
