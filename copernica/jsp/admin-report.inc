<%!
private String color(final int color)
{
	switch(color)
	{
	case Report.COLOR_OK:
		return "ok";
	case Report.COLOR_YELLOW:
		return "yellow";
	case Report.COLOR_RED:
		return "red";
	default:
		throw new RuntimeException(String.valueOf(color));
	}
}
%><%
final Report report = Database.theInstance.reportDatabase();
final String reportColor = color(report.getCumulativeColor());

%><span class="<%=reportColor%>">Database Report</span>
<ul><%

for(Iterator i = report.getTables().iterator(); i.hasNext(); )
{
	final Report.Table table = (Report.Table)i.next();
	%>
	<li><%
	
	final String tableColor = color(table.getCumulativeColor());
	%><span class="<%=tableColor%>"><small>table</small> <%=table.name%></span><%
	
	final String tableError = table.getError();
	if(tableError!=null)
	{
		%> <span class="<%=color(table.getParticularColor())
										%>"><%=tableError%></span><%
	}
	
	if(!table.getConstraints().isEmpty())
	{
		%>
		<ul><%
		for(Iterator j = table.getConstraints().iterator(); j.hasNext(); )
		{
			final Report.Constraint constraint = (Report.Constraint)j.next();
			%>
			<li><%
			final String constraintColor = color(constraint.getCumulativeColor());
			%><span class="<%=constraintColor%>"><small>constraint</small> <%=constraint.name%></span><%
			final String constraintError = constraint.getError();
			if(constraintError!=null)
			{
				%>
					<span class="<%=color(constraint.getParticularColor())
												%>"><%=constraintError%></span><%
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