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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public final class ReportTable extends ReportNode
{
	public final Report report;
	public final String name;
	final Table table;
	private boolean exists = false;
	private ReportLastAnalyzed lastAnalyzed = null;

	private final HashMap columnMap = new HashMap();
	private final ArrayList columnList = new ArrayList();

	private final HashMap constraintMap = new HashMap();
	private final ArrayList constraintList = new ArrayList();

	ReportTable(final Report report, final Table table)
	{
		this.report = report;
		this.name = table.id;
		this.table = table;
		this.exists = false;
	}

	ReportTable(final Report report, final String name)
	{
		this.report = report;
		this.name = name;
		this.table = null;
		this.exists = true;
	}
		
	final void setLastAnalyzed(final Date lastAnalyzed)
	{
		if(this.lastAnalyzed!=null)
			throw new RuntimeException();

		this.lastAnalyzed = new ReportLastAnalyzed(lastAnalyzed, this);
	}
	
	final void notifyExists()
	{
		exists = true;
	}
		
	final void notifyRequiredColumn(final Column column)
	{
		final ReportColumn result = new ReportColumn(column.id, column.getDatabaseType(), true, this);
		if(columnMap.put(result.name, result)!=null)
			throw new RuntimeException(column.toString());
		columnList.add(result);

		if(column.primaryKey)
		{
			addRequiredConstraint(
					new ReportConstraint(column.getPrimaryKeyConstraintID(), ReportConstraint.TYPE_PRIMARY_KEY, this));
		}
		else
		{
			final String checkConstraint = column.getCheckConstraint();
			if(checkConstraint!=null)
			{
				addRequiredConstraint(
						new ReportConstraint(column.getCheckConstraintID(), ReportConstraint.TYPE_CHECK, this, checkConstraint));
			}
		}
		if(column instanceof ItemColumn)
		{
			final ItemColumn itemColumn = (ItemColumn)column;
			addRequiredConstraint(
					new ReportConstraint(itemColumn.integrityConstraintName, ReportConstraint.TYPE_FOREIGN_KEY, this));
		}
	}
		
	final ReportColumn notifyExistentColumn(final String columnName, final String existingType)
	{
		ReportColumn result = (ReportColumn)columnMap.get(columnName);
		if(result==null)
		{
			result = new ReportColumn(columnName, existingType, false, this);
			columnMap.put(columnName, result);
			columnList.add(result);
		}
		else
		{
			result.notifyExists(existingType);
		}

		return result;
	}
	
	private final void addRequiredConstraint(final ReportConstraint constraint)
	{
		if(constraintMap.put(constraint.name, constraint)!=null)
			throw new RuntimeException(constraint.name);
		constraintList.add(constraint);
		constraint.notifyRequired();
	}
	
	final ReportConstraint notifyRequiredConstraint(final String constraintName, final int type)
	{
		final ReportConstraint result = new ReportConstraint(constraintName, type, this);
		addRequiredConstraint(result);
		return result;
	}
	
	final ReportConstraint notifyRequiredConstraint(final String constraintName, final int type, final String condition)
	{
		final ReportConstraint result = new ReportConstraint(constraintName, type, this, condition);
		addRequiredConstraint(result);
		return result;
	}
	
	private final ReportConstraint getOrCreateExistentConstraint(final String constraintName, final int type)
	{
		ReportConstraint result = (ReportConstraint)constraintMap.get(constraintName);
		if(result==null)
		{
			result = new ReportConstraint(constraintName, type, this);
			constraintMap.put(constraintName, result);
			constraintList.add(result);
		}
		return result;
	}
		
	final ReportConstraint notifyExistentConstraint(final String constraintName, final int type)
	{
		final ReportConstraint result = getOrCreateExistentConstraint(constraintName, type);
		result.notifyExists();
		return result;
	}
	
	final ReportConstraint notifyExistentCheckConstraint(final String constraintName, final String condition)
	{
		final ReportConstraint result = getOrCreateExistentConstraint(constraintName, ReportConstraint.TYPE_CHECK);
		result.notifyExistsCondition(condition);
		return result;
	}
	
	final ReportConstraint notifyExistentUniqueConstraint(final String constraintName, final String condition)
	{
		final ReportConstraint result = getOrCreateExistentConstraint(constraintName, ReportConstraint.TYPE_UNIQUE);
		result.notifyExistsCondition(condition);
		return result;
	}
	
	public final boolean required()
	{
		return table!=null;
	}
	
	public final boolean exists()
	{
		return exists;
	}
		
	public final ReportLastAnalyzed getLastAnalyzed()
	{
		return lastAnalyzed;
	}
		
	public final Collection getColumns()
	{
		return columnList;
	}
		
	public final ReportColumn getColumn(final String columnName)
	{
		return (ReportColumn)columnMap.get(columnName);
	}
		
	public final Collection getConstraints()
	{
		return constraintList;
	}
		
	public final ReportConstraint getConstraint(final String constraintName)
	{
		return (ReportConstraint)constraintMap.get(constraintName);
	}
		
	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		if(!exists)
		{
			error = "MISSING !!!";
			particularColor = COLOR_ERROR;
		}
		else if(table==null)
		{
			error = "not used";
			particularColor = COLOR_WARNING;
		}
		else
			particularColor = COLOR_OK;
				
		cumulativeColor = particularColor;
			
		if(lastAnalyzed!=null)
		{
			lastAnalyzed.finish();
			cumulativeColor = Math.max(cumulativeColor, lastAnalyzed.cumulativeColor);
		}
			
		for(Iterator i = columnList.iterator(); i.hasNext(); )
		{
			final ReportColumn column = (ReportColumn)i.next();
			column.finish();
			cumulativeColor = Math.max(cumulativeColor, column.cumulativeColor);
		}

		for(Iterator i = constraintList.iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();
			constraint.finish();
			cumulativeColor = Math.max(cumulativeColor, constraint.cumulativeColor);
		}
	}
	
	public final void create()
	{
		report.database.createTable(table);
	}
	
	public final void renameTo(final String newName)
	{
		report.database.renameTable(name, newName);
	}

	public final void drop()
	{
		report.database.dropTable(name);
	}

	public final void analyze()
	{
		report.database.analyzeTable(name);
	}
	
}
