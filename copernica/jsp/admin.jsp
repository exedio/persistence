<%@ page import="com.exedio.cope.lib.Database" %>
<%@ page import="com.exedio.cope.lib.SystemException" %>
<html>
	<head>	
		<title>
			Copernica - Database Administration
		</title>
	</head>
	<body>
		<h1>Copernica</h1>
		<h2>Generic Backoffice for COPE</h2>
		<h3>Database Administration</h3>

<%
	final CopernicaProvider provider;
	try
	{
		final String providerName = getInitParameter("com.exedio.copernica.provider");
		if(providerName==null)
			throw new NullPointerException("init-param com.exedio.copernica.provider missing");
		final Class providerClass = Class.forName(providerName);
		provider = (CopernicaProvider)providerClass.newInstance();
	}
	catch(ClassNotFoundException e)
	{
		throw new SystemException(e);
	}
%>
		<form action="admin.jsp" method="POST">
			Database:
			<br>
			<input type="submit" name="CREATE" value="create" />
			<input type="submit" name="TEARDOWN" value="tear down"/>
			<input type="submit" name="DROP" value="drop"/>
			<br>
			<%
				if(request.getParameter("CREATE")!=null)
				{
					Database.theInstance.createDatabase();
					provider.initializeExampleSystem();
					%>Database successfully created!<%
				}
				else if(request.getParameter("TEARDOWN")!= null)
				{
					Database.theInstance.tearDownDatabase();
					%>Database successfully torn down!<%
				}
				else if(request.getParameter("DROP")!=null)
				{
					Database.theInstance.dropDatabase();
					%>Database successfully dropped!<%
				}
			%>
		</form>
	</body>
</html>
