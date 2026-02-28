<?php $title = "Merchants"; ?>
<?php $subtitle = "Community"; ?>

<?php include("../includes/head.php") ?>
<?php include("../includes/logo_login.php") ?>
<?php $navdown = 1; ?>
<?php include("../includes/nav.php") ?>
<?php $subnavdown = 1; ?>
<?php include("../includes/subnav_wim.php") ?>
<script type="text/javascript">
<!--
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
<body onLoad="MM_preloadImages('../images/wim/join_button_small_on.gif')">
<div id="content">

<table id="twocols">
<tr>
<td valign="top" class="lftcol">

<br/><img src="/images/spacer.gif" width="162" height="1"/>
<?php include("../includes/join_today_inc.php") ?>

</td>
<td valign="top" class="midcol">

	<table width="auto">
	<tr>
	<td valign="top">
	<h1>Community</h1>
	<h2>Welcome to migme</h2>
	<h3>migme is the first global mobile community, bringing the power of the Internet to your mobile phone. </h3>
	<p>With migme, you're connected to everyone around the world through the most popular online services: from IM, chat rooms and SMS, to 	email, photo-sharing, and of course inexpensive, international calls, all from your mobile phone. </p>	</td>
	<td valign="top">
	<h2>The whole world on your mobile phone</h2>
	<p><img src="/images/wim/wim_world_bot.gif" width="323" height="162" alt=""></p>
	<p>&nbsp;</p>
	<p>&nbsp;</p>	</td>
	</tr>
	<tr>
	  <td colspan="2" valign="top">

      <table>
	<tr>
	<td valign="top"><h2>Chat Rooms</h2>
	Chat with anyone, anywhere, and in any language inside thousands of migme chat rooms on your mobile. Join an existing chat room and meet new people, or create one for you and some friends. Best of all, our chat rooms are absolutely free!<br/>
(Help keep our chat rooms safe. See <a href="http://blog<?=$$session_cookie_domain?>/support/mig33-community">chat room safety tips</a>.)</td>
	<td valign="top"><img src="/images/wim/menu_chatrooms.gif" width="311" height="179" alt=""></td>
	</tr>
	</table>
	<table>
	<tr>
	<td valign="top" colspan="2">
	<h2>migme IM</h2>
	</td>
	</tr>
	<tr>
	<td valign="top">
	<h3>Instant Messaging</h3>
	<p>With migme, you can chat online in real-time with millions of other migme users. Check your buddy list to see who's available, or block users to protect your privacy. You can also use compatible instant messaging services like <a href="connectivity.php">MSN, Yahoo! and AIM</a> on your phone, or IM friends from your PC.</p></td>
	<td valign="top"><img src="/images/wim/menu_mig33im.gif" width="116" height="135" alt="" align="right"></td>
	<td valign="top">
	<h3>Profiles</h3>
	Plus, you can create your own profile, tell the world about yourself and meet friends with similar interests. And if you refer a friend to join migme, it's even easier to share profiles, photos, scrapbooks and more with them.</td>
	<td valign="top"><img src="/images/wim/menu_profile.gif" width="123" height="147" alt="" align="right"></td>
	</tr>
	</table>
	<br/>
	<table>
	<tr>
	<td width="505" valign="top"><h3>Photo Sharing</h3>
	Send photos directly from your mobile phone to online buddies. These 	pictures are automatically saved to your online scrapbook, which you can view and organize from your phone or PC.</td>
	<td width="459" valign="top"><img src="/images/wim/menu_photo.gif" width="459" height="149" alt=""></td>
	</tr>
	<tr>
	  <td colspan="2" valign="top">
	    <table width="99%" border="0">
          <tr>
            <td width="59%"><h2>For Members Only</h2>
	    As a migme member, you&rsquo;ll have access to our members&rsquo; only website where you can share and organize photos, create and edit your profile, recharge your account, and even refer friends  to become members. Everything you can do on your phone, you can do on our site, like accessing IM, email, and chat rooms, or making inexpensive calls and SMS.</td>
            <td width="41%" align="right"><img src="../images/wim/ajax_pages_sample.jpg" height="243" width="347" /></td>
          </tr>
        </table></td>
	  </tr>
	</table>
      </td>
	  </tr>
	<table>



	</td>
	</tr>
	</table>
</table>
</div>

<?php include("../includes/footer.php") ?>