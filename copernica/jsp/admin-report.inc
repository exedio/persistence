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

if(request.getParameter("APPLY")!=null)
{
	final Report report = ((DatabaseReportable)Database.theInstance).reportDatabase();
	%>
	<ol><%
	final String[] analyzeTables = (String[])request.getParameterMap().get("ANALYZE_TABLE");
	
	if(analyzeTables!=null)
	{
		for(int i = 0; i<analyzeTables.length; i++)
		{
			final String analyzeTable = analyzeTables[i];
			final ReportTable table = report.getTable(analyzeTable);
			if(table==null)
				throw new RuntimeException(analyzeTable);
			%>
			<li>analyzing table <%=analyzeTable%> ...</li><%
			out.flush();
			final long time = System.currentTimeMillis();
			table.analyze();
			%><li><b>ok</b>, <%=System.currentTimeMillis()-time%>ms.</li><%
			out.flush();
		}
	}
	%>
	</ol>
	<hr><%
}

final Report report = ((DatabaseReportable)Database.theInstance).reportDatabase();
final String reportColor = color(report.getCumulativeColor());

%>
<span class="<%=reportColor%>">Database Report</span>
<input type="submit" name="APPLY" value="apply"/>
<ul class="<%=reportColor%>"><%

for(Iterator i = report.getTables().iterator(); i.hasNext(); )
{
	final ReportTable table = (ReportTable)i.next();
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
	if(table.exists())
	{
		%>
		<input type="checkbox" name="DROP_TABLE" value="<%=table.name%>"/>drop<%
	}
	else if(table.required())
	{
		%>
		<input type="checkbox" name="CREATE_TABLE" value="<%=table.name%>"/>create<%
	}
	
	%>
		<ul><%
		final ReportLastAnalyzed lastAnalyzed = table.getLastAnalyzed();
		if(lastAnalyzed!=null)
		{
			final Date date = lastAnalyzed.lastAnalyzed;
			%>
			<li class="<%=color(lastAnalyzed.getCumulativeColor())
				%>">last analyzed: <%=date!=null?date.toString():"NEVER"%>
				<input type="checkbox" name="ANALYZE_TABLE" value="<%=table.name%>"/>analyze
			</li><%
		}
		
		for(Iterator j = table.getColumns().iterator(); j.hasNext(); )
		{
			final ReportColumn column = (ReportColumn)j.next();
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
			if(column.exists())
			{
				%>
				<input type="checkbox" name="DROP_COLUMN" value="<%=column.name%>"/>drop<%
			}
			else if(column.required() &&
						column.table.exists() )
			{
				%>
				<input type="checkbox" name="CREATE_COLUMN" value="<%=column.name%>"/>create<%
			}
			%>
			</li><%
		}
		
		for(Iterator j = table.getConstraints().iterator(); j.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)j.next();
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
			if(constraint.exists())
			{
				%>
				<input type="checkbox" name="DROP_CONSTRAINT" value="<%=constraint.name%>"/>drop<%
			}
			else if( constraint.required() &&
						constraint.table.exists() )
			{
				%>
				<input type="checkbox" name="CREATE_CONSTRAINT" value="<%=constraint.name%>"/>create<%
			}
			%>
			</li><%
		}
		%>
		</ul><%
	%>
	</li><%
}
%>
</ul>