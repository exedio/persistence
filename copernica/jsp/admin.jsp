<%@
page import="com.exedio.cope.lib.Database" %><%@
page import="com.exedio.cope.lib.DatabaseReportable" %><%@
page import="com.exedio.cope.lib.Report" %><%@
page import="com.exedio.cope.lib.ReportTable" %><%@
page import="com.exedio.cope.lib.ReportLastAnalyzed" %><%@
page import="com.exedio.cope.lib.ReportColumn" %><%@
page import="com.exedio.cope.lib.ReportConstraint" %><%@
page import="com.exedio.cope.lib.SystemException" %><%@

page import="java.util.Date" %><%@

include file="copernica-provider.inc"

%>
<html>
	<head>
		<title>
			Copernica - Database Administration
		</title>
		<style type="text/css">
			span.ok     { background-color:#ccffcc; }
			span.yellow { background-color:#ffff33; }
			span.red    { background-color:#ff3333; }
			li.ok     { background-color:#ccffcc; }
			li.yellow { background-color:#ffff33; }
			li.red    { background-color:#ff3333; }
			ul.ok     { background-color:#ccffcc; }
			ul.yellow { background-color:#ffff33; }
			ul.red    { background-color:#ff3333; }
		</style>
		<!-- TODO: include only if reports are printed -->
		<script type="text/javascript">
			function checkAllAnalyze()
			{
				var checkboxes = document.all.tags("input");
				for(j=0; j<checkboxes.length; j++)
				{
					var checkbox = checkboxes[j];
					if(checkbox.type=="checkbox" &&
						checkbox.name=="ANALYZE_TABLE")
					{
						checkbox.checked = true;
					}
				}
			}
		</script>
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
			<%
			if(Database.theInstance instanceof DatabaseReportable)
			{
				%><input type="submit" name="REPORT" value="report"/><%
			}
			%>
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
				else if(request.getParameter("REPORT")!=null ||
							request.getParameter("APPLY")!=null)
				{
					%><hr><%@ include file="admin-report.inc" %><%
				}
			%>
		</form>
	</body>
</html>
