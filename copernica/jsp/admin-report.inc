
<ul><%
final Report report = Database.theInstance.reportDatabase();
for(Iterator i = report.getTables().iterator(); i.hasNext(); )
{
	final Report.Table table = (Report.Table)i.next();
	%>
	<li><small>table</small> <%=table.name%><%
	if(table.isMissing())
	{
		%> <b>MISSING !!!</b><%
	}
	if(table.isUnused())
	{
		%> <b>not used</b><%
	}
	if(!table.getConstraints().isEmpty())
	{
		%>
		<ul><%
		for(Iterator j = table.getConstraints().iterator(); j.hasNext(); )
		{
			final Report.Constraint constraint = (Report.Constraint)j.next();
			%>
			<li><small>constraint</small> <%=constraint.name%><%
			if(constraint.isMissing())
			{
				%> <b>MISSING !!!</b><%
			}
			if(constraint.isUnused())
			{
				%> <b>not used</b><%
			}
			%></li><%
		}
		%>
		</ul><%
	}
	%>
	</li><%
}
%>
</ul>