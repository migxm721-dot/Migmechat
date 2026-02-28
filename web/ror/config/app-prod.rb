# configuration file

# database configuration
DB_SETTINGS = {
	
	:master => { 	:host => "dbpm",
        			:username => "fusion",
        			:password => "eWNZ6jiSCXtoyyeE",
        			:database => "fusion" },
    
    :olap => {		:host => "misdb.vip",
        			:username => "fusion",
        			:password => "eWNZ6jiSCXtoyyeE",
        			:database => "fusion"},
    
    :ods => {		:host => "ods.sjc01.projectgoth.com",
        			:username => "ods",
        			:password => "GHz6ChefybLeV7e8",
        			:database => "ods"},  
    
    :mis_hive => {	:host => "10.3.1.146",
        			:username => "mis",
        			:password => "grav30_occurs",
        			:database => "mis_hive"},
    
    :qry_read => { :host => 'misdb.vip',
    				:username => 'fusionmis',
    				:password => 'mWG5aSK6tqsx5p99',
    				:database => 'fusion'}   			  			
        			
}

# soap configuration
SOAP_EJB_SERVICE_URL = 'http://jboss.vip.vljb:8080/soap/servlet/rpcrouter'
SOAP_EJB_SERVICE_NAME = 'urn:fusion'
  
# memcache confisguration
MEMCACHE_SERVER_ADDRESSES = ['10.3.1.60:11216', '10.3.1.61:11216']
  
# asset details
ASSETS_SCRIPT_LOGS_DIRECTORY = '/var/www/htdocs/mis/scripts/logs'
ASSETS_RELEASE_SCRIPTS_DIRECTORY = '/var/www/htdocs/mis/scripts/release_scripts'
ASSETS_SCRIPT_DIRECTORY = '/var/www/htdocs/mis/scripts' 
ASSETS_DIRECTORY = '/var/www/htdocs/mis/web_resources'

# svn access
SVN_SETTINGS = {
	'svn_repo_master' => 'https://svn.projectgoth.com/svn/Mig33/web_resources/branches/MIS',
	'svn_user' => 'mis',
	'svn_pass' => 'Fon?JaWAn:Ai',
	'svn_repo_working_copy' => ASSETS_DIRECTORY
}


IMAGE_SERVER_URL = 'http://img.mig33.com'

ICE_REGISTRY_CONNECTION = 'Registry:tcp -h 10.3.1.28 -p 10000';

# Constant values for ice 
LONG_MIN_VALUE = 0 #-9223372036854775808l
INT_MIN_VALUE = 0 #-2147483648
DBL_MIN_VALUE = 0 #4.94065645841246544 ** 0.324

# mis log directory
MIS_LOG_DIRECTORY = '/var/www/htdocs/ror/log/mis'

# sales support approve email
SALES_SUPPORT_APPROVE_EMAIL = 'sales.support.approve@mig33global.com'

# vendor management rates
VENDOR_MANAGEMENT_APPROVE_EMAIL = 'ops@mig33global.com'

# mis assets release email notification, set to asets release owner
MIS_ASSETS_UPLOAD_EMAIL = 'saravanavelu@mig33global.com'

# max chatroom size limit
MAX_CHATROOM_LIMIT = 150

# call rate location
CALL_RATE_LOCATION = '/var/www/htdocs/ror/lib/call_rates/providers'
CALL_RATE_PROPOSED = '/var/www/htdocs/ror/lib/call_rates/tmp'

# unfunded credits description
UNFUNDED_CREDITS_DESCRIPTION_PATH = '/var/www/htdocs/ror/lib/unfunded_credits_description.txt'

MIGBO_DATA_SVC = 'http://carina.prod.sjc02.projectgoth.com:8080/migbo_datasvc'
FUSION_REST = 'http://jboss.vip.vljb:8080/fusion-rest' #'http://localhost:8080/fusion-rest'

BADGES_RESOURCE_PATH = 'http://b.mig33.com/resources/img/badges'

MKT_MSG_TOP_COUNTRIES_LIST = '/var/www/htdocs/ror/lib/marketing_message_top_countries.txt'

# email template content directory
EMAIL_CONTENT_TEMPLATE_DIR = '/var/www/htdocs/ror/email_templates'
