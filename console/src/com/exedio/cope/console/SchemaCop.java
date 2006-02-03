/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
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
	
	static SchemaCop getCop(final String schemaID, final HttpServletRequest request)
	{
		boolean showDropBoxes = false;
		boolean showRenameFields = false;

		final String[] showIDs = request.getParameterValues(SchemaCop.SHOW);
		if(showIDs!=null)
		{
			for(int i = 0; i<showIDs.length; i++)
			{
				final String showID = showIDs[i];
				if(SchemaCop.SHOW_DROP_BOXES.equals(showID))
					showDropBoxes = true;
				else if(SchemaCop.SHOW_RENAME_FIELDS.equals(showID))
					showRenameFields = true;
				else
					throw new RuntimeException(showID);
			}
		}
		
		if(schemaID.length()==0)
			return new SchemaCop(null, showDropBoxes, showRenameFields);
		else
			return new SchemaCop(schemaID, showDropBoxes, showRenameFields);
	}
	
	void writeHead(final HttpServletRequest request, final PrintStream out) throws IOException
	{
		Schema_Jspm.writeHead(request, out);
	}

	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		Schema_Jspm.writeSchema(out, model, this, request);
	}
	
	final SchemaCop narrow(final Table table)
	{
		return new SchemaCop(table.getName(), showDropBoxes, showRenameFields);
	}
	
	final SchemaCop widen()
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
	
	final boolean isNarrow()
	{
		return table!=null;
	}
	
	final boolean skipTable(final Table table)
	{
		return this.table!=null && !this.table.equals(table.getName());
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
			throws IOException
	{
		final Schema schema = model.getVerifiedSchema();
		{
			final String[] dropConstraints = (String[]) request.getParameterMap().get(DROP_CONSTRAINT);
			if (dropConstraints != null)
			{
				for (int i = 0; i < dropConstraints.length; i++)
				{
					final String dropConstraint = dropConstraints[i];
					final Constraint constraint = getConstraint(schema, dropConstraint);
					Schema_Jspm.writeDrop(out, constraint);
					out.flush();
					final long startTime = System.currentTimeMillis();
					constraint.drop();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
		{
			final String[] dropColumns = (String[]) request.getParameterMap().get(
					"DROP_COLUMN"); // TODO use constant and use the constant in Schema.jspm
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
					"DROP_TABLE"); // TODO use constant and use the constant in Schema.jspm
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

				Schema_Jspm.writeRename(out, column, targetName);
				out.flush();
				final long startTime = System.currentTimeMillis();
				column.renameTo(targetName);
				Schema_Jspm.writeDone(out, startTime);
			}
		}
		{
			final String[] createTables = (String[]) request.getParameterMap()
					.get("CREATE_TABLE"); // TODO use constant and use the constant in Schema.jspm
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
					.get("ANALYZE_TABLE"); // TODO use constant and use the constant in Schema.jspm
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
			final String[] createColumns = (String[]) request.getParameterMap()
					.get("CREATE_COLUMN"); // TODO use constant and use the constant in Schema.jspm
			if (createColumns != null)
			{
				for (int i = 0; i < createColumns.length; i++)
				{
					final String createColumn = createColumns[i];
					final Column column = getColumn(schema, createColumn);
					Schema_Jspm.writeCreate(out, column);
					out.flush();
					final long startTime = System.currentTimeMillis();
					column.create();
					Schema_Jspm.writeDone(out, startTime);
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
					Schema_Jspm.writeCreate(out, constraint);
					out.flush();
					final long startTime = System.currentTimeMillis();
					constraint.create();
					Schema_Jspm.writeDone(out, startTime);
				}
			}
		}
	}

}
