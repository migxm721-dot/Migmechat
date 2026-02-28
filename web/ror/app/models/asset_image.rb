require "base64"
require "rubygems"
require "RMagick"

class AssetImage

  attr_accessor :file, :filename, :dim, :type, :webpath, :height, :width, :opts
  
  @@types = ['vg','em','av', 'st', 'st_pk', 'ci']
  
  def initialize(args={})
        @filename = args[:shortName]
        @file = args[:image]
        @type = args[:type]
        @dim = args[:dim]
        @height = args[:height]
        @width = args[:width]
        @opts = args[:opts] || {}
  end 

  def upload
    AssetImage.file_storage_location(@type, @opts).each{ |filestorage|
      if !@file.path.nil?

        begin
          filestorename = AssetImage.generate_file_name(@filename, @dim, @file.content_type, @height, @width, @type)

          # Read in the image from the temp directory
          tmpImg = File.open(@file.path)
          ilist = Magick::ImageList.new
          ilist.from_blob(tmpImg.read)
          tmpImg.close
          # Remove all profiles and comments from image
          ilist.strip!
          # Overwrite the tmp image
          ilist.write(@file.path)

          FileUtils.mkdir_p(filestorage)
          FileUtils.cp @file.path, File.join(filestorage, filestorename)

          FileUtils.chmod 0666, File.join(filestorage, filestorename)
          cmd = "cd #{filestorage}; git add #{filestorename}"
          Rails.logger.debug "adding asset to git using \n #{cmd}"
          system(cmd)

        rescue Exception => e
          Rails.logger.error "Unable to upload image to git filesystem \n #{e}"
        end

      else
        throw "Unable to upload to #{File.join(filestorage, filestorename)}. Source image is not available."
      end
    }

  end

  def filePath
    return @file.path
  end

  def isUploaded
    if !height.nil? and !width.nil?
          File.open(File.join(AssetImage.file_storage_location(@type, @opts), "#{@filename}_#{@width}x#{@height}.#{@file.content_type.split('/').pop.downcase}"), "r").nil? 
        else  
          File.open(File.join(AssetImage.file_storage_location(@type, @opts), "#{@filename}_#{@dim}.#{@file.content_type.split('/').pop.downcase}"), "r").nil? 
        end
    
  end
    
  def destroy(image_type)
    # we only accept gif and png at the moment
      if image_type.include? 'png'
        content_type = 'png'
      else
        content_type = 'gif'
      end   

    AssetImage.file_storage_location(@type, @opts).each{ |filestorage|
      filestorename = AssetImage.generate_file_name(@filename, @dim, content_type, @height, @width, @type)
      if File.exists? (File.join(filestorage, filestorename))
        File.delete(File.join(filestorage, filestorename))
        cmd = "cd #{filestorage}; git rm #{filestorename}"
        Rails.logger.debug "deleting asset from git using \n #{cmd}"
        system(cmd)
      else
        Rails.logger.debug("Unable to delete #{File.join(filestorage, filestorename)}. File not found.")
      end   
    }
  end
  
  def isPNG
      return (!@file.content_type.nil? and @file.content_type.downcase.include? 'png')
  end
  
  def isGIF
    return (!@file.content_type.nil? and @file.content_type.downcase.include? 'gif')
  end
    
  def rename_file(name, image_type, args={})
      
      # we only accept gif and png at the moment
      if /^.*png.*$/.match image_type
        content_type = 'png'
      else
        content_type = 'gif'
      end   

      oldfilename = AssetImage.generate_file_name(@filename, @dim, content_type, @height, @width, args[:type])
      newfilename = AssetImage.generate_file_name(name, @dim, content_type, @height, @width, args[:type])
      
      AssetImage.file_storage_location(@type, args).each{ |filestorage|
        if File.exists? (File.join(filestorage, oldfilename))
        Rails.logger.debug "renaming file in file system using \n mv #{File.join(filestorage, oldfilename)} #{File.join(filestorage, newfilename)}"
        FileUtils.mv(File.join(filestorage, oldfilename), File.join(filestorage, newfilename))
        cmd = "cd #{filestorage}; git rm #{oldfilename}; git add #{newfilename}"
        Rails.logger.debug "renaming git asset using : #{cmd}"
        system(cmd)
      else
        throw "Unable to move #{File.join(filestorage, oldfilename)} to #{File.join(filestorage, newfilename)} . File not found."
      end
    }
      
      @webpath = File.join(AssetImage.web_location(@type, args), newfilename)
    
  end
    
  def self.batch_delete(shortname, type)
      
      self.file_storage_location(type).each{ |filestorage|
        FileUtils.rm_f Dir.glob(File.join(filestorage, shortname, '*'))
      }
      
  end
    
  def self.isPNG(image)
      return image.content_type.split('/').pop.downcase.eql? 'png'
  end
    
  def self.isGIF(image)
      return image.content_type.split('/').pop.downcase.eql? 'gif'
  end
    
  def self.file_storage_location(type, args={})

  case type
    
    when 'vg'
          dir = [ File.join(ASSETS_DIRECTORY, 'images', 'emoticons', 'virtualgifts') ]
        when 'av'
          dir = [File.join(ASSETS_DIRECTORY, 'images', 'avatar')]  
        when 'ep'
          if args.has_key? 'epShortName'.to_sym
            dir = [ File.join(ASSETS_DIRECTORY, 'images', 'emoticons', args[:epShortName]) ]
          else
            throw "Emoticon pack shortname not supplied."
          end
        when 'st', 'st_pk'
      if args.has_key? 'epShortName'.to_sym
        dir = [ File.join(ASSETS_DIRECTORY, 'images', 'emoticons', 'stickers', args[:epShortName]) ]
      else
              throw "Sticker pack shortname not supplied."
            end
        when 'ci'
          dir = [ File.join(ASSETS_DIRECTORY, args["relpath"]) ]
        else
          throw "Invalid asset image type: #{type}" 
        end
        
        return dir

  end 
    
  def self.web_location(type, args = {})
      
    case type
    
      when 'vg'
        dir = '/images/assets/images/emoticons/virtualgifts'
        when 'av'
          dir = '/images/assets/images/avatar'
        when 'ep'
          if args.has_key? 'epShortName'.to_sym
            dir = "/images/assets/images/emoticons/#{args[:epShortName]}"
          else
            throw "Emoticon pack shortname not supplied."
        end
        when 'st', 'st_pk'
          if args.has_key? 'epShortName'.to_sym
            dir = "/images/assets/images/emoticons/stickers/#{args[:epShortName]}"
          else
            throw "Sticker pack shortname not supplied."
          end          
        end
        
      return dir
  end
    
  def self.find_upload_image(shortname, type, dim, content_type, args={}, height=nil, width=nil)
    filestorename = self.generate_file_name(shortname, dim, content_type, height, width, type)
    # get at least one directory
    filestorage = AssetImage.file_storage_location(type, args).shift
    img = File.open(File.join(filestorage, filestorename),"r") unless !File.exists? (File.join(filestorage, filestorename))

    if !img.nil?
      astImg = AssetImage.new(:shortName => shortname, :image => img, :dim => dim, :type => type, :opts => args, :height => height, :width => width)
      astImg.webpath = File.join(AssetImage.web_location(type, args), filestorename)
      return astImg
    else
      Rails.logger.debug "Unable to find upload image #{File.join(filestorage, filestorename)}"
      return nil
    end
      
  end
    
  def self.generate_file_name(name, dim, content_type, height=nil, width=nil, type=nil)
    # we only accept gif and png at the moment
    if content_type.include? 'png'
      content_type = 'png'
    else
      content_type = 'gif'
    end   
    
    if !height.nil? and !width.nil?
      filestorename = "#{name}_#{width}x#{height}.#{content_type}"
    elsif dim == 0
      filestorename = "#{name}.#{content_type}"
    else
      if !type.nil? and type = 'st_pk'
        # append preview for sticker pack images
        filestorename = "#{name}-preview_#{dim}.#{content_type}"
      else
        filestorename = "#{name}_#{dim}.#{content_type}"
      end
    end 
    return filestorename
    
  end
  
end
