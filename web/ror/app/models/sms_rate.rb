class SmsRate

    @countryId
    @rate
    @currency
    
    
    DEFAULT_CURRENCY = 'AUD'
    
    def buzz
    end
    
    def lookout
    
    end
    
    def email_alert
    
    end
    
    def group_notification
    
    end
    
    def self.get_system_rate_cost(sms_type)
        
        db = Dbconn.get_instance "  SELECT country.iddcode, smswholesalecost.areacode, country.name
                                    FROM smswholesalecost, smsroute, country, smsgateway
                                    WHERE smsroute.iddcode = country.iddcode
                                    AND smsroute.gatewayid = smswholesalecost.gatewayid
                                    AND smswholesalecost.gatewayid = smsgateway.id"     
    end

end