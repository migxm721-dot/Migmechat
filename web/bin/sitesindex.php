<?php

/**
 * Possible scenarios:
 *
 * JavaScript: replace with restful string
 * PHP: replace with get_controller_action_url()
 * PHP with string
 * HTML: replace with <?=get_controller_action_url()?>
 *
 **/

//include_once 'sites/common/utilities.php';
include_once 'sites/common/query_string_field.php';
class Restifier extends QueryStringField
{
	public static $filelimit = -1;
	private $file = null;
	private $filetype = null;
	private $changecounter = 0;
	private $replacetype = null;

	public function __construct($file)
	{
		$this->file = $file;
		$this->filetype = pathinfo($this->file, PATHINFO_EXTENSION);
		$file_content = file_get_contents($this->file);
		if (! preg_match_all('/'
				. '(?P<server_root>\$server_root\s*)?'
				. '(?P<predot>\.\s*)?'
				. '(?P<prequote>[\'"])'
				. '(?P<server_root2><\?=\$server_root;?\?>\s*)?'
				. '(?P<url>\/sites\/index.php\?[^"\'\s]+[^=&])'
				. '(?P<postquote>[\'"])'
				. '(?P<postdot>\s*\.|\s*;)?'
				. '(?P<remainder>.*)'
				. '/i'
				, $file_content
				, $matches
				, PREG_SET_ORDER
			)
		)
			return null;

		//var_dump($matches);//echo json_encode($matches) . "\n";//print_r($matches);
		foreach ($matches as $match)
		{
			parse_str(parse_url($match['url'], PHP_URL_QUERY), $parse_str);
			//echo json_encode($parse_str) . "\n";//var_dump($parse_str);

			if (empty($parse_str['c'])
				|| empty($parse_str['a'])
				|| empty($parse_str['v'])
				|| isset($parse_str['<?php ']))
				continue;
			//if (pathinfo($file, PATHINFO_BASENAME) != $view . '.php') continue;

			$this->replacetype = $this->get_replacetype($match);
			$file_content = $this->replace($file_content, $match, $parse_str);
		}

		if ($this->changecounter) $this->save($file_content);
	}

	private function get_replacetype($match)
	{
		$type = 'html';

		if ($match['prequote'] != $match['postquote']) $type = 'php';

		if ((! empty($match['predot'])
			|| ! empty($match['postdot']))
			&& $match['prequote'] == $match['postquote']
			)
			$type = 'php_string';

		if ($this->filetype == 'js') $type = 'js';

		return $type;
	}

	private function replace($file_content, $match, $parse_str)
	{
		$pattern = $this->get_pattern($match) . $match['postdot'] . $match['remainder'];
		$replacement = $this->get_replacement($match, $parse_str) . $match['postdot'] . $match['remainder'];
		//echo $replacement . "\n";
		$this->changecounter++;
		return str_replace($pattern, $replacement, $file_content);
	}

	private function get_pattern($match)
	{
		if ($this->replacetype == 'html')
			return $match['server_root2'] . $match['url'] . $match['postquote'];

		if (! empty($match['server_root']))
			return $match['server_root'] . $match['predot'] . $match['prequote'] . $match['url'] . $match['postquote'];

		return $match['prequote'] . $match['server_root2'] . $match['url'] . $match['postquote'];
	}

	private function get_replacement($match, $parse_str)
	{
		$controller = $parse_str['c'];
		$action     = $parse_str['a'];
		$view       = $parse_str['v'];
		$attributes = $this->get_attributes($parse_str);
		switch($this->filetype)
		{
			case 'php':
				$url = preg_match('/sites\/view\/(\w+)\//i', $this->file, $file_controller)
					&& $file_controller[1] == $controller
					? "get_action_url('$action'$attributes)"
					: "get_controller_action_url('$controller', '$action'$attributes)";
				switch($this->replacetype)
				{
					case 'php':
						if (! empty($match['predot']))
						{
							$url = $url . " . " . $match['prequote'] . $match['postquote'];
						}
						else if (! empty($match['predot']))
						{
							$url = $match['prequote'] . $match['postquote'] . " . " . $url;
						}
						else
						{
							//single quotes
							$url = "' . " . $url . " . '";
							//double quotes
							//$url = '" . ' . $url . ' . "';
						}
						break;
					case 'html':
						$url = "<" . "?=" . $url . "?" . ">" . $match['postquote'];
						break;
					case 'php_string':
					default:
						break;
				}
				break;
			case 'js':
				$url = $match['prequote'] . "/sites/$view/$controller/$action$attributes" . $match['postquote'];
				break;
		}
		return $url;
	}

