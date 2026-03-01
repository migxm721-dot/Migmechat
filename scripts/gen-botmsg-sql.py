import sys

args = sys.argv[1:]

if len(args) < 2:
    print "Usage: %s BotCommandName input-tsv-fil" % sys.argv[0]
    print " input file two columns, key\\tmsg"
    sys.exit(1)

botname=args[0]
f=open(args[1])
for l in f:
    l = l.rstrip('\r\n')
    if len(l)>0:
        ls = l.split('\t', 2)
        print "insert into botmessage (BotID, LanguageCode, MessageKey, MessageValue) values ((select id from bot where commandname='%s'),'ENG', '%s', '%s');" % (botname, ls[0], ls[1].replace("'", "''"))

