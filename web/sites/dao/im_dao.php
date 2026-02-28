<?php
	fast_require('DAO', get_dao_directory() . '/dao.php');
	fast_require('IceDAO', get_dao_directory() . '/ice_dao.php');

	class ImDAO extends DAO
	{
		public function get_im_contacts($username,$im_type=0,$page=1,$num_entries=10)
		{
			//$memcache = Memcached::get_instance();
			//$ims = $memcache->get($username."/im");

			$imdata=array();

			try
			{
				$ice = new IceDAO();
				$ims = $ice->get_other_im_contacts();
			}
			catch(IceException $e)
			{
				$imdata['error']= $e->getMessage();
			}

			$im_details=array();

			$first=($page-1)*$num_entries;
			$limit=$page*$num_entries;

			if($im_type != 0)
			{
				$im_in_type = $ims;
				foreach($ims as $im)
					if($im->defaultIM == $im_type)
						$im_in_type[]=$im;
				$ims = $im_in_type;
			}
			$total_results=count($ims);
			$total_pages=ceil($total_results/$num_entries);
			$im_details=array_slice($ims, $first, $num_entries);

			$imdata['totalresults']=$total_results;
			$imdata['totalpages']=$total_pages;
			$imdata['page']=$page;
			$imdata['im_contacts']=$im_details;
			//$memcache->add_or_update($username."/im", $im, self::$MEMCACHE_EXPIRY);

			return $imdata;
		}
	}
?>