package com.exedio.cope.lib;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public final class ReportTable extends ReportNode
{
	public final String name;
	private final Table table;
	boolean exists = false;
	private ReportLastAnalyzed lastAnalyzed = null;
	private final HashMap columns = new HashMap();
	private final HashMap constraints = new HashMap();

	ReportTable(final com.exedio.cope.lib.Table table)
	{
		this.name = table.id;
		this.table = table;
		this.exists = false;
	}

	ReportTable(final String name)
	{
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
		
	final ReportColumn notifyRequiredColumn(final Column column)
	{
		final ReportColumn result = new ReportColumn(column, this);
		if(columns.put(result.name, result)!=null)
			throw new RuntimeException(column.toString());
		return result;
	}
		
	final ReportColumn notifyExistentColumn(final String columnName)
	{
		ReportColumn result = (ReportColumn)columns.get(columnName);
		if(result==null)
		{
			result = new ReportColumn(columnName);
			columns.put(columnName, result);
		}
		else
			result.exists = true;

		return result;
	}
	
	final ReportConstraint notifyRequiredConstraint(final String constraintName)
	{
		final ReportConstraint result = new ReportConstraint(constraintName, this);
		if(constraints.put(result.name, result)!=null)
			throw new RuntimeException(constraintName);
		result.required = true;
		return result;
	}
		
	final ReportConstraint notifyExistentConstraint(final String constraintName)
	{
		ReportConstraint result = (ReportConstraint)constraints.get(constraintName);
		if(result==null)
		{
			result = new ReportConstraint(constraintName, this);
			constraints.put(constraintName, result);
		}
		result.exists = true;
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
		return columns.values();
	}
		
	public final Collection getConstraints()
	{
		return constraints.values();
	}
		
	protected void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		if(!exists)
		{
			error = "MISSING !!!";
			particularColor = COLOR_RED;
		}
		else if(table==null)
		{
			error = "not used";
			particularColor = COLOR_YELLOW;
		}
		else
			particularColor = COLOR_OK;
				
		cumulativeColor = particularColor;
			
		if(lastAnalyzed!=null)
		{
			lastAnalyzed.finish();
			cumulativeColor = Math.max(cumulativeColor, lastAnalyzed.cumulativeColor);
		}
			
		for(Iterator i = columns.values().iterator(); i.hasNext(); )
		{
			final ReportColumn column = (ReportColumn)i.next();
			column.finish();
			cumulativeColor = Math.max(cumulativeColor, column.cumulativeColor);
		}

		for(Iterator i = constraints.values().iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();
			constraint.finish();
			cumulativeColor = Math.max(cumulativeColor, constraint.cumulativeColor);
		}
	}

}
