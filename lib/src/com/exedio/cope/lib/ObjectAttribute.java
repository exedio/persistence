package com.exedio.cope.lib;

import com.exedio.cope.lib.search.EqualCondition;


public abstract class ObjectAttribute
	extends Attribute
	implements Function, Selectable
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

	public final Item searchUnique(final Object value)
	{
		// TODO: search nativly for unique constraints
		return getType().searchUnique(new EqualCondition(this, value));
	}

}
