// Regex
var numbers_regex = /^[0-9]+$/;
var alphanumeric_regex = /^[a-zA-Z0-9]+$/;
var alphabets_regex =/^[a-zA-Z]+$/;
var username_regex  = /^[a-zA-Z]{1}(\.{0,1}[\w-])+$/;
var email_regex = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;

// Replace suggested username to the username text input box
function replace_username(username) {
	$("#username").val(username);
	$("#username_status").html("");
	$("#username_suggestions").html("");
	$("#registerBox").css("height", "580px");
}

// Set Username to taken
function set_username_status(available) {
	if(available === 0) {
		$("#username_taken").val("1");
	} else {
		$("#username_taken").val("0");
	}
}

// Generate and populate suggested usernames to the user
function suggest_usernames(json) {
	var username_status= '';
	var username_suggested = '';
	set_username_status(json.available);
	if(json.available === 0)  {
		username_status = '<span style="color: red;">Username is already in use.</span>';
		username_suggested += "<div>Suggested usernames:</div>"
		$.each(json.usernames, function(key, value){
			username_suggested += '<label class="radio"><input type="radio" name="username_suggest" id="username_suggest_' + key +'" value="' + value + '" onclick="replace_username(\'' + value + '\');" />' + value + "</label>";
		});
	} else {
		username_status = '<span style="color: green;">Username is available</span>';
	}
	$("#username_status").html(username_status);
	$("#username_suggestions").html(username_suggested);
	$("#username_loading").hide();
}

//AJAX to check username if username is available
function is_username_available(username)
{
	var json = $.ajax({
		url: '/sites/corporate/registration/check_username'
		, cache: false
		, async: false
		, data: {"checkonly": "1", username: username}
		, dataType: "json"
	});
	json = JSON.parse(json.responseText).data;
	var is_available = parseInt(json.available, 10);
	return is_available;
}

// Validate Username Field
function validate_username_field(username, validate_min_length) {
	var error_username = '';
	if(username == '') {
		error_username += 'Username is required.<br />';
	} else {
		if(!isNaN(username.substr(0, 1))) {
			error_username += 'Username must start with a letter.<br />';
		} else if(username.length < 6 && validate_min_length) {
			error_username += 'Username must be between 6 and 20 characters long.<br />';
		} else if(username.length > 20) {
			error_username += 'Username must be between 6 and 20 characters long.<br />';
		} else if(!username_regex.test(username)) {
			error_username += 'Username contains illegal characters.<br />';
		}
	}
	return error_username;
}

// AJAX to check username availiblity
function check_username_availability(username)
{
	var error_username = validate_username_field(username, 1);
	$("#username_status, #username_suggestions, #error_username").html("");
	if (error_username != "") {
		$("#error_username").html(error_username);
	} else {
		$("#username_loading").show();
		$.ajax({
			url: '/sites/corporate/registration/check_username'
			, data: {username: username}
			, success: suggest_usernames
			, dataType: "json"
		});
	}
}

// Refreshes Captcha
function refresh_captcha()
{
	$("#captcha_loading").show();
	$.ajax({
		  url: '/sites/corporate/registration/refresh_captcha'
		, dataType:'json'
		, success: function(json){
			$("#session_code").val(json.captcha_session_code);
			$("#captcha_image").attr("src", json.captcha_image_path);
			$("#captcha_loading").hide();
		}
    });
    return false;
}



// Verify Captcha
/**
function verify_captcha() {
	var json = $.ajax({ url: "/registration/verify_captcha.php", global: false, async: false, type: "GET", data: ({ session_code: $("#session_code").val(), captcha: $("#captcha").val() }), dataType: "json" }).responseText;
	json = JSON.parse(json);
	var verified = parseInt(json.verified, 10);
	return verified;
}
/**/

// Validate registration/resend_smscode.php
function validate_resend_smscode_form() {
	// Error Variables
	var error = false;
	var error_username = '';
	var error_mobile= '';
	// Text Fields Variables
	var username = $.trim($("#username").val());
	var mobile = $.trim($("#mobile").val());
	// Validate Username
	error_username = validate_username_field(username, 1);
	if(error_username != '') {
		$("#error_username").html(error_username);
		error = true;
	} else {
		$("#error_username").html("");
	}
	// Validate Mobile
	if(mobile == '') {
		error_mobile += 'Mobile number is required<br />';
	} else {		
		if(!numbers_regex.test(mobile)) {
			error_mobile += 'Mobile number contains illegal characters<br />';
		}
	}
	if(error_mobile != '') {
		$("#error_mobile").html(error_mobile);
		error = true;
	} else {
		$("#error_mobile").html("");	
	}
	if(error) {
		return false;
	} else {
		return true;
	}
}

// Validate registration/register_successful.php
function validate_activate_form() {
	// Error Variables
	var error = false;
	var error_smscode = '';
	// Text Fields Variables
	var smscode = $.trim($("#smscode").val());
	// Validate SMS Code
	if(smscode == '') {
		error_smscode += 'SMS Code is required<br />';
	} else {		
		if(!numbers_regex.test(smscode)) {
			error_smscode += 'SMS Code contains illegal characters<br />';
		}
	}
	if(error_smscode != '') {
		$("#error_smscode").html(error_smscode);
		error = true;
	} else {
		$("#error_smscode").html("");	
	}
	if(error) {
		return false;
	} else {
		return true;
	}
}

