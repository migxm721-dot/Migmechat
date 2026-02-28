#mig33 Super Repo#

Checkout all the mig33 working project repositories using the [mr(1)](https://github.com/joeyh/myrepos) tool.

##Prerequisite##
Macports or Homebrew must be installed

##To install myrepos##

```
$ sudo port install myrepos
```

or

```
$ brew install mr
```

##Usage##
In miab/data:

###To checkout###

```
$ mr -t co
```

###To update###

```
$ mr -t update
```

or 

```
$ mr -t pull
```

###Other commands###

```
$ mr -t status
$ mr -t st
$ mr -t diff
$ mr -t remote
$ mr -t fetch
$ mr -t stash
$ mr -t clean
$ mr -t cleanfd
```

To inspect the commands, edit the `.mrconfig` file in this folder.

###To run actions concurrently###

```
$ mr -t -j 10 checkout
#where j is the number of threads to use concurrently
```
##Further Readings##

Read the `myrepos` man pages for more details.

```
$ man mr
```

Or visit the wiki: [http://myrepos.branchable.com](http://myrepos.branchable.com)