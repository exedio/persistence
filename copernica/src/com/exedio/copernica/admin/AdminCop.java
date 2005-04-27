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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.ReportTable;
import com.exedio.cops.Cop;

final class AdminCop extends Cop
{
	static final String REPORT = "report";
	static final String SHOW = "show";
	static final String SHOW_DROP_BOXES = "drop";
	static final String SHOW_RENAME_FIELDS = "rename";

	final boolean report;
	final String reportTable;
	final boolean showDropBoxes;
	final boolean showRenameFields;

	AdminCop()
	{
		this(false, null, false, false);
	}

	private AdminCop(final boolean report, final String reportTable, final boolean showDropBoxes, final boolean showRenameFields)
	{
		super("admin.jsp");
		this.report = report;
		this.reportTable = reportTable;
		this.showDropBoxes = showDropBoxes;
		this.showRenameFields = showRenameFields;
		if(!report && reportTable!=null)
			throw new RuntimeException();
		
		if(report)
			addParameter(REPORT, reportTable==null ? "" : reportTable);
		if(showDropBoxes)
			addParameter(SHOW, SHOW_DROP_BOXES);
		if(showRenameFields)
			addParameter(SHOW, SHOW_RENAME_FIELDS);
	}
	
	final AdminCop toggleReport()
	{
		return new AdminCop(!report, null, false, false);
	}
	
	final AdminCop narrowReport(final ReportTable reportTable)
	{
		if(!report)
			throw new RuntimeException();
			
		return new AdminCop(true, reportTable.name, showDropBoxes, showRenameFields);
	}
	
	final AdminCop widenReport()
	{
		if(!report)
			throw new RuntimeException();
			
		return new AdminCop(true, null, showDropBoxes, showRenameFields);
	}
	
	final AdminCop toggleDropBoxes()
	{
		if(!report)
			throw new RuntimeException();

		return new AdminCop(true, reportTable, !showDropBoxes, showRenameFields);
	}
	
	final AdminCop toggleRenameFields()
	{
		if(!report)
			throw new RuntimeException();

		return new AdminCop(true, reportTable, showDropBoxes, !showRenameFields);
	}
	
	final boolean isNarrowReport()
	{
		return reportTable!=null;
	}
	
	final boolean skipTable(final ReportTable table)
	{
		return reportTable!=null && !reportTable.equals(table.name);
	}

	static final AdminCop getCop(final HttpServletRequest request)
	{	
		final String reportID = request.getParameter(REPORT);
		if(reportID==null)
		{
			return new AdminCop(false, null, false, false);
		}
		else
		{
			boolean showDropBoxes = false;
			boolean showRenameFields = false;

			final String[] showIDs = request.getParameterValues(SHOW);
			if(showIDs!=null)
			{
				for(int i = 0; i<showIDs.length; i++)
				{
					final String showID = showIDs[i];
					if(SHOW_DROP_BOXES.equals(showID))
						showDropBoxes = true;
					else if(SHOW_RENAME_FIELDS.equals(showID))
						showRenameFields = true;
					else
						throw new RuntimeException(showID);
				}
			}
			
			if(reportID.length()==0)
				return new AdminCop(true, null, showDropBoxes, showRenameFields);
			else
				return new AdminCop(true, reportID, showDropBoxes, showRenameFields);
		}
	}

}
