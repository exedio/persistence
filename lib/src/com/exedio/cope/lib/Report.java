package com.exedio.cope.lib;

import java.util.Collection;
import java.util.HashMap;

public final class Report
{
	private final HashMap tables = new HashMap();

	public class Table
	{
		public final String name;
		private boolean required = false;
		private boolean exists = false;
		private final HashMap constraints = new HashMap();

		private Table(final String name)
		{
			this.name = name;
		}
		
		final Constraint notifyExistentConstraint(final String constraintName)
		{
			Constraint result = (Constraint)constraints.get(constraintName);
			if(result==null)
			{
				result = new Constraint(constraintName, this);
				constraints.put(constraintName, result);
			}
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
	}
	
	public class Constraint
	{
		public final String name;
		public final Table table;
		
		private Constraint(final String name, final Table table)
		{
			this.name = name;
			this.table = table; 
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

}
