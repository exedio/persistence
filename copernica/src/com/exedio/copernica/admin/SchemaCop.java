/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.copernica.admin;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;


final class SchemaCop extends AdminCop
{
	static final String SCHEMA = "schema";
	static final String SHOW = "show";
	static final String SHOW_DROP_BOXES = "drop";
	static final String SHOW_RENAME_FIELDS = "rename";

	final String table;
	final boolean showDropBoxes;
	final boolean showRenameFields;

	SchemaCop(final String table, final boolean showDropBoxes, final boolean showRenameFields)
	{
		super("schema");
		this.table = table;
		this.showDropBoxes = showDropBoxes;
		this.showRenameFields = showRenameFields;
		
		addParameter(SCHEMA, table==null ? "" : table);
		if(showDropBoxes)
			addParameter(SHOW, SHOW_DROP_BOXES);
		if(showRenameFields)
			addParameter(SHOW, SHOW_RENAME_FIELDS);
	}
	
	void writeHead(final PrintStream out) throws IOException
	{
		Schema_Jspm.writeHead(out);
	}

	final void writeBody(final PrintStream out, final Model model) throws IOException
	{
		Schema_Jspm.writeSchema(out, model, this);
	}
	
	final SchemaCop narrow(final Table table)
	{
		return new SchemaCop(table.getName(), showDropBoxes, showRenameFields);
	}
	
	final SchemaCop widenReport()
	{
		return new SchemaCop(null, showDropBoxes, showRenameFields);
	}
	
	final SchemaCop toggleDropBoxes()
	{
		return new SchemaCop(table, !showDropBoxes, showRenameFields);
	}
	
	final SchemaCop toggleRenameFields()
	{
		return new SchemaCop(table, showDropBoxes, !showRenameFields);
	}
	
	final boolean isNarrowReport()
	{
		return table!=null;
	}
	
	final boolean skipTable(final Table table)
	{
		return this.table!=null && !this.table.equals(table.getName());
	}

	private static final Column getColumn(final Schema schema, final String columnParameter)
	{
		final int pos = columnParameter.indexOf('#');
		if(pos<=0)
			throw new RuntimeException(columnParameter);
		
		final Table table = schema.getTable(columnParameter.substring(0, pos));
		if(table==null)
			throw new RuntimeException(columnParameter);
		
		final Column column = table.getColumn(columnParameter.substring(pos+1));
		if(column==null)
			throw new RuntimeException(columnParameter);
		
		return column;
	}
	
	final static void writeApply(final PrintStream out,
			final HttpServletRequest request, final Model model)
			throws IOException
	{
		final Schema schema = model.getVerifiedSchema();
		{
			final String[] dropColumns = (String[]) request.getParameterMap().get(
					"DROP_COLUMN");
			if (dropColumns != null)
			{
				for (int i = 0; i < dropColumns.length; i++)
				{
					final String dropColumn = dropColumns[i];
					final Column column = getColumn(schema, dropColumn);
					Schema_Jspm.writeDrop(out, column);
					out.flush();
					final long startTime = System.currentTimeMillis();
					column.drop();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
		{
			final String[] dropTables = (String[]) request.getParameterMap().get(
					"DROP_TABLE");
			if (dropTables != null)
			{
				for (int i = 0; i < dropTables.length; i++)
				{
					final String dropTable = dropTables[i];
					final Table table = schema.getTable(dropTable);
					if (table == null)
						throw new RuntimeException(dropTable);
					Schema_Jspm.writeDrop(out, table);
					out.flush();
					final long startTime = System.currentTimeMillis();
					table.drop();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
		{
			for (Iterator i = request.getParameterMap().keySet().iterator(); i
					.hasNext();)
			{
				final String parameterName = (String) i.next();
				if (!parameterName.startsWith("RENAME_TABLE_"))
					continue;

				final String targetName = request.getParameter(parameterName).trim();
				if (targetName.length() == 0)
					continue;

				final String sourceName = parameterName.substring("RENAME_TABLE_"
						.length());
				final Table table = schema.getTable(sourceName);
				if (table == null)
					throw new RuntimeException(sourceName);

				Schema_Jspm.writeRename(out, table, targetName);
				out.flush();
				final long startTime = System.currentTimeMillis();
				table.renameTo(targetName);
				Schema_Jspm.writeDone(out, startTime);
			}
		}
		{
			for (Iterator i = request.getParameterMap().keySet().iterator(); i
					.hasNext();)
			{
				final String parameterName = (String) i.next();
				if (!parameterName.startsWith("MODIFY_COLUMN_"))
					continue;

				final String targetType = request.getParameter(parameterName).trim();
				if (targetType.length() == 0)
					continue;

				final String sourceName = parameterName.substring("MODIFY_COLUMN_"
						.length());

				final Column column = getColumn(schema, sourceName);
				if (column == null)
					throw new RuntimeException(sourceName);

				Schema_Jspm.writeModify(out, column, targetType);
				out.flush();
				final long startTime = System.currentTimeMillis();
				column.modify(targetType);
				Schema_Jspm.writeDone(out, startTime);
			}
		}
		{
			for (Iterator i = request.getParameterMap().keySet().iterator(); i
					.hasNext();)
			{
				final String parameterName = (String) i.next();
				if (!parameterName.startsWith("RENAME_COLUMN_"))
					continue;

				final String targetName = request.getParameter(parameterName).trim();
				if (targetName.length() == 0)
					continue;

				final String sourceName = parameterName.substring("RENAME_COLUMN_"
						.length());

				final Column column = getColumn(schema, sourceName);
				if (column == null)
					throw new RuntimeException(sourceName);

				Schema_Jspm.writeRename(out, column, targetName);
				out.flush();
				final long startTime = System.currentTimeMillis();
				column.renameTo(targetName);
				Schema_Jspm.writeDone(out, startTime);
			}
		}
		{
			final String[] createTables = (String[]) request.getParameterMap()
					.get("CREATE_TABLE");
			if (createTables != null)
			{
				for (int i = 0; i < createTables.length; i++)
				{
					final String createTable = createTables[i];
					final Table table = schema.getTable(createTable);
					if (table == null)
						throw new RuntimeException(createTable);

					Schema_Jspm.writeCreate(out, table);
					out.flush();
					final long startTime = System.currentTimeMillis();
					table.create();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
		{
			final String[] analyzeTables = (String[]) request.getParameterMap()
					.get("ANALYZE_TABLE");
			if (analyzeTables != null)
			{
				for (int i = 0; i < analyzeTables.length; i++)
				{
					final String analyzeTable = analyzeTables[i];
					final Table table = schema.getTable(analyzeTable);
					if (table == null)
						throw new RuntimeException(analyzeTable);
					Schema_Jspm.writeAnalyze(out, table);
					out.flush();
					final long startTime = System.currentTimeMillis();
					table.analyze();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
		{
			final String[] createColums = (String[]) request.getParameterMap()
					.get("CREATE_COLUMN");
			if (createColums != null)
			{
				for (int i = 0; i < createColums.length; i++)
				{
					final String createColumn = createColums[i];
					final Column column = getColumn(schema, createColumn);
					Schema_Jspm.writeCreate(out, column);
					out.flush();
					final long startTime = System.currentTimeMillis();
					column.create();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
	}

}
