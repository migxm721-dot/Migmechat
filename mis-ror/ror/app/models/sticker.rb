class Sticker 

  @id
  @name
  @shortName
  @images
  @attachment
  @stickerPackId
  @stickerPackShortName
  
  attr_accessor :name, :shortName, :images, :attachment, :stickerPackId, :stickerPackShortName, :id
  
  def initialize(args={})
    @name = args[:name]
    @shortName = args[:shortName]
    @images = args[:images] || {}
    @attachment = args[:attachment] || {}
    @stickerPackId = args[:stickerPackId]
    @stickerPackShortName = args[:stickerPackShortName] || {}
    @id = args[:id]
  end
  
  def self.imgDimType
    return @@imgDimType
  end 
  
  private
  
  def update_validation
    errors = {}
    return errors
  end

  
end