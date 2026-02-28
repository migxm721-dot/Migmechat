class Emoticon

  @id
  @name
  @shortName
  @type
  @images
  @attachment
  @emoticonPackId
  @emoticonPack
  
  @@imgDimType = {  
            'png' => [[12,12],[14,14],[16,16]],
            'gif' => [[12,12],[14,14],[16,16]] 
  }
  
  attr_accessor :name, :shortName, :type, :images, :attachment, :emoticonPackId, :emoticonPack, :id
  
  def initialize(args={})
    @name = args[:name]
    @shortName = args[:shortName]
    @type = args[:type]
    @images = args[:images] || {}
    @attachment = args[:attachment] || {}
    @emoticonPackId = args[:emoticonPackId]
    @id = args[:id]
  end

  def self.imgDimType
    return @@imgDimType
  end 
  
  def update_validation
    errors = {}
    return errors
  end
end