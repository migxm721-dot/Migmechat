# environment
ENV['RAILS_ENV'] = 'development'

# Load the rails application
require File.expand_path('../application', __FILE__)

# Load general configurations
require File.join(File.dirname(__FILE__), 'app')

# Initialize the rails application
Ror::Application.initialize!

# global variables

LOGIN_INVALID_USERNAME_PASSWORD = 0
LOGIN_UNABLE_CREATE_SESSION = 1
LOGIN_SUCCESS = 2
LOGOUT_UNABLE_DESTROY_SESSION = 0
LOGOUT_SUCCESS = 1

# MERCHANT PIN
UNAUTHENTICATED_MERCHANT_PIN = 15
AUTHENTICATED_MERCHANT_PIN = 16