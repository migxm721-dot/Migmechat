<?php
class CampaignController
{
	public function validate_mobile_number(&$model_data)
	{
		$view = new ControllerMethodReturn("input", array());
		$cid = get_value("cid");
		$mobile_number = get_value("mobile_number", "string");
		$error_msg = "";
		
		if (!$model_data['is_smartfren_device']) {
			// Translation: This contest is only open to Smartfren users on the Andromax C3
			$error_msg = "Kontes ini hanya berlaku bagi pengguna Smartfren Andromax C3";
		} else{
			if (isset($mobile_number)) {
				$valid_smartfren = $this->check_smartfren_mobile_number($mobile_number);
				if (!$valid_smartfren) {
					// Translation: You have entered an invalid mobile number. You need to enter a valid Smartfren mobile number.
					$error_msg = "Anda memasukkan nomor Smartfren yang salah. Masukkan nomor Smartfren Anda yang benar.";
				}
			}
		}
		
		if (isset($cid) && empty($error_msg)) {
			$view->method = "confirm";
		}
		
		$model_data['error'] = $error_msg;
		$view->model_data = $model_data;
		return $view;
	}
	
	public function store_user_campaign_details(&$model_data)
	{
		$view = new ControllerMethodReturn("input", array());
		$cid = get_value("cid");
		$email = get_value("email");
		$reference = get_value("reference");
		$error_msg = "";
		$mobile_number = get_value("mobile_number", "string");
		$mobile_number_reentered = get_value("mobile_number_reentered", "string");
		
		if($mobile_number === $mobile_number_reentered) {
			// Mobile numbers match. Store the user details.
			try {
				// Store user details
				$resp = FusionRest::get_instance()->json_post(
					sprintf(FusionRest::KEYSPACE_STORE_USER_CAMPAIGN_DETAILS, SessionUtilities::$session_user_id)
					, array(
						'data' => array(
							'userId' => $model_data['session_user_id']
							, 'campaignId' => $cid
							, 'mobilePhone' => $mobile_number
							, 'emailAddress' => $emailAddress
							, 'reference' => $reference
						)
					)
				);
				
				$view->method = "confirmed";
			} catch(Exception $e) {
				error_log("Unable to store campaign details: " . $e->getMessage());
				
				// Fusion: (2000, "Duplicate Mobile")
				if($e->getCode() == 2000) {
					$error_msg = "Nomor Smartfren yang Anda masukkan telah terdaftar. Anda hanya dapat mengunakan 1 (satu) nomor Smartfren.";
				} else {
					$error_msg = $e->getMessage();
				}
			}
		} else {
			// Failed verification
			$error_msg = "Nomor handphone Anda tidak sesuai.";
		}
		
		$model_data['error'] = $error_msg;
		$view->model_data = $model_data;
		return $view;
	}

	private function check_smartfren_mobile_number($mobile_number)
	{
		// 62 is indonesian phone number, hence +62888
		return preg_match('/(^0888|^0889|^0881|^0882|^62888|^62889|^62881|^62882)[0-9]{6,7}$/', $mobile_number);
	}
}
?>


