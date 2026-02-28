# memcache
class CacheServer

    @@server_instance = nil
    
    @@key_space = {
        'ACCOUNT_BALANCE' => "BAL",
        'ALERT_MESSAGE' => "AM",
        'BLOCK_LIST' => "BL",
        'CHATROOM' => "CHR",
        'CHATROOM_BAN' => "CHB",
        'CHATROOM_MODERATORS' => "CRM",
        'CHATROOM_BANNED_USERS' => "CRBU",
        'CONTACT_GROUP' => "CGR",
        'CONTACT_LIST_VERSION' => "CLVV",
        'CURRENCY' => "CUR",
        'DID_NUMBER' => "DID",
        'DISTRIBUTED_LOCK' => "LOCK",
        'EMOTICON_PACKS_OWNED' => "EPO",
        'FAILED_LOGIN_ATTEMPTS' => "FLA",
        'GLOBAL_COLLECT_REPORT_FEED' => "GCRF",
        'GROUP' => "GRP",
        'GROUP_ANNOUNCEMENT' => "GA",
        'GROUP_INVITATION' => "GI",
        'LOGIN_BAN' => "LB",
        'NUM_VIRTUAL_GIFTS_RECEIVED' => "NVGR",
        'RECENT_CHATROOM_COUNT' => "RCC",
        'REPUTATION_LEVEL' => "RL",
        'SCRAPBOOK' => "SB",
        'SYSTEM_PROPERTY' => "SYS",
        'USER_ID' => "UID",
        'USER_PROFILE' => "UP",
        'USER_PROFILE_STATUS' => "UPS",
        'USER_SETTING' => "US",
        'MERCHANT_TAG' => "MTT",
        'FLOOD_CONTROL' => "FC",
        'PERMA_BAN' => "PBFC"}
    
    def self.get_instance
        
        if @@server_instance.nil?
            
            require 'memcache'
            
            MEMCACHE_SERVER_ADDRESSES.each{ |server|
                @@server_instance = ActiveSupport::Cache::MemCacheStore.new(server)
                break if !@@server_instance.nil?    
            }
            
        end
        
        return @@server_instance
    end
    
    def close
        if @@server_instance.nil? is false
            @@server_instance.close
        end
    end
    
    def self.key_space(key)
        return @@key_space.has_key?(key) ? @@key_space[key] : nil 
    end
    
end