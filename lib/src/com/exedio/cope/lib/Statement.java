
package com.exedio.cope.lib;

import java.util.ArrayList;

public final class Statement
{
	final StringBuffer text = new StringBuffer();
	final ArrayList columnTypes;
		
	Statement(final boolean useDefineColumnTypes)
	{
		columnTypes = useDefineColumnTypes ? new ArrayList() : null;
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
		if(function instanceof ObjectAttribute)
			append((ObjectAttribute)function);
		else
			append((ComputedFunction)function);
			
		return this;
	}

	public Statement append(final ComputedFunction function)
	{
		function.append(this);
		return this;
	}

	public Statement append(final ObjectAttribute attribute)
	{
		this.text.
			append(attribute.getType().table.protectedID).
			append('.').
			append(attribute.getMainColumn().protectedID);
			
		return this;
	}
		
	public Statement appendPK(final Type type)
	{
		final Table table = type.table;
		this.text.
			append(table.protectedID).
			append('.').
			append(table.getPrimaryKey().protectedID);
			
		return this;
	}
		
	public Statement appendValue(Function function, final Object value)
	{
		while(function instanceof ComputedFunction)
			function = ((ComputedFunction)function).mainSource;
			
		final ObjectAttribute attribute = (ObjectAttribute)function;
		appendValue(attribute.getMainColumn(), attribute.surfaceToCache(value));
		return this;
	}
	
	public Statement appendValue(final Column column, final Object value)
	{
		this.text.append(column.cacheToDatabase(value));
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
		
	public String getText()
	{
		return text.toString();
	}

	public String toString()
	{
		return text.toString();
	}
}
