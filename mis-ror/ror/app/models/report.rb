class Report
    
    SQL_ADHOC_DIRECTORY = '/var/www/htdocs/ror/lib/sql/adhoc'
    SQL_MESSAGING_DIRECTORY = '/var/www/htdocs/ror/lib/sql/messaging'
    SQL_MARKETING_DIRECTORY = '/var/www/htdocs/ror/lib/sql/marketing'
    SQL_EMAIL_DIRECTORY = '/var/www/htdocs/ror/lib/sql/email'
    SQL_PAYMENT_DIRECTORY = '/var/www/htdocs/ror/lib/sql/payment'
    SQL_MERCHANT_ANALYTICS_DIRECTORY = '/var/www/htdocs/ror/lib/sql/merchant_analytics'
    SQL_VAS_METRICS_DIRECTORY = '/var/www/htdocs/ror/lib/sql/vas_metrics'
    MERCHANT_OPERATIONS_DIR = '/var/www/htdocs/ror/tmp/merchant_operations'
    REPORTS_DIR = '/var/www/htdocs/ror/tmp/reports/'
    QUERY_TIMEOUT = 1800 # 30 minutes
    CSV_MAX_ROWS = 1000
    HTML_MAX_ROWS = 500
    REPORT_AVAILABILITY = 1
    USER_REPORT_FILE = "reports"
    
    @type
    @name
    @parameters
    @description
    
    # derived
    @fields
    @fieldParams
    
    attr_accessor :type, :name, :parameters, :description, :fields
    
    def execute(args, username=nil)
        
        sql = ''
        is_retrieved = false
        response = ''
        db = 'qry_read'
        
        case @type  
        when 'adhoc'
            dir = SQL_ADHOC_DIRECTORY
        when 'messaging'
            dir = SQL_MESSAGING_DIRECTORY
        when 'marketing'
            dir = SQL_MARKETING_DIRECTORY
        when 'email'
            dir = SQL_EMAIL_DIRECTORY   
        when 'payment'
            dir = SQL_PAYMENT_DIRECTORY
        when 'merchant_analytics'
            dir = SQL_MERCHANT_ANALYTICS_DIRECTORY
            db = 'mis_hive'
        when 'vas_metrics'
            dir = SQL_VAS_METRICS_DIRECTORY
        end
        
        query_parameters = []
        
        @fieldParams.each{ |key|
            if @parameters[key].eql? 'required' and !['orderby', 'orderby1', 'orderby2', 'searchby'].include? key
                query_parameters << args[key]
            end
        }
        
        lines = ''
        begin
            File.open("#{dir}/#{@name}.sql", 'r').each{ |line|
                if !line.include? '#des ' and !line.include? '#req ' and !line.include? '#req2 ' and !line.include? '#opt ' and !line.include? '#var_column '
                    
                    # replace variable column name
                    # format *var_column-<column name>
                    line =~ /^.*\*var_column\-([a-zA-A\.0-9]+).*$/i
                    line = line.gsub("*var_column-#{$1}", args[$1]) unless $1.nil?
                    
                    # check for orderby
                    line =~ /^[\s]?ORDER BY[\s]+(\?)[\s]?.+$/i
                    if !$1.nil?
                        sql << " #{line.gsub('?',args['orderby'] || args['orderby1'] || args['orderby2'])}" 
                    else
                        # check if line is optional
                        line =~ /^#([a-zA-Z]+)\s(.*)$/i
                        
                        if !$1.nil?
                            app_sql_query = " #{$1} #{$2}"
                            app_sql = " #{$2}"
                            app_sql =~ /([a-zA-Z\.]+)\s?\=\s?\?/
                            if !args[$1].nil? and !args[$1].eql? ''
                                query_parameters << args[$1]
                                sql << app_sql_query
                            end
                        else
                            sql << " #{line}"
                        end
                    end
                end 
            }
        rescue Exception => e
            response = e.message
        end 
        
        if username.nil?
            t_result = Report.query(db, sql, query_parameters)
            @fields = t_result[:fields]
            response = t_result[:records]
            is_retrieved = t_result[:error].nil?
        else
            # database set to nil, will read from slave db
            Report.query_csv(db, sql, username, query_parameters, @name)
            is_retrieved = true
        end
    
        return is_retrieved, response
        
    end
    
    def self.get(report_type, report)
        
        case report_type    
        when 'adhoc'
            dir = SQL_ADHOC_DIRECTORY
        when 'messaging'
            dir = SQL_MESSAGING_DIRECTORY
        when 'marketing'
            dir = SQL_MARKETING_DIRECTORY   
        when 'email'
            dir = SQL_EMAIL_DIRECTORY
        when 'payment'
            dir = SQL_PAYMENT_DIRECTORY     
        when 'merchant_analytics'
            dir = SQL_MERCHANT_ANALYTICS_DIRECTORY
        when 'vas_metrics'
            dir = SQL_VAS_METRICS_DIRECTORY
        end
        
        return nil unless File.exists? "#{dir}/#{report}.sql"
        
        parameters = {}
        field_params = []
        description = ''
        
        File.open("#{dir}/#{report}.sql", 'r').each{ |line| 
            if line.include? '#des '
                description = line.gsub('#des ', '').strip
            elsif line.include? '#req '
                parameters[line.gsub('#req ', '').strip] = 'required'
                field_params << line.gsub('#req ', '').strip
            elsif line.include? '#req2 '    
                parameters[line.gsub('#req2 ', '').strip.concat('1')] = 'required'
                field_params << line.gsub('#req2 ', '').strip
            elsif line.include? '#opt '     
                parameters[line.gsub('#opt ', '').strip] = 'optional'
                field_params << line.gsub('#opt ', '').strip
            elsif line.include? '#var_column '
                parameters[line.gsub('#var_column ', '').strip] = 'optional'
                field_params << line.gsub('#var_column ', '').strip         
            end 
        }
        report = Report.new(
            :type => report_type,
            :name => report,
            :parameters => parameters,
            :description => description,
            :fieldParams => field_params
        )
        
        return report
    
    end
    
    def self.get_available_adhoc_reports
        return self.get_available_reports SQL_ADHOC_DIRECTORY
    end
    
    def self.get_available_messaging_reports
        return self.get_available_reports SQL_MESSAGING_DIRECTORY
    end
    
    def self.get_available_marketing_reports
        return self.get_available_reports SQL_MARKETING_DIRECTORY
    end
    
    def self.get_available_email_reports
        return self.get_available_reports SQL_EMAIL_DIRECTORY
    end
    
    def self.get_available_payment_reports
        return self.get_available_reports SQL_PAYMENT_DIRECTORY
    end
    
    def self.get_available_merchant_analytics_reports
        return self.get_available_reports SQL_MERCHANT_ANALYTICS_DIRECTORY
    end

    def self.get_available_vas_metrics_reports
        return self.get_available_reports SQL_VAS_METRICS_DIRECTORY
    end
    
    def self.query(database, query, args=[])
        result = run_query( database, query, nil, args )
        return result
    end
    
    def self.query_csv(database, query, username, args=[], label=nil)
        
        report_delim = "## REPORT: "
        
        result = {
            :filename => '',
            :error => nil 
        }
        
        begin
            
            # create user directory if it does not exist
            Dir.mkdir "#{REPORTS_DIR}#{username}" unless File.directory? "#{REPORTS_DIR}#{username}" 
            user_report_dir = "#{REPORTS_DIR}#{username}"
            filename = "#{Time.now.to_i}.csv"
            
            # append to report file the report
            description = label || query
            `echo "#{report_delim}#{DateTime.now.strftime("%Y-%m-%d %H:%M:%S")} - #{filename}\n#{description}" >> #{user_report_dir}/#{USER_REPORT_FILE}.tmp;`
            `cat #{user_report_dir}/#{USER_REPORT_FILE} >> #{user_report_dir}/#{USER_REPORT_FILE}.tmp; mv #{user_report_dir}/#{USER_REPORT_FILE}.tmp #{user_report_dir}/#{USER_REPORT_FILE}`
            `echo "Report is still being generated" > "#{user_report_dir}/#{filename}"`
            
            #Thread.new{ run_query(database, query, "#{user_report_dir}/#{filename}") }
            run_query(database, query, "#{user_report_dir}/#{filename}", args) 
            result[:filename] = filename
        
        rescue Exception => e
            result[:error] = e.message
        end
        
        return result
    end
    
    def self.explain_query(database, query)
        
        result = {
            :fields => [],
            :records => [],
            :error => nil
        }
        
        # cleanup query
        
        query =~ /^(\s*EXPLAIN)/
        
        query = "EXPLAIN EXTENDED #{query}" if $1.nil?

        begin
            db = Dbconn.get_instance database
            qry_result = db.query "#{query}"
            
            qry_result.fetch_fields.each{ |field|
                result[:fields] << field.name
            }
            
            qry_result.each{ |row| 
                result[:records] << row
            }
        rescue Exception => e
            result[:error] = e.message
        end
        
        return result
            
    end
    
    def self.user_reports(username)
        
        return [] unless File.exists? "#{REPORTS_DIR}#{username}/#{USER_REPORT_FILE}"
        report_start_regex = /## REPORT: ([0-9]{4}\-[0-9]{2}-[0-9]{2}\s[0-9]{2}:[0-9]{2}:[0-9]{2}) \- (.*)/
        
        reports = []
        report = nil
        
        # parse reports
        File.open("#{REPORTS_DIR}#{username}/#{USER_REPORT_FILE}", 'r').each{ |line|
            
            next if line.gsub(/\s+/,'').eql? "" or /\* MIS Report: (.*)\*/.match line
            if report_start_regex.match(line).nil?
                report['query'] << line
            else    
                reports << report unless report.nil? 
                report = {
                    'date' => $1,
                    'filename' => $2,
                    'query' => ''
                }
                report['query'] = ''
            end
            
        }
        
        # append last report
        reports << report unless report.nil?
        
        return reports
        
    end
    
    def self.merchant_operations
        
        daily_reports = {}
        Dir.new(MERCHANT_OPERATIONS_DIR).entries.delete_if{ |entry| ['.','..'].include? entry }.each{ |dt| 
            daily_reports[dt] = Dir.new(MERCHANT_OPERATIONS_DIR + '/' + dt).entries.delete_if{ |entry| ['.','..'].include? entry }.map{ |report|
                report = {  'report' => report,
                            'date' => dt
                         }
            }
        }
        
        return daily_reports
        
    end
    
    private
    
    def self.get_available_reports(directory)
        return Dir.new(directory).entries.delete_if{|entry| ['.','..'].include? entry }.map{|filename| filename.gsub('.sql','') }.sort
    end
    
    def initialize(args)
        @type = args[:type]
        @name = args[:name]
        @parameters = args[:parameters]
        @description = args[:description]
        @fieldParams = args[:fieldParams]
    end
    
    def self.run_query(database, query, filename=nil, qry_args=[])
        
        result = {
            :fields => [],
            :records => [],
            :error => nil
        }
        
        has_records = true  
        rows_per_page = filename.nil? ? HTML_MAX_ROWS : CSV_MAX_ROWS
        apply_limit = true
        
        # cleanup query
        query = query.gsub(/;\s*/,'')
        
        # check for queries not needing limit
        filter_regex = /^\s*(DESCRIBE|SHOW|EXPLAIN|DESC).*/i
        filter_regex.match query
        
        if $1.nil?
            
            # check query for limit
            query =~ /LIMIT\s+([0-9]+\s*,)?\s*([0-9]+)\s*$/i
            
            orig_rowsperpage = $2.to_i
            start = $1.nil? ? 0 : $1.gsub(',','').to_i
            
            if orig_rowsperpage > 0
                query = query.gsub(/LIMIT\s+([0-9]+\s*,)?\s*[0-9]+\s*$/i, 'LIMIT ?,?')
                rows_per_page = orig_rowsperpage if orig_rowsperpage < rows_per_page
            else
                query = "#{query} LIMIT ?,?"    
            end
            
        else
            apply_limit = false
        end
        
        begin
            
            if !filename.nil?
                require 'csv'
                report_csv = File.open("#{filename}.tmp", 'wb')
            end

            db = Dbconn.get_instance database
            stmt = db.prepare query 
            
            while has_records
        
                args = !apply_limit ? [] : [qry_args, start, rows_per_page].flatten 
                
                Timeout::timeout(QUERY_TIMEOUT){
                    stmt.execute *args
                }
                
                if stmt.num_rows
                
                    if filename.nil?
                        
                        result[:fields] = Dbconn.get_column_names(stmt) if start == 0
                        while row = stmt.fetch do
                          # we do this because the way ruby mysql handles float and v1.8.7 does not have round(4) ability (>=v1.9.0)
                            result[:records] << self.make_float_safe(row)
                        end 
                        
                    else
                        
                        CSV::Writer.generate(report_csv) do |csv|
                            if start == 0
                                column_names = Dbconn.get_column_names(stmt)
                                column_names[0] = column_names[0].gsub(/^ID/i,'.ID')
                                csv << column_names
                            end
                            while row = stmt.fetch do
                                csv << row
                            end
                        end     
                        
                    end
                    
                end
                
                # we limit the number of records for html view
                break if report_csv.nil? or !apply_limit
                
                start = start + rows_per_page
                
                if orig_rowsperpage <= start + rows_per_page and orig_rowsperpage > 0
                    rows_per_page = orig_rowsperpage % rows_per_page
                end
                
                has_records = false if stmt.num_rows == 0 or ( start >= orig_rowsperpage and orig_rowsperpage > 0 )
                stmt.free_result
                
            end
            
            # close file from being written
            report_csv.close unless filename.nil? 
            
        rescue Exception => e
            result[:error] = e.message
        
            # dump error in csv report
            report_csv.puts e.message unless report_csv.nil? 
        end

        stmt.free_result if stmt.class.to_s.downcase.eql? 'mysql::stmt'
        
        # replace temporary file 
        `mv #{filename}.tmp #{filename}`
        
        return result
    
