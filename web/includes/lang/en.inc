<?php

require_once('../../sites/common/constants.php');

/**************************
*
* Recharge 
*
***************************/

/** MONEYBOOKERS **/
define("MONEYBOOKERS_TITLE", "Moneybookers", true);
define("MONEYBOOKERS_PAYMENT_EMAIL", Constants::get_value('MONEYBOOKERS_PAYMENT_EMAIL'), true);
define("MONEYBOOKERS_PAYMENT_RECIEVED_EMAIL", Constants::get_value('MONEYBOOKERS_PAYMENT_EMAIL'), true);

define("MONEYBOOKERS_STEP", "How do i do a pay through moneybookers.com?", true);
define("MONEYBOOKERS_DEVICE_STEP_1", "On a computer, go to the moneybookers.com website. Register an account with them, if you don't already have one.", true);
define("MONEYBOOKERS_DEVICE_STEP_2", "Login to your account and click 'Send Money'.", true);
define("MONEYBOOKERS_DEVICE_STEP_3", "Enter amount in USD.", true);
define("MONEYBOOKERS_DEVICE_STEP_4", "Enter email: " . Constants::get_value('MONEYBOOKERS_PAYMENT_EMAIL') . ".", true);
define("MONEYBOOKERS_DEVICE_STEP_5", "In Subject enter: migme credits.", true);
define("MONEYBOOKERS_DEVICE_STEP_6", "In Explanation Note enter: migme username and registered mobile number.", true);
define("MONEYBOOKERS_DEVICE_STEP_7", "Submit.", true);

define("MONEYBOOKERS_TITLE_AJAX", "Buy Credits using  Moneybookers.com", true);
define("MONEYBOOKERS_STEP_1_AJAX", "Enter your moneybookers email address and amount in USD you want to send", true);
define("MONEYBOOKERS_STEP_2_AJAX", "You will be re-directed to moneybookers.com", true);
define("MONEYBOOKERS_STEP_3_AJAX", "Login into your moneybookers account", true);
define("MONEYBOOKERS_STEP_4_AJAX", "You will see confirmation page about your purchase", true);
define("MONEYBOOKERS_STEP_5_AJAX", "Your migme account will be credited in 1-2 days", true);

/** Bank Transfer **/
define("BANKTRANSFER_TITLE", "Local Bank Deposit", true);
define("BANKTRANSFER_STARTER_DISCOUNT", "Get started with at least US$5 and you can buy at a Starter Discount of 30%, for your first purchase.", true);

define("BANKTRANSFER_STEP", "How do I do a Local Bank Deposit?", true);
define("BANKTRANSFER_STEP_1_TITLE", "Get migme's bank account details and your Payment Reference number.", true);
define("BANKTRANSFER_STEP_1", "On the next page, we will ask you to fill in a short form. We will then provide you with a Payment Reference number to credit your payment.", true);
define("BANKTRANSFER_STEP_2_TITLE", "Go to a bank to make a deposit.", true);
define("BANKTRANSFER_STEP_2", "You can go to a local bank to make a deposit. You can also ask your local bank to transfer money from your bank account to migme's.", true);
define("BANKTRANSFER_STEP_3_TITLE", "Fill out a Bank Deposit form.", true);
define("BANKTRANSFER_STEP_4_TITLE", "Give the completed form and money to the bank staff.", true);
define("BANKTRANSFER_STEP_4", "They will give you a receipt and tell migme you have made a payment. Please keep this as proof of payment as we may require this to track your payment progress.", true);
define("BANKTRANSFER_STEP_5_TITLE", "We will send you an SMS when we have recharged your account.", true);
define("BANKTRANSFER_STEP_5", "It usually takes 3-5 days to credit your account with Local Bank Deposit. We will automatically apply your merchant discount. Once your account is recharged you are ready to start selling!", true);

define("BANKTRANSFER_STEP_1_AJAX", "Your account is credited after you make a bank deposit by selecting Next below.", true);
define("BANKTRANSFER_STEP_2_AJAX", "Go to a bank where migme has an account. We will tell you.", true);
define("BANKTRANSFER_STEP_3_AJAX", "Fill out a Bank Deposit form with the details we give you.", true);
define("BANKTRANSFER_STEP_4_AJAX", "Give the form and money to the bank staff. They will give you a receipt.", true);
define("BANKTRANSFER_STEP_5_AJAX", "We will send you an SMS when we have recharged your account (3-5 days).", true);

