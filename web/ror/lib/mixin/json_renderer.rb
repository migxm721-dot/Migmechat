# lazy hash to attribute initializer
# require the class using this to implement attr_writer or attr_accessor
module Mixin::JsonRenderer

	def render_json(success,dataOrError)
		response = { :success => success }
		if success
			response[:data] = dataOrError
		else
			response[:data] = { :error => dataOrError }
		end

		render :json => response
	end
end