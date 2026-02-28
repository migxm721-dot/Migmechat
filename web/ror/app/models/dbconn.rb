class Dbconn

    @@conn = {}
    
    def self.get_instance(dbName='olap')
    
        db_key = dbName.to_sym
        
        if DB_SETTINGS.has_key? db_key
        
            db_details = DB_SETTINGS[db_key]
            
            if !@@conn.has_key? db_key
                begin
                    if db_details.has_key? :port
                        @@conn[db_key] = Mysql.new(db_details[:host], db_details[:username], db_details[:password], db_details[:database], db_details[:port])
                    else
                        # check if report
                        if dbName.eql? 'qry_read'
                            db = Mysql.init
                            db.options(Mysql::OPT_READ_TIMEOUT, Report::QUERY_TIMEOUT)
                            @@conn[db_key] = db.connect(db_details[:host], db_details[:username], db_details[:password], db_details[:database])
                        else
                            @@conn[db_key] = Mysql.new(db_details[:host], db_details[:username], db_details[:password], db_details[:database])
                        end 
                    end     
                    @@conn[db_key].reconnect = true
                rescue Exception => e
                    throw e.message
                end 
            end
            
            return @@conn[db_key]
        
        else
            throw "MIS is unable to connect to database '#{dbName}'. Please contact the administrator."
        end 
        
    end
    
    def self.get_column_names(stmt)
        
        return stmt.result_metadata.fetch_fields.map{ |field| field.name } if stmt.class.to_s.downcase.eql? "mysql::stmt"
        
    end
    
    
    private 
    
    def initialize
    end
    
end
