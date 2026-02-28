class Asset

    # note: this method is to be run on Mondays only, a day before the scheduled release day
    # this will create:
    #   - an sql file to be run on the release day: asset-<%Y-%m-%d>.sql
    #   - a rollback sql: rollback-<%Y-%m-%d>.sql
    #   - sql files for future releases: <%Y-%m-%d>.sql
    # all files are stored in: /var/www/htdocs/ror/lib/assets_sql
    
    def self.generate_cron
        
        # filename
        
        assetSqlDir = "/var/www/htdocs/ror/lib/assets_sql"
        releaseDay = (Date.today + 1).strftime('%Y-%m-%d')
        
        # avatar
        
        rollback = ''
        
        db = Dbconn.get_instance
        
        stmt = db.prepare "SELECT id, ownershiprequired, date_format(datelisted,'%Y-%m-%d') FROM avataritem WHERE status = 0 and dateListed > NOW()"
        stmt.execute
        
        while row = stmt.fetch do
            
            if row[2].eql? releaseDay
                writeFile = "asset-#{releaseDay}.sql"   
            else    
                writeFile = "#{row[2]}.sql"
            end
            
            # check if activate script of asset is already in place
            writeToFile = !(`grep "\#activate_2_#{row[0]}" #{assetSqlDir}/#{writeFile}`.strip.eql? "\#activate_2_#{row[0]}")     
            
            if writeToFile
            
                sql = ''
                
                if row[1] == 1
            
                    if row[2].eql? releaseDay
                        rollback << "UPDATE storeitem SET sortorder=sortorder-1 WHERE sortorder IS NOT NULL AND status=1 AND type=2;\n"
                        rollback << "UPDATE storeitem SET status=0, sortorder=0 WHERE referenceid = #{row[0]} AND type=2;\n" 
                    else
                        sql << "\#activate_2_#{row[0]}\n"
                    end
                    
                    sql << "UPDATE storeitem SET sortorder=sortorder+1 WHERE sortorder IS NOT NULL AND status=1 AND type=2;\n"
                    sql << "UPDATE storeitem SET status=1, sortorder=1 WHERE referenceid = #{row[0]} AND type=2;\n"
                
                end
            
                sql << "UPDATE avataritem SET status = 1 WHERE id=#{row[0]};\n\n"
                
                if row[2].eql? releaseDay
                    rollback << "UPDATE avataritem SET status = 0 WHERE id=#{row[0]};\n\n"
                end 
            
                File.open("#{assetSqlDir}/#{writeFile}", 'a+'){ |f|
                    f.write sql
                }
            end
            
        end
        
        stmt.free_result
        stmt.close
        
        # virtual gifts
        
        stmt = db.prepare "SELECT storeitem.id, virtualgift.id, virtualgift.numavailable, date_format(datelisted,'%Y-%m-%d') FROM virtualgift, storeitem WHERE virtualgift.id = storeitem.referenceid AND storeitem.status = 0 AND virtualgift.status = 0 AND storeitem.datelisted > NOW() AND storeitem.type = 1"
        stmt.execute
        
        while row = stmt.fetch do
        
            if row[2].eql? releaseDay
                writeFile = "asset-#{releaseDay}.sql"   
            else    
                writeFile = "#{row[3]}.sql"
            end
            
            # check if activate script of asset is already in place
            writeToFile = !(`grep "\#activate_1_#{row[0]}" #{assetSqlDir}/#{writeFile}`.strip.eql? "\#activate_1_#{row[0]}")     
            
            if writeToFile
                
                sql = ''
            
                if row[3].eql? releaseDay
                    rollback << "UPDATE virtualgift SET sortorder=sortorder-1 WHERE sortorder IS NOT NULL AND status=1;\n"
                    rollback << "UPDATE virtualgift SET status=0, sortorder=0 WHERE id = #{row[1]};\n"
                    rollback << "UPDATE storeitem SET sortorder=sortorder-1 WHERE sortorder IS NOT NULL AND status=1 AND type=1;\n"
                    rollback << "UPDATE storeitem SET status=0, sortorder=0 WHERE id = #{row[0]};\n\n"
                else
                    sql << "\#activate_1_#{row[0]}\n"
                end 
            
                sql << "UPDATE virtualgift SET sortorder=sortorder+1 WHERE sortorder IS NOT NULL AND status=1;\n"
                sql << "UPDATE virtualgift SET status=1, sortorder=1 WHERE id = #{row[1]};\n"
                sql << "UPDATE storeitem SET sortorder=sortorder+1 WHERE sortorder IS NOT NULL AND status=1 AND type=1;\n"
                sql << "UPDATE storeitem SET status=1, sortorder=1 WHERE id = #{row[0]};\n\n"
            
                File.open("#{assetSqlDir}/#{writeFile}", 'a+'){ |f|
                    f.write sql
                }
            
            end
            
        end
        
        stmt.free_result
        stmt.close
        
        # emoticon packs
        
        stmt = db.prepare "SELECT emoticonpack.id, storeitem.id, date_format(storeitem.dateListed,'%Y-%m-%d') FROM  emoticonpack, storeitem WHERE emoticonpack.id = storeitem.referenceid AND storeitem.status = 0 AND storeitem.datelisted > NOW()"
        stmt.execute
        
        while row = stmt.fetch do
        
            if row[2].eql? releaseDay
                writeFile = "asset-#{releaseDay}.sql"   
            else    
                writeFile = "#{row[2]}.sql"
            end
            
            # check if activate script of asset is already in place
            writeToFile = !(`grep "\#activate_3_#{row[1]}" #{assetSqlDir}/#{writeFile}`.strip.eql? "\#activate_3_#{row[1]}")     
            
            if writeToFile
            
                sql = ''
            
                if row[2].eql? releaseDay
                    rollback << "UPDATE emoticonpack SET sortorder=sortorder-1 WHERE sortorder IS NOT NULL AND status=1 AND forsale=1;\n"
                    rollback << "UPDATE emoticonpack SET status=0, sortorder=0, forsale=0 WHERE id=#{row[0]};\n"
                    rollback << "UPDATE storeitem SET sortorder=sortorder-1 WHERE sortorder IS NOT NULL AND status=1 AND type=3;\n"
                    rollback << "UPDATE storeitem SET status=0, sortorder=0 WHERE id=#{row[1]};\n\n"
                else
                    sql << "\#activate_3_#{row[1]}\n"
                end 
            
                sql << "UPDATE emoticonpack SET sortorder=sortorder+1 WHERE sortorder IS NOT NULL AND status=1 AND forsale=1;\n"
                sql << "UPDATE emoticonpack SET status=1, sortorder=1, forsale=1 WHERE id=#{row[0]};\n"
                sql << "UPDATE storeitem SET sortorder=sortorder+1 WHERE sortorder IS NOT NULL AND status=1 AND type=3;\n"
                sql << "UPDATE storeitem SET status=1, sortorder=1 WHERE id=#{row[1]};\n\n"
                
                File.open("#{assetSqlDir}/#{writeFile}", 'a+'){ |f|
                    f.write sql
                }

            end
        end
        
        stmt.free_result
        stmt.close
        
        if !rollback.empty?
            File.open("#{assetSqlDir}/rollback-#{releaseDay}.sql",'w'){ |f|
                f.write(rollback)
            }
        end 
        
        return File.open("#{assetSqlDir}/#{writeFile}",'r')
        
    end
    
    def self.get_log_files
        
        files = []
        @files = Dir.glob("#{ASSETS_SCRIPT_LOGS_DIRECTORY}/*.txt"){ |logFile|
            files.push File.open(logFile,'r') 
        }
        return files
    end
    
    def self.get_release_scripts
        
        releaseDay = (Date.today + 1).strftime('%Y-%m-%d')
        
        files = []
        @files = Dir.glob("#{ASSETS_RELEASE_SCRIPTS_DIRECTORY}/*.*"){ |logFile|
            files.push File.open(logFile,'r') if logFile.match(/(asset|rollback)-#{releaseDay}/) 
        }
        
        return files
        
    end
    
    def self.get_next_release_date
    
        nextReleaseDay = SystemProperty::get_int('AssetRelease', 2)
    
        releaseDate = Time.now
        releaseDate += (60 * 60 * 24) while releaseDate.wday != nextReleaseDay
        
        return releaseDate
        
    end
        
end