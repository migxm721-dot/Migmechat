Auth Web
========

Auth web is a library that provides ease of use for OAuth 2.0 authorization between client and server.

Click [here](https://docs.google.com/a/mig33global.com/file/d/0BwfacrD42QLud3JIZERHX0U4d0k) to see a flowchart of a full authorization process.

MIAB
----

### Setup

Add an entry to /etc/hosts:

```
192.168.56.101 miab oauth.localhost.projectgoth.com [...]
```

Add an entry to ~/.ssh/config:

```
Host miab
  Hostname	192.168.56.101
  User vagrant
  IdentityFile ~/.ssh/id_rsa
```

Add the following lines to /usr/local/tomcat7/bin/catalina.sh:

```
# Set the config dir path for Auth-Service
CATALINA_OPTS="$CATALINA_OPTS -Dauth.config.dir=/usr/auth/config"
CATALINA_OPTS="$CATALINA_OPTS -Dauth.log.path=/usr/auth/logs/"
```

### Deployment

To deploy a new version to MIAB just run

```sh
./deployToQALAB.sh
```

###Paths

|     |     |
|:--- |:--- |
| **Tomcat**         | /usr/local/tomcat7/ |
| **War deployment** | /usr/local/tomcat7/webapps/ROOT.war |
| **Config files**   | /usr/auth/config/ --> /data/auth-service/service/config |
| **AES keys**       | /usr/auth/aeskeys/ --> /data/auth-service/service/aeskeys |
| **Logs**           | /usr/auth/logs/ |


Note that the config files and AES keys paths are both symlinked and will therefore be automatically up-to-date.

QAlab
----

### Setup

Add an entry to /etc/hosts:

```
175.41.214.49 oauth.qalab.projectgoth.com
```

Add an entry to ~/.ssh/config:

```
Host qalab_oauth
  Hostname  ec2-175-41-214-49.ap-northeast-1.compute.amazonaws.com
  User your_username
  IdentityFile ~/.ssh/id_rsa
```

Add the following lines to /usr/local/tomcat7/bin/catalina.sh:

```
# Set the config dir path for Auth-Service
CATALINA_OPTS="$CATALINA_OPTS -Dauth.config.dir=/usr/auth/config"
CATALINA_OPTS="$CATALINA_OPTS -Dauth.log.path=/usr/auth/logs/"
```

### Deployment

To deploy a new version to QAlab just run

```sh
./deployToQAlab.sh
```

Config files and AES keys need to be manually updated. However, *deployToQAlab.sh* optionally allows you to update them.

###Paths

|     |     |
|:--- |:--- |
| **Tomcat**         | /usr/local/tomcat7/ |
| **War deployment** | /usr/local/tomcat7/webapps/ROOT.war |
| **Config files**   | /usr/auth/config/ |
| **AES keys**       | /usr/auth/aeskeys/ |
| **Logs**           | /usr/auth/logs/ |


Staging
----

### Setup

Add an entry to ~/.ssh/config:

```
Host aquarius-oauth1.stg [...]
  Hostname %h.sjc02.projectgoth.com
  User mig
  IdentityFile ~/.ssh/valhalla.pem
```

Add the following lines to /etc/tomcat/tomcat.conf:

```
# Set the config dir path for Auth-Service
CATALINA_OPTS="$CATALINA_OPTS -Dauth.config.dir=/usr/auth/config"
CATALINA_OPTS="$CATALINA_OPTS -Dauth.log.path=/usr/auth/logs/"
```

###Paths

|     |     |
|:--- |:--- |
| **Tomcat**         | /usr/share/tomcat/ |
| **War deployment** | /var/lib/tomcat/webapps/ROOT.war |
| **Config files**   | /usr/auth/config/ |
| **AES keys**       | /usr/auth/aeskeys/ |
| **Logs**           | /usr/auth/logs/ |
