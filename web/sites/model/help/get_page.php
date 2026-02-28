<?php
	fast_require("MobileTranslator", get_library_directory() . "/translator/translator/mobile_translator.php");

	fast_require("DataTranslator", get_library_directory() . "/translator/translator/data_translator.php");

	fast_require("TouchTranslator", get_library_directory() . "/translator/translator/touch_translator.php");

	fast_require("WordPress", get_library_directory() . "/wordpress/wordpress.php");
	fast_require("WordPressDomain", get_domain_directory() . "/wordpress.php");

	class GetPageModel extends Model
	{
		public function get_data($model_data)
		{
			$page_id = get_attribute_value('page_id', 'integer', get_attribute_value('page_id', 'integer', 0));

			if(is_wap_view() && $page_id == 0)
			{
				$page_id = WordPressDomain::$HELP_WAP_HOME_PAGEID;
			}
			else if((is_midlet_view() || is_mre_view()) && $page_id == 0)
			{
				$page_id = WordPressDomain::$HELP_MIDLET_HOME_PAGEID;
			}
			else if(is_touch_view() || is_json_view() || is_blackberry_view() || is_ios_view())
			{
				if ($page_id == 0)
					$page_id = WordPressDomain::$HELP_TOUCH_HOME_PAGEID;
			}

			$from = get_attribute_value('from');

			$content = WordPress::get_instance()->get_page_content($page_id);

			if (is_midlet_view() || is_mre_view())
			{
				$translator = new MobileTranslator();
				$content = $translator->parse($content, $model_data);
			}
			else if (is_touch_view() || is_blackberry_view() || is_ios_view())
			{
				$translator = new TouchTranslator();
				$content = $translator->parse($content);
			}

			$translator = new DataTranslator();
			$content = $translator->parse($content, $model_data);

			return array('page_id' => $page_id,
						 'content' => $content,
						 'from' => $from);
		}
	}
?>