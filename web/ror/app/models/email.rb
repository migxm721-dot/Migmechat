class Email
    include Mixin::HashInitializer
    attr_accessor :html_content, :text_content, :rich_text_content, :subject, :target_criteria

    # utility methods that dont really belong here
    def self.send_email
    end

    def self.build_criteria
    end
end