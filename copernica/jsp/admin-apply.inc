
<%
{
	final Report report = model.reportDatabase();
	%>
	<ol><%
	{
		final String[] dropColumns = (String[])request.getParameterMap().get("DROP_COLUMN");
		if(dropColumns!=null)
		{
			for(int i = 0; i<dropColumns.length; i++)
			{
				final String dropColumn = dropColumns[i];
				final ReportColumn column = getColumn(report, dropColumn);
				%>
				<li>report.getTable(&quot;<b><%=
					column.table.name%></b>&quot;).getColumn(&quot;<b><%=
					column.name%></b>&quot;).drop();</li><%
				out.flush();
				final long startTime = System.currentTimeMillis();
				column.drop();
				final long endTime = System.currentTimeMillis();
				%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
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
				<li>report.getTable(&quot;<b><%=
					table.name%></b>&quot;).drop();</li><%
				out.flush();
				final long startTime = System.currentTimeMillis();
				table.drop();
				final long endTime = System.currentTimeMillis();
				%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
				out.flush();
			}
		}
	}
	{
		for(Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); )
		{
			final String parameterName = (String)i.next();
			if(!parameterName.startsWith("RENAME_TABLE_"))
				continue;
			
			final String targetName = request.getParameter(parameterName);
			if(targetName.length()==0)
				continue;
			
			final String sourceName = parameterName.substring("RENAME_TABLE_".length());
			final ReportTable table = report.getTable(sourceName);
			if(table==null)
				throw new RuntimeException(sourceName);
			
			%>
			<li>report.getTable(&quot;<b><%=
				table.name%></b>&quot;).renameTo(&quot;<%=
				targetName%>&quot;);</li><%
			out.flush();
			final long startTime = System.currentTimeMillis();
			table.renameTo(targetName);
			final long endTime = System.currentTimeMillis();
			%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
			out.flush();
		}
	}
	{
		for(Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); )
		{
			final String parameterName = (String)i.next();
			if(!parameterName.startsWith("MODIFY_COLUMN_"))
				continue;
			
			final String targetType = request.getParameter(parameterName);
			if(targetType.length()==0)
				continue;
			
			final String sourceName = parameterName.substring("MODIFY_COLUMN_".length());
			
			final ReportColumn column = getColumn(report, sourceName);
			if(column==null)
				throw new RuntimeException(sourceName);
			
			%>
			<li>report.getTable(&quot;<b><%=
				column.table.name%></b>&quot;).getColumn(&quot;<b><%=
				column.name%></b>&quot;).modify(&quot;<b><%=
				targetType%></b>&quot;);</li><%
			out.flush();
			final long startTime = System.currentTimeMillis();
			column.modify(targetType);
			final long endTime = System.currentTimeMillis();
			%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
			out.flush();
		}
	}
	{
		for(Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); )
		{
			final String parameterName = (String)i.next();
			if(!parameterName.startsWith("RENAME_COLUMN_"))
				continue;
			
			final String targetName = request.getParameter(parameterName);
			if(targetName.length()==0)
				continue;
			
			final String sourceName = parameterName.substring("RENAME_COLUMN_".length());
			
			final ReportColumn column = getColumn(report, sourceName);
			if(column==null)
				throw new RuntimeException(sourceName);
			
			%>
			<li>report.getTable(&quot;<b><%=
				column.table.name%></b>&quot;).getColumn(&quot;<b><%=
				column.name%></b>&quot;).renameTo(&quot;<b><%=
				targetName%></b>&quot;);</li><%
			out.flush();
			final long startTime = System.currentTimeMillis();
			column.renameTo(targetName);
			final long endTime = System.currentTimeMillis();
			%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
			out.flush();
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
				<li>report.getTable(&quot;<b><%=
					table.name%></b>&quot;).create();</li><%
				out.flush();
				final long startTime = System.currentTimeMillis();
				table.create();
				final long endTime = System.currentTimeMillis();
				%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
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
				final long startTime = System.currentTimeMillis();
				table.analyze();
				final long endTime = System.currentTimeMillis();
				%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
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
				final long startTime = System.currentTimeMillis();
				column.create();
				final long endTime = System.currentTimeMillis();
				%><li><b>ok</b>, <%=endTime-startTime%>ms.</li><%
				out.flush();
			}
		}
	}
	%>
	</ol>
	<hr><%
}%>