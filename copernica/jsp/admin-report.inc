<%@
page import="java.util.Iterator"

%><%!
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

private ReportColumn getColumn(final Report report, final String columnParameter)
{
	final int pos = columnParameter.indexOf('#');
	if(pos<=0)
		throw new RuntimeException(columnParameter);
	
	final ReportTable table = report.getTable(columnParameter.substring(0, pos));
	if(table==null)
		throw new RuntimeException(columnParameter);
	
	final ReportColumn column = table.getColumn(columnParameter.substring(pos+1));
	if(column==null)
		throw new RuntimeException(columnParameter);
	
	return column;
}
%><%

final Report report = ((DatabaseReportable)Database.theInstance).reportDatabase();
final String reportColor = color(report.getCumulativeColor());
final boolean narrowReport = cop.isNarrowReport();

%>
<span class="<%=reportColor%>"><%
if(narrowReport)
{
	%><a href="<%=cop.widenReport()%>"><%
}
%>Database Report<%
if(narrowReport)
{
	%></a><%
}
%></span>
<input type="button" value="check all analyze" onClick="checkAllAnalyze()">
<input type="submit" name="APPLY" value="apply"/>
<ul class="<%=reportColor%>"><%

for(Iterator i = report.getTables().iterator(); i.hasNext(); )
{
	final ReportTable table = (ReportTable)i.next();
	
	if(cop.skipTable(table))
		continue;
	
	final String tableColor = color(table.getCumulativeColor());
	%>
	<li class="<%=tableColor%>"><%
	
	%><small>table</small> <%
	if(!narrowReport)
	{
		%><a href="<%=cop.narrowReport(table)%>"><%
	}
	%><%=table.name%><%
	if(!narrowReport)
	{
		%></a><%
	}
	
	final String tableError = table.getError();
	if(tableError!=null)
	{
		%> <span class="<%=color(table.getParticularColor())
										%>"><%=tableError%></span><%
	}
	if(table.exists())
	{
		if(narrowReport)
		{
			%>,
		rename to <input name="RENAME_TABLE_<%=table.name%>"/><%
		}
		%>,
		<input type="checkbox" name="DROP_TABLE" value="<%=table.name%>"/>drop<%
	}
	else if(table.required())
	{
		%>,
		<input type="checkbox" name="CREATE_TABLE" value="<%=table.name%>"/>create<%
	}
	
	%>
		<ul><%
		final ReportLastAnalyzed lastAnalyzed = table.getLastAnalyzed();
		if(lastAnalyzed!=null)
		{
			final Date date = lastAnalyzed.lastAnalyzed;
			%>
			<li class="<%=color(lastAnalyzed.getCumulativeColor())%>">
				last analyzed: <%=date!=null?date.toString():"NEVER"%>
				<input type="checkbox" name="ANALYZE_TABLE" value="<%=table.name%>"/>analyze
			</li><%
		}
		
		for(Iterator j = table.getColumns().iterator(); j.hasNext(); )
		{
			final ReportColumn column = (ReportColumn)j.next();
			final String color = color(column.getCumulativeColor());
			%>
			<li class="<%=color%>">
				<small>column</small> <%=column.name%> <small><%=column.getDatabaseType()%></small><%
			final String error = column.getError();
			if(error!=null)
			{
				%>
				<span class="<%=color(column.getParticularColor())%>"><%=error%></span><%
			}
			if(column.exists())
			{
				%>
				<input type="checkbox" name="DROP_COLUMN" value="<%=table.name%>#<%=column.name%>"/>drop<%
			}
			else if(column.required() &&
						column.table.exists() )
			{
				%>
				<input type="checkbox" name="CREATE_COLUMN" value="<%=table.name%>#<%=column.name%>"/>create<%
			}
			%>
			</li><%
		}
		
		for(Iterator j = table.getConstraints().iterator(); j.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)j.next();
			final String constraintColor = color(constraint.getCumulativeColor());
			%>
			<li class="<%=constraintColor%>">
				<small>constraint</small> <%=constraint.name%><%
			final String constraintError = constraint.getError();
			if(constraintError!=null)
			{
				%>
				<span class="<%=color(constraint.getParticularColor())%>"><%=constraintError%></span><%
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