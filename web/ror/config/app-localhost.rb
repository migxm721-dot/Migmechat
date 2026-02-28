# configuration file

# database configuration
DB_SETTINGS = {
	
	:master => { 	:host => "dev-db-1",
        			:username => "fusion",
        			:password => "abalone5KG",
        			:database => "fusion" },
    
    :olap => {		:host => "dev-db-1",
        			:username => "fusion",
        			:password => "abalone5KG",
        			:database => "fusion"},
  :qry_read => {    :host => "dev-db-1",
              :username => "fusion",
              :password => "abalone5KG",
              :database => "fusion"}
}

# soap configuration
SOAP_EJB_SERVICE_URL = 'http://dev-app-1:8088/soap/servlet/rpcrouter'
SOAP_EJB_SERVICE_NAME = 'urn:fusion'
  
# memcache confisguration
MEMCACHE_SERVER_ADDRESSES = ['dev-db-1:11211']
  
# asset details
ASSETS_SCRIPT_LOGS_DIRECTORY = '/var/www/mis-ror/mis/scripts/logs'
ASSETS_SCRIPT_DIRECTORY = '/var/www/mis/mis-ror/scripts' 
ASSETS_DIRECTORY = '/var/www/web_resources/'

# svn access
SVN_SETTINGS = {
	'svn_repo_master' => 'https://svn.projectgoth.com/svn/Mig33/web_resources/branches/MIS',
	'svn_user' => 'mis',
	'svn_pass' => 'Fon?JaWAn:Ai',
	'svn_repo_working_copy' => ASSETS_DIRECTORY
}


IMAGE_SERVER_URL = 'http://img.localhost.projectgoth.com'

ICE_REGISTRY_CONNECTION = 'Registry:tcp -h 127.0.0.1 -p 10000';

# Constant values for ice 
LONG_MIN_VALUE = 0 #-9223372036854775808l
INT_MIN_VALUE = 0 #-2147483648
DBL_MIN_VALUE = 0 #4.94065645841246544 ** 0.324

# mis log directory
MIS_LOG_DIRECTORY = '/var/www/mis-ror/ror/log'

# unfunded credits description
UNFUNDED_CREDITS_DESCRIPTION_PATH = '/var/www/mis-ror/ror/lib/unfunded_credits_description.txt'

MIGBO_DATA_SVC = 'http://dev-app-1:8088/fusion-rest/migbo-datasvc-proxy'

BADGES_RESOURCE_PATH = 'http://localhost.projectgoth.com/resources/img/badges'

FUSION_REST = 'http://dev-app-1:8088/fusion-rest'

HLR_LOOKUP_SERVICE_URL = 'https://hlr.routotelecom.com/'
HLR_LOOKUP_USER = '1174803'
HLR_LOOKUP_PASSWORD = 'kkvnn9gs'

MKT_MSG_TOP_COUNTRIES_LIST = '/var/www/mis-ror/ror/lib/marketing_message_top_countries.txt'
