// Set the require.js configuration for your application.
require.config({
	paths: {
		// Components Folder - Packages In Bower
		"react":							"../components/react/react-with-addons",
		"JSXTransformer":					"../components/react/JSXTransformer",
		"jquery":                           "../components/jquery/dist/jquery",
		"bb":                               "../components/backbone/backbone",
		"underscore":                       "../components/underscore/underscore",
		"underscore.string":                "../components/underscore.string/lib/underscore.string",
		"text":                             "../components/requirejs-text/text",
		"handlebars":                       "../components/handlebars/handlebars",
		"backbone.babysitter":              "../components/backbone.babysitter/lib/amd/backbone.babysitter",
		"backbone.wreqr":                   "../components/backbone.wreqr/lib/amd/backbone.wreqr",
		"backbone.marionette":              "../components/marionette/lib/core/amd/backbone.marionette",
		"marionette_handlebars":            "../components/backbone.marionette.handlebars/backbone.marionette.handlebars",
		"hbs":                              "../components/requirejs-hbs/hbs",
		"bootstrap":                        "../components/bootstrap/dist/js/bootstrap",
		"jquery.mousewheel":                "../components/jquery-mousewheel/jquery.mousewheel",
		"fancybox":                         "../components/fancybox/source/jquery.fancybox",
		"fancybox.buttons":                 "../components/fancybox/source/helpers/jquery.fancybox-buttons",
		"fancybox.thumbs":                  "../components/fancybox/source/helpers/jquery.fancybox-thumbs",
		"jquery.cookie":                    "../components/jquery.cookie/jquery.cookie",
		"jquery.scrollTo":                  "../components/jquery.scrollTo/jquery.scrollTo",
		"jstorage":                         "../components/jstorage/jstorage",
		"jquery.atwho":                     "../components/jquery.atwho/dist/js/jquery.atwho",
		"imagesloaded":                     "../components/imagesloaded/imagesloaded",
		"eventEmitter/EventEmitter":		"../components/eventEmitter/EventEmitter",
		"eventie/eventie":		            "../components/eventie/eventie",
		"backgrid":                         "../components/backgrid/lib/backgrid",
		"chosen":                           "../components/chosen/public/chosen.jquery",
        "cropper":                          "../components/cropper/dist/cropper",

		// Vendor Folder - Packages Outside Of Bower
		"qtip":                             "./vendor/jquery.qtip/mig33.qtip",
		"CLDRPluralRuleParser":             "./vendor/jquery.i18n/CLDRPluralRuleParser",
		"jquery.i18n":                      "./vendor/jquery.i18n/jquery.i18n.min",
		"jqueryui":                         "./vendor/jqueryui/jquery-ui-1.10.0.custom.min",
		"easypaginate":                     "./vendor/jquery.easypaginate",
		"jquery.loadmask":                  "./vendor/jquery.loadmask",
		"jquery.rule":                      "./vendor/jquery.rule",
		"jquery.textinputs":                "./vendor/jquery.textinputs.min",
		"jquery.caret":                     "./vendor/jquery.caret",
		"jquery.mentions":					"./vendor/jquery.mentions/jquery.mentions",
		"bootstrapx-clickover":             "./vendor/bootstrapx-clickover",
		"jquery.highlight-4":               "./vendor/jquery.highlight-4.min",
		"jquery.tag-it":                    "./vendor/jquery.tag-it",
		"jquery.json":                      "./vendor/jquery.json.min",
		"jquery.waypoints":                 "./vendor/jquery.waypoints",
		"jquery.timeago":                   "./vendor/jquery.timeago",
		"jquery.tinysort":                  "./vendor/jquery.tinysort",
		"jquery.menuaim":                   "./vendor/jquery.menu-aim",
		"jquery.mCustomScrollbar":          "./vendor/jquery.mCustomScrollbar",
		"tourist":                          "./vendor/tourist",
		"slick":                            "./vendor/slick",
		// mig33's Backbone
		"mig33_backbone":                   "../scripts/mig33_backbone"
	},

	shim: {
		jquery: {
			exports: "jQuery"
		},

		underscore: {
			exports: "_"
		},

		handlebars: {
			exports: 'Handlebars'
		},

		// Backbone library depends on lodash and jQuery.
		bb: {
			deps: ["underscore" , "jquery"],
			exports: "Backbone"
		},
		"backgrid": {
			deps: ["underscore", "bb"]
		},
		"backbone.babysitter": {
			deps: ["underscore", "bb"]
		},

		"backbone.wreqr": {
			deps: ["underscore", "backbone"],
			exports: "Backbone.Wreqr"
		},

		"backbone.marionette": {
			deps: ["backbone.babysitter"],
			exports: "Marionette"
		},

		"marionette_handlebars": {
			deps: ["backbone.marionette"]
		},

		"bootstrap": {
			deps: ["jquery"]
		},
		"bootstrapx-clickover": {
			deps: ["bootstrap"]
		},

		"jquery.caret": {
			deps: ["jquery"]
		},
		"jquery.atwho": {
			deps: ["jquery.caret"]
		},

		"jquery.mousewheel": {
			deps: ["jquery"]
		},

		"jquery.tinysort": {
			deps: ["jquery"]
		},

		"jquery.cookie": {
			deps: ["jquery"]
		},

		"jquery.highlight-4": {
			deps: ["jquery"]
		},

		"jquery.tag-it": {
			deps: ["jquery", "jqueryui"]
		},

		"jquery.json": {
			deps: ["jquery"]
		},

		"jquery.scrollTo": {
			deps: ["jquery"]
		},

		"jquery.mentions": {
			deps: ["jquery","underscore"]
		},

		"fancybox": {
			deps: ["jquery","jquery.mousewheel"]
		},

		"fancybox.buttons": {
			deps: ["jquery","jquery.mousewheel", "fancybox"]
		},

		"fancybox.thumbs": {
			deps: ["jquery","jquery.mousewheel", "fancybox"]
		},

		"mig33_backbone": {
			deps: ["underscore", "backbone.marionette"],
			exports: "Backbone"
		},

		"less": {
			exports: "less"
		},

		"jquery.i18n": {
			deps: ["jquery", "CLDRPluralRuleParser"]
		},

		"jquery.ui" : {
			deps: ["jquery"]
		},

		"qtip" : {
			deps: ["jquery", "imagesloaded"]
		},

		"imagesloaded" : {
			deps: ["jquery"]
		},

		"jstorage" : {
			deps: ["jquery"]
		},

		"easypaginate" : {
			deps: ["jquery"]
		},

		"jquery.loadmask" : {
			deps: ["jquery"]
		},

		"jquery.rule" : {
			deps: ["jquery"]
		},

		"jquery.textinputs" : {
			deps: ["jquery"]
		},

		"jqueryui":{
			deps: ["jquery"]
		},
		"jquery.waypoints" : {
			deps: ["jquery"]
		},
		"jquery.timeago" : {
			deps: ["jquery"]
		},
		"jquery.menuaim": {
			deps: ["jquery"]
		},
		"jquery.mCustomScrollbar": {
			deps: ["jquery", "jquery.mousewheel"]
		},
		"chosen": {
			deps: ["jquery"]
		},
		"tourist": {
			  exports: "Tourist"
			, deps: ["jquery", "bootstrap"]
		},
		"slick": {
			deps: ["jquery"]
		}
	},
	map : {
		//every where else load normal backbone
		"*" : {
			'backbone': 'bb'
		},
		//Load mig33_backbone for all classes inside modules
		'modules/*' : {
			'backbone': 'mig33_backbone'
		}
	}
});


require([
	//Global requires
	'jquery',
	'backbone',
	'underscore',
	'handlebars',
	'backbone.marionette',
	'marionette_handlebars',
	//Application router and controller requires
	'modules/mig33_app',
	'modules/app_controller',
	'modules/mig33_router',
	'modules/common/session_user',
	'modules/globals/session_user',
	'modules/globals/session_user_settings',
	'modules/globals/settings',
	'modules/globals/url',
	'modules/globals/variable',
	'modules/template/helpers/general_helpers',
	'modules/template/helpers/user_helpers',
	'mig33_backbone'
],
	function ($, Backbone, _, Handlebars, Marionette, MarionetteHandlebars, Mig33App, Mig33Controller, Mig33Router, SessionUser, session_user, session_user_settings, settings, url, variable) {
		"use strict";
		// Initialize the session object
		SessionUser.initialize(session_user);

		// Initialize the controller and app router
		Mig33App.addInitializer(function (options) {
			// initialize the controller
			var controller = new Mig33Controller({});
			// initialize the router
			new Mig33Router({
				controller: controller
			});
		});

		// Initialize and start the application
		Mig33App.start({
			root: window.location.pathname,
			path_root: "/"
		});

		return {};
	});
