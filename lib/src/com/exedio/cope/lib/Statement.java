
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
		
	public Statement append(final Attribute attribute)
	{
		final AttributeMapping mapping = attribute.mapping;
		if(attribute.mapping!=null)
		{
			this.text.append(mapping.sqlMappingStart);
			append(mapping.sourceAttribute);
			this.text.append(mapping.sqlMappingEnd);
		}
		else
			this.text.
				append(attribute.getType().protectedName).
				append('.').
				append(attribute.getMainColumn().protectedName);
			
		return this;
	}
		
	public Statement appendPK(final Type type)
	{
		this.text.
			append(type.protectedName).
			append('.').
			append(type.primaryKey.protectedName);
			
		return this;
	}
		
	public Statement appendValue(Attribute attribute, final Object value)
	{
		while(attribute.mapping!=null)
			attribute = attribute.mapping.sourceAttribute;

		this.text.append(attribute.getMainColumn().cacheToDatabase(attribute.surfaceToCache(value)));
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
			columnTypes.add(IntegerColumn.JDBC_TYPE);
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