end

def self.get_scheduled_reports(id=nil)
  
  result = {
      :fields => [],
      :records => [],
      :error => nil
    }
  query = "select * from reportschedule"
  if !id.nil?
      query = query + " where id = " + id
  end
    
  begin
      db = Dbconn.get_instance
      
      qry_result = db.query query
      qry_result.fetch_fields.each{ |field|
        result[:fields] << field.name
      }
      
      qry_result.each{ |row| 
        result[:records] << row
      }
  rescue Exception => e
      result[:error] = e.message
  end
  
  return result
end

def self.add_scheduled_reports(title,query,database,minute,hour,day_of_week,date_of_month,email_address,message,status,username)
  
  result = {:status => false, :error => nil, :id => nil}
  
  sql = "insert into reportschedule 
  (`query`, `database`, `minute`, `hour`, `date`, `day`, `title`, 
  `message`, `emailaddress`, `createdby`, `datecreated`, `status`)
  values 
  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?)"
  
  date = date_of_month == "" ? nil : date_of_month
  day = day_of_week == "" ? nil : day_of_week
  begin
      db = Dbconn.get_instance('master')
      stmt = db.prepare sql
      stmt.execute query,database,minute.to_i,hour.to_i,date,day,title,message,email_address,username,status
      result[:status] = true 
      id = stmt.insert_id
      result[:id] = id
      if status.to_i == 1
        self.add_cron(minute,hour,date_of_month,day_of_week,id)
      end
  rescue Exception => e
      result[:error] = e.message
  end
  
  stmt.close
  return result
