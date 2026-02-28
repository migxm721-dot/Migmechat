class HelloWorld
	def call(env)
		[200, { "Content-Type"=>"text/html"}, "hello <strong>world</strong>!"]
	end
end

run HelloWorld.new
