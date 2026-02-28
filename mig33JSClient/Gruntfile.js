module.exports = function(grunt) {
	'use strict';

	// Project configuration.
	grunt.initConfig({
		// Metadata.
		pkg: grunt.file.readJSON('package.json'),
		// Task configuration.
		clean: {
			files: ['dist']
		},
		shell: {
			'build-jsx': {
				command: [
					'jsx -x jsx app/scripts/modules/jsx/ app/scripts/modules/jsx/',
					'rm -rf app/scripts/modules/jsx/.module-cache/'
				].join(' && '),
				stdout: true,
				failOnError: true
			}
		},
		concat: {
			options: {
				stripBanners: true
			},
			dist: {
				src: ['app/components/requirejs/require.js', '<%= concat.dist.dest %>'],
				dest: 'dist/require.js'
			}
		},
		uglify: {
			options: {
				compress: {
					drop_console: false
				}
			},
			dist: {
				src: '<%= concat.dist.dest %>',
				dest: 'dist/require.min.js'
			}
		},
		jshint: {
			/*
			gruntfile: {
				options: {
					jshintrc: '.jshintrc'
				},
				src: 'Gruntfile.js'
			},
			*/
			app: {
				options: {
					jshintrc: '.jshintrc'
				},
				src: ['app/scripts/modules/**/*.js']
			}
		},
		requirejs: {
			compile: {
				options: {
					name: 'main',
					mainConfigFile: 'app/scripts/main.js',
					out: '<%= concat.dist.dest %>',
					optimize: 'none',
					wrap: false
				}
			}
		},
		less: {
			dist: {
				files: {
					"dist/app/styles/web.css": "app/styles/web.less"
				}
			}
		},
		cssmin: {
			dist: {
				files: {
					"dist/app/styles/web.min.css": "dist/app/styles/web.css"
				}
			}
		},
		imagemin: {
			dist: {
				options: {
					optimizationLevel: 7
				},
				files: [{
					expand: true,
					cwd: 'app/images',
					src: '{,*/}*.{png,jpg,jpeg}',
					dest: 'app/images'
				}]
			}
		},
		copy: {
			dist: {
				files: {
					"dist/": [
						"app/fonts/**",
						"app/images/**",
						"app/language/**",
						"app/sound/**",
						"app/favicon.ico",
						"app/robots.txt"
					]
				}
			}
		},
		typescript: {
			base: {
				src: ['app/scripts/**/*.ts'],
				options: {
					module: 'amd', //or commonjs
					target: 'es5', //or es3
					basePath: 'app/scripts/',
					sourceMap: true,
					declaration: false,
					comments: true
				}
			}
		}
	});

	// These plugins provide necessary tasks.
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-cssmin');
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-imagemin');
	grunt.loadNpmTasks('grunt-contrib-requirejs');
	grunt.loadNpmTasks('grunt-contrib-less');
	//grunt.loadNpmTasks('grunt-shell');
	grunt.loadNpmTasks('grunt-typescript');

	// Default task.
	//grunt.registerTask('jsx', ['shell:build-jsx']);
	grunt.registerTask('qa', ['typescript', 'jshint', 'clean', 'requirejs', 'concat', 'uglify', 'less', 'cssmin', 'copy']);
	grunt.registerTask('default', ['typescript', 'jshint', 'clean', 'requirejs', 'concat', 'uglify', 'less', 'cssmin', 'imagemin', 'copy']);
	grunt.registerTask('c', ['typescript', 'jshint', 'clean', 'requirejs', 'concat', 'less', 'cssmin', 'copy']);
	grunt.registerTask('cless', ['less', 'cssmin']);
};

