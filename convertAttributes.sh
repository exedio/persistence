for i in `find . -name *.java`
do
	#echo $i
	sed -e 's/new *EnumAttribute *< *\([0-9,A-Z,a-z]*\) *>\( *\)(\( *\)\([A-Z,_]*\)\( *\))/newEnumAttribute\2(\3\4, \1.class\5)/g' \
		< $i \
		> $i.sed
	diff $i $i.sed
	mv -f $i.sed $i
done
