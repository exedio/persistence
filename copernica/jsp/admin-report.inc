<%!
private String color(final int color)
{
	switch(color)
	{
	case Report.COLOR_OK:
		return null;
	case Report.COLOR_YELLOW:
		return "ffff33";
	case Report.COLOR_RED:
		return "ff3333";
	default:
		throw new RuntimeException(String.valueOf(color));
	}
}
%><%
final Report report = Database.theInstance.reportDatabase();
final String reportColor = color(report.getCumulativeColor());
if(reportColor!=null)
{
	%><span style="background-color:#<%=reportColor%>"><%
}
%>Database Report<%
if(reportColor!=null)
{
	%></span><%
}
%>
<ul><%
for(Iterator i = report.getTables().iterator(); i.hasNext(); )
{
	final Report.Table table = (Report.Table)i.next();
	%>
	<li><%
	
	final String tableColor = color(table.getCumulativeColor());
	if(tableColor!=null)
	{
		%><span style="background-color:#<%=tableColor%>"><%
	}
	
	%><small>table</small> <%=table.name%><%
	
	if(tableColor!=null)
	{
		%></span><%
	}
	
	final String tableError = table.getError();
	if(tableError!=null)
	{
		%> <span style="background-color:#<%=color(table.getParticularColor())
										%>"><%=tableError%></span><%
	}
	
	if(!table.getConstraints().isEmpty())
	{
		%>
		<ul><%
		for(Iterator j = table.getConstraints().iterator(); j.hasNext(); )
		{
			final Report.Constraint constraint = (Report.Constraint)j.next();
			final String constraintColor = color(constraint.getCumulativeColor());
			if(constraintColor!=null)
			{
				%><span style="background-color:#<%=constraintColor%>"><%
			}
			%>
			<li><small>constraint</small> <%=constraint.name%><%
			if(constraintColor!=null)
			{
				%></span><%
			}
			if(constraint.isMissing())
			{
				%> <span style="background-color:#ffff33">MISSING !!!</span><%
			}
			if(constraint.isUnused())
			{
				%> <span style="background-color:#ff3333">not used</span><%
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