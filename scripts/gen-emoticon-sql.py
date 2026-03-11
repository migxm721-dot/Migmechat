import sys

args = sys.argv[1:]
if len(args) != 3:
    print "Usage: %s template-file data-file outputfile" % sys.argv[0]
    print " e.g.: %s sql/emoticon-template.sql /tmp/warriors.tsv /tmp/warriors-emoticon.sql" % sys.argv[0]
    print "       data-file should be TSV with the following columns:"
    print "       e.g.: #PackName         PackFolder  FilePrefix  Alias            HotKey"
    print "             Warriors Pack    warriors    trophy      warriors-trophy  (warriors-trophy)"
    sys.exit(1)

tfn = args[0]
dfn = args[1]
ofn = args[2]
f=open(tfn)
template=f.read()
f.close()

#column to variable name mapping
colmap = {0:'pack', 1:'folder', 2:'fileprefix', 3:'alias', 4:'hotkey'}

f=open(dfn)
fo=open(ofn, 'w')
count = 0
for ll in f:
    l = ll.rstrip('\r\n')
    if len(l)>0 and l[0] != '#':
        # skip comments and empty lines
        ls = l.split('\t')
        if len(ls) != len(colmap):
            print "Error: incorrect line, expecting %d columns, got %d: %s" % (len(colmap), len(ls), l)
        else:
            fo.write(template % dict ( (v, ls[k]) for (k,v) in colmap.iteritems() ))
            fo.write('\n')
            count+=1
f.close()
fo.close()
print "Wrote %d emoticon to %s" % (count, ofn)
