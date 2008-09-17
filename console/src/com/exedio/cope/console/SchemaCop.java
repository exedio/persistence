/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import com.exedio.cops.XMLEncoder;
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
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history,
			final boolean historyModelShown)
	{
		Schema_Jspm.writeBody(this, out, model, request);
	}
	
	private static final Table getTable(final Schema schema, final String param)
	{
		final Table result = schema.getTable(param);
		if(result==null)
			throw new RuntimeException(param);
		return result;
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
			final HttpServletRequest request, final Model model, final boolean dryRun)
	{
		final Schema schema = model.getVerifiedSchema();
		final StatementListener listener = new StatementListener()
		{
			long beforeExecuteTime = Long.MIN_VALUE;
			
			public boolean beforeExecute(final String statement)
			{
				out.print("\n\t\t<li>");
				out.print(XMLEncoder.encode(statement));
				out.print("</li>");
				if(dryRun)
				{
					return false;
				}
				else
				{
					out.flush();
					beforeExecuteTime = System.currentTimeMillis();
					return true;
				}
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
		for(final String p : getParameters(request, DROP_CONSTRAINT))
			getConstraint(schema, p).drop(listener);
		for(final String p : getParameters(request, "DROP_COLUMN")) // TODO use constant and use the constant in Schema.jspm
			getColumn    (schema, p).drop(listener);
		for(final String p : getParameters(request, "DROP_TABLE")) // TODO use constant and use the constant in Schema.jspm
			getTable     (schema, p).drop(listener);
		
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
			
			
			

			getTable(schema, sourceName).renameTo(targetName, listener);
		}
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

			
			getColumn(schema, sourceName).modify(targetType, listener);
		}
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

			
			getColumn(schema, sourceName).renameTo(targetName, listener);
		}
		for(final String p : getParameters(request, "CREATE_TABLE")) // TODO use constant and use the constant in Schema.jspm
			getTable     (schema, p).create(listener);
		for(final String p : getParameters(request, "CREATE_COLUMN")) // TODO use constant and use the constant in Schema.jspm
			getColumn    (schema, p).create(listener);
		for(final String p : getParameters(request, CREATE_CONSTRAINT))
			getConstraint(schema, p).create(listener);
	}
	
	private static final String[] EMPTY_STRINGS = new String[]{};
	
	private static final String[] getParameters(final HttpServletRequest request, final String name)
	{
		final String[] result = (String[]) request.getParameterMap().get(name);
		return result!=null ? result : EMPTY_STRINGS;
	}
}
