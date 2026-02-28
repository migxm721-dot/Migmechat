class Vasworld

    # Attributes
    @id
    @partner
    @partnerId
    @name
    @content
    @status
    @remarks
    @dateCreated
    @dateUpdated
    
    attr_accessor :id, :partner, :name, :content, :status, :remarks, :dateCreated, :dateUpdated
    
    # Constant
    STATUS_DRAFT = 0
    STATUS_PENDING = 1
    STATUS_REJECTED = 2
    STATUS_CANCELLED = 3
    STATUS_WITHDREW = 4
    STATUS_APPROVED = 5
    STATUS_PUBLISHED = 6
    
    
    def initialize(args={})
        @id = args[:id] || nil
        @partnerId = args[:partnerId] || nil

        @partner = args[:partner] || nil
        @name = args[:name] || nil
        @content = args[:content] || nil
        @status = args[:status] || nil
        @remarks = args[:remarks] || nil
        
        @dateCreated = DateTime.strptime(args[:dateCreated].to_s, '%Y-%m-%d %H:%M:%S').to_formatted_s(:long_ordinal) || nil 
        @dateUpdated = DateTime.strptime(args[:dateUpdated].to_s, '%Y-%m-%d %H:%M:%S').to_formatted_s(:long_ordinal) || nil 
    end
    
    
    def show_status
        #
        # Show status message
        #
        
        case @status
            when STATUS_DRAFT
                status = 'Draft'    
            when STATUS_WITHDREW
                status = 'Withdrew'
            when STATUS_CANCELLED
                status = 'Cancelled'
            when STATUS_PENDING
                status = 'Pending Approval'
            when STATUS_REJECTED
                status = 'Rejected'
            when STATUS_APPROVED
                status = 'Approved'
            when STATUS_PUBLISHED
                status = 'Published'
        end 
        
        return status
    end
    
    def self.create(args={})
        #
        # Create a VASWorld
        #
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "INSERT INTO partnervasworld 
                             (Name, Content, Status, DateCreated, DateUpdated, Remarks, PartnerAgreementID) 
                             VALUES (?, ?, ?, NOW(), NOW(), ?, ?)"
        stmt.execute args[:name], args[:content], args[:status], args[:remarks], args[:agreementId]
        
        @vasworldId = stmt.insert_id    
            
        stmt.close
        
        return @vasworldId
    end
    
    def self.list(status)
        #
        # Select a list of VASWorld
        #

        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "SELECT pvw.Id, pvw.Name, pvw.Content, pvw.Status, pvw.Remarks, 
                                  pvw.DateCreated, pvw.DateUpdated, 
                                  p.Id as PartnerID, p.Name as PartnerName, p.DateCreated as PartnerDateCreated 
                             FROM partnervasworld pvw, partneragreement pa, partner p 
                            WHERE p.Id = pa.PartnerId 
                              AND pa.Id = pvw.PartnerAgreementId
                              AND pvw.Status = ? 
                         ORDER BY pvw.DateUpdated DESC"
        stmt.execute status
        
        vasworlds = []
        
        while row = stmt.fetch do
        
            partner = Partner.new(
                    :id => row[7],
                    :name => row[8],
                    :dateCreated => row[9]
                )
        
            vasworld = Vasworld.new(
                    :id => row[0],
                    :partner => partner,
                    :name => row[1],
                    :content => row[2],
                    :status => row[3],
                    :remarks => row[4],
                    :dateCreated => row[5],
                    :dateUpdated => row[6]
                )
            
            vasworlds << vasworld
        end
        
        stmt.free_result
        stmt.close
        
        return vasworlds
    end
    
    def self.past(limit=10)
        #
        # List past approved/rejected VASWorld
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "SELECT pvw.Id, pvw.Name, pvw.Content, pvw.Status, pvw.Remarks, 
                                  pvw.DateCreated, pvw.DateUpdated, 
                                  p.Id as PartnerID, p.Name as PartnerName, p.DateCreated as PartnerDateCreated 
                             FROM partnervasworld pvw, partneragreement pa, partner p 
                            WHERE pa.Id = pvw.PartnerAgreementId 
                              AND (pvw.Status = ? OR pvw.Status = ?)
                              AND pa.PartnerId = p.Id 
                         ORDER BY pvw.DateUpdated DESC"
        stmt.execute STATUS_APPROVED, STATUS_REJECTED
        
        vasworlds = []
        
        while row = stmt.fetch do
        
            partner = Partner.new(
                    :id => row[7],
                    :name => row[8],
                    :dateCreated => row[9]
                )
        
            vasworld = Vasworld.new(
                    :id => row[0],
                    :partner => partner,
                    :name => row[1],
                    :content => row[2],
                    :status => row[3],
                    :remarks => row[4],
                    :dateCreated => row[5],
                    :dateUpdated => row[6]
                )
            
            vasworlds << vasworld
        end
        
        stmt.free_result
        stmt.close
    
        return vasworlds
    end
    
    def self.find(id)
        #
        # Get a VASWorld
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "SELECT pvw.Id, pa.PartnerId, pvw.Name, pvw.Content, pvw.Status, pvw.Remarks, 
                                  pvw.DateCreated, pvw.DateUpdated 
                             FROM partnervasworld pvw, partneragreement pa 
                            WHERE pvw.Id = ? AND pa.Id = pvw.PartnerAgreementId"
        stmt.execute id
        
        while row = stmt.fetch do
            vasworld = Vasworld.new(
                    :id => row[0],
                    :partner_id => row[1],
                    :name => row[2],
                    :content => row[3],
                    :status => row[4],
                    :remarks => row[5],
                    :dateCreated => row[6],
                    :dateUpdated => row[7]
                )
        end     
        
        stmt.free_result
        stmt.close
        
        return vasworld
    end
    
    def self.approve(id)
        #
        # Approve pending VASWorld
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "UPDATE partnervasworld pvw 
                              SET status = ?
                            WHERE pvw.Id = ?"
        
        stmt.execute STATUS_APPROVED, id
        stmt.close
    end
    
    def self.reject(id)
        #
        # Reject pending VASWorld
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "UPDATE partnervasworld pvw 
                              SET status = ?
                            WHERE pvw.Id = ?"
        
        stmt.execute STATUS_REJECTED, id
        stmt.close
    end
end

