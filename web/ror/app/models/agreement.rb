class Agreement

    @id
    @partner
    @name
    @dateCreated
    @finderFee
    @revenueShare
    @startDate
    @endDate
    
    attr_accessor :id, :name, :dateCreated, :finderFee, :revenueShare, :startDate, :endDate
    
    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil
        @partner = args[:partner] || nil

        @finderFee = args[:finderFee] || nil
        @revenueShare = args[:revenueShare] || nil
        
        @startDate = args[:startDate] || nil
        @endDate = args[:endDate] || nil

        begin
            @dateCreated = DateTime.strptime(args[:dateCreated], "%m/%d/%Y").to_time unless args[:dateCreated].nil? 
        rescue ArgumentError => e
            @dateCreated = nil
        end
    end
    
    
    def self.create(args={})
        #
        # Create agreement
        #
    
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "INSERT INTO partneragreement 
                                       (Name, PartnerId, FinderFee, RevenueShare, StartDate, EndDate, DateCreated) 
                                VALUES (?, ?, ?, ?, ?, ?, NOW())"
        
        stmt.execute args[:name], args[:partnerId], args[:finderFee], args[:revenueShare], args[:startDate], args[:endDate]
                     
        @agreementId = stmt.insert_id
        
        stmt.close
    
        return @agreementId
    end
    
    
    def self.find_by_partner_id(partner_id)
        #
        # Find agreements associated with a partner
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "SELECT pa.Id, pa.Name, date_format(pa.DateCreated, '%m/%d/%Y') as AgreementDateCreated,
                                  pa.FinderFee, pa.RevenueShare, 
                                  date_format(pa.StartDate,'%m/%d/%Y') as AgreementStartDate, 
                                  date_format(pa.EndDate,'%m/%d/%Y') as AgreementEndDate
                             FROM partneragreement pa
                            WHERE pa.PartnerId = ?
                         ORDER BY pa.StartDate DESC"
        stmt.execute partner_id
        
        agreements = []
        
        while row = stmt.fetch do
        
            agreement = Agreement.new(
                            :id => row[0],
                            :name => row[1],
                            :dateCreated => row[2],
                            :finderFee => row[3],
                            :revenueShare => row[4],
                            :startDate => row[5],
                            :endDate => row[6]
                        )
                
            agreements << agreement
        end 
        
        stmt.free_result
        stmt.close  
        
        return agreements
    end
    
    
    def self.find(agreement_id)
        #
        # Find agreement
        #
        
        db = Dbconn.get_instance('master')
        stmt = db.prepare "SELECT pa.Id, pa.Name, date_format(pa.DateCreated, '%m/%d/%Y') as AgreementDateCreated,
                                  pa.FinderFee, pa.RevenueShare, 
                                  date_format(pa.StartDate,'%m/%d/%Y') as AgreementStartDate, 
                                  date_format(pa.EndDate,'%m/%d/%Y') as AgreementEndDate
                             FROM partneragreement pa
                            WHERE pa.Id = ?"
        stmt.execute agreement_id
        
        agreement = nil
        if row = stmt.fetch 
            agreement = Agreement.new(
                            :id => row[0],
                            :name => row[1],
                            :dateCreated => row[2],
                            :finderFee => row[3],
                            :revenueShare => row[4],
                            :startDate => row[5],
                            :endDate => row[6]
                        )
        end
        
        stmt.free_result
        stmt.close
        
        return agreement
    end
    
    def self.add_build(agreement_id, build_id)
        #
        # Add build to the agreement
        #
        
        db = Dbconn.get_instance('master')
        stmt = db.prepare "INSERT INTO partneragreementbuild (PartnerAgreementID, PartnerBuildID) 
                            SELECT ?, ? FROM DUAL  
                            WHERE NOT EXISTS 
                            (SELECT * FROM partneragreementbuild WHERE PartnerAgreementID = ? AND PartnerBuildID = ?)"
        stmt.execute agreement_id, build_id, agreement_id, build_id
        
        stmt.close
    end
    
    def self.remove_build(agreement_id, build_id)
        #
        # Remove build in the agreement
        #
        
        db = Dbconn.get_instance('master')
        stmt = db.prepare "DELETE FROM partneragreementbuild WHERE PartnerAgreementID=? AND PartnerBuildID=?"
        stmt.execute agreement_id, build_id
        
        stmt.close
    end
end