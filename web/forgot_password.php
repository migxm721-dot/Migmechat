<?php
header('Location: /sites/corporate/forgot_password/details');
exit();

$headTitle = "Forgot your migme password?";
$pageTitle = "";
$globalNavSelected = "";
$metaDescription = "Forgot your migme password?";
$metaKeywords = "forgot migme password";

include_once("common/common-inc.php");
if($_POST){
	if(empty($_POST['mobile'])){
		$error = 'Please enter the mobile number you registered with migme';
	}else if(!is_numeric($_POST['mobile'])){
		$error = 'Please ensure the mobile number only contains numbers';
	} else {
		try{
			soap_call_ejb('forgotPasswordWithMobileNumber', array($_POST['mobile'], getRemoteIPAddress(), getMobileDevice(), getUserAgent()));
			$success = 'Your username and password has been sent to '.$_POST['mobile'];
		}catch(Exception $e){
			$error = 'Error: '.$e->getMessage();
		}
	}
}
include("includes_corporate/html_head.php");
?>
<body>
<div id="forgotPassword" class="pageContainer">

<?php include("includes_corporate/page_header.php"); ?>

<div id="pageContent" class="container_16 clearfix">

    <div id="content" class="grid_16">
    	<h2>Forgot your password?</h2>

        <form action="forgot_password.php" method="post">
        	<?php
				if(!empty($error)){
					echo '<div class="infoBox error"><p>'.$error.'</p></div>';
				}else if(!empty($success)){
					echo '<div class="infoBox success"><p>'.$success.'</p></div>';
				}
			?>
            <div class="fieldPair">
                <label for="mobileNum">Please Enter Your Mobile Number:</label>
                <input class="ctrl_field" type="text" name="mobile" id="mobileNum" size="15" value="<?= $_POST['mobile']?>" />
            </div>
            <div class="fieldPair">
                <div class="submitButton">
                	<input type="submit" value="Submit" />
                </div>
            </div>
    	</form>
        <br />
    </div> <!-- end content -->
</div> <!-- end pageContent -->

<?php include("includes_corporate/page_footer.php"); ?>

</div> <!-- end pageContainer -->

<script type="application/javascript">
	$(document).ready(function () {
		$("input#mobileNum").focus();
	});
</script>

<?php include("includes_corporate/footer_js.php"); ?>

</body>
</html>
