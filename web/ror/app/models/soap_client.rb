class SoapClient
    
    @@instance = nil
    
    def self.get_instance
    
        if @@instance.nil?
            
            #Require The Library
            require 'soap/rpc/driver'
            
            begin
                #Connections
                @@instance = SOAP::RPC::Driver.new(SOAP_EJB_SERVICE_URL,SOAP_EJB_SERVICE_NAME)
            rescue Exception => e
                throw "Unable to connect to SOAP service: #{e.message}" 
            end
                
        end
        
        return @@instance
        
    end
    
    
    # function call
    # args must be of type array
    
    def self.call(method_name, args=[])
    
        if !args.class.to_s.downcase.eql? 'array'
            throw 'Invalid argument passed.'
        end
        
        if @@instance.nil?
            get_instance
        end
        
        success = false
        
        # on the fly adding of methods
        tmpCtr = 1
        @@instance.add_method(method_name, *args.map{ "arg_#{tmpCtr+=1}" } )
        
        begin
        
            response = @@instance.send(method_name, *args)
            
            # expect an array for non string or integer return from soap
            if response.class.to_s.downcase.eql? 'soap::mapping::object'
                t_response = response.item.first
                response = response.item
                #response = nil?
            elsif response.class.to_s.downcase.eql? 'array'
                t_response = response.first
            else
                t_response = response
            end
                    
            if t_response.nil? or t_response.eql? ''
                success = true
            else
                if !t_response.include? 'EJBException'  
                    success = true
                else
                    response = t_response.to_s.gsub!('EJBException:', '')   
                end
            end
            
        rescue Exception => e
            response = e.message
        end
        
        return success, response

    end
    
    private
    
    def initialize
    end
    
end