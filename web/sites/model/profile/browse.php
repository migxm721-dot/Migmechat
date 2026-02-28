<?php
	fast_require("SoapModel", get_framework_common_directory() . "/soap_model.php");

	class BrowseModel extends SoapModel
	{
		public function get_data($model_data)
		{
			$session_user = get_value_from_array("session_user", $model_data);
			$session_user_detail = $model_data["session_user_detail"];
			$username = get_value_from_array("username", $model_data, "string", get_value("username"));


			if( ! is_json_view())
			{
				$page = get_attribute_value("page", "integer", 1);
				$number_of_entries = get_attribute_value("number_of_entries", "integer", 5);
			}
			else
			{
				$record = get_attribute_value("page", "integer", 1);
				$number_of_entries = get_attribute_value("number_of_entries", "integer", 8);
				$page = intval($record/$number_of_entries + 1);
			}

			$countryIDString = $session_user_detail->countryID;

			settype($countryIDString, "string");

			$result = $this->make_soap_call('searchUserProfiles',
						array("", $countryIDString, "", "", "", "", "", $page, $number_of_entries, "TRUE", ""));

			$info = $result->data[0];

			$num_pages = get_value_from_array("numPages", $info, "integer", 0);

			$profiles = array();

			if( $num_pages > 0 )
			{
				for($i=1; $i<count($result->data); $i++)
				{
					$p = array(
							"username" => get_value_from_array("username", $result->data[$i]),
							"country" => get_value_from_array("country", $result->data[$i]),
							"display_id" => get_value_from_array("displayPicture", $result->data[$i])
						);
					$profiles[] = $p;
				}
			}

			$data = array(
						"number_of_pages" => $num_pages,
						"total_results" => get_value_from_array("numEntries", $info, "integer", 0),
						"page" => $page,
						"profiles" => $profiles
					);
			return $data;
		}
	}
?>