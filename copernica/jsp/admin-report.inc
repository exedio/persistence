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
final Report report = ((DatabaseReportable)Database.theInstance).reportDatabase();
final String reportColor = color(report.getCumulativeColor());

%><span class="<%=reportColor%>">Database Report</span>
<ul class="<%=reportColor%>"><%

for(Iterator i = report.getTables().iterator(); i.hasNext(); )
{
	final Report.Table table = (Report.Table)i.next();
	final String tableColor = color(table.getCumulativeColor());
	%>
	<li class="<%=tableColor%>"><%
	
	%><small>table</small> <%=table.name%><%
	
	final String tableError = table.getError();
	if(tableError!=null)
	{
		%> <span class="<%=color(table.getParticularColor())
										%>"><%=tableError%></span><%
	}
	
	%>
		<ul><%
		final Report.LastAnalyzed lastAnalyzed = table.getLastAnalyzed();
		if(lastAnalyzed!=null)
		{
			final Date date = lastAnalyzed.lastAnalyzed;
			%>
			<li class="<%=color(lastAnalyzed.getCumulativeColor())
					%>">last analyzed: <%=date!=null?date.toString():"NEVER"%></li><%
		}
		
		for(Iterator j = table.getColumns().iterator(); j.hasNext(); )
		{
			final Report.Column column = (Report.Column)j.next();
			final String color = color(column.getCumulativeColor());
			%>
			<li class="<%=color%>"><%
			%><small>column</small> <%=column.name%><%
			final String error = column.getError();
			if(error!=null)
			{
				%>
					<span class="<%=color(column.getParticularColor())
												%>"><%=error%></span><%
			}
			%></li><%
		}
		
		for(Iterator j = table.getConstraints().iterator(); j.hasNext(); )
		{
			final Report.Constraint constraint = (Report.Constraint)j.next();
			final String constraintColor = color(constraint.getCumulativeColor());
			%>
			<li class="<%=constraintColor%>"><%
			%><small>constraint</small> <%=constraint.name%><%
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
	%>
	</li><%
}
%>
</ul>