class StickerPack

    @id
    @name
    @shortName
    @dateRelease
    @type
    @category
    @currency
    @price
    @stickers
    @stickersCount
    @images
    @attachment
    
    attr_accessor :name, :shortName, :dateRelease, :type, :category, :currency, :price, :stickers, :stickersCount, :id, :images, :attachment
    
    @@dirPermissions = 0777
    
    @@imgDimType = { 'png' => [[16,16],[24,24],[36,36],[48,48],[64,64],[72,72],[96,96],[300,100],[650,350]] }
    
    # category is hard-coded in script parse-ep.php
    @@categories = {
        82 => 'Other',
        83 => 'Animal',
        84 => 'Food',
        85 => 'Doodle',
        86 => 'Supernatural',
        87 => 'Creature',
        88 => 'Sports',
        89 => 'Characters'
    }

    def initialize(args={})
        @name = args[:name]
        @shortName = args[:shortName]
        @dateRelease = DateTime.strptime(args[:dateRelease], "%m/%d/%Y").to_time unless args[:dateRelease].nil?
        @type = args[:type]
        @category = args[:category]
        @currency = args[:currency]
        @price = args[:price]
        @stickers = args[:stickers] || []
        @stickersCount = args[:stickersCount] || 0
        @id = args[:id]
        @images = args[:images] || {}
        @attachment = args[:attachment] || {}
    end
    
    def view
        @errors = {}
        @sticker_pack = StickerPack.find_in_upload_list(:shortName => params[:id])
        if @sticker_pack.nil?
            @errors[:nilStickerPack] = 'Sticker pack does not exist.'
        else
            @categories = getCategories
        end 
    end
    
    def dateRelease=(releaseDate)
        releaseDate = DateTime.strptime(releaseDate, "%m/%d/%Y").to_time unless releaseDate.nil?
        @dateRelease = releaseDate
    end
    
    def self.categories
        return getCategories
    end
    
    def get_stickers
        
        db = Dbconn.get_instance('olap')
        
        find_sticker_aliases = db.prepare "SELECT DISTINCT alias FROM emoticon WHERE emoticonpackid = ?"
        find_sticker_aliases.execute @id

        while st_alias = find_sticker_aliases.fetch do
            stmt = db.prepare "SELECT id, alias, locationPNG, width, height FROM emoticon WHERE emoticonpackid = ? AND alias = ? LIMIT 1"
            stmt.execute @id, st_alias[0]

            row = stmt.fetch
            tmpName = row[2].split('/').pop.gsub(/(_(.+)?\.png)/,'')
            img = {
                :id => row[0],
                :name => row[1],
                :shortName => tmpName,
                :image => AssetImageHelper::find_upload_image(@shortName,'st', "#{row[3]}x#{row[4]}", 'png', {'hotkey' => tmpName})
            }
            @stickers.push img
            stmt.free_result
            stmt.close
        end

        find_sticker_aliases.free_result
        find_sticker_aliases.close
        
        return @stickers
        
    end
    
  def self.search_user_sticker_purchased(args={})
    
        username = args[:username]
        stickerPack = args[:stickerPack]
        dateFrom = args[:dateFrom]
        dateTo = args[:dateTo]
        
        db = Dbconn.get_instance
        
        result = []
        qryArgs = [27]
        
        qry = "SELECT accountentry.username, country.name, COUNT(*) as purchaseCount FROM emoticonpackowner, accountentry, emoticonpack, country, user WHERE accountentry.reference = emoticonpackowner.emoticonpackid AND user.countryId = country.id AND user.username = emoticonpackowner.username AND accountentry.type = ? AND accountentry.username = emoticonpackowner.username AND emoticonpack.id = accountentry.reference"

        if !username.empty?
            qry = "#{qry} AND accountentry.username = ? "
            qryArgs.push username
        end 
        
        if !stickerPack.empty?
            qry = "#{qry} AND emoticonpack.name = ? "   
            qryArgs.push stickerPack
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
    
    def self.imgDimType
        return @@imgDimType
    end 

    def self.getCategories

        db = Dbconn.get_instance('master')

        stmt = db.prepare "SELECT id, name FROM storecategory WHERE parentstorecategoryid = 6"
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