end

def self.update_scheduled_reports(id,title,query,database,minute,hour,day_of_week,date_of_month,email_address,message,status)
  result = {:status => false, :error => nil}
  sql = "update reportschedule set
      `title` = ?, `database`= ?, `minute`= ?, `hour` = ?, `date` = ?, `day` = ?, `query` = ? , `message` = ?, `emailaddress` = ?,
      `status` = ? where `id` = ?"
  
  date = date_of_month == "" ? nil : date_of_month
  day = day_of_week == "" ? nil : day_of_week
  
  begin
      db = Dbconn.get_instance('master')
      stmt = db.prepare sql
      stmt.execute title,database,minute.to_i,hour.to_i,date,day,query,message,email_address,status,id
     
      result[:status] = true 
      if status.to_i == 1
        self.add_cron(minute,hour,date_of_month,day_of_week,id)
      elsif status.to_i == 0
        self.del_cron(id)
      end
      stmt.close
  rescue Exception => e
      result[:error] = e.message
  end
  
  
  return result
end

def self.delete_scheduled_reports(id)
  result = {:status => false, :error => nil}
  sql = "delete from reportschedule where `id` = ?"
  
  begin
      db = Dbconn.get_instance('master')
      stmt = db.prepare sql
      stmt.execute id
     
      result[:status] = true 
      self.del_cron(id)
      stmt.close
  rescue Exception => e
      result[:error] = e.message
  end
  
  return result
end

def self.del_cron(id)
  CronEdit::Crontab.Remove id
end

def self.add_cron(minute,hour,date_of_month,day_of_week,id)
  if date_of_month == '' || date_of_month.to_i == 99
    date_of_month = '*'
  end
  if day_of_week == '' || day_of_week.to_i == 9
    day_of_week = '*'
  end
  self.del_cron(id)
  
  CronEdit::Crontab.Add id, minute.to_s+' '+ hour.to_s+' '+date_of_month.to_s+' * '+day_of_week.to_s+' php ' +
       ASSETS_SCRIPT_DIRECTORY + '/report_writer_mailer.php ' + id.to_s + ' >> ' + ASSETS_SCRIPT_LOGS_DIRECTORY + '/reports_writer.log 2>&1'
  
end

private
def self.make_float_safe(row)
  vals = []
  
    row.each { | value |
      begin
        if(value.to_f.to_s == value.to_s)
          value = (value*10000).round/10000.to_f
        end
      rescue Exception => e
      end
      vals.push(value)
    }
  
  return vals
end

end