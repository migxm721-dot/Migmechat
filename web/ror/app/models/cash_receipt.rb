class CashReceipt
    
    @id
    @providerTransactionId
    @senderUsername
    @mobilePhone
    @enteredBy # staff who entered the cash receipt
    @dateCreated
    @dateReceived
    @dateMatched
    @amountSent
    @amountReceived
    @amountCredited
    @type
    @paymentDetails
    @comments
    @status
    @matchedBy
    @owner  # staff who matched cash receipt
    @bonus
    @referenceCashReceiptId
    
    # non-db - derived
    @country
    @countryId
    
    @@payment_detail_types = {
        0 => '',
        1 => 'Telegraphic Transfer',
        2 => 'Local Bank HDFC',
        3 => 'Western Union',
        4 => 'Money Bookers',
        5 => 'Paypal',
        #6 => 'Adjustment Credits', remove by NMIS-201 
        7 => 'Advance Credits',
        #8 => 'Free Credits', remove by NMIS-201
        9 => 'Corporate Billing',
        10 => 'Local Bank GC',
        11 => 'Credit Card Bonus',
        12 => 'Reimbursement Loss Currency',
        13 => 'Reimbursement Transfer Fee',
        14 => 'Payment Bonus',
        15 => 'Adjustment Bonus',
        16 => 'Internal Cash Receipts',
        17 => 'Payment Bonus',
        18 => 'Adjustment Bonus'
    }
    
    
    @@types = {
        0 => 'Telegraphic Transfer',
        1 => 'Direct',
        2 => 'Western Union'
    }

    
    @@status = {
        0 => 'Unmatched',
        1 => 'Matched',
        2 => 'Deleted', 
        3 => 'Reversed'  
    }
    
    attr_accessor :id, :providerTransactionId, :senderUsername, :mobilePhone, :enteredBy, :dateCreated, :dateReceived, :dateMatched, :amountSent, :amountReceived, :amountCredited, :type, :paymentDetails, :comments, :status, :owner, :country, :matchedBy, :bonus, :referenceCashReceiptId, :countryId

    
    def initialize(args={})
        
        @id = args[:id] || nil
        @providerTransactionId = args[:providerTransactionId] || nil
        @senderUsername = args[:senderUsername] || nil
        @mobilePhone = args[:mobilePhone] || nil
        @enteredBy = args[:enteredBy] || nil
        begin
            @dateCreated = DateTime.strptime(args[:dateCreated], "%m/%d/%Y").to_time unless args[:dateCreated].nil?
        rescue
            @dateCreated = nil
        end
        begin
            @dateReceived = DateTime.strptime(args[:dateReceived], "%m/%d/%Y").to_time unless args[:dateReceived].nil?
        rescue
            @dateReceived = nil
        end
        begin
            @dateMatched = DateTime.strptime(args[:dateMatched], "%m/%d/%Y").to_time unless args[:dateMatched].nil?
        rescue
            @dateMatched = nil
        end
        @amountSent = args[:amountSent] || nil
        @amountReceived = args[:amountReceived] || nil
        @amountCredited = args[:amountCredited] || nil
        @type = args[:type] || nil
        @paymentDetails = args[:paymentDetails] || nil
        @comments = args[:comments] || nil
        @status = args[:status] || nil
        @owner = args[:owner] || nil
        @matchedBy = args[:matchedBy] || nil
        @bonus = args[:bonus] || nil
        @referenceCashReceiptId = args[:referenceCashReceiptId] || nil
    
        # non-db - derived
        @country = args[:country] || nil
        @countryId = args[:countryId] || nil
        
    end
    
    def self.payment_detail_types
        return @@payment_detail_types
    end
    
    def self.display_type type
        return @@types[type] if @@types.has_key? type
    end
    
    def self.display_status status
        return @@status[status] if @@status.has_key? status
    end
    
    def self.status
        return @@status
    end
    
    def self.transaction_reference_id_exists(transaction_id)
    
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT providerTransactionID FROM cashreceipt WHERE providerTransactionId = ? AND status NOT IN (2,3)"
        stmt.execute transaction_id
        
        if stmt.num_rows > 0 
            stmt.free_result
            stmt.close
            return true
        else
            stmt.free_result
            stmt.close
            return false    
        end
        
    end
    
    def create

        @dateCreated ||= DateTime.now
        restClient  = RestClient::Resource.new(FUSION_REST + "/account/cashreceipt/create")
        begin 
            response = JSON.parse(restClient.post get_json_payload(), :content_type => :json, :accept => :json)

            Rails.logger.debug(response)
            if response["error"].nil?
                @id = response["data"]["id"]
                return true, "Cash receipt successfully created"
            else
                return false, response['error']['message']
            end

        rescue Exception => e

            return false, e.message;
        end
        
    end
    @@types = {
        0 => 'Telegraphic Transfer',
        1 => 'Direct',
        2 => 'Western Union'
    }

    def get_json_payload
        return { 
            :data => {
                :id => @id,
                :dateCreated => @dateCreated.strftime('%Y-%m-%d'),
                :enteredBy   => @enteredBy,
                :dateReceived=> @dateReceived.strftime('%Y-%m-%d'),
                :amountSent  => @amountSent,
                :amountReceived => @amountReceived,
                :amountCredited => @amountCredited,
                :matchedBy => @matchedBy,
                :dateMatched => @dateMatched,
                :senderUsername => @senderUsername,
                :providerTransactionID => @providerTransactionId,
                :paymentDetails => @paymentDetails,
                :comments => @comments,
                :status => @status,
                :type => @@types.invert[@type]
            }
        }.to_json
    end
    
    def delete
    
        is_deleted = false
        error = ""
        
        begin
        
            db = Dbconn.get_instance 'master'
            stmt = db.prepare "DELETE FROM cash_receipt WHERE ID = ?"
            stmt.execute @id
            stmt.close
            
            is_deleted = true
            
        rescue Exception => e
            
            error = e.message
        end
        
        return is_deleted, error
        
    end
    
    def match

        restClient  = RestClient::Resource.new(FUSION_REST + "/account/cashreceipt/match")
        begin 
            response = restClient.post get_json_payload(), :content_type => :json, :accept => :json

            if response.length < 2 # empty response means success, we have response, but its empty..
                return true, "Cash receipt successfully matched"
            else
                response = JSON.parse(response)
                if response["error"].nil?
                    return true, "Cash receipt successfully matched"
                end
                return false, response['error']['message']
            end

        rescue Exception => e

            return false, e.message;
        end
        
    end
    
    def dateReceived=(dateReceived)
        @dateReceived = DateTime.strptime(dateReceived, "%m/%d/%Y").to_time unless dateReceived.nil?
    end
    
    
    def self.summary
        
        summary = {
            'matched' => 0,
            'unmatched' => 0,
            'deleted' => 0
        }
        
        db = Dbconn.get_instance
        
        
        stmt = db.prepare "SELECT COUNT(id) AS 'Unmatched', (SELECT COUNT(id) FROM cashreceipt WHERE status = 1 AND referenceCashReceiptId IS NULL) AS 'Matched', (SELECT COUNT(id) FROM cashreceipt WHERE status = 2 AND referenceCashReceiptId IS NULL) AS 'Deleted' FROM cashreceipt WHERE status = 0 AND referenceCashReceiptId IS NULL"
        stmt.execute
        
        while row = stmt.fetch do
            summary = {
                'unmatched' => row[0],
                'matched' => row[1],
                'deleted' => row[2]
            }
        end
        
        stmt.free_result
        stmt.close
        
        return summary
        
    end
    
    def self.get_transaction(id)
    
        transactions = []
        db = Dbconn.get_instance

        stmt = db.prepare "SELECT 
                            id,
                        date_format(datecreated,'%m/%d/%Y'), 
                        status, 
                        date_format(datematched,'%m/%d/%Y'),
                        matchedby, 
                        senderusername, 
                        status, 
                        mobilephone, 
                        bonus,
                        enteredby, 
                        datereceived, 
                        amountsent, 
                        amountreceived, 
                        amountcredited, 
                        type, 
                        providertransactionid, 
                        paymentdetails, 
                        comments,
                        country
                       FROM
                           (SELECT 
                            a.*,
                          country.name as country
                         FROM 
                          cashreceipt a JOIN user ON a.senderUsername = user.Username JOIN country ON user.countryID = country.id
                         WHERE a.referencecashreceiptid IS NULL
                          AND a.id = ?
                         UNION 
                         SELECT
                          b.*,
                          country.name
                         FROM 
                          cashreceipt b JOIN user ON b.senderUsername = user.Username JOIN country ON user.countryID = country.id
                         WHERE
                          b.referencecashreceiptid = ?) trsn 
                       ORDER BY id"
                        
        stmt.execute id, id
        
        transaction = {
                'id' => 0,
                'dateCreated' => nil,
                'senderUsername' => nil,
                'mobilePhone' => nil,
                'country' => nil,
                'amountReceived' => 0,
                'amountCredited' => 0,
                'bonus' => nil,
                'status' => nil,
                'cash_receipts' => Array.new,
                'matchedBy' => nil,
                'dateMatched' => nil
            }
        row = stmt.fetch
            
        transactions << transaction unless transaction['id'] == 0
        dateMatched = DateTime.strptime(row[3], "%m/%d/%Y").to_time unless row[3].nil?
        transaction = {
            'id' => row[0],
            'dateCreated' => DateTime.strptime(row[1], "%m/%d/%Y").to_time,
            'senderUsername' => row[5],
            'mobilePhone' => row[7],
            'country' => row[18],
            'amountReceived' => 0,
            'amountCredited' => 0,
            'bonus' => row[8],
            'status' => row[6],
            'cash_receipts' => Array.new,
            'matchedBy' => row[4],
            'dateMatched' => dateMatched
        }
            
    begin
            cash_receipt = CashReceipt.new(
                :id => row[0],
                :providerTransactionId => row[15],
                :enteredBy => row[9],
                :dateCreated => row[1],
                :dateReceived => row[10],
                :amountSent => row[11],
                :amountReceived => row[12],
                :amountCredited => row[13],
                :type => row[14],
                :paymentDetails => row[16],
                :comments => row[17])
            
            transaction['cash_receipts'] << cash_receipt
            transaction['amountReceived'] = transaction['amountReceived'] + row[12].to_f
            transaction['amountCredited'] = transaction['amountCredited'] + row[13].to_f
        end while row = stmt.fetch
        
        transactions << transaction
        
        stmt.free_result
        stmt.close
        
        return transactions
    
    end
    
    def self.list_cash_receipts(args)
    
        search_by = args[:searchBy] || 'datecreated'
        order_by = args[:orderBy] || 'dateCreated'
        filterColumns = []
        filters = [args[:dateFrom],args[:dateTo]]
        transactions = []
        
        if args.has_key? :providerTransactionId  and !args[:providerTransactionId].empty?
            filterColumns << 'AND a.providerTransactionId = ?'
            filters << args[:providerTransactionId]
        end
        
        if args.has_key? :senderUsername and !args[:senderUsername].empty?
            filterColumns << 'AND a.senderUsername = ?'
            filters << args[:senderUsername]
        end
        
        if args.has_key? :status and args[:status].to_i >= 0
            filterColumns << 'AND cashreceipt.status = ?'
            filters << args[:status]
        end
        
        db = Dbconn.get_instance 
        stmt = db.prepare " SELECT a.id, 
                                   date_format(a.datecreated,'%m/%d/%Y'), 
                                   a.status, 
                                   date_format(a.datematched,'%m/%d/%Y'),
                                   a.matchedby, 
                                   a.senderusername, 
                                   a.status, 
                                   a.mobilephone, 
                                   a.bonus, 
                                   b.id, 
                                   b.datecreated, 
                                   b.enteredby, 
                                   b.datereceived, 
                                   b.amountsent, 
                                   b.amountreceived, 
                                   b.amountcredited, 
                                   b.type, 
                                   b.providertransactionid, 
                                   b.paymentdetails, 
                                   b.comments,
                                   country.name
                            FROM cashreceipt a LEFT JOIN cashreceipt b ON ( a.id = b.id AND b.referencecashreceiptid IS NULL) OR a.id = b.referencecashreceiptid LEFT OUTER JOIN USER ON a.senderUsername = USER.Username LEFT OUTER JOIN COUNTRY ON USER.countryID = COUNTRY.ID
                            WHERE a.referencecashreceiptid IS NULL
                            AND Date(b.#{search_by}) >= ? AND Date(b.#{search_by}) <= adddate(?, 1)
                            #{filterColumns.join(' ')}
                            ORDER BY a.id, b.id ASC"
        
        stmt.execute *filters
        
        transaction = {
                'id' => 0,
                'dateCreated' => nil,
                'senderUsername' => nil,
                'mobilePhone' => nil,
                'country' => nil,
                'amountReceived' => 0,
                'amountCredited' => 0,
                'bonus' => nil,
                'status' => nil,
                'cash_receipts' => Array.new,
                'matchedBy' => nil,
                'dateMatched' => nil
            }
        
        while row = stmt.fetch do
            
            if transaction['id'].to_i != row[0]
                
                transactions << transaction unless transaction['id'] == 0
                dateMatched = DateTime.strptime(row[3], "%m/%d/%Y").to_time unless row[3].nil?
                transaction = {
                    'id' => row[0],
                    'dateCreated' => DateTime.strptime(row[1], "%m/%d/%Y").to_time,
                    'senderUsername' => row[5],
                    'mobilePhone' => row[7],
                    'country' => row[20],
                    'amountReceived' => 0,
                    'amountCredited' => 0,
                    'bonus' => row[8],
                    'status' => row[6],
                    'cash_receipts' => Array.new,
                    'matchedBy' => row[4],
                    'dateMatched' => dateMatched
                }
            
            end
            
            cash_receipt = CashReceipt.new(
                :id => row[9],
                :providerTransactionId => row[17],
                :enteredBy => row[11],
                :dateCreated => row[1],
                :dateReceived => row[12],
                :amountSent => row[13],
                :amountReceived => row[14],
                :amountCredited => row[15],
                :type => row[16],
                :paymentDetails => row[18],
                :comments => row[19])
            
            transaction['cash_receipts'] << cash_receipt
            transaction['amountReceived'] = transaction['amountReceived'] + row[14].to_f
            transaction['amountCredited'] = transaction['amountCredited'] + row[15].to_f
        
        end
        
        transactions << transaction
        
        stmt.free_result
        stmt.close
        
        return transactions
                
    end
    
    def self.list_account_receipts(args)
    
        sort_by = args[:sortBy] 
        filterColumns = []
        filters = [args[:dateFrom],args[:dateTo]]
        cash_receipts = []
        
        if args.has_key? :providerTransactionId  and !args[:providerTransactionId].empty?
            filterColumns << 'AND moneytransfer.receiptNumber LIKE ?'
            filters << "%#{args[:providerTransactionId]}%"
        end
        
        if args.has_key? :senderUsername  and !args[:senderUsername].empty?
            filterColumns << 'AND moneytransfer.username LIKE ?'
            filters << "%#{args[:senderUsername]}%"
        end
        
        if args.has_key? :senderName and !args[:senderName].empty?
            filterColumns << 'AND moneytransfer.fullname = ?'
            filters << "%#{args[:senderName]}%"
        end
        
        if args.has_key? :mobilePhone and !args[:mobilePhone].empty?
            filterColumns << 'AND user.mobilephone = ?'
            filters << "%#{args[:mobilePhone]}%"
        end
        
        if args.has_key? :countryId and args[:countryId].to_i > 0
            filterColumns << 'AND user.countryID = ?'
            filters << args[:countryId]
        end
        
        db = Dbconn.get_instance
        stmt = db.prepare "SELECT moneytransfer.id AS 'ID', moneytransfer.username AS 'Username', User.mobilephone AS 'Mobile', country.name AS 'Country', moneytransfer.datecreated AS 'Date Received', moneytransfer.receiptnumber AS 'Receipt Number', moneytransfer.fullname AS 'Name', moneytransfer.amount AS 'Amount' FROM moneytransfer, user, country WHERE moneytransfer.username = user.username AND user.countryID = country.id AND Date(datecreated) >= ? AND Date(datecreated) <= adddate(?, 1) #{filterColumns.join(' ')} ORDER BY #{sort_by}"
        stmt.execute *filters
        
        #cash_receipts << "SELECT moneytransfer.id AS 'ID', moneytransfer.username AS 'Username', User.mobilephone AS 'Mobile', country.name AS 'Country', moneytransfer.datecreated AS 'Date Received', moneytransfer.receiptnumber AS 'Receipt Number', moneytransfer.fullname AS 'Name', moneytransfer.amount AS 'Amount' FROM moneytransfer, user, country WHERE moneytransfer.username = user.username AND user.countryID = country.id AND Date(datecreated) >= ? AND Date(datecreated) <= adddate(?, 1) #{filterColumns.join(' ')} ORDER BY #{sort_by}"
        
        #cash_receipts << filters
        
        while row = stmt.fetch do
            
            cash_receipts << {
                'id' => row[0],
                'username' => row[1],
                'mobilePhone' => row[2],
                'country' => row[3],
                'dateCreated' => row[4],
                'providerTransactionId' => row[5],
                'name' => row[6],
                'amount' => row[7]
            }
            
        end
        
        stmt.free_result
        stmt.close
        
        return cash_receipts    
    
    end
    
    def self.get(id)
    
        error = {}
        cash_receipt = nil
        
        db = Dbconn.get_instance 
        stmt = db.prepare "SELECT 
                        cashreceipt.ID, 
                        cashreceipt.providerTransactionID AS 'Transaction Reference No', 
                        IFNULL(cashreceipt.senderUsername, 'nil') AS Username, 
                        IFNULL(USER.MobilePhone, 'nil') AS Mobile, 
                        IFNULL(COUNTRY.Name, 'nil') AS Country, 
                        IFNULL(cashreceipt.enteredBy, 'Nil') AS 'Entered By', 
                        date_format(DateCreated,'%m/%d/%Y') AS 'Date Entered', 
                        date_format(dateReceived,'%m/%d/%Y') AS 'Date Received', 
                        IFNULL(date_format(dateMatched,'%m/%d/%Y'), 'Nil') AS 'Date Matched', 
                        AmountSent AS 'Amount Sent', 
                        AmountReceived AS 'Amount Received', 
                        IFNULL(AmountCredited,0) AS 'Amount Credited', 
                        case 
                          when cashreceipt.type = 0 then 'Telegraphic Transfer' 
                          when cashreceipt.type = 1 then 'Direct Credit' 
                          when cashreceipt.type = 2 then 'Western Union' 
                        end as type, 
                        cashreceipt.paymentdetails AS 'Details of Payment', 
                        IFNULL(comments, '') AS 'Comments', 
                        cashreceipt.status,
                        IFNULL(matchedBy, 'nil') AS 'Owner', 
                        matchedBy, 
                        bonus, 
                        referenceCashReceiptId, 
                        country.id 
                           FROM 
                            cashreceipt 
                            LEFT OUTER JOIN USER ON cashreceipt.senderUsername = USER.Username 
                            LEFT OUTER JOIN COUNTRY ON USER.countryID = COUNTRY.ID 
                           WHERE cashreceipt.id = ?"
        stmt.execute id
        
        while row = stmt.fetch do
            
            cash_receipt = CashReceipt.new(
                :id => row[0],
                :providerTransactionId => row[1],
                :senderUsername => row[2],
                :mobilePhone => row[3],
                :country => row[4],
                :enteredBy => row[5],
                :dateCreated => row[6],
                :dateReceived => row[7],
                :dateMatched => row[8],
                :amountSent => row[9],
                :amountReceived => row[10],
                :amountCredited => row[11],
                :type => row[12],
                :paymentDetails => row[13],
                :comments => row[14],
                :status => row[15],
                :owner => row[16],
                :matchedBy => row[17],
                :bonus => row[18],
                :referenceCashReceiptId => row[19],
                :countryId => row[20])
            
        end
        
        stmt.free_result
        stmt.close
        
        return cash_receipt
        
    end

    #
    # Reverse cash receipt by its matching accountentry
    #
    # CashReceipt.reverse(String misusername, Long transaction_id)
    #
    def self.reverse(misusername, transaction_id)

        begin 
            if(is_reversed?(transaction_id))
                return false, "Cash Receipt has already been reversed"
            end

            restClient  = RestClient::Resource.new(FUSION_REST + "/account/reverse/ttcredit")
            data_holder = {
                :misUserName => misusername,
                :accountEntryID => transaction_id
            }
            data = { :data => data_holder }.to_json
            response = JSON.parse(restClient.post data, :content_type => :json, :accept => :json)
            if(response['error'].nil?)
                return true, 'Succesfully reversed transfer credit'
            else
                return false, response['error']['message']
            end

        rescue Exception => e

            return false, e.message;
        end

    end

    private
    def self.is_reversed?(transaction_id)
        db = Dbconn.get_instance 
        stmt = db.prepare "SELECT status FROM cashreceipt WHERE id = (SELECT reference FROM accountentry WHERE id = ?)"
        stmt.execute transaction_id

        result = stmt.fetch

        if result.nil?
            throw new Exception("No matching cash receipt with accountentry id #{transaction_id}")
        elsif @@status[result[0]] == 'Reversed'
            return true
        else
            return false
        end
    end

    
end