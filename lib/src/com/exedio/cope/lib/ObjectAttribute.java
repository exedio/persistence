package com.exedio.cope.lib;


public abstract class ObjectAttribute extends Attribute implements Function
{
	protected ObjectAttribute(final Option option)
	{
		super(option);
	}
	
	abstract Object cacheToSurface(Object cache);
	abstract Object surfaceToCache(Object surface);
	
	void checkValue(final boolean initial, final Object value, final Item item)
		throws
			ReadOnlyViolationException,
			NotNullViolationException,
			LengthViolationException
	{
		if(!initial && isReadOnly())
			throw new ReadOnlyViolationException(item, this);
		if(isNotNull() && value == null)
			throw new NotNullViolationException(item, this);
	}


}
