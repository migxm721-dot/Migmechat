class MigboMailer
	include Mixin::HashInitializer
	attr_accessor :email, :criteria, :mig_lvl_min, :mig_lvl_max, :mis_log_id

	def send_email
		restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/email/send_email');
		data = {
			:condition => self.criteria.get,
			:emailSubject => self.email.subject,
			:plainEmailBody => self.email.text_content,
			:htmlEmailBody => self.email.html_content,
			:mislogID => self.mis_log_id
		}

		#optional params
		if(!self.mig_lvl_max.empty?)
			data[:migLevelMin] = self.mig_lvl_min
		end

		if(!self.mig_lvl_max.empty?)
			data[:migLevelMax] = self.mig_lvl_max
		end

		begin
			response = JSON.parse(restClient.post data.to_json, :content_type => :json, :accept => :json)
			if(response.has_key?("error"))
				return false, response["error"]["message"]
			else
				return true, "Email Successfully sent"
			end
		rescue Exception => e
			return false, e.message
		end
	end

	def send_to_list(recipients)
		restClient = RestClient::Resource.new(MIGBO_DATA_SVC + '/email/email_idle_user');
		data = {
			:recipients => recipients,
			:emailSubject => self.email.subject,
			:plainEmailBody => self.email.text_content,
			:htmlEmailBody => self.email.html_content,
			:mislogID => self.mis_log_id
		}
		begin
			response = JSON.parse(restClient.post data.to_json, :content_type => :json, :accept => :json)
			if(response.has_key?("error"))
				return false, response["error"]["message"]
			else
				return true, "Email Successfully sent"
			end
		rescue Exception => e
			return false, e.message
		end

		return true
	end
end