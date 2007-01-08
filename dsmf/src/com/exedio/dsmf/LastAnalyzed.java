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

package com.exedio.dsmf;

import java.util.Date;

public final class LastAnalyzed extends Node
{
	final Table table;
	final Date date;
		
	LastAnalyzed(final Table table, final Date date)
	{
		super(table.driver, table.connectionProvider);
		this.table = table;
		this.date = date;
	}
	
	public final Date getDate()
	{
		return date;
	}

	@Override
	void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		if(date==null)
		{
			error = "not analyzed !!!";
			particularColor = Color.ERROR;
		}
		else
			particularColor = Color.OK;

		if(!table.required())
			particularColor = particularColor.min(Color.WARNING);
				
		cumulativeColor = particularColor;
	}
}