// Validate registration/auth_username.php
function validate_auth_username_form() {
	// Error Variables
	var error = false;
	var error_smscode = '';
	var error_username = '';
	// Text Fields Variables
	var smscode = $.trim($("#smscode").val());
	var username = $.trim($("#username").val());
	// Validate Username
	error_username = validate_username_field(username, 1);
	if(error_username != '') {
		$("#error_username").html(error_username);
		error = true;
	} else {
		$("#error_username").html("");
	}
	// Validate SMS Code
	if(smscode == '') {
		error_smscode += 'SMS Code is required<br />';
	} else {		
		if(!numbers_regex.test(smscode)) {
			error_smscode += 'SMS Code contains illegal characters<br />';
		}
	}
	if(error_smscode != '') {
		$("#error_smscode").html(error_smscode);
		error = true;
	} else {
		$("#error_smscode").html("");	
	}
	if(error) {
		return false;
	} else {
		return true;
	}
}

// Validate register.php	
function validate_registration_form(is_email_registration)
{
	// Error Variables
	var error = false;
	var error_username = '';
	var error_password = '';
	var error_repassword = '';
	if (is_email_registration)
	{
		var error_email = '';
	}
	else
	{
		var error_mobile = '';
		var error_remobile = '';
		var error_dob = '';
	}
	// Text Fields Variables
	var username = $.trim($("#username").val());
	var password = $.trim($("#password").val());
	var repassword = $.trim($('input[name="repassword"]').val());
	if (is_email_registration)
	{
		var email = $.trim($('input[name="email"]').val());
	}
	else
	{
		var mobile = $.trim($("#mobile").val());
		var remobile = $.trim($("#remobile").val());
		var dob_year = parseInt($('select[name="dob-year"]').val(), 10);
		var dob_month = parseInt($('select[name="dob-month"]').val(), 10)-1;
		var dob_day = parseInt($('select[name="dob-day"]').val(), 10);
		var dob = new Date(dob_year, dob_month, dob_day);
	}
	var captcha = $.trim($("#captcha").val());
	// Remove Username Status
	$("#username_status").html("");
	// Validate Username
	error_username = validate_username_field(username, 1);
	if (error_username == '' && username != "" && ! is_username_available(username))
	{
		error_username += 'Username is already in use<br />';
	}
	if (error_username != '') error = true;
	$("#error_username").html(error_username);
	// Validate Password
	if (password == '')
	{
		error_password += 'Password is required.<br />';
	}
	else
	{
		if (password.length < 6)
		{
			error_password += 'Password must be at least 6 characters long.<br />';
		}
		else if (!alphanumeric_regex.test(password))
		{
			error_password += 'Password contains illegal characters.<br />';
		}
		else if (alphabets_regex.test(password) || numbers_regex.test(password))
		{
			error_password += 'Password must contain at least 1 letter and 1 number.<br />';
		}
		else if (username == password)
		{
			error_password += 'Password must NOT be similar to username.<br />';
		}
	}
	if (error_password != '') error = true;
	$("#error_password").html(error_password);
	// Validate password
	if (repassword == '')
	{
		error_repassword += 'Please re-enter your password.<br />';
	}
	else if (password != repassword)
	{
		error_repassword += 'Password do not match.<br />';
	}
	if (error_repassword != '') error = true;
	$("#error_repassword").html(error_repassword);
	if (is_email_registration)
	{
		// Validate Email
		if (email == '')
		{
			error_email += 'Sorry. You need to fill in a valid email address before continuing.<br />';
		}
		else if (! email_regex.test(email))
		{
			error_email += 'Email address contains illegal characters.<br />';
		}
		if (error_email != '') error = true;
		$("#error_email").html(error_email);
	}
	else
	{
		// Validate Mobile
		if (mobile == '')
		{
			error_mobile += 'Mobile number is required.<br />';
		}
		else if (!numbers_regex.test(mobile))
		{
			error_mobile += 'Mobile number contains illegal characters.<br />';
		}
		if (error_mobile != '') error = true;
		$("#error_mobile").html(error_mobile);
		// Validate ReMobile
		if (remobile == '')
		{
			error_remobile += 'Mobile number is required.<br />';
		}
		else if (mobile != remobile)
		{
			error_remobile += 'Mobile number does not match.<br />';
		}
		if (error_remobile != '') error = true;
		$("#error_remobile").html(error_remobile);
		// Validate Date of Birth
		if (dob_day!= dob.getDate() || dob_month != dob.getMonth())
		{
			error_dob = 'Date of birth is invalid.';
		}
		if(error_dob != '') error = true;
		$("#error_dob").html(error_dob);
	}
	// Validate Captcha
	if (captcha == '')
	{
		$("#error_captcha").html('Security code is required.<br />');
		error = true;
	} else {
		$("#error_captcha").html("");
	}
	return false == error;
}

$('.settings_form').ready(function(){
	$("#username").focus();
	$("#check_availability").click(function(event){
		event.preventDefault();
		username = $.trim($("#username").val());
		check_username_availability(username);
	});
	$('.refresh_captcha').click(refresh_captcha);
});
