<%@
page import="com.exedio.cope.lib.Database" %><%@
page import="com.exedio.cope.lib.Report" %><%@
page import="com.exedio.cope.lib.SystemException" %><%@

page import="java.util.Iterator" %><%@

include file="copernica-provider.inc"

%>
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

		<form action="admin.jsp" method="POST">
			Database:
			<br>
			<input type="submit" name="CREATE" value="create" />
			<input type="submit" name="TEARDOWN" value="tear down"/>
			<input type="submit" name="DROP" value="drop"/>
			<input type="submit" name="REPORT" value="report"/>
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
				else if(request.getParameter("REPORT")!=null)
				{
					%>
					<hr>
					<ul><%
					final Report report = Database.theInstance.reportDatabase();
					for(Iterator i = report.getTables().iterator(); i.hasNext(); )
					{
						final Report.Table table = (Report.Table)i.next();
						%><li><%=table.name%></li><%
					}
					%>
					</ul><%
				}
			%>
		</form>
	</body>
</html>
