
package com.exedio.cope.lib;

import bak.pcj.list.IntArrayList;

public final class Statement
{
	final StringBuffer text = new StringBuffer();
	final IntArrayList columnTypes;
		
	Statement(final boolean useDefineColumnTypes)
	{
		columnTypes = useDefineColumnTypes ? new IntArrayList() : null;
	}

	public Statement append(final String text)
	{
		this.text.append(text);
		return this;
	}
		
	public Statement append(final char text)
	{
		this.text.append(text);
		return this;
	}
		
	public Statement append(final int text)
	{
		this.text.append(text);
		return this;
	}
		
	public Statement append(final Object text)
	{
		this.text.append(text);
		return this;
	}
	
	public Statement append(final Function function)
	{
		function.append(this);
		return this;
	}

	public Statement appendPK(final Type type)
	{
		final Table table = type.getTable();
		this.text.
			append(table.protectedID).
			append('.').
			append(table.getPrimaryKey().protectedID);
			
		return this;
	}
		
	public Statement appendValue(final Function function, final Object value)
	{
		if(function instanceof ComputedFunction)
		{
			this.text.append(((ComputedFunction)function).surface2Database(value));
		}
		else
		{
			final ObjectAttribute attribute = (ObjectAttribute)function;
			appendValue(attribute.getMainColumn(), attribute.surfaceToCache(value));
		}
		return this;
	}
	
	public Statement appendValue(final Column column, final Object value)
	{
		this.text.append(column.cacheToDatabase(value));
		return this;
	}
	
	public Statement defineColumn(final ComputedFunction function)
	{
		if(columnTypes!=null)
			columnTypes.add(function.jdbcType);
		return this;
	}
		
	public Statement defineColumn(final Column column)
	{
		if(columnTypes!=null)
			columnTypes.add(column.jdbcType);
		return this;
	}
		
	public Statement defineColumnInteger()
	{
		if(columnTypes!=null)
			columnTypes.add(IntegerColumn.JDBC_TYPE_INT);
		return this;
	}
		
	public Statement defineColumnString()
	{
		if(columnTypes!=null)
			columnTypes.add(StringColumn.JDBC_TYPE);
		return this;
	}
		
	public Statement defineColumnTimestamp()
	{
		if(columnTypes!=null)
			columnTypes.add(TimestampColumn.JDBC_TYPE);
		return this;
	}
		
	public String getText()
	{
		return text.toString();
	}

	public String toString()
	{
		return text.toString();
	}
}
