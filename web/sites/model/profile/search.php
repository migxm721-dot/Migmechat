<?php
/**
 * Created by PhpStorm.
 * User: phong
 * Date: Nov 23, 2010
 * Time: 11:49:50 AM
 */

fast_require("UserDAO", get_dao_directory() . '/user_dao.php');

class SearchModel extends Model
{

    function get_data($data)
    {
        $dao = new UserDAO();

        $session_user = get_value_from_array('session_user', $data);
        $search = get_value('search');

        // validate input
		// Since the search is on username, only valid usernames characters are accepted as part of the search string
		$search = preg_replace('/[^a-z0-9_.-]+/i', '', $search);

        $page = get_attribute_value('page', 'integer',  get_attribute_value('page', 'integer', 1));
        $number_of_entries = get_attribute_value('number_of_entries', 'integer', 5);

        $friends = $dao->get_friends($session_user, $page, $number_of_entries, $search);

        $user_exists = $dao->mig33_user_exists($search);

        return array('friends' => $friends['contacts'],
            'number_of_entries' => get_value_from_array('number_of_entries', $friends, 'integer', 5),
            'page' => get_value_from_array('page', $friends, 'integer', 1),
            'older_entries_exist' => $friends['older_entries_exist'],
            'mig33_user_exists'=>$user_exists,
            'num_of_contacts'=>$friends['num_of_contacts'],
            'search' => $search);
    }
}

?>