<?php
// this was coded in migbo previously. delete if not needed anymore
/* if ( ! defined('BASEPATH')) exit('No direct script access allowed');

//store_user_campaign_details

class Campaign extends MY_Controller
{
	/**
	 * __construct function.
	 *
	 * @access public
	 * @return void
	 /
	public function __construct()
	{
		parent::__construct();

		$this->load->model('campaign/Campaign_model');
	}

	/**
	 * index function.
	 *
	 * @access public
	 * @return void
	 /
	public function index($cid = 1)
	{
		// Requires Valid Session
		$this->requires_valid_session(false);
		$error_msg = '';

		$campaign_active = $this->Campaign_model->get_campaign_status($cid);
		if (!$campaign_active) {
			$error_msg = 'This campaign has finished.';
		} else {
			try {
				$user_details = $this->Campaign_model->get_user_campaign_details($username, $cid);
				if (isset($user_details['mobile_number'])) {
					$this->load_view(_('Campaign'), 'campaign/confirmed', array());
					return;
				}
			} catch(Exception $ex) {
				$error_msg = $ex->getMessage();
			}
		}

		$data['error'] = $error_msg;
		$this->load_view(_('Campaign'), 'campaign/input', $data);
	}

	public function confirm()
	{
		$error_msg = '';

		$mobile_number = intval(trim($this->input->get_post('mobile_number')));
		if (!check_smartfren_mobile_number($mobile_number)) {
			$error_msg = _('Please key in your smartfren mobile number');
		}

		if (!$error_msg) {
			// store the mobile number via fusion rest
			try
			{
				$user_data['mobile_number'] = $mobile_number;
				$data['message'] = $this->Campaign_model->store_user_campaign_detail($user_data);
			}
			catch(Exception $ex)
			{
				$error_msg = $ex->getMessage();
			}
		}
		
		$data['error'] = $error_msg;
		$data['mobile_number'] = $mobile_number;

		if ($error_msg) {
			$this->load_view(_('Campaign'), 'campaign/input', $data);
		} else {
			$this->load_view(_('Campaign'), 'campaign/confirmed', $data);
		}
	}

	private function check_smartfren_mobile_number($mobile_number)
	{
		$result = preg_match('/^0888|^0881|^62888|^62881|/', $mobile_number);
		return $result;
	}

	public function users($page = 1)
	{
		// Requires Valid Session
		$this->requires_valid_session(false);

		// Web View, We Don't Need More Details
		if($this->is_web_view())
		{
			$this->load_view();
			return;
		}

		$data = array();
		$data = array_merge($this->_facets_hashtags(), $data);

		if(is_null($this->query))
		{
			$data['searching'] = false;
			$this->load_view(_('Search Users'), 'search/users', $data);
		}
		else
		{
			// Limit
			$limit = $this->default_entries_count['wap']['max']; // WAP-146
			// Data
			$data = array_merge($this->_search('users', $limit, $page), $data);
			$data['searching'] = true;

			if(empty($data['search_error']))
			{
				$data = array_merge($this->paginate($this->router->platform.'/search/users', $limit, $data['search']->total, 3, '?'.http_build_query(array('query' => $this->query))), $data);

				$this->load_view(sprintf(_('Search Results For "%s" In Users'), $this->query), 'search/users', $data);
			}
			else
			{
				$this->load_view(_('Search In Users'), 'search/users', $data);
			}
		}
	}

	public function posts($page = 1)
	{
		// Requires Valid Session
		$this->requires_valid_session(false);

		// Web View, We Don't Need More Details
		if($this->is_web_view())
		{
			$this->load_view();
			return;
		}

		$data = array();
		$data = array_merge($this->_facets_hashtags(), $data);

		if(is_null($this->query))
		{
			$data['searching'] = false;
			$this->load_view(_('Search Posts'), 'search/posts', $data);
		}
		else
		{
			// Sorting
			$sort_by = $this->input->get('sortby', true) ? $this->input->get('sortby', true) : 'date';

			// Limit
			$limit = $this->default_entries_count['wap']['max'];

			// Data
			$data = array_merge($this->_search('posts', $limit, $page, $sort_by), $data);
			$data['searching'] = true;
			if(empty($data['search_error']))
			{
				$data = array_merge(
					$this->paginate(
						  $this->router->platform.'/search/posts'
						, $limit
						, $data['search']->total
						, 3
						, '?'.http_build_query(array('query' => $this->query, 'sortby' => $sort_by))
					)
					, $data
				);

				$this->load_view(sprintf(_('Search Results For "%s" In Posts'), $this->query), 'search/posts', $data);
			}
			else
			{
				$this->load_view(_('Search In Posts'), 'search/posts', $data);
			}
		}
	}

	public function topics($page = 1)
	{
		// Web View, We Don't Need More Details
		if($this->is_web_view())
		{
			$this->load_view();
			return;
		}

		$data = array();
		$data = array_merge($this->_facets_hashtags(), $data);

		if(is_null($this->query))
		{
			$data['searching'] = false;
			$this->load_view(_('Search Topics'), 'search/topics', $data);
		}
		else
		{
			// Sorting
			$sort_by = $this->input->get('sortby', true) ? $this->input->get('sortby', true) : 'date';

			// Limit
			$limit = $this->default_entries_count['wap']['max'];

			// Data
			$data = array_merge($this->_search('topics', $limit, $page, $sort_by), $data);
			$data['searching'] = true;
			if(empty($data['search_error']))
			{
				$data = array_merge(
					$this->paginate(
						  $this->router->platform.'/search/topics'
						, $limit
						, $data['search']->total
						, 3
						, '?'.http_build_query(array('query' => $this->query, 'sortby' => $sort_by))
					)
					, $data
				);

				$this->load_view(sprintf(_('Search Results For "#%s" In Topics'), $this->query), 'search/topics', $data);
			}
			else
			{
				$this->load_view(_('Search In Topics'), 'search/topics', $data);
			}
		}
	}

	private function _search($location, $limit = -1, $page = 1, $sort_by = 'date')
	{
		// Variables
		$search = null;
		$search_error = '';

		// Get Posts
		try
		{
			if(is_null($limit))
				throw new Exception(_('Search limit is not specified.'));

			if(is_null($this->query))
				throw new Exception(_('Search query is empty.'));

			if($page < 1 || $page > 100)
				$page = 1;

			switch($location)
			{
				case 'posts':
					$this->load->model('search/Search_posts_model');
					$search = $this->Search_posts_model->search_posts($this->query, $limit, ($page - 1), $sort_by);
					break;
				case 'topics':
					$this->load->model('search/Search_posts_model');
					$search = $this->Search_posts_model->search_topics($this->query, $limit, ($page - 1), $sort_by);
					break;
				case 'users':
					$this->load->model('search/Search_users_model');
					$search = $this->Search_users_model->search_users($this->query, $limit, ($page - 1));
					break;
				default:
					throw new Exception(_('Invalid Search Location'));
			}
		}
		catch(Exception $ex)
		{
			$search_error = _('Search is temporarily unavailable, please try again later.'); // $ex->getMessage();
			$search_error = $ex->getMessage();
		}

		// Pass Data Back To View
		$data = array();
		$data['search'] = $search;
		$data['search_sort'] = $sort_by;
		$data['search_error'] = $search_error;

		return $data;
	}

	private function _facets_hashtags($since = -1, $size = 5)
	{
		// Variables
		$data = array();
		$data['facets_hashtags'] = null;
		$data['facets_hashtags_error'] = '';

		try
		{
			$this->load->model('search/Search_facets_model');
			$data['facets_hashtags'] = $this->Search_facets_model->search_hashtags($since, $size);
		}
		catch(Exception $ex)
		{
			$data['facets_hashtags_error'] = $ex->getMessage();
		}

		return $data;
	}
}*/