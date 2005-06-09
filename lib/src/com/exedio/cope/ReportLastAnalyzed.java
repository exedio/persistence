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
package com.exedio.cope;

import java.util.Date;

public final class ReportLastAnalyzed extends ReportNode
{
	public final ReportTable table;
	public final Date lastAnalyzed;
		
	ReportLastAnalyzed(final ReportTable table, final Date lastAnalyzed)
	{
		super(table.database, table.driver);
		this.table = table;
		this.lastAnalyzed = lastAnalyzed;
	}

	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		if(lastAnalyzed==null)
		{
			error = "not analyzed !!!";
			particularColor = COLOR_ERROR;
		}
		else
			particularColor = COLOR_OK;

		if(!table.required())
			particularColor = Math.min(particularColor, COLOR_WARNING);
				
		cumulativeColor = particularColor;
	}
}
