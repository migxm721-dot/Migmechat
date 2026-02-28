# migbo-web {#mainpage}

## Introduction
migbo-web is a web repository that contains the front-end code for migme's miniBlog for WAP and Midlet. It also acts as a data proxy for Android APIs and newer [mig33JSClient](https://github.com/migme/mig33JSClient) APIs through its *datasvc* controller. For older mig33JSClient APIs, migbo-web also handle AJAX requests from them through *api/miniblog* controller

## Documentation
We are using [Doxygen](http://www.stack.nl/~dimitri/doxygen/) to generate the documentation and [Graphviz](http://www.graphviz.org/) for the graphs used in the documentation. We are assuming your dot executable is located in */usr/local/bin/dot*.

To install Doxygen & Graphviz using brew:

`
brew install doxygen graphviz
`

To generate the documentation:

```
cd YOUR_MIGBO-WEB_FOLDER; doxygen docs/migbo-web.doxygen
```

The generated documentation will be in *docs/doxygen* and that folder is git ignored. The index.html is located in *docs/doxygen/html/index.html* and you can view it on your browser.

## Dependencies
Whenever migbo-web is deployed, the [CodeIgniter package](https://github.com/migme/codeigniter.mig33.com) has to be deployed as well. The CodeIgniter package contains CodeIgniter framework itself as well as 5 other packages

- CIUnit for PHP Unit Testing
- mig33 which contains shared code in mig33
- phptal which contains the PHPTAL templating system
- restserver which is the base class for the data proxy mentioned in the introduction
- xhprof for PHP profiling

migbo-web is configured to be deployed to */var/www/migbo/* and CodeIgniter is configured to be deployed to */var/www/codeigniter/*

## Data Proxy
Some newer mig33JSClient APIs and almost all Android APIs hits *http://mig.me/datasvc/* and then the controller in *application/controllers/datasvc.php* will handle the requests.

Depending on the rest path being requested, it will be routed accordingly to fusion-rest at *http://jboss.vip.vljb:8080/fusion-rest* or migbo-datasvc at *http://carina.prod.sjc02.projectgoth.com:8080/migbo_datasvc*

Apart from forwarding all the query parameters, it also adds 3 extra parameters

- _sessionId_ which indicates the user's session ID
- _requestingUserid_ which indicates the user's ID
- _view_ which indicates which platform the call is being made from. Values (all in uppercase) include:
 - MIGBO_BLAAST
 - MIGBO_BLACKBERR
 - MIGBO_MIDLET
 - MIGBO_MRE
 - MIGBO_TOUCH
 - MIGBO_WAP
 - MIGBO_WINDOWS_MOBILE
 - MIGBO_WEB
 - MIGBO_IOS

By default all calls to /datasvc/ will be checked for session (meaning only logged-in users can access) expect those in the whitelisted paths and whitelisted root.

Currently, only */search/facets/hashtags* and */API/* calls will not be checked for session

### Removing Data Proxy
The client team has expressed interest in totally bypassing migbo-web for calls made by Android. For that to happen:

* fusion-rest and migbo-datasvc has to be public facing
* Session checks has to be done on fusion-rest and migbo-datasvc themselves
* All calls to fusion-rest and migbo-datasvc has to contain _sessionId_, _requestingUserid_ & _view_
* The client has to differentiate the call whether it is meant for fusion-rest or migbo-datasvc since it will be a direct call

## AJAX Requests
For older mig33JSClient APIs, all the AJAX requests are send to *http://mig.me/api/miniblog/* and then the controller in *application/controllers/api/miniblog.php* will handle the requests.

## WAP/Midlet Views
Since the web view is being served by mig33JSClient package, what's left of the migbo-web package in terms of templates are just serving the WAP and Midlet view.

There are two special exceptions:

* Web decorator used to load mig33JSClient
* *hidden_post* used by Android

A view always starts with a decorator */application/views/decorator/* which is the skeleton or layout of a page. For example, a WAP view will load the wap decorator */application/views/decorator/wap.php* and inside the decorator, it will load the main content. for example */application/views/wap/home/index.php*.

Modules */application/views/module/* are reusable view component that can be used in more than 1 views, for example */application/views/module/wap/postbox/postbox.php* can be used on the homefeed and in the profile page.

### Web Decorator
*/application/views/decorator/web.php* is the only view used by mig33JSClient. Inside the web decorator, it loads the mig33JSClient *require.min.js* using the script tag.

### hidden_post
Whenever Android has to create a miniBlog post with an image, it will do a form post to hidden_post in */application/views/touch/post/hidden_post.php*. The Android team will be moving away from this method soon.

## Static Assets
migbo-web also holds static assets in */htdocs/resources/img/* like badges, labels, migAlerts, emotional footprints and images used in emails. Very often we need to add new badges for campaigns.

### Labels (/htdocs/resources/img/labels/16x16)
* a.png Global Admin
* c.png Coach
* ca.png Chat Room Admin
* ga.png Group Admin
* m.png Merchant
* mt.png Merchant Mentor
* s.png Staff
* v.png Verified

### Badges (/htdocs/resources/img/badges)
* /16x16/
* /16x16/monotone
* /24x24/
* /24x24/monotone
* /48x48/
* /48x48/monotone
* /64x64/
* /64x64/monotone
* /96x96/
* /96x96/monotone

As an example. the folder called 96x96 which is the size of the badge and inside the 96x96 folder, there is a subfolder called monotone. The image name has TO BE THE SAME. So you place the monotone image of the badge in the monotone subfolder of 96x96 and the colored one in 96x96 itself. Do note that the badge image name HAS TO BE THE SAME regardless of which folder there are place in.