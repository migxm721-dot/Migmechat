<?php
/**
include_once("../common/common-inc.php");
include_once("member2/emit.php");
include_once("member2/check.php");

$pf = $_GET['pf'];

emitHeader_min();
emitTitle("What is mig33");
**/
include_once("../common/common-inc.php");
include_once("wap_includes/wap_functions.php");

global $server_root;

emitHeader('What is mig33');
?>
	<div id="content">
		<div class="section">
			<p>mig33 adalah komunitas global terbesar yang membawakan fitur-fitur internet ke handphone kamu.</p><br/>
			<p><a href="http://m.mig33.com/indosat">Download mig33</a></p><br/>
			&gt;&nbsp;Chatting dengan jutaan anggota mig33 di seluruh dunia<br/>
			&gt;&nbsp;Berbagi cerita dengan teman-teman dengan memperbaharui status<br/>
			&gt;&nbsp;Telepon murah ke telepon manapun, di mana pun, dan kapan pun!<br/>
			&gt;&nbsp;SMS teman-temanmu dengan harga tetap yang murah<br/>
			&gt;&nbsp;Tunjukkan kepribadianmu dengan tema, wallpaper, dan ringtones yang keren<br/>
			&gt;&nbsp;Ekspresikan dirimu dengan segudang emoticon<br/>
			&gt;&nbsp;Berbagi foto dengan teman-temanmu dan simpan di internet<br/>
			&gt;&nbsp;Dapatkan kredit tambahan gratis dengan mengundang teman-temanmu untuk bergabung<br/>

			<p align="center">
				<a href="about_more.php?q=1"><img src="img/1_4.png" height="45" width="37"></img></a>
				<a href="about_more.php?q=2"><img src="img/2_4.png" height="45" width="37"></img></a>
			</p>
			<p align="center">
				<a href="about_more.php?q=3"><img src="img/3_4.png" height="45" width="37"></img></a>
				<a href="about_more.php?q=4"><img src="img/4_4.png" height="45" width="37"></img></a>
			</p>
			<p><a href="<?php echo get_server_root(); ?>/sites/index.php?c=registration&a=register&v=wap"><b>Gabung Sekarang</b></a></p>

			<p><a href="http://m.mig33.com/indosat">Kembali</a></p>
		</div>
<?php
emitFooter();
?>
	</div>
<?php
emitFooter_end();
?>