define("BANKTRANSFER_TITLE_NOT_ALLOWED", "Start a Local Bank Deposit", true);
define("BANKTRANSFER_NO_ALLOWED_STEP_1", "Get migme's bank account details and your Payment Reference number.", true);
define("BANKTRANSFER_NO_ALLOWED_STEP_2", "Please fill out this form and select Next to request bank deposit details.", true);

define("BANKTRANSFER_DEVICE_STEP_1", "Your account is credited after you make a bank deposit by selecting the link below.", true);
define("BANKTRANSFER_DEVICE_STEP_2", "Go to a bank where migme has an account. We will tell you.", true);
define("BANKTRANSFER_DEVICE_STEP_3", "Fill out a Bank Deposit form with the details.", true);
define("BANKTRANSFER_DEVICE_STEP_4", "Give the money to the bank staff. They will give you a receipt.", true);
define("BANKTRANSFER_DEVICE_STEP_5", "We will send you an SMS when we have recharged your account (3-5 days).", true);

/** Telegraphic Transfer **/
define("TT_TITLE", "Telegraphic Transfer Account Details", true);
define("TT_INSTRUCTIONS", "Please write down and fill in all the details below on the TT form.", true);
define("TT_INSTRUCTIONS_1", "Write down or print the following details to make your payment.", true);
define("TT_BANKDETAILS_TITLE", "Our Bank Details", true);
define("TT_BENEFICIARY_TITLE", "Name of Beneficiary:", true);
define("TT_BENEFICIARY_DETAIL", "Project Goth, Inc", true);
define("TT_BANK_TITLE", "Name of Bank:", true);
define("TT_BANK_DETAIL", "Silicon Valley Bank (if sending from outside USA) / Sil Vly BK SJ (if sending from inside USA)", true);
define("TT_BANK_ACCOUNT_NUMBER_TITLE", "Bank Account Number:", true);
define("TT_BANK_ACCOUNT_NUMBER_DETAIL", "3300659157", true);
define("TT_BANK_SWIFTCODE_TITLE", "Bank Swift Code:", true);
define("TT_BANK_SWIFTCODE_DETAIL", "SVBKUS6S", true);
define("TT_SPECIAL_INSTRUCTION_TITLE", "Special Instructions:<br>(or Payment Details/ Description)", true);
define("TT_SPECIAL_INSTRUCTION_DETAIL", "Enter your migme username and registered mobile number along with the country code. (eg. migmeusername, +12345678900)", true);
define("TT_COMPANY_ADDRESS_TITLE", "Company Address:", true);
define("TT_COMPANY_ADDRESS_DETAIL", "270 East Lane, Suite 2, Burlingame, CA 94010", true);
define("TT_BANK_ADDRESS_TITLE", "Address of Bank:", true);
define("TT_BANK_ADDRESS_DETAIL", "3003 Tasman Drive, Santa Clara, CA 95054, USA", true);
define("TT_ROUTING_AND_TRANSIT_NUMBER_TITLE", "Routing &amp; Transit Number:", true);
define("TT_ROUTING_AND_TRANSIT_NUMBER_DETAIL", "121140399", true);

define("TT_DEVICE_TITLE", "Telegraphic Transfer", true);
define("TT_DEVICE_ACCOUNT_TITLE", "TT Account Details", true);
define("TT_DEVICE_QUESTION", "How do i do a Telegraphic Transfer (TT)?", true);
define("TT_DEVICE_STEP_1", "Get migme's account details by selecting the link below.", true);
define("TT_DEVICE_STEP_2", "Go to your bank and fill out a TT form. You must enter your mobile number and country into the 'Reference/Notes' section.", true);
define("TT_DEVICE_STEP_3", "Give the money to the bank staff. They will give you a receipt with a TT receipt number on it.", true);
define("TT_DEVICE_STEP_4", "Tell migme that you have sent payment by submitting a TT Notification.", true);
define("TT_DEVICE_STEP_5", "We will send you an SMS when we have recharged your account (3-5 days).", true);

/*********************** Recharge **********************/
?>