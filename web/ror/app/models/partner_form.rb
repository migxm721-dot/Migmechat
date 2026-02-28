class PartnerForm < ActiveForm
    
    @txtName
    
    attr_accessor :txtName
    
    validates_presence_of :txtName,          :message => 'Name cant be empty'
    validates_length_of   :txtName,          :minimum => 3, 
                                             :message => 'Name cant be less than 3 chars'
end