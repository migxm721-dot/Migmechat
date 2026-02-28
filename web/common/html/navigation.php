<tr>
				<td colspan="2" class="top_nav_cell">



						<?php
							if ($pageName === "home") {
						?>

						<a class="top_nav" href="<?=$actualPath?>/indexif.php" title="Home">

						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Home" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_Home"><span class="tab_text">Home</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Home" style="display: block;" />
						<?php
						} else {
						?>
						<a class="top_nav" href="<?=$actualPath?>/indexif.php" title="Home" onmouseover="showhideMenuItems('Home');" onmouseout="showhideMenuItems('Home');">
							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Home" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_Home"><span class="tab_text">Home</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Home" style="display: none;" />
						<?php
						}
						?>


					</a>



						<?php
							if ($pageName === "whatis") {
						?>
						<a class="top_nav" href="<?=$actualPath?>/what_is_mig33.php" title="What is migme?">

						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_WhatIs" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_WhatIs"><span class="tab_text">What is migme?</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_WhatIs" style="display: block;" />
						<?php
						} else {
						?>
						<a class="top_nav" href="<?=$actualPath?>/what_is_mig33.php" title="What is migme?" onmouseover="showhideMenuItems('WhatIs');" onmouseout="showhideMenuItems('WhatIs');">

							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_WhatIs" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_WhatIs"><span class="tab_text">What is migme?</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_WhatIs" style="display: none;" />
						<?php
						}
						?>



					</a>



					<?php
							if ($pageName === "join") {
						?>
						<a class="top_nav" href="<?=$actualPath?>/join_form.php" title="Join">
						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Join" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_Join"><span class="tab_text">Join</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Join" style="display: block;" />
						<?php
						} else {
						?>

						<a class="top_nav" href="<?=$actualPath?>/join_form.php" title="Join" onmouseover="showhideMenuItems('Join');" onmouseout="showhideMenuItems('Join');">

							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Join" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_Join"><span class="tab_text">Join</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Join" style="display: none;" />
						<?php
						}
						?>


					</a>

						<?php
							if ($pageName === "affiliates") {
						?>
						<a class="top_nav" href="<?=$actualPath?>/affiliates.php" title="Affiliates">
						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Affiliates" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_Affiliates"><span class="tab_text">Affiliates</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Affiliates" style="display: block;" />
						<?php
						} else {
						?>
						<a class="top_nav" href="<?=$actualPath?>/affiliates.php" title="Affiliates" onmouseover="showhideMenuItems('Affiliates');" onmouseout="showhideMenuItems('Affiliates');">
							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Affiliates" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_Affiliates"><span class="tab_text">Affiliates</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Affiliates" style="display: none;" />
						<?php
						}
						?>
					</a>



						<?php
							if ($pageName === "help") {
						?>
						<a class="top_nav" href="<?=$wikiPath?>/?q=node/13" title="Help">

						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Help" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_Help"><span class="tab_text">Help</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Help" style="display: block;" />
						<?php
						} else {
						?>
						 <a class="top_nav" href="<?=$wikiPath?>/?q=node/13" title="Help" onmouseover="showhideMenuItems('Help');" onmouseout="showhideMenuItems('Help');">

							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Help" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_Help"><span class="tab_text">Help</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Help" style="display: none;" />
						<?php
						}
						?>


					</a>



					<?php
							if ($pageName === "about") {
						?>
						<a class="top_nav" href="<?=$actualPath?>/about.php" title="About Us">

						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_About" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_About"><span class="tab_text">About Us</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_About" style="display: block;" />
						<?php
						} else {
						?>
						<a class="top_nav" href="<?=$actualPath?>/about.php" title="About" onmouseover="showhideMenuItems('About');" onmouseout="showhideMenuItems('About');">

							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_About" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_About"><span class="tab_text">About Us</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_About" style="display: none;" />
						<?php
						}
						?>

					</a>

					<?php
							if ($pageName === "contact") {
						?>
						<a class="top_nav" href="<?=$actualPath?>/contact.php" title="Contact">

						<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Contact" style="display: block;" />
						<span class="tab_text_block_selected" id="menu_Contact"><span class="tab_text">Contact</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Contact" style="display: block;" />
						<?php
						} else {
						?>
						<a class="top_nav" href="<?=$actualPath?>/contact.php" title="Contact" onmouseover="showhideMenuItems('Contact');" onmouseout="showhideMenuItems('Contact');">

							<img class="fl_left" src="<?=$actualPath?>/img/leftarctab.gif" height="27" width="5" alt="" id="menu_left_image_Contact" style="display: none;" />
						<span class="tab_text_block_deselected" id="menu_Contact"><span class="tab_text">Contact</span></span>
							<img class="fl_left" src="<?=$actualPath?>/img/rightarctab.gif" height="27" width="5" alt="" id="menu_right_image_Contact" style="display: none;" />
						<?php
						}
						?>

					</a>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="undertab_row_1"></td>
			</tr>

			<?php
			if (isset($_SESSION['user'])) {
			?>

			 <tr>
        <td colspan="2" class="undertab_row_3">
          <div class="member_spacer">&nbsp;</div>

          <a class="mem_nav_link" style="<?php if ($pageName === "mem_home") { echo "color: white;";} ?>" href="<?=$actualPath?>/member/view_profile.php" title="My Profile">My Profile</a>
		    <a class="mem_nav_link" style="<?php if ($pageName === "mem_comm") { echo "color: white;";} ?>" href="<?=$actualPath?>/member/mig33_community.php" title="migme Community">migme Community</a>
          <!-- <a class="mem_nav_link" style="<?php if ($pageName === "mem_invite") { echo "color: white;";} ?>" href="<?=$actualPath?>/member/invite_friend.php" title="Recharge">Invite Friend</a> -->
         <a class="mem_nav_link" style="<?php if ($pageName === "mem_voipbar") { echo "color: white;";} ?>" href="<?=$actualPath?>/member/voip_toolbar.php" title="VoIP Toolbar">VoIP Toolbar</a>
		 <?php if ( ($_SESSION['user']['type'] == 'MIG33_AFFILIATE') || ($_SESSION['user']['type'] == 'MIG33_MASTER_DISTRIBUTOR') ) { ?>
		<a class="mem_nav_link" style="<?php if ($pageName === "mem_affiliate") { echo "color: white;";} ?>" href="<?=$actualPath?>/member/affiliate_home.php" title="Affiliate Resource Center">Affiliate Resource Center</a>
         <?php } ?>
         <!--<a class="mem_nav_link" style="" href="#" title="Contact List">Contact List</a>-->
        </td>
      </tr>
      <tr>


			<?php } else { ?>
					<tr>
						<td colspan="2" class="undertab_row_2">&nbsp;</td>
					</tr>

			<?php } ?>

			<!-- START OF BODY -->
			<?php

			$determinePage =  strrchr($_SERVER["PHP_SELF"],"/");
			//if index.php add class. Else no need.
			if ((strpos($determinePage,"index.php")) || (strpos($determinePage,"indexif.php"))) {
			?>
				<td class="call_card_cell">
			<?php
			} else {
			?>


				<td>

			<?php } ?>
