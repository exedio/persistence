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

if(request.getParameter("APPLY")!=null)
{
	final Report report = ((DatabaseReportable)Database.theInstance).reportDatabase();
	%>
	<ol><%
	{
		final String[] dropColums = (String[])request.getParameterMap().get("DROP_COLUMN");
		if(dropColums!=null)
		{
			for(int i = 0; i<dropColums.length; i++)
			{
				final String dropColum = dropColums[i];
				final ReportColumn column = getColumn(report, dropColum);
				%>
				<li>dropping column <%=dropColum%> ...</li><%
				out.flush();
				final long time = System.currentTimeMillis();
				column.drop();
				%><li><b>ok</b>, <%=System.currentTimeMillis()-time%>ms.</li><%
				out.flush();
			}
		}
	}
	{
		final String[] dropTables = (String[])request.getParameterMap().get("DROP_TABLE");
		if(dropTables!=null)
		{
			for(int i = 0; i<dropTables.length; i++)
			{
				final String dropTable = dropTables[i];
				final ReportTable table = report.getTable(dropTable);
				if(table==null)
					throw new RuntimeException(dropTable);
				%>
				<li>dropping table <%=dropTable%> ...</li><%
				out.flush();
				final long time = System.currentTimeMillis();
				table.drop();
				%><li><b>ok</b>, <%=System.currentTimeMillis()-time%>ms.</li><%
				out.flush();
			}
		}
	}
	{
		final String[] createTables = (String[])request.getParameterMap().get("CREATE_TABLE");
		if(createTables!=null)
		{
			for(int i = 0; i<createTables.length; i++)
			{
				final String createTable = createTables[i];
				final ReportTable table = report.getTable(createTable);
				if(table==null)
					throw new RuntimeException(createTable);
				%>
				<li>creating table <%=createTable%> ...</li><%
				out.flush();
				final long time = System.currentTimeMillis();
				table.create();
				%><li><b>ok</b>, <%=System.currentTimeMillis()-time%>ms.</li><%
				out.flush();
			}
		}
	}
	{
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
	}
	{
		final String[] createColums = (String[])request.getParameterMap().get("CREATE_COLUMN");
		if(createColums!=null)
		{
			for(int i = 0; i<createColums.length; i++)
			{
				final String createColum = createColums[i];
				final ReportColumn column = getColumn(report, createColum);
				%>
				<li>creating column <%=createColum%> ...</li><%
				out.flush();
				final long time = System.currentTimeMillis();
				column.create();
				%><li><b>ok</b>, <%=System.currentTimeMillis()-time%>ms.</li><%
				out.flush();
			}
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