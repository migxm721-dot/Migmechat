<?php
$arr_bgs = array('url(/images/backgrounds/custom_bg.jpg) no-repeat top left #e2e0c7',
                'url(/images/backgrounds/grass_bg_WR.jpg) no-repeat top left #5e7b39',
                'url(/images/backgrounds/scratchboard_bg_WR.jpg) no-repeat top left #6e6666',
                'url(/images/backgrounds/sky_bg_WR.jpg) no-repeat top left #c5ccd4');
$arr_logos = array('/images/global/mig33_logo_grass.gif',
                '/images/global/mig33_logo_grass.gif',
                '/images/global/mig33_logo_scratch.gif',
                '/images/global/mig33_logo_grass.gif');

function getRandomIndex($arr)
{
	$arrlen = sizeof($arr);
	$random = (rand(0,$arrlen-1));
	return $random;
}

$index = getRandomIndex($arr_bgs);
//echo("getRandomBGIndex:". $index);
$bg = $arr_bgs[$index];
$logo = $arr_logos[$index];
?>