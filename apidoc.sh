APIDIR=api
rm -rf ${APIDIR}
mkdir ${APIDIR}
javadoc -private -d ${APIDIR} -use -version -author \
	test \
	mail \
	persistence \
	persistence.search \
	injection

