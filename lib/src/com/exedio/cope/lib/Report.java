package com.exedio.cope.lib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public final class Report
{
	private static final int COLOR_NOT_YET_CALC = 0;
	public static final int COLOR_OK = 1;
	public static final int COLOR_YELLOW = 2;
	public static final int COLOR_RED = 3;

	private final HashMap tables = new HashMap();
	private int color = COLOR_NOT_YET_CALC;

	public final class Table
	{
		public final String name;
		private boolean required = false;
		private boolean exists = false;
		private final HashMap constraints = new HashMap();
		private int color = COLOR_NOT_YET_CALC;

		private Table(final String name)
		{
			this.name = name;
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
			return !required;
		}

		private void finish()
		{
			if(color!=COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			if(isMissing())
				color = COLOR_RED;
			else if(isUnused())
				color = COLOR_YELLOW;
			else
				color = COLOR_OK;
			
			for(Iterator i = constraints.values().iterator(); i.hasNext(); )
			{
				final Constraint constraint = (Constraint)i.next();
				constraint.finish();
				color = Math.max(color, constraint.color);
			}
		}
		
		public int getColor()
		{
			if(color==COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			return color;
		}

	}
	
	public final class Constraint
	{
		public final String name;
		public final Table table;
		private boolean required = false;
		private boolean exists = false;
		private int color = COLOR_NOT_YET_CALC;
		
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

		private void finish()
		{
			if(color!=COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			// TODO: make this dependend on type of constraint:
			// check/not null constraint are yellow only if missing
			// foreign key/unique constraint are red when missing or unused
			if(isMissing() || isUnused())
				color = COLOR_RED;
			else
				color = COLOR_OK;
		}
		
		public int getColor()
		{
			if(color==COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			return color;
		}
	}

	final Table notifyRequiredTable(final String tableName)
	{
		Table result = (Table)tables.get(tableName);
		if(result==null)
		{
			result = new Table(tableName);
			tables.put(tableName, result);
		}
		result.required = true;
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
		result.exists = true;
		return result;
	}
	
	public Collection getTables()
	{
		return tables.values();
	}
	
	void finish()
	{
		if(color!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		for(Iterator i = tables.values().iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			table.finish();
			color = Math.max(color, table.color);
		}
	}

	public int getColor()
	{
		if(color==COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return color;
	}

}
