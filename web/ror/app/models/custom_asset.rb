class CustomAsset

    @shortName
    @image
    @relativePath
    @fullPath
                        
    attr_accessor :shortName, :image, :relativePath, :fullPath
    
    def initialize(args={})
        @shortName = args[:shortName] || nil
        @image = args[:image]
        @relativePath = args[:relativePath] || nil
        @fullPath = args[:fullPath] || nil
    end 

end