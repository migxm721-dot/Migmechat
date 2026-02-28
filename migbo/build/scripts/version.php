<?php
	if(!file_exists('.svn'))
		define('USING_SVN', false);
	else
		define('USING_SVN', true);

	define('MAX_MINOR_VERSION', 99);
	define('VERSION_LOCATION', '../application/config/version.txt');

	$mig33_svn_url = 'https://svn.projectgoth.com/svn/Mig33';
	$mig33_migbo_svn_url = $mig33_svn_url.'/migbo-web';
	$mig33_migbo_tags_svn_url = $mig33_migbo_svn_url.'/tags';
	
	// Tag Number
	$tag_number = get_tag_number();
	echo 'Latest Tag: '.$tag_number."\n";

	$rel_tag_number = get_rel_tag_number();
	echo 'Current RC Tag: '.$rel_tag_number."\n";
	
	if(!empty($tag_number) && !empty($rel_tag_number))
	{
		if(version_compare($rel_tag_number, get_version($tag_number, false), '=='))
		{
			$new_version = increment_version($tag_number, true);
			echo 'This release is a .1 release ('.$new_version.')'."\n";	
		}
		elseif(version_compare($rel_tag_number, $tag_number, '>'))
		{
			$new_version = increment_version($tag_number);
			echo 'This release is NEW release ('.$new_version.')'."\n";
		}
	}
	else
	{
		echo 'ERROR: Latest tag number or current RC tag number is missing'."\n";
	}
	
	if(isset($new_version))
	{
		if(write_version($new_version))
			echo 'Version number ('.$new_version.') written to '.VERSION_LOCATION."\n";
		else
			echo 'ERROR: Writing version number ('.$new_version.') to '.VERSION_LOCATION."\n";
	}
	else
	{
		echo 'ERROR: Version number is empty'."\n";
	}
	
	function get_tag_number()
	{
		$latest_tag = exec('svn list '.$GLOBALS['mig33_migbo_tags_svn_url'].' | tail -1 | cut -c5-');
		return substr($latest_tag, 0, -1);
	}
	
	function get_rel_tag_number()
	{
		if(USING_SVN)
		{
			$rel_tag = exec("svn info | grep '^URL:'");
		}
		else
		{
			$rel_tag = exec("git svn info | grep '^URL:'");
		}

		preg_match('/REL_(([0-9]+)\.([0-9]+))_RC/', $rel_tag, $matches);

		return $matches[1]; 
	}
	
	function write_version($version)
	{
		return file_put_contents(VERSION_LOCATION, $version);
	}
	
	function get_version($version, $include_revision = false)
	{
		$version = explode('.', $version);
		$major_version = intval($version[0]);
		$minor_version = intval($version[1]);
		$revision_version = intval($version[2]);
		
		if($include_revision)
			return $major_version.'.'.$minor_version.'.'.$revision_version;
		else
			return $major_version.'.'.$minor_version;
	}
	
	function increment_version($tag_number, $increment_revision_only = false)
	{
		$version = explode('.', $tag_number);
		$major_version = intval($version[0]);
		$minor_version = intval($version[1]);
		$revision_version = intval($version[2]);
		
		if($increment_revision_only)
		{
			$revision_version++;
			return $major_version.'.'.$minor_version.'.'.$revision_version;
		}
		else
		{
			$minor_version++;
			
			if($minor_version > MAX_MINOR_VERSION)
			{
				$major_version++;
				$minor_version = 0;
			}

			return $major_version.'.'.$minor_version;
		}
	}
?>