
package com.exedio.cope.lib;

public final class Statement
{
	final StringBuffer text = new StringBuffer();
		
	Statement()
	{
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
			this.text.append(attribute.getMainColumn().protectedName);
			
		return this;
	}
		
	public Statement appendValue(Attribute attribute, final Object value)
	{
		while(attribute.mapping!=null)
			attribute = attribute.mapping.sourceAttribute;

		this.text.append(attribute.getMainColumn().cacheToDatabase(attribute.surfaceToCache(value)));
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
