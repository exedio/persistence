for i in `find . -name *.java`
do
	#echo $i
	sed -e 's/new *\(com.exedio.cope.\)\?\(Enum\|Item\)Attribute *< *\([0-9,A-Z,a-z,.]*\) *>\( *\)(\( *\)\([A-Z,_]*\)\( *\(, *[A-Z]* *\)\?\))/new\2Attribute\4(\5\6, \3.class\7)/g' \
		< $i \
		> $i.sed
	diff $i $i.sed
	mv -f $i.sed $i
done
