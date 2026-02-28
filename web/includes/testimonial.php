<?php

include_once("../common/common-config.php");

function showTestimonials_main($clientType) {
	global $server_root;
	if ($clientType == "WAP"){
		print '<p><small>Click a link to read what merchants have to say:</small></p>';
		print '<p><small><a href="?index=1">mig33 is easy to use &gt;&gt;</a></small></p>';
		print '<p><small><a href="?index=2">selling has earned me a profit &gt;&gt;</a></small></p>';
		print '<p><small><a href="?index=3">mig33 is 75% cheaper &gt;&gt;</a></small></p>';
		print '<p><small><a href="?index=4">my payments are handled quickly and with certainty &gt;&gt;</a></small></p>';
		print '<p><small><a href="?index=5">within two days i sold out &gt;&gt;</a></small></p>';
		print '<p><small><a href="?index=6">thank mig33 staff for helping me &gt;&gt;</a></small></p>';
		print '<p><small><a href="?index=7">i really enjoy selling mig33 &gt;&gt;</a></small></p>';
	} else {
		print '<p>Click a link to read what merchants have to say:</p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=1">mig33 is easy to use &gt;&gt;</a></p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=2">selling has earned me a profit &gt;&gt;</a></p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=3">mig33 is 75% cheaper &gt;&gt;</a></p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=4">my payments are handled quickly and with certainty &gt;&gt;</a></p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=5">within two days i sold out &gt;&gt;</a></p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=6">thank mig33 staff for helping me &gt;&gt;</a></p>';
		print '<p><a href="'.$server_root.'/midlet/member/testimonial.php?index=7">i really enjoy selling mig33 &gt;&gt;</a></p>';
	}
}

function showTestimonials_pages($clientType, $index) {
	global $server_root;
	settype($index, "int");
	print '<p><small><a href="'.$server_root.'/midlet/member/testimonial.php">Back</a></small></p>';

	print '<p>';
	if ($clientType == "WAP"){
		print '<small>';
	}

	if($index == 1){
		print '"mig33 is easy to use and provides very cheap calls. It is a very attractive service for creating customers. They like it due to the high quality service and ease to get connected. As a student, I have found being a merchant a great source to build a side business." - Afzal Russia';
	}else if($index == 2){
		print '"Selling mig33 credits has earned me a good profit. It has also made me a lot of new friends who love mig33. I\'m happy to be a mig33 merchant and will stay in this business as long as mig33 is in the Maldives." - Mohammad, Maldives';
	}else if($index == 3){
		print '"I have found it very easy to spread the knowledge of mig33 to many different people. They find launching calls is easy with many wide options. (SMS, mig33 client, WAP site, Web site). In my country, mig33 is 75% cheaper than other operators to most destinations. They like to use the chat and messaging features in mig33, making mig33 their preferred choice for talking with their friends and family." - Rama, Kazakhstan';
	}else if($index == 4){
		print '"Sometimes users are worried about buying credits from an international company because they aren\'t sure to trust it. However, as a merchant, I work directly with the mig33 team to ensure my payments are handled quickly and with certainty. In turn, my clients who buy credits can trust the service because they can buy directly from me. My credits never expire so this means I am free from fear of vanishing credits. It is easy to buy credits by bank and sell them by mobile, the faster you sell the faster you earn. The mig33 team has also helped &amp; guided me in making sales. My merchant program is very successful as far as I am concerned. I am now presently looking to expand to Ukraine. Before becoming a merchant in December 2006 I needed to ask for money from my family to support me. Now I am in a position that I can send money to them." - Rama, Kazakhstan';
	}else if($index == 5){
		print '"All my friends asked me where can they find mig33 credits, so I became interested in trying out the mig33 merchant program. At the start I bought $150 of credits and within two days I sold out. I bought again, this time $350 of credits, and announced my business on mig33. On the first day I received more than 20 calls and 50 SMS. I even had more than 30 mig33 users ask to add me on their buddy list. I sold out again in a few days. They included Indonesians and Filipinos working in my country wanting to buy credits because using mig33 for calls and SMS is much cheaper." - Shamril, Brunei';
	}else if($index == 6){
		print '"I would like to thank mig33 staff for helping me and giving me the strength to start this business as a merchant. It has been very successful for me. I am happy mig33 is recruiting merchants in many countries as mig33 is offering a very good service for users and they want to buy credits." - Mustafa, Kenya';
	}else if($index == 7){
		print '"After mig33 team started the advert for me to mig33 users it only took me few days to turn my small number of credits into a big revenue. I really enjoy selling mig33 and it is very easy for me." - Aden, South Africa';
	}

	if ($clientType == "WAP"){
		print '</small>';
	}
	print '</p>';
	print '<p><small><a href="'.$server_root.'/midlet/member/testimonial.php">Back</a></small></p>';
}

?>