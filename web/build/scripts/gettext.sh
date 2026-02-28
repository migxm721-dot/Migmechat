#!/bin/sh

# Generate Master POT File

# sed sets extended regular expressions differently on macox & linux
is_linux=$(uname|grep -i 'linux')
if [ "$is_linux" ]; then
	sed_option ="-r";
else
	sed_option="-E";
fi

echo Generating POT File
find $1 -type f -iname "*.php" | xgettext --output-dir=$1/lib/language/packs --output=mig33.pot --default-domain=mig33 --language=php --keyword=_,_n,gettext,gettext_noop,ngettext,_x:1,2c --no-wrap -f -
sed $sed_option -i -e 's_^#: /.*/sites/_#: /sites/_g' $1/lib/language/packs/mig33.pot
echo POT File Generated at $1/lib/language/packs/mig33.pot

# Merge POT File With Exisiting Languages
for file in $(ls -d $1/lib/language/packs/*/);
do
	lang="$file"LC_MESSAGES

	if [ -e $lang/mig33.po ]
	then
		echo Generating PO File For $file
		msgmerge --update --no-wrap --no-fuzzy-matching --no-location $lang/mig33.po $1/lib/language/packs/mig33.pot
#		sed $sed_option -i -e 's_^#: /.*/sites/_#: /sites/_g' $lang/mig33.po
		echo PO File Generated at $lang/mig33.po
	
		echo Generating MO File For $file
		msgfmt --output-file=$lang/mig33.mo $lang/mig33.po
		echo MO File Generated at $lang/mig33.mo
	fi
done

# Cleanup any garbage left by sed
find -E "$1/lib/language" -iregex '^.*(~|-e)$' -delete

