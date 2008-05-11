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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;

final class DatabaseLogCop extends ConsoleCop
{
	static final String ENABLE = "dblogenable";
	static final String THRESHOLD = "dblogthreshold";
	static final String SQL = "dblogsql";
	
	DatabaseLogCop()
	{
		super(TAB_DATBASE_LOG, "db logs");
	}
	
	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			final boolean enable = request.getParameter(ENABLE)!=null;
			final String threshold = request.getParameter(THRESHOLD).trim();
			final String sql = request.getParameter(SQL).trim();
			model.setDatabaseLog(
					enable,
					threshold.length()>0 ? Integer.parseInt(threshold) : 0,
					sql.length()>0 ? sql : null,
					System.out);
		}
	}
	
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request, final boolean historyAvailable, final boolean historyModelShown, final boolean historyRunning)
	{
		DatabaseLog_Jspm.writeBody(this, out,
				model.isDatabaseLogEnabled(),
				model.getDatabaseLogThreshold(),
				model.getDatabaseLogSQL());
	}
}
