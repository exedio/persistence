APIDIR=api
rm -rf ${APIDIR}
mkdir ${APIDIR}
javadoc -private -d ${APIDIR} -use -version -author \
	-windowtitle "New Cool Persistence Framework (NCPF)" \
	-doctitle "New Cool Persistence Framework (NCPF<sup>TM</sup>)" \
	-header "Welcome To A Better World,<br>Where There Is <a href="http://www.javageeks.com/SBJP/index.html" target="_top">No EJB</a>,<br><a href="http://radio.weblogs.com/0112098/2002/11/26.html" target="_top">Really</a>." \
	-footer "Don&apos;t Make Things More<br>Complicated They Have To Be.<br>Don&apos;t Use EJB." \
	-bottom "&copy; Copyright 2004 <a href="http://www.exedio.com" target="_top">exedio</a> Gesellschaft f&uuml;r Softwareentwicklung mbH" \
	test \
	mail \
	persistence \
	persistence.search \
	injection

