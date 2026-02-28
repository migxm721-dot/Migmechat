<?php

global $buzzcity_conf;
$now = time();

$ad_qs = array('partnerid' => $buzzcity_conf['partner_id'], 'get' => 'mweb', 'ts' => $now);
$click_qs = array('partnerid' => $buzzcity_conf['partner_id'], 'ts' => $now);

$ad_qs['bn'] = $click_qs['bn'] = isset($banner_num) ? $banner_num : 1;

// todo verify if the conf uri contain an existing query string
$ad_uri = $buzzcity_conf['ad_uri'] . '?' . http_build_query($ad_qs);
$click_uri = $buzzcity_conf['click_uri'] . '?' . http_build_query($click_qs);



?><p align="center"><a id="buzzcity_<?=$ad_qs['bn']?>" class="buzzcity" href="<?=$click_uri?>" target="_blank"><img src="<?=$ad_uri?>" alt="" /></a></p>
