package com.exedio.cope.lib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

abstract class Node
{
	protected int cumulativeColor = Report.COLOR_NOT_YET_CALC;

	abstract void finish();

	public final int getCumulativeColor()
	{
		if(cumulativeColor==Report.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return cumulativeColor;
	}
}

public final class Report extends Node
{
	static final int COLOR_NOT_YET_CALC = 0;
	public static final int COLOR_OK = 1;
	public static final int COLOR_YELLOW = 2;
	public static final int COLOR_RED = 3;

	private final HashMap tables = new HashMap();
	
	public final class Table extends Node
	{
		public final String name;
		public final com.exedio.cope.lib.Table table;
		private boolean exists = false;
		private final HashMap constraints = new HashMap();

		private Table(final com.exedio.cope.lib.Table table)
		{
			this.name = table.id;
			this.table = table;
			this.exists = false;
		}

		private Table(final String name)
		{
			this.name = name;
			this.table = null;
			this.exists = true;
		}
		
		final Constraint notifyRequiredConstraint(final String constraintName)
		{
			Constraint result = (Constraint)constraints.get(constraintName);
			if(result==null)
			{
				result = new Constraint(constraintName, this);
				constraints.put(constraintName, result);
			}
			result.required = true;
			return result;
		}
		
		final Constraint notifyExistentConstraint(final String constraintName)
		{
			Constraint result = (Constraint)constraints.get(constraintName);
			if(result==null)
			{
				result = new Constraint(constraintName, this);
				constraints.put(constraintName, result);
			}
			result.exists = true;
			return result;
		}
		
		public final Collection getConstraints()
		{
			return constraints.values();
		}
		
		public final boolean isMissing()
		{
			return !exists;
		}
		
		public final boolean isUnused()
		{
			return table==null;
		}

		protected void finish()
		{
			if(cumulativeColor!=COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			if(isMissing())
				cumulativeColor = COLOR_RED;
			else if(isUnused())
				cumulativeColor = COLOR_YELLOW;
			else
				cumulativeColor = COLOR_OK;
			
			for(Iterator i = constraints.values().iterator(); i.hasNext(); )
			{
				final Constraint constraint = (Constraint)i.next();
				constraint.finish();
				cumulativeColor = Math.max(cumulativeColor, constraint.cumulativeColor);
			}
		}

	}
	
	public final class Constraint extends Node
	{
		public final String name;
		public final Table table;
		private boolean required = false;
		private boolean exists = false;
		
		private Constraint(final String name, final Table table)
		{
			this.name = name;
			this.table = table; 
		}

		public final boolean isMissing()
		{
			return !exists;
		}
		
		public final boolean isUnused()
		{
			return !required;
		}

		protected void finish()
		{
			if(cumulativeColor!=COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			// TODO: make this dependend on type of constraint:
			// check/not null constraint are yellow only if missing
			// foreign key/unique constraint are red when missing or unused
			if(isMissing() || isUnused())
				cumulativeColor = COLOR_RED;
			else
				cumulativeColor = COLOR_OK;
		}
		
	}

	final Table notifyRequiredTable(final com.exedio.cope.lib.Table table)
	{
		final Table result = new Table(table);
		if(tables.put(table.id, result)!=null)
			throw new RuntimeException();
		return result;
	}
	
	final Table notifyExistentTable(final String tableName)
	{
		Table result = (Table)tables.get(tableName);
		if(result==null)
		{
			result = new Table(tableName);
			tables.put(tableName, result);
		}
		else
			result.exists = true;

		return result;
	}
	
	public Collection getTables()
	{
		return tables.values();
	}
	
	void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();
		
		cumulativeColor = COLOR_OK;

		for(Iterator i = tables.values().iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			table.finish();
			cumulativeColor = Math.max(cumulativeColor, table.cumulativeColor);
		}
	}

}
