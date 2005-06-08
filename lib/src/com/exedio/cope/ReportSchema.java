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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public final class ReportSchema extends ReportNode
{
	final Database database;
	private final HashMap tableMap = new HashMap();
	private final ArrayList tableList = new ArrayList();
	
	ReportSchema(final Database database)
	{
		this.database = database;
	}

	final void register(final ReportTable table)
	{
		if(tableMap.put(table.name, table)!=null)
			throw new RuntimeException(table.name);
		tableList.add(table);
	}
	
	final ReportTable notifyExistentTable(final String tableName)
	{
		ReportTable result = (ReportTable)tableMap.get(tableName);
		if(result==null)
			result = new ReportTable(this, tableName);
		else
			result.notifyExists();

		return result;
	}
	
	public ReportTable getTable(final String name)
	{
		return (ReportTable)tableMap.get(name);
	}
	
	public Collection getTables()
	{
		return tableList;
	}
	
	void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();
		
		particularColor = COLOR_OK;

		cumulativeColor = particularColor;
		for(Iterator i = tableList.iterator(); i.hasNext(); )
		{
			final ReportTable table = (ReportTable)i.next();
			table.finish();
			cumulativeColor = Math.max(cumulativeColor, table.cumulativeColor);
		}
	}

}
