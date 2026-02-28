module.exports = function(grunt) {
	'use strict';

	// Project configuration.
	grunt.initConfig({
		less: {
			dist: {
				files: {
					"sites/resources/less/touch/touch.css": "sites/resources/less/touch/touch.main.less",
					"sites/resources/less/touch/touch.overrides.css": "sites/resources/less/touch/touch.overrides.less"
				}
			}
		},
		cssmin: {
			dist: {
				files: {
					"sites/resources/css/touch-v3-blue.min.css": "sites/resources/css/touch-v3-blue.css",
					"sites/resources/css/touch.min.css": "sites/resources/less/touch/touch.css",
					"sites/resources/css/touch.overrides.min.css": "sites/resources/less/touch/touch.overrides.css"
				}
			}
		},
		concat: {
	      options:{
	        separator: ';'
	      },
	      js: {
	      	src: ['sites/resources/js/touch/v4/lib/require.js', 'sites/resources/js/touch/v4/lib/require.min.js'],
	      	dest: 'sites/resources/js/touch/v4/lib/require.min.js'
	      },
	      css: {
	        src: ['sites/resources/css/touch-v3-blue.min.css', 'sites/resources/css/touch.overrides.min.css'],
	        dest: 'sites/resources/css/touch.overrides.min.css'
	      }
	    },
		requirejs: {
			compile: {
				options: {
					name: 'main',
					mainConfigFile: 'sites/resources/js/touch/v4/main.js',
					out: 'sites/resources/js/touch/v4/lib/require.min.js',
					optimize: 'uglify'
				}
			}
		}
	});

	// These plugins provide necessary tasks.
	grunt.loadNpmTasks('grunt-contrib-less');
	grunt.loadNpmTasks('grunt-contrib-cssmin');
	grunt.loadNpmTasks('grunt-contrib-concat');
  	grunt.loadNpmTasks('grunt-contrib-uglify');
  	grunt.loadNpmTasks('grunt-contrib-requirejs');

	// Default task.
	grunt.registerTask('touch', ['less', 'cssmin', 'requirejs', 'concat']);
};


