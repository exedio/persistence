package com.exedio.cope.lib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

abstract class Node
{
	protected String error = null;
	protected int particularColor = Report.COLOR_NOT_YET_CALC;
	protected int cumulativeColor = Report.COLOR_NOT_YET_CALC;

	abstract void finish();

	public final String getError()
	{
		if(particularColor==Report.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return error;
	}

	public final int getParticularColor()
	{
		if(particularColor==Report.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return particularColor;
	}

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

		protected void finish()
		{
			if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
				throw new RuntimeException();

			// TODO: make this dependend on type of constraint:
			// check/not null constraint are yellow only if missing
			// foreign key/unique constraint are red when missing or unused
			if(!exists)
			{
				error = "missing";
				particularColor = COLOR_RED;
			}
			else if(!required)
			{
				error = "not used";
				if(table.table==null)
					particularColor = COLOR_YELLOW;
				else
					particularColor = COLOR_RED;
			}
			else
				particularColor = COLOR_OK;
				
			cumulativeColor = particularColor;
		}
		
	}
	
	Report(final List modelTables)
	{
		for(Iterator i = modelTables.iterator(); i.hasNext(); )
		{
			final com.exedio.cope.lib.Table modelTable = (com.exedio.cope.lib.Table)i.next();
			final Report.Table reportTable = new Table(modelTable);
			if(tables.put(modelTable.id, reportTable)!=null)
				throw new RuntimeException();
	
			for(Iterator j = modelTable.getAllColumns().iterator(); j.hasNext(); )
			{
				final Column column = (Column)j.next();
				
				if(column.primaryKey)
					reportTable.notifyRequiredConstraint(column.getPrimaryKeyConstraintID());
				else if(column.notNull)
					reportTable.notifyRequiredConstraint(column.getNotNullConstraintID());
					
				if(column instanceof StringColumn)
				{
					final StringColumn stringColumn = (StringColumn)column;

					if(stringColumn.minimumLength>0)
						reportTable.notifyRequiredConstraint(stringColumn.getMinimumLengthConstraintID());

					if(stringColumn.maximumLength!=Integer.MAX_VALUE)
						reportTable.notifyRequiredConstraint(stringColumn.getMaximumLengthConstraintID());
				}
				else if(column instanceof IntegerColumn)
				{
					final IntegerColumn intColumn = (IntegerColumn)column;
					if(intColumn.allowedValues!=null)
						reportTable.notifyRequiredConstraint(intColumn.getAllowedValuesConstraintID());

					if(intColumn instanceof ItemColumn)
					{
						final ItemColumn itemColumn = (ItemColumn)intColumn;
						reportTable.notifyRequiredConstraint(itemColumn.integrityConstraintName);
					}
				}
			}
			for(Iterator j = modelTable.getUniqueConstraints().iterator(); j.hasNext(); )
			{
				final UniqueConstraint uniqueConstraint = (UniqueConstraint)j.next();
				reportTable.notifyRequiredConstraint(uniqueConstraint.getID());
			}
		}
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
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();
		
		particularColor = COLOR_OK;

		cumulativeColor = particularColor;
		for(Iterator i = tables.values().iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			table.finish();
			cumulativeColor = Math.max(cumulativeColor, table.cumulativeColor);
		}
	}

}
