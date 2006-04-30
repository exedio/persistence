for i in `find . -name *.java`
do
	#echo $i
	sed -e 's/new *\(Enum\|Item\)Attribute *< *\([0-9,A-Z,a-z]*\) *>\( *\)(\( *\)\([A-Z,_]*\)\( *\(, *[A-Z]* *\)\?\))/new\1Attribute\3(\4\5, \2.class\6)/g' \
		< $i \
		> $i.sed
	diff $i $i.sed
	mv -f $i.sed $i
done
