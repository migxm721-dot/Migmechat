<?php
	fast_require("Rest", get_library_directory() . "/rest/rest.php");
	fast_require("RestJsonResponse", get_library_directory() . "/rest/rest_json_response.php");
	fast_require("VirtualGiftDAO", get_dao_directory() . "/virtual_gift_dao.php");

	class TouchApiController
	{
		protected $response;

		public function __construct()
		{
			$this->response = new RestJsonResponse();
		}

		protected function send_on_error($call_result, $response_code = 500, $data = array())
		{
			if( $call_result->is_error() )
			{
				$this->response->send(500, $data);
			}
		}

		protected function convert_location_to_url( $location )
		{
			global $server_root;
			$location = str_replace("\\", "/", $location);
			$pos = strpos($location, "emoticons");
			$base = substr($location, $pos);
			$imageUrl = "/images/".$base;
			return $imageUrl;
		}

        public function update_status_message($data)
        {
           	$this->response->send(200, array("ok" => isset($data['successes'])));
        }

        public function get_friends_carousel($data)
        {
        	$this->response->send(200, array('type' => 'friends', 'items' => $data['friends']));
        }
        public function get_chatrooms_carousel($data)
        {
                // get the specific page
                $page = get_attribute_value("p", "integer", 1);
                $page_size = get_attribute_value("e", "integer", 8);

                $slice_start = $page_size * ($page -1);
                $slice = array_slice( $data["current_chatrooms"], $slice_start, $slice_start + $page_size);
                $this->response->send(200, array('type' => 'chatrooms', 'items' => $slice ));
        }
        public function get_mutual_friends_carousel($data)
        {
        	$this->response->send(200, array('type' => 'mutual_friends', 'items' => $data['friends_minimal']));
        }

        public function get_virtual_gifts_carousel($data)
        {
            $this->response->send(200, array('type' => 'virtual_gifts', 'items' => $data['gifts_received']));
        }
	}
?>
