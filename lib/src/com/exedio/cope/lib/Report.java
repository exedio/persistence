package com.exedio.cope.lib;

import java.util.Collection;
import java.util.HashMap;

public final class Report
{
	private final HashMap tables = new HashMap();

	public class Table
	{
		public final String name;

		private Table(final String name)
		{
			this.name = name;
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
		return result;
	}
	
	public Collection getTables()
	{
		return tables.values();
	}

}
