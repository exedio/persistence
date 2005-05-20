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

public final class ReportColumn extends ReportNode
{
	public final String name;
	public final ReportTable table;
	private final Column column;
	private boolean exists;
	private String existingType;
		
	ReportColumn(final Column column, final ReportTable table)
	{
		this.name = column.id;
		this.table = table;
		this.column = column;
		exists = false;
	}

	ReportColumn(final String name, final String existingType, final ReportTable table)
	{
		this.name = name;
		this.table = table;
		this.column = null;
		this.existingType = existingType;
		exists = true;
	}
	
	void notifyExists(final String existingType)
	{
		if(exists && !this.existingType.equals(existingType))
			throw new RuntimeException(name);

		this.existingType = existingType;
		exists = true;
	}

	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		if(!exists)
		{
			error = "missing";
			particularColor = COLOR_RED;
		}
		else if(column==null)
		{
			error = "not used";
			particularColor = COLOR_YELLOW;
		}
		else
		{
			if(column!=null &&
				existingType!=null &&
				!column.getDatabaseType().equals(existingType))
			{
				error = "different type in database: >"+existingType+"<";
				particularColor = COLOR_RED;
			}
			else
				particularColor = COLOR_OK;
		}
				
		cumulativeColor = particularColor;
	}
		
	public final boolean required()
	{
		return column!=null;
	}
	
	public final boolean exists()
	{
		return exists;
	}
		
	public final String getDatabaseType()
	{
		if(column!=null)
			return column.getDatabaseType();
		else
			return existingType;
	}
		
	public final void create()
	{
		table.report.database.createColumn(column);
	}

	public final void renameTo(final String newName)
	{
		table.report.database.renameColumn(table.name, name, newName, existingType);
	}

	public final void modify(final String newType)
	{
		table.report.database.modifyColumn(table.name, name, newType);
	}

	public final void drop()
	{
		table.report.database.dropColumn(table.name, name);
	}

}
	
