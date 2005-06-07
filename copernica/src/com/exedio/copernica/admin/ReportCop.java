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
import com.exedio.cope.Report;
import com.exedio.cope.ReportColumn;
import com.exedio.cope.ReportTable;


final class ReportCop extends AdminCop
{
	static final String REPORT = "report";
	static final String SHOW = "show";
	static final String SHOW_DROP_BOXES = "drop";
	static final String SHOW_RENAME_FIELDS = "rename";

	final String reportTable;
	final boolean showDropBoxes;
	final boolean showRenameFields;

	ReportCop(final String reportTable, final boolean showDropBoxes, final boolean showRenameFields)
	{
		super("reports");
		this.reportTable = reportTable;
		this.showDropBoxes = showDropBoxes;
		this.showRenameFields = showRenameFields;
		
		addParameter(REPORT, reportTable==null ? "" : reportTable);
		if(showDropBoxes)
			addParameter(SHOW, SHOW_DROP_BOXES);
		if(showRenameFields)
			addParameter(SHOW, SHOW_RENAME_FIELDS);
	}
	
	void writeHead(final PrintStream out) throws IOException
	{
		Report_Jspm.writeHead(out);
	}

	final void writeBody(final PrintStream out, final Model model) throws IOException
	{
		Report_Jspm.writeReport(out, model, this);
	}
	
	final ReportCop narrowReport(final ReportTable reportTable)
	{
		return new ReportCop(reportTable.name, showDropBoxes, showRenameFields);
	}
	
	final ReportCop widenReport()
	{
		return new ReportCop(null, showDropBoxes, showRenameFields);
	}
	
	final ReportCop toggleDropBoxes()
	{
		return new ReportCop(reportTable, !showDropBoxes, showRenameFields);
	}
	
	final ReportCop toggleRenameFields()
	{
		return new ReportCop(reportTable, showDropBoxes, !showRenameFields);
	}
	
	final boolean isNarrowReport()
	{
		return reportTable!=null;
	}
	
	final boolean skipTable(final ReportTable table)
	{
		return reportTable!=null && !reportTable.equals(table.name);
	}

	private static final ReportColumn getColumn(final Report report, final String columnParameter)
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
	
	final static void writeApply(final PrintStream out,
			final HttpServletRequest request, final Model model)
			throws IOException
	{
		final Report report = model.reportDatabase();
		{
			final String[] dropColumns = (String[]) request.getParameterMap().get(
					"DROP_COLUMN");
			if (dropColumns != null)
			{
				for (int i = 0; i < dropColumns.length; i++)
				{
					final String dropColumn = dropColumns[i];
					final ReportColumn column = getColumn(report, dropColumn);
					Report_Jspm.writeDrop(out, column);
					out.flush();
					final long startTime = System.currentTimeMillis();
					column.drop();
					Report_Jspm.writeDone(out, startTime);
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
					final ReportTable table = report.getTable(dropTable);
					if (table == null)
						throw new RuntimeException(dropTable);
					Report_Jspm.writeDrop(out, table);
					out.flush();
					final long startTime = System.currentTimeMillis();
					table.drop();
					Report_Jspm.writeDone(out, startTime);
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
				final ReportTable table = report.getTable(sourceName);
				if (table == null)
					throw new RuntimeException(sourceName);

				Report_Jspm.writeRename(out, table, targetName);
				out.flush();
				final long startTime = System.currentTimeMillis();
				table.renameTo(targetName);
				Report_Jspm.writeDone(out, startTime);
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

				final ReportColumn column = getColumn(report, sourceName);
				if (column == null)
					throw new RuntimeException(sourceName);

				Report_Jspm.writeModify(out, column, targetType);
				out.flush();
				final long startTime = System.currentTimeMillis();
				column.modify(targetType);
				Report_Jspm.writeDone(out, startTime);
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

				final ReportColumn column = getColumn(report, sourceName);
				if (column == null)
					throw new RuntimeException(sourceName);

				Report_Jspm.writeRename(out, column, targetName);
				out.flush();
				final long startTime = System.currentTimeMillis();
				column.renameTo(targetName);
				Report_Jspm.writeDone(out, startTime);
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
					final ReportTable table = report.getTable(createTable);
					if (table == null)
						throw new RuntimeException(createTable);

					Report_Jspm.writeCreate(out, table);
					out.flush();
					final long startTime = System.currentTimeMillis();
					table.create();
					Report_Jspm.writeDone(out, startTime);
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
					final ReportTable table = report.getTable(analyzeTable);
					if (table == null)
						throw new RuntimeException(analyzeTable);
					Report_Jspm.writeAnalyze(out, table);
					out.flush();
					final long startTime = System.currentTimeMillis();
					table.analyze();
					Report_Jspm.writeDone(out, startTime);
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
					final ReportColumn column = getColumn(report, createColumn);
					Report_Jspm.writeCreate(out, column);
					out.flush();
					final long startTime = System.currentTimeMillis();
					column.create();
					Report_Jspm.writeDone(out, startTime);
				}
			}
		}
	}

}
