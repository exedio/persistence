<%@
page import="com.exedio.cope.lib.Model" %><%@
page import="com.exedio.cope.lib.Report" %><%@
page import="com.exedio.cope.lib.ReportTable" %><%@
page import="com.exedio.cope.lib.ReportLastAnalyzed" %><%@
page import="com.exedio.cope.lib.ReportColumn" %><%@
page import="com.exedio.cope.lib.ReportConstraint" %><%@
page import="com.exedio.cope.lib.NestingRuntimeException" %><%@

page import="java.util.Date" %><%@

page import="com.exedio.copernica.admin.Properties_Jspm"

%><%!
	
	Model model = null;
	
	public final void jspInit()
	{
		if(this.model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		try
		{
			this.model = Util.getModel(getServletConfig());
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}
%><%
final Model model = this.model;
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
					model.createDatabase();
					%>Database successfully created!<%
				}
				else if(request.getParameter("TEARDOWN")!= null)
				{
					model.tearDownDatabase();
					%>Database successfully torn down!<%
				}
				else if(request.getParameter("DROP")!=null)
				{
					model.dropDatabase();
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
				
				%><hr><%
				Properties_Jspm.write(out, model.getProperties());
			%>
		</form>
	</body>
</html>
