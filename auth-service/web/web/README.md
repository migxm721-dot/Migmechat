Auth Web
=============

Authorization server web

Click [here](https://docs.google.com/a/mig33global.com/file/d/0BwfacrD42QLud3JIZERHX0U4d0k) to see the flowchart.


### How to run it ###

- Standalone Web App
	```
	mvn clean install
	mvn tomcat7:run
	open "http://localhost:9090"
	```

### How to deploy it to MIAB ###
	```
	mvn clean install
	mvn tomcat7:deploy
	```
	or
	```
	mvn tomcat7:deploy
	```

### How to interact with it ###

Try running the Consumer demo or your own OAuth client and connect to this server.
