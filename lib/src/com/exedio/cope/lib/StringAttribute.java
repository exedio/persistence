
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class StringAttribute extends Attribute
{
	private final int minimumLength;

	public StringAttribute(final Option option)
	{
		super(option);
		this.minimumLength = 0;
	}
	
	public StringAttribute(final Option option, final int minimumLength)
	{
		super(option);
		this.minimumLength = minimumLength;
		if(minimumLength<=0)
			throw new InitializerRuntimeException("mimimum length must be greater than zero.");
	}
	
	public StringAttribute(final Option option, final AttributeMapping mapping)
	{
		super(option, mapping);
		this.minimumLength = 0;
	}
	
	public final int getMinimumLength()
	{
		return minimumLength;
	}
	
	public final boolean isLengthConstrained()
	{
		return minimumLength!=0;
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		if(mapping==null)
			return Collections.singletonList(new StringColumn(getType(), name, notNull));
		else
			return Collections.EMPTY_LIST;
	}
	
	Object cacheToSurface(final Object cache)
	{
		return (String)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (String)surface;
	}
	
	void checkValue(final Object value, final Item item)
		throws
			LengthViolationException
	{
		super.checkValue(value, item);
		if(value!=null)
		{
			final String stringValue = (String)value;
			if(stringValue.length()<minimumLength)
				throw new LengthViolationException(item, this, stringValue);
		}
	}
	
}
