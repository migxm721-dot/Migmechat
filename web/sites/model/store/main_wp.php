<?php
	fast_require("MobileCommonTranslator", get_library_directory() . "/translator/translator/mobilecommon_translator.php");
	fast_require("MobileNewTranslator", get_library_directory() . "/translator/translator/mobilenew_translator.php");

	fast_require("WordPress", get_library_directory() . "/wordpress/wordpress.php");
	fast_require("WordPressDomain", get_domain_directory() . "/wordpress.php");
	fast_require("Memcached", get_framework_common_directory() . "/memcached.php");

	require_once(get_framework_common_directory() . "/pagelet_utilities.php");

	class MainWpModel extends Model
	{
		public function get_data($model_data)
		{
			$text = WordPress::get_instance()->get_page_content(WordPressDomain::$STORE_MIDLET_FRONT_PAGEID);

			// Process the data
			if (is_midlet_view() || is_mre_view())
			{
				$translator = new MobileNewTranslator();
				$text = $translator->parse($text);
				$translator = new MobileCommonTranslator();
				$text = $translator->parse($text);
			}

			return array("page_fragment" => $text);
		}
	}
?>