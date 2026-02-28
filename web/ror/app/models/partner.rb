require 'date'

class Partner

    @id
    @name
    @admin
    @agreements
    @dateCreated

    attr_accessor :id, :name, :admin, :dateCreated, :agreements

    def initialize(args={})
        @id = args[:id] || nil
        @name = args[:name] || nil
        @admin = args[:admin] || nil
        @agreements = args[:agreements] || nil
        @dateCreated = DateTime.strptime(args[:dateCreated].to_s, '%Y-%m-%d %H:%M:%S').to_formatted_s(:long_ordinal) || nil
    end

    def self.find_all
        #
        # Get the list of Partners
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "SELECT p.Id as PartnerID, p.Name as PartnerName, p.DateCreated as PartnerDateCreated
                             FROM partner p
                         ORDER BY p.DateCreated DESC"
        stmt.execute
        
        partners = []
        
        while row = stmt.fetch do
            
            admin = Partner.find_admin_by_id(row[0])
            
            agreements = Agreement.find_by_partner_id(row[0])
        
            partner = Partner.new(
                    :id => row[0],
                    :admin => admin,
                    :agreements => agreements,
                    :name => row[1],
                    :dateCreated => row[2]
                )
        
            partners << partner
        end
        
        stmt.free_result
        stmt.close

        return partners
    end
    
    def self.find(partner_id)
        #
        # Get the list of Partners
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "SELECT p.Id as PartnerID, p.Name as PartnerName, p.DateCreated as PartnerDateCreated
                             FROM partner p
                            WHERE p.id = ?
                         ORDER BY p.DateCreated DESC"
        stmt.execute partner_id
        
        while row = stmt.fetch do
            
            agreements = Agreement.find_by_partner_id(row[0])
        
            partner = Partner.new(
                    :id => row[0],
                    :agreements => agreements,
                    :name => row[1],
                    :dateCreated => row[2]
                )
        end
        
        stmt.free_result
        stmt.close

        return partner
    end
    
    def self.is_admin(partner_id, user_id)
        #
        # Is admin?
        #
        db = Dbconn.get_instance('master')
        stmt = db.prepare "SELECT uid.username
                             FROM partneruser pu, userid uid
                            WHERE pu.PartnerID = ?
                              AND pu.UserID = ?
                              AND pu.Membership = 2"
        stmt.execute partner_id, user_id
        
        rows = stmt.num_rows
        
        stmt.free_result
        stmt.close
        
        return rows > 0
    end
    
    def self.is_member(partner_id, user_id)
        #
        # Is member?
        #
        db = Dbconn.get_instance('master')
        stmt = db.prepare "SELECT uid.username
                             FROM partneruser pu, userid uid
                            WHERE pu.PartnerID = ?
                              AND pu.UserID = ?"
        stmt.execute partner_id, user_id
        
        rows = stmt.num_rows
        
        stmt.free_result
        stmt.close
        
        return rows > 0
    end
    
    def self.find_admin_by_id(id)
        #
        # Find admin by partner ID
        #
    
        db = Dbconn.get_instance('master')
        stmt = db.prepare "SELECT uid.username
                             FROM partneruser pu, userid uid
                            WHERE pu.PartnerID = ?
                              AND pu.UserID = uid.id
                              AND pu.Membership = 2
                            LIMIT 1"
        stmt.execute id
        
        while row = stmt.fetch do
            admin = Mig33User.get(row[0])
        end
        
        stmt.free_result
        stmt.close
        
        return admin
    end
    
    def self.find_admins_by_id(id)
        #
        # Find admin by partner ID
        #
    
        db = Dbconn.get_instance('master')
        stmt = db.prepare "SELECT uid.username
                             FROM partneruser pu, userid uid
                            WHERE pu.PartnerID = ?
                              AND pu.UserID = uid.id
                              AND pu.Membership = 2"
        stmt.execute id
        
        admins = []
        while row = stmt.fetch do
            admin = Mig33User.get(row[0])
            
            admins << admin
        end
        
        stmt.free_result
        stmt.close
        
        return admins
    end
    
    def self.add_admin(partner_id, user_id)
        #
        # Add partner's admin
        #
        
        db = Dbconn.get_instance('master')
        stmt = db.prepare "INSERT INTO partneruser (PartnerID, UserID, Membership) VALUES (?, ?, 2)"
        stmt.execute partner_id, user_id
        
        stmt.close
    end
    
    def self.change_admin(partner_id, user_id)
        #
        # Change partner's admin
        #
        
        db = Dbconn.get_instance('master')
        stmt = db.prepare "UPDATE partneruser SET UserID = ? WHERE PartnerID =? AND Membership = 2"
        stmt.execute user_id, partner_id
        
        stmt.close
    end
    
    def self.remove_admin(partner_id, user_id)
        #
        # Remove partner's admin
        #
        
        db = Dbconn.get_instance('master')
        stmt = db.prepare "DELETE FROM partneruser WHERE UserID = ? AND PartnerID =? AND Membership = 2"
        stmt.execute user_id, partner_id
        
        stmt.close
    end
    
    def self.create(name)
        #
        # Create partner
        #
        
        db = Dbconn.get_instance('master')
        
        stmt = db.prepare "INSERT INTO partner (Name, DateCreated) VALUES (?, NOW())"
        stmt.execute name
        
        stmt.close
    end
    
    def show_status
        #
        # Show partner status
        # - Show "active" if there is an active agreement, else "inactive"
        #
        
        status = "Inactive"
        
        @agreements.each do |agreement|
            break status = "Active" if true
        end
        
        return status
    end
end