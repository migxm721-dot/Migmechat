class BannerAsset

  @platform
  @placement
  @size
  @img
  @url
  @ref_id

  attr_accessor :platform, :placement, :size, :img, :url, :ref_id
  
  def initialize(args={})
        @platform = args[:platform]
        @placement = args[:placement]
        @size = args[:size]
        @img = args[:img]
        @url = args[:url]
        @ref_id = args[:id]
  end

  def upload(ref_id)
    begin
      errors = {}
      @ref_id = ref_id
      db = Dbconn.get_instance('master')
      db.autocommit false
      db.query "begin"

      # insert into bannerassets table
      stmt = db.prepare "INSERT INTO bannerassets (`BannerID`, `platform`, `placement`, `size`, `imageUrl`, `url`) VALUES (?, ?, ?, ?, ?, ?)"
      stmt.execute @ref_id, @platform, @placement, @size, @img, @url
      @id = stmt.insert_id
      stmt.close

      db.commit
      is_created = true

    rescue Exception => e
      db.rollback unless db.nil?
      errors[:create] = e.message
      return false, errors
    end

  end

end
