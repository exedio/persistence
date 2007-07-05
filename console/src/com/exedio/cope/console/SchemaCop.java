/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.console;

import java.io.PrintStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.StatementListener;
import com.exedio.dsmf.Table;

final class SchemaCop extends ConsoleCop
{
	SchemaCop()
	{
		super(TAB_SCHEMA, "schema");
	}
	
	@Override
	void writeHead(final PrintStream out)
	{
		Schema_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		Schema_Jspm.writeBody(this, out, model, request);
	}
	
	private static final Column getColumn(final Schema schema, final String param)
	{
		final int pos = param.indexOf('#');
		if(pos<=0)
			throw new RuntimeException(param);
		
		final Table table = schema.getTable(param.substring(0, pos));
		if(table==null)
			throw new RuntimeException(param);
		
		final Column result = table.getColumn(param.substring(pos+1));
		if(result==null)
			throw new RuntimeException(param);
		
		return result;
	}
	
	private static final Constraint getConstraint(final Schema schema, final String param)
	{
		final int pos = param.indexOf('#');
		if(pos<=0)
			throw new RuntimeException(param);
		
		final Table table = schema.getTable(param.substring(0, pos));
		if(table==null)
			throw new RuntimeException(param);
		
		final Constraint result = table.getConstraint(param.substring(pos+1));
		if(result==null)
			throw new RuntimeException(param);
		
		return result;
	}
	
	static final String DROP_CONSTRAINT = "DROP_CONSTRAINT";
	static final String CREATE_CONSTRAINT = "CREATE_CONSTRAINT";
	
