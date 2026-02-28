class MigLevel
    
    @score
    @level
    @name
    @image
    
    attr_accessor :score, :level, :name, :image
    
    def initialize(args={})
        @score = args[:score]
        @level = args[:level]
        @name = args[:name]
        @image = args[:image]
    end
    
    def self.get_level(score)
        
        level = 1
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT IF(MAX(level) IS NULL, 1, MAX(level)) level, name, image FROM reputationscoretolevel WHERE score <= ?"
        stmt.execute score
        
        if !stmt.rowcount
            throw "Undefined level"
        end
        
        while row = stmt.fetch do
            level = MigLevel.new(
                        :score => score,
                        :level => row[1],
                        :name => row[2],
                        :image => row[3]
                    )
        end
        
        stmt.free_result
        stmt.close
        
        return level
        
    end
    
    def self.get_levels()
        
        levels = {}
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT score, level, name, image FROM reputationscoretolevel"
        stmt.execute
        
        while row = stmt.fetch do
            levels[row[1]] = MigLevel.new(
                        :score => row[0], 
                        :level => row[1],
                        :name => row[2],
                        :image => row[3]
                    )
        end
        
        stmt.free_result
        stmt.close
        
        return levels
        
    end
    
    def self.get_level_ranges(rangesize)
      
      ranges = {}
      totallevels = 0
      
      db = Dbconn.get_instance
    stmt = db.prepare "SELECT count(distinct level) FROM reputationscoretolevel" # table has duplicate entries
    stmt.execute
    
    while row = stmt.fetch do
      totallevels =  row[0]
    end
    
    numgroups = (totallevels / rangesize).to_i
    overflow = (totallevels % rangesize).to_i
    for i in 0..(numgroups-1)
      min = (i * rangesize) + 1
      max = (i + 1) * rangesize
      ranges[i] = { "min" => min, "max" => max, "text" => "#{min} - #{max}" }
    end
    
    if (overflow > 0) 
      ranges[numgroups] = { "min" => ranges[numgroups-1]["max"] + 1, "max" => ranges[numgroups-1]["max"] + overflow, "text" => "#{ranges[numgroups-1]["max"] + 1} and above" }
    end
    
    stmt.free_result
    stmt.close
    
    return ranges
    end
end