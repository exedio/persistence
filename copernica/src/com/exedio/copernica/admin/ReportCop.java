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

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.ReportTable;


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

}
