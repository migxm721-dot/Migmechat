class Criteria 
    include Mixin::HashInitializer
    attr_accessor :CountryID, :AllCountries, :Status, :Type

    def get
        return self.get_condition_array.join(' AND ')
    end

    def get_condition_array
        condition_array = []
        if(!self.AllCountries && !self.CountryID.empty?)
            condition_array.push(self.get_country_id_condition)
        end

        if(!self.Status.empty?)
            condition_array.push(self.get_status_condition)
        end

        if(!self.Type.empty?)
            condition_array.push(self.get_type_condition)
        end
        return condition_array
    end

    def get_country_id_condition
        escaped_string = Mysql::escape_string self.CountryID.join(',')
        return "CountryID in (#{escaped_string})"
    end

    def get_status_condition
        escaped_string = Mysql::escape_string self.Status
        return "Status = #{escaped_string}"
    end

    def get_type_condition
        escaped_string = Mysql::escape_string self.Type
        return "Type = #{escaped_string}"
    end
    
    
end