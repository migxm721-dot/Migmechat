class MigboPost
    
    @id
    @author
    @dateCreated
    @content
    @status
    @reports
    @photo
    @type
    
    attr_accessor :id, :author, :dateCreated, :content, :status, :reports, :photo, :type
    
    DELETED = 0
    ACTIVE = 1
    REPORTED = 2
    
    def initialize(args={})
        @id = args[:id]
        @author = args[:author]
        begin
            @dateCreated = Time.at(args[:dateCreated]/1000).strftime("%b %d, %Y %H:%M")
        rescue ArgumentError => e
            @dateCreated = nil
        end
        @content = args[:content]
        @status = args[:status]
        @reports = args[:reports]
        @photo = args[:photo]
        @type = args[:type]
    end
    
    def live?
        return @status == ACTIVE
    end
    
    def deleted?
        return @status == DELETED
    end
    
    def reported?
        return @status == REPORTED
    end
    
    def self.remove(id)
        restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/post/' + id + '/report/review?action=accept')
        begin
            jsonReject = JSON.parse(restClient.post :id => id)
            if jsonReject["error"]
                return false, jsonReject["error"]["message"]
            end
        rescue Exception => e
            return false, e.message
        end
        return true, ""
    end
    
    def self.recover(id)
        restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/post/' + id + '/report/review?action=reject')
        begin
            jsonRecover = JSON.parse(restClient.post :id => id)
            if jsonRecover["error"]
                return false, jsonRecover["error"]["message"]
            end
        rescue Exception => e
            return false, e.message
        end
        return true, ""
    end
    
    def self.search(args={})
        posts = []
        numRecords = 0
        searchQuery = args.map{ |key, value| 
            if key.eql? :page 
                value = value.to_i - 1
            end
            "#{key}=#{value}" 
        }.join("&")
        
        restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/search/posts/reported?' + searchQuery)
        begin
            jsonStrPosts = JSON.parse(restClient.get)
            tPosts = jsonStrPosts["data"]["search"]["result"]
            
            tPosts.each{ |post| post.map { |id, attributes|
                reports = []
                if attributes.has_key? "reporters"
                    attributes["reporters"].each{ |reporter|
                        jsonReporter = JSON.parse(reporter)
                        reports << MigboPostAbuseReport.new(
                            :user => Mig33User.new(
                                            :username => jsonReporter["username"],
                                            :type => jsonReporter["type"]),
                            :dateCreated => jsonReporter["timestamp"]
                        )
                    }
                end
                posts << MigboPost.new(
                    :id => id,
                    :author => attributes["author_username"],
                    :dateCreated => attributes["post_timestamp"],
                    :content => attributes["body"],
                    :status => attributes["status"],
                    :reports => reports
                )
            }}
            
            numRecords = jsonStrPosts["data"]["search"]["totalHits"]
        rescue Exception => e
            raise "Unable to retrieve reported posts: #{e.message}"
        end 
        
        return posts, numRecords
 
    end

    def self.delete(postIds, userId)
        result = {:posts => {}}
        ids = postIds.split(',')
        ids.each { |postId|
            restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/post/' + postId + '?requestingUserid=' + userId.to_s)
            begin
                result[:posts][postId] = JSON.parse(restClient.delete)
            rescue Exception => e
                result[:posts][postId] = 'failed'
            end
        }
        return result
    end

    def self.userFeed(args={})
        posts = []
        searchQuery = args.map{ |key, value|
            if key.eql? :offset
                value = (value.to_i - 1) * args[:limit].to_i
            end
            if key.eql? :author
                nil
            else
                "#{key}=#{value}"
            end
        }.compact.join('&')
        
        restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/user/' + args[:author].to_s + '/posts?requestingUserid=' + args[:author].to_s + '&' + searchQuery)
        begin
            jsonStrPosts = JSON.parse(restClient.get)
            tPosts = jsonStrPosts["data"]

            typeenum = {1=>'text',2=>'link',4=>'photo',8=>'video',16=>'rss',32=>'activity',64=>'game'}
            tPosts.each { |post|
                posts << MigboPost.new(
                    :id => post["id"],
                    :author => post["author"]["username"],
                    :dateCreated => post["timestamp"].to_i,
                    :content => post["body"],
                    :status => post["status"],
                    :photo => post["photo"] || nil,
                    :type => typeenum[post["type"]]
                )
            }
        rescue Exception => e
            raise "Unable to retrieve miniblog posts: #{e.message}"
        end
        return posts
    end
    
end