	private function get_attributes($parse_str)
	{
		unset($parse_str['c'], $parse_str['a'], $parse_str['v']);
		if (empty($parse_str)) return '';

		switch($this->filetype)
		{
			case 'php':
				foreach($parse_str as $key => $value)
				{
					unset($parse_str[$key]);
					$parse_str[self::get_full_field_name($key)] = $value;
				}

				$attributes = preg_replace('/\n/', '', var_export($parse_str, true));
				$attributes = preg_replace('/array \(\s+/', 'array(', $attributes);
				$attributes = preg_replace('/\',\)/', '\')', $attributes);
				$attributes = preg_replace('/\',  \'/', '\', \'', $attributes);
				$attributes = preg_replace('/\'(\$[\w\-\>]+|\d+)\'/', '\1', $attributes);
				$attributes = preg_replace('/\'\<\?\=([^?;]+);?\s*\?\>\'/', '\1', $attributes);
				$attributes = ", " . trim($attributes);
				//echo $attributes . "\n";
				break;
			case 'js':
				$attributes = '?' . http_build_query($parse_str);
				break;
		}
		return $attributes;
	}

	private static function get_full_field_name($field)
	{
		foreach (QueryStringField::$fields as $key => $value)
			if ($value == $field) return $key;

		return $field;
	}

	private function save($file_content)
	{
		echo $this->file . "\n";
		file_put_contents($this->file, $file_content);
		if (! --self::$filelimit) exit();
	}

	public static function get_unique_fields()
	{
		foreach(self::$fields as $desc => $short)
			if ($desc==$short)
				unset(self::$fields[$desc]);
		return self::$fields;
	}
}

if (in_array('unique', $argv))
{
	print_r(Restifier::get_unique_fields());
	exit;
}
else if ('exists'==$argv[1])
{
	array_shift($argv);
	array_shift($argv);

	$unique = Restifier::get_unique_fields();
	$unique_list = array_keys($unique) + array_values($unique);
	foreach ($argv as $v)
	{
		echo $v . ' ' . (in_array($v, $unique_list)?'1':'0') . "\n";
	}
	exit;
}

//php sitesindex.php sites/view --ignore-dir=view; git diff
array_shift($argv); // exclude PHP_SELF
$exec = "ack 'sites/index.php' -l " . implode(' ', $argv);
exec($exec, $files);//var_dump($files);

foreach ($files as $file)
{
	$replace = new Restifier($file);
}

exit();
$server_root = '';
?>

PHP String: <?php
/**sprintf('<a href="<?=get_controller_action_url('call', 'home', array('from' => 'call'))?>">%s</a>', $data);/**/
?>

Pure HTML:
<a href="/sites/index.php?c=account&a=recharge_credit&v=ajax">Choose another option &gt;&gt;</a><br/>

HTML with attributes and QueryStringField:
<small><a href="/sites/index.php?c=store&v=wap&a=view_item&itid=<?=$storeitem->id?>">Buy</a></small><br>

HTML with PHP shorttags:
<a href="/sites/index.php?c=help&a=main&v=ajax&pid=<?=WordPressDomain::$HELP_MIDLET_MIGLEVEL_PAGEID?>">[?]</a>

Cases where php was added in by mistake and no PHP syntax error was thrown:
$ ack "'[^'?]+<\?[p=]" $(git diff master.. --name-only)
<?php $mail = "<a href=\"".$server_root."/sites/index.php?c=help&a=contact_us&v=midlet&t=1\">contact@".$imap_domain."</a>"; ?>
<a href="<?=$server_root?>/sites/index.php?c=profile&a=home&v=midlet&username=<?=$leader['username'] ?>">
<a href="<?=$server_root?>/sites/index.php?c=store&a=view_item&v=midlet&itid=<?=$storeitem->id?><?=$username_query_string?>"><?=$storeitem->name?></a>&nbsp;
<a href="<?=$server_root?>/sites/index.php?c=store&a=view_item&v=midlet&itid=<?=$storeitem->id?><?=$username_query_string?>"><?=$storeitem->name?></a>&nbsp;
<p><a href="<?=$server_root?>/sites/index.php?c=store&a=featured&v=midlet&e=10&ty=<?=get_attribute_value('type')?><?=$username_query_string?>"><?=_('More Featured Items')?></a></p
<a href="/sites/index.php?c=store&a=show_with_category&v=wap&ty=1&catid=1<?=$numentries_string?><?=$show_preview?>">Gifts</a>&nbsp;|&nbsp;

Cases that're not covered:
<a href="/sites/index.php?c=help&a=merchant_help&v=wap&page_id=<?= Constants::get_value('HELP_ABOUT_MIG33_PAGE_ID'); ?>">About migme</a>
<div class="thumb"><a href="#" onClick="parent.window.mig33.reload_store_content_from_side('/sites/index.php?c=store&a=view_item&v=ajax&<?=get_field_name('item_id')?>=<?=$storeitem->id?><?=$username_query_string?>&ptype=<?=$type ?>&pcatid=<?=$category_id ?>&pentries=<?=$e ?>&ppage=<?=$current_page ?>&win_id=store-window', <?=$storeitem->type?>);return false;"><img src="<?=$storeitem->get_catalog_image_url_web()?>"></a></div>

<?=sprintf(_('<a href="%s">Go to Facebook</a>'), 'mig33:invokeNativeBrowser('.$server_root.'/sites/index.php?c=im&a=facebook_setup&v=wap)')?>
<?php
$my_photos = $server_root.'/sites/index.php?c=group&v=wap&a=choose_avatar&cid='.$group_id.'&f='.$from.'&room_name='.$room_name;
?>
