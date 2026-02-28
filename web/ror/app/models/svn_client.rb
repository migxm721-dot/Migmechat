# TODO: modify as needed

class SvnClient

  @@status = {
    '?' => 'NEW FILE',
    'M' => 'UPDATED',
    'A' => 'FLAGGED FOR ADDITION',
    'D' => 'FLAGGED FOR DELETION',
    'C' => 'CONFLICTED STATE',
                '!' => 'FILE DELETED'
  }
  
  def initialize(working_copy = SVN_SETTINGS['svn_repo_working_copy'])
      @repository_base = SVN_SETTINGS['svn_repo_master']
      @working_copy = working_copy
      @svn = `which svn`.strip
      @svnadmin = `which svnadmin`.strip
    end
    
    def delete(file)
      `cd #{@working_copy} && #{@svn} delete #{file}`
    end
    
    def commit(message)
      `cd #{@working_copy} && #{@svn} commit -m "#{message}" --username #{SVN_SETTINGS['svn_user']} --password #{SVN_SETTINGS['svn_pass']} <<EOF\np\nEOF`
    end
 
    def add(file)
        `cd #{@working_copy} && #{@svn} add #{file}`
    end
 
    def update
        result = `cd #{@working_copy} && #{@svn} up --username #{SVN_SETTINGS['svn_user']} --password #{SVN_SETTINGS['svn_pass']} <<EOF\np\nEOF`
    end
 
    def dump(repository, dump_file_name)
        `#{@svnadmin} dump #{@repository_base}/#{repository} > #{dump_file_name}`
        $?.success?
    end
 
    def incremental_dump(repository, dump_file_name, from, to)
        `#{@svnadmin} dump #{@repository_base}/#{repository} --incremental -r#{from}:#{to} > #{dump_file_name}`
       $?.success?
    end
 
    def load(repository, dump_file_name)
        `#{@svnadmin} load #{@repository_base}/#{repository} < #{dump_file_name}`
        $?.success?
    end
 
    #def checkout(repository, working_copy_path)
    #   result = `cd #{working_copy_path} && #{@svn} co file:///#{@repository_base}/#{repository}`
    #   result[/d+/]
    #end
 
    def create(repository)
        `#{@svnadmin} create #{@repository_base}/#{repository}`
        $?.success?
    end
 
    def info
        result = `#{@svn} info #{@working_copy}`
        yaml = YAML.load(result)
        yaml['Revision'].to_s
    end
  
  # filepath should be relative to working copy
  
  def status(filepath='')
    if filepath.empty?
      result = `#{@svn} status #{@working_copy}`
      result = result.split(/\n/)
      files = {}
      result.each{ |str|
        /^(.*\s)(.*)/.match(str) 
        files[$2] = @@status[$1.strip]
      }
      return files
    else
      result = `#{@svn} status #{@working_copy}/#{filepath} | awk '{print $1}'` 
      return @@status[result.strip]
    end 
    
  end
  
end
