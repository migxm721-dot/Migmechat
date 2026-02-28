class CoverPhoto
    
    @userid
    @coverPhotos

    attr_reader :coverPhotos

    def initialize(args={})
        @userid = args[:userid]
        @coverPhotos = args[:coverPhotos] || []
    end

    def self.get(userid)
        restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/user/' + userid.to_s + '/coverphoto')
        begin
            jsonCovers = JSON.parse(restClient.get)
        rescue Exception => e
            return CoverPhoto.new(:coverPhotos=>[])
        end
        return CoverPhoto.new(:coverPhotos=>jsonCovers['data'])
    end

    def custom
        @coverPhotos.each do |cover|
            if cover['type'] == 2
                return cover
            end
        end
        return nil
    end
    def selected
        @coverPhotos.each do |cover|
            if cover['selected']
                return cover
            end
        end
    end
    def has_custom?
        @coverPhotos.each do |cover|
            if cover['type'] == 2
                return true
            end
        end
        return false
    end
    def has_selected?
        @coverPhotos.each do |cover|
            if cover['selected']
                return true
            end
        end
        return false
    end
    def custom_selected?
        return has_selected? && has_custom? && custom['url'] == selected['url']
    end
    
end