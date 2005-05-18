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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.Model;
import com.exedio.cops.Cop;

abstract class AdminCop extends Cop
{
	final String name;

	protected AdminCop(final String name)
	{
		super("admin.jsp");
		this.name = name;
	}
	
	final PropertiesCop toProperties()
	{
		return new PropertiesCop();
	}
	
	final ReportCop toReport()
	{
		return new ReportCop(null, false, false);
	}
	
	final StatisticsCop toStatistics()
	{
		return new StatisticsCop();
	}
	
	void writeHead(PrintStream out) throws IOException
	{
		// default implementation does nothing
	}
	
	abstract void writeBody(PrintStream out, Model model) throws IOException;
	
	static final AdminCop getCop(final HttpServletRequest request)
	{
		if(request.getParameter(StatisticsCop.STATISTICS)!=null)
			return new StatisticsCop();
		
		final String reportID = request.getParameter(ReportCop.REPORT);
		if(reportID==null)
		{
			return new PropertiesCop();
		}
		else
		{
			boolean showDropBoxes = false;
			boolean showRenameFields = false;

			final String[] showIDs = request.getParameterValues(ReportCop.SHOW);
			if(showIDs!=null)
			{
				for(int i = 0; i<showIDs.length; i++)
				{
					final String showID = showIDs[i];
					if(ReportCop.SHOW_DROP_BOXES.equals(showID))
						showDropBoxes = true;
					else if(ReportCop.SHOW_RENAME_FIELDS.equals(showID))
						showRenameFields = true;
					else
						throw new RuntimeException(showID);
				}
			}
			
			if(reportID.length()==0)
				return new ReportCop(null, showDropBoxes, showRenameFields);
			else
				return new ReportCop(reportID, showDropBoxes, showRenameFields);
		}
	}

}
