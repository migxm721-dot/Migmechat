<?php
	/*
		fusion store custom param
		sortby= name, datelisted
		sortorder= asc, desc
		featured= true, false
		offset=
		limit=

		- below are not common params
		type
		categoryId
		query
		minPrice
		maxPrice
	*/

	require_once(get_framework_common_directory() . "/database.php");
	fast_require("Store", get_domain_directory() . "/store/store.php");
	fast_require("AvatarStoreitem", get_domain_directory() . "/store/avatar_storeitem.php");
	fast_require("VirtualGiftStoreitem", get_domain_directory() . "/store/virtual_gift_storeitem.php");
	fast_require("EmoticonPackStoreitem", get_domain_directory() . "/store/emoticon_pack_storeitem.php");
	fast_require("SuperEmoticonPackStoreitem", get_domain_directory() . "/store/super_emoticon_pack_storeitem.php");
	fast_require("ThemeStoreitem", get_domain_directory() . "/store/theme_storeitem.php");
	fast_require("StickerStoreitem", get_domain_directory() . "/store/sticker_storeitem.php");
	fast_require("StoreCategory", get_domain_directory() . "/store/store_category.php");
	fast_require("DAO", get_dao_directory() . "/dao.php");
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");

	class StoreDAO extends DAO
	{
		public $FORCE_RELOAD = false;
		public $MEMCACHE_EXPIRY = 600;

		//Function to get featured items (can be specific to store item type)
		// the featured page is not accessible by link
		public function get_featured_items($storeitem_type, $number_entries, $page, $username, $currency)
		{
			$memcache = Memcached::get_instance();
			$storeitems = $memcache->get('Store/Featured/Type_'.$storeitem_type.'/'.$currency);

			$index = ($page-1) * $number_entries;
			$query_param = http_build_query(array(
				  'sortby' => 'datelisted'
				, 'featured' => 'true'
				, 'offset' => $index
				, 'limit' => $number_entries
			));
			$result = FusionRest::get_instance()->get(
						sprintf(FusionRest::KEYSPACE_STORE_TYPE_ITEMS, $storeitem_type) . '?' .$query_param
						);

			$storeitems = array();
			$store = new Store();
			foreach ($result['listData'] as $key => $value) {
				$store->convert_store_data_fusion_to_sql($value);
				$storeitems[] = $store->get_item($value);
			}

			$total_results = $result['totalResults'];
			$total_pages = ceil($total_results / $number_entries);

			$index = ($page-1) * $number_entries;
			$count = 0;
			$storeitems_output = array();
			$storeitems_ids = array();
			while($index < $total_results && $count < $number_entries)
			{
				$storeitems_output[] = $storeitems[$count];
				$storeitems_ids[] = $storeitems[$count]->id;
				$index += 1;
				$count += 1;
			}

			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "storeitems"=>$storeitems_output, 'storeitemsids' => $storeitems_ids);
		}

		//Function to get new items (can be specific to store item type). New items are those listed less than 7 days ago
		public function get_new_items($storeitem_type, $number_entries, $page, $username, $currency)
		{
			$index = ($page-1) * $number_entries;
			$query_param = http_build_query(array(
				  'sortby' => 'datelisted'
				, 'offset' => $index
				, 'limit' => $number_entries
			));
			$result = FusionRest::get_instance()->get(
						sprintf(FusionRest::KEYSPACE_STORE_TYPE_ITEMS, $storeitem_type) . '?' .$query_param
						);

			$storeitems = array();
			$store = new Store();
			foreach ($result['listData'] as $key => $value) {
				$store->convert_store_data_fusion_to_sql($value);
				$storeitems[] = $store->get_item($value);
			}

			$total_results = $result['totalResults'];
			$total_pages = ceil($total_results / $number_entries);

			$count = 0;
			$storeitems_output = array();
			$storeitems_ids = array();
			while($index < $total_results && $count < $number_entries)
			{
				$storeitems_output[] = $storeitems[$count];
				$storeitems_ids[] = $storeitems[$count]->id;
				$index += 1;
				$count += 1;
			}

			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "storeitems"=>$storeitems_output, "storeitemsids" => $storeitems_ids);
		}

		//function to get just one new item of a specific type, catered for user's currency
		public function get_new_item($storeitem_type, $username, $currency)
		{
			return $this->get_new_items($storeitem_type, 1, 1, $username, $currency);
		}

		//Function to get free items (can be specific to store item type).
		// not accessible to public as well
		public function get_free_items($storeitem_type, $number_entries, $page, $username, $currency)
		{
			$memcache = Memcached::get_instance();
			$storeitems = $memcache->get('Store/Free/Type_'.$storeitem_type.'/'.$currency);

			if($this->FORCE_RELOAD || empty($storeitems))
			{
				$params = array();

				if(!empty($username))
				{
					$query = "SELECT
								si.ID,
								si.Type,
								si.ReferenceID,
								si.Name,
								si.Description,
								si.price/sic.exchangerate*uic.exchangerate Price,
								usr.currency Currency,
								si.Status,
								si.NumAvailable,
								si.NumSold,
								si.Featured,
								si.CatalogImage,
								si.PreviewImage,
								si.ForSale,
								si.SortOrder,
								si.ExpiryDate,
								si.DateListed,
								si.migLevelMin,
								si.GroupID
								FROM
								storeitem si,
								currency sic,
								currency uic,
								user usr
								WHERE
								si.price = 0
								AND si.status = 1
								AND si.currency = sic.code
								AND si.forsale = 1
								AND uic.code = usr.currency
								AND usr.username=?
								";
					$params[] = array('s'=>$username);
				}
				else
				{
					$query = "SELECT
								si.*
								FROM
								storeitem si
								WHERE
								si.status = 1
								AND si.forsale = 1
								AND si.price = 0";
				}

				if ($storeitem_type != 0)
				{
					$query .= " AND si.type=? ";
					$params[] = array('i'=>$storeitem_type);
				}

				$query .= " ORDER BY si.featured DESC, si.datelisted DESC, si.sortorder ASC ";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$this->auto_bind_params($stmt, $params);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				$stmt->store_result();

				$storeitems = array();
				$store = new Store();
				while( $stmt->fetch())
				{
					$storeitems[] = $store->get_item($row);
				}

				//Store in memcache for 10 minutes
				$memcache->add_or_update('Store/Free/Type_'.$storeitem_type.'/'.$currency, $storeitems, $this->MEMCACHE_EXPIRY);

				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();
			}

			$total_results = sizeof($storeitems);
			$total_pages = ceil($total_results / $number_entries);

			$index = ($page-1) * $number_entries;
			$count = 1;
			$storeitems_output = array();
			$storeitems_ids = array();
			while($index < $total_results && $count <= $number_entries)
			{
				$storeitems_output[] = $storeitems[$index];
				$storeitems_ids[] = $storeitems[$index]->id;
				$index += 1;
				$count += 1;
			}

			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "storeitems"=>$storeitems_output, "storeitemsids" => $storeitems_ids);
		}

		//Function to get popular items (can be specific to store item type). popular items are those that have been sold the most
		public function get_popular_items($storeitem_type, $number_entries, $page, $username, $currency)
		{
			$index = ($page-1) * $number_entries;
			$query_param = http_build_query(array(
				  'sortby' => 'numsold'
				, 'offset' => $index
				, 'limit' => $number_entries
			));
			$result = FusionRest::get_instance()->get(
						sprintf(FusionRest::KEYSPACE_STORE_TYPE_ITEMS, $storeitem_type) . '?' .$query_param
						);

			$storeitems = array();
			$store = new Store();
			foreach ($result['listData'] as $key => $value) {
				$store->convert_store_data_fusion_to_sql($value);
				$storeitems[] = $store->get_item($value);
			}

			$total_results = $result['totalResults'];
			$total_pages = ceil($total_results / $number_entries);

			$index = ($page-1) * $number_entries;
			$count = 0;
			$storeitems_output = array();
			$storeitems_ids = array();
			while($index < $total_results && $count < $number_entries)
			{
				$storeitems_output[] = $storeitems[$count];
				$storeitems_ids[] = $storeitems[$count]->id;
				$index += 1;
				$count += 1;
			}

			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "storeitems"=>$storeitems_output, "storeitemsids" => $storeitems_ids);
		}

		//Function to get 1 popular item of a specific store item type, catered for user's currency
		public function get_popular_item($storeitem_type, $username, $currency)
		{
			return $this->get_popular_items($storeitem_type, 1, 1, $username, $currency);
		}

		private function get_store_item_id($item)
		{
			return $item->id;
		}

		public function get_recent_gifts_given($sender_username, $viewer_username, $number_entries, $page)
		{
			$storeitems = array();
			$memcache = Memcached::get_instance();
			$memcache_key = 'Store/RecentGiven/Type_'.$storeitem_type.'/'.$sender_username;

			$need_privacy = ($sender_username != $viewer_username);

			if (false && !$this->FORCE_RELOAD && !$need_privacy)
			{
				// if we have to force reload, no need to get the key from memcache at all
				$storeitems = $memcache->get($memcache_key);
			}

			if($this->FORCE_RELOAD || empty($storeitems))
			{
				$params = array();

				$query = "SELECT
							si.ID,
							si.Type,
							si.ReferenceID,
							si.Name,
							si.Description,
							si.price/sic.exchangerate*uic.exchangerate Price,
							usr.currency Currency,
							si.Status,
							si.NumAvailable,
							si.NumSold,
							si.Featured,
							si.CatalogImage,
							si.PreviewImage,
							si.ForSale,
							si.SortOrder,
							si.ExpiryDate,
							si.DateListed,
							si.migLevelMin,
							si.GroupID,
							MAX(vgr.DateCreated) as vgrDateCreated
							FROM
							storeitem si,
							currency sic,
							currency uic,
							user usr,
							virtualgiftreceived vgr
							WHERE
							si.status = 1
							AND si.currency = sic.code
							AND si.forsale = 1
							AND uic.code = usr.currency
							AND usr.username=?
							AND si.type=1
							AND si.referenceid=vgr.VirtualGiftID
							AND vgr.sender=?
							AND vgr.removed=0
							AND si.price >= " . Constants::get_value('STORE_GIFT_SHOWER_PRICE');

				$params[] = array('s' => $viewer_username);
				$params[] = array('s' => $sender_username);

				if ($needPrivacy)
				{
					$query .= " AND (vgr.private=0 or (vgr.private=1 and vgr.Username=?))";
					$params[] = array('s' => $viewer_username);
				}

				$query .= " GROUP BY si.ID
					ORDER BY vgrDateCreated DESC";
				$query .= " LIMIT 80";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$this->auto_bind_params($stmt, $params);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				$stmt->store_result();

				$storeitems = array();
				$store = new Store();
				while( $stmt->fetch() )
				{
					$storeitems[] = $store->get_item($row);
				}

				// Store in memcache for 10 minutes (ONLY for same user)
				if (!$needPrivacy)
				{
					$memcache->add_or_update($memcache_key, $this->MEMCACHE_EXPIRY);
				}

				$stmt->free_result();
			}

			$total_results = sizeof($storeitems);
			$total_pages = ceil($total_results / $number_entries);

			$index = ($page-1) * $number_entries;

			$storeitems    = array_slice($storeitems, $index, $number_entries);
			$storeitemsids = array_map(array($this, 'get_store_item_id'), $storeitems);

			return array(
				"total_pages"     => $total_pages
				, "total_results" => $total_results
				, "storeitems"    => $storeitems
				, "storeitemsids" => $storeitemsids
			);
		}


		//Function to get all items of certain type
		public function get_all_items($storeitem_type, $number_entries, $page, $username, $currency)
		{
			$memcache = Memcached::get_instance();
			$storeitems = $memcache->get('Store/All/Type_'.$storeitem_type.'/'.$currency);

			$index = ($page-1) * $number_entries;
			$query_param = http_build_query(array(
				  'sortby' => 'numsold'
				, 'offset' => $index
				, 'limit' => $number_entries
			));
			$result = FusionRest::get_instance()->get(
						sprintf(FusionRest::KEYSPACE_STORE_TYPE_ITEMS, $storeitem_type) . '?' .$query_param
						);

			$storeitems = array();
			$store = new Store();
			foreach ($result['listData'] as $key => $value) {
				$store->convert_store_data_fusion_to_sql($value);
				$storeitems[] = $store->get_item($value);
			}

			$total_results = $result['totalResults'];
			$total_pages = ceil($total_results / $number_entries);

			$storeitems_output = array();
			$storeitems_ids = array();
			$count = 0;
			while($index < $total_results && $count < $number_entries)
			{
				$storeitems_output[] = $storeitems[$count];
				$storeitems_ids[] = $storeitems[$count]->id;
				$index += 1;
				$count += 1;
			}

			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "storeitems"=>$storeitems_output, "storeitemsids" => $storeitems_ids);
		}

		//Function to get items in a specific category
		public function get_items_in_category($category_id, $number_entries, $page, $username, $currency, $randomize=false)
		{
			$index = ($page-1) * $number_entries;
			$query_param = http_build_query(array(
				  'sortby' => 'numsold'
				, 'offset' => $index
				, 'limit' => $number_entries
			));
			$result = FusionRest::get_instance()->get(
						sprintf(FusionRest::KEYSPACE_STORE_CATEGORY_ITEMS, $category_id) . '?' .$query_param
						);

			$storeitems = array();
			$store = new Store();
			foreach ($result['listData'] as $key => $value) {
				$store->convert_store_data_fusion_to_sql($value);
				$storeitems[] = $store->get_item($value);
			}
			
			$total_results = $result['totalResults'];
			if($number_entries == 0)
				$number_entries = 10;
			$total_pages = ceil($total_results / $number_entries);

			$index = ($page-1) * $number_entries;
			$count = 0;
			$storeitems_output = array();
			$storeitems_ids = array();

			if($randomize && $number_entries < $total_results)
			{
				shuffle($storeitems);
			}

			while($index < $total_results && $count < $number_entries)
			{
				$storeitems_output[] = $storeitems[$count];
				$storeitems_ids[] = $storeitems[$count]->id;
				$index += 1;
				$count += 1;
			}

			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "storeitems"=>$storeitems_output, "storeitemsids" => $storeitems_ids);
		}

		//Function to get all categories/sub-categories.
		public function get_categories($category_id, $number_entries, $page, $order)
		{
			$memcache = Memcached::get_instance();
			$categories = $memcache->get('Store/Categories/ID_'.$category_id);

			if(empty($categories)) {
				$categories = array();
				$sortby = 'numsold';
				if ($order) {
					$sortby = 'sortorder,numsold';
				}
				$query_param = http_build_query(array(
					  'sortby' => $sortby
				));
				$result = FusionRest::get_instance()->get(
							sprintf(FusionRest::KEYSPACE_STORE_CATEGORIES, $category_id) . '?' . $query_param
							);

				foreach ($result as $key => $value)
				{
					$categories[] = new StoreCategory($value);
				}
				$memcache->add_or_update('Store/Categories/ID_'.$category_id, $categories, $this->MEMCACHE_EXPIRY);
			}

			$total_results = sizeof($categories);
			$total_pages = ceil($total_results / $number_entries);

			$index = ($page-1) * $number_entries;
			$count = 1;
			$categories_output = array();
			while($index < $total_results && $count <= $number_entries)
			{
				$categories_output[] = $categories[$index];
				$index += 1;
				$count += 1;
			}
			return array("total_pages"=> $total_pages, "total_results"=>$total_results, "categories"=>$categories_output);
		}

		//Function to get the parent category id of a sub category
		public function get_parent_category($category_id)
		{
			$memcache = Memcached::get_instance();
			$store_category = $memcache->get('Store/Category/Parent/ID_'.$category_id);

			if(empty($store_category)) {
				$result = FusionRest::get_instance()->get(
							sprintf(FusionRest::KEYSPACE_STORE_CATEGORY, $category_id)
							);
				$store_category = new StoreCategory($result);

				//Store in memcache for 10 minutes
				$memcache->add_or_update('Store/Category/Parent/ID_'.$category_id, $store_category, $this->MEMCACHE_EXPIRY);
			}
			return $store_category;
		}

		//Function to determine if current category have a child
		public function has_subcategory($category_id)
		{
			$memcache = Memcached::get_instance();
			$has_subcategory = $memcache->get('Store/Categories/HasSub/ID_'.$category_id);

			if(empty($has_subcategory))
			{
				$categories = $this->get_categories($category_id, 50, 1, false);
				$has_subcategory = $categories['total_results'];

				//Store in memcache for 10 minutes
				$memcache->add_or_update('Store/Categories/HasSub/ID_'.$category_id, $has_subcategory, $this->MEMCACHE_EXPIRY);
			}

			if($has_subcategory > 0)
			{
				return true;
			}
			return false;
		}

		//Function to get the parent category id of a sub category
		public function get_category($category_id)
		{
			$memcache = Memcached::get_instance();
			$store_category = $memcache->get('Store/Category/ID_'.$category_id);

			if(empty($store_category)) {
				$result = FusionRest::get_instance()->get(
							sprintf(FusionRest::KEYSPACE_STORE_CATEGORY, $category_id)
							);
				$store_category = new StoreCategory($result);

				//Store in memcache for 10 minutes
				$memcache->add_or_update('Store/Category/ID_'.$category_id, $store_category, $this->MEMCACHE_EXPIRY);
			}

			return $store_category;
		}

		//Function to get recently purchased items. Returns only unique items
		public function get_recently_purchased($storeitem_type, $number_entries, $page, $username)
		{
			$params = array();

			//Depends on what type it is
			if($storeitem_type == 1)
			{
				//Virtual gift
				$query = "SELECT
						si.ID,
						si.Type,
						si.ReferenceID,
						si.price/sic.exchangerate*uic.exchangerate Price,
						usr.currency Currency,
						si.Name,
						si.Description,
						si.Status,
						si.NumAvailable,
						si.NumSold,
						si.Featured,
						si.CatalogImage,
						si.PreviewImage,
						si.ForSale,
						si.SortOrder,
						si.ExpiryDate,
						si.DateListed,
						si.migLevelMin,
						si.GroupID
						FROM
						storeitem si,
     					virtualgiftreceived vg,
						currency sic,
						currency uic,
						user usr
						WHERE
						si.type = 1
						AND vg.virtualgiftid = si.referenceid
						AND si.currency = sic.code
						AND uic.code = usr.currency
						AND si.forsale = 1
						AND usr.username = vg.sender
						AND vg.sender=?
						AND si.price >= " . Constants::get_value('STORE_GIFT_SHOWER_PRICE') .
						"GROUP BY vg.virtualgiftid ORDER BY vg.datecreated DESC
						";

				$params[] = array('s'=>$username);
			}
			else if($storeitem_type == 2)
			{
				//Avatar items
				//TODO:
				return;
			}
			else if($storeitem_type == 3 || $storeitem_type == 4)
			{
				//Emoticon pack
				//Super emoticon pack
				$query = "SELECT
						si.ID,
						si.Type,
						si.ReferenceID,
						si.price/sic.exchangerate*uic.exchangerate Price,
						usr.currency Currency,
						si.Name,
						si.Description,
						si.Status,
						si.NumAvailable,
						si.NumSold,
						si.Featured,
						si.CatalogImage,
						si.PreviewImage,
						si.ForSale,
						si.SortOrder,
						si.ExpiryDate,
						si.DateListed,
						si.migLevelMin,
						si.GroupID
						FROM
						storeitem si,
     					emoticonpackowner epo,
						currency sic,
						currency uic,
						user usr
						WHERE
						si.type = 3
						AND epo.emoticonpackid = si.referenceid
						AND si.currency = sic.code
						AND uic.code = usr.currency
						AND si.forsale = 1
						AND usr.username = epo.username
						AND epo.username=?
						AND si.price >= " . Constants::get_value('STORE_GIFT_SHOWER_PRICE') .
						"ORDER BY epo.emoticonpackid DESC
						";
				$params[] = array('s'=>$username);
			}
			else if($storeitem_type == 5)
			{
				//Themes
				//TODO:
				return;
			}
			else
			{
				return;
			}

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$this->auto_bind_params($stmt, $params);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();
			$stmt->data_seek(($page - 1) * $number_entries);
			$count = 1;

			$purchased = array();
			$store = new Store();
			$storeitems_ids = array();
			while( $stmt->fetch() && $count <= $number_entries)
			{
				$purchased[] = $store->get_item($row);
				$storeitems_ids[] = $row["ID"];
				$count += 1;
			}

			$num_rows = $stmt->num_rows();
			$total_pages = ceil($num_rows / $number_entries);

			$stmt->free_result();

			return array("total_pages"=> $total_pages, "total_results"=>$num_rows, "recent_purchases"=>$purchased,  "storeitemsids" => $storeitems_ids);
		}

		//Function get a specific store item
		public function get_item($storeitem_id, $username)
		{
			$result = FusionRest::get_instance()->get(
						sprintf(FusionRest::KEYSPACE_STORE_ITEM, $storeitem_id)
						);
			
			$store = new Store();
			$store->convert_store_data_fusion_to_sql($result);
			$item = $store->get_item($result);

			return $item;
		}

		//Function to get the categories that the item belong to
		public function get_categories_where_item_belong($item_id)
		{
			$memcache = Memcached::get_instance();
			$categories = $memcache->get('Store/Item/BreadCrumb/ID_'.$item_id);

			if(empty($categories))
			{
				$query = "SELECT sc.* from storeitemcategory sic, storecategory sc
						   WHERE sic.storeitemid=? AND sic.storecategoryid = sc.id";

				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->bind_param('i', $item_id);
				$stmt->execute();

				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				$stmt->store_result();

				$categories = array();
				while( $stmt->fetch())
				{
					$category_id = $row['ID'];
				}

				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();

				$categories[] = $this->get_category($category_id);

				//Store in memcache for 10 minutes
				$memcache->add_or_update('Store/Item/BreadCrumb/ID_'.$item_id, $categories, $this->MEMCACHE_EXPIRY);
			}

			return $categories;
		}

		//Get a list of currency
		public function get_currency_list()
		{
			$query = 'select currency from country group by currency';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();

			$currency_list = array();
			while( $stmt->fetch())
			{
				array_push($currency_list, $row["currency"]);
			}
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();

			return $currency_list;
		}

		// Get a list of category_id
		public function get_category_id_list()
		{
			$query = 'select `id` from storecategory order by `id` asc';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();

			$category_id_list = array();
			while( $stmt->fetch())
			{
				array_push($category_id_list, $row["id"]);
			}
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();

			return $category_id_list;
		}

		// Get a list of item_id
		public function get_item_id_list()
		{
			$query = 'select id from storeitem order by id asc';
			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->execute();

			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
			$stmt->store_result();

			$item_id_list = array();
			while( $stmt->fetch())
			{
				array_push($item_id_list, $row["id"]);
			}
			$stmt->free_result();
			$stmt->close();
			$this->closeSlaveConnection();

			return $item_id_list;
		}

		public function rate_item($session_username, $storeitem_id, $rating)
		{
			// Ensure that ratings are between 1 and 5
			if ($rating < 0 || $rating > 5)
				return;
			// Ensure that user has permission to like the store item. Default to 10
			$userDAO = new UserDAO();
			$reputation_level_permission = $userDAO->get_user_level_and_reputation_level_permission($session_username, 'PostCommentLikeUserWall');
			if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
				throw new Exception(sprintf(_('You must be migLevel %s or higher to like a store item'), $reputation_level_permission['required_level']));

			// Get userid from username
			$session_userid = $this->get_userid($session_username);
			if (!isset($session_userid))
				return;

			$query = "INSERT INTO storeitemrating (storeitemid, userid, datecreated, rating) VALUES (?, ?, NOW(), ?) ON DUPLICATE KEY UPDATE rating = ?";
			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			$stmt->bind_param("iiii", $storeitem_id, $session_userid, $rating, $rating);
			$stmt->execute();
			$affected_rows = $stmt->affected_rows;
			$stmt->close();
			// Update the storeitemratingsummary table only if there is change
			if ($affected_rows > 0) {
				$query = "SELECT CAST(SUM(rating) AS UNSIGNED INTEGER) AS total, CAST(COUNT(*) AS UNSIGNED INTEGER) AS numratings FROM storeitemrating WHERE storeitemid = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("i", $storeitem_id);
				$stmt->execute();
				$stmt->bind_result($total, $numratings);
				$stmt->fetch();
				$stmt->close();

				$new_total = is_numeric($total) ? $total : 0;
				$new_numratings = is_numeric($numratings) ? $numratings : 0;
				$new_average = number_format($new_total/$new_numratings, 5);

				$query = "INSERT INTO storeitemratingsummary (storeitemid, average, total, numratings) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE average = ?, total = ?, numratings = ?";
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
				$stmt->bind_param("idiidii", $storeitem_id, $new_average, $new_total, $new_numratings, $new_average, $new_total, $new_numratings);
				$stmt->execute();
				$stmt->close();
			}
			$this->closeMasterConnection();
			// Return the new ratings
			return $this->get_item_ratings($storeitem_id, true);
		}

		public function get_item_ratings($storeitem_id, $from_master = false)
		{
			// Return the new ratings
			$query = "SELECT average, total, numratings FROM storeitemratingsummary WHERE storeitemid = ?";
			if($from_master) {
				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
			} else {
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			}
			$stmt->bind_param("i", $storeitem_id);
			$stmt->execute();
			$stmt->bind_result($average, $total, $num_ratings);
			$stmt->fetch();
			$stmt->free_result();
			$stmt->close();
			if($from_master) {
				$this->closeMasterConnection();
			} else {
				$this->closeSlaveConnection();
			}
			return array(
				'average' => $average,
				'total' => intval($total),
				'numratings' => intval($num_ratings)
			);
		}

		public function get_items_ratings($storeitem_ids)
		{
			if(is_array($storeitem_ids) && !empty($storeitem_ids)) {
				$storeitem_ids = array_map('intval', $storeitem_ids);
				$storeitem_ids_sql = implode(',', $storeitem_ids);
				// Return the new ratings
				$query = "SELECT storeitemid, average, total, numratings FROM storeitemratingsummary WHERE storeitemid IN($storeitem_ids_sql)";
				$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
				$stmt->execute();
				$storeitem_ratings = array();
				$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);
				while( $stmt->fetch())
				{
					$storeitem_ratings[$row['storeitemid']] = array('average' => $row['average'], 'total' => intval($row['total']), 'numratings' => intval($row['numratings']));
				}
				$stmt->free_result();
				$stmt->close();
				$this->closeSlaveConnection();
				return $storeitem_ratings;
			}
		}

		// Function to search store items
		public function search_items($search, $username, $type, $offset, $number_of_entries)
		{
			// Determine To Search Within Type Or Search All Types
			$type = intval($type);
			$storeitem_type = '';
			if($type > 0)
				$storeitem_type = sprintf('si.type = %s AND', $type);

			// We Are Using sprintf Instead of Prepared Statement Because We Need To Execute 2 Queries In One Connection
			$query = sprintf(
					"SELECT SQL_CALC_FOUND_ROWS
						si.ID,
						si.Type,
						si.ReferenceID,
						si.Name,
						si.Description,
						si.price/sic.exchangerate*uic.exchangerate Price,
						usr.currency Currency,
						si.Status,
						si.NumAvailable,
						si.NumSold,
						si.Featured,
						si.CatalogImage,
						si.PreviewImage,
						si.ForSale,
						si.SortOrder,
						si.ExpiryDate,
						si.DateListed,
						si.migLevelMin,
						si.GroupID,
						sir.Average AS RatingsAverage,
						sir.Total AS RatingsTotal,
						sir.NumRatings
					FROM
						storeitem si LEFT JOIN storeitemratingsummary sir ON si.id = sir.storeitemid,
						currency sic,
						currency uic,
						user usr
					WHERE
						si.status = 1
						AND si.currency = sic.code
						AND si.forsale = 1
						AND si.name LIKE ('%%%s%%')
						AND %s
						uic.code = usr.currency
						AND usr.username = '%s'
						AND si.price >= " . Constants::get_value('STORE_GIFT_SHOWER_PRICE') . " 
					ORDER BY
						si.featured DESC,
						si.datelisted DESC,
						si.numsold DESC
					LIMIT %s, %s;
					SELECT FOUND_ROWS()",
					$this->getSlaveConnection()->escape_string($search),
					$storeitem_type,
					$this->getSlaveConnection()->escape_string($username),
					intval($offset), intval($number_of_entries));

			$storeitems = array();
			$store = new Store();
			$total_count = 0;

		    if($this->getSlaveConnection()->multi_query($query) && $results = $this->getSlaveConnection()->store_result())
		    {
		        // First Query Fetch The Store Item Objects
		        while ($row = $results->fetch_array(MYSQLI_ASSOC))
		        {
		            $storeitems[] = $store->get_item($row);
		        }

		        // The Second Query Contains The Total Number Of Rows Returned Without Being Restricted By The LIMIT Clause
		        $this->getSlaveConnection()->next_result();
		        list($total_count) = $this->getSlaveConnection()->store_result()->fetch_row();
		    }

			$this->closeSlaveConnection();

			return array('storeitems' => $storeitems, 'total_storeitems' => $total_count);
		}
	}
?>