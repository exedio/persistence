
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.List;

public final class BooleanAttribute extends Attribute
{
	public BooleanAttribute(final Search.Option option)
	{
		super(option);
	}
	
	protected List createColumns(final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(getType(), name, notNull, 1));
	}
	
	Object cacheToSurface(final Object cache)
	{
		if(cache==null)
			return null;
		else
		{
			switch(((Integer)cache).intValue())
			{
				case 0:
					return Boolean.FALSE;
				case 1:
					return Boolean.TRUE;
				default:
					throw new RuntimeException("cacheToSurface:"+cache);
			}
		}
	}
	
	private static final Integer FALSE = new Integer(0);
	private static final Integer TRUE = new Integer(1);
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ?
				null :
				((Boolean)surface).booleanValue() ? TRUE : FALSE;
	}
	
}