	final static void writeApply(final PrintStream out,
			final HttpServletRequest request, final Model model)
	{
		final Schema schema = model.getVerifiedSchema();
		final StatementListener listener = new StatementListener()
		{
			long beforeExecuteTime = Long.MIN_VALUE;
			
			public void beforeExecute(final String statement)
			{
				out.print("\n\t\t<li>");
				out.print(encodeXml(statement));
				out.print("</li>");
				out.flush();
				beforeExecuteTime = System.currentTimeMillis();
			}
			
			public void afterExecute(final String statement, final int rows)
			{
				final long time = System.currentTimeMillis()-beforeExecuteTime;
				out.print("\n\t\t<li class=\"timelog\">");
				out.print(time);
				out.print("ms, ");
				out.print(rows);
				out.print(" rows</li>");
			}
		};
		{
			final String[] dropConstraints = (String[]) request.getParameterMap().get(DROP_CONSTRAINT);
			if (dropConstraints != null)
			{
				for (int i = 0; i < dropConstraints.length; i++)
				{
					final String dropConstraint = dropConstraints[i];
					final Constraint constraint = getConstraint(schema, dropConstraint);
					constraint.drop(listener);
				}
			}
		}
		{
			final String[] dropColumns = (String[]) request.getParameterMap().get("DROP_COLUMN"); // TODO use constant and use the constant in Schema.jspm
			if (dropColumns != null)
			{
				for (int i = 0; i < dropColumns.length; i++)
				{
					final String dropColumn = dropColumns[i];
					final Column column = getColumn(schema, dropColumn);
					column.drop(listener);
				}
			}
		}
		{
			final String[] dropTables = (String[]) request.getParameterMap().get("DROP_TABLE"); // TODO use constant and use the constant in Schema.jspm
			if (dropTables != null)
			{
				for (int i = 0; i < dropTables.length; i++)
				{
					final String dropTable = dropTables[i];
					final Table table = schema.getTable(dropTable);
					if (table == null)
						throw new RuntimeException(dropTable);
					table.drop(listener);
				}
			}
		}
		{
			for (Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); )
			{
				final String parameterName = (String) i.next();
				if (!parameterName.startsWith("RENAME_TABLE_")) // TODO use constant and use the constant in Schema.jspm
					continue;

				final String targetName = request.getParameter(parameterName).trim();
				if (targetName.length() == 0)
					continue;

				final String sourceName = parameterName.substring("RENAME_TABLE_" // TODO use constant and use the constant in Schema.jspm
						.length());
				final Table table = schema.getTable(sourceName);
				if (table == null)
					throw new RuntimeException(sourceName);

				table.renameTo(targetName, listener);
			}
		}
		{
			for (Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); )
			{
				final String parameterName = (String) i.next();
				if (!parameterName.startsWith("MODIFY_COLUMN_")) // TODO use constant and use the constant in Schema.jspm
					continue;

				final String targetType = request.getParameter(parameterName).trim();
				if (targetType.length() == 0)
					continue;

				final String sourceName = parameterName.substring("MODIFY_COLUMN_" // TODO use constant and use the constant in Schema.jspm
						.length());

				final Column column = getColumn(schema, sourceName);
				if (column == null)
					throw new RuntimeException(sourceName);

				column.modify(targetType, listener);
			}
		}
		{
			for (Iterator i = request.getParameterMap().keySet().iterator(); i.hasNext(); )
			{
				final String parameterName = (String) i.next();
				if (!parameterName.startsWith("RENAME_COLUMN_")) // TODO use constant and use the constant in Schema.jspm
					continue;

				final String targetName = request.getParameter(parameterName).trim();
				if (targetName.length() == 0)
					continue;

				final String sourceName = parameterName.substring("RENAME_COLUMN_" // TODO use constant and use the constant in Schema.jspm
						.length());

				final Column column = getColumn(schema, sourceName);
				if (column == null)
					throw new RuntimeException(sourceName);

				column.renameTo(targetName, listener);
			}
		}
		{
			final String[] createTables = (String[]) request.getParameterMap().get("CREATE_TABLE"); // TODO use constant and use the constant in Schema.jspm
			if (createTables != null)
			{
				for (int i = 0; i < createTables.length; i++)
				{
					final String createTable = createTables[i];
					final Table table = schema.getTable(createTable);
					if (table == null)
						throw new RuntimeException(createTable);

					table.create(listener);
				}
			}
		}
		{
			final String[] analyzeTables = (String[]) request.getParameterMap().get("ANALYZE_TABLE"); // TODO use constant and use the constant in Schema.jspm
			if (analyzeTables != null)
			{
				for (int i = 0; i < analyzeTables.length; i++)
				{
					final String analyzeTable = analyzeTables[i];
					final Table table = schema.getTable(analyzeTable);
					if (table == null)
						throw new RuntimeException(analyzeTable);
					table.analyze(listener);
				}
			}
		}
		{
			final String[] createColumns = (String[]) request.getParameterMap().get("CREATE_COLUMN"); // TODO use constant and use the constant in Schema.jspm
			if (createColumns != null)
			{
				for (int i = 0; i < createColumns.length; i++)
				{
					final String createColumn = createColumns[i];
					final Column column = getColumn(schema, createColumn);
					column.create(listener);
				}
			}
		}
		{
			final String[] createConstraints = (String[]) request.getParameterMap().get(CREATE_CONSTRAINT);
			if (createConstraints != null)
			{
				for (int i = 0; i < createConstraints.length; i++)
				{
					final String createConstraint = createConstraints[i];
					final Constraint constraint = getConstraint(schema, createConstraint);
					constraint.create(listener);
				}
			}
		}
	}

	final static void writeCheckUnsupportedConstraints(final PrintStream out,
			final Model model)
	{
		int sumCount = 0;
		final Schema schema = model.getSchema();
		for(final Table t : schema.getTables())
		{
			for(final Constraint c : t.getConstraints())
				if(!c.isSupported())
				{
					Schema_Jspm.writeCheck(out, c);
					out.flush();
					final long startTime = System.currentTimeMillis();
					final int count = c.check();
					Schema_Jspm.writeDoneCheck(out, startTime, count);
					sumCount += count;
				}
		}
		Schema_Jspm.writeDoneCheckSum(out, sumCount);
	}
}
