<%@
page import="com.exedio.cope.lib.Report" %><%@
page import="com.exedio.cope.lib.ReportTable" %><%@
page import="com.exedio.cope.lib.ReportLastAnalyzed" %><%@
page import="com.exedio.cope.lib.ReportColumn" %><%@
page import="com.exedio.cope.lib.ReportConstraint" %><%@
page import="com.exedio.cope.lib.NestingRuntimeException" %><%@

page import="java.util.Date" %><%@

include file="provider.inc"

%><%
final AdminCop cop = AdminCop.getCop(request);
%>
<html>
	<head>
		<title>
			Copernica - Database Administration
		</title><%
		if(cop.report)
		{
			%>
		<link rel="STYLESHEET" type="text/css" href="admin-report.css">
		<script src="admin-report.js" type="text/javascript"></script><%
		}%>
	</head>
	<body>
		<h1>Copernica</h1>
		<h2>Generic Backoffice for COPE</h2>
		<h3>Database Administration</h3>

		<form action="<%=cop%>" method="POST">
			Database:
			<br>
			<input type="submit" name="CREATE" value="create" />
			<input type="submit" name="TEARDOWN" value="tear down"/>
			<input type="submit" name="DROP" value="drop"/>
			<a href="<%=cop.toggleReport()%>"><%=cop.report?"disable":"enable"%> reports</a>
			<br>
			<%
				if(request.getParameter("CREATE")!=null)
				{
					provider.getModel().createDatabase();
					%>Database successfully created!<%
				}
				else if(request.getParameter("TEARDOWN")!= null)
				{
					provider.getModel().tearDownDatabase();
					%>Database successfully torn down!<%
				}
				else if(request.getParameter("DROP")!=null)
				{
					provider.getModel().dropDatabase();
					%>Database successfully dropped!<%
				}
				else if(request.getParameter("APPLY")!=null)
				{
					%><hr><%@ include file="admin-apply.inc" %><%
				}
				
				if(cop.report)
				{
					%><hr><%@ include file="admin-report.inc" %><%
				}
				
				%><hr><%@ include file="admin-properties.inc" %><%
			%>
		</form>
	</body>
</